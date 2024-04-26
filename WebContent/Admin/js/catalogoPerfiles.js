Ext.onReady(function(){
	/*
	 * Variables para configuracion de Grid
	 */
	
	var tituloBtnDescarga="Descargar";
	var tituloColumna="Perfiles";
	var nombreIndex='Nombre';
	var actionStore="listPerfiles";
	var tituloGrid="Perfiles";
	var tituloField="Perfiles";
	var tituloPanel="Perfiles";
	var tituloBtnCrear="Perfil";
	var ancho=500;
	var txtLabelFilas='Perfiles';
	var root='catalog';
	var link_btncreate=basePath+'/Admin/jsp/crearPerfil.jsp';
	var accionDescargar="getReport";
	/*
	 * Variables de Columnas
	 */
	var columnaActualizar='Actualizar';
	var linkActionColumnActualizar=basePath+"/Admin/jsp/crearPerfil.jsp?actualizar=true&nombre=";
	var columnaPrivilegios='Asignar';
	var linkActionColumnPrivilegios=basePath+"/Admin/jsp/AsignaPerfiles.jsp?nombre=";
	var columnaEliminar='Eliminar';
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
        {name: nombreIndex,  		type: 'string'}
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
	              action: actionStore
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
	var btncrear = Ext.create('Ext.Button', {
	    text: 'Crear ' + tituloBtnCrear,
	    scale: 'small',
	    iconCls: 'BtnCrear',
	    iconAlign: 'left',
	    margin:'15 0 5 0',
	    handler: function() 
	    {
	    	window.self.location.href = link_btncreate;
	    }
	});
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
 		    disabled:true,
 		    select:'imx_usuario',
 		    nombreReporte:'Reporte Usuarios',
 		    columnaBuscar:'NOMBRE_USUARIO',
 		    Filtro:'txtFiltro',
 		   action:accionDescargar,
 		    icon:'../imagenes/iconos/download16x16.png'
 			});
	
	var Grid=new Ext.grid.Panel({
		id:'Grid',
		title:tituloGrid,
		width:ancho,
		height:pantalla.height-120,
		store:storeEstructura,
		columnLines: true,
		tbar:[btnDescargar,'->',txtFiltro],
		autoScroll:true,
		//dockedItems: [bbar],
		bbar:[labelFilas],
		columns:[
			  {xtype: 'rownumberer', width:25,tdCls: 'numero',width:60,header:'ID',align:'left'},
			  { header: tituloColumna,  dataIndex: nombreIndex, width:ancho-257 },
			  { header: columnaActualizar, xtype: 'actioncolumn', width: 60, align  : 'center',items: [
			         {icon   : '../imagenes/iconos/actualizar.png', cls:'actionColumns',tooltip: columnaActualizar,
			        	getClass : function(){return 'actionColumns';},
			         handler: function(grid, rowIndex, colIndex) {
			         window.self.location.href = linkActionColumnActualizar + storeEstructura.getAt(rowIndex).get(nombreIndex);
			         }}	
			         ]
			   },
			   { header: columnaPrivilegios, xtype: 'actioncolumn', width: 60, align  : 'center',items: [
			   {icon   : '../imagenes/iconos/privilegios.png', cls:'actionColumns',tooltip: columnaPrivilegios,
			   getClass : function(){return 'actionColumns';},
			   handler: function(grid, rowIndex, colIndex) {
			   window.self.location.href = linkActionColumnPrivilegios + storeEstructura.getAt(rowIndex).get(nombreIndex);
			   }}	
			   ]
			   },
			   { header: columnaEliminar, xtype: 'actioncolumn', flex: 1, align  : 'center',items: [
			   {icon   : '../imagenes/iconos/eliminar.png', cls:'actionColumns',tooltip: columnaEliminar,
			   getClass : function(){return 'actionColumns';},
			   handler: function(grid, rowIndex, colIndex) {
				   	Eliminar(storeEstructura.getAt(rowIndex).get(nombreIndex));
			  }}	
			  ]
			  }
		]
	});
	
	var Fieldset=new Ext.form.FieldSet({
		title:tituloField,
		width:pantalla.width-45,
		height:pantalla.height,
		collapsible:true,
		margin:'5 0 0 15',
		items:[btncrear,Grid]
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
			Grid.setHeight(h-120);
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
		     msg: '&iquestDeseas eliminar el perfil: '+Nombre+' ?',
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
		        			action: 'borrarPerfil',  				
		        			select:Nombre
		        			 }});
		         }
		     }
		});
		
	};
	
});