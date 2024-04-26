package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio;

public abstract class imx_grupo_privilegio_manager {

	public static ArrayList<imx_grupo_privilegio> get(Session sess) {
		return get(sess, null);
	}

	public static ArrayList<imx_grupo_privilegio> get() {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_grupo_privilegio> gp = get(sess);
		sess.clear();
		return gp;
	}

	public static ArrayList<imx_grupo_privilegio> get(String nombreGrupo) {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_grupo_privilegio> gp = get(sess, nombreGrupo);
		sess.clear();
		return gp;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<imx_grupo_privilegio> get(Session sess,
			String nombreGrupo) {
		ArrayList<imx_grupo_privilegio> gp = new ArrayList<imx_grupo_privilegio>();

		StringBuffer sb = new StringBuffer(
				"select gp from imx_grupo_privilegio gp ");
		if (nombreGrupo != null) {
			sb.append(" where gp.id.nombreGrupo= :nombreGrupo");
		}
		sb.append(" order by gp.id.nombreGrupo");

		Query q = sess.createQuery(sb.toString());
		if (nombreGrupo != null) {
			q.setString("nombreGrupo", nombreGrupo);
		}

		gp = (ArrayList<imx_grupo_privilegio>) q.list();

		return gp;
	}

}
