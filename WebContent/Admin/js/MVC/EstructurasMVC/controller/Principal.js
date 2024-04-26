Ext.define('FMX.controller.Principal', {
    extend: 'Ext.app.Controller',
	
    statics: {
        idCarpeta: 0,
        contadorGral: 1,
        resetIds: function() {
        	this.idCarpeta = 0;
        },
        getNextIdCarpeta: function() {
            return this.idCarpeta++;
        },
        getNextContadorGral: function() {
            return this.contadorGral++;
        },
        storeAtributosDefault: null,
        objetoAtributos: null,
        atributoSeleccionadoGrid:null
    },
	
    stores:[
//        	'store',
	],
        	
    models: [
//             'model',
    ],
        	
	views:[
	       'ContainerPrincipal'
    ],

    init: function() {
    	this.control({
    		'containerprincipal':{
    			render: this.onRenderContainerPrincipal
    		},
    		'containerprincipal button[accion=GuardarCambiosEstructura]':{
    			click: this.onClickContainerPrincipalButtonGuardar
    		},
    		'containerprincipal button[accion=CargarEstructura]':{
    			click: this.onClickContainerPrincipalButtonCargar
    		},
    		'treepanel':{
    			render: this.onRenderTreePanel,
    			beforeitemclick: this.onBeforeItemClickTreepanel,
    			itemcontextmenu: this.onItemContextMenuTreepanel,
    			select: this.onSelectTreePanelRecord
    		},
    		'treepanel button[accion=ExportarEstructura]':{
    			click: this.onClickTreePanelButtonExportar
    		},
    		'treepanel button[accion=ImportarEstructura]':{
    			click: this.onClickTreePanelButtonImportar
    		},
    		'panel[accion=PanelAtributos]':{
    			render: this.onRenderPanelAtributos
    		},
    		'propertygrid':{
    			render: this.onRenderPropertyGrid,
    			propertychange: this.onPropertyChange,
    			select: this.onSelectPropertyGridRecord
    		},
    		'checkbox[accion=AtributoActivo]':{
    			change: this.onChangeFieldMetaDatoAtributo
    		},
    		'checkbox[accion=AtributoModificable]':{
    			change: this.onChangeFieldMetaDatoAtributo
    		}
    	});	
    },
    
    onRenderContainerPrincipal: function(){
    	var store = Ext.create('Ext.data.Store', { //TODO: Store a cargar del servidor
    		fields:['id_atributo', 'etiqueta_atributo', 'tipo_atributo', 'valor_default', 'activo', 'modificable', 'descripcion'],
    		data: [{id_atributo: 1, etiqueta_atributo:'Requerido', 			tipo_atributo:'Boolean', valor_default:false,	 		activo:false, modificable:false, descripcion:'El documento NO debe estar vacio, para ser válido'}, 
    		       {id_atributo: 2, etiqueta_atributo:'Historico', 			tipo_atributo:'Boolean', valor_default:false, 			activo:false, modificable:false, descripcion:'El documento genera copias en cada modificacion del documento'},
    		       {id_atributo: 3, etiqueta_atributo:'Vigencia', 			tipo_atributo:'Integer', valor_default:10, 				activo:false, modificable:false, descripcion:'Cantidad de dias en que el documento es válido a partir de su alta'},
    		       {id_atributo: 4, etiqueta_atributo:'Vencimiento', 		tipo_atributo:'Date', 	 valor_default:new Date(), 		activo:false, modificable:false, descripcion:'Fecha especifica en la que el documento deja de ser válido'},
    		       {id_atributo: 5, etiqueta_atributo:'Existencia Física', 	tipo_atributo:'String',  valor_default:'Por definir',	activo:false, modificable:false, descripcion:'Es donde se encuentra fisicamente el documento ( Edificio, archivo, piso, gaveta, seccion, etc. )'}
    		       ]
    	}); //TODO SACAR
    	this.statics().storeAtributosDefault = store;
    },
    
    onRenderTreePanel: function(treepanel){
    	//IMPORTANTE: Necesario para que funcione la edicion de celdas en treepanel
    	Ext.override(Ext.data.AbstractStore,{
    		indexOf: Ext.emptyFn
    	});

    	// Cargar arbol
    	if(actualizar)
    		this.cargarArbol(treepanel);
    },
    
    cargarArbol : function(treepanel) {
    	treepanel.getRootNode().removeAll();
    	var nombre = treepanel.up('panel[accion=PanelGlobal]').down('textfield').getValue();

    	Ext.Ajax.request({
    		url: operacionesServlet,
    		params: {
    			action: "getEstructuraMVC",
    			nombre: nombre
    		},
    		success: function(response){
    			var respuesta = Ext.decode(response.responseText);
//    			var json = Ext.JSON.encode(text);
    			if(respuesta.success) {
    				treepanel.getStore().setRootNode(respuesta.definicion);
    				treepanel.getStore().getRootNode().expand(true);
    				treepanel.up('panel[accion=PanelGlobal]').down('textfield').setValue(respuesta.nombre);
    				treepanel.up('panel[accion=PanelGlobal]').down('textarea').setValue(respuesta.descripcion);
    			}
    		}
    	});
    },
    
    onClickContainerPrincipalButtonGuardar: function(button) {
        var root = button.up('panel[accion=PanelGlobal]').down('treepanel').getStore().getRootNode();
        if(!root.hasChildNodes()){
        	Ext.Msg.show({
				title: 'Advertencia',
				msg: 'Añada hijos a la raíz del arbol',
				buttons: Ext.Msg.OK,
				icon: Ext.MessageBox.WARNING
			});
        	return;
        }
        
        this.statics().resetIds();
        var arbol = this.generaArbol(root);
       
        if(!button.up('panel').down('textfield').validate())
        	return;
        var nombre = button.up('panel').down('textfield').getValue();
        var descripcion = button.up('panel').down('textarea').getValue();
        
		Ext.Ajax.request({
		    url: operacionesServlet,
		    params: {
		    	action: "creaEstructuraMVC",
		    	nombre: nombre,
		    	descripcion: descripcion,
		    	definicion: Ext.JSON.encode(arbol)
		    },
		    success: function(response){
		    	var respuesta = Ext.decode(response.responseText);
    			Ext.Msg.show({
    				title: 'Información',
    				msg: respuesta.message,
    				buttons: Ext.Msg.OK,
    				icon: Ext.MessageBox.INFO
    			});
		    }
		});
    },
    
    generaArbol: function(nodo){
    	var me = this;
    	var id;
    	
    	if(nodo.data.leaf)
    		id = nodo.parentNode.customId + 'D' + (nodo.parentNode.contadorHijos++); 
    	else{
    		nodo.contadorHijos = 1;
    		id = 'C' + me.statics().getNextIdCarpeta();
    		nodo.customId = id; //para que ids de doc antepongan el de su padre
    	}
    	//crea nodo tipo NodoModel
    	var json = {
    			id: id,
    			text: nodo.data.text,
    			type: nodo.data.type,
    			expanded: nodo.data.expanded,
    			leaf: nodo.data.leaf,
    			atributos: nodo.data.atributos,
    			children: []
    	};
    	//añade hijos
    	if(nodo.hasChildNodes()){
    		nodo.eachChild(function(child){
    			json.children.push(me.generaArbol(child));
    		});
    	}
    	
        return json;
    },
    
    onBeforeItemClickTreepanel: function(treeview, record, item, index, e, eOpts){
    	var panelAtributos = treeview.up('panel[accion=PanelGlobal]').up('container').down('panel[accion=PanelAtributos]');
    	if(record.isLeaf()){
    		panelAtributos.enable();
    	} else {
    		panelAtributos.disable();
    	}
    },
    
    onItemContextMenuTreepanel: function( treepanel, record, item, index, e, eOpts ){
    	e.stopEvent();
    	this.mostrarMenuContextual(record, e);
    },
    
    onSelectTreePanelRecord: function( rowModel, record, index, eOpts ){
    	var grid = Ext.ComponentQuery.query('propertygrid[accion=PropertyGridAtributos]')[0];
    	//Se requiere esta referencia para funcionar dentro de forEach(function(){})
    	var referenciaStore = this.statics().storeAtributosDefault;
    	if(record.data.leaf){
   		Ext.ComponentQuery.query('panel[accion=PanelAtributos]')[0].setTitle('Atributos de: ' + record.data.text);
    		if(record.data.atributos && record.data.atributos.length != 0){
    			for(var i = 0; i < record.data.atributos.length; i++){
    				var atributo = record.data.atributos[i];
    				var etiqueta_atributo = referenciaStore.findRecord('id_atributo', atributo.id_atributo).get('etiqueta_atributo');
    				var tipo_atributo = referenciaStore.findRecord('id_atributo', atributo.id_atributo).get('tipo_atributo');
    				var valor_atributo = atributo.valor_default;
    				if(tipo_atributo=='Date'){
    					valor_atributo = Ext.Date.parse(atributo.valor_default, 'Y-m-d H:i:s');//Recibe string. Muestra Date
    				}
    				grid.setProperty(etiqueta_atributo, valor_atributo, true);
    			}
    		} else {
    			this.cargaAGridAtributosDefault();
    		}
    	}
    },
    
    onClickTreePanelButtonExportar: function(button) {
        var root = button.up('treepanel').getStore().getRootNode();
        this.statics().resetIds();
        var arbol = this.generaArbol(root);
        var nombre = button.up('panel[accion=PanelGlobal]').down('textfield').getValue();
        
        var form = Ext.create('Ext.form.Panel',
    			{ 									
					standardSubmit: true, 
            		url: operacionesServlet
        		}
        );
		form.submit({
			params: {
				action: "exportarJson",
		    	nombre_archivo: nombre,
		    	json: Ext.JSON.encode(arbol)	
			}
		});
    },

    onClickTreePanelButtonImportar: function(button) {
    	Ext.create('Ext.window.Window', {
    		title: 'Importar estructura',
    		height: 100,
    		width: 400,
    		layout: 'fit',
    		closeAction: 'destroy',
    		items: {
    			xtype: 'form',
    			frame: false,
    			fileUpload: false,
    		    standardSubmit: false,
    			items:[{
    				xtype: 'filefield',
    				fieldLabel: 'Estructura',
    				allowBlank: false,
    				labelWidth: 60,
    				anchor: '100%',
    				margin: '5 5 0 5',
    				buttonText: 'Seleccionar...'
    			}],
    			buttons: [{
    		        text: 'Importar',
    		        handler: function() {
    		        	var botonImportar = this;
    		            var form = botonImportar.up('form').getForm();
    		            if(form.isValid()){
    		            	form.submit({
    		            		url: operacionesServlet+'?action=importarJsonEstructura',
//  		            		waitMsg: 'Cargando...',
    		            		success: function(form, action) {
    		            			var respuesta = Ext.decode(action.response.responseText);
    		            			var estructura = respuesta.estructura;
    		            			var treepanel = button.up('treepanel');
    		            			treepanel.getStore().setRootNode(estructura);
    		            			treepanel.getStore().getRootNode().expand(true);   
    		            			Ext.Msg.show({
    		            				title: 'Importación exitosa',
    		            				msg: respuesta.message,
    		            				buttons: Ext.Msg.OK,
    		            				icon: Ext.MessageBox.INFO
    		            			});
    		            			botonImportar.up('window').close();
    		            		},
    		            		failure: function(form, action){
    		            			var respuesta = Ext.decode(action.response.responseText);
    		            			Ext.Msg.show({
    		            				title: 'Error al importar',
    		            				msg: respuesta.message,
    		            				buttons: Ext.Msg.OK,
    		            				icon: Ext.MessageBox.ERROR
    		            			});
    		            		}
    		            	});
    		            }
    		        }
    		    }]
    		}
    	}).show();
    },
    
    mostrarMenuContextual: function(record, e){
    	var me = this;
    	var menu = Ext.create('Ext.menu.Menu',{
    		items: [
    		        {text: 'Nueva carpeta', iconCls:'addC', handler: function(){
    		        	me.agregarHijo(record, 'folder');
    		        }},
    		        {text: 'Nuevo documento', iconCls:'addD', handler: function(){
    		        	me.agregarHijo(record, 'document');
    		        }},
    		        {text: 'Eliminar elemento', iconCls:'del', handler: function(){
    		        	if(record.parentNode)
    		        		record.parentNode.removeChild(record);
    		        }}
    		        ]
    	});
    	menu.showAt(e.getXY());
    },
    
    agregarHijo: function(recordPadre, tipoHijo){
    	var me = this;
    	if(recordPadre.isLeaf())
    		recordPadre = recordPadre.parentNode;
    	var hijo;

    	if(tipoHijo=='document'){
    		hijo = {
    				text: 'Documento' + me.statics().getNextContadorGral(),
    				leaf: true,
    				expanded: false,
    				type: tipoHijo,
    				atributos: []
    		};    		
    	} else {
    		hijo = {
    				text: 'Carpeta' + me.statics().getNextContadorGral(),
    				leaf: false,
    				expanded: true,
    				type: tipoHijo,
    				atributos: [],
    				children: []
    		};   
    	}
    	recordPadre.appendChild(hijo);
    },
    
    onRenderPanelAtributos: function(){
    	//TODO: Revisar ya que quiza en la carga desde el servidor aun no este listo este store
    	var obj = [];
    	this.statics().storeAtributosDefault.each(function(record){
    		obj.push({
    			'id_atributo':record.data.id_atributo, 
    			'valor_default':record.data.valor_default, 
    			'activo':record.data.activo, 
    			'modificable':record.data.modificable
    		});
    	});
    	this.statics().objetoAtributos = obj;
    },
    
    onRenderPropertyGrid: function(){
    	//TODO: Revisar ya que quiza en la carga desde el servidor aun no este listo este store
    	this.cargaAGridAtributosDefault();
    },
    
    cargaAGridAtributosDefault: function(){
    	var grid = Ext.ComponentQuery.query('propertygrid[accion=PropertyGridAtributos]')[0];
    	this.statics().storeAtributosDefault.each(function(record){
    		grid.setProperty(record.data.etiqueta_atributo, record.data.valor_default, true);
    	});
    },
    
    onPropertyChange: function( source, recordId, value, oldValue, eOpts ){
    	var treepanel = Ext.ComponentQuery.query('treepanel[accion=ArbolEstructura]')[0];
    	var nodo = treepanel.getSelectionModel().getSelection()[0];
    	if(nodo.data.leaf){
    		if(!nodo.data.atributos || nodo.data.atributos.length == 0)
    			nodo.data.atributos = Ext.clone(this.statics().objetoAtributos);

    		var idUpdate = this.statics().storeAtributosDefault.findRecord('etiqueta_atributo', recordId).get('id_atributo');
    		for(var i = 0; i < nodo.data.atributos.length; i++){
				var atributo = nodo.data.atributos[i];
				if(atributo.id_atributo == idUpdate){
    				if(Ext.typeOf(value)=='date'){
    					value = Ext.Date.format(value, 'Y-m-d H:i:s');//Envia string
    				}
    				atributo.valor_default = value;
    			}
			}
    	}
    },
    
    onSelectPropertyGridRecord: function(_this, record){
    	var me = this;
    	me.statics().atributoSeleccionadoGrid = record.data;
    	var fieldset = Ext.ComponentQuery.query('fieldset[accion=FieldSetMetadatosAtributo]')[0];
    	fieldset.setTitle(record.data.name);
    	var chActivo = Ext.ComponentQuery.query('checkbox[accion=AtributoActivo]')[0];
    	var chModificable = Ext.ComponentQuery.query('checkbox[accion=AtributoModificable]')[0];
    	var txtDescripcion = Ext.ComponentQuery.query('textarea[accion=DescripcionAtributo]')[0];
    	
    	var nodo = Ext.ComponentQuery.query('treepanel[accion=ArbolEstructura]')[0].getSelectionModel().getSelection()[0];
    	
    	if(nodo.data.leaf && nodo.data.atributos){
    		if(nodo.data.atributos.length == 0){
    			var valor_activo = me.statics().storeAtributosDefault.findRecord('etiqueta_atributo', record.data.name).get('activo');
    			var valor_modificable = me.statics().storeAtributosDefault.findRecord('etiqueta_atributo', record.data.name).get('modificable');
    			chActivo.setValue(valor_activo);
    			chModificable.setValue(valor_modificable);
    		} else {
    			var referenciaStore = me.statics().storeAtributosDefault;
    			for(var i = 0; i < nodo.data.atributos.length; i++){
    				var atributo = nodo.data.atributos[i];
    				var id_atributo = referenciaStore.findRecord('etiqueta_atributo', record.data.name).get('id_atributo');
    				if(atributo.id_atributo == id_atributo){
    					var valor_activo = atributo.activo;
    					var valor_modificable = atributo.modificable;
    					chActivo.setValue(valor_activo);
    	    			chModificable.setValue(valor_modificable);
    				}
    			}
    		}
    	}
    	var descripcion = me.statics().storeAtributosDefault.findRecord('etiqueta_atributo', record.data.name).get('descripcion');
    	txtDescripcion.setValue(descripcion);
    },
    
    onChangeFieldMetaDatoAtributo: function(field, newValue, oldValue, eOpts){
    	var me = this;
    	var referenciaStore = me.statics().storeAtributosDefault;
    	var nodo = Ext.ComponentQuery.query('treepanel[accion=ArbolEstructura]')[0].getSelectionModel().getSelection()[0];
    	var idUpdate = referenciaStore.findRecord('etiqueta_atributo', me.statics().atributoSeleccionadoGrid.name).get('id_atributo');
    	if(nodo.data.leaf && nodo.data.atributos){
    		if(nodo.data.atributos.length != 0){
    			for(var i = 0; i < nodo.data.atributos.length; i++){
    				var atributo = nodo.data.atributos[i];
    				if(atributo.id_atributo == idUpdate){
    					switch(field.name){
    					case 'activo': atributo.activo = field.value; break;
    					case 'modificable': atributo.modificable = field.value; break;
    					}
    				}
    			}
    		}
    	}
    }
});