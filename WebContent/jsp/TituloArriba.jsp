<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.syc.user.*,com.syc.utils.*,com.jenkov.prizetags.tree.itf.*,com.syc.tree.*" %>

<% Usuario u = (Usuario)session.getAttribute(ParametersInterface.USER_KEY); %>

<%

   try{

   	while(true){

	    if(session.getAttribute(ParametersInterface.TREE_KEY)==null)

		   	Thread.sleep(2000);

		else

			break;

	}

   }

   catch(Exception e){

   }

   ITree tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);

   ITreeNode node = tree.getRoot();

	String url =

		request.getScheme()

			+ "://"

			+ request.getServerName()

			+ ":"

			+ request.getServerPort()

			+ request.getContextPath()

			+ "/addimaxkeeper";

			

%>



<html>

<head>

<title>Fortimax - Barra de Herramientas</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta http-equiv="cache-control" content="no-cache">

<link rel="shortcut icon" href="../../favicon.ico"/>

<link href="../css/fortimax_sistema.css" rel="stylesheet" type="text/css">

<style type="text/css">

.menuTop {

color: #FFFFFF;;



}



A:LINK {

	font-family: "Verdana, Arial, Helvetica, sans-serif";

	color : #FFFFFF;

	text-decoration : none;

	font-size : 10pt;

	font-style: normal;

}



A:VISITED {

	font-family: "Verdana, Arial, Helvetica, sans-serif";

	color : #FFFFFF;

	text-decoration : none;

	font-size : 10pt;

	font-style: normal;

}



A:HOVER {

	font-family: "Verdana, Arial, Helvetica, sans-serif";

	color : #FFFFFF;

	text-decoration : none;

	font-size : 10pt;

	font-style: normal;

}



 </style>

<script language="javascript" type="text/javascript" src="../js/fortimax_sistema.js"></script>

<script language="javascript">
	var cargado=1;
</script>

<style type="text/css">

<!--

body {

	background-image:  url(../imagenes/interfaz/i_sistema_fondo_menu.gif);

	background-repeat:   repeat-x;

}

@media print {

	body {

		display: none;

	}

}

-->

</style>

</head>

<body>	

<table width="100%" border="0" cellpadding="0" cellspacing="0" id="Tabla_01">

  <tr>

    <td rowspan="3" valign="top" align="left"><img src="../imagenes/interfaz/i_sistema_logotipo.gif" width="232" height="71"></td>

    <td align="right">

	<table border="0" cellpadding="0" cellspacing="0" id="menu1" style="width: 721px; height: 28px;" width="721" height="28">

      <tr>
		<!-- Angel 1/04/09 -->
        <td>&nbsp;<br></td>
        <td>
        	<a href="javascript:inicio();"><img width="20" hspace="0" height="20" border="0" src="../imagenes/menup/b_inicio.gif" alt="Inicio"></a>
        </td>
        <td>
        	<span style="font-size:18px;color:white;">&nbsp;&nbsp;|&nbsp;&nbsp;</span>
        </td>
        <td><br></td>
        <!--  <td nowrap><a href="../portal/registro-exitoso.jsp" target="main" onMouseOver="MM_displayStatusMsg('Verificar instalación de JRE en tu PC');return document.MM_returnValue" onMouseOut="MM_displayStatusMsg('Fortimax.com');return document.MM_returnValue"><img src="../imagenes/menup/b_opciones.gif" width="25" height="24" hspace="0" border="0">Verificar instalación </a></td>

        <td><img src="../imagenes/interfaz/i_sistema_separador.gif" width="30" height="24"></td>-->

        <td nowrap class="menuTop">
        	<a href="javascript:CambiaPassword();" onMouseOver="MM_displayStatusMsg('Cambiar mi contraseña');MM_displayStatusMsg('Fortimax.com');return document.MM_returnValue">
        		<img src="../imagenes/menup/b_password.gif" width="20" height="20" hspace="0" border="0">
        		<span class="menuTop">Cambiar contrase&ntilde;a</span>
        	</a>
        </td>
        <td>
        	<span style="font-size:18px;color:white;">&nbsp;&nbsp;|&nbsp;&nbsp;</span>
        </td>
        <td nowrap>
        	<a href="javascript:Guias();" onMouseOver="MM_displayStatusMsg('Guías de uso');return document.MM_returnValue" onMouseOut="MM_displayStatusMsg('Fortimax.com');return document.MM_returnValue"><img src="../imagenes/menup/b_manual.gif" width="24" height="20" hspace="0" border="0"><span class="menuTop">Gu&iacute;as de uso</span> </a>
        </td>

        <td nowrap><span style="font-size:18px;color:white;">&nbsp;&nbsp;|&nbsp;&nbsp;</span></td>

        <td nowrap>
        	<a href="DescargaScanTools.jsp" target="main" onMouseOver="MM_displayStatusMsg('Descarga de herramienta para digitalización Fortimax');return document.MM_returnValue" onMouseOut="MM_displayStatusMsg('Fortimax.com');return document.MM_returnValue"><span class="menuTop">Descargar utiler&iacute;a digitalización</span></a>
        </td>

        <!--  <td><img src="../imagenes/interfaz/i_sistema_separador.gif" width="30" height="24"><img src="../imagenes/interfaz/i_sistema_fortimax.gif" alt="" width="147" height="25"></td>-->

        <td>
        	<span style="font-size:18px;color:white;">&nbsp;&nbsp;|&nbsp;&nbsp;</span>
        </td>

        <td nowrap>
        	<a href="javascript:funcion_salida();" onMouseOver="MM_displayStatusMsg('Salir de sesión en FortImax');return document.MM_returnValue" onMouseOut="MM_displayStatusMsg('FortImax.com');return document.MM_returnValue"><strong><span class="menuTop">Salir</span><img src="../imagenes/menup/b_salir.gif" width="20" height="20" hspace="0" border="0"></strong></a>
        </td>
	</tr>
    </table></td>

    <td><img src="../imagenes/espacio.gif" width="1" height="24" alt=""></td>

  </tr>

  <tr>

    <td>&nbsp;</td>

    <td><img src="../imagenes/espacio.gif" width="1" height="22" alt=""></td>

  </tr>

  <tr>

    <td rowspan="2"><table width="100%" border="0" cellspacing="0" cellpadding="0">

      <tr>

        <td align="right" width="87%">

<!--tabla de botones de operaciones-->

		<table style="display:none;"  cellspacing="0" cellpadding="0" border="0" id="boperaciones" style="display:block">

            <tr>

              <td><table  id="btnCrearCarpeta" border="0" cellspacing="0" cellpadding="0" class="A" title="Crear carpeta en el nivel seleccionado" onClick="creaCarpetaDocto('false','0');" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/b_carpeta.gif" width="30" height="44"></td>

                    <td class="A">Crear<br>

                      carpeta</td>

                  </tr>

              </table></td>

             
              <td><img id="sepaDigitalizar" src="../imagenes/menup/divisor.gif" width="4" height="34"></td>
              <td><table   id="btnDigitalizar" border="0" cellspacing="0" cellpadding="0" class="A" title="Digitalizar documentos con el escáner"  onClick="creaCarpetaDocto('true','1');" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/b_digitalizar.gif" width="35" height="44"></td>

                    <td class="A">Digitalizar<br>

                      documento</td>

                  </tr>

              </table></td>


              <!--td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

              <td><table border="0" cellspacing="0" cellpadding="0" class="A" title="Think Free Office"  onClick="void window.open('../thinkfree.html','main');" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/thinkfree.gif"></td>

                  </tr>

              </table></td-->

              <!--td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

              <td><table border="0" cellspacing="0" cellpadding="0" class="A" title="Crear álbum de fotos"  onClick="creaCarpetaDocto('true','2');" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/b_album.gif" width="36" height="44"></td>

                    <td class="A">álbum<br>

                  de<nobr> fotos</td>

                  </tr>

              </table></td-->

              <td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

              <td><table  id="btnRespaldarArchivo" border="0" cellspacing="0" cellpadding="0" class="A" title="Respaldar archivo de office&reg; o de cualquier otro programa" onClick="importaDocto();" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/b_respaldar_archivo.gif" width="50" height="44"></td>

                    <td class="A">Respaldar<br>

                      archivo</td>

                  </tr>

              </table></td>

              <td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

              <td><table  id="btnRespaldarCarpeta" border="0" cellspacing="0" cellpadding="0" class="A" title="Respaldar carpeta de mi PC" onClick="respaldaCarpeta();" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/b_respaldar_carpeta.gif" width="53" height="44"></td>

                    <td class="A">Respaldar<br>

                      carpeta</td>

                  </tr>

              </table></td>

              <td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>
					

              <td><table  id="btnRecuperarCarpeta" border="0" cellspacing="0" cellpadding="0" class="A" title="Recuperar carpeta a mi PC" onClick="exportaCarpeta();" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">
                  <tr>
                    <td><img src="../imagenes/menup/b_recuperar.gif" width="56" height="44"></td>
                    <td class="A">Recuperar<br> a mi PC</td>
                  </tr>
              </table></td>

              <!--td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

              <td><table  border="0" cellspacing="0" cellpadding="0" class="A" title="Eliminar selección actual" onClick="elimina();" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/b_eliminar.gif" width="36" height="44"></td>

                    <td class="A">Eliminar</td>

                  </tr>

              </table>

              </td-->

            </tr>

        </table>

<!-- tabla boton editar -->

		    <table cellspacing="0" cellpadding="0" border="0" id="beditardocto" style="display:none" align="right">

		     <tr>

		      <!--td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

	          <td>

	            <table  border="0" cellspacing="0" cellpadding="0" class="A" title="Editar documento" onClick="editaDocto('<%=session.getId()%>');" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

	              <tr>

	                <td><img src="../imagenes/menup/b_editadoc.gif" width="24" height="24"></td>

	                <td class="A">Editar<br>documento</td>

	              </tr>

	            </table>

	          </td-->

	         </tr>

	        </table>



<!-- tabla documentos -->


		    <table cellspacing="0" cellpadding="0" border="0" id="brecuperardoctocarpeta" style="display:none" align="right">

		     <tr>

		      <td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

	          <td>

	            <table  border="0" cellspacing="0" cellpadding="0" class="A" title="Recuperar documento a mi PC" onClick="exportaDocumento();" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

	              <tr>

	                <td><img src="../imagenes/menup/b_recuperar.gif" width="56" height="44"></td>

	                <td class="A">Recuperar<br>a mi PC</td>

	              </tr>

	            </table>

	          </td>

	         </tr>

	        </table>

          </td>

        <td align="right" valign="top">

<!--tabla de boton elimanar-->

		<table border="0" cellpadding="0" cellspacing="0" id="beliminar" style="display:none" align="right">

            <tr>

              <td><img src="../imagenes/menup/divisor.gif" width="4" height="34"></td>

              <td><table  border="0" cellspacing="0" cellpadding="0" class="A" title="Eliminar selección actual" onClick="elimina();" onMouseOver="this.className='OV'" onMouseOut="this.className='A'">

                  <tr>

                    <td><img src="../imagenes/menup/b_eliminar.gif" width="36" height="44"></td>

                    <td class="A">Eliminar</td>

                  </tr>

              </table></td>

            </tr>

        </table></td>

      </tr>

    </table>	</td>

    <td><img src="../imagenes/espacio.gif" width="1" height="23" alt=""></td>

  </tr>

  <tr>

   <td width="232" id="bienvenido">Bienvenido:<strong>&nbsp;<%=u.getNombre()%></strong></td>  

    <td><img src="../imagenes/espacio.gif" width="0" height="19" alt=""></td>

  </tr>

</table>



<script>

function actualizaArbol(){

	top.left.location.reload();

}



var homeid = '<%=request.getParameter("select")%>';

</script>

</body>

</html>