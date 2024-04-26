Ext.define('FMX.model.Visualizador.AtributoDocumento', {
	extend: 'Ext.data.Model',
	fields: [
	         {name:'atributo', type:'string'},
	         {name:'activo', type:'bool'},
	         {name:'valor', type:'string'},
	         {name:'modificable', type:'bool'},
	         {name:'descripcion', type:'string'}
	         ]
});