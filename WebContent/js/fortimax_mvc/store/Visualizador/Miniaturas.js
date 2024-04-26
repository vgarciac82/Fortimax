Ext.define('FMX.store.Visualizador.Miniaturas', {
	extend: 'Ext.data.Store',
	model: 'FMX.model.Visualizador.Miniatura',
	sortInfo: { field: 'pagina', direction: 'DESC'}, 
	proxy: {

		type: 'ajax',
		url: operacionesServlet,
		extraParams:{
			action: 'getMiniaturas'
		},
		actionMethods: 'POST',
		reader: {
			type: 'json',
			root: 'miniaturas'
		}
	}
});