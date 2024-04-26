<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,javax.naming.*,java.sql.*,com.syc.user.*,com.syc.imaxfile.*,com.jenkov.prizetags.tree.itf.*,com.syc.tree.*,com.syc.utils.*,org.apache.lucene.document.Document;" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Buscar Documento</title>
<link rel="stylesheet" href="../css/fortimax_sistema.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="../css/scrolltable.css">
<script language="javascript" src="../js/scrolltable.js"></script>
<script language="javascript" type="text/javascript" src="../js/fortimax_sistema.js"></script>

<link rel="stylesheet" type="text/css" href="../css/demo_table_jui.css">
<link rel="stylesheet" type="text/css" href="../css/themes/start/jquery-ui-1.8rc1.custom.css">
<link rel="stylesheet" type="text/css" href="../css/dTableManager.css">
<script language="javascript" src="../js/jquery-1.3.2.min.js"></script>
<script language="javascript" src="../js/jquery.dataTables.js"></script>  
<script type="text/javascript" charset="utf-8" src="../js/ZeroClipboard.js"></script>
<script type="text/javascript" charset="utf-8" src="../js/TableTools.js"></script>

<%
//ORDEN DE LOS CAMPOS DEL QUERY ORIGINAL
//0.- d.nombre_documento 
//1- d.descripcion
//2.- d.fh_creacion
//3.- d.fh_modificacion
//4.- d.titulo_aplicacion
//5.- d.id_gabinete
//6.- d.id_carpeta_padre
//7.- d.id_documento 

Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
if (u == null) {
	response.sendRedirect("../index.jsp");
	return;
}
 
String titulo_aplicacion=request.getParameter("gaveta");
boolean isMyDocs = "Mis Documentos".equals(titulo_aplicacion);
boolean todaGaveta = "Todas".equals(titulo_aplicacion);
titulo_aplicacion = isMyDocs ? "USR_GRALES" : titulo_aplicacion;
titulo_aplicacion = todaGaveta ? "" : titulo_aplicacion;

%>
</head>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" marginwidth="0" marginheight="0">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td>
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr>
				<td align="center" class="bordetit">B&uacute;squeda de Documentos en <%=!isMyDocs&!todaGaveta?"Gaveta":""%> <strong><%=isMyDocs ? "Mis Documentos" : titulo_aplicacion%><%=todaGaveta ? "Todas las Gavetas":""%></strong></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td>
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr>
				<td align="right" ><a href="../jsp/BuscaDocumento.jsp?nueva=true">Nueva b&uacute;squeda</a></td>
			</tr>
		</table>
		</td>
	</tr>	
</table>
<div id="tableContainer" class="tableContainer"></div>
    
	<script type="text/javascript" charset="UTF-8">
		var oTable;
		var oDetalles;
		var idExp = new Array();
		var MAX_NUM_COL=6;
		var pag = "<%=request.getParameter("pag")%>";
	    var plantilla = "<%=request.getParameter("plantilla")%>";
	    var datos = "<%=request.getParameter("datos")%>";
	    var gaveta = "<%=titulo_aplicacion%>";
	    	
		$.post("../showtipodocumento",{ gaveta:gaveta,pag:pag,plantilla:plantilla,datos:datos },function(json){
				$('#tableContainer').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="example">'+		
				'<thead> '+
				'<tr>'+
				'</tr> '+
				'</thead> '+
				'</table>' );
				oTable = $('#example').dataTable(
				 { 
					 
				 	"fnInitComplete": function() {
					 	var oSettings = oTable.fnSettings();
						if(oSettings.aoColumns.length > MAX_NUM_COL)
							for (i = MAX_NUM_COL; i < oSettings.aoColumns.length; i++)
								{fnShowHide( i );}
						fnOpenClose(oSettings);
					},
					"fnRowCallback": function( nRow, aData, iDisplayIndex ) {
					$(nRow).attr('id', aData[0]);
					return nRow;
					},
				 	//"fnServerData": fnDataTablesPipeline,
				 	"iCookieDuration": 60*60*24*7,
				 	"bStateSave": false,
				 	"bSortClasses": false,
				 	"iDisplayLength": 25,
				 	"bJQueryUI": true,
					"oLanguage": {"sUrl": "../css/language/dt_es.txt"},
					"sPaginationType": "full_numbers",
				 	"bProcessing": true,
					"bServerSide": false,
					"aaData": json.aaData,
					"aoColumns": json.aoColumns,
					"aaSorting": [[1, 'asc']],
					"bAutoWidth": false,
					"sDom": '<"H"Tlfr>t<"F"ip>',
					//"sDom": '<"H"fr><"dataTables_length"T>t<"F"ip>',
					//"sDom": 'T<"dataTables_filter">lfrtip',
					"oTableTools": {
						"sSwfPath": "<%=basePath%>/swf/copy_cvs_xls_pdf.swf",
						"aButtons": [
										{"sExtends":"copy","sButtonText": " Copiar "}, 
										{"sExtends":"csv","sButtonText": " Exportar CSV "}, 
										{"sExtends":"xls","sButtonText": " Exportar Excel "}, 
										{"sExtends":"pdf","sButtonText": " Exportar PDF "},
										{"sExtends":"print","sButtonText": " Mostrar todo "}
									]							
						}

			      });
		      },"json" );
			
	      	function fn_default(obj) { 
				var sReturn = obj.aData[ obj.iDataColumn ]; 
				return sReturn; 
			}
	      	
	      	function fnOpenClose ( oSettings )
	    	{
	    	
	    		var Old_Location="";
	    		$('td img', oTable.fnGetNodes() ).each( function () {
	    			var imagen = this;
	    			$(this).parent().parent().click( function () {
	    				var nTr = imagen.parentNode.parentNode;
	    				var aData = oTable.fnGetData( nTr );
	    				var Fila_Pos = oTable.fnGetPosition( imagen.parentNode );
	    				if ( imagen.src.match('details_close') )
	    				{
	    					imagen.src = "imagenes/details_open.png";
	    					var nRemove = $(nTr).next()[0];
	    					nRemove.parentNode.removeChild( nRemove );
	    					parent.frames.left.location='<%=basePath%>/jsp/ArbolExpediente.jsp?select=Gaveta&arbol.tipo=g';		
	    					oTable.fnClose( nTr );
	    					
	    				}
	    				else
	    				{
	    					Old_Location=parent.frames.left.location;
	    					parent.frames.left.location='<%=basePath%>getexpedient?select='+select+'&id_gabinete='+aData[0]+'&expedient=true';
	    					/*Abre los Detalles de la Fila*/
	    					imagen.src = "imagenes/details_close.png";
	    					oTable.fnOpen( nTr, fnFormatDetails(nTr, Fila_Pos), 'details' );
	    				}
	    			} );
	    		} );
	    	}
	</script>
</body>
</html>
