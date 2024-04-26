<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Bitacora</title>
    <!-- Framework:Extjs -->
	<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
    <script type="text/javascript" src="<%=Config.AdminExt%>"></script>
    <script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
    <script type="text/javascript" src="<%=Config.AdminUx%>Exportar.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
	<link href="../css/MostrarBitacora.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="../js/MostrarBitacora.js"></script>  
<!-- /Aplicacion:Extjs --> 
 <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>   
<script type="text/javascript">
var rutaServlet='<%=basePath%>';

var basePath='<%=basePath%>';
rutaServlet = rutaServlet + '/Admin/OperacionesServlet';
</script>
</head>
<body></body>