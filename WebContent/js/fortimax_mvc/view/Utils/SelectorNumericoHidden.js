Ext.define('FMX.view.Utils.SelectorNumericoHidden', {
    extend: 'Ext.form.field.Hidden',
    alias: 'widget.selectornumericohidden',
	
	decimalSeparator:'.',
	decimalPrecision:2,
	hideTrigger: true,
	keyNavEnabled: false,
	mouseWheelEnabled: false,
	hidden: true,
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
			allowDecimals : this.fortimax_campo.tipo=='Decimal'||this.fortimax_campo.tipo=='Doble'
		}),
		//console.log('initComponent: SelectorNumerico',this);
		this.callParent(arguments);
    }
});