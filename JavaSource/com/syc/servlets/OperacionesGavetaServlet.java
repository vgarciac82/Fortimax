package com.syc.servlets;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.google.gson.JsonSyntaxException;
import com.syc.catalogos.CatalogosManager;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.configuracion.VariablesEntornoManager;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_bitacora;
import com.syc.fortimax.hibernate.entities.imx_catalogo;
import com.syc.fortimax.hibernate.entities.imx_catalogo_estructuras;
import com.syc.fortimax.hibernate.entities.imx_catalogo_privilegios;
import com.syc.fortimax.hibernate.entities.imx_catalogo_tipo_documento;
import com.syc.fortimax.hibernate.entities.imx_config;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_documentos_atributos;
import com.syc.fortimax.hibernate.entities.imx_estruc_doctos;
import com.syc.fortimax.hibernate.entities.imx_estruc_doctos_id;
import com.syc.fortimax.hibernate.entities.imx_estruc_doctos_manager;
import com.syc.fortimax.hibernate.entities.imx_grupo;
import com.syc.fortimax.hibernate.entities.imx_historico_documento;
import com.syc.fortimax.hibernate.entities.imx_org_carpeta;
import com.syc.fortimax.hibernate.entities.imx_pagina;
import com.syc.fortimax.hibernate.entities.imx_perfiles_privilegios;
import com.syc.fortimax.hibernate.entities.imx_tipo_usuario;
import com.syc.fortimax.hibernate.entities.imx_usuario;
import com.syc.fortimax.hibernate.entities.imx_videos;
import com.syc.fortimax.hibernate.managers.imx_bitacora_manager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_estructuras_manager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_manager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_tipo_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_documentos_atributos_manager;
import com.syc.fortimax.hibernate.managers.imx_historico_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_org_carpeta_manager;
import com.syc.fortimax.hibernate.managers.imx_pagina_manager;
import com.syc.fortimax.hibernate.managers.imx_tipo_usuario_manager;
import com.syc.fortimax.managers.DocumentosManager;
import com.syc.fortimax.managers.GruposManager;
import com.syc.fortimax.managers.PerfilesManager;
import com.syc.fortimax.managers.PrivilegioManager;
import com.syc.gavetas.EstructuraManager;
import com.syc.gavetas.Gaveta;
import com.syc.gavetas.GavetaCampo;
import com.syc.gavetas.GavetaManager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.servlets.XMLConfig.XMLConfig;
import com.syc.servlets.models.AtributosModel;
import com.syc.servlets.models.CatalogoModel;
import com.syc.servlets.models.ConfigPDFModel;
import com.syc.servlets.models.CorreoBasicoModel;
import com.syc.servlets.models.CorreoOpcionalModel;
import com.syc.servlets.models.GavetaCampoModel;
import com.syc.servlets.models.NodoModel;
import com.syc.servlets.models.UsuarioModel;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.Encripta;
import com.syc.utils.Json;
import com.syc.utils.Utils;
import com.syc.utils.XML;

public class OperacionesGavetaServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(OperacionesGavetaServlet.class);

	private static final long serialVersionUID = -8593520240823895736L;
	/**
	 * Constructor of the object.
	 */
	String tituloApp = "";

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 *
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		try {
			doWork(request, response);
		} catch (Exception e) {	log.error(e,e);

		log.debug(e);
		} catch (Throwable t) {	log.error(t,t);

		log.error(t,t);
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to
	 * post.
	 *
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String mensaje = "";
		boolean succes = false;
		try {

			succes = doWork(request, response);
			if (succes)
				mensaje = "Operacion terminada con exito";
			else
				mensaje = "La operacion no se pudo completar";

			// response.setContentType("text/plain");
			//TODO: Convertir a JSON para que lo reciba jsp
			// Se comentarizo lo siguiente porque al recibirse se agrega este script al json y se vuelve indecodificable.
			//			PrintWriter out = response.getWriter();
			//			out.println("<SCRIPT LANGUAGE=javascript>");
			//			out.println("alert('" + mensaje + "');");
			//			out.println("</SCRIPT>");
			//			out.flush();
			//			out.close();
			//			out = null;


		} catch (Exception e) {	log.error(e,e);


		mensaje = "No fue posible realizar la operacion: " + e.getMessage();
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setContentType("text/plain");
		log.debug("No fue posible realizar la operacion: " + e);
		PrintWriter out = response.getWriter();
		out.println(mensaje);
		out.flush();
		out.close();
		out = null;

		} catch (Throwable t) {	log.error(t,t);

		log.error(t,t);
		}
	}

	public boolean doWork(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String action = request.getParameter("action");
		log.debug(" Ejecutando accion: " + action);
		if (action != null) {
			if ("getIndicesListJson".equals(action))
				enviaListaIndicesJson(request, response);
			else if ("getTiposListJson".equals(action))
				enviaListaTiposJson(request, response);
			else if ("getCatalogosListJson".equals(action))
				enviaListaCatalogosJson(request, response);
			else if ("deletecatalogo".equals(action))
				DeleteCatalogo(request, response);
			else if ("bitacora".equals(action))
				ConsultarBitacora(request, response);
			else if ("getbitacora".equals(action))
				obtenerBitacora(request, response);
			else if("getgaveta".equals(action))
				ObtenerGavetaJson(request, response);
			else if("listgavetas".equals(action))
				ListGavetas(request, response);
			else if("editgaveta".equals(action))
				EditarGavetas(request, response);
			else if("deletegavetas".equals(action))
				EliminarGavetas(request, response);
			else if("listusuarios".equals(action))
				ListUsuarios(request, response);
			else if("createusuario".equals(action))
				CrearUsuario(request, response);
			else if("getcatalogo".equals(action))
				ObtenerCatalogo(request, response);
			else if("getusuario".equals(action))
				ObtenerUsuario(request, response);
			else if("editusuario".equals(action))
				EditarUsuario(request, response);
			else if("deleteusuario".equals(action))
				EliminarUsuario(request, response);
			else if("listcatalogos".equals(action))
				ListCatalogos(request, response);
			else if("redirect".equals(action)) //TODO: Hacer de esto su propio servlet
				Redirect(request, response);
			else if("create".equals(action)) //TODO: Cambiar el Action y el nombre del Metodo una vez que este listo.
				CrearGavetaJson(request, response);
			else if("insertCatalogo".equals(action)) 
				CrearCatalogoJsonExtjs(request, response);
			else if("ObtenerCatalogo".equals(action)) 
				ObtenerCatalogoJsonExtjs(request, response);
			else if("ActualizaCatalogo".equals(action))
				ActualizaCatalogo(request,response);
			else if("ActualizaVariablesG".equals(action))
				ActualizacionVariablesEntorno(request,response);
			else if("getVariablesEntorno".equals(action))
				ObtenerVariables(request,response);
			else if("getUserPriv".equals(action))
				ObtenerUsuariosPriv(request,response);
			else if("getGavetPriv".equals(action))
				ObtenerGavetasPriv(request,response);
			else if("getPriv".equals(action))
				ObtenerPrivilegios(request,response);
			else if("getPrivG".equals(action))
				ObtenerArbolPrivilegios(request,response);
			else if("modPriviU".equals(action))
				GuardarPrivUs(request,response);
			else if("getGSelect".equals(action))
				ObtenerGavetasSelect(request,response);
			else if("getGrupos".equals(action))
				ObtenerGrupos(request,response);
			else if("getUsD".equals(action)||"getUsA".equals(action))
				ObtenerUsuariosGrupo(request,response);
			else if("crearGrupo".equals(action))
				CrearGrupo(request,response);
			else if("updateGrupo".equals(action))
				ActualizarGrupo(request,response);
			else if("deleteGrupo".equals(action))
				EliminaGrupo(request,response);
			else if("getGrupoPriv".equals(action))
				ObtenerGruposPrivilegios(request,response);
			else if("getGSelectG".equals(action))
				ObtenerGavetasGrupo(request,response);
			else if("getPrivGru".equals(action))
				ObtenerPrivGrupos(request,response);
			else if("getPrivGG".equals(action))
				ObtenerArbolPGrupos(request,response);
			else if("modPriviG".equals(action))
				GuardarPrivGru(request,response);
			else if("getEstructuras".equals(action))
				ObtenerEstructuras(request,response);
			else if("creaEstructura".equals(action))
				CrearEstructuras(request,response);
			else if("getEstructuraDatos".equals(action))
				ObtenerDatosEstructura(request,response);
			else if("getEstructuraArbol".equals(action))
				ObtenerArbolEstructura(request,response);
			else if("actualizaEstructura".equals(action))
				ActualizaEstructura(request,response);
			else if("deleteEstructura".equals(action))
				EliminaEstructura(request,response);
			else if("getEstructuraMVC".equals(action)) //TODO: mvc?
				ObtenerEstructuraMVC(request,response);
			else if("creaEstructuraMVC".equals(action))
				CrearActualizarEstructuraMVC(request,response);
			else if("getAtributosByNodo".equals(action))
				getAtributosByNodo(request,response);
			else if("exportarJson".equals(action))
				ExportarJson(request, response);
			else if("importarJsonEstructura".equals(action))
				ImportarJsonEstructura(request, response);
			else if("getGavetasPDF".equals(action))
				ObtenerGavetasPDF(request,response);
			else if("getConfigPDF".equals(action))
				ObtenerConfigPDF(request,response);
			else if("editConfigPDF".equals(action))
				EditarConfigPDF(request,response);
			else if("getConfigXML".equals(action))
				ObtenerConfigXML(request,response);
			else if("getGavD".equals(action)||"getGavA".equals(action))		 	
                ObtenerGavetasEst(request,response);
			else if("asignaGaveE".equals(action))
                AsignaGavetasE(request,response);
			else if("createDoc".equals(action))
				crearPlantillaDocumento(request,response);
			else if("getDocumento".equals(action))
				obtenerPlantillaDocumento(request,response);
			else if("editDoc".equals(action))
				editarPlantillaDocumento(request,response);
			else if("getDocumentos".equals(action))
				obtenerPlantillas(request,response);
			else if("deleteDocumento".equals(action))
				eliminaPlantillaDocumento(request,response);
			else if("CambiarPassword".equals(action))
				ModificarPassword(request,response);
			else if("getVideos".equals(action))
				ObtenerVideos(request,response);
			else if("getOCR".equals(action))
				ObtenerOCR(request,response);
			else if("setOCR".equals(action))
				EscribirOCR(request,response);
			else if("getMiniaturas".equals(action))
				ObtenerMiniaturas(request,response);
			else if("getPrintFiles".equals(action))
				ObtenerArchivosImpresion(request, response);
			else if("listPerfiles".equals(action))
				ObtenerCatalogoPerfiles(request,response);
			else if("obtenerPrivilegiosPerfiles".equals(action))
				ObtenerPrivilegiosPerfiles(request,response);
			else if("creaPerfil".equals(action))
				CreaPerfil(request,response);
			else if("actualizaPerfil".equals(action))
				ActualizaPerfil(request,response);
			else if("borrarPerfil".equals(action))
				BorrarPerfil(request,response);
			else if("getAtributos".equals(action))
				ObtenerAtributos(request, response);
			else if("getHistorico".equals(action))
				ObtenerHistorico(request, response);
			else if("getUsuariosDisponiblesPerfil".equals(action)||"getUsuariosAsignadosPerfil".equals(action))
				ObtenerUsuairosGruposPerfil(request,response);
			else if("asignaUsuariosPerfil".equals(action))
				AsignarUsuariosGavetasPerfil(request,response);
			else if("getMailElements".equals(action))
				getMailElements(request, response);
			else if("getPerfilesCorreo".equals(action))
				getPerfilesCorreo(request, response);
			else if("editMailElements".equals(action))
				editMailElements(request, response);
			else if("existeNodo".equals(action))
				existeNodo(request, response);
			else if("existBD".equals(action))
				existBD(request, response);
			else if("validateBD".equals(action))
				validateBD(request, response);
			else if("test".equals(action))
				test(request, response);
			else
				AccionInvalida(request,response);
		}
		return true;
	}

	private void AccionInvalida(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = "El action ingresado no es válido";
		json.add("message", message);
		json.returnJson(response);
	}

	private void ListCatalogos(HttpServletRequest request,
			HttpServletResponse response) {

		Json jsonmanager = new Json();

		try {
				HashMap<String, Object> record = new HashMap<String, Object>();
				ArrayList<Object> catalog_model= new ArrayList<Object>();

				imx_catalogo catalogo = null;
				List<imx_catalogo> catalogos = new imx_catalogo_manager().list();

				for(int i=0;i<catalogos.size();i++) {
					catalogo = catalogos.get(i);
					record.clear();
					record.put("ID", i+1 );
					record.put("Nombre", catalogo.getNombreCatalogo());
					record.put("Actualizar", "1");
					record.put("Eliminar", "1");
					catalog_model.add(record.clone());
					jsonmanager.add("catalog", catalog_model);
				}

				jsonmanager.add("success",true); //Aunque no lo utilizamos manejamos esta metavariable de control.
				jsonmanager.add("total", catalog_model.size() ); //Mandamos el total
				jsonmanager.returnJson(response); //Aqui enviamos la estrucutura a "Gson" para que nos regrese el "string json" validado.
			} catch (Exception e) {
				//Se deben manejar metadatos de control
				log.error(e,e);
				jsonmanager.add("success",false);


				jsonmanager.add("message", "Error no controlado");
				jsonmanager.add("error", e.toString());
				jsonmanager.returnJson(response);
			}

	}

	private void ListGavetas(HttpServletRequest request,
			HttpServletResponse response) {
		Json jsonmanager = new Json();

		try {
				GavetaManager gm = new GavetaManager();
				HashMap<String, Object> record = new HashMap<String, Object>();
				ArrayList<Object> catalog_model= new ArrayList<Object>();
				Gaveta gaveta = null;
				ArrayList<Gaveta> gavetas = gm.listGavetas();

				for(int i=0;i<gavetas.size();i++) {
					gaveta = (Gaveta)gavetas.get(i);
					record.clear();
					record.put("ID", i+1 );
					record.put("Nombre", gaveta.getNombre());
					record.put("Actualizar", "1");
					record.put("Eliminar", "1");
					catalog_model.add(record.clone());
					jsonmanager.add("catalog", catalog_model);
				}

				jsonmanager.add("success",true); //Aunque no lo utilizamos manejamos esta metavariable de control.
				jsonmanager.add("total", catalog_model.size() ); //Mandamos el total
				jsonmanager.returnJson(response); //Aqui enviamos la estrucutura a "Gson" para que nos regrese el "string json" validado.
			} catch (Exception e) {
				//Se deben manejar metadatos de control
				log.error(e,e);
				jsonmanager.add("success",false);
				jsonmanager.add("message", "Error no controlado");
				jsonmanager.add("error", e.toString());
				jsonmanager.returnJson(response);
			}

	}

// SIN REF
//	private void CrearGaveta(HttpServletRequest request,
//			HttpServletResponse response){}

	private void CrearGavetaJson(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String gaveta_msgs;
		//String gavetaJson = null;
		String gavetaCamposJson = null;
		try{
			//gavetaJson = Json.getFromRequest(request,"gavetamodel"); //tienes que recibir el stringJson del request
			//gavetaCamposJson = Json.getFromRequest(request,"gavetacampomodel"); //tienes que recibir el stringJson del request
			//GavetaModel gavetaModel = Json.getObject(gavetaJson, GavetaModel.class);
			
			String nombre_gav = request.getParameter("nombre_gav");
			String descripcion_gav = request.getParameter("descripcion_gav");
			gavetaCamposJson = request.getParameter("gavetamodel");
			GavetaCampoModel[] gavetaCamposModel = Json.getObject(gavetaCamposJson, GavetaCampoModel[].class);
			
			GavetaManager gm = new GavetaManager();
			Gaveta gaveta = GavetaCampoModel.getGaveta(nombre_gav,descripcion_gav,gavetaCamposModel);

			if(gm.existeGaveta(gaveta.getNombre())){ 
				gaveta_msgs = "Ya existe una Gaveta con este nombre";
			} else {
				if(gm.creaGaveta(gaveta)) {
					gaveta_msgs = "Gaveta creada exitosamente";
					json.add("success",true);
				}
				else {
					gaveta_msgs = "Hubo un problema al crear la gaveta";
				}
			}
		} catch(Exception e){
			log.error(e,e);
			gaveta_msgs = "Hubo un problema al crear la gaveta<br />"+e.toString();
			json.add("error", e.toString());
			/*
			try {
				json.add("gavetaJson", Json.getMap(gavetaJson));
			} catch (Exception ex) {
			}
			*/
			try {
				json.add("gavetaCamposJson", Json.getMap(gavetaCamposJson));
			} catch (Exception ex) {
			}
		}
		json.add("message", gaveta_msgs);
		json.returnJson(response);
	}
	
	private void EditarGavetas(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try{
			String nombre_gaveta = request.getParameter("nombre_gav");
			String descripcion_gaveta = request.getParameter("descripcion_gav");
			
			String camposModificadosJson = request.getParameter("gavetamodelchanges");
			String camposEliminadosJson = request.getParameter("gavetamodeldeletes");
			GavetaCampoModel[] camposModificadosModel = Json.getObject(camposModificadosJson, GavetaCampoModel[].class);
			GavetaCampoModel[] camposEliminadosModel = Json.getObject(camposEliminadosJson, GavetaCampoModel[].class);
			
			GavetaManager gm = new GavetaManager();
			if(gm.existeGaveta(nombre_gaveta)) {
				Gaveta gavetaOriginal = gm.selectGaveta(nombre_gaveta);
				Gaveta gavetaModificada = GavetaCampoModel.getGaveta(nombre_gaveta,descripcion_gaveta,camposModificadosModel);
				gm.actualizaGaveta(gavetaOriginal,gavetaModificada,GavetaCampoModel.getNombresCampos(gavetaModificada));
				
				GavetaCampo[] camposEliminados = GavetaCampoModel.getCampos(camposEliminadosModel);
				for(int i=0;i<camposEliminados.length;i++) {
					gm.eliminaCampo(gavetaOriginal, camposEliminados[i]);
				}
				
				json.add("message", "Gaveta modificada exitosamente");
				json.add("success",true);
			} else {
				json.add("message", "La gaveta \""+nombre_gaveta+"\" no existe");
				json.add("error","La gaveta \""+nombre_gaveta+"\" no existe");
			}
		} catch(Exception e){
			log.error(e,e);
			json.add("message","Hubo un problema al editar la gaveta<br />"+e.toString());
			json.add("error", e.toString());
		}
		json.returnJson(response);
	}
	
	private void ObtenerGavetaJson(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String nombre_gaveta = request.getParameter("select");
		try{
			GavetaManager gm = new GavetaManager();
			if(gm.existeGaveta(nombre_gaveta)) {
				Gaveta gaveta = gm.selectGaveta(nombre_gaveta);
				json.add("Nombre_gav",gaveta.getNombre());
				json.add("Descripcion_gav",gaveta.getDescripcion());
				ArrayList<GavetaCampoModel> campos = new ArrayList<GavetaCampoModel>(Arrays.asList(GavetaCampoModel.getGavetaCamposModel(gaveta)));
				GavetaCampoModel idGabinete = new GavetaCampoModel("ID_GABINETE","ID");
				idGabinete.setEditable(false);
				idGabinete.setTipo("Entero Hidden");
				campos.add(0,idGabinete);
				json.add("Campos", campos);
				json.add("success",true);
			} else {
				json.add("message", "La gaveta \""+nombre_gaveta+"\" no existe");
				json.add("error","La gaveta \""+nombre_gaveta+"\" no existe");
			}
		} catch(Exception e){
			log.error(e,e);
			json.add("message", "Hubo un problema al crear la gaveta<br />"+e.toString());
			json.add("error", e.toString());
		}
		json.returnJson(response);
	}

	private void EliminarGavetas(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Entro a eliminar gavetas");
		Json json=new Json();
		json.add("success", false);
		String msg="";
		try{
				String select= request.getParameter("select");
				GavetaManager gm = new GavetaManager();
				if(gm.borraGaveta(select)){
					
					json.add("success", true);
					msg = "Gaveta borrada exitosamente";
					log.trace("La gaveta "+select+" fue borrada correctamente");
				}
				else{
					
					json.add("success",false);
					msg = "Hubo un problema al borrar la gaveta: "+ select;
					log.error("Hubo un problema al borrar la gaveta " + select);
				}
			}
			catch(Exception e){
				log.error(e,e);
				json.add("success",false);
			}
		json.add("message", msg);
		json.returnJson(response);

	}

	private void ListUsuarios(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		try{
			UsuarioManager um = new UsuarioManager();
			HashMap<String, Object> record = new HashMap<String, Object>();
			ArrayList<Object> catalog_model= new ArrayList<Object>();

			Usuario usuario = null;
			ArrayList<Usuario> usuarios=um.listUsuarios();

			for(int i=0;i<usuarios.size();i++){
				usuario = (Usuario)usuarios.get(i);
				record.clear();
				record.put("ID", i+1 );
				record.put("Nombre", usuario.getNombreUsuario());
				record.put("Actualizar", "1");
				record.put("Privilegios","Privilegios");
				record.put("Eliminar", "1");
				catalog_model.add(record.clone());
				json.add("catalog", catalog_model);
			}
			json.add("total", catalog_model.size() ); //Mandamos el total
			json.add("success",true); //Aunque no lo utilizamos manejamos esta metavariable de control.

			json.returnJson(response); //Aqui enviamos la estrucutura a "Gson" para que nos regrese el "string json" validado.
		} catch(Exception e){
			log.error(e,e);
			json.add("message", "Error no controlado");
			json.add("error", e.toString());
			json.add("success",false);
			json.returnJson(response);
		}
	}

	private void EliminarUsuario(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String usuario_msgs;
		String select = request.getParameter("select");
		try{
			UsuarioManager um = new UsuarioManager();
			if(select==null) {
				usuario_msgs="El parámetro select es requerido";
			} else if(!um.existeUsuario(select)) {
				usuario_msgs="El usuario "+select+" no existe";
			} else if(um.borraUsuario(select)){
				usuario_msgs = "Usuario eliminado exitósamente";
				json.add("success",true);
			} else{
				usuario_msgs = "Hubo un problema al borrar el usuario";
			}
		} catch(Exception e){
			log.error(e,e);
			usuario_msgs = "Hubo un problema al borrar el usuario";
			json.add("error", e.toString());
		}
		json.add("message", usuario_msgs);
		json.returnJson(response);
	}

	private void ObtenerUsuario(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/plain; charset=utf-8");
		Json json = new Json();
		json.add("success",false);
		String usuario_msgs;
		try {
				UsuarioManager um = new UsuarioManager();
				Usuario usuario = um.selectUsuario(request.getParameter("select"));
				json.add("usuario",getUsuarioModel(usuario));
				json.add("success",true);
			} catch (Exception e) {
				log.error(e,e);
				usuario_msgs = "Hubo un problema al obtener el usuario";
				json.add("error", e.toString());
				json.add("message", usuario_msgs);
			}
		json.returnJson(response);
	}

	private void ObtenerCatalogo(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/plain; charset=utf-8");
		ArrayList<Object> catalogo = new ArrayList<Object>();
		String action = request.getParameter("select");

		Json json = new Json();
		json.add("success",false);
		String catalogos_msgs;
		try {
				if (action != null) {
					if ("tipo_usuario".equals(action))
						catalogo=obtenerTiposUsuario();
					else if ("pregunta_secreta".equals(action))
						catalogo=obtenerPreguntasSecretas();
					else if ("activo".equals(action))
						catalogo=obtenerEstadoUsuario();
					else if ("nivel".equals(action))
						catalogo=obtenerNivelUsuario();
				}
				json.add("catalogo",catalogo);
				json.add("success",true);
			} catch (Exception e) {
				log.error(e,e);
				catalogos_msgs = "Hubo un problema al obtener el catalogo ";
				json.add("error", e.toString());
				json.add("message", catalogos_msgs);
			}
		json.returnJson(response);
	}

	private ArrayList<Object> obtenerTiposUsuario() {
		HashMap<String, Object> rec = new HashMap<String, Object>();		
		ArrayList<Object> catalogo_tipo_usuario= new ArrayList<Object>();
		
		imx_tipo_usuario_manager tipoUsuarioManager = new imx_tipo_usuario_manager();
		ArrayList<imx_tipo_usuario> tipos = tipoUsuarioManager.getTiposUsuario();

		for (int i = 0; i < tipos.size(); i++) {
			imx_tipo_usuario tipo_usuario = tipos.get(i);
			rec.clear();
			rec.put("ID", tipo_usuario.getTipoUsuario() );
			rec.put("Valor", tipo_usuario.getTuDescripcion());
			catalogo_tipo_usuario.add(rec.clone());
		}

		return catalogo_tipo_usuario;
	}	
	private ArrayList<Object> obtenerPreguntasSecretas() {
		HashMap<String, Object> rec = new HashMap<String, Object>();		
		ArrayList<Object> catalogo_pregunta= new ArrayList<Object>();
		
		rec.clear();
		rec.put("ID", 1);
		rec.put("Valor", "Nombre de mi mascota favorita");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 2);
		rec.put("Valor", "Pelicula favorita");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 3);
		rec.put("Valor", "Aniversario [mm/dd/aa]");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 4);
		rec.put("Valor", "Nombre de mi abuela paterna");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 5);
		rec.put("Valor", "Nombre de mi abuelo paterno");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 6);
		rec.put("Valor", "Mi lugar favorito");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 7);
		rec.put("Valor", "Nombre de mi escuela secundaria");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 8);
		rec.put("Valor", "Mi obra de arte favorita");
		catalogo_pregunta.add(rec.clone());
		rec.put("ID", 9);
		rec.put("Valor", "Mi restaurante favorito");
		catalogo_pregunta.add(rec.clone());
		
		return catalogo_pregunta;
	}
	
	private ArrayList<Object> obtenerEstadoUsuario(){
		HashMap<String, Object> rec = new HashMap<String, Object>();		
		ArrayList<Object> catalogo_estado= new ArrayList<Object>();
		
		//TODO: Este metodo debe obtener los valores de la tabla
		//imx_usuario de la columna VIGENCIA
		rec.clear();
		rec.put("ID", 0);
		rec.put("Valor", "Activo");
		catalogo_estado.add(rec.clone());
		rec.put("ID", 1);
		rec.put("Valor", "Inactivo");
		catalogo_estado.add(rec.clone());
		rec.put("ID", 2);
		rec.put("Valor", "Deshabilitado");
		catalogo_estado.add(rec.clone());
		
		return catalogo_estado;
	}
	
	private ArrayList<Object> obtenerNivelUsuario(){
		HashMap<String, Object> rec = new HashMap<String, Object>();		
		ArrayList<Object> catalogo_nivel= new ArrayList<Object>();

		//TODO: Este metodo debe obtener los valores de la tabla
		//imx_usuario de la columna ADMINISTRADOR
		rec.clear();
		rec.put("ID", 0);
		rec.put("Valor", "Administrador");
		catalogo_nivel.add(rec.clone());
		rec.put("ID", 1);
		rec.put("Valor", "Estandar");
		catalogo_nivel.add(rec.clone());
		
		return catalogo_nivel;
	}
	
	private UsuarioModel getUsuarioModel(Usuario usuario) {
		UsuarioModel usuarioModel = new UsuarioModel();
		usuarioModel.setNombreUsuario(usuario.getNombreUsuario());
		usuarioModel.setNombre(usuario.getNombre());
		usuarioModel.setApellidoPaterno(usuario.getApellidoPaterno());
		usuarioModel.setApellidoMaterno(usuario.getApellidoMaterno());
		usuarioModel.setDescripcion(usuario.getDescripcion());
		usuarioModel.setCdg(usuario.getCdg());
		usuarioModel.setGenero(usuario.getGenero());
		usuarioModel.setCorreo(usuario.getCorreo());
		usuarioModel.setPreguntaSecreta(usuario.getPreguntaSecreta());
		usuarioModel.setRespuestaSecreta(usuario.getRespuestaSecreta());

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String fecha_nac = formatter.format(usuario.getFechaNacimiento());
		usuarioModel.setFechaNac(fecha_nac);

		usuarioModel.setCuotaAutorizada(usuario.getBytesAutorizados()/1048576); //Dividir entre 1024*1024 ya que se almacenan como bytes
		usuarioModel.setTipo_usuario(usuario.getTipo_usuario());

		usuarioModel.setActivo(usuario.getActivo());
		usuarioModel.setAdministrador(usuario.getAdministrador());
		
		return usuarioModel;
	}

	private Usuario getUsuario(UsuarioModel usuarioModel) throws ParseException {
		Usuario usuario = new Usuario();
		usuario.setNombreUsuario(usuarioModel.getNombreUsuario());
		usuario.setNombre(usuarioModel.getNombre());
		usuario.setApellidoPaterno(usuarioModel.getApellidoPaterno());
		usuario.setApellidoMaterno(usuarioModel.getApellidoMaterno());
		usuario.setDescripcion(usuarioModel.getDescripcion());
		usuario.setCdg(usuarioModel.getCdg());
		usuario.setGenero(usuarioModel.getGenero());
		usuario.setCorreo(usuarioModel.getCorreo());
		usuario.setPreguntaSecreta(usuarioModel.getPreguntaSecreta());
		usuario.setRespuestaSecreta(usuarioModel.getRespuestaSecreta());

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha_nacimiento = (Date)formatter.parse(usuarioModel.getFechaNac());
		usuario.setFechaNacimiento(fecha_nacimiento);

		usuario.setBytesAutorizados(usuarioModel.getCuotaAutorizada()*1048576); //Multiplicar por 1024*1024 ya que se recibe como bytes

		usuario.setActivo(usuarioModel.getActivo()); //En la interfaz aparece como 'Estado'
		
		imx_tipo_usuario imxTipoUsuario = new imx_tipo_usuario();
		imxTipoUsuario.setTipoUsuario((short)usuarioModel.getTipo_usuario());
		usuario.setImxTipoUsuario(imxTipoUsuario);
		
		usuario.setAdministrador(usuarioModel.getAdministrador());//En la interfaz aparece como 'Nivel'
		
		return usuario;
	}

	private void EditarUsuario(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String usuario_msgs;
		String usuarioJson=null;

		try{
			usuarioJson = Json.getFromRequest(request,"usuariomodel"); //tienes que recibir el stringJson del request
			UsuarioModel usuarioModel = Json.getObject(usuarioJson, UsuarioModel.class);

			UsuarioManager um = new UsuarioManager();

			if(um.existeUsuario(usuarioModel.getNombreUsuario())){
				Usuario usuario = getUsuario(usuarioModel);
				usuario.setCdg(!"".equals(usuarioModel.getCdg()) ? Encripta.code32(usuarioModel.getCdg()) : "");
				
				imx_tipo_usuario imxTipoUsuario = new imx_tipo_usuario();
				imxTipoUsuario.setTipoUsuario((short)usuarioModel.getTipo_usuario());
				usuario.setImxTipoUsuario(imxTipoUsuario);//TODO
				
				usuario.setLocaleDefault(request.getParameter("locale_default"));
				if(um.actualizaUsuario(usuario)) {
					usuario_msgs = "Usuario actualizado exitosamente";
					json.add("success",true);
				} else
					usuario_msgs = "Hubo un problema al actualizar el usuario";
			} else {
				usuario_msgs = "No se puede modificar un usuario que no existe";
			}
		} catch(Exception e){
			log.error(e,e);
			usuario_msgs = "Hubo un problema al actualizar el usuario";
			json.add("error", e.toString());
			try {
				json.add("jsonEntrada", Json.getMap(usuarioJson));
			} catch (Exception ex) {
			}
		}
		json.add("message", usuario_msgs);
		json.returnJson(response);
	}

private void CrearUsuario(HttpServletRequest request, HttpServletResponse response) {
	Json json = new Json();
	json.add("success",false);
	String usuario_msgs;
	String usuarioJson=null;

	try{
		HttpSession session = request.getSession(false);
		usuarioJson = Json.getFromRequest(request,"usuariomodel"); //tienes que recibir el stringJson del request
		UsuarioModel usuarioModel = Json.getObject(usuarioJson, UsuarioModel.class);

		UsuarioManager um = new UsuarioManager();

		if(um.existeUsuario(usuarioModel.getNombreUsuario())){ //TODO Verificar porque lanza error esta linea
			usuario_msgs = "Ya existe ese nombre de usuario";
		} else {
			//Implementacion con Hibernate
			Usuario usuario = getUsuario(usuarioModel);
			usuario.setBanderaConexion('N');
			usuario.setCodigo(session.getId().substring(session.getId().length()-3));
			usuario.setTipoOperacion('4');
			usuario.setCambioCdg('0');
			usuario.setBytesUsados(0);
			usuario.setCdg(um.creaPassword(usuarioModel.getNombreUsuario()));
			imx_tipo_usuario imxTipoUsuario = new imx_tipo_usuario();
			imxTipoUsuario.setTipoUsuario((short)usuarioModel.getTipo_usuario());
			usuario.setImxTipoUsuario(imxTipoUsuario);//TODO
			usuario.setLocaleDefault(request.getParameter("locale_default"));

			if(um.creaUsuario(usuario)) {
				usuario_msgs = "Usuario creado exitosamente";
				json.add("success",true);
			}
			else {
				usuario_msgs = "Hubo un problema al crear el usuario";
			}
		}
	} catch(Exception e){
		log.error(e,e);
		usuario_msgs = "Hubo un problema al crear el usuario<br />"+e.toString();
		json.add("error", e.toString());
		try {
			json.add("jsonEntrada", Json.getMap(usuarioJson));
		} catch (Exception ex) {
		}
	}
	json.add("message", usuario_msgs);
	json.returnJson(response);
}	

	//TODO: Dividir este servlet en varios y crear sus managers,
	// los servlets solo dirigen a clases manager no se opera sobre ellos !!!!!
	private void ConsultarBitacora(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		try {
		log.debug("Creando consulta a la Bitacora ");

		int cantidad = 1000;
		try {
			cantidad = Integer.parseInt(request.getParameter("cantidad"));
		} catch (Exception e) {
			
		}
        imx_bitacora_manager bitacoraManager = new imx_bitacora_manager();
        bitacoraManager.setMaxResults(cantidad);
		List<imx_bitacora> list = bitacoraManager.list();
		
        imx_bitacora logs = new imx_bitacora();

        PrintWriter out = response.getWriter();
        log.debug("Generando informacion para DT ");
		out.println("{'aaData': [");
		//for(int i=0;i < list.size();i++)
		System.out.println("valor list " + list.size());


		for(int i=0;i<list.size();i++)
		{
			logs = list.get(i);
			out.println("['" + (i+1) + "','"+logs.getFecha() +"','"+ logs.getClase() +"','"+ logs.getPrioridad() +"','"+  StringEscapeUtils.escapeJavaScript(logs.getMensaje()) +"']");
			if ( i < list.size()-1)
			{
				out.print(",");
			}
		}

		out.print("]");
		out.println(",'aoColumns': [");
		// ID
		out.println("{ 'sTitle': 'ID', 'sClass': 'center', 'bSortable': true, 'asSorting': [ 'desc' ], 'sWidth': '5%', 'bVisible': true },");

		// Fecha
		out.println("{'sTitle': 'Fecha', 'sClass': 'left', 'bSortable': true, 'sWidth': '10%', 'bVisible': true},");

		//Clase
		out.println("{'sTitle': 'Clase', 'sClass': 'left', 'bSortable': true, 'sWidth': '20%', 'bVisible': true},");

		//Prioridad
		out.println("{'sTitle': 'Prioridad', 'sClass': 'left', 'bSortable': true, 'sWidth': '10%', 'bVisible': true},");

		//Mensaje
		out.println("{'sTitle': 'Mensaje', 'sClass': 'left', 'bSortable': true, 'sWidth': '55%', 'bVisible': true}");
		out.println("] }");
		out.flush();
		out.close();

		} catch (Exception e) {	log.error(e,e);
			log.error(e,e);
		}
		return;
	}
		
	private void obtenerBitacora(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		try {
			int firstResult = -1;
			int maxResults = -1;
			
			String campoBusqueda = request.getParameter("campoBusqueda");
			if(campoBusqueda==null||"".equals(campoBusqueda))
				log.trace("No se especifico un campoBusqueda");
			else
				log.trace("Realizando una búsqueda con campoBusqueda="+campoBusqueda);
			
			String start = null;
			try {
				start = request.getParameter("start");
				firstResult = Integer.parseInt(start);
			} catch (Exception e) {
				log.trace("No se pudo procesar start="+start+" como un entero, no se paginaran los resultados");
			}
			
			String limit = null;
			try {
				limit = request.getParameter("limit");
				maxResults = Integer.parseInt(limit);
			} catch (Exception e) {
				log.trace("No se pudo procesar limit="+limit+" como un entero, se mostraran todos los resultados");
			}
			
	        imx_bitacora_manager bitacoraManager = new imx_bitacora_manager(campoBusqueda,firstResult,maxResults);
			json.add("totalFilas", bitacoraManager.rowCount());
			json.add("valores", bitacoraManager.list());
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}

	@SuppressWarnings("unchecked")
	private void DeleteCatalogo(HttpServletRequest request,
			HttpServletResponse response) throws Exception, IOException {
		log.debug("Eliminar Lista");
		String nombre_catalogo = request.getParameter("select");
		nombre_catalogo = nombre_catalogo.toUpperCase();

		Json json=new Json();
		boolean result=false;
		String msgRegreso="";
		HibernateManager hm = new HibernateManager();

		try {
			Query q = hm.createSQLQuery("SELECT TITULO_APLICACION, NOMBRE_CAMPO FROM imx_campo_catalogo WHERE NOMBRE_CATALOGO = '" + nombre_catalogo + "'");

			if (!q.list().isEmpty()) {
				q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map<String, String>> regs = q.list();

				StringBuffer mensaje = new StringBuffer("ATENCION: El catálogo " + nombre_catalogo);
				mensaje.append(" está asociado con las siguientes gavetas: ");
				for (Map<String, String> map : regs) {
					map = new CaseInsensitiveMap(map);
					mensaje.append(Utils.getString(map.get("TITULO_APLICACION")) + " " );
				}

				msgRegreso = mensaje.toString();
				result = false;

			} else {
				List<Query> queries = new ArrayList<Query>();
				queries.add(hm.createSQLQuery("DELETE FROM IMX_CATALOGO WHERE TBL_CATALOGO = 'MX" + nombre_catalogo + "'"));
				queries.add(hm.createSQLQuery("Drop TABLE MX" + nombre_catalogo));
				hm.executeQueries(queries);
				msgRegreso = "Se elimino el catalogo: " + request.getParameter("select") + " exitosamente";
				result = true;
			}
		} catch (Exception e) {	
			log.debug(e,e);
			result = false;
			msgRegreso = "Hubo un error al eliminar el catalogo";
		} finally {
			hm.close();
		}
		json.add("success", result);
		json.add("message",msgRegreso);
		json.returnJson(response);
	}

// METODO SIN REFERENCIAS
//	public boolean updateAll(HttpServletRequest request,
//			HttpServletResponse response) throws Exception {}

	public void dumpCampos(Gaveta g) {
		log.debug("Volcado de Capos ");
		ArrayList<GavetaCampo> campos = g.getCampos();
		for (int i = 0; i < campos.size(); i++) {
			GavetaCampo gc = campos.get(i);
			log.debug("Campo [" + i + "] valores [" + gc.getNombreCampo()
					+ "] [" + gc.getNombreDesplegar() + "] [" + gc.getTamano()
					+ "] [" + gc.getValPredefinido() + "] [" + gc.getTipoDato()
					+ "] [" + gc.getIndice() + "] [" + gc.getRequerido()
					+ "] [" + gc.getEditable() + "] [" + gc.getLista() + "("
					+ gc.getNombreCatalogo() + ")]");
		}
	}

	@SuppressWarnings("unused")
	private void showRequestContent(HttpServletRequest request,
			HttpServletResponse response) {
		Enumeration<?> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String namElem = enu.nextElement().toString();
			log.debug("Elemento Recibido: " + namElem + " valor: "
					+ request.getParameter(namElem));
		}

	}

	private boolean parametroValido(String parametro) {
		log.debug("parametro: " + parametro + "Valido: "
				+ (parametro != null && parametro.length() > 0));
		return null != parametro && parametro.length() > 0;

	}
	
	private ArrayList<HashMap<String,String>> getArrayListComboBox(BidiMap bidiMap) {
		ArrayList<HashMap<String,String>> arrayListComboBox = new ArrayList<HashMap<String,String>>();
		@SuppressWarnings("unchecked")
		Collection<String> collection = bidiMap.values();
		Iterator<String> iterator = collection.iterator();
		while(iterator.hasNext()) {
			HashMap<String,String> elemento = new HashMap<String,String>();
			String value = iterator.next();
			elemento.put("value", value);
			elemento.put("text", value);
			arrayListComboBox.add(elemento);
		}
		return arrayListComboBox;
	}
	
	private void enviaListaTiposJson(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {	
			json.add("tipos",getArrayListComboBox(GavetaCampoModel.getTipos()));
			json.add("success",true);
		} catch (Exception e) {
			json.add("error","Hubo un error al tratar de generar la lista de tipos: "+e);
		}
		json.returnJson(response);
	}
	
	private void enviaListaIndicesJson(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {	
			json.add("indices",getArrayListComboBox(GavetaCampoModel.getIndices()));
			json.add("success",true);
		} catch (Exception e) {
			json.add("error","Hubo un error al tratar de generar la lista de indices: "+e);
		}
		json.returnJson(response);
	}
	
	private void enviaListaCatalogosJson(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {	
			json.add("catalogos",getArrayListComboBox(CatalogoModel.getCatalogos()));
			json.add("success",true);
		} catch (Exception e) {
			json.add("error","Hubo un error al tratar de generar la lista de catalogos: "+e);
		}
		json.returnJson(response);
	}

	private static class RedirectModel {
		String url;
		private RedirectModel() {
			url="";
		}
	}
	private void CrearCatalogoJsonExtjs(HttpServletRequest request, HttpServletResponse response) {
		log.info("Entro a la Funcion de Crear Catalogo");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso;
		String CatalogoModelJson = null;
		String NombreCat="",TipoCat="";
		try{			
			CatalogoModelJson = request.getParameter("catModel");
			CatalogoModel catalogoModel=Json.getObject(CatalogoModelJson, CatalogoModel.class);
			
			NombreCat=catalogoModel.getNombreCat();
			TipoCat=catalogoModel.getTipoCat();

			
//			ENVIAR LOS DATOS PARA SU TRATAMIENTO
			CatalogosManager cm =new CatalogosManager();
			boolean Unico=cm.catalogoUnico(NombreCat);
			if(Unico==true){
				log.debug("Correcto: Catalogo Unico");
				boolean insertado=cm.insertarCatalogo(NombreCat, TipoCat,catalogoModel.getDatosCat(), catalogoModel.getDefinicion());
				if(insertado==true){
					log.debug("Catalogo Insertado Correctamente");
					msgRegreso="Catalogo Creado Correctamente";
					json.add("success",true);
				}
				else{
					log.debug("Error de Insercion");
					msgRegreso="Error de Insecion.";
					json.add("success",false);
				}
				
			}
			else{
				log.debug("Catlogo Existe");
				msgRegreso="El Nombre del Catalogo ya Existe";
				json.add("success",false);
			}
			
		 
		}
		catch(Exception e){
			log.debug("Entro al Catch de CrearCatalogoJson...");
			msgRegreso="Error Durante la Creacion";
			log.error(e,e);
			json.add("success",false);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);
	}
	private void ObtenerCatalogoJsonExtjs(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Entro a la Funcion de Obtener Catalogo: "+request.getParameter("select"));
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		String Catalogo=request.getParameter("select");
		
		CatalogosManager cm = new CatalogosManager();
		try{
			imx_catalogo imx_catalogo = new imx_catalogo_manager().select(Catalogo).uniqueResult(); 
			if(imx_catalogo==null) {
				log.debug("Catalogo no Existe");
				msgRegreso="El Catalogo no Existe";
			} else {
				json.add("definicion",imx_catalogo.getDefinicion());
				json.add("datos",cm.getDatos(imx_catalogo,null));
				json.add("success",true);
			}
		}
		catch(Exception e){
			log.debug("Entro al Catch de ObtenerCatalogoJson...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);
	}
//ActualizaCatalogo
	private void ActualizaCatalogo(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Entro a la Funcion Actualizar Datos de Catalogo:"+ request.getParameter("select"));
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="",CatalogoModelJson,NombreCat;
		try{
			CatalogoModelJson = request.getParameter("catModel");
			CatalogoModel catalogoModel=Json.getObject(CatalogoModelJson, CatalogoModel.class);
			NombreCat=catalogoModel.getNombreCat();
			CatalogosManager cm =new CatalogosManager();
			boolean Existe=cm.catalogoUnico(NombreCat);
			if(Existe==false){
				log.debug("Catalogo Existe");
				boolean Actualizado=cm.Actualiza(NombreCat,catalogoModel.getDatosCat(),catalogoModel.getDatosBorradosCat(),catalogoModel.getDefinicion());
				if(Actualizado==true){
					log.debug("Catalogo Actualizado Correctamente");
					msgRegreso="Catalogo Actualizado Correctamente";
					json.add("success",true);
				}
				else{
					log.debug("Error de Actualizacion");
					msgRegreso="Error al Actualizar el Catalogo";
				}
				
			}
			else{
				log.debug("Catalogo no Existe");
				msgRegreso="El Nombre del Catalogo no Existe";
			}		
		}
		catch(Exception e){
			log.debug("Entro al Catch de ActualizaCatalogo...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);
	}
	
	//Funciones de Variables de Entorno
	
	private void ActualizacionVariablesEntorno(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a la Funcion Actualizar Variables de Entorno");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			String Actualizados=request.getParameter("ActFil");
			String Eliminados=request.getParameter("EliFil");
			
			VariablesEntornoManager ve=new VariablesEntornoManager();
			if(ve.EliminaFilas(Eliminados)==true){
				Vector<String> Vector=new Vector<String>();
				Vector=ve.ActualizaFilas(Actualizados);
				if(Vector.size()==0){
					log.debug("Cambios Guardados en Variables de Entorno");
					msgRegreso="Cambios Guardados Correctamente";
					json.add("success",true);
				}
				else{
					msgRegreso="Informacion de actualizacion:<br /><br />";
					for(int i=0;i<Vector.size();i++){
						msgRegreso=msgRegreso+Vector.elementAt(i)+"<br />";
					}			
					json.add("success",true);
				}
			}
			else{
				//Error de Eliminacion
				log.debug("Error de Eliminacion de Variables de Entorno");
				msgRegreso="Error durante la Eliminacion";
				json.add("success",false);
			}			
		}
		catch(Exception e){
			log.debug("Entro al Catch de Actualiza Variables de Entorno");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);
	}
	private void ObtenerVariables(HttpServletRequest request, HttpServletResponse response){
		response.setContentType("text/plain; charset=utf-8");
		log.debug("Entro a Obtener Variables de Entorno");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			VariablesEntornoManager ve=new VariablesEntornoManager();
			ArrayList<HashMap<String, Object>> VariablesE = new ArrayList<HashMap<String, Object>>();
			VariablesE=ve.obtenerVariables();
			json.add("Parametros",VariablesE);
			json.add("success",true);
			
		}
		catch(Exception e){
			log.debug("Entro al Catch de Obtener Variables...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	private void ObtenerUsuariosPriv(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener Usurios de privilegios");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			ArrayList<Object> UsuariosA=new ArrayList<Object>();
			UsuariosA=PrivilegioManager.getUsuariosP();
			json.add("nControl", UsuariosA);
			json.add("success",true);	
		}
		catch(Exception e){
			log.debug("Entro al Catch de Obtener Usuarios de privilegios...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerGavetasPriv(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener gavetas de privilegios");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			ArrayList<Object> GavetasP=new ArrayList<Object>();
			GavetasP=PrivilegioManager.getGavetasTotal();
			json.add("gavetas", GavetasP);
			json.add("success",true);	
		} catch(Exception e) {
			log.debug("Entro al Catch de Obtener gavetas de privilegios...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerGavetasSelect(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener gavetas especificas");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			json.add("gavetas", PrivilegioManager.getGavetasP(request.getParameter("select")));
			json.add("success",true);	
		} catch(Exception e) {
			log.debug("Entro al Catch de Obtener gavetas especificas...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerPrivilegios(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener  privilegios");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			ArrayList<Object> Privilegios=new ArrayList<Object>();
			Privilegios=PrivilegioManager.getPrivilegiosG(request.getParameter("usuario"),request.getParameter("gaveta"));
			json.add("privilegios",Privilegios);
			json.add("success", true);
		} catch(Exception e) {
			log.debug("Entro al Catch de Obtener privilegios...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerArbolPrivilegios(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener arbol de privilegios");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
				ArrayList<Object> Arbol=new ArrayList<Object>();
				Arbol=PrivilegioManager.getArbolPrivilegios(request.getParameter("select"));
				json.add("children", Arbol);
				json.add("success", true);
		} catch(Exception e) {
			log.debug("Entro al Catch de arbol de privilegios...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void GuardarPrivUs(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a guardar privilegios de usuarios");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			String PrivilegiosJson=request.getParameter("Modificaciones");
			if(PrivilegioManager.ModPrivUsu(PrivilegiosJson)==true){
				json.add("success", true);
				msgRegreso="Privilegios asignados correctamente";
			} else {
				json.add("success", false);
				msgRegreso="Error al asignar privilegios";
			}	
		} catch(Exception e) {
			log.debug("Entro al Catch de guardar privilegios de usuarios");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerGrupos(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener grupos");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		List<HashMap<String, String>> Grupos=new ArrayList<HashMap<String, String>>();
		try{
			if(request.getParameter("select")==null){
				GruposManager gm=new GruposManager();
				Grupos=gm.ObtenerGrupos(true,null);
				json.add("grupos",Grupos);
				json.add("success",true);	
			} else {
				GruposManager gm=new GruposManager();
				Grupos=gm.ObtenerGrupos(false,request.getParameter("select"));
				json.add("grupos",Grupos);
				json.add("success",true);	
			}
		} catch(Exception e) {
			log.debug("Entro al Catch de obtener grupos");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerUsuariosGrupo(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener usuarios de grupo");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		ArrayList<Object> Usuarios=new ArrayList<Object>();
		GruposManager gm=new GruposManager();
		try{
			if("getUsD".equals(request.getParameter("action"))){
				Usuarios=gm.ObtenerUsuariosD(request.getParameter("select"));
				json.add("usuarios",Usuarios);
				json.add("success",true);
			} else {
				Usuarios=gm.ObtenerUsuariosA(request.getParameter("select"));
				json.add("usuarios",Usuarios);
				json.add("success",true);		
			}
		} catch(Exception e) {
			log.debug("Entro al Catch de obtener usuarios de grupo");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void CrearGrupo(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a crear grupo");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		String Datos=null;
		GruposManager gm=new GruposManager();
		try{
			Datos=request.getParameter("grupo");
			imx_grupo grupo=Json.getObject(Datos, imx_grupo.class);
			if(gm.existe(grupo.getNombreGrupo())==false){
				if(gm.insertarGrupo(grupo.getNombreGrupo(),grupo.getDescripcion())==true){
					json.add("success", true);
					msgRegreso="Grupo creado correctamente";
				} else {
					json.add("success", false);
					msgRegreso="Ocurrio un error al insertar el grupo";
				}
			} else {
				json.add("success", false);
				msgRegreso="El grupo ya existe.";
			}		
		} catch(Exception e) {
			log.debug("Entro al Catch de crear grupo");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	private void ActualizarGrupo(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a actualizar grupo");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		String Datos=null,Usuarios=null;
		
		GruposManager gm=new GruposManager();
		try{			
			Datos=request.getParameter("mods");
			Usuarios=request.getParameter("usuarios");
			imx_grupo grupo=Json.getObject(Datos, imx_grupo.class);
			if(gm.modGrupo(grupo.getNombreGrupo(), grupo.getDescripcion())==true){
				if(gm.modUsuariosGrupo(grupo.getNombreGrupo(),Usuarios)==true){
					json.add("success", true);
					msgRegreso="Grupo modificado correctamente.";
				} else {
					json.add("success", false);
					msgRegreso="Error al modificar los usuarios del grupo";
				}

			} else {
				json.add("success", false);
				msgRegreso="Error al modificar el grupo";
			}
		} catch(Exception e) {
			log.debug("Entro al Catch de actualizar grupo");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void EliminaGrupo(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a eliminar grupo");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";		
		GruposManager gm=new GruposManager();
		try{
			
			String grupo=request.getParameter("select");
			if(gm.eliminaGrupo(grupo)==true){
				json.add("success", true);
				msgRegreso="Grupo eliminado correctamente";
			}
			else{
				json.add("success", false);
				msgRegreso="Error al eliminar el grupo";
			}
			
		}
		catch(Exception e){
			log.debug("Entro al Catch de eliminar grupo");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerGruposPrivilegios(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener grupos para privilegios");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		ArrayList<Object> Grupos=new ArrayList<Object>();
		try{
			if(request.getParameter("select")==null){
			GruposManager gm=new GruposManager();
			Grupos=gm.ObtenerGruposP();
			json.add("nControl",Grupos);
			json.add("success",true);	
			}
		}
		catch(Exception e){
			log.debug("Entro al Catch de obtener grupos para privilegios");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerGavetasGrupo(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener gavetas especificas de grupo");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			GruposManager gm=new GruposManager();
			List<HashMap<String, String>> GavetasP = gm.getGavetasP(request.getParameter("select")); //mod
			json.add("gavetas", GavetasP);
			json.add("success",true);	
		}
		catch(Exception e){
			log.debug("Entro al Catch de Obtener gavetas especificas de grupo");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerPrivGrupos(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener  privilegios de grupo");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			GruposManager gm=new GruposManager();
			ArrayList<Object> Privilegios=new ArrayList<Object>();
			Privilegios=gm.getPrivilegiosG(request.getParameter("usuario"),request.getParameter("gaveta"));
			json.add("privilegios",Privilegios);
			json.add("success", true);
		}
		catch(Exception e){
			log.debug("Entro al Catch de Obtener privilegios de grupo");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerArbolPGrupos(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener arbol de privilegios de grupos");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
				GruposManager gm=new GruposManager();
				ArrayList<Object> Arbol=new ArrayList<Object>();
				Arbol=gm.getArbolPrivilegios(request.getParameter("select"));
				json.add("children", Arbol);
				json.add("success", true);
		}
		catch(Exception e){
			log.debug("Entro al Catch de arbol de privilegios de grupos");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void GuardarPrivGru(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a guardar privilegios de grupos");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			GruposManager gm=new GruposManager();
			String PrivilegiosJson=request.getParameter("Modificaciones");
			if(gm.ModPrivUsu(PrivilegiosJson)==true){
				json.add("success", true);
				msgRegreso="Privilegios asignados correctamente";
			}
			else{
				json.add("success", false);
				msgRegreso="Error al asignar privilegios";
			}
	
		}
		catch(Exception e){
			log.debug("Entro al Catch de guardar privilegios de grupos");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerDatosEstructura(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener datos de estructura");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		ArrayList<Object> Estructuras=new ArrayList<Object>();
		try{

			EstructuraManager em=new EstructuraManager();
			Estructuras=em.ObtenerEstructura(request.getParameter("select"));
			json.add("estructura",Estructuras);
			json.add("success",true);	
		}
		catch(Exception e){
			log.debug("Entro al Catch de obtener datos de estructura");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void CrearEstructuras(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a  crear estructuras");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		boolean result=false;
		try{
			
			EstructuraManager em=new EstructuraManager();
			if(em.Disponible(request.getParameter("estruc"))==true){
			result=em.crearEstructura(request.getParameter("estruc"));
			if(result==true){
				msgRegreso="Estructura creada correctamente";
				json.add("success",true);	
			}
			else{
				msgRegreso="Error al crear estructura";
				json.add("success",false);	
			}
			}
			else{
				msgRegreso="El nombre de la estructura ya se encuentra en uso";
				json.add("success",false);
			}
			
		}
		catch(Exception e){
			log.debug("Entro al Catch de obtener Estructuras");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerEstructuras(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener Estructuras");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			imx_catalogo_estructuras_manager imx_catalogo_estructuras_manager = new imx_catalogo_estructuras_manager();
	    	List<imx_catalogo_estructuras> imx_catalogo_estructuras = imx_catalogo_estructuras_manager.select(null, null, null, null).list();
	    	for(imx_catalogo_estructuras estructura: imx_catalogo_estructuras) {
	    		try {
	    			Json.getObject(estructura.getDefinicion(), NodoModel.class);
	    			estructura.setDefinicion("mvc");
	    		} catch (Exception e) {
	    			estructura.setDefinicion("old");
	    		}
	    	}
			json.add("estructuras",imx_catalogo_estructuras);
			json.add("success",true);	
		}
		catch(Exception e){
			log.debug("Entro al Catch de obtener Estructuras");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ObtenerArbolEstructura(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a Obtener arbol de estructura");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			EstructuraManager em=new EstructuraManager();
			Boolean buscaGaveta=request.getParameter("buscaGaveta")!=null?true:false;
			String select=request.getParameter("select");
			ArrayList<Object> Arbol=new ArrayList<Object>();
			if(!buscaGaveta){				
				Arbol=em.getArbolE(select);
				json.add("children", Arbol);
				json.add("success", true);
			}
			else{
				String estructura=em.gavetaTieneEstructura(select);
				if(!estructura.isEmpty()){
					Arbol=em.getArbolE(estructura);
					json.add("children", Arbol);
					json.add("success", true);
				}
				else{
					json.add("children", Arbol);
					json.add("success", true);
				}
			}
		}
		catch(Exception e){
			log.debug("Entro al Catch de arbol de privilegios...");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void ActualizaEstructura(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a actualizarestructuras");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		boolean result=false;
		try{
			
			EstructuraManager em=new EstructuraManager();
			result=em.actualizaEstructura(request.getParameter("select"),request.getParameter("estruc"));
			if(result==true){
				msgRegreso="Estructura actualizada correctamente";
				json.add("success",true);	
			}
			else{
				msgRegreso="Error al actualizar estructura";
				json.add("success",false);	
			}
			
		}
		catch(Exception e){
			log.debug("Entro al Catch de obtener Estructuras");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void EliminaEstructura(HttpServletRequest request, HttpServletResponse response){
         log.debug("Entro a eliminar estructura");
         Json json = new Json();
         json.add("success",false);
         String msgRegreso="";           
         EstructuraManager em=new EstructuraManager();
         try{
                 
                 String estructura=request.getParameter("select");
                 if(em.tieneGavetas(estructura)==true){
                 if(em.eliminaEstructura(estructura)==true){
                         json.add("success", true);
                         msgRegreso="Estructura eliminada correctamente";
                 }
                 else{
                         json.add("success", false);
                         msgRegreso="Error al eliminar la estructura";
                 }
                 }
                 else{
                         json.add("success", false);
                         msgRegreso="La estrucutura esta asignada a una o varias gavetas, desasigne las gavetas antes de eliminarla.";
                 }
                 
         }
         catch(Exception e){
                 log.debug("Entro al Catch de eliminar estructura");
                 log.error(e,e);
         }
         json.add("message", msgRegreso);
         json.returnJson(response);      
	}
	
	private void CrearActualizarEstructuraMVC(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a crear estructura de MVC");
		Json json = new Json();
	    json.add("success",false);
	    try {
	    	String nombre = request.getParameter("nombre");
	    	String descripcion = request.getParameter("descripcion");
	    	String definicion = request.getParameter("definicion");
	    	definicion = new Json().objectToJson(Json.getObject(definicion, NodoModel.class));
	    	
	    	imx_catalogo_estructuras_manager imx_catalogo_estructuras_manager = new imx_catalogo_estructuras_manager();
	    	imx_catalogo_estructuras actual = imx_catalogo_estructuras_manager.select(null, nombre, null, null).uniqueResult();
	    	imx_catalogo_estructuras imx_catalogo_estructuras = new imx_catalogo_estructuras();
	    	if(actual==null) {
	    		json.add("message", "Se guardo la estructura '"+nombre+"' exitosamente");
	    	} else {
	    		imx_catalogo_estructuras.setId(actual.getId());
	    		json.add("message", "Se actualizo la estructura '"+actual.getNombre()+"' exitosamente");
	    	}
	    	imx_catalogo_estructuras.setNombre(nombre);
	    	imx_catalogo_estructuras.setDescripcion(descripcion);
	    	imx_catalogo_estructuras.setDefinicion(definicion);
	    	imx_catalogo_estructuras_manager.saveOrUpdate(imx_catalogo_estructuras);
	    	json.add("success",true);
	    } catch (Exception e) {
	    	log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
	    }
	    json.returnJson(response);
	}
	
	private void getAtributosByNodo(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener atributos por nodo.");
		Json json = new Json();
		json.add("success",false);
		try {
			String nodo = request.getParameter("nodo");
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			//	    	definicion = new Json().objectToJson(Json.getObject(definicion, NodoModel.class));

			imx_documentos_atributos_manager imx_documentos_atributos_manager = new imx_documentos_atributos_manager();
			List<imx_documentos_atributos> imx_documentos_atributos = imx_documentos_atributos_manager.select(gdn).list();

			if(imx_documentos_atributos.isEmpty()){
				//Se obtienen atributos default
				//obtenemos nombre de estructura en base a nodo
				imx_estruc_doctos_manager imx_estruc_doctos_manager = new imx_estruc_doctos_manager();
				//El resultado debe ser único para las estructuras nuevas
				imx_estruc_doctos_manager.setMaxResults(1);
				imx_estruc_doctos imx_estruc_doctos  = imx_estruc_doctos_manager.select(gdn).list().get(0);
				String nombre_estructura = imx_estruc_doctos.getLNombreEstructura();
				//Obtenemos definicion de estructura
				imx_catalogo_estructuras_manager imx_catalogo_estructuras_manager = new imx_catalogo_estructuras_manager();
				imx_catalogo_estructuras imx_catalogo_estructuras = imx_catalogo_estructuras_manager.select(null, nombre_estructura, null, null).uniqueResult();
				NodoModel nodoModel = Json.getObject(imx_catalogo_estructuras.getDefinicion(), NodoModel.class);
				//Obtenemos documento buscado
				NodoModel documentoModel = findNodoModelByIdNodo(nodoModel, gdn);
				if(documentoModel == null){
					throw new Exception("El documento no tiene atributos asociados");
				}
				//Obtenemos atributos
				ArrayList<Object> atributos = new ArrayList<Object>();
				HashMap<String, Object> rec = new HashMap<String,Object>();

				Iterator<AtributosModel> it = documentoModel.atributos.iterator();
				while(it.hasNext()) {
					AtributosModel atributo = it.next();
					if(atributo.isActivo()){
						rec.clear();
						rec.put("id_atributo", atributo.getIdAtributo());
						rec.put("valor_atributo", atributo.getValorDefault());
						rec.put("modificable", atributo.isModificable());
						atributos.add(rec.clone());
					}
				}
				json.add("atributos", atributos);
				json.add("message", "Se obtuvieron atributos default correctamente.");
			} else {
				ArrayList<Object> atributos = new ArrayList<Object>();
				HashMap<String, Object> rec = new HashMap<String,Object>();

				for(imx_documentos_atributos atributo : imx_documentos_atributos){
					rec.clear();
					rec.put("id_atributo", atributo.getId().getIdAtributo());
					rec.put("valor_atributo", atributo.getValor());
					rec.put("modificable", true); //TODO HARDCODE Obtener de Definicion de imx_catalogo_atributos
					atributos.add(rec.clone());
				}
				json.add("atributos", atributos);
				json.add("message", "Se obtuvieron atributos particuales correctamente.");
			}
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	
	}
	
	private NodoModel findNodoModelByIdNodo(NodoModel nodoModel, GetDatosNodo gdn){
		//Obtener carpeta padre
		NodoModel padre = findNodoModelByIdCarpeta(nodoModel, gdn.getIdCarpeta().toString());
		
		//Obtener documento hijo
		if(padre==null)
			return null;
		
		Iterator<NodoModel> it = padre.children.iterator();
		while(it.hasNext()) {
			NodoModel nodo = it.next();
			if(nodo.type.equals("document") && nodo.id.equals("D"+gdn.getIdDocumento())){
				return nodo;
			}
		}
		return null;
	}
	
	private NodoModel findNodoModelByIdCarpeta(NodoModel nodoModel, String idCarpeta){
		NodoModel padre = null;
		if(nodoModel.type.equals("folder") && nodoModel.id.equals("C"+idCarpeta)){
			padre = nodoModel;
		} else {
			Iterator<NodoModel> it = nodoModel.children.iterator();
			while(it.hasNext()) {
				NodoModel nodo = it.next();
				if(nodo.type.equals("folder")){
					NodoModel hijo = findNodoModelByIdCarpeta(nodo, idCarpeta);
					if(hijo!=null){
						padre = hijo;
						break;
					}
				}
			}
		}
		return padre;
	}
	
	private void ExportarJson(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String nombreArchivo = request.getParameter("nombre_archivo");
    	String jsonExportar = request.getParameter("json");
		log.debug("Genera archivo Json "+nombreArchivo);
		response.setContentType("application/json");
		response.setHeader("Content-Disposition","attachment; filename=\""+nombreArchivo+".json"+"\"");
		try {
			IOUtils.write(jsonExportar, response.getOutputStream(), "UTF-8");
		} finally {
			IOUtils.closeQuietly(response.getOutputStream());
	    }
	}
	
	private void ImportarJsonEstructura(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		json.add("success",false);
		ArrayList<String> contenido;
		try {
			contenido = obtenerTextoDesdeArchivos(request);
			if(!contenido.isEmpty()){
				json.add("estructura", Json.getObject(contenido.get(0), NodoModel.class));
				json.add("message", "Se importó la estructura exitosamente");
				json.add("success", true);
			} else
				json.add("message", "Error al cargar contenido de archivo.");
			
		} catch (JsonSyntaxException e) {
			json.add("message", "El Json de la estructura no está bien formado. Revise sintaxis.");
		} catch (IOException e) {
			json.add("error", e.getCause());
			json.add("message", e.getMessage());
		}
		json.returnJson(response, "text/html; charset=utf-8");
	}
	
	private ArrayList<String> obtenerTextoDesdeArchivos(HttpServletRequest request) throws IOException{
		ArrayList<String> contenido = new ArrayList<String>();
		if (!ServletFileUpload.isMultipartContent(request)) {
			return contenido;
		}
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					if (!item.isFormField()) {
						contenido.add(item.getString());
					}
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
		return contenido;
	}
	
	private void ObtenerEstructuraMVC(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a crear estructura de MVC");
		Json json = new Json();
	    json.add("success",false);
	    try {
	    	String nombre = request.getParameter("nombre");
	    	
	    	imx_catalogo_estructuras_manager imx_catalogo_estructuras_manager = new imx_catalogo_estructuras_manager();
	    	imx_catalogo_estructuras imx_catalogo_estructuras = imx_catalogo_estructuras_manager.select(null, nombre, null, null).uniqueResult();
	    	if(imx_catalogo_estructuras==null) {
	    		json.add("message", "No existe la estructura '"+nombre+"'");
	    	} else {
	    		json.add("nombre", imx_catalogo_estructuras.getNombre());
	    		json.add("descripcion", imx_catalogo_estructuras.getDescripcion());
	    		json.add("definicion", Json.getObject(imx_catalogo_estructuras.getDefinicion(), NodoModel.class));
	    		json.add("message", "Se cargo la estructura '"+imx_catalogo_estructuras.getNombre()+"' exitosamente");
		    	json.add("success",true);
	    	}
	    } catch (Exception e) {
	    	log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
	    }
	    json.returnJson(response);
	}
	 
	private void ObtenerGavetasEst(HttpServletRequest request, HttpServletResponse response){
        log.debug("Entro a obtener gavetas de la estructura");
        Json json = new Json();
        json.add("success",false);
        String msgRegreso="";
        List<HashMap<String,String>> Gavetas=new ArrayList<HashMap<String,String>>();
        try{
                EstructuraManager em=new EstructuraManager();
                if("getGavD".equals(request.getParameter("action"))){
                Gavetas=em.ObtenerGavetas(request.getParameter("select"),true);
                json.add("gavetas",Gavetas);
                json.add("success",true);       
                }
                else{
                        Gavetas=em.ObtenerGavetas(request.getParameter("select"),false);
                        json.add("gavetas",Gavetas);
                        json.add("success",true);       
                }
        }
        catch(Exception e){
                log.debug("Entro al Catch de obtener datos de estructura");
                log.error(e,e);
        }
        json.add("message", msgRegreso);
        json.returnJson(response);      
	}
	
	private void AsignaGavetasE(HttpServletRequest request, HttpServletResponse response){
        log.debug("Entro a asignar gavetas a estructura");
        Json json = new Json();
        json.add("success",false);
        String msgRegreso="";
        try{
        		String jsonGavetas = request.getParameter("gav");
        		String nombre_estructura = request.getParameter("select");
        		
        		imx_catalogo_estructuras_manager imx_catalogo_estructuras_manager = new imx_catalogo_estructuras_manager();
    	    	imx_catalogo_estructuras imx_catalogo_estructuras = imx_catalogo_estructuras_manager.select(null, nombre_estructura, null, null).uniqueResult();
    	    	if(imx_catalogo_estructuras==null) {
    	    		msgRegreso = "No existe la estructura '"+nombre_estructura+"'";
    	    	} else {
    	    		NodoModel nodoModel = null;
    	    		try {
    	    			nodoModel = Json.getObject(imx_catalogo_estructuras.getDefinicion(), NodoModel.class);
    	    		} catch (Exception e) {
    	    			//TODO: Eliminar esta excepción y el try/catch cuando no queden estructuras viejas.
    	    			 EstructuraManager em=new EstructuraManager();
    	                 if(em.asignacion(jsonGavetas, nombre_estructura)==true){
    	                	 msgRegreso="Asignacion realizada correctamente.";
    	                	 json.add("success",true);       
    	                 } else {
    	                         msgRegreso="Error durante la asignacion de gavetas.";
    	                         json.add("success",false);      
    	                 }
    	    		}
    	    		if (nodoModel!=null) {
    	    			HibernateManager hm = new HibernateManager();
    	    			Query query = hm.createQuery("DELETE imx_estruc_doctos WHERE NOMBRE_ESTRUCTURA=:nombre_estructura");
    					query.setParameter("nombre_estructura", nombre_estructura);
    					hm.executeQuery(query);
    	    			for(imx_catalogo_estructuras estructura : Json.getObject(jsonGavetas, imx_catalogo_estructuras[].class)) {
    	    				String titulo_aplicacion = estructura.getNombre();
        	    			imx_estruc_doctos_id id = new imx_estruc_doctos_id(titulo_aplicacion, nombre_estructura, -1);
        	        		imx_estruc_doctos imx_estruc_doctos = new imx_estruc_doctos(id,nodoModel.text,1,-1,imx_catalogo_estructuras.getDescripcion(),nombre_estructura);
        	        		hm.saveOrUpdate(imx_estruc_doctos);
    	    			}
    	    			msgRegreso="Asignacion realizada correctamente.";
    	    			json.add("success",true);
    	    		}
    	    	}
        }
        catch(Exception e){
                log.debug("Entro al Catch de asignar gavetas a estructura");
                log.error(e,e);
        }
        json.add("message", msgRegreso);
        json.returnJson(response);      
	}
	
	private void Redirect(HttpServletRequest request, HttpServletResponse response) {
		String redirect_model = Json.getFromRequest(request, "redirect_model");
		RedirectModel redirectModel = Json.getObject(redirect_model, RedirectModel.class);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
		try {
			response.sendRedirect(basePath+redirectModel.url);
		} catch (IOException e) {
			log.error(e,e);
		}
	}
	
	private void ObtenerGavetasPDF(HttpServletRequest request, HttpServletResponse response) {
		Json jsonmanager = new Json();
		try {
				GavetaManager gm = new GavetaManager();
				HashMap<String, Object> record = new HashMap<String, Object>();
				ArrayList<Object> catalog_model= new ArrayList<Object>();
				Gaveta gaveta = null;
				ArrayList<Gaveta> gavetas = gm.listGavetas();

				for(int i=0;i<gavetas.size();i++) {
					gaveta = (Gaveta)gavetas.get(i);
					record.clear();
					record.put("titulo", gaveta.getNombre());
					record.put("descripcion",gaveta.getDescripcion());
					catalog_model.add(record.clone());
					jsonmanager.add("pdf", catalog_model);
				}

				jsonmanager.add("success",true); //Aunque no lo utilizamos manejamos esta metavariable de control.
				jsonmanager.add("total", catalog_model.size() ); //Mandamos el total
				jsonmanager.returnJson(response); //Aqui enviamos la estrucutura a "Gson" para que nos regrese el "string json" validado.
			} catch (Exception e) {
				//Se deben manejar metadatos de control
				log.error(e,e);
				jsonmanager.add("success",false);
				jsonmanager.add("message", "Error no controlado");
				jsonmanager.add("error", e.toString());
				jsonmanager.returnJson(response);
			}	
	}
	
	private void EditarConfigPDF(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = null;
		String configModelJson=null;

		try{
			//configModelJson = Json.getFromRequest(request,"configModel"); //tienes que recibir el stringJson del request
			configModelJson = request.getParameter("configModel");
			String nombre_gaveta = request.getParameter("select");
			@SuppressWarnings("unused")
			ConfigPDFModel configPDFModel = Json.getObject(configModelJson, ConfigPDFModel.class); //Verifica que serialize correctamente

			VariablesEntornoManager variablesEntornoManager=new VariablesEntornoManager();
			
			Json jsonConfigPDFEntorno = new Json();
			
			String nombreVariableEntorno = "fortimax.uploader.gaveta."+nombre_gaveta;
			int id = variablesEntornoManager.getID(nombreVariableEntorno);
			if (id<0)
				id = variablesEntornoManager.idDisponible();
			
			imx_config imx_config_PDF = new imx_config(id,nombreVariableEntorno,configModelJson,"Applet","Configuración para FortimaxUploader en la gaveta "+nombre_gaveta);
			imx_config[] imx_config_array = {imx_config_PDF}; 
			jsonConfigPDFEntorno.objectToJson(imx_config_array);
			variablesEntornoManager.ActualizaFilas(jsonConfigPDFEntorno.returnJson());
			
			json.add("success",true);

			message="La configuración para la gaveta "+nombre_gaveta+" se guardo exitosamente.";
		} catch(Exception e){
			log.error(e,e);
			message = "Hubo un problema al guardar la Configuración del Applet<br />"+e.toString();
			json.add("error", e.toString());
			try {
				json.add("jsonEntrada", Json.getMap(configModelJson));
			} catch (Exception ex) {
			}
		}
		json.add("message", message);
		json.returnJson(response);		
	}

	private void ObtenerConfigPDF(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = null;

		try{
			String nombre_gaveta = request.getParameter("select");
			String nombreVariableEntorno = "fortimax.uploader.gaveta."+nombre_gaveta;
			ConfigPDFModel configPDFModel = new ConfigPDFModel();
			
			String variableEntorno = VariablesEntornoManager.getValue(nombreVariableEntorno);
			if(variableEntorno!=null)
				configPDFModel = Json.getObject(variableEntorno, ConfigPDFModel.class); //Verifica que serialize correctamente
			
			json.add("config",configPDFModel);		
			json.add("success",true);
			json.add("message", "La configuración para la gaveta "+nombre_gaveta+" se guardo exitosamente.");
		} catch(Exception e){
			log.error(e,e);
			message = "Hubo un problema al obtener la Configuración del Applet<br />"+e.toString();
			json.add("error", e.toString());
		}
		json.add("message", message);
		json.returnJson(response);		
		
	}
	
	private void ObtenerConfigXML(HttpServletRequest request, HttpServletResponse response) {
		try{
			String gaveta = request.getParameter("select");
			String nombreVariableEntorno = "fortimax.uploader.gaveta."+gaveta;
			String configGaveta = VariablesEntornoManager.getValue(nombreVariableEntorno);		
			ConfigPDFModel configPDFModel = new ConfigPDFModel();
			if (configGaveta!=null)
				try {
					configPDFModel = Json.getObject(configGaveta, ConfigPDFModel.class); //Verifica que serialize correctamente
				} catch (Exception e) {
					log.error("Hubo un problema al tratar de cargar la configuración de la gaveta: "+gaveta);
					log.error(e,e);
				}
			XML xml = new XML(new XMLConfig().getConfig(configPDFModel));
			log.debug("XML de configuración generado:");
			log.debug(xml.toString());
			xml.printXML(response);
		} catch(Exception e){
			log.error(e,e);
		}		
	}

	private void crearPlantillaDocumento(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a  crear plantilla.");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{
			
			DocumentosManager dm=new DocumentosManager();
			String nombre = request.getParameter("nombre_doc");
			imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
			imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(nombre);
			if(tipoDocumento==null){
				String Descripcion=request.getParameter("descripcion_doc");
				String datosJson=request.getParameter("docmodel");
				if(dm.insertaDocumento(nombre, Descripcion, datosJson)){
					msgRegreso="Plantilla de documento creada correctamente";
					json.add("success",true);
				} else{
					msgRegreso="Ocurrio un error al intentar crear la plantilla de documento.";
					json.add("success",false);
				}
			
			} else{
				msgRegreso="El nombre de la plantilla ya se encuentra en uso";
				json.add("success",false);
			}
			
		}
		catch(Exception e){
			log.error("Entro al Catch de crear plantilla de documento");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void obtenerPlantillaDocumento(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener plantilla de documento: "+request.getParameter("select"));
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		ArrayList<Object> Lista=null;
		try{
			
			DocumentosManager dm=new DocumentosManager();
			json.add("success",false);
			Lista=dm.ObtenerDocumento(request.getParameter("select"));
			if(Lista!=null){
				json.add("success", true);
				json.add("Campos", Lista);
			}
			else{
				msgRegreso="Error al obtener plantilla de documento: "+request.getParameter("select").toString();
				json.add("success",false);
			}	
		}
		catch(Exception e){
			log.error("Entro al Catch de obtener plantilla de documento");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void editarPlantillaDocumento(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a  editar plantilla de documento");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		try{		
			DocumentosManager dm=new DocumentosManager();
			String nombre=request.getParameter("nombre_doc");
			imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
			imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(nombre);
			if(tipoDocumento!=null){
				
				String Descripcion=request.getParameter("descripcion_doc");
				String datosJson=request.getParameter("docmodel");
				if(dm.editaDocumento(nombre, Descripcion, datosJson)){
					msgRegreso="Plantilla de documento editado correctamente";
					json.add("success",true);
				}
				else{
					msgRegreso="Ocurrio un error al editar plantilla de documento";
					json.add("success",false);
				}
			
			}
			else{
				msgRegreso="El documento no existe";
				json.add("success",false);
			}		
		}
		catch(Exception e){
			log.error("Entro al Catch de editar plantilla de documento");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void obtenerPlantillas(HttpServletRequest request, HttpServletResponse response){
		log.debug("Entro a obtener lista de plantillas");
		Json json = new Json();
		json.add("success",false);
		String msgRegreso="";
		ArrayList<Object> plantillas=new ArrayList<Object>();
		try{

			DocumentosManager dm=new DocumentosManager();
			plantillas=dm.obtenerPlantillas();
			json.add("documentos",plantillas);
			json.add("success",true);	
		}
		catch(Exception e){
			log.error("Entro al Catch de obtener lista de plantillas");
			log.error(e,e);
		}
		json.add("message", msgRegreso);
		json.returnJson(response);	
	}
	
	private void eliminaPlantillaDocumento(HttpServletRequest request, HttpServletResponse response){
        log.debug("Entro a eliminar Plantilla de documento");
        Json json = new Json();
        json.add("success",false);
        String message="";           
        try{
        	imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
                String nombre=request.getParameter("select");
                imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(nombre);
                if(tipoDocumento!=null){
                	if(tipoDocumentoManager.deleteTipoDocumento(tipoDocumento)){
                        json.add("success", true);
                        message="Plantilla de documento eliminada correctamente";
                	} else{
                        message="Error al eliminar Plantilla de documento";
                	}
                } else{
                        message="No se encontrol la Plantilla de documento";
                }
                
        }
        catch(Exception e){
			log.error(e,e);
			json.add("error", e.toString());
			message = e.getMessage();
        }
        json.add("message", message);
        json.returnJson(response);      
	}
	
	private void ModificarPassword(HttpServletRequest request,HttpServletResponse response){
		response.setContentType("text/plain; charset=utf-8");
		log.debug("Modificar password");
		Json json=new Json();
		json.add("success", false);
		String msjRegreso="";
		try{
			imx_usuario[] u=Json.getObject(request.getParameter("datos"), imx_usuario[].class);
			com.syc.user.UsuarioManager Um=new com.syc.user.UsuarioManager();
			if(Um.verificarPassword(u[0].getNombreUsuario(),u[0].getCdg())){
				Boolean resultado=Um.cambiarPasswors(u[1].getNombreUsuario(), u[1].getCdg());
				if(resultado){
					msjRegreso="Contraseña cambiada correctamente, vuelva a iniciar session";
					json.add("success", true);
				}
				else{
					msjRegreso="Existio un error al cambiar la contraseña";
					json.add("success", false);
				}
			} else {
				msjRegreso="Tu contraseña es incorrecta";
				json.add("success", false);
			}
		}
		catch(Exception ex){
			log.error("Error al modificar password: "+ex.toString());
			msjRegreso="Ocurrio un error al cambiar la contraseña";
		}
		json.add("message", msjRegreso);
		json.returnJson(response);
	}
	
	@SuppressWarnings("unchecked")
	private void ObtenerVideos(HttpServletRequest request,HttpServletResponse response){
		log.debug("Obteniendo videos");
		Json json=new Json();
		json.add("success", false);
		Boolean administrador=false;
		try{
			administrador=Boolean.parseBoolean(request.getParameter("select"));
			HibernateManager hibernateManager=new HibernateManager();
			List<imx_videos> lista=null;
			List<imx_videos> listaS=null;
			if(!administrador){
				hibernateManager.setCriterion(Restrictions.eq("Administrador", "0"));
				lista=(List<imx_videos>)hibernateManager.list(imx_videos.class);
			}
			else{
				
			}
			ArrayList<String> listaString=new ArrayList<String>();
			for(int i=0;i<lista.size();i++){
				if(listaString.indexOf(lista.get(i).getSeccion())==-1){
					listaString.add(lista.get(i).getSeccion());
				}
			}
			ArrayList<Object> secciones=new ArrayList<Object>();
			for(int i=0;i<listaString.size();i++){
				hibernateManager.setCriterion(Restrictions.eq("Seccion", listaString.get(i)));
				listaS=(List<imx_videos>)hibernateManager.list(imx_videos.class);
				
				ArrayList<Object> videos=new ArrayList<Object>();
				HashMap<String, Object> rec=new HashMap<String,Object>();
				for(int x=0;x<listaS.size();x++){
					rec.clear();
					rec.put("Texto", listaS.get(x).getNombreVideo());
					rec.put("Descripcion", listaS.get(x).getDescripcion());
					rec.put("Ruta",listaS.get(x).getRuta());
					rec.put("Seccion", "0");
					rec.put("SeccionNombre", listaS.get(x).getSeccion());
					rec.put("cls", "videoCls");
					rec.put("iconCls", "videoIconCls");
					rec.put("leaf", true);
					videos.add(rec.clone());
				}
				
				rec.clear();
				rec.put("Texto", listaString.get(i));
				rec.put("Seccion", "1");
				rec.put("cls", "seccionCls");
				rec.put("children", new ArrayList<Object>(videos));
				secciones.add(rec.clone());
				videos.clear();
			}
			json.add("children", secciones);
			json.add("success", true);
		}
		catch(Exception e){
			log.error("Error el obtener videos: "+e.toString());
		}
		json.returnJson(response);
	}
	
// sin ref	
//	private void ObtenerZIP(HttpServletRequest request, HttpServletResponse response) {}
	
	private void ObtenerOCR(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try{
			String nodoDocumento = request.getParameter("select");
			int numero_pagina = Integer.parseInt(request.getParameter("pagina"));
			
			GetDatosNodo gdn = new GetDatosNodo(nodoDocumento);
			imx_pagina imx_pagina = new imx_pagina_manager().select(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento(),numero_pagina).uniqueResult();
			HashMap<String,String> ocr = new HashMap<String,String>();
			String texto="";
			if(imx_pagina!=null) {
				File fileOCR = new File(imx_pagina.getPathOCR());
				if(fileOCR.exists()) {
					texto = Utils.readFile(fileOCR,"UTF-8");
				}
			}
			ocr.put("texto", texto);
			json.add("ocr",ocr);
			json.add("success",true);
		} catch(Exception e){
			log.error(e,e);
			json.add("error", e.toString());
		}
		json.returnJson(response);		
	}
	
	static class OCR {
		int id;
		String texto;
	}
	
	private void EscribirOCR(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try{
			String nodoDocumento = request.getParameter("select");
			String ocrJson = request.getParameter("ocr");
			OCR ocr = Json.getObject(ocrJson, OCR.class);
			GetDatosNodo gdn = new GetDatosNodo(nodoDocumento);
			imx_pagina imx_pagina = new imx_pagina_manager().select(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento(),ocr.id).uniqueResult();
			FileUtils.writeStringToFile(new File(imx_pagina.getPathOCR()), ocr.texto, "UTF-8");
			json.add("message","El OCR se ha actualizado con éxito");
			json.add("success",true);
		} catch(Exception e){
			log.error(e,e);
			json.add("message","Hubo un error al actualizar el OCR: "+e.toString());
			json.add("error", e.toString());
		}
		json.returnJson(response);		
	}
	
	private void ObtenerMiniaturas(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try{
			String nodo = request.getParameter("select");
			int start = Integer.parseInt(request.getParameter("start"));
			int limit = Integer.parseInt(request.getParameter("limit"));
						
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			if (gdn.toString()==null)
				throw new Exception("Nodo inválido");
			imx_pagina_manager imx_pagina_manager = new imx_pagina_manager();
			imx_pagina_manager.select(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta(), gdn.getIdDocumento(), gdn.getPagina());
			imx_pagina_manager.setFirstResult(start);
			imx_pagina_manager.setMaxResults(limit);
			List<imx_pagina> imx_paginas = imx_pagina_manager.list();
		
			String path = request.getContextPath();
			String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
			String imagestorePath = basePath+"imagestore/";
			
			List<HashMap<String,Object>> miniaturas = new ArrayList<HashMap<String,Object>>();
			
			for(imx_pagina imx_pagina : imx_paginas) {	
				//Lo de las dimensiones realmente debería ir aca?
				ImageInfo imageInfo = null;
				try{
					imageInfo = Imaging.getImageInfo(new File(imx_pagina.getAbsolutePath()));
				} catch (Exception e) {
				}
				String paginaPath = imagestorePath+imx_pagina.getNomArchivoOrg()+"?select="+imx_pagina.getId();
				HashMap<String,Object> hashMap = new HashMap<String,Object>();
				hashMap.put("id", imx_pagina.getId().toString());
				hashMap.put("pagina", imx_pagina.getPagina());
				hashMap.put("numero_pagina", imx_pagina.getId().getNumeroPagina());
				
				//No me gusta hacer así la consulta de las dimensiones, deberia almacenarse en pagina al subirlo o pedirlo hasta que sea necesario.
				if(imageInfo!=null) {
					if(imageInfo.getNumberOfImages()>1) {
						log.debug(imageInfo.getNumberOfImages()+" imagenes en 1 solo archivo, pensar como reaccionar");
					}
					hashMap.put("ancho", imageInfo.getWidth());
					hashMap.put("alto", imageInfo.getHeight());
				} else {
					log.error("ImageIO no pudo leer la página "+imx_pagina.getId()+" ("+imx_pagina.getNomArchivoOrg()+")");
					log.debug("Archivo de la página en el volumen: "+imx_pagina.getAbsolutePath());
					hashMap.put("ancho", 0);
					hashMap.put("alto", 0);
				}
				hashMap.put("nombre", imx_pagina.getNomArchivoOrg());
				hashMap.put("calidad", 3);
				hashMap.put("imagen", imagestorePath+imx_pagina.getNomArchivoOrg()+".jpg?select="+imx_pagina.getId()+"&image.thumbnail=true");
				hashMap.put("imagenPagina", paginaPath);
				miniaturas.add(hashMap);
			}	
			json.add("miniaturas",miniaturas);
			json.add("success",true);
		} catch(Exception e){
			log.error(e,e);
			json.add("error", e.toString());
		}
		json.returnJson(response);		
	}
	
	static class PrintConfig {
		String idDocumento;
		boolean pdf;
		String calidad;
		String paginas;
		String formato;
		int orientacion;
	}
	
	private void ObtenerArchivosImpresion(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try{
			String select = request.getParameter("select");
			PrintConfig printConfig = Json.getObject(select, PrintConfig.class);
			String path = request.getContextPath();
			String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
			List<HashMap<String,Object>> archivos = new ArrayList<HashMap<String,Object>>();
			if(printConfig.pdf) {
				String pdfPath = basePath+"pdfstore/"+printConfig.idDocumento+".pdf?select="+printConfig.idDocumento+"&formato="+printConfig.formato+"&orientacion="+printConfig.orientacion;
				HashMap<String,Object> hashMap = new HashMap<String,Object>();
				hashMap.put("id", 1);
				hashMap.put("ruta", pdfPath);
				archivos.add(hashMap);
			} else {
				String imagestorePath = basePath+"imagestore/";
				imx_pagina_manager imx_pagina_manager = new imx_pagina_manager();
				imx_pagina_manager.select(printConfig.idDocumento);
				if(!"todas".equals(printConfig.paginas)){
					HashSet<Integer> paginasPorImprimir = Utils.rangeToHashSet(printConfig.paginas);
					imx_pagina_manager.getConjunction().add(Restrictions.in("Pagina", paginasPorImprimir));
				}
				int id = 1;
				for(imx_pagina imx_pagina : imx_pagina_manager.list()) {
					String paginaPath = imagestorePath+imx_pagina.getNomArchivoOrg()+"?select="+printConfig.idDocumento+"P"+imx_pagina.getId().getNumeroPagina();
					HashMap<String,Object> hashMap = new HashMap<String,Object>();
					hashMap.put("id", id++);
					hashMap.put("ruta", paginaPath);
					archivos.add(hashMap);
				}
			}
			json.add("archivos",archivos);
			json.add("success",true);
		} catch(Exception e){
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);		
	}
	private void ObtenerCatalogoPerfiles(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		try{
			PerfilesManager _pm = new PerfilesManager();
			String Nombre=request.getParameter("select")!=null?request.getParameter("select"):"";
			HashMap<String, Object> record = new HashMap<String, Object>();
			ArrayList<Object> catalog_model= new ArrayList<Object>();
			imx_perfiles_privilegios p = null;
			if(Nombre.isEmpty()){
				ArrayList<imx_perfiles_privilegios> perfiles=_pm.listPerfiles();
				for(int i=0;i<perfiles.size();i++){
					p = (imx_perfiles_privilegios)perfiles.get(i);
					record.clear();
					record.put("ID", i+1 );
					record.put("Nombre", p.getNombre());
					catalog_model.add(record.clone());
					json.add("catalog", catalog_model);
				}
				json.add("total", catalog_model.size() );
			}
			else{
				imx_perfiles_privilegios pe=_pm.getPerfil(Nombre, 0);
				json.add("perfil", pe);
			}

			json.returnJson(response); 
		} catch(Exception e){
			log.error(e,e);
			json.add("message", "Error no controlado");
			json.add("error", e.toString());
			json.add("success",false);
			json.returnJson(response);
		}
	}
	private void ObtenerPrivilegiosPerfiles(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		try{
			PerfilesManager _pm = new PerfilesManager();
			String perfil=request.getParameter("select")!=""?request.getParameter("select"):"";
			ArrayList<imx_catalogo_privilegios> lista=_pm.listPrivilegiosPerfiles(perfil);
			
			HashMap<String, Object> record = new HashMap<String, Object>();
			ArrayList<Object> privilegios= new ArrayList<Object>();
			imx_catalogo_privilegios p=null;
			if(perfil.isEmpty()){
				for(int i=0;i<lista.size();i++){
					p = (imx_catalogo_privilegios)lista.get(i);
					record.clear();
					record.put("id", i+1 );
					record.put("nombre", p.getNombre());
					record.put("valor", p.getValor());
					record.put("descripcion", p.getDescripcion());
					record.put("seleccionado", false);
					privilegios.add(record.clone());
				}
			}
			else{
				imx_perfiles_privilegios per=_pm.getPerfil(perfil, 0);
				for(int i=0;i<lista.size();i++){				
					p = (imx_catalogo_privilegios)lista.get(i);
					boolean s=(per.getValor()&p.getValor())==p.getValor()?true:false;
					record.clear();
					record.put("id", i+1 );
					record.put("nombre", p.getNombre());
					record.put("valor", p.getValor());
					record.put("descripcion", p.getDescripcion());
					record.put("seleccionado", s);
					privilegios.add(record.clone());
				}
			}
			json.add("privilegios", privilegios);
			json.returnJson(response);
			
		} catch(Exception e){
			log.error(e,e);
			json.add("message", "Error no controlado");
			json.add("error", e.toString());
			json.add("success",false);
			json.returnJson(response);
		}
	}
	private void CreaPerfil(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		String mensajeRegreso="";
		try{
			PerfilesManager _pm = new PerfilesManager();
			String p=request.getParameter("perfil");
			imx_perfiles_privilegios perfil=Json.getObject(p, imx_perfiles_privilegios.class);
			imx_perfiles_privilegios pe=_pm.getPerfil(perfil.getNombre(), 0);
			if(pe==null){
				imx_perfiles_privilegios per=_pm.getPerfil(null, perfil.getValor());
				if(per==null){
					if(_pm.creaPerfil(perfil)){
						mensajeRegreso="Perfil creado correctamente";
						json.add("success", true);
					}
					else{
						mensajeRegreso="Ocurrio un error al crear el perfil";
						json.add("success", false);
					}
				}
				else{
					mensajeRegreso="Ya existe un perfil con esos privilegios: "+per.getNombre();
					json.add("success", false);
				}
			}
			else{
				 mensajeRegreso="Ya existe un perfil con ese nombre";
				 json.add("success", false);
			}
			json.add("message", mensajeRegreso);
			json.returnJson(response);
			
		} catch(Exception e){
			log.error(e,e);
			json.add("message", "Error no controlado");
			json.add("error", e.toString());
			json.add("success",false);
			json.returnJson(response);
		}
	}
	private void ActualizaPerfil(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		String mensajeRegreso="";
		try{
			PerfilesManager _pm = new PerfilesManager();
			String p=request.getParameter("perfil");
			imx_perfiles_privilegios perfil=Json.getObject(p, imx_perfiles_privilegios.class);
			imx_perfiles_privilegios pe=_pm.getPerfil(perfil.getNombre(), 0);
			if(pe!=null){
				imx_perfiles_privilegios per=_pm.getPerfil(null, perfil.getValor());
				if(per==null||per.getNombre().equals(perfil.getNombre())){
					perfil.setId(pe.getId());
					if(_pm.actualizaPerfil(perfil)){
						mensajeRegreso="Perfil actualizado correctamente";
						json.add("success", true);
					}
					else{
						mensajeRegreso="Error al actualizar el perfil";
						json.add("success", false);
					}
				}
				else{
					mensajeRegreso="Ya existe un perfil con esos privilegios: "+per.getNombre();
					json.add("success", false);
				}
			}
			else{
				mensajeRegreso="Error, perfil no encontrado";
				json.add("success", false);
			}
			json.add("message", mensajeRegreso);
			json.returnJson(response);
			
		} catch(Exception e){
			log.error(e,e);
			json.add("message", "Ocurrio un error al actualizar el perfil");
			json.add("error", e.toString());
			json.add("success",false);
			json.returnJson(response);
		}
	}
	private void BorrarPerfil(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String mensajeRegreso="";
		String nombrePerfil = request.getParameter("select");
		try{
			PerfilesManager _pm = new PerfilesManager();
			imx_perfiles_privilegios pe=_pm.getPerfil(nombrePerfil, 0);
			if(pe!=null){
				if(_pm.perfilVacio(nombrePerfil)){
					if(_pm.borrarPerfil(pe)){
						mensajeRegreso="Perfil eliminado exitosamente";
						json.add("success",true);
					}
					else{
						mensajeRegreso = "Ocurrio un problema al borrar el perfil";
						json.add("success",false);
					}
				}
				else{
					mensajeRegreso = "Tienes que desasignar todos los usuarios y/o grupos del perfil";
					json.add("success",false);
				}
			}
			else{
				mensajeRegreso="Error, perfil no encontrado";
				json.add("success",false);
			}
		} catch(Exception e){
			log.error(e,e);
			mensajeRegreso = "Ocurrio un problema al borrar el perfil";
			json.add("error", e.toString());
		}
		json.add("message", mensajeRegreso);
		json.returnJson(response);
	}
	
	@SuppressWarnings("unused")
	private static class Atributo {
		Atributo(String atributo, String valor, Boolean activo, Boolean modificable, String descripcion) {
			this.atributo = atributo;
			this.valor = valor;
			this.activo = activo;
			this.modificable = modificable;
			this.descripcion = descripcion;
		}
		
		String atributo;
		String valor;
		Boolean activo;
		Boolean modificable;
		String descripcion;
	}
	
	@SuppressWarnings("unused")
	private void ObtenerAtributos(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String mensajeRegreso="";
		String nodo = request.getParameter("select");
		String page = request.getParameter("page");
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		try{
			List<Atributo> atributos = new ArrayList<Atributo>();
			atributos.add(new Atributo("Requerido","false",false,false,"Requerido"));
			atributos.add(new Atributo("Vigencia","10d",true,true,""));
			atributos.add(new Atributo("Vencimiento","11/02/2013",false,false,""));
			atributos.add(new Atributo("Historico","false",false,false,""));
			atributos.add(new Atributo("Existencia Fisica","DESCONOCIDA",true,true,"Descripcion de prueba"));
			// TODO Falta toda la implementacion para devolver atributos en base a un documento específico
			json.add("atributos",atributos);
			json.add("success",true);
		} catch(Exception e){
			log.error(e,e);
			mensajeRegreso = "Ocurrio un problema al obtener los atributos";
			json.add("error", e.toString());
		}
		json.add("message", mensajeRegreso);
		json.returnJson(response);
	}
	
	@SuppressWarnings("unused")
	private static class Historico {
		@SuppressWarnings("deprecation")
		Historico(imx_historico_documento historico_documento) {
			this.Id_Version = historico_documento.getId().getIdVersion();
			this.Fecha_generacion = historico_documento.getFechaGeneracion().toLocaleString();
			this.Nombre_Documento = historico_documento.getNombreDocumento();
			this.Descripcion = historico_documento.getDescripcion();
			this.numero_paginas = historico_documento.getNumeroPaginas();
			this.tamano = Math.round(historico_documento.getTamano()*1.0/1024)+"Kb";
			this.usuario_generador = historico_documento.getUsuarioGenerador();
		}
		
		public static List<Historico> getHistorico(List<imx_historico_documento> list) {
			List<Historico> historicos = new ArrayList<Historico>();
			for (imx_historico_documento historico_documento : list) {
				historicos.add(new Historico(historico_documento));
			}
			return historicos;
		}
		
		int Id_Version;
		String Fecha_generacion;
		String Nombre_Documento;
		String Descripcion;
		int numero_paginas;
		String tamano;
		String usuario_generador;
	}
	
	@SuppressWarnings("unused")
	private void ObtenerHistorico(HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String mensajeRegreso="";
		String nodo = request.getParameter("select");
		int page = 0;
		try {
			page = Integer.parseInt(request.getParameter("page"));
		} catch (Exception e) {		
		}
		int start = -1;
		try {
			start = Integer.parseInt(request.getParameter("start"));
		} catch (Exception e) {		
		}
		int limit = -1;
		try {
			limit = Integer.parseInt(request.getParameter("limit"));
		} catch (Exception e) {		
		}
		
		try{
			imx_documento imx_documento = new imx_documento_manager().select(nodo).uniqueResult();
			imx_historico_documento_manager historicoManager = new imx_historico_documento_manager();
			historicoManager.setFirstResult(start);
			historicoManager.setMaxResults(limit);
			historicoManager.select(new GetDatosNodo(imx_documento.getId().toString()));
			List<Historico> historico  = Historico.getHistorico(historicoManager.list());
			json.add("historico",historico);
			json.add("success",true);
		} catch(Exception e){
			log.error(e,e);
			mensajeRegreso = "Ocurrio un problema al obtener el historico";
			json.add("error", e.toString());
		}
		json.add("message", mensajeRegreso);
		json.returnJson(response);
	}
	
	static class datosPerfil{
		String Nombre="";
		String Tipo="";
		public String getNombre(){return this.Nombre;}
		public void setNombre(String Nombre){this.Nombre=Nombre;}
		
		public String getTipo(){return this.Tipo;}
		public void setTipo(String Tipo){this.Tipo=Tipo;}
	}
	public void ObtenerUsuairosGruposPerfil(HttpServletRequest request,HttpServletResponse response){
		Json json=new Json();
		Boolean resultado=false;
		String accion=request.getParameter("action");
		HashMap<String,Object> rec=new HashMap<String,Object>();
		ArrayList<Object> UG=new ArrayList<Object>();
		try{
			PerfilesManager _pm=new PerfilesManager();
			String perfil=request.getParameter("select");
			Boolean disponibles=accion.equals("getUsuariosDisponiblesPerfil")?true:false;
			List<String> usuarios=_pm.obtenerUsuariosPerfil(perfil, disponibles);
			List<String> grupos=_pm.obtenerGruposPerfil(perfil, disponibles);
			for(String u:usuarios){
				rec.clear();
				rec.put("Nombre", u);
				rec.put("Icono", "usuariosIconCls");
				rec.put("Tipo", "usuario");
				UG.add(rec.clone());
			}
			for(String u:grupos){
				rec.clear();
				rec.put("Nombre", u);
				rec.put("Icono", "gruposIconCls");
				rec.put("Tipo", "grupo");
				UG.add(rec.clone());
			}
			resultado=true;
			
		}
		catch(Exception e){
			resultado=false;
			log.error("Error obtener usuarios y grupos de perfil");
			log.error(e.toString());
		}
		json.add("usuarios", UG);
		json.add("success", resultado);
		json.returnJson(response);
	}
	public void AsignarUsuariosGavetasPerfil(HttpServletRequest request,HttpServletResponse response){
		Json json=new Json();
		Boolean resultado=false;
		String mensajeRegreso="";
		try{
			String perfil=request.getParameter("select");
			datosPerfil[] datos=Json.getObject(request.getParameter("datos"), datosPerfil[].class);
			PerfilesManager pm=new PerfilesManager();
			List<String> usuarios=new ArrayList<String>();
			List<String> grupos=new ArrayList<String>();
			for(datosPerfil d:datos)
				if(d.getTipo().equals("usuario"))
					usuarios.add(d.getNombre());
			for(datosPerfil d:datos)
				if(d.getTipo().equals("grupo"))
					grupos.add(d.getNombre());
			//Boolean usuariosEliminados=pm.eliminarPerfilUsuarios(usuarios, perfil);
			//Boolean gruposEliminados=pm.eliminarPerfilGrupos(grupos, perfil);
			
			Boolean eliminacionCorrecta=pm.eliminarPerfilGruposUsuarios(usuarios, grupos, perfil);
			if(eliminacionCorrecta){
				resultado=pm.asignaUsuariosGruposPerfil(usuarios, grupos, perfil);
				if(resultado)
					mensajeRegreso="Asignacion a perfil modificada correctamente";
				else
					mensajeRegreso="Ocurrio un error al asignar los usuario y/o grupos al perfil";
			}
			else
				mensajeRegreso="Ocurrio un error al asignar los usuario y/o grupos al perfil";
			
		}
		catch(Exception e){
			log.error("Error a asignar usuarios y/o gavetas a perfil");
			log.error(e.toString());
		}
		json.add("success", resultado);
		json.add("message", mensajeRegreso);
		json.returnJson(response);
	}
	

	/**
	 * Devuelve los elementos de configuración de correo.
	 */
	private void getMailElements(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		String message = null;
		boolean success = false;
		String contenido;

		try{
			String select = request.getParameter("select");

			if(select.equals("fortimax.correo.conexion")){
				contenido = VariablesEntornoManager.getValue(select);
				if(contenido != null){
					CorreoBasicoModel correoBasicoModel = Json.getObject(contenido, CorreoBasicoModel.class);
					json.add("config", correoBasicoModel);
					message = "La configuración para la conexión con el servidor de correo se obtuvo exitosamente.";
					success = true;
				} else {
					message = "No hay configuracion de correo para: " + select;
				}
			} else {
				select = "fortimax.correo.perfil." + select;
				contenido = VariablesEntornoManager.getValue(select);
				if(contenido != null){
					CorreoOpcionalModel correoOpcionalModel = Json.getObject(contenido, CorreoOpcionalModel.class);
					json.add("config", correoOpcionalModel);
					message = "La configuración para el perfil de correo se obtuvo exitosamente.";
					success = true;
				} else {
					message = "No hay configuracion de correo para: " + select;
				}
			}
			
		} catch(Exception e){
			log.error(e,e);
			message = "Hubo un problema al obtener la configuración del Correo";
			json.add("error", e.toString());
		}
		json.add("success", success);
		json.add("message", message);
		json.returnJson(response);
	}
	
	/**
	 * Devuelve los nombre de los perfiles de correo.
	 */
	private void getPerfilesCorreo(HttpServletRequest request, HttpServletResponse response){

		Json json = new Json();
		String message = null;
		boolean success = false;
		
		try{
			VariablesEntornoManager vem = new VariablesEntornoManager();
			HashMap<String,String> rec = new HashMap<String,String>();
			ArrayList<HashMap<String, Object>> variables = vem.obtenerVariables();
			ArrayList<Object> listaNombres = new ArrayList<Object>();
			String nombre = null;
			
			for(HashMap<String, Object> variable: variables){
				if(variable.get("name").toString().contains("fortimax.correo.perfil.")){
					nombre = variable.get("name").toString();
					nombre = nombre.substring(nombre.lastIndexOf('.') + 1 , nombre.length());

					rec.put("nombre", nombre );
					listaNombres.add(rec.clone());
					rec.clear();
				}
			}
			
			json.add("lista", listaNombres);
			message = "Lista de perfiles de correo cargada exitosamente.";
			success = true;

		} catch (Exception e){
			json.add("error", e.toString());
			log.error(e,e);
		}
		
		json.add("success", success);
		json.add("message", message);
		json.returnJson(response);
	}
	
	/**
	 * Recibe los nuevos valores de los elementos de correo y los actualiza en base de datos.
	 */
	private void editMailElements(HttpServletRequest request, HttpServletResponse response){
		
		String tipoVariables = request.getParameter("select");
		String model = request.getParameter("model");
		String name = request.getParameter("name");
		
		if(tipoVariables.equals("basico")){
			
		} else {
			name = "fortimax.correo.perfil." + name;
		}
			
		Json json = new Json();
		boolean success = false;
		String message = null;
		
		try {
			VariablesEntornoManager vem = new VariablesEntornoManager();
			if(vem.getID(name)>0){
				imx_config variableEntorno = new imx_config();
				variableEntorno.setID(vem.getID(name));
				variableEntorno.setName(name);
				variableEntorno.setValue(model);
				variableEntorno.setCategory("Mail");
				variableEntorno.setDescription("");

				vem.ActualizaFila(variableEntorno);
			} else
				vem.InsertarFila("Mail", name, model, "");
			
			log.info("Elementos de correo actualizados.");
			success = true;
			message = "Elementos de correo guardados exitosamente.";
		} catch (Exception e) {
			log.error(e, e);
			json.add("error", e.toString());
		}
		
		json.add("success", success);
		json.add("message", message);
		json.returnJson(response);
	}
	
	private void existeNodo(HttpServletRequest request, HttpServletResponse response) {
		String nodo_padre = request.getParameter("nodo_padre");
		String nombre_hijo = request.getParameter("nombre_hijo");
		
		Json json = new Json();
		json.add("success", false);
		try {
			boolean existe = false;
			String tipo = null;
			String encontrado = null;
			
			GetDatosNodo gdn = new GetDatosNodo(nodo_padre);
			gdn.separaDatosCarpeta();
			String tituloAplicacion = gdn.getGaveta();
			int idGabinete = gdn.getGabinete();
			int idCarpetaPadre = gdn.getIdCarpeta();
			
			imx_org_carpeta_manager imx_org_carpeta_manager = new imx_org_carpeta_manager();
			imx_org_carpeta_manager.select(tituloAplicacion, idGabinete, null, idCarpetaPadre, nombre_hijo);
			List<imx_org_carpeta> imx_org_carpetas = imx_org_carpeta_manager.list();
			if(!imx_org_carpetas.isEmpty()) {
				existe = true;
				tipo = "carpeta";
				encontrado = imx_org_carpetas.get(0).getNombreHija();
			}
			
			imx_documento_manager imx_documento_manager = new imx_documento_manager();
			imx_documento_manager.select(tituloAplicacion, idGabinete, idCarpetaPadre, null).selectNombreDocumento(nombre_hijo);
			List<imx_documento> imx_documentos = imx_documento_manager.list();
			if(!imx_documentos.isEmpty()) {
				existe = true;
				tipo = "documento";
				encontrado = imx_documentos.get(0).getNombreDocumento();
			}
			
			json.add("existe", existe);
			json.add("tipo", tipo);
			json.add("encontrado", encontrado);
			json.add("success", true);
		} catch (Exception e) {
			json.add("message", e.toString());
		}
		json.returnJson(response);
	}
	
	private void existBD(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success", false);
		try {
			json.add("exist", HibernateUtils.existBD());
			json.add("success", true);
		} catch (Exception e) {
			json.add("message", e.toString());
		}
		json.returnJson(response);
	}
	
	private void validateBD(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success", false);
		try {
			boolean valid = HibernateUtils.validateBD();
			List<String> exceptions = new ArrayList<String>();
			String message = "";
			if(!valid) {
				for (Exception e : HibernateUtils.getValidationException()) {
					exceptions.add(e.toString());
					message+= e.getMessage()+"<br />";
				}
			}
			json.add("exceptions", exceptions);
			json.add("message", message);
			json.add("success", valid);
		} catch (Exception e) {
			json.add("message", e.toString());
		}
		json.returnJson(response);
	}
	
	private void test(HttpServletRequest request, HttpServletResponse response) {
		String select = request.getParameter("select");
		Json json=new Json();
		try {
			int indexError = Integer.parseInt(select);
			
			List<String> queries = new ArrayList<String>();
			for(int i = 0; i<=30; i++) {
				queries.add(""+i);
			}
			List<String> queriesAntesDelError = queries.subList(0, indexError);
			json.add("q0", queriesAntesDelError.toArray());
			//executeQueries(hm,table,queriesAntesDelError); //Ejecutar los queries antes del error.
			queriesAntesDelError.clear(); //Eliminar los queries antes del error de 'queries'.
			json.add("q1", queries.get(0));
			queries.remove(0); //Eliminar el query con error de 'queries'.
			json.add("q2", queries.toArray());
			//executeQueries(hm,table,queries); //Ejecutar los queries posteriores al error.
			json.add("success", true);
			json.returnJson(response);
			
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("success", false);
			json.returnJson(response);
		}	
	}
	
	private static class UsuarioLocal {
		String nombre;
		String password;
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		
	}
}