Ext.define('FMX.model.Visualizador.DocumentoCompartido', {
	extend: 'Ext.data.Model',
	fields: [
	         {name: 'nombre_documento',  	type: 'string'},
	         {name: 'compartir',  			type: 'string'},
	         {name: 'token',  				type: 'string'},
	         {name: 'nombre_tipo_docto',  	type: 'string'},
	         {name: 'titulo_aplicacion',  	type: 'string'},
	         {name: 'id_gabinete',  			type: 'int'},
	         {name: 'id_carpeta_padre',  	type: 'int'},
	         {name: 'id_documento',  		type: 'int'},
	         {name: 'dateExp',  				type: 'string'},
	         {name: 'houreExp',  			type: 'string'},
	         {name: 'ligaPermisoBajar',  		type: 'string'}
	         ]
});