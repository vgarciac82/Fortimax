package com.syc.volumenes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import com.syc.catalogos.CatalogosManager;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_unidad_volumen;
import com.syc.fortimax.hibernate.entities.imx_unidad_volumen_id;
import com.syc.fortimax.hibernate.entities.imx_volumen;
import com.syc.utils.Json;



public class VolumenesManager {
	private static final Logger log = Logger.getLogger(CatalogosManager.class);		
	public ArrayList<Object> ObtenerVolumenes(){
		

		ArrayList<Object> Volumenes = new ArrayList<Object>();
		Session session=HibernateUtils.getSession();
		HashMap<String, Object> rec = new HashMap<String, Object>();
		List<imx_unidad_volumen> ListC=null;
		try{
			Query query=session.createQuery("from imx_unidad_volumen");
			ListC=(List<imx_unidad_volumen>) query.list();
			for (imx_unidad_volumen volu : ListC) {
//	            log.info("Leido: "+vari.getName());
	            rec.clear();
	            rec.put("Unidad", volu.getId().Unidad);
				rec.put("RutaBase",volu.getId().RutaBase);
				rec.put("EstadoUnidad",volu.getEstadoUnidad());
				Volumenes.add(rec.clone());
	        }
				
		}
		catch (Exception exc) {
			log.error("Entro Catch  Obtener Variables VariablesEntornoManager");
			log.error(exc, exc);
		}
		return Volumenes;
	}
	
	
	public boolean existe(String datosJson){
		log.trace("Entro a buscar si existe volumen");
		Session session = HibernateUtils.getSession();
		boolean existe;
		try{
			imx_unidad_volumen_id unidadId=Json.getObject(datosJson, imx_unidad_volumen_id.class);
			Query query=session.createQuery("from imx_unidad_volumen WHERE UNIDAD= :unidad AND RUTA_BASE=:ruta");
			query.setParameter("unidad", unidadId.getUnidad());
			query.setParameter("ruta", unidadId.getRutaBase());
			if(query.uniqueResult()!=null){
				existe=true;
			}
			else{
				existe=false;
			}
			log.trace("El resultado de la Busqueda es: "+existe);
			return existe;
		}
		catch (Exception exc) {
			log.error("Entro Catch  Existe Volumen");
			log.error(exc, exc);
			return true;
		}
	}
	
	public boolean insertarVolumen(String datosJson){
		log.trace("Entro a insertar volumen");
		Session session = HibernateUtils.getSession();
		boolean correcto=false;
		try{
			
			imx_unidad_volumen_id unidadId=Json.getObject(datosJson, imx_unidad_volumen_id.class);
			imx_unidad_volumen unidad=new imx_unidad_volumen();
			
				
			Transaction trans=session.beginTransaction();
			unidad.setId(unidadId);
			unidad.setEstadoUnidad(0);
			unidad.setTipoDispositivo('0');
			session.save(unidad);
			trans.commit();
//			if(insertarVolumenConsecutivo(unidadId.getUnidad(),unidadId.getRutaBase())==true){
			if(CrearVolumenDisco(unidadId.getUnidad(),unidadId.getRutaBase(),"")==true){
			correcto=true;
			}
			else{
				eliminaUnidadVolumen(unidadId.getUnidad(),unidadId.getRutaBase());
				correcto=false;
			}
			
		}
		catch (Exception exc) {
			log.error("Entro Catch  Existe Volumen");
			log.error(exc, exc);
			correcto =false;
		}
		return correcto;
	}
	public void eliminaUnidadVolumen(String Unidad,String Ruta){
		log.trace("Elimina Uniad y Ruta al ocurrir un error en su creacion en directorio o al insertar en imx_volumen Unidad: "+Unidad+" Ruta: "+Ruta);
		Session session=HibernateUtils.getSession();
		try{
			Transaction trans=session.beginTransaction();
			Query query=session.createQuery("delete imx_unidad_volumen where UNIDAD = :Unidad AND RUTA_BASE=:Ruta");
			query.setParameter("Unidad", Unidad);
			query.setParameter("Ruta", Ruta);
			query.executeUpdate();
			trans.commit();
		}
		catch (Exception exc) {
			log.error("Entro al catch elimina Unidad");
			log.error(exc, exc);
		}
	}
	public boolean insertarVolumenConsecutivo(String Unidad,String Ruta){
		log.trace("Entro a insertar volumenConsecutivo");
		Session session = HibernateUtils.getSession();
		boolean correcto=false;
		try{
			if(CrearVolumenDisco(Unidad,Ruta,VolumenMaximo())==true){
			Transaction trans=session.beginTransaction();
			imx_volumen vo=new imx_volumen();
			vo.setVolumen(VolumenMaximo());
			vo.setUnidadDisco(Unidad);
			vo.setRutaBase(Ruta);
			vo.setRutaDirectorio("_volumen"
			+System.getProperty("file.separator")+"000"
			+System.getProperty("file.separator")+"000"
			+System.getProperty("file.separator")+"000"
			+System.getProperty("file.separator")+VolumenMaximo()
			+System.getProperty("file.separator"));
			vo.setCapacidad('0');
			vo.setTipoVolumen('0');
			session.save(vo);
			trans.commit();		
			correcto=true;
			}
			else{
				return false;
			}
		}
		
		catch (Exception exc) {
			log.error("Entro Catch  Existe Volumen");
			log.error(exc, exc);
			correcto=false;
		}
		return correcto;
	}

	public String VolumenMaximo(){
		log.trace("Entro a insertar volumen maximo existente");
		Session session = HibernateUtils.getSession();
		String VolR="",Vol,VolPref;
		try{
			
			Criteria crit = session.createCriteria(imx_volumen.class);
	        ProjectionList projList = Projections.projectionList();
	        projList.add(Projections.max("Volumen"));
	        crit.setProjection(projList);
	        if(crit.uniqueResult()!=null){
	        	List list = crit.list();
	        	log.trace("Maximo Volumen es: "+list.get(0).toString());
				Vol=list.get(0).toString();
				VolPref=Vol.substring(0, 3);
				Vol=nextVolumenSequence(Vol.substring(3));
				VolR=VolPref+Vol;
	        }
	        else{
				VolR="0a000000";
			}

		log.trace("El siguiente volumen es: "+VolR);
			return VolR;
		}
		catch (Exception exc) {
			log.error("Entro Catch volumen maximo");
			log.error(exc, exc);
			return "";
		}
	}
	public boolean CrearVolumenDisco(String Volumen,String Ruta,String Maximo){
		try{
			log.trace("Entro a crear unidad en disco");
			File dir = new File(Volumen + Ruta
					+ "_volumen" + System.getProperty("file.separator")
					+ "000" + System.getProperty("file.separator") + "000"
					+ System.getProperty("file.separator") + "000"
					+ System.getProperty("file.separator") + Maximo);
			log.trace(Volumen + Ruta
					+ "_volumen" + System.getProperty("file.separator")
					+ "000" + System.getProperty("file.separator") + "000"
					+ System.getProperty("file.separator") + "000"
					+ System.getProperty("file.separator") + Maximo);
			if (!dir.exists()) {
				if (dir.mkdirs()){
					log.trace("Directorio creado correctamente");
					return true;
				}
				else{
					log.trace("Error al crear directorio");
					return false;
				}
			}
			else{
				log.trace("Directorio existe");
				return true;
			}
		}
		catch (Exception exc) {
			log.error("Entro Catch crear unidad en disco");
			log.error(exc, exc);
			return false;
		}
	}
	public boolean ActivaUnidad(String datosJson){
		log.trace("Entro a activar unidad");
		Session session=HibernateUtils.getSession();
		try{
			
			imx_unidad_volumen_id unidadId=Json.getObject(datosJson, imx_unidad_volumen_id.class);
			String max=VolumenMaximo();
			if(insertarVolumenConsecutivo(unidadId.getUnidad(),unidadId.getRutaBase())==true){				
					Transaction trans=session.beginTransaction();
					Query queryI=session.createQuery("UPDATE imx_volumen SET CAPACIDAD=0 WHERE CAPACIDAD=1");
					queryI.executeUpdate();
					Query queryA=session.createQuery("UPDATE imx_volumen SET CAPACIDAD=1 WHERE VOLUMEN=:Maximo");
					queryA.setParameter("Maximo",max);
					queryA.executeUpdate();
					Query query=session.createQuery("UPDATE imx_unidad_volumen SET ESTADO_UNIDAD=0 WHERE ESTADO_UNIDAD=1");
					query.executeUpdate();
					Query queryU=session.createQuery("UPDATE imx_unidad_volumen SET ESTADO_UNIDAD=1 WHERE UNIDAD=:Unidad AND RUTA_BASE=:RutaB");
					queryU.setParameter("Unidad", unidadId.getUnidad());
					queryU.setParameter("RutaB", unidadId.getRutaBase());
					queryU.executeUpdate();
					trans.commit();
					return true;
			}
			else{
				return false;
			}
		}
		catch (Exception exc) {
			log.debug("Entro Catch Activar unidad");
			log.error(exc, exc);
			return false;
		}

	}
	private static String nextVolumenSequence(String volumen){
		try{
		String value = volumen, retVal = null, lastChar = null;

		if (("zzzzz".equals(value)) || ("".equals(value)) || (value == null))
			log.trace("Secuencia maxima de volumenes alcanzada");

		lastChar = value.substring(value.length() - 1).toLowerCase();

		if (lastChar.equals("z"))
			value = nextVolumenSequence(value.substring(0, value.length() - 1));
		else if (lastChar.equals("9"))
			value = value.substring(0, value.length() - 1) + "a";
		else
			value = value.substring(0, value.length() - 1)
					+ siguienteCaracter(lastChar);

		retVal = (value + "00000".substring(0, 5 - value.length()));

		log.trace("Nuevo Volumen: " + retVal);

		return retVal;
		}
		catch (Exception exc) {
			log.error("Entro Catch  nextVolumenSequence");
			log.error(exc, exc);
			return "";
		}
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
}
