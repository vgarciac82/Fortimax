Ext.define('FMX.store.Visualizador.DetallesDocumento', {
	extend: 'Ext.data.Store',
	model:'FMX.model.Visualizador.DatoGeneral',
	proxy: { 
			type: 'ajax', 
			url: rutaServlet,							
			reader: { 										
				type: 'json', 
				root: 'datos'
			},
			extraParams: 
			{
				action: 'getDGeneral',
				select: null
			} 
	},
	autoLoad:false
});