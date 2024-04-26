<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
    <%@ page import="java.util.*,javax.naming.*,java.sql.*,com.syc.user.*,com.syc.imaxfile.*,com.jenkov.prizetags.tree.itf.*,com.syc.tree.*,com.syc.utils.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Busca Documento</title>
<%
String titulo_aplicacion = "Gavetas";
ITree tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);
if(!tree.getRoot().getType().startsWith("gaveta")){
	//Carpeta c = (Carpeta)tree.getRoot().getObject();
	titulo_aplicacion = "Mis Documentos";//c.getTituloAplicacion();
}
else{
	Iterator<?> it = tree.getSelectedNodes().iterator();
	if(it.hasNext()){
		ITreeNode n = (ITreeNode)it.next();
		titulo_aplicacion = (n != null)? n.getId(): tree.getRoot().getId();
	}
}
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" />
<script type="text/javascript" src="<%=Config.ForExt%>"></script>
<script type="text/javascript" src="<%=Config.ForLo%>"></script>
<!-- /Framework:Extjs -->
<script type="text/javascript">
var titulo_aplicacion='<%=titulo_aplicacion%>';
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
rutaServlet = rutaServlet + '/TipoDocumentoServlet';
//rutaServlet='../jsonPrueba';
</script>
<!-- Aplicacion:Extjs -->
<script src="../js/generaForm.js" type="text/JavaScript"></script>
<link rel="stylesheet" type="text/css" href="../css/BuscaDocumento.css" />
<script type="text/javascript" src="../js/BuscaDocumento.js"></script>
<!-- /Aplicacion:Extjs -->
</head>
<body>

</body>
</html>