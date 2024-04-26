<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.apache.commons.lang.StringUtils"%>
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String idNode = request.getParameter("select");
	if (idNode == null) {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST,
				"Sin nodo seleccionado");
		return;
	}

	//VGC Agregado, si se envia como parametro el nombre del documento, entonces este sera no editable.
	String documentName = (String) session
			.getAttribute("document.name");
	session.removeAttribute("document.name");
	//VGC Agregado, permite manejar la respuesta para iFImax.
	boolean fromIFImax = "true".equals(request.getParameter("ifimax"));
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../css/fortimax_sistema.css"
	type="text/css">
<title>Guardar archivo</title>
<script language="JavaScript">
var docNameEditable = <%=String.valueOf(StringUtils.stripToNull(documentName) == null)%>;
	function getName() {
		if (docNameEditable == true) {
			var a = document.forms[0].archivo.value;
			var d = document.forms[0].docName;
			d.value = a.substring(a.lastIndexOf("\\") + 1, a.lastIndexOf("."))
					.substring(0, 32);
		}
	}

	function valName() {
		var a = document.forms[0].archivo;
		var d = document.forms[0].docName;

		if (isWhitespace(a.value)) {
			alert("El 'Archivo' no debe estar vacio.");
			a.focus();
		} else if (isWhitespace(d.value)) {
			alert("El 'Nombre del Documento' no debe estar vacio.");
			d.focus();
		} else
			return true;
		//return confirm("Se creará el documento con los datos proporcionados. ¿Continuar?");

		return false;
	}

	function isWhitespace(s) {
		var whitespace = " \t\n\r";
		if ((s == null) || (s.length == 0))
			return true;
		for ( var i = 0; i < s.length; i++) {
			var c = s.charAt(i);
			if (whitespace.indexOf(c) == -1)
				return false;
		}
		return
true;
}
</script>
<style type="text/css">
<!--
.tip {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 9px;
	color: #666666;
}
-->
</style>
</head>
<body>
	<p>&nbsp;</p>
	<table border="0" cellspacing="0" cellpadding="0" align="center"
		class="bordes">
		<tr>
			<td>
				<form name="upload" method="post" enctype="multipart/form-data"
					action="../addimaxkeeper?select=<%=idNode%>&ifimax=<%=fromIFImax%>">
					<table width="100%" border="0" cellspacing="0" cellpadding="0"
						id="texto">
						<tr>
							<td colspan="2" class="bordetit">Respaldar archivo</td>
						</tr>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="2">Archivo:&nbsp; <input name="archivo"
								type="file" size="50" maxlength="256" onChange="getName()">
							</td>
						</tr>
						<tr>
							<td>Nombre del archivo:&nbsp;</td>
							<td><input type="text" name="docName" size="32"
								maxlength="32"
								value="<%=(StringUtils.stripToNull(documentName) == null
					? ""
					: documentName)%>"
								<%=(StringUtils.stripToNull(documentName) == null
					? ""
					: " readonly=\"readonly\"")%>>
								<span class="tip">máximo 32 caracteres</span></td>
						</tr>
						<tr>
							<td>Descripción del archivo:&nbsp;</td>
							<td><textarea cols="35" rows="3" name="docDesc" id="texto"></textarea>
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td><input type="submit" name="boton"
								value="Respaldar archivo" onClick="return valName()">
							</td>
						</tr>
					</table>
				</form></td>
		</tr>
	</table>
</body>
</html>
