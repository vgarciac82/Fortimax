<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Grupos Crear</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/GruposCrear.css" />
<script src="../js/GruposCrear.js" type="text/JavaScript"></script>
<!-- /Aplicacion:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminUx%>example.css"/>
<script type="text/javascript" src="<%=Config.AdminUx%>examples.js"></script>
<!-- /Extras:Extjs -->
 <% 
 boolean actualizar;
 String grupo="";
 if (request.getParameter("actualizar") != null) //cambiar por action
{
	 actualizar =Boolean.parseBoolean(request.getParameter("actualizar"));
	 if(Boolean.parseBoolean(request.getParameter("actualizar"))==true){
		 grupo=request.getParameter("nombre_grupo");
	 }
	 else{
		 grupo="";
	 }
	 
}
 else{
	 actualizar=false;
 }
 
 
 
 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet = rutaServlet + '/Admin/OperacionesServlet';

var base_path = "<%=basePath%>";
var link_get_store = 		rutaServlet; 
var actualizar=<%=actualizar%>;
var grupo="<%=grupo%>";
</script>
</head>
<body>

</body>
</html>