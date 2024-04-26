package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_descripcion;
import com.syc.fortimax.hibernate.entities.imx_descripcion_id;
import com.syc.fortimax.hibernate.entities.imx_grupo;

public class imx_descripcion_manager {
	private static final Logger log = Logger.getLogger(imx_descripcion_manager.class);
	
	@SuppressWarnings("unchecked")
	public static ArrayList<imx_descripcion> get(Session sess, imx_descripcion_id id) {
		ArrayList<imx_descripcion> returnList = new ArrayList<imx_descripcion>(); 
		
		StringBuffer sb = new StringBuffer("select desc from imx_descripcion desc ");
		boolean isFirstSet = false;
		
		if(id.getTituloAplicacion() != null || id.getNombreCampo() != null)
			sb.append("where ");
		
		if (id.getTituloAplicacion() != null)
		{
			sb.append("gp.id.tituloAplicacion = :nombreGrupo ");
			isFirstSet = true;
		}
			
		if(id.getNombreCampo() != null)
		{
			if(isFirstSet)sb.append("and ");
			sb.append("gp.id.nombreCampo = :tituloAplicacion ");
		}
		
		Query q = sess.createQuery(sb.toString());
		
		if (id.getTituloAplicacion() != null) {
			q.setString("tituloAplicacion", id.getTituloAplicacion());
		}
		if (id.getNombreCampo() != null) {
			q.setString("nombreCampo", id.getNombreCampo());
		}
		
		sb.append("order by desc.posicionCampo");
		
		returnList = (ArrayList<imx_descripcion>)sess.get(imx_descripcion.class,id);
		
		return returnList;
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
