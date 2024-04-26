Ext.define('FMX.view.centro.Visualizador.PanelLienzo', {
	extend: 'Ext.form.Panel',
	alias:'widget.panellienzo',
	layout:'fit',
	autoScroll: true, 
	autorender: true,
	region: 'center',
	
	//hidden:externo
	initComponent: function() {
		var me = this;
		
		Ext.apply(me, {
			flex: 7,
			bodyStyle: {
			    background: '#a8b4c2'
			},
			items: 
				[
				 Ext.widget('drawimagen')
				 ]
		});
		this.callParent(arguments);
	}
});