/* 
 * 
 * 
 * STORE
 * 
 * 
 */

	 Ext.define('VolumenModel', {
     extend: 'Ext.data.Model',
     fields: [
         {name: 'Unidad', type: 'string'},
         {name: 'RutaBase', type: 'string'},
         {name: 'EstadoUnidad', type: 'string'}

     ]
 });
	 
	 Ext.define('LoginModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'Unidad', type: 'string'},
	         {name: 'RutaBase', type: 'string'},
	         {name: 'nombreUsuario', type: 'string'},
	         {name:'codigo',type:'string'}

	     ]
	 });

 var Store = Ext.create('Ext.data.Store', {
     model: 'VolumenModel',
     proxy: {
         type: 'ajax',
         url: rutaServlet,
         reader: {
             type: 'json',
             root: 'VolumenModel'
         },
         extraParams: 
         {
              action: 'getVolumenes'
          }
     },

     autoLoad: true
 });
 /* 
  * 
  * 
  * VARIABLES UTILIZADAS
  * 
  * 
  */
 	var volumenActivo=''; //Variable que se llenara cuando se seleccione algun Volumen
 	var rutaActiva='';//Variable que se llenara cuando se seleccione algun Volumen
 	var msgAdvertencia="<p class='pa'>Advertencia:</p><br /><br />"+
 	'<p>1.-El cambio de volumen implica riesgos y debe ser realizado con cautela.<br /><br />'+
 	"2.-La 'Ruta' debe ser válida y debe tener privilegios de lectura y escritura.<br /><br />"+
 	'3.-La activaci&oacuten de una unidad debe ser realizada cuando la aplicaci&oacuten no esté en uso.<br /><br />'+
 	'4.-Debe tener privilegios de Administrador para realizar la activaci&oacuten.</p>';
 	
/* 
 * 
 * 
 * REGION DE OBJETOS UTILIZADOS
 * 
 * 
 */


var txtVolumen = Ext.create('Ext.form.field.Text', {
		id:'txtVolumen',
		name:'txtVolumen',
		fieldLabel:'Unidad',
    	allowBlank:false,
    	margin:'0 0 0 20',
    	regex: /[\x2F]$/ ,
    	regexText:"El volumen debe terminar con /",
    	emptyText:'ejm: /'
});

	var txtRuta = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Ruta',
		componentCls:'labels',
    	name:'txtRuta',
    	margin:'5 0 5 20',
    	allowBlank:false,
    	regex: /[\x2F]$/ ,
    	regexText:"La ruta debe terminar con /",
    	emptyText:'ejm: Fortimax/'
});
	var btnBorrar=Ext.create('Ext.button.Button',{
		text: 'Limpiar Campos',
        handler: function() {
            this.up('form').getForm().reset();
        }
	});
	var btnCrear=Ext.create('Ext.button.Button',{
		text: 'Crear',
		 formBind: true, 
		 iconCls: 'add',
	        handler: function() {
	            var form = this.up('form').getForm();
	            if (form.isValid()) {
	                       CrearVolumen();
	            }
	        } 
	});
	var btnCancelarCambios=Ext.create('Ext.button.Button',{
		text: 'Cancelar Cambios',
        handler: function() {
            cancelarCambios();
        }
	});
	var btnActivarUnidad=Ext.create('Ext.button.Button',{
		text: 'Activar Unidad Seleccionada',
		disabled:true,
		iconCls:'guardar',
        handler: function() {
            page1Aut.show();
        }
	});
	
	
	//Grid de Unidades
	var grdUnidades=Ext.create('Ext.grid.Panel', {
	    id:'grdUnidades',
	    width:600,
	    height:'100%',
	    store: Store, 
	    scroll:true,
	    columns: [
	        { text: 'Volumen',  dataIndex: 'Unidad', width:120},
	        { text: 'Ruta',  dataIndex: 'RutaBase', width:375 },
	        {text:'Activa',dataIndex:'EstadoUnidad',width: 120,align:'center',   
         	  renderer : function (value, cell, doc, idx) {       	 
        	      if (value=='1') {
        	    	return  "<img src='../imagenes/activo.png' width='20'/>";
        	 	   }
        	      else{return null;}
        	  }}//Termina Columna con 
	    ],
	    listeners: {
	        itemclick: function(dataview, record, item, index, e, objeto) {
	        	if(grdUnidades.getStore().getAt(index).get('EstadoUnidad')=='0'){
	        		btnActivarUnidad.enable();
		            volumenActivo=grdUnidades.getStore().getAt(index).get('Unidad');
		            rutaActiva=grdUnidades.getStore().getAt(index).get('RutaBase');
	        	}
	        	else{
	        		btnActivarUnidad.disable();
	        	}
	        }}
	});
	
	//OBJETO VENTANA

	//TERMINA OBJETO VENTANA
	/* 
	 * 
	 * 
	 * FUNCIONES UTILIZADAS
	 * 
	 * 
	 */
	
		//FUNCION CREAR VOLUMEN
		function CrearVolumen(){
			var VolModel = Ext.create('VolumenModel', {
			    Unidad : txtVolumen.getValue(),
			    RutaBase  : txtRuta.getValue(),
			    EstadoUnidad:'0'
			});			
			VolModel = Ext.JSON.encode(VolModel.data);	
             txtVolumen.setValue('');	
             txtRuta.setValue('');
             
             
             Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: successAjaxFnN,  
					timeout: 30000,  
					params: {  
					action: 'insertVolumen',  				
					VolumenModel:VolModel
					 }});
             
		} //TERMINA FUNCION CREAR VOLUMEN

		//INICIA FUNCION ACTIVAR UNIDAD
		function accionLogin(){
			var loginModel = Ext.create('LoginModel', {
			    Unidad : volumenActivo,
			    RutaBase  : rutaActiva,
			    nombreUsuario:txtUsuario.getValue(),
			    codigo:hex_md5(txtContrase.getValue())
			});	
			loginModel = Ext.JSON.encode(loginModel.data);
			 Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: successAjaxFnN,  
					timeout: 30000,  
					params: {  
					action: 'updateVolumenActivo',  				
					LoginModel:loginModel
					 }});
			 txtUsuario.setValue('');
			 txtContrase.setValue('');
			page2Aut.hide();
			//CODIGO DE AUTENTICARSE
		}//TERMINA FUNCION ACTIVAR UNIDAD
	
		 var successAjaxFnN = function(response, request) {
		        var jsonData = Ext.JSON.decode(response.responseText);
		        if (true == jsonData.success) {
		            Ext.Msg.show({
		                title: 'Correcto',
		                msg: jsonData.message,
		                buttons: Ext.Msg.OK,
		                icon: Ext.MessageBox.INFO
		            });

		        } else {
		            Ext.Msg.show({
		                title: 'Error',
		                msg: jsonData.message,
		                buttons: Ext.Msg.OK,
		                animEl: 'elId',
		                icon: Ext.MessageBox.ERROR
		            });
		        }
		        Store.load();
		    };
	
	
	
	
	
/* 
 * 
 * 
 * FUNCION ONREADY
 * 
 * 
 */
Ext.require([
     'Ext.form.*',
     'Ext.tip.*'
]);



Ext.onReady(function() {
	var tama = Ext.getBody().getViewSize();
	Ext.create('Ext.form.Panel',{
		id:'panPrincipal',
		name:'panPrincipal',
		title:'Administracion de Volumenes',
		border:false,
		width:'100%',
		height:tama.height-20,
		url:'#',
		//Comienzan los Items
		items:[
		       {//Inicia Panel Agregar
		    	   xtype:'panel',id:'panAgregar',layout:{ type:'anchor' },width:600,height:105,name:'panAgregar',title:'Agregar Unidad',collapsed:false,collapsible:false, //Propiedades Basicas Panel Agregar
		    	   items:[
		    	          txtVolumen,txtRuta,{xtype:'toolbar',width:600,cls:'toolbar',items:[btnBorrar,'->',btnCrear]}
		    	          ]//Terminan Items de Panel Agregar
		       }//Termina Panel Agregar
		       ,
		       {//Inicia Panel Grid
		    	   xtype:'panel',id:'panUnidades',layout:{ type:'fit' },height:'70%',width:600,name:'panUnidades',title:'Unidades Guardadas',collapsed:false,collapsible:false, //Propiedades Basicas Panel Grid
		    	   items:[
							grdUnidades
		    	          ]//Terminan Items de Panel Grid
		       }//Termina Panel Grid
		       ,
		       {//Inicia Panel botones cambios
		    	   xtype:'toolbar',id:'panBotonesCambios',layout:{ type:'hbox' },width:600,height:30,name:'panBotonesCambios',title:'Guarda Cambios Permanentemente',collapsed:false,collapsible:false, //Propiedades Basicas Panel Grid
		    	   items:[
							'->',btnActivarUnidad
		    	          ]//Terminan Items botones de cambios
		       }//Termina panel botones cambios
		      ],renderTo: Ext.getBody()//Termina los Items e Inserta
	}); //Termina Panel Principal	
});		//Termina OnReady