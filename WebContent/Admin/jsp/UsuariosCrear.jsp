<%@page import="com.syc.fortimax.config.Config"%>
<%
	String action = "";
	String select = "";
	if (request.getParameter("actualizar") != null //cambiar por action
			&& "true".equals(request.getParameter("actualizar"))) {
		action = "update";
	}
	if (request.getParameter("nombre_usuario") != null) {
		select = request.getParameter("nombre_usuario");//cambiar por select
	}
	
	//RUTA AL SERVIDOR
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Administración de usuarios</title>

<script type="text/javascript">
//Variables globales, a ser inicializadas por la implementacion
var context_title = 'Usuario'; 					//Texto del contexto del grid pj. "Gavetas" , "Usuarios" etc.
var base_path = '<%=basePath%>';				//Ruta base para del aplicativo
var action = '<%=action%>';
var select = '<%=select%>';
var link_opGav_servlet = base_path +'/Admin/OperacionesServlet';
</script>

<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/UsuariosCrear.css" />
<script src="../js/usuariosCrear.js" type="text/JavaScript"></script>
<!-- /Aplicacion:Extjs -->
</head>
<body>
</body>
</html>