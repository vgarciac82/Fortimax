//Definimos un namespace para usar funciones internas desde afuera
var scannerAppExtjs = {};

Ext.onReady(function(){
//	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	/**
	 * VARIABLES
	 */
	var alturaMiniaturasDefault = 65;
	var anchoMiniaturasDefault = 65;
	var indiceImagenSeleccionada = 0;
	var rowDeleted = -1;

	/**
	 * MODELS
	 */
	Ext.define('Image', {
		extend: 'Ext.data.Model',
		fields: [
		         { name:'src', type:'string' },
		         { name:'height', type:'int' },
		         { name:'width', type:'int' },
		         { name:'altoOriginal', type:'int' },
		         { name:'anchoOriginal', type:'int' }
		         ]
	});
	
	/**
	 * STORES
	 */
	

	var imagesStore = Ext.create('Ext.data.Store', {
//	    id:'imagesStore',
	    model: 'Image'
	});

	/**
	 * OBJECTS
	 */
	var imageTpl = new Ext.XTemplate(
			'<tpl for=".">',
			'<div style="margin-bottom: 10px;" class="miniatura">',
			'<img src="{src}" height={height} width={width}/>',
			'</div>',
			'</tpl>'
	);
	var drawComponent = Ext.create('Ext.draw.Component', {
		viewBox: false,
		flex:5,
		margin: '1 2 2 2',
		height: 390
	});
	var dataView = Ext.create('Ext.view.View', {
//		store: Ext.data.StoreManager.lookup('imagesStore'),
		margin: '2 2 5 6',
		store: imagesStore,
		tpl: imageTpl,
		itemSelector : 'div.miniatura',
		emptyText: 'Cargue imágenes desde dispositivo.',
		autoScroll: true,
        listeners: {
        	itemclick: function(dataView, record, item, index, eOpts){
        		indiceImagenSeleccionada = index;
        		drawComponent.surface.removeAll(true);

        		var sprite = drawComponent.surface.add({
            		type: 'image',
            		src: record.data.src,
            		width: record.data.anchoOriginal,
            		height: record.data.altoOriginal,
            		draggable: true
            	});
            	
            	sprite.show(true);   
            }
        }
	});
	var applet = document.getElementById('scannerAppletID');
	Ext.create('Ext.panel.Panel', {
	    layout: 'hbox',
	    height: 440,
	    autoScroll: true,
	    margin: 1,
	    items:[drawComponent, {
	    	xtype: 'panel',
	    	flex: 1,
	    	layout: 'fit',
	    	margin: 1,
	    	height: 390,
	    	items: [dataView]
	    }],
	    tbar: [{
	    	xtype: 'button',
	    	scale   : 'medium',
	    	iconCls: 'btnScan',
	        handler: function() {
	        	applet.doButtonMScan();
	        }
	    },{
	    	xtype: 'button',
	    	scale   : 'medium',
	    	iconCls: 'btnFlipHor',
	        handler: function() {
	        	if(dataView.getSelectedNodes()[0]){
	        		applet.doButtonFlipHor();
	        	} else {
	        		Ext.Msg.show({
	        			title:'Advertencia',
	        			buttons: Ext.Msg.OK,
	        			icon: Ext.Msg.WARNING,
	        			msg: 'Seleccione una miniatura.'
	        		});
	        	}
	        }
	    },{
	    	xtype: 'button',
	    	scale   : 'medium',
	    	iconCls: 'btnFlipVer',
	        handler: function() {
	        	if(dataView.getSelectedNodes()[0]){
	        		applet.doButtonFlipVer();
	        	} else {
	        		Ext.Msg.show({
	        			title:'Advertencia',
	        			buttons: Ext.Msg.OK,
	        			icon: Ext.Msg.WARNING,
	        			msg: 'Seleccione una miniatura.'
	        		});
	        	}
	        }
	    },{
	    	xtype: 'button',
	    	scale   : 'medium',
	    	iconCls: 'btnRotateLeft',
	        handler: function() {
	        	if(dataView.getSelectedNodes()[0]){
	        		applet.doButtonRotateLeft();
	        	} else {
	        		Ext.Msg.show({
	        			title:'Advertencia',
	        			buttons: Ext.Msg.OK,
	        			icon: Ext.Msg.WARNING,
	        			msg: 'Seleccione una miniatura.'
	        		});
	        	}
	        }
	    },{
	    	xtype: 'button',
	    	scale   : 'medium',
	    	iconCls: 'btnRotateRight',
	        handler: function() {
	        	if(dataView.getSelectedNodes()[0]){
	        		applet.doButtonRotateRight();
	        	} else {
	        		Ext.Msg.show({
	        			title:'Advertencia',
	        			buttons: Ext.Msg.OK,
	        			icon: Ext.Msg.WARNING,
	        			msg: 'Seleccione una miniatura.'
	        		});
	        	}
	        }
	    },{
	    	xtype: 'button',
	    	scale   : 'medium',
	    	iconCls: 'btnRemove',
	    	scale   : 'medium',
	        handler: function() {
	        	if(dataView.getSelectedNodes()[0]){
	        		applet.doButtonRemove();
	        	} else {
	        		Ext.Msg.show({
	        			title:'Advertencia',
	        			buttons: Ext.Msg.OK,
	        			icon: Ext.Msg.WARNING,
	        			msg: 'Seleccione una miniatura.'
	        		});
	        	}
	        }
	    }
//	    {
//	    	xtype: 'button',
//	    	text: 'scan',
//	        handler: function() {
//	        	applet.doButtonScan();
//	        }
//	    }
	    ,'->',{
	    	xtype: 'button',
	    	scale   : 'medium',
	    	iconCls: 'btnSend',
	        handler: function() {
	        	if(dataView.getStore().getCount()>0){
	        		var respuesta = applet.doButtonSend();
	        		if(respuesta[0]=='true'){
	        			Ext.Msg.show({
	        				title:'Envío exitoso',
	        				buttons: Ext.Msg.OK,
	        				icon: Ext.Msg.INFO,
	        				msg: respuesta[1]
	        			});
	        			drawComponent.surface.removeAll(true);
	        			dataView.getStore().removeAll();
	        		} else{
	        			Ext.Msg.show({
	        				title:'Advertencia',
	        				buttons: Ext.Msg.OK,
	        				icon: Ext.Msg.WARNING,
	        				msg: respuesta[1]
	        			});
	        		}
	        	}else{
	        		Ext.Msg.show({
        				title:'Advertencia',
        				buttons: Ext.Msg.OK,
        				icon: Ext.Msg.WARNING,
        				msg: 'No hay imágenes para enviar'
        			});
	        	}
	        }
	    }]
	    ,renderTo: Ext.getBody()
	});
	
	/**
	 * FUNCTIONS
	 */
	scannerAppExtjs.addRow = function(tableID, imageSRC, imageWidth, imageHeight) {
		var data = [{ 
			src: imageSRC, 
			height:alturaMiniaturasDefault, 
			width: anchoMiniaturasDefault,
			altoOriginal: imageHeight,
			anchoOriginal: imageWidth
		}];
		dataView.getStore().loadData(data, true);
	};

	scannerAppExtjs.delRow = function() {
		if(dataView.getSelectedNodes()[0]){
			rowDeleted = dataView.getSelectedNodes()[0].viewIndex;
			dataView.getStore().removeAt(rowDeleted);
			drawComponent.surface.removeAll(true);
			return true; //Debe retornarse para el applet.
		}
	};

	scannerAppExtjs.getRowDeleted = function(){
		return rowDeleted;
	};

//	scannerAppExtjs.showImage = function(imgIdx, imgSRC, imgWidth, imgHeight) {
//		drawComponent.surface.removeAll(true);
//		var sprite = drawComponent.surface.add({
//    		type: 'image',
//    		src: imgSRC,
//    		width: imgWidth,
//    		height: imgHeight,
//    		draggable: true
//    	});
//    	
//    	sprite.show(true); 
//	};

	scannerAppExtjs.updateImage = function(imgIdx, imgSRC, imgWidth, imgHeight) {
		dataView.getStore().getAt(imgIdx).set('src', imgSRC);
		dataView.getStore().getAt(imgIdx).set('altoOriginal', imgHeight);
		dataView.getStore().getAt(imgIdx).set('anchoOriginal', imgWidth);
		dataView.fireEvent('itemclick', dataView, dataView.getStore().getAt(imgIdx), dataView.getNode(imgIdx), imgIdx);
	};

	scannerAppExtjs.getIndexImgSelected = function(){
		return indiceImagenSeleccionada;
	};
	
	var selectedSource = "";
	function getSelectedSource(){
		return selectedSource;
	}

	function setSelectedSource(s){
		selectedSource = s;
	}

});