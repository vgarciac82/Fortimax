package com.syc.fortimax.hibernate.managers;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_seguridad;

public abstract class imx_seguridad_manager {

	@SuppressWarnings("unchecked")
	public static ArrayList<imx_seguridad> get(Session sess, String nombreUsuario,
			String tituloAplicacion) {
		ArrayList<imx_seguridad> s = new ArrayList<imx_seguridad>();

		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sb
				.append("s.TITULO_APLICACION, s.PRIORIDAD, s.NOMBRE_NIVEL, s.DESCRIPCION ");
		sb.append("from imx_seguridad s ");
		sb.append("where TITULO_APLICACION = :tituloAplicacion ");
		sb.append("and NOMBRE_NIVEL in ");
		sb.append("( ");
		sb.append("   select ");
		sb.append("   NOMBRE_NIVEL ");
		sb.append("   from imx_privilegio p ");
		sb.append("   where p.NOMBRE_USUARIO= :nombreUsuario ");
		sb.append("   and p.TITULO_APLICACION = :tituloAplicacion ");
		sb.append("   union ");
		sb.append("   select ");
		sb.append("   gp.NOMBRE_NIVEL ");
		sb.append("   from imx_grupo_privilegio gp ");
		sb
				.append("   INNER JOIN imx_grupo_usuario gu on gp.NOMBRE_GRUPO = gu.NOMBRE_GRUPO ");
		sb.append("   where gu.NOMBRE_USUARIO = :nombreUsuario ");
		sb.append("   and gp.TITULO_APLICACION = :tituloAplicacion ");
		sb.append(") ");
		sb.append("ORDER by TITULO_APLICACION, PRIORIDAD ");

		//SQL estandar
		//TODO cambiar a HQL revisar bien por el UNION!!
		SQLQuery q = sess.createSQLQuery(sb.toString());
		q.setString("nombreUsuario", nombreUsuario);
		q.setString("tituloAplicacion", tituloAplicacion);

		List<Object[]> ls = q.list();

		imx_seguridad last = new imx_seguridad();
		for (Object[] seg : ls) {
			imx_seguridad next = new imx_seguridad(seg);
			if (s.isEmpty()) {
				s.add(next);
			} else {
				if (next.getId().getTituloAplicacion().equals(
						last.getId().getTituloAplicacion())) {
					if (next.getId().getPrioridad() > last.getId()
							.getPrioridad()) {
						s.set(s.size() - 1, next);
					}
				} else {
					s.add(next);
				}
			}
			last = next;
		}

		return s;
	}

	public static ArrayList<imx_seguridad> get(String nombreUsuario,
			String tituloAplicacion) {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_seguridad> s = get(sess, nombreUsuario, tituloAplicacion);
		if (sess.isOpen()) {
			sess.close();
		}
		return s;
	}

}
