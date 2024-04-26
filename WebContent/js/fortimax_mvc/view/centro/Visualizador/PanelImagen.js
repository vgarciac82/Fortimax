Ext.define('FMX.view.centro.Visualizador.PanelImagen', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.panelimagen',
	title: 'Imagen',
//	hidden:externo,
//	region: 'center',
//	unstyled:true,
//	border:false,
	layout: 'border',
//	layout: {
//	    type: 'vbox',
//	    align : 'stretch'
//	},
	
	initComponent: function() {
		var me = this;
		
		Ext.apply(me, {
			items:[
			       Ext.widget('panelimagencontenedorizquierdo'),
			       Ext.widget('panelminiaturas')
			       ]
		});
		
        me.callParent(arguments);
    }
	
});