Ext.define('FMX.view.Utils.SelectorRangoFecha', {
    extend: 'Ext.form.field.Date',
    alias: 'widget.selectorrangofecha',
    
    minDate: '2014-01-01',
    maxDate: '2015-01-01',
   	values: ['2014-01-01','2015-01-01'],
	dateFormat: 'Y-m-d',
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
		}),	
		//console.log('initComponent: SelectorFecha',this);
		this.callParent(arguments);
    }
});