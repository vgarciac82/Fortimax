Ext.define('FMX.store.IfimaxDocumentos.Documentos', {
	extend: 'Ext.data.Store',
	model: 'FMX.model.IfimaxDocumentos.Documento', 
	proxy: { 
		type: 'ajax', 
		//url: './IfimaxDocumentosResourceTest/data.json',
		url: basePath + 'IfimaxDocumentosServlet',  
		reader: { 										
			type: 'json', 
			root: 'documentos'
		},
    	extraParams: {
	          action: 'getDocumentos'
	    }
	}
});