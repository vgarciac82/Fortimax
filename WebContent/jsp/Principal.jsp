<%@ page language="java" contentType="text/html; charset=UTF-8" import="com.syc.utils.*"%>
<%
if (session.getAttribute(ParametersInterface.USER_KEY) != null) {
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%	String idNode = request.getParameter("select");
	String tipo = request.getParameter(ParametersInterface.TREE_TYPE_KEY);
	if (tipo == null)
		tipo = "d";
%>
<title>Fortimax.com</title>
<link rel="shortcut icon" href="../fortimax.ico"/>
</head>
<frameset rows="90,*" cols="*" framespacing="0" frameborder="no" border="0">
	<frame name="superior" src="TituloArriba.jsp?select=<%=idNode%>" frameborder="0" scrolling="no" noresize>
	<frameset cols="230,*" framespacing="0" frameborder="no" border="1">
		<frame name="left" src="ArbolExpediente.jsp?select=<%=idNode%>&<%=ParametersInterface.TREE_TYPE_KEY+"="+tipo%>" scrolling="no" noresize></frame>
		<!--frame name="left" src="ArbolContenedor.jsp?select=<%=idNode%>&arbol.tipo=d" scrolling="no" noresize></frame-->
		<frame name="main" src="Bienvenida.jsp" scrolling="auto" noresize></frame>
	</frameset>
</frameset>
<body>
<noframes><body>Su Browser no soporta &lt;frames&gt;</body></noframes>
</body>
</html>
<%	} else {
		response.sendRedirect("/");
	} %>
