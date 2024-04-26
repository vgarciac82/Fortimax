Ext.define('FMX.view.Utils.SelectorLista', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.selectorlista',

	displayField: 'nombre',
	valueField: 'nombre',
	
    initComponent: function() {
    	FMX.view.Utils.CampoDinamico.applyDefaults(this);
    	Ext.apply(this, {
    		editable : false,
    		emptyText:'Selecciona...',
			store : this.createStore(this)
		}),
		//console.log('initComponent: SelectorLista',this);
		this.callParent(arguments);
    },
    
    createStore: function(parametros) {
	    return Ext.create('Ext.data.Store', { 
        	model: Ext.define('model-'+parametros.id, {
	    		extend: 'Ext.data.Model',
	     		fields: [
	        		{name: 'id', type: 'string'},     
	         		{name: 'nombre', type: 'string'}
	     		]
	 		}), 
        	proxy: { 
            	type: 'ajax', 
            	url: rutaServlet,//'../jsonPrueba/lista.json',  							
            	reader: { 										
                	type: 'json', 
                	root: 'lista'
        		},
        		extraParams: {
	    			action: 'getListas',
	        		select:parametros.fortimax_campo.lista
	        	}
        	},
        	autoLoad:false
    	});
    }
});