/* 
 * 
 * 
 * STORE
 * 
 * 
 */
	var rutaJson='../js/unidades.json';

	 Ext.define('VolumenModel', {
     extend: 'Ext.data.Model',
     fields: [
         {name: 'volumen', type: 'string'},
         {name: 'ruta', type: 'string'},
         {name: 'activo', type: 'string'},

     ]
 });
	 
	 Ext.define('LoginModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'volumen', type: 'string'},
	         {name: 'ruta', type: 'string'},
	         {name: 'usuario', type: 'string'},
	         {name:'contrasena',type:'string'}

	     ]
	 });

 var Store = Ext.create('Ext.data.Store', {
     model: 'VolumenModel',
     proxy: {
         type: 'ajax',
         url: rutaJson,
         reader: {
             type: 'json',
             root: 'VolumenModel'
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
 	var rutaServlet='#'; //ruta del servlet con que se comunicara el js al enviar el login unidad activa y crear volumen
 	var volumenActivo=''; //Variable que se llenara cuando se seleccione algun Volumen
 	var rutaActiva='';//Variable que se llenara cuando se seleccione algun Volumen
 	var msgAdvertencia='Advertencia: La modificacion de este valor puede afectar Considerablemente el funcionamiento del sistema, solo un usiario ADMINISTRADOR debe realizar esta accion.<br/><br /> Pulsa Siguiente para AUTENTICAR tus Permisos.';
 
/* 
 * 
 * 
 * REGION DE OBJETOS UTILIZADOS
 * 
 * 
 */
 var lblAdvertencia = new Ext.form.Label({
     id:'lblAdvertencia',
 	//text: 'Advertencia: La modificacion de este valor puede afectar Considerablemente el funcionamiento del sistema, solo un usiario ADMINISTRADOR debe realizar esta accion. Pulsa Siguiente para AUTENTICAR tus Permisos',
     html:msgAdvertencia,
     shadow:true
 });
 var btnSiguiente=Ext.create('Ext.button.Button',{
		text: 'Siguiente',
     handler: function() {
    	 win.hide();
			winLogin.show();
     }
	});
 var btnCancelarAceptarUnidad=Ext.create('Ext.button.Button',{
		text: 'Cancelar',
  handler: function() {
	  txtUsuario.setValue('');
		txtContrase.setValue('');
      win.hide();
  }
	});
 var btnCancelarLogin=Ext.create('Ext.button.Button',{
	text:'Cancelar',
	handler:function(){
		winLogin.hide();
		txtUsuario.setValue('');
		txtContrase.setValue('');
	}
 });
 var btnLogin=Ext.create('Ext.button.Button',{
		text:'Activar Unidad',
		formBind: true,
		handler:function(){
			activarUnidad();
		}
	 });
	 
var txtVolumen = Ext.create('Ext.form.field.Text', {
		id:'txtVolumen',
		name:'txtVolumen',
		fieldLabel:'Unidad Volumen',
    	allowBlank:false,
    	emptyText:'ejm: /'
});
var txtUsuario= Ext.create('Ext.form.field.Text',{
	id:'txtUsuario',
	name:'txtUsuario',
	fieldLabel:'Usuario',
	allowBlank:false
});
var txtContrase= Ext.create('Ext.form.field.Text',{
	id:'txtContrase',
	name:'txtContrase',
	fieldLabel:'Password',
	inputType: 'password',
	allowBlank:false
});
	var txtRuta = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Ruta',
		componentCls:'labels',
    	name:'txtRuta',
    	allowBlank:false,
    	//activeError:'Error: Introducir el valor de Ruta',
    	emptyText:'ejm: Fortimax/'
});
	var btnBorrar=Ext.create('Ext.button.Button',{
		text: 'Borrar Campos',
        handler: function() {
            this.up('form').getForm().reset();
        }
	});
	var btnCrear=Ext.create('Ext.button.Button',{
		text: 'Crear Volumen',
		 formBind: true, 
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
        handler: function() {
            win.show();
        }
	});
	
	
	//Grid de Unidades
	var grdUnidades=Ext.create('Ext.grid.Panel', {
	    id:'grdUnidades',
	    width:500,
	    height:500,
	    store: Store, 
	    columns: [
	        { text: 'Volumen',  dataIndex: 'volumen', width:120},
	        { text: 'Ruta',  dataIndex: 'ruta', width:375 },
	        {text:'Activa',dataIndex:'activo',width: 100,align:'center',   
         	  renderer : function (value, cell, doc, idx) {       	 
        	      if (value=='1') {
        	    	return  "<img src='../imagenes/activo.png' width='20'/>";
        	 	   }
        	      else{return null;}
        	  }}//Termina Columna con 
	    ],
	    listeners: {
	        itemclick: function(dataview, record, item, index, e, objeto) {
	        	if(grdUnidades.getStore().getAt(index).get('activo')=='0'){
	        		btnActivarUnidad.enable();
		            volumenActivo=grdUnidades.getStore().getAt(index).get('volumen');
		            rutaActiva=grdUnidades.getStore().getAt(index).get('ruta');
	        	}
	        	else{
	        		btnActivarUnidad.disable();
	        	}
	        }},
	    height: 215,
	    width: 565,
	});
	
	//OBJETO VENTANA
	var win = Ext.create('Ext.window.Window', {
	    title: 'Activar Volumen',
	    height: 400,
	    width: 300,
	    closable : false,
	    layout: 'anchor',
	    resizable :false,
	    items: [
	           { xtype: 'fieldset',layout:'fit',height:325,style:{borderStyle:'none'},name:'fieldsetMensaje' ,items:[lblAdvertencia] },
	           { xtype: 'toolbar' ,layout:'hbox',style:{borderStyle:'none'},items:[btnCancelarAceptarUnidad,'->',btnSiguiente] }//Propiedades Basicas de FieldSet 
	           ]
	});
	var winLogin = Ext.create('Ext.window.Window', {
	    title: 'Autenticarse',
	    height: 200,
	    width: 300,
	    closable : false,
	    layout: 'anchor',
	    resizable :false,
	    items: [Ext.create('Ext.form.Panel',{ baseCls:'fmrLog', items:[
	            { xtype: 'fieldset',layout:'anchor',height:130,style:{borderStyle:'none'},name:'fieldsetMensaje' ,items:[txtUsuario,txtContrase] },
	            { xtype: 'toolbar' ,layout:'hbox',style:{borderStyle:'none'},items:[btnCancelarLogin,'->',btnLogin] }
	    ]})]
	});
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
//			Ext.Msg.alert('Aviso', 'El Volumen solo sera cargado en la aplicacion cuando se Guarden lo Cambios');
//			 var r = Ext.ModelManager.create({
//                 volumen: txtVolumen.getValue(),
//                 ruta: txtRuta.getValue(),
//                 activo:'0' 
//             }, 'UnidadesModel');
//             r.setDirty(); //Indicamos que los datos son Nuevos y estan sucios (modificados) con los triangulos rojos.
//             Store.insert(Store.getCount(), r);
//			var valores = new Array();
//			valores.push(txtVolumen.getValue());
//			valores.push(txtRuta.getValue());
			
			var VolModel = Ext.create('VolumenModel', {
			    volumen : txtVolumen.getValue(),
			    ruta  : txtRuta.getValue(),
			    activo:'0'
			});			
			VolModel = Ext.JSON.encode(VolModel.data);	
             txtVolumen.setValue('');	
             txtRuta.setValue('');
             
             
             Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: function(){
						 Ext.Msg.alert('Aviso', 'Volumen Creado');
						 Store.load();
					 },  
					 failure: function(){
						 Ext.Msg.alert('Aviso', 'Ocurrio un Error');
					 },  
					timeout: 30000,  
					params: {  
					action: 'insertVolumen',  				
					VolumenModel:VolModel

					 }});
             
		} //TERMINA FUNCION CREAR VOLUMEN
		var contrase=hex_md5(txtContrase.getValue());

		//INICIA FUNCION ACTIVAR UNIDAD
		function activarUnidad(){
			var loginModel = Ext.create('LoginModel', {
			    volumen : volumenActivo,
			    ruta  : rutaActiva,
			    usuario:txtUsuario.getValue(),
			    contrasena:contrase
			});	
			loginModel = Ext.JSON.encode(loginModel.data);
			 Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: function(){
						 Ext.Msg.alert('Aviso', 'Volumen a sido Activado');
						 Store.load();
					 },  
					 failure: function(){
						 Ext.Msg.alert('Aviso', 'Ocurrio un Error');
					 },  
					timeout: 30000,  
					params: {  
					action: 'updateVolumenActivo',  				
					VolumenModel:loginModel
					 }});
			 txtUsuario.setValue('');
			 txtContrase.setValue('');
			winLogin.hide();
			//CODIGO DE AUTENTICARSE
		}//TERMINA FUNCION ACTIVAR UNIDAD
	

	
	
	
	
	
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
	Ext.create('Ext.form.Panel',{
		id:'panPrincipal',
		name:'panPrincipal',
		title:'Administracion de Volumenes',
		width:600,
		height:500,
		url:'#',
		//Comienzan los Items
		items:[
		       {//Inicia Panel Agregar
		    	   xtype:'panel',id:'panAgregar',layout:{ type:'anchor',anchor:'80%' },name:'panAgregar',title:'Agregar Unidad',collapsed:false,collapsible:false, //Propiedades Basicas Panel Agregar
		    	   items:[
		    	          txtVolumen,txtRuta,{xtype:'toolbar',cls:'toolbar',items:['->',btnBorrar,btnCrear]}
		    	          ]//Terminan Items de Panel Agregar
		       }//Termina Panel Agregar
		       ,
		       {//Inicia Panel Grid
		    	   xtype:'panel',id:'panUnidades',layout:{ type:'fit' },height:340,name:'panUnidades',title:'Unidades Guardadas',collapsed:false,collapsible:false, //Propiedades Basicas Panel Grid
		    	   items:[
							grdUnidades
		    	          ]//Terminan Items de Panel Grid
		       }//Termina Panel Grid
		       ,
		       {//Inicia Panel botones cambios
		    	   xtype:'toolbar',id:'panBotonesCambios',layout:{ type:'hbox' },name:'panBotonesCambios',title:'Guarda Cambios Permanentemente',collapsed:false,collapsible:false, //Propiedades Basicas Panel Grid
		    	   items:[
							'->',btnActivarUnidad
		    	          ]//Terminan Items botones de cambios
		       }//Termina panel botones cambios
		      ],renderTo: Ext.getBody()//Termina los Items e Inserta
	}); //Termina Panel Principal	
});		//Termina OnReady