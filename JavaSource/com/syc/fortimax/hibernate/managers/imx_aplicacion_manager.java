package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;

public abstract class imx_aplicacion_manager {

	@SuppressWarnings("unchecked")
	public static ArrayList<imx_aplicacion> get(Session sess,
			boolean includeGrales) {
		ArrayList<imx_aplicacion> a = new ArrayList<imx_aplicacion>();

		StringBuffer sb = new StringBuffer("select a from imx_aplicacion a ");
		if (includeGrales == false) {
			sb.append(" WHERE a.tituloAplicacion <> 'USR_GRALES'");
		}

		Query q = sess.createQuery(sb.toString());

		a = (ArrayList<imx_aplicacion>) q.list();

		return a;
	}
	
	public static imx_aplicacion select(String titulo_aplicacion) {
		Session session=HibernateUtils.getSession();	
		Query query=session.createQuery("from imx_aplicacion WHERE TITULO_APLICACION=:titulo_aplicacion");
		query.setParameter("titulo_aplicacion", titulo_aplicacion);
		return (imx_aplicacion)query.uniqueResult();
	}

	public static ArrayList<imx_aplicacion> get() {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_aplicacion> a = get(sess, false);
		if (sess.isOpen()) {
			sess.close();
		}
		return a;
	}

	public static ArrayList<imx_aplicacion> get(boolean includeGrales) {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_aplicacion> a = get(sess, includeGrales);
		if (sess.isOpen()) {
			sess.close();
		}
		return a;
	}
}