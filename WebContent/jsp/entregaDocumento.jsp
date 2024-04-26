<%@page import="com.syc.fortimax.hibernate.managers.imx_documento_manager"%>
<%@page import="com.syc.fortimax.hibernate.entities.imx_documentoextend"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
    <%@page import="java.util.*,java.net.URI,com.jenkov.prizetags.tree.itf.*,com.syc.utils.*,com.syc.imaxfile.*,com.syc.tree.*,com.syc.user.*" %>
    <%@page import="com.syc.fortimax.hibernate.managers.imx_documento_manager"%>
    <%@page import="com.syc.fortimax.hibernate.entities.imx_documento"%> 
    <%@page import="com.syc.fortimax.hibernate.entities.imx_documento_id"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Documento compartido</title>
<%
		String selectId = request.getParameter("select");
//Ruta servlet
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String select="";
String token="";
String tipoDocto="";

if(request.getParameter("t")==null){
	URI uri = new URI(request.getRequestURI());
	String uripath = uri.getPath();
	token = uripath.substring(uripath.lastIndexOf('/') + 1);
} else {
	token=(String)request.getParameter("t");
}
imx_documento imx_documento = imx_documento_manager.selectDocument(token);
if(imx_documento==null) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return ;
} else {
	imx_documento_id imx_documento_id = imx_documento.getId();
	select=imx_documento_id.toString();
	//tipoDocto=(String)request.getParameter("tipodoc");
}
%>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>/dummy/<%=Config.ForCSS %>" />
<script type="text/javascript" src="<%=basePath%>/dummy/<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=basePath%>/dummy/<%=Config.ForLo%>" charset="utf-8"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>/dummy/<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=basePath%>/dummy/<%=Config.ForUx%>examples.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/entregaDocumento.css" />
<script type="text/javascript" src="<%=basePath%>/js/entregaDocumento.js" charset="utf-8"></script>
<!-- /Aplicacion:Extjs -->
<script type="text/javascript">
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
rutaServlet = rutaServlet + '/documentshared';

var select='<%=select%>';
var token='<%=token%>';
var tipoDocto='<%=tipoDocto%>';
</script>
</head>
<body>

</body>
</html>