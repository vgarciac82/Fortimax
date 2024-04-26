Ext.define('FMX.view.Utils.LineaTexto', {
    extend: 'Ext.form.field.Text',
    alias: 'widget.lineatexto',
    
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
		}),
		//console.log('initComponent: LineaTexto',this);
		this.callParent(arguments);
    }
});