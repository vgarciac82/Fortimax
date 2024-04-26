Ext.onReady(function(){
	/*
	 * Variables para configuracion de Grid
	 */
	
	var tituloBtnDescarga="Descargar";
	var tituloColumna="Grupos";
	var nombreIndex='name';
	var actionStore="obtenerGrupos";
	var tituloGrid="Grupos LDAP";
	var tituloField="Grupos LDAP";
	var tituloPanel="Grupos LDAP";
	var ancho=500;
	var txtLabelFilas='Grupos';
	var root='grupos';
	/*
	 * Variables de Columnas
	 */
	var columnaPrivilegios='Privilegios';
	var linkActionColumnPrivileigos=basePath+"/Admin/jsp/UsuariosPrivilegios.jsp?aGrupo=true&LDAP=true&Grupo="
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
    grid: 'Grid',
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
			  { header: tituloColumna,  dataIndex: nombreIndex, width:ancho-137 },
			  { header: columnaPrivilegios, xtype: 'actioncolumn',flex: 1, align  : 'center',items: [
			  	{icon   : '../imagenes/iconos/privilegios.png', cls:'privilegios',	tooltip: columnaPrivilegios,getClass : function(){return columnaPrivilegios;},
						handler: function(grid, rowIndex, colIndex) {
							Privilegios(storeEstructura.getAt(rowIndex).get(nombreIndex));
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
			Fieldset.setHeight(255);
			Panel.setHeight(300);
		}

	});

	/*
	 * Funciones
	 */
	function Privilegios(nombre){
		var action =linkActionColumnPrivileigos+nombre;
		location.href=action;
	}
	
});