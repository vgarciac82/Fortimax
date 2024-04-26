<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Documentos</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" />
<script type="text/javascript" src="<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=Config.ForLo%>" charset="utf-8"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<!-- /Extras:Extjs -->
<script type="text/javascript">
var personalizado=true;
</script>
<!-- JS de la Interfaz --> 
<script src="../js/generaForm.js" type="text/JavaScript"></script>
<script src="../js/md5.js" type="text/JavaScript"></script>
<script src="../js/CreaCarpetaDocto.js" type="text/JavaScript"></script>

<!-- Llamada a CSS -->
<link rel="stylesheet" type="text/css" href="../css/CreaCarpetaDocto.css" />
<%
 Boolean docto=false;
String select="";
String tipo="";
 if (request.getParameter("docto") != null) {
	 docto = Boolean.parseBoolean(request.getParameter("docto").toString());
	 select=request.getParameter("select");
	 tipo=request.getParameter("tipo");
}

 
 
 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet = rutaServlet + '/TipoDocumentoServlet';
var docto = <%=docto%>;
var matricula = "<%=select%>";
</script>
</head>
<body>

</body>
</html>