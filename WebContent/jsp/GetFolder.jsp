<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="expires" content="0">
	
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@page import="com.syc.fortimax.config.Config"%>
<%@page import="com.syc.fortimax.hibernate.managers.imx_documento_manager"%>
<%@page import="com.syc.fortimax.hibernate.entities.imx_documento"%>
<%@page import="com.syc.imaxfile.Documento"%>
<%@page import="com.syc.imaxfile.GetDatosNodo"%>
<%
String idNode = request.getParameter("select");
if (idNode == null) {
	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
	return;
}
int tipo = -1;
GetDatosNodo gdn = new GetDatosNodo(idNode);
if(gdn.isDocumento())
	tipo = new imx_documento_manager().select(idNode).uniqueResult().getIdTipoDocumento();

//Ruta servlet
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Exportar</title>
<link href="../css/fortimax_sistema.css" rel="stylesheet" type="text/css">
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" />
<script type="text/javascript" src="<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=Config.ForLo%>" charset="utf-8"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/GetFolder.css" />
<script type="text/javascript" src="../js/GetFolder.js" charset="utf-8"></script>
<!-- /Aplicacion:Extjs -->
<script type="text/javascript">
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
var tipo = <%=tipo%>;
var idNode = '<%=idNode%>';
</script>
</head>
<body>

</body>
</html>