Ext.onReady(function(){
	var pantalla=Ext.getBody().getViewSize();
	if(actualizar){
		Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	}
	/*
	 * Store y modelos
	 */
	 Ext.define('modeloPerfil', {
        extend: 'Ext.data.Model',
        fields:  [
        	{name: 'Id', type: 'int'},
        	{name: 'Nombre', type: 'string'},
        	{name: 'Valor', type: 'int'},
        	{name: 'Descripcion', type: 'string'}
        	] 
    });
	 Ext.define('modeloPrivilegios', {
        extend: 'Ext.data.Model',
        fields:  [
        	{name: 'id', type: 'int'},
        	{name: 'nombre', type: 'string'},
        	{name: 'valor', type: 'int'},
        	{name: 'descripcion', type: 'string'},
        	{name: 'seleccionado', type: 'boolean'}
        	] 
    });
	var storePrivilegios=new Ext.data.Store({
		model:'modeloPrivilegios',
		proxy: { 
	            type: 'ajax', 
	            	url: rutaServlet,  							
	            	reader: { 										
	                type: 'json', 
	                root: 'privilegios'
	            },
	            extraParams: 
		         {
		              action: 'obtenerPrivilegiosPerfiles',
		              select:perfil
		          }   
	        },
	        listeners:{
	        	load:function(_this,e){
			        	for(var i=0;i<_this.getCount();i++){
			        		if(_this.getAt(i).get('seleccionado')){
			        				checkBoxPrivilegios.select(_this.getAt(i),true);
			        		}
			        	}
	        		}
	        },
	         autoLoad:true
		});
		var storePerfil=new Ext.data.Store({
		model:'modeloPerfil',
		proxy: { 
	            type: 'ajax', 
	            	url: rutaServlet,  							
	            	reader: { 										
	                type: 'json', 
	                root: 'perfil'
	            },
	            extraParams: 
		         {
		              action: 'listPerfiles',
		              select:perfil
		          }   
	        },
	        listeners:{
	        	load:function(_this,e){
			        	if(_this.getCount()>0){
			        		txtNombre.setValue(_this.getAt(0).get('Nombre'));
			        		txtDescripcion.setValue(_this.getAt(0).get('Descripcion'));
			        	}
			        	Ext.getBody().unmask();
	        		}
	        },
	         autoLoad:true
		});
	/*
	 * Objetos
	 */
	var txtNombre = Ext.create('Ext.form.field.Text', {
		id:'txtNombre',
		name:'txtNombre',
		fieldLabel:'Nombre',
    	allowBlank:false,
    	disabled:actualizar,
    	margin:'10 0 0 20',
    	width:350,
    	emptyText:'Perfil',
    	maxLength:16,
    	enforceMaxLength:true,
    	listeners:{
    		change:function( _this, newValue, oldValue, eOpts ){
    			if(newValue!=""){
    				_this.setValue(newValue.toUpperCase());
    			}
    		}
    	}
});
	var txtDescripcion = Ext.create('Ext.form.field.TextArea', {
		id:'txtDescripcion',
		name:'txtDescripcion',
		fieldLabel:'Descripcion',
    	allowBlank:true,
    	margin:'10 0 0 20',
    	width:350,
    	height:50,
    	emptyText:'Descripcion del perfil',
    	maxLength:70,
    	enforceMaxLength:true
});
	var checkBoxPrivilegios = Ext.create('Ext.selection.CheckboxModel',
	{name:'checkBoxPrivilegios',id:'checkBoxPrivilegios',mode:'MULTI',
	checkOnly:true,enableKeyNav:true	
	});
	var gridPrivilegios=new Ext.grid.Panel({
		id:'gridPrivilegios',
		name:'gridPrivilegios',
		store: storePrivilegios,
		selModel:checkBoxPrivilegios,
		width:'100%',
		height:'100%',
		autoScroll:true,
	    columns: [
	        { text: 'Nombre',  dataIndex: 'nombre', flex:1 },
	        { text: 'Descripcion', dataIndex: 'descripcion', flex: 2 }
	    ]
	});
	var btnGuardar=new Ext.button.Button({
		id:'btnGuardar',
		name:'btnGuardar',
		text:'Guardar',
		iconCls:'btnGuardarIconCls',
		formBind:true,
		handler:function(_this,e){
			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			guardarPerfil();
		}
	});
	/*
	 * Paneles
	 */
	var panelGrid=new Ext.panel.Panel({
		id:'panelGrid',
		name:'panelGrid',
		layout:'fit',
		width:'100%',
		height:pantalla.height-165,
		title:'Privilegios del perfil',
		items:[gridPrivilegios]
	});
	var panelPrincipal=new Ext.form.Panel({
		id:'panelPrincipal',
		name:'panelPrincipal',
		width:500,
		height:pantalla.height,
		title:actualizar?'Modificar perfil':'Crear perfil',
		items:[
		{xtype:'fieldset',id:'fieldGeneral',height:100,border:0,cls:'fieldGeneralCls',items:[txtNombre,txtDescripcion]}
		,panelGrid],
		bbar:['->',btnGuardar],
		renderTo:Ext.getBody()
	});
	/*
	 * Funciones
	 */
	var successAjaxFnN = function(response, request) {
		Ext.getBody().unmask();
	    var jsonData = Ext.JSON.decode(response.responseText);
	    if (true == jsonData.success) {
	        Ext.Msg.show({
	            title: 'Correcto',
	            msg: jsonData.message,
	            buttons: Ext.Msg.OK,
	            icon: Ext.MessageBox.INFO
	        });	
	        if(!actualizar)
	        	document.location.href="crearPerfil.jsp?actualizar=true&nombre="+txtNombre.getValue().toUpperCase();
	    } else {
	        Ext.Msg.show({
	            title: 'Error',
	            msg: jsonData.message,
	            buttons: Ext.Msg.OK,
	            animEl: 'elId',
	            icon: Ext.MessageBox.WARNING
	        });
	    }
	    
	};
	
	function guardarPerfil(){
		if(!actualizar){
			var privilegios=0;
	    	for(var i=0;i<checkBoxPrivilegios.getSelection().length;i++){
		        	privilegios+=checkBoxPrivilegios.getSelection()[i].raw.valor;   					
		     }

			   		var perfilesM = Ext.create('modeloPerfil', {
			   		Id:0,
	 			    Nombre : txtNombre.getValue().toUpperCase(),
	 			    Valor:privilegios,
	 			    Descripcion  : txtDescripcion.getValue()
	 			});	
				perfilesM=Ext.JSON.encode(perfilesM.data);
				Ext.Ajax.request({  
						url: rutaServlet,  
						method: 'POST',  
						 success: successAjaxFnN,   
						timeout: 30000,  
						params: {  
						action: 'creaPerfil',  				
						perfil:perfilesM
				}});
		}
		else{
			var privilegios=0;
		    	for(var i=0;i<checkBoxPrivilegios.getSelection().length;i++){
			        	privilegios+=checkBoxPrivilegios.getSelection()[i].raw.valor;   					
			     }
			   		var perfilesM = Ext.create('modeloPerfil', {
			   		Id:0,
	 			    Nombre : perfil,
	 			    Valor:privilegios,
	 			    Descripcion  : txtDescripcion.getValue()
	 			});	
				perfilesM=Ext.JSON.encode(perfilesM.data);
				Ext.Ajax.request({  
						url: rutaServlet,  
						method: 'POST',  
						 success: successAjaxFnN,   
						timeout: 30000,  
						params: {  
						action: 'actualizaPerfil',  				
						perfil:perfilesM
				}});
		}
	}
});