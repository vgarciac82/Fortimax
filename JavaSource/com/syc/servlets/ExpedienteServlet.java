package com.syc.servlets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.csvreader.CsvWriter;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.gavetas.Gaveta;
import com.syc.gavetas.GavetaManager;
import com.syc.imaxfile.Descripcion;
import com.syc.imaxfile.DescripcionManager;
import com.syc.imaxfile.ExpedienteManager;
import com.syc.servlets.models.GavetaCampoModel;
import com.syc.servlets.models.NodoModel;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;
import com.syc.utils.Utils;

/**
 * Servlet implementation class BusquedaExpedienteServlet
 */
public class ExpedienteServlet extends HttpServlet implements ParametersInterface {
	private static final long serialVersionUID = 1L;
    private static final Logger log=Logger.getLogger(ExpedienteServlet.class);   
    
    public ExpedienteServlet() {
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
		if("getGridColumns".equals(action))
			generaColumnasGrid(request,response);
		else if("getExpedientesGaveta".equals(action))
			generaExpedientesGaveta(request,response);
		else if("getExpedientesGavetaCSV".equals(action))
			generaExpedientesGavetaCSV(request,response);
		else if("setExpedienteGaveta".equals(action))
			creaExpediente(request,response);
		else if("updateExpediente".equals(action))
			actualizaExpediente(request,response);
		else if("deleteExpediente".equals(action))
			eliminarExpediente(request,response);
		else if("getArbolExpedienteJson".equals(action))
			getArbolExpedienteJson(request,response);
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
	private void generaColumnasGrid(HttpServletRequest request,HttpServletResponse response){
		Json json=new Json();
		json.add("success", false);
		try{
			String select=request.getParameter("select");
			log.trace("Gaveta: "+select);
			DescripcionManager dm = new DescripcionManager();
			Descripcion[] descripcion=dm.selectDescripcion(select);
			
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			
			Map<String, String> idmap = new HashMap<String,String>();
			idmap.put("name", "ID_GABINETE");
			idmap.put("type", "java.lang.Integer");
			idmap.put("label", "ID");
			list.add(idmap);
			
			for(int i=0;i<descripcion.length;i++){
				String tipo="java.lang.String";
				if(descripcion[i].getNombreTipoDatos().toUpperCase().contains("INT"))
					tipo="java.lang.Integer";
				else if(descripcion[i].getNombreTipoDatos().toUpperCase().contains("FLOAT")||descripcion[i].getNombreTipoDatos().toUpperCase().contains("DECIMAL"))
					tipo="java.lang.Float";
				Map<String, String> map = new HashMap<String,String>();
				map.put("name", descripcion[i].getNombreCampo().toUpperCase());
				map.put("type", tipo);
				map.put("label", descripcion[i].getNombreColumna());
				list.add(map);
			}
			
			json.add("columnas", list);
			json.add("success", true);
		}
		catch(Exception e){
			log.error(e,e);
		}
		json.returnJson(response);
	}
	
	@SuppressWarnings("unchecked")
	private void generaExpedientesGaveta(HttpServletRequest request, HttpServletResponse response){
		log.debug("Generando lista de expedientes en gaveta");
		Json json=new Json();
		json.add("success", true);
		try{
			String select = request.getParameter("gaveta");
			String jsonBusqueda = request.getParameter("jsonBusqueda");
			String liveSearch = request.getParameter("LiveSearch");
			Integer start = null;
			if(request.getParameter("start")!=null)
				start = Integer.parseInt(request.getParameter("start"));
			Integer limit = null;
			if(request.getParameter("limit")!=null)
				limit = Integer.parseInt(request.getParameter("limit"));
			ExpedienteManager em=new ExpedienteManager();
			List<HashMap<String, Object>> listaDatos = em.getExpedientes(select, jsonBusqueda, liveSearch, start, limit);
			
			ArrayList<Object> filas= new ArrayList<Object>();
			for(Object o:listaDatos){
				HashMap<String, Object> rec = new HashMap<String, Object>();						
				HashMap<String,Object> map=(HashMap<String,Object>)o;
				for (Entry<String, Object> entry : map.entrySet()) {				
						  String key = entry.getKey();
						  Object value = map.get(key)!=null?map.get(key):"";				  
						  rec.put(key, value.toString());
					}
				filas.add(rec.clone());
			}

			json.add("valores", filas);
			json.add("totalFilas",em.getTotalExpedientes(select,jsonBusqueda,liveSearch));
			json.add("success", true);
			
		}
		catch(Exception e){
			log.error(e,e);
		}
		json.returnJson(response);
	}
	
	private void generaExpedientesGavetaCSV(HttpServletRequest request, HttpServletResponse response){
		log.debug("Generando reporte en CSV de Gaveta");
		try{
			String select = request.getParameter("select");
			String jsonBusqueda = request.getParameter("jsonBusqueda");
			String liveSearch = request.getParameter("LiveSearch");
			
			Gaveta gaveta = new GavetaManager().selectGaveta(select);
			
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    	String timestamp = sdf.format(Calendar.getInstance().getTime());
	    	String filename = gaveta.getDescripcion()+"-"+timestamp+".csv";
			response.setHeader("Content-Disposition","attachment; filename=\""+filename+"\"");
			response.setContentType("text/csv");
			response.setCharacterEncoding("ISO-8859-1");
			
			/* Las siguientes configuraciones estan enfocadas en que el csv generado se abra correctamente en un excel occidental sin problema.
			 * Sin embargo el archivo generado no esta ligado con excel de ninguna otra forma y puede ser utilizado en cualquier proceso que
			 * reciba de entrada un csv, basta con configurarlo de la misma manera.
			 * 
			 * Configuraciones:
			 * - Separador de valores: ,
			 * - Encoding: ISO-8859-1
			 * - Calificador de texto: "
			 * - Calificador de texto obligatorio
			 * - Secuencia de escape para carácteres especiales: duplicar el carácter
			 */
			CsvWriter csvOutput = new CsvWriter(response.getOutputStream(),',',Charset.forName("ISO-8859-1"));
			csvOutput.setTextQualifier('"');
			csvOutput.setForceQualifier(true);
			csvOutput.setEscapeMode(CsvWriter.ESCAPE_MODE_DOUBLED);
			
			GavetaCampoModel[] fmx_campos = {new GavetaCampoModel("ID_GABINETE","ID")};
			GavetaCampoModel[] campos = GavetaCampoModel.getGavetaCamposModel(gaveta);
			campos = (GavetaCampoModel[]) ArrayUtils.addAll(fmx_campos,campos);
			
			for(GavetaCampoModel campo : campos){
				csvOutput.write(campo.getEtiqueta());
			}
			csvOutput.endRecord();
			
			List<HashMap<String, Object>> expedientes = new ExpedienteManager().getExpedientes(select, jsonBusqueda, liveSearch, null, null);
			
			for (HashMap<String, Object> expediente : expedientes){
				for(GavetaCampoModel campo : campos){
					Object valor = expediente.get(campo.getNombre());
					if(valor==null)
						csvOutput.write("");
					else
						csvOutput.write(valor.toString());
				}
				csvOutput.endRecord();
			}
			csvOutput.close();
		}
		catch(Exception e){
			log.error(e,e);
		}
	}
	
	
	private void creaExpediente(HttpServletRequest request,HttpServletResponse response){
		log.debug("Creando expediente nuevo");
		Json json=new Json();
		String message="";
		json.add("success",false);
		try{
			HttpSession session = request.getSession(false);
			Usuario u = (Usuario)session.getAttribute(USER_KEY);
			ExpedienteManager em=new ExpedienteManager();
			String select=request.getParameter("gaveta");
			String jsonInsertar=request.getParameter("jsonInsertar");
			Map<String, Object> mapaCampos = Json.getMap(jsonInsertar);
			String nombreCarpeta=em.getNombreCarpeta(mapaCampos);
			Map<String, String[]> map=em.creaMapaParametros(select,mapaCampos);
			String[] result = em.insertExpediente(select, u.getNombreUsuario(), nombreCarpeta, map);
			message=result[2];
			json.add("success", true);
		}
		catch(Exception e){
			message="Error al crear expediente";
			log.error(e,e);
		}
		json.add("message", message);
		json.returnJson(response);
	}
	static class jsonDinamico {
		String nombre;
		String valor;
	}
	public void actualizaExpediente(HttpServletRequest request,HttpServletResponse response){
		log.debug("Actualizando expediente");
		Json json=new Json();
		String message="";
		Boolean resultado=false;
		try{
			ExpedienteManager em=new ExpedienteManager();
			String gaveta=request.getParameter("gaveta");
			String datos=request.getParameter("json");
			Map<String, Object> mapaCampos = Json.getMap(datos);
			int idGabinete=Integer.parseInt((String)mapaCampos.get("ID_GABINETE"));
			resultado=em.updateExpedientes(gaveta, idGabinete, mapaCampos);
			if(resultado)
				message="Expediente actualizado correctamente";
			else
				message="Ocurrio un error al actualizar el expediente";
			
		}
		catch(Exception e){
			log.error("Error al actualizar un expediente");
			log.error(e,e);
			message = e.toString();
		}
		json.add("message", message);
		json.add("success", resultado);
		json.returnJson(response);
	}
	public void eliminarExpediente(HttpServletRequest request,HttpServletResponse response){
		log.debug("Eliminando expediente");
		Json json=new Json();
		String message="";
		Boolean result=false;
		try{
			String gaveta=request.getParameter("gaveta");
			String recordData=request.getParameter("recordData");
			int idGabinete = Utils.getInteger(Json.getMap(recordData).get("ID_GABINETE"));
			ExpedienteManager em=new ExpedienteManager();
			result=em.deleteExpediente(gaveta, idGabinete);
			if(result)
				message="Expediente eliminado correctamente";
			else
				message="Ocurrio un error al eliminar el expediente";
			
			json.add("message", message);
			json.add("success", result);
			json.returnJson(response);
		}
		catch(Exception e){
			log.error("Error al eliminar expediente");
			log.error(e,e);
		}
	}
	
	public void getArbolExpedienteJson(HttpServletRequest request,HttpServletResponse response){
		try {
			log.debug("Creando arbol de documentos para ifimax.");
			String titulo_aplicacion = request.getParameter("titulo_aplicacion");
			int id_gabinete = Integer.parseInt(request.getParameter("id_gabinete"));
			
			HttpSession session = request.getSession(false);
			Usuario usuario = (Usuario)session.getAttribute(USER_KEY);
			
			ArbolManager arbolManager = new ArbolManager(titulo_aplicacion, id_gabinete);
			ITreeNode root = arbolManager.generaExpediente(usuario.getNombreUsuario()).getRoot();
			NodoModel nodoModel = new NodoModel(root);
			nodoModel.expanded = true;
			nodoModel.iconCls = "nodo_root";
			
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
}
