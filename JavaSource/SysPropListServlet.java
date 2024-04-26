import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.syc.fortimax.config.Config;
//import com.syc.fortimax.config.Config;
import com.syc.utils.Json;

 public class SysPropListServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(SysPropListServlet.class); 

	private static final long serialVersionUID = 2639500861634727314L;
	
	private HttpServletRequest req;
	private HttpServletResponse res;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request, response);	
	}
	
	public void doWork(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.req=request;
		this.res=response;
		
		String action=req.getParameter("action");
		String select=req.getParameter("select");
		
		if(action!=null) {
			if(action.equals("consulta")) {
				if(select!=null) {
					if(select.equals("fortimax"))
						fortimaxInfo();
					else if(select.equals("javaEntorno"))
						javaInfo(request,response);
					else
						throwErrorJson(select+" no es un \"select\" valido para el \"action\" "+action);
				} else {
					throwErrorJson("Es necesario especificar el parametro \"select\"");
				} 
			} else {
				throwErrorJson(action+" no es un \"action\" valido");
			}
		} else {
			throwErrorJson("Es necesario especificar el parametro \"action\"");
		}
	}
	
	private void throwErrorJson(String error) {
		Json json = new Json();
		json.add("success", false);
		json.add("error", error);
		json.returnJson(res);
	}
	
	private void fortimaxInfo() {
		Json json = new Json();
		try{
		List<?> Versiones=Config.countV("versiones",null);
		Element El=null;
		Element Ed=null;
		ArrayList<Object> catalog_model= new ArrayList<Object>();
		HashMap<String,String> Version = new HashMap<String,String>();
		for(int i=0;i<Versiones.size();i++){
			Version.clear();
			El=(Element)Versiones.get(i);
			Ed=(Element)Config.countV("versiones", El.getName()).get(0);			
			Version.put("ID", Integer.toString(i+1));
			Version.put("Nombre", Ed.getValue());
			Ed=(Element)Config.countV("versiones", El.getName()).get(1);	
			Version.put("Valor", Ed.getValue());
			catalog_model.add(Version.clone());
			json.add("catalog", catalog_model.clone());
			json.add("success", true);
		}
		}
		catch (Exception e) {
			log.error(e, e);
			json.add("success", false);
			json.add("error", e.getMessage());
		}
		json.returnJson(res);
	}

	@SuppressWarnings("unchecked")
	private void javaInfo(HttpServletRequest request, HttpServletResponse response) {
		log.debug("Generando Informacion de Java");
		Json json=new Json();
		try{
			int start=Integer.valueOf(request.getParameter("start"));
			int limit=Integer.valueOf(request.getParameter("limit"));
			String campoBusqueda=(String)request.getParameter("campoBusqueda").toLowerCase();
			Boolean busqueda=false;
			if(campoBusqueda!=null&&campoBusqueda!="")
				busqueda=true;
			
			Properties pr = System.getProperties();
			Enumeration<?> epr = pr.propertyNames();
			HashMap<String, Object> record = new HashMap<String, Object>();
			List<HashMap<String, Object>> informacion_model= new ArrayList<HashMap<String, Object>>();
			if(!busqueda){
			record.put("propiedad", "fortimax.installationPath");
			record.put("valor", Config.fortimaxPath);
			informacion_model.add((HashMap<String, Object>)record.clone());
			}
			else{
				if("fortimax.installationpath".indexOf(campoBusqueda)!=-1||Config.fortimaxPath.toLowerCase().indexOf(campoBusqueda)!=-1){
					record.put("propiedad", "fortimax.installationPath");
					record.put("valor", Config.fortimaxPath);
					informacion_model.add((HashMap<String, Object>)record.clone());
				}
			}
			log.debug("Busqueda de informacion con filtro: "+busqueda);
			while (epr.hasMoreElements()) {
				String propiedad=(String) epr.nextElement();
				record.clear();
				if(busqueda){
					if(propiedad.toLowerCase().indexOf(campoBusqueda)!=-1||((String)pr.get(propiedad)).toLowerCase().indexOf(campoBusqueda)!=-1){
						record.put("propiedad",propiedad );
						record.put("valor", (String) pr.get(propiedad));
						informacion_model.add((HashMap<String, Object>)record.clone());
					}
				}
				else{
					record.put("propiedad",propiedad );
					record.put("valor", (String) pr.get(propiedad));
					informacion_model.add((HashMap<String, Object>)record.clone());
				}
				
			}

			Collections.sort(informacion_model, new Comparator<HashMap<String, Object>>() {

				@Override
				public int compare(HashMap<String, Object> arg0,
						HashMap<String, Object> arg1) {
					String v1=arg0.get("propiedad").toString();
					String v2=arg1.get("propiedad").toString();
					if(v1.compareTo(v2)>0)
						return 1;
					else if(v1.compareTo(v2)<0)
						return 0;
					else
						return 1;
				}

		    });
			List<HashMap<String, Object>> Info=null;
			if(start+limit<=informacion_model.size()){
				Info=informacion_model.subList(start, start+limit);
			}
			else{
				Info=informacion_model.subList(start, informacion_model.size());
			}
				
			
			json.add("valores", Info);
			json.add("totalFilas", informacion_model.size());
			json.returnJson(response);
		}
		catch(Exception ex){
			log.error("Error al generarl Informacion Java");
			log.error(ex.toString());
		}
	}
}