Ext.Loader.setConfig({enabled:true});
Ext.application({
    name: 'FMX',
    appFolder: '../js/fortimax_mvc',

	controllers:[
		'IfimaxDocumentos.PrincipalIfimaxDocumentos'
		],

	autoCreateViewport: false,

	launch: function() {
		
		Ext.create('FMX.view.IfimaxDocumentos.IfimaxDocumentosViewport');
		
	}

});