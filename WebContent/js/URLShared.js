Ext.onReady(function(){
	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	
/*
 * Variables
 */
	var pantalla = Ext.getBody().getViewSize();
	var actionStore='getSharedDocumentInfo';
	var ancho=850;
	var alto=400;
	var compartido;
 /*
  * Variables estaticas
  */
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
  * Store y modelos
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
        {name: 'ligaPermisoBajar',  		type: 'string'}
        
    ]
	});
	var storeEstructura=new Ext.data.Store({
	model:'modeloDocumento',
	proxy: { 
            type: 'ajax', 
            	url: rutaServletCompartirDoc,  							
            	reader: { 										
                type: 'json', 
                root: 'documento'
            },
            extraParams: 
	         {
	              action: actionStore,
	              select:select
	          }
	          
        },
        listeners:{
        	load:function(){
        		
        		if(this.getCount()>0){
        			if(this.getAt(0).get('compartir')=='N'){
        				btnOnOff.setIconCls('btnOn');
        				btnOnOff.setText('Compartir');
        				textoEstatusColor.setText('<font class="rojo">Sin compartir</font>',false);
        				compartido=false;
        			}
        			else{
        				btnOnOff.setIconCls('btnOff');
        				btnOnOff.setText('No compartir');
        				textoEstatusColor.setText('<font class="verde">Compartido</font>',false);
        				compartido=true;
        			}
        			var fecha=new Date(this.getAt(0).get('dateExp'));
					
        			textoEncabezado.setText(this.getAt(0).get('nombre_documento'),false);
        			var url='\n'+
        			basePath+"s/"+this.getAt(0).get('token');
        			areaLiga.setValue(url);
        			
        			textoCuerpo.setText('Compartido hasta el <font class="resaltar">'+
        			textosemana[fecha.getDay()]+' '+fecha.getDate()+' de ' +
					textomes[fecha.getMonth()]+' del '+
					fecha.getFullYear()+' a las '+
					this.getAt(0).get('houreExp')+' hrs. </font>',false);
					
					fechaExpira.setValue(fecha);
					horaExpira.setValue(this.getAt(0).get('houreExp'));
					chbPermitirZip.setValue(this.getAt(0).get('ligaPermisoBajar')=='N'?false:true);
        		}
        		Ext.getBody().unmask();
        		}
        },
        autoLoad:true
	});
 /*
  * Objetos
  */
	var textoDocumento=new Ext.form.Label({
		html:'Documento: ',
		 shadow:true
	});
	var textoEncabezado=new Ext.form.Label({
		html:'',
		componentCls:'textoEncabezado',
		 shadow:true
	});
	var textoEstatus=new Ext.form.Label({
		html:'Estatus: ',
		 shadow:true
	});
	var textoEstatusColor=new Ext.form.Label({
		html:'',
		 shadow:true
	});
	var textoCuerpo=new Ext.form.Label({
		html:'',
		componentCls:'textoCuerpo',
		 shadow:true
	});
   var areaLiga=new Ext.form.TextArea({
   	 grow      : true,
   	 rows:3,
   	 readOnly:true,
   	 fieldCls:'areaLigaTexto',
   	 cls:'areaLigaTextoCls',
   	  resizable:false
   });
   
   var fechaExpira=new Ext.form.field.Date({
		id:'fechaExpira',
		fieldLabel: 'Fecha',
		labelPad:0,
		labelWidth:80,
		width:180,
        name: 'fechaExpira',
        margin:'5 10 25 0',
        editable:false,
        minValue:new Date()
	});
   
	var horaExpira=new Ext.form.field.Spinner({
		fieldLabel:'Hora',
		labelWidth:80,
		width:180,
		editable:false,
		value:'00:00',
		margin:'5 25 20 0',
		listeners:{
			spin:function( spin, direction, eOpts ){
				var hora=spin.getValue().split(':');
				var fecha=new Date();
				fecha.setHours(hora[0]);
				fecha.setMinutes(hora[1]);
				fechafinal=new Date(fecha);
				var fechafinal=new Date(fecha);
				if(direction=='up'){
					fechafinal.setMinutes(fecha.getMinutes()+30);
				}
				else{
					fechafinal.setMinutes(fecha.getMinutes()-30);
				}
				//alert(fecha.getHours());
				var hora=fechafinal.getHours();
				hora=hora>9?hora:'0'+hora;
				var minutos=fechafinal.getMinutes();
				minutos=minutos==0?'00':minutos;
				spin.setValue(hora+":"+minutos);
			}
		}
	});
	var chbPermitirZip=new Ext.form.field.Checkbox({
			boxLabel  : 'Descargar en zip',
            name      : 'chbPermitirZip',
            inputValue: '0',
            id        : 'chbPermitirZip'
		});
	var btnGuardar=new Ext.button.Button({
		text:'Guardar',
		scale: 'medium',
		iconCls:'btnGuardar',
		margin:'3 0 0 0',
		handler:function(){
			modificarValores(select,true);
		}
	});

	var btnOnOff=new Ext.button.Button({
		text:'',
		iconCls:'btnOn',
		scale: 'medium',
		handler:function(){
			if(compartido){
				cancelarCompartir(select);
			}
			else{
				modificarValores(select,false);
			}
			
		}
	});
	
	var btnCopiar=new Ext.button.Button({
		iconCls:'btnCopiarIcon',
		text:'Copiar',
		cls:'btnCopiar',
		align:'right',
	    scale: 'medium',
	    handler:function(){
	    	clipboardCopy(areaLiga.getValue());
	    }
	});
	var ventana=new Ext.window.Window({
		id:'ventana',
		title:'Compartir documento',
		width:pantalla.width>ancho?ancho:pantalla.width-50,
		height:alto,
		layout: 'anchor',
		autoScroll:false,
		iconCls:'ventanaIcon',
		//closable:false,
		tbar:['->',btnOnOff],
		items:[		
					{xtype:'fieldset',cls:'fieldSets',width:'100%',layout:'hbox',margin:'10 0 0 0',items:[
						{xtype:'fieldset',cls:'fieldSets',width:'100%',layout:'fit',items:[textoDocumento,textoEncabezado]},
						{xtype:'fieldset',cls:'FieldtextoEstatus',width:'100%',layout:'fit',items:[textoEstatus,textoEstatusColor]}
					]},	
					{xtype:'fieldset',cls:'fieldSets',width:'100%',layout:'hbox',margin:'5 0 0 0',items:[areaLiga]},
					{xtype:'fieldset',cls:'fieldSetsbtnCopiar',id:'fieldSetsbtnCopiar',layout:'anchor',items:[btnCopiar],width:(pantalla.width>ancho?ancho:pantalla.width-50)-30},
					{xtype:'fieldset',title:'Expiracion de la liga',cls:'fieldSetExpira',
					items:[{xtype:'fieldset',cls:'fieldSetsbtnGuardar',layout:'hbox',height:50,items:[fechaExpira,horaExpira,btnGuardar]},textoCuerpo]},
					
					{xtype:'fieldset',cls:'fieldSets',items:[chbPermitirZip]}
			],
		listeners:{
			resize:function( _ventana, width, height, eOpts ){
				ancho=width;
				areaLiga.setWidth(width-50);
				Ext.getCmp('fieldSetsbtnCopiar').setWidth(width-30);
				_ventana.setHeight(alto);
			},
			close:function(){
				top.main.location.replace('Bienvenida.jsp');
			}
		}
	}).show();
 /*
  * Funciones
  */
	function clipboardCopy(txt) { 
    if (window.clipboardData) { 
        window.clipboardData.clearData(); 
        window.clipboardData.setData("Text", txt);
        Ext.example.msg("Copiado","URL copiada");
    } 
    else if (window.netscape) { 
    	
        try { 
            netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect"); 
        } 
        catch (e) { 
            alert("Un script no puede Cortar / Copiar / Pegar automáticamente por razones de seguridad.\n"+ 
                  "Para hacerlo necesitas activar 'signed.applets.codebase_principal_support' en about:config'"); 
            return false; 
        } 
        var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard); 
        if (!clip) 
            return; 
        var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable); 
        if (!trans) 
            return; 
        trans.addDataFlavor('text/unicode'); 
        var str = new Object(); 
        var len = new Object(); 
        var str = Components.classes['@mozilla.org/supports-tring;1'].createInstance(Components.interfaces.nsISupportsString); 
        var copytext = txt; 
        str.data = copytext; 
        trans.setTransferData("text/unicode",str,copytext.length*2); 
        var clipid = Components.interfaces.nsIClipboard; 
        if (!clip) 
            return false; 
        clip.setData(trans,null,clipid.kGlobalClipboard); 
    } 
}
var successAjaxFnN = function(response, request) {
		top.left.location.href=("../refreshtree");
		storeEstructura.load();
		Ext.getBody().unmask();
	    var jsonData = Ext.JSON.decode(response.responseText);
	    if (true == jsonData.success) {
	    	Ext.example.msg("Realizado correctamente",jsonData.message);         
	    } else {
	        Ext.example.msg("Error",jsonData.message); 
	    }
	};
function cancelarCompartir(documento){
	 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		        	 btnOnOff.setIconCls('btnOn');
		        	 btnOnOff.setText('Compartir')
		        	 textoEstatusColor.setText('<font class="rojo">Sin compartir</font>',false);
		        	 compartido=false;
					Ext.Ajax.request({  
					url: rutaServletCompartirDoc,  
					method: 'POST',  
			 		success: successAjaxFnN,   
					timeout: 30000,  
					params: {  
					action: 'cancelarCompartir',
					select:documento
					}});
}
function modificarValores(documento,personalizado){
	if(personalizado){
		//Thu Feb 28 2013 00:00:00 GMT-0800 (PST)
		var fecha=new Date(fechaExpira.getValue());
		var txtF=fecha.getFullYear().toString()+'/'+(fecha.getMonth()+1).toString()+'/'+fecha.getDate();
		var datos=Ext.create('modeloDocumento',{
			dateExp:txtF,
			houreExp:horaExpira.getValue(),
			ligaPermisoBajar:chbPermitirZip.getValue()?'S':'N'
		});
		datos=Ext.JSON.encode(datos.data);
		btnOnOff.setIconCls('btnOff');
		btnOnOff.setText('No compartir');
		textoEstatusColor.setText('<font class="verde">Compartido</font>',false);
		compartido=true;
		Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		Ext.Ajax.request({  
			url: rutaServletCompartirDoc,  
			method: 'POST',  
			success: successAjaxFnN,   
			timeout: 30000,  
			params: {  
				action: 'modificaCompartir',
				select:documento,
				datos:datos
			}});
	}
	else{
		var datos=Ext.create('modeloDocumento',{
			ligaPermisoBajar:chbPermitirZip.getValue()?'S':'N'
		});
		datos=Ext.JSON.encode(datos.data);
		btnOnOff.setIconCls('btnOff');
		btnOnOff.setText('No compartir');
		textoEstatusColor.setText('<font class="verde">Compartido</font>',false);
		compartido=true;
		Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		Ext.Ajax.request({  
			url: rutaServletCompartirDoc,  
			method: 'POST',  
			success: successAjaxFnN,   
			timeout: 30000,  
			params: {  
				action: 'modificaCompartir',
				select:documento,
				datos:datos
			}});
	}
}

});