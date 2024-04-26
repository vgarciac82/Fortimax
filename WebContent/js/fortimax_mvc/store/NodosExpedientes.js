Ext.define('FMX.store.NodosExpedientes', {
		extend:'Ext.data.TreeStore',
		model:'FMX.model.FortimaxNodo',
		
    	proxy: {
				requestMethod : 'GET',
				reader : {
					type : 'json'
				},
				type : 'ajax',
				url: rutaArbolServlet,
				extraParams : {
					action : 'getExpediente',
					select : 'GP_AUDIT_INTERNA_G1'
				}
        },
        root: {
            expanded: true
        },
        folderSort: true,
        sorters: [{
            property: 'text',
            direction: 'ASC'
        }]
});