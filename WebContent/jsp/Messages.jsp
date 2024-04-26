<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*,com.syc.user.*,com.syc.tree.*,com.jenkov.prizetags.tree.itf.*,com.syc.imaxfile.*,com.syc.utils.*"%>
<%@page session="false" %>
<%
	HttpSession session = request.getSession(false);
%>
<%
//System.out.println("[jsp/Messages.jsp] Session ID:[" + session.getId() + "]" );
boolean isOk = "true".equals(request.getParameter("ok"));
String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":"
		+ request.getServerPort() + request.getContextPath();
		
	urlPrefix = "http://neptuno.segurosatlas.com.mx/Magic94Scripts/mgrqispi94.dll?APPNAME=Siniestros_IMAX_web&PRGNAME=cap_siniestro&ARGUMENTS=-A,-N,-A"+request.getParameter("nombre_usuario")+",-A";
if ("true".equals(request.getParameter("applet"))) {
	isOk = "true".equals((String)session.getAttribute("uploadStatus"));

	session.removeAttribute("pageCount");
	session.removeAttribute("uploadStatus");
	session.removeAttribute("docImaxFileAnt");

	Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
	if (u == null) {
		response.sendRedirect("index.jsp");
		return;
	}

	ITree tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);
	if (tree == null) {
		response.sendRedirect("index.jsp");
		return;
	}

	Carpeta c = (Carpeta) tree.getRoot().getObject();
	boolean isMyDocs = "USR_GRALES".equals(c.getTituloAplicacion());
	ArbolManager amd = new ArbolManager(c.getTituloAplicacion(), c.getIdGabinete());
	ITree ntree = amd.generaExpediente(u.getNombreUsuario());
	Set<?> expandedNodes = tree.getExpandedNodes();
	Set<?> selectedNodes = tree.getSelectedNodes();
	
	Iterator<?> i = expandedNodes.iterator();
	while (i.hasNext()) {
		ITreeNode tn = (ITreeNode) i.next();
		ntree.expand(tn.getId());
	}

	i = selectedNodes.iterator();
	while (i.hasNext()) {
		ITreeNode tn = (ITreeNode) i.next();
		ntree.select(tn.getId());
	}
	
	 
	ntree.select(ntree.getRoot().getId());
	session.setAttribute((isMyDocs ? ParametersInterface.TREE_MDC_KEY : ParametersInterface.TREE_EXP_KEY), ntree);
}

String msg[] = (String[]) session.getAttribute("msg");
String bodyAtt = (String) session.getAttribute("bodyAttributes");
bodyAtt = (bodyAtt == null) ? "" : " " + bodyAtt;
String scrptPrfix[] = (String[]) session.getAttribute("scriptPrefix");
String scrptSufix[] = (String[]) session.getAttribute("scriptSufix"); %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Mensaje FortImax</title>
<link href="../css/fortimax_sistema.css" rel="stylesheet" type="text/css">
<%if (scrptPrfix != null) {%>
<script type="text/javascript">
<%for (int i = 0; i < scrptPrfix.length; i++) { out.println(scrptPrfix[i]); }%>
	</script>
<%}%>
</head>
<body<%=bodyAtt%>>
<p>&nbsp;</p>
<table border="0" align="center" cellpadding="0" cellspacing="0" class="bordes">
	<tr>
		<td>
		<table align="center" width="100%" id="texto">
			<tr>
			<%if (isOk) {%>
				<td class="bordetit"><img src="../imagenes/aceptar.gif" width="36" height="36" hspace="15" align="top"><strong>Mensajes
				Fortimax</strong>&nbsp;&nbsp;&nbsp;</td>
			<%} else {%>
				<td class="bordetit"><img src="../imagenes/cancelar.gif" width="36" height="36" hspace="15" align="top"><strong>Mensajes
				Fortimax</strong>&nbsp;&nbsp;&nbsp;</td>
			<%}%>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
<%if (msg != null) { for (int i = 0; i < msg.length; i++) { if (msg[i] != null) { %>
			<tr>
				<td align="center" <%=isOk ? "" : "class=\"advertencia\""%>><%=msg[i]%></td>
			</tr>
<% } } }%> <%if (!isOk && request.getParameter("fromShared")==null) {%>
			<tr>
				<td align="right"><a href="<%=request.getParameter("fromLogin")!=null ? urlPrefix : "javascript:history.go(-1)"%>">Regresar</a></td>
			</tr>
		<%}%>
		<%if(Boolean.parseBoolean(request.getParameter("ifimax"))) {%>
			<tr>
				<td align="center"><%=request.getParameter("msg")%></td>
			</tr>
		<%}%>
		</table>
		</td>
	</tr>
</table>
<%if (scrptSufix != null) {%>
<script type="text/javascript">
<%for (int i = 0; i < scrptSufix.length; i++) { out.println(scrptSufix[i]); }%>
	</script>
<%}%>
</body>
</html>
<%	session.removeAttribute("msg");
	session.removeAttribute("bodyAttributes");
	session.removeAttribute("scriptPrefix");
	session.removeAttribute("scriptSufix"); %>
