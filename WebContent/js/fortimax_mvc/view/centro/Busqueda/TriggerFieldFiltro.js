Ext.define('FMX.view.centro.Busqueda.TriggerFieldFiltro', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.triggerfieldfiltro',
		
		fieldLabel:'Refinar',
		triggerTip: 'Click para filtrar.',
		triggerBaseCls :'x-form-trigger',
		triggerCls:'x-form-clear-trigger',
		width:465,
		labelPad:0,
		labelWidth:50,
		emptyText:'Escriba para buscar en todos los campos.',
    	
    	initComponent: function() {
	    var me = this;
	    
	    Ext.apply(me, {
			    onTriggerClick: function() {
			        me.setValue("");
			    }	
      	}),	
		//console.log('initComponent: TriggerFieldFiltro',this);
		this.callParent(arguments);
    	}
});