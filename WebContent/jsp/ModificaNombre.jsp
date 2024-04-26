<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,javax.naming.*,java.sql.*,com.syc.user.*,com.syc.imaxfile.*,com.jenkov.prizetags.tree.itf.*,com.syc.tree.*,com.syc.utils.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Modificar Datos</title>
<link rel="stylesheet" href="../css/fortimax_sistema.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="../css/scrolltable.css">
<script language="javascript" src="../js/scrolltable.js"></script>
<script language="javascript" type="text/javascript" src="../js/fortimax_sistema.js"></script>
</head>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">

<%


Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}

String titulo_aplicacion = "Gavetas";
ITree tree  = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);
if(tree==null){
	response.sendRedirect("../index.jsp");
	return;
}

Object obj = null;
Documento d = null;
Carpeta c = null;
Iterator<?> it = tree.getSelectedNodes().iterator();
if(it.hasNext()){
	ITreeNode n = (ITreeNode)it.next();
	if(n.getObject() instanceof Documento){
		d = (Documento)n.getObject();
		titulo_aplicacion = d.getTituloAplicacion();
	}
	else{
		c = (Carpeta)n.getObject();
		titulo_aplicacion = c.getTituloAplicacion();

	}
}




//String titulo_aplicacion = ((Carpeta) tree.getRoot().getObject()).getTituloAplicacion();
boolean isMyDocs = "USR_GRALES".equals(titulo_aplicacion);

%>
<form action="../ModificaNombre" method="post" onSubmit="return validaForma();">
<input type="hidden" name="titulo_aplicacion" value="<%=d != null ? d.getTituloAplicacion() : c.getTituloAplicacion()%>">
<input type="hidden" name="id_gabinete" value="<%=d != null ? d.getIdGabinete() : c.getIdGabinete()%>">
<input type="hidden" name="id_carpeta" value="<%=d != null ? d.getIdCarpetaPadre() : c.getIdCarpeta()%>">
<%if(d !=null){%>
<input type="hidden" name="id_documento" value="<%=d.getIdDocumento()%>">
<%}else{%>
<input type="hidden" name="id_carpeta_padre" value="<%=c.getIdCarpetaPadre()%>">
<%}%>
<input type="hidden" name="select" value="<%=request.getParameter("select")%>">
<table  width="100%" cellpadding="0" cellspacing="0" border="0" class="bordes">
	<tr>
		<td>
			<table cellpadding="0"style="height:110px; width:100%" cellspacing="0">
				<tr>
					<td colspan="2">
					<table width="100%" class="bordetit">
						<tr>
							<td width="90%" align="center"><font class="gpcolors" size="1">Modificar datos</font></td>
							<td width="10%" align="right">
								<a href="javascript:window.close();">
									<img src="../imagenes/b_cerrar.gif" alt="Cerrar ventana" width="17" height="13" border="0">
								</a>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				
				<tr>
					<td align="center">
						<table style="height:100px;">
							<tr>
								<td><font size="1">Nombre:&nbsp;</font></td>
							</tr>
							<tr>
								<td align="center" ><font size="1">
									<input type="text" name="nombre" style="width:250px" size="49" value="<%= d != null ? d.getNombreDocumento() : c.getNombreCarpeta() %>" maxlength="256">
								</font></td>
							</tr>
							<tr>
								<td ><font size="1">Descripción:&nbsp;</font></td>
							</tr>
							<tr>
								<td align="center"><font size="1"><textarea name="descripcion" style="width:250px" cols="36" rows="3"><%=d!=null?d.getDescripcion():c.getDescripcion()%></textarea></font></td>			
							</tr>							
						</table>
					</td>
				</tr>
              	<tr>
					<td align="center"><input type="submit" value="Modificar"></td>
			  	</tr>
			  	<tr><td><br /></td></tr>	
		</table>
		</td>
	</tr>
</table>
</form>
<script>
setTimeout('window.close();',300000);


function validaForma(){
	if(document.forms[0].nombre.value==''){
		alert('El nombre no puede ser vacío.');
		return false;
	}
	
	return true;
	
}

</script>

</body>
</html>
