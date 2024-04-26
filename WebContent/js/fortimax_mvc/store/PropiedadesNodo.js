Ext.define('FMX.store.PropiedadesNodo', {
extend:'Ext.data.Store',
model: 'FMX.model.NodoPropiedades', 
proxy: { 
	type: 'ajax', 
	url: './data/propiedades.json',
	//url: rutaArbolServlet,
		reader: { 										
		type: 'json', 
		root: 'items'
	},
	extraParams: 
	 {
		  action: 'getDatosNodo',
		  select:''
	  } 
} ,
autoLoad:true
});