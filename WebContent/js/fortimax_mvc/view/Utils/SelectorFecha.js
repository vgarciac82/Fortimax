Ext.define('FMX.view.Utils.SelectorFecha', {
    extend: 'Ext.form.field.Date',
    alias: 'widget.selectorfecha',
    
    altFormats:'Y-m-d',
	submitFormat: 'Y-m-d',
	format:'Y-m-d',
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
		}),	
		//console.log('initComponent: SelectorFecha',this);
		this.callParent(arguments);
    }
});