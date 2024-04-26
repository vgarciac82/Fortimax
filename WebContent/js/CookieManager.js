  function setCookie(nombre, valor, tiempo){
	  var f=tiempo.substring(tiempo.length-1);
	  var t=tiempo.substring(0,tiempo.length-1);
	  if(f=='d'){
		  tiempo=t*24*60*60*1000;
	  }
	  else if(f=='h'){
		  tiempo=t*60*60*1000;
	  }
	  else if(f=='m'){
		  tiempo=t*60*1000;
	  }
	  else if(f=='s'){
		  tiempo=t*1000;
	  }
	  else{
		  tiempo=t*60*60*1000;
	  }
    var fecha = new Date();
    fecha.setTime(fecha.getTime() + tiempo);
    document.cookie = nombre + ' = ' + escape(valor) + ((tiempo == null) ? '' : '; expires = ' + fecha.toGMTString()) + '; path=/';
	  return true;
  }

  function getCookie(nombre){
    var nombreCookie, valorCookie, cookie = null, cookies = document.cookie.split(';');
    for (var i=0; i<cookies.length; i++){
      valorCookie = cookies[i].substr(cookies[i].indexOf('=') + 1);
      nombreCookie = cookies[i].substr(0,cookies[i].indexOf('=')).replace(/^\s+|\s+$/g, '');
      if (nombreCookie == nombre)
        cookie = unescape(valorCookie);
    }
    return cookie;
  }