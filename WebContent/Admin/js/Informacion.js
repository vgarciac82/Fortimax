Ext.onReady(function(){
	/*
	 * Variables Configuracion
	 */
	var actionStoreDatos="consulta";
	var actionSelect="javaEntorno";
	var tituloGrid="Informacion";
	var tamanoDePagina=20;
	var intervaloEscrituraBusqueda=1000; //Milisegundos
	var accionDescargar="getReportInformacion";
	/*
	 * Variables Generales
	 */
	var pantalla=Ext.getBody().getViewSize();
	var busqueda="";
	/*
	 * Stores y Modelos
	 */
	/*
	 * Modelo Dinamico
	 */
			Ext.define('modeloInformacion', {
        extend: 'Ext.data.Model',
        fields:  [{name: 'propiedad', type: 'string'},{name: 'valor', type: 'string'}] 
    });
    var storeGeneral=new Ext.data.Store({
	model:'modeloInformacion',
	pageSize:tamanoDePagina,
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
	              select:actionSelect,
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
            		extraParams:{action: actionStoreDatos,campoBusqueda:busqueda,select:actionSelect}});  				
    				bbar.moveFirst();
    				
    			}    	
    		}
    	}
	});
	    var btnDescargar = new Ext.ux.DownloadButton({
	        text: 'Descargar',
	        select:'informacion',
	        nombreReporte:'Reporte Informacion',
	        columnaBuscar:null,
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
		columns:[{header:"Propiedad",dataIndex:"propiedad",width:350,align:"left"},
		         {header:"Valor",dataIndex:"valor",flex:1,align:"left"}],
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
            extraParams:{action: actionStoreDatos,campoBusqueda:texto,select:actionSelect}}); 
            bbar.moveFirst();

  }
	
});