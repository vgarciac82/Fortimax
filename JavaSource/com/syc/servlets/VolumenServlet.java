package com.syc.servlets;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.entities.imx_usuario;
import com.syc.security.Login;
import com.syc.utils.Json;
import com.syc.volumenes.VolumenesManager;
import com.syc.fortimax.hibernate.entities.imx_usuario;

public class VolumenServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(VolumenServlet.class);
	
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		log.debug(" Ejecutando accion: " + action);
		if("getVolumenes".equals(action))
			ObtenerVolumenes(request,response);
		else if("insertVolumen".equals(action))
			InsertaVolumen(request,response);
		else if("updateVolumenActivo".equals(action))
			ActivaVolumen(request,response);
	}
	private void ObtenerVolumenes(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener Volumenes");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			VolumenesManager vm=new VolumenesManager();
			ArrayList<Object> Volumenes=new ArrayList<Object>();
			Volumenes=vm.ObtenerVolumenes();
			json.add("VolumenModel", Volumenes);
			json.add("success", true);
		}
		catch(Exception e){
			log.debug("Entro al Catch de Obtener Volumenes...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	private void InsertaVolumen(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Insertar Volumenes");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		String datosJson=request.getParameter("VolumenModel");
		try{
			VolumenesManager vm=new VolumenesManager();
			if(vm.existe(datosJson)==false){
				if(vm.insertarVolumen(datosJson)==true){
					log.debug("Volumen insertado correctamente");
					msgRegreso="El volumen fue insertado correctamente";
					json.add("success",true);
				}
				else{
					log.debug("Ocurrio un problema al insertar el volumen");
					msgRegreso="Ocurrio un problema al insertar el volumen";
					json.add("success",false);
				}
			}
			else{
				log.debug("El Volumen existe");
				msgRegreso="El Volumen ya existe: favor de verificarlo";
				json.add("success", false);
			}
		}
		catch(Exception e){
			log.debug("Entro al Catch de Insertar Volumenes");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	private void ActivaVolumen(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		log.debug("Entro a Activar Volumenes");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			String Modelo=request.getParameter("LoginModel");
			imx_usuario us=Json.getObject(Modelo, imx_usuario.class);
			Login ls=new Login();
			VolumenesManager vm=new VolumenesManager();
			if(ls.Autenticacion(us.getNombreUsuario(), us.getCodigo())==true){
				log.debug("Usuario autenticado");
				if(vm.existe(Modelo)==true){
					if(vm.ActivaUnidad(Modelo)==true){
						log.debug("Volumen activado correctamente");
						msgRegreso="Volumen activado correctamente";
						json.add("success", true);
					}
					else{
						log.debug("Error al Activar el volumen");
						msgRegreso="Error al activar el volumen.";
						json.add("success", false);
					}
				}
				else{
					log.debug("Error volumen no existe");
					msgRegreso="Error el volumen no existe.";
					json.add("success", false);
				}
			}
			else{
				log.debug("Usuario o contraseña incorrecta");
				msgRegreso="Error de Autenticacion";
				json.add("success", false);
			}
			
		}
		catch(Exception e){
			log.debug("Entro al Catch de Activar Volumen");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
}
