Ext.define('FMX.view.sur.PanelInferiorPrincipal', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.PanelInferiorPrincipal',

	id:'PanelInferiorPrincipal',
	region: 'south',
	frame:true,
	height: 70,
	
	initComponent: function() {

	this.callParent(arguments);
	}
});