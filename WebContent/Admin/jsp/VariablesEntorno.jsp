<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Variables de Entorno</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminUx%>CheckHeader.css" />
<script type="text/javascript" src="<%=Config.AdminUx%>RowExpander.js"></script>
<script type="text/javascript" src="<%=Config.AdminUx%>CheckColumn.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/VariablesEntorno.css" />
<script type="text/javascript" src="../js/VariablesEntorno.js"></script>
<!-- /Aplicacion:Extjs -->
 <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  

<base href="<%=basePath%>">
</head>

<body>
<script type="text/javascript">
<% if(request.getParameter("action")==null){
	%>var action = 'getVariablesEntorno';<%
}
else{
	%>var action = '<%=request.getParameter("action")%>';<%
}
		%>

var select = '<%=request.getParameter("select")%>';

//Variables globales, ha ser inicializadas por la implementacion
var context_title = 'Configuracion'; 				//Texto del contexto del grid pj. "Gavetas" , "Usuarios" etc.
var display_action = 'Create';				//Create or Update
var base_path = "<%=basePath%>";			//Ruta base para del aplicativo
base_path = base_path + '/Admin/OperacionesServlet';


</script>
<div id="editor-grid"></div>
</body>
</html>