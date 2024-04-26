package com.syc.imaxfile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_carpeta;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.fortimax.hibernate.entities.imx_org_carpeta;
import com.syc.fortimax.hibernate.entities.imx_org_carpeta_id;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_org_carpeta_manager;
import com.syc.servlets.models.NodoModel;
import com.syc.tree.ArbolManager;
import com.syc.utils.Utils;

public class CarpetaManager {

	private Carpeta cRoot = null;
	private ITree tree = null;
	private static int lastIdInserted;

	private static final Logger log = Logger.getLogger(CarpetaManager.class);

	public CarpetaManager() throws CarpetaManagerException {
	}

	public CarpetaManager(Carpeta cRoot) throws CarpetaManagerException {

		this.cRoot = cRoot;
	}

	public ITree getTree() {
		return tree;
	}

	public void setTree(ITree tree) {
		this.tree = tree;
	}
	
	public Carpeta getFolder(Carpeta c, String folderName) {
		return getFolder(c, folderName, true, false); //MIN Y !isFirstTime
	}

	public Carpeta getFolder(Carpeta c, String folderName, boolean isFirstTime) {
		return getFolder(c, folderName, isFirstTime, isFirstTime); // MIN = isFirstTime
	}
	
	@SuppressWarnings("unchecked")
	private Carpeta getFolder(Carpeta c, String folderName, boolean MIN, boolean isFirstTime) {
		Carpeta cr = null;
		HibernateManager hm = new HibernateManager();
		try {
			String sQuery = "SELECT titulo_aplicacion, id_gabinete, id_carpeta_padre, "
							+ (MIN ? "MIN" : "MAX")
							+ "(id_carpeta_hija) id_carpeta_hija"
							+ " FROM imx_org_carpeta"
							+ " WHERE titulo_aplicacion = '"
							+ c.getTituloAplicacion()
							+ "' AND id_gabinete = "
							+ c.getIdGabinete()
							+ " AND id_carpeta_padre = "
							+ c.getIdCarpeta()
							+ " AND LOWER(nombre_hija) = '"
							+ folderName.trim().toLowerCase()
							+ "'"
							+ " GROUP BY titulo_aplicacion, id_gabinete, id_carpeta_padre";
			List<Map<String, ?>> list = hm.createSQLQuery(sQuery).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			

			if (list.size()>0 && !isFirstTime) {
				Map<String,?> map = new CaseInsensitiveMap(list.get(0));

				sQuery = "SELECT titulo_aplicacion, id_gabinete, id_carpeta, nombre_carpeta, nombre_usuario"
								+ ", bandera_raiz, fh_creacion, fh_modificacion, numero_accesos, numero_carpetas"
								+ ", numero_documentos, descripcion, password"
								+ " FROM imx_carpeta"
								+ " WHERE titulo_aplicacion = '"
								+ Utils.getString(map.get("titulo_aplicacion"))
								+ "' AND id_gabinete = "
								+ Utils.getInteger(map.get("id_gabinete"))
								+ " AND id_carpeta = "
								+ Utils.getInteger(map.get("id_carpeta_hija"));
				
				list = hm.createSQLQuery(sQuery).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

				if (list.size()>0) {
					int id_carpeta_padre = Utils.getInteger(map.get("id_carpeta_padre"));
					int id_carpeta_hija = Utils.getInteger(map.get("id_carpeta_hija"));
					
					map = new CaseInsensitiveMap(list.get(0));
					cr = new Carpeta(Utils.getString(map.get("titulo_aplicacion")),Utils.getInteger(map.get("id_gabinete")), Utils.getInteger(map.get("id_carpeta")));
					cr.setNombreCarpeta(Utils.getString(map.get("nombre_carpeta")));
					cr.setNombreUsuario(Utils.getString(map.get("nombre_usuario")));
					cr.setBanderaRaiz(Utils.getString(map.get("bandera_raiz")));
					cr.setFechaCreacion(Utils.getDate(map.get("fh_creacion")));
					cr.setFechaModificacion(Utils.getDate(map.get("fh_modificacion")));
					cr.setNumeroAccesos(Utils.getInteger(map.get("numero_accesos")));
					cr.setNumeroCarpetas(Utils.getInteger(map.get("numero_carpetas")));
					cr.setNumeroDocumentos(Utils.getInteger(map.get("numero_documentos")));
					cr.setDescripcion(Utils.getString(map.get("descripcion")));
					cr.setIdCarpetaPadre(id_carpeta_padre);
					cr.setIdCarpetaHija(id_carpeta_hija);
					cr.setNombreHija(folderName);
					cr.setPassword(Utils.getString(map.get("password")));
				}
			} else {
				cr = new Carpeta(c.getTituloAplicacion(), c.getIdGabinete(),c.getIdCarpeta());
				cr.setNombreCarpeta(folderName);
				cr.setNombreUsuario(c.getNombreUsuario());
				cr.setBanderaRaiz("N");
				cr.setFechaCreacion(new Date(System.currentTimeMillis()));
				cr.setFechaModificacion(new Date(System.currentTimeMillis()));
				cr.setNumeroAccesos(0);
				cr.setNumeroCarpetas(0);
				cr.setNumeroDocumentos(0);
				cr.setDescripcion("");
				cr.setIdCarpetaPadre(c.getIdCarpeta());
				cr.setIdCarpetaHija(c.getIdCarpeta());
				cr.setNombreHija(folderName);
				cr.setPassword("-1");
				insertCarpeta(cr);
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return cr;
	}

	@SuppressWarnings("unchecked")
	public Carpeta selectCarpeta(Carpeta c) {
		Carpeta cr = null;
		HibernateManager hm = new HibernateManager();
		try {
			String sQuery = "SELECT titulo_aplicacion, id_gabinete, id_carpeta, nombre_carpeta, nombre_usuario"
							+ ", bandera_raiz, fh_creacion, fh_modificacion, numero_accesos, numero_carpetas"
							+ ", numero_documentos, descripcion, password"
							+ " FROM imx_carpeta"
							+ " WHERE titulo_aplicacion = '"
							+ c.getTituloAplicacion()
							+ "' AND id_gabinete = "
							+ c.getIdGabinete()
							+ " AND id_carpeta = "
							+ c.getIdCarpeta();
			List<Map<String,?>> list = hm.createSQLQuery(sQuery).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

			if (list.size()>0) {
				Map<String,?> map = new CaseInsensitiveMap(list.get(0));
				cr = new Carpeta(Utils.getString(map.get("titulo_aplicacion")),Utils.getInteger(map.get("id_gabinete")), Utils.getInteger(map.get("id_carpeta")));
				cr.setNombreCarpeta(Utils.getString(map.get("nombre_carpeta")));
				cr.setNombreUsuario(Utils.getString(map.get("nombre_usuario")));
				cr.setBanderaRaiz(Utils.getString(map.get("bandera_raiz")));
				cr.setFechaCreacion(Utils.getDate(map.get("fh_creacion")));
				cr.setFechaModificacion(Utils.getDate(map.get("fh_modificacion")));
				cr.setNumeroAccesos(Utils.getInteger(map.get("numero_accesos")));
				cr.setNumeroCarpetas(Utils.getInteger(map.get("numero_carpetas")));
				cr.setNumeroDocumentos(Utils.getInteger(map.get("numero_documentos")));
				cr.setDescripcion(Utils.getString(map.get("descripcion")));
				cr.setPassword(Utils.getString(map.get("password")));
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return cr;
	}


	private synchronized boolean updateRootCarpeta() {
		boolean retVal = false;

		HibernateManager hm = new HibernateManager();
		try {
			
			Query query = hm.createSQLQuery("UPDATE imx_carpeta"
					+ " SET fh_modificacion = ?"
					+ " WHERE titulo_aplicacion = '"
					+ cRoot.getTituloAplicacion() + "' AND id_gabinete = "
					+ cRoot.getIdGabinete() + " AND bandera_raiz = 'S'");
			query.setTimestamp(0, new Timestamp(System.currentTimeMillis()));
			hm.executeQuery(query);
			retVal = true;
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return retVal;
	}
	
	public synchronized List<GetDatosNodo> getHijosNombre(Carpeta c, String nombre_hijo) throws EntityManagerException {
		List<GetDatosNodo> encontrados = new ArrayList<GetDatosNodo>();
		
		String tituloAplicacion = c.getTituloAplicacion();
		int idGabinete = c.getIdGabinete();
		int idCarpetaPadre = c.getIdCarpeta();
		
		imx_org_carpeta_manager imx_org_carpeta_manager = new imx_org_carpeta_manager();
		imx_org_carpeta_manager.select(tituloAplicacion, idGabinete, null, idCarpetaPadre, nombre_hijo);
		List<imx_org_carpeta> imx_org_carpetas = imx_org_carpeta_manager.list();
		for (imx_org_carpeta imx_org_carpeta : imx_org_carpetas) {
			imx_org_carpeta_id id = imx_org_carpeta.getId();
			GetDatosNodo gdn = new GetDatosNodo(id.getTituloAplicacion(), id.getIdGabinete(), id.getIdCarpetaHija());
			encontrados.add(gdn);
		}
		
		imx_documento_manager imx_documento_manager = new imx_documento_manager();
		imx_documento_manager.select(tituloAplicacion, idGabinete, idCarpetaPadre, null).selectNombreDocumento(nombre_hijo);
		List<imx_documento> imx_documentos = imx_documento_manager.list();
		for (imx_documento imx_documento : imx_documentos) {
			imx_documento_id id = imx_documento.getId();
			GetDatosNodo gdn = new GetDatosNodo(id.getTituloAplicacion(), id.getIdGabinete(), id.getIdCarpetaPadre(), id.getIdDocumento());
			encontrados.add(gdn);
		}
		return encontrados;
	}

	public synchronized boolean insertCarpeta(Carpeta c) {
		return insertCarpeta(c, true);
	}

	public synchronized boolean insertSingleFolder(Carpeta c) throws FortimaxException {
		boolean retVal = false;
		HibernateManager hm = new HibernateManager();
		try {
			List<Query> queries = new ArrayList<Query>();
			Query query = hm.createSQLQuery("INSERT INTO imx_carpeta "
							+ "( titulo_aplicacion, id_gabinete, id_carpeta"
							+ ", nombre_carpeta, nombre_usuario, bandera_raiz"
							+ ", fh_creacion, fh_modificacion, numero_accesos"
							+ ", numero_carpetas, numero_documentos, descripcion, password) "
							+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			query.setString(0, c.getTituloAplicacion());
			query.setInteger(1, c.getIdGabinete());
			query.setInteger(2, c.getIdCarpeta());
			query.setString(3, c.getNombreCarpeta());
			
			String nombre_usuario;
			if(c.getNombreUsuario()==null || c.getNombreUsuario().isEmpty()){
				//Si no hay usuario relacionado se inserta string vacío. No debe suceder, pero este escenario resulta de 
				//el uso del ws uploadFile, que permite crear expedientes, si no existen.
				//TODO: Cambiar uploadFile para que envíe usuario.
				nombre_usuario = "WEBSERVICE";
				log.error("Se creará la carpeta " + c.getNombreCarpeta() + " sin usuario relacionado.");
			} else {
				nombre_usuario= c.getNombreUsuario();
			}
			query.setString(4, nombre_usuario);
			query.setString(5, String.valueOf(c.getBanderaRaiz()));
			query.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			query.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			query.setInteger(8, 0);
			query.setInteger(9, 0);
			query.setInteger(10, 0);
			query.setString(11, c.getDescripcion());
			query.setString(12, c.getPassword());

			queries.add(query);

			if ("N".equals(c.getBanderaRaiz())) {
				query = hm.createSQLQuery("INSERT INTO imx_org_carpeta "
								+ "( titulo_aplicacion, id_gabinete, id_carpeta_hija, id_carpeta_padre, nombre_hija) "
								+ "VALUES (?, ?, ?, ?, ?)");
				query.setString(0, c.getTituloAplicacion());
				query.setInteger(1, c.getIdGabinete());
				query.setInteger(2, c.getIdCarpetaHija());
				query.setInteger(3, c.getIdCarpetaPadre());
				query.setString(4, c.getNombreCarpeta());

				queries.add(query);
				
				query = hm.createSQLQuery("UPDATE imx_carpeta"
						+ " SET numero_carpetas = numero_carpetas + 1"
						+ " WHERE titulo_aplicacion = ?"
						+ " AND id_gabinete = ?" + " AND id_carpeta = ?");
				query.setString(0, c.getTituloAplicacion());
				query.setInteger(1, c.getIdGabinete());
				query.setInteger(2, c.getIdCarpetaPadre());
				
				queries.add(query);
			}
			
			hm.executeQueries(queries);
			retVal = true;
		} catch (Exception e) {
			log.error(e, e);
			throw new FortimaxException("Unknow", e.getMessage());
		} finally {
			hm.close();
		}
		return retVal;
	}

	public synchronized boolean insertCarpeta(Carpeta c, boolean updateRoot) {
		boolean retVal = false;
		HibernateManager hm = new HibernateManager();
		try {
			//System.out.println("a entrarrrrrr a update");
			boolean continuar = true;

			if (updateRoot) {
				continuar = updateRootCarpeta();
			}

			if (continuar) {
				List<?> list= hm.createSQLQuery(" SELECT MAX(id_carpeta) FROM imx_carpeta"
								+ " WHERE titulo_aplicacion = '"
								+ c.getTituloAplicacion()
								+ "' AND id_gabinete = " + c.getIdGabinete()).list();

				if (list.isEmpty()) {
					log.error("No existe Titulo Aplicacion("+c.getTituloAplicacion()+"), Gabinete("+c.getIdGabinete()+")");
				}

				c.setIdCarpetaPadre(c.getIdCarpeta());
				int id_carpeta = 0;
				try {
					id_carpeta = Utils.getInteger(list.get(0)) + 1;
				} catch (Exception e) {	
				}
				c.setIdCarpeta(id_carpeta);
				lastIdInserted = c.getIdCarpeta();
				c.setIdCarpetaHija(c.getIdCarpeta());
				
				List<Query> queries = new ArrayList<Query>();

				Query query = hm.createSQLQuery("INSERT INTO imx_carpeta "
								+ "( titulo_aplicacion, id_gabinete, id_carpeta"
								+ ", nombre_carpeta, nombre_usuario, bandera_raiz"
								+ ", fh_creacion, fh_modificacion, numero_accesos"
								+ ", numero_carpetas, numero_documentos, descripcion, password) "
								+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				query.setString(0, c.getTituloAplicacion());
				query.setInteger(1, c.getIdGabinete());
				query.setInteger(2, c.getIdCarpeta());
				query.setString(3, c.getNombreCarpeta());
				query.setString(4, c.getNombreUsuario());
				query.setString(5, String.valueOf(c.getBanderaRaiz()));
				query.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
				query.setTimestamp(7,new Timestamp(System.currentTimeMillis()));
				query.setInteger(8, 0);
				query.setInteger(9, 0);
				query.setInteger(10, 0);
				query.setString(11, c.getDescripcion());
				query.setString(12, c.getPassword());

				queries.add(query);

				if ("N".equals(c.getBanderaRaiz())) {
					query = hm.createSQLQuery("INSERT INTO imx_org_carpeta "
									+ "( titulo_aplicacion, id_gabinete, id_carpeta_hija, id_carpeta_padre, nombre_hija) "
									+ "VALUES (?, ?, ?, ?, ?)");
					query.setString(0, c.getTituloAplicacion());
					query.setInteger(1, c.getIdGabinete());
					query.setInteger(2, c.getIdCarpetaHija());
					query.setInteger(3, c.getIdCarpetaPadre());
					query.setString(4, c.getNombreCarpeta());

					queries.add(query);
				}

				query = hm.createSQLQuery("UPDATE imx_carpeta"
						+ " SET numero_carpetas = numero_carpetas + 1"
						+ " WHERE titulo_aplicacion = ?"
						+ " AND id_gabinete = ?" + " AND id_carpeta = ?");
				query.setString(0, c.getTituloAplicacion());
				query.setInteger(1, c.getIdGabinete());
				query.setInteger(2, c.getIdCarpetaPadre());
				
				queries.add(query);
				
				hm.executeQueries(queries);
				retVal = true;
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return retVal;
	}
	
	public int getLastIdInserted() {
		return lastIdInserted;
	}

	public synchronized boolean deleteCarpeta(Carpeta c) {
		boolean retVal = false;
		HibernateManager hm = new HibernateManager();
		try {
			if (updateRootCarpeta()) {
				List<Query> queries = new ArrayList<Query>();
				
				queries.add(hm.createSQLQuery("DELETE FROM imx_pagina_index"
						+ " WHERE titulo_aplicacion = '"
						+ c.getTituloAplicacion() + "' AND id_gabinete = "
						+ c.getIdGabinete() + " AND id_carpeta_padre = "
						+ c.getIdCarpeta()));

				queries.add(hm.createSQLQuery("DELETE FROM imx_pagina"
						+ " WHERE titulo_aplicacion = '"
						+ c.getTituloAplicacion() + "' AND id_gabinete = "
						+ c.getIdGabinete() + " AND id_carpeta_padre = "
						+ c.getIdCarpeta()));

				queries.add(hm.createSQLQuery("DELETE FROM imx_documento"
						+ " WHERE titulo_aplicacion = '"
						+ c.getTituloAplicacion() + "' AND id_gabinete = "
						+ c.getIdGabinete() + " AND id_carpeta_padre = "
						+ c.getIdCarpeta()));

				queries.add(hm.createSQLQuery("DELETE FROM imx_org_carpeta"
						+ " WHERE titulo_aplicacion = '"
						+ c.getTituloAplicacion() + "' AND id_gabinete = "
						+ c.getIdGabinete() + " AND id_carpeta_hija = "
						+ c.getIdCarpeta()));

				queries.add(hm.createSQLQuery("DELETE FROM imx_carpeta"
						+ " WHERE titulo_aplicacion = '"
						+ c.getTituloAplicacion() + "' AND id_gabinete = "
						+ c.getIdGabinete() + " AND id_carpeta = "
						+ c.getIdCarpeta()));

				queries.add(hm.createSQLQuery("UPDATE imx_carpeta"
						+ " SET numero_carpetas = numero_carpetas - 1"
						+ " WHERE titulo_aplicacion = '"
						+ c.getTituloAplicacion() + "' AND id_gabinete = "
						+ c.getIdGabinete() + " AND id_carpeta = "
						+ c.getIdCarpetaPadre()));

				hm.executeQueries(queries);
				retVal = true;
			}
		} catch (Exception e) {
			log.error(e,e);
		} finally {
			hm.close();
		}
		return retVal;
	}

	public Object[] getOrgCarpetaOnName(String titulo_aplicacion, int idGabinete, String folderName) throws Exception {
		// VALIDACIONES
		titulo_aplicacion = StringUtils.trimToNull(titulo_aplicacion);
		if (titulo_aplicacion == null)
			throw new Exception(
					"Par?metro titulo_aplicacion inv?lido en getIdCarpetaOnName");

		if (idGabinete < 0)
			throw new Exception(
					"Par?metro idGabinete inv?lido en getIdCarpetaOnName");

		folderName = StringUtils.trimToNull(folderName);
		if (folderName == null)
			throw new Exception(
					"Par?metro folderName inv?lido en getIdCarpetaOnName");

		int idCarpetaPadre = -1;
		String nombreCarpetaPadre = null;
		int idCarpetaHija = -1;
		String nombreCarpetaHija = null;
		String sSQL = "SELECT "
				+ "C_PADRE.ID_CARPETA AS id_carpeta_padre, "
				+ "C_PADRE.NOMBRE_CARPETA AS nombre_carpeta_padre, "
				+ "C_HIJA.ID_CARPETA AS id_carpeta_hija, "
				+ "C_HIJA.NOMBRE_CARPETA AS nombre_carpeta_hija "
				+ "FROM "
				+ "IMX_CARPETA C_PADRE "
				+ "INNER JOIN "
				+ "IMX_ORG_CARPETA C_ORG "
				+ "ON "
				+ "C_ORG.ID_GABINETE = C_PADRE.ID_GABINETE AND "
				+ "C_ORG.TITULO_APLICACION = C_PADRE.TITULO_APLICACION AND "
				+ "C_ORG.ID_CARPETA_PADRE = C_PADRE.ID_CARPETA "
				+ "INNER JOIN "
				+ "IMX_CARPETA C_HIJA "
				+ "ON "
				+ "C_ORG.ID_GABINETE = C_HIJA.ID_GABINETE AND "
				+ "C_ORG.TITULO_APLICACION = C_HIJA.TITULO_APLICACION AND "
				+ "C_ORG.ID_CARPETA_HIJA = C_HIJA.ID_CARPETA "
				+ "WHERE C_PADRE.TITULO_APLICACION = ? AND C_PADRE.ID_GABINETE = ? AND C_HIJA.NOMBRE_CARPETA = ?";

		HibernateManager hm = new HibernateManager();
		try {
			Query query = hm.createSQLQuery(sSQL);
			query.setString(0, titulo_aplicacion.trim());
			query.setInteger(1, idGabinete);
			query.setString(2, folderName.trim());
			log.debug("{" + titulo_aplicacion.trim() + ", " + idGabinete + ", " + folderName.trim() + "}");
			
			@SuppressWarnings("unchecked")
			List<Map<String,?>> list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			if (list.size()>0) {
				@SuppressWarnings("unchecked")
				Map<String,?> map = new CaseInsensitiveMap(list.get(0));
				idCarpetaPadre = Utils.getInteger(map.get("id_carpeta_padre"));
				nombreCarpetaPadre = Utils.getString(map.get("nombre_carpeta_padre"));
				idCarpetaHija = Utils.getInteger(map.get("id_carpeta_hija"));
				nombreCarpetaHija = Utils.getString(map.get("nombre_carpeta_hija"));
			}
		} catch (Exception e) {
			log.error(e,e);
		}

		log.trace("return de CarpetaManager.getOrgCarpetaOnName: {"
				+ idCarpetaPadre + "," + nombreCarpetaPadre + " , "
				+ idCarpetaHija + ", " + nombreCarpetaHija + "}");
		return new Object[] { idCarpetaPadre, nombreCarpetaPadre,
				idCarpetaHija, nombreCarpetaHija };
	}
	
	public static int getNewIdCarpeta(String gaveta, int id_gabinete) {
		int id_carpeta = 0;
		String sCount = "SELECT count(id_carpeta) FROM imx_carpeta where titulo_aplicacion ='"+gaveta+"' and id_gabinete= "+id_gabinete;
		String sMax = "SELECT MAX(id_carpeta)+1 FROM imx_carpeta where titulo_aplicacion ='"+gaveta+"' and id_gabinete= "+id_gabinete;
		HibernateManager hm = new HibernateManager();
		try{	
			if(Utils.getInteger(hm.createSQLQuery(sCount).uniqueResult())>0)
				id_carpeta = Utils.getInteger(hm.createQuery(sMax).uniqueResult());
		} catch (Exception e) {
			log.error(e,e);
		} finally {
			hm.close();
		}
		return id_carpeta;
	}
	
	// TODO optimizar para carpetas anidadas y contrase;as :S
	// no se hizo por falta de tiempo xD
	public static int createCarpeta(String gaveta, int id_gabinete, String carpeta) {
		int id = -1;
		
		int tmp_id = -1;
		HibernateManager hm = new HibernateManager();
		
		tmp_id = getNewIdCarpeta(gaveta, id_gabinete);
		
		log.debug("El nuevo id sera " + tmp_id);

		String contrasena = "-1";

		String[] spl = StringUtils.split(carpeta, '?');

		carpeta = spl[0];

		StringBuffer hexString = new StringBuffer();
		hexString.append("-1");
		if (spl.length >= 2) {
			contrasena = spl[1];

			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");

				md.reset();
				md.update(contrasena.getBytes());
				byte messageDigest[] = md.digest();

				hexString = new StringBuffer();
				for (int i = 0; i < messageDigest.length; i++) {
					String val = Integer.toHexString(0xFF & messageDigest[i]);
					while (val.length() < 2) {
						val = "0" + val;
					}
					hexString.append(val);
				}
			} catch (NoSuchAlgorithmException e) {
				log.error(e, e);
			}

		}
		try {		
			List<Query> queries = new ArrayList<Query>();
			
			Query sqlQuery = hm.createSQLQuery("INSERT INTO imx_carpeta (" +
					"titulo_aplicacion, " +
					"id_gabinete, " +
					"id_carpeta, " +
					"nombre_carpeta, " +
					"nombre_usuario, " +
					"bandera_raiz, " +
					"fh_creacion, " +
					"fh_modificacion, " +
					"numero_accesos, " +
					"numero_carpetas, " +
					"numero_documentos, " +
					"descripcion, " +
					"password) " +
					"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			sqlQuery.setString(0, gaveta);
			sqlQuery.setInteger(1, id_gabinete);
			sqlQuery.setInteger(2, tmp_id);
			sqlQuery.setString(3, carpeta);
			sqlQuery.setString(4, " ");
			sqlQuery.setString(5, "N");
			sqlQuery.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			sqlQuery.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			sqlQuery.setInteger(8, 0);
			sqlQuery.setInteger(9, 0);
			sqlQuery.setInteger(10, 0);
			sqlQuery.setString(11, " ");
			sqlQuery.setString(12, hexString.toString());
			
			log.debug("Creando carpeta "+ carpeta +"...");
				
			queries.add(sqlQuery);
			
			StringBuffer query = new StringBuffer();
			query.append("INSERT INTO imx_org_carpeta (titulo_aplicacion, id_gabinete, id_carpeta_hija, id_carpeta_padre, nombre_hija) VALUES ('");
			query.append(gaveta);
			query.append("',");
			query.append(id_gabinete);
			query.append(",");
			query.append(tmp_id);
			query.append(",0,'");
			query.append(carpeta);
			query.append("')");

			sqlQuery = hm.createSQLQuery(query.toString());
			
			queries.add(sqlQuery);
			
			hm.executeQueries(queries);

			id = tmp_id;		
		} finally {
			hm.close();
		}
		return id;
	}

	/**
	 * Devuelve todas las carpetas de un expediente, sin tomar en cuenta su
	 * jerarquia en la estructura de documentos. Se puede evitar recibir
	 * también la raiz mediante el parámetro
	 * <code>includeRoot</code>
	 * @param titulo_aplicacion
	 *            Titulo de la aplicación origen.
	 * @param id_gabinete
	 *            ID Gabinete del expediente
	 * @param includeRoot
	 *            <code>true</code> Devuelve el listado completo de las carpetas
	 *            del expediente incluida la raiz.
	 *            <code>false</code> Devuelve las carpetas dentro de la
	 *            raiaz.
	 * 
	 * @return Lista de objetos carpeta, que contiene el total de carpetas del
	 *         expediente.
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public List<Carpeta> getExpedientFolders(String titulo_aplicacion, int id_gabinete, boolean includeRoot) throws FortimaxException {
		List<Carpeta> folders = null;
		String query = "SELECT c.titulo_aplicacion, "
				+ "  c.id_gabinete, " + "  c.id_carpeta, "
				+ "  c.nombre_carpeta, " + "  c.nombre_usuario, "
				+ "  c.bandera_raiz, " + "  c.fh_creacion, "
				+ "  c.fh_modificacion, " + "  c.numero_accesos, "
				+ "  c.numero_carpetas, " + "  c.numero_documentos, "
				+ "  c.descripcion, " + "  c.password, "
				+ "  oc.id_carpeta_hija, " + "  oc.id_carpeta_padre, "
				+ "  oc.nombre_hija " + "FROM imx_carpeta c, "
				+ "  imx_org_carpeta oc "
				+ "WHERE c.titulo_aplicacion = oc.titulo_aplicacion "
				+ "AND c.id_gabinete         = oc.id_gabinete "
				+ "AND c.id_carpeta          = oc.id_carpeta_hija "
				+ "AND c.titulo_aplicacion   = '" + titulo_aplicacion
				+ "' " + "AND c.id_gabinete         =  " + id_gabinete
				+ " ORDER BY c.id_carpeta";
		
		HibernateManager hm = new HibernateManager();
		try {
			List<Map<String,?>> list = hm.createSQLQuery(query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

			for(Map<String,?> map : list) {
				map = new CaseInsensitiveMap(map);
				
				Carpeta cr  = new Carpeta(Utils.getString(map.get("titulo_aplicacion")),Utils.getInteger(map.get("id_gabinete")), Utils.getInteger(map.get("id_carpeta")));
				cr.setNombreCarpeta(Utils.getString(map.get("nombre_carpeta")));
				cr.setNombreUsuario(Utils.getString(map.get("nombre_usuario")));
				cr.setBanderaRaiz(Utils.getString(map.get("bandera_raiz")));
				cr.setFechaCreacion(new Date(System.currentTimeMillis()));
				cr.setFechaModificacion(new Date(System.currentTimeMillis()));
				cr.setNumeroAccesos(Utils.getInteger(map.get("numero_accesos")));
				cr.setNumeroCarpetas(Utils.getInteger(map.get("numero_carpetas")));
				cr.setNumeroDocumentos(Utils.getInteger(map.get("numero_documentos")));
				cr.setDescripcion(Utils.getString(map.get("descripcion")));
				cr.setPassword(Utils.getString(map.get("password")));
				cr.setIdCarpetaHija(Utils.getInteger(map.get("id_carpeta_hija")));
				cr.setIdCarpetaPadre(Utils.getInteger(map.get("id_carpeta_padre")));

				if (folders == null)
					folders = new ArrayList<Carpeta>();

				folders.add(cr);
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new FortimaxException(e);
		} finally {
			hm.close();
		}
		return folders;
	}

	public static boolean preInsert(imx_carpeta imx_carpeta) {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean isValid(imx_documento imx_documento) {
		boolean cancelInsert = true;
		String nodo = new GetDatosNodo(imx_documento.getId().getTituloAplicacion(),imx_documento.getId().getIdGabinete(),imx_documento.getId().getIdCarpetaPadre()).toString();
		ArbolManager arbolManager = new ArbolManager(nodo);
		ITreeNode root = arbolManager.getChildren(null,true).getRoot();
		List<NodoModel> children = new NodoModel(root).children;
		
		if(children!=null)
			for(NodoModel child : children) {
				if(imx_documento.getNombreDocumento().equalsIgnoreCase(child.text))
					return false;
			}
		return cancelInsert;
	}
}
