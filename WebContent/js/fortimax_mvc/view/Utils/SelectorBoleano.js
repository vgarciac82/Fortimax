Ext.define('FMX.view.Utils.SelectorBoleano', {
    extend: 'Ext.form.field.Checkbox',
    alias: 'widget.selectorboleano',
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
    		checked: this.fortimax_campo.valor
		}),	
		//console.log('initComponent: SelectorBoleano',this);
		this.callParent(arguments);
    }
});