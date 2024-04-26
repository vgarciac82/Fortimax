<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
    <%@page import="com.syc.user.*"%>
    <%@page import="com.syc.utils.*"%>
    <%@page import="java.util.Enumeration"%>
    <%@page import="java.util.Map"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Busqueda</title>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String select=request.getParameter("select")!=null?request.getParameter("select"):"";
String tipoAccion=request.getParameter("tipoAccion")!=null?request.getParameter("tipoAccion"):"expedientes";
String jsonBusqueda=request.getParameter("jsonBusqueda")!=null?request.getParameter("jsonBusqueda"):"";
Usuario u = null;
u=(Usuario) session.getAttribute(ParametersInterface.USER_KEY);

UsuarioPermisos privilegios =  new UsuarioPermisos(u, select);
Boolean eliminar=privilegios.isEliminar();
Boolean modificar=privilegios.isModificar();
Boolean crear=privilegios.isCrear();
%>
<!-- Framework:Extjs -->
<!--<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS%>" /> -->
<script type="text/javascript" src="<%=Config.ForExt%>" ></script>
<script type="text/javascript" src="<%=Config.ForLo%>" ></script>
 <link rel="stylesheet" type="text/css" href="../resources/css/fortimax_blue.css" /> 
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<script type="text/javascript" src="<%=Config.ForUx%>Exportar.js"></script>
<script type="text/javascript" src="<%=Config.ForUx%>RowExpander.js"></script>
<!-- Extras:Extjs -->
<script type="text/javascript">
var basePath = "<%=basePath%>";
var select='<%=select%>';
var tipoAccion='<%=tipoAccion%>';
var jsonBusqueda="<%=jsonBusqueda%>";
var rutaServletBusqueda = basePath + 'ExpedienteServlet';
var rutaServletOperaciones = basePath + '/Admin/OperacionesServlet';
var rutaServlet = basePath + '/TipoDocumentoServlet';
var permisoEliminar=<%=eliminar%>;
var permisoModificar=<%=modificar%>;
var permisoCrear=<%=crear%>;
</script>
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/ResultadosBusquedaExpedientes.css" />
<script type="text/javascript" src="../js/fortimax_mvc.js" charset="utf-8"></script>
<!-- /Aplicacion:Extjs -->
</head>
<body>

</body>
</html>