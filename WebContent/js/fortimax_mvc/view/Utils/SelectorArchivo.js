Ext.define('FMX.view.Utils.SelectorArchivo', {
    extend: 'Ext.form.field.File',
    alias: 'widget.selectorarchivo',

    etiqueta : 'Selecciona Archivo:',
	buttonConfig: {
		text: '',
		iconCls:'btnAgregarArchivo'
	},

    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
		}),	
		this.callParent(arguments);
    }
});