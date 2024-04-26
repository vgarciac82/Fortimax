<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.syc.user.*,com.syc.utils.*" %>
<%@ page import="com.syc.fortimax.config.Config"%>
<%String idNode = request.getParameter("select");
Usuario u = null;
if ((u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY)) == null) 
{ 
	response.sendRedirect("../index.jsp");
}
if (idNode == null) {
	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
	return;
}

String component =
	request.getScheme()
		+ "://"
		+ request.getServerName()
		+ ":"
		+ request.getServerPort()
		+ request.getContextPath()
		+ "/addpagekeeper"
		+ ";jsessionid="
		+ session.getId();

%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Escanear documentos</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS%>" />
<script type="text/javascript" src="<%=Config.ForExt%>" ></script>
<script type="text/javascript" src="<%=Config.ForLo%>" ></script>
<!-- /Framework:Extjs -->
<!-- Extras:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForUx%>example.css"/>
<script type="text/javascript" src="<%=Config.ForUx%>examples.js"></script>
<!-- Extras:Extjs -->
<script type="text/javascript" src="../js/Scanner.js" charset="utf-8"></script>
<link type="text/css" href="../css/Scanner.css" rel="stylesheet">
<script src=
  "https://www.java.com/js/deployJava.js"></script>
<script>
	var token = "<%=session.getId()%>";
	var idnode = "<%=idNode%>";
	var component = "<%=component %>";
	var attributes = {
		id : 'scannerAppletID',
		code : 'com.syc.client.ScanApplet.class',
		codebase : '../jars/scanner/',
		archive : 'morena_6.jar,morena_osx.jar,morena_windows.jar,morena_license.jar,scanner.jar,jai_codec.jar,jai_core.jar,jai_imageio.jar,mlibwrapper_jai.jar,clibwrapper_jiio.jar,commons-codec-1.5.jar',
		width : 1,
		height : 1
	};
	var parameters = {
		pluginspage : 'http://java.com/download/',
		token : token,
		idnode : idnode,
		tableid : 'xxx-id', //?
		addfunc : 'addRow',
		delfunc : 'delRow',
		updateImgfunc: 'updateImage',
		component : component, //?
		qryString : 'close=true',
		tiffMode : 'true'
	};
	deployJava.runApplet(attributes, parameters, '1.6');
</script>
<script type="text/javascript">
	function addRow(tableID, imageSRC, imageWidth, imageHeight) {
		scannerAppExtjs.addRow(tableID, imageSRC, imageWidth, imageHeight);
	}
	function updateImage(imgIdx, imgSRC, imgWidth, imgHeight) {
		scannerAppExtjs.updateImage(imgIdx, imgSRC, imgWidth, imgHeight);
	}
	function delRow(arg1) {
		return scannerAppExtjs.delRow();
	}
	function getRowDeleted() {
		return scannerAppExtjs.getRowDeleted();
	}
	function getIndexImgSelected(){
		return scannerAppExtjs.getIndexImgSelected();
	}
</script>
</head>
<body>
</body>
</html>
