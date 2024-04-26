Ext.define('FMX.controller.VariablesEntorno', {
    statics: {
        esFortimax: true,
        esIfimax: false,
		nodoSeleccionado: null,
		arbolSeleccionado: null
    },

    config: {
        esFortimax: true,
        esIfimax: false,
		nodoSeleccionado: null,
		arbolSeleccionado: null
    },

    constructor: function(config) {
        this.initConfig(config);
        return this;
    }
});