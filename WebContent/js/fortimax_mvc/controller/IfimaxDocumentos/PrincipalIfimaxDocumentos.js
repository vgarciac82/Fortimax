Ext.define('FMX.controller.IfimaxDocumentos.PrincipalIfimaxDocumentos', {
	extend: 'Ext.app.Controller',

	models: ['IfimaxDocumentos.Documento',
	         'IfimaxDocumentos.MiniaturaOriginal',
	         'IfimaxDocumentos.DetalleElemento',
	         'IfimaxDocumentos.Imagen'],
	         
	stores: ['IfimaxDocumentos.Documentos',
	         'IfimaxDocumentos.MiniaturasOriginales',
	         'IfimaxDocumentos.ArbolExpedientes',
	         'IfimaxDocumentos.DetallesElemento',
	         'IfimaxDocumentos.ImagenesLocales',
	         'IfimaxDocumentos.ImagenesTotales'],
	         
	views: 	['IfimaxDocumentos.IfimaxDocumentosViewport',  	 
	       	 'IfimaxDocumentos.FieldSetFilaDocumento',
	       	 'IfimaxDocumentos.FieldSetImagenDocumento',
	       	 'IfimaxDocumentos.FieldSetTextoDocumento',
	       	 'IfimaxDocumentos.ButtonAgregar',
	       	 'IfimaxDocumentos.FormPanel',
	       	 'IfimaxDocumentos.DataViewPreview',
	       	 'IfimaxDocumentos.PanelPreview',
	       	 'IfimaxDocumentos.WindowPreview'],
    
//    refs: [
//           {
//           ref: 'loginButton',
//           selector: 'loginform button[action=login]'
//           }
//       ],
    
    init: function() {
    	this.control({
    		'tabPanelIfimaxDocumentos': {
    			afterrender: this.afterRenderTabPanelIfimaxDocumentos
    		},
    		'button[accion=AgregarContenidoDoc]':{
    			click: this.onClickButtonAgregarContenidoDoc
    		},
    		'button[accion=GuardarCambios]':{
    			click: this.onClickButtonGuardarCambios
    		},
    		'button[accion=CancelarCambios]':{
    			click: this.onClickButtonCancelarCambios
    		}, 
    		'button[accion=verImagenAnterior]':{
    			click: this.onClickButtonVerImagenAnterior
    		},
    		'button[accion=verImagenSiguiente]':{
    			click: this.onClickButtonVerImagenSiguiente
    		},
    		'treepanel[accion=TreePanelExpedientes]':{
    			itemclick: this.onItemClickTreePanelExpedientes
    		},
    		'button[accion=EliminarMiniaturas]':{
    			click: this.onClickButtonEliminarMiniaturas
    		},
    		'button[accion=AgregarMiniaturas]':{
    			click: this.onClickButtonAgregarMiniaturas
    		},
    		'button[accion=CancelarCambiosMiniaturas]':{
    			click: this.onClickButtonCancelarCambiosMiniaturas
    		},
    		'filefield[accion=SeleccionarImagenDeDisco]':{
    			change: this.onChangeFileFieldSeleccionarImagenDeDisco
    		}
    	});
    },

    afterRenderTabPanelIfimaxDocumentos: function(tabpanel){
    	var me = this;
    	var documentosStore = Ext.create('FMX.store.IfimaxDocumentos.Documentos');
    	documentosStore.on({
    		load: function(store, records, successful, operation, eOpts){
    			records.forEach(function(record, idx, records) {
    				var filaDocumento = Ext.create('FMX.view.IfimaxDocumentos.FieldSetFilaDocumento');
    				var imagenDocumento = Ext.create('FMX.view.IfimaxDocumentos.FieldSetImagenDocumento');
    				var textoDocumento = Ext.create('FMX.view.IfimaxDocumentos.FieldSetTextoDocumento');
    				var botonSubir = Ext.create('FMX.view.IfimaxDocumentos.ButtonAgregar');
    				
    				textoDocumento.down('label[accion=labelDescripcion]').setText(record.data.descripcion);
    				
    				filaDocumento.setTitle(record.data.nombre);
    				filaDocumento.add(imagenDocumento);
    				filaDocumento.add(textoDocumento);
    				filaDocumento.add(botonSubir);
//    				filaDocumento.add(treePanelDoc);
    				
    				tabpanel.down('panel[accion=panelDocumentos]').add(filaDocumento);
    				
    				filaDocumento.nodo = record.data.nodo;
    			});
    			me.cargaImagenes(records);
    		}
    	});
    	
    	documentosStore.load();
    },
    
    cargaImagenes: function(recordsDocumentos){
    	//Por cada nodo  de documento obtenemos sus miniaturas y usamos la primera
    	var miniaturasOriginales = Ext.create('FMX.store.IfimaxDocumentos.MiniaturasOriginales');
    	miniaturasOriginales.on({
    		load: function(store, records, successful, operation, eOpts){
    			
    			var i = records[0].data.id.lastIndexOf('P');//TODO Se elimina del nodo la página
    			var matriculaDocumento = records[0].data.id.substring(0,i);

    			var imagenesTotales = Ext.create('FMX.store.IfimaxDocumentos.ImagenesTotales',{
    				id: 'storeImagenesTotales-'+matriculaDocumento
    			});
    			
    			for(var k=0; k<records.length; k++){
    				imagenesTotales.add({src:records[k].data.imagen, caption:'', position:k});
    			}

//    			console.log(imagenesTotales);
    			var field = Ext.ComponentQuery.query('fieldset[nodo='+matriculaDocumento+']')[0];
    			field.down('image[accion=imageDocumento]').setSrc(imagenesTotales.getAt(0).data.src);
    		}
    	});
    	
    	recordsDocumentos.forEach(function(record, idx, records) {
    		miniaturasOriginales.load({
    			params: {select: record.data.nodo}
    		});
    	});
    	
    	
    },
    
    onClickButtonAgregarContenidoDoc: function(b){
    	var arregloDeArchivos = new Array();
    	var dataView = Ext.create('FMX.view.IfimaxDocumentos.DataViewPreview', {
    		id: 'miniaturas' // se requiere para que funcione el style //TODO
    	});
    	
    	var matriculaDocumento = b.up('fieldset[accion=FilaDocumento]').nodo;
    	var imagenesLocales = Ext.data.StoreManager.lookup('storeImagenesLocales'+matriculaDocumento);
    	if(!imagenesLocales){
    		imagenesLocales = Ext.create('FMX.store.IfimaxDocumentos.ImagenesLocales',{
    			id: 'storeImagenesLocales-'+matriculaDocumento
    		});    		
    	}
    	console.log(imagenesLocales);
    	dataView.store = imagenesLocales;
    	
    	var panel = Ext.create('FMX.view.IfimaxDocumentos.PanelPreview');
    	panel.add(dataView);
    	
    	//TODO Validar que no haya otra ventana abierta
    	var window = Ext.create('FMX.view.IfimaxDocumentos.WindowPreview');
    	window.add(panel);
    	window.nodo = matriculaDocumento;
    	window.show();
    },
    
    onClickButtonGuardarCambios: function(){
    	alert('Subir cambios');
    },
    
    onClickButtonCancelarCambios: function(){
    	alert('Cancelar cambios');
    },
    
    onItemClickTreePanelExpedientes: function( treepanel, record, item, index, e, eOpts ){
    	var grid = treepanel.up('tabPanelIfimaxDocumentos').down('grid[accion=detallesElementoArbol]');
    	
    	grid.getStore().getProxy().extraParams.select = record.data.nodo;
    	
    	grid.getStore().load();
    },
    
    onClickButtonVerImagenAnterior: function(b, e, eOpts){
    	var matriculaDocumento = b.up('fieldset[accion=FilaDocumento]').nodo;
    	var imagenesTotales = Ext.data.StoreManager.lookup('storeImagenesTotales-'+matriculaDocumento);
    	if(imagenesTotales){
    		var posicionActual = b.up('fieldset').down('image').posicion;
    		if(imagenesTotales.getAt(posicionActual-1)){
    			b.up('fieldset').down('image').setSrc(imagenesTotales.getAt(posicionActual-1).data.src);
    			b.up('fieldset').down('image').posicion = posicionActual-1;
    		}
    	}
    },
    
    onClickButtonVerImagenSiguiente: function(b, e, eOpts){
    	var matriculaDocumento = b.up('fieldset[accion=FilaDocumento]').nodo;
    	var imagenesTotales = Ext.data.StoreManager.lookup('storeImagenesTotales-'+matriculaDocumento);
    	if(imagenesTotales){
    		var posicionActual = b.up('fieldset').down('image').posicion;
    		if(imagenesTotales.getAt(posicionActual+1)){
    			b.up('fieldset').down('image').setSrc(imagenesTotales.getAt(posicionActual+1).data.src);
    			b.up('fieldset').down('image').posicion = posicionActual+1;
    		}
    	}
    },
    
    onClickButtonCancelarCambiosMiniaturas: function(b, e, eOpts){
    	console.log('Se borra contenido de arregloDeArchivos'); 
    	//TODO arregloDeArchivos = null;
    	b.up('panel').up('window').close();
    },
    
    onClickButtonAgregarMiniaturas: function(b, e, eOpts){
		console.log('Se envía a FormPanel arregloDeArchivos');
		for (var i = 0; i < arregloDeArchivos.length; i++) {
			FMX.view.IfimaxDocumentos.FormPanel.add(arregloDeArchivos[i]);
		}
		console.log(FMX.view.IfimaxDocumentos.FormPanel.getForm());
		FMX.view.IfimaxDocumentos.FormPanel.submit({
			url: 'http://localhost:8080/Fortimax/Admin/OperacionesServlet'
//				waitMsg: 'Uploading your photo...',
//				success: function(fp, o) {
//				Ext.Msg.alert('Success', 'Your photo "' + o.result.file + '" has been uploaded.');
//				}
		});
    },
    
    onClickButtonEliminarMiniaturas: function(b, e, eOpts){
    	var selection = b.up('panel').down('dataview').getSelectionModel().getSelection();
    	var imagenesLocales = b.up('panel').down('dataview').getStore();
    	for(var i=0; i<selection.length;i++){
    		imagenesLocales.remove(selection[i]);
    		//TODO Eliminar tambien de imagenestotales
    	}
    },
    
    onChangeFileFieldSeleccionarImagenDeDisco: function(field, value, eOpts){
    	//TODO arregloDeArchivos.push(filefield.cloneConfig());
		var file = field.extractFileInput();
//		var field = Ext.ComponentQuery.query('fieldset[nodo='+'USR_GRALES_G1C1D10'+']')[0];
		var reader = new FileReader();
		var imagenesLocales = field.up('panel').down('dataview').getStore();
		reader.onload = function (e) {
			var conteoLocales = imagenesLocales.getCount();
			imagenesLocales.add({src:e.target.result, caption:conteoLocales, position:conteoLocales});
			
	    	var matriculaDocumento = field.up('window').nodo;
			var imagenesTotales = Ext.data.StoreManager.lookup('storeImagenesTotales-'+matriculaDocumento);
			imagenesTotales.add({src:e.target.result, caption:conteoLocales, position:imagenesTotales.getCount()+conteoLocales});
//			field.down('image[accion=imageDocumento]').setSrc(e.target.result);
		};
		reader.readAsDataURL(file.files[0]);
    }
});