Ext.onReady(function(){
	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
/*
 * Variables
 */
	var pantalla=Ext.getBody().getViewSize();
	var saveRecord=null;
	var al=false;
/*
 * Store y Modelos
 */
	Ext.define('modeloVideos', {
        extend: 'Ext.data.Model',
        fields:  [	{name: 'Texto', type: 'string'},
        			{name: 'Descripcion', type: 'string'},
        			{name: 'Ruta', type: 'string'},
        			{name: 'Seccion', type: 'string'},
        			{name: 'SeccionNombre', type: 'string'}
        			] 
    });
    var storeGeneral=new Ext.data.TreeStore({
	model:'modeloVideos',
	proxy: { 
            type: 'ajax', 
            	url: rutaServlet,  							
            	reader: { 										
                type: 'json'
            },
            extraParams: 
	         {
	              action: 'getVideos',
	              select: false
	          }   
        },
        listeners:{
        	load:function(_this, node, records, successful, eOpts){
        				if(records[0].childNodes.length>0){
        					mostrarVideoInicial(records[0].firstChild.data.Ruta);
        				}
        		},
        		beforeload: function(store, op){
        	        if (!al){
        	            return false;
        	        }
        	    }
        },
         autoLoad:false
		});
/*
 * Objetos
 */
		var lblTituloInformacion= new Ext.form.Label({
	     id:'lblTituloInformacion',
	     cls:'lblTituloInformacion',
	      html:'Titulo:',
	      shadow:true,
	      width: 250
	  });
	  var lblSeccion= new Ext.form.Label({
	     id:'lblSeccion',
	     cls:'lblTituloInformacion',
	      html:'Seccion:',
	      shadow:true,
	      width: 250
	  });
	  var lblDescripcion= new Ext.form.Label({
	     id:'lblDescripcion',
	     cls:'lblTituloInformacion',
	      html:'Descripcion: ',
	      shadow:true,
	      width: 250
	  });
	var grdArbol = Ext.create('Ext.tree.Panel', {
	    //height:400,
		layout:'fit',
		height:'100%',
	    cls:'grdArbol',
	    id:'grdArbol',
	    collapsible: false, 
	    useArrows: true, 	    
	    store: storeGeneral,
	    multiSelect: false, 
	    hideHeaders:true,
	    lines:true,
	        columns: [{
	            xtype: 'treecolumn',flex: 2,sortable: true,dataIndex: 'Texto'
	        }],
			listeners:{
				itemclick:function( _grdArbol, record, item, index, e, eOpts ){				
					if(record.data.Seccion=='0'){
						Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
						lblTituloInformacion.setText('Titulo: <font class="titulo">'+record.data.Texto+'</font>',false)
						lblSeccion.setText('Seccion: <font class="seccion">'+record.data.SeccionNombre+'</font>',false);
						lblDescripcion.setText('Descripcion: <font class="descripcion">'+record.data.Descripcion+'</font>',false);
						creaVideo(record.data.Ruta);
						Ext.getBody().unmask();
					}
				}
			}, rootVisible: false
			
	    });
/*
 * Paneles
 */
	var panel=Ext.form.Panel({
		region: 'west',
        collapsible: true,
        title: 'Guias',
        width: 280,
        items:[grdArbol]
	});
	var panelInformacion=Ext.form.Panel({
		region: 'east',
		layout:'vbox',
        collapsible: true,
        collapsed:true,
        title: 'Informacion',
        width: 250,
        items:[lblTituloInformacion,lblSeccion,lblDescripcion]
	});
	Ext.create('Ext.container.Viewport', {
    layout: 'border',
    items: [panel, {
        region: 'center',
        html: '<div class="panelVideo" id="panelVideo"></div>'
    },panelInformacion],
    listeners:{
    	afterrender:function(){
    		al=true;
    		storeGeneral.load();
    	}
    }
});

function creaVideo(rutaV){
	var ruta=basePath+rutaV;
    jwplayer("panelVideo").setup({
    	width:'100%',
    	height:'100%',
        file: ruta,
       // image: basePath+"/imagenes/SyC_Blue.png",
        autostart:true
    });
    
}
function mostrarVideoInicial(rutaV){
	
	var ruta=basePath+rutaV;
    jwplayer("panelVideo").setup({
    	width:'100%',
    	height:'100%',
        file: ruta,
        autostart:false
    });
    Ext.getBody().unmask();
}
     
});