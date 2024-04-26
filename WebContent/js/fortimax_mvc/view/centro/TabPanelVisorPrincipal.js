Ext.define('FMX.view.centro.TabPanelVisorPrincipal', {
    extend: 'Ext.tab.Panel',
    alias : 'widget.TabPanelVisorPrincipal',
	id:'TabPanelVisorPrincipal',
	region: 'center', // a center region is ALWAYS required for border layout
	deferredRender: false,
	activeTab: 1,     // first tab initially active
	
	
	    initComponent: function() {
		
		var GraficaEspacioUsuario = Ext.create('FMX.view.oeste.GraficaEspacioUsuario');	
		
        this.items = [
						{
							id:'panel_principal',
							xtype: 'panel',
							//contentEl: 'center1',
							title: 'Principal',
							closable: false,
							autoScroll: true,
							iconCls:'info',
							layout: {
								type: 'hbox'
							},
							items:[
									GraficaEspacioUsuario,						
									{
										xtype: 'panel',
										id:'panel_principal_banner',
										margin:'10 5 3 10',
										unstyled:true,
										border:false,
										contentEl: 'center1',	
										closable: false,
										autoScroll: true
									}		
							]
						}/*, 
						
						Ext.create('FMX.view.centro.Visualizador.TabPanelVisualizador',{
							title: 'Visualizador #1',
							nodo: 'USR_GRALES_G45C0D403'
						}),

						Ext.create('FMX.view.centro.Visualizador.TabPanelVisualizador',{
							title: 'Visualizador #2',
							nodo: 'USR_GRALES_G45C0D404'
						}),
						
						Ext.create('FMX.view.centro.Visualizador.TabPanelVisualizador',{
							title: 'Visualizador #3',
							nodo: 'USR_GRALES_G45C0D372'
						}),
						Ext.create('FMX.view.centro.Expedientes.PanelExpedientes',{
							title: 'CONTRATOS GP',
							nodo: 'CONTRATOS_GP'	
						}),
						Ext.create('FMX.view.centro.Expedientes.PanelExpedientes',{
							title: 'CONTRATOS_PMC',
							nodo: 'CONTRATOS_PMC'
						}),
						Ext.create('FMX.view.centro.Expedientes.PanelExpedientes',{
							title: 'CONTRATOS_GP',
							nodo: 'CONTRATOS_GP'
						})*/
						

					];
        this.callParent(arguments);
		}
});