<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
    <%@page import="org.apache.log4j.Logger"%>
    <%@page import="java.util.*,com.jenkov.prizetags.tree.itf.*,com.syc.utils.*,com.syc.imaxfile.*,com.syc.tree.*,com.syc.user.*,com.syc.fortimax.hibernate.managers.*,com.syc.fortimax.hibernate.entities.*" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Visualizador</title>
<script type="text/javascript" src="../js/CookieManager.js" charset="utf-8"></script>
<%
String selectId = request.getParameter("select");
String pagina = request.getParameter("pagina");

//ITree tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);
//Documento documento = new DocumentoManager().selectDocumento(selectId);
//tree.findNode(selectId).setObject(documento);
//session.setAttribute(ParametersInterface.TREE_KEY, tree);

//Ruta servlet
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
Boolean refreshtree=request.getParameter("refreshtree")!=null?Boolean.parseBoolean(request.getParameter("refreshtree")):false;
String rutaArbol = (String)session.getAttribute("rutaArbol");
Boolean visualizadorCompartido=request.getParameter("compartido")!=null?Boolean.parseBoolean(request.getParameter("compartido")):false;

if (session.getAttribute(ParametersInterface.USER_KEY) == null && !visualizadorCompartido) {
	response.sendRedirect("../index.jsp");
	return;
}
List<imx_pagina> imx_paginas = (List<imx_pagina>)new imx_pagina_manager().select(selectId).setMaxResults(1).list();
imx_documento imx_documento = new imx_documento_manager().select(selectId).uniqueResult();

//TODO: VALIDACION QUE CAMBIA A -1 EL TIPO DE DOC SI ESTAN VACIOS. ESTO POR QUE DESDE UN LADO (AL PARECER DESDE UN WS)
//SE CREAN DOCS NUEVOS DEL TIPO -1. ESO PROVOCA ERRORES EN INTERFAZ
if (imx_paginas.size() == 0 && imx_documento.getIdTipoDocumento() != -1) {
	imx_documento.setIdTipoDocumento(-1);
		Logger log = Logger.getLogger(this.getClass());
	try {
		log.info("FIXING. Cambiando el tipo de documento a SIN_TIPO.");
		new imx_documento_manager().update(imx_documento);
		
	} catch (Exception e) {
		log.error(e, e);
	}
}

Boolean externo = (imx_documento.getIdTipoDocumento() == Documento.EXTERNO);

String extension = imx_paginas.size()>0?imx_paginas.get(0).getPageExtension():"";
String nombreOriginal = imx_documento.getNombreDocumento()+extension;
GetDatosNodo gdn = new GetDatosNodo(selectId);
UsuarioPermisos privilegios =  new UsuarioPermisos(u, gdn.getGaveta());
String jsonPrivilegios = new Json(privilegios).returnJson();

%>
<!-- /Aplicacion:Extjs -->
<script type="text/javascript">
var jsonPrivilegios = '<%=jsonPrivilegios%>';
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
var rutaServletA = "<%=basePath%>";
var rutaServletP = "<%=basePath%>";
var rutaServletD = "<%=basePath%>";
rutaServletA = rutaServletA + '/Admin/OperacionesServlet';
rutaServlet = rutaServlet + '/TipoDocumentoServlet';
rutaServletP = rutaServletP + '/Privilegios';
rutaServletD = rutaServletD + '/delpagekeeper';
var nombreOriginal = "<%=nombreOriginal%>";
/*
 * Variables atributos de documento
 */
var rutaServletAtributos = rutaServletA;
var mostrarAtributos=true;
var mostrarHistorico=true;
var editarAtributos=true;
/*
 * 
 */
var select="<%=selectId%>";
var pagina=<%=pagina%>;
var refreshtree=<%=refreshtree%>
var rutaArbol="<%=rutaArbol%>";
var visualizadorCompartido=<%=visualizadorCompartido%>;
var externo=<%=externo%>;

//Variables de Ventana de agregar imagen
var rutaAddPageKeeper = basePath + '/addpagekeeper';
</script>
<%
	int v=0;
	if(Config.VersionVisualizador.equals("auto"))
		v=0;
	else if(Config.VersionVisualizador.equals("1.0"))
		v=1;
	else if(Config.VersionVisualizador.equals("2.0"))
		v=2;
	else
		v=0;
%>

<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" /> 
<script type="text/javascript" src="<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=Config.ForLo%>" charset="utf-8"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<script type="text/javascript" src="<%=Config.ForUx%>Exportar.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->

<script src="../js/generaForm.js" type="text/JavaScript"></script>
<link rel="stylesheet" type="text/css" href="../css/Visualizador.css" />
<script type="text/javascript" src="../js/Visualizador.js" charset="utf-8"></script>


</head>
<body>

</body>
</html>