Ext.define('FMX.model.Visualizador.Miniatura', {
	extend: 'Ext.data.Model',
	fields: [
	         {type: 'string', name: 'pagina'},
	         {type: 'string', name: 'ancho'},
	         {type: 'string', name: 'alto'},
	         {type: 'string', name: 'nombre'},
	         {type: 'string', name: 'calidad'},
	         {type: 'string', name: 'imagen'},
	         {type: 'string', name: 'imagenPagina'}
	         ],
	         idProperty:'ID'
});