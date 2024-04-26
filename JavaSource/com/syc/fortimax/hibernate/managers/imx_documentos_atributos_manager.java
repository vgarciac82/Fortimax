package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_documentos_atributos;
import com.syc.imaxfile.GetDatosNodo;

public class imx_documentos_atributos_manager extends EntityManager {
	
	public imx_documentos_atributos_manager() {
		setClassOfEntity(imx_documentos_atributos.class);
		orders.add(Order.asc("id.tituloAplicacion"));
		orders.add(Order.asc("id.idGabinete"));
		orders.add(Order.asc("id.idCarpetaPadre"));
		orders.add(Order.asc("id.idDocumento"));
		orders.add(Order.asc("id.idAtributo"));
		orders.add(Order.asc("Valor"));
	} 

	@Override
	public List<imx_documentos_atributos> list() throws EntityManagerException {
		return list(imx_documentos_atributos.class);
	}

	@Override
	public imx_documentos_atributos uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_documentos_atributos.class);
	}
	
	public imx_documentos_atributos_manager select(GetDatosNodo nodo) {
		return select(nodo.getGaveta(),nodo.getGabinete(),nodo.getIdCarpeta(),nodo.getIdDocumento());
	}

	public imx_documentos_atributos_manager select(String tituloAplicacion, Integer idGabinete, Integer idCarpetaPadre, Integer idDocumento) {
		conjunction = Restrictions.conjunction();
		if(tituloAplicacion!=null)
			conjunction.add(Restrictions.eq("id.tituloAplicacion", tituloAplicacion));
		if(idGabinete!=null)
			conjunction.add(Restrictions.eq("id.idGabinete", idGabinete));
		if(idCarpetaPadre!=null)
			conjunction.add(Restrictions.eq("id.idCarpetaPadre", idCarpetaPadre));
		if(idDocumento!=null)
			conjunction.add(Restrictions.eq("id.idDocumento", idDocumento));
		return this;
	}
}
