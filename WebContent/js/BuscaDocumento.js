Ext.onReady(function(){
  /*
   * Variables
   */
	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	var pantalla=Ext.getBody().getViewSize();
	var context_title="Busqueda";
	var varBusqueda="general";
	/*
	 * Store y Modelos
	 */
	Ext.define('gavetasModel',{
	extend:'Ext.data.Model',
	fields:[
		{name:'Nombre',type:'string'},
		{name:'Descripcion',type:'string'}
		]
	});
	var ColMD=Ext.ModelManager.create({
 		Nombre:'Mis Documentos',
 		Descripcion:'Carpeta personal'
 	},'gavetasModel');
 	var ColTo=Ext.ModelManager.create({
 		Nombre:'Todas',
 		Descripcion:'Todas las gavetas sobre las que tienes permisos, incluyendo Mis Documentos'
 	},'gavetasModel');
	var storeGavetas=new Ext.data.Store({
	model:'gavetasModel',
	proxy: { 
            type: 'ajax', 
            	url: rutaServlet,//+'/gavetasBusqueda.json',  							
            	reader: { 										
                type: 'json', 
                root: 'gavetas'
            },
            extraParams: 
	         {
	              action: 'getGavetasBusqueda'
	          }
        },listeners:{
		    	 load:function(){
		    		 this.insert(0,ColMD);	
		    		 this.insert(0,ColTo);
		    		 cmbGavetas.setValue(titulo_aplicacion);
		    		 storeDoc.load();
		    	 }
		     },
        autoLoad:true
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
 		Descripcion:'Ninguna plantilla de documento'
 	},'documentos');
	 var storeDoc = new Ext.data.Store({ 
	        model: 'documentos', 
	        proxy: { 
	            type: 'ajax', 
	            url: rutaServlet,  							
	            	reader: { 										
	                type: 'json', 
	                root: 'documentos'
	            },
	            extraParams: 
		         {
		              action: 'getTiposDocumentos'
		          } 
	        } ,
	        listeners:{
		    	 load:function(){
		    		 this.insert(0,Col);	
		    		 Ext.getBody().unmask();
		    	 }
		     },
	        autoLoad:false
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
	    idProperty:'nombre'
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
	             property: 'Orden',
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
	    //Modelos para enviar datos
	     Ext.define('modeloGeneral', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name:'Carpeta', type:'string'},
	     {name: 'Nombre', type: 'string'},
	     {name: 'Descripcion', type: 'string'},
	     {name: 'CreacionInicio', type: 'string'},
	     {name: 'CreacionFin', type: 'string'},
	     {name: 'ModificacionInicio', type: 'string'},
	     {name: 'ModificacionFin', type: 'string'},
	     {name: 'VencimientoInicio', type: 'string'},
	     {name: 'VencimientoFin', type: 'string'}
	    ]
	    });
	    Ext.define('modeloContenido', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name:'Carpeta', type:'string'},
	     {name: 'Contenido', type: 'string'}
	    ]
	    });
	    Ext.define('modeloPlantilla', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name:'Carpeta', type:'string'},
	     {name: 'Plantilla', type: 'string'},
	     {name: 'DatosPlantilla', type: 'string'}
	    ]
	    });
	/*
	 * Objetos
	 */

	var cmbGavetas=new Ext.form.ComboBox({
			id:'cmbGavetas',
			store:storeGavetas,
			fieldLabel:'Buscar en',
			hideLabel :true,
			displayField:'Nombre',
			valueField:'Nombre',
			width:500,
			isFormField:false,
			queryMode: 'local',
			multiSelect: false,
			forceSelection:true,
			listConfig: {
        	getInnerTpl: function() {
            return '<div ><font class="comboGavetas">{Descripcion}</font><p class="descripcion">{Nombre}</p></div>';
        }
    },
    		listeners:{
    			select:function( combo, records, eOpts ){
    				var valor=records[0].data.Nombre;
    				if(valor=='Mis Documentos'){
    					
    				}
    				else{
    					if(valor!='Todas'){
    						//parent.frames.left.location=basePath+'/jsp/ArbolExpediente.jsp?select='+valor+'&arbol.tipo=g';
    					}
    				}
    				
    		
    				
    			}
    		}
	});
	var txtContenido=new Ext.form.field.Text({
		id:'txtContenido',
		name:'txtContenido',
		fieldLabel:'Texto',
		emptyText:'Texto a buscar dentro de los documentos',
		margin:'10 0 0 10',
		width:520,
		labelWidth:50
		
	});
	var txtNombre=new Ext.form.field.Text({
		id:'txtNombre',
		fieldLabel:'Nombre',
		emptyText:'Documento',
		margin:'10 0 0 10',
		width:370
	});
	var txtDescripcion=new Ext.form.field.TextArea({
		id:'txtDescripcion',
		fieldLabel:'Descripcion',
		emptyText:'Descripcion',
		margin:'10 0 0 10',
		width:370,
		height:100
	});
	var dateCreacion1=new Ext.form.field.Date({
		id:'dateCreacion1',
		fieldLabel: 'Desde',
		labelPad:0,
		labelWidth:40,
		width:150,
        name: 'dateCreacion1',
        margin:'0 0 0 3',
        maxValue: new Date()
	});
	var dateCreacion2=new Ext.form.field.Date({
		id:'dateCreacion2',
		fieldLabel: 'Hasta',
		labelPad:0,
		labelWidth:40,
		width:150,
        name: 'dateCreacion2',
        margin:'0 0 0 3',
        maxValue: new Date()
	});
	var dateModificacion1=new Ext.form.field.Date({
		id:'dateModificacion1',
		fieldLabel: 'Desde',
		labelPad:0,
		labelWidth:40,
		width:150,
        name: 'dateModificacion1',
        margin:'0 0 0 3',
        maxValue: new Date()
	});
	var dateModificacion2=new Ext.form.field.Date({
		id:'dateModificacion2',
		fieldLabel: 'Hasta',
		labelPad:0,
		labelWidth:40,
		width:150,
        name: 'dateModificacion2',
        margin:'0 0 0 3',
        maxValue: new Date()
	});
	var dateVencimiento1=new Ext.form.field.Date({
		id:'dateVencimiento1',
		fieldLabel: 'Desde',
		labelPad:0,
		labelWidth:40,
		width:150,
        name: 'dateVencimiento1',
        margin:'0 0 0 3'
	});
	var dateVencimiento2=new Ext.form.field.Date({
		id:'dateVencimiento2',
		fieldLabel: 'Hasta',
		labelPad:0,
		labelWidth:40,
		width:150,
        name: 'dateVencimiento2',
        margin:'0 0 0 3'
	});
		 var cmbPlantilla=Ext.create('Ext.form.ComboBox', {
		 id:'cmbPlantilla',
		 name:'cmbPlantilla',
		    fieldLabel: 'Plantilla de documento',
		    store: storeDoc,
		    emptyText:'-Ninguna-',
		   queryMode: 'local',
		    displayField: 'Nombre',
		    editable: false,
		    width:520,
		    labelWidth:150,
		    labelPad:35,
		    margin:'10 0 0 10',
		    valueField: 'Id',
		    value:-1,
		    listConfig: {
        	getInnerTpl: function() {
            return '<div ><font class="comboGavetas">{Nombre}</font><p class="descripcion">{Descripcion}</p></div>';
        }
    },
		    listeners:{
		    	change:function( cmb, newValue, oldValue, eOpts ){
		    		if(newValue!=-1){
							Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		    			reiniciaForm();
		    			fieldSetForm.setVisible(false);
		    			fieldSetForm.removeAll();
		    			storeCampos.load({
		  				  params: {
		  					  action:'getTDoc',
							  select: newValue
						  }
						});
		    		}
		    		else{
							fieldSetForm.setVisible(false);
		    			fieldSetForm.removeAll();
		    			//Ext.getCmp('panPrincipal').setHeight(pantalla.height);
		    			reiniciaForm();
		    		}
		    	}
		   }
		});
		
		var btnLimpiar=new Ext.button.Button({
			id:'btnLimpiar',
			name:'btnLimpiar',
			text:'Limpiar',
			//height:35,
			//width:80,
			scale: 'large',
			iconCls:'btnLimpiar',
			handler:function(){
				panelPricipal.getForm().reset();
			}
		});
		var btnBuscar=new Ext.button.Button({
			id:'btnBuscar',
			name:'btnBuscar',
			text:'Buscar',
			//height:35,
			//width:80,
			scale: 'large',
			iconCls:'btnBuscar',
			handler:function(){
				if(varBusqueda=='general')
					buscar(1);
				else if(varBusqueda=='contenido')
					buscar(2);
				else{
					if(cmbPlantilla.getValue()!=-1)
					buscar(3);
				}
			}
		});
		var labelCarpeta=new Ext.form.Label({
			id:'labelCarpeta',
			html:'Buscar en: OSCAR'
		});
		var toolbar=Ext.create('Ext.toolbar.Toolbar',{
			id:'toolbar',
			height:50,
			width:'100%',
			layout:{
        		pack: 'center'
    		},
			items:[{ xtype: 'tbspacer' },btnLimpiar,'-',btnBuscar]
		});
		var radioGenerales=new Ext.form.field.Radio({
			boxLabel:'Búsqueda  en General',
			margin:'10 0 0 10',
			name:'Busqueda',
			id:'radioGeneral',
			checked:true,
			cls:'radio',
			handler:function(){
				if(this.getValue()){
					activa(1);	
				}
			}
		});
		var radioContenido=new Ext.form.field.Radio({
			boxLabel:'Buscar en Contenido',
			margin:'10 0 0 10',
			name:'Busqueda',
			id:'radioContenido',
			checked:false,
			cls:'radio',
			handler:function(){
				if(this.getValue()){
					activa(2);	
				}
			}
		});
		var radioDocumento=new Ext.form.field.Radio({
			boxLabel:'Busqueda en Detalles',
			margin:'10 0 0 10',
			name:'Busqueda',
			id:'radioDocumento',
			checked:false,
			cls:'radio',
			handler:function(){
				if(this.getValue()){
					activa(3);	
				}
			}
		});
	/*
	 * Paneles
	 */
	var fieldSetConfig=new Ext.form.FieldSet({
	title:'<span class="titulo">Buscar en:</span>',
	cls:'fieldSetConfig',
	height:50,
	layout:{
				type:'anchor',
        		pack: 'center'
    		},
	items:[cmbGavetas]
	});
	var fieldSetDatosIzq=new Ext.form.FieldSet({
		cls:'fieldSetDatosIzq',
		height:180,
		items:[txtNombre,txtDescripcion]
	});
	var fieldSetDatosDer=new Ext.form.FieldSet({
		cls:'fieldSetDatosDer',
		height:180,		
		items:[
			{xtype:'fieldset',minWidth:400,layout:'hbox',margin:'10 0 0 10',title:'Fecha de creación:',cls:'fieldSetFechas',items:[dateCreacion1,dateCreacion2]},
			{xtype:'fieldset',minWidth:400,layout:'hbox',margin:'10 0 0 10',title:'Fecha de modificación:',cls:'fieldSetFechas',items:[dateModificacion1,dateModificacion2]},
			{xtype:'fieldset',minWidth:400,layout:'hbox',margin:'10 0 0 10',title:'Fecha de vencimiento:',cls:'fieldSetFechas',items:[dateVencimiento1,dateVencimiento2]}
			]
	});
	var fieldSetEspecificos=new Ext.form.FieldSet({
		title:'Busqueda en contenido',
		cls:'fieldSetEspecificos',
		border:false,
		disabled:true,
		height:60,
		items:[txtContenido]
	});
	var fieldSetDatos=new Ext.form.FieldSet({
		title:'Búsqueda en atributos generales',
		cls:'fieldSetDatos',
		layout:'hbox',
		border:false,
		items:[fieldSetDatosIzq,fieldSetDatosDer]
	});
	
	var fieldSetForm=new Ext.form.FieldSet({
		cls:'fieldSetForm',
		hidden:true,
		items:[]
	});
	var fieldSetTipo=new Ext.form.FieldSet({
		title:'Busqueda por detalles del tipo de Documento',
		cls:'fieldSetTipo',
		border:false,
		disabled:true,
		items:[cmbPlantilla,fieldSetForm]
	});
	var panelPricipal=new Ext.form.Panel({
	id:'panelPricipal',
	title:context_title,
	autoScroll:true,
	tbar:toolbar,
	minWidth:600,
	items:[fieldSetConfig,radioGenerales,fieldSetDatos,radioContenido,fieldSetEspecificos,radioDocumento,fieldSetTipo],
	renderTo:Ext.getBody()
	});
	/*
	 * Functiones
	 */
	Ext.EventManager.onWindowResize(function(w, h){
		if(w>=850){
      		panelPricipal.setWidth(w);
      		fieldSetDatosIzq.setWidth(w/2);
      		fieldSetDatosDer.setWidth(w/2);
		}
		else{
			panelPricipal.setWidth(850);
      		fieldSetDatosIzq.setWidth(425);
      		fieldSetDatosDer.setWidth(425);
		}
	});
	function interfazPersonalizada(){
		for(var i=0;i<storeCampos.getCount();i++){
			creaFormulario(storeCampos.getAt(i).get('tipo'),storeCampos.getAt(i).get('nombre'),storeCampos.getAt(i).get('etiqueta'),storeCampos.getAt(i).get('longitud'),storeCampos.getAt(i).get('valor'),storeCampos.getAt(i).get('requerido'),storeCampos.getAt(i).get('editable'),storeCampos.getAt(i).get('lista'),fieldSetForm)
		}
		fieldSetForm.setVisible(true);
		Ext.getBody().unmask(); 
	}
	function activa(opcion){
		switch(opcion){
			case 1:
				fieldSetDatos.setDisabled(false);
				fieldSetEspecificos.setDisabled(true);
				fieldSetTipo.setDisabled(true);
				varBusqueda="general";
				break;
			case 2:
				fieldSetDatos.setDisabled(true);
				fieldSetEspecificos.setDisabled(false);
				fieldSetTipo.setDisabled(true);
				varBusqueda="contenido";
				break;
			case 3:
				fieldSetDatos.setDisabled(true);
				fieldSetEspecificos.setDisabled(true);
				fieldSetTipo.setDisabled(false);
				varBusqueda="tipoDocumento";
				break;
		}
	}
	var successAjaxFnN = function(response, request) {
		Ext.getBody().unmask();
        var jsonData = Ext.JSON.decode(response.responseText);
        if (!jsonData.success) {
              Ext.Msg.show({
                title: 'Error',
                msg: jsonData.message,
                buttons: Ext.Msg.OK,
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }
        if(jsonData.redirectLeftWindow!=null&&jsonData.redirectLeftWindow!=""){
        	 top.left.location.href=jsonData.redirectLeftWindow;
        }
        if(jsonData.redirectMainWindow!=null&&jsonData.redirectMainWindow!=""){
        	 window.location=jsonData.redirectMainWindow;
        }
        if(jsonData.redirectSuperiorWindow!=null&&jsonData.redirectSuperiorWindow!=""){
        	top.superior.location.href=jsonData.redirectSuperiorWindow;
        }
        	
            	
            	 
	}
	function buscar(opcion){
		Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		var Carpeta=cmbGavetas.getValue();
		var busqueda="";
		switch(opcion){
			case 1:
				busqueda='general';
				var datos=Ext.create('modeloGeneral',{
					Carpeta:Carpeta,
					Nombre:txtNombre.getValue(),
					Descripcion:txtDescripcion.getValue(),
					CreacionInicio:dateCreacion1.getValue(),
					CreacionFin:dateCreacion2.getValue(),
					ModificacionInicio:dateModificacion1.getValue(),
					ModificacionFin:dateModificacion2.getValue(),
					VencimientoInicio:dateVencimiento1.getValue(),
					VencimientoFin:dateVencimiento2.getValue()
				});
				datos=Ext.JSON.encode(datos.data);
				console.log(datos);
				break;
			case 2:
				busqueda='contenido';
				var datos=Ext.create('modeloContenido',{
					Carpeta:Carpeta,
					Contenido:txtContenido.getValue()
				})
				datos=Ext.JSON.encode(datos.data);
				break;
			case 3:
				busqueda='plantilla';
				var datos=Ext.create('modeloPlantilla',{
					Carpeta:Carpeta,
					Plantilla: cmbPlantilla.getValue(),
					DatosPlantilla:getJsonForm()
				})
				datos=Ext.JSON.encode(datos.data);
				break;
		}
		Ext.Ajax.request({  
				url: rutaServlet,  
				method: 'POST',  
				 success: successAjaxFnN,   
				timeout: 30000,  
				params: {  
				action: 'busquedaDocumentos',
				select: busqueda,
				datos:datos
		}});
	}
});