import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.tree.ArbolManager;
import com.syc.utils.DesEncrypter;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;
import com.syc.utils.linkshared;
import com.syc.zip.MakeZip;

 public class GetSharedDocumentServlet extends HttpServlet implements ParametersInterface {

	private static final long serialVersionUID = -3061927317237526950L;
	private static Logger log = Logger.getLogger(GetSharedDocumentServlet.class);
				
	public GetSharedDocumentServlet(){
		super();
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
			doWork(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doWork(request,response);
	}
	public void doWork(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String action=request.getParameter("action");
		log.debug("Ejecutando accion: "+action);
		if("getShareDocument".equals(action))
			obtenerDocumentoCompartido(request,response);
		else if("verDocumento".equals(action))
			verDocumentoCompartido(request,response);
		else if("descargarDocumento".equals(action))
			descargarDocumento(request,response);
		else if("getSharedDocumentInfo".equals(action))
			getSharedDocumentInfo(request,response);
		else if("cancelarCompartir".equals(action))
			CancelarCompartir(request,response); //TODO Método aun usado sólo en viejo FMX
		else if("modificaCompartir".equals(action))
			ModificarCompartirDocumento(request,response);
		else
			AccionInvalida(request,response);
	}
	private void AccionInvalida(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		json.add("success",false);
		String message = "El action ingresado no es válido";
		json.add("message", message);
		json.returnJson(response);
	}
	private void obtenerDocumentoCompartido(HttpServletRequest request,HttpServletResponse response){
		response.setContentType("text/plain; charset=utf-8");
		log.debug("Entro a obtener documento compartido");
		Json json=new Json();
		json.add("success", false);
		try{			
			String identificadorDocumento=request.getParameter("select");
			String token=(String)request.getParameter("token");
			linkshared Ls=new linkshared();
			Documento d=Ls.obtenerDocumentoCompartido(identificadorDocumento);
			if(d!=null){
				if(d.getCompartir().equals("S")){
							if(token.equals(d.getTokenCompartir())){
							SimpleDateFormat sdfParameter = new SimpleDateFormat("yyyy/MM/dd:HH:mm");
							Date fecha=new Date();
							//SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy/MM/dd");
							if(fecha.before(sdfParameter.parse(d.getDateExp()+":"+d.getHoureExp()))){
								ArrayList<Object> documento=new ArrayList<Object>();
								HashMap<String, Object> rec=new HashMap<String,Object>();
								rec.put("nombre_documento", d.getNombreDocumento());
								rec.put("compartir", d.getCompartir());
								rec.put("token", d.getTokenCompartir());
								rec.put("nombre_tipo_docto", d.getNombreTipoDocto());
								rec.put("titulo_aplicacion", d.getTituloAplicacion());
								rec.put("id_gabinete", d.getIdGabinete());
								rec.put("id_carpeta_padre", d.getIdCarpetaPadre());
								rec.put("id_documento", d.getIdDocumento());
								rec.put("dateExp",d.getDateExp());
								rec.put("houreExp", d.getHoureExp());
								rec.put("ligaPermisoBajar", d.getLigaPermisoBajar());
								documento.add(rec.clone());
								json.add("documento", documento);
								
								HttpSession session = request.getSession();
								ITree tree = null;
								ArbolManager am = new ArbolManager(d.getTituloAplicacion(),d.getIdGabinete());
								tree = am.generaExpediente(d.getNombreUsuario());
								
								session.setAttribute(TREE_KEY, tree);
								
							}
							else{
								ArrayList<Object> documento=new ArrayList<Object>();
								HashMap<String, Object> rec=new HashMap<String,Object>();
								rec.put("nombre_documento", d.getNombreDocumento());
								rec.put("compartir", "N");
								rec.put("token", "");
								rec.put("nombre_tipo_docto", d.getNombreTipoDocto());
								rec.put("titulo_aplicacion", "");
								rec.put("id_gabinete", "");
								rec.put("id_carpeta_padre", "");
								rec.put("id_documento", "");
								rec.put("dateExp",d.getDateExp());
								rec.put("houreExp", d.getHoureExp());
								rec.put("ligaPermisoBajar", "N");
								documento.add(rec.clone());
								json.add("documento", documento);
							}
							
						}
						else{
							log.debug("El token no es correcto para ver el documento: "+identificadorDocumento);
							json.add("documento", null);
						}
			}
			else{
				json.add("documento", null);
				}
				}
			else{
				json.add("documento", null);
			}
				json.add("success", true);
		}
		catch(Exception e){
			log.error("Error al compartir documento: "+e.toString());
		}
		json.returnJson(response);
	}
	
	@SuppressWarnings("deprecation")
	private void verDocumentoCompartido(HttpServletRequest request,HttpServletResponse response){
		log.debug("Ver documento compartido: "+request.getParameter("select"));
		Json json=new Json();
		String msjRegreso="";
		json.add("success",false);
		StringBuffer queryString = new StringBuffer();
		try{
			String nodo=request.getParameter("select");
			DocumentoManager dm=new DocumentoManager();
			Documento d= dm.selectDocumento(nodo);
			queryString.append("?select=" + nodo);
			queryString.append("&" + ParametersInterface.INDEX_KEY + "=" + ((d.getFilesNames().length > 0) ? "0" : "-1"));
			String[] fullPathFileNames = d.getFullPathFilesNames();
			DesEncrypter de = new DesEncrypter(DesEncrypter.PASSWORD);
			if(fullPathFileNames.length > 0){
				queryString.append("&image.hash="+URLEncoder.encode(de.encrypt(fullPathFileNames[0]), "UTF-8"));
				json.add("success",true);
				json.add("rutaDireccion", URLEncoder.encode(queryString.toString()));
			}
			else{
				queryString=null;
				json.add("success",false);
			}
			
		}
		catch(Exception e){
			json.add("success",false);
			log.error("Error verDocumentoCompartido: "+e.toString());
		}
		json.add("message", msjRegreso);
		json.returnJson(response);
	}
	
	private void descargarDocumento(HttpServletRequest request,HttpServletResponse response){
		log.debug("Descargar documento compartido: "+request.getParameter("select"));
		try{
			String nodo = request.getParameter("select");
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition","attachment; filename=\""+gdn.getName()+".zip"+"\"");
			try {
				MakeZip.writeZip("", nodo, false, response.getOutputStream());
			} finally {
				IOUtils.closeQuietly(response.getOutputStream());
			}
		}
		catch(Exception e){
			log.error(e.toString());
		}
	}
	
	private void getSharedDocumentInfo(HttpServletRequest request, HttpServletResponse response){
		response.setContentType("text/plain; charset=utf-8");
		log.debug("Compartir documento");
		Json json=new Json();
		json.add("success", false);
		try{		
			String identificadorDocumento=request.getParameter("select");
			linkshared Ls=new linkshared();
			Documento d=Ls.comparteDocumento(identificadorDocumento);
			
			SimpleDateFormat sdfParameter = new SimpleDateFormat("yyyy/MM/dd:HH:mm");
			SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy/MM/dd");
			ArrayList<Object> documento=new ArrayList<Object>();
			HashMap<String, Object> rec=new HashMap<String,Object>();
			rec.put("nombre_documento", d.getNombreDocumento());
			rec.put("compartir", d.getCompartir());
			rec.put("token", d.getTokenCompartir());
			rec.put("nombre_tipo_docto", d.getNombreTipoDocto());
			rec.put("titulo_aplicacion", d.getTituloAplicacion());
			rec.put("id_gabinete", d.getIdGabinete());
			rec.put("id_carpeta_padre", d.getIdCarpetaPadre());
			rec.put("id_documento", d.getIdDocumento());
			try{
			rec.put("dateExp",sdfParam.format(sdfParameter.parse(d.getDateExp())));
			}
			catch(ParseException e){
				rec.put("dateExp",d.getDateExp());
			}
			rec.put("houreExp", d.getHoureExp());
			rec.put("ligaPermisoBajar", d.getLigaPermisoBajar());
			documento.add(rec.clone());
			json.add("documento", documento);
			json.add("success", true);
		}
		catch(Exception e){
			log.error("Error al compartir documento: "+e.toString());
		}
		json.returnJson(response);
	}
	
	private void CancelarCompartir(HttpServletRequest request, HttpServletResponse response){
		response.setContentType("text/plain; charset=utf-8");
		log.debug("Dejar de compartir documento");
		Json json=new Json();
		json.add("success", false);
		String msjRegreso="";
		try{ //TODO: Verificar uso de este metodo, pues no deberia existir

			String identificadorDocumento=request.getParameter("select");
			linkshared Ls=new linkshared();
			Boolean resultado=Ls.cancelarCompartir(identificadorDocumento);
			if(resultado){
				msjRegreso="Se dejo de compartir el documento";
				json.add("success", true);
			}
			else{
				msjRegreso="Ocurrio un problema al dejar de compartir el documento";
				json.add("success", false);
			}
		}
		catch(Exception ex){
			msjRegreso="Ocurrio un problema al dejar de compartir el documento";
			log.error("Error al dejar de compartir un documento: "+ex.toString());
		}
		json.add("message", msjRegreso);
		json.returnJson(response);
	}
	
	private void ModificarCompartirDocumento(HttpServletRequest request, HttpServletResponse response){
		response.setContentType("text/plain; charset=utf-8");
		log.debug("Modificando configuracion de compartir documento");
		Json json = new Json();
		String mensaje = "Ocurrio un problema al modificar propiedades de la liga";
		boolean exito = false;
		
		try{
			String nodo = request.getParameter("select");
			String datos = request.getParameter("datos");
			Documento documento = Json.getObject(datos, Documento.class);
			linkshared ligaCompartida = new linkshared();
			String fecha = "";
			Boolean resultado = false;
			
			if(documento.getCompartir().equals("N")){
				if(ligaCompartida.cancelarCompartir(nodo)){
					mensaje = "Documento ya no esta compartido.";
					exito = true;
				}
			}else{
				if(documento.getDateExp().length()>0){
					fecha = documento.getDateExp();
				}
				Boolean recompartir = documento.getDateExp().length()>0?false:true;
				resultado = ligaCompartida.modificarExpiracion(nodo, fecha, documento.getHoureExp(), documento.getLigaPermisoBajar(),recompartir);
				if(resultado){
					mensaje = "Modifico la configuracion correctamente";
					exito = true;
				}
			}
		}
		catch(Exception e){
			log.error("Error al modificar propiedades de liga de documento." + e.toString());
		}
		
		json.add("success", exito);
		json.add("message", mensaje);
		json.returnJson(response);	
	}
}