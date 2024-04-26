<%@page import="com.syc.fortimax.config.Config"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Ldap</title>
    <!-- Framework:Extjs -->
	<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
    <script type="text/javascript" src="<%=Config.AdminExt%>"></script>
    <script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
    <script type="text/javascript" src="<%=Config.AdminUx%>Exportar.js"></script>
<!-- /Extras:Extjs -->
<!-- Aplicacion:Extjs -->
	<link href="../css/GruposLDAP.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="../js/GruposLDAP.js"></script>  
<!-- /Aplicacion:Extjs --> 
 <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>   
<script type="text/javascript">
var rutaServlet='<%=basePath%>'+'/LDAPServlet';
rutaServlet='../js/gruposLDAP/grupos2.json';
var basePath='<%=basePath%>';
</script>
</head>
<body></body>
</html>