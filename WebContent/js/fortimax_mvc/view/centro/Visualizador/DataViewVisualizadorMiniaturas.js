Ext.define('FMX.view.centro.Visualizador.DataViewVisualizadorMiniaturas', {
	extend:'Ext.view.View',
	alias: 'widget.dataviewvisualizadorminiaturas',
//	deferInitialRefresh: false,
//	viewConfig: {
//		scrollOffset: 0//or 1, or 2 as mentioned above
//	},
	initComponent: function() {
		var me = this;
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
			'<div class="miniatura" id="{pagina}" align="center">',
			(!Ext.isIE6? '<img width="65" height="91" src="{imagen}" />' :
			'<div style="width:99px;height:95px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'{imagen}\',sizingMethod=\'scale\')"></div>'),
			'<br /><span>Pagina: {pagina}</span><br/>',
			'</div>',
			'</tpl>'
		);

		Ext.apply(me, {
			store: Ext.create('FMX.store.Visualizador.Miniaturas'),
			tpl: tpl,
			autoScroll:true,
			itemSelector : 'div.miniatura',
			overItemCls : 'miniatura-hover',
			height: '100%'
		});

		me.callParent(arguments);
	}

});