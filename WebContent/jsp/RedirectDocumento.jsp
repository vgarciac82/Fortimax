<%@page import="com.syc.fortimax.hibernate.managers.imx_documento_manager"%>
<%@page import="com.syc.fortimax.hibernate.entities.imx_documento"%>
<%@page import="com.syc.imaxfile.Documento"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Redirect</title>
<% 
//TODO: Eliminar este JSP
String selectId = request.getParameter("select");
imx_documento imx_documento = new imx_documento_manager().select(selectId).uniqueResult();

if (imx_documento.getNumeroPaginas()>0||imx_documento.getIdTipoDocumento()==Documento.IMAX_FILE) {
	response.sendRedirect("./Visualizador.jsp?select="+selectId);
} else {
	response.sendRedirect("./PreGuardaDocto.jsp?select="+selectId+"&nuevo=false&editable=false");
}

%>
</head>
<body>

</body>
</html>