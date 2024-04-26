<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Fortimax </title>
<style type="text/css">
<!--
body {
	background: #42413C;
	margin: 0;
	padding: 0;
	color: #000;
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 13px;
	line-height: 1.4;
}

/* ~~ Element/tag selectors ~~ */
ul, ol, dl { /* Due to variations between browsers, it's best practices to zero padding and margin on lists. For consistency, you can either specify the amounts you want here, or on the list items (LI, DT, DD) they contain. Remember that what you do here will cascade to the .nav list unless you write a more specific selector. */
	padding: 0;
	margin: 0;
}
h1, h2, h3, h4, h5, h6, p {
	margin-top: 0;	 /* removing the top margin gets around an issue where margins can escape from their containing div. The remaining bottom margin will hold it away from any elements that follow. */
	padding-right: 15px;
	padding-left: 15px; /* adding the padding to the sides of the elements within the divs, instead of the divs themselves, gets rid of any box model math. A nested div with side padding can also be used as an alternate method. */
}
a img { /* this selector removes the default blue border displayed in some browsers around an image when it is surrounded by a link */
	border: none;
}
/* ~~ Styling for your site's links must remain in this order - including the group of selectors that create the hover effect. ~~ */
a:link {
	color: #42413C;
	text-decoration: underline; /* unless you style your links to look extremely unique, it's best to provide underlines for quick visual identification */
}
a:visited {
	color: #6E6C64;
	text-decoration: underline;
}
a:hover, a:active, a:focus { /* this group of selectors will give a keyboard navigator the same hover experience as the person using a mouse. */
	text-decoration: none;
}

/* ~~ this fixed width container surrounds the other divs ~~ */
.container {
	width: 960px;
	background: #FFF;
	margin: 0 auto; /* the auto value on the sides, coupled with the width, centers the layout */
}

/* ~~ the header is not given a width. It will extend the full width of your layout. It contains an image placeholder that should be replaced with your own linked logo ~~ */
.header {
	background-color: #6993FF;
	background-image: url(<%=request.getContextPath()%>/error/imagenes/top.png);
}

/* ~~ This is the layout information. ~~ 

1) Padding is only placed on the top and/or bottom of the div. The elements within this div have padding on their sides. This saves you from any "box model math". Keep in mind, if you add any side padding or border to the div itself, it will be added to the width you define to create the *total* width. You may also choose to remove the padding on the element in the div and place a second div within it with no width and the padding necessary for your design.

*/

.content {

	padding: 10px 0;
}

/* ~~ The footer ~~ */
.footer {
	background-color: #6993FF;
	background-image: url(<%=request.getContextPath()%>/error/imagenes/fondo-encabezado.png);
	background-repeat: repeat-x;
	height: 75px;
	width: auto;
	/* [disabled]padding-top: 10px; */
	padding-right: 01;
	/* [disabled]padding-bottom: 10px; */
	padding-left: 01;
}

/* ~~ miscellaneous float/clear classes ~~ */
.fltrt {  /* this class can be used to float an element right in your page. The floated element must precede the element it should be next to on the page. */
	float: right;
	margin-left: 8px;
}
.fltlft { /* this class can be used to float an element left in your page. The floated element must precede the element it should be next to on the page. */
	float: left;
	margin-right: 8px;
}
.clearfloat { /* this class can be placed on a <br /> or empty div as the final element following the last floated div (within the #container) if the #footer is removed or taken out of the #container */
	clear:both;
	height:0;
	font-size: 1px;
	line-height: 0px;
}
.container .footer p {
	color: #FFF;
}
-->
</style></head>

<body>

<div class="container">
  <div class="header"><a href="#"><img src="<%=request.getContextPath()%>/error/imagenes/Fortimax.jpg" alt="Insert Logo Here" name="Insert_logo" width="425" height="85" id="Insert_logo" style="background: #C6D580; display:block; color: #03C;" /><a href="#"></a><!-- end .header --></div>
  <div class="content">
    <table width="960" border="0">
      <tr>
        <td width="865"><h1>La solicitud no puede ser completada, porque no esta autorizado!. </h1></td>
        <td width="85"><span class="header"><a href="#"></a><a href="#"><a href="#"><img src="<%=request.getContextPath()%>/error/imagenes/acceso_no_autorizado.jpg" alt="Seguridad" width="85" height="85" align="right" /></a></a></span></td>
      </tr>
    </table>
<BR>
<h2>Verifique lo siguiente:</h2>
    <p>1.- Asegurese de que  se ha Registrado y su session sigue activa.<BR>
      2.- Si continua el problema contacte con el Administrador.    </p>
<h2>Error 400</h2>
<p>* Cualquier intento de acceso no autorizado, es registrado con su informacion. </p>
  </div>
  <div class="footer">
  <p>    D.R. ®2006 Sistemas y Computadores de Gestión S.A. de C.V.<br />
Av. Revolución 2042, Col. La Otra Banda, Del. Álvaro Obregón, C.P 01090, México D.F. 
  <!-- end .footer --></div>
  <!-- end .container --></div>
</body>
</html>