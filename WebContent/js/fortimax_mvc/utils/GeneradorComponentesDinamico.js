Ext.define('FMX.utils.GeneradorComponentesDinamico', {
	views: [
		'Utils.CampoDinamico'
	],
	
    statics: {
   		generaCampo: function(parametros) {
			return Ext.create('FMX.view.Utils.CampoDinamico',parametros);
    	},
    	
    	generaCampos: function(campos,datos) {
    		for(var i=0;i<campos;i++){
				var campo = storeCamposGaveta.getAt(i);
				console.log(campo.data);
				if(data==null)
					var valor = campo.get('Valor');
				else
					var valor = data[campo.get('Nombre')];
				componente.add({
					xtype : 'campodinamico',
					tipo : campo.get('Tipo'),
					nombre : campo.get('Nombre'),
					etiqueta : campo.get('Etiqueta'),
					tamano : campo.get('Tamano'),
					valor : valor,
					requerido : campo.get('Requerido'),
					editable : campo.get('Editable'),
					lista : campo.get('Lista')
				})
			}
    	},
    	
    	generaFieldSetDinámico: function(parametros) {
    		var fieldset = Ext.create('Ext.form.FieldSet', {
    			cls:'fieldSet',
	    		autoScroll:true
    		});
    		for(var i=0;i<parametros.campos.length;i++){
    			if(parametros.datos!=null) {
    			
    			}
    		}
       		return fieldset;
    	}
    },
    
    config: {
    },
    	
    constructor: function(config) {
        this.initConfig(config);
        return this;
    }
});