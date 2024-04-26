package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_catalogo_atributos;

public class imx_catalogo_atributos_manager extends EntityManager {
	public imx_catalogo_atributos_manager() {
		setClassOfEntity(imx_catalogo_atributos.class);
	} 

	@Override
	public List<imx_catalogo_atributos> list() throws EntityManagerException {
		return list(imx_catalogo_atributos.class);
	}

	@Override
	public imx_catalogo_atributos uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_catalogo_atributos.class);
	}
	
	public imx_catalogo_atributos_manager select(Integer id) {
		conjunction = Restrictions.conjunction();
		if(id!=null)
			conjunction.add(Restrictions.eq("id", id));
		return this;
	}
}