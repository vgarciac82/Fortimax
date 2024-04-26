Ext.define('FMX.view.IfimaxDocumentos.TabPanelIfimaxDocumentos', {
	extend: 'Ext.tab.Panel',
	alias : 'widget.tabPanelIfimaxDocumentos',
	region: 'center',
	height: 300,
//	require:['FMX.store.IfimaxDocumentos.ArbolExpedientes'],
	activeTab: 0,
	items: [{
		title: 'Documentos',
		tbar: ['->',{
			xtype:'button',
			scale: 'large',
			accion: 'GuardarCambios',
			iconCls: 'btnGuardar'
		},{
			xtype:'button',
			scale: 'large',
			accion: 'CancelarCambios',
			iconCls: 'btnCancelar'
		}],
		layout: 'fit',
		items: [{
			xtype: 'panel',
			accion: 'panelDocumentos',
			autoScroll : true,
			height: 350,
			layout: {
				type: 'anchor'
//					align: 'stretch' //los textfield ocupan todo el ancho con vbox
			}
		,items:[{}]
		}]
	}
	,{
	title: 'Expedientes'
		,items: [{
			xtype: 'panel',
			autoScroll : true,
			height: 350,
			layout: {
				type: 'anchor'
//					align: 'stretch' //los textfield ocupan todo el ancho con vbox
			}
			,items:[{
				xtype: 'container',
				layout: 'hbox',
//				title: 'Datos de Expediente',
				items :[{
					xtype: 'treepanel',
					title: 'Arbol de expedientes',
					margin: '5 5 5 5',
					accion: 'TreePanelExpedientes',
					store: Ext.create('FMX.store.IfimaxDocumentos.ArbolExpedientes'),
					rootVisible: false,
					width: 500,
					height: 300
				},{
					xtype: 'grid',
					title: 'Detalles de nodo',
					accion: 'detallesElementoArbol',
					store: Ext.create('FMX.store.IfimaxDocumentos.DetallesElemento'),
					margin: '5 5 5 5',
					width: 500,
					height: 300,
					layout:'fit',
					columns: [
					          { header: 'Nombre',  dataIndex: 'name', width:125, tdCls:'column_bold' },
					          { header: 'Valor', dataIndex: 'value', flex: 1}
					          ]
				}]
			}]
		}]
	}
	]
});