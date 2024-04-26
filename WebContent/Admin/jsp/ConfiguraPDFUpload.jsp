<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Configura PDF Upload</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<script type="text/javascript" src="<%=Config.AdminUx%>BannedComboItems.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<script src="../js/ConfiguraPDFUpload.js" type="text/JavaScript"></script>
<link rel="stylesheet" type="text/css" href="../css/ConfiguraPDFUpload.css" />
<!-- /Aplicacion:Extjs -->
 <% 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet = rutaServlet + '/Admin/OperacionesServlet';
</script>
</head>
<body>

</body>
</html>