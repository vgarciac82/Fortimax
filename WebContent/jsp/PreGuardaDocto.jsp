<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
        <%@page import="java.util.*,com.syc.utils.*,com.syc.imaxfile.*,com.syc.user.*" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Guardar</title>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String select=request.getParameter("select")!=null?request.getParameter("select"):"";
Boolean nuevo=request.getParameter("nuevo")!=null?Boolean.parseBoolean(request.getParameter("nuevo")):true;
Boolean editable=request.getParameter("editable")!=null?Boolean.parseBoolean(request.getParameter("editable")):false;

Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
GetDatosNodo gdn = new GetDatosNodo(select);
UsuarioPermisos usuarioPermisos = new UsuarioPermisos(u,gdn.getGaveta());
String jsonPrivilegios = new Json(usuarioPermisos).returnJson();
%>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS%>" />
<script type="text/javascript" src="<%=Config.ForExt%>" ></script>
<script type="text/javascript" src="<%=Config.ForLo%>" ></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<!-- Extras:Extjs -->
<script type="text/javascript">
var jsonPrivilegios = '<%=jsonPrivilegios%>';
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
var select='<%=select%>';
var nuevo=<%=nuevo%>;
var editable=<%=editable%>;
rutaServlet = rutaServlet + '/TipoDocumentoServlet';
var rutaServletAgregar = basePath + '/addimaxkeeper';
</script>
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/PreGuardaDocto.css" />
<script type="text/javascript" src="../js/PreGuardaDocto.js" charset="utf-8"></script>
<!-- /Aplicacion:Extjs -->
</head>
<body>
</body>
</html>