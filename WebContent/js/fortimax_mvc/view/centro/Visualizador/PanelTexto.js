Ext.define('FMX.view.centro.Visualizador.PanelTexto', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.paneltexto',
	autoScroll: true,
	title: 'Texto',
	border:false,
	layout:'fit',
	tbar:[
		'->',
		{
			xtype: 'button',
			text: 'Guardar',
			formBind: false,
			iconCls:'btnGuardarOCRIcon'
		}
	],
	items:[
		{
			xtype: 'htmleditor'
		}
	],
	
	initComponent: function() {	
		console.log('initComponent: PanelTexto',this);
		this.callParent(arguments);
	}
	
});