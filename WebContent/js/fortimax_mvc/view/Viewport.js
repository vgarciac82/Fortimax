/**
 * The main application viewport, which displays the whole application
 * @extends Ext.Viewport
 */
Ext.define('FMX.view.Viewport', {
    extend: 'Ext.Viewport',    
	
    requires: [
/*
	'FMX.controller.VariablesEntorno',
	'FMX.view.norte.PanelHerramientasPrincipal',
	'FMX.view.sur.PanelInferiorPrincipal',
	'FMX.view.este.TabPanelPropiedadesPrincipal',
	'FMX.view.oeste.PanelArbolesPrincipal',
	'FMX.view.centro.TabPanelVisorPrincipal',
	'FMX.view.oeste.GraficaEspacioUsuario',
	'Ext.layout.container.Border',	
	'FMX.view.centro.Visualizador.PanelVisualizador',
*/
	'FMX.view.centro.Expedientes.PanelExpedientes'
    ],
	//layout: 'fit',
    id: 'border-example',
    layout: 'border',
    
    initComponent: function() {
	
		//var esIfimax = FMX.controller.VariablesEntorno.esIfimax;
		//var esFortimax = FMX.controller.VariablesEntorno.esFortimax;
		
		//console.log('Inicializando en modo Fortimax: '  + esFortimax);
        var me = this;
        
        Ext.apply(me, {
		items: 
		[
		
		Ext.create('FMX.view.centro.Expedientes.PanelExpedientes',{
							title: select,
							region: 'center',
							gaveta: select
		})
		/*
			//Panel Opciones Superiores Norte
			Ext.widget('PanelHerramientasPrincipal'),
			
			//Panel Pie de Pagina Sur
			Ext.widget('PanelInferiorPrincipal'),
			
			//Panel Propiedades Este
			Ext.widget('TabPanelPropiedadesPrincipal'),
		
			//Panel Arboles Oeste
			Ext.widget('PanelArbolesPrincipal'),		

			
			//Panel Central Tabs principales
			Ext.widget('TabPanelVisorPrincipal')
		
		*/
		]
        });
                
        me.callParent(arguments);
    }
});
