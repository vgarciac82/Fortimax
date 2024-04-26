Ext.define('FMX.view.Utils.AreaTexto', {
    extend: 'Ext.form.field.TextArea',
    alias: 'widget.areatexto',
	
	grow : false,
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
		}),	
		//console.log('initComponent: AreaTexto',this);
		this.callParent(arguments);
    }
});