Ext.define('FMX.store.NodosGavetas', {
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
					action : 'getGavetas'
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