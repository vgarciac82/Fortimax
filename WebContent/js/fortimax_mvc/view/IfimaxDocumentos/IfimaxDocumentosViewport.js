Ext.define('FMX.view.IfimaxDocumentos.IfimaxDocumentosViewport', {
    extend: 'Ext.Viewport',
    layout: 'border',
    
    requires: [
               'FMX.view.IfimaxDocumentos.TabPanelIfimaxDocumentos'
    ],
    
    initComponent: function() {
        var me = this;
        Ext.apply(me, {
		items: 
		[
			{
	        region: 'north',
	        html: '<img src="../imagenes/IfimaxDocumentos/afirme.png" align="left"> <img src="../imagenes/IfimaxDocumentos/fortimax.jpg" align="right">',
//	        autoHeight: true,
	        height: 50,
	        border: false,
	        margins: '0 0 5 0'
	    }, {
	        region: 'south',
	        title: 'Afirme Derechos Reservados 2015',
	        collapsible: true,
	        collapsed: true,
	        html: 'Aviso de Privacidad',
	        split: true,
	        height: 100,
	        minHeight: 100
	    },
			Ext.widget('tabPanelIfimaxDocumentos')
		]
        });
                
        me.callParent(arguments);
    }
});