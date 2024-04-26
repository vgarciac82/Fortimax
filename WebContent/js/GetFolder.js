Ext.onReady(function(){
	
	var botonPdf=new Ext.Button({
				    text: 'Formato PDF',	
				    iconCls:'botonPdfIconCls',
				    scale:'large',
				    iconAlign:'top',
				    disabled:tipo==2?false:true,
				    margin:'70 0 0 80',
				    handler: function() {
				    	descargarPdf();
				    }
				});
	var botonZip=new Ext.Button({
				    text: 'Formato ZIP',	
				    iconCls:'botonZipIconCls',
				    scale:'large',
				    iconAlign:'top',
				    margin:'70 0 0 80',
				    handler: function() {
				    	descargarZip();
				    }
				});
	/*var ventana=new Ext.window.Window({
    title: 'Descargar',
    height: 150,
    width: 300,
    layout:'anchor',
    closable:false,
    items: [botonZip,botonPdf]
}).show();*/
	var panel=new Ext.panel.Panel({
		id:'panel',
		cls:'panelCls',
		layout:'anchor',
		title:'Descargar',
		height:250,
		width:400,
		items:[botonZip,botonPdf],
		renderTo:Ext.getBody()
	});
//Funciones
function descargarZip(){
	location.replace('../getfolder?select='+idNode+'&extension_zip=true');
}
function descargarPdf(){
	location.replace(basePath+'pdfstore/'+idNode+'.pdf?select='+idNode+'&download=true');
}
});