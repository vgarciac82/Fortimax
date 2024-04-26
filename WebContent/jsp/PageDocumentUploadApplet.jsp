<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.syc.imaxfile.GetDatosNodo"%>
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%

	String ifimaxRequest = request.getRequestURL().toString();
	System.out.println("Request Desde: " + ifimaxRequest);

	String idNode = request.getParameter("select");
	if (idNode == null) {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
		return;
	}
	GetDatosNodo gdn = new GetDatosNodo(idNode);
	gdn.separaDatosGabinete();
	String tituloAplicacion = gdn.getGaveta();
	int idGabinete = gdn.getGabinete();
	String urlFortimax = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/addpagekeeper;jsessionid="+session.getId() + "?select=" + idNode + "&norefresh=true";		
	String urlConfig = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/Admin/OperacionesServlet?action=getConfigXML&select="+tituloAplicacion;
	String uploadConfig = "uploader.config";
	String xmlTreePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +"/XmlTree?select="+idNode;
	String 	urlAction = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +"/addpagekeeper;jsessionid="+pageContext.getSession().getId() + "?action=applet&select="+idNode;
	boolean requestFromiFImax = "true".equalsIgnoreCase((String) session.getAttribute("FROM_IFIMAX"));
	boolean requestArbol=request.getParameter("appletExtended")!=null?Boolean.parseBoolean(request.getParameter("appletExtended")):false;	
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Agregar imágenes al documento</title>
<link href="../css/fortimax_sistema.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
var count = 0;
var fromIFImax = <%=requestFromiFImax%>;
var urlFortimax = "<%=urlFortimax%>";
var idNode = "<%=idNode%>";
function init() {
	document.JUpload.js().listenUploadProgress("UploadProgress");
}

function UploadProgress_uploadedFile(client, file) {
    count++;
}

function UploadProgress_uploadingFile(){
}

function UploadProgress_closed(client, succeeded) {
    if (succeeded) {
    	if( fromIFImax){
	        alert("Recepción exitosa de " + count + "pagina(s) en FortImax!!\n" + "Esta ventana se cerrara automaticamente al dar clic en aceptar");
	        self.opener.top.digDoc.location.href=urlPrefix + "/imgmng/VisorDeImagenes.jsp?select="+idNode+"&image.index=-1";
	        self.close();
    	}else{
    		alert("Recepción exitosa de " + count + "pagina(s) en FortImax!!\n" + "Esta ventana se cerrara automaticamente al dar clic en aceptar");
    		self.opener.top.left.location.href=urlFortimax+"/imgmng/ArbolExpediente.jsp?select=" + idNode + "\"";
			self.opener.top.main.location.href= urlFortimax + "/imgmng/VisorDeImagenes.jsp?select=" + idNode + "&image.index=-1";
			self.close();
    	}
    }
    else {
        alert("User cancelled upload.");
    }
}
</script>
</head>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">
	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" id="texto">
		<% if(!requestArbol){ %>
		<tr>
			<td width="100%" align="center" class="bordetit">&nbsp;&nbsp;Agregar imagen</td>
		</tr>
		<% } %>
 		<tr>
			<td colspan="2" align="center"><img src="../imagenes/espacio.gif" width="1" height="5">
			</td>
		</tr>
		<tr>
		<% if(!requestArbol){ %>
            <td colspan="2" align="right">
            	<div id="divFileUpload" style="width: 100%; height: 340; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
                	<table id="tableFile" width="100%" align="center" cellspacing="0" cellpadding="0" border="0">
                    	<tr>
                        	<td align="center" width="100%" height="340">
                            	<jsp:plugin type="applet" name="clientupload" width="100%" height="100%" codebase="../jars/jclientupload/" archive="jclientupload.jar,chttpclient.jar,clogging.jar" code="javazoom.upload.client.MApplet">
									<jsp:params>
										<jsp:param name="pluginspage" value="http://java.com/download/" />
										<jsp:param name="policy" value="ignore" />
	                                    <jsp:param name="scriptable" value="true" />
	                                    <jsp:param name="url" value="<%=url%>" />
	                                    <jsp:param name="paramfile" value="uploadfile" />
	                                    <jsp:param name="param1" value="todo" />
	                                    <jsp:param name="value1" value="upload" />
	                                    <jsp:param name="mode" value="http" />
	                                    <jsp:param name="folderdepth" value="-1" />
	                                    <jsp:param name="param2" value="relativefilename" />
	                                    <jsp:param name="value2" value="true" />
	                                    <jsp:param name="cookiename" value="jsessionid" />
	                                    <jsp:param name="cookievalue" value="<%=session.getId()%>" />
	                                    <jsp:param name="chunksize" value="268435456" />
	                                    <jsp:param name="forward" value="Messages.jsp?applet=true" />
	                                    <jsp:param name="forwardtarget" value="_self" />
	                                    <jsp:param name="whitelist" value="*.tif,*.tiff,*.bmp,*.gif,*.jpg,*.jpeg,*.png,*.pnm,*.wbmp,*.fpx" />
									</jsp:params>
									<jsp:fallback>Su Browser no soporta Applets!</jsp:fallback>
								</jsp:plugin>
                           	</td>
                      	</tr>
           			</table>
                </div>
            </td>
         <% }else{%> 
          		<td colspan="2" align="right">
                	<div id="divFileUpload" style="width: 100%; height: 600; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
                    	<table id="tableFile" width="100%" align="center" cellspacing="0" cellpadding="0" border="0">
                        	<tr>
                            	<td align="center" width="100%" height="600">
                            		<jsp:plugin type="applet" name="fortimaxUploader" width="100%" height="100%" codebase="../jars/fortimaxUploader/" archive="fortimaxUploader.jar" code="com.syc.upload.classic.JUpload">
										<jsp:params>
											<jsp:param name="pluginspage" value="http://java.com/download/" />
											<jsp:param name="Config" value="<%=uploadConfig%>" />
                                            <jsp:param name="EstructuraXMLPath" value="<%=xmlTreePath%>" />
                                            <jsp:param name="ImageManagerXML" value="<%=urlConfig%>" />
                                            <jsp:param name="Upload.URL.Action" value="<%=urlAction%>" />
                                            <jsp:param name="DefaultDir" value="" />
                                            <jsp:param name="mayscript" value="mayscript" />
										</jsp:params>
										<jsp:fallback>Su Browser no soporta Applets!</jsp:fallback>
									</jsp:plugin>
     							</td>
                   			</tr>
						</table>
					</div>   
				</td>                                 
    	 <% } %>
	</table>
</body>
</html>

