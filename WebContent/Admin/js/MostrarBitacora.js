Ext.onReady(function(){
	/*
	 * Variables Configuracion
	 */
	var actionStoreColumnas="getColumnasGrid";
	var actionStoreDatos="getbitacora";
	var tituloGrid="Bitacora";
	var tamanoDePagina=1000;
	var intervaloEscrituraBusqueda=1000; //Milisegundos
	var rutaServlet2='../js/';
	var accionDescargar="getReport";
	/*
	 * Variables Generales
	 */
	var pantalla=Ext.getBody().getViewSize();
	var busqueda="";
	/*
	 * Stores y Modelos
	 */
	Ext.define('modeloGeneral', {
        extend: 'Ext.data.Model',
        fields: 
    [{name: 'modelo', type: 'Array'},{name: 'columnas', type: 'Array'}] 
    });
    var storeGeneral=new Ext.data.Store({
	model:'modeloGeneral',
	proxy: { 
            type: 'ajax', 
            	url: rutaServlet2+'Bitacora.json',  							
            	reader: { 										
                type: 'json', 
                root: 'configuracion'
            },
            extraParams: 
	         {
	              action: actionStoreColumnas
	          }
        },
        listeners:{
        	load:function(){
        		var columnas=new Array();
        		var modelo=new Array();
        		columnas=this.getAt(0).get("columnas");
        		modelo=this.getAt(0).get("modelo");
        		crear(columnas,modelo,tamanoDePagina);
        		}
        },
        autoLoad:true
	});
	/*
	 * Modelo Dinamico
	 */
		function crear(columnas,modelo,tamanoPagina){
			Ext.define('modeloDinamico', {
        extend: 'Ext.data.Model',
        fields: modelo
    });
    var storeGeneral=new Ext.data.Store({
	model:'modeloDinamico',
	pageSize:tamanoPagina,
	proxy: { 
            type: 'ajax', 
            	url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: 'valores',
                totalProperty: 'totalFilas'
            },
            extraParams: 
	         {
	              action: actionStoreDatos,
	              campoBusqueda:busqueda
	          }   
        },
        listeners:{
        	load:function(){
						//Quitar mascara
        		}
        },
         autoLoad:true
		});
		var bbar=new Ext.PagingToolbar({
		store:storeGeneral,
		  dock: 'bottom',
        displayInfo: true
		});
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	    	clicksToEdit: 2
	    });
	    var txtFiltro = Ext.define('Ext.ux.CustomTrigger', {
		extend: 'Ext.form.field.Trigger',
		id:'txtFiltro',
		fieldLabel:'Buscar',
		alias: 'widget.xtext',
		triggerTip: 'Click para filtrar.',
		triggerBaseCls :'x-form-trigger',
		triggerCls:'x-form-clear-trigger',
		width:500,
		labelPad:0,
		labelWidth:50,
		emptyText:'Filtrar...',
	    onTriggerClick: function() {
	        this.setValue("");
	    },
	    listeners:{
    		change:function(text, newValue, oldValue, eOpts){
    			if(this.getValue()!=""){				   				
    				var t=window.setTimeout(function(){
    					if(Ext.getCmp('txtFiltro').getValue()==newValue){
    						busqueda=Ext.getCmp('txtFiltro').getValue();
    						enviarDatosBusqueda(newValue);
    					}
    					
    				},intervaloEscrituraBusqueda);
    			}
    			else{
    				busqueda="";
    				storeGeneral.setProxy({type: 'ajax',url: rutaServlet,reader: {type: 'json',root: 'valores',totalProperty: 'totalFilas'},
            		extraParams:{action: actionStoreDatos,campoBusqueda:busqueda}});  				
    				//storeGeneral.load({params:{start:0,limit:tamanoDePagina,campoBusqueda:""}});
    				bbar.moveFirst();
    				
    			}    	
    		}
    	}
	});
	    var btnDescargar = new Ext.ux.DownloadButton({
	        text: 'Descargar',
	        select:'imx_bitacora',
	        nombreReporte:'Reporte Bitacora',
	        columnaBuscar:'MENSAJE',
	        Filtro:'txtFiltro',
	        action:accionDescargar,
	        icon:'../imagenes/iconos/download16x16.png'
	    	});
		var Grid=new Ext.grid.Panel({
		id:'Grid',
		cls:'Grid',
		title:tituloGrid,
		store:storeGeneral,
		columnLines: true,
		tbar:[btnDescargar,'->',txtFiltro],
		columns:[columnas],
		dockedItems: [bbar],
		plugins: [cellEditing]
	});
	var viewport = new Ext.Viewport({
    layout: 'fit',
    items: [Grid]
  });
  /*
   * Funciones
   */
  function enviarDatosBusqueda(texto){
  			storeGeneral.setProxy({type: 'ajax',url: rutaServlet,reader: {type: 'json',root: 'valores',totalProperty: 'totalFilas'},
            extraParams:{action: actionStoreDatos,campoBusqueda:texto}}); 
            bbar.moveFirst();
			//storeGeneral.load({params:{start:0,limit:tamanoDePagina,campoBusqueda:texto}});
			//bbar.moveFirst();
  	
  }
	}
});