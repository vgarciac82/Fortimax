package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.EntityManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.LiveSearch;
import com.syc.fortimax.hibernate.entities.imx_bitacora;

public class imx_bitacora_manager extends EntityManager {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(imx_bitacora_manager.class);
	
	public imx_bitacora_manager() {
		setClassOfEntity(imx_bitacora.class);
		orders.add(Order.desc("id"));
	}
	
	public imx_bitacora_manager(String campoBusqueda, int firstResult, int maxResults) {
		this();
		conjunction = Restrictions.conjunction();
		if(campoBusqueda!=null&&!"".equals(campoBusqueda)) {
			LiveSearch liveSearch = new LiveSearch(imx_bitacora.class,campoBusqueda);
			conjunction.add(liveSearch.criterion());
		}
		this.firstResult=firstResult;
		this.maxResults=maxResults;
	}
	
	@Override
	public List<imx_bitacora> list() throws EntityManagerException {
		return list(imx_bitacora.class);
	}
	
	@Override
	public imx_bitacora uniqueResult() throws EntityManagerException {
		return uniqueResult(imx_bitacora.class);
	}
}
