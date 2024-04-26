Ext.define('FMX.store.IfimaxDocumentos.DetallesElemento', {
	extend: 'Ext.data.Store',
	model: 'FMX.model.IfimaxDocumentos.DetalleElemento',
//	sortInfo: { field: 'pagina', direction: 'DESC'}, 
	proxy: { 
		type: 'ajax',
		//url: './IfimaxDocumentosResourceTest/datosNodo.json',
		url: basePath + 'ArbolServlet',  
		reader: { 										
			type: 'json', 
			root: 'items'
		},
    	extraParams: {
	          action: 'getDatosNodo'
//	          select: 'USR_GRALES_G1C0D1
	    }
	}
});