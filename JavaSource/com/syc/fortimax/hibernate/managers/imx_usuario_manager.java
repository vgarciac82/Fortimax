package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_usuario;

public abstract class imx_usuario_manager {

	private static final Logger log = Logger
			.getLogger(imx_usuario_manager.class);

	@SuppressWarnings("unchecked")
	public static ArrayList<imx_usuario> get(Session sess) {
		Query q = sess.createQuery("select u from imx_usuario u");
		return (ArrayList<imx_usuario>) q.list();
	}

	public static ArrayList<imx_usuario> get() {
		ArrayList<imx_usuario> u = new ArrayList<imx_usuario>();
		Session sess = HibernateUtils.getSession();
		if (sess != null) {
			u = get(sess);
			if (sess.isOpen()) {
				sess.close();
			}
		}
		return u;
	}

	public static boolean exist_IMX_USUARIO() {
		boolean exist = true;
		Session sess = HibernateUtils.getSession();
		if (sess != null) {
			try {
				sess.createSQLQuery("Select * from IMX_USUARIO").list();
			} catch (HibernateException e) {
				exist = false;
				log.error(e, e);
			} finally {
				if (sess.isOpen()) {
					sess.close();
				}
			}
		}
		return exist;
	}

}
