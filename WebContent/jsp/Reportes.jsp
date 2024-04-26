<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Reportes</title>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String select=request.getParameter("select");
Boolean selectValido=request.getParameter("select")!=null?true:false;
%>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" />
<script type="text/javascript" src="<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=Config.ForLo%>" charset="utf-8"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<script type="text/javascript" src="<%=Config.ForUx%>creaObjetoExtjs.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<script src="../js/generaCamposReportes.js" type="text/JavaScript"></script>
<link rel="stylesheet" type="text/css" href="../css/Reportes.css" />
<script type="text/javascript" src="../js/Reportes.js" charset="utf-8"></script>
<!-- /Aplicacion:Extjs -->
<script type="text/javascript">
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
rutaServlet = rutaServlet + '/Admin/ReporteServlet';
var rutaServletOperaciones = basePath + '/Admin/OperacionesServlet';
var rutaServletPlantilla = basePath + '/TipoDocumentoServlet';
var rutaServletReporte = basePath + '/TipoDocumentoServlet';
var select='<%=select%>';
var selectValido=<%=selectValido%>;
</script>
</head>
<body>

</body>
</html>