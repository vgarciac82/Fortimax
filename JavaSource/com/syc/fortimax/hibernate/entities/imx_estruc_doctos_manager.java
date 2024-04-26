package com.syc.fortimax.hibernate.entities;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.imaxfile.GetDatosNodo;

public class imx_estruc_doctos_manager  extends EntityManager {
	private static final Logger log = Logger.getLogger(imx_estruc_doctos_manager.class);

	public imx_estruc_doctos_manager() {
		setClassOfEntity(imx_estruc_doctos.class);
		orders.add(Order.asc("id.TituloAplicacion"));
		orders.add(Order.asc("id.NombreEstructura"));
		orders.add(Order.asc("id.PosicionElemento"));
	} 
	
	@Override
	public List<imx_estruc_doctos> list() throws EntityManagerException {
		return list(imx_estruc_doctos.class);
	}

	@Override
	public imx_estruc_doctos uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_estruc_doctos.class);
	}
	
	
	public imx_estruc_doctos_manager select(GetDatosNodo nodo) {
//		GetDatosNodo gdn = new GetDatosNodo(nodo);
		return select(nodo.getGaveta());
	}

	public imx_estruc_doctos_manager select(String tituloAplicacion) {
		conjunction = Restrictions.conjunction();
		if(tituloAplicacion!=null)
			conjunction.add(Restrictions.eq("id.TituloAplicacion", tituloAplicacion));
		return this;
	}
	
}
