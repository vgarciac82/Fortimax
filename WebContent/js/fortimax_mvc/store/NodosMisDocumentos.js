Ext.define('FMX.store.NodosMisDocumentos', {
			extend : 'Ext.data.TreeStore',
			model : 'FMX.model.FortimaxNodo',

			proxy : {
				requestMethod : 'GET',
				reader : {
					type : 'json'
				},
				type : 'ajax',
				url: rutaArbolServlet,
				extraParams : {
					action : 'getMisDocumentos',
					usuario : usuario
				}
			},
			root : {
				expanded : true
			},

			folderSort : true,
			sorters : [{
						property : 'text',
						direction : 'ASC'
					}]
		});