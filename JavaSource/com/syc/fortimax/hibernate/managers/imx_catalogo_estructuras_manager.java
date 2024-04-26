package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_catalogo_estructuras;

public class imx_catalogo_estructuras_manager extends EntityManager {
	
	public imx_catalogo_estructuras_manager() {
		setClassOfEntity(imx_catalogo_estructuras.class);
		orders.add(Order.asc("Id"));
		orders.add(Order.asc("Nombre"));
		orders.add(Order.asc("Descripcion"));
//		orders.add(Order.asc("Definicion"));
	} 

	@Override
	public List<imx_catalogo_estructuras> list() throws EntityManagerException {
		return list(imx_catalogo_estructuras.class);
	}

	@Override
	public imx_catalogo_estructuras uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_catalogo_estructuras.class);
	}

	public imx_catalogo_estructuras_manager select(Integer id, String nombre, String descripcion, String definicion) {
		conjunction = Restrictions.conjunction();
		if(id!=null)
			conjunction.add(Restrictions.eq("Id", id));
		if(nombre!=null)
			conjunction.add(Restrictions.eq("Nombre", nombre));
		if(descripcion!=null)
			conjunction.add(Restrictions.eq("Descripcion", descripcion));
		if(definicion!=null)
			conjunction.add(Restrictions.eq("Definicion", definicion));
		return this;
	}
}
