Ext.define('FMX.store.EspacioAsignado', {
extend:'Ext.data.Store',
model: 'FMX.model.EspacioAsignadoUsuario', 

//    fields: ['nombre', 'espacio_utilizado', 'espacio_disponible', 'unidades'],
//   data: [{ 'nombre': 'Espacio',   'espacio_utilizado':101, 'espacio_disponible': 160, 'unidades': 'MB' }]

proxy: { 
	type: 'ajax', 
	url: './data/espacioUsuario.json',
	//url: rutaArbolServlet,
	reader: { 										
	type: 'json', 
	root: 'data'
	},
	extraParams: {
		  action: 'getDatosNodo',
		  select:''
	} 
},
autoLoad:true
});