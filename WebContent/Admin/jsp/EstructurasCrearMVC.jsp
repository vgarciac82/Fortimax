<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Crear estructuras</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<link type="text/css" rel="stylesheet" href="../css/EstructurasCrear.css" />
<script type="text/JavaScript" src="../js/MVC/EstructurasMVC.js"></script>
<%
	boolean actualizar = false;
	String estructura = "";
	actualizar = Boolean.parseBoolean(request.getParameter("actualizar"));
	if (actualizar)
		estructura = request.getParameter("nombre_estru");
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" 
			+ request.getServerPort()
			+ request.getContextPath() + "/";
%>
<script type="text/javascript">
var basePath = "<%=basePath%>";
var operacionesServlet = basePath + '/Admin/OperacionesServlet'; 
var actualizar=<%=actualizar%>;
var estructura="<%=estructura%>";
</script>
</head>
<body>

</body>
</html>