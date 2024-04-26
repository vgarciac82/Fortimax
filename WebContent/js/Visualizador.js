Ext.onReady(function(){
	/*
	 * Variables
	 */
//	CAMBIAR Viewport por un Panel para ser contenido por el Viewport principal (no puede haber viewport dentro de otro)
	Ext.getBody().mask('Espere por favor...', 'x-mask-loading'); //Obtener mask del panel padre del visualizador
	var pantalla=Ext.getBody().getViewSize(); //Obtener size del panel padre del visualizador
	
	var privilegios = Ext.JSON.decode(jsonPrivilegios)

	//Crear una clase estática para var de entorno y guardar estos configs para accederlas así:
	//FMX.ClaseConVariables.variacionZoom;
	var miniaturasPorCarga=10;
	var variacionZoom=1;
	var actionStoreBuffer='getMiniaturas';
	//var select='USR_GRALES_G1C1D1';
	var variacionTbar=70;
	var AnchoMiniaturas=115;
	var mover=false;
	var dibujar=false;

	document.oncontextmenu=inhabilitar; //TODO: VERIFICAR si entra en las del tipo statics
	var xG; //Especificar más los nombres
	var yG;
	var xF=0;
	var yF=0;
	var xFtmp=0;
	var yFtmp=0;
	var anchoImg;
	var altoImg;
	var anchoR=0;
	var altoR=0;
	var xR=0;
	var yR=0;
	var tmpTexto="";
	var divEliminar;
	var totalPaginas;
	var tabOcr=false;
	var tmpIndex=0;
	var altoPanel;
	
	Ext.tip.QuickTipManager.init(); //VERFICAR QUEHACE Y SI ES NECESARIO
	 
/*
 *  MODELOS
 */

	Ext.define('Atributo', {
		extend: 'Ext.data.Model',
		fields: [
		         {name: 'id_atributo',		type:'int'},
		         {name: 'valor_atributo', 	type:'string'},
		         {name: 'modificable', 		type:'boolean'}
		         ]
	});
	
	Ext.define('ModeloMiniaturas', {
		extend: 'Ext.data.Model',
		fields: [
		         {type: 'string', name: 'pagina'},
		         {type: 'string', name: 'numero_pagina'},
		         {type: 'string', name: 'ancho'},
		         {type: 'string', name: 'alto'},
		         {type: 'string', name: 'nombre'},
		         {type: 'string', name: 'calidad'},
		         {type: 'string', name: 'imagen'},
		         {type: 'string', name: 'imagenPagina'}
		         ],
		         idProperty:'ID'
	});

	Ext.define('PrivilegiosModel', {
		extend: 'Ext.data.Model',
		fields: [
		         {name: 'privilegio', type: 'string'},
		         {name: 'descripcion', type: 'string'},
		         {name:'seleccionado', type:'string'}
		         ]
	});

	Ext.define('ModeloOCR', {
		extend: 'Ext.data.Model',
		fields: [{type: 'string', name: 'texto'},{type: 'int', name: 'id'}]
	});

	Ext.define('SaveModel', {
		extend: 'Ext.data.Model',
		fields: 
			[
			 {name:'Matricula',type:'string'},
			 {name: 'PlantillaD', type: 'string'}
			 ]
	});

	Ext.define('DGenerales', {
		extend: 'Ext.data.Model',
		fields: 
			[
			 {name:'nombre',type:'string'},
			 {name: 'valor', type: 'string'}
			 ]
	});

	Ext.define('CamposPlantilla', {
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

	Ext.define('atributoDocumentoModel', {
		extend: 'Ext.data.Model',
		fields: [
		         {name:'atributo', type:'string'},
		         {name:'activo', type:'bool'},
		         {name:'valor', type:'string'},
		         {name:'modificable', type:'bool'},
		         {name:'descripcion', type:'string'}
		         ]
	});

	Ext.define('historicoDocumentoModel', {
		extend: 'Ext.data.Model',
		fields: [
		         {name:'Id_Version', type:'int'},
		         {name:'Fecha_generacion', type:'string'},
		         {name:'Nombre_Documento', type:'string'},
		         {name:'Descripcion', type:'string'},
		         {name:'numero_paginas', type:'int'},
		         {name:'tamano', type:'string'},
		         {name:'usuario_generador', type:'string'}
		         ]
	});

/*
 *  STORES
 */
	var StoreOCR = Ext.create('Ext.data.Store', {
		model: 'ModeloOCR',
		proxy: {
			type: 'ajax',
			url: rutaServletA,	//'../JsonPrueba/privilegios.json',
			reader: {
				type: 'json',
				root: 'ocr'
			},
			extraParams: 
			{
				action: "getOCR"
			}
		},
		listeners:{
			load: storeOCRSetValueOnLoad
		},
		autoLoad: false
	});
/*
	var PrivilegiosStore = Ext.create('Ext.data.Store', {
		model: 'PrivilegiosModel',
		proxy: {
			type: 'ajax',
			url: rutaServletP,	//'../JsonPrueba/privilegios.json',
			reader: {
				type: 'json',
				root: 'privilegios'
			},
			extraParams: 
			{
				action: "getPrivilegios",
				select:select
			}
		},
		listeners:{
			load:
				PrivilegiosStoreDisableButtonsOnLoad
		},
		autoLoad: false
	});
*/
	var StoreMiniaturas = Ext.create('Ext.data.Store', {
		model: 'ModeloMiniaturas',
		sortInfo: { field: 'pagina', direction: 'DESC'}, 
		proxy: {

			type: 'ajax',
			url: rutaServletA, //nombre más especifico (nombrar el servlet)
			extraParams:{
				action: 'getMiniaturasI'
			},
			actionMethods: 'POST',
			reader: {
				type: 'json',
				root: 'miniaturas'
			}
		},
		autoLoad:false
	});

	var StoreMiniaturasBuffer = Ext.create('Ext.data.Store', {
		model: 'ModeloMiniaturas',
		sortInfo: { field: 'pagina', direction: 'DESC'}, 
		proxy: {

			type: 'ajax',
			url: rutaServletA,
			extraParams:{
				action: actionStoreBuffer ,
				select:select,
				start:StoreMiniaturas.getCount(),
				limit:miniaturasPorCarga
			},
			actionMethods: 'POST',
			reader: {
				type: 'json',
				root: 'miniaturas'
			}
		},
		listeners:{
			load:StoreMiniaturasBufferCreaSpriteOnLoad
		},
		autoLoad:false
	});
	
	var StoreMiniaturasBuffer2 = Ext.create('Ext.data.Store', {
		model: 'ModeloMiniaturas',
		sortInfo: { field: 'pagina', direction: 'DESC'}, 
		proxy: {

			type: 'ajax',
			url: rutaServletA,
			extraParams:{
				action: actionStoreBuffer ,
				select:select,
				start:StoreMiniaturas.getCount(),
				limit:miniaturasPorCarga
			},
			actionMethods: 'POST',
			reader: {
				type: 'json',
				root: 'miniaturas'
			}
		},
		listeners:{
			load:StoreMiniaturasBuffer2QuitaSpriteOnLoad,
			beforeload: function(){
				Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			}
		},
		autoLoad:false
	});
	
/*
 * STORES DE Detalles
 */
	var storeGeneral = new Ext.data.Store({ 
		model: 'DGenerales', 
		proxy: { 
			type: 'ajax', 
			url: rutaServlet,							
			reader: { 										
				type: 'json', 
				root: 'datos'
			},
			extraParams: 
			{
				action: 'getDGeneral',
				select:select
			} 
		},
		listeners:{
			load: storeGeneralLoadDatosYPrivilegiosOnLoad
		},
		autoLoad:true
	});
	
	var storeCamposPlantilla = new Ext.data.Store({ 
		model: 'CamposPlantilla', 
		proxy: { 
			type: 'ajax', 
			url: rutaServlet,  							
			reader: { 										
				type: 'json', 
				root: 'campos'
			},
			extraParams: 
			{

				action: 'getDatosDoc',
				select:select

			} 
		},
		sorters: [{
			property: 'orden',
			direction:'ASC'
		}],
		listeners:{
			load: storeCamposInterfazPersonalizadaOnLoad
		},
		autoLoad:false
	});
			    
/*
 * Objetos de Detalles
 * 
 */
	var btnEditar=Ext.create('Ext.button.Button',{
		text: 'Editar',
		formBind: false,
		iconCls:'btnEditar',
		handler: btnEditarHandler
	});
	var btnGuardar=Ext.create('Ext.button.Button',{
		text: 'Guardar',
		formBind: true,
		iconCls:'btnGuardar',
		disabled:false,
		handler: btnGuardarHandler
	});
	var btnExportar=Ext.create('Ext.button.Button',{
		text: 'Exportar',
		formBind: false,
		iconCls:'btnExportar',
		disabled:true
	});
	var txtFiltro = Ext.define('Ext.ux.CustomTrigger', {
		id:'txtFiltro',
		extend: 'Ext.form.field.Trigger',
		alias: 'widget.xtext',
		triggerTip: 'Click para filtrar.',
		triggerBaseCls :'x-form-trigger',
		triggerCls:'x-form-clear-trigger',
		width:300,
		emptyText:'Filtrar...',
		onTriggerClick: function() { 
			this.reset(); 
		}
	,
	listeners: {
		change: txtFiltroClearFilterOnChange
	}
	});
	var btnDescargar = new Ext.ux.DownloadButton({
		text: 'Descargar',
		select:select,
		nombreReporte:'Detalles del documento',
		columnaBuscar:null,
		Filtro:'txtFiltro',
		action:'getDoumentInfo',
		icon:'../imagenes/iconos/descargar16.png',
		fortimax:true
	});
	var Grid=new Ext.grid.Panel({
		id:'Grid',
		title:'Detalles',
		cls:'gridDetallesCls',
		width:'100%',
		height:300,
		store:storeGeneral,
		columnLines: true,
		tbar:[btnDescargar,'->',txtFiltro],
		autoScroll:true,
		columns:[
		         {xtype: 'rownumberer', width:25,tdCls: 'numero',width:60,header:'ID',align:'left'},
		         { header: 'Nombre',  dataIndex: 'nombre', flex:1},
		         {header: 'Valor',  dataIndex: 'valor', flex:3}
		         ],
		         listeners:{
		        	 select:gridOpenWindowOnSelect
		         }
	});
 /*
  * Paneles Anotaciones
  * 
  */
	var toolbar=Ext.create('Ext.toolbar.Toolbar',{
		items:[btnExportar,'->',btnEditar,'',btnGuardar]
	});
	var panelForm=Ext.create('Ext.form.FieldSet',{
		title:'',
		cls:'fieldS'
	});

	var panelFormPlantilla=Ext.create('Ext.form.Panel', {
		title: 'Datos de documento',
		id:'frmPlantilla',
		layout:'anchor',
		width: '100%',
		scroll:true,
		autoScroll:true,
		height:pantalla.height-50,
		items:[
		       toolbar, panelForm
		       ]
	});
 /*
  * Objetos
  */
	var imageTpl = new Ext.XTemplate( //REVISAR 
			'<tpl for=".">',
			'<div class="miniatura" id="{numero_pagina}">',
			(!Ext.isIE6? '<img width="53" height="74" src="{imagen}" />' :
			'<div style="width:99px;height:95px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'{imagen}\',sizingMethod=\'scale\')"></div>'),
			'<br /><span>Pagina: {pagina}</span>', //TODO: Cambiar a "posicion_pagina"
			'</div>',
			'</tpl>'
	);
	var visualizadorMinitaturas = Ext.create('Ext.view.View', {
		deferInitialRefresh: false,
		autoScroll  : true,
		store: StoreMiniaturas,
		tpl  : imageTpl,
		id: 'miniaturas', //Este id evita instanciar el view. Tal como txtfiltro (más arriba)
		//agregar initcomponent en cada obj de este tipo (tiene sub objetos)
		itemSelector: 'div.miniatura',
		overItemCls : 'miniatura-hover',
		trackOver: true,
		multiSelect : false,
		autoScroll:true,
		viewConfig: {
			scrollOffset: 0//or 1, or 2 as mentioned above
		},
		listeners:{
			itemclick: visualizadorMinitaturasCreaSpriteOnItemClick
		}

	});
	    //TODO Organizar los botones como en PanelArbolesPrincipal.js
	 	 //es un caso en que se crea el boton con un id
	var btnSiguienteMiniaturas=new Ext.button.Button({
		text:' + '+miniaturasPorCarga,
		iconCls:'btnSiguienteMiniaturas',
		iconAlign:'right', 
		handler: btnSiguienteMiniaturasCargaMiniaturasHandler
	});
	var bbarAvanzar=new Ext.toolbar.Toolbar({
		layout:'fit',
		width   : 500,
		items: [btnSiguienteMiniaturas]
	});
	var slideZoom=new Ext.slider.Single({
		width: 50,
		minValue: -100,
		maxValue: 100,
		labelSeparator:'',
		labelWidth:50,
		labelAlign :'bottom',
		fieldLabel:'Zoom',
		labelCls:'slideZoomLabelCls',
		value: 0,
		listeners: {change: slideZoomAjustaImagenOnChange}
	});
	var slideRotar=new Ext.slider.Single({
		width: 50,
		minValue: -180,
		maxValue: 180,
		labelSeparator:'',
		labelWidth:50,
		labelAlign :'bottom',
		fieldLabel:'Angulo',
		labelCls:'slideRotarLabelCls',
		value: 0,
		listeners: {
			change: slideRotarRotaImagenOnChange
		}
	});
	// Objetos tbar
	var btnEscanear=new Ext.button.Button({
		id:'btnEscanear',
		text:'Escanear',
		scale: 'medium',
		iconAlign: 'top',
		iconCls:'btnEscanearIcon',
		disabled: !privilegios.digitalizar,
		hidden:visualizadorCompartido,
		handler: btnEscanearHandler
	});
	var btnAgregarImagen=new Ext.button.Button({
		id:'btnAgregarImagen',
		text:'Agregar foto',
		scale: 'medium',
		iconAlign: 'top',
		iconCls:'btnAgregarImagenIcon',
		disabled: !privilegios.digitalizar,
		hidden:visualizadorCompartido,
		handler: btnAgregarImagenHandler
	});
	var btnImprimir=new Ext.button.Button({
		id:'btnImprimir',
		text:'Imprimir',
		scale: 'medium',
		iconAlign: 'top',
		iconCls:'btnImprimirIcon',
		disabled: !privilegios.imprimir,
		hidden:visualizadorCompartido,
		handler: btnImprimirHnadler
	});
	var btnInicio=new Ext.button.Button({
		id:'btnInicio',
		scale: 'medium',
		iconAlign: 'top',
		iconCls:'btnInicioIcon',
		handler: btnInicioHandler
	});
	var btnAnterior=new Ext.button.Button({
		id:'btnAnterior',
		scale: 'medium',
		iconAlign: 'top',
		iconCls:'btnAnteriorIcon',
		handler: btnAnteriorHandler
	});
	var btnSiguiente=new Ext.button.Button({
		id:'btnSguiente',
		scale: 'medium',
		iconAlign: 'top',
		iconCls:'btnSiguienteIcon',
		handler: btnSiguienteHandler
	});
	var btnFinal=new Ext.button.Button({
		id:'btnFinal',
		scale: 'medium',
		iconAlign: 'top',
		iconCls:'btnFinalIcon',
		handler: btnFinalHandler
	});
	var txtPagina=new Ext.form.field.Text({
		id:'txtPagina',
		width:30,
		maxLength:4,
		enforceMaxLength:true,
		value:'0',
		regex: /^[0-9]+$/,
		maskRe: /^[0-9]+$/,
		listeners:{
			scope:this,  
			specialkey: txtPaginaOnSpecialKey
		}
	});
	var lblPaginas=new Ext.form.Label({
		html:'de 0 páginas',
		shadow:true
	});
	var btnZoomOut=new Ext.button.Button({
		id:'btnZoomOut',
		scale: 'small',
		iconAlign: 'top',
		iconCls:'btnZoomOutIcon',
		handler: btnZoomOutHandler
	});
	var btnZoomIn=new Ext.button.Button({
		id:'btnZoomIn',
		scale: 'small',
		iconAlign: 'top',
		iconCls:'btnZoomInIcon',
		handler: btnZoomInHandler
	});
	var btnRotarDerecha=new Ext.button.Button({
		id:'btnRotarDerecha',
		scale: 'small',
		iconAlign: 'top',
		iconCls:'btnRotarDerechaIcon',
		handler: btnRotarDerechaHandler
	});
	var btnRotarIzquierda=new Ext.button.Button({
		id:'btnRotarIzquierda',
		scale: 'small',
		iconAlign: 'top',
		iconCls:'btnRotarIzquierdaIcon',
		handler: btnRotarIzquierdaHandler
	});
	var btnAjustar=new Ext.button.Button({
		id:'btnAjustar',
		scale: 'small',
		iconAlign: 'top',
		iconCls:'btnAjustarIcon',
		handler: btnAjustarHandler
	});
	var btnAjustarAncho=new Ext.button.Button({
		id:'btnAjustarAncho',
		scale: 'small',
		iconAlign: 'top',
		iconCls:'btnAjustarAnchoIcon',
		handler: btnAjustarAnchoHandler
	});
	var btnAjustarAlto=new Ext.button.Button({
		id:'btnAjustarAlto',
		scale: 'small',
		iconAlign: 'top',
		iconCls:'btnAjustarAltoIcon',
		handler: btnAjustarAltoHandler
	});
	var btnVersionNavegador=new Ext.button.Button({
		hidden: true,
		id:'btnVersionNavegador',
		scale: 'medium',
		iconAlign: 'right',
		text:'Vista clásica',
		iconCls:'btnVersionNavegadorIcon',
		handler: btnVersionNavegadorHandler
	});
	var editorTexto=new Ext.form.HtmlEditor({
		id:'editorTexto',
		width:pantalla.width-AnchoMiniaturas,
		height:pantalla.height-30

	});
	var btnGuardarOCR=Ext.create('Ext.button.Button',{
		text: 'Guardar',
		formBind: false,
		iconCls:'btnGuardarOCRIcon',
		handler: btnGuardarOCRHandler
	});
	var btnEliminarPagina=new Ext.button.Button({
		id:'btnEliminarPagina',
		scale: 'medium',
		iconAlign: 'top',
		disabled: !privilegios.eliminar,
		hidden:visualizadorCompartido,
		iconCls:'btnEliminarPaginaIcon',
		handler: btnEliminarPaginaHandler
	});

	 /*
	  * Paneles
	  */

	var panImg=Ext.create('Ext.draw.Component', {
	    id: 'rotateImg',
	    viewBox: true,
	    autoSize: true,
	    style:'background-color:#f2f1f0;',
	    draggable:true,
	    width:'100%',
	    cls:'panImgCls',
	    height:pantalla.height-50,
	    listeners:{
	    		afterrender: panImgAfterRender
	    }
	});
	var toolbarVisor=new Ext.toolbar.Toolbar({
		width:'100%',
		height:50,
		border:0,
		hidden:externo,
		cls:'toolbarVisorCls',
	    items: [btnEscanear,'-',btnAgregarImagen,'-',btnImprimir
	        	,'-',btnInicio,btnAnterior,'-',txtPagina,lblPaginas,'-',btnSiguiente,btnFinal,'-',
	        	btnZoomOut,slideZoom,btnZoomIn,'-',btnRotarIzquierda,slideRotar,btnRotarDerecha,
	        	'-',btnAjustar,btnAjustarAncho,btnAjustarAlto,btnVersionNavegador,'->',btnEliminarPagina]
	});
	 var panelVisor=new Ext.panel.Panel({
	 		id:'panelVisor',
	        collapsible: false,
	        region: 'center',
	        cls:'panelVisor',
	        autoScroll:true,
	        title: '<font class="textoTabs">Imagen</font>',
	        tbar:[toolbarVisor],
	        hidden:externo,
	        items:[panImg]
		});
	 var panelArchivo= new Ext.panel.Panel({
		id:'panel_archivo',
	 	title: 'Archivo',
	 	hidden:!externo,
	 	//iconCls: Nodo.iconCls,	
		items:
		[
			{
	        	xtype : 'component',
	        	region: 'center',
	        	autoScroll: false,
		        style: {
		        			border:'none'
			    },
	        	autoEl : {
	            			tag : "iframe",
	            			src : basePath+'/filestore/'+nombreOriginal+'?select='+select
	        			}
			}
		],
		layout: 'fit'
		});
	 var panelDetalles=new Ext.panel.Panel({
		 id:'panelDetalles',
		 collapsible: false,
		 region: 'center',
		 cls:'panelDetalles',
		 autoScroll:true,
		 hidden:visualizadorCompartido,
		 title: '<font class="textoTabs">Detalles</font>',
		 items:[//panelFormP]
		        {xtype:'fieldset',cls:'fieldS',id:'panelGen',title:'Datos generales',layout:'anchor',items:[Grid]}]
	 });
	 var panelPlantilla=new Ext.panel.Panel({
		 id:'panelPlantilla',
		 collapsible: false,
		 region: 'center',
		 //cls:'panelDetalles',
		 hidden:visualizadorCompartido,
		 title: 'Plantilla',
		 items:[panelFormPlantilla]
	 });
	 var panelTexto=new Ext.panel.Panel({
		 id:'panelTexto',
		 collapsible: false,
		 region: 'center',
		 cls:'panelTexto',
		 hidden:externo?true:visualizadorCompartido,
				 title: '<font class="textoTabs">Texto</font>',//TODO
				 tbar:['->',btnGuardarOCR],
				 items:[editorTexto]
	 });
	 var panelPDF=new Ext.panel.Panel({
	 	 
		 id:'panelPDF',
		 collapsible: false,
		 hidden: externo?true:!privilegios.descargar,
		 region: 'center',
		 cls:'panelPDF',
	     title: '<font class="textoTabs">PDF</font>',
		 html: '<div style="height:100%; scrolling="yes" overflow:scroll !important;"><iframe id="framePDF" src="'+basePath+'pdfstore/'+select+'.pdf?select='+select+'"  style="width:100%;height:100%;border:none;"></iframe></div>'
	 });
	 var btnActualizarArbol=Ext.create('Ext.button.Button',{
		 text: 'Actualizar',
		 iconCls:'btnRefrescarArbolIconCls',
		 handler: btnActualizarArbolHAndler
	 });
	 var itemsTabPanel = [panelVisor,panelPDF,panelTexto,panelPlantilla,panelDetalles];
	 if(externo)
	 	itemsTabPanel = [panelArchivo,panelPlantilla,panelDetalles];
	 var panel=new Ext.tab.Panel({
		 collapsible: false,
		 layout:'fit',
		 region: 'center',
		 //cls:'panel',
		 minTabWidth:150,
		 tbar:['->',btnActualizarArbol],
		 items:itemsTabPanel,
		 listeners:{
			 tabchange: panelOnTabChange
		 }
	 });
	 var panelMiniaturas=new Ext.form.Panel({
		 region: 'east',
		 layout:'fit',
		 collapsible: true,
		 collapsed:false,
		 title: 'Páginas',
		 width: AnchoMiniaturas,
		 bbar:bbarAvanzar,
		 hidden:externo,
		 items:[visualizadorMinitaturas]
	 });
	 var viewport=Ext.create('Ext.container.Viewport', {
		 layout: 'border',
		 items: [panel,panelMiniaturas

		         ]
	 });

	//Funciones
	function log(msj){
		Ext.example.msg("Log",msj);
		}
	function creaSprite(_this,Src,ancho,alto){
	    				var m=_this.getWidth()*.06;
	    				var altoP=_this.getHeight()-m;
	    				var anchoP=_this.getWidth()-m;
	    				if(altoP<alto){
	    					var p=altoP/alto;
	    					alto=altoP;
	    					ancho=(ancho*p)-10;
	    				}
	    				if(anchoP<ancho){
	    					var p=anchoP/ancho;
	    					ancho=anchoP;
	    					alto=(alto*p);
	    				}
	    				var left=(panImg.getWidth()-ancho)/2;
	    				var top=(panImg.getHeight()-alto)/3;
	    				var sprite = panImg.surface.add({
				    	type: 'image',
				    	src: Src,
				    	width: ancho,
				    	height:alto,
	        			x:left,
	        			y:top
				});
				sprite.show(true);
				sprite.el.dom.stroked = 'f';
				anchoImg=ancho;
	 			altoImg=alto;
				
	}
	function creaSpriteAjusta(_this,Src,ajustaAncho,ancho,alto){
		if(ajustaAncho){
	    				var altoP=_this.getHeight();
	    				var anchoP=_this.getWidth();
	    				
	    				alto=(alto*anchoP)/ancho;
	    				ancho=anchoP;
	    				var left=0;
	    				altoPanel=alto;
	    				panImg.setHeight(alto);
		}	
		else{
						
	    				var altoP=panelVisor.getHeight();//_this.getHeight();
	    				_this.setHeight(altoP);
	    				var anchoP=_this.getWidth();
	    				ancho=(altoP*ancho)/alto;
	    				alto=altoP-20;
	    				var left=(panImg.getWidth()-ancho)/2;
		}
	    				
	    			var sprite = panImg.surface.add({
				    	type: 'image',
				    	src: Src,
				    	width: ancho-20,
				    	height:alto-20,
	        			x:left+10,
	        			y:10
				});
				sprite.show(true);
				sprite.el.dom.stroked = 'f';
				anchoImg=ancho;
	 			altoImg=alto;
				
	}
	function moverImagen(xV,yV){
		var sprite = panImg.surface.items.first();
		var difX=(xV-xG)+xF;
		var difY=(yV-yG)+yF;
	    sprite.setAttributes({
	                translate: {
					x: difX,
					y: difY
		}},true);
		var anot=panImg.surface.getGroup('anotaciones');
		if(anot!=null&&anot!=''){
					anot.setAttributes({
	                translate: {
					x: difX,
					y: difY
		}},true);
			}
		xFtmp=difX;
	 	yFtmp=difY;
	}
	function moverImagenScroll(xV,yV){
		var sprite = panImg.surface.items.first();
		var difX=(xV-0)+xF;
		var difY=(yV-0)+yF;
	    sprite.setAttributes({
	                translate: {
					x: difX,
					y: difY
		}},true);
		var anot=panImg.surface.getGroup('anotaciones');
		if(anot!=null&&anot!=''){
					anot.setAttributes({
	                translate: {
					x: difX,
					y: difY
		}},true);
			}
		xFtmp=difX;
	 	yFtmp=difY;
	}
	function zoomIn(xV,yV){
		var sprite = panImg.surface;
	       sprite.setViewBox(xV-sprite.x,yV-sprite.y,variacionZoom,variacionZoom);
	       
	}
	function quitaSprite(i){
		//panImg.surface.remove(i,true);
		var sprite = panImg.surface.items.get(i);
		if(sprite!=null){	
			sprite.remove();
		}
	}


	/*
	 * Funciones Anotaciones
	 */
	 function DatosGenerales(){/*
			 	var url="";
			 	if(storeGeneral.getAt(0).get('compartir')){
			 	 url='\n'+
	        			basePath+"jsp/entregaDocumento.jsp?select="+storeGeneral.getAt(0).get('titulo_aplicacion')+
	        			"_G"+storeGeneral.getAt(0).get('id_gabinete')+"C"+storeGeneral.getAt(0).get('id_carpeta_padre')+
	        			"D"+storeGeneral.getAt(0).get('id_documento')+"&token="+storeGeneral.getAt(0).get('token')+
	        			"&tipodoc="+storeGeneral.getAt(0).get('nombre_tipo_docto');
	        			
	        			ligaCompartir.setText("<b class='LabelTP'>Liga: </b><a class='infoP' target = '_blank' href='"+url+"'>"+url+"</a><br /><br />",false);
				 
			 	}
			 	else{
			 			ligaCompartir.setText("<b class='LabelTP'>Liga: </b><a class='infoP'>No compartido</a><br /><br />",false);
				 
			 	}
			 	
				 panelForm.setTitle('Plantilla: '+storeGeneral.getAt(0).get('Plantilla'));
				 lblNombre.setText("<b class='LabelT'>Nombre: </b><font class='info'>"+storeGeneral.getAt(0).get('Nombre')+"</font><br /><br />",false);
				 lblDescripcion.setText("<b class='LabelT'>Descripción: </b><font class='info'>"+storeGeneral.getAt(0).get('Descripcion')+"</font><br /><br />",false);
				 lblContiene.setText("<b class='LabelTP'>Contiene: </b><font class='infoP'>"+storeGeneral.getAt(0).get('Contiene')+" paginas</font><br /><br />",false);
				 lblTamano.setText("<b class='LabelTP'>Tama&ntilde;o: </b><font class='infoP'>"+storeGeneral.getAt(0).get('Tamano')+" Kb</font><br /><br />",false);
				 lblMods.setText("<b class='LabelTP'>Creado: </b><font class='infoP'>"+storeGeneral.getAt(0).get('Creado')+"</font><br /><b class='LabelTP'>Modificado: </b><font class='infoP'>"+storeGeneral.getAt(0).get('Modificado')+"</font><br /><br />",false);
				 lblPaginas.setText("de "+storeGeneral.getAt(0).get('Contiene')+' paginas');*/
	 			 totalPaginas=storeGeneral.getAt(3).get('valor');
	 			 lblPaginas.setText("de "+totalPaginas+' páginas');
				 storeCamposPlantilla.load();
			 }
				function interfazPersonalizada(){
					for(var i=0;i<storeCamposPlantilla.getCount();i++){
						creaFormulario(storeCamposPlantilla.getAt(i).get('tipo'),storeCamposPlantilla.getAt(i).get('nombre'),storeCamposPlantilla.getAt(i).get('etiqueta'),storeCamposPlantilla.getAt(i).get('longitud'),storeCamposPlantilla.getAt(i).get('valor'),storeCamposPlantilla.getAt(i).get('requerido'),false,storeCamposPlantilla.getAt(i).get('lista'),panelForm);
					}
					var fields = panelForm.items.items;
					for(var i=0;i<fields.length;i++)
						fields[i].setReadOnly(true);
					btnEditar.setText('Editar');
					btnGuardar.setDisabled(true);
					
					Ext.getBody().unmask();
					
				}
				function activaEdicion(){
					for(var i=0;i<storeCamposPlantilla.getCount();i++){
						creaFormulario(storeCamposPlantilla.getAt(i).get('tipo'),storeCamposPlantilla.getAt(i).get('nombre'),storeCamposPlantilla.getAt(i).get('etiqueta'),storeCamposPlantilla.getAt(i).get('longitud'),storeCamposPlantilla.getAt(i).get('valor'),storeCamposPlantilla.getAt(i).get('requerido'),storeCamposPlantilla.getAt(i).get('editable'),storeCamposPlantilla.getAt(i).get('lista'),panelForm);
					}
					btnEditar.setText('Cancelar');
					btnGuardar.setDisabled(false);	T=pantalla.height;
					Ext.getBody().unmask();
				}
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
			        	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			        	panelForm.removeAll();
			        	reiniciaForm();
			        	storeCamposPlantilla.load();

			        } else {
			            Ext.Msg.show({
			                title: 'Error',
			                msg: jsonData.message,
			                buttons: Ext.Msg.OK,
			                animEl: 'elId',
			                icon: Ext.MessageBox.ERROR
			            });
			        }
			        btnEditar.setText('Editar');
					btnGuardar.setDisabled(true);
			        
			    };
			    var successAjaxFnNGral = function(response, request) {
					Ext.getBody().unmask();
			        var jsonData = Ext.JSON.decode(response.responseText);
			        if (true == jsonData.success) {
			           	Ext.example.msg("Correcto",jsonData.message);
			        } else {
			            Ext.Msg.show({
			                title: 'Error',
			                msg: jsonData.message,
			                buttons: Ext.Msg.OK,
			                animEl: 'elId',
			                icon: Ext.MessageBox.ERROR
			            });
			        }

			    };
				function guardar(){
					Ext.Ajax.request({  
						url: rutaServlet,  
						method: 'POST',  
						 success: successAjaxFnN,   
						timeout: 30000,  
						params: {  
							action: 'EditDocument',
							select: select,
							plantilla_documento: Ext.JSON.encode(panelFormPlantilla.getValues())
						}
					});
				}
				function funcionImprimir(paginaIndex){
					var urlI='../jsp/Impresora.jsp?select='+select+'&page_actual='+paginaIndex;
					crearVentana(urlI,"Imprimir",520,405);
				}
				function btnAgregarImagenHandler(button, e){
					crearVentanaAgregarImagen();
				}
				function btnEscanearHandler(button, e){
					var urlI='../jsp/PageDocumentScan.jsp?select='+select;
					crearVentana(urlI,"Escanear",600,500);
				}
				function crearVentana(url,titulo,w,h){
					if(Ext.getCmp('ventana')==null){			
						var ventana=new Ext.window.Window({
						id:'ventana',
		    			title: titulo,
		    			height: h,
		    			constrain:true,
		    			width: w,
		    			layout:'anchor',
		    			draggable:true,
		    			resizable:false,
		    			 html: '<div style="height:100%; scrolling="yes" overflow:scroll !important;"><iframe id="framePDF" src="'+url+'"  style="width:100%;height:100%;border:none;"></iframe></div>'
		    			}).show();
					}
				}
				function cargaOCR(doc,index){
					StoreOCR.load({params: {action:"getOCR",select:doc,pagina:index}});
				}
				var successAjaxFnNElimina = function(response, request) {
					Ext.getBody().unmask();
			        var jsonData = Ext.JSON.decode(response.responseText);
			        if (jsonData.success) {
//			        	if(StoreMiniaturas.getCount()<=1){
//							document.location.href="PreGuardaDocto.jsp?select="+select;
//						}
			            Ext.example.msg("Correcto",jsonData.message);
			            
			            StoreMiniaturas.removeAll();
			            StoreMiniaturas.on({
			    			load: function(store, records, successful, operation, eOpts){
//			    				visualizadorMinitaturas.getSelectionModel().select(0);
			    				if(store.data.getAt(0))
			    					visualizadorMinitaturas.fireEvent('itemclick', visualizadorMinitaturas, store.data.getAt(0));
			    				lblPaginas.setText("de "+store.getCount()+' páginas');
			    			},
			    			single: true
			    		});
			           	StoreMiniaturas.load({params: {action:actionStoreBuffer,select:select,start: 0, limit:miniaturasPorCarga}});
			            
			           	quitaSprite(0);
			            var div = document.getElementById(divEliminar);
						if(div)
							div.parentNode.removeChild(div);
			        } else {
			            Ext.Msg.show({
			                title: 'Error',
			                msg: jsonData.message,
			                buttons: Ext.Msg.OK,
			                animEl: 'elId',
			                icon: Ext.MessageBox.ERROR
			            });
			        }

			    };
			    function eliminaPagina(selectP,index,id_pagina){
			    	var posicion_pagina=index;
			    	Ext.Msg.show({
			    		title:'Eliminar',
			    		msg: '&iquestDeseas eliminar la pagina: '+posicion_pagina+' ?',
			    		buttons: Ext.Msg.YESNOCANCEL,
			    		icon: Ext.Msg.QUESTION,
			    		fn: function (btn){
			    			if(btn=='yes'){ 
			    				divEliminar=posicion_pagina;
			    				Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			    				Ext.Ajax.request({  
			    					url: rutaServletD,  
			    					method: 'POST',  
			    					success: successAjaxFnNElimina,   
			    					timeout: 30000,  
			    					params: {  
			    						action: 'delPageDocument',  				
			    						select:selectP,
			    						index:index,
			    						id_pagina:id_pagina
			    					}});
			    			}
			    		}
			    	});
			    }
		function eliminarEtiquetas(string){
			string=string.replace("<br>","\n");
	  return string.replace(/(?:<(?:script|style)[^>]*>[\s\S]*?<\/(?:script|style)>|<[!\/]?[a-z]\w*(?:\s*[a-z][\w\-]*=?[^>]*)*>|<!--[\s\S]*?-->|<\?[\s\S]*?\?>)[\r\n]*/gi, '');
	}
		function guardaOcr(texto,selectP,index){
			var datos=Ext.create('ModeloOCR',{
						id:index,
						texto:texto
					});
					datos = Ext.JSON.encode(datos.data);
					Ext.Ajax.request({  
			        			url: rutaServletA,  
			        			method: 'POST',  
			        			 success: successAjaxFnNGral,   
			        			timeout: 30000,  
			        			params: {  
			        			action: 'setOCR',  				
			        			select:selectP,
			        			ocr:datos
			        }});
		}
			function inhabilitar(){ 
			   	return false ;
			}
			function modificaCompartido(){
				panel.remove(panelPDF);
				panel.remove(panelTexto);
				panel.remove(panelDetalles);
				toolbarVisor.remove(btnEscanear);
				toolbarVisor.remove(btnImprimir);
				toolbarVisor.remove(btnAgregarImagen);
				toolbarVisor.remove(btnEliminarPagina);
				/*
				toolbarVisor.remove(s1);
				toolbarVisor.remove(s2);
				toolbarVisor.remove(s3);
				*/
			}
			function modificaExterno(){
				panel.remove(panelPDF);
				panel.remove(panelTexto);
				panel.remove(panelVisor);
				panelMiniaturas.setVisible(false);
				viewport.remove(panelMiniaturas);
				panel.setActiveTab(0);
				Ext.getBody().unmask();
			}
			/*
			 * Seccion de Atributos/Historico de documentos.
			 */
			var filaHistorico=-1;
		
			var CatalogoAtributosStore = Ext.create('Ext.data.Store', { //TODO: Store a cargar del servidor
	    		fields:['id_atributo', 'etiqueta_atributo', 'tipo_atributo', 'descripcion_atributo'],
	    		data: [{id_atributo: 1, etiqueta_atributo:'Requerido', 			tipo_atributo:'Boolean', descripcion_atributo:'El documento NO debe estar vacio, para ser válido'}, 
	    		       {id_atributo: 2, etiqueta_atributo:'Historico', 			tipo_atributo:'Boolean', descripcion_atributo:'El documento genera copias en cada modificacion del documento'},
	    		       {id_atributo: 3, etiqueta_atributo:'Vigencia', 			tipo_atributo:'Integer', descripcion_atributo:'Cantidad de dias en que el documento es válido a partir de su alta'},
	    		       {id_atributo: 4, etiqueta_atributo:'Vencimiento', 		tipo_atributo:'Date', 	 descripcion_atributo:'Fecha especifica en la que el documento deja de ser válido'},
	    		       {id_atributo: 5, etiqueta_atributo:'Existencia Física', 	tipo_atributo:'String',  descripcion_atributo:'Es donde se encuentra fisicamente el documento ( Edificio, archivo, piso, gaveta, seccion, etc. )'}
	    		       ]
	    	});
			
			var AtributosNodoStore = Ext.create('Ext.data.Store', {
				model: 'Atributo',
				proxy: {
					type: 'ajax',
					url: rutaServletA,
					reader: {
						type: 'json',
						root: 'atributos'
					},
					extraParams: 
					{
						action: 'getAtributosByNodo',
						nodo: select
					}
				}
			});
			 
	        var storeAtributos = Ext.create('Ext.data.Store', {
		     model: 'atributoDocumentoModel',
		     proxy: {
		         type: 'ajax',
		         url: rutaServletAtributos,	//OperacionesGavetaServlet!
		         reader: {
		             type: 'json',
		             root: 'atributos'
		         },
		         extraParams: 
		         {
		              action: "getAtributos",
		              select:select
		          }
		     },
		     listeners:{
		    	 load: storeAtributosMostrarFieldSetOnLoad
		     },
		     autoLoad: true
		 });
		 
			 var storeHistorico = Ext.create('Ext.data.Store', {
		     model: 'historicoDocumentoModel',
		     proxy: {
		         type: 'ajax',
		         url: rutaServletAtributos,	
		         reader: {
		             type: 'json',
		             root: 'historico'
		         },
		         extraParams: 
		         {
		              action: "getHistorico",
		              select:select
		          }
		     },
		     listeners:{
		    	 load: storeHistoricoInsertarHistoricoOnLoad
		     },
		     autoLoad: false
		 });
		 /*
		  * Funciones atributos/Historico
		  */
		 	function isDate(sDate) {
			var scratch = new Date(sDate);
			if (scratch.toString() == "NaN" ||scratch.toString() == "Invalid Date") {
				return false;
				} else {
				return true;
				}
			}
			function insertarHistorico(){
		       //insertar grid historico
				var contextMenu = new Ext.menu.Menu({
			  		items: [
			   		{text: 'Datos documento',
			   			menu:[
			   				{iconCls:'pdfIconCls',text:'PDF',handler:function(){if(filaHistorico!=-1&&filaHistorico!=null){llamarHistorico(1,1,filaHistorico);}}},
			   				{iconCls:'zipIconCls',text:'ZIP',handler:function(){if(filaHistorico!=-1&&filaHistorico!=null){llamarHistorico(1,2,filaHistorico);}}}
			   			]},
			   			{text: 'Documento',
			   			menu:[
			   				{iconCls:'pdfIconCls',text:'PDF',handler:function(){if(filaHistorico!=-1&&filaHistorico!=null){llamarHistorico(2,1,filaHistorico);}}},
			   				{iconCls:'zipIconCls',text:'ZIP',handler:function(){if(filaHistorico!=-1&&filaHistorico!=null){llamarHistorico(2,2,filaHistorico);}}}
			   			]},
			   			{text: 'Ambos',
			   			menu:[
			   				{iconCls:'pdfIconCls',text:'PDF',handler:function(){if(filaHistorico!=-1&&filaHistorico!=null){llamarHistorico(3,1,filaHistorico);}}},
			   				{iconCls:'zipIconCls',text:'ZIP',handler:function(){if(filaHistorico!=-1&&filaHistorico!=null){llamarHistorico(3,2,filaHistorico);}}}
			   			]}
		       		]
				});
				
				var gridHisorico = new Ext.grid.Panel({
				    title: 'Historico',
				    store: storeHistorico,
				    cls:'grdHistorico',
				    columns: [
				        { header: 'Version',  dataIndex: 'Id_Version',width:50 },
				        { header: 'Fecha', dataIndex: 'Fecha_generacion', flex: 1 },
				        { header: 'Nombre', dataIndex: 'Nombre_Documento',flex:1 },
				        { header: 'Descripcion', dataIndex: 'Descripcion',flex:2 },
				        { header: 'Paginas', dataIndex: 'numero_paginas',width:50 },
				        { header: 'Tamaño', dataIndex: 'tamano',width:80},
				        { header: 'Usuario', dataIndex: 'usuario_generador',flex:1 },
				        { xtype:'actioncolumn',width:50,align:'center',items: [{
	                		icon: '../imagenes/iconos/documentoHistorico16.png', 
	                		tooltip: 'Descargar',
	                		handler: gridHisoricoDescargarHandler
	            		}]}
				    ],
				    height: 400,
				    width: '100%',
				    autoScroll:true			    
				});
				var fieldSetHistorico=new Ext.form.FieldSet({
			 		id:'fieldSetHistorico',
					cls:'fieldSetHistoricoCls',
					title:'Historico',
					items:[gridHisorico]
				});
				panelDetalles.add(fieldSetHistorico);
			}
		 function insertaAtributos(store){
		 	var btnGuardarAtributos=new Ext.button.Button({
		 		id:'btnGuardarAtributos',
		 		name:'btnGuardarAtributos',
		 		text:'Guardar',
		 		iconCls:'btnGuardarAtributosIconCls',
		 		cls:'btnGuardarAtributosCls',
		 		scale:'medium',
		 		disabled:!editarAtributos,
		 		hidden:!editarAtributos,
		 		handler:function(_this,e){
		 			Ext.Msg.show({
			     title:'Modificación de Atributos',
			     msg: '¿Deseas modificar los atributos del documento?',
			     buttons: Ext.Msg.YESNOCANCEL,
			     icon: Ext.Msg.QUESTION,
			     fn: function (btn){
			    	 if(btn=='yes'){
			    		 var arregloAtributos=new Array();
			    		 for(var x=0;x<store.getCount();x++){
			    			 //En la modificación sólo interesa el atributo y su valor a modificar:
			    			 if(store.getAt(x).get('modificable')){
			    				 var etiqueta_atributo = store.getAt(x).get('atributo');
			    				 var id_atributo = CatalogoAtributosStore.findRecord('etiqueta_atributo', etiqueta_atributo).get('id_atributo');
			    				 var datos = {
			    						 id_atributo: id_atributo,
			    						 valor:Ext.getCmp('campo'+x.toString()).getValue()
			    				 };
			    				 arregloAtributos.push(datos);
			    			 }
			    		 }
		 				Ext.Ajax.request({  
		 					url: rutaServletA,
		 					method: 'POST',
		 					success: function(response){
		 						var jsonData = Ext.JSON.decode(response.responseText);
		 						if (jsonData.success){
		 							alert('¡Éxito!');
		 						}
		 					},
		 					params: {  
		 						action: 'editAtributos',
		 						atributos: Ext.JSON.encode(arregloAtributos),
		 						nodo: select
		 					}
		 				});
			         }
			     }
				});
		 			
		 		}
		 		
		 	});
		 	var fieldSetAtributos=new Ext.form.FieldSet({
		 		id:'fieldSetAtributos',
				cls:'fieldSetAtributosCls',
				disabled:!editarAtributos,
				disabledCls:'disabledCls',
				title:'Atributos'
			});

		 	
		 	AtributosNodoStore.on({
		 		'load': function(store, records, successful, eOpts ){
		 			
		 	for(var i=0;i<AtributosNodoStore.getCount();i++){
		 		var id_atributo = store.getAt(i).get('id_atributo');
		 		var valor_atributo = AtributosNodoStore.getAt(i).get('valor_atributo');
		 		var modificable = AtributosNodoStore.getAt(i).get('modificable');
		 		//console.log(id_atributo, modificable, modificable==false, modificable=='false');
		 		
		 		var tipo_atributo = CatalogoAtributosStore.findRecord('id_atributo', id_atributo).get('tipo_atributo');
		 		var etiqueta_atributo = CatalogoAtributosStore.findRecord('id_atributo', id_atributo).get('etiqueta_atributo');
		 		var descripcion_atributo = CatalogoAtributosStore.findRecord('id_atributo', id_atributo).get('descripcion_atributo');
		 		
		 		var campo;
		 		if(tipo_atributo=='Integer'){
		 			campo = new Ext.form.field.Number({
		 				id:'campo'+i.toString(),
		 				accion: 'campoAtributo',
		 				name:'campo'+i.toString(),
		 				value: valor_atributo,
	        			minValue: 0,
	        			fieldLabel:etiqueta_atributo,
	        			width:250,
		 				labelWidth:100,
		 				cls:'campoAtributosCls',
	        			readOnly: !modificable,
	        			tip: descripcion_atributo,
	        			listeners: {
	        				afterrender: function(thisCampo){
	        					Ext.create('Ext.tip.ToolTip', {
	        					    target: thisCampo.id,
	        					    html: thisCampo.tip
	        					});
	        				}
	        			}
		 				
		 			});
		 		}
		 		else if(tipo_atributo=='Date'){
						campo=new Ext.form.field.Date({
						id:'campo'+i.toString(),
		 				accion: 'campoAtributo',
		 				name:'campo'+i.toString(),
		 				value:  Ext.Date.parse(valor_atributo, "Y-m-d"),
		 				format: 'd/m/Y',
		 				fieldLabel:etiqueta_atributo,
		 				width:250,
		 				labelWidth:100,
		 				cls:'campoAtributosCls',
	        			readOnly: !modificable,
	        			tip: descripcion_atributo,
	        			listeners: {
	        				afterrender: function(thisCampo){
	        					Ext.create('Ext.tip.ToolTip', {
	        					    target: thisCampo.id,
	        					    html: thisCampo.tip
	        					});
	        				}
	        			}
					});
		 		}
		 		else if(tipo_atributo=='Boolean'){
		 			campo=new Ext.form.field.Checkbox({
						id:'campo'+i.toString(),
		 				accion: 'campoAtributo',
		 				name:'campo'+i.toString(),
		 				fieldLabel:etiqueta_atributo,
		 				checked: valor_atributo==1||valor_atributo,
		 				width:250,
		 				boxLabelAlign:'before',
		 				cls:'campoAtributosCls',
	        			readOnly: !modificable,
	        			tip: descripcion_atributo,
	        			listeners: {
	        				afterrender: function(thisCampo){
	        					Ext.create('Ext.tip.ToolTip', {
	        					    target: thisCampo.id,
	        					    html: thisCampo.tip
	        					});
	        				}
	        			}
					});
		 		}
		 		else{
		 				campo=new Ext.form.field.Text({
						id:'campo'+i.toString(),
		 				accion: 'campoAtributo',
		 				name:'campo'+i.toString(),
		 				fieldLabel:etiqueta_atributo,
		 				value: valor_atributo,
		 				width:250,
		 				labelWidth:100,
		 				cls:'campoAtributosCls',
	        			readOnly: !modificable,
	        			tip: descripcion_atributo,
	        			listeners: {
	        				afterrender: function(thisCampo){
	        					Ext.create('Ext.tip.ToolTip', {
	        					    target: thisCampo.id,
	        					    html: thisCampo.tip
	        					});
	        				}
	        			}
					});
		 		}
		 		fieldSetAtributos.add(
		 			Ext.create('Ext.form.FieldSet', {
		 				id:'fieldSetAtributo'+i.toString(),
		 				name:'fieldSetAtributo'+i.toString(),
		 				border:0,
		 				cls:'fieldSetAtributoCls',
		 				layout:'hbox',
		 				items:[
		 					campo
		 				]
		 			})	
		 		);
		 	}
		 	
		 	if(editarAtributos)
		 		fieldSetAtributos.add(btnGuardarAtributos);
		 		}
		 	
		 	});
		 	
//		 	AtributosNodoStore.load(); En Afirme aun no hay atributos
		 		
		 	panelDetalles.add(fieldSetAtributos);
		 }
		 function llamarHistorico(opcion,tipo,version){
			//opcion: 1 Datos documento, 2 Documento, 3 Ambos
			//tipo: 1 PDF, 2 ZIP
			 
		 	alert(opcion+" : "+tipo+" : "+version);
		 	//Generando Historico
		 }
		 
	/**
	 *	FUNCIONES SACADAS DE COMPONENTES DE ARRIBA
	**/
		 function storeGeneralLoadDatosYPrivilegiosOnLoad(records, options, success, thisFn){
			 if(records.getCount()>0){
				DatosGenerales();
			 }
			/* if(!visualizadorCompartido){
				PrivilegiosStore.load();
			 }*/
			StoreMiniaturasBuffer.load();
		 }
		 
		 /*
		 function PrivilegiosStoreDisableButtonsOnLoad(records, options, success, thisFn){
			 if(!visualizadorCompartido){
				 btnAgregarImagen.setDisabled(records.findRecord('privilegio', 'Digitalizar').get('seleccionado')!=1?true:false);	
				 btnImprimir.setDisabled(records.findRecord('privilegio', 'Imprimir').get('seleccionado')!=1?true:false);
				 btnEscanear.setDisabled(records.findRecord('privilegio', 'Digitalizar').get('seleccionado')!=1?true:false);
				 btnEliminarPagina.setDisabled(records.findRecord('privilegio', 'Eliminar').get('seleccionado')!=1?true:false);
			 }
		 }
		 */
		 
		 function StoreMiniaturasBufferCreaSpriteOnLoad(store, records, options, success, thisFn){
			 txtPagina.setValue(1);
			 if(pagina!=null) {
		 		txtPagina.setValue(parseInt(pagina));
				pagina=null;
				txtPagina.fireEvent('specialkey',txtPagina,{
				getKey: function() {
				 return Ext.EventObject.ENTER;
				}
				});
			}
		 	
			 if(StoreMiniaturas.getCount()==0){
				 if(store.getCount()>0){
					 creaSprite(panImg,store.getAt(0).get('imagenPagina'),
							 store.getAt(0).get('ancho'),store.getAt(0).get('alto'));
				 }
			 }
			 for(var i=0;i<store.getCount();i++){
				 StoreMiniaturas.insert(StoreMiniaturas.getCount(),store.getAt(i));
			 }
			 if(!externo){
				 if(visualizadorCompartido){
					 modificaCompartido();
				 }
				 else{
					 Ext.getBody().unmask();
				 }
			 }
			 else{
				 modificaExterno();
			 }
		 }
		 
		 function StoreMiniaturasBuffer2QuitaSpriteOnLoad(store, records, options, success, thisFn){
	 		for(var i=0;i<store.getCount();i++){
	 			StoreMiniaturas.insert(StoreMiniaturas.getCount(),store.getAt(i));
	 		}
	 		if(store.getCount()>0){
	 			quitaSprite(0);
	     		xFtmp=0;
					yFtmp=0;
					xF=0;
					yF=0;
	 			var t=StoreMiniaturas.getCount()-1;
	 			creaSprite(panImg,StoreMiniaturas.getAt(t).get('imagenPagina'),
					StoreMiniaturas.getAt(t).get('ancho'),StoreMiniaturas.getAt(t).get('alto'));
					visualizadorMinitaturas.select(t,true);
	 		}
	 		Ext.getBody().unmask();
	 		lblPaginas.setText("de "+store.getCount()+' páginas');
		 }
		 
		 function storeCamposInterfazPersonalizadaOnLoad(records, options, success, thisFn){
			 if(records.getCount()>0){
				 interfazPersonalizada();
			 }
			 else{
				 panelForm.setVisible(false);
				 toolbar.setVisible(false);
				 panelPlantilla.setDisabled(true);
				 panel.remove(panelPlantilla);
				 Ext.getBody().unmask();
			 }
		 }
		 
		 function btnEditarHandler(button, e, eOpts){
			 if(button.getText()=='Editar'){
				 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
				 panelForm.removeAll();
				 reiniciaForm();
				 activaEdicion();
			 }
			 else{
				 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
				 panelForm.removeAll();
				 reiniciaForm();
				 interfazPersonalizada();
			 }
		 }
		
		 function btnGuardarHandler(button, e, eOpts){
			 if(btnEditar.getText()!='Editar'){
				 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
				 guardar();
			 }
			 else{
				 button.setDisabled(true);
			 }
		 }
		
		 function  txtFiltroClearFilterOnChange(me, newString, oldString, thisFunction){
			 if(me.getValue()!=""){
				 storeGeneral.clearFilter();
				 storeGeneral.filter({
					 property: 'nombre',
					 value: me.getValue(),
					 anyMatch: true,
					 caseSensitive: false
				 });
			 }
			 else{
				 storeGeneral.clearFilter();
			 } 
		 } 

		 function gridOpenWindowOnSelect(rowModel, record, index, thisFn){
			if(record.data.nombre=="Compartir"){
				window.open(record.data.valor);
			}
		 }
		 
		 function visualizadorMinitaturasCreaSpriteOnItemClick(me, record, item, index, e, eOpts ){
			 if(!index)
				 index=0;
			 txtPagina.setValue(index+1);
			 if(!tabOcr){
				 slideZoom.setValue(0,true);
				 slideRotar.setValue(0,true);

				 var altoP=panelVisor.getHeight();
				 panImg.setHeight(altoP);
				 quitaSprite(0);
				 xFtmp=0;
				 yFtmp=0;
				 xF=0;
				 yF=0;
				 creaSprite(panImg,record.data.imagenPagina,record.data.ancho,record.data.alto);
			 }
			 else{
				 cargaOCR(select,record.data.numero_pagina);
			 }
		 }
		 
		 function btnSiguienteMiniaturasCargaMiniaturasHandler(button, e){
			 StoreMiniaturasBuffer.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:miniaturasPorCarga}});
		 }

		 function slideZoomAjustaImagenOnChange(me, zoom, thumb, thisFn) {
			 var sprite = panImg.surface.items.first();
			 if(zoom>=0){
				 moverImagenScroll(0,zoom*25);
				 panImg.setHeight(altoPanel+(zoom*45));
				 sprite.setAttributes({
					 scale: {
						 x: (zoom+10)/10,
						 y: (zoom+10)/10
					 }
				 }, true);
				 var anot=panImg.surface.getGroup('anotaciones');
				 if(anot!=null&&anot!=''){
					 anot.setAttributes({
						 scale: {
							 x: (zoom+10)/10,
							 y: (zoom+10)/10
						 }
					 }, true);
				 }
			 }
			 else{
				 sprite.setAttributes({
					 scale: {
						 x: (zoom+100)/100,
						 y: (zoom+100)/100
					 }
				 }, true);
				 var anot=panImg.surface.getGroup('anotaciones');
				 if(anot!=null&&anot!=''){
					 anot.setAttributes({
						 scale: {
							 x: (zoom+100)/100,
							 y: (zoom+100)/100
						 }
					 }, true);
				 }
			 }
		 }
		 
		 function slideRotarRotaImagenOnChange(me, degrees, thumb, thisFn) {
			 var sprite = panImg.surface.items.first();
			 sprite.setAttributes({
				 rotation: {
					 degrees: degrees
				 }
			 }, true);
		 }
		 
		 function btnImprimirHnadler(){
			 var index;
			 if(visualizadorMinitaturas.getSelectedNodes()!=''&&visualizadorMinitaturas.getSelectedNodes()!=null){
				 index=visualizadorMinitaturas.getSelectedNodes()[0].id;
				 index--;
			 }
			 else{
				 index=0;
			 }
			 funcionImprimir(index);
		 }
		 
		 function btnInicioHandler(button, e){
			 txtPagina.setValue(1);
			 _this=txtPagina;
			 var pag=totalPaginas;
			 if(_this.getValue()>StoreMiniaturas.getCount()){
				 if(_this.getValue()<=pag){
					 var limit=_this.getValue()-StoreMiniaturas.getCount();
					 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
				 }
				 else{
					 _this.setValue(pag);
					 var limit=pag-StoreMiniaturas.getCount();
					 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
				 }
			 }
			 else{
				 quitaSprite(0);
				 xFtmp=0;
				 yFtmp=0;
				 xF=0;
				 yF=0;
				 creaSprite(panImg,StoreMiniaturas.getAt(_this.getValue()-1).get('imagenPagina'),
						 StoreMiniaturas.getAt(_this.getValue()-1).get('ancho'),StoreMiniaturas.getAt(_this.getValue()-1).get('alto'));
				 visualizadorMinitaturas.select(_this.getValue(),true);
			 }
		 }

		 function btnAnteriorHandler(button, e){
			 txtPagina.setValue(parseInt(txtPagina.getValue())-1);
			 _this=txtPagina;
			 if(_this.getValue()>0){
				 var pag=totalPaginas;
				 if(_this.getValue()>StoreMiniaturas.getCount()){
					 if(_this.getValue()<=pag){
						 var limit=_this.getValue()-StoreMiniaturas.getCount();
						 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
					 }
					 else{
						 _this.setValue(pag);
						 var limit=pag-StoreMiniaturas.getCount();
						 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
					 }
				 }
				 else{
					 quitaSprite(0);
					 xFtmp=0;
					 yFtmp=0;
					 xF=0;
					 yF=0;
					 creaSprite(panImg,StoreMiniaturas.getAt(_this.getValue()-1).get('imagenPagina'),
							 StoreMiniaturas.getAt(_this.getValue()-1).get('ancho'),StoreMiniaturas.getAt(_this.getValue()-1).get('alto'));
					 visualizadorMinitaturas.select(_this.getValue(),true);
				 }
			 }
			 else{
				 _this.setValue(1);
			 }

		 }
		 
		 function btnSiguienteHandler(button, e){
			 var totalPgs=0;
			 if(StoreMiniaturas.getTotalCount())
				 totalPgs = StoreMiniaturas.getTotalCount();
			 else
				 totalPgs = StoreMiniaturas.getCount();
			
			 if((parseInt(txtPagina.getValue())+1)<=totalPgs)
				 txtPagina.setValue(parseInt(txtPagina.getValue())+1);
			 
			 _this=txtPagina;
			 if(_this.getValue()>StoreMiniaturas.getCount()){
//				 if(_this.getValue()<=totalPgs){
//					 var limit=_this.getValue()-StoreMiniaturas.getCount();
//					 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
//				 }
//				 else{
//					 _this.setValue(totalPgs);
//					 var limit=totalPgs-StoreMiniaturas.getCount();
//    						 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
//				 }
			 }
			 else{
				 quitaSprite(0);
				 xFtmp=0;
				 yFtmp=0;
				 xF=0;
				 yF=0;
				 creaSprite(panImg,StoreMiniaturas.getAt(_this.getValue()-1).get('imagenPagina'),
						 StoreMiniaturas.getAt(_this.getValue()-1).get('ancho'),StoreMiniaturas.getAt(_this.getValue()-1).get('alto'));
				 visualizadorMinitaturas.select(_this.getValue(),true);
			 }
		 }
		 
		 function btnFinalHandler(button, e){
			 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			 var totPags;
			 if(StoreMiniaturas.getTotalCount())
				 totPags = StoreMiniaturas.getTotalCount();
			 else 
				 totPags = StoreMiniaturas.getCount();
			
			 txtPagina.setValue(parseInt(totPags));
			 _this=txtPagina;
			 var pag=totalPaginas;
			 if(_this.getValue()>StoreMiniaturas.getCount()){
				 if(_this.getValue()<=pag){
					 var limit=_this.getValue()-StoreMiniaturas.getCount();
					 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
				 }
				 else{
					 _this.setValue(pag);
					 var limit=pag-StoreMiniaturas.getCount();
					 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
				 }
			 }
			 else{
				 quitaSprite(0);
				 xFtmp=0;
				 yFtmp=0;
				 xF=0;
				 yF=0;
				 creaSprite(panImg,StoreMiniaturas.getAt(_this.getValue()-1).get('imagenPagina'),
						 StoreMiniaturas.getAt(_this.getValue()-1).get('ancho'),StoreMiniaturas.getAt(_this.getValue()-1).get('alto'));
				 visualizadorMinitaturas.select(_this.getValue(),true);
				 Ext.getBody().unmask();
			 }
		 }

		 //funcion usada al hacer enter en el field textual del número de la página
		 function txtPaginaOnSpecialKey(_this, e){
			 //Encontrar como reproducir el evento que llama esta función.
			 if(e.getKey()==Ext.EventObject.ENTER){
				 if(!isNaN(_this.getValue())){
					 if(_this.getValue()!=0){
						 tmpTexto=_this.getValue();
						 var pag=totalPaginas;
						 if(_this.getValue()>StoreMiniaturas.getCount()){
							 if(_this.getValue()<=pag){
								 var limit=_this.getValue()-StoreMiniaturas.getCount();
								 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
							 }
							 else{
								 _this.setValue(pag);
								 var limit=pag-StoreMiniaturas.getCount();
								 StoreMiniaturasBuffer2.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:limit}});
							 }
						 }
						 else{
							 quitaSprite(0);
							 xFtmp=0;
							 yFtmp=0;
							 xF=0;
							 yF=0;
							 creaSprite(panImg,StoreMiniaturas.getAt(_this.getValue()-1).get('imagenPagina'),
									 StoreMiniaturas.getAt(_this.getValue()-1).get('ancho'),StoreMiniaturas.getAt(_this.getValue()-1).get('alto'));
							 visualizadorMinitaturas.select(_this.getValue(),true);
						 }
					 }
					 else{
						 if(tmpTexto!=""){
							 _this.setValue(tmpTexto);
						 }
						 else{
							 _this.setValue(1);
						 }
					 }
				 }

			 } 

		 }
		 
		 function btnZoomOutHandler(button, e){
			 if(slideZoom.getValue()>-100){
				 slideZoom.setValue(slideZoom.getValue()-variacionZoom,true);
			 }
		 }
		 
		 function btnZoomInHandler(button, e){
				if(slideZoom.getValue()<100){
					slideZoom.setValue(slideZoom.getValue()+variacionZoom,true);
					
				}
			}
		 
		 function btnRotarDerechaHandler(button, e){
			 if(slideRotar.getValue()>=0&&slideRotar.getValue()<90){
				 slideRotar.setValue(90,true);
			 }
			 else if(slideRotar.getValue()>=90&&slideRotar.getValue()<180){
				 slideRotar.setValue(180,true);
			 }
			 else if(slideRotar.getValue()>=180){
				 slideRotar.setValue(-90,true);
			 }
			 else if(slideRotar.getValue()>=-90&&slideRotar.getValue()<0){
				 slideRotar.setValue(0,true);
			 }
			 else if(slideRotar.getValue()>=-180&&slideRotar.getValue()<-90){
				 slideRotar.setValue(-90,true);
			 }
			 else{
				 slideRotar.setValue(0,true);
			 }

		 }
		 
		 function btnRotarIzquierdaHandler(button, e){
			 if(slideRotar.getValue()<=0&&slideRotar.getValue()>-90){
				 slideRotar.setValue(-90,true);
			 }
			 else if(slideRotar.getValue()<=-90&&slideRotar.getValue()>-180){
				 slideRotar.setValue(-180,true);
			 }
			 else if(slideRotar.getValue()<=-180){
				 slideRotar.setValue(90,true);
			 }
			 else if(slideRotar.getValue()>90&&slideRotar.getValue()<180){
				 slideRotar.setValue(90,true);
			 }
			 else if(slideRotar.getValue()>=0&&slideRotar.getValue()<90){
				 slideRotar.setValue(0,true);
			 }
			 else{
				 slideRotar.setValue(0,true);
			 }

		 }
		 
		 function btnAjustarHandler(button, e){
			 slideZoom.setValue(0,true);
			 slideRotar.setValue(0,true);
			 var sprite = panImg.surface.items.first();
			 sprite.setAttributes({
				 translate: {
					 x: 1,
					 y: 0
				 }},true);
			 xFtmp=0;
			 yFtmp=0;
			 xF=0;
			 yF=0;       
			 var altoP=panelVisor.getHeight();//_this.getHeight();
			 panImg.setHeight(altoP);
		 }
		 
		 function btnAjustarAnchoHandler(button, e){		
			 var index;
			 if(visualizadorMinitaturas.getSelectedNodes()!=''&&visualizadorMinitaturas.getSelectedNodes()!=null){
				 index=visualizadorMinitaturas.getSelectedNodes()[0].id;
				 index--;
			 }
			 else{
				 index=0;
			 }
			 quitaSprite(0);
			 xFtmp=0;
			 yFtmp=0;
			 xF=0;
			 yF=0;
			 creaSpriteAjusta(panImg,StoreMiniaturas.getAt(index).get('imagenPagina'),true,StoreMiniaturas.getAt(index).get('ancho'),StoreMiniaturas.getAt(index).get('alto'));

		 }
		 
		 function btnAjustarAltoHandler(button, e){
			 var index;
			 if(visualizadorMinitaturas.getSelectedNodes()!=''&&visualizadorMinitaturas.getSelectedNodes()!=null){
				 index=visualizadorMinitaturas.getSelectedNodes()[0].id;
				 index--;
			 }
			 else{
				 index=0;
			 }
			 quitaSprite(0);
			 xFtmp=0;
			 yFtmp=0;
			 xF=0;
			 yF=0;
			 creaSpriteAjusta(panImg,StoreMiniaturas.getAt(index).get('imagenPagina'),false,StoreMiniaturas.getAt(index).get('ancho'),StoreMiniaturas.getAt(index).get('alto'));

		 }
		 
		 function btnVersionNavegadorHandler(button, e){
			 if(setCookie('_VF',1,'30d')){
				 if(visualizadorCompartido){
					 document.location.href=basePath+ "jsp/VisorDeImagenes.jsp?select="+select+'&compartido=true';
				 }
				 else{
					 document.location.href=basePath+ "jsp/VisorDeImagenes.jsp?select="+select;
				 }
			 }
		 }

		 function btnGuardarOCRHandler(button, e){
			 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			 var index;
			 var t=eliminarEtiquetas(editorTexto.getValue());
			 if(visualizadorMinitaturas.getSelectedNodes()!=''&&visualizadorMinitaturas.getSelectedNodes()!=null){
//				 index=visualizadorMinitaturas.getSelectedNodes()[0].id;
				 index=visualizadorMinitaturas.getSelectionModel().getSelection()[0].data.numero_pagina;
//				 index--;
			 }
			 else{
				 index=1;
			 }
			 guardaOcr(t,select,index);	
		 }
		 
		 function btnEliminarPaginaHandler(button, e){
			 var index;
			 var id_pagina;
			 if(visualizadorMinitaturas.getStore().getCount()>0){
				 if(visualizadorMinitaturas.getSelectedNodes()!=''&&visualizadorMinitaturas.getSelectedNodes()!=null){
					 index=visualizadorMinitaturas.getSelectionModel().getSelection()[0].data.pagina;
					 id_pagina=visualizadorMinitaturas.getSelectionModel().getSelection()[0].data.numero_pagina;
				 }
				 else{
					 id_pagina=1;
					 index=1;
				 }
				 eliminaPagina(select,index,id_pagina);
			 } else {
				 Ext.Msg.show({
					 title: 'Advertencia',
					 msg: 'No hay imágenes para eliminar.',
					 buttons: Ext.Msg.OK,
					 icon: Ext.MessageBox.WARNING
				 });
			 }
			 
		 }
		 
		 function panImgAfterRender(me, thisFn){
			me.surface.on({
					'mousedown': function( e, t, eOpts) {
						if(e.button!='2'){
							mover=true;
							xG=e.getXY()[0];
							yG=e.getXY()[1];
						}
						
						
			}});
			me.surface.on({
					'mouseup': function( e, t, eOpts) {
						if(e.button!='2'){
							mover=false;
							xF=xFtmp;
							yF=yFtmp;
						}

			}});
			me.surface.on({
					'mousemove': function( e, t, eOpts) {
		        			if(mover){
		        				moverImagen(e.getXY()[0],e.getXY()[1]);
		        			}
						
			}});
			altoPanel=me.getHeight();
		
		 }
		 
		 function btnActualizarArbolHAndler(button, e) {
			 StoreMiniaturasBuffer.load({params: {action:actionStoreBuffer,select:select,start: StoreMiniaturas.getCount(),limit:miniaturasPorCarga}});
			 top.left.location.href=("../refreshtree");
		 }
		 
		 function panelOnTabChange( tabPanel, newCard, oldCard, eOpts ){
			 if(newCard.getId()!="panelVisor"){
				 if(newCard.getId()=="panelTexto"){
					 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
					 var index;
					 var id_pagina;
					 if(visualizadorMinitaturas.getSelectedNodes()!=''&&visualizadorMinitaturas.getSelectedNodes()!=null){
						 index=visualizadorMinitaturas.getSelectedNodes()[0].id;
						 index--;
						 id_pagina=visualizadorMinitaturas.getSelectionModel().getSelection()[0].data.numero_pagina;
					 }
					 else{
						 index=0;
						 id_pagina=visualizadorMinitaturas.getStore().getAt(0).data.numero_pagina;
					 }
					 tmpIndex=index;
					 tabOcr=true;
					 cargaOCR(select,id_pagina);
					 panelMiniaturas.setDisabled(false);
					 panelMiniaturas.expand();
				 }
				 else{
					 tabOcr=false;
					 panelMiniaturas.setDisabled(true);
					 panelMiniaturas.collapse();
				 }
			 }
			 else{
				 visualizadorMinitaturas.select(tmpIndex,true);
				 tabOcr=false;
				 panelMiniaturas.setDisabled(false);
				 panelMiniaturas.expand();
			 }
		 }
		 
		 function storeAtributosMostrarFieldSetOnLoad(store, records, options, success, thisFn){
			 if(store.getCount()>0&&mostrarAtributos){
				 insertaAtributos(store);
			 }
			 storeHistorico.load();
		 }

		 function storeHistoricoInsertarHistoricoOnLoad (records, options, success, thisFn){
			 if(records.getCount()>0&&mostrarHistorico){
				 insertarHistorico();
			 }
		 }

		 function gridHisoricoDescargarHandler(view, rowIndex, colIndex, item, e) {
			 filaHistorico = storeHistorico.getAt(rowIndex).get('Id_Version');
			 contextMenu.showAt(e.getX()-150,e.getY(),false);
		 }
		 
		 function storeOCRSetValueOnLoad(records, options, success, thisF){
    		 editorTexto.setValue('');
    		 editorTexto.setValue(records.getAt(0).get('texto'));
    		 Ext.getBody().unmask(); // CUSTOMIZAR PARA PANEL PADRE DE VISUALIZADOR
    	 }
		 
		 /**
		  * Inicia sección importada para Ventana de Agregar Imágenes
		  */

		 /*
		  * Variables
		  */
		 var contador=0;
		 var imgOcultarNo='imgEstado';
		 var nombreGenericoNo='progressBar';
		 var subirGenericoNo='file';
		 var tiempoRedireccion=1200;
		 var anchoProressBar=130;
		 /*
		  * Objetos Definidos
		  */
		 Ext.define("progressBarCustom", {
			 extend : 'Ext.ProgressBar',
			 alias : 'widget.progressBarCustom',
			 max : null,
			 ave : null,
			 min : null,
			 color : null,

			 initComponent : function() {

				 var me = this;
				 me.imgOcultar='imgEstado';
				 me.nombreGenerico='progressBar';
				 me.rutaImgOk='../imagenes/iconos/ok16.png';
					 me.width = me.width;
				 me.height=me.height;
				 me.callParent(arguments);
				 me.setEnd=function(multi){
					 if(multi){
						 me.reset();
						 me.updateProgress( 1,'', true );
						 var n=me.name.substring(me.nombreGenerico.length,me.name.length);
						 var nombre=me.imgOcultar+n;
						 Ext.getCmp(nombre).setSrc(me.rutaImgOk);
					 }
				 };
				 me.setEndFail=function(multi){

				 };
				 me.setStart=function(multi){
					 if(multi){
						 me.setVisible(true);
						 me.wait();
						 var n=me.name.substring(me.nombreGenerico.length,me.name.length);
						 var nombre=me.imgOcultar+n;
						 Ext.getCmp(nombre).setVisible(true);
					 }
				 };
			 },

			 listeners : {
				 update : function(obj, val) {
					 if(val==0){
						 obj.getEl().child(".x-progress-bar", true).style.backgroundColor = "#FFF";
						 obj.getEl().child(".x-progress-bar", true).style.borderRightColor = "#FFF";
						 obj.getEl().child(".x-progress-bar", true).style.backgroundImage = "url('')";
					 }
					 else if (this.max != null && this.ave != null && this.min != null) {
						 if (val  <= this.min) {
							 obj.getEl().child(".x-progress-bar", true).style.backgroundColor = "#FF0000";
							 obj.getEl().child(".x-progress-bar", true).style.borderRightColor = "#FF0000";
							 obj.getEl().child(".x-progress-bar", true).style.backgroundImage = "url('')";
						 } else if (val  <= this.ave) {
							 obj.getEl().child(".x-progress-bar", true).style.backgroundColor = "#FFFF00";
							 obj.getEl().child(".x-progress-bar", true).style.borderRightColor = "#FFFF00";
							 obj.getEl().child(".x-progress-bar", true).style.backgroundImage = "url('')";
						 } else {
							 obj.getEl().child(".x-progress-bar", true).style.backgroundColor = "#52ff5e";
							 obj.getEl().child(".x-progress-bar", true).style.borderRightColor = "#52ff5e";
							 obj.getEl().child(".x-progress-bar", true).style.backgroundImage = "url('')";
						 }
					 } else if (this.color != null) {
						 obj.getEl().child(".x-progress-bar", true).style.backgroundColor = this.color;
						 obj.getEl().child(".x-progress-bar", true).style.borderRightColor = this.color;
						 obj.getEl().child(".x-progress-bar", true).style.backgroundImage = "url('')";
					 }
				 }
			 }
		 });
		 /*
		  * Objetos
		  */
//		 var imgEstado0 = Ext.create('Ext.Img', {
//			 id:imgOcultarNo+contador.toString(),
//			 name:imgOcultarNo+contador.toString(),
//			 width:16,
//			 height:16,
//			 cls:'imgEstados',
//			 hidden:true,
//			 src: basePath+'/imagenes/iconos/flechaArriba16.png'
//		 });
//		 var progressBar0 = Ext.create('progressBarCustom', {
//			 id:nombreGenericoNo+contador.toString(),
//			 name:nombreGenericoNo+contador.toString(),
//			 width: anchoProressBar,
//			 value:0,
//			 height:8,
//			 hidden:true,
//			 cls:'progressBarsCls',
//			 componentCls:'progressBarsComponentCls',
//			 color:'#52ff5e'
//		 });
		 var progressBarTotal = Ext.create('progressBarCustom', {
			 id:'progressBarTotal',
			 name:'progressBarTotal',
			 width: 250,
			 value:0,
			 text: '...',
			 cls:'progressBarTotalCls',
			 color:'#0066ae'
		 });
		 var checkCerrarAlEnviar = new Ext.form.field.Checkbox({
			 fieldLabel: 'Cerrar al enviar',
			 checked: true,
			 width: 150,
			 labelWidth : 100,
			 labelPad: 0,
			 boxLabelAlign:'before'
		 });
		 var btnEnviarFotos=new Ext.button.Button({
			 id:'btnEnviarFotos',
			 name:'btnEnviarFotos',
			 text:'Enviar',
			 scale:'medium',
			 iconCls:'btnEnviarFotosIconCls',
			 handler:function(){
				 var fieldsValidos = true;
				 var filefields = Ext.ComponentQuery.query('filefield');
				 var imagesRegEx = /^.+\.(gif|jpg|jpeg|tiff|png|bmp|tif)$/i;
				 
				 filefields.forEach(function(field){
					 if(field.value && !imagesRegEx.test(field.value)){
						 fieldsValidos = false;
						 field.markInvalid('Sólo se permiten imágenes: gif, jpg, jpeg, tiff, tif, png, bmp.');
					 }
				 }); //TODO Verificar forEach para cambiarlo
				 
				 if(fieldsValidos)
					 enviarFotos();
				 else 
					 return;
			 }
		 });
		 var lblTotal=new Ext.form.Label({
			 id:'lblTotal',
			 cls:'lblTotalCls',
			 disabled:false,
			 html:'<font class="totalTexto">Total filas: </font><font class="totalNumero">0</font>'
		 });
		 var btnAgregarFila=new Ext.button.Button({
			 id:'btnAgregarFila',
			 name:'btnAgregarFila',
			 text:'Agregar fila',
			 iconCls:'btnAgregarFilaIconCls',
			 handler:function(){
				 agregarFile(contador);
			 }
		 });

//		 var subir0=new Ext.form.field.File({
//			 id:subirGenericoNo+contador.toString(),
//			 name: subirGenericoNo+contador.toString(),
//			 fieldLabel: 'Selecciona imagen',
//			 labelWidth: 150,
//			 msgTarget: 'side',
//			 allowBlank: true,
//			 cls:'subirCls',
//			 width:400,
//			 buttonText: '',
//			 listeners:{
//				 change:function( me, value, eOpts ){
//					 var n=parseInt(me.getName().substring(subirGenericoNo.length,me.getName().length));
//					 if(n==contador){
//						 agregarFile(contador);
//					 }
//				 }
//			 },
//			 buttonConfig:{
//				 xtype: "button",
//				 id: "btnSubirArchivo",
//				 iconCls:'btnSubirArchivoIconCls'
//			 }
//		 });

		 /*
		  * Paneles
		  */
//		 var form0=new Ext.form.Panel({
//			 id:'form0',
//			 layout:'hbox',
//			 name:'form0',
//			 cls:'formsCls',
//			 border:0,
//			 height:40,
//			 items:[subir0,progressBar0,imgEstado0]
//		 });
		 var panelApplet=new Ext.panel.Panel({
			 id:'panelApplet',
			 name:'panelApplet',
			 title:'Carga masiva',
			 autoScroll:false,
			 autoEl: {
				 id:'divApplet',
				 name:'divApplet',
				 tag: 'div',
				 html: '<div style="height:100%; scrolling="yes" overflow:scroll !important;">'+
				 '<iframe src="PageDocumentUploadApplet.jsp?select='+select+'"  style="width:100%;height:100%;border:none;"></iframe></div>'
				 //'<iframe src="http://www.radiza.mx"  style="width:100%;height:100%;border:none;"></iframe></div>'

			 }
		 });
		 var panelHtml=new Ext.panel.Panel({
			 id:'panelHtml',
			 name:'panelHtml',
			 title:'Selección',
			 autoScroll:true,
			 tbar:[btnAgregarFila,'-',lblTotal],
			 bbar:[progressBarTotal,'->',checkCerrarAlEnviar, btnEnviarFotos],
			 items:[
//			        form0
			        ]
		 });
		 var panelTab=new Ext.tab.Panel({
//			 id:'panelTab',
			 name:'panelTab',
			 items:[panelHtml,panelApplet]
		 });

		 /*
		  * Funciones
		  */
		 function agregarFile(numeroNombre){
//			 numeroNombre=numeroNombre+1;
			 var btnEliminar=new Ext.button.Button({
				 id:'btnEliminar'+numeroNombre.toString(),
				 name:'btnEliminar'+numeroNombre.toString(),
				 cls:'btnEliminarCls',
				 iconCls:'btnEliminarIconCls',
				 handler:function(){
					 if(contador>1){
						 panelHtml.remove(Ext.getCmp('form'+numeroNombre.toString()));
						 contador--;
						 if(contador>1){	
							 if(Ext.getCmp('btnEliminar'+(numeroNombre-1).toString())){
								 Ext.getCmp('btnEliminar'+(numeroNombre-1).toString()).setVisible(true);
								 Ext.getCmp('btnEliminar'+(numeroNombre-1).toString()).setDisabled(false);

							 }
						 }
						 lblTotal.setText('<font class="totalTexto">Total filas: </font><font class="totalNumero">'+contador.toString()+'</font>',false);
					 }
				 }
			 });

			 var f=new Ext.form.field.File({
				 id:subirGenericoNo+numeroNombre.toString(),
				 name: subirGenericoNo+numeroNombre.toString(),
				 linea: numeroNombre,
				 fieldLabel: 'Selecciona imagen',
				 labelWidth: 150,
				 msgTarget: 'side',
				 allowBlank: true,
				 cls:'subirCls',
				 width:400,
				 buttonText: '',
				 listeners:{
					 change:function( me, value, eOpts ){
						 if((me.linea)==contador-1)
							 agregarFile(contador);
					 }
				 },
				 buttonConfig:{
					 xtype: "button",
					 id: "btnSubirArchivo"+numeroNombre.toString(),
					 iconCls:'btnSubirArchivoIconCls'
				 }
			 });

			 var i= Ext.create('Ext.Img', {
				 id:imgOcultarNo+numeroNombre.toString(),
				 name:imgOcultarNo+numeroNombre.toString(),
				 width:16,
				 height:16,
				 cls:'imgEstados',
				 hidden:true,
				 src: '../imagenes/iconos/flechaArriba16.png'
			 });

			 var p=Ext.create('progressBarCustom', {
				 id:nombreGenericoNo+numeroNombre.toString(),
				 name:nombreGenericoNo+numeroNombre.toString(),
				 width: anchoProressBar,
				 value:0,
				 hidden:true,
				 height:8,
				 cls:'progressBarsCls',
				 componentCls:'progressBarsComponentCls',
				 color:'#52ff5e'
			 });

			 var pa=Ext.create('Ext.form.Panel',{
				 id:'form'+numeroNombre.toString(),
				 accion: 'formPanelFilaImagen',
				 name:'form'+numeroNombre.toString(),
				 layout:'hbox',name:'form'+numeroNombre.toString(),
				 cls:'formsCls',
				 border:0,
				 height:40,
				 items:[f,p,i,btnEliminar]
			 });		
			 
			 panelHtml.add(pa);			
			 var btnABorrar=0;
			 
			 if (contador >1)
				 btnABorrar=contador-1;
			 
			 if(Ext.getCmp('btnEliminar'+btnABorrar.toString())){
				 Ext.getCmp('btnEliminar'+btnABorrar.toString()).setVisible(false);
				 Ext.getCmp('btnEliminar'+btnABorrar.toString()).setDisabled(true);
			 }
			 contador++;
			 lblTotal.setText('<font class="totalTexto">Total filas: </font><font class="totalNumero">'+(contador).toString()+'</font>',false);
		 }
		 
		 function enviarFotos(){
			 for(var i=0;i<contador;i++){
//				 Ext.getCmp(subirGenericoNo+i.toString()).el.mask();
				 if(i>0){
					 if(Ext.getCmp('btnEliminar'+i.toString())){
						 Ext.getCmp('btnEliminar'+i.toString()).setDisabled(true);
						 Ext.getCmp('btnEliminar'+i.toString()).setVisible(false);
					 }
				 }
			 }
			 var arreglo=new Array();
			 for(var i=0;i<contador;i++){
				 var s=Ext.getCmp(subirGenericoNo+i.toString());
				 if(s.getValue()!=""){
					 arreglo.push('form'+i.toString());
				 }
				 else{
					 panelHtml.remove('form'+i.toString());
				 }
			 }
			 //Agregar datos a progress bar	
			 lblTotal.setText('<font class="totalTexto">Total filas: </font><font class="totalNumero">'+(arreglo.length).toString()+'</font>',false);
			 
			 submitF(arreglo,0);
		 }
		 
		 function submitF(arregloPanel,posicion){
			 var nombrePanel=arregloPanel[posicion];
			 var pos=parseInt(arregloPanel[posicion].substring(4,arregloPanel[posicion].length));
			 var form=Ext.getCmp(nombrePanel);
			 var pr=Ext.getCmp(nombreGenericoNo+pos.toString());
			 pr.setStart(true);
			 posicion++;
			 form.submit({
				 url: rutaAddPageKeeper+'?select='+select+'&redirect=true',
				 success: function (formPanel, action) {
					 var data = Ext.decode(action.response.responseText);
					 //Sumar a progressBar
					 progressBarTotal.updateProgress((posicion/arregloPanel.length),'Subiendo imagenes',true);

					 pr.setEnd(true);
					 if(posicion<arregloPanel.length){
						 submitF(arregloPanel,posicion);
					 }
					 else{
						 var data = Ext.decode(action.response.responseText);
						 progressBarTotal.updateProgress(1,'Finalizado',true);
						 Ext.example.msg("Finalizado",'Espera un momento...');
						 var task = new Ext.util.DelayedTask(function(){
							 panelHtml.removeAll();
							 contador = 0;
							 agregarFile(contador);
							 progressBarTotal.updateProgress(0,'...',true);
							 if(checkCerrarAlEnviar.getValue())
								 Ext.getCmp('ventanaAgregarimagen').close();

							 StoreMiniaturas.on({
								 load: function(store, records, successful, operation, eOpts){
									 lblPaginas.setText("de "+store.getCount()+' páginas');
								 },
								 single: true
							 });
							 StoreMiniaturas.load({params: {action:actionStoreBuffer,select:select,start: 0, limit:miniaturasPorCarga}});

						 });
						 task.delay(2000);
					 }
					
				 },
				 failure: function (formPanel, action) {
					 var data = Ext.decode(action.response.responseText);
					 Ext.example.msg("Error",data.message);
				 }
			 });
		 }

		 /**
		  * Termina sección importada para Ventana de Agregar Imágenes
		  */
		 
		 function crearVentanaAgregarImagen(){
			 if(!Ext.getCmp('ventanaAgregarimagen')){
				 Ext.create('Ext.window.Window', {
					 id:'ventanaAgregarimagen',
					 closeAction: 'hide',
					 title: 'Agregar imágenes',
					 height: 400,
					 width: 620,
//					 constrain:true,
					 layout:'fit',
					 draggable:true,
					 resizable:false,
					 items:[panelTab]
				 }).show();
				 agregarFile(contador);
			 } else {
				 Ext.getCmp('ventanaAgregarimagen').show(); 
			 }
		 }
	});
