package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_pagina;
import com.syc.fortimax.hibernate.entities.imx_pagina_id;
import com.syc.imaxfile.GetDatosNodo;

public class imx_pagina_manager extends EntityManager {
	
	public imx_pagina_manager() {
		setClassOfEntity(imx_pagina.class);
		orders.add(Order.asc("Id.TituloAplicacion"));
		orders.add(Order.asc("Id.IdGabinete"));
		orders.add(Order.asc("Id.IdCarpetaPadre"));
		orders.add(Order.asc("Id.IdDocumento"));
		orders.add(Order.asc("Pagina"));
		orders.add(Order.asc("Id.NumeroPagina"));
	} 

	@Override
	public List<imx_pagina> list() throws EntityManagerException {
		return list(imx_pagina.class);
	}
	
	public List<imx_pagina_id> getListIDs() throws EntityManagerException {
		HibernateManager hibernateManager = new HibernateManager();
		hibernateManager.setCriterion(conjunction);
		hibernateManager.setFirstResult(firstResult);
		hibernateManager.setMaxResults(maxResults);
		hibernateManager.setOrders(orders);
		hibernateManager.setProjection(Projections.property("Id"));
		@SuppressWarnings("unchecked")
		List<imx_pagina_id> list = (List<imx_pagina_id>) hibernateManager.list(getClassOfEntity());
		hibernateManager.close();
		return list;
	}

	@Override
	public imx_pagina uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_pagina.class);
	}
	
	public imx_pagina_manager select(String nodo) {
		GetDatosNodo gdn = new GetDatosNodo(nodo);
		return select(gdn);
	}
	
	public imx_pagina_manager select(GetDatosNodo gdn) {
		return select(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento(),gdn.getPagina());
	}

	public imx_pagina_manager select(String tituloAplicacion, Integer idGabinete, Integer idCarpetaPadre, Integer idDocumento, Integer idPagina) {
		conjunction = Restrictions.conjunction();
		if(tituloAplicacion!=null)
			conjunction.add(Restrictions.eq("Id.TituloAplicacion", tituloAplicacion));
		if(idGabinete!=null)
			conjunction.add(Restrictions.eq("Id.IdGabinete", idGabinete));
		if(idCarpetaPadre!=null)
			conjunction.add(Restrictions.eq("Id.IdCarpetaPadre", idCarpetaPadre));
		if(idDocumento!=null)
			conjunction.add(Restrictions.eq("Id.IdDocumento", idDocumento));
		if(idPagina!=null)
			conjunction.add(Restrictions.eq("Id.NumeroPagina", idPagina));
		return this;
	}
}
