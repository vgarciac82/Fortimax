package com.syc.fortimax.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public abstract class EntityManager {
	private HibernateManager hibernateManager = new HibernateManager();
	private boolean autoCloseSession = true;
	private Class<?> classOfEntity = null;
	protected int firstResult=-1;
	protected int maxResults=-1;
	protected Conjunction conjunction = Restrictions.conjunction();
	protected Projection projection = null;
	protected List<Order> orders = new ArrayList<Order>();
	
	public abstract Object uniqueResult() throws EntityManagerException;
	public abstract List<?> list() throws EntityManagerException;
	
	protected Class<?> getClassOfEntity() throws EntityManagerException {
		if(classOfEntity!=null)
			return classOfEntity;
		else
			throw new EntityManagerException("setClassOfEntity(classOfEntity.class) debe ser la primera línea del constructor principal y cada sobrecarga de este constructor debe mandar llamar this() como primera línea");
	}
	
	protected EntityManager setClassOfEntity(Class<?> classOfEntity) {
		this.classOfEntity = classOfEntity;
		return this;
	}
	
	public int getFirstResult() {
		return firstResult;
	}
	public EntityManager setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}
	public int getMaxResults() {
		return maxResults;
	}
	public EntityManager setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}
	
	public Conjunction getConjunction() {
		return conjunction;
	}

	public EntityManager setConjunction(Conjunction conjunction) {
		this.conjunction = conjunction;
		return this;
	}
	
	public Projection getProjection() {
		return projection;
	}
	
	public EntityManager setProjection(Projection projection) {
		this.projection = projection;
		return this;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public EntityManager setOrders(List<Order> orders) {
		this.orders = orders;
		return this;
	}
	
	public EntityManager setOrders(com.syc.fortimax.hibernate.Order[] orders) {
		if (orders!=null) {
			this.orders = new ArrayList<Order>();
			for(com.syc.fortimax.hibernate.Order order : orders) {
				if("DESC".equals(order.getDirection()))
					this.orders.add(Order.desc(order.getProperty()));
				else
					this.orders.add(Order.asc(order.getProperty()));
			}
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> list(Class<T> classOfT) throws EntityManagerException {
		hibernateManager.close();
		try {
			if(projection!=null)
				hibernateManager.setProjection(projection);
			hibernateManager.setCriterion(conjunction);
			hibernateManager.setFirstResult(firstResult);
			hibernateManager.setMaxResults(maxResults);
			hibernateManager.setOrders(orders);
			List<?> list = hibernateManager.list(getClassOfEntity());
			return (List<T>)list;
		} finally {
			if(autoCloseSession)
				hibernateManager.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T uniqueResult(Class<T> classOfT) throws EntityManagerException {
		hibernateManager.close();
		try {
			if(projection!=null)
				hibernateManager.setProjection(projection);
			hibernateManager.setCriterion(conjunction);
			Object object = hibernateManager.uniqueResult(getClassOfEntity());
			return (T)object;
		} finally {
			if(autoCloseSession)
				hibernateManager.close();
		}
	}
	
	public Long rowCount() throws EntityManagerException {
		hibernateManager.close();	
		try {
			hibernateManager.setCriterion(conjunction);
			hibernateManager.setProjection(Projections.rowCount());
			Long rowCount = (Long) hibernateManager.uniqueResult(getClassOfEntity());
			return rowCount;
		} finally {
			hibernateManager.close();
		}
	}
	
	private List<?> objectsFromClassOfEntity(List<?> objects) throws EntityManagerException {
		List<Object> list = new ArrayList<Object>();
		for(Object object : objects) {
			list.add(objectFromClassOfEntity(object));
		}
		return list;
	}
	
	private Object objectFromClassOfEntity(Object object) throws EntityManagerException {
		if(object.getClass().equals(getClassOfEntity()))
				return object;
			else
				throw new EntityManagerException("Se esperaba objeto de la clase "+getClassOfEntity().getName()+", objeto de la clase "+object.getClass().getName()+" encontrado");
	}
	
	public Serializable save(Object object) throws EntityManagerException {
		hibernateManager.close();
		return hibernateManager.save(objectFromClassOfEntity(object));
	}
	
	public HashMap<Object, Serializable> save(List<?> objects) throws HibernateException, EntityManagerException {
		hibernateManager.close();
		return hibernateManager.save(objectsFromClassOfEntity(objects));
	}
	
	public void update(Object object) throws EntityManagerException {
		hibernateManager.close();
		hibernateManager.update(objectFromClassOfEntity(object));
	}
	
	public void update(List<?> objects) throws HibernateException, EntityManagerException {
		hibernateManager.close();
		hibernateManager.update(objectsFromClassOfEntity(objects));
	}
	
	public void saveOrUpdate(Object object) throws EntityManagerException {
		hibernateManager.close();
		hibernateManager.saveOrUpdate(objectFromClassOfEntity(object));
	}
	
	public void saveOrUpdate(List<?> objects) throws HibernateException, EntityManagerException {
		hibernateManager.close();
		hibernateManager.saveOrUpdate(objectsFromClassOfEntity(objects));
	}
	
	public void delete(Object object) throws EntityManagerException {
		hibernateManager.close();
		hibernateManager.delete(objectFromClassOfEntity(object));
	}
	
	public void delete(List<?> objects) throws HibernateException, EntityManagerException {
		hibernateManager.close();
		hibernateManager.delete(objectsFromClassOfEntity(objects));
	}
	
	public void close() {
		hibernateManager.close();
	}
	
	public boolean isAutoCloseSession() {
		return autoCloseSession;
	}
	
	public EntityManager setAutoCloseSession(boolean autoCloseSession) {
		this.autoCloseSession = autoCloseSession;
		return this;
	}
	
	public HibernateManager getHibernatManager() {
		return hibernateManager;
	}
}
