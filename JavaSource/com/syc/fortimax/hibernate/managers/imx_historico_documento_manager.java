package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_historico_documento;
import com.syc.imaxfile.GetDatosNodo;

public class imx_historico_documento_manager extends EntityManager {
	
	public imx_historico_documento_manager() {
		setClassOfEntity(imx_historico_documento.class);
		orders.add(Order.asc("id.tituloAplicacion"));
		orders.add(Order.asc("id.idGabinete"));
		orders.add(Order.asc("id.idCarpetaPadre"));
		orders.add(Order.asc("id.idDocumento"));
		orders.add(Order.desc("id.idVersion"));
	} 

	@Override
	public imx_historico_documento uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_historico_documento.class);
	}
	
	@Override
	public List<imx_historico_documento> list() throws EntityManagerException {
		return list(imx_historico_documento.class);
	}
	
	public imx_historico_documento_manager select(GetDatosNodo nodo) {
		return select(nodo, null);
	}
	
	public imx_historico_documento_manager select(GetDatosNodo nodo, Integer idVersion) {
		return select(nodo.getGaveta(),nodo.getGabinete(),nodo.getIdCarpeta(),nodo.getIdDocumento(),idVersion);
	}

	public imx_historico_documento_manager select(String tituloAplicacion, Integer idGabinete, Integer idCarpetaPadre, Integer idDocumento, Integer idVersion) {
		conjunction = Restrictions.conjunction();
		if(tituloAplicacion!=null)
			conjunction.add(Restrictions.eq("id.tituloAplicacion", tituloAplicacion));
		if(idGabinete!=null)
			conjunction.add(Restrictions.eq("id.idGabinete", idGabinete));
		if(idCarpetaPadre!=null)
			conjunction.add(Restrictions.eq("id.idCarpetaPadre", idCarpetaPadre));
		if(idDocumento!=null)
			conjunction.add(Restrictions.eq("id.idDocumento", idDocumento));
		if(idVersion!=null)
			conjunction.add(Restrictions.eq("id.idVersion", idVersion));
		return this;
	}
}