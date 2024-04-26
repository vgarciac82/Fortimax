Ext.define('FMX.view.Utils.CampoDinamico', {
    alias: 'widget.campodinamico',
    
    requires: [
    	'FMX.view.Utils.LineaTexto',
    	'FMX.view.Utils.SelectorFecha',
    	'FMX.view.Utils.SelectorRangoFecha',
    	'FMX.view.Utils.AreaTexto',
    	'FMX.view.Utils.SelectorBoleano',
    	'FMX.view.Utils.SelectorNumerico',
    	'FMX.view.Utils.SelectorLista'
    ],
    
    statics: {
    	applyDefaults: function(campoDinamico) {
    		campoDinamico.allowBlank = !campoDinamico.fortimax_campo.requerido,
    		campoDinamico.anchor = '100%';
    		campoDinamico.cls = 'textoForm';
    		//campoDinamico.disabled = !campoDinamico.fortimax_campo.editable;
    		campoDinamico.emptyText = campoDinamico.fortimax_campo.etiqueta;
    		campoDinamico.enforceMaxLength = true;
    		campoDinamico.fieldLabel = campoDinamico.fortimax_campo.etiqueta;
    		campoDinamico.margin = '3 20 3 20';
    		campoDinamico.labelWidth = 175;
    		campoDinamico.maxLength = campoDinamico.fortimax_campo.longitud;
    		campoDinamico.name = campoDinamico.fortimax_campo.nombre;
    		campoDinamico.on({
    			render: function(campoDinamico, eOpts) {
                		Ext.QuickTips.register({
                    		target: campoDinamico.getEl(),
                    		text: campoDinamico.fortimax_campo.descripcion
                		});
        		},
        		scope: this
    		});
    		campoDinamico.readOnly = !campoDinamico.fortimax_campo.editable;
    		if (campoDinamico.fortimax_campo.requerido)
        		campoDinamico.labelSeparator = "<span class='red'>*</span>"+campoDinamico.labelSeparator;
        	//campoDinamico.size = campoDinamico.tamano<36?campoDinamico.tamano:36;
    		campoDinamico.value = campoDinamico.fortimax_campo.valor;
    		//campoDinamico.width = campoDinamico.tamano<35?campoDinamico.labelWidth+10+(campoDinamico.tamano*8):campoDinamico.labelWidth+290;
    	},
    	
    	getFromStore: function (storeCampos,data){
    		var camposDinamicos = [];
			for(var i=0;i<storeCampos.getCount();i++){
				var campo = storeCampos.getAt(i);
				if(data==null){
					var editable = true;
					var valor = campo.get('valor');	
				} else {
					var editable = campo.get('editable');
					var valor = data[campo.get('nombre')];
				}
				camposDinamicos.push({
					xtype : 'campodinamico',
					fortimax_campo: {
						orden : campo.get('orden'),
						tipo : campo.get('tipo'),
						nombre : campo.get('nombre'),
						descripcion : campo.get('nombre'),
						etiqueta : campo.get('etiqueta'),
						longitud : campo.get('longitud'),
						valor : valor,
						requerido : campo.get('requerido'),
						editable : editable,
						lista : campo.get('lista')
					}
				})
			}
			return camposDinamicos;
		}
    },
    
    constructor: function(config) {
         return this.generaCampo(config)
    },
    
    generaCampo: function(config) {
		if(config.fortimax_campo.lista=='-Ninguna-') {
			switch (config.fortimax_campo.tipo) {
				case 'Entero Hidden': return Ext.create('FMX.view.Utils.SelectorNumericoHidden',config);
				/* form.add({
        			xtype: 'hiddenfield',
        			name: 'ID_GABINETE',
        			value: record.data.ID_GABINETE
    			}); */
				case 'Texto': 
				case 'Texto (VarChar)': 
					if(config.fortimax_campo.longitud<37)
						return Ext.create('FMX.view.Utils.LineaTexto',config);
					else
					 	return Ext.create('FMX.view.Utils.AreaTexto',config);
				case 'Fecha': return Ext.create('FMX.view.Utils.SelectorFecha',config);
				case 'Memo': return Ext.create('FMX.view.Utils.AreaTexto',config);
				case 'Check': return Ext.create('FMX.view.Utils.SelectorBoleano',config);
				default: return Ext.create('FMX.view.Utils.SelectorNumerico',config);
			}
		} else{
			return Ext.create('FMX.view.Utils.SelectorLista',config);
		}
		return null;
    }
    	
});

