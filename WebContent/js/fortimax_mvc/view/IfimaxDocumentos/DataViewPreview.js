Ext.define('FMX.view.IfimaxDocumentos.DataViewPreview', {
	extend: 'Ext.view.View',
	alias : 'widget.dataviewpreview',
//	store: Ext.data.StoreManager.lookup('imagesStore'),
//	deferInitialRefresh: false,
	autoScroll: true,
	multiSelect: true,
	layout: 'fit',
	height: 144,
	width: 540,
	emptyText: ' Sin imágenes que mostrar.',
	trackOver: true,
//	itemSelector: 'div.miniatura',
//	overItemCls : 'miniatura-hover',
	cls: 'dataViewStyle',
	initComponent: function() {
		var me = this;
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="miniatura" style="margin-left: 5px; margin-right: 5px; margin-bottom: 5px; margin-top: 5px; display: inline-block;">',
			    		'<img src="{src}" width="80px" height="100px"/>',
			    		'<br/><span>Página: {#}</span>',
				'</div>',
			'</tpl>'
    	);
		
		Ext.apply(me, {
//			store: Ext.create('FMX.store.IfimaxDocumentos.ImagenesLocales'),
			tpl: tpl,
			autoScroll:true,
			itemSelector : 'div.miniatura',
			overItemCls : 'miniatura-hover'
		});

		me.callParent(arguments);
	}
});