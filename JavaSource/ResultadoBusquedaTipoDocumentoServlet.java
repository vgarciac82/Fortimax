import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.fortimax.hibernate.managers.imx_tipos_documentos_index_manager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.servlets.PlantillaDocumentoServlet.DatosPlantilla;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;
 public class ResultadoBusquedaTipoDocumentoServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(ResultadoBusquedaTipoDocumentoServlet.class); 


	private static final long serialVersionUID = 2903989704714853933L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Expires", "-1");
		PrintWriter out = resp.getWriter();
		
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

		String pag = req.getParameter("pag");
		String gaveta = req.getParameter("gaveta");
		gaveta = (gaveta.length()>0)?gaveta:null;
		String plantilla = req.getParameter("plantilla");
		String datos = req.getParameter("datos");

		@SuppressWarnings("unused")
		int page = 0;
		try {
			page = Integer.parseInt(pag);
		} catch (Exception e) {	
			log.trace("No se establecio la página de resultados, se cargara la primera");
		}

		List<imx_documento_id> indices = null;
		try {
			indices = getIndices(gaveta,Integer.parseInt(plantilla),datos);
		} catch (EntityManagerException e) {
			log.error(e,e);
		}
		
		GenerarConsultaTipoDocumento(req, resp, out, getData(indices), getHeaders());
	}

	private List<imx_documento_id> getIndices(String gaveta, int plantilla, String datos) throws EntityManagerException {
		log.debug("Se hara una busqueda en la gaveta "+gaveta+" con la plantilla "+plantilla+" y datos "+datos);
		DatosPlantilla[] datosPlantilla = Json.getObject(datos, DatosPlantilla[].class);
		List<imx_documento_id> resultado = new ArrayList<imx_documento_id>();
		for(DatosPlantilla dato : datosPlantilla) {
			if(dato.valor.length()>0) {
				imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager(gaveta, plantilla, dato);
				List<imx_documento_id> ids = indexManager.getDocumentoIDs();
				if(resultado.isEmpty())
					resultado = ids;
				else 
					resultado.retainAll(ids);
			}
		}
		return resultado;
	}

	private String[][] getData(List<imx_documento_id> resultados) {
		String[][]  data = new String[resultados.size()][5];
			try {
				Iterator<imx_documento_id> it = resultados.iterator();
				int row = 0;
				while(it.hasNext()){
					imx_documento_id id = it.next();
					String select = "";
					DocumentoManager docMgr = new DocumentoManager();
					Documento d = docMgr.selectDocumento(id.getTituloAplicacion(),
								 					 id.getIdGabinete(),
								 					 id.getIdCarpetaPadre(),
								 					 id.getIdDocumento());

					select = ArbolManager.generaIdNodeDocumento(d.getTituloAplicacion(),
															d.getIdGabinete(),
															d.getIdCarpetaPadre(),
															d.getIdDocumento());
					
					
					String liga    = "../jsp/Visualizador.jsp?"
	          					   + "select="
	          					   +select;
					String target = "_self";
					
										
					data[row][0]=""+(row+1);
					data[row][1]=d.getNombreAplicacion();
					data[row][2]=d.getNombreCarpeta();
					data[row][3]="<a href=\""+liga+"\" target=\""+target+"\">"+d.getNombreDocumento()+"</a>";
					data[row][4]=d.getDescripcion()!=null && !"".equals((String)d.getDescripcion()) ? d.getDescripcion() : "&nbsp;";
					row++;
			}	
		} catch (Exception e) {
			
		}
		return data;
	}

	private String[] getHeaders() {
		String[] headers = new String[5];
		headers[0]="{ 'sTitle': '"+"Num"+"', 'sClass': 'right','sWidth': '10px','fnRender':fn_default }";
		headers[1]="{ 'sTitle': '"+"Gaveta"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[2]="{ 'sTitle': '"+"Expediente"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[3]="{ 'sTitle': '"+"Nombre"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[4]="{ 'sTitle': '"+"Descripción"+"', 'sClass': 'left','fnRender':fn_default }";
		return headers;
	}

	private void GenerarConsultaTipoDocumento(HttpServletRequest req,HttpServletResponse resp,PrintWriter out, String[][] data, String[] headers) 
	throws IOException, ServletException
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
