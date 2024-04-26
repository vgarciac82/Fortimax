Ext.onReady(function(){
	/*
	 * Variables
	 */
	var pantalla=Ext.getBody().getViewSize();
	
		//Configuracion
		var altoPanelFiltros=150;
		var anchoPanelFiltrosAvanzados=300;
	/*
	 * Modelos
	 */
		Ext.define('Campos',{
			extend:'Ext.data.Model',
			fields:[
			        {name: 'nombre', type: 'string'},
			        {name: 'etiqueta', type: 'string'},
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
		Ext.define('documentos', {
	        extend: 'Ext.data.Model',
	        fields:[
				     {name: 'Id', type: 'int'},
				     {name: 'Nombre', type: 'string'},
				     {name: 'Descripcion', type: 'string'}
				    ],
			idProperty:'Id'
	    });
	    Ext.define('gavetasModel',{
			extend:'Ext.data.Model',
			fields:[
				{name:'Nombre',type:'string'},
				{name:'Descripcion',type:'string'}
				]
		});
		Ext.define('estructuraModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'ide',     type: 'string'},
            {name: 'texto',     type: 'string'},
            {name:'tipo', type:'string'},
            {name:'atributos', type:'string'}
        ]
    });
    Ext.define('exportModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'gaveta',     type: 'string'},
            {name: 'filtroGenerales',     type: 'bool'},
            {name: 'fechaInicioCreado',     type: 'date'},
            {name: 'fechaFinCreado',     type: 'date'},
            {name: 'fechaInicioModificado',     type: 'date'},
            {name: 'fechaFinModificado',     type: 'date'},
            {name: 'fechaInicioOk',     type: 'date'},
            {name: 'fechaFinOk',     type: 'date'},
            {name: 'estado',     type: 'string'},
            {name: 'filtroCamposGaveta',     type: 'bool'},
            {name: 'camposGaveta',     type: 'array'},
            {name: 'filtroCamposPlantilla',     type: 'bool'},
            {name: 'plantilla',     type: 'string'},
            {name: 'camposPlantilla',     type: 'array'},
            {name: 'filtroTipoDocumento',     type: 'bool'},
            {name: 'nodoTipoDocumento',     type: 'string'},
            {name: 'filtroAtributos',     type: 'bool'},
            {name: 'atributos',     type: 'array'},
            {name: 'filtroDatosDocumento',     type: 'bool'},
            {name: 'nombreDocumento',     type: 'string'},
            {name: 'descripcionDocumento',     type: 'string'},
            {name: 'fechaInicioCreadoDocumento',     type: 'date'},
            {name: 'fechaFinCreadoDocumento',     type: 'date'},
            {name: 'fechaInicioModificadoDocumento',     type: 'date'},
            {name: 'fechaFinModificadoDocumento',     type: 'date'}
        ]
    });
    Ext.define('generalModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'nombre',     type: 'string'},
			{name: 'valor',     type: 'string'}
        ]
    });
	/*
	 * Stores
	 */
		var storeStatus = Ext.create('Ext.data.Store', {
		    fields: ['status', 'nombre'],
		    data : [
		        {"status":"cualquiera", "nombre":"Cualquiera"},
		        {"status":"nocumple", "nombre":"No cumple"},
		        {"status":"cumple", "nombre":"Cumple"}
		    ]
		});
		var storeCampos=new Ext.data.Store({
			model:'Campos',
			proxy:{
				type:'ajax',
				url:rutaServletOperaciones,
				reader:{
					type:'json',
					root:'Campos'
				},
				extraParams:{
					action:'getgaveta',
					select:select
				}
			},
			listeners:{
				load:function(){					
					creaFormularioCampos();
				},
				beforeload:function(_this,e){
					fieldSet2.setDisabled(true);
				}
			},
			autoLoad:selectValido
		});
		 var storePlantillas = new Ext.data.Store({ 
	        model: 'documentos', 
	        proxy: { 
	            type: 'ajax', 
	            url: rutaServletPlantilla,  							
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
		    	 load:function(_this,e){
		    	 	var Col=Ext.ModelManager.create({
				 		Id:-1,
				 		Nombre:'Ninguna',
				 		Descripcion:''
				 	},'documentos');
		    		 _this.insert(0,Col);	 
		    	 }
		     },
	        autoLoad:true
	    });
	     var storeCamposPlantilla = new Ext.data.Store({ 
	        model: 'Campos', 
	        proxy: { 
	            type: 'ajax', 
	            url: rutaServletPlantilla,  							
	            	reader: { 										
	                type: 'json', 
	                root: 'campos'
	            },
	            extraParams: 
		         {
		              action: 'getTDoc'
		          } 
	        },
	        sorters: [{
	             property: 'orden',
	             direction:'ASC'
	         }],
	        listeners:{
		    	 load:function(){
		    		creaFormularioPlantilla();
		    	 }
		     },
	        autoLoad:false
	    });
	    var storeGavetas=new Ext.data.Store({
			model:'gavetasModel',
			proxy: { 
		            type: 'ajax', 
		            	url: rutaServletPlantilla,						
		            	reader: { 										
		                type: 'json', 
		                root: 'gavetas'
		            },
		            extraParams: 
			         {
			              action: 'getGavetasBusqueda'
			          }
		        },
		        listeners:{
		        	load:function(_this,e){
		        		var Col=Ext.ModelManager.create({
					 		Nombre:'Todas',
					 		Descripcion:'Todas las gavetas'
					 	},'documentos');
					 	_this.insert(0,Col);
		        	}
		        },
		        autoLoad:true
			});
			 var estructuraStore = Ext.create('Ext.data.TreeStore', {
	        model: 'estructuraModel',
	        autoLoad: false,
	        proxy: {
	            type: 'ajax',
	            url: rutaServletOperaciones,
	            reader: {
		             type: 'json'
		         },
		         extraParams: 
		         {
		              action: 'getEstructuraArbol',
		              select:select,
		              buscaGaveta:true
		          }
	        },
	        listeners:{
	        	load:function(_this,e){
	        		if(_this.getRootNode().getChildAt(0)!=null){
	        			checkBoxBuscarEstructura.setDisabled(false);
	        			fieldSet4.setDisabled(!checkBoxBuscarEstructura.getValue());
	        		}
	        		else{
	        			checkBoxBuscarEstructura.setValue(false);
	        			checkBoxBuscarEstructura.setDisabled(true);
	        			fieldSet4.setDisabled(true);
	        			fieldSet4.collapse();
	        		}
	        	}
	        },
	        folderSort: true,
	        autoLoad:false	        
	    });
	 Ext.override(Ext.data.AbstractStore,{
		    indexOf: Ext.emptyFn
		}); 
	/*
	 * Peticiones independientes
	 */
		/*Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: successAjaxFnN,   
					timeout: 30000,  
					params: {  
					action: 'actualizaEstructura',  				
					estruc:exportM,
					select:txtNombre.getValue()
			}});*/
	/*
	 * Objetos
	 */
		
		//Objetos Filtro General
		var txtFechaInicioCreacion=generadorExtjs.getDatePicker('txtFechaInicioCreacion','Creado',150,50,null,null);
		var txtFechaFinCreacion=generadorExtjs.getDatePicker('txtFechaFinCreacion','a',120,20,null,null);
		var txtFechaInicioModificacion=generadorExtjs.getDatePicker('txtFechaInicioModificacion','Modificado',160,60,'5 0 0 25',null);
		var txtFechaFinModificacion=generadorExtjs.getDatePicker('txtFechaFinModificacion','a',120,20,null,null);
		var txtFechaInicioOk=generadorExtjs.getDatePicker('txtFechaInicioOk','Fecha OK',150,50,null,null);
		var txtFechaFinOk=generadorExtjs.getDatePicker('txtFechaFinOk','a',120,20,null,null);
		var comboStatus=generadorExtjs.getComboBox('comboStatus','Estado',200,60,'5 0 0 25','Selecciona...','nombre','status',null,storeStatus);
		var checkBoxBuscarGeneral=generadorExtjs.getCheckBox('checkBoxBuscarGeneral','Filtrar por valores generales','10 0 0 25',null);
		    checkBoxBuscarGeneral.addListener('change',function(_this, newValue, oldValue, eOpts){
		    	if(newValue==true){
		    		fieldSet1.setDisabled(false);
		    		fieldSet1.expand();
		    	}
		    	else{
		    		fieldSet1.setDisabled(true);
		    		fieldSet1.collapse();
		    	}
		    });	
		
		//Objetos panel campos.
		var checkBoxBuscarCampos=generadorExtjs.getCheckBox('checkBoxBuscarCampos','Filtrar por campos de la gaveta','10 0 0 25',null);
		    checkBoxBuscarCampos.addListener('change',function(_this, newValue, oldValue, eOpts){
		    	if(newValue==true){
		    		fieldSet2.setDisabled(false);
		    		fieldSet2.expand();
		    	}
		    	else{
		    		fieldSet2.setDisabled(true);
		    		fieldSet2.collapse();
		    	}
		    });
		//Objetos panel plantilla
		    var checkBoxBuscarPlantilla=generadorExtjs.getCheckBox('checkBoxBuscarPlantilla','Filtrar por plantilla de documento','10 0 0 25',null);
		    checkBoxBuscarPlantilla.addListener('change',function(_this, newValue, oldValue, eOpts){
		    	if(newValue==true){
		    		fieldSet3.setDisabled(false);
		    		fieldSet3.expand();
		    	}
		    	else{
		    		fieldSet3.setDisabled(true);
		    		fieldSet3.collapse();
		    	}
		    });
		    var cmbPlantillas=Ext.create('Ext.form.ComboBox', {
		 		id:'cmbPlantillas',
			 	name:'cmbPlantillas',
		    	fieldLabel: 'Plantilla',
			    store: storePlantillas,
			    emptyText:'-Ninguna-',
			    displayField: 'Nombre',
			    editable: false,
			    width:250,
			    labelWidth:80,
			    labelPad:35,
			    valueField: 'Id',
			    value:-1,
			    margin:'10 0 0 20',
			    listeners:{
			    	change:function( cmb, newValue, oldValue, eOpts ){
			    		if(newValue!=-1){
			    			fieldSetPlantilla.removeAll();
			    			storeCamposPlantilla.load({
			  				  params: {
			  					  action:'getTDoc',
								  select: newValue
							  }
							});
			    		}
			    		else{
			    			
			    			fieldSetPlantilla.setVisible(false);
			    			fieldSetPlantilla.setDisabled(true);
			    			fieldSetPlantilla.removeAll();
			    		}
			    	}
			   }
		});
		
		//Objetos Atributos
		var checkBoxBuscarAtributos=generadorExtjs.getCheckBox('checkBoxBuscarAtributos','Filtrar por atributos','10 0 0 25',null);
		    checkBoxBuscarAtributos.addListener('change',function(_this, newValue, oldValue, eOpts){
		    	if(newValue==true){
		    		fieldSet5.setDisabled(false);
		    		fieldSet5.expand();
		    	}
		    	else{
		    		fieldSet5.setDisabled(true);
		    		fieldSet5.collapse();
		    	}
		    });
		var proAtributos=Ext.create('Ext.grid.property.Grid', {
			cls:'proAtributos',
			disabled:false,
		    source: {
		        "Requerido": false,
		        "Vigencia":'',
		        "Vencimiento": Ext.Date.parse('01/01/2013', 'd/m/Y'),
		        "Historico": false,
		        "Existencia fisica": ''
		    }});
		    
		    //Objetos Ventana Gavetas
		    var cmbGavetas=new Ext.form.ComboBox({
				id:'cmbGavetas',
				store:storeGavetas,
				fieldLabel:'Seleccionar gaveta',
				displayField:'Nombre',
				valueField:'Nombre',
				width:320,
				labelWidth:110,
				margin:'10 10 10 25',
				allowBlank:false,			
				queryMode: 'local',
				multiSelect: false,
				//forceSelection:true,
				listConfig: {
	        	getInnerTpl: function() {
			            return '<div ><font class="comboGavetas">{Descripcion}</font><p class="descripcion">{Nombre}</p></div>';
			        }
			    },
	    		listeners:{
	    			change:function( cmb, newValue, oldValue, eOpts ){
	    				var valor=newValue;//records[0].data.Nombre;
	    				if(newValue!='Todas'){
	    					checkBoxBuscarEstructura.setDisabled(false);
	    					checkBoxBuscarCampos.setDisabled(false);
	    					fieldSet2.setDisabled(!checkBoxBuscarCampos.getValue());
	    					fieldSet4.setDisabled(!checkBoxBuscarEstructura.getValue());
	    					select=newValue;
	    					selectValido=true;
	    					storeCampos.load({params:{action:'getgaveta', select: select}});
	    					 var delNode;
						        while (delNode = estructuraStore.getRootNode().childNodes[0]) {
						            estructuraStore.getRootNode().removeChild(delNode);
						        }
	    					estructuraStore.load({params:{action:'getEstructuraArbol', select: select,buscaGaveta:true}});
	    					//estructuraStore.load();
	    				}
	    				else{
	    					checkBoxBuscarEstructura.setDisabled(true);
	    					checkBoxBuscarCampos.setDisabled(true);
	    					fieldSet2.setDisabled(true);
	    					fieldSet2.collapse();
	    					fieldSet4.setDisabled(true);
	    					fieldSet4.collapse();
	    				}
	    			},
	    			afterrender:function(_this,e){
	    				if(selectValido){
	    					cmbGavetas.setValue(select);
	    				}
	    			}
	    		}
		});
		
		//Objetos tbar filtros
		var btnGenerarReporte=generadorExtjs.getButton('btnGenerarReporte','Generar reporte',110,'0 0 0 0','btnGenerarReporteIconCls',null,false);
		btnGenerarReporte.setHandler(function(_this,e){
			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			if(cmbGavetas.getValue()!=""&&cmbGavetas.getValue()!=null)
				generaReporte();
			else
				Ext.getBody().unmask();
		});
		//Objetos estructua
		
		 var checkBoxBuscarEstructura=generadorExtjs.getCheckBox('checkBoxBuscarEstructura','Filtrar tipo de documento','10 0 0 25',null);
		    checkBoxBuscarEstructura.addListener('change',function(_this, newValue, oldValue, eOpts){
		    	if(newValue==true){
		    		fieldSet4.setDisabled(false);
		    		fieldSet4.expand();
		    	}
		    	else{
		    		fieldSet4.setDisabled(true);
		    		fieldSet4.collapse();
		    	}
		    });

		    //Objetos campos documento
		 var checkBoxBuscarDocumento=generadorExtjs.getCheckBox('checkBoxBuscarDocumento','Filtrar por datos de documento','10 0 0 25',null);
		    checkBoxBuscarDocumento.addListener('change',function(_this, newValue, oldValue, eOpts){
		    	if(newValue==true){
		    		fieldSet6.setDisabled(false);
		    		fieldSet6.expand();
		    	}
		    	else{
		    		fieldSet6.setDisabled(true);
		    		fieldSet6.collapse();
		    	}
		    });
		var txtNombredocumento=generadorExtjs.getTextBox('txtNombredocumento','Nombre',270,120,'Nombre',null);
		var textDescripcion=generadorExtjs.getTextArea('textDescripcion','Descripcion',350,40,140,'Descripcion',null);
		
		var txtFechaInicioCreacionDocumento=generadorExtjs.getDatePicker('txtFechaInicioCreacionDocumento','Creado',150,50,null,null);
		var txtFechaFinCreacionDocumento=generadorExtjs.getDatePicker('txtFechaFinCreacionDocumento','a',120,20,null,null);
		var txtFechaInicioModificacionDocumento=generadorExtjs.getDatePicker('txtFechaInicioModificacionDocumento','Modificado',160,60,'5 0 0 25',null);
		var txtFechaFinModificacionDocumento=generadorExtjs.getDatePicker('txtFechaFinModificacionDocumento','a',120,20,null,null);
	/*
	 * Paneles
	 */

	var fieldSetPlantilla=new Ext.form.FieldSet({
		id:'fieldSetPlantilla',
		cls:'fieldSetPlantillaCls',
		title:'Campos plantilla',
		disabled:true,
		hidden:true
	});
	var checkBoxBuscar = Ext.create('Ext.selection.CheckboxModel',
	{name:'checkBoxBuscar',id:'checkBoxBuscar',mode:'SINGLE',
	checkOnly:true,enableKeyNav:true,
	listeners:{
		beforeselect:function( _this, record, index, eOpts ){
			if(record.data.tipo!='documento')
				return false;
		}
	}
	});
	var panelArbolEstructura=new Ext.tree.Panel({
		id:'panelArbolEstructura',
		name:'panelArbolEstructura',
		cls:'panelArbolEstructuraCls',
		collapsible: false, 
	    useArrows: true, 	    
	    title:'Estructura de la gaveta',
	    store: estructuraStore,
	    selModel:checkBoxBuscar,
	    multiSelect: false, 
	    hideHeaders:true,
	    columns: [
	    	{xtype: 'treecolumn',flex: 2,sortable: true,dataIndex: 'texto'
	    }],
	    rootVisible: false
	});
	
	var fieldSet0=new Ext.form.FieldSet({
		id:'fieldSet0',
		cls:'fieldSetsFiltros',
		title:'Selecciona gaveta',
		disabled:false,
		collapsible:false,
		collapsed:false,
		items:[cmbGavetas]
	});
	var fieldSet1=new Ext.form.FieldSet({
		id:'fieldSet1',
		cls:'fieldSetsFiltros',
		title:'Filtro general',
		disabled:true,
		collapsible:true,
		collapsed:true,
		items:[
		{xtype:'fieldset',id:'fieldset1.1',layout:'hbox',cls:'fieldSetOcultos',items:[txtFechaInicioCreacion,txtFechaFinCreacion,txtFechaInicioModificacion,txtFechaFinModificacion]},
		{xtype:'fieldset',id:'fieldset1.2',layout:'hbox',cls:'fieldSetOcultos',items:[txtFechaInicioOk,txtFechaFinOk,comboStatus]}
		]
	});
	var fieldSet2=new Ext.form.FieldSet({
		id:'fieldSet2',
		cls:'fieldSetsFiltros',
		title:'Campos gaveta',
		collapsible:true,
		collapsed:true,
		disabled:true,
		items:[]
	});
	var fieldSet3=new Ext.form.FieldSet({
		id:'fieldSet3',
		cls:'fieldSetsFiltros',
		title:'Plantilla documento',
		collapsible:true,
		collapsed:true,
		disabled:true,
		items:[cmbPlantillas,fieldSetPlantilla]
	});
	var fieldSet4=new Ext.form.FieldSet({
		id:'fieldSet4',
		cls:'fieldSetsFiltros',
		title:'Estructura',
		collapsible:true,
		collapsed:true,
		disabled:true,
		items:[panelArbolEstructura]
	});
	var fieldSet5=new Ext.form.FieldSet({
		id:'fieldSet5',
		cls:'fieldSetsFiltros',
		title:'Atributos de documento',
		collapsible:true,
		collapsed:true,
		disabled:true,
		items:[proAtributos]
	});
	var fieldSet6=new Ext.form.FieldSet({
		id:'fieldSet6',
		cls:'fieldSetsFiltros',
		title:'Datos de documento',
		collapsible:true,
		collapsed:true,
		disabled:true,
		items:[
			{xtype:'fieldset',id:'fieldset6.1',layout:'hbox',cls:'fieldSetOcultos',items:[txtNombredocumento,textDescripcion]},
			{xtype:'fieldset',id:'fieldset6.2',layout:'hbox',cls:'fieldSetOcultos',items:[txtFechaInicioCreacionDocumento,txtFechaFinCreacionDocumento,txtFechaInicioModificacionDocumento,txtFechaFinModificacionDocumento]}
			]
	});
	var panelFiltros=new Ext.panel.Panel({
		id:'panelFiltros',
		name:'panelFiltros',
		layout:'anchor',
		title:'Filtros',
		collapsible:true,
		autoScroll:true,
		region:'north',
		tbar:['->',btnGenerarReporte],
		items:[
		fieldSet0,checkBoxBuscarGeneral,fieldSet1,checkBoxBuscarCampos,fieldSet2,checkBoxBuscarPlantilla,fieldSet3,checkBoxBuscarEstructura,fieldSet4,
		checkBoxBuscarAtributos,fieldSet5,checkBoxBuscarDocumento,fieldSet6
		/*{xtype:'fieldset',id:'fieldset0',layout:'hbox',cls:'fieldSetOcultos',items:[txtNombredocumento,textDescripcion]},
		{xtype:'fieldset',id:'fieldset1',layout:'hbox',cls:'fieldSetOcultos',items:[txtFechaInicioCreacion,txtFechaFinCreacion,txtFechaInicioModificacion,txtFechaFinModificacion,comboStatus]}
		*/
		]
	});
	

	var panelReporte=new Ext.tab.Panel({
		id:'panelReporte',
		name:'panelReporte',
		title:'Reporte',
		region:'center',
		items:[panelFiltros]
	});
	
	var viewPort=new Ext.container.Viewport({
		id:'viewPort',
		name:'viewPort',
		layout:'border',
		items:[panelReporte]
	});
	/*
	 * Funciones
	 */
	
	function creaFormularioCampos(){
		reiniciaForm();
		fieldSet2.removeAll();
		for(var i=0;i<storeCampos.getCount();i++){
				creaFormulario(storeCampos.getAt(i).get('tipo'),storeCampos.getAt(i).get('nombre'),storeCampos.getAt(i).get('etiqueta'),storeCampos.getAt(i).get('longitud'),storeCampos.getAt(i).get('valor'),storeCampos.getAt(i).get('requerido'),storeCampos.getAt(i).get('editable'),storeCampos.getAt(i).get('lista'),fieldSet2,pantalla.width,rutaServletPlantilla);
		}
		if(checkBoxBuscarCampos.getValue())
			fieldSet2.setDisabled(false);
	}
	function creaFormularioPlantilla(){
		reiniciaForm();
		fieldSetPlantilla.removeAll();
		for(var i=0;i<storeCamposPlantilla.getCount();i++){
				creaFormulario(storeCamposPlantilla.getAt(i).get('tipo'),storeCamposPlantilla.getAt(i).get('nombre'),storeCamposPlantilla.getAt(i).get('etiqueta'),storeCamposPlantilla.getAt(i).get('longitud'),storeCamposPlantilla.getAt(i).get('valor'),storeCamposPlantilla.getAt(i).get('requerido'),storeCamposPlantilla.getAt(i).get('editable'),storeCamposPlantilla.getAt(i).get('lista'),fieldSetPlantilla,pantalla.width,rutaServletPlantilla);
		}
		fieldSetPlantilla.setDisabled(false);
		fieldSetPlantilla.setVisible(true);
	}
	function generaReporte(){
		var tipoDocumento='-1';
		if(panelArbolEstructura.getSelectionModel().getSelection().length>0)
			tipoDocumento=panelArbolEstructura.getSelectionModel().getSelection()[0].data.ide;		
		
		var datos = {
			gaveta:cmbGavetas.getValue(),
			filtroGenerales:checkBoxBuscarGeneral.getValue(),
			fechaInicioCreado:txtFechaInicioCreacion.getValue(),
			fechaFinCreado:txtFechaFinCreacion.getValue(),
			fechaInicioModificado:txtFechaInicioModificacion.getValue(),
			fechaFinModificado:txtFechaFinModificacion.getValue(),
			fechaInicioOk:txtFechaInicioOk.getValue(),
            fechaFinOk:txtFechaFinOk.getValue(),
			estado:comboStatus.getValue(),
			filtroCamposGaveta:checkBoxBuscarCampos.getValue(),
			camposGaveta:obtenerDatosCamposGaveta(),
			filtroCamposPlantilla:checkBoxBuscarPlantilla.getValue(),
			plantilla:cmbPlantillas.getValue(),
			camposPlantilla:obtenerDatosCamposPlantilla(),
			filtroTipoDocumento:checkBoxBuscarEstructura.getValue(),
			nodoTipoDocumento:tipoDocumento,
			filtroAtributos:checkBoxBuscarAtributos.getValue(),
			atributos:obtenerDatosAtributos(),
			filtroDatosDocumento:checkBoxBuscarDocumento.getValue(),
			nombreDocumento:txtNombredocumento.getValue(),
            descripcionDocumento:textDescripcion.getValue(),
            fechaInicioCreadoDocumento:txtFechaInicioCreacionDocumento.getValue(),
            fechaFinCreadoDocumento:txtFechaFinCreacionDocumento.getValue(),
            fechaInicioModificadoDocumento:txtFechaInicioModificacionDocumento.getValue(),
            fechaFinModificadoDocumento:txtFechaFinModificacionDocumento.getValue()		
		};
		Ext.Ajax.request({  
			url: rutaServletReporte,  
			method: 'POST',  
			success: successAjaxFnN,   
			timeout: 30000,  
				params: {  
					action: "getReport",
					select: Ext.JSON.encode(datos)
		}});
	}
	var successAjaxFnN = function(response, request) {
	        var jsonData = Ext.JSON.decode(response.responseText);
	        if (true == jsonData.success) {
	            panelReporte.add({
            	title: 'Resultado',
				items:[	{
						region: 'center',
						xtype: 'panel',
						autoScroll: false,
						html: 
							  '<div style="height:100%; scrolling="yes" overflow:scroll !important;">' +
							  '<iframe style="width:100%;height:100%;border:none;"' +
							  		' src="'+basePath+'/ReporteServlet?action=getReporteDocumentos&reporteDocumentos='+jsonData.reporteDocumentos+'">' +
							  '</iframe>' +
							  '</div>'  						
						}	
				],
				layout: 'fit',
            	closable:true
        	}).show();
	        } else {
	            Ext.Msg.show({
	                title: 'Error',
	                msg: jsonData.message,
	                buttons: Ext.Msg.OK,
	                animEl: 'elId',
	                icon: Ext.MessageBox.ERROR
	            });
	        }
	        Ext.getBody().unmask();
	}
	function obtenerDatosAtributos(){
		var datosAtributos = {};
		for(var i=0;i<proAtributos.getStore().getCount();i++){
			datosAtributos[proAtributos.getStore().getAt(i).get('name')] = proAtributos.getStore().getAt(i).get('value');
		}
		
		return datosAtributos;
	}
	function obtenerDatosCamposGaveta(){
		var f=Ext.getCmp('fieldSet2');
		var children = f.items ? f.items.items : [];
		var camposGaveta = {};
		Ext.each(children, function (child) {
			camposGaveta[child.getName().toString()] = child.getValue();
		});
		return camposGaveta;
	}
	function obtenerDatosCamposPlantilla(){
		var f=Ext.getCmp('fieldSetPlantilla');
		var children = f.items ? f.items.items : [];
		var camposPlantilla = {};
		Ext.each(children,function(child){
			camposPlantilla[child.getName().toString()] = child.getValue();
		});
		return camposPlantilla;
	}
	//Verifica inicio de interfaz
/*	if(!selectValido){
		panelFiltros.setDisabled(true);
		panelFiltrosAvanzados.setDisabled(true);
		panelReporte.setDisabled(true);
		windowGavetas.show();
	}*/
});