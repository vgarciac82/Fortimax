Ext.define('FMX.view.Utils.SelectorOpcionMultiple', {
    extend: 'Ext.form.field.Radio',
    alias: 'widget.selectoropcionmultiple',
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
		//console.log('initComponent: SelectorOpcionMultiple',this);
		this.callParent(arguments);
    }
});