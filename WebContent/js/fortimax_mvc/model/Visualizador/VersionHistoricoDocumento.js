Ext.define('FMX.model.Visualizador.VersionHistoricoDocumento', {
	 extend: 'Ext.data.Model',
	 fields: [
	          {name:'Id_Version', type:'int'},
	          {name:'Fecha_generacion', type:'string'},
	          {name:'Nombre_Documento', type:'string'},
	          {name:'Descripcion', type:'string'},
	          {name:'numero_paginas', type:'int'},
	          {name:'tamano', type:'string'},
	          {name:'usuario_generador', type:'string'}
	          ]
});
//HistoricoDocumento