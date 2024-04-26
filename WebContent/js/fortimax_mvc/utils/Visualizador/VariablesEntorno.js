Ext.define('FMX.utils.Visualizador.VariablesEntorno', {
    statics: {
//    	var pantalla: Ext.getBody().getViewSize(), //Obtener size del panel padre del visualizador

//FMX.resources.VariablesEntorno
    	miniaturasPorCarga: 10,
    	variacionZoom: 1,
    	actionStoreBuffer: 'getMiniaturas',
    	variacionTbar: 70,
    	AnchoMiniaturas: 115,
    	mover: false,
    	dibujar: false,
//    	document.oncontextmenu: inhabilitar, //TODO: VERIFICAR si entra en las del tipo statics
    	xG: 0, //Especificar más los nombres
    	yG: 0,
    	xF: 0,
    	yF: 0,
    	xFtmp: 0,
    	yFtmp: 0,
    	anchoImg: 0,
    	altoImg: 0,
    	anchoR: 0,
    	altoR: 0,
    	xR: 0,
    	yR: 0,
    	tmpTexto: '',
    	divEliminar: 1,
    	totalPaginas: 0,
    	tabOcr: false,
    	tmpIndex: 0,
    	altoPanel: 0
    },

    config: {
    	miniaturasPorCarga: 10,
    	variacionZoom: 1,
    	actionStoreBuffer: 'getMiniaturas',
    	variacionTbar: 70,
    	AnchoMiniaturas: 115,
    	mover: false,
    	dibujar: false,
//    	document.oncontextmenu: inhabilitar, //TODO: VERIFICAR si entra en las del tipo statics
    	xG: 0, //Especificar más los nombres
    	yG: 0,
    	xF: 0,
    	yF: 0,
    	xFtmp: 0,
    	yFtmp: 0,
    	anchoImg: 0,
    	altoImg: 0,
    	anchoR: 0,
    	altoR: 0,
    	xR: 0,
    	yR: 0,
    	tmpTexto: '',
    	divEliminar: 1,
    	totalPaginas: 0,
    	tabOcr: false,
    	tmpIndex: 0,
    	altoPanel: 0
    },

    constructor: function(config) {
        this.initConfig(config);
        return this;
    }
});