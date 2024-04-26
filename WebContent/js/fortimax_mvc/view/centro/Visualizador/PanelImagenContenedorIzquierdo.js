Ext.define('FMX.view.centro.Visualizador.PanelImagenContenedorIzquierdo', {
	extend: 'Ext.panel.Panel',
	alias:'widget.panelimagencontenedorizquierdo',
//	layout:'fit',
//	autoScroll: true, 
//	autorender: true,
	region: 'center',
	layout: 'border',
	
	initComponent: function() {
		var me = this;
		
		Ext.apply(me, {
//			flex: 7,
//			bodyStyle: {
//			    background: '#a8b4c2'
//			},
			items: 
				[
					Ext.widget('toolbarimagen'),
					Ext.widget('panellienzo')
				 ]
		});
		this.callParent(arguments);
	}
});