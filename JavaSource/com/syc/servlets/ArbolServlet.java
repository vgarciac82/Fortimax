package com.syc.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.managers.PrivilegioManager;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.servlets.models.NodoModel;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;
import com.syc.utils.UsedStorageCapacity;

public class ArbolServlet extends HttpServlet implements ParametersInterface {
	private static final long serialVersionUID = 1L;
    private static final Logger log=Logger.getLogger(ArbolServlet.class);   
    
    public ArbolServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request,response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request,response);
	}
	protected void doWork(HttpServletRequest request, HttpServletResponse response){
		String action=request.getParameter("action");
		log.debug("Ejecutando accion: "+action);
		if("getGavetas".equals(action))
			obtenerGavetas(request,response);
		else if("getExpediente".equals(action))
			obtenerExpediente(request, response);
		else if("getMisDocumentos".equals(action))
			obtenerMisDocumentos(request, response);
		else if("getDatosNodo".equals(action))
			obtenerDatosNodo(request, response);
		else if("getTableExpedientes".equals(action))
			obtenerTablaExpedientes(request, response);
		else if("salir".equals(action))
			salida(request, response);
		else if("getStorage".equals(action))
			obtenerStorage(request, response);
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

	protected class Item {
		String name;
		String value;
		
		public Item(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	public void obtenerDatosNodo(HttpServletRequest request,HttpServletResponse response){
		try {
			String nodo = request.getParameter("select");
			log.debug("Obteniendo datos del nodo "+nodo);
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			Object hibernateObject = gdn.getHibernateObject();
			
			List<Item> items = new ArrayList<Item>();
			if(hibernateObject instanceof imx_aplicacion) {
				imx_aplicacion imx_aplicacion = (imx_aplicacion)hibernateObject;
				items.add(new Item("Nodo",nodo));
				items.add(new Item("Gaveta",gdn.getGaveta()));
				items.add(new Item("Nombre",imx_aplicacion.getDescripcion()));
				items.add(new Item("Tabla",imx_aplicacion.getTblAplicacion()));
			} else if(hibernateObject instanceof imx_documento) {
				imx_documento imx_documento =  (imx_documento)hibernateObject;
				items.add(new Item("Nodo",nodo));
				items.add(new Item("Gaveta",gdn.getGaveta()));
				items.add(new Item("ID Expediente",""+gdn.getGabinete()));
				items.add(new Item("ID Carpeta Padre",""+gdn.getIdCarpeta()));
				items.add(new Item("ID Documento",""+gdn.getIdDocumento()));
				items.add(new Item("Nombre",imx_documento.getNombreDocumento()));
				items.add(new Item("Usuario",imx_documento.getNombreUsuario()));
				items.add(new Item("Prioridad",""+imx_documento.getPrioridad()));
				items.add(new Item("Fecha Creacion",""+imx_documento.getFhCreacion()));
				items.add(new Item("Fecha Modificacion",""+imx_documento.getFhModificacion()));
				items.add(new Item("No. Accesos",""+imx_documento.getNumeroAccesos()));
				items.add(new Item("No. Paginas",""+imx_documento.getNumeroPaginas()));
				items.add(new Item("Titulo",imx_documento.getTitulo()));
				items.add(new Item("Autor",imx_documento.getAutor()));
				items.add(new Item("Materia",imx_documento.getMateria()));
				items.add(new Item("Descripcion",imx_documento.getDescripcion()));
				items.add(new Item("claseDocumento",""+imx_documento.getClaseDocumento()));
				items.add(new Item("Estado",""+imx_documento.getEstadoDocumento()));
				items.add(new Item("Tamaño",""+imx_documento.getTamanoBytes()));
				items.add(new Item("Compartido",""+imx_documento.getCompartir()));
				items.add(new Item("Token compartido",imx_documento.getTokenCompartir()));	
			} else if(hibernateObject instanceof Carpeta) {
				Carpeta carpeta =  (Carpeta)hibernateObject;
				items.add(new Item("Nodo",nodo));
				items.add(new Item("Gaveta",gdn.getGaveta()));
				items.add(new Item("ID Expediente",""+gdn.getGabinete()));
				items.add(new Item("ID Carpeta",""+gdn.getIdCarpeta()));
				items.add(new Item("Nombre",carpeta.getNombreCarpeta()));
				items.add(new Item("Usuario",carpeta.getNombreUsuario()));
				items.add(new Item("Prioridad",""+carpeta.getPrioridad()));
				items.add(new Item("Fecha Creacion",""+carpeta.getFechaCreacion()));
				items.add(new Item("Fecha Modificacion",""+carpeta.getFechaModificacion()));
				items.add(new Item("No. Accesos",""+carpeta.getNumeroAccesos()));
				items.add(new Item("No. Documentos",""+carpeta.getNumeroDocumentos()));
				items.add(new Item("Descripcion",carpeta.getDescripcion()));
				items.add(new Item("Tamaño",""+carpeta.getTamanoBytes()));
			} else if(hibernateObject instanceof HashMap) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> expediente = (HashMap<String, Object>)hibernateObject; 
				for(Entry<String,Object> entry : expediente.entrySet()) {
					items.add(new Item(entry.getKey(),""+entry.getValue()));
				}
			}
			
			Json json=new Json();
			json.add("items", items);
			json.add("success", true);
			json.returnJson(response);
		} catch (Exception e) {
			log.error(e,e);
			Json json=new Json();
			json.add("error", e.toString());
			json.add("success", false);
			json.returnJson(response);
		}
	}
	
	public void obtenerGavetas(HttpServletRequest request,HttpServletResponse response){
		try {
			log.debug("Creando árbol de gavetas");
			HttpSession session = request.getSession(false);
			Usuario usuario = (Usuario)session.getAttribute(USER_KEY);
			
			List<imx_aplicacion> gavetas = new ArrayList<imx_aplicacion>();
			for(HashMap<String, String> hashMapGaveta : PrivilegioManager.getGavetasP(usuario.getNombreUsuario())) {
				String tituloAplicacion = hashMapGaveta.get("gaveta");
				String tblAplicacion = "imx"+hashMapGaveta.get("gaveta");
				String descripcion = hashMapGaveta.get("gaveta");
				gavetas.add(new imx_aplicacion(tituloAplicacion,tblAplicacion,descripcion));
			}
			
			NodoModel nodoModel = new NodoModel(gavetas);
			Json json=new Json();
			json.add("text", ".");
			json.add("children", nodoModel);
			json.add("success", true);
			json.returnJson(response);
		} catch (Exception e) {
			log.error(e,e);
			Json json=new Json();
			json.add("error", e.toString());
			json.add("success", false);
			json.returnJson(response);
		}
	}
	
	public void obtenerExpediente(HttpServletRequest request,HttpServletResponse response){
		try {
			String nodo = request.getParameter("node");
			if(nodo.equals("root"))
				nodo = request.getParameter("select")+"C0";
			String password = request.getParameter("password");
			log.debug("Creando árbol de expediente "+nodo+".");			
			HttpSession session = request.getSession(false);
			Usuario usuario = (Usuario)session.getAttribute(USER_KEY);
			
			ArbolManager arbolManager = new ArbolManager(nodo);
			ITreeNode root = arbolManager.getChildren(usuario.getNombreUsuario(),false).getRoot();
			NodoModel nodoModel = new NodoModel(root);
			
			if(nodoModel.protegido&&password==null)
				nodoModel.children = null;
			
			if(nodo.endsWith("C0")) {
				nodoModel.expanded = true;
				nodoModel.iconCls = "nodo_root";
				Json json=new Json();
				json.add("text", ".");
				json.add("children", nodoModel);
				json.returnJson(response);
			} else {
				if (nodoModel.children==null)
					nodoModel.children = new ArrayList<NodoModel>();
				new Json(nodoModel.children).returnJson(response);
			}
		} catch (Exception e) {
			log.error(e,e);
			Json json=new Json();
			json.add("error", e.toString());
			json.add("success", false);
			json.returnJson(response);
		}
	}
	
	public void obtenerMisDocumentos(HttpServletRequest request,HttpServletResponse response){
		try {
			log.debug("Creando arbol de documentos para ifimax.");			
			String nombre_usuario = request.getParameter("usuario");
			Usuario usuario = null;
			if(nombre_usuario==null) {
				HttpSession session = request.getSession(false);
				usuario = (Usuario)session.getAttribute(USER_KEY);
			} else 
				usuario = new UsuarioManager().selectUsuario(nombre_usuario);
			
			String nodo = request.getParameter("node");

			if(nodo.equals("root"))
				nodo = "USR_GRALES_G"+usuario.getIdGabinete()+"C0";
			
			String password = request.getParameter("password");
				
			ArbolManager arbolManager = new ArbolManager(nodo);
			ITreeNode root = arbolManager.getChildren(usuario.getNombreUsuario(),false).getRoot();
			NodoModel nodoModel = new NodoModel(root);
			
			if(nodoModel.protegido&&password==null)
				nodoModel.children = null;
			
			if(nodo.endsWith("C0")) {
				nodoModel.expanded = true;
				nodoModel.iconCls = "nodo_root";
				Json json=new Json();
				json.add("text", ".");
				json.add("children", nodoModel);
				json.returnJson(response);
			} else {
				if (nodoModel.children==null)
					nodoModel.children = new ArrayList<NodoModel>();
				new Json(nodoModel.children).returnJson(response);
			}
		} catch (Exception e) {
			log.error(e,e);
			Json json=new Json();
			json.add("error", e.toString());
			json.add("success", false);
			json.returnJson(response);
		}
	}
	
	private void obtenerTablaExpedientes(HttpServletRequest request, HttpServletResponse response) {
		String select = request.getParameter("select");
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		String destino = basePath+"/jsp/ResultadosBusquedaExpedientes.jsp?select="+select+"&tipoAccion=expedientes";
		try {
			response.sendRedirect(destino);
		} catch (IOException e) {
			log.error(e,e);
		}
	}
	
	private void salida(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		String destino = basePath+"/jsp/Salida.jsp";
		try {
			response.sendRedirect(destino);
		} catch (IOException e) {
			log.error(e,e);
		}
	}
	
	private void obtenerStorage(HttpServletRequest request, HttpServletResponse response) {
		String usuario = request.getParameter("select");
		Json json=new Json();
		try {
			UsedStorageCapacity usc = new UsedStorageCapacity();
			usc.getQuota(usuario);
			json.add("nombre", "Espacio");
			json.add("espacio_asignado", usc.bytesAutorizados/(1024*1024));
			json.add("espacio_utilizado",usc.bytesUsadosUsr/(1024*1024));
			json.add("unidades", "MB");
			json.add("success", true);
			json.returnJson(response);
			
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("success", false);
			json.returnJson(response);
		}	
	}
}
