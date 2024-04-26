<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Control Gaveta/Estructura</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminUx%>example.css"/>
<script type="text/javascript" src="<%=Config.AdminUx%>examples.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<script src="../js/EstGavetas.js" type="text/JavaScript"></script>
<link rel="stylesheet" type="text/css" href="../css/EstGavetas.css" /> 
<!-- /Aplicacion:Extjs -->
<% 

 String estructura="";
 if (request.getParameter("nombre_estru") != null) 
{
	 estructura =request.getParameter("nombre_estru");
	  
}
 else{
	 estructura="";
 } 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet = rutaServlet + '/Admin/OperacionesServlet';
var base_path = "<%=basePath%>"; 
var estructura="<%=estructura%>";
</script>
</head>
<body>

</body>
</html>