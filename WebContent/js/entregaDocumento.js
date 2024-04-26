Ext.onReady(function(){
/*
 * Variables
 */
	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	var pantalla=Ext.getBody().getViewSize();
	var ancho=500;
	var alto=350;
	var actionStore='getShareDocument';
/*
 * Variables estaticas
 */
	var accionBoton="";
	var textosemana=new Array(7);
	textosemana[0]="Domingo";
	textosemana[1]="Lunes";
	textosemana[2]="Martes";
	textosemana[3]="Miércoles";
	textosemana[4]="Jueves";
	textosemana[5]="Viernes";
	textosemana[6]="Sábado";
	var textomes = new Array (12);
	textomes[0]="Enero";
	textomes[1]="Febrero";
	textomes[2]="Marzo";
	textomes[3]="Abril";
	textomes[4]="Mayo";
	textomes[5]="Junio";
	textomes[6]="Julio";
	textomes[7]="Agosto";
	textomes[8]="Septiembre";
	textomes[9]="Octubre";
	textomes[10]="Noviembre";
	textomes[11]="Diciembre";
/*
 * Store y Modelo
 */
	Ext.define('modeloDocumento', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'nombre_documento',  	type: 'string'},
        {name: 'compartir',  			type: 'string'},
        {name: 'token',  				type: 'string'},
        {name: 'nombre_tipo_docto',  	type: 'string'},
        {name: 'titulo_aplicacion',  	type: 'string'},
        {name: 'id_gabinete',  			type: 'int'},
        {name: 'id_carpeta_padre',  	type: 'int'},
        {name: 'id_documento',  		type: 'int'},
        {name: 'dateExp',  				type: 'string'},
        {name: 'houreExp',  			type: 'string'},
        {name: 'ligaPermisoBajar',  	type: 'string'}
        
    ]
	});
	var storeDocumento=new Ext.data.Store({
	model:'modeloDocumento',
	proxy: { 
            type: 'ajax', 
            	url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: 'documento'
            },
            extraParams: 
	         {
	              action: actionStore,
	              select:select,
	              token:token
	          }
	          
        },
        listeners:{
        	load:function(){
        		if(this.getCount()>0){
        			if(this.getAt(0).get('compartir')=='S'){
        				var fecha=new Date(this.getAt(0).get('dateExp'));
        				var texto='Compartido hasta el: <br /><br /><font class="resaltar">'+
        				textosemana[fecha.getDay()]+' '+fecha.getDate()+' de ' +
						textomes[fecha.getMonth()]+' del '+
						fecha.getFullYear()+' a las '+
						this.getAt(0).get('houreExp')+' hrs. </font>';
						
        				lblTitulo.setText('Documento: <font class="resaltar">'+this.getAt(0).get('nombre_documento')+'</font>',false);
        				lblMensaje.setText(texto,false);
        				if(this.getAt(0).get('ligaPermisoBajar')=='N'){
        					
        					btnAccion.setVisible(true);
        					imagenCompartir.setVisible(false);
        					btnAccion.setIconCls('btnAccionVer');
        					btnAccion.setText('Ver documento');
        					accionBoton="ver";
        				}
        				else{
        					btnAccion.setVisible(true);
        					imagenCompartir.setVisible(false);
        					btnAccion.setIconCls('btnAccionDescargar');
        					btnAccion.setText('Descargar documento');
        					accionBoton="descargar";
        				}
        			}
        			else{
        				var fecha=new Date(this.getAt(0).get('dateExp'));
        				var texto='El documento expiro el dia: <br /><br /><font class="resaltar">'+
        				textosemana[fecha.getDay()]+' '+fecha.getDate()+' de ' +
						textomes[fecha.getMonth()]+' del '+
						fecha.getFullYear()+' a las '+
						this.getAt(0).get('houreExp')+' hrs. </font>';
						
        				lblTitulo.setText('Documento: <font class="resaltar">'+this.getAt(0).get('nombre_documento')+'</font>',false);
        				lblMensaje.setText(texto,false);
        				
        					ventana.setHeight(400);
        					btnAccion.setVisible(false);
        					imagenCompartir.setSrc(basePath+'/imagenes/expirado.png');
        					imagenCompartir.setVisible(true);
        			}
        		}
        		else{
        			var texto='El documento que intento acceder ya no esta compartido, por alguna de las siguientes causas:<br /><br />' +
        					'&nbsp;&nbsp;&nbsp;-El propietario ha cancelado el documento compartido.<br />' +
        					'&nbsp;&nbsp;&nbsp;-El Documento ha sido borrado del sistema.<br /><br />' +
        					'&nbsp;&nbsp;&nbsp;Contacte al propietario del documento si necesita acceder a este'
        				lblTitulo.setText('Error:<font class="resaltar"> Documento no encontrado </font>',false);
        				lblMensaje.setText(texto,false);
        				
        					btnAccion.removeCls('btnAccionVer');
        					btnAccion.removeCls('btnAccionDescargar');
        					btnAccion.setVisible(false);
        					imagenCompartir.setSrc(basePath+'/imagenes/errorDocumentoCompartido.png');
        					imagenCompartir.setVisible(true);
        					ventana.setHeight(420);
        		}
        		Ext.getBody().unmask();
        		}
        },
        autoLoad:true
	});
/*
 * Objetos
 */
	var lblTitulo=new Ext.form.Label({
		html:'Documento: ',
		 shadow:true,
		 cls:'lblTituloCls'
	});
	var lblMensaje=new Ext.form.Label({
		html:'Documento: ',
		 shadow:true,
		 cls:'lblMensajeCls'
	});
	var btnAccion=new Ext.button.Button({
		id:'btnAccion',
		scale: 'medium',
		iconAlign:'top',
		handler:function(){
			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			funcionAccion(accionBoton);
		}
	});
	var imagenCompartir = Ext.create('Ext.Img', {
    width:120,
    height:80,
    hidden:true
});
var imagenBanner = Ext.create('Ext.Img', {
	src:basePath+'/Admin/imagenes/banner2.jpg',
    width:ancho,
    height:120
});
/*
 * Paneles y contenedores
 */
	var ventana=new Ext.window.Window({
		id:'ventana',
		title:'Documento compartido',
		width:ancho,
		height:alto,
		closable:false,
		layout: 'anchor',
		autoScroll:false,
		resizable:false,
		items:[	
			{xtype:'fieldset',cls:'fieldSetTitulo',width:'100%',layout:'anchor',items:[lblTitulo]},
			{xtype:'fieldset',cls:'fieldSetMensaje',width:'100%',layout:'anchor',items:[lblMensaje]},
			{xtype:'fieldset',cls:'fieldSetboton',width:'100%',layout:'anchor',items:[imagenBanner]},
			{xtype:'fieldset',cls:'fieldSetboton',width:'100%',layout:'anchor',items:[btnAccion,imagenCompartir]}
			]
	}).show();
	/*
	 * Funciones
	 */
	var successAjaxFnVer = function(response, request) {
		Ext.getBody().unmask();
	    var jsonData = Ext.JSON.decode(response.responseText);
	    if (true == jsonData.success) {
	    	document.location.href=basePath+'/jsp/Visualizador.jsp?select='+select+'&compartido=true';
	    } else {
	        Ext.example.msg("Error",jsonData.message); 
	    }
	};
	function funcionAccion(accionRealizar){
		if(accionRealizar=="ver"){
			Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
			 		success: successAjaxFnVer,   
					timeout: 30000,  
					params: {  
					action: 'verDocumento',
					select:select
				}});
		}
		else if(accionRealizar=="descargar"){
			Ext.example.msg("Correcto","Generando");
			var url=rutaServlet+'?action=descargarDocumento&select='+select;
			var downloadFrame = document.createElement("iframe"); 
			downloadFrame.setAttribute('src',url);
			downloadFrame.setAttribute('class',"frameOculto"); 
			downloadFrame.setAttribute('style',"width:0px;heigth:0px;visibility:hidden;display:none;"); 
			document.body.appendChild(downloadFrame)
			Ext.getBody().unmask();
		}
	}
});