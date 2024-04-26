package com.syc.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.catalogos.CatalogosManager;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_carpeta;
import com.syc.fortimax.hibernate.entities.imx_catalogo;
import com.syc.fortimax.hibernate.entities.imx_catalogo_tipo_documento;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.fortimax.hibernate.entities.imx_tipos_documentos_index;
import com.syc.fortimax.hibernate.entities.imx_tipos_documentos_index_id;
import com.syc.fortimax.hibernate.managers.imx_carpeta_manager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_manager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_tipo_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.fortimax.hibernate.managers.imx_tipos_documentos_index_manager;
import com.syc.fortimax.managers.GruposManager;
import com.syc.fortimax.managers.PrivilegioManager;
import com.syc.gavetas.Gaveta;
import com.syc.gavetas.GavetaManager;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.servlets.models.GavetaCampoModel;
import com.syc.servlets.models.NodoModel;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.Encripta;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;
import com.syc.utils.Utils;

/**
 * Servlet implementation class TipoDocumentoServlet
 */
public class PlantillaDocumentoServlet extends HttpServlet implements ParametersInterface {
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(PlantillaDocumentoServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlantillaDocumentoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request, response);
	}
	
	public void doWork(HttpServletRequest request, HttpServletResponse response) {
		String action = request.getParameter("action");
		log.debug(" Ejecutando acción: " + action);
		if ("getTDocs".equals(action))//TODO Renombrar a getPlantillasDocumento
			obtenerPlantillas(request, response);
		else if ("getTDoc".equals(action))//TODO Renombrar a getCamposPlantilla
			obtenerCamposByIDPlantilla(request, response);
		else if ("getDatosDoc".equals(action))
			obtenerDatosPlantillaByNodo(request, response);
		else if ("createTDoc".equals(action))
			crearTipoDocumento(request, response);
		else if ("getListas".equals(action))
			obtenerElementosLista(request, response);
		else if ("getGavetasBusqueda".equals(action))
			obtenerGavetasBusqueda(request, response);
		else if ("getTiposDocumentos".equals(action))
			obtenerTiposDocumentosBusqueda(request, response);
		else if ("busquedaDocumentos".equals(action))
			obtenerRedireccionBusqueda(request, response);
		else if ("getDGeneral".equals(action))
			obtenerDatosDocumento(request, response);
		else if ("getInfoDocumento".equals(action)) //TODO: se llama desde ventana. Hay que homogeneizar
			obtenerInfoDocumento(request, response);
		else if ("getInfoCarpeta".equals(action)) //TODO: se llama desde ventana. Hay que homogeneizar
			obtenerInfoCarpeta(request, response);
		else if ("editInfoDocumento".equals(action)) //TODO: se llama desde ventana. Hay que homogeneizar
			modificaDocumento(request, response);
		else if ("editInfoCarpeta".equals(action)) //TODO: se llama desde ventana. Hay que homogeneizar
			modificaCarpeta(request, response);
		else if("getDatosDocumento".equals(action))
			obtenerDatosDocumentoJson(request,response);
		else if ("saveDocument".equals(action))
			crearDocumento(request, response);
		else if ("EditDocument".equals(action))
			editarDocumento(request, response);
		else if ("saveFolder".equals(action))
			crearCarpeta(request, response);
		else if ("getReport".equals(action))
			obtenerReporte(request, response);
		else
			accionInvalida(request,response);
	}
	
	
	/**
	 * Modifica el nombre de un documento o carpeta.
	 * @param request
	 * @param response
	 */
	
	public void modificaDocumento(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		json.add("success",false);
		String mensaje = "";
		boolean exito = false;
		HibernateManager hm = new HibernateManager();
		try {
			String select = request.getParameter("select");
			DocumentoConPlantilla doc = Json.getObject(select, DocumentoConPlantilla.class);
			String nodo = request.getParameter("nodo");
			String titulo_aplicacion = null;
			int id_gabinete = -1;
			int id_carpeta = -1;
			int id_documento = -1;

			GetDatosNodo gdn = new GetDatosNodo(nodo);
			gdn.separaDatosDocumento();
			if(gdn.isDocumento()){
				id_documento = gdn.getIdDocumento();
			} else {
				log.error("No se trata de un nodo de documento.");
				throw new Exception("No se trata de un nodo de documento.");
			}
							
			titulo_aplicacion = gdn.getGaveta();
			id_gabinete = gdn.getGabinete();
			id_carpeta = gdn.getIdCarpeta();

			Query query = hm.createSQLQuery("UPDATE imx_documento SET nombre_documento = '"
					+ doc.getNombre()
					+"', DESCRIPCION='" + doc.getDescripcion()
					+"', ID_TIPO_DOCTO='" + doc.getTipoDocumento()
					+ "' WHERE titulo_aplicacion='"
					+ titulo_aplicacion
					+ "' "
					+ " AND id_gabinete="
					+ id_gabinete
					+ " AND id_carpeta_padre="
					+ id_carpeta
					+ " AND id_documento="
					+ id_documento);

			hm.executeQuery(query);
			
			imx_documento_manager docManager = new imx_documento_manager();
			docManager.select(titulo_aplicacion, id_gabinete, id_carpeta, id_documento);
			imx_documento imx_doc = docManager.uniqueResult();
			Documento d = new Documento(imx_doc);
			
			try {
				ArrayList<imx_tipos_documentos_index> campos = generarCamposDocumento(gdn.getNodo(), doc);
				imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager();
				indexManager.save(campos);
			} catch (Exception e) {
				log.error("No pudo establecerse la plantilla al documento.");
				throw e;
			}
			
			mensaje = "Documento editado exitosamente.";
			exito = true;
			json.add("nodo", new NodoModel(d));
			
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			mensaje = e.toString();
		} finally {
			hm.close();
			json.add("success", exito);
			json.add("message", mensaje);
			json.returnJson(response);
		}
		
	}
	
	public void modificaCarpeta(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		json.add("success",false);
		String mensaje = "Carpeta editado exitosamente.";
		boolean exito = true;
		HibernateManager hm = new HibernateManager();
		try {
			String select = request.getParameter("select");
			String nodo = request.getParameter("nodo");
			CarpetaJson carpetaJson = Json.getObject(select, CarpetaJson.class);
			String nodo_padre = null;
			String titulo_aplicacion = null;
			int id_gabinete = -1;
			int id_carpeta = -1;
			int id_carpeta_padre = -1;

			GetDatosNodo gdn = new GetDatosNodo(nodo);
			gdn.separaDatosCarpeta();
			if(gdn.isCarpeta()){
				id_carpeta = gdn.getIdCarpeta();
				nodo_padre = request.getParameter("nodo_padre");
				GetDatosNodo gdn_padre = new GetDatosNodo(nodo_padre);
				gdn_padre.separaDatosCarpeta();
				if(gdn_padre.isCarpeta()){
					id_carpeta_padre = gdn_padre.getIdCarpeta();
				}
			} else {
				log.error("No se trata de un nodo de carpeta.");
				throw new Exception("No se trata de un nodo de carpeta.");
			}
							
			titulo_aplicacion = gdn.getGaveta();
			id_gabinete = gdn.getGabinete();
			id_carpeta = gdn.getIdCarpeta();

			String pass = "";
			if(carpetaJson.isDeletePassword()){
				pass = "', PASSWORD='-1";
			} else if(carpetaJson.isUsingPassword()){
				pass = "', PASSWORD='" + Encripta.code32(carpetaJson.getPassword());
			} else if(carpetaJson.isChangePassword()){
				imx_carpeta_manager icm = new imx_carpeta_manager();
				icm.select(titulo_aplicacion, id_gabinete, id_carpeta, null);
				imx_carpeta ic = icm.uniqueResult();

				String oldPass = Encripta.code32(carpetaJson.getPasswordold());
				if(oldPass.equals(ic.getPassword())){
					pass = "', PASSWORD='" + Encripta.code32(carpetaJson.getPassword());
				} else{
					exito = false;
					mensaje = "Contraseña no modificada. Introduzca la actual correctamente.";
				}
			}
			
			List<Query> queries = new ArrayList<Query>();
			
			Query query = hm.createSQLQuery(
					"UPDATE imx_carpeta SET nombre_carpeta = '"
							+ carpetaJson.getNombre()
							+"', DESCRIPCION='" + carpetaJson.getDescripcion()
							+ pass
							+ "' WHERE titulo_aplicacion='"
							+ titulo_aplicacion
							+ "' "
							+ " AND id_gabinete="
							+ id_gabinete
							+ " AND id_carpeta="
							+ id_carpeta);

			queries.add(query);
			query = hm.createSQLQuery(
					"UPDATE imx_org_carpeta SET nombre_hija = '"
							+ carpetaJson.getNombre()
							+ "' WHERE titulo_aplicacion='"
							+ titulo_aplicacion
							+ "' "
							+ " AND id_gabinete="
							+ id_gabinete
							+ " AND id_carpeta_hija="
							+ id_carpeta
							+ " AND id_carpeta_padre="
							+ id_carpeta_padre);
//			if(!trans.wasCommitted())
//			trans.commit();
			queries.add(query);
			hm.executeQueries(queries);
			
			Carpeta c = new Carpeta(titulo_aplicacion, id_gabinete, id_carpeta);
			CarpetaManager cm = new CarpetaManager();
			c = cm.selectCarpeta(c); //TODO Usar imx_carpeta_manager
			
			json.add("nodo", new NodoModel(c));
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			mensaje = e.toString();
		} finally {
			hm.close();
			json.add("success", exito);
			json.add("message", mensaje);
			json.returnJson(response);
		}
	}
	
	/**
	 * Este método devuelve los datos de un documento de acuerdo al modelo Documento en MVC a diferencia del método obtenerDatosDocumento
	 * que devuelve los datos para el Tab de Detalles y usa un modelo incorrecto con campos nombre/valor.
	 * @param request
	 * @param response
	 */
	private void obtenerInfoDocumento(HttpServletRequest request, HttpServletResponse response){		
		Json json = new Json();
		json.add("success",false);	
		String mensaje = "";
		try {		
			String select = request.getParameter("select");
			GetDatosNodo gdn = new GetDatosNodo(select);
			imx_documento imx_documento = new imx_documento_manager().select(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta(), gdn.getIdDocumento()).uniqueResult();
			
//			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(documento);
//			List<imx_tipos_documentos_index> camposDocumento = indexManager.list();
			
//			String plantilla="-Ninguna-";
//			if(camposDocumento.size()>0) {
//				int idTipoDocumento = camposDocumento.get(0).getId().getIdTipoDocumento();
			
//				imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
//				imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(idTipoDocumento);
//				plantilla = tipoDocumento.getNombre();
//			}
			
			HashMap<String, Object> record = new HashMap<String, Object>();
//			ArrayList<Object> catalog_model= new ArrayList<Object>();

			record.clear();
			record.put("nodo", gdn.getNodo());
			record.put("nombre", imx_documento.getNombreDocumento());
			record.put("descripcion", imx_documento.getDescripcion());
			record.put("idTipoDocumento", imx_documento.getIdTipoDocumento());
			
			imx_documento_id idDocumento = new imx_documento_id(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento());
			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(idDocumento);
			List<imx_tipos_documentos_index> camposDocumento = indexManager.list();
			
			int idPlantilla = -1;
			if(camposDocumento.size()>0){
				idPlantilla = camposDocumento.get(0).getId().getIdTipoDocumento();
			}
			record.put("idPlantilla", idPlantilla);
			
//			record.put("plantilla", plantilla);
//				catalog_model.add(record.clone());
			json.add("documento", record.clone());

			json.add("success",true);
			mensaje = "Datos de Documentos enviados exitosamente.";
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			mensaje = e.getMessage();
		}
		json.add("message", mensaje);
		json.returnJson(response);
	}

	private void obtenerInfoCarpeta(HttpServletRequest request, HttpServletResponse response) {		
		Json json = new Json();
		json.add("success",false);	
		try {		
			String nodo = request.getParameter("select");
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			Carpeta carpeta = null;
			
			if(gdn.isCarpeta()){
				carpeta = (Carpeta)gdn.getObject();
			} else {
				throw new Exception("No es un nodo de Carpeta.");
			}
			
			HashMap<String, Object> record = new HashMap<String, Object>();
			String password = null;
			if(carpeta.isProtected())
				password = carpeta.getPassword();
			
			record.clear();
			record.put("nombre", carpeta.getNombreCarpeta());
			record.put("descripcion", carpeta.getDescripcion());
			record.put("protegida", carpeta.isProtected());
			record.put("password", password);
			json.add("carpeta", record.clone());

			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.add("message", "Datos de Carpeta enviados exitosamente.");
		json.returnJson(response);
	}
	
	private void obtenerDatosDocumento(HttpServletRequest request, HttpServletResponse response) {		
		Json json = new Json();
		json.add("success",false);	
		try {		
			String select = request.getParameter("select");
			GetDatosNodo gdn = new GetDatosNodo(select);
			gdn.separaDatosDocumento();
			imx_documento imx_documento = new imx_documento_manager().select(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta(), gdn.getIdDocumento()).uniqueResult();
			
			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(imx_documento.getId());
			List<imx_tipos_documentos_index> camposDocumento = indexManager.list();
			
			String plantilla="-Ninguna-";
			if(camposDocumento.size()>0) {
				int idTipoDocumento = camposDocumento.get(0).getId().getIdTipoDocumento();
			
				imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
				imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(idTipoDocumento);
				plantilla = tipoDocumento.getNombre();
			}
			
			Datos datos = new Datos();
			datos.add("Nombre",imx_documento.getNombreDocumento());
			datos.add("Nodo", imx_documento.getId().toString());
			//datos.add("Gaveta", imx_documento.getId().getTituloAplicacion().equals("USR_GRALES")?"Mis documentos":imx_documento.getId().getTituloAplicacion());
			datos.add("Descripcion", imx_documento.getDescripcion());
			datos.add("Numero de paginas", imx_documento.getNumeroPaginas());
			datos.add("Tamaño (KB)", (int)Math.round(imx_documento.getTamanoBytes()/1024));
			datos.add("Plantilla", plantilla);
			datos.add("Creado", imx_documento.getFhCreacion());
			datos.add("Modificado", imx_documento.getFhModificacion());
			if("S".equals(imx_documento.getCompartir())){
				Date fecha=new Date();
					if(fecha.before(imx_documento.getFechaExpira())){
						String path = request.getContextPath();
						String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
						datos.add("Compartir", basePath+"s/"+imx_documento.getTokenCompartir());
						//datos.add("token", documento.getTokenCompartir());
						//datos.add("nombre_tipo_docto", documento.getNombreTipoDocto());
						//datos.add("id_gabinete", documento.getIdGabinete());
						//datos.add("Carpeta", imx_documento.getNombreCarpeta());
						//datos.add("id_documento", documento.getIdDocumento());
						datos.add("Fecha expiración", imx_documento.getFechaExpira());
					}
					else{
						datos.add("Compartir", "No compartido");
					}
			}
			else{
				datos.add("Compartir", "No compartido");
			}
			json.add("datos", datos.getList());
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}
	private void obtenerDatosDocumentoJson(HttpServletRequest request, HttpServletResponse response) {		
		Json json = new Json();
		json.add("success",false);	
		try {		
			String select = request.getParameter("select");
			imx_documento imx_documento = new imx_documento_manager().select(select).uniqueResult();
			
			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(imx_documento.getId());
			List<imx_tipos_documentos_index> camposDocumento = indexManager.list();
			
			String plantilla="-Ninguna-";
			if(camposDocumento.size()>0) {
				int idTipoDocumento = camposDocumento.get(0).getId().getIdTipoDocumento();
			
				imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
				imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(idTipoDocumento);
				plantilla = tipoDocumento.getNombre();
			}
			ArrayList<Object> d=new ArrayList<Object>();
			HashMap<String, Object> rec=new HashMap<String,Object>();
			rec.put("Nombre",imx_documento.getNombreDocumento());
			rec.put("Descripcion", imx_documento.getDescripcion());
			rec.put("Nodo", imx_documento.getId().toString());
			rec.put("Numero de paginas", imx_documento.getNumeroPaginas());
			rec.put("Tamaño (KB)", (int)Math.round(imx_documento.getTamanoBytes()/1024));
			rec.put("Plantilla", plantilla);
			rec.put("Creado", imx_documento.getFhCreacion());
			rec.put("Modificado", imx_documento.getFhModificacion());
			if("S".equals(imx_documento.getCompartir())){
				Date fecha=new Date();
					if(fecha.before(imx_documento.getFechaCompartido())){
						String path = request.getContextPath();
						String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
						rec.put("Compartir", basePath+"s/"+imx_documento.getTokenCompartir());
						rec.put("Expiración", imx_documento.getFechaExpira());
					}
					else{
						rec.put("Compartir", "No compartido");
					}
			}
			else{
				rec.put("Compartir", "No compartido");
			}
			d.add(rec.clone());
			json.add("datos", d);
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}
	static class Datos {

		List<HashMap<String,Object>> datos= new ArrayList<HashMap<String,Object>>();
		
		public void add(String nombre, Object valor) {
			HashMap<String,Object> dato = new HashMap<String,Object>();
			dato.put("nombre", nombre);
			dato.put("valor", valor);
			datos.add(dato);	
		}

		public List<HashMap<String,Object>> getList() {
			return datos;
		}
		
		
	}
	

	//TODO: Todo el codigo de listas no tiene sentido en este servlet, mover a uno propio o por lo menos uno con mas sentido.

	private void obtenerElementosLista(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {
			String lista = request.getParameter("select");
			if(lista.startsWith("_fmx")) {
				json.add("lista",Utils.getFMXList(lista));
				json.add("success",true);
			} else {
				imx_catalogo imx_catalogo = new imx_catalogo_manager().select(lista).uniqueResult(); 		
				if(imx_catalogo==null) {
					throw new Exception("El Catalogo no Existe");
				} else {
					ArrayList<Map<String, Object>> datos = new CatalogosManager().getDatos(imx_catalogo, null); 
					for(int i=0; i < datos.size(); i++) {
						datos.get(i).put("id", datos.get(i).get("nombre"));
					}
					json.add("lista",datos);
					json.add("success",true);
				}
			}
		} catch (Exception e) {
			log.error(e,e);
			json.add("error","Hubo un error al tratar de generar la lista de catalogos: "+e);
		}
		json.returnJson(response);	
	}
	
	private void obtenerGavetasBusqueda(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {
			HttpSession session = request.getSession(false);
			Usuario usuario = (Usuario) session.getAttribute(USER_KEY);
			
			HashMap<String, String> gavetaDescripcion = new HashMap<String,String>(); 
			List<String> gruposAsignados = new GruposManager().getGruposAsignados(usuario.getNombreUsuario());
			for(String nombreGrupo : gruposAsignados) {
				List<HashMap<String, String>> gavetas = new GruposManager().getGavetasP(nombreGrupo);
				for(HashMap<String, String> gaveta : gavetas) {
					gavetaDescripcion.put(gaveta.get("gaveta"),null);
				}
			}
			
			for(Object objectGavetas : PrivilegioManager.getGavetasP(usuario.getNombreUsuario()) ) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> gavetasMap = (HashMap<String, String>)objectGavetas;
				gavetaDescripcion.put(gavetasMap.get("gaveta"),null);
			}
			
			List<HashMap<String,String>> gavetas = new ArrayList<HashMap<String,String>>();
			for(String titulo_aplicacion : gavetaDescripcion.keySet()) {
				Gaveta gaveta = new GavetaManager().selectGaveta(titulo_aplicacion);
				HashMap<String, String> rec = new HashMap<String,String>();
				rec.put("Nombre", gaveta.getNombre());
				rec.put("Descripcion", gaveta.getDescripcion());
				gavetas.add(rec);
			}
			
			json.add("gavetas",gavetas);
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error","Hubo un error al tratar de obtener la lista de gavetas para búsqueda: "+e);
		}
		json.returnJson(response);	
	}
	
	private void obtenerTiposDocumentosBusqueda(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {
			List<HashMap<String,Object>> documentos = new ArrayList<HashMap<String,Object>>();
			
			List<Integer> ids = new imx_tipos_documentos_index_manager().getTiposDocumentosID();
			for(int id : ids) {
				imx_catalogo_tipo_documento tipoDocumento = new imx_catalogo_tipo_documento_manager().getTipoDocumento(id);
				HashMap<String, Object> rec = new HashMap<String,Object>();
				rec.put("Id", tipoDocumento.getId());
				rec.put("Nombre", tipoDocumento.getNombre());
				rec.put("Descripcion", tipoDocumento.getDescripcion());
				documentos.add(rec);
			}		
			json.add("documentos",documentos);
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error","Hubo un error al tratar de obtener la lista de tipos de documentos para búsqueda: "+e);
		}
		json.returnJson(response);	
	}
	
	static class BusquedaGeneral {
		String Carpeta;
		String Nombre;
	    String Descripcion;
		String CreacionInicio;
		String CreacionFin;
		String ModificacionInicio;
		String ModificacionFin;
		String VencimientoInicio;
		String VencimientoFin;
	}
	
	static class BusquedaContenido {
		String Carpeta;
		String Contenido;
	}
	
	public static class DatosPlantilla {
		public String nombre;
		public String valor;
	}
	
	static class BusquedaPlantilla {
		String Carpeta;
		int Plantilla;
		String DatosPlantilla;		
	}
	
	private void obtenerRedireccionBusqueda(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {
			String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
			
			String select = request.getParameter("select");
			String datos = request.getParameter("datos");
			
			String redirectMainWindow = null;
			if ("general".equals(select)) {
				BusquedaGeneral busquedaGeneral = Json.getObject(datos, BusquedaGeneral.class);
				redirectMainWindow = basePath+"jsp/ResultadosBusquedaDocumento.jsp"
										+"?nombre="+URLEncoder.encode(busquedaGeneral.Nombre, "UTF-8")
										+"&descripcion="+URLEncoder.encode(busquedaGeneral.Descripcion, "UTF-8")
										+"&gaveta="+URLEncoder.encode(busquedaGeneral.Carpeta, "UTF-8");
				try{
					if(!"".equals(busquedaGeneral.CreacionInicio))
						redirectMainWindow+="&fc1="+Utils.getDateExtJSString(busquedaGeneral.CreacionInicio,"dd/MM/yyyy");
				} catch (ParseException e) {
					log.trace(e);
				}
				try{
					if(!"".equals(busquedaGeneral.CreacionFin))
						redirectMainWindow+="&fc2="+Utils.getDateExtJSString(busquedaGeneral.CreacionFin,"dd/MM/yyyy");
				} catch (ParseException e) {
					log.trace(e);
				}
				try{
					if(!"".equals(busquedaGeneral.ModificacionInicio))
						redirectMainWindow+="&fm1="+Utils.getDateExtJSString(busquedaGeneral.ModificacionInicio,"dd/MM/yyyy");
				} catch (ParseException e) {
					log.trace(e);
				}
				try{
					if(!"".equals(busquedaGeneral.ModificacionFin))
						redirectMainWindow+="&fm2="+Utils.getDateExtJSString(busquedaGeneral.ModificacionFin,"dd/MM/yyyy");
				} catch (ParseException e) {
					log.trace(e);
				}
			}
			else if ("contenido".equals(select)) {
				BusquedaContenido busquedaContenido = Json.getObject(datos, BusquedaContenido.class);
				redirectMainWindow = basePath+"jsp/ResultadosBusquedaLucene.jsp";
				redirectMainWindow+= "?palabra_clave="+URLEncoder.encode(busquedaContenido.Contenido, "UTF-8");
				redirectMainWindow+= "&gaveta="+URLEncoder.encode(busquedaContenido.Carpeta, "UTF-8");
			}
			else if ("plantilla".equals(select)) {
				BusquedaPlantilla busquedaPlantilla = Json.getObject(datos, BusquedaPlantilla.class);
				redirectMainWindow = basePath+"jsp/ResultadosBusquedaTipoDocumento.jsp?";
				redirectMainWindow += "gaveta="+URLEncoder.encode(busquedaPlantilla.Carpeta, "UTF-8");
				redirectMainWindow += "&plantilla="+busquedaPlantilla.Plantilla;
				//DatosPlantilla[] datosPlantilla = Json.getObject(busquedaPlantilla.DatosPlantilla, DatosPlantilla[].class);
				redirectMainWindow += "&datos="+URLEncoder.encode(busquedaPlantilla.DatosPlantilla, "UTF-8");;
			}
			
			log.debug("redirectMainWindow="+redirectMainWindow);
			json.add("redirectSuperiorWindow",null);
			json.add("redirectLeftWindow",null);
			json.add("redirectMainWindow",redirectMainWindow);
			json.add("message","Operación realizada con éxito");
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error","Hubo un error al tratar de obtener las redirecciones para búsqueda: "+e);
		}
		json.returnJson(response);	
	}

	private void crearTipoDocumento(HttpServletRequest request, HttpServletResponse response) {
		// TODO: Obtener y Serializar el Json Correspondiente a esta pantalla.
		
		Json json = new Json();
		json.add("success",false);	
		try {		
			imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
			int Id=-1;
			String Nombre = null;
			String Descripcion = null;
			String EstructuraFormulario = null;
			tipoDocumentoManager.createTipoDocumento(new imx_catalogo_tipo_documento(Id, Nombre, Descripcion, EstructuraFormulario));
			
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}

	private void obtenerCamposByIDPlantilla(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		try {
			String select = request.getParameter("select");
			GavetaCampoModel[] campos = new GavetaCampoModel[0]; 
			if (select!=null&&!"".equals(select)) {
				int id = Integer.parseInt(select);
			
				imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
				imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(id);
			
				campos = Json.getObject(tipoDocumento.getEstructuraFormulario(), GavetaCampoModel[].class);
			}
			json.add("campos", campos);
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}
	
	private void obtenerDatosPlantillaByNodo(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		try {
			String select = request.getParameter("select");
			GetDatosNodo gdn = new GetDatosNodo(select);
			gdn.separaDatosDocumento();
			
			imx_documento_id idDocumento = new imx_documento_id(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento());
			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(idDocumento);
			List<imx_tipos_documentos_index> camposDocumento = indexManager.list();
			
			if (camposDocumento.size()==0) {
				String noExistePlantilla = "El documento no tiene una plantilla definida"; 
				json.add("error", noExistePlantilla);
				json.add("message", noExistePlantilla);
			} else {
				int idTipoDocumento = camposDocumento.get(0).getId().getIdTipoDocumento();
				json.add("campos", getCamposModel(idTipoDocumento,camposDocumento));
				json.add("success",true);
			}
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}

	private GavetaCampoModel[] getCamposModel(int idTipoDocumento, List<imx_tipos_documentos_index> camposDocumento) {
		HashMap<String,String> valorCampos = new HashMap<String,String>();
		
		Iterator<imx_tipos_documentos_index> iteratorCampos = camposDocumento.iterator();
		while (iteratorCampos.hasNext()) {
			imx_tipos_documentos_index campo = iteratorCampos.next();
			if(campo.getId().getIdTipoDocumento()==idTipoDocumento){
				valorCampos.put(campo.getId().getNombreCampo(), campo.getValorCampo());
			}
		}
		
		imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
		imx_catalogo_tipo_documento tipoDocumento = tipoDocumentoManager.getTipoDocumento(idTipoDocumento);
		GavetaCampoModel[] campos = Json.getObject(tipoDocumento.getEstructuraFormulario(), GavetaCampoModel[].class);
		
		for (int i=0; i<campos.length; i++) {
			if("-Ninguna-".equals(campos[i].getLista()))
				campos[i].setLista("");
			String valor = valorCampos.get(campos[i].getNombre());
			valor = (valor!=null)?valor:"";
			campos[i].setValor(valor);
		}
		
		return campos;
	}

	private void obtenerPlantillas(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		try {
			imx_catalogo_tipo_documento_manager tipoDocumentoManager = new imx_catalogo_tipo_documento_manager();
			ArrayList<imx_catalogo_tipo_documento> tiposDocumento = tipoDocumentoManager.getTiposDocumento();
			Iterator<imx_catalogo_tipo_documento> tiposDocumentoIterator = tiposDocumento.iterator();
		
			ArrayList<HashMap<String, Object>> tiposDocumentos = new ArrayList<HashMap<String,Object>>();
			
			while(tiposDocumentoIterator.hasNext()){
				imx_catalogo_tipo_documento tipoDocumento = tiposDocumentoIterator.next();
				HashMap<String, Object> tipoDocumentoHashMap = new HashMap<String,Object>();
				tipoDocumentoHashMap.put("Id", tipoDocumento.getId());
				tipoDocumentoHashMap.put("Nombre", tipoDocumento.getNombre());
				tipoDocumentoHashMap.put("Descripcion", tipoDocumento.getDescripcion());
				tiposDocumentos.add(tipoDocumentoHashMap);
			}
			json.add("documentos", tiposDocumentos);
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}
	

	
	private void crearCarpeta(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		try {
			HttpSession session = null;
			session = request.getSession(false);

			crearCarpetaDesdeFortimax(request, response);

		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.toString());
			json.returnJson(response);
		}
	}
	
	private void crearCarpetaDesdeFortimax(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		json.add("success",false);	
		try {
			String select = request.getParameter("select");
			CarpetaJson carpetaJson = Json.getObject(select, CarpetaJson.class);
			
			HttpSession session = request.getSession(false);
			Usuario u = (Usuario) session.getAttribute(USER_KEY);
			ITree tree = (ITree) session.getAttribute(TREE_KEY);
			ITreeNode treeNode = tree.findNode(carpetaJson.getNodo());
			
			if (!treeNode.getType().startsWith("carpeta"))
				treeNode = treeNode.getParent();
						
			Carpeta c = (Carpeta) treeNode.getObject();
			
			ArbolManager amd = new ArbolManager(c.getTituloAplicacion(), c.getIdGabinete());
			boolean isMyDocs = "USR_GRALES".equals(c.getTituloAplicacion());

			String password = "-1";
			if (carpetaJson.isUsingPassword())
				password = carpetaJson.getPassword();

			c.setNombreCarpeta(carpetaJson.getNombre());
			c.setDescripcion(carpetaJson.getDescripcion());
			c.setBanderaRaiz("N");
			c.setPassword(password);

			Carpeta cRoot = (Carpeta) tree.getRoot().getObject();
			
			CarpetaManager cm = new CarpetaManager(cRoot);
			cm.insertCarpeta(c);
			log.info("Creacion del Carpeta: "+ c.getNombreCarpeta()+ " en la Gaveta : "+c.getTituloAplicacion()+" por el usuario: "+c.getNombreUsuario() );
			
			String idNode = ArbolManager.generaIdNodeCarpeta(c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta());
			actualizaArbol(session, u.getNombreUsuario(), tree.getExpandedNodes(), treeNode, idNode, amd, isMyDocs);
			
			String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
			
			String redirectSuperiorWindow = urlPrefix+"/jsp/TituloArriba.jsp?select="+idNode;
			String redirectLeftWindow = urlPrefix+"/jsp/ArbolExpediente.jsp?select="+idNode+"&"+TREE_TYPE_KEY+"="+(isMyDocs?"d":"e")+"#"+idNode+"\";";
			String redirectMainWindow = urlPrefix+"/jsp/Bienvenida.jsp";
			
			json.add("redirectSuperiorWindow", redirectSuperiorWindow);
			json.add("redirectLeftWindow", redirectLeftWindow);
			json.add("redirectMainWindow", redirectMainWindow);
			json.add("message", "Carpeta Creada Exitosamente");
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	
	}
	
	public void actualizaArbol(HttpSession session,String nombre_usuario,Set<?> expandedNodes,ITreeNode treeNode,String idNode,ArbolManager amd,boolean isMyDocs) {
			ITree tree = amd.generaExpediente(nombre_usuario);

			if (tree != null) {
				Iterator<?> i = expandedNodes.iterator();
				while (i.hasNext()) {
					ITreeNode tn = (ITreeNode) i.next();
					tree.expand(tn.getId());
				}

				tree.expand(treeNode.getId());

				session.setAttribute((isMyDocs ? TREE_MDC_KEY : TREE_EXP_KEY), tree);
			}
		}
	
	
	
	private void crearDocumento(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		try {
			HttpSession session = null;
			session = request.getSession(false);
			
			if(session.getAttribute("FMX_MVC")==null)
				crearDocumentoDesdeFortimax(request, response);
			else
				crearDocumentoDesdeMVC(request, response);
				
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.toString());
			json.returnJson(response);
		}
	}

	private void crearDocumentoDesdeMVC(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		String mensaje = "";
		boolean exito = false;
		try {		
			String datos = request.getParameter("select");
			String nombre_usuario = request.getParameter("usuario");
			String nodo = request.getParameter("nodo");
			UsuarioManager um = new UsuarioManager();

			if(!um.existeUsuario(nombre_usuario))
				throw new Exception("Se intenta crear documento con usuario inexistente.");

			DocumentoConPlantilla doc = Json.getObject(datos, DocumentoConPlantilla.class);

			GetDatosNodo gdn = new GetDatosNodo(nodo);
			if(gdn.isDocumento()){
				gdn.eliminarDocumento();
			}

//			if(!gdn.isCarpeta())
//				throw new Exception("Nodo incorrecto. No es posible crear sobre él un documento");

			Documento d = new Documento(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta());

			d.setNombreDocumento(doc.getNombre());
			d.setNombreUsuario(nombre_usuario);
			d.setNombreTipoDocto("IMX_SIN_TIPO_DOCUMENTO");
			d.setIdTipoDocto(doc.getTipoDocumento());
			d.setMateria("ORIGINAL");
			d.setClaseDocumento(0);
			d.setDescripcion(doc.getDescripcion());

			DocumentoManager dm = new DocumentoManager();
			dm.insertDocumento(d);
			int idNewDoc = dm.getLastIdInserted();
			d.setIdDocumento(idNewDoc);
			gdn.setDocumento(idNewDoc);
			String nuevoNodo = gdn.getNodo();
			
			try {
				ArrayList<imx_tipos_documentos_index> campos = generarCamposDocumento(nuevoNodo,doc);
				imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager();
				indexManager.save(campos);
			} catch (Exception e) {
				dm.deleteDocumento(d);
				throw e;
			}

			json.add("nodo", new NodoModel(d));
			mensaje = "Documento Creado Exitosamente";
			exito = true;
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			mensaje = e.getMessage();
		} finally{
			json.add("success", exito);
			json.add("message", mensaje);
			json.returnJson(response);
		}

	}
	
	private void crearDocumentoDesdeFortimax(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		try {		
			String select = request.getParameter("select");
			DocumentoConPlantilla doc = Json.getObject(select, DocumentoConPlantilla.class);
			
			HttpSession session = request.getSession(false);
			Usuario u = (Usuario) session.getAttribute(USER_KEY);
			ITree tree = (ITree) session.getAttribute(TREE_KEY);
			ITreeNode treeNode = tree.findNode(doc.getNodo());
			
			if (!treeNode.getType().startsWith("carpeta"))
				treeNode = treeNode.getParent();
						
			Carpeta c = (Carpeta) treeNode.getObject();
			
			ArbolManager amd = new ArbolManager(c.getTituloAplicacion(), c.getIdGabinete());
			boolean isMyDocs = "USR_GRALES".equals(c.getTituloAplicacion());

			Documento d = new Documento(c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta());

			d.setNombreDocumento(doc.getNombre());
			d.setNombreUsuario(u.getNombreUsuario());
			d.setNombreTipoDocto("IMX_SIN_TIPO_DOCUMENTO");
			d.setIdTipoDocto(Documento.SIN_TIPO);
			d.setMateria("ORIGINAL");
			d.setClaseDocumento(0);
			d.setDescripcion(doc.getDescripcion());
			
			DocumentoManager dm = new DocumentoManager();
			dm.insertDocumento(d);
			
			try {
				ArrayList<imx_tipos_documentos_index> campos = generarCamposDocumento(d.getNodo(),doc);
				imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager();
				indexManager.save(campos);
			} catch (Exception e) {
				dm.deleteDocumento(d);
				throw e;
			}

			String idNode = ArbolManager.generaIdNodeDocumento(d.getTituloAplicacion(),d.getIdGabinete(),d.getIdCarpetaPadre(),d.getIdDocumento());
			
			actualizaArbol(session, u.getNombreUsuario(), tree.getExpandedNodes(), treeNode, idNode, amd, isMyDocs);			
			String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
			
			String redirectLeftWindow = urlPrefix+"/jsp/ArbolExpediente.jsp?select="+idNode+"&"+TREE_TYPE_KEY+"="+(isMyDocs?"d":"e")+"#"+idNode+"\";";
			String redirectMainWindow = urlPrefix+"/jsp/PreGuardaDocto.jsp?select="+idNode+"&nuevo=false&editable=false";
			
			json.add("redirectLeftWindow", redirectLeftWindow);
			json.add("redirectMainWindow", redirectMainWindow);
			json.add("message", "Documento Creado Exitosamente");
			json.add("success",true);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.getMessage());
		}
		json.returnJson(response);
	}
	
	private void editarDocumento(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);	
		String mensaje = "";
		boolean exito = false;
		try {
			String select = request.getParameter("select");
			
			imx_documento imx_documento = new imx_documento_manager().select(select).uniqueResult();
			
			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(imx_documento.getId());
			List<imx_tipos_documentos_index> camposDocumento = indexManager.list();
			
			if (camposDocumento.size()==0) {
				throw new Exception("El documento no tiene una plantilla definida");
			}
			
			int idTipoDocumento = camposDocumento.get(0).getId().getIdTipoDocumento();
			
			DocumentoConPlantilla doc = new DocumentoConPlantilla();
			doc.setNodo(select);
			String plantilla_documento = request.getParameter("plantilla_documento");
			Map<String, Object> mapaPlantilla = Json.getMap(plantilla_documento);
//			List<CamposPlantilla> camposPlantilla = new ArrayList<CamposPlantilla>(); //TODO NO usar CamposPlantilla
			HashMap<String, String> nuevoMapa = new HashMap<String, String>();
			for(String nombre : mapaPlantilla.keySet()) {
//				String valor = (String) mapaPlantilla.get(nombre);
				nuevoMapa.put(nombre, (String) mapaPlantilla.get(nombre));
//				CamposPlantilla campoPlantilla = new CamposPlantilla();
//				campoPlantilla.setNombre(nombre);
//				campoPlantilla.setValor(valor);
//				camposPlantilla.add(campoPlantilla);
			}
//			Json jsonPlantillaD = new Json();
//			jsonPlantillaD.objectToJson(camposPlantilla);
			doc.setCamposPlantilla(nuevoMapa);//TODO: jsonPlantillaD.returnJson());
			doc.setIdPlantilla(idTipoDocumento);
			ArrayList<imx_tipos_documentos_index> campos = generarCamposDocumento(imx_documento.getId().toString(),doc);
			indexManager.saveOrUpdate(campos);
			
			mensaje = "Los datos del documento se editaron exitosamente";
			exito = true;
		} catch (Exception e) {
			exito = false;
			log.error(e,e);
			json.add("error", e.toString());
			mensaje = e.getMessage();
		} finally {
			json.add("success", exito);
			json.add("message", mensaje);
			json.returnJson(response);
		}
	}

	private ArrayList<imx_tipos_documentos_index> generarCamposDocumento(String nodo, DocumentoConPlantilla dp) {
		ArrayList<imx_tipos_documentos_index> campos = new ArrayList<imx_tipos_documentos_index>();
		if(dp.getIdPlantilla()>=0) {
			
//			HashMap<String, String> mapCampos = Json.getObject(dp.getCamposPlantilla(), HashMap.class);
			HashMap<String, String> mapCampos = dp.getCamposPlantilla();
			
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			
			for (String key: mapCampos.keySet()) {
				String nombreCampo = key;
				String valorCampo = mapCampos.get(key);

				imx_tipos_documentos_index_id campoId = new imx_tipos_documentos_index_id();
				campoId.setTituloAplicacion(gdn.getGaveta());
				campoId.setIdGabinete(gdn.getGabinete());
				campoId.setIdCarpetaPadre(gdn.getIdCarpeta());
				campoId.setIdDocumento(gdn.getIdDocumento());
				campoId.setIdTipoDocumento(dp.getIdPlantilla());
				campoId.setNombreCampo(nombreCampo);
				imx_tipos_documentos_index campo = new imx_tipos_documentos_index(campoId,valorCampo);
				campos.add(campo);
			}
		}
		return campos;
	}
	
	private void obtenerReporte(HttpServletRequest request, HttpServletResponse response) {
		String select = request.getParameter("select");
		Json json = new Json();
		json.add("success",false);
		try {
			json.add("reporteDocumentos", URLEncoder.encode(select,"ISO-8859-1"));
			json.add("success",true);
		} catch (UnsupportedEncodingException e) {
			json.add("message",e.toString());
		}
		json.returnJson(response);
	}

	private void accionInvalida(HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = "El action ingresado no es válido";
		json.add("message", message);
		json.returnJson(response);
	}

	@SuppressWarnings("unused")
	private static class CarpetaJson {
		private String nodo; //En MVC el nodo se recibe del request
		private String nombre;
		private String descripcion;
		private boolean usePassword;
		private boolean changePassword;
		private boolean deletePassword;
		private String password;
		private String passwordold;
		
		String getNodo() {
			return nodo;
		}
		void setNodo(String nodo) {
			this.nodo = nodo;
		}
		
		String getNombre() {
			return nombre;
		}
		void setNombre(String nombre) {
			this.nombre = nombre;
		}
		String getDescripcion() {
			return descripcion;
		}
		void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}
		boolean isUsingPassword() {
			return usePassword;
		}
		void setUsePassword(boolean usePassword) {
			this.usePassword = usePassword;
		}
		boolean isChangePassword() {
			return changePassword;
		}
		void setChangePassword(boolean changePassword) {
			this.changePassword = changePassword;
		}
		boolean isDeletePassword() {
			return deletePassword;
		}
		void setDeletePassword(boolean deletePassword) {
			this.deletePassword = deletePassword;
		}
		String getPassword() {
			return password;
		}
		void setPassword(String password) {
			this.password = password;
		}

		public String getPasswordold() {
			return passwordold;
		}
	}
	
	@SuppressWarnings("unused")
	private static class DocumentoConPlantilla {
		private String nodo;//En MVC el nodo se recibe del request
		private String nombre;
		private int tipoDocumento = -1;
		private String descripcion;
		private int idPlantilla = -1;
		private HashMap<String,String> camposPlantilla;
		private boolean usePassword;
		private String password;
		
		String getNodo() {
			return nodo;
		}
		void setNodo(String nodo) {
			this.nodo = nodo;
		}
		
		String getNombre() {
			return nombre;
		}
		void setNombre(String nombre) {
			this.nombre = nombre;
		}
		int getTipoDocumento() {
			return tipoDocumento;
		}
		void setTipoDocumento(int tipoDocumento) {
			this.tipoDocumento = tipoDocumento;
		}
		String getDescripcion() {
			return descripcion;
		}
		void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}
		int getIdPlantilla() {
			return idPlantilla;
		}
		void setIdPlantilla(int idPlantilla) {
			this.idPlantilla = idPlantilla;
		}
		HashMap<String, String> getCamposPlantilla() {
			return camposPlantilla;
		}
		void setCamposPlantilla(HashMap<String, String> camposPlantilla) {
			this.camposPlantilla = camposPlantilla;
		}
		boolean usePassword() {
			return usePassword;
		}
		void setUsePassword(boolean usePassword) {
			this.usePassword = usePassword;
		}
		String getPassword() {
			return password;
		}
		void setPassword(String password) {
			this.password = password;
		}
		
	}
}
