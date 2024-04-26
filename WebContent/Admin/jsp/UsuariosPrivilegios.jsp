<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Administrador de Privilegios</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminUx%>example.css"/>
<script type="text/javascript" src="<%=Config.AdminUx%>examples.js"></script>
<!-- Extras:Extjs -->
<script type="text/javascript" src="<%=Config.AdminUx%>RowExpander.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/UsuariosPrivilegios.css" />
<script src="../js/UsuariosPrivilegios.js" type="text/JavaScript"></script>
<!-- /Aplicacion:Extjs -->

 <%
 String Usuario;
 if (request.getParameter("nombre_usuario") != null) {
	 Usuario = request.getParameter("nombre_usuario");
}
 else{
	 Usuario="";
 }
 String aGrupo;
 if (request.getParameter("aGrupo") != null) {
	 aGrupo = request.getParameter("aGrupo");
}
 else{
	 aGrupo="";
 }
 String Grupo;
 if (request.getParameter("Grupo") != null) {
	 Grupo = request.getParameter("Grupo");
	 Usuario=Grupo;
}
 else{
	 Grupo="";
 }
 Boolean LDAP=false;
 if(request.getParameter("LDAP")!=null){
	 LDAP=Boolean.valueOf(request.getParameter("LDAP"));
 }
 
 
 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>  
<!-- SERVLET -->
<script type="text/javascript">
var rutaServlet = "<%=basePath%>";			//Ruta base para del aplicativo
rutaServlet = rutaServlet + '/Admin/OperacionesServlet';
var Usuario = '<%=Usuario%>';
var Grupo = '<%=Grupo%>';
var aGrupo = '<%=aGrupo%>';
var LDAP=<%=LDAP%>;
</script>

</head>
<body>

</body>
</html>