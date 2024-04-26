<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Asignar perfiles</title>
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
<link rel="stylesheet" type="text/css" href="../css/AsignaPerfiles.css" />
<script type="text/javascript" src="../js/AsignaPerfiles.js"></script>
<!-- /Aplicacion:Extjs -->
 <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String perfil=request.getParameter("nombre")!=null?request.getParameter("nombre"):"";
%>
</head>
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet=rutaServlet + '/Admin/OperacionesServlet';
var rutaServletDisponibles = '../../jsonPrueba/usuariosDisponiblesPerfiles.json';//rutaServlet + '/Admin/OperacionesServlet';
var rutaServletAsignados = '../../jsonPrueba/usuariosAsignadosPerfiles.json';
var nombrePerfil='<%=perfil%>';
</script>
<body>

</body>
</html>