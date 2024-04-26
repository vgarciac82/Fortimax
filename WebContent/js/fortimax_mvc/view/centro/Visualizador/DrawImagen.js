Ext.define('FMX.view.centro.Visualizador.DrawImagen', {
	extend: 'Ext.draw.Component',
	alias: 'widget.drawimagen',
	viewBox: false,
	style:'background-color:#a8b4c2;',
	cls:'panImgCls',
	autoRender: true,
	height: 300, //TODO
	width: 300,
	layout: 'fit',
	
	initComponent: function() {
		var me = this;
//		Ext.apply(me, { 
//		});

		me.callParent(arguments);
	}

});