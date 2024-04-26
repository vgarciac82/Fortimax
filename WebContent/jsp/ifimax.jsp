<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.imaxfile.Carpeta"%>
<%@page session="false" %>
<%@page import="com.syc.utils.*,com.syc.user.*"%>
<%@page import="com.syc.fortimax.managers.PrivilegioManager"%>
<%@page import="java.util.Map"%>
<%@page import="com.syc.fortimax.config.Config"%>
<%@page import="com.syc.imaxfile.GetDatosNodo"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ifimax</title>

<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.ForCSS %>" /> 
<script type="text/javascript" src="<%=Config.ForExt%>" charset="utf-8"></script>
<script type="text/javascript" src="<%=Config.ForLo%>" charset="utf-8"></script>
<!-- Framework:Extjs -->

<!-- Aplicacion:Extjs -->
<link rel="stylesheet" type="text/css" href="../css/ifimax.css" />
<script type="text/javascript" src="../js/ifimax.js"></script>
<!-- /Aplicacion:Extjs -->
 <%
HttpSession session = request.getSession(false);
String nodo = (String)request.getParameter("select");
GetDatosNodo gdn = new GetDatosNodo(nodo);
if(gdn.isDocumento())
	gdn.separaDatosDocumento();
else if (gdn.isCarpeta())
	gdn.separaDatosCarpeta();
else {
	gdn.separaDatosGabinete();
	nodo += "C0";
}
Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);

//RW: si se envía falso, no se permite escritura.
//Si se envía cierto, entonces se revisan los permisos del Usuario.
Boolean ifimax_privilegioEditarExpediente =  (Boolean) session.getAttribute("rw");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
UsuarioPermisos usuarioPermisos = new UsuarioPermisos(u,gdn.getGaveta());
boolean puedeBorrar = usuarioPermisos.isEliminar();
String jsonPrivilegios = new Json(usuarioPermisos).returnJson();
%>

</head>
<body>
<script type="text/javascript">
var jsonPrivilegios = '<%=jsonPrivilegios%>';
var basePath='<%=basePath%>';
var rutaExpedienteServlet=basePath+	'/ExpedienteServlet';
var rutaArbolServlet=basePath+	'/ArbolServlet';
var rutaServletIfimax = basePath + '/ifimax';
var rutaPageError = basePath + '/error/error404.jsp';
var titulo_aplicacion ='<%=gdn.getGaveta()%>';
var id_gabinete ='<%=gdn.getGabinete()%>';
var nodo ='<%=nodo%>';
var usuario ='<%=u.getNombreUsuario()%>';
var nombre_usuario=  '<%=u.getNombre() +" "+ u.getApellidoPaterno()%>';
var ifimax_privilegioEditarExpediente = '<%=ifimax_privilegioEditarExpediente%>';
</script>
    	<div id="west" class="x-hide-display"></div>
    	<div id="center1" class="x-hide-display">
    	<IMG src="../Admin/imagenes/banner2.jpg" align="right">
    	</div>    
	    <div id="banner" >		
		<table id="Table_01" width="1800" height="64" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><img src="../imagenes/fortimax/banner22_01.png" width="247" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_02.png" width="18" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_03.png" width="8" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_04.png" width="102" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_05.png" width="23" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_06.png" width="821" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_07.png" width="23" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_08.png" width="90" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_09.png" width="8" height="64" alt=""></td>
				<td><img src="../imagenes/fortimax/banner22_10.png" width="460" height="64" alt=""></td>
			</tr>
		</table>
		</div>

</body>
</html>