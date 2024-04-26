package com.syc.fortimax.hibernate.managers;

import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.GetDatosNodo;

public class imx_documento_manager extends EntityManager {

	private static final Logger log = Logger.getLogger(imx_documento_manager.class);
	
	
	public static imx_documento selectDocument(String titulo_aplicacion, int id_gabinete,
			int id_carpeta_padre, int id_documento) throws HibernateException, Exception {
				
                HibernateManager hm = new HibernateManager();
                imx_documento imxdocumento=null;
        		try {
        			
              
        			Query q = hm.createQuery("FROM imx_documento WHERE titulo_aplicacion = :titulo_aplicacion "
        			+ "AND id_gabinete = :id_gabinete AND id_carpeta_padre = :id_carpeta_padre AND id_documento = :id_documento");
        			
        			q.setString("titulo_aplicacion", titulo_aplicacion);
        			q.setInteger("id_gabinete", id_gabinete);
        			q.setInteger("id_carpeta_padre", id_carpeta_padre);
        			q.setInteger("id_documento", id_documento);
              
        			imxdocumento = (imx_documento)q.uniqueResult();
        			
        			if (imxdocumento == null) {
        				
        				log.warn("No se encontro ningun Documento: "
        	        			+" titulo_aplicacion "+ titulo_aplicacion
        	        			+" id_gabinete "+ id_gabinete
        	        			+" id_carpeta_padre "+ id_carpeta_padre
        	        			+" id_documento "+ id_documento);
        				return null;
        			} 
        			
        		} catch (Exception e) {
        			
        			log.error(e,e);
        			
        		} finally {
        			hm.close();
        		}
        		
        		return imxdocumento;
	}
	
	public static imx_documento selectDocument(String tokenCompartir) throws HibernateException, Exception {
				
                HibernateManager hm = new HibernateManager();
                imx_documento imxdocumento=null;
        		try {
        			Query q = hm.createQuery("FROM imx_documento WHERE tokenCompartir = :tokenCompartir ");
        			
        			q.setString("tokenCompartir", tokenCompartir);
              
        			imxdocumento = (imx_documento)q.uniqueResult();
        			
        			if (imxdocumento == null) {
        				
        				log.warn("No se encontro ningun Documento: "
        	        			+" tokenCompartir "+ tokenCompartir);
        				return null;
        			} 
        			
        		} catch (Exception e) {
        			
        			log.error(e,e);
        			
        		} finally {
        			hm.close();
        		}
        		
        		log.trace(imxdocumento.getId());
        		return imxdocumento;
	}
	

	public static imx_documento_id insertDocument(Session session, imx_documento imx_documento, Integer id_documento) throws HibernateException, Exception {
		//TODO: Encontrar una manera de que el metodo anterior lanze la excepción al fallar en vez de verificarlo manualmente
		if(!CarpetaManager.isValid(imx_documento))
			throw new Exception("Ya existe una carpeta/documento de nombre "+ imx_documento.getNombreDocumento()); 
		
		
		imx_documento_id id = null;

		log.trace("[Creando Documento: " + imx_documento.getNombreDocumento() + " Gaveta: "
				+ imx_documento.getId().getTituloAplicacion() + " Carpeta: "
				+ imx_documento.getId().getIdCarpetaPadre() + " Usuario: "
				+ imx_documento.getNombreUsuario() + "]");

		Query q;
		Integer tmpid = 1;
		if(id_documento == null){
			q = session.createQuery("select MAX(imxdocumento.id.idDocumento) AS idMax from imx_documento as imxdocumento WHERE titulo_aplicacion = :titulo_aplicacion AND id_gabinete = :id_gabinete AND id_carpeta_padre = :id_carpeta_padre");
			q.setString("titulo_aplicacion", imx_documento.getId().getTituloAplicacion());
			q.setInteger("id_gabinete", imx_documento.getId().getIdGabinete());
			q.setInteger("id_carpeta_padre", imx_documento.getId().getIdCarpetaPadre());
			
			tmpid = (Integer) q.uniqueResult();
			
			if (tmpid == null) {
				tmpid = 1;
			} else {
				tmpid++;
			}			
		} else {
			tmpid = id_documento;
		}

		imx_documento.getId().setIdDocumento(tmpid);

		if (imx_documento.getIdTipoDocumento() == -1) {

			//q = session.createQuery("SELECT imxtipodocumento.id.idTipoDocto from imx_tipo_documento as imxtipodocumento WHERE titulo_aplicacion = :titulo_aplicacion AND nombre_tipo_docto = :nombre_tipo_docto").addEntity(imx_tipo_documento.class);;
			
			String query = "SELECT *"
					+ " FROM imx_tipo_documento"
					+ " WHERE titulo_aplicacion = ?"
					+ " AND nombre_tipo_docto = ?";
			
            Query hquery = session.createSQLQuery(query).addEntity(imx_tipo_documento.class);
			
            hquery.setString(0, imx_documento.getId().getTituloAplicacion());
            hquery.setString(1, imx_documento.getNombreTipoDocto());

			imx_tipo_documento tmptipodoc = (imx_tipo_documento) hquery.uniqueResult();

			if (tmptipodoc == null) {				
				log.warn("No se encontro el tipo de documento: "+ imx_documento.getNombreTipoDocto()+" en Gaveta: " + imx_documento.getId().getTituloAplicacion()
		            	+ " Se aplicaran valores por default: documento tipo 'Externo'");
		            	imx_documento.setIdTipoDocumento(Documento.EXTERNO);
		            	imx_documento.setNombreTipoDocto("EXTERNO");
		            	imx_documento.setPrioridad(3);// validar esta variable, ya que posiblemente no se use.
				throw new Exception("No se encontro el tipo de documento");
			} else {
				imx_documento.setIdTipoDocumento(tmptipodoc.getId().getIdTipoDocto());
				imx_documento.setNombreTipoDocto(tmptipodoc.getNombreTipoDocto());
			}

		}

		if (imx_documento.getPrioridad() == -1
				&& StringUtils.isNotBlank(imx_documento.getNombreTipoDocto())) {
			if ((imx_documento.getIdTipoDocumento() != -1)
					|| (imx_documento.getIdTipoDocumento() != -2)) {
				q = session
						.createQuery("select prioridad from imx_tipo_documento WHERE titulo_aplicacion = :titulo_aplicacion AND id_tipo_docto = :id_tipo_docto");
				q.setString("titulo_aplicacion", imx_documento.getId()
						.getTituloAplicacion());
				q.setInteger("id_tipo_docto", imx_documento.getIdTipoDocumento());

				Integer prioridad = (Integer) q.uniqueResult();

				if (prioridad != null) {
					imx_documento.setPrioridad(prioridad);
				}
			}
		}

		session.save("imx_documento", imx_documento );

		id = imx_documento.getId();

		log.info("[Creacion exitosa del Documento: " + imx_documento +" "+imx_documento.getNombreDocumento()
				+ "] Gaveta : " + imx_documento.getId().getTituloAplicacion()
				+ " Usuario: " + imx_documento.getNombreUsuario());	

		// TODO no deberia actualizar la cantidad de documentos en la carpeta,
		// eso es und ato calculado, faias en la normalizaion de la BD
		q = session
				.createQuery("UPDATE imx_carpeta set numero_documentos = numero_documentos+1, fh_modificacion = :fh_modificacion WHERE titulo_aplicacion = :titulo_aplicacion AND id_gabinete = :id_gabinete and id_carpeta = :id_carpeta");

		q.setTimestamp("fh_modificacion", new Date(System.currentTimeMillis()));
		q.setString("titulo_aplicacion", imx_documento.getId().getTituloAplicacion());
		q.setInteger("id_gabinete", imx_documento.getId().getIdGabinete());
		q.setInteger("id_carpeta", imx_documento.getId().getIdCarpetaPadre());

		int upd = q.executeUpdate();

		if (upd >= 1) {
			log.info("[Actualizacion exitosa de Carpeta("
					+ imx_documento.getId().getIdCarpetaPadre() + ")" + ", Gaveta: "
					+ imx_documento.getId().getTituloAplicacion() + ", Usuario: "
					+ imx_documento.getNombreUsuario() + "]");
		}

		return id;
	}
	
	public imx_documento_manager() {
		setClassOfEntity(imx_documento.class);
		orders.add(Order.asc("id.tituloAplicacion"));
		orders.add(Order.asc("id.idGabinete"));
		orders.add(Order.asc("id.idCarpetaPadre"));
		orders.add(Order.asc("id.idDocumento"));
	} 

	@Override
	public List<imx_documento> list() throws EntityManagerException {
		return list(imx_documento.class);
	}

	@Override
	public imx_documento uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_documento.class);
	}
	
	public imx_documento_manager select(String nodo) {
		GetDatosNodo gdn = new GetDatosNodo(nodo);
		return select(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento());
	}

	public imx_documento_manager select(String tituloAplicacion, Integer idGabinete, Integer idCarpetaPadre, Integer idDocumento) {
		conjunction = Restrictions.conjunction();
		if(tituloAplicacion!=null)
			conjunction.add(Restrictions.eq("id.tituloAplicacion", tituloAplicacion));
		if(idGabinete!=null)
			if(idGabinete>=0)
				conjunction.add(Restrictions.eq("id.idGabinete", idGabinete));
		if(idCarpetaPadre!=null)
			if(idCarpetaPadre>=0)
				conjunction.add(Restrictions.eq("id.idCarpetaPadre", idCarpetaPadre));
		if(idDocumento!=null)
			if(idDocumento>=0)
				conjunction.add(Restrictions.eq("id.idDocumento", idDocumento));
		return this;
	}
	
	public imx_documento_manager selectNombreDocumento(String nombreDocumento) {
		if(nombreDocumento!=null)
			conjunction.add(Restrictions.eq("nombreDocumento", nombreDocumento).ignoreCase());
		return this;
	}
}
