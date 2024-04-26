package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_tipo_usuario;

public class imx_tipo_usuario_manager {
	
	public ArrayList<imx_tipo_usuario> getTiposUsuario() {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_tipo_usuario> tiposUsuario = new ArrayList<imx_tipo_usuario>();

		String hquery = "from imx_tipo_usuario";

		Query q = sess.createQuery(hquery);

		tiposUsuario = (ArrayList<imx_tipo_usuario>) q.list();

		return tiposUsuario;
	}

}
