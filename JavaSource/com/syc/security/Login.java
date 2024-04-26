package com.syc.security;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.utils.Encripta;
public class Login {
	private static final Logger log = Logger.getLogger(Login.class);		
	public boolean Autenticacion(String usuario, String password){
		boolean exito = false;
		try{
			String passwordConfigXml = Config.PASS;
			String usuarioConfigXml = Config.USR;

			if(usuarioConfigXml.equals(Encripta.code32(usuario)) && passwordConfigXml.equals(password)){
				exito = true;
			}
			else{
				Session session=HibernateUtils.getSession();
				Query query=session.createQuery("from imx_usuario where NOMBRE_USUARIO=:usuario AND CDG=:password AND administrador=0 AND activo=0");
				query.setParameter("usuario", usuario);
				query.setParameter("password", password);
				if(query.uniqueResult()!=null){
					exito = true;
				}
			}
		}
		catch(Exception e){
			log.error(e,e);
		}
		return exito;
	}
}