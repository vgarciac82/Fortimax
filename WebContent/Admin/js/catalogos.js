/* 
 * 
 * REQUIRE Y FUNCIO ONREADY
 * 
 */
Ext.require([
     'Ext.form.*',
     'Ext.tip.*'
]);
Ext.onReady(function() {
	/* 
	 * 
	 * VARIABLES
	 * 
	 */	
		
		var accion="ObtenerCatalogo";
		var actualiza=false;
//		var rutaDatosStore='';
//		var rutaServlet='OperacionesGavetaServlet';
	/* 
	 * 
	 * MODELOS Y STORE
	 * 
	 */
	Ext.define('datosModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'id', type: 'int'},
	         {name: 'nombre', type: 'string'}
	     ]
	 });
	Ext.define('catModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'nombre', type: 'string'},
	         {name: 'tipo', type: 'string'},
	         {name: 'datos', type:'string'},
	         {name: 'definicion', type:'string'},
	         {name: 'datosBorrados', type:'string'}
	     ]
	 });
	var tiposStore = Ext.create('Ext.data.Store', {
	    fields: ['tipo'],
	    data : [
	        {"tipo":"Texto"},
	        {"tipo":"Numeros"},
	        {"tipo":"Dinamico"}
	    ]
	});

	 var datosStore = Ext.create('Ext.data.Store', {
	     model: 'datosModel',
	     proxy: {
	         type: 'ajax',
	         url: rutaServlet,
	         reader: {
	             type: 'json',
	             root: 'datos'
	         },
	         extraParams: 
	            {
	                 action: accion,
	                 select:NombreCatalogo
	             }
	     },
	    
	     autoLoad: false
	 });
	 /* 
		 * 
		 * FUNCIONES
		 * 
		 */	
	 function ActivaEdicion(Catalogo){
		 if(Catalogo!=""&& Catalogo!=null){
		 	 txtCatalogo.setValue(NombreCatalogo);
			 actualiza=true;
			 panNombre.disable();
			 panDatos.enable();
			 btnAgregarCampo.enable();
			 Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: function(response, resquest)
					 {  
						 var jsonData = Ext.JSON.decode(response.responseText);
						 if (jsonData.success) {
						 	   if(jsonData.definicion==null) {
					           		datosStore.load();
					           		grdDatos.show();
						 	   } else {
						 	   		cmbTpo.setValue('Dinamico');
						 	   		txtDefinicion.show();
						 	   		txtDefinicion.setValue(jsonData.definicion);
					           }
						 }
						 else{
						 	Ext.Msg.show({
					            	title: 'Error',
					                msg: jsonData.message,
					                buttons: Ext.Msg.OK,
					                animEl: 'elId',
					                icon: Ext.MessageBox.ERROR
					            });
						 }
					 },  
					timeout: 30000,  
					params: {
			        	action: accion,
			            select: NombreCatalogo
					 }});
		 }
	 }

	    function GuardarCambios() {
        	if(datosStore.getCount()>0||cmbTpo.getValue()=='Dinamico'){
        		if(actualiza==false){
        		Ext.Msg.show({
        		     title:'Guardar',
        		     msg: '&iquestGuardar cambios?',
        		     buttons: Ext.Msg.OKCANCEL,
        		     icon: Ext.Msg.QUESTION,
        		     fn: function (btn){
        		         if(btn=='ok'){     
        		        	 var CambiosFilas = datosStore.getUpdatedRecords();
        		        	 var DatosD = new Array();
        		        	 for (var i=0; i < CambiosFilas.length; i++)
         					{  
        		        		 DatosD.push(CambiosFilas[i].data);  
         					}
        		        	 DatosD = Ext.JSON.encode(DatosD);
//        		        	 alert(DatosD);
        		        	 
        		        	 var catModelo = Ext.create('catModel', {
        		 			    nombre : txtCatalogo.getValue().toUpperCase(),
        		 			    tipo  : cmbTpo.getValue(),
        		 			    definicion : txtDefinicion.getValue(),
        		 			    datos:DatosD,       //Modelo de Datos,
        		 			    datosBorrados:""
        		 			});	
        		        	 catModelo = Ext.JSON.encode(catModelo.data);
//        		        	 	alert(catModelo);
        		        	 	//ENVIAR EL JSON AL SERVLET
        		        	     Ext.Ajax.request({  
        		 					url: rutaServlet,  
        		 					method: 'POST',  
        		 					 success: successAjaxFnN,   
        		 					timeout: 30000,  
        		 					params: {  
        		 					action: 'insertCatalogo',  				
        		 					catModel:catModelo

        		 					 }});
        		         }
        		     }
        		});
        		}
        		else{
        			if(datosStore.getUpdatedRecords().length>0 || datosStore.getRemovedRecords().length>0 || cmbTpo.getValue()=='Dinamico'){
        			Ext.Msg.show({
              		     title:'Actualizar',
              		     msg: '&iquestActualizar catalogo?',
              		     buttons: Ext.Msg.OKCANCEL,
              		     icon: Ext.Msg.QUESTION,
              		     fn: function (btn){
              		    	 if(btn=='ok'){ 
              		    	var FilasActualizadas = datosStore.getUpdatedRecords();
              		    	var FilasBorradas=datosStore.getRemovedRecords();
              		    	var DatosAct = new Array();
              		    	var DatosBorrados = new Array();
       		        	 for (var i=0; i < FilasActualizadas.length; i++)
        					{  
       		        		DatosAct.push(FilasActualizadas[i].data);  
        					}
       		        	 for (var i=0;i<FilasBorradas.length;i++){
       		        		DatosBorrados.push(FilasBorradas[i].data);
       		        	 }
       		        	 DatosAct=Ext.JSON.encode(DatosAct);
       		        	 DatosBorrados=Ext.JSON.encode(DatosBorrados);
       		        	 var catModelo = Ext.create('catModel', {
     		 			    nombre : NombreCatalogo,
     		 			    tipo: cmbTpo.getValue(),
     		 			    definicion : txtDefinicion.getValue(),
     		 			    datos:DatosAct,       //Modelo de Datos,
     		 			    datosBorrados:DatosBorrados
     		 			});	
       		        	catModelo = Ext.JSON.encode(catModelo.data);
       		        	Ext.Ajax.request({  
		 					url: rutaServlet,  
		 					method: 'POST',  
		 					 success: successAjaxFnA,  
		 					timeout: 30000,  
		 					params: {  
		 					action: 'ActualizaCatalogo',
		 					catModel:catModelo
		 					 }});	
              		    	 }
              		     }
           			});
        		}
        		}
        	}
        } 
	 
	    var successAjaxFnA = function(response, request) {
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
	        ActivaEdicion(NombreCatalogo);
	    };
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
	        datosStore.removeAll();
				txtCatalogo.setValue('');
				cmbTpo.setValue('');
    	 	panDatos.disable();
    	 	panNombre.enable();
	    };
	    
	/* 
	 * 
	 * OBJETOS
	 * 
	 */
	 var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	    	clicksToEdit: 1
	    });
	var txtCatalogo = Ext.create('Ext.form.field.Text', {
		id:'txtCatalogo',
		name:'txtCatalogo',
		fieldLabel:'Nombre',
    	allowBlank:false,
    	labelAlign:'left',
    	labelPad:10,
    	width:350,
    	margin:'15 0 0 50',
    	regex: /^[0-9a-zA-Z_]*$/,
    	maskRe: /^[0-9a-zA-Z_]*$/,
    	emptyText:'Nombre catalogo',
    	listeners:{
    		change:function(){
    			txtCatalogo.setValue(txtCatalogo.getValue().toUpperCase());
    		}
    	}
});

	var txtDefinicion = Ext.create('Ext.form.field.TextArea', {
		id:'txtDefinicion',
		name:'txtDefinicion',
		//fieldLabel:'Definicion',
    	allowBlank:false,
    	//labelAlign:'left',
    	//labelPad:10,
    	labelWidth: 0,
		labelSeparator: '<br />',
    	anchor : '100%',
    	hidden: true,
    	margin:'15',
    	grow: true,
    	emptyText:'Definición Catalogo'
	});
	
	var btnCrear=Ext.create('Ext.button.Button',{
		text: 'Crear',
		 formBind: true,
		 width:100,
		 margin:'10 0 0 200',
	        handler: function() {
	        		if(cmbTpo.getValue()=='Dinamico') {
	        			txtDefinicion.show();
	        			panNombre.disable();
	                	panDatos.enable();
	        		} else {
	        			grdDatos.show();
	            		var r = Ext.ModelManager.create({
	                    	id: '1',
	                    	nombre: 'Campo 1'
	                	}, 'datosModel');
	                	r.setDirty(); 
	                	datosStore.insert(datosStore.getCount(), r);
	                	cellEditing.startEditByPosition({row: datosStore.getCount()-1, column: 1});
	                	panNombre.disable();
	                	panDatos.enable();
	                	btnAgregarCampo.enable();
	        		}
	        } 
	           
	});
	var btnCancelar=Ext.create('Ext.button.Button',{
		text: 'Cancelar',
		 formBind: false,
		 iconCls:'cancel',
	        handler: function() {
	            	if(datosStore.getCount()>0){
	            		Ext.Msg.show({
	            		     title:'Cancelar',
	            		     msg: '&iquestSeguro que quieres cancelar? : Se perderan los cambios no guardados.',
	            		     buttons: Ext.Msg.OKCANCEL,
	            		     icon: Ext.Msg.QUESTION,
	            		     fn: function (btn){
	            		         if(btn=='ok'){     
	            		        	 	datosStore.removeAll();
	            		        	 	btnAgregarCampo.disable();
	            		        	 	panDatos.disable();
	            		        	 	panNombre.enable();
	            		        	 	ActivaEdicion(NombreCatalogo);
	            		        	 	
	            		         }
	            		     }
	            		});
	            	}
	        } 
	           
	});
	var btnGuardarCambios=Ext.create('Ext.button.Button',{
		text: 'Guardar',
		 formBind: false,
		 iconCls:'guardar',
	        handler: GuardarCambios
	           
	});
	var cmbTpo= Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Tipo',
	    labelAlign:'left',
    	labelPad:10,
    	width:350,
    	margin:'15 0 0 50',
	    store: tiposStore,
	    queryMode: 'local',
	    displayField: 'tipo',
	    emptyText:'Selecciona...',
	    allowBlank:false, 
	    valueField: 'tipo'
	});
	var btnAgregarCampo=Ext.create('Ext.button.Button',{
		itemId: 'Agregar-campo',
        text: 'Agregar Campo',
        disabled:true,
        iconCls: 'add',
        handler : function(){
        	if(datosStore.getCount()>0){ 		
        	var maxId = datosStore.getMax(datosStore.data.items, 'id')+1;
        	var d = Ext.ModelManager.create({
                id: maxId,
                nombre: 'Campo '+maxId
            }, 'datosModel');
            d.setDirty(); 
            datosStore.insert(datosStore.getCount(), d);
            cellEditing.startEditByPosition({row: datosStore.getCount()-1, column: 1});
        }
        	else{
            	
            	var d = Ext.ModelManager.create({
                    id: '1',
                    nombre: 'Campo '+ '1'
                }, 'datosModel');
                d.setDirty(); 
                datosStore.insert(datosStore.getCount(), d);
                cellEditing.startEditByPosition({row: datosStore.getCount()-1, column: 1});
        	}
        }		
	});
	//GRID

	var grdDatos = Ext.create('Ext.grid.Panel', {
        store:datosStore,
        stateId: 'grdDatos',  
        height:'100%',
        tbar: ['->',btnAgregarCampo],
        hidden: true,
        columns: [
                  {text: 'id', width:25,dataIndex:'id'},
                  {text: 'Nombre',width:425,sortable : true,editor: {xtype: 'textfield',allowBlank: true},dataIndex: 'nombre'},
                  {
                      xtype:'actioncolumn',
                      text: 'Eliminar;',
                      width:50,
                      align:'center',
                      items: [{
                          icon: '../imagenes/iconos/delete.gif',  // Use a URL in the icon config
                          tooltip: 'Eliminar',
                          handler: function(grid, rowIndex, colIndex) {
                        	  grdDatos.getSelectionModel().select(rowIndex);
                        	  var sm = grdDatos.getSelectionModel();
                        	  datosStore.remove(sm.getSelection());
                          }
                      }]}],//Termina Columnas de Grid
				  plugins: [cellEditing]
	
	});
	//TERMINA GRID
	//PANEL NOMBRE
	var viewSize = Ext.getBody().getViewSize();
	var panNombre= Ext.create('Ext.form.Panel',{
		id:'panNombreCatalogo',
		bodyCls:'panNombre',
		layout:'anchor',
		width:600,
		height:150,
		name:'panAgregar',
		title:'Crear Cataogo',
		collapsed:false,
	   items:[txtCatalogo,cmbTpo,btnCrear]});
	//TERMINA PANEL NOMBRE
	//PANEL DATOS
	var panDatos=Ext.create('Ext.form.Panel',{
		id:'panDatos',
	    disabled:true,
		bodyCls:'panDatos',
		layout:'anchor',
		width:'100%',
		height:viewSize.height-205,
		name:'panDatos',
		title:'Datos',
		collapsed:false,
 	   items:[txtDefinicion,grdDatos]
	});
	//TERMINA PANEL DATOS
	//TOOLBAR BOTONCES CREAR Y CANCELAR
	var toolbar=Ext.create('Ext.toolbar.Toolbar', {
	    width   : '100%',
	    height:30,
	    items: [btnCancelar,'->',btnGuardarCambios ]
	});
	
	
	//TERMINA TOOLBAR
	
/*	

	
	/* 
	 * 
	 * PANEL PRINCIPAL
	 * 
	 */	
  var viewSize = Ext.getBody().getViewSize();
	Ext.create('Ext.form.Panel',{
		id:'panPrincipal',
		name:'panPrincipal',
		title:'Administracion de catalogos',
		layout:'anchor',
		width:500,
		height:viewSize.height,
		items:[
		       panNombre
		       ,
		       panDatos
		       ,
		       toolbar
		       ]//TERMINAN LOS ITEMS
		,renderTo: Ext.getBody()
	});//TERMINA PANEL PRINCIPAL
	ActivaEdicion(NombreCatalogo);
});//TERMINA FUNCION ONREADY