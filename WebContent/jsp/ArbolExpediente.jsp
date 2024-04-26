
<%@page import="java.io.PrintWriter"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.syc.user.*,com.syc.imaxfile.*,com.syc.fortimax.hibernate.entities.*,java.util.Map,java.util.HashMap,java.util.Calendar,java.text.SimpleDateFormat,com.jenkov.prizetags.tree.itf.*,com.syc.tree.*,com.syc.utils.*,com.syc.fortimax.config.Config" %>
<%@ taglib uri="/WEB-INF/tags/treetag.tld" prefix="tree" %>
<%!	Usuario u = null; Carpeta c = null; Documento d = null; String vista = "d"; ITree tree = null; String nodeId, newNodeId; Carpeta cp = null;%>
<%	imx_aplicacion ap = null;
String desc = "";

if ((u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY)) == null) { %>
<script language="javascript">self.top.location.href = "../index.jsp";</script>
<%		return;
	}


	tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);
	boolean conMisDocumentos = true;
	if(session.getAttribute("conMisDocumentos")!=null){
		String strConMisDocumentos= (String)session.getAttribute("conMisDocumentos");
		if(!"".equals(strConMisDocumentos) && strConMisDocumentos.equalsIgnoreCase("false")){
			conMisDocumentos = false;
		}
		else{
			conMisDocumentos = true;
		}
	}
	ITreeNode copyNode = null;
	String copy = request.getParameter("node.copy");
	if (copy != null) {
		copyNode = tree.findNode(copy);
		session.setAttribute("copyNodeId", copyNode);
		session.removeAttribute("cutNodeId");
	}
	//cpia con Layers... 
	String gaveta=null;
	int id_gabinete=-1;
	String carpeta_trabajo="Versionado";
	String copyLayer = request.getParameter("node.copy.layer");
	if (copyLayer != null) {
		copyNode = tree.findNode(copyLayer);
		session.setAttribute("copyNodeId", copyNode);
		session.removeAttribute("cutNodeId");
	    Carpeta c=null;
	    //Obtiene datos de carpeta
		Object obj = tree.getRoot().getObject();
	    Carpeta carpeta_root=((Carpeta)obj);
	    carpeta_root.setNombreUsuario(u.getNombreUsuario());
	    carpeta_root.setPassword(u.getCdg());
		gaveta = carpeta_root.getTituloAplicacion();
		id_gabinete=carpeta_root.getIdGabinete();

		CarpetaManager carpManager= new CarpetaManager(carpeta_root);	    

	    c= carpManager.getFolder(carpeta_root,carpeta_trabajo);
		ITreeNode pasteNode = tree.findNode(copyLayer);

		PasteNodeManager pnm = new PasteNodeManager(
			true,
			u,
			copyNode,
			copyNode);
		Documento newDocto=(Documento)copyNode.getObject();
		
		//<dMod: para que se agregue la fecha a cada nueva version		
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

		newDocto.setNombreDocumento(newDocto.getNombreDocumento() + " " + sdf.format(cal.getTime()));
		//dMod>
		
		newDocto = pnm.crearLayerDocumento(session,newDocto,c);
		newNodeId = ArbolManager.generaIdNodeDocumento
		(newDocto.getTituloAplicacion(),
				newDocto.getIdGabinete(),
				newDocto.getIdCarpetaPadre(),
				newDocto.getIdDocumento()
				);
		System.out.println("newNodeId[" + newNodeId + "]");
		String Gabinete = newDocto.getTituloAplicacion();
		String Expediente="G"+newDocto.getIdGabinete();
		String Carpeta="C"+c.getIdCarpeta();
		String Documento="D"+newDocto.getIdDocumento();
		session.setAttribute("node.copy.layer",Gabinete+"_"+Expediente+Carpeta+Documento);
	}

	ITreeNode cutNode = null;
	String cut = request.getParameter("node.cut");
	if (cut != null) {
		cutNode = tree.findNode(cut);
		session.setAttribute("cutNodeId", cutNode);
		session.removeAttribute("copyNodeId");
	}

	copyNode = (ITreeNode) session.getAttribute("copyNodeId");
	cutNode = (ITreeNode) session.getAttribute("cutNodeId");
	boolean cutOrCopy = ((copyNode != null) || (cutNode != null));

	newNodeId = null;
	String paste = request.getParameter("node.paste");
	if (paste != null) {
		ITreeNode pasteNode = tree.findNode(paste);
		PasteNodeManager pnm = new PasteNodeManager(
			(copyNode != null),
			u,
			(copyNode != null ? copyNode : cutNode),
			pasteNode);
		newNodeId = pnm.doPaste(session);

		session.removeAttribute("copyNodeId");
		session.removeAttribute("cutNodeId");

		copy = cut = null;
		cutOrCopy = false;
	}

	String vistaValue = request.getParameter(ParametersInterface.TREE_TYPE_KEY);
	if(vistaValue == null)
		vistaValue = (String) session.getAttribute(ParametersInterface.TREE_TYPE_KEY);
		
	vista = (vistaValue != null)? vistaValue: "d";

    session.setAttribute(ParametersInterface.TREE_TYPE_KEY, vista);

	if ("g".equals(vista)) {
		session.setAttribute(ParametersInterface.TREE_KEY, session.getAttribute(ParametersInterface.TREE_APP_KEY));
		tree = (ITree) session.getAttribute(ParametersInterface.TREE_APP_KEY);
	} else	if ("e".equals(vista)) {
		session.setAttribute(ParametersInterface.TREE_KEY, session.getAttribute(ParametersInterface.TREE_EXP_KEY));
		tree = (ITree) session.getAttribute(ParametersInterface.TREE_EXP_KEY);
		System.out.println(ParametersInterface.TREE_EXP_KEY);
		System.out.println(session.getAttribute(ParametersInterface.TREE_EXP_KEY));
	} else {
		session.setAttribute(ParametersInterface.TREE_KEY, session.getAttribute(ParametersInterface.TREE_MDC_KEY));
		tree = (ITree) session.getAttribute(ParametersInterface.TREE_MDC_KEY);	
	}
	String aplicacion = "";
	int privilegios_aplicacion = 0;
	boolean creacion = false;
	if(!"g".equals(vista)){
		
		Object obj = tree.getRoot().getObject();
		if(obj instanceof Carpeta){
			aplicacion = ((Carpeta)obj).getTituloAplicacion();
		}
		else{
			aplicacion = ((Documento)obj).getTituloAplicacion();
		}
	}
	UsuarioPermisos privilegios =  new UsuarioPermisos(u, aplicacion);
	if(!"g".equals(vista))
		creacion = privilegios.isCrear() && u.getTipoOperacion()=='4';//priv: 4  tipo = 4

	//tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);

	QuotaInfo qinf = new QuotaInfo();

	String QuotaPercent="0";
	String sColorQuotaPercent="";
	String sLabelQuotaUsedTotal="";

	if (!qinf.GetUsedStorageCapacity(u.getNombreUsuario())){
		System.out.println("ArbolExpediente.jsp - No se puede obtener el espacio de almacenamiento ");
	} else {
		QuotaPercent = qinf.GetQuotaPercentFormat();
		//System.out.println("[ArbolExpediente.jsp] Porcentaje de almacenamiento : " + QuotaPercent);
		sColorQuotaPercent = qinf.GetColorPercentCuota();
		//System.out.println("[ArbolExpediente.jsp] Color del porcentaje de almacenamiento : " + sColorQuotaPercent);
		sLabelQuotaUsedTotal = qinf.GetUsedQuota() + " / " + qinf.GetQuotaTotal();
	}

	
%>
<script language="JavaScript">
var s = 1; 

var creacion = <%=creacion%>;
			
/*
privilegios
1 = eliminar
4 = digitalizar
8 = consultar
*/
			 
function botonesCrpta(isRoot) {

	try {
		if(creacion){
			 window.parent.superior.mostrar("boperaciones");		
		}
		else{
			window.parent.superior.ocultar("boperaciones");
		}

		if(<%= privilegios.isDigitalizar() && u.getTipoOperacion()=='4'%>){//priv: 64, tipo = 4)
			 window.parent.superior.mostrar("btnDigitalizar");
			 window.parent.superior.mostrar("sepaDigitalizar");
		}else{
			window.parent.superior.ocultar("btnDigitalizar");	
			window.parent.superior.ocultar("sepaDigitalizar");
		}
			if(<%=privilegios.isDescargar()%>)
			{	
				window.parent.superior.mostrar("btnRecuperarCarpeta");
				window.parent.superior.document.getElementById("brecuperardoctocarpeta").style.display = "block";
			}
			else
			{
				window.parent.superior.ocultar("btnRecuperarCarpeta");
			}
		
		window.parent.superior.document.getElementById("beditardocto").style.display = isRoot ? "none": "block";
	} catch (e) {
		setTimeout("botonesCrpta(" + isRoot + ")",2000);
	}
}

function botonesGavts() {
	
	
	if(window.parent.superior.cargado==1){
		window.parent.superior.ocultar("boperaciones");
		window.parent.superior.ocultar("beliminar");
		window.parent.superior.ocultar("brecuperardoctocarpeta");
		window.parent.superior.ocultar("beditardocto");
	}
	else{
		setTimeout("botonesGavts()", 500); 
	}
}

function botonesDocto(isProtected,isEditable) {
	window.parent.superior.ocultar("boperaciones");
	if(<%=privilegios.isEliminar()%>  && <%=u.getTipoOperacion()%>==4)
	{ 
		window.parent.superior.document.getElementById("beliminar").style.display = isProtected ? "none": "block";
	}
	if(<%=privilegios.isDescargar()%>  && <%=u.getTipoOperacion()%>==4)
	{	
	window.parent.superior.document.getElementById("brecuperardoctocarpeta").style.display = isProtected ? "none": "block";
	}
	window.parent.superior.document.getElementById("beditardocto").style.display = isProtected ? "none": (isEditable ? "block" : "none");
}

function botonesCrpta2() {
	if(creacion){  
		window.parent.superior.mostrar("boperaciones");
	}
	
	window.parent.superior.ocultar("beliminar");
	window.parent.superior.ocultar("brecuperardoctocarpeta");
	window.parent.superior.ocultar("beditardocto");
}

function selectTab(type) {
	var urlTree = 'ArbolExpediente.jsp?cambio=si&select=' + getNodeSelect() + '&arbol.tipo=' + type + '#' + getNodeSelect();
	var urlMain = 'Bienvenida.jsp';
	parent.left.location.replace(urlTree);
	top.main.location.replace(urlMain);
}

//RMN - AGREGADO EL 25-01-2006 PARA MODIFICAR ATR DESC DOCUMENTOS
function DoctosDescAtr(){
	//window.open('AtributoDocumento.jsp?select='+getNodeSelect(),'','width=330,height=260,scroll=yes');
	//openCenteredWindow('AtributoDocumento.jsp?select='+getNodeSelect(),'_black',213,335);
	 var url='AtributoDocumento.jsp?select='+getNodeSelect();
	   oIFrm = document.getElementById('frmDownload');
     oIFrm.src = url;
}

function ModificaNombre(){
	//window.open('ModificaNombre.jsp?select='+getNodeSelect(),'','width=330,height=120,scroll=yes');
	openCenteredWindow('ModificaNombre.jsp?select='+getNodeSelect(),'_blank',175,334);
}

//FIN RMN

function openCenteredWindow( url, name, height, width, parms ) {
 var left = Math.floor( (screen.width - width) / 2);
 var top = Math.floor( (screen.height - height) / 2);
 var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";

	if (parms) { winParms += "," + parms; }
	var win = window.open(url, name, winParms);
	if (parseInt(navigator.appVersion) >= 4) { win.window.focus(); }
	return win;
}

</script>
<tree:onExpand>
	<tree:expandedNode tree="arbol.modelo" expandedNode="arbol.nodoActual" />
</tree:onExpand>
<tree:onCollapse>
	<tree:collapsedNode tree="arbol.modelo" collapsedNode="arbol.nodoActual" />
</tree:onCollapse>
<tree:onSelect>
	<tree:selectedNode tree="arbol.modelo" selectedNode="arbol.nodoActual" />
</tree:onSelect>
<tree:detachNodeObject node="arbol.nodoActual" detachedObject="nodo.objeto" />
<%	ITreeNode n = (ITreeNode) request.getAttribute("arbol.nodoActual");
	nodeId = (n != null)? n.getId(): tree.getRoot().getId();
	newNodeId = (newNodeId == null) ? nodeId : newNodeId; %>
<script language="JavaScript">
var idnodo = "";
function getNodeSelect() {
	return "<%=nodeId%>";
}
</script>
<script language="javascript" type="text/javascript" src="../js/fortimax_sistema.js"></script>
<script language="javascript" type="text/javascript" src="../js/md5.js"></script>
<link rel="stylesheet" href="../css/fortimax_sistema.css" type="text/css" />
<link rel="stylesheet" href="../css/fortimax_arbol.css" type="text/css">

<style type="text/css">
<!--
body {
	background-image: url(../imagenes/interfaz/a_fondo.gif);
	background-repeat: repeat-y;
}
.vinculo {
	cursor: hand;
}
-->
</style>
<title><%=(("g".equals(vista))? "Gavetas": (("e".equals(vista))? "Expediente": "Mis Documentos"))%></title>
<!-- JS para utilizar EXTJS4 -->
 <link rel="stylesheet" type="text/css" href="../resources/css/ext-all.css" />  
<!--   <link rel="stylesheet" type="text/css" href="../resources/css/custom.css" /> --> 
<script type="text/javascript" src="../js/Extjs4/ext-all.js"></script>
<script type="text/javascript" src="../js/Extjs4/locale/ext-lang-es.js"></script>
<!-- Llamada a CSS -->
<link rel="stylesheet" type="text/css" href="../css/autenticacion.css" />
<!-- JS de la Interfaz --> 
<script type="text/javascript">
var tituloA='Autenticacion';
var msjA='Introduzca la contrase\u00F1a de está carpeta';
var horasC=8;
Ext.onReady(function(){
	function ajustaArbol(){
		var is_safari = navigator.userAgent.toLowerCase().indexOf('safari/') > -1;
	    var is_chrome= navigator.userAgent.toLowerCase().indexOf('chrome/') > -1;
	    var is_firefox = navigator.userAgent.toLowerCase().indexOf('firefox/') > -1;
	    var is_ie = navigator.userAgent.toLowerCase().indexOf('msie ') > -1;
	    var alto=Ext.getBody().getViewSize().height;
	    var altoIE=alto-120;
	    var altoSA=alto-20;
	    if(is_ie)
	    	document.getElementById('tablaArbol').style.height = altoIE+"px";
	    else if(is_safari)
	    	document.getElementById('tablaArbol').style.height = altoSA+"px";
	    else if(is_firefox)
	    	document.getElementById('tablaArbol').style.height = altoIE+"px";

	}
	Ext.EventManager.onWindowResize(function(w, h){
		ajustaArbol();
	});
	ajustaArbol();
});

</script>
<script src="../js/md5.js" type="text/JavaScript"></script>
<script src="../js/autenticacion.js" type="text/JavaScript"></script>
</head>
<body>

<table id="tablaArbol" style="height:100%;" width="225" align="left" border="0" cellpadding="0" cellspacing="0">
<%	if (u.isPyME()) { %>
	<tr>
		<td class="detalle" valign="top" height="3%">
			<table cellpadding="0" cellspacing="0" border="0" height="4%">
				<tr>					
					<% if(conMisDocumentos){ %>
					<td height="27" nowrap class="vinculo" onclick="botonesCrpta2();selectTab('d');"><img src="../imagenes/interfaz/p_mis_doc_o<%="d".equals(vista) ? "n":"ff" %>.gif" alt="Vista Mis Documentos" width="82" height="27"></td>
					<% }else{%>
					<td height="27" nowrap class="vinculo"><img src="../imagenes/interfaz/p_sin_mis_doc_off.gif" width="82" height="27"></td>
					<%	} %>
					<td height="27" nowrap class="vinculo" onclick="selectTab('g');botonesGavts();"><%	if ("g".equals(vista)) { %><img src="../imagenes/interfaz/p_gavetas_on.gif" alt="Vista de Gavetas" width="67" height="27"><%	} else { %><img src="../imagenes/interfaz/p_gavetas_off.gif" alt="Vista de Gavetas" width="67" height="27"><%	} %></td>
					<td height="27" nowrap class="vinculo"><%	if (session.getAttribute(ParametersInterface.TREE_EXP_KEY) != null) { %><div onclick="selectTab('e')"><%	} else { %><div onclick="alert('No se ha seleccionado un expediente')"><%	} %><%	if ("e".equals(vista)) { %><img src="../imagenes/interfaz/p_expediente_on.gif" alt="Vista de Expedientes" width="76" height="27"><%	} else { %><img src="../imagenes/interfaz/p_expediente_off.gif" alt="Vista de Expedientes" width="76" height="27"><%	} %></div></div></td>
				</tr>
				<tr>
				  <td colspan="3"><%	if ("d".equals(vista)) { %><img src="../imagenes/interfaz/p_mis_doc_barra.gif" width="225" height="9"><% } if ("g".equals(vista)) { %><img src="../imagenes/interfaz/p_gavetas_barra.gif" width="225" height="9"><% } if ("e".equals(vista)) { %><img src="../imagenes/interfaz/p_expediente_barra.gif" width="225" height="9"><%	} %></td>
			  </tr>
		  </table>
		</td>
	</tr>
<%	} else { %>
	<tr>
		<td height="2%"><img src="../imagenes/interfaz/a_pmisdoctosp.gif" width="225" height="36"></td>
	</tr>
<%	} %>
	<tr>
		<td height="2%">
			<table align="right" border="0" cellspacing="1" cellpadding="0">
				<tr>
					<td>
						<!-- a href="../ConvertirAPDF?idNode=<%=nodeId%>" target="main">P</a -->
					</td>			
					<td>
						<!-- a href="Sincronizacion.jsp?idNode=<%=nodeId%>" target="main">S</a -->
					</td>
					<td width="16">
						<!-- a onclick="openCenteredWindowMax('../TFOWrite.jsp','_blank',600,800);" onmouseover="window.status='Crear Documento Procesador de Texto';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/write.gif" width="16" height="16" alt="Crear Documento Procesador de Texto">
						</a -->
					</td>
					<td width="16">
						<!-- a onclick="openCenteredWindowMax('../TFOCalc.jsp','_blank',600,800);" onmouseover="window.status='Crear Documento Hoja de Cálculo';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/calc.gif" width="16" height="16" alt="Crear Documento Hoja de Cálculo">
						</a -->
					</td>
					<td width="16">
						<!-- a onclick="openCenteredWindowMax('../TFOShow.jsp','_blank',600,800);" onmouseover="window.status='Crear Documento Presentaciones';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/show.gif" width="16" height="16" alt="Crear Documento Presentaciones">
						</a -->
					</td>
					

<%	if (!"g".equals(vista))	{ %>

					<td width="16">
						<a href="BuscaDocumento.jsp<%=(request.getParameter("cambio")!=null && "si".equalsIgnoreCase(request.getParameter("cambio")) ? "?nueva=true" :"") %>" target="main" onmouseover="window.status='Buscar documento';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/find_24x24.png" width="24" height="24" alt="Buscar Documento">
						</a>
					</td>
<%if(privilegios.isDigitalizar() && u.getTipoOperacion()=='4'){ %>
					<td width="16">
										
						<a  onmouseover="window.status='Subir Imagenes';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/fupload.png" width="24" height="24" alt="Subir imagenes" onclick="window.open('PageDocumentUploadApplet.jsp?select=<%=nodeId%>&appletExtended=true','_blank','width=1000,height=600',true)">
						</a>
	<%	} else { %>
						<img src="../imagenes/tree/fupload_off.png" width="24" height="24" alt="Subir imagenes">

					</td>
	
<%	} %>

<%if(privilegios.isCompartir() && u.getTipoOperacion()=='4'){ %>
					<td width="16">
	<%	if (tree.findNode(nodeId).getType().startsWith("docto")) { %>
										
						<a href="URLShared.jsp?select=<%=nodeId%>" target="main" onmouseover="window.status='Compartir este documento';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/web_24x24.png" width="24" height="24" alt="Compartir este documento">
						</a>
	<%	} else { %>
						<img src="../imagenes/tree/web_24x24_off.png" width="24" height="24" alt="Compartir Documento">

					</td>
	<%}%>
	
<%	} %>

					<td width="16">
	<%	if (tree.getRoot().getId().equals(nodeId) || !creacion) { %>
						<img src="../imagenes/tree/cut_24x24.png" width="24" height="24" alt="Cortar">
	<%	} else { %>
						<a href="ArbolExpediente.jsp?select=<%=nodeId%>&node.cut=<%=nodeId%>&arbol.tipo=<%=vista%>#<%=nodeId%>" onmouseover="window.status='Cortar carpeta o documento';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/cut_24x24_off.png" width="24" height="24" alt="Cortar">
						</a>
	<%	} %>
					</td>
					<td width="16">
	<%	if (tree.getRoot().getId().equals(nodeId) || !creacion) { %>
						<img src="../imagenes/tree/copy_24x24.png" width="24" height="24" alt="Copiar">
	<%	} else { %>
						<a href="ArbolExpediente.jsp?select=<%=nodeId%>&node.copy=<%=nodeId%>&arbol.tipo=<%=vista%>#<%=nodeId%>" onmouseover="window.status='Copiar carpeta o documento';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/copy_24x24_off.png" width="24" height="24" alt="Copiar">
						</a>
	<%	} %>
					</td>					
	<%	if (cutOrCopy && tree.findNode(nodeId).getType().startsWith("carpeta")  && creacion) { %>
					<td width="16">
						<a href="ArbolExpediente.jsp?select=<%=newNodeId%>&node.paste=<%=nodeId%>&arbol.tipo=<%=vista%>#<%=nodeId%>" onmouseover="window.status='Pegar carpeta o documento';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/paste_24x24.png" width="24" height="24" alt="Pegar">
						</a>
					</td>
	<% } else { %>
					<td width="16">
						<img src="../imagenes/tree/paste_24x24_off.png" width="24" height="24" alt="Pegar">
					</td>
	<% } %>
					<td width="16">&nbsp;</td>
<%	} else { %>
					<td width="16">
						<a href="BuscaDocumento.jsp<%=(request.getParameter("cambio")!=null && "si".equalsIgnoreCase(request.getParameter("cambio")) ? "?nueva=true" :"") %>" target="main" onmouseover="window.status='Buscar documento';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/find_24x24.png" width="24" height="24" alt="Buscar Documento">
						</a>
					</td>
<%	}%> <!-- TODO: Reportes ocultar para producción, no esta 100% funcional -->
					<td>&nbsp;</td>
					<td width="16">
						<a href="Reportes.jsp" target="main" onmouseover="window.status='Reportes';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/report.png" width="24" height="24" alt="Reportes">
						</a>
					</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td width="16">
						<a href="../refreshtree" onmouseover="window.status='Actualizar arbol';return true;" onmouseout="window.status='';return true;">
							<img src="../imagenes/tree/refresh_24x24.png" width="24" height="24" alt="Actualizar arbol">
						</a>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td height="<%="g".equals(vista)? "94": "70"%>%">
		<div id="imgDiv" style="overflow: auto; width: 225px; height: 100%; background-color: #FFFFFF; border-right: #333399 1px solid; border-top: #333399 1px solid; border-left: #333399 1px solid; border-bottom: #333399 1px solid">
		<table border="0" cellspacing="0" cellpadding="0">
			<tree:tree tree="arbol.modelo" node="arbol.nodo">
				<tr>
					<td nowrap>
					<table cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td nowrap><tree:nodeIndent node="arbol.nodo" indentationType="type"><tree:nodeIndentVerticalLine indentationType="type"><img src="../imagenes/tree/verticalLine.gif"></tree:nodeIndentVerticalLine><tree:nodeIndentBlankSpace indentationType="type"></tree:nodeIndentBlankSpace></tree:nodeIndent></td>
							<tree:nodeMatch node="arbol.nodo" expanded="false" isLastChild="true" type="carpeta.root">
								<td nowrap><a href="ArbolExpediente.jsp?expand=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedLastNode.gif" border="0"></a><img src="../imagenes/tree/closedFolder.png" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="true" isLastChild="true" type="carpeta.root">
								<td nowrap><a href="ArbolExpediente.jsp?collapse=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Cerrar carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/expandedLastNode.gif" border="0"></a><img src="../imagenes/tree/openFolder.png" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" isLastChild="false" type="carpeta.hija">
								<td nowrap><a href="ArbolExpediente.jsp?expand=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedMidNode.gif" border="0"></a><img src="../imagenes/tree/closedFolder.png" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="true" isLastChild="false" type="carpeta.hija">
								<td nowrap><a href="ArbolExpediente.jsp?collapse=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Cerrar carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/expandedMidNode.gif" border="0"></a><img src="../imagenes/tree/openFolder.png" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" isLastChild="true" type="carpeta.hija">
								<td nowrap><a href="ArbolExpediente.jsp?expand=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedLastNode.gif" border="0"></a><img src="../imagenes/tree/closedFolder.png" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="true" isLastChild="true" type="carpeta.hija">
								<td nowrap><a href="ArbolExpediente.jsp?collapse=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Cerrar carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/expandedLastNode.gif" border="0"></a><img src="../imagenes/tree/openFolder.png" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" isLastChild="false" type="carpeta.prtgda">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	Carpeta cp = (Carpeta) request.getAttribute("nodo.objeto"); %>
							<%	if (!cp.isOpen()) { %>
								<script language="JavaScript">
								function <tree:nodeId node="arbol.nodo"/>() {
									var intentos = 0;
									var password = "<%=cp.getPassword()%>";

									while (intentos < 3) {
										var answer = prompt("Introduzca la contrase\u00F1a de está Carpeta","");
										if (answer != null) {
											if (hex_md5(answer) == password) {
												window.open("ArbolExpediente.jsp?expand=<tree:nodeId node='arbol.nodo'/>#<tree:nodeId node='arbol.nodo'/>","left");
												return;
											}
											alert("Contrase\u00F1a inválida.\nIntentelo nuevamente.");
										} else {
											return;
										}
										intentos += 1;
									}
								}
								</script>
								<td nowrap><a href="javascript:<tree:nodeId node="arbol.nodo"/>()" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedMidNode.gif" border="0"></a><img src="../imagenes/tree/closedFolderPadlock.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							<%	} else { %>
								<td nowrap><a href="ArbolExpediente.jsp?expand=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedMidNode.gif" border="0"></a><img src="../imagenes/tree/closedFolderPadlock.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							<%	} %>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="true" isLastChild="false" type="carpeta.prtgda">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	cp = (Carpeta) request.getAttribute("nodo.objeto"); cp.setOpen(true); %>
								<td nowrap><a href="ArbolExpediente.jsp?collapse=<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Cerrar carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/expandedMidNode.gif" border="0"></a><img src="../imagenes/tree/openFolderPadlock.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" isLastChild="true" type="carpeta.prtgda">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	cp = (Carpeta) request.getAttribute("nodo.objeto"); %>
							<%	if (!cp.isOpen()) { %>
								<script language="JavaScript">
								function <tree:nodeId node="arbol.nodo"/>() {
									var intentos = 0;
									var password = "<%=cp.getPassword()%>";

									while (intentos < 3) {
										var answer = prompt("Introduzca la contraseña de está Carpeta","");
										if (answer != null) {
											if (hex_md5(answer) == password) {
												window.open("ArbolExpediente.jsp?expand=<tree:nodeId node='arbol.nodo'/>#<tree:nodeId node='arbol.nodo'/>","left");
												return;
											}
											alert("Contraseña inválida.\nIntentelo nuevamente.");
										} else {
											return;
										}
										intentos += 1;
									}
								}
								</script>
								<td nowrap><a href="javascript:<tree:nodeId node="arbol.nodo"/>()" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedLastNode.gif" border="0"></a><img src="../imagenes/tree/closedFolderPadlock.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							<%	} else { %>
								<td nowrap><a href="ArbolExpediente.jsp?expand=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedLastNode.gif" border="0"></a><img src="../imagenes/tree/closedFolderPadlock.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							<%	} %>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="true" isLastChild="true" type="carpeta.prtgda">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	cp = (Carpeta) request.getAttribute("nodo.objeto"); cp.setOpen(true); %>
								<td nowrap><a href="ArbolExpediente.jsp?collapse=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Cerrar carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/expandedLastNode.gif" border="0"></a><img src="../imagenes/tree/openFolderPadlock.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" isLastChild="false" type="gaveta.root">
								<td nowrap><a href="ArbolExpediente.jsp?expand=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedMidNode.gif" border="0"></a><img src="../imagenes/tree/closedRootLocker.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="true" isLastChild="false" type="gaveta.root">
								<td nowrap><a href="ArbolExpediente.jsp?collapse=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Cerrar carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/expandedMidNode.gif" border="0"></a><img src="../imagenes/tree/openRootLocker.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" isLastChild="true" type="gaveta.root">
								<td nowrap><a href="ArbolExpediente.jsp?expand=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/collapsedLastNode.gif" border="0"></a><img src="../imagenes/tree/closedRootLocker.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="true" isLastChild="true" type="gaveta.root">
								<td nowrap><a href="ArbolExpediente.jsp?collapse=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Cerrar carpeta';return true;" onmouseout="window.status='';return true;"><img src="../imagenes/tree/expandedLastNode.gif" border="0"></a><img src="../imagenes/tree/openRootLocker.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" hasChildren="false" isLastChild="false" type="docto.frtimx">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	d = (Documento) request.getAttribute("nodo.objeto"); %>
								<td nowrap><img src="../imagenes/tree/noChildrenMidNode.gif"><img src="../imagenes/tree/<%=d.noEstaCompartido()? "image_24x24.png": "image_24x24_shared.png"%>" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" hasChildren="false" isLastChild="true" type="docto.frtimx">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	d = (Documento) request.getAttribute("nodo.objeto"); %>
								<td nowrap><img src="../imagenes/tree/noChildrenLastNode.gif"><img src="../imagenes/tree/<%=d.noEstaCompartido()? "image_24x24.png": "image_24x24_shared.png"%>" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" hasChildren="false" isLastChild="false" type="docto.externo">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	d = (Documento) request.getAttribute("nodo.objeto"); %>
								<td nowrap><img src="../imagenes/tree/noChildrenMidNode.gif"><img align="middle" src="../imagenes/tree/<%=d.noEstaCompartido()? "files_24x24.png": "files_24x24_shared.png"%>" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" hasChildren="false" isLastChild="true" type="docto.externo">
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
								<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
							<%	d = (Documento) request.getAttribute("nodo.objeto"); %>
								<td nowrap><img src="../imagenes/tree/noChildrenLastNode.gif"><img align="middle" src="../imagenes/tree/<%=d.noEstaCompartido()? "files_24x24.png": "files_24x24_shared.png"%>" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" hasChildren="false" isLastChild="false" type="gaveta.hija">
								<td nowrap><img src="../imagenes/tree/noChildrenMidNode.gif"><img src="../imagenes/tree/closedLocker.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<tree:nodeMatch node="arbol.nodo" expanded="false" hasChildren="false" isLastChild="true" type="gaveta.hija">
								<td nowrap><img src="../imagenes/tree/noChildrenLastNode.gif"><img src="../imagenes/tree/closedLocker.gif" alt="<tree:nodeToolTip node="arbol.nodo"/>"></td>
							</tree:nodeMatch>
							<td valign="top" nowrap>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="carpeta.root">
									<script language="JavaScript">setTimeout("botonesCrpta(true)", s*1000)</script>
									<a name="<tree:nodeId node="arbol.nodo"/>"><span><i><tree:nodeName node="arbol.nodo" /></i></span></a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="carpeta.hija">
									<script language="JavaScript">setTimeout("botonesCrpta(false)", 1000)</script>
									<a name="<tree:nodeId node="arbol.nodo"/>"><span><i><tree:nodeName node="arbol.nodo" /></i></span></a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="carpeta.prtgda">
									<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
								<%	cp = (Carpeta) request.getAttribute("nodo.objeto"); %>
								<%	if (cp.isOpen()) { %>
									<script language="JavaScript">setTimeout("botonesCrpta(false)", 1000)</script>
								<%	} else { %>
									<script language="JavaScript">setTimeout("botonesDocto(false,false)", 1000)</script>
								<%	} %>
									<a name="<tree:nodeId node="arbol.nodo"/>"><span><i><tree:nodeName node="arbol.nodo" /></i></span></a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="false" type="carpeta.*">
									<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
								<%	cp = (Carpeta) request.getAttribute("nodo.objeto"); %>
								<%	if (cp.isProtected() && !cp.isOpen()) { %>
									<script language="JavaScript">
									function <tree:nodeId node="arbol.nodo"/>() {
										var carpeta='<tree:nodeId node="arbol.nodo"/>';
										var password = "<%=cp.getPassword()%>";
										var accion="ArbolExpediente.jsp?expand=<tree:nodeId node='arbol.nodo'/>#<tree:nodeId node='arbol.nodo'/>";
										var parametro='left';	
									if(!verificaCookie(carpeta,accion,parametro)){									
										mostrarVentana(tituloA,msjA,horasC,carpeta,password,accion,parametro);
									}//ifVerifica
									}
									</script>
								<%	} %>
								<% if ((cp.isProtected() && cp.isOpen()) || (!cp.isProtected())) { %>
								<td nowrap>
									<a name="<tree:nodeId node="arbol.nodo"/>" href="ArbolExpediente.jsp?select=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;" title="<tree:nodeToolTip node="arbol.nodo"/>">
										<span><tree:nodeName node="arbol.nodo" /></span>
									</a>
								</td>
								<%	} else { %>
								<td nowrap>
									<a name="<tree:nodeId node="arbol.nodo"/>" href="javascript:<tree:nodeId node="arbol.nodo"/>()" onmouseover="window.status='Abrir carpeta';return true;" onmouseout="window.status='';return true;">
										<span><tree:nodeName node="arbol.nodo" /></span>
									</a>
								</td>
								<%	} %>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="gaveta.root">
									<script language="JavaScript">setTimeout("botonesGavts()", 1000)</script>
										<a name="<tree:nodeId node="arbol.nodo"/>"><span><i><tree:nodeName node="arbol.nodo" /></i></span></a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="false" type="gaveta.root">
									<a name="<tree:nodeId node="arbol.nodo"/>" href="ArbolExpediente.jsp?select=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Seleccionar gaveta';return true;" onmouseout="window.status='';return true;" title="<tree:nodeToolTip node="arbol.nodo"/>">
										<span><tree:nodeName node="arbol.nodo" /></span>
									</a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="gaveta.hija">
									<script language="JavaScript">setTimeout("botonesGavts()", 1000)</script>
									<script language="JavaScript">window.open("ResultadosBusquedaExpedientes.jsp?select=<tree:nodeId node="arbol.nodo"/>&tipoAccion=expedientes", "main")</script>
									<a name="<tree:nodeId node="arbol.nodo"/>" href="ResultadosBusquedaExpedientes.jsp?select=<tree:nodeId node="arbol.nodo"/>&tipoAccion=expedientes" target="main" onmouseover="window.status='Seleccionar gaveta';return true;" onmouseout="window.status='';return true;">
									<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
									<%	ap = (imx_aplicacion) request.getAttribute("nodo.objeto"); 
										desc = ap.getDescripcion(); %>
										<!-- span><i>cambia_valores(<tree:nodeName node="arbol.nodo" />)</i></span -->
										<span><i><%= desc %></i></span >
									</a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="false" type="gaveta.hija">
									<a name="<tree:nodeId node="arbol.nodo"/>" href="ArbolExpediente.jsp?select=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Seleccionar gaveta';return true;" onmouseout="window.status='';return true;" title="<tree:nodeToolTip node="arbol.nodo"/>">
									<tree:detachNodeObject node="arbol.nodo" detachedObject="nodo.objeto" />
									<%	ap = (imx_aplicacion) request.getAttribute("nodo.objeto"); 
										 desc = ap.getDescripcion(); %>
										<!-- span>cambia_valores(<tree:nodeName node="arbol.nodo" />)</span -->
										<span><i><%= desc %></i></span >
									</a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="carpeta.exproot">
								<% c = (Carpeta) n.getObject(); %>
									<script language="JavaScript">setTimeout("botonesCrpta(true)", 1000)</script>
									<!-- Angel: La linea de abajo la comento para que muestre el arbol de Documentos sin quitar la lista de expedientes  -->
									<!--<script language="JavaScript">window.open("../showexpedients?select=<tree:nodeId node="arbol.nodo"/>&expedient=true", "main")</script>  -->
									<a name="<tree:nodeId node="arbol.nodo"/>"><span><i><tree:nodeName node="arbol.nodo" /></i></span></a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="docto.externo">
									<script language="JavaScript">setTimeout("botonesDocto(false,false)", 1000)</script>
									<a name="<tree:nodeId node="arbol.nodo"/>" href="RedirectDocumento.jsp?select=<tree:selectedNodeId/>" target="main" onmouseover="window.status='Abrir documento';return true;" onmouseout="window.status='';return true;">
										<span><i><tree:nodeName node="arbol.nodo" /></i></span>
									</a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="true" type="docto.frtimx">
									<script language="JavaScript">setTimeout("botonesDocto(false,false)", 1000)</script>
									<a name="<tree:nodeId node="arbol.nodo"/>" href="RedirectDocumento.jsp?select=<tree:selectedNodeId/>" target="main" onmouseover="window.status='Abrir documento';return true;" onmouseout="window.status='';return true;">
										<span><i><tree:nodeName node="arbol.nodo" /></i></span>
									</a>
								</tree:nodeMatch>
								<tree:nodeMatch node="arbol.nodo" selected="false" type="docto.*">
									<a name="<tree:nodeId node="arbol.nodo"/>" href="ArbolExpediente.jsp?select=<tree:nodeId node="arbol.nodo"/>#<tree:nodeId node="arbol.nodo"/>" onmouseover="window.status='Seleccionar documento';return true;" onmouseout="window.status='';return true;" title="<tree:nodeToolTip node="arbol.nodo"/>">
										<span><tree:nodeName node="arbol.nodo" /></span>
									</a>
								</tree:nodeMatch>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</tree:tree>
		</table>
		</div><!--fin del div arbol-->
		</td>
	</tr>

<%	if (!"g".equals(vista)) { %>
	<tr>
		<td height="30%"><img src="../imagenes/espacio.gif" height="4" width="20"><br>
		<div id="divDesc" style="overflow: auto; width: 225; height: 150px">
<%	if (n != null) {
		if (n.getType().startsWith("carpeta")) {
			c = (Carpeta) n.getObject(); %>
			<table width="100%" height="90%" border="0" cellspacing="0" cellpadding="0"  class="det">
				<tr>
					<!--td class="fdet">&nbsp;Detalle de carpeta</td-->
					<td class="fdetgpcolors">&nbsp;Detalle de carpeta 
					<%if(creacion && u.getTipoOperacion()=='4'){ %>
					 | <a href="javascript: ModificaNombre();"><font class="gpcolors" size="1">Modificar</font></a>
					<%}%>
					</td>
				</tr>
				<tr>
					<td><font class="det">Nombre:</font><font style="color:#000;font-weight:bold;"><%=c.getNombreCarpeta() %></font></td>
				</tr>
				<tr>
					<td><font class="det">Descripción:</font><font style="color:#000;font-weight:bold;"><%=c.getDescripcion() %></font></td>
				</tr>
				<tr>
					<td class="det">Contiene: <input type="text" size="28" value="<%=c.getContenidoCarpeta()%>"/ readonly></td>
				</tr>
				<tr>
					<td class="det">Creada el: <input type="text" size="25" value="<%=c.getFechaCarpetaCreacion()%>"/ readonly ></td>
				</tr>
				<!--tr>
					<td class="det">Tama&ntilde;o <input type="text" size="10" value="< %=c.getTamanoBytesTexto()% >"/ readonly ></td>
				</tr-->
				<tr>
					<td class="det">Protegida: <input type="text" size="2" value="<%=(c.isProtected()? "Sí": "No")%>"/ readonly ></td>
				</tr>
				
				<tr>
					<td class="fdetf"></td>
				</tr>

			</table>
<%		} else if (n.getType().startsWith("docto")) {
			d = (Documento) n.getObject(); %>
			<table width="100%" height="90%" border="0" cellspacing="0" cellpadding="0" class="det">
				<tr>
					<!--<td height="20" align="left" valign="middle" class="fdet">&nbsp;Detalle de documento</td>-->
					<!--RMN - AGREGADO EL 25-01-2006 PARA MODIFICAR ATR DESC DOCUMENTOS-->
					
					<td height="20" align="left" valign="middle" class="fdetgpcolors">&nbsp;Detalle de documento <!--  <a href="javascript: DoctosDescAtr();"><font class="gpcolors" size="1">Desc.</font></a>  -->
					<%if(creacion && u.getTipoOperacion()=='4'){ %>
						 | <a href="javascript: ModificaNombre();"><font class="gpcolors" size="1">Modificar</font></a>
					<%}%>
					</td>
				</tr>
				<tr>
					<td><font class="det">Nombre:</font><font style="color:#000;font-weight:bold;"><%=d.getNombreDocumento() %></font></td>
				</tr>
				<tr>
					<td><font class="det">Descripción:</font><font style="color:#000;font-weight:bold;"><%=d.getDescripcion() %></font></td>
				</tr>
				<tr>
					<td class="det">Contiene: <input type="text" size="25" value="<%=d.getDescripcionPaginas()%>" readonly></td>
				</tr>
				<tr>
					<td class="det">Tama&ntilde;o:&nbsp;<input type="text" size="20" readonly value="<%=d.getTamanoBytesTexto()%>"></td>
				</tr>
				<tr>
					<td class="det">Creado el: <input type="text" size="25" readonly value="<%=d.getFechaDocumentoCreacion()%>"></td>
				</tr>
				<tr>
					<td class="det">Modificado el:<input type="text" size="25" readonly value="<%=d.getFechaDocumentoModificacion()%>"></td>
				</tr>
				<tr>
					<td class="fdetf"><input type="hidden" size="20" readonly value="<%=d.getEstadoDocumentoTipoDocto()%>"><input type="hidden" readonly value="<%=d.getClaseDocumentoTexto()%>"><input type="hidden" readonly value="<%=d.getMateria()%>"></td>
				</tr>
				
				
				
				
			</table>
<%	}
		} %>
		</div>
		</td>
	</tr>
<%	} %>
	<tr>
		<td height="1%">
			<table width="100%" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<div align="center" style="position: relative; top: 13px; width: 100%; height: 5px; z-index: 1"><font size="1" color="#000000"><%="".equals(sLabelQuotaUsedTotal)?"0k / 50Mb":sLabelQuotaUsedTotal%></font></div>
						<table border="0" width="100%" style="border-right: #000000 1px solid; border-top: #000000 1px solid; border-left: #000000 1px solid; border-bottom: #000000 1px solid" bgcolor="#dee7f0" cellspacing="1" cellpadding="1">
							<tr>
								<td>
								<table border="0" width="<%=QuotaPercent%>" bgcolor="<%=sColorQuotaPercent%>">

								<!--table border="0" width="50%" bgcolor="#70bd7a"><!--0-50-->
								<!--table border="0" width="89%" bgcolor="#fbc159"><!--51-89-->
								<!--table border="0" width="90%" bgcolor="#fe0000"><!--90-100-->
									<tr>
										<td width="100%" height="5px"></td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<%
String val = request.getParameter("fromBusqueda");
String image_index_act = request.getParameter("image.index");

if(request.getParameter("fromBusqueda")!=null){
	Documento d = (Documento)tree.findNode(nodeId).getObject();
	String ligalocal = null;
	if ("si".equals(request.getParameter("fromBusqueda"))){ 	
		ligalocal = request.getContextPath()+"/"+"jsp/Visualizador.jsp"+"?select="+nodeId;
					//("EXTERNO".equals(d.getNombreTipoDocto())? //"filestore;jsessionid="+session.getId()
	} else if("ocr".equals(request.getParameter("fromBusqueda"))){
		ligalocal = request.getContextPath()+"/"+ "jsp/Visualizador.jsp"+"?select="+nodeId 
					+ "&image.index=" + (request.getParameter("image.index")==null? "0":request.getParameter("image.index"));
	}				 
			         
%>
<script>
	//top.main.location.href='ligaloca';
	top.frames['main'].window.navigate('<%=ligalocal%>');
</script>

<%}%>
</body>
</html>