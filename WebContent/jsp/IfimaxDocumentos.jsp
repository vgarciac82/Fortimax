<!DOCTYPE html>
    <%@page import="com.syc.fortimax.config.Config"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<% 
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<script type="text/javascript">
var basePath = "<%=basePath%>";
var rutaAddPageKeeper = basePath + '/addpagekeeper';
</script>
<title>Ifimax Documentos</title>

<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" />
<script type="text/javascript" src="<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=Config.ForLo%>" charset="utf-8"></script>
<!-- /Framework:Extjs -->

<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/IfimaxDocumentos/ifimaxDocumentos.css" />

<!-- /Aplicacion:Extjs -->

<script type="text/javascript" src="../js/IfimaxDocumentosApp.js"></script>
</head>
<body>
</body>
</html>