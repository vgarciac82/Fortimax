<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	String nodo = request.getParameter("select");
	String idSesion = session.getId();
	System.out.println(idSesion);
	String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + 
			request.getContextPath() + "/addpagekeeper;jsessionid="+ idSesion + "?select=" + nodo;		
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0"
	marginwidth="0" marginheight="0">
		<table id="tableFile" width="100%" align="center"
			cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td align="center" width="100%" height="283"><jsp:plugin
						type="applet" name="clientupload" width="100%" height="100%"
						codebase="../jars/jclientupload/"
						archive="jclientupload.jar,chttpclient.jar,clogging.jar"
						code="javazoom.upload.client.MApplet" jreversion="1.6">
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
							<jsp:param name="cookievalue" value="123456789" />
							<jsp:param name="chunksize" value="268435456" />
							<jsp:param name="forward" value="Messages.jsp?applet=true" />
							<jsp:param name="forwardtarget" value="_self" />
							<jsp:param name="whitelist"
								value="*.tif,*.tiff,*.bmp,*.gif,*.jpg,*.jpeg,*.png,*.pnm,*.wbmp,*.fpx" />
						</jsp:params>
						<jsp:fallback>Su Browser no soporta Applets!</jsp:fallback>
					</jsp:plugin></td>
			</tr>
		</table>
</body>
</html>