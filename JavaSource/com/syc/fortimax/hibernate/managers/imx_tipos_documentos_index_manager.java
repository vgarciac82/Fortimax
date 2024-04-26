package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_catalogo_tipo_documento;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.fortimax.hibernate.entities.imx_tipos_documentos_index;
import com.syc.imaxfile.Documento;
import com.syc.servlets.PlantillaDocumentoServlet.DatosPlantilla;

public class imx_tipos_documentos_index_manager extends EntityManager {

	private static final Logger log = Logger.getLogger(imx_tipos_documentos_index_manager.class);
	
	public imx_tipos_documentos_index_manager(Documento documento) {
		this();
		select(documento.getTituloAplicacion(), documento.getIdGabinete(), documento.getIdCarpetaPadre(), documento.getIdDocumento(), null, null);
	}
	
	public imx_tipos_documentos_index_manager(imx_documento_id id) {
		this();
		select(id.getTituloAplicacion(), id.getIdGabinete(), id.getIdCarpetaPadre(), id.getIdDocumento(), null, null);
	}
	
	public imx_tipos_documentos_index_manager(String tituloAplicacion, int idTipoDocumento, DatosPlantilla datosPlantilla) {
		this();
		select(tituloAplicacion, null, null, null, idTipoDocumento, datosPlantilla);
	}
	
	public imx_tipos_documentos_index_manager(String tituloAplicacion, int idGabinete, int idCarpetaPadre, int idDocumento, int idTipoDocumento) {
		this();
		select(tituloAplicacion, idGabinete, idCarpetaPadre, idDocumento, idTipoDocumento, null);
	}

	public imx_tipos_documentos_index_manager() {
		setClassOfEntity(imx_tipos_documentos_index.class);
	}

	public imx_tipos_documentos_index_manager select(String tituloAplicacion, Integer idGabinete, Integer idCarpetaPadre, Integer idDocumento, Integer idTipoDocumento, DatosPlantilla datosPlantilla) {
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
		if(idTipoDocumento!=null)
			if(idTipoDocumento>=0)
				conjunction.add(Restrictions.eq("id.idTipoDocumento", idTipoDocumento));	
		if(datosPlantilla!=null) {
			conjunction.add(Restrictions.eq("id.nombreCampo", datosPlantilla.nombre));
			conjunction.add(Restrictions.eq("valorCampo", datosPlantilla.valor));
		}
		return this;
	}
	
	@Override
	public List<imx_tipos_documentos_index> list() throws EntityManagerException {
		return list(imx_tipos_documentos_index.class);
	}
	
	@Override
	public imx_tipos_documentos_index uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_tipos_documentos_index.class);
	}
	
	public List<imx_documento_id> getDocumentoIDs() throws EntityManagerException {
		HibernateManager hibernateManager = new HibernateManager();
		hibernateManager.setCriterion(conjunction);
		hibernateManager.setFirstResult(firstResult);
		hibernateManager.setMaxResults(maxResults);
		hibernateManager.setOrders(orders);
		ProjectionList proyecciones = Projections.projectionList();
		proyecciones.add(Projections.property("id.tituloAplicacion"));
		proyecciones.add(Projections.property("id.idGabinete"));
		proyecciones.add(Projections.property("id.idCarpetaPadre"));
		proyecciones.add(Projections.property("id.idDocumento"));
		hibernateManager.setProjection(Projections.distinct(proyecciones));
		@SuppressWarnings("unchecked")
		List<Object[]> list = (List<Object[]>) hibernateManager.list(getClassOfEntity());
		hibernateManager.close();
		List<imx_documento_id> ids = new ArrayList<imx_documento_id>();
		for(Object[] object : list) {
			String tituloAplicacion = (String) object[0];
			int idGabinete = (Integer) object[1];
			int idCarpetaPadre = (Integer) object[2];
			int idDocumento = (Integer) object[3];
			imx_documento_id id = new imx_documento_id(tituloAplicacion,idGabinete,idCarpetaPadre,idDocumento);
			ids.add(id);	
		}	
		return ids;
	}
	
	public List<Integer> getTiposDocumentosID() throws EntityManagerException {
		HibernateManager hibernateManager = new HibernateManager();
		hibernateManager.setCriterion(conjunction);
		hibernateManager.setFirstResult(firstResult);
		hibernateManager.setMaxResults(maxResults);
		hibernateManager.setOrders(orders);
		hibernateManager.setProjection(Projections.distinct(Projections.property("id.idTipoDocumento")));
		@SuppressWarnings("unchecked")
		List<Integer> list = (List<Integer>) hibernateManager.list(getClassOfEntity());
		hibernateManager.close();
		return list;
	}
	
	/* TODO: El siguiente método hace 2 vueltas a la BD para eliminar los campos, mientras que
	 * 		con HQL podría hacerse en 1, además de que tempo que si alguien ingresa nuevos
	 * 		registros a la BD en el momento justo, estos no se eliminaran.
	 * 
	 * 		Por el momento no existe HibernateManager.delete(String HQL), en cuanto exista se debera
	 * 		cambiar el siguiente método a menos que se encuantre como conseguirlo en una sola corrida
	 * 		con Criterions.
	 */
	public int delete(String tituloAplicacion, int idGabinete, int idCarpetaPadre, int idDocumento, int idTipoDocumento) throws EntityManagerException {
		imx_tipos_documentos_index_manager tipos_documentos_index_manager = new imx_tipos_documentos_index_manager(tituloAplicacion, idGabinete, idCarpetaPadre, idDocumento, idTipoDocumento);
		List<imx_tipos_documentos_index> campos = tipos_documentos_index_manager.list();
		HibernateManager hibernateManager = new HibernateManager();
		hibernateManager.delete(campos);
		log.debug("Se eliminaron "+campos.size()+" registros");
		return campos.size();
	}
	
	private int delete(String tituloAplicacion, int idGabinete, int idCarpetaPadre, int idDocumento) throws EntityManagerException {
		log.debug("Se eliminara todo el indice correspondiente al documento "+tituloAplicacion+"_G"+idGabinete+"C"+idCarpetaPadre+"D"+idDocumento);
		return delete(tituloAplicacion,idGabinete,idCarpetaPadre,idDocumento, -1);
	}
	
	public int delete(imx_catalogo_tipo_documento tipoDocumento) throws EntityManagerException {
		log.debug("Se eliminara todo el indice correspondiente a la plantilla Id:"+tipoDocumento.getId()+" Nombre:"+tipoDocumento.getNombre());
		return delete(null,-1,-1,-1,tipoDocumento.getId());		
	}
	
	public int delete(imx_documento_id id) throws EntityManagerException {
		return delete(id.getTituloAplicacion(),id.getIdGabinete(),id.getIdCarpetaPadre(),id.getIdDocumento());
	}

	public int delete(Documento documento) throws EntityManagerException {
		return delete(documento.getTituloAplicacion(),documento.getIdGabinete(),documento.getIdCarpetaPadre(),documento.getIdDocumento());
	}
}
