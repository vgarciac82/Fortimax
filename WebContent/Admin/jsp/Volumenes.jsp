<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Volumenes</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
 <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  
<!-- SERVLET -->
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet = rutaServlet + '/Admin/VolumenServlet';

</script>
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/Volumenes.css" />
<script src="../../js/md5.js" type="text/JavaScript"></script>
<script src="../js/Volumenes.js" type="text/JavaScript"></script>
<script src="../js/autenticacion.js" type="text/JavaScript"></script>
<!-- /Aplicacion:Extjs -->
</head>
<body>

</body>
</html>