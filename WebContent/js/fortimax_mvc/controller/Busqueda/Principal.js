Ext.define('FMX.controller.Busqueda.Principal', {
	extend: 'Ext.app.Controller',
	
	requires: [
		'FMX.utils.FuncionesGlobales'
	],

	stores: [
		'Busqueda.Campos',
		'Busqueda.DefinicionesColumnas',
		'Busqueda.CantidadRegistrosMostrados'
	],

	models: [
		'Utils.CampoDinamico',
		'Busqueda.DefinicionColumnas'
	],

	views: [
		'centro.Busqueda.PanelBusqueda',
		'centro.Busqueda.GridBusqueda',
		'centro.Busqueda.WindowBusqueda',
		'centro.Busqueda.WindowEdicion',
		'centro.Busqueda.TriggerFieldFiltro',
		'Utils.CampoDinamico'
	],

	init: function () {
		//console.log('init: Busqueda.Principal');
		
		this.control({
              //Aquí empiezan eventos relacionados con el PanelBusqueda
             'panelbusqueda': {
             	render: this.onRenderPanelBusqueda
             },
             //Aquí empiezan eventos relacionados con el gridbusqueda
             'panelbusqueda gridbusqueda': {
             	render: this.onRenderGridBusqueda,
             	select: this.arbolExpediente
             },
             'panelbusqueda gridbusqueda gridview': {
             	expandbody: this.arbolExpediente,
		        collapsebody: this.arbolGavetas
             },
             'panelbusqueda gridbusqueda triggerfieldfiltro': {
             	change: this.onChangeTriggerFieldFiltro
             },
             'panelbusqueda gridbusqueda button[accion="DescargarReporte"]': {
				click: this.onClickGridBusquedaButtonDescargarReporte           	
             },
             'panelbusqueda gridbusqueda combo[accion="CantidadRegistrosMostrados"]': {
             	change: this.onChangeGridBusquedaComboCantidadRegistrosMostrados
             },
             'panelbusqueda gridbusqueda button[accion="MostrarVentanaBusqueda"]': {
				click: this.onClickGridBusquedaButtonMostrarVentanaBusqueda            	
             },
             'panelbusqueda gridbusqueda button[accion="RecargarBusqueda"]': {
				click: this.onClickGridBusquedaButtonRecargarBusqueda            	
             },
             'panelbusqueda gridbusqueda button[accion="EditarFila"]': {
				click: this.onClickGridBusquedaButtonEditarFila            	
             },
             'panelbusqueda gridbusqueda button[accion="EliminarFila"]': {
				click: this.onClickGridBusquedaButtonEliminarFila            	
             },
             //Aquí empiezan los eventos de WindowBusquedaBusqueda
             'windowbusquedabusqueda': {
             	beforecollapse: this.disableWindowBusquedaBusquedaTriggerFieldFiltro,
             	beforeexpand: this.enableWindowBusquedaBusquedaTriggerFieldFiltro,
             	beforehide: this.disableWindowBusquedaBusquedaTriggerFieldFiltro,
             	beforeshow: this.enableWindowBusquedaBusquedaTriggerFieldFiltro,      	
      			minimize: FMX.utils.FuncionesGlobales.minimizeWindow,
      			render: this.onRenderWindowBusqueda
             },
             'windowbusquedabusqueda triggerfieldfiltro': {
             	change: this.onChangeTriggerFieldFiltro
             },
             'windowbusquedabusqueda multislider[accion="ControlOpacidad"]': {
				change: this.onChangeMultisliderControlOpacidad            	
             },
             'windowbusquedabusqueda button[accion="LimpiarCampos"]': {
				click: this.onClickWindowBusquedaBusquedaButtonLimpiarCampos            	
             },
             'windowbusquedabusqueda button[accion="CrearFila"]': {
				click: this.onClickWindowBusquedaBusquedaButtonCrearFila            	
             },
             'windowbusquedabusqueda button[accion="Buscar"]': {
				click: this.onClickWindowBusquedaBusquedaButtonBuscar          	
             },
             //Aquí empiezan los eventos de WindowEdicionBusqueda
             'windowedicionbusqueda': {
             	beforeclose: this.beforeCloseWindowEdicionBusqueda,
      			minimize: FMX.utils.FuncionesGlobales.minimizeWindow,
      			render: this.onRenderWindowBusqueda
             },
             'windowedicionbusqueda multislider[accion="ControlOpacidad"]': {
				change: this.onChangeMultisliderControlOpacidad            	
             },
             'windowedicionbusqueda button[accion="CancelarEdicion"]': {
				click: this.onClickWindowEdicionBusquedaButtonCancelarEdicion           	
             },
             'windowedicionbusqueda button[accion="GuardarEdicion"]': {
				click: this.onClickWindowEdicionBusquedaButtonGuardarEdicion           	
             }
        });
	},
    
/* Funciones */
	
	//Aqui hay funciones temporales que deben ser eliminadas:
	
	arbolExpediente:
		function( rowModelOrNode, record, index, eOpts  ) {
			var select = record.store.getProxy().extraParams.select;
			if(typeof(parent.frames.left) != 'undefined') {
				var location = basePath+'getexpedient?select='+select+'&id_gabinete='+record.data.ID_GABINETE+'&expedient=true';
				parent.frames.left.location = location;	
			}
	},
	
	arbolGavetas:
		function() {
			if(typeof(parent.frames.left) != 'undefined') {
				var location=basePath+'/jsp/ArbolExpediente.jsp?select=Gaveta&arbol.tipo=g';
				parent.frames.left.location = location;	
			}
	},
    
    //Aqui empiezan las funciones relacionadas con los Store
    onLoadBusquedaCampos:
    	function(BusquedaCampos, records, successful, eOpts ) {
    		var gridbusqueda = eOpts.parent;
    		gridbusqueda.down('button[accion="EditarFila"]').setDisabled(false);
    		var form = gridbusqueda.windowbusquedabusqueda.down('form')
    		
    		var camposDinamicos = FMX.view.Utils.CampoDinamico.getFromStore(gridbusqueda.campos);
    		form.add(camposDinamicos);
    },
    	
    // Generate a model dynamically, provide fields
    modelFactory:
		function (name, fields) {
    		return Ext.define(name, {
       	 		extend: 'Ext.data.Model',
        		fields: fields
    	})
	},
	
	createRegistrosStore:
		function (panelBusqueda,fields,pageSize) {
			var registrosStore =  new Ext.data.Store({
                        model: this.modelFactory('model-'+new Date().getTime(),fields),
                        pageSize:pageSize,
                        proxy: panelBusqueda.cargarFilas,
         				autoLoad:true
            });
            registrosStore.on(
				{
					beforeload: this.beforeLoadRegistrosStore,
					load: this.onLoadRegistrosStore,
					scope: this
				}
    		);
    		return registrosStore;  
	},
	
	javaTypeToExtJSType:
		function(type){
			switch (type) {
				case 'java.lang.Long':
				case 'long':
				case 'java.lang.Integer':
				case 'int': return 'int';
				case 'java.lang.Double':
				case 'double':
				case 'java.lang.Float':
				case 'float': return 'float';
				case 'java.lang.Boolean':
				case 'boolean': return 'boolean';
				default: return 'string'; 
  			}
	},
	
	recordsToGridConfig:
		function(records){
			var gridConfig = {
    			columns: [],
    			fields: [],
    			rowBodyTpl: ""
			}		
			for	(index = 0; index < records.length; index++) {
  				var name = records[index].data['name'];
  				var type = this.javaTypeToExtJSType(records[index].data['type']);
  				var label = records[index].data['label'];
  				var hidden = index>6;
  				
  				gridConfig.columns.push({
  					hidden: hidden,
  					align: "center",
  					dataIndex: name,
  					header: label,
  					flex: 1
  				});
  				gridConfig.fields.push({
  					name: name,
  					type: type
  				});
  				if(hidden) {
  					gridConfig.rowBodyTpl += "<p><font class='campoRow'>";
  					gridConfig.rowBodyTpl += label;
  					gridConfig.rowBodyTpl += ": </font><font class='parrafoRow'>{";
  					gridConfig.rowBodyTpl += name;
  					gridConfig.rowBodyTpl += "}</font></p>";
  				}
			}
			return gridConfig;
	},
	
    
    onLoadBusquedaDefinicionesColumnas:
    	function(BusquedaDefinicionesColumnas, records, successful, eOpts ) {
    		var gridConfig = this.recordsToGridConfig(records);
    		var registrosGaveta = this.createRegistrosStore(eOpts.parent,gridConfig.fields,25);
        	var gridbusqueda = this.getView('FMX.view.centro.Busqueda.GridBusqueda').create(
    			{
    				windowbusquedabusqueda : this.getView('FMX.view.centro.Busqueda.WindowBusqueda').create({
    					title : '[BUSQUEDA/CREACIÓN]'
    				}),
    				plugins: [{
						ptype: 'rowexpander',
						rowBodyTpl : [gridConfig.rowBodyTpl]
		     		}],
		     		store: registrosGaveta,
		     		columns : gridConfig.columns,
		     		
		     		dockedItems: [{
                    	xtype: 'pagingtoolbar',
                        store: registrosGaveta,
                        dock: 'bottom',
                		displayInfo: true
        			}]
    			}
        	);
        	registrosGaveta.parent = gridbusqueda;
        	gridbusqueda.windowbusquedabusqueda.parent = gridbusqueda;
        	eOpts.parent.add(gridbusqueda);
    },
    	
    beforeLoadRegistrosStore:
    	function ( registrosStore, operation, eOpts ) {
    		var gridbusqueda = registrosStore.parent;
    		var parametros = registrosStore.getProxy().extraParams
    		if(this.objectOnlyHasEmptyStrings(gridbusqueda.camposBusqueda))
    			parametros.jsonBusqueda = null;
    		else
				parametros.jsonBusqueda = Ext.JSON.encode(gridbusqueda.camposBusqueda);
			parametros.LiveSearch = gridbusqueda.liveSearch;
    },
    
   	objectOnlyHasEmptyStrings:
   		function (object){
            for(var key in object){
            	if(object[key]!='')
                	return false;
            }
            return true;
    },
    	
   	onLoadRegistrosStore:
    	function ( registrosStore, records, successful, eOpts ){
    		var gridbusqueda = registrosStore.parent;
			labelResultados = gridbusqueda.down('label[accion="MostrarFiltro"]')
			var parametros = registrosStore.getProxy().extraParams;
    		if(parametros.jsonBusqueda==null&&parametros.LiveSearch=="")
				labelResultados.setText('<b>Todos</b>',false);
			else if(parametros.jsonBusqueda!=null&&parametros.LiveSearch!="")
				labelResultados.setText('<b>Filtrado Vivo y por Campos</b>',false);
			else if(parametros.jsonBusqueda!=null)
				labelResultados.setText('<b>Filtrado por Campos</b>',false);
			else
				labelResultados.setText('<b>Filtrado Vivo</b>',false);
					
    	},	
    
    //Aquí empiezan las funciones del PanelBusqueda
    onRenderPanelBusqueda:
    	function( panelBusqueda, eOpts ) {
    		var definicionesColumnas = Ext.create('FMX.store.Busqueda.DefinicionesColumnas', {proxy: panelBusqueda.columnas})
    		definicionesColumnas.on(
				{
					load: this.onLoadBusquedaDefinicionesColumnas,
					scope: this,
					parent: panelBusqueda
				}
			);
			definicionesColumnas.load();
    	},	
    			
    	
    //Aquí empiezan las funciones del gridbusqueda
    onRenderGridBusqueda:
		function ( gridbusqueda, eOpts ){
			gridbusqueda.body.on({
				click : function() {
					gridbusqueda.windowbusquedabusqueda.down('multislider[accion="ControlOpacidad"]').setValue(0,0,false);
					for(var i=0;i<gridbusqueda.windowedicionbusqueda.length;i++) {
						gridbusqueda.windowedicionbusqueda[i].down('multislider[accion="ControlOpacidad"]').setValue(0,0,false);
					}
				},
				scope : this
			}); 	
        	
        	gridbusqueda.campos = Ext.create('FMX.store.Busqueda.Campos', {proxy: gridbusqueda.up('panelbusqueda').camposBusqueda});
        	gridbusqueda.campos.on(
				{
					load: this.onLoadBusquedaCampos,
					scope: this,
					parent: gridbusqueda
				}
			);
			gridbusqueda.campos.load();
	},
    
	onClickGridBusquedaButtonDescargarReporte:
    	function( buttonDescargarReporte, e, eOpts ) {
    		var gridbusqueda = buttonDescargarReporte.up('gridbusqueda');
    		var params = Ext.JSON.decode(Ext.JSON.encode(gridbusqueda.getStore().getProxy().extraParams));
    		var descargarFilas = gridbusqueda.up('panelbusqueda').descargarFilas;
    		FMX.utils.FuncionesGlobales.mergeRecursive(params,descargarFilas.params);
    		var form = Ext.create('Ext.form.Panel',
    			{ 									
					standardSubmit: true, 
            		url: descargarFilas.url
        		}
        	);
			form.submit({
				params: params
			});
    },	
		
    onChangeGridBusquedaComboCantidadRegistrosMostrados: 
    	function(comboCantidadRegistrosMostrados, newValue, oldValue, eOpts ) {
        	var storeRegistros = comboCantidadRegistrosMostrados.up('gridbusqueda').getStore();
        	var start = (storeRegistros.currentPage-1)*storeRegistros.pageSize
			var pagina = Math.floor(start/newValue)+1;
			storeRegistros.pageSize=newValue;
			storeRegistros.loadPage(pagina);
	},
    
    onClickGridBusquedaButtonMostrarVentanaBusqueda:
    	function( buttonMostrarVentanaBusqueda, e, eOpts ) {
    		var gridbusqueda = buttonMostrarVentanaBusqueda.up('gridbusqueda');
    		var windowbusquedabusqueda = gridbusqueda.windowbusquedabusqueda;
    		if(windowbusquedabusqueda.isHidden()||windowbusquedabusqueda.collapsed) {
    			windowbusquedabusqueda.down('multislider[accion="ControlOpacidad"]').setValue(0,100,false);
	    		windowbusquedabusqueda.show();
	    		FMX.utils.FuncionesGlobales.restoreWindow(windowbusquedabusqueda, eOpts);
    		} else {
    			windowbusquedabusqueda.hide();
    		}  		  	
    },
			    	
	onClickGridBusquedaButtonRecargarBusqueda:
		function( buttonRecargarBusqueda, e, eOpts ) {
			var gridbusqueda = buttonRecargarBusqueda.up('gridbusqueda');
	 		gridbusqueda.getStore().load();
	}, 
	
	onClickGridBusquedaButtonEditarFila:
		function( buttonEditarFila, e, eOpts ) {
			var gridbusqueda = buttonEditarFila.up('gridbusqueda')
			var selectedRecords = gridbusqueda.getSelectionModel().getSelection();
			if(selectedRecords.length>0)
				this.crearWindowEdicionBusqueda(gridbusqueda,selectedRecords[0]);
			else
				//FMX.utils.FuncionesGlobales.mostrarMensajeEmergente(gridbusqueda.up(),"Advertencia","Selecciona un registro para editar");
				FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        			componente: gridbusqueda.up(),
	        			titulo: 'Advertencia',
	        			mensaje: 'Selecciona un registro para editar',
	        			iconCls: ''
	        	});
	}, 
	
	onClickGridBusquedaButtonEliminarFila:
		function( buttonEliminarFila, e, eOpts ) {
			var gridbusqueda = buttonEliminarFila.up('gridbusqueda');
			var selectedRecords = gridbusqueda.getSelectionModel().getSelection();
			if(selectedRecords.length>0)
					this.eliminarFilagridbusqueda(gridbusqueda,selectedRecords[0]);
				else
					//FMX.utils.FuncionesGlobales.mostrarMensajeEmergente(gridbusqueda.up(),"Advertencia","Selecciona un registro para eliminar");
					FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        			componente: gridbusqueda.up(),
	        			titulo: 'Advertencia',
	        			mensaje: 'Selecciona un registro para eliminar',
	        			iconCls: ''
	        		});
	},
	
	eliminarFilagridbusqueda:
		function eliminarFila(gridbusqueda,record){
			var store = gridbusqueda.getStore();
			var parametros = store.getProxy().extraParams; 
	    	var Principal = this;
	    	Ext.Msg.show({
		    	title:'Eliminar',
		     	msg: '¿Deseas eliminar el Fila?',
		     	buttons: Ext.Msg.YESNO,
		     	icon: Ext.Msg.WARNING,
		     	fn: function (btn){
		        	if(btn=='yes'){
		        		gridbusqueda.up().setLoading('Espere por favor...',true);
		        		var request = {  
							method: 'POST',
							store: store,
							parent: gridbusqueda.up(),
							success: Principal.successAjaxFnN,   
							timeout: 30000,  
							params: {  
									action: "deleteFila",
									recordData: Ext.JSON.encode(record.data)
							}
						}
						var eliminarFila = gridbusqueda.up('panelbusqueda').eliminarFila;
						FMX.utils.FuncionesGlobales.mergeRecursive(request,eliminarFila);
						Ext.Ajax.request(request);
		         }
		     }
		});    	
	},
	    
	successAjaxFnN :
	    function(response, opts) {
	        var jsonData = Ext.JSON.decode(response.responseText);
	        if (jsonData.success) {
	        	FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        		componente: opts.parent,
	        		titulo: 'Éxito',
	        		mensaje: jsonData.message,
	        		iconCls: ''
	        	});
				opts.store.load();
				if(typeof(parent.frames.left) != 'undefined') {
					var location=basePath+'/jsp/ArbolExpediente.jsp?select=Gaveta&arbol.tipo=g';
					parent.frames.left.location = location;	
				} //Remover en un futuro.
	        } else {
	        	FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        		componente: opts.parent,
	        		titulo: 'Error',
	        		mensaje: jsonData.message,
	        		iconCls: ''
	        	});
	        }
	        opts.parent.setLoading(false);
	    },
	
	//Aquí empiezan funciones relacionadas con TriggerFieldFiltro
	onChangeTriggerFieldFiltro: 
		function(triggerfieldfiltro, newValue, oldValue, eOpts ) {
			var gridbusqueda = triggerfieldfiltro.up('gridbusqueda');
			var windowbusquedabusqueda = triggerfieldfiltro.up('windowbusquedabusqueda');
			if(gridbusqueda==null) {
				gridbusqueda = windowbusquedabusqueda.parent
				anothertriggerfieldfiltro = gridbusqueda.down('triggerfieldfiltro')
			} else {
				windowbusquedabusqueda = gridbusqueda.windowbusquedabusqueda;
				anothertriggerfieldfiltro = windowbusquedabusqueda.down('triggerfieldfiltro')
			}
		   	//Cambiar esto para relizarse con Extjs Delayedtask
			if(!triggerfieldfiltro.isDisabled()) {
				if(anothertriggerfieldfiltro.isDisabled)
					anothertriggerfieldfiltro.setValue(newValue);
		    	setTimeout(function()
		    		{
		    			if(triggerfieldfiltro.getValue()==newValue){
		    				gridbusqueda.liveSearch = newValue;
							gridbusqueda.getStore().loadPage(1);
		   			}
		   					
		   		},
				1000
				);
			}
	},
	
	beforeCloseWindowEdicionBusqueda:
		function ( windowedicionbusqueda, eOpts ){
			//FMX.utils.FuncionesGlobales.restoreWindow(windowedicionbusqueda, eOpts);
			var gridbusqueda = windowedicionbusqueda.parent;
			for(var i=0;i<gridbusqueda.windowedicionbusqueda.length;i++) {
				if(gridbusqueda.windowedicionbusqueda[i].id==windowedicionbusqueda.id) {
					gridbusqueda.windowedicionbusqueda.splice(i,1);
					return true;
				}
			}
	},
	
	onRenderWindowBusqueda:
		function ( windowBusqueda, eOpts ){
			windowBusqueda.body.on({
				click : function() {
					windowBusqueda.down('multislider[accion="ControlOpacidad"]').setValue(0,100,false);
				},
				scope : this
			});
	},
	
	//Aquí empiezan funciones relacionadas con WindowBusquedaBusqueda
	disableWindowBusquedaBusquedaTriggerFieldFiltro:
		function (windowbusquedabusqueda, eOpts) {
			windowbusquedabusqueda.down('triggerfieldfiltro').setDisabled(true);
			var gridbusqueda = windowbusquedabusqueda.parent;
			gridbusqueda.down('triggerfieldfiltro').setDisabled(false);
	},
	
	enableWindowBusquedaBusquedaTriggerFieldFiltro:
		function (windowbusquedabusqueda, eOpts) {
			var gridbusqueda = windowbusquedabusqueda.parent;
			gridbusqueda.down('triggerfieldfiltro').setDisabled(true);
			windowbusquedabusqueda.down('triggerfieldfiltro').setDisabled(false);
	},
	
	onChangeMultisliderControlOpacidad: 
		function( multisliderControlOpacidad, newValue, thumb, eOpts ) {
		  if(newValue<100)
		  	minimoOpacidad=newValue;
		  multisliderControlOpacidad.up().down('label').setText('Transparencia '+newValue+'%:',false);
		  multisliderControlOpacidad.up('window').getEl().setOpacity(newValue/100);
	},
	
	onClickWindowBusquedaBusquedaButtonLimpiarCampos:
		function(buttonLimpiarCampos, e, eOpts ) {
			var windowbusquedabusqueda = buttonLimpiarCampos.up('windowbusquedabusqueda');
			var form = windowbusquedabusqueda.down('form');
			var campos = form.query();
			for(var i=0;i<campos.length;i++){
				campos[i].reset();
			}
	},

	onClickWindowBusquedaBusquedaButtonCrearFila:
		function(buttonBuscarBusqueda, e, eOpts ) {
			var windowbusquedabusqueda = buttonBuscarBusqueda.up('windowbusquedabusqueda');
			var form = windowbusquedabusqueda.down('form').getForm();
			if(!form.isValid())
				FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        		componente: windowbusquedabusqueda,
	        		titulo: 'Error',
	        		mensaje: 'Hay campos inválidos, favor de corregir',
	        		iconCls: ''
	        	});
	        else {
				var gridbusqueda = windowbusquedabusqueda.parent;
				var store = gridbusqueda.getStore();
				windowbusquedabusqueda.setLoading('Espere por favor...',true);
				var Principal = this;
				var request = {  
					method: 'POST',
					parent: windowbusquedabusqueda,
					store: store,
					success: Principal.successAjaxFnN,   
					timeout: 30000,  
					params: {  
						jsonInsertar: Ext.JSON.encode(form.getValues())
					}
				}
				var crearFila = gridbusqueda.up('panelbusqueda').crearFila;
				FMX.utils.FuncionesGlobales.mergeRecursive(request,crearFila);
  				Ext.Ajax.request(request);
	        }
	},
	
	onClickWindowBusquedaBusquedaButtonBuscar:
		function(buttonBuscarBusqueda, e, eOpts ) {
			var windowbusquedabusqueda = buttonBuscarBusqueda.up('windowbusquedabusqueda');
			var form = windowbusquedabusqueda.down('form').getForm();
			form.clearInvalid();
			var gridbusqueda = windowbusquedabusqueda.parent;
			gridbusqueda.camposBusqueda = form.getValues();
			gridbusqueda.getStore().loadPage(1);
	},
	
	crearWindowEdicionBusqueda:
		function (gridbusqueda,record){
			var windowedicionbusqueda = null;
			for (index = 0; index < gridbusqueda.windowedicionbusqueda.length; ++index) {
				if(record.data == gridbusqueda.windowedicionbusqueda[index].data)
    				windowedicionbusqueda = gridbusqueda.windowedicionbusqueda[index];
			}
	    	if(windowedicionbusqueda==null){
				windowedicionbusqueda = Ext.create('FMX.view.centro.Busqueda.WindowEdicion',
    				{
						parent: gridbusqueda,
						data: record.data
    				}
        		);
        		gridbusqueda.windowedicionbusqueda.push(windowedicionbusqueda);
        		windowedicionbusqueda.show();
        		var form = windowedicionbusqueda.down('form');
    			var camposDinamicos = FMX.view.Utils.CampoDinamico.getFromStore(gridbusqueda.campos,record.data);
				form.add(camposDinamicos);
	    	} else {
	    		windowedicionbusqueda.show();
	    		windowedicionbusqueda.down('multislider[accion="ControlOpacidad"]').setValue(0,100,false);
	    		FMX.utils.FuncionesGlobales.restoreWindow(windowedicionbusqueda);
	    	}
	},
	
	onClickWindowEdicionBusquedaButtonCancelarEdicion:
		function(buttonCancelarEdicion, e, eOpts ) {
			var windowedicionbusqueda = buttonCancelarEdicion.up('windowedicionbusqueda');
			windowedicionbusqueda.close();
	},
	
	onClickWindowEdicionBusquedaButtonGuardarEdicion:
		function(buttonGuardarEdicion, e, eOpts ) {
			var windowedicionbusqueda = buttonGuardarEdicion.up('windowedicionbusqueda');
			var form = windowedicionbusqueda.down('form').getForm();
			if(!form.isValid())
				FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        		componente: windowedicionbusqueda,
	        		titulo: 'Error',
	        		mensaje: 'Hay campos inválidos, favor de corregir',
	        		iconCls: ''
	        	});
	        else {
				var gridbusqueda = windowedicionbusqueda.parent;
				var store = gridbusqueda.getStore();
				windowedicionbusqueda.setLoading('Espere por favor...',true);
				
				var Principal = this;
				
				var request = {  
					parent: windowedicionbusqueda,
					store: store,
					success: Principal.successAjaxFnN,   
					timeout: 30000,  
					params: {  
						json: Ext.JSON.encode(form.getValues())
					}
				}
				
				FMX.utils.FuncionesGlobales.mergeRecursive(request,gridbusqueda.up('panelbusqueda').editarFila);
  				Ext.Ajax.request(request);
	        }
	}
});