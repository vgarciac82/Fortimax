<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%
session.removeAttribute("isFirstFromUploadCarpetaApplet");

String idNode = request.getParameter("select");
if (idNode == null) {
	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
	return;
}

String ses = session.getId();

String url =
	request.getScheme()
		+ "://"
		+ request.getServerName()
		+ ":"
		+ request.getServerPort()
		+ request.getContextPath()
		+ "/addfolderkeeper"	
		+ "?select="
		+ idNode;
url = response.encodeURL(url);//este es VITAL en WAS, previa activacion del encode en opciones de session
if(url.indexOf("jsessionid")!=-1){
	ses = url.substring(url.indexOf(";jsessionid"));
	ses = ses.substring(ses.indexOf("=")+1);
	if(ses.indexOf("?")!=-1){
		ses = ses.substring(0,ses.indexOf("?"));
	}
} else{
	url =
		request.getScheme()
			+ "://"
			+ request.getServerName()
			+ ":"
			+ request.getServerPort()
			+ request.getContextPath()
			+ "/addfolderkeeper"
			+ ";jsessionid="
			+ session.getId()	
			+ "?select="
			+ idNode;
}

%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Respaldar Carpeta</title>
<link href="../css/fortimax_sistema.css" rel="stylesheet" type="text/css">
</head>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" id="texto">
	<tr>
		<td width="90%" align="center" class="bordetit">&nbsp;&nbsp;Respaldar Carpeta</td>
		<td width="10%" align="right" class="bordetit">
			<a href="javascript:window.close();">
				<img src="../imagenes/b_cerrar.gif" alt="Cerrar ventana" width="17" height="13" border="0">
			</a>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center"><img src="../imagenes/espacio.gif" width="1" height="5"></td>
	</tr>
	<tr>
		<td align="center"><strong>ADVERTENCIA:</strong> Las carpetas protegidas que no hayan sido abiertas previamente, no serán actualizadas.</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
		<div id="divFileUpload" style="width: 100%; height: 360; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
		<table id="tableFile" width="100%" align="center" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="center" width="100%" height="360">
					<jsp:plugin type="applet" name="clientupload" width="100%" height="100%" codebase="../jars/jclientupload/" archive="jclientupload.jar,chttpclient.jar,clogging.jar" code="javazoom.upload.client.MApplet">
						<jsp:params>
							<jsp:param name="pluginspage" value="http://java.com/download/" />
							<jsp:param name="scriptable" value="true" />
							<jsp:param name="url" value="<%=url%>" />
							<jsp:param name="paramfile" value="uploadfile" />
							<jsp:param name="mode" value="http" />
							<jsp:param name="folderdepth" value="-1" />
							<jsp:param name="param1" value="relativefilename" />
							<jsp:param name="value1" value="true" />
							<jsp:param name="cookiename" value="jsessionid" />
							<jsp:param name="cookievalue" value="<%=ses%>" />
							<jsp:param name="chunksize" value="268435456" />
							<jsp:param name="forward" value="Messages.jsp?applet=true" />
							<jsp:param name="forwardtarget" value="_self" />
						</jsp:params>
						<jsp:fallback>Su Browser no soporta Applets!</jsp:fallback>
					</jsp:plugin>
				</td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
</table>
</body>
</html>
