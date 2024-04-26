Ext.define('FMX.store.IfimaxDocumentos.MiniaturasOriginales', {
	extend: 'Ext.data.Store',
	model: 'FMX.model.IfimaxDocumentos.MiniaturaOriginal',
	sortInfo: { field: 'pagina', direction: 'DESC'}, 
	proxy: {

		type: 'ajax',
		url: basePath + '/Admin/OperacionesServlet', //TODO Migrar servlet a MVC
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