package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_grupo;

public abstract class imx_grupo_manager {

	private static final Logger log = Logger.getLogger(imx_grupo_manager.class);

	@SuppressWarnings("unchecked")
	public static ArrayList<imx_grupo> get(Session sess) {
		Query q = sess.createQuery("select g from imx_grupo g");
		return (ArrayList<imx_grupo>) q.list();
	}

	public static ArrayList<imx_grupo> get() {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_grupo> g = get(sess);
		sess.clear();
		return g;
	}

	public static boolean delete(Session sess, String nombreGrupo) {
		boolean deleted = false;
		sess.delete(new imx_grupo(nombreGrupo));
		deleted = true;
		return deleted;
	}

	public static boolean delete(String nombreGrupo) {
		boolean deleted = false;
		Session sess = HibernateUtils.getSession();
		if (sess != null) {
			Transaction trans = sess.beginTransaction();
			try {
				deleted = delete(sess, nombreGrupo);
				if (trans != null && deleted) {
					trans.commit();
				}
			} catch (HibernateException e) {
				log.error(e, e);
				deleted = false;
				if (trans != null) {
					trans.rollback();
				}
			} finally {
				if (sess.isOpen()) {
					sess.close();
				}
			}
		}
		return deleted;
	}

}
