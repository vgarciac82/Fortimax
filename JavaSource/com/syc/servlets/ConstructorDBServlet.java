package com.syc.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.utils.Json;

public class ConstructorDBServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ConstructorDBServlet.class);
	private static final long serialVersionUID = 1222465349870635382L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		   throws ServletException, IOException {
		doWork(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doWork(request, response);
	}
	
	public void doWork(HttpServletRequest request, HttpServletResponse response) {
		String action = request.getParameter("action");
		log.debug(" Ejecutando accion: " + action);
		if (action != null) {
			if ("build_db".equals(action))
				construyeBD(request, response);
			else if ("init_db".equals(action))
				inicializaDB(request, response);
			else
				accionInvalida(request,response);
		}
	}

	private void accionInvalida(HttpServletRequest request,
				HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = "El action ingresado no es válido";
		json.add("message", message);
		json.returnJson(response);
	}
		
	public void construyeBD(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		boolean success = false;
		String message = "";
		String error = "";
		try {
			message="La Base de Datos fue creada exitosamente";
			
			List<String> exceptions = new ArrayList<String>();
			List<Exception> exceptionsUpdateSchema = HibernateUtils.updateSchema();
			 	
			if(exceptionsUpdateSchema.size()>0) {
					message+=" aunque con algunos problemas<br /><br />Se encontraron las siguientes excepciones al crear la DB:";
					for(Exception e : exceptionsUpdateSchema) {
						exceptions.add(e.getMessage());
						message+="<br />"+e.getMessage();
					}
			}
			
			List<Exception> exceptionsImportInitialData = HibernateUtils.importInitialData();
			
			if(exceptionsImportInitialData.size()>0) {
				message+="<br /><br />Adicionalmente se intento importar los datos iniciales, aparecieron las siguientes advertencias (Puede ser normal):";
				for(Exception e : exceptionsImportInitialData) {
					exceptions.add(e.getMessage());
					message+="<br />"+e.getMessage();
				}
			}
			
			json.add("exceptions", exceptions);
			
			success=true;
		} catch (HibernateException e) {
			log.error(e, e);
			message="Hubo un error al ejecutar la actualización de la Base de Datos";
			error=e.toString();
		} catch (Exception e) {
			log.error(e, e);
			message="Error no controlado: "+e.getMessage();
			error=e.toString();
		}			
		json.add("success",success);
		json.add("message", message);
		if(!error.equals(""))
			json.add("error", error);
		json.returnJson(response);
	}
	
	private void inicializaDB(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		boolean success = false;
		String message = "";
		String error = "";
		try {
			message="La Base de Datos fue inicializada exitosamente";
			
			List<String> exceptions = new ArrayList<String>();
			for(Exception e : HibernateUtils.importInitialData()) {
				exceptions.add(e.getMessage());
			}
			
			json.add("exceptions", exceptions);
			success=true;
		} catch (HibernateException e) {
			log.error(e, e);
			message="Hubo un error al ejecutar la importación de datos iniciales de la Base de Datos";
			error=e.toString();
		} catch (Exception e) {
			log.error(e, e);
			message="Error no controlado "+e.getMessage();
			error=e.toString();
		}			
		json.add("success",success);
		json.add("message", message);
		if(!error.equals(""))
			json.add("error", error);
		json.returnJson(response);
	}
}
