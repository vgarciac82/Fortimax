Ext.onReady(function(){
	/*
	 * Variables para configuracion de Grid
	 */
	
	var tituloBtnDescarga="Descargar";
	var tituloColumna="Nombre";
	var nombreIndex='Nombre';
	var tituloColumna2="Valor";
	var nombreIndex2='Valor';
	var actionStore="consulta";
	var tituloGrid="Versiones";
	var tituloField="Versiones";
	var tituloPanel="Versiones";
	var ancho=500;
	var txtLabelFilas='Versiones';
	var root='catalog';
	var accionDescargar="getReportVersiones";
	var select="fortimax";
	/*
	 * Variables
	 */
	var pantalla=Ext.getBody().getViewSize();
	var totalFilas=0;
	Ext.QuickTips.init();
	/*
	 * Store y Modelos
	 */
	
	Ext.define('modeloEstructura', {
    extend: 'Ext.data.Model',
    fields: [
        {name: nombreIndex,  		type: 'string'},
         {name: nombreIndex2,  		type: 'string'}
    ]
	});
	var storeEstructura=new Ext.data.Store({
	model:'modeloEstructura',
	pageSize: 10,
	proxy: { 
            type: 'ajax', 
            	url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: root
            },
            extraParams: 
	         {
	              action: actionStore,
	              select:select
	          }
	          
        },
        listeners:{
        	load:function(){
        		totalFilas=this.getCount();
        		labelFilas.setText('<font class="numeroFilas">Mostrando <font class="numeroF">'+this.getCount()+'</font> de <font class="numeroF">'+totalFilas+'</font> '+txtLabelFilas+'</font>',false);
        	}
        },
        autoLoad:true
	});
	//storeEstructura.load({params:{start: 0, limit: 10}});

	var txtFiltro = Ext.define('Ext.ux.CustomTrigger', {
		id:'txtFiltro',
		extend: 'Ext.form.field.Trigger',
		  alias: 'widget.xtext',
		  triggerTip: 'Click para filtrar.',
		  triggerBaseCls :'x-form-trigger',
		  triggerCls:'x-form-clear-trigger',
		  width:300,
		  emptyText:'Filtrar...',
	    onTriggerClick: function() {
	        this.setValue("");
	    },
	    listeners:{
    		change:function(){
    			if(this.getValue()!=""){
    				storeEstructura.clearFilter();
    				storeEstructura.filter({
    	    			 property: nombreIndex,
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    	    			labelFilas.setText(storeEstructura.getCount());
    			}
    			else{
    				storeEstructura.clearFilter();
    			}
    			labelFilas.setText('<font class="numeroFilas">Mostrando <font class="numeroF">'+storeEstructura.getCount()+'</font> de <font class="numeroF">'+totalFilas+'</font> '+txtLabelFilas+'</font>',false);
        	
    		}
    	}
	});
	/*var bbar=new Ext.PagingToolbar({
		store:storeEstructura,
		  dock: 'bottom',
        displayInfo: true
	});*/
 	
	/*
	 * Objetos
	 */

	var labelFilas=new Ext.form.Label({
 		id:'labelFilas',
 		html:'Filas'
 		});
 		
 		var labelEncabezado=new Ext.form.Label({
 		id:'labelEncabezado',
 		html:'Lista de <span class="labelEncabezado">'+tituloGrid+'</span> en Fortimax',
 		margin:'5 0 0 10'
 		});
 		
 		var btnDescargar = new Ext.ux.DownloadButton({
 		    text: tituloBtnDescarga,
 		    select:'versiones',
 		    nombreReporte:'Reporte de Versiones',
 		    columnaBuscar:null,
 		    Filtro:'txtFiltro',
 		   	action:accionDescargar,
 		    icon:'../imagenes/iconos/download16x16.png'
 			});
	
	var Grid=new Ext.grid.Panel({
		id:'Grid',
		title:tituloGrid,
		width:ancho,
		height:pantalla.height-80,
		store:storeEstructura,
		columnLines: true,
		tbar:[btnDescargar,'->',txtFiltro],
		autoScroll:true,
		//dockedItems: [bbar],
		bbar:[labelFilas],
		columns:[
			  {xtype: 'rownumberer', width:25,tdCls: 'numero',width:60,header:'ID',align:'left'},
			  { header: tituloColumna,  dataIndex: nombreIndex, width:200 },
			  { header: tituloColumna2,  dataIndex: nombreIndex2, flex:1 }
			  ]
	});
	
	var Fieldset=new Ext.form.FieldSet({
		title:tituloField,
		width:pantalla.width-45,
		height:pantalla.height,
		collapsible:true,
		margin:'5 0 0 15',
		items:[Grid]
	});
	var Panel=new Ext.panel.Panel({
		title:tituloPanel,
		layout:'vbox',
		width:pantalla.width,
		height:pantalla.height,
		items:[labelEncabezado,Fieldset],
		renderTo:Ext.getBody()
	});
	/*
	 * Funciones de la interfaz
	 */
	Ext.EventManager.onWindowResize(function(w, h){
		if(h>300){
			Grid.setHeight(h-80);
			Fieldset.setHeight(h);
			Panel.setHeight(h);
		}
		else{
			Grid.setHeight(220);
			Fieldset.setHeight(300);
			Panel.setHeight(300);
		}

	});

	/*
	 * Funciones
	 */
	var successAjaxFnN = function(response, request) {
		Ext.getBody().unmask();
	    var jsonData = Ext.JSON.decode(response.responseText);
	    if (true == jsonData.success) {
	        Ext.Msg.show({
	            title: 'Correcto',
	            msg: jsonData.message,
	            buttons: Ext.Msg.OK,
	            icon: Ext.MessageBox.INFO
	        });	            
	    } else {
	        Ext.Msg.show({
	            title: 'Error',
	            msg: jsonData.message,
	            buttons: Ext.Msg.OK,
	            animEl: 'elId',
	            icon: Ext.MessageBox.ERROR
	        });
	    }
	    
	    storeEstructura.load();
	};
	function Eliminar(Nombre){
		Ext.Msg.show({
		     title:'Eliminar',
		     msg: '&iquestDeseas eliminar el grupo: '+Nombre+' ?',
		     buttons: Ext.Msg.OKCANCEL,
		     icon: Ext.Msg.QUESTION,
		     fn: function (btn){
		         if(btn=='ok'){ 
		        	 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		Ext.Ajax.request({  
			url: rutaServlet,  
			method: 'POST',  
			 success: successAjaxFnN,   
			timeout: 30000,  
			params: {  
			action: 'deleteGrupo',
			select:Nombre
					}});
		         }
		     }
		});
	};
	
});