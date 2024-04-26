Ext.define('FMX.store.Visualizador.HistoricoDocumento', {
	extend: 'Ext.data.Store',
	model:'FMX.model.Visualizador.VersionHistoricoDocumento',
	
	proxy: {
		type: 'ajax',
		url: rutaServletAtributos,	
		reader: {
			type: 'json',
		    root: 'historico'
		},
		extraParams: 
			{
		    	action: "getHistorico",
		        select: null
		    }
	},
	autoLoad:false
});