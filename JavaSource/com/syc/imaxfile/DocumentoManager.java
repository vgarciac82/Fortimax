package com.syc.imaxfile;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_carpeta;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.fortimax.hibernate.entities.imx_documentoextend;
import com.syc.fortimax.hibernate.entities.imx_pagina;
import com.syc.fortimax.hibernate.entities.imx_pagina_index;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_pagina_index_manager;
import com.syc.fortimax.hibernate.managers.imx_pagina_manager;
import com.syc.fortimax.lucene.DeleteFiles;
import com.syc.utils.DateTimeUtils;
import com.syc.utils.Utils;

public class DocumentoManager {

	// Para loguear las acciones con documentos.
	private static Logger log = Logger.getLogger(DocumentoManager.class);

	private static int lastIdInserted;

	public DocumentoManager() throws DocumentoManagerException {

	}

	public Documento getDocumento(Carpeta c, String fileName) {

		Documento dr = null;
		String filename = fileName;
		String ext = "";

		int pos = fileName.lastIndexOf('.');
		if (pos > -1) {
			filename = fileName.substring(0, pos);
			ext = fileName.substring(pos + 1);
		}

		filename = filename.toLowerCase();

		try {
			
			HibernateManager hm = new HibernateManager();
			Query q = null;	
					
			String restriccion =" AND SUBSTRING(p.nom_archivo_org, INSTR(p.nom_archivo_org, '.') + 1, 3) = ?"; 
			if(Config.database.equals(Config.Database.ORACLE)) //Crear este query con ANSI-SQL
				restriccion = " AND SUBSTR(p.nom_archivo_org, INSTR(p.nom_archivo_org, '.') + 1, 3) = ?";
			 String sSelect = 
						"SELECT d.titulo_aplicacion,d.id_gabinete,d.id_carpeta_padre,d.id_documento,d.nombre_documento,p.pagina "
						+" FROM imx_documento d, imx_pagina p"
						+" WHERE p.titulo_aplicacion = d.titulo_aplicacion"
						+" AND p.id_gabinete = d.id_gabinete"
						+" AND p.id_carpeta_padre = d.id_carpeta_padre"
						+" AND p.id_documento = d.id_documento"
						+" AND d.titulo_aplicacion = ?"
						+" AND d.id_gabinete =?"
						+" AND d.id_carpeta_padre = ?"
						+" AND LOWER(d.nombre_documento) = ?"
						+ restriccion
						+" GROUP BY d.titulo_aplicacion,d.id_gabinete,d.id_carpeta_padre,d.id_documento,d.nombre_documento,p.pagina";
												
				q = hm.createSQLQuery(sSelect);				
				q.setString(0, c.getTituloAplicacion());
				q.setInteger(1, c.getIdGabinete());
				q.setInteger(2, c.getIdCarpeta());
				q.setString(3, filename);
				q.setString(4, ext);
				q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				@SuppressWarnings("unchecked")
				Map<String, String> documento = (Map<String,String>)q.uniqueResult();
				
				if(documento == null)
				{
					dr = null;	
				}
				else
				{
					
					DocumentoManager dm = new DocumentoManager();
					dr = dm.selectDocumento(c.getTituloAplicacion(), c.getIdGabinete(), 
							Utils.getInteger(documento.get("id_carpeta_padre")), 
							Utils.getInteger(documento.get("id_documento")));
					dr.setExtension("imx".equals(ext) ? "" : ext);
				}
					
		} catch (Exception se) {
			log.error(se, se);

		} finally {
			
		}
		return dr;
	}

	@SuppressWarnings("unchecked")
	public List<Documento> selectDocumentosExpediente(String titulo_aplicacion, int id_gabinete) throws Exception {

		Documento d = null;
		List<Documento> result = new ArrayList<Documento>();
		HibernateManager hm = new HibernateManager();
		Query q = null;	
		
		try {
			
			
			String sSelect = "SELECT d.nombre_documento, d.nombre_usuario, d.prioridad, d.id_tipo_docto, d.fh_creacion,"
					+ " d.fh_modificacion, d.numero_accesos, d.numero_paginas, d.titulo, d.autor, d.materia,"
					+ " d.descripcion, d.clase_documento, d.estado_documento, d.tamano_bytes,"
					+ " t.nombre_tipo_docto, d.compartir, d.token_compartir,d.FECHA_EXPIRA,"
					+ " a.descripcion as apdesc, c.nombre_carpeta, d.ID_CARPETA_PADRE, d.ID_DOCUMENTO "
					+ " FROM imx_documento d, imx_tipo_documento t,"
					+ "  imx_aplicacion a, imx_carpeta c "
					+ " WHERE t.titulo_aplicacion = d.titulo_aplicacion"
					+ " AND t.id_tipo_docto = d.id_tipo_docto"
					+ " AND a.titulo_aplicacion = d.titulo_aplicacion"
					+ " AND d.id_gabinete = c.id_gabinete"
					+ " AND d.id_carpeta_padre = c.id_carpeta"
					+ " AND d.titulo_aplicacion = c.titulo_aplicacion"
					+ " AND d.id_gabinete = c.id_gabinete"
					+ " AND t.id_tipo_docto = d.id_tipo_docto"
					+ " AND d.titulo_aplicacion = ?"
					+ " AND d.id_gabinete = ?";
											
			q = hm.createSQLQuery(sSelect);				
			q.setString(0, titulo_aplicacion);
			q.setInteger(1, id_gabinete);
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> DocumentosExpediente = (List<Map<String,String>>)q.list();
			
			for(Map<String,String> DocumentoExpediente : DocumentosExpediente) {
				DocumentoExpediente = new CaseInsensitiveMap(DocumentoExpediente);
				
				
				d = new Documento(titulo_aplicacion, id_gabinete, 
						Utils.getInteger(DocumentoExpediente.get("id_carpeta_padre")), 
						Utils.getInteger(DocumentoExpediente.get("id_documento")));
				
				d.setNombreDocumento(Utils.getString(DocumentoExpediente.get("nombre_documento")));
				d.setNombreUsuario(Utils.getString(DocumentoExpediente.get("nombre_usuario")));
				d.setPrioridad(Utils.getInteger(DocumentoExpediente.get("prioridad")));
				d.setIdTipoDocto(Utils.getInteger(DocumentoExpediente.get("id_tipo_docto")));
				d.setNombreTipoDocto(Utils.getString(DocumentoExpediente.get("nombre_tipo_docto")));
				d.setFechaCreacion(Utils.getDate(DocumentoExpediente.get("fh_creacion")));
				d.setFechaModificacion(Utils.getDate(DocumentoExpediente.get("fh_modificacion")));
				d.setNumeroAccesos(Utils.getInteger(DocumentoExpediente.get("numero_accesos")));
				d.setNumeroPaginas(Utils.getInteger(DocumentoExpediente.get("numero_paginas")));
				d.setTitulo(Utils.getString(DocumentoExpediente.get("titulo")));
				d.setAutor(Utils.getString(DocumentoExpediente.get("autor")));
				d.setMateria(Utils.getString(DocumentoExpediente.get("materia")));
				d.setDescripcion(Utils.getString(DocumentoExpediente.get("descripcion")));
				d.setClaseDocumento(Utils.getInteger(DocumentoExpediente.get("clase_documento")));
				d.setEstadoDocumento(Utils.getString(DocumentoExpediente.get("estado_documento")));
				d.setTamanoBytes(Utils.getInteger(DocumentoExpediente.get("tamano_bytes")));
				d.setCompartir(Utils.getString(DocumentoExpediente.get("compartir")));
				d.setTokenCompartir(Utils.getString(DocumentoExpediente.get("token_compartir")));
				Date fechaExpira = Utils.getDate(DocumentoExpediente.get("FECHA_EXPIRA"));
				if (fechaExpira!=null) {
					d.setDateExp(DateTimeUtils.transformFromSQLDate(Utils.getDate(DocumentoExpediente.get("FECHA_EXPIRA"))));				
					d.setHoureExp(DateTimeUtils.transformFromSQLTime(new java.sql.Time(Utils.getDate(DocumentoExpediente.get("FECHA_EXPIRA")).getTime())));
				}
				d.setNombreAplicacion(Utils.getString(DocumentoExpediente.get("apdesc")));
				d.setNombreCarpeta(Utils.getString(DocumentoExpediente.get("nombre_carpeta")));
				d.setNumeroPaginas((Utils.getInteger(DocumentoExpediente.get("numero_paginas"))));
				
				
				if (d.getTokenCompartir() != null && (d.getTokenCompartir().trim() != "")) {					
		            imx_documentoextend imxdocumentoextend =null;					
		            q = hm.createQuery("FROM imx_documentoextend WHERE TOKEN_COMPARTIR = :TOKEN_COMPARTIR");
					q.setString("TOKEN_COMPARTIR", d.getTokenCompartir());		      
					imxdocumentoextend = (imx_documentoextend)q.uniqueResult();

					if (imxdocumentoextend == null) 
					{
						log.warn("No se encontro el token compartir en documentoextend");
					}
						else
					{
						d.setLigaPermisoBajar(imxdocumentoextend.getBanderaDescarga());
					}

				}
				
				result.add(d);

			}
			return result;

		} catch (Exception se) {
			log.error(se, se);
			throw new FortimaxException("Error al obtener la informacion del documento", se.getMessage());
		} finally {
			hm.close();
		}
	}

	public Documento selectDocumento(String selectedNode) {
		GetDatosNodo gdn = new GetDatosNodo(selectedNode);
		gdn.separaDatosDocumento();

		Documento d = null;
		try {
			d = selectDocumento(gdn.getGaveta(), gdn.getGabinete(),
					gdn.getIdCarpeta(), gdn.getIdDocumento());
		} 
		catch (HibernateException e) {
			log.error(e,e);
			
		} catch (Exception e) {
			log.error(e,e);
		}
		
		return d;
	}

	@SuppressWarnings("unchecked")
	public Documento selectDocumento(String titulo_aplicacion, int id_gabinete,int id_carpeta_padre, int id_documento)  {

		HibernateManager hm = new HibernateManager();
		Query q = null;		
		Documento d = null;

		try {
			

			imx_documento imxdocumento;
			imxdocumento = imx_documento_manager.selectDocument(titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento);
			
			if(imxdocumento==null)
			{
				log.warn("El documento solicitado no se encontro");
			}
			else
			{				
				
				String DateExp = "";						
				String HoureExp= "";
				if(imxdocumento.getFechaExpira()!=null)
				{
					DateExp = DateTimeUtils.transformFromSQLDate(new java.sql.Date(imxdocumento.getFechaExpira().getTime()));							
					HoureExp = DateTimeUtils.transformFromSQLTime(new java.sql.Time(imxdocumento.getFechaExpira().getTime()));
				}
				
				d = new Documento(titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento);
				d.setNombreDocumento(imxdocumento.getNombreDocumento());
				d.setNombreUsuario(imxdocumento.getNombreUsuario());
				d.setPrioridad(imxdocumento.getPrioridad());
				d.setIdTipoDocto(imxdocumento.getIdTipoDocumento());
				d.setNombreTipoDocto(d.getNombreTipoDocto(imxdocumento.getIdTipoDocumento())); //Estocampo no deberia exisitir basta con el ID tipo.
				d.setFechaCreacion(new Date(imxdocumento.getFhCreacion().getTime()));
				d.setFechaModificacion(new Date(imxdocumento.getFhModificacion().getTime()));
				d.setNumeroAccesos(imxdocumento.getNumeroAccesos());
				d.setNumeroPaginas(imxdocumento.getNumeroPaginas());
				d.setTitulo(imxdocumento.getTitulo());
				d.setAutor(imxdocumento.getAutor());
				d.setMateria(imxdocumento.getMateria());
				d.setDescripcion(imxdocumento.getDescripcion());
				d.setClaseDocumento(imxdocumento.getClaseDocumento());
				d.setEstadoDocumento(imxdocumento.getEstadoDocumento().toString());
				d.setTamanoBytes(imxdocumento.getTamanoBytes());
				d.setCompartir(imxdocumento.getCompartir());
				d.setTokenCompartir(imxdocumento.getTokenCompartir());				
				d.setDateExp(DateExp);							
				d.setHoureExp(HoureExp);
				d.setNombreAplicacion(imxdocumento.getId().getTituloAplicacion()); //aqui se deberia setear la descripcion de la gaveta
						
				// Integra liga con permisos de compartir
							
				//TODO: Esto deberia estar en un imx_carpeta_manager
	            imx_carpeta imxcarpeta =null;
				
	            q = hm.createQuery("FROM imx_carpeta WHERE titulo_aplicacion = :titulo_aplicacion "
				+ "AND id_gabinete = :id_gabinete AND id_carpeta = :id_carpeta");
				
				q.setString("titulo_aplicacion", titulo_aplicacion);
				q.setInteger("id_gabinete", id_gabinete);
				q.setInteger("id_carpeta", id_carpeta_padre); //La carepta padre es la carpeta del documento
	      
				imxcarpeta = (imx_carpeta)q.uniqueResult();
				
				
				if(imxcarpeta==null)
				{
					log.warn("No se encontro la carpeta solicitada");
				}
				else
				{
					//Verificar si realmente es necesario este dato en obj "Documento"
					d.setNombreCarpeta(imxcarpeta.getNombreCarpeta());		
				}
			
				if (d.getTokenCompartir() != null && (d.getTokenCompartir().trim() != "")) {
					
		            imx_documentoextend imxdocumentoextend =null;
					
		            q = hm.createQuery("FROM imx_documentoextend WHERE TOKEN_COMPARTIR = :TOKEN_COMPARTIR");
					q.setString("TOKEN_COMPARTIR", d.getTokenCompartir());		      
					imxdocumentoextend = (imx_documentoextend)q.uniqueResult();

					if (imxdocumentoextend == null) 
					{
						log.warn("No se encontro el token compartir en documentoextend");
					}
						else
					{
						d.setLigaPermisoBajar(imxdocumentoextend.getBanderaDescarga());
					}

				}

				String sSelect = "SELECT p.numero_pagina, p.tipo_pagina, p.volumen, p.tipo_volumen, v.unidad_disco,"
						+ " v.ruta_base, v.ruta_directorio, p.nom_archivo_vol, p.nom_archivo_org,"
						+ " p.estado_pagina, p.tamano_bytes"
						+ " FROM imx_pagina p, imx_volumen v"
						+ " WHERE v.volumen = p.volumen"
						+ " AND p.titulo_aplicacion = ?"
						+ " AND p.id_gabinete = ?"
						+ " AND p.id_carpeta_padre = ?"
						+ " AND p.id_documento = ?"
						+ " ORDER BY p.pagina asc";
												
				q = hm.createSQLQuery(sSelect);				
				q.setString(0, titulo_aplicacion);
				q.setInteger(1, id_gabinete);
				q.setInteger(2, id_carpeta_padre);
				q.setInteger(3, id_documento);
				q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			
				List<Map<String, String>> paginasconvolumen = (List<Map<String,String>>)q.list();
				
				boolean prvez = true;
				List<Pagina> lp = new ArrayList<Pagina>();
				
				for(Map<String,String> paginaconvolumen : paginasconvolumen) {
					paginaconvolumen = new CaseInsensitiveMap(paginaconvolumen);					

					Pagina p = new Pagina(titulo_aplicacion, id_gabinete,id_carpeta_padre, id_documento,
							Utils.getInteger(paginaconvolumen.get("numero_pagina")),
							Utils.getString(paginaconvolumen.get("volumen")),
							Utils.getString(paginaconvolumen.get("tipo_volumen")), 
							Utils.getString(paginaconvolumen.get("nom_archivo_vol").trim()), 
							Utils.getString(paginaconvolumen.get("nom_archivo_org").trim()),
							Utils.getString(paginaconvolumen.get("tipo_pagina")), 
							null,//Anotaciones, deberia ser seteado o eliminado el campo si no se usa.
							Utils.getString(paginaconvolumen.get("estado_pagina")),
							Utils.getDouble(paginaconvolumen.get("tamano_bytes")));

					p.setUnidadDisco(paginaconvolumen.get("unidad_disco").trim());
					p.setRutaBase(paginaconvolumen.get("ruta_base").trim());
					p.setRutaDirectorio(paginaconvolumen.get("ruta_directorio").trim());
					
					
					lp.add(p);

					if ((prvez) && (!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (Utils.getString(paginaconvolumen.get("nom_archivo_org")) != null)) {
						prvez = false;
						int pos = Utils.getString(paginaconvolumen.get("nom_archivo_org")).lastIndexOf(".");
						d.setExtension((pos > -1) ?Utils.getString(paginaconvolumen.get("nom_archivo_org")).substring(pos + 1) : "");
					}
				
				}
			
				Pagina ps [] = lp.toArray(new Pagina [lp.size()]);
				d.setNumeroPaginas(lp.size());
				d.setPaginasDocumento(ps);
			}
			if (d!= null) {
				log.info("[Consultando Documento: " + d.getNombreDocumento()
						+ "] Gaveta: " + d.getTituloAplicacion() + " Carpeta: "
						+ d.getNombreCarpeta() + " Usuario: "
						+ d.getNombreUsuario());
			}
		
		}  
		catch (Exception se) {
			log.error(se, se);
		}
		
		finally {
			hm.close();
		}
		return d;
	}

	public synchronized boolean insertDocumento(Documento d) throws Exception {
		boolean ok = false;
		imx_documento_id id = null;

		Session s = null;
		try {
			s = HibernateUtils.getSession();
			s.beginTransaction();

			//TODO: Cambiar uploadFile para que envíe usuario.
			String nombre_usuario;
			if(d.getNombreUsuario()==null || d.getNombreUsuario().isEmpty()){
				//Si no hay usuario relacionado se inserta string vacío. No debe suceder, pero este escenario resulta de 
				//el uso del ws uploadFile, que permite crear expedientes, si no existen.
				nombre_usuario = "WEBSERVICE";
				log.error("Se creará el documento " + d.getNombreDocumento() + " con usuario genérico: WEBSERVICE");
			} else {
				nombre_usuario= d.getNombreUsuario();
			}
			
			d.setNombreUsuario(nombre_usuario);
			id = imx_documento_manager.insertDocument(s,
					new imx_documento(d), d.getIdDocumento()==-1?null:d.getIdDocumento());
			lastIdInserted = id.getIdDocumento();
			d.setIdDocumento(id.getIdDocumento());
			
			s.getTransaction().commit();
		} catch (Exception E) {
			log.error(E, E);

			if (s != null) {
				s.getTransaction().rollback();
			}
			throw E;
		} finally {
			if (s != null && s.isOpen()) {
				s.close();
			}
		}

		if (id != null) {
			ok = true;
		}

		return ok;
	}

	public int getLastIdInserted() {
		return lastIdInserted;
	}

	public synchronized void insertPaginaDocumento(Volumen v, Documento d,
			String nombreArchivoOriginal, String nombreArchivoModificado, String tipo_pagina,
			double tamano_bytes) throws DocumentoManagerException 
	{
		insertPaginaDocumento(v, d, nombreArchivoOriginal, nombreArchivoModificado, tipo_pagina,
				tamano_bytes, Config.getLuceneIndex());
	}
//
//	public synchronized void insertPaginaDocumento(
//			Volumen v, Documento d, String nombreArchivoOriginal, String nombreArchivoModificado,
//			String tipo_pagina, double tamano_bytes, String indexPath)
//			throws DocumentoManagerException {
//		insertPaginaDocumento(v, d, nombreArchivoOriginal, nombreArchivoModificado,
//				nombreArchivoModificado, tipo_pagina, tamano_bytes, indexPath);
//
//	}

	@SuppressWarnings("unchecked")
	public synchronized void insertPaginaDocumento(Volumen v, Documento d,
			String nombreArchivoOriginal, String nombreArchivoModificado,
			String tipo_pagina, double tamano_bytes, String indexPath)
			throws DocumentoManagerException {
		
        HibernateManager hm = new HibernateManager();
        int numeroPagina = 0;
		int pagina =  0;
		
		try {
					
			
			Query q = hm.createSQLQuery("SELECT max(numero_pagina) as numero_pagina, max(pagina) as pagina FROM imx_pagina WHERE titulo_aplicacion = ? "
		 			+ "AND id_gabinete = ? AND id_carpeta_padre = ? AND id_documento = ?");
		 			
		 			q.setString(0, d.getTituloAplicacion());
		 			q.setInteger(1, d.getIdGabinete());
		 			q.setInteger(2, d.getIdCarpetaPadre());
		 			q.setInteger(3, d.getIdDocumento());
		 			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		       
		 
		 			Map<String, String> imxpagina = (Map<String,String>)q.uniqueResult();
		 			
		 			imxpagina = new CaseInsensitiveMap(imxpagina);
					if (imxpagina.get("pagina") == null) {
						
						log.warn("No se encontro ninguna Pagina: "
			        			+" titulo_aplicacion "+ d.getTituloAplicacion()
			        			+" id_gabinete "+ d.getIdGabinete()
			        			+" id_carpeta_padre "+ d.getIdCarpetaPadre()
			        			+" id_documento "+  d.getIdDocumento());
						pagina =  1;
					}
					if (imxpagina.get("numero_pagina") == null) {
						numeroPagina = 1;
					} 
					else
					{
						numeroPagina = Utils.getInteger(imxpagina.get("numero_pagina")) + 1;
						pagina = Utils.getInteger(imxpagina.get("pagina")) + 1;
					}	
						String sInsert ="INSERT INTO imx_pagina "
								+ "(titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento, numero_pagina, pagina, volumen, "
								+ "tipo_volumen, nom_archivo_vol, nom_archivo_org, tipo_pagina, anotaciones, estado_pagina, tamano_bytes) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

						Query sqlQuery = hm.createSQLQuery(sInsert);
						
						sqlQuery.setString(0, d.getTituloAplicacion());
						sqlQuery.setInteger(1, d.getIdGabinete());
						sqlQuery.setInteger(2, d.getIdCarpetaPadre());
						sqlQuery.setInteger(3, d.getIdDocumento());
						sqlQuery.setInteger(4, numeroPagina);
						sqlQuery.setInteger(5, pagina);
						sqlQuery.setString(6, v.getVolumen());
						sqlQuery.setString(7, v.getTipoVolumen());
						sqlQuery.setString(8, nombreArchivoModificado + ".tif");
						sqlQuery.setString(9, nombreArchivoOriginal);
						sqlQuery.setString(10, tipo_pagina);
						sqlQuery.setString(11, "");
						sqlQuery.setString(12, "V");
						sqlQuery.setDouble(13, tamano_bytes);
						
						hm.executeQuery(sqlQuery);
						
						d.setNumeroPaginas(d.getNumeroPaginas() + 1);
		
						long time = System.currentTimeMillis();
						d.setFechaModificacion(new Date(time));
						time += (1000L * 60L);
						String files[] = d.getFullPathFilesNames();
						for (int i = 0; i < files.length; i++) {
							File f = new File(files[i]);
							f.setLastModified(time);
							time += (1000L * 60L);
						}
		
						updateDocumento(d);		
						// Hibernate Indexacion
						try {
		
							imx_pagina_index_manager ControlIndex = new imx_pagina_index_manager();
		
							imx_pagina_index DatosIndex = new imx_pagina_index(
									d.getTituloAplicacion(), d.getIdCarpetaPadre(),
									d.getIdDocumento(), d.getIdGabinete(),
									d.getNumeroPaginas(), v.getUnidad()
											+ v.getRutaBase() + v.getRutaDirectorio()
											+ v.getVolumen() + File.separator
											+ nombreArchivoModificado + ".tif", "N",
									tipo_pagina, d.getExtension(), d.getFechaModificacion());
		
							//ControlIndex.guardaDatosIndex(DatosIndex);
		
						} catch (HibernateException e) {
							log.error(e, e);
		
						}
		
						String index = "N";
						boolean ind = false;
						// Aqui inserta el registro para el indice de lucene
						try {
							//ind = indexLucene(d, v, nombreArchivoModificado,indexPath);
							log.trace("Aqui insertaba el registro para el indice de lucene");
						} catch (Exception e) {
							log.error(e, e);
							ind = false;
						}
		
						if (ind) {
							index = "Y";
						}

			log.info("[Insertando Pagina Documento: " + d.getNombreDocumento()
					+ "] Gaveta: " + d.getTituloAplicacion() + " Carpeta: "
					+ d.getNombreCarpeta() + " Usuario: "
					+ d.getNombreUsuario());

		} catch (Exception se) {
			log.error(se, se);

		} finally {
			hm.close();
		}
	}

	public synchronized boolean updateDocumento(Documento d,boolean updateShared) {
		boolean retVal = false;
		boolean retExtended = false;

		retVal = updateDocumento(d);

		if (updateShared) {
			HibernateManager hm = new HibernateManager();
			Query sqlQuery;
			try {
				
				List<Query> queries = new ArrayList<Query>();
				
				String sql = "DELETE FROM imx_documentoExtend WHERE TOKEN_COMPARTIR= ?";
				sqlQuery = hm.createSQLQuery(sql);
				sqlQuery.setString(0,d.getTokenCompartir() );
				queries.add(sqlQuery);

				sql = "INSERT INTO imx_documentoExtend(TOKEN_COMPARTIR,BANDERA_DESCARGAR) VALUES(?,?)";
				sqlQuery = hm.createSQLQuery(sql);
				sqlQuery.setString(0, d.getTokenCompartir());
				sqlQuery.setString(1, d.getLigaPermisoBajar());
				queries.add(sqlQuery);
				
				hm.executeQueries(queries);

			} catch (Exception se) {
				log.error(se, se);

			} finally {
				hm.close();
			}

		}

		log.info("[Actualizando Documento: " + d.getNombreDocumento()
				+ "] Gaveta: " + d.getTituloAplicacion() + " Carpeta: "
				+ d.getNombreCarpeta() + " Usuario: " + d.getNombreUsuario());
		return (retVal && retExtended);
	}

	public synchronized boolean updateDocumento(Documento d) {
		boolean retVal = false;

		try {
				log.info("[Actualizando Documento: " + d.getNombreDocumento()
						+ "] Gaveta: " + d.getTituloAplicacion() + " Carpeta: "
						+ d.getNombreCarpeta() + " Usuario: "
						+ d.getNombreUsuario());
				
				d.setFechaModificacion(new Date(System.currentTimeMillis()));
				d.setNumeroAccesos(0);
				if (d.getEstadoDocumento() == null || "null".equalsIgnoreCase(d.getEstadoDocumento()))
					d.setEstadoDocumento("V");	
				
				new imx_documento_manager().update(new imx_documento(d));
				
				retVal = true;
			} catch (EntityManagerException e) {
				log.error(e,e);
			}
			return retVal;
	}

	public synchronized boolean deleteDocumento(Documento d) {
		return deleteDocumento(d, Config.getLuceneIndex());
	}

	public synchronized boolean deleteDocumento(Documento d, String indexPath) {
		boolean retVal = false;
		
		HibernateManager hm = new HibernateManager();
		Query sqlQuery;
		try {

				List<Query> queries = new ArrayList<Query>();
				
				String sql = "DELETE FROM imx_pagina_index"
								+ " WHERE titulo_aplicacion = ?"
								+ " AND id_gabinete = ?"
								+ " AND id_carpeta_padre = ?" 
								+ " AND id_documento = ?"; 
				
				sqlQuery = hm.createSQLQuery(sql);
				sqlQuery.setString(0,d.getTituloAplicacion() );
				sqlQuery.setInteger(1,d.getIdGabinete());
				sqlQuery.setInteger(2,d.getIdCarpetaPadre() );
				sqlQuery.setInteger(3,d.getIdDocumento() );
				queries.add(sqlQuery);
				
				sql = "DELETE FROM imx_pagina"
					+ " WHERE titulo_aplicacion = ?"
					+ " AND id_gabinete = ?"
					+ " AND id_carpeta_padre = ?"
					+ " AND id_documento = ?"; 
		
				sqlQuery = hm.createSQLQuery(sql);
				sqlQuery.setString(0,d.getTituloAplicacion() );
				sqlQuery.setInteger(1,d.getIdGabinete());
				sqlQuery.setInteger(2,d.getIdCarpetaPadre() );
				sqlQuery.setInteger(3,d.getIdDocumento() );
				queries.add(sqlQuery);
				
				sql = "DELETE FROM imx_documento"
						+ " WHERE titulo_aplicacion = ?"
						+ " AND id_gabinete = ?"
						+ " AND id_carpeta_padre = ?"
						+ " AND id_documento = ?"; 
			
				sqlQuery = hm.createSQLQuery(sql);
				sqlQuery.setString(0,d.getTituloAplicacion() );
				sqlQuery.setInteger(1,d.getIdGabinete());
				sqlQuery.setInteger(2,d.getIdCarpetaPadre() );
				sqlQuery.setInteger(3,d.getIdDocumento() );
				queries.add(sqlQuery);
					
					
				sql = "UPDATE imx_carpeta SET numero_documentos = numero_documentos - 1"
						+ ", fh_modificacion = ?" 
						+ " WHERE titulo_aplicacion = ?"
						+ " AND id_gabinete = ?" 
						+ " AND id_carpeta = ?";
							
				sqlQuery = hm.createSQLQuery(sql);
				sqlQuery.setTimestamp(0,new Timestamp(System.currentTimeMillis()));
				sqlQuery.setString(1,d.getTituloAplicacion());
				sqlQuery.setInteger(2,d.getIdGabinete() );
				sqlQuery.setInteger(3,d.getIdCarpetaPadre() );
				queries.add(sqlQuery);
								
				hm.executeQueries(queries);

			retVal = true;
			log.info("[Borrado Documento] Gaveta: " + d.getTituloAplicacion()
					+ " Documento: " + d.getNombreDocumento() + " Usuario: "
					+ d.getNombreUsuario());
		} catch (Exception se) {
			log.error(se, se);

		} finally {
			hm.close();
		}
		return retVal;
	}

	private synchronized Integer ajustaPaginas(Documento d){
		try{
			Conjunction conjunction = Restrictions.conjunction();
			conjunction.add(Restrictions.eq("Id.TituloAplicacion", d.getTituloAplicacion()));
			conjunction.add(Restrictions.eq("Id.IdGabinete", d.getIdGabinete()));
			conjunction.add(Restrictions.eq("Id.IdCarpetaPadre", d.getId_carpeta_padre()));
			conjunction.add(Restrictions.eq("Id.IdDocumento", d.getIdDocumento()));
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.asc("Pagina"));
			HibernateManager hibernateManager = new HibernateManager();
			hibernateManager.setCriterion(conjunction);
			hibernateManager.setOrders(orders);
			@SuppressWarnings("unchecked")
			List<imx_pagina> lista = (List<imx_pagina>)hibernateManager.list(imx_pagina.class);
			
			for(int i=0; i<lista.size(); i++) {
				lista.get(i).setPagina(i+1);
			}
			hibernateManager.update(lista);
			hibernateManager.close();
			
			return lista.size();
		}
		catch(Exception e){
			log.error("Error ajustar Paginas: "+e.getMessage(),e);
		}
		return null;
	}
	
	public synchronized void deletePaginasDocumento(Documento d, Integer[] indices, String usuario) throws DocumentoManagerException {
		deletePaginaDocumento(d, indices, usuario);
	}

	public synchronized void deletePaginaDocumento(Documento d, Integer[] indices, String usuario) throws DocumentoManagerException {
		if(indices.length<1) {
			log.info("No se borra ninguna pagina");
			return;
		}

		HibernateManager hm = new HibernateManager();
		Query sqlQuery;
		
		try {
			List<imx_pagina> imx_paginas = new imx_pagina_manager().select(d.getNodo()).list();
			
			List<Query> queries = new ArrayList<Query>();
			String sql = "DELETE FROM imx_pagina "
			+ " WHERE titulo_aplicacion = ? "
			+ " AND id_gabinete = ? "
			+ " AND id_carpeta_padre = ? "
			+ " AND id_documento = ? "
			+ " AND numero_pagina IN "
			+"("+StringUtils.join(indices,',')+")"; 
			
			sqlQuery = hm.createSQLQuery(sql);
			sqlQuery.setString(0,d.getTituloAplicacion() );
			sqlQuery.setInteger(1,d.getIdGabinete());
			sqlQuery.setInteger(2,d.getIdCarpetaPadre() );
			sqlQuery.setInteger(3,d.getIdDocumento() );
			
			queries.add(sqlQuery);
			
			sql = "DELETE FROM imx_pagina_index"
					+ " WHERE titulo_aplicacion = ?"
					+ " AND id_gabinete = ?"
					+ " AND id_carpeta_padre = ?"
					+ " AND id_documento = ?"
					+ " AND numero_pagina IN "
					+"("+StringUtils.join(indices,',')+")"; 
			
					sqlQuery = hm.createSQLQuery(sql);
					sqlQuery.setString(0,d.getTituloAplicacion() );
					sqlQuery.setInteger(1,d.getIdGabinete());
					sqlQuery.setInteger(2,d.getIdCarpetaPadre() );
					sqlQuery.setInteger(3,d.getIdDocumento() );
					
			queries.add(sqlQuery);
			hm.executeQueries(queries);
		
			//TODO: verificar  el borrado de la pagina.
			
			for(int i=0; i<indices.length; i++) {
				DeleteFiles.Delete(d, indices[i]);
			}
				
			long time = System.currentTimeMillis();
			d.setFechaModificacion(new Date(time));
			
			time += (1000L * 60L);
			
			for (imx_pagina imx_pagina : imx_paginas) {
				File f = new File(imx_pagina.getAbsolutePath());
				if (Arrays.asList(indices).contains(imx_pagina.getId().getNumeroPagina())) {
					if(Config.deleteFromVolumen) {
						log.trace("Borrado fisico de "+d.getNodo()+"P"+imx_pagina.getId().getNumeroPagina()+" : "+f.getAbsolutePath());
						FileUtils.deleteQuietly(f);
					} else {
						log.trace("Borrado logico de "+d.getNodo()+"P"+imx_pagina.getId().getNumeroPagina()+" : "+f.getAbsolutePath());
					}
				} else {
					f.setLastModified(time);
					time += (1000L * 60L);
				}
			}
			
			d.setNumeroPaginas(ajustaPaginas(d));
			updateDocumento(d);

			log.info("[Borradas paginas con indices: " + "("+StringUtils.join(indices,',')+")" + "] Gaveta: "
					+ d.getTituloAplicacion() + " Documento: "
					+ d.getNombreDocumento() + " Usuario: "
					+ d.getNombreUsuario());
		} catch (Exception se) {
			log.error(se, se);
		} finally {
			hm.close();
		}
	}

	public synchronized void deleteAllPaginasDocumento(Documento d, String usuario) throws DocumentoManagerException {
		HibernateManager hm = new HibernateManager();
		try {
			String sql = "SELECT numero_pagina FROM imx_pagina "
					+ " WHERE titulo_aplicacion = ? "
					+ " AND id_gabinete = ? "
					+ " AND id_carpeta_padre = ? "
					+ " AND id_documento = ? ";
			SQLQuery sqlQuery = hm.createSQLQuery(sql);
			sqlQuery.setString(0,d.getTituloAplicacion() );
			sqlQuery.setInteger(1,d.getIdGabinete());
			sqlQuery.setInteger(2,d.getIdCarpetaPadre() );
			sqlQuery.setInteger(3,d.getIdDocumento() );
			sqlQuery.addScalar("numero_pagina",Hibernate.INTEGER);
			@SuppressWarnings("unchecked")
			List<Integer> list = sqlQuery.list();
			deletePaginasDocumento(d, list.toArray(new Integer[list.size()]), usuario);
		} finally {
			hm.close();
		}
	}
/*
	public synchronized void deleteAllPaginasDocumento(Documento d,
			String indexPath) throws DocumentoManagerException {

		HibernateManager hm = new HibernateManager();
		imx_volumen imxvolumen=null;
		try {
			

    			Query q = hm.createQuery("FROM imx_volumen WHERE capacidad = :capacidad ");    
    			q.setInteger("capacidad", 1);          
    			imxvolumen = (imx_volumen)q.uniqueResult();
    			
    			if (imxvolumen == null) {
    				log.warn("No se encontro un volumen activo.");   				
    			} 
    			
    			String pathBase = imxvolumen.getUnidadDisco() + imxvolumen.getRutaBase() + imxvolumen.getRutaDirectorio();
    			
    			q = hm.createSQLQuery("SELECT volumen, nom_archivo_vol FROM imx_pagina "
										+ "WHERE titulo_aplicacion = ? "
										+ "AND id_gabinete = ? "
										+ "AND id_carpeta_padre = ? "
										+ "AND id_documento = ?");
    		 			
    		 			q.setString(0, d.getTituloAplicacion());
    		 			q.setInteger(1, d.getIdGabinete());
    		 			q.setInteger(2, d.getIdCarpetaPadre());
    		 			q.setInteger(3, d.getIdDocumento());
    		 			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    		       
    		 			@SuppressWarnings("unchecked")
    		 			List<Map<String, String>> imxpaginas = (List<Map<String,String>>)q.list();
    		 			
    					if (imxpaginas == null) {
    						
    						log.warn("No se encontro ninguna Pagina: "
    			        			+" titulo_aplicacion "+ d.getTituloAplicacion()
    			        			+" id_gabinete "+ d.getIdGabinete()
    			        			+" id_carpeta_padre "+ d.getIdCarpetaPadre()
    			        			+" id_documento "+  d.getIdDocumento());

    					} 
    					   					
    					List<Query> queries = new ArrayList<Query>();
    					
    					String sql = "DELETE FROM imx_pagina_index"
    					+ " WHERE titulo_aplicacion = ?"
    					+ " AND id_gabinete = ?"
    					+ " AND id_carpeta_padre = ?"
    					+ " AND id_documento = ?";
    			
    					q = hm.createSQLQuery(sql);  					
    					q.setString(0,d.getTituloAplicacion() );
    					q.setInteger(1,d.getIdGabinete());
    					q.setInteger(2,d.getIdCarpetaPadre() );
    					q.setInteger(3,d.getIdDocumento() );
    					queries.add(q);
    					
    					sql = "DELETE FROM imx_pagina "
    					+ " WHERE titulo_aplicacion = ? "
    					+ " AND id_gabinete = ? "
    					+ " AND id_carpeta_padre = ? "
    					+ " AND id_documento = ? ";
    					
    					q = hm.createSQLQuery(sql);
    					q.setString(0,d.getTituloAplicacion() );
    					q.setInteger(1,d.getIdGabinete());
    					q.setInteger(2,d.getIdCarpetaPadre() );
    					q.setInteger(3,d.getIdDocumento() );
    					queries.add(q);
    									
    					hm.executeQueries(queries);
    					
    					//Importante se efectue el borrado hasta el final
  
    					for(Map<String,String> imxpagina : imxpaginas) 
    					{
    						File f = new File(pathBase + Utils.getString(imxpagina.get("volumen")) 
    										+ File.separator,Utils.getString(imxpagina.get("nom_archivo_vol")));
            				log.warn("Eliminando pagina fisicamente. " + f.getAbsolutePath());
    						f.delete();
    					}
    					
    					DeleteFiles.DeleteAll(indexPath, d);   					
    					d.setNumeroPaginas(0);
    					updateDocumento(d);

			log.info("[Borradas Paginas: " + d.getNumeroPaginas()
					+ "] Gaveta: " + d.getTituloAplicacion() + " Documento: "
					+ d.getNombreDocumento() + " Usuario: "
					+ d.getNombreUsuario());
		} catch (Exception se) {
			log.error(se, se);

		} finally {
			hm.close();
		}
	}
*/
	public  Documento findDocumentsByName(
			String tituloAplicacion, int idGabinete, int idCarpetaPadre,
			String nombreDocumento) throws FortimaxException {

		Documento d = null;
		try {
			
			imx_documento imxdocumento = getDocumentbyName( tituloAplicacion, idGabinete, idCarpetaPadre, nombreDocumento);	
			if(imxdocumento!=null)
				d = selectDocumento(tituloAplicacion, idGabinete, idCarpetaPadre, imxdocumento.getId().getIdDocumento());
		} catch (Exception e) {
			log.error(e, e);
		}
		return d;
	}

	public static ArrayList<String> findDocuments(String aplicacion,
			String campo, String valor) throws FortimaxException {
		ArrayList<String> docs = new ArrayList<String>();

		HibernateManager hm = new HibernateManager();
		try {
							
				Query q = hm.createSQLQuery("SELECT nombre_documento FROM "+"imx"+aplicacion.toLowerCase()+" g, imx_documento d" 
					+ " WHERE g."+campo+" = ? "
					+ " AND d.titulo_aplicacion = ?"
					+ " AND d.id_gabinete = g.id_gabinete"
					+ " AND d.numero_paginas > ?");
			 			
			 			q.setString(0, valor);
			 			q.setString(1, aplicacion);
			 			q.setInteger(2, 0);
			 			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			       
			 			@SuppressWarnings("unchecked")
			 			List<Map<String, String>> imxdocumentos = (List<Map<String,String>>)q.list();
			 			
    					for(Map<String,String> imxdocumento : imxdocumentos) 
    					{
    						imxdocumento = new CaseInsensitiveMap(imxdocumento);
    						docs.add(Utils.getString(imxdocumento.get("nombre_documento")));
    					}
			 			
						
			}catch (Exception se) {
				log.error(se, se);
			}
			
			finally {
				hm.close();
			}
		return docs;
	}

	public static boolean updateNumeroPaginas(
			String titulo_aplicacion, int id_gabinete, int id_carpeta_padre,
			int id_documento, int numero_paginas) throws EntityManagerException {
		boolean ok = false;
		int upd = 0;
		
		log.debug("Actualizando numero de paginas de documento...");
		
		imx_documento_manager imx_documento_manager = new imx_documento_manager();
		imx_documento_manager.select(titulo_aplicacion,id_gabinete,id_carpeta_padre,id_documento);
		imx_documento documento = imx_documento_manager.uniqueResult();
		documento.setNumeroPaginas(numero_paginas);
		documento.setFhModificacion(new Date(System.currentTimeMillis()));
		imx_documento_manager.update(documento);

		log.debug("Se actualizaron " + upd + " registros");

		ok = true;
		return ok;
	}

	public static boolean addTamanoBytes(
			String titulo_aplicacion, int id_gabinete, int id_carpeta_padre,
			int id_documento, double tamanoBytes) throws EntityManagerException {
		boolean ok = false;
		int upd = 0;
	
		log.debug("Actualizando tamano de bytes de documento...");
		
		imx_documento_manager imx_documento_manager = new imx_documento_manager();
		imx_documento_manager.select(titulo_aplicacion,id_gabinete,id_carpeta_padre,id_documento);
		imx_documento documento = imx_documento_manager.uniqueResult();
		documento.setTamanoBytes(documento.getTamanoBytes()+(long)tamanoBytes);
		documento.setFhModificacion(new Date(System.currentTimeMillis()));
		imx_documento_manager.update(documento);
		
		log.debug("Se actualizaron " + upd + " registros");

		ok = true;

		return ok;
	}

	//TODO: considerar este metodo para validad la existencia de un solo documento con ese Nombre.
	public  imx_documento getDocumentbyName(String titulo_aplicacion,int id_gabinete, int id_carpta_padre, String nombre_documento) throws Exception {		
        HibernateManager hm = new HibernateManager();
        imx_documento imxd=null;
		try {
					
			
					Query q = hm.createQuery("FROM imx_documento WHERE titulo_aplicacion = :titulo_aplicacion "
					+ "AND id_gabinete = :id_gabinete AND id_carpeta_padre = :id_carpeta_padre AND nombre_documento = :nombre_documento");
					
					q.setString("titulo_aplicacion", titulo_aplicacion);
					q.setInteger("id_gabinete", id_gabinete);
					q.setInteger("id_carpeta_padre", id_carpta_padre);
					q.setString("nombre_documento", nombre_documento);
		       
		 			@SuppressWarnings("unchecked")
		 			imx_documento imxdocumento = (imx_documento)q.uniqueResult();

		 			imxd=imxdocumento;
		 			
					
		}catch (Exception se) {
			log.error(se, se);
		}
		
		finally {
			hm.close();
		}
		return imxd;
	}

	/**
	 * EJRV Este algoritmo genera un identificador único de 32 caracteres
	 *
	 * Se basa en la concatenacion de los hexadecimales de: - La direccion IP
	 * del equipo dode se este ejecutando - La fecha y hora en milisegundos La
	 * unidad de tiempo devuelta es en milisegundos, la exactitud del valor
	 * depende del SO en el que se ejecute, pues muchos SO calculan la unidad de
	 * tiempo en decimas de milisegundos - Un numero random seguro Un numero
	 * random seguro produce una salida no determinista y por lo tanto se
	 * requiere de una semilla no predecible y la salida de un random seguro es
	 * una secuencia criptograficamente solida como se describe en RFC1750
	 * (Randomness Recommendations for Security) - El Hash de identidad del
	 * objeto El metodo "System.identityHashCode(Object)" regresa enteros
	 * distintos para cada objeto
	 */
	public String getNextFilename(Documento d, String userCode)
			throws DocumentoManagerException {

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
			throw new DocumentoManagerException("Excepcion host desconocido: "
					+ ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			throw new DocumentoManagerException(
					"Excepcion no se encontro el algoritmo: " + ex.getMessage());
		}

		return userCode + strRetVal.toLowerCase();
	}

	public static String concatenateDocumentAttributes(Documento d,
			String separator) {
		StringBuffer b = new StringBuffer();
		String idDocumento = d.getTituloAplicacion() + "_G" + d.getIdGabinete()
				+ "C" + d.getIdCarpetaPadre() + "D" + d.getIdDocumento();
		b.append("IDENTIFICADOR_FORTIMAX=");
		b.append(idDocumento);
		b.append(separator);

		b.append("NOMBRE_DOCUMENTO=");
		b.append(d.getNombreDocumento());
		b.append(separator);

		b.append("NOMBRE_USUARIO=");
		b.append(d.getNombreUsuario());
		b.append(separator);

		b.append("FH_CREACION=");
		b.append(d.getFechaCreacion());
		b.append(separator);

		b.append("FH_MODIFICACION=");
		b.append(d.getFechaModificacion());
		b.append(separator);

		b.append("NUMERO_PAGINAS=");
		b.append(d.getNumero_paginas());
		b.append(separator);

		b.append("TAMANO_BYTES=");
		b.append(d.getTamanoBytes());
		b.append(separator);

		b.append("COMPARTIR=");
		b.append(d.getCompartir());
		b.append(separator);

		b.append("TOKEN_COMPARTIR=");
		b.append(d.getTokenCompartir());
		b.append(separator);

		b.append("FECHA_COMPARTIDO=");
		b.append(d.getDateShared());
		b.append(separator);

		b.append("DIAS_PERMITIDOS=");
		b.append("0");
		return b.toString();
	}
}