<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Catalogos</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Aplicacion:Extjs -->
<script src="../js/catalogos.js" type="text/JavaScript"></script>
<link rel="stylesheet" type="text/css" href="../css/catalogos.css" />
<!-- /Aplicacion:Extjs -->
 <%
 String NombreCatalogo;
 if (request.getParameter("NombreCatalogo") != null //cambiar por action
	&& request.getParameter("actualizar")!="") {
	 NombreCatalogo = request.getParameter("NombreCatalogo");
}
 else{
	 NombreCatalogo="";
 }
 
 
 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  
<!-- SERVLET -->
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet = rutaServlet + '/Admin/OperacionesServlet';
var NombreCatalogo = '<%=NombreCatalogo%>';
</script>
</head>
<body>

</body>
</html>