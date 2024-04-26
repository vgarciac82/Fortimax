package com.syc.imaxfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.ParametersInterface;
import com.syc.utils.Utils;

public class PaginaManager {

	private static final Logger log = Logger.getLogger(PaginaManager.class);

	@SuppressWarnings("unchecked")
	public static ArrayList<Pagina> getPaginas(String aplicacion, int expediente) {
		ArrayList<Pagina> p = new ArrayList<Pagina>();

		HibernateManager hm = new HibernateManager();
		try {
			String sQuery = "select pag.*, vol.UNIDAD_DISCO, vol.RUTA_BASE, vol.RUTA_DIRECTORIO from imx_pagina pag INNER JOIN imx_volumen vol on pag.VOLUMEN = vol.VOLUMEN "
					+ " where "
					+ " pag.TITULO_APLICACION = ? "
					+ " and pag.id_gabinete = ? "
					+ " order by pag.nom_archivo_vol ";

			Query query = hm.createSQLQuery(sQuery);
			query.setString(0, aplicacion);
			query.setInteger(1, expediente);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String,?>> list = query.list();

			for (Map<String,?> map : list) {
				map = new CaseInsensitiveMap(map);
				Pagina pag = new Pagina();
				pag.setTitulo_aplicacion(Utils.getString(map.get("TITULO_APLICACION")));
				pag.setId_gabinete(Utils.getInteger(map.get("ID_GABINETE")));
				pag.setId_carpeta_padre(Utils.getInteger(map.get("ID_CARPETA_PADRE")));
				pag.setId_documento(Utils.getInteger(map.get("ID_DOCUMENTO")));
				Integer pagina = Utils.getInteger(map.get("PAGINA"));
				if(pagina==null)
					pagina = Utils.getInteger(map.get("NUMERO_PAGINA"));
				pag.setNumero_pagina(pagina);
				pag.setVolumen(Utils.getString(map.get("VOLUMEN")));
				pag.setTipoVolumen(Utils.getString(map.get("TIPO_VOLUMEN")));
				pag.setNomArchivoVol(Utils.getString(map.get("NOM_ARCHIVO_VOL")));
				pag.setNomArchivoOrg(Utils.getString(map.get("NOM_ARCHIVO_ORG")));
				pag.setTipoPagina(Utils.getString(map.get("TIPO_PAGINA")));
				pag.setAnotaciones(Utils.getString(map.get("ANOTACIONES")));
				pag.setEstadoPagina(Utils.getString(map.get("ESTADO_PAGINA")));
				pag.setTamanoBytes(Utils.getDouble(map.get("TAMANO_BYTES")));
				pag.setUnidadDisco(Utils.getString(map.get("UNIDAD_DISCO")));
				pag.setRutaBase(Utils.getString(map.get("RUTA_BASE")));
				pag.setRutaDirectorio(Utils.getString(map.get("RUTA_DIRECTORIO")));
				p.add(pag);
			}
		} finally {
			hm.close();
		}
		return p;
	}
	
	public int insertPagina(String gaveta, int id_expediente, int id_carpeta, int id_documento, File file) throws EntityManagerException, VolumenManagerException, IOException {
		return insertPagina(gaveta,id_expediente,id_carpeta,id_documento,null,file);
	}
	
	//BUGFIX para la concurrencia en el uso de ws uploadFile
	private synchronized String generarRandomString() {
		int time = (int)System.currentTimeMillis();
		String hex = Integer.toHexString(time);
		if(hex.length()%2 != 0)
			hex = "0"+hex;
		byte[] bytes = DatatypeConverter.parseHexBinary(hex);
		String token = new String(Base64.encodeBase64(bytes)).replace('+','-').replace('/','_');
		
		SecureRandom random = new SecureRandom();
		String randomString = new BigInteger(130, random).toString(32);
		
		return token+randomString;
	}
	
	public int insertPagina(String gaveta, int id_expediente, int id_carpeta, int id_documento, String tipoPagina, File file) throws EntityManagerException, VolumenManagerException, IOException {

		Pagina p = new Pagina();
		p.setNomArchivoOrg(file.getName());
		p.setTamanoBytes(file.length());

		// TODO revisar fue sacado de AddPageDocumentServlet
		String s = "" + new Date().getTime();
		String new_name = s.substring(s.length() - 10);
		new_name = "aut" + "0000000000".substring(0, 10 - new_name.length())
				+ new_name + generarRandomString() + ".tif";
		Volumen v = VolumenManager.getVolumen();
		p.setNomArchivoVol(new_name);
		p.setVolumen(v.getVolumen());
		p.setTipoVolumen(v.getTipoVolumen());

		File new_folder = new File(v.getUnidad()
				+ ("*".equals(v.getRutaBase()) ? "" : v.getRutaBase())
				+ v.getRutaDirectorio() + v.getVolumen());

		new_folder.mkdir();

		File new_file = new File(new_folder, new_name);

		FileUtils.moveFile(file, new_file);
		// file.renameTo(new_file);
		// Hasta aki

		p.setTitulo_aplicacion(gaveta);
		p.setId_gabinete(id_expediente);
		p.setId_carpeta_padre(id_carpeta);
		p.setId_documento(id_documento);
		
		if (tipoPagina!=null)
			p.setTipoPagina(tipoPagina);

		return insertPagina(p);
	}

	public static boolean clonePage(Pagina pag, boolean copyFisical) throws FortimaxException {
		boolean exito = false;
		
		StringBuffer query = new StringBuffer();
		HibernateManager hm = new HibernateManager();
		try {
			if (copyFisical) {
				copyPage(pag);
			}
			query = new StringBuffer();
			query.append("INSERT INTO imx_pagina (titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento, numero_pagina, pagina, volumen, tipo_volumen, nom_archivo_vol, nom_archivo_org, tipo_pagina, anotaciones, estado_pagina,tamano_bytes) VALUES ('");
			query.append(pag.getTituloAplicacion());
			query.append("',");
			query.append(pag.getIdGabinete());
			query.append(",");
			query.append(pag.getIdCarpetaPadre());
			query.append(",");
			query.append(pag.getIdDocumento());
			query.append(",");
			query.append(pag.getNumeroPagina());
			query.append(",");
			query.append(pag.getNumeroPagina());
			query.append(",'");
			query.append(pag.getVolumen());
			query.append("',");
			query.append(pag.getTipoVolumen());
			query.append(",'");
			query.append(pag.getNomArchivoVol());
			query.append("','");
			query.append(pag.getNomArchivoOrg());
			query.append("','");
			query.append(pag.getTipoPagina());
			query.append("','");
			query.append(pag.getAnotaciones());
			query.append("','");
			query.append(pag.getEstadoPagina());
			query.append("',");
			query.append(pag.getTamanoBytes());
			query.append(")");
			Query sqlQuery = hm.createSQLQuery(query.toString());
			hm.executeQuery(sqlQuery);
			exito = true;
		} catch (Exception e) {
			log.error(e, e);
			throw new FortimaxException("UNKNOW", e.getMessage());
		} finally {
			hm.close();
		}
		return exito;
	}

	@SuppressWarnings("unchecked")
	public static int insertPagina(Pagina pag) throws EntityManagerException {
		int id = -1;

		StringBuffer query = new StringBuffer();
		query.append("SELECT MAX(numero_pagina) AS numero_pagina, MAX(pagina) AS pagina FROM imx_pagina WHERE titulo_aplicacion='");
		query.append(pag.getTituloAplicacion());
		query.append("' AND id_gabinete=");
		query.append(pag.getIdGabinete());
		query.append(" AND id_carpeta_padre=");
		query.append(pag.getIdCarpetaPadre());
		query.append(" AND id_documento=");
		query.append(pag.getIdDocumento());

		HibernateManager hm = new HibernateManager();
		try {
			Query sqlQuery = hm.createSQLQuery(query.toString());
			sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			Map<String,?> map = (Map<String, ?>) sqlQuery.uniqueResult();
			map = new CaseInsensitiveMap(map);
			Integer numero_pagina = Utils.getInteger(map.get("numero_pagina"));
			
			numero_pagina = numero_pagina == null ? 1 : numero_pagina+1;
			Integer pagina = Utils.getInteger(map.get("pagina"));
			
			pagina = pagina == null ? 1 : pagina+1;
			pag.setNumero_pagina(pagina);
			
			log.debug("Se insertara la pagina " + pag.getNumeroPagina());
			
			query = new StringBuffer();
			query.append("INSERT INTO imx_pagina (titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento, numero_pagina, pagina, volumen, tipo_volumen, nom_archivo_vol, nom_archivo_org, tipo_pagina, anotaciones, estado_pagina,tamano_bytes) VALUES ('");
			query.append(pag.getTituloAplicacion());
			query.append("',");
			query.append(pag.getIdGabinete());
			query.append(",");
			query.append(pag.getIdCarpetaPadre());
			query.append(",");
			query.append(pag.getIdDocumento());
			query.append(",");
			query.append(numero_pagina);
			query.append(",");
			query.append(pag.getNumeroPagina());
			query.append(",'");
			query.append(pag.getVolumen());
			query.append("',");
			query.append(pag.getTipoVolumen());
			query.append(",'");
			query.append(pag.getNomArchivoVol());
			query.append("','");
			query.append(pag.getNomArchivoOrg());
			query.append("','");
			query.append(pag.getTipoPagina());
			query.append("','");
			query.append(pag.getAnotaciones());
			query.append("','");
			query.append(pag.getEstadoPagina());
			query.append("',");
			query.append(pag.getTamanoBytes());
			query.append(")");
			
			sqlQuery = hm.createSQLQuery(query.toString());
			int ins = hm.executeQuery(sqlQuery);
			log.debug("Se insertaron " + ins + " registros");
			
			id = pag.getNumeroPagina();

			DocumentoManager.updateNumeroPaginas(pag.getTituloAplicacion(),
					pag.getIdGabinete(), pag.getIdCarpetaPadre(),
					pag.getIdDocumento(), pag.getNumeroPagina());

			DocumentoManager.addTamanoBytes(pag.getTituloAplicacion(),
					pag.getIdGabinete(), pag.getIdCarpetaPadre(),
					pag.getIdDocumento(), pag.getTamanoBytes());

			// TODO indexacion con lucene!!!
		} finally {
			hm.close();
		}
		return id;
	}

	public static boolean copyPage(Pagina pag) throws FortimaxException {

		FileInputStream frin = null;
		FileOutputStream fwout = null;

		try {

			boolean exito = false;

			String fileName = getNextFilename();
			String extVol = pag.getNomArchivoVol().substring(
					pag.getNomArchivoVol().lastIndexOf("."));
			String extOrg = pag.getNomArchivoOrg().substring(
					pag.getNomArchivoOrg().lastIndexOf("."));

			String pathOrg = pag.getUnidadDisco() + pag.getRutaBase()
					+ pag.getRutaDirectorio() + pag.getNomArchivoVol();
			String pathDes = pag.getUnidadDisco() + pag.getRutaBase()
					+ pag.getRutaDirectorio() + "CLON_" + fileName + extVol;

			frin = new FileInputStream(pathOrg);
			fwout = new FileOutputStream(pathDes);

			byte buf[] = new byte[1024 * 4];
			while ((frin.read(buf)) != -1)
				fwout.write(buf);

			pag.setNomArchivoOrg("CLON_" + fileName + extOrg);
			pag.setNomArchivoVol("CLON_" + fileName + extVol);

			return exito;
		} catch (Exception e) {
			log.error(e, e);
			throw new FortimaxException("UNKNOW", e.getMessage());
		} finally {
			if (fwout != null)
				try {
					fwout.flush();
					fwout.close();
				} catch (IOException exc) {
					log.warn(exc, exc);
				}

			if (frin != null)
				try {
					frin.close();
				} catch (Exception e2) {
					log.warn(e2, e2);
				}

		}
	}

	public static String getNextFilename() throws Exception {

		String strRetVal = "";
		String strTemp = "";

		try {
			// Obtiene direccion IP
			InetAddress addr = InetAddress.getLocalHost();

			byte[] ipaddr = addr.getAddress();
			for (int i = 0; i < ipaddr.length; i++) {
				Byte b = new Byte(ipaddr[i]);

				strTemp = Integer.toHexString(b.intValue() & 0x000000ff);
				while (strTemp.length() < 2)
					strTemp = '0' + strTemp;

				strRetVal += strTemp;
			}

			// Obtiene tiempo actual con milisegundos
			strTemp = Long.toHexString(System.currentTimeMillis());
			while (strTemp.length() < 12)
				strTemp = '0' + strTemp;

			strRetVal += strTemp;

			// Obtiene un numero random
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
			strTemp = Integer.toHexString(prng.nextInt());
			while (strTemp.length() < 8)
				strTemp = '0' + strTemp;

			strRetVal += strTemp.substring(4);

			// Obtiene el hash de identidad del objeto
			strTemp = Long.toHexString(System
					.identityHashCode((Object) new String()));
			while (strTemp.length() < 8)
				strTemp = '0' + strTemp;

			strRetVal += strTemp;
		} catch (UnknownHostException ex) {
			log.error("Excepcion host desconocido: "+ ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			log.error("Excepcion no se encontro el algoritmo: "+ ex.getMessage());
		}

		return strRetVal.toLowerCase();
	}

	public void actualizaArbol(HttpSession session, String usuario_nombre,ITreeNode treeNode, Set<?> expandedNodes, Set<?> selectedNodes,
			boolean isMyDocs, Documento doc) {
			try{
			ArbolManager amd = new ArbolManager(doc.getTituloAplicacion(),doc.getIdGabinete());
			ITree tree = amd.generaExpediente(usuario_nombre);
			if (tree != null) {
				Iterator<?> i = expandedNodes.iterator();
				while (i.hasNext()) {
					ITreeNode tn = (ITreeNode) i.next();
					tree.expand(tn.getId());
				}
				i = selectedNodes.iterator();
				while (i.hasNext()) {
					ITreeNode tn = (ITreeNode) i.next();
					tree.select(tn.getId());
				}
				session.setAttribute((isMyDocs ? ParametersInterface.TREE_MDC_KEY : ParametersInterface.TREE_EXP_KEY), tree);
			}
		}
		catch(Exception e){
			log.error("Error actualizar Arbol: "+e.toString());
		}
	}
	
	public Boolean eliminarPaginas(HttpSession session,String nodoDocumento, Integer[] indices){
		Boolean result=true;
		try{
			Usuario u = (Usuario)session.getAttribute(ParametersInterface.USER_KEY);
			ITree tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);      	
			ITreeNode treeNode= tree.findNode(nodoDocumento);					
			Documento doc = (Documento)treeNode.getObject();			
			Boolean isMyDocs = "USR_GRALES".equals(doc.getTituloAplicacion());
			
			DocumentoManager dm = new DocumentoManager();
			dm.deletePaginasDocumento(doc, indices, u.getNombreUsuario());
			
			actualizaArbol(session, u.getNombreUsuario(), treeNode,	tree.getExpandedNodes(), tree.getSelectedNodes(), isMyDocs, doc);
		} catch(Exception e){
			log.error(e.toString());
			result = false;
		}
		return result;
	}

}
