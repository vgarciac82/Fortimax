Ext.define('FMX.store.Visualizador.CamposPlantilla', {
	extend: 'Ext.data.Store',
	model:'FMX.model.Utils.CampoDinamico',
	sorters: [{
		property: 'orden',
		direction:'ASC'
	}],
	proxy:{
			type:'ajax',
			url: rutaServlet,
			reader:{
					type:'json',
					root:'campos'
			},
			extraParams:{
					action:'getDatosDoc'
			}
		  },
	autoLoad:false
});