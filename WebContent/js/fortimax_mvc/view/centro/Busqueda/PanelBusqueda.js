Ext.define('FMX.view.centro.Busqueda.PanelBusqueda', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.panelbusqueda',

   	name: 'PanelBusqueda',
   	layout: 'fit',
   	unstyled: true,

	initComponent: function() {	
		//console.log('initComponent: PanelBusqueda',this);
		this.callParent(arguments);
	}
});