Ext.define('FMX.utils.Visualizador.Utils', {
	statics: {
		textosemana:["Domingo",
		             "Lunes",
		             "Martes",
		             "Miércoles",
		             "Jueves",
		             "Viernes",
		             "Sábado"
		 ],

         textomes: ["Enero",
                    "Febrero",
                    "Marzo",
                    "Abril",
                    "Mayo",
                    "Junio",
                    "Julio",
                    "Agosto",
                    "Septiembre",
                    "Octubre",
                    "Noviembre",
                    "Diciembre"]
	},

	config: {
		textosemana:["Domingo",
		             "Lunes",
		             "Martes",
		             "Miércoles",
		             "Jueves",
		             "Viernes",
		             "Sábado"
		 ],

         textomes: ["Enero",
                    "Febrero",
                    "Marzo",
                    "Abril",
                    "Mayo",
                    "Junio",
                    "Julio",
                    "Agosto",
                    "Septiembre",
                    "Octubre",
                    "Noviembre",
                    "Diciembre"]
	},

	constructor: function(config) {
		this.initConfig(config);
		return this;
	}
});