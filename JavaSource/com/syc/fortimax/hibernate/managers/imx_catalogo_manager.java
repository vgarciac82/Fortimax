package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_catalogo;

public class imx_catalogo_manager extends EntityManager {
	
	public imx_catalogo_manager() {
		setClassOfEntity(imx_catalogo.class);
	}

	@Override
	public List<imx_catalogo> list() throws EntityManagerException {
		return list(imx_catalogo.class);
	}

	@Override
	public imx_catalogo uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_catalogo.class);
	}
	
	public imx_catalogo_manager select(String NombreCatalogo) {
		conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.eq("NombreCatalogo", NombreCatalogo));
		return this;
	}
}
