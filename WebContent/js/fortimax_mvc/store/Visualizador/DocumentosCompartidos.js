Ext.define('FMX.store.Visualizador.DocumentosCompartidos', {
	extend:'Ext.data.Store',
	model:'FMX.model.Visualizador.DocumentoCompartido',
	proxy: { 
		type: 'ajax', 
		url: rutaServletCompartirDocumento,  							
		reader: { 										
			type: 'json', 
			root: 'documento'
		},
		extraParams: 
		{
			action: 'getSharedDocumentInfo'
		}

	}
});