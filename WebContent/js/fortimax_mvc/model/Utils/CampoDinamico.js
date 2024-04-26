Ext.define('FMX.model.Utils.CampoDinamico', {
	extend: 'Ext.data.Model',
    fields:[
			        {name: 'nombre', type: 'string'},
			        {name: 'etiqueta', type: 'string'},
			        {name: 'orden', type: 'int'},
			        {name: 'longitud', type: 'int'},
			        {name: 'valor', type: 'string'},
			        {name: 'tipo'},
			        {name: 'indice'},
			        {name: 'requerido', type: 'bool'},
			        {name: 'editable', type: 'bool'},
			        {name: 'lista',type:'string'}
			],
	idProperty:'nombre'
});