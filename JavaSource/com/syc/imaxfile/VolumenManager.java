package com.syc.imaxfile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_unidad_volumen;
import com.syc.fortimax.hibernate.entities.imx_volumen;
import com.syc.utils.Utils;

public class VolumenManager {

	private static Logger log = Logger.getLogger(VolumenManager.class);
	private static Object sync = new Object();

	public static int VOL_MAX_DIR = 999; // 3 - 600 - 999
	public static int VOL_MAX_ARCH = 3072; // 5 - 3072

	public static Volumen getVolumen()  {

		Volumen vol = new Volumen(null);	
		HibernateManager hm = new HibernateManager();
		synchronized (sync) {
			try {
				
        			Query q = hm.createQuery("FROM imx_unidad_volumen WHERE estado_unidad = :estado_unidad ");    			
        			q.setString("estado_unidad", vol.getEstadoUnidad());              
        			List<imx_unidad_volumen> imxunidadvolumen = (List<imx_unidad_volumen>)q.list();        			
        			if (imxunidadvolumen == null) {       				
        				log.error("No se encontro ningun Volumen activo");
        			} 
        			if (imxunidadvolumen.size() > 1) {       				
        				log.warn("Existen mas de un volumen activo, se utilizara:  "+imxunidadvolumen.get(0).getId().getUnidad() + imxunidadvolumen.get(0).getId().getRutaBase());
        				//throw new FortimaxException("Volumen Manager","Existe mas de un volumen activo");
        				//TODO: verificar si realmente deberia haber mas de un volumen activo, solo para lectura ?.
        			} 
 
    				vol.setUnidad(imxunidadvolumen.get(0).getId().getUnidad());
    				vol.setTipoDispositivo(Utils.getString(imxunidadvolumen.get(0).getTipoDispositivo()));
    				vol.setRutaBase(imxunidadvolumen.get(0).getId().getRutaBase());
				  				
					q = hm.createQuery("FROM imx_volumen WHERE unidad_disco = ?  AND tipo_volumen = ? AND capacidad = ?");		
	    			q.setString(0, vol.getUnidad());          
	    			q.setInteger(1,Integer.valueOf(vol.getTipoDispositivo())); 
	    			q.setInteger(2, Integer.valueOf(vol.getCapacidad())); 
	    			
	    			imx_volumen imxvolumen = (imx_volumen)q.uniqueResult();
					
	    			if (imxvolumen == null) {
	    				
	    				log.error("No se encontro Unidad activa");
	    			}
	    			  			  			
	    			vol.setVolumen(imxvolumen.getVolumen());   	
	    			vol.setRutaDirectorio(imxvolumen.getRutaDirectorio());    			
					String volumenAnterior = vol.getVolumen();
	
					vol.setVolumen(administraVolumen(vol));
					// boolean createDirectory = false;
	
					if (!volumenAnterior.equals(vol.getVolumen())) {
						
						String sInsert = "UPDATE imx_volumen SET capacidad = '0' WHERE volumen = ? AND unidad_disco = ? AND tipo_volumen = ? AND capacidad = ?";
						q = hm.createSQLQuery(sInsert);
						q.setString(0, volumenAnterior);
						q.setString(1, vol.getUnidad());
						q.setString(2, vol.getTipoVolumen());
						q.setString(3, "1");						
	
						vol.setRutaDirectorio(creaRutaDirectorio(vol.getUnidad(),vol.getRutaBase(), vol.getRutaDirectorio(), 0));
	
						if (hm.executeQuery(q) != 0) {
							
							sInsert = "INSERT INTO imx_volumen (volumen, unidad_disco, ruta_base, ruta_directorio, capacidad, tipo_volumen) VALUES (?, ?, ?, ?, ?, ?)";
							q = hm.createSQLQuery(sInsert);
							q.setString(0, vol.getVolumen());
							q.setString(1,vol.getUnidad());
							q.setString(2, vol.getRutaBase());
							q.setString(3, vol.getRutaDirectorio() + vol.getVolumen()+ File.separator);
							q.setString(4, "1");
							q.setString(5, vol.getTipoVolumen());
							
							hm.executeQuery(q);
						}

					File v = new File(vol.getUnidad() + vol.getRutaBase()+ vol.getRutaDirectorio() + vol.getVolumen());
					try {
						if (!v.exists())
							if (!makeDirectories(v))
								log.error("No se logro crear directorio "+ v.getPath());
					} 
					
					catch (Exception exc) {
						log.error(exc);
					}
				} else {
					File v = new File(vol.getUnidad() + vol.getRutaBase()
							+ vol.getRutaDirectorio() + vol.getVolumen());
					try {
						if (!v.exists())
							if (!makeDirectories(v))
								log.error("No se logro crear directorio "+ v.getPath());
					} catch (Exception exc) {
						log.error(exc);
					}
				}
			} finally {
				
				hm.close();
			}

			return vol;
		}
	}

	private static String administraVolumen(Volumen vol) {

		File file = new File(vol.getUnidad() + vol.getRutaBase()
				+ vol.getRutaDirectorio() + vol.getVolumen());
		String[] lista = file.list(new FilenameFilter() {

			public boolean accept(File f, String s) {
				return new File(f, s).isFile();
			}
		});
		int totFiles = (lista != null) ? lista.length : 0;
		String volPrefix = vol.getVolumen().substring(0, 3);
		String volumen = vol.getVolumen().substring(3);

		if (totFiles >= VOL_MAX_ARCH)
			return (volPrefix + nextVolumenSequence(volumen));

		return vol.getVolumen();
	}

	private static String nextVolumenSequence(String volumen) {

		String value = volumen, retVal = null, lastChar = null;

		if (("zzzzz".equals(value)) || ("".equals(value)) || (value == null))
			log.error("Secuencia maxima de volumenes alcanzada");

		lastChar = value.substring(value.length() - 1).toLowerCase();

		if (lastChar.equals("z"))
			value = nextVolumenSequence(value.substring(0, value.length() - 1));
		else if (lastChar.equals("9"))
			value = value.substring(0, value.length() - 1) + "a";
		else
			value = value.substring(0, value.length() - 1)
					+ siguienteCaracter(lastChar);

		retVal = (value + "00000".substring(0, 5 - value.length()));

		log.info("Nuevo Volumen: " + retVal);

		return retVal;
	}

	private static String siguienteCaracter(String ch) {

		try {
			return String.valueOf(Integer.parseInt(ch) + 1);
		} catch (NumberFormatException e) {
			String letters[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
					"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
					"v", "w", "x", "y", "z" };

			for (int i = 0; i < letters.length; i++) {
				if (ch.equals(letters[i])) {
					return letters[i + 1];
				}
			}
		}

		return null;
	}

	private static String creaRutaDirectorio(String unidad_disco,
			String ruta_base, String ruta_directorio, int level) {

		String newRutaDirectorio = ruta_directorio;

		if (ruta_directorio == null)
			log.error("La ruta_directorio no debe ser nula");

		if (((null + File.separator).equals(ruta_directorio)) && (level > 0))
			log.error("Limite maximo de directorios alcanzado");

		File pathDir = new File(newRutaDirectorio);
		File realDir = new File(unidad_disco + ruta_base + newRutaDirectorio);

		String[] dirList = realDir.list(new FilenameFilter() {

			public boolean accept(File f, String s) {
				return f.isDirectory();
			}
		});

		int totDir = (dirList == null) ? 0 : dirList.length;

		if (totDir >= VOL_MAX_DIR) {
			newRutaDirectorio = creaRutaDirectorio(unidad_disco, ruta_base,
					pathDir.getParent() + File.separator, level + 1);
		} else {
			if (level > 0) {
				Arrays.sort(dirList); // Se ordena, puede venir en desorden
				String[] dirSuffix = { null, null, File.separator + "000",
						File.separator + "000" + File.separator + "000" };
				String newDirname = null;
				try {
					newDirname = String.valueOf(Integer
							.parseInt(dirList[dirList.length - 1]) + 1);
				} catch (NumberFormatException ne) {
					newDirname = "";
				}

				newDirname = "000".substring(0, 3 - newDirname.length())
						+ newDirname;

				File d = new File(realDir.getPath()
						+ File.separator
						+ newDirname
						+ ((dirSuffix[level] != null) ? dirSuffix[level]
								+ File.separator : File.separator));

				if (!makeDirectories(d))
					log.error("No se logro crear directorio "+ d.getPath());

				newRutaDirectorio = pathDir.getPath()
						+ File.separator
						+ newDirname
						+ ((dirSuffix[level] != null) ? dirSuffix[level]
								+ File.separator : File.separator);
			} else if (!realDir.exists()) {
				if (!makeDirectories(realDir))
					log.error("No se logro crear directorio "+ realDir.getPath());
			}
		}

		return newRutaDirectorio;
	}

	private synchronized static boolean makeDirectories(File path) {
		log.info("Creando nuevo volumen: " + path);
		if (path == null)
			log.error("path no debe ser nulo");

		if (path.exists())
			return true;

		if (path.mkdir())
			return true;

		File canonPath = null;
		try {
			canonPath = path.getCanonicalFile();
		} catch (IOException e) {
			return false;
		}

		String parent = canonPath.getParent();
		return (parent != null)
				&& (makeDirectories(new File(parent)) && canonPath.mkdir());
	}
}