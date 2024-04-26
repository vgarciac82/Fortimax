Ext.define('FMX.model.IfimaxDocumentos.Documento', {
	extend: 'Ext.data.Model',
	fields: 
		[
		 {name: 'nodo', type: 'string'},
		 {name: 'nombre', type: 'string'},
		 {name: 'descripcion', type: 'string'},
		 {name: 'existencia', type: 'boolean'},
		 {name: 'soloLectura', type: 'boolean'},
		 {name: 'src', type: 'string'}
		 ]
});