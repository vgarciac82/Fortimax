import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import com.google.gson.Gson;
import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.imaxfile.Carpeta;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.ParametersInterface;
import com.syc.utils.Utils;
 public class ResultadoBusquedaDocumentosServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(ResultadoBusquedaDocumentosServlet.class); 


	private static final long serialVersionUID = 2903989704714853933L;
	
	private ArrayList<Object[]> resultados = null;
	private ArrayList<Object> valores = null;

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
		
		try {
			consultaDocumentos(req, tree, u, session);

			session.setAttribute("documentos", resultados);
			
			//TODO Reescribir, lo que pasa es que cuando son demasiados registros, va uno por uno a traer informacion extra acerca de la cantidad de archivos que contiene y demas
			//cosa que deberia de hacer en un solo jalon para que sea mas rapido, igual con una buena paginacion se aliviana el cotorreo.
			//	String[][]  expedientInfo = em.getExpedienteInfo(data, titulo_aplicacion);
				
			GenerarJSON(req, resp,out, getData(tree), getHeaders());
			return;
			
		}
 		catch (Exception e) 
		{	log.error(e,e);
		} 
}	

	public void consultaDocumentos(HttpServletRequest req, ITree tree,
			Usuario u, HttpSession session) throws Exception {
		StringBuffer sQuery = null;

		resultados = new ArrayList<Object[]>();
		valores = new ArrayList<Object>();

		sQuery = armaQuery(req, u);
		
		HibernateManager hm = new HibernateManager();
		
		try{
			Query query = hm.createSQLQuery(sQuery.toString());
			for (int i = 0; i < valores.size(); i++) {
				
				if (valores.get(i) instanceof java.sql.Date){
					query.setDate(i, (java.sql.Date) valores.get(i));
				}
				else if (valores.get(i) instanceof java.sql.Timestamp){
					query.setTimestamp(i, (java.sql.Timestamp) valores.get(i));
				}
				else{
					query.setParameter(i, valores.get(i));
				}
			}
			
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			if(!query.list().isEmpty()){
				@SuppressWarnings("unchecked")
				List<Map<String, ?>> listresultset = query.list();
				int cols2 = listresultset.get(0).size();
				for (Map<String, ?> map : listresultset) {
					map = new CaseInsensitiveMap(map);
					Object obj[] = new Object[cols2];
					
					obj[0] = Utils.getString(map.get("nombre_documento"));
					obj[1] = Utils.getString(map.get("descripcion"));
					obj[2] = Utils.getDate(map.get("fh_creacion"));
					obj[3] = Utils.getDate(map.get("fh_modificacion"));
					obj[4] = Utils.getString(map.get("titulo_aplicacion"));
					obj[5] = Utils.getInteger(map.get("id_gabinete"));
					obj[6] = Utils.getInteger(map.get("id_carpeta_padre"));
					obj[7] = Utils.getInteger(map.get("id_documento"));
					
					if (resultados.isEmpty()
							|| !Arrays.equals(obj, (Object[]) resultados.get(resultados
									.size() - 1)))
						resultados.add(obj);
				}
			}
		} finally{
			hm.close();
		}
	}

	private void addValores(Object valor) {
		valores.add(valor);
	}

	private StringBuffer armaQuery(HttpServletRequest req, Usuario u) {
		StringBuffer sQuery = new StringBuffer();
		Enumeration<?> e = req.getParameterNames();
		boolean withAttributes = false;
		while (e.hasMoreElements()) {
			String campo = (String) e.nextElement();
			if (campo.startsWith("attr")) {
				withAttributes = true;
				break;
			}

		}
		sQuery
				.append("SELECT DISTINCT d.nombre_documento, d.descripcion,d.fh_creacion,d.fh_modificacion, d.titulo_aplicacion,d.id_gabinete,"
						+ " d.id_carpeta_padre, d.id_documento FROM  imx_documento d, imx_carpeta c"
						+ " WHERE"
						+ " d.titulo_aplicacion = c.titulo_aplicacion AND"
						+ " d.id_gabinete = c.id_gabinete AND"
						+ " d.id_carpeta_padre = c.id_carpeta AND"
						+ " d.titulo_aplicacion in ");

		sQuery.append("( ");
		sQuery.append("   select ");
		sQuery.append("   ia.TITULO_APLICACION ");
		sQuery.append("   from imx_privilegio p, imx_aplicacion ia ");
		sQuery.append("   where p.TITULO_APLICACION = ia.TITULO_APLICACION ");
		sQuery.append("   and p.NOMBRE_USUARIO = '");
		sQuery.append(u.getNombreUsuario());
		sQuery.append("' ");
		
		sQuery.append("   union ");
		sQuery.append("   select ");
		sQuery.append("   ia.TITULO_APLICACION ");
		sQuery
				.append("   from imx_grupo_privilegio gp, imx_aplicacion ia, imx_grupo_usuario gu ");
		sQuery.append("   where gp.TITULO_APLICACION = ia.TITULO_APLICACION ");
		sQuery.append("   and gp.NOMBRE_GRUPO = gu.NOMBRE_GRUPO ");
		sQuery.append("   and gu.NOMBRE_USUARIO = '");
		sQuery.append(u.getNombreUsuario());
		sQuery.append("' ");
		sQuery.append(") ");

		if (!"Mis Documentos".equals(req.getParameter("gaveta"))) {

			if ("Todas".equals(req.getParameter("gaveta"))) {
				//
			} else {
				sQuery.append(" AND d.titulo_aplicacion='"
						+ req.getParameter("gaveta") + "'");
			}
			
			req.getSession(false);
		} else {
			if (!"Todas".equals(req.getParameter("gaveta"))) {
				sQuery.append(" AND  ( (d.titulo_aplicacion = 'USR_GRALES' AND d.id_gabinete = "
						+ u.getIdGabinete()
						+ ") "
						+ "			OR (((d.prioridad >= 2 OR d.prioridad=-1) "
						+ "		 OR d.nombre_usuario='"+u.getNombreUsuario()+"')");
				if ("Todas".equals(req.getParameter("gaveta")))
					sQuery.append(" AND d.titulo_aplicacion != 'USR_GRALES')) ");
				else
					sQuery.append(" AND d.titulo_aplicacion = '" + req.getParameter("gaveta")
							+ "')) ");
			} else {
				sQuery
				.append(" AND  ( (d.titulo_aplicacion = 'USR_GRALES' AND d.id_gabinete = "
						+ u.getIdGabinete()
						+ ") "
						+ "			OR (((d.prioridad >= 2 OR d.prioridad=-1) "
						+ "		 OR d.nombre_usuario='"+u.getNombreUsuario()+"')"
						+ " 	 AND d.titulo_aplicacion != 'USR_GRALES')) ");
			}
		}

		String nombre_documento = req.getParameter("nombre_documento");
		if (nombre_documento!=null && !nombre_documento.isEmpty()) {
			sQuery.append(
					" AND "
					+"("
					+"lower(d.nombre_documento)" + " like '%" + nombre_documento.toLowerCase() + "%'"
					+" OR "
					+"lower(c.nombre_carpeta)" + " like '%" + nombre_documento.toLowerCase() + "%'"
					+")"
			);
		}

		String descripcion = req.getParameter("descripcion");
		if (descripcion!=null && !descripcion.isEmpty()) {
			sQuery.append(" AND " + " lower(descripcion) " + " like '%"
					+ descripcion.toLowerCase() + "%' ");
		}

		SimpleDateFormat sdfParameter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			if(!"null".equals(req.getParameter("fc1"))) {
				Calendar calendarFC1 = Calendar.getInstance();
				calendarFC1.setTime(sdfParameter.parse(req.getParameter("fc1")));
				calendarFC1.set(Calendar.MILLISECOND, 000);
				
				sQuery.append(" AND d.fh_creacion >= ? ");
				addValores(new java.sql.Timestamp(calendarFC1.getTimeInMillis()));
			}
			
			if(!"null".equals(req.getParameter("fc1"))) {
				Calendar calendarFC2 = Calendar.getInstance();
				calendarFC2.setTime(sdfParameter.parse(req.getParameter("fc2")));
				calendarFC2.set(Calendar.MILLISECOND, 999);
				
				sQuery.append(" AND d.fh_creacion <= ? ");
				addValores(new java.sql.Timestamp(calendarFC2.getTimeInMillis()));
			}
		} catch (ParseException pe) {
			log.error(pe, pe);
		}
		try {
			if(!"null".equals(req.getParameter("fc1"))) {
				Calendar calendarFM1 = Calendar.getInstance();
				calendarFM1.setTime(sdfParameter.parse(req.getParameter("fm1")));
				calendarFM1.set(Calendar.MILLISECOND, 000);
				
				sQuery.append(" AND d.fh_modificacion >= ? ");
				addValores(new java.sql.Timestamp(calendarFM1.getTimeInMillis()));
			}
			
			if(!"null".equals(req.getParameter("fc1"))) {
				Calendar calendarFM2 = Calendar.getInstance();
				calendarFM2.setTime(sdfParameter.parse(req.getParameter("fm2")));
				calendarFM2.set(Calendar.MILLISECOND, 999);
				
				sQuery.append(" AND d.fh_modificacion <= ? ");
				addValores(new java.sql.Timestamp(calendarFM2.getTimeInMillis()));
			}	
		} catch (ParseException pe) {
			log.error(pe, pe);
		}

		e = req.getParameterNames();
		boolean isFirst = true;
		while (e.hasMoreElements()) {
			String campo = (String) e.nextElement();
			if (campo.startsWith("attr")) {
				String valor = req.getParameter(campo);

				if (valor == null || "".equals(valor))
					continue;

				if (campo.startsWith("attr_day")) { // dd/mm/aaaa
					String s = campo.substring(campo.indexOf("_") + 4);
					valor = req.getParameter(campo) + "/"
							+ req.getParameter("attr_mes" + s) + "/"
							+ req.getParameter("attr_year" + s);
				} else if (campo.startsWith("attr_mes")
						|| campo.startsWith("attr_year"))
					continue;

				if (isFirst) {
					sQuery
					.append("  AND a.titulo_aplicacion = d.titulo_aplicacion "
							+ " AND a.id_gabinete = d.id_gabinete"
							+ " AND a.id_carpeta_padre = d.id_carpeta_padre"
							+ " AND a.id_documento = d.id_documento ");
				}
				sQuery.append((isFirst ? " AND ( " : " OR ")
						+ "  (a.id_atributo='"
						+ (campo.startsWith("attr_day") ? campo.substring(campo
								.indexOf("_") + 5) : campo.substring(campo
										.indexOf("_") + 1)) + "' "
										+ " AND lower(a.atributo_valor) like '%"
										+ valor.toLowerCase() + "%') ");
				isFirst = false;
			}
		}
		if (withAttributes)
			sQuery.append(" ) ");
		
		return sQuery;
	}

//	private boolean parametroNulo(String parametro) {}
	
	private String[][] getData(ITree tree) {
		String titulo_aplicacion = "Gavetas";
		if(!tree.getRoot().getType().startsWith("gaveta")){
			Carpeta c = (Carpeta)tree.getRoot().getObject();
			titulo_aplicacion = c.getTituloAplicacion();
		}
		
		String[][]  data = new String[resultados.size()][5];
  	          	ArrayList<Object[]> list = resultados;
  	          	int row = -1;
  	          	Iterator<Object[]> it = list.iterator();
  		while(it.hasNext()){
  					row++;
  	          		Object obj[] = it.next();
  	          		String select = "";
  	          		try {
  	          			select = ArbolManager.generaIdNodeDocumento(
  	          				(String)obj[4],
  	          				new Integer(obj[5].toString()).intValue(),
  	          				new Integer(obj[6].toString()).intValue(),	          			
  	          				new Integer(obj[7].toString()).intValue()
  	          			);
  	          		} catch (Exception e) {
  	          				log.error(e, e);
  	          		}
  	          		//String liga = "onClick=\"window.open('getexpedient?select="+select+"&id_gabinete="+obj[5]+"','left');\"";
  	          		String liga = "../getexpedient?fromBusqueda=si"+("Gavetas".equalsIgnoreCase(titulo_aplicacion)?"&cambio=si":"")+"&select="+obj[4]+"&id_gabinete="+obj[5]+"&toSelect="+select;
  	          		data[row][0]="<a href=\""+liga+"\" target=\"left\">"+obj[0]+"</a>";
					data[row][1]=(String)(obj[1]!=null && !"".equals((String)obj[1]) ? obj[1] : "&nbsp;");
					data[row][2]=(String)(obj[2]!=null ? obj[2].toString() : "&nbsp;");
					data[row][3]=(String)(obj[3]!=null ? obj[3].toString() : "&nbsp;");
					if(tree.getRoot().getType().startsWith("gaveta"))
						data[row][4]=(String)(obj[4]!=null ? ("USR_GRALES".equals(obj[4])?"Mis documentos":obj[4]) : "&nbsp;");
					else
						data[row][4]="-";
			}
		return data;
	}

	private String[] getHeaders() {
		String[] headers = new String[5];
		headers[0]="{ 'sTitle': '"+"Nombre"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[1]="{ 'sTitle': '"+"Descripción"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[2]="{ 'sTitle': '"+"Fecha de Creación"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[3]="{ 'sTitle': '"+"Fecha de Modificación"+"', 'sClass': 'left','fnRender':fn_default }";
		headers[4]="{ 'sTitle': '"+"Gaveta"+"', 'sClass': 'left','fnRender':fn_default }";
		return headers;
	}

	private void GenerarJSON(HttpServletRequest req,HttpServletResponse resp,PrintWriter out, String[][] data, String[] headers) 
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
