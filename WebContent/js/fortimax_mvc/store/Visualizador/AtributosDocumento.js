Ext.define('FMX.store.Visualizador.AtributosDocumento', {
	extend: 'Ext.data.Store',
	model:'FMX.model.Visualizador.AtributoDocumento',
	
	proxy: {
		type: 'ajax',
		url: rutaServletAtributos,	//'../JsonPrueba/privilegios.json',
		reader: {
			type: 'json',
		    root: 'atributos'
		},
		extraParams: {
			action: "getAtributos",
		    select: null
		}
	},
	autoLoad: false
});