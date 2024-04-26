package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_config;

public class imx_config_manager extends EntityManager {
	
	public imx_config_manager() {
		setClassOfEntity(imx_config.class);
	}
	
	public void selectName(String name) {
		conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.eq("name", name));
	}

	@Override
	public List<imx_config> list() throws EntityManagerException {
		return list(imx_config.class);
	}

	@Override
	public imx_config uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_config.class);
	}
}
