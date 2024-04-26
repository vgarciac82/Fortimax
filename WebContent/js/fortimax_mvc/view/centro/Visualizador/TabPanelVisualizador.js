Ext.define('FMX.view.centro.Visualizador.TabPanelVisualizador',{
	extend: 'Ext.tab.Panel',
	alias: 'widget.tabpanelvisualizador',
	closable:true,
//	layout: 'border',

	initComponent: function() {
		var me = this;

		Ext.apply(me, {
//			height: '100%',
			items: [
			        Ext.widget('panelimagen'),
			        Ext.widget('panelplantilla'),
			        Ext.widget('paneltexto'),
			        Ext.widget('paneldetalles')
			        ]
		});

		me.callParent(arguments);
	}
});