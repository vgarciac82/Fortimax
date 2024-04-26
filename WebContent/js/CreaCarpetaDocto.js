Ext.require([
     'Ext.form.*',
     'Ext.tip.*',
     'Ext.window.MessageBox'
]);
Ext.onReady(function() {
	/* 
	 * 
	 * VARIABLES
	 * 
	 */
	
	var pantalla = Ext.getBody().getViewSize();
	var contex="";
	if(docto==true){
		contex='Documento';
	}
	else{
		contex='Carpeta';
	}
	
	var context_title='Crear '+contex;
	/*
	 * Store
	 * 
	 */
	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	var storeTipoD = Ext.create('Ext.data.Store', {
	    fields: ['tipo','texto'],
	    data : [
	        {"tipo":"imax","texto":"IMAX_FILE"},
	        {"tipo":"ext","texto":"EXTERNO"}
	    ]
	});
	 Ext.define('Campos', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name: 'nombre', type: 'string'},
	     {name: 'etiqueta', type: 'string'},
	     {name: 'orden', type: 'int'},
	     {name: 'longitud', type: 'int'},
	     {name: 'valor', type: 'string'},
	     {name: 'tipo'},
	     {name: 'indice'},
	     {name: 'requerido', type: 'bool'},
	     {name: 'editable', type: 'bool'},
	     {name: 'lista',type:'string'}
	    ],
	    idProperty:'Nombre'
	    });
	 var storeCampos = new Ext.data.Store({ 
	        model: 'Campos', 
	        proxy: { 
	            type: 'ajax', 
	            url: rutaServlet,  							
	            	reader: { 										
	                type: 'json', 
	                root: 'campos'
	            },
	            extraParams: 
		         {
		              action: 'getTDoc',
		              select:''
		          } 
	        },
	        sorters: [{
	             property: 'orden',
	             direction:'ASC'
	         }],
	        listeners:{
		    	 load:function(){
		    		 if(this.getCount()>0){
		    			 interfazPersonalizada();
		    		 }
		    			 
		    	 }
		     },
	        autoLoad:false
	    });
	 Ext.define('documentos', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name: 'Id', type: 'int'},
	     {name: 'Nombre', type: 'string'},
	     {name: 'Descripcion', type: 'string'}
	    ],
	    idProperty:'Id'
	    });
	 var Col=Ext.ModelManager.create({
 		Id:-1,
 		Nombre:'Ninguna',
 		Descripcion:''
 	},'documentos');
	 var storeDoc = new Ext.data.Store({ 
	        model: 'documentos', 
	        proxy: { 
	            type: 'ajax', 
	            url: rutaServlet,//'../jsonPrueba/documentos.json',  							
	            	reader: { 										
	                type: 'json', 
	                root: 'documentos'
	            },
	            extraParams: 
		         {
		              action: 'getTDocs'
		          } 
	        } ,
	        listeners:{
		    	 load:function(){
		    		 this.insert(0,Col);
		    		 
		    			 onLoa();	 
		    	 }
		     },
	        autoLoad:true
	    });
	 Ext.define('SaveModelD', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name: 'nodo', type: 'string'},
	     {name: 'nombre', type: 'string'},
	     {name: 'tipoDocumento', type: 'string'},
	     {name: 'descripcion', type: 'string'},
	     {name: 'idPlantilla', type: 'string'},
	     {name: 'camposPlantilla', type: 'string'}
	    ]
	    });
	 Ext.define('SaveModelC', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name: 'nodo', type: 'string'},
	     {name: 'nombre', type: 'string'},
	     {name: 'descripcion', type: 'string'},
	     {name: 'usePassword', type: 'boolean'},
	     {name: 'password', type: 'string'}
	    ]
	    });
	 /*
	  * Objetos
	  */
	 var txtNombre = Ext.create('Ext.form.field.Text', {
			id:'txtNombre',
			name:'txtNombre',
			fieldLabel:'Nombre',
	    	allowBlank:false,
	    	labelAlign:'left',
	    	width:400,
	    	labelPad:35,
	    	margin:'15 0 0 20',
	    	emptyText:'Nombre de '+contex
	});
	 var cmbTipoD=Ext.create('Ext.form.ComboBox', {
		    fieldLabel: 'Tipo de documento',
		    store: storeTipoD,
		    emptyText:'Selecciona...',
		    queryMode: 'local',
		    displayField: 'texto',
		    editable: false,
		    width:360,
		    labelWidth:120,
		    labelPad:55,
		    valueField: 'tipo',
		    value:'imax',
		    margin:'15 0 0 40',
		    hidden:true
		});
	 var txtDescripcion=Ext.create('Ext.form.field.TextArea',{
		 	grow      : true,
	        name      : 'txtDescripcion',
	        fieldLabel: 'Descripcion',
	        width:400,
	    	labelPad:35,
	        margin:'15 0 0 20'
	 });
	 
	 var cmbPlantilla=Ext.create('Ext.form.ComboBox', {
		 id:'cmbPlantilla',
		 name:'cmbPlantilla',
		    fieldLabel: 'Plantilla de documento',
		    store: storeDoc,
		    emptyText:'-Ninguna-',
		   // queryMode: 'local',
		    displayField: 'Nombre',
		    editable: false,
		    width:400,
		    labelWidth:140,
		    labelPad:35,
		    valueField: 'Id',
		    value:-1,
		    margin:'15 0 0 20',
		    listeners:{
		    	change:function( cmb, newValue, oldValue, eOpts ){
		    		if(newValue!=-1){
		    			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		    			reiniciaForm();
		    			panelForm.setVisible(false);
		    			panelForm.setTitle('Formulario: '+cmb.getRawValue());
		    			panelForm.removeAll();
		    			storeCampos.load({
		  				  params: {
		  					  action:'getTDoc',
							  select: newValue
						  }
						});
		    		}
		    		else{
		    			panelForm.setVisible(false);
		    			panelForm.removeAll();
		    			Ext.getCmp('panPrincipal').setHeight(pantalla.height);
		    			reiniciaForm();
		    		}
		    	}//,
//		    	afterrender:function(){
//		    		this.setValue(-1);
//		    	}
		   }
		});

	 var anC=((pantalla.width/2)-90).toString();
	 var btnGuardar=Ext.create('Ext.button.Button',{
			text: 'Guardar',
			 formBind: true,
			 width:180,
			 height:40,
			 scale: 'large',
			 margin:'10 0 0 '+anC,
			 iconCls:'btnGuardar',
			 componentCls:'boton',
//			 disabledCls:'botonD',
		        handler: function() {
		        	if(docto==true){
		        	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		            	guardar();
		        	}
		        	else{
		        		if(chbPass.checked==true&&txtContrase.getValue()==""&&txtContrase2.getValue()==""){
		        			txtContrase.focus();
		        		}
		        		else{
		        			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			            	guardar();
		        		}
		        	}
		        } 
		           
		});
	 var txtContrase= Ext.create('Ext.form.field.Text',{
			id:'txtContrase',
			name:'txtContrase',
			margin:'10 0 0 20',
			fieldLabel:'Contrase&ntilde;a',
			inputType: 'password',
			labelWidth:150,
			disabled:true,
			allowBlank:true
		});
	 Ext.apply(Ext.form.VTypes, {
		    password : function(val, field) {
		        if (field.initialPassField) {
		           var pwd = Ext.getCmp(field.initialPassField);
		           return (val == pwd.getValue());
		        }
		        return true;
		     },
		    passwordText : 'Las contrase&ntilde;as no son iguales'
		});
	 var txtContrase2= Ext.create('Ext.form.field.Text',{
			id:'txtContrase2',
			name:'txtContrase2',
			margin:'10 0 0 20',
			labelWidth:150,
			fieldLabel:'Confirmacion de contrase&ntilde;a',
			inputType: 'password',
			vtype:'password',
			initialPassField: 'txtContrase',
			disabled:true,
			allowBlank:true
		});
	 
	 var chbPass=Ext.create('Ext.form.field.Checkbox',{
		 name:'chbPass',
		 labelWidth:300,
		 id:'chbPass',
		 margin:'10 0 0 20',
		 fieldLabel:'&iquest;Desea proteger con contrase&ntilde;a la carpeta?',
		 handler:function(){
			 if(this.checked==true){
				 txtContrase.setDisabled(false);
				 txtContrase2.setDisabled(false);
				 txtContrase.allowBlank=false;
				 txtContrase2.allowBlank=false;
				 
			 }
			 else{
				 txtContrase.setDisabled(true);
				 txtContrase2.setDisabled(true);
				 txtContrase.allowBlank=true;
				 txtContrase2.allowBlank=true;
				 txtContrase.setValue('');
				 txtContrase2.setValue('');
			 }
		 }
	 });
	/*
	 * PANEL PRINCIPAL
	 * 
	 */
	 var centrar=((pantalla.width-500)/2).toString();
	 if(docto){
	 var panelD=Ext.create('Ext.form.Panel',{
			id:'panelD',
			name:'panelD',
			cls:'panelD',
			layout:'anchor',
			title:'Crear '+contex,
			width:'100%',
			height:240,//pantalla.height-40,
			scroll:false,
			autoScroll:false,
			border:false,
			items:[
			       {xtype:'fieldset',name:'fieldSetCentrar',cls:'fieldSetCentrarCls',items:[
			       {xtype:'fieldset',name:'fieldSetPanel',cls:'fieldSetPanelCls',width:500,margin:'0 0 0 '+centrar,
			    	   items:[txtNombre,cmbTipoD,txtDescripcion,cmbPlantilla]}]}
			       ]
		});
	 }
	 else{
		 var panelD=Ext.create('Ext.form.Panel',{
				id:'panelD',
				name:'panelD',
				cls:'panelD',
				layout:'anchor',
				title:'Crear '+contex,
				width:'100%',
				height:270,//pantalla.height-40,
				scroll:false,
				autoScroll:false,
				border:false,
				items:[
				       {xtype:'fieldset',name:'fieldSetCentrar',cls:'fieldSetCentrarCls',items:[
				       {xtype:'fieldset',name:'fieldSetPanel',cls:'fieldSetPanelCls',width:500,margin:'0 0 0 '+centrar,
				      items:[txtNombre,txtDescripcion,chbPass,txtContrase,txtContrase2]}]}
				       
				       ]
			});
	 }
	var panelForm=Ext.create('Ext.form.Panel',{
		id:'panForm',
		name:'panForm',
		cls:'panForm',
		layout:'anchor',
		title:'Formulario: ',
		width:'100%',
//		height:pantalla.height,
		scroll:false,
		autoScroll:false,
		hidden:true
	});
	var scroll;
	if(pantalla.height<360){
		scroll=true;
	}
	else{
		scroll=false;
	}
	Ext.create('Ext.form.Panel',{
		id:'panPrincipal',
		name:'panPrincipal',
		cls:'panPrincipal',
		header:false,
		layout:'anchor',
		width:'100%',
		height:pantalla.height,
		scroll:scroll,
		autoScroll:scroll,
		items:[panelD,panelForm,btnGuardar],
//		bbar:[btnGuardar],
		renderTo: Ext.getBody()
	});
	/*
	 * FUNCIONES
	 * 
	 */	
		btnGuardar.setText('Crear '+contex);
	function onLoa(){
		Ext.getBody().unmask();
	}
	function interfazPersonalizada(){
		for(var i=0;i<storeCampos.getCount();i++){
			creaFormulario(storeCampos.getAt(i).get('tipo'),storeCampos.getAt(i).get('nombre'),storeCampos.getAt(i).get('etiqueta'),storeCampos.getAt(i).get('longitud'),storeCampos.getAt(i).get('valor'),storeCampos.getAt(i).get('requerido'),storeCampos.getAt(i).get('editable'),storeCampos.getAt(i).get('lista'),panelForm)
		}
		panelForm.setVisible(true);
		var Ta=400+panelForm.getHeight();
		if(Ta<pantalla.height)
			Ta=pantalla.height;
		Ext.getCmp('panPrincipal').setHeight(Ta);
		Ext.getBody().unmask(); 
	}
	var successAjaxFnN = function(response, request) {
		Ext.getBody().unmask();
        var jsonData = Ext.JSON.decode(response.responseText);
        if (true == jsonData.success) {
            Ext.example.msg("Correcto",jsonData.message);
            /*txtNombre.setValue('');
            txtDescripcion.setValue('');*/
            if(docto==true){
            	/*panelForm.setVisible(false);
    			panelForm.removeAll();
    			Ext.getCmp('panPrincipal').setHeight(pantalla.height);
    			reiniciaForm();
    			cmbPlantilla.setValue(-1);*/
    			top.left.location.href=jsonData.redirectLeftWindow;
    			window.location=jsonData.redirectMainWindow;
            }
            else{
            	/*txtContrase.setValue('');
            	txtContrase2.setValue('');
            	chbPass.setValue(false);*/
            	 top.left.location.href=jsonData.redirectLeftWindow;
            	 window.location=jsonData.redirectMainWindow;
            	 top.superior.location.href=jsonData.redirectSuperiorWindow;
            }

        } else {
            Ext.Msg.show({
                title: 'Error',
                msg: jsonData.message,
                buttons: Ext.Msg.OK,
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }
       
//        top.left.location.href=jsonData.redirectMainWindow;//'http://localhost:8080/Fortimax/jsp/ArbolExpediente.jsp?select=USR_GRALES_G26C2&arbol.tipo=USR_GRALES_G26C2';
    };
	function guardar(){
		if(docto==true){
			var datos=Ext.create('SaveModelD',{
				nodo:matricula,
				nombre:txtNombre.getValue(),
				tipoDocumento:'-1',/*cmbTipoD.getValue(),//No se usa esta seccion pero no se ha definido su eliminacion*/
				descripcion:txtDescripcion.getValue(),
				idPlantilla:cmbPlantilla.getValue()
//				camposPlantilla: Ext.JSON.encode(panelForm.getValues())
			});
    		
			var objPlantilla = {camposPlantilla: panelForm.getValues()};
    		var camposTotales = Ext.Object.merge(datos.data, objPlantilla);
    		camposTotales = Ext.JSON.encode(camposTotales);
			
//    		datos = Ext.JSON.encode(datos.data);
			Ext.Ajax.request({  
				url: rutaServlet,  
				method: 'POST',  
				 success: successAjaxFnN,   
				timeout: 30000,  
				params: {  
				action: 'saveDocument',
				select: camposTotales
				 }});
		}
		else{
			var datos=Ext.create('SaveModelC',{
				nodo:matricula,
				nombre:txtNombre.getValue(),
				descripcion:txtDescripcion.getValue(),
				usePassword:chbPass.checked,
				password:hex_md5(txtContrase.getValue())
			});
			datos = Ext.JSON.encode(datos.data);
			Ext.Ajax.request({  
				url: rutaServlet,  
				method: 'POST',  
				 success: successAjaxFnN,   
				timeout: 30000,  
				params: {  
				action: 'saveFolder',
				select: datos
				 }});
		}
		Ext.getBody().unmask();
	}
});
/**
 * ACTIONS:
 * getTDoc 			Para obtener campos de un tipo de documento
 * getTDocs 		Obtiene los tipos de imx_catalogo_tipo_documento
 * saveDocument		Crea documento
 * saveFolder		Crea carpeta
 * 
 * MODEL DOCUMENTOCARPETA:
 * 	Matricula, 
 * 	Nombre, 
 * 	Descripcion, 
 * 	UsaPassword, 
 *	Password, 
 * 	TipoDocumento,  
 * 	NombrePlantilla, 
 * 	PlantillaDocumento
 * 
 */