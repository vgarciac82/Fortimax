package com.syc.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.managers.imx_aplicacion_manager;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.servlets.models.NodoModel;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;

/**
 * Servlet implementation class BusquedaExpedienteServlet
 */
public class IfimaxDocumentosServlet extends HttpServlet implements ParametersInterface {
	private static final long serialVersionUID = 1L;
    private static final Logger log=Logger.getLogger(IfimaxDocumentosServlet.class);   
    
    public IfimaxDocumentosServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		doWork(request,response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doWork(request,response);
	}
	
	protected void doWork(HttpServletRequest request, HttpServletResponse response) {
		String action=request.getParameter("action");
		log.debug("Ejecutando accion: "+action);
		if("getDocumentos".equals(action))
			getDocumentos(request,response);
		else if("getArbol".equals(action))
			getArbol(request,response);
		else
			AccionInvalida(request,response);
	}
	
	private void AccionInvalida(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = "El action ingresado no es válido";
		json.add("message", message);
		json.returnJson(response);
	}
	
	String[] nodos = new String[]{"USR_GRALES_G45C0", "USR_GRALES_G1C1"};
	
	//TODO: Corregir, es posible tener documentos repetidos
	private void getDocumentos(HttpServletRequest request,HttpServletResponse response){
		Json json=new Json();
		json.add("success", false);
		try{
			List<Map<String,Object>> documentos = new ArrayList<Map<String,Object>>();
			for(String nodo : nodos) {
				List<imx_documento> imx_documentos = new imx_documento_manager().select(nodo).list();
				for(imx_documento imx_documento : imx_documentos) {
					Map<String,Object> documento = new HashMap<String,Object>();
					documento.put("nodo", 
						new GetDatosNodo(
							imx_documento.getId().getTituloAplicacion(),
							imx_documento.getId().getIdGabinete(),
							imx_documento.getId().getIdCarpetaPadre(),
							imx_documento.getId().getIdDocumento()
						).toString()
					);
					documento.put("nombre", imx_documento.getNombreDocumento());
					documento.put("descripcion", imx_documento.getDescripcion());
					documento.put("existencia", true);
					documento.put("soloLectura", true);
					documentos.add(documento);
				}		
			}
			json.add("documentos", documentos);
			json.add("success", true);
		}
		catch(Exception e){
			log.error(e,e);
			json.add("message", "Hubo un problema al obtener los documentos<br />"+e.toString());
			json.add("error", e.toString());
		}
		json.returnJson(response);
	}
	
	private void getArbol(HttpServletRequest request,HttpServletResponse response){
		Json json=new Json();
		json.add("success", false);
		try{
			List<Map<String,Object>> documentos = new ArrayList<Map<String,Object>>();
			Set<String> gavetas = new HashSet<String>();
			for(String nodo : nodos) {
				String gaveta = new GetDatosNodo(nodo).getGaveta(); 
				gavetas.add(gaveta);
			}
			List<imx_aplicacion> imx_aplicaciones = new ArrayList<imx_aplicacion>();
			for(String gaveta : gavetas) {
				imx_aplicaciones.add(imx_aplicacion_manager.select(gaveta));
			}
			
			NodoModel nodoModel = new NodoModel(imx_aplicaciones);
			json.add("text", ".");
			json.add("children", nodoModel);
			json.add("root", documentos);
			json.add("success", true);
		}
		catch(Exception e){
			log.error(e,e);
			json.add("message", "Hubo un problema al obtener el árbol<br />"+e.toString());
			json.add("error", e.toString());
		}
		json.returnJson(response);
	}
}
