package com.syc.fortimax.hibernate;

import java.io.Serializable;

public abstract class Entity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5826459608222833783L;
	public abstract Serializable getId();
	HibernateManager hm = null;
	
	public Entity open() {
		getHibernateManager();
		return (Entity) hm.getSession().get(this.getClass(), this.getId());
	}
	
	public void close() {
		if(hm!=null) {
			hm.close();
			hm = null;
		}
	}
	
	private void getHibernateManager() {
		if(hm==null)
			hm = new HibernateManager();
	}
	
	public void delete() {
		getHibernateManager();
		try {
			hm.delete(this);
		} finally {
			close();
		}
	}
	
	public Serializable save() {
		getHibernateManager();
		try {
			return hm.save(this);
		} finally {
			close();
		}
	}
	
	public void saveOrUpdate() {
		getHibernateManager();
		try {
			hm.saveOrUpdate(this);
		} finally {
			close();
		}
	}
	
	public void update() {
		getHibernateManager();
		try {
			hm.update(this);
		} finally {
			close();
		}
	}
}
