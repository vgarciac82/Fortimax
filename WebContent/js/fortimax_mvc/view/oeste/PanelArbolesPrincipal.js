Ext.define('FMX.view.oeste.PanelArbolesPrincipal', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.PanelArbolesPrincipal',
	
	requires: [
	'FMX.view.oeste.Arbol',
	'FMX.view.oeste.GraficaEspacioUsuario',
	'FMX.store.NodosMisDocumentos',
	'FMX.store.NodosExpedientes',
	'FMX.store.NodosGavetas'	
	],
	
	id:'PanelArbolesPrincipal',
	region: 'west',
	stateId: 'navigation-panel',
	title: 'Bienvenido: '+ usuario,
	split: true,
	width: 260,
	minWidth: 260,
	//maxWidth: 250,
	collapsible: true,
	animCollapse: true,
	layout: {
		type: 'accordion',
		align: 'stretch'
	},
	dockedItems: [{
		dock: 'top',
		xtype: 'toolbar',
		items: [ 
		
				//Boton Buscar
				Ext.create('Ext.Button', {
				id:'btnBuscarDocumentos',
				accion:'toolBarBuscarDocumento',
				hidden: false,//ifimax,
				text: null,
				iconCls: 'btnbuscar',
				scale: 'medium',
				handler: function() {
					//alert('Por implementar !');
				}
				}),

				//Boton Applet FortimaxUpload
				Ext.create('Ext.Button', {
				id:'btnappletfortimaxupload',
				text: null,
				iconCls: 'btnappletfortimaxupload',
				scale: 'medium',
				handler: function() {
					//crearVentana('../jsp/PageDocumentUploadApplet.jsp?select='+titulo_aplicacion+'_G'+id_gabinete+'C1D0&appletExtended=true', "Applet de carga",500, 600);
				}
				}),
				
				//Boton Reporte
				Ext.create('Ext.Button', {
				id:'btnReporte',
				hidden: false,//ifimax,
				text: null,
				iconCls: 'btnReporte',
				scale: 'medium',
				handler: function() {
					alert('Por implementar !');;
				}
				}),

				//Boton mas
				Ext.create('Ext.Button', {
				id:'btnmas',
				hidden: false,//ifimax,
				text: null,
				iconCls: 'btnEngrane',
				scale: 'medium',
				menu : {
						items: [{text:'Copiar', iconCls: 'btncopiar'},
								{text:'Cortar', iconCls: 'btncortar'},
								{text:'Pegar', iconCls: 'btnpegar'},
								{text:'Otras', iconCls: 'settings'}
								]
						},
				handler: function() {
					//alert('Por implementar !');
					}
				}),
				
				//Separador y alineador btn actualizar
				'->','-',
				
				//Boton Actualizar
				Ext.create('Ext.Button', {
				id:'btnactualizarArbol',
				accion:'toolbarActualizartArboles',
				hidden: false,//ifimax,
				text: null,
				iconCls: 'btnactualizar',
				scale: 'medium',
				handler: function() { 
					}
				})			
			]
	}],
	
	initComponent: function() {
	    var me = this;
	
		var ArbolExpedientes = Ext.create('FMX.view.oeste.Arbol', {
			id: 'ArbolExpediente',
			title: 'Expediente',
			iconCls: 'expediente'
		});		
		
		var ArbolGavetas = Ext.create('FMX.view.oeste.Arbol', {
			id: 'ArbolGavetas',
			title: 'Gavetas',
			iconCls: 'gaveta'
		});	
		
		var ArbolMisDocumentos = Ext.create('FMX.view.oeste.Arbol', {
			id: 'ArbolMisDocumentos',
			title: 'Mis Documentos',
			iconCls: 'misdoc'
		}); 
		
		Ext.apply(me, {
		items: 
		[
			 ArbolMisDocumentos	
			,ArbolExpedientes			
			,ArbolGavetas					
		]
        });

	this.callParent(arguments);
	}
});