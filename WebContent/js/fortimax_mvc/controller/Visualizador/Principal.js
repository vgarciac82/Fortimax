Ext.define('FMX.controller.Visualizador.Principal', {
	extend: 'Ext.app.Controller',
	
	requires: [
	           'FMX.utils.Visualizador.VariablesEntorno',
	           'FMX.controller.VariablesEntorno',
	           'FMX.utils.Visualizador.Utils',
	           'FMX.utils.FuncionesGlobales', //usar para minimizacion de windows y para enviar mensajes deslizantes
	           'FMX.view.Utils.SelectorArchivo'
    ],
	
	models: [
		'Visualizador.Miniatura',
		'Visualizador.DocumentoCompartido'
	],

	views: [
	        'centro.Visualizador.TabPanelVisualizador',
			'centro.Visualizador.PanelMiniaturas', 
			'centro.Visualizador.PanelImagen',
			'centro.Visualizador.PanelImagenContenedorIzquierdo',
			'centro.Visualizador.PanelPlantilla',
			'centro.Visualizador.PanelTexto',
			'centro.Visualizador.PanelDetalles',
			'centro.Visualizador.ToolbarImagen',
			'centro.Visualizador.PanelLienzo',
			'centro.Visualizador.DrawImagen',
			'centro.Visualizador.DataViewVisualizadorMiniaturas',
			'centro.Visualizador.WindowEscanearDocumento',
			'centro.Visualizador.WindowAgregarImagen',
			'centro.Visualizador.WindowImprimirDocumento',
			'centro.Visualizador.WindowCompartirDocumento',
			'Utils.CampoDinamico'
    ],

    stores: [
    			'Visualizador.Miniaturas',
    			'Visualizador.CamposPlantilla',
    			'Visualizador.DetallesDocumento',
    			'Visualizador.AtributosDocumento',
    			'Visualizador.HistoricoDocumento',
    			'Visualizador.DocumentosCompartidos'
    		],
	
//    refs: [{
//		ref : 'PanelArbolesPrincipal',
//		selector: 'PanelArbolesPrincipalView'
//	}],

    init: function() {
//    	var me = this;
    	this.control({
    		'tabpanelvisualizador' : {
    			beforerender: this.onBeforeRenderTabPanelVisualizador,
    			render: this.onRenderTabPanelVisualizador
    		},
    		'panellienzo' : {
    			afterrender: this.onAfterRenderPanelLienzo
    		},
            'panelplantilla button[accion="EditarDatosDocumento"]': {
             	click: this.onClickPanelPlantillaButtonEditarDatosDocumento   
            },
            'panelplantilla button[accion="GuardarDatosDocumento"]': {
             	click: this.onClickPanelPlantillaButtonGuardarDatosDocumento   
            },
    		'toolbarimagen' : {
    			afterrender: this.onAfterRenderToolBarImagen
    		},
    		'drawimagen' : {
    			afterrender: this.onAfterRenderDrawImagen
    		},
    		'dataviewvisualizadorminiaturas': {
    			itemclick: this.onItemClickDataViewVisualizadorMinitaturasCreaSprite
    		},
    		'toolbarimagen button[accion=EscanearDocumento]':{
				click: this.onClickToolbarImagenButtonEscanearDocumento
			},
			'toolbarimagen button[accion=AgregarImagen]':{
				click: this.onClickToolbarImagenButtonAgregarImagen
			},
			'toolbarimagen button[accion=ImprimirDocumento]':{
				click: this.onClickToolbarImagenButtonImprimirDocumento
			},
			'toolbarimagen button[accion=PaginaInicial]':{
				click: this.onClickToolbarImagenButtonPaginaInicial
			},
			'toolbarimagen button[accion=PaginaAnterior]':{
				click: this.onClickToolbarImagenButtonPaginaAnterior
			},
			'toolbarimagen textfield[accion=PaginaActual]':{
				specialkey: this.onSpecialKeyToolbarImagenTextfieldPaginaActual
			},
			'toolbarimagen button[accion=PaginaSiguiente]':{
				click: this.onClickToolbarImagenButtonPaginaSiguiente
			},
			'toolbarimagen button[accion=PaginaFinal]':{
				click: this.onClickToolbarImagenButtonPaginaFinal
			},
			'toolbarimagen button[accion=AlejarImagen]':{
				click: this.onClickToolbarImagenButtonAlejarImagen
			},
			'toolbarimagen slider[accion=ZoomImagen]':{
				change: this.onChangeToolbarImagenSliderZoomImagen
			},
			'toolbarimagen button[accion=AcercarImagen]':{
				click: this.onClickToolbarImagenButtonAcercarImagen
			},
			'toolbarimagen button[accion=RotarIzquierdaImagen]':{
				click: this.onClickToolbarImagenButtonRotarIzquierdaImagen
			},
			'toolbarimagen slider[accion=RotarImagen]':{
				change: this.onChangeToolbarImagenSliderRotarImagen
			},
			'toolbarimagen button[accion=RotarDerechaImagen]':{
				click: this.onClickToolbarImagenButtonRotarDerechaImagen
			},
			'toolbarimagen button[accion=AjustarAreaImagen]':{
				click: this.onClickToolbarImagenButtonAjustarAreaImagen
			},
			'toolbarimagen button[accion=AjustarAnchoImagen]':{
				click: this.onClickToolbarImagenButtonAjustarAnchoImagen
			},
			'toolbarimagen button[accion=AjustarAltoImagen]':{
				click: this.onClickToolbarImagenButtonAjustarAltoImagen
			},
			'toolbarimagen button[accion=MostrarTamanoOriginalImagen]':{
				click: this.onClickToolbarImagenButtonMostrarTamanoOriginalImagen
			},
			'toolbarimagen button[accion=LimpiarDocumento]':{
				click: this.onClickToolbarImagenButtonLimpiarDocumento
			},
			'toolbarimagen button[accion=EliminarPagina]':{
				click: this.onClickToolbarImagenButtonEliminarPagina
			},
			'toolbarimagen button[accion=CompartirDocumento]':{
				click: this.onClickToolbarImagenButtonCompartirDocumento
			},
			'panelminiaturas': {
				afterrender: this.onAfterRenderPanelMiniaturas
			},
			'panelminiaturas button[accion=ActualizarMiniaturas]': {
				click: this.onClickPanelMiniaturasButtonActualizarMiniaturas
			}, 
			'panelminiaturas button[accion=AgregarSiguientesMiniaturas]': {
				click: this.onClickPanelMiniaturasButtonAgregarSiguientesMiniaturas
			}, 
			'panelminiaturas button[accion=AgregarTodasMiniaturas]': {
				click: this.onClickPanelMiniaturasButtonAgregarTodasMiniaturas
			},
			'paneldetalles': {
				afterrender: this.onAfterRenderPanelDetalles
			},
			'windowcompartirdocumento button[accion=GuardarCambios]': {
				click: this.onClickWindowCompartirDocumentoButtonGuardarCambios
			},
			'windowcompartirdocumento button[accion=CopiarLiga]': {
				click: this.onClickWindowCompartirDocumentoButtonCopiarLiga
			},
			'windowcompartirdocumento checkboxfield[accion=CompartirDocumento]': {
				change: this.onChangeWindowCompartirDocumentoCheckboxCompartirDocumento
			},
			'windowcompartirdocumento datefield[accion=FechaDeVencimiento]': {
				change: this.onChangeWindowCompartirDocumentoDatefieldFechaDeVencimiento
			},
			'windowcompartirdocumento spinnerfield[accion=HoraDeVencimiento]': {
				change: this.onChangeWindowCompartirDocumentoSpinnerfieldHoraDeVencimiento,
				spin: this.onSpinWindowCompartirDocumentoSpinnerfieldHoraDeVencimiento
			},
			'windowcompartirdocumento': {
				beforeclose: this.onBeforeCloseWindowCompartirDocumento
			},
			'windowagregarimagen button[accion=AgregarFila]': {
				click: this.onClickWindowAgregarImagenButtonAgregarFila
			},
			'windowagregarimagen button[accion=EnviarImagenes]': {
				click: this.onClickWindowAgregarImagenButtonEnviarImagenes
			}
    	});
    	
    },

    onItemClickDataViewVisualizadorMinitaturasCreaSprite: function(dataViewMini, record, item, index, e, eOpts ){
    	dataViewMini.up('tabpanelvisualizador').down('panellienzo').setLoading('Cargando imagen...', true);

    	var sliderRotar = dataViewMini.up('tabpanelvisualizador').down('toolbarimagen').down('slider[accion=RotarImagen]');
    	sliderRotar.setValue(0);

    	var textFieldPaginaActual = dataViewMini.up('tabpanelvisualizador').down('toolbarimagen').down('textfield[accion=PaginaActual]');
    	var indicePaginaSeleccionada = record.index + 1;
    	textFieldPaginaActual.setValue(indicePaginaSeleccionada);

    	var drawImagen = dataViewMini.up('tabpanelvisualizador').down('drawimagen');

    	var urlImagen = record.data.imagenPagina;
    	var anchoImagen = parseInt(record.data.ancho);
    	var altoImagen = parseInt(record.data.alto);

    	this.agregarSprite(drawImagen, urlImagen, altoImagen, anchoImagen);

    	var botonAjustar = dataViewMini.up('tabpanelvisualizador').down('toolbarimagen').down('button[accion=AjustarAreaImagen]');
    	botonAjustar.toggle(true);
    	botonAjustar.fireEvent('click', botonAjustar);
    },
    
    onAfterRenderDrawImagen: function(drawImagen, eOpts){
    	drawImagen.getEl().on({
    		'contextmenu': function( e, t, eOpts) {
    			e.preventDefault();
    		}});
    },
	
    onClickToolbarImagenButtonEscanearDocumento: function(boton){
    	var nodo = boton.up('tabpanelvisualizador').nodo;
    	var windowUrl = basePath + '/jsp/PageDocumentScan.jsp?select=' + nodo;
    	if(!boton.up('panelimagen').windowescaneardocumento){
    		Ext.apply(boton.up('panelimagen'), {
    			windowescaneardocumento: Ext.create('FMX.view.centro.Visualizador.WindowEscanearDocumento',{
//    				id: 'windowescaneardocumentoid',
    				html: '<div style="height:100%; scrolling="yes" overflow:scroll !important;"><iframe id="framePDF" src="'
    					+ windowUrl + '"  style="width:100%;height:100%;border:none;"></iframe></div>'
    			})
    		});
    	}
    	
    	boton.up('panelimagen').windowescaneardocumento.show();
    },
    
    onClickToolbarImagenButtonAgregarImagen: function(boton){
    	var nodo = boton.up('tabpanelvisualizador').nodo;
    	var nombreDocumento = boton.up('tabpanelvisualizador').nombreDoc;
    	if(!boton.up('panelimagen').windowagregarimagen || (!boton.up('panelimagen').windowagregarimagen.isVisible())){
    		//Si no está creado o si no es visible
    		
    		Ext.apply(boton.up('panelimagen'), {
    			windowagregarimagen: Ext.create('FMX.view.centro.Visualizador.WindowAgregarImagen', {nodo: nodo, nombreDocumento: nombreDocumento})
    		});
    		boton.up('panelimagen').windowagregarimagen.show();
    	}
    },
    
    onClickToolbarImagenButtonImprimirDocumento: function(boton){
    	var nodo = boton.up('tabpanelvisualizador').nodo;
    	var indicePaginaSeleccionada = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas').getSelectedNodes()[0].id;
    	indicePaginaSeleccionada--;

    	var windowUrl = basePath + '/jsp/Impresora.jsp?select=' + nodo + '&page_actual=' + indicePaginaSeleccionada;
    	if(!boton.up('panelimagen').windowimprimirdocumento){
    		Ext.apply(boton.up('panelimagen'), {
    			windowimprimirdocumento: Ext.create('FMX.view.centro.Visualizador.WindowImprimirDocumento',{
//    				id: 'windowimprimirdocumentoid',
    				html: '<div style="height:100%; scrolling="yes" overflow:scroll !important;"><iframe id="framePDF" src="'
    					+ windowUrl + '"  style="width:100%;height:100%;border:none;"></iframe></div>'
    			})
    		});
    	}
    	boton.up('panelimagen').windowimprimirdocumento.show();
    },
    
    onClickToolbarImagenButtonPaginaInicial: function(boton){
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	dataViewMini.fireEvent('itemclick', dataViewMini, dataViewMini.store.data.items[0]);
    },
    
    onClickToolbarImagenButtonPaginaAnterior: function(boton){
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var indicePaginaSeleccionada = parseInt(dataViewMini.getSelectedNodes()[0].id);
    	if( indicePaginaSeleccionada > 1){
    		indicePaginaSeleccionada = indicePaginaSeleccionada - 2;
    		dataViewMini.fireEvent('itemclick', dataViewMini, dataViewMini.store.data.items[indicePaginaSeleccionada]);
    	}
    },
    
    onSpecialKeyToolbarImagenTextfieldPaginaActual: function(textField, e, eOpts){
    	if(e.keyCode==13){
    		var dataViewMini = textField.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    		if(textField.getValue() > 0 && textField.getValue() <= dataViewMini.getNodes().length){
    			var indicePaginaSeleccionada = parseInt(textField.getValue() - 1);
    			dataViewMini.fireEvent('itemclick', dataViewMini, dataViewMini.store.data.items[indicePaginaSeleccionada]);
    		}
    	}
    },
    
    onClickToolbarImagenButtonPaginaSiguiente: function(boton){
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var indicePaginaSeleccionada = parseInt(dataViewMini.getSelectedNodes()[0].id);
    	if( indicePaginaSeleccionada < dataViewMini.store.getCount()){
    		dataViewMini.fireEvent('itemclick', dataViewMini, dataViewMini.store.data.items[indicePaginaSeleccionada]);
    	}
    },
    
    onClickToolbarImagenButtonPaginaFinal: function(boton){
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var indicePaginaFinal =  dataViewMini.store.getCount() - 1;
    	dataViewMini.fireEvent('itemclick', dataViewMini, dataViewMini.store.data.items[indicePaginaFinal]);
    },
    
    onClickToolbarImagenButtonAlejarImagen: function(boton, e, eOpts){
    	var sliderZoom = boton.up('toolbarimagen').down('slider[accion=ZoomImagen]');
    	var escalaNueva = sliderZoom.getValue() - 5;
    	sliderZoom.setValue(escalaNueva);
    	
    },
    
    onChangeToolbarImagenSliderZoomImagen: function(slider, newValue, thumb, eOpts){
    	escala = newValue/100;
    	
    	var drawImagen = slider.up('panelimagen').down('drawimagen');

    	var anchoLienzo = drawImagen.up('panellienzo').getWidth();
    	var altoLienzo = drawImagen.up('panellienzo').getHeight();

    	var sprite = slider.up('tabpanelvisualizador').down('drawimagen').surface.items.first();

    	var ejeX = (anchoLienzo - (sprite.width*escala))/2;
    	var ejeY = (altoLienzo - (sprite.height*escala))/2;

    	if(ejeX<5){
    		ejeX= 5;
    	}

    	if(ejeY<5){
    		ejeY = 5;
    	}

    	sprite.setAttributes({
    		x: ejeX,
    		y: ejeY,
    		width: sprite.width*escala,
    		height: sprite.height*escala
    	}, true);

    	drawImagen.setWidth(sprite.width*escala + 2*ejeX);
    	drawImagen.setHeight(sprite.height*escala + 2*ejeY);
    	
    	if(((sprite.width*escala)<anchoLienzo)&&((sprite.height*escala)<altoLienzo)){
    		drawImagen.up('panellienzo').setAutoScroll(false);
    	} else {
    		drawImagen.up('panellienzo').setAutoScroll(true);
    	}
    },
    
    onClickToolbarImagenButtonAcercarImagen: function(boton, e, eOpts){
    	var sliderZoom = boton.up('toolbarimagen').down('slider[accion=ZoomImagen]');
    	var escalaNueva = sliderZoom.getValue() + 5;
    	sliderZoom.setValue(escalaNueva);
    },
    
    onClickToolbarImagenButtonRotarIzquierdaImagen: function(boton, e, eOpts){
    	var sliderRotar = boton.up('toolbarimagen').down('slider[accion=RotarImagen]');
    	var gradosNuevos = sliderRotar.getValue() - 1;
    	sliderRotar.setValue(gradosNuevos);
    },
    
    onChangeToolbarImagenSliderRotarImagen: function(slider, grados, thumb, thisFn){
    	var sprite = slider.up('tabpanelvisualizador').down('drawimagen').surface.items.first();
    	sprite.setAttributes({
    		rotation: {
    			degrees: grados
    		}
    	}, true);
    },
    
    onClickToolbarImagenButtonRotarDerechaImagen: function(boton, e, eOpts){
    	var sliderRotar = boton.up('toolbarimagen').down('slider[accion=RotarImagen]');
    	var gradosNuevos = sliderRotar.getValue() + 1;
    	sliderRotar.setValue(gradosNuevos);
    },
    
    onClickToolbarImagenButtonAjustarAreaImagen: function(boton, e, eOpts){
    	boton.up('toolbarimagen').down('slider[accion=ZoomImagen]').setValue(100);
    	
    	var anchoLienzo = boton.up('tabpanelvisualizador').down('panellienzo').getWidth();
    	var altoLienzo = boton.up('tabpanelvisualizador').down('panellienzo').getHeight();
    	var anchoImagen = boton.up('tabpanelvisualizador').down('drawimagen').surface.items.first().width;
		var altoImagen = boton.up('tabpanelvisualizador').down('drawimagen').surface.items.first().height;

		var escala = 1;
    	if((anchoLienzo/anchoImagen)<(altoLienzo/altoImagen)){
    		escala = anchoLienzo/anchoImagen; 
    	} else {
    		escala = altoLienzo/altoImagen;
    	}
    	
    	boton.up('toolbarimagen').down('slider[accion=ZoomImagen]').setValue(escala*100*0.8);
    },
    
    onClickToolbarImagenButtonAjustarAnchoImagen: function(boton, e, eOpts){
    	boton.up('toolbarimagen').down('slider[accion=ZoomImagen]').setValue(100);

    	var anchoLienzo = boton.up('tabpanelvisualizador').down('panellienzo').getWidth();
    	var anchoImagen = boton.up('tabpanelvisualizador').down('drawimagen').surface.items.first().width;
		var escala = anchoLienzo/anchoImagen; 

		boton.up('toolbarimagen').down('slider[accion=ZoomImagen]').setValue(escala*100*0.9);
    },
    
    onClickToolbarImagenButtonAjustarAltoImagen: function(boton, e, eOpts){
    	boton.up('toolbarimagen').down('slider[accion=ZoomImagen]').setValue(100);

    	var altoLienzo = boton.up('tabpanelvisualizador').down('panellienzo').getHeight();
    	var altoImagen = boton.up('tabpanelvisualizador').down('drawimagen').surface.items.first().height;
    	var escala = altoLienzo/altoImagen; 

    	boton.up('toolbarimagen').down('slider[accion=ZoomImagen]').setValue(escala*100*0.9);
    },

    onClickToolbarImagenButtonMostrarTamanoOriginalImagen: function(boton, e, eOpts){
    	boton.up('toolbarimagen').down('slider[accion=ZoomImagen]').setValue(100);
    },
    
    onClickToolbarImagenButtonLimpiarDocumento: function(boton, e, eOpts){
    	var nodo = boton.up('tabpanelvisualizador').nodo;
    	var numPaginas = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas').store.getCount();
    	var me = this;
    	if(numPaginas>0){
    		Ext.MessageBox.confirm('Confirmacion', 'Desea limpiar el contenido del documento?', function(btn, text){
    			if (btn == 'yes'){
    				Ext.Ajax.request({
    					url: rutaServletLimpiaDoc,
    					params: {
    						select: nodo
    					},
    					success: function(response){
    						var text = Ext.decode(response.responseText);
    						Ext.MessageBox.alert('Confirmacion', text.msg);
    						boton.up('tabpanelvisualizador').close();
    						me.getController('FMX.controller.Principal').ActualizaArbol(FMX.controller.VariablesEntorno.arbolSeleccionado);
    					}
    				});
    			}
    		});
    	} else {
    		Ext.MessageBox.alert('Aviso', 'Documento sin páginas.');
    	}
    },
    
    onClickToolbarImagenButtonEliminarPagina: function(boton, e, eOpts){
    	var nodoSeleccionado = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas').getSelectedNodes()[0];
    	if(nodoSeleccionado==null){
    		console.log('No hay miniatura seleccionada.');
    		return;
    	}
    	var indicePaginaSeleccionada = nodoSeleccionado.id - 1;
    	var nodo = boton.up('tabpanelvisualizador').nodo;
    	var indiceAux = indicePaginaSeleccionada + 1;
    	var tabPanelVisualizador = boton.up('tabpanelvisualizador');
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var storeMini = dataViewMini.store;
    	
    	Ext.Msg.show({
    		title:'Eliminar',
    		msg: '¿Deseas eliminar la pagina: '+ indiceAux +'?',
    		buttons: Ext.Msg.YESNO,
    		icon: Ext.Msg.QUESTION,
    		fn: function (respuesta){
    			if(respuesta == 'yes'){ 
    				tabPanelVisualizador.getEl().mask('Eliminando página...', 'x-mask-loading');
    				Ext.Ajax.request({
    					url: rutaServletD,  
    					method: 'POST',  
    					success: function(response, request){
    						storeMini.load({params: {select: tabPanelVisualizador.nodo}});
    						tabPanelVisualizador.getEl().unmask();
    					},
    					failure: function(response, request){
    						var jsonData = Ext.JSON.decode(response.responseText);
    						Ext.Msg.show({
    							title: 'Error',
    							msg: jsonData.message,
    							buttons: Ext.Msg.OK,
    							animEl: 'elId',
    							icon: Ext.MessageBox.ERROR
    						});
    					},
    					timeout: 10000,  
    					params: {  
    						action: 'delPageDocument',  				
    						select: nodo,
    						index: indicePaginaSeleccionada
    					}});
    			}
    		}
    	});
    
    },
    
    onBeforeRenderTabPanelVisualizador: function(cmp, e, eOpts){
    	var dataViewMini = cmp.down('dataviewvisualizadorminiaturas');

    	dataViewMini.store.getProxy().extraParams = {
    		action: 'getMiniaturas',
    		start: 0,
    		limit: FMX.utils.Visualizador.VariablesEntorno.miniaturasPorCarga
    	};

    	dataViewMini.store.on({
    		load: this.onLoadMiniaturasStore,
    		scope: this,
    		vistaPadre: dataViewMini,
    		clickParaCargarSprite: true
    	});
    	
    },
    
    onLoadMiniaturasStore: function(MiniaturasStore, records, successful, eOpts){
    	var dataViewMini = eOpts.vistaPadre;
    	if(eOpts.clickParaCargarSprite){
    		if(records[0]!=null){
    			dataViewMini.fireEvent('itemclick', dataViewMini, records[0]);
    		}
    	}
    	eOpts.clickParaCargarSprite = false;
    },
    
    agregarSprite: function(drawImagen, urlImagen, altoImagen, anchoImagen){
    	drawImagen.up('panellienzo').setLoading(false);
    	drawImagen.show();
    	drawImagen.surface.removeAll(true);
    	
    	var sprite = drawImagen.surface.add({
    		type: 'image',
    		src: urlImagen,
    		width: anchoImagen,
    		height: altoImagen,
    		draggable: true
    	});
    	
    	sprite.show(true);    	
    },
    
    onAfterRenderPanelLienzo: function(panelLienzo){
    	var dataViewMini = panelLienzo.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var tabPanelVisualizador = panelLienzo.up('tabpanelvisualizador');
    	dataViewMini.store.load({params: {select: tabPanelVisualizador.nodo}});
    },
    
    onRenderTabPanelVisualizador: 
    	function(tabpanelvisualizador, eOpts){
    		var camposPlantilla = Ext.create('FMX.store.Visualizador.CamposPlantilla');
    		camposPlantilla.getProxy().extraParams.select = tabpanelvisualizador.nodo;
        	camposPlantilla.on(
				{
					load: this.onLoadVisualizadorCamposPlantilla,
					scope: this,
					parent: tabpanelvisualizador
				}
			);
			camposPlantilla.load();
    },
    
    onLoadVisualizadorCamposPlantilla:
    	function(visualizadorCamposPlantilla, records, successful, eOpts ) {
    		var tabpanelvisualizador = eOpts.parent;
    		var panelPlantilla = tabpanelvisualizador.down('panelplantilla');
    		if(visualizadorCamposPlantilla.getCount()>0) {
    			var camposDinamicos = FMX.view.Utils.CampoDinamico.getFromStore(visualizadorCamposPlantilla);
    			var fieldset = panelPlantilla.down('fieldset');
    			fieldset.removeAll();
    			fieldset.add(camposDinamicos);
    			var buttonEditarDatosDocumento = panelPlantilla.down('button[accion="EditarDatosDocumento"]');
    			this.permitirEdicionForm(panelPlantilla.down('form'),buttonEditarDatosDocumento.pressed);
    		} else {
    			tabpanelvisualizador.remove(panelPlantilla);
    		}
    },
    
    permitirEdicionForm:
    	function(form,editable) {
    		var fields = form.getForm().getFields().items;		
			if(editable) {
				for(var i=0;i<fields.length;i++)
					fields[i].setReadOnly(!fields[i].fortimax_campo.editable);
			} else {
    			for(var i=0;i<fields.length;i++)
					fields[i].setReadOnly(true);
			}
    },
    
   	onClickPanelPlantillaButtonEditarDatosDocumento:
		function( buttonEditarDatosDocumento, e, eOpts ) {
			var form = buttonEditarDatosDocumento.up('form');
			buttonEditarDatosDocumento.setText((buttonEditarDatosDocumento.pressed)?'Cancelar':'Editar');
			form.down('button[accion="GuardarDatosDocumento"]').setDisabled(!buttonEditarDatosDocumento.pressed);
			this.permitirEdicionForm(form,buttonEditarDatosDocumento.pressed);
	},
	
	onClickPanelPlantillaButtonGuardarDatosDocumento:
		function( buttonGuardarDatosDocumento, e, eOpts ) {
			buttonGuardarDatosDocumento.setDisabled(true);
			var form = buttonGuardarDatosDocumento.up('form');
			form.setLoading(true);
			var nodo = form.up('tabpanelvisualizador').nodo;
			Ext.Ajax.request({  
				url: rutaServlet, 
				method: 'POST',
				success: function(response, opts) {
					var jsonData = Ext.JSON.decode(response.responseText);
					FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        			componente: form.up('panelplantilla'),
	        			titulo: (jsonData.success)?'Éxito':'Error',
	        			mensaje: jsonData.message,
	        			iconCls: ''
	        		});
	        		form.setLoading(false);
	        		buttonGuardarDatosDocumento.setDisabled(false);
				},
				failure : function(response, opts) {
					FMX.utils.FuncionesGlobales.mostrarMensajeEmergente({
	        			componente: form.up('panelplantilla'),
	        			titulo: 'Error',
	        			mensaje: 'No se pudo conectar a '+opts.url,
	        			iconCls: ''
	        		});
	        		form.setLoading(false);
	        		buttonGuardarDatosDocumento.setDisabled(false);
				},
				timeout: 30000,  
				params: {  
					action: 'EditDocument',
					select: nodo,
					plantilla_documento: Ext.JSON.encode(form.getValues())
				}
			});
	}, 
    
    onClickPanelMiniaturasButtonActualizarMiniaturas: function(boton, e, eOpts){
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var tabPanelVisualizador = boton.up('tabpanelvisualizador');
    	dataViewMini.store.load({params: {select: tabPanelVisualizador.nodo}});
    },
    
    onClickPanelMiniaturasButtonAgregarSiguientesMiniaturas: function(boton, e, eOpts){
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var storeMini = dataViewMini.store;
    		
    	storeMini.getProxy().extraParams = {
    		action: 'getMiniaturas',
    		start: 0,
    		limit: storeMini.getCount() + FMX.utils.Visualizador.VariablesEntorno.miniaturasPorCarga
    	};
    	
    	var tabPanelVisualizador = boton.up('tabpanelvisualizador');
    	storeMini.load({params: {select: tabPanelVisualizador.nodo}});
    },
    
    onClickPanelMiniaturasButtonAgregarTodasMiniaturas: function(boton, e, eOpts){
    	var dataViewMini = boton.up('tabpanelvisualizador').down('dataviewvisualizadorminiaturas');
    	var storeMini = dataViewMini.store;
    		
    	storeMini.getProxy().extraParams = {
    		action: 'getMiniaturas',
    		start: 0,
    		limit: 1000
    	};
    	
    	var tabPanelVisualizador = boton.up('tabpanelvisualizador');
    	storeMini.load({params: {select: tabPanelVisualizador.nodo}});
    },
    
    onAfterRenderToolBarImagen: function(toolbar, e, eOpts){
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=EscanearDocumento]').getEl(),
    		html: 'Escanear Documento'
    	});

    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=AgregarImagen]').getEl(),
    		html: 'Agregar Imagen'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=ImprimirDocumento]').getEl(),
    		html: 'Imprimir Documento'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=PaginaInicial]').getEl(),
    		html: 'Ir a Página Inicial'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=PaginaAnterior]').getEl(),
    		html: 'Ir a Página Anterior'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('textfield[accion=PaginaActual]').getEl(),
    		html: 'Número de Página Actual'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=PaginaSiguiente]').getEl(),
    		html: 'Ir a Página Siguiente'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=PaginaFinal]').getEl(),
    		html: 'Ir a Página Final'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=AlejarImagen]').getEl(),
    		html: 'Alejar Imagen'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=AcercarImagen]').getEl(),
    		html: 'Acercar Imagen'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=RotarIzquierdaImagen]').getEl(),
    		html: 'Rotar a la Izquierda'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=RotarDerechaImagen]').getEl(),
    		html: 'Rotar a la Derecha'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=AjustarAreaImagen]').getEl(),
    		html: 'Ajustar'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=AjustarAnchoImagen]').getEl(),
    		html: 'Ajustar Horizontal'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=AjustarAltoImagen]').getEl(),
    		html: 'Ajustar Vertical'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=MostrarTamanoOriginalImagen]').getEl(),
    		html: 'Original'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: toolbar.down('button[accion=EliminarPagina]').getEl(),
    		html: 'Eliminar Página'
    	});
    },
    
    onAfterRenderPanelMiniaturas: function(panelMiniaturas){
    	Ext.create('Ext.tip.ToolTip', {
    		target: panelMiniaturas.down('button[accion=ActualizarMiniaturas]').getEl(),
    		html: 'Actualizar Miniaturas'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: panelMiniaturas.down('button[accion=AgregarSiguientesMiniaturas]').getEl(),
    		html: 'Cargar ' + FMX.utils.Visualizador.VariablesEntorno.miniaturasPorCarga + ' Más'
    	});
    	Ext.create('Ext.tip.ToolTip', {
    		target: panelMiniaturas.down('button[accion=AgregarTodasMiniaturas]').getEl(),
    		html: 'Cargar Todas'
    	});
    },
    
    onAfterRenderPanelDetalles: function(panelDetalles){
    	var detallesDocumento = Ext.create('FMX.store.Visualizador.DetallesDocumento');
    	var nodo = panelDetalles.up('tabpanelvisualizador').nodo;
   
    	var gridpanelDetalles = panelDetalles.down('gridpanel[title="Detalles"]');
    	gridpanelDetalles.setLoading(true);
    	var formAtributos = panelDetalles.down('fieldset[title="Atributos"] form');
		formAtributos.setLoading(true);
    	var gridpanelHistorico = panelDetalles.down('gridpanel[title="Historico"]');
		gridpanelHistorico.setLoading(true);
    	
    	detallesDocumento.getProxy().extraParams.select = nodo;
        detallesDocumento.on(
			{
				load: this.onLoadDetallesDocumento,
				scope: this,
				parent: gridpanelDetalles
			}
		);
		detallesDocumento.load();
		
		var atributosDocumento = Ext.create('FMX.store.Visualizador.AtributosDocumento');
    	atributosDocumento.getProxy().extraParams.select = nodo;
        atributosDocumento.on(
			{
				load: this.onLoadAtributosDocumento,
				scope: this,
				parent: formAtributos
			}
		);
		atributosDocumento.load();
		
		var historicoDocumento = Ext.create('FMX.store.Visualizador.HistoricoDocumento');
    	historicoDocumento.getProxy().extraParams.select = nodo;
        historicoDocumento.on(
			{
				load: this.onLoadHistoricoDocumento,
				scope: this,
				parent: gridpanelHistorico
			}
		);
		historicoDocumento.load();
    },

    onLoadDetallesDocumento: function(detallesDocumento, records, successful, eOpts ) {
    	var gridpanelDetalles = eOpts.parent;
    	gridpanelDetalles.reconfigure(detallesDocumento);
    	gridpanelDetalles.setLoading(false);
    },

    onLoadAtributosDocumento: function(atributosDocumento, records, successful, eOpts ) {
    	var camposDinamicos = [];
		for(var i=0;i<atributosDocumento.getCount();i++){
			var campo = atributosDocumento.getAt(i);
				
			if (campo.get('activo')) {
				camposDinamicos.push({
					xtype : 'campodinamico',
					fortimax_campo: {
						tipo : 'Texto (VarChar)',
						nombre : campo.get('atributo'),
						descripcion : campo.get('descripcion'),
						etiqueta : campo.get('atributo'),
						tamano : 36,
						valor : campo.get('valor'),
						requerido : true,
						editable : campo.get('modificable'),
						lista : '-Ninguna-'
					}
				});
			}
		}
		
		var formAtributos = eOpts.parent;
		formAtributos.add(camposDinamicos);
		formAtributos.setLoading(false);
    },
    
    onLoadHistoricoDocumento: function(historicoDocumento, records, successful, eOpts ) {
    	var gridpanelHistorico = eOpts.parent;
    	gridpanelHistorico.reconfigure(historicoDocumento);
    	gridpanelHistorico.setLoading(false);
    },
    
    onClickToolbarImagenButtonCompartirDocumento: function(boton){
    	if(!boton.up('panelimagen').windowcompartirdocumento){
    		Ext.apply(boton.up('panelimagen'), {
    			windowcompartirdocumento: Ext.create('FMX.view.centro.Visualizador.WindowCompartirDocumento')
    		});
    	}

    	var WindowCompartirDocumento = boton.up('panelimagen').windowcompartirdocumento;
    	
    	WindowCompartirDocumento.parent = boton.up('panelimagen'); //redundancia necesaria.

    	WindowCompartirDocumento.store.on({
    		load: this.onLoadDocumentosCompartidosStore,
    		scope: this,
    		parent: WindowCompartirDocumento
    	});

    	var nodo = boton.up('tabpanelvisualizador').nodo;

    	WindowCompartirDocumento.store.load({params: {select: nodo}});

    	WindowCompartirDocumento.show();
    },
    
    onLoadDocumentosCompartidosStore: function(DocumentosCompartidosStore, records, successful, eOpts){
    	if(successful){
    		var window = eOpts.parent;
    		window.down('label[accion=NombreDocumento]').setText(records[0].get('nombre_documento'),false);
    		var checkboxCompartir = window.down('checkboxfield[accion=CompartirDocumento]');
    		checkboxCompartir.setValue(records[0].get('compartir')=='S');
    		if(DocumentosCompartidosStore.getCount()>0){
    			if(records[0].get('compartir')=='N'){
    				checkboxCompartir.fireEvent('change', checkboxCompartir, false);
    				window.down('label[accion=EstatusDocumento]').setText('No compartido.');
    				window.down('textareafield[accion=MostrarLiga]').setValue('Para obtener liga de documento habilite compartir, configure y guarde');
    				window.down('datefield[accion=FechaDeVencimiento]').setValue(new Date());
    				window.down('spinnerfield[accion=HoraDeVencimiento]').setValue('23:59');
    			}else{
    				var fecha = new Date(records[0].get('dateExp'));
    				var hora = records[0].get('houreExp');
    				this.setTextoInformativoWindowCompartirDocumento(window, fecha, hora);
    				window.down('label[accion=EstatusDocumento]').setText('Compartido');
    				window.down('datefield[accion=FechaDeVencimiento]').setValue(fecha);
    				window.down('spinnerfield[accion=HoraDeVencimiento]').setValue(records[0].get('houreExp'));
    				window.down('checkboxfield[accion=PermitirDescargarZip]').setValue((records[0].get('ligaPermisoBajar'))=='S');
    				
    				var url = '\n' + basePath + "jsp/entregaDocumento.jsp?" + "t=" + records[0].get('token');

    				window.down('textareafield[accion=MostrarLiga]').setValue(url);
    				window.down('textareafield[accion=MostrarLiga]').setFieldStyle({
    				     'fontFamily': 'tahoma',
    				     'fontSize': '16px',
    				     'fontWeight': 'bolder',
    				     'text-decoration': 'underline',
    				     'color': '#3366FF'
    				});
    			}
    		}
    	} else {
    		console.log('Datos de documento a compartir no cargados.');
    	}
    },
    
    onClickWindowCompartirDocumentoButtonGuardarCambios: function(boton){
    	var window = boton.up('windowcompartirdocumento');
    	var record = window.store.getAt(0);

    	var fechaOriginal = new Date(record.get('dateExp'));
    	var horaOriginal = record.get('houreExp');
    	var compartirOriginal = record.get('compartir')=='S'?true:false;
    	var descargaZipOriginal = record.get('ligaPermisoBajar')=='S'?true:false;
    	
    	var fechaActual = window.down('datefield[accion=FechaDeVencimiento]').getValue();
    	var horaActual = window.down('spinnerfield[accion=HoraDeVencimiento]').getValue();
		var compartirActual = window.down('checkboxfield[accion=CompartirDocumento]').getValue();
		var descargaZipActual = window.down('checkboxfield[accion=PermitirDescargarZip]').getValue();

		if((String(fechaActual)!=String(fechaOriginal))||(horaActual!=horaOriginal)||(compartirActual!=compartirOriginal)||(descargaZipActual!=descargaZipOriginal)){
			var panelImagen = boton.up('windowcompartirdocumento').parent;
			var window = boton.up('windowcompartirdocumento');

			var fechaVencimiento = window.down('datefield[accion=FechaDeVencimiento]').getValue();

			var datos = Ext.create('FMX.model.Visualizador.DocumentoCompartido',{
				compartir: window.down('checkboxfield[accion=CompartirDocumento]').getValue()?'S':'N',
				houreExp : window.down('spinnerfield[accion=HoraDeVencimiento]').getValue(),
				dateExp: fechaVencimiento.getFullYear().toString()+'/'+(fechaVencimiento.getMonth()+1).toString()+'/'+fechaVencimiento.getDate(),
				ligaPermisoBajar: window.down('checkboxfield[accion=PermitirDescargarZip]').getValue()?'S':'N'
			});
			datos = Ext.JSON.encode(datos.data);

			Ext.Ajax.request({  
				url: rutaServletCompartirDocumento,  
				method: 'POST',  
				success: function(exito){
					if(exito){
						console.log('Cambios guardados en Liga de Documento Compartido.');
						Ext.Msg.show({
		    				title:'Cambios Guardados',
		    				msg: 'Liga de Documento compartido actualizada.',
		    				buttons: Ext.Msg.OK,
		    				icon: Ext.Msg.INFO
		    			});
						window.store.load({
							params: {
								select: panelImagen.up('tabpanelvisualizador').nodo
							}
						});
					}
				},   
				timeout: 10000,
				params: {
					action: 'modificaCompartir',
					select: panelImagen.up('tabpanelvisualizador').nodo,
					datos: datos
				}
			});
		}
    },

    onClickWindowCompartirDocumentoButtonCopiarLiga: function(boton, e, eOpts) {
    	var liga = boton.up('windowcompartirdocumento').down('textareafield[accion=MostrarLiga]').getValue();
    	if (window.clipboardData) { 
    		window.clipboardData.clearData(); 
    		window.clipboardData.setData("Text", liga);
    		//Ext.example.msg("Copiado","URL copiada"); TODO:Anuncio tipo scroll 
    	} 
    },
    
    onChangeWindowCompartirDocumentoCheckboxCompartirDocumento: function(checkbox, e, eOpts){
    	var fieldset = checkbox.up('windowcompartirdocumento').down('fieldset[accion=PropiedadesLiga]');
    	fieldset.setDisabled(!checkbox.getValue());
    	if(!checkbox.getValue()){
    		checkbox.up('windowcompartirdocumento').down('datefield[accion=FechaDeVencimiento]').setValue(new Date());
    		checkbox.up('windowcompartirdocumento').down('spinnerfield[accion=HoraDeVencimiento]').setValue('23:59');
    	}
    },
    
    onChangeWindowCompartirDocumentoDatefieldFechaDeVencimiento: function(datefield, e, eOpts){
    	var window = datefield.up('windowcompartirdocumento');
    	var hora = window.down('spinnerfield[accion=HoraDeVencimiento]').getValue();
		var fecha = datefield.getValue();
		this.setTextoInformativoWindowCompartirDocumento(window, fecha, hora);
    },
    
    onChangeWindowCompartirDocumentoSpinnerfieldHoraDeVencimiento: function(spinnerfield, e, eOpts){
    	var window = spinnerfield.up('windowcompartirdocumento');
    	var fecha = new Date(window.down('datefield[accion=FechaDeVencimiento]').getValue());
		var hora = spinnerfield.getValue();
		this.setTextoInformativoWindowCompartirDocumento(window, fecha, hora);
    },
    
    onSpinWindowCompartirDocumentoSpinnerfieldHoraDeVencimiento: function(spinnerfield, direction, eOpts){
    	var horaActual = spinnerfield.getValue().split(':');
    	var hr = parseInt(horaActual[0]);
    	var min = parseInt(horaActual[1]);
    	if(isNaN(hr)||isNaN(min)){
    		hr = 12;
    		min = 0;
    	}else{
    		if(hr<0||hr>23){
    			hr = 0;
    		}
    		if(min<0||min>59){
    			min = 0;
    		}
    		if(direction=='up'){
        		min = min + 30;
        		if(min>59){
        			hr ++;
        			if(hr>23)
        				hr = 0;
        			min = min%60;
        		}
        	}
        	else{
        		min = min - 30;
        		if(min<0){
        			hr --;
        			if(hr<0)
        				hr = 23;
        			min = -min;
        		}
        	}
    	}
    	var horaNueva = (hr<10?('0'+hr):hr) + ':' + (min<10?('0'+min):min);
    	spinnerfield.setValue(horaNueva);
    },
    
    onBeforeCloseWindowCompartirDocumento: function(window, e, eOpts){
    	var record = window.store.getAt(0);

    	var fechaOriginal = new Date(record.get('dateExp'));
    	var horaOriginal = record.get('houreExp');
    	var compartirOriginal = record.get('compartir')=='S'?true:false;
    	var descargaZipOriginal = record.get('ligaPermisoBajar')=='S'?true:false;

    	var fechaActual = new Date(window.down('datefield[accion=FechaDeVencimiento]').getValue());
    	var horaActual = window.down('spinnerfield[accion=HoraDeVencimiento]').getValue();
    	var compartirActual = window.down('checkboxfield[accion=CompartirDocumento]').getValue();
    	var descargaZipActual = window.down('checkboxfield[accion=PermitirDescargarZip]').getValue();

    	if((String(fechaActual)!=String(fechaOriginal))||(horaActual!=horaOriginal)||(compartirActual!=compartirOriginal)||(descargaZipActual!=descargaZipOriginal)){
    		if(!compartirActual && (compartirActual==compartirOriginal)){
    			//No se muestre nada si checkbox de compartir queda igual en false aun y cuando haya otros cambios.
    		} else {
    			Ext.Msg.show({
    				title:'Guardar cambios',
    				msg: '¿Desea guardar cambios antes de salir?',
    				buttons: Ext.Msg.YESNO,
    				icon: Ext.Msg.QUESTION,
    				fn: function(buton){
    					if(buton=='yes'){
    						window.down('button[accion=GuardarCambios]').fireEvent('click', window.down('button[accion=GuardarCambios]'));
    					}
    				}
    			});
    		}
    	}
    },
    
    setTextoInformativoWindowCompartirDocumento: function(window, fecha, hora){
    	var textosemana = FMX.utils.Visualizador.Utils.textosemana;
    	var textomes = FMX.utils.Visualizador.Utils.textomes;
    	var ligaDate = new Date(fecha);
    	var horaLiga = String(hora).split(':');
    	ligaDate.setHours(horaLiga[0], horaLiga[1]);
    	var currentDate = new Date();
    	if(currentDate.getTime()<ligaDate.getTime()){
    		window.down('label[accion=TextoInformativo]').setText('Compartir hasta el <font class="negritas">' +
    				textosemana[fecha.getDay()] + ' ' + fecha.getDate() + ' de ' +
    				textomes[fecha.getMonth()] + ' del ' +
    				fecha.getFullYear() + ' a las ' +
    				hora + ' hrs. </font>', false);
    	} else{
    		window.down('label[accion=TextoInformativo]').setText('FECHA Y HORA PASADAS.');
    	}
    },
    
    onClickWindowAgregarImagenButtonAgregarFila: function(boton, e, eOpts){
//    	var copia = boton.up('windowagregarimagen').down('filefield[accion=SeleccionaImagen]').cloneConfig();
//    	boton.up('windowagregarimagen').down('form[accion=FormAreaCamposArchivo]').add(copia);
    	var campoArchivo = Ext.create('FMX.view.Utils.CampoDinamico', {
//			xtype : 'widget.campodinamico',
			fortimax_campo: {
				tipo : 'Archivo',
				nombre : 'file',//para el submit
				descripcion : 'tooltip',
				etiqueta : 'Selecciona Imagen',
				tamano : 1000,//longitud de la cadena a enviar
				//el nombre "tamano" DEBE cambiarse
				valor : '',
				requerido : false,
				editable : true,
				lista : '-Ninguna-' //TODO: Cambiar a null
			},
			accion: 'SeleccionaImagen',
			labelWidth: 150,
			width: 400,
			margin: '15 0 0 15'
		});
    	boton.up('windowagregarimagen').down('form[accion=FormAreaCamposArchivo]').add(campoArchivo);
    },
    
    onClickWindowAgregarImagenButtonEnviarImagenes: function(boton, e, eOpts){
    	var nodo = boton.up('windowagregarimagen').nodo;
    	var nombreDocumento = boton.up('windowagregarimagen').nombreDocumento;
    	var form = boton.up('windowagregarimagen').down('form[accion=FormAreaCamposArchivo]').getForm();
    	if(form.isValid()){
    		form.submit({
    			url: rutaServletSubirImagenes+'?select='+nodo+'&redirect=true',
    			waitMsg: 'Enviando imágenes...',
    			success: function(fp, o) {
    				Ext.Msg.show({
    		            title: 'Éxito',
    		            msg: 'Imágenes agregadas al documento: ' + nombreDocumento,
    		            minWidth: 200,
    		            modal: true,
    		            icon: Ext.Msg.INFO,
    		            buttons: Ext.Msg.OK
    		        });
    			}
    		});
    	}
    }
});

//'Privilegio',
//'TextoOCR',
//'Plantilla',
//'DatoGeneral',
//'CampoPlantilla',
//'AtributoDocumento',
//'VersionHistoricoDocumento'