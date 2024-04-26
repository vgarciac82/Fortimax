<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="com.syc.fortimax.config.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Imprimir</title>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//select=USR_GRALES_G1C1D2&page_actual=0
String select=request.getParameter("select")!=null?request.getParameter("select"):"";
int pagina=request.getParameter("page_actual")!=null?(Integer.parseInt(request.getParameter("page_actual"))+1):1;
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
var basePath = "<%=basePath%>";
var rutaServlet = "<%=basePath%>";
var select='<%=select%>';
var pagina='<%=pagina%>';
rutaServlet = rutaServlet + 'Admin/OperacionesServlet';
</script>
<!-- Aplicacion:Extjs -->
<script type="text/javascript" src="../js/CookieManager.js" charset="utf-8"></script>
<link rel="stylesheet" type="text/css" href="../css/Impresora.css" />
<script type="text/javascript" src="../js/Impresora.js" charset="utf-8"></script>
<!-- /Aplicacion:Extjs -->
<script type="text/javascript" src="../jars/qz-print/js/deployJava.js"></script>
<script type="text/javascript">
	/**
	* Optionally used to deploy multiple versions of the applet for mixed
	* environments.  Oracle uses document.write(), which puts the applet at the
	* top of the page, bumping all HTML content down.
	*/
	deployQZ();
	
	/**
	* Deploys different versions of the applet depending on Java version.
	* Useful for removing warning dialogs for Java 6.  This function is optional
	* however, if used, should replace the <applet> method.  Needed to address 
	* MANIFEST.MF TrustedLibrary=true discrepency between JRE6 and JRE7.
	*/
	function deployQZ() {
		var attributes = {
				id: "qz", 
				code:'qz.PrintApplet.class',
				archive:'../jars/qz-print/qz-print.jar',
				width:1, 
				height:1
		};
		var parameters = {
			jnlp_href: '../jars/qz-print/qz-print_jnlp.jnlp', 
			cache_option:'plugin',
			disable_logging:'false', 
			initial_focus:'false'
		};
		if (deployJava.versionCheck("1.7+") == true) {
		} else if (deployJava.versionCheck("1.6+") == true) {
			attributes['archive']+=',../jars/qz-print/lib/jssc_qz.jar,../jars/qz-print/lib/pdf-renderer_qz.jar';
			delete parameters['jnlp_href'];
		}
		deployJava.runApplet(attributes, parameters, '1.5');
	}
</script>
</head>
<body>
</body>
</html>