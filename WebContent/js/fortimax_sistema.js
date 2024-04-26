function MM_displayStatusMsg(msgStr) {
	status=msgStr;
	document.MM_returnValue = true;
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function inicio(){

	if (top.CUERPO){
		var urldatos1 = 'imflinstructivo1.jsp?gaveta='+ parent.OPCIONES.gaveta +'&idExp='+ parent.OPCIONES.expediente+'&usr='+usr;
		var urldatos2 = 'arbolapplet.jsp;jsessionid=' + parent.SUPERIOR.id_sesion + '?creo_objeto=1&idCarpeta=0&idExp=' + parent.OPCIONES.expediente + '&gaveta=' + parent.OPCIONES.gaveta;
		parent.OPCIONES.location.replace(urldatos2);
		top.CUERPO.location.replace(urldatos1);
	} else {
		/*var urldatos1 = 'Bienvenida.jsp';
		var urldatos2 = 'ArbolExpediente.jsp?select=' + parent.left.idnodo + '&arbol.tipo=d';
		parent.left.location.replace(urldatos2);
		top.main.location.replace(urldatos1);*/
		top.location.href = 'Principal.jsp?select='+ parent.superior.homeid + '&arbol.tipo=g';
	}

}

function irHome(idhome){
	if (top.CUERPO){
		var urldatos1 = 'imflinstructivo1.jsp?gaveta='+ parent.OPCIONES.gaveta +'&idExp='+ parent.OPCIONES.expediente+'&usr='+usr;
		var urldatos2 = 'arbolapplet.jsp;jsessionid=' + parent.SUPERIOR.id_sesion + '?creo_objeto=1&idCarpeta=0&idExp=' + parent.OPCIONES.expediente + '&gaveta=' + parent.OPCIONES.gaveta;
		parent.OPCIONES.location.replace(urldatos2);
		top.CUERPO.location.replace(urldatos1);
	} else {
		/*var urldatos1 = 'Bienvenida.jsp';
		var urldatos2 = 'ArbolExpediente.jsp?select=' + parent.left.idnodo + '&arbol.tipo=d';
		parent.left.location.replace(urldatos2);
		top.main.location.replace(urldatos1);*/
		top.location.href = 'Principal.jsp?select='+idhome;
	}

}

function CambiaPassword(){
	if (top.CUERPO){
	window.open('cambiarpassword.jsp','CUERPO');
	} else {
	window.open("cambiarpassword.jsp","main");
	}
}

function Guias(){
	if (top.CUERPO){
	//window.open("../videos/index.html","CUERPO");
		window.open("../jsp/Guias.jsp","CUERPO");
	} else {
	//window.open("../videos/index.html","main");
		window.open("../jsp/Guias.jsp","main");
	}
}

function funcion_salida(){
	if (top.CUERPO){
		parent.OPCIONES.location.href='blanco.html';
		setTimeout('location.href="salida.jsp;jsessionid="+id_sesion',1500);
	} else {
		top.location.href = "Salida.jsp";
	}
}

function creaCarpetaDocto(opcion,tipodoc){
if (top.CUERPO){
		top.OPCIONES.creaDoctoCarpeta(opcion,tipodoc);
	} else {
		var select = top.left.getNodeSelect();
		window.open("CreaCarpetaDocto.jsp?select=" + select + "&docto=" + opcion + "&tipo=" + tipodoc, "main");
	}
}

function importaDocto(){
	var select = top.left.getNodeSelect();
	window.open("PreGuardaDocto.jsp?select=" + select+"&nuevo=true&editable=true", "main");
}

function elimina(){
	if (top.CUERPO){
		top.OPCIONES.eliminaDoctoCarpeta();
	} else {
		var select = top.left.getNodeSelect();
		if (confirm("¿Confirme que desea eliminar la selección actual y todo su contenido?")) {
		 var select = top.left.getNodeSelect();
			window.open("../delkeeper?select=" + select, "main");
		}
	}
}

function ocultar(idelemento) {
	if(idelemento && idelemento!=null){
		document.getElementById(idelemento).style.display='none';
	}
	else{
		alert("Elemento nulo:" + idelemento );
	}
}

function mostrar(idelemento) {
	document.getElementById(idelemento).style.display='block';
}

function exportaCarpeta() {
	 var select = top.left.getNodeSelect();
		window.open("GetFolder.jsp?select=" + select, "main");
//		openCenteredWindow("GetFolder.jsp?select=" + select, "main", 150, 300);
}

function respaldaCarpeta() {
	 var select = top.left.getNodeSelect();
		//window.open("FolderUploadApplet.jsp?select=" + select, "_blank");
		openCenteredWindow("FolderUploadApplet.jsp?select=" + select, "_blank", 450, 470);
}


function openCenteredWindow( url, name, height, width, parms ) {
 var left = Math.floor( (screen.width - width) / 2);
 var top = Math.floor( (screen.height - height) / 2);
 var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";

	if (parms) { winParms += "," + parms; }
	var win = window.open(url, name, winParms);
	if (parseInt(navigator.appVersion) >= 4) { win.window.focus(); }
	return win;
}

function openCenteredWindowMax( url, name, height, width, parms ) {
 var left = Math.floor( (screen.width - width) / 2);
 var top = Math.floor( (screen.height - height) / 2);
 var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=no,status=no,resizable=yes,location=no";

	if (parms) { winParms += "," + parms; }
	var win = window.open(url, name, winParms);
	if (parseInt(navigator.appVersion) >= 4) { win.window.focus(); }
	return win;
}

function exportaDocumento() {
	 var select = top.left.getNodeSelect();
		window.open("GetFolder.jsp?opcion=versiones&accion=abrir&select=" + select, "main");
}

function editaDocto(session) {
	var select = top.left.getNodeSelect();
	openCenteredWindowMax("../filestore;jsessionid=" + session + "?select=" + select + "&edit=true", "_blank", 600, 800);
}