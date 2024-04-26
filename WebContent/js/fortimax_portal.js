// JavaScript Document
function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_changeProp(objName,x,theProp,theValue) { //v6.0
  var obj = MM_findObj(objName);
  if (obj && (theProp.indexOf("style.")==-1 || obj.style)){
    if (theValue == true || theValue == false)
      eval("obj."+theProp+"="+theValue);
    else eval("obj."+theProp+"='"+theValue+"'");
  }
}

function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}

function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}
//*******Mostra y ocultar celdas y tablas
	function ocultar(idfila)
	{
		document.getElementById(idfila).style.display='none';
	}
	
	function mostrar(idfila)
	{
		document.getElementById(idfila).style.display='block';
	}

function MM_callJS(jsStr) { //v2.0
  return eval(jsStr);
}

function validafort(){
	var urlact =  window.location.host;	
	if(urlact == "fortimax.com" || urlact == "fortimax.com.mx" || urlact == "www.fortimax.com.mx")
	{
      var urlseguro = "https://www.fortimax.com";		
	}
	else
	{
     var urlseguro = "https://" + urlact;
	}
	var mensaje="";
    var longitud;
    var aux;
    var posi;
    var invalidChars;
      if(document.frmfort.txtUsuario.value.length == 0)
      {
           mensaje += "Escriba el usuario\n";
      }
      else
      {
          longitud = document.getElementById('txtUsuario').value.length;// document.frmfort.txtUsuario.value.length;
          aux = document.getElementById('txtClave').value;// document.frmfort.txtUsuario.value;
          posi = aux.indexOf("...",0);
          if(posi == 0 || posi == 1 || posi == 2) mensaje += "El usuario no debe tener caracteres especiales\n";
          /*else
          {
             invalidChars = "/:,;~ ¡()áéíóúÁÉÍÓÚ\"'?¡¿!#$%&{}[]<>+*|°¬@";
             for (i=0; i<invalidChars.length; i++)
             { badChar = invalidChars.charAt(i); if(aux.indexOf(badChar,0) != -1){ mensaje +="El usuario no debe tener caracteres especiales\n"; break; } }
          }*/
      }
      if(document.frmfort.txtClave.value.length == 0)
      {
           mensaje += "Escriba la contrase\u00f1a\n";
      }
      else
      {
    	  longitud = document.getElementById('txtUsuario').value.length;// document.frmfort.txtUsuario.value.length;
          aux = document.getElementById('txtClave').value;// document.frmfort.txtUsuario.value;
          posi = aux.indexOf("...",0);
          if(posi == 0 || posi == 1 || posi == 2) mensaje += "La contrase\u00f1a no debe tener caracteres especiales\n";
          /*else
          {
             invalidChars = "/:,;~ ¡()áéíóúÁÉÍÓÚ\"'?¡¿!#$%&{}[]<>+*|°¬@";
             for (i=0; i<invalidChars.length; i++)
             { badChar = invalidChars.charAt(i); if(aux.indexOf(badChar,0) != -1){ mensaje +="La contrase\u00f1a no debe tener caracteres especiales\n"; break; } }
          }*/
      }
      
      if(mensaje.length != 0)
      {
          msg  = "Verifique lo siguiente antes de continuar:\n\n";
          msg += mensaje;
          alert(msg);
          return false;
      }
      else
      {
 		//document.frmfort.action = "/fortimaxCNDH/login";
    	var form = document.getElementById('frmfort');
    	form.setAttribute('action', window.location.pathname + "login");
    	form.submit();
    	return true;
	  }
}