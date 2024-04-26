Ext.define('FMX.view.Viewport', {
    extend: 'Ext.Viewport',    

    requires: [
	'FMX.view.ContainerPrincipal'
    ],
    
    layout: 'fit',
    
    initComponent: function() {
        var me = this;
    	Ext.apply(me, {
    		items:[
    		       Ext.widget('containerprincipal')
    		]
    	});
        me.callParent(arguments);
    }

});
