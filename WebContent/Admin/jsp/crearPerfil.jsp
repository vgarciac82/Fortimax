<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Perfiles</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
    <script type="text/javascript" src="<%=Config.AdminUx%>Exportar.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/crearPerfil.css" />
<script type="text/javascript" src="../js/crearPerfil.js"></script>
<!-- /Aplicacion:Extjs -->
 <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Boolean actualizar=request.getParameter("actualizar")!=null?Boolean.parseBoolean(request.getParameter("actualizar")):false;
String perfil=request.getParameter("nombre")!=null?request.getParameter("nombre"):"";
actualizar=perfil.isEmpty()?false:actualizar;
%>
</head>
<body>
<script type="text/javascript">
var basePath='<%=basePath%>';
var rutaServlet=basePath+	'/Admin/OperacionesServlet';
var actualizar=<%=actualizar%>;
var perfil='<%=perfil%>';
</script>


</body>
</html>