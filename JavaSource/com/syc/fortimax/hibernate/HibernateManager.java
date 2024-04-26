package com.syc.fortimax.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.ResultTransformer;

import com.syc.fortimax.config.HibernateUtils;

public class HibernateManager {
	
	private static final Logger log = Logger.getLogger(HibernateManager.class);
	
	private Criterion criterion = null;
	private ResultTransformer resultTransformer = null;
	private List<Order> orders = new ArrayList<Order>();
	private ProjectionList projections = Projections.projectionList();
	private int firstResult = -1;
	private int maxResults = -1;
	Session session = null;
	
	public void setCriterion(Criterion criterion) {
		this.criterion = criterion;
	}
		
	public Criterion getCriterion() {
		return this.criterion;
	}
	
	public ResultTransformer getResultTransformer() {
		return resultTransformer;
	}

	public void setResultTransformer(ResultTransformer resultTransformer) {
		this.resultTransformer = resultTransformer;
	}
	
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	public List<Order> getOrders() {
		return this.orders;
	}
	
	public void setProjection(Projection projection) {
		ProjectionList projections = Projections.projectionList();
		projections.add(projection);
		this.projections = projections;
	}
	
	public void setProjections(ProjectionList projections) {
		this.projections = projections;
	}
	
	public void addProjection(Projection projection) {
		this.projections.add(projection);
	}
	
	public void addProjection(Projection projection, String alias) {
		this.projections.add(projection, alias);
	}
	
	public void addProjections(ProjectionList projections) {
		this.projections.add(projections);
	}
	
	public ProjectionList getProjections() {
		return this.projections;
	}
	
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}
	
	public int getFirstResult() {
		return this.firstResult;
	}
	
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}
	
	public int getMaxResults() {
		return this.maxResults;
	}
	
	public void close() {
		closeSession();
		clear();
	}
	
	private void closeSession() {
		if(this.session!=null)
			if(this.session.isOpen())
				this.session.close();
		this.session=null;
	}
	
	public void clear() {
		this.criterion = null;
		this.resultTransformer = null;
		this.orders = new ArrayList<Order>();
		this.projections = Projections.projectionList();
		this.firstResult = -1;
		this.maxResults = -1;
	}
	
	public Query createQuery(String query) {
		getSession();
		return session.createQuery(query);
	}
	
	public SQLQuery createSQLQuery(String sqlQuery) {
		getSession();
		return session.createSQLQuery(sqlQuery);
	}
	
	public Session getSession() {
		if(this.session==null)
			this.session=HibernateUtils.getSession();
		if(!this.session.isOpen())
			this.session=HibernateUtils.getSession();
		return this.session;
	}
	
	public Transaction beginTransaction() {
		return getSession().beginTransaction();
	}
	
	private enum GetAction {
		list, uniqueResult
	}
	
	private Object executeGet(GetAction action, Class<?> classOfEntity) {
		getSession();
		this.session.setFlushMode(FlushMode.MANUAL);
		Criteria criteria = session.createCriteria(classOfEntity);
		if(criterion!=null) {
			criteria.add(criterion);
		}
		for(Order order : this.orders) {
			criteria.addOrder(order);
		}
		if(this.projections.getLength()>0)
			criteria.setProjection(this.projections);
		if(this.firstResult>=0)
			criteria.setFirstResult(this.firstResult);
		if(this.maxResults>=0)
			criteria.setMaxResults(this.maxResults);
		if(resultTransformer!=null) {
			criteria.setResultTransformer(resultTransformer);
		}
		Object object = null;
		switch(action) {
			case list: object = criteria.list(); break;
			case uniqueResult: object = criteria.uniqueResult(); break;
			default: break;
		}
		return object;	
	}
	
	public List<?> list(Class<?> classOfEntity) {
		List<?> lista = (List<?>)executeGet(GetAction.list,classOfEntity);
		return lista;
	}
	
	public Object uniqueResult(Class<?> classOfEntity) {
		Object uniqueResult = executeGet(GetAction.uniqueResult,classOfEntity);
		return uniqueResult;
	}
	
	private enum UpdateAction {
		save, update, saveOrUpdate, delete
	}
	
	private class UpdateControl {
		public HashMap<Object, Serializable> saved = new HashMap<Object,Serializable>();
		public int updated = 0;
	}
	
	private UpdateControl executeUpdate(UpdateAction action, List<?> objects) throws HibernateException {
		UpdateControl updateControl = new UpdateControl();
		getSession();
		this.session.setFlushMode(FlushMode.AUTO);
		log.trace("----------BEGIN TRANSACTION----------");
		Transaction transaction = session.beginTransaction();
		try {
			for(int i = 0; i<objects.size() ; i++) {
				Object object = objects.get(i);
				if(object instanceof String)
					updateControl.updated += session.createSQLQuery((String)object).executeUpdate();
				else if(object instanceof Query || object instanceof SQLQuery)
					updateControl.updated += ((Query)object).executeUpdate();
				else switch(action) {
					case save: updateControl.saved.put(object, session.save(object)); break;
					case update: session.update(object); break;
					case saveOrUpdate: session.saveOrUpdate(object); break;
					case delete: session.delete(object); break;
					default: break;
				}
				if ( i % 20 == 0 ) {		 //20, same as the JDBC batch size
					session.flush(); //flush a batch of inserts and release memory:
					session.clear();
			    }
			}
			log.trace("----------COMMIT TRANSACTION----------");
			transaction.commit();
		} catch(HibernateException e) {
			log.trace("----------ROLLBACK TRANSACTION----------");
			transaction.rollback();
			if(session.isOpen()) {
				session.flush();
				closeSession();
			}
			throw e;
		} finally {
			closeSession();
		}
		return updateControl;
	}
	
	public int executeSQLQueries(List<String> queries) throws HibernateException {
		return executeUpdate(null,queries).updated;
	}
	
	public int executeSQLQuery(String query) throws HibernateException {
		List<String> queries = new ArrayList<String>();
		queries.add(query);
		return executeSQLQueries(queries);
	}
	
	public int executeQueries(List<Query> queries) throws HibernateException {
		return executeUpdate(null,queries).updated;
	}
	
	public int executeQuery(Query query) throws HibernateException {
		List<Query> queries = new ArrayList<Query>();
		queries.add(query);
		return executeQueries(queries);
	}
	
	//Regresa un HashMap de los objetos insertados (Object) con sus respectivos id (Serializable).
	public HashMap<Object, Serializable> save(List<?> objects) throws HibernateException {
		return executeUpdate(UpdateAction.save,objects).saved;
	}
	
	//Regresa el id que se le asigno al objeto insertado como un Serializable.
	public Serializable save(Object object) throws HibernateException {
		List<Object> objects = new ArrayList<Object>();
		objects.add(object);
		HashMap<Object, Serializable> hashMap = save(objects);
		return hashMap.get(object);
	}
	
	public void update(List<?> objects) throws HibernateException {
		executeUpdate(UpdateAction.update,objects);
	}
	
	public void update(Object object) throws HibernateException {
		List<Object> objects = new ArrayList<Object>();
		objects.add(object);
		update(objects);
	}
	
	public void saveOrUpdate(List<?> objects) throws HibernateException {
		executeUpdate(UpdateAction.saveOrUpdate,objects);
	}
	
	public void saveOrUpdate(Object object) throws HibernateException {
		List<Object> objects = new ArrayList<Object>();
		objects.add(object);
		saveOrUpdate(objects);
	}
	
	public void delete(List<?> objects) throws HibernateException {
		executeUpdate(UpdateAction.delete,objects);
	}
	
	public void delete(Object object) throws HibernateException {
		List<Object> objects = new ArrayList<Object>();
		objects.add(object);
		delete(objects);
	}
}
