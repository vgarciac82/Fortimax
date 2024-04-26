Ext.define('FMX.view.centro.Visualizador.PanelPlantilla', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.panelplantilla',
	autoScroll: true,
	title: 'Plantilla',
	border:false,
	layout:'anchor',
	items:[
		{
			xtype:'form',
			cls:'form',
			//title: 'Datos de documento',
			layout:'anchor',
			width: '100%',
			scroll:true,
			autoScroll:true,
			tbar: [
				{
					xtype: 'button',
					text: 'Exportar',
					iconCls:'btnExportar',
					disabled:true
				},
				'->',
				{
					xtype: 'button',
					text: 'Editar',
					accion: 'EditarDatosDocumento',
					enableToggle: true,
					iconCls:'btnEditar'
				},
				' ',
				{
					xtype: 'button',
					text: 'Guardar',
					accion: 'GuardarDatosDocumento',
					iconCls:'btnGuardar',
					disabled : true
				}
			],
			items:[
				{
					xtype:'fieldset',
					title:'',
					cls:'fieldS'
				}
			]
		}
	],
	
	initComponent: function() {	
		console.log('initComponent: PanelPlantilla',this);
		this.callParent(arguments);
	}
	
});