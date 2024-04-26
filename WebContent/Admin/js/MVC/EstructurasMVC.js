Ext.Loader.setConfig({enabled:true});
Ext.application({
    name: 'FMX',
    appFolder: '../js/MVC/EstructurasMVC', //buscandolo desde posicion del jsp

	controllers:[
	             'Principal'
		],
	
	autoCreateViewport: true

});