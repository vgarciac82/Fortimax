package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_carpeta;

public class imx_carpeta_manager extends EntityManager {
	
	public imx_carpeta_manager() {
		setClassOfEntity(imx_carpeta.class);
		orders.add(Order.asc("id.tituloAplicacion"));
		orders.add(Order.asc("id.idGabinete"));
		orders.add(Order.asc("id.idCarpeta"));
	} 

	@Override
	public List<imx_carpeta> list() throws EntityManagerException {
		return list(imx_carpeta.class);
	}

	@Override
	public imx_carpeta uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_carpeta.class);
	}

	public imx_carpeta_manager select(String tituloAplicacion, Integer idGabinete, Integer idCarpeta, String nombreCarpeta) {
		conjunction = Restrictions.conjunction();
		if(tituloAplicacion!=null)
			conjunction.add(Restrictions.eq("id.tituloAplicacion", tituloAplicacion));
		if(idGabinete!=null)
			conjunction.add(Restrictions.eq("id.idGabinete", idGabinete));
		if(idCarpeta!=null)
			conjunction.add(Restrictions.eq("id.idCarpeta", idCarpeta));
		if(nombreCarpeta!=null)
			conjunction.add(Restrictions.eq("nombreCarpeta", nombreCarpeta));
		return this;
	}
}
