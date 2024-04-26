Ext.define('FMX.view.Utils.SelectorNumerico', {
    extend: 'Ext.form.field.Number',
    alias: 'widget.selectornumerico',
	
	decimalSeparator:'.',
	decimalPrecision:2,
	hideTrigger: true,
	keyNavEnabled: false,
	mouseWheelEnabled: false,
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
			allowDecimals : this.fortimax_campo.tipo=='Decimal'||this.fortimax_campo.tipo=='Doble'
		}),
		//console.log('initComponent: SelectorNumerico',this);
		this.callParent(arguments);
    }
});