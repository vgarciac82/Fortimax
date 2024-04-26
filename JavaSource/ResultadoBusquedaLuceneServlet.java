import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.ParseException;

import com.google.gson.Gson;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.lucene.SearchFiles;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;
 public class ResultadoBusquedaLuceneServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(ResultadoBusquedaLuceneServlet.class); 


	private static final long serialVersionUID = 2903989704714853933L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		PrintWriter out = resp.getWriter();
		if(action==null) {
			resp.setContentType("text/json");
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Cache-Control", "no-cache");
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader("Expires", "-1");
		
			HttpSession session = req.getSession(false);
			if (session == null) {
				out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
				out.flush();
				out.close();
				return;
			}

			Usuario u = (Usuario) session.getAttribute(USER_KEY);
			if (u == null) {
				out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
				out.flush();
				out.close();
				return;
			}

			ITree tree = (ITree) session.getAttribute(TREE_KEY);
			if (tree == null) {
				out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
				out.flush();
				out.close();
				return;
			}
		}
		
		String pag = req.getParameter("pag");
		String palabraClave = req.getParameter("palabra_clave");
		byte[] utf8 = palabraClave.getBytes("UTF-8");
		palabraClave = new String(utf8, "UTF-8");
		if(palabraClave.startsWith("*")||palabraClave.startsWith("?"))
			palabraClave=palabraClave.substring(1);

		String pathToDir = Config.getLuceneIndex();

		int page = 0;
		try {
			page = Integer.parseInt(pag);
		} catch (Exception e) {	
			log.info("No se establecio la página de resultados, se cargara la primera");
		}
		
		ArrayList<Document> resultados = null;
		
		try {
				resultados = SearchFiles.search(pathToDir, palabraClave, 1000,
						page);
			} catch (ParseException e) {	
				log.error(e,e);
			}
			//TODO Reescribir, lo que pasa es que cuando son demasiados registros, va uno por uno a traer informacion extra acerca de la cantidad de archivos que contiene y demas
			//cosa que deberia de hacer en un solo jalon para que sea mas rapido, igual con una buena paginacion se aliviana el cotorreo.
			//	String[][]  expedientInfo = em.getExpedienteInfo(data, titulo_aplicacion);
			
		try {
				if(action==null)
					GenerarConsultaLucene(req, resp, out, getData(resultados), getHeaders());
				else
					GenerarConsultaLuceneJson(resp,resultados);
				return;
		} catch (Exception e) {
			log.error(e,e);
		} 

	}
	
	private void GenerarConsultaLuceneJson(HttpServletResponse response, ArrayList<Document> resultados) {
		Json json = new Json();
		try {
			List<Map<String,String>> valores = new ArrayList<Map<String,String>>();
			
			for(Document document : resultados) {
				Map<String,String> map = new HashMap<String,String>(); 
				for(Fieldable field : document.getFields()) {
					String value = document.get(field.name());
					if(value==null)
						value = "";
					map.put(field.name(),value);
				}
				valores.add(map);
			}
			
			json.add("valores", valores);
			json.add("totalFilas", resultados.size());
			json.add("success",true);
			json.returnJson(response);
		} catch (Exception e) {
			log.error(e,e);
			json.add("message", e.toString());
			json.add("success",false);
			json.returnJson(response);
		}
	}

	//private

	private String[][] getData(ArrayList<Document> resultados) {
		String[][]  data = new String[resultados.size()][6];
			try {
				Iterator<Document> it = resultados.iterator();
				int row = 0;
				while(it.hasNext()){
					Document documento = it.next();
					String select = "";
					DocumentoManager docMgr = new DocumentoManager();
					Documento d = docMgr.selectDocumento(documento.get("aplicacion"),
								 					 Integer.parseInt(documento.get("gabinete")),
								 					 Integer.parseInt(documento.get("carpeta")),
								 					 Integer.parseInt(documento.get("documento")));

					select = ArbolManager.generaIdNodeDocumento(d.getTituloAplicacion(),
															d.getIdGabinete(),
															d.getIdCarpetaPadre(),
															d.getIdDocumento());
					int numPagina = Integer.parseInt(documento.get("pagina")); 	//en la coleccion de paginas comienza en uno
					int numIndex  = numPagina -1;
					//en la coleccion de imagenes comienza en cero
					
					String liga    = "../getexpedient?"
          					   + "fromBusqueda=ocr"
          					   + "&select="
          					   + d.getTituloAplicacion()
          					   + "&id_gabinete="
          					   + d.getIdGabinete()
          					   + "&toSelect="
          					   +select;
					String ligaPag = "../getexpedient?"
							   + "fromBusqueda=ocr"
							   + "&image.index="
							   + numIndex 
							   + "&select="
							   + d.getTituloAplicacion()
							   + "&id_gabinete="
							   + d.getIdGabinete()
							   + "&toSelect="
							   + select;
					String target = "left";
					
					if(!d.getNombreTipoDocto().equals("IMAX_FILE")) {
						liga    = "../filestore?"
	          					   + "select="
	          					   +select;
						ligaPag = "../filestore?"
	          					   + "select="
	          					   +select;
						target = "_self";
					}
					
					
					data[row][0]=""+(row+1);
					data[row][1]=d.getNombreAplicacion();
					data[row][2]=d.getNombreCarpeta();
					data[row][3]="<a href=\""+liga+"\" target=\""+target+"\">"+d.getNombreDocumento()+"</a>";
					data[row][4]=d.getDescripcion()!=null && !"".equals((String)d.getDescripcion()) ? d.getDescripcion() : "&nbsp;";
					data[row][5]="<a href=\""+ligaPag+"\" target=\""+target+"\">"+numPagina+"&nbsp;</a>";
					row++;
			}	
		} catch (Exception e) {
			
		}
		return data;
	}

	private String[] getHeaders() {
		String[] headers = new String[6];
		headers[0]="{ 'sTitle': '"+"Num"+"', 'sClass': 'right','sWidth': '10px','fnRender':fn_default }";
		headers[1]="{ 'sTitle': '"+"Gaveta"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[2]="{ 'sTitle': '"+"Expediente"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[3]="{ 'sTitle': '"+"Nombre"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[4]="{ 'sTitle': '"+"Descripción"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[5]="{ 'sTitle': '"+"Página"+"', 'sClass': 'center','sWidth': '10px','fnRender':fn_default }";
		return headers;
	}

	private void GenerarConsultaLucene(HttpServletRequest req,HttpServletResponse resp,PrintWriter out, String[][] data, String[] headers) 
	throws IOException, ServletException, Exception 
	{		
		Gson g = new Gson();
		String[][] Filas_json;
		try {
			
		if(data.length!= 0)
		{	
			Filas_json = new String[data.length][data[0].length];
			out.println("{'aaData': ");
			for (int i = 0; i < data.length; i++) //Lleva el control de las Filas
			{			
				for (int j = 0; j < data[i].length; j++)//Lleva el control de las columnas
				{
						Filas_json[i][j]=(data[i][j] == null) ? "---" : data[i][j].replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\t", " ");
				}
			}

			out.println(g.toJson(Filas_json));
		}
		else
		{
			out.println("{'aaData':[]");
		}
		out.println(",'aoColumns': [");
		
		//OPERACIONES PARA IMPRIMIR ENCABEZADOS DE TABLA
		//Estas Lineas definen los campos y sus propiedades, para DataTables esto debe ser dinamicamente,
		//Se debe planear que sea dinamicamente para cada campo, almacenando la informacion en BD...quiza en imx_descripcion
		//fnRender, aqui se establece un metodo, para prevenir que el campo tenga un comportamiento especial
		//define una funcion Javascript que recibira la columna y podra procesar la informacion.
		//Por default es "FN_NOMBREDELCAMPO"
		//Especifica para el tipo de ordenamiento sType: 'string', 'numeric', 'date' or 'html'

		//Las demas columnas
		for (int i = 0; i < headers.length; i++) 
			{
				if(i+1 < headers.length)
				//,'fnRender': fn_"+dsc[i].getNombreCampo()+
					out.println(headers[i]+",");
				else
				//,'fnRender': fn_"+dsc[i].getNombreCampo()+"
					out.println(headers[i]);
			}
		out.println("] }");

		} 
		catch (Exception dme) 
		{	log.error(dme,dme);

			throw new ServletException(dme);
		} 

	}
	
}
