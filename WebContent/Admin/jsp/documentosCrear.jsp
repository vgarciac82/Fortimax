<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Documento</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- Framework:Extjs -->
<!-- Extras:Extjs -->
<!-- Extras:Extjs -->
<script type="text/javascript" src="<%=Config.AdminUx%>CheckColumn.js"></script>
<link rel="stylesheet" type="text/css" href="<%=Config.AdminUx%>CheckHeader.css" />
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/documentosCrear.css" />
<script type="text/javascript" src="../js/documentosCrear.js"></script>
<!-- /Aplicacion:Extjs -->
<!-- Este JS no deberia tener que ser modificado directamente.  -->

 <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  

<base href="<%=basePath%>">
</head>

<body>
<script type="text/javascript">

var action = '<%=request.getParameter("action")%>';
var select = '<%=request.getParameter("select")%>';

//Variables globales, ha ser inicializadas por la implementacion
var context_title = 'Plantilla'; 				//Texto del contexto del grid pj. "Gavetas" , "Usuarios" etc.
var display_action = 'Create';				//Create or Update
var base_path = "<%=basePath%>";			//Ruta base para del aplicativo
base_path = base_path + '/Admin/OperacionesServlet';


</script>
<div id="editor-grid"></div>
</body>
</html>