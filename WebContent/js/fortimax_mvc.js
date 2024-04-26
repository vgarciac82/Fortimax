Ext.Loader.setConfig({enabled:true});
Ext.application({
    name: 'FMX',
    appFolder: '../js/fortimax_mvc',

	controllers:[
		/*'Principal',
		'Visualizador.Principal',*/
		'Busqueda.Principal'
		],
	
	autoCreateViewport: true
  /*   launch: function() {
	console.log('Fortimax inicializado');
	
       Ext.create('Ext.container.Viewport', {
            //layout: 'fit',
            layout:'column',
			items: [
                {
                    xtype: 'panel',
                    title: 'Fortimax',
                    html : 'Lista de usuarios'
                },
				{
                    xtype: 'panel',
                    title: 'otro panel',
                    html : 'LLista deo tras cosas'
                }
				
            ]
        }); 
    }*/
});