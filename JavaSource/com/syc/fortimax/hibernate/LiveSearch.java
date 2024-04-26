package com.syc.fortimax.hibernate;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;

import com.syc.fortimax.config.HibernateUtils;

public class LiveSearch {
	
	Class<?> classOfEntity = null;
	String campoBusqueda = null;
	
	public Class<?> getClassOfEntity() {
		return classOfEntity;
	}

	public void setClassOfEntity(Class<?> classOfEntity) {
		this.classOfEntity = classOfEntity;
	}

	public String getCampoBusqueda() {
		return campoBusqueda;
	}

	public void setCampoBusqueda(String campoBusqueda) {
		this.campoBusqueda = campoBusqueda;
	}

	public LiveSearch(Class<?> classOfEntity, String campoBusqueda) {
		this.classOfEntity=classOfEntity;
		this.campoBusqueda=campoBusqueda;
	}
	
	public Criterion criterion() {
		List<String> columnNames = HibernateUtils.getColumnNames(classOfEntity);
		Disjunction disjunction = Restrictions.disjunction();
		for(String propertyName : columnNames) {
			disjunction.add(Restrictions.sqlRestriction("LOWER("+propertyName+") LIKE ?","%"+campoBusqueda.toLowerCase()+"%",new StringType()));
		}
		return disjunction;
	}
}
