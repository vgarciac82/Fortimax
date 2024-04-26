<%@ page language="java" contentType="text/html; charset=UTF-8" import="com.syc.utils.*,org.apache.log4j.Logger"%>
<%@page import="com.syc.user.Usuario"%>
<%	

	Usuario u = (Usuario)session.getAttribute(ParametersInterface.USER_KEY);
	
	Logger log = Logger.getLogger(this.getClass());
	log.info("[Finalizando Session: "+u.getNombreUsuario()+"]");
	System.out.println("[Finalizando Session: "+u.getNombreUsuario()+"]");
	
	session.removeAttribute(ParametersInterface.USER_KEY);
	session.removeAttribute(ParametersInterface.TREE_KEY);
	session.removeAttribute(ParametersInterface.TREE_MDC_KEY);
	session.removeAttribute(ParametersInterface.TREE_APP_KEY);
	session.removeAttribute(ParametersInterface.TREE_EXP_KEY);
	session.invalidate();
	//response.sendRedirect("/"); 
	String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":"
		+ request.getServerPort() + request.getContextPath();
%>
<script>
//var urlnoseguro = "http://" + window.location.host;
var urlnoseguro = "<%=urlPrefix%>";
top.location.replace(urlnoseguro);
</script>
