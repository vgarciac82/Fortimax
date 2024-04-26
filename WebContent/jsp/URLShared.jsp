<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
    <%@page import="java.util.*,com.jenkov.prizetags.tree.itf.*,com.syc.utils.*,com.syc.imaxfile.*,com.syc.tree.*,com.syc.user.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Share Document</title>
<%
		String selectId = request.getParameter("select");
//Ruta servlet
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" />
<script type="text/javascript" src="<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=Config.ForLo%>" charset="utf-8"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/URLShared.css" />
<script type="text/javascript" src="../js/URLShared.js" charset="utf-8"></script>
<!-- /Aplicacion:Extjs -->
<script type="text/javascript">
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
rutaServlet = rutaServlet + '/Admin/OperacionesServlet';

var rutaServletCompartirDoc = basePath + '/documentshared';


var select="<%=selectId%>";
</script>
</head>
<body>

</body>
</html>