<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Catalogo</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
    <script type="text/javascript" src="<%=Config.AdminUx%>Exportar.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<script type="text/javascript" src="../js/Version.js"></script>
<link rel="stylesheet" type="text/css" href="../css/Version.css" />
<!-- /Aplicacion:Extjs -->
 <%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>  

</head>

<body>
<script type="text/javascript">

//Variables globales, ha ser inicializadas por la implementacion
var context_title = 'Versiones'; 				//Texto del contexto del grid pj. "Gavetas" , "Usuarios" etc.
var basePath = "<%=basePath%>";			//Ruta base para del aplicativo
var rutaServlet = 						basePath +'/SysPropListServlet';

</script>


</body>
</html>