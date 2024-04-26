<META HTTP-EQUIV="Refresh" CONTENT="0; URL=login.jsp">
<%
	String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":"
		+ request.getServerPort() + request.getContextPath();
%>
<script>
//var urlnoseguro = "http://" + window.location.host;
var urlnoseguro = "<%=urlPrefix%>";
top.location.replace(urlnoseguro);
</script>