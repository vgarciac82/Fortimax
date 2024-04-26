<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.syc.user.*,com.syc.imaxfile.*,com.jenkov.prizetags.tree.itf.*,com.syc.tree.*,com.syc.utils.*,java.util.*" %>
<%@ taglib uri="/WEB-INF/tags/treetag.tld" prefix="tree" %>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>Modificar Descripcion/Atributos Documento</TITLE>
<link rel="stylesheet" href="css/fortimax_sistema.css" type="text/css" />
<script>
	opener.location.href = 'getexpedient?select=<%=request.getParameter("titulo_aplicacion")%>&id_gabinete=<%=request.getParameter("id_gabinete")%>&toSelect=<%=request.getParameter("select")%>';
</script>
</HEAD>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">
<table width="100%"  height="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="bordes">
  <tr>
    <td valign="middle"><img src="imagenes/aceptar.gif" width="36" height="36" hspace="10" align="middle"><strong>
    Acción realizada con éxito    
    </strong> </td>
  </tr>
  <tr>
  	<td>	
  		<table  border="0">
  			<tr>
  				<td align="left" valign="middle"><font size="1"><a href="javascript: window.close();">Cerrar ventana</a></font></td>
  				<td align="right" valign="middle"><font size="1"><a href="jsp/<%=request.getParameter("accion")!=null && "mn".equals(request.getParameter("accion")) ? "ModificaNombre.jsp" : "AtributoDocumento.jsp"%>?select=<%=request.getParameter("select")%>">Regresar</a></font></td>
  			</tr>
  		</table>
  	</td>
  </tr>
<!--   <table width="200"  border="0" align="center" cellpadding="0" cellspacing="0" class="bordes"> -->
<!--   <tr> -->
<!--     <td><img src="../imagenes/aceptar.gif" width="36" height="36" hspace="10" align="middle"><strong> -->
<!--     Acción realiza con exito     -->
<!--     </strong> </td> -->
<!--   </tr> -->
<!-- </table> -->
  
</table>
<script>
setTimeout('window.close();',300000);
</script>

</BODY>
</HTML>
