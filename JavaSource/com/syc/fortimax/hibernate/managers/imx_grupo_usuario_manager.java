package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_grupo_usuario;

public abstract class imx_grupo_usuario_manager {

	@SuppressWarnings("unchecked")
	public static ArrayList<imx_grupo_usuario> get(Session sess,
			String nombreGrupo) {
		ArrayList<imx_grupo_usuario> gu = new ArrayList<imx_grupo_usuario>();

		StringBuffer sb = new StringBuffer(
				"select gu from imx_grupo_usuario gu ");
		if (nombreGrupo != null) {
			sb.append(" where gu.id.nombreGrupo= :nombreGrupo");
		}
		Query q = sess.createQuery(sb.toString());
		if (nombreGrupo != null) {
			q.setString("nombreGrupo", nombreGrupo);
		}
		gu = (ArrayList<imx_grupo_usuario>) q.list();
		return gu;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<imx_grupo_usuario> getByUser(Session sess,
			String nombreUsuario) {
		ArrayList<imx_grupo_usuario> gu = new ArrayList<imx_grupo_usuario>();

		StringBuffer sb = new StringBuffer(
				"select gu from imx_grupo_usuario gu ");
		if (nombreUsuario != null) {
			sb.append(" where gu.id.nombreUsuario= :nombreUsuario");
		}
		Query q = sess.createQuery(sb.toString());
		if (nombreUsuario != null) {
			q.setString("nombreUsuario", nombreUsuario);
		}
		gu = (ArrayList<imx_grupo_usuario>) q.list();
		return gu;
	}

	public static ArrayList<imx_grupo_usuario> get(String nombreGrupo) {
		ArrayList<imx_grupo_usuario> gu = new ArrayList<imx_grupo_usuario>();
		Session sess = HibernateUtils.getSession();
		if(sess!=null){
			gu = get(sess, nombreGrupo);
			if (sess.isOpen()) {
				sess.close();
			}
		}
		return gu;
	}

}
