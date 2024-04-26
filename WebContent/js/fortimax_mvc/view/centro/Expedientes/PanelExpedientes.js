Ext.define('FMX.view.centro.Expedientes.PanelExpedientes', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.panelexpedientes',

   	name: 'PanelExpedientes',
   	layout: 'fit',
   	unstyled: true,
   	closable: true,
   	config : {
        gaveta : null
    },
	getItems: function(parametros) {
    	panelbusqueda = 
        {
			xtype: 'panelbusqueda',
			columnas: {
				type: 'ajax',
				url: basePath + 'ExpedienteServlet',  							
            	reader: { 										
                	type: 'json', 
                	root: 'columnas'
            	},
    			extraParams: {
	            	action: 'getGridColumns',
	            	select: parametros.gaveta
	          	}
			},
			cargarFilas: {
				type: 'ajax',
            	url: basePath + 'ExpedienteServlet',                                                      
                reader: {                                                                              
                	type: 'json',
                	root: 'valores',
                	totalProperty: 'totalFilas'
            	},
            	extraParams: {
                    action: 'getExpedientesGaveta',
                    gaveta: parametros.gaveta,
                    select: parametros.gaveta,
                    liveSearch: ''
                }  
			},
			camposBusqueda: {
				type: 'ajax',
				url: basePath + '/Admin/OperacionesServlet',
				reader: {
					type: 'json',
					root: 'Campos'
				},
				extraParams: {
					action:'getgaveta',
                    select: parametros.gaveta
				}
			},
			descargarFilas: {
            	url: basePath + 'ExpedienteServlet',
            	params: {
                    action: 'getExpedientesGavetaCSV',
                    start: null,
                    limit: null
                }  
			},
			crearFila: {
				url: basePath + 'ExpedienteServlet',  
				method: 'POST',  
				params: {  
					action:"setExpedienteGaveta",
					gaveta: parametros.gaveta
				}
			},
			editarFila: {
				url: basePath + 'ExpedienteServlet',    
				method: 'POST',   
				params: {  
					action: "updateExpediente",
					gaveta: parametros.gaveta
				}
			},
			eliminarFila: {
				url: basePath + 'ExpedienteServlet',   
				method: 'POST',  
				params: {  
					action: "deleteExpediente",
					gaveta: parametros.gaveta
				}
			}
		};
        	return [panelbusqueda];
        },

	initComponent: function() {	
		//console.log('initComponent: PanelExpedientes',this);
		Ext.apply(this, {
			items : this.getItems(this)
		}),
		this.callParent(arguments);
	}
});