package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_org_carpeta;

public class imx_org_carpeta_manager extends EntityManager {
	
	public imx_org_carpeta_manager() {
		setClassOfEntity(imx_org_carpeta.class);
		orders.add(Order.asc("id.tituloAplicacion"));
		orders.add(Order.asc("id.idGabinete"));
		orders.add(Order.asc("idCarpetaPadre"));
		orders.add(Order.asc("id.idCarpetaHija"));
		orders.add(Order.asc("nombreHija"));
	} 

	@Override
	public List<imx_org_carpeta> list() throws EntityManagerException {
		return list(imx_org_carpeta.class);
	}

	@Override
	public imx_org_carpeta uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_org_carpeta.class);
	}

	public imx_org_carpeta_manager select(String tituloAplicacion, Integer idGabinete, Integer idCarpetaHija, Integer idCarpetaPadre, String nombreHija) {
		conjunction = Restrictions.conjunction();
		if(tituloAplicacion!=null)
			conjunction.add(Restrictions.eq("id.tituloAplicacion", tituloAplicacion));
		if(idGabinete!=null)
			conjunction.add(Restrictions.eq("id.idGabinete", idGabinete));
		if(idCarpetaHija!=null)
			conjunction.add(Restrictions.eq("id.idCarpetaHija", idCarpetaHija));
		if(idCarpetaPadre!=null)
			conjunction.add(Restrictions.eq("idCarpetaPadre", idCarpetaPadre));
		if(nombreHija!=null)
			conjunction.add(Restrictions.eq("nombreHija", nombreHija).ignoreCase());
		return this;
	}
}
