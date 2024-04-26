Ext.require([
     'Ext.form.*',
     'Ext.tip.*',
     'Ext.window.MessageBox'
]);
Ext.onReady(function() {
	/* 
	 * 
	 * VARIABLES
	 * 
	 */
	var context_title='Modificar gavetas en estructura';
	var pantalla = Ext.getBody().getViewSize();
	/* 
	 * 
	 * MODELOS Y STORE
	 * 
	 */
	Ext.define('gavetas_model', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {name: 'Nombre',  		type: 'string'}
	    ]
	});
	var GavetasDStore = new Ext.data.Store({ 
        model: 'gavetas_model', 
        proxy: { 
            type: 'ajax', 
            url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: 'gavetas'
            },
            extraParams: 
	         {
	              action: 'getGavD',
	              select:''
	          } 
        } ,
	autoLoad:false
    });
	var GavetasAStore = new Ext.data.Store({ 
        model: 'gavetas_model', 
        proxy: { 
            type: 'ajax', 
            url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: 'gavetas'
            },
            extraParams: 
	         {
	              action: 'getGavA',
	              select:''
	          } 
        } ,
	autoLoad:false
    });
	
	
	
	/* 
	 * 
	 * OBJETOS
	 * 
	 */	
	var btnGuardar=Ext.create('Ext.button.Button',{
		text: 'Guardar',
		 formBind: false, 
		 iconCls: 'save',
	        handler: function() {
	        	Ext.Msg.show({
	   		     title:'Guardar',
	   		     msg: '&iquestDeseas guardar los cambios en la estructura: '+estructura+' ?',
	   		     buttons: Ext.Msg.OKCANCEL,
	   		     icon: Ext.Msg.QUESTION,
	   		     fn: function (btn){
	   		         if(btn=='ok'){ 
	   		        	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	   		        	 	guardar();
	   		         }
	   		     }
	   		});
	        } 
	});
	var columasUN = [
		               {xtype: 'actioncolumn', width: 20, align  : 'center',
		            items: [{iconCls:'Gavetas'}]},
		               {text: "Gavetas", flex: 1, sortable: true, dataIndex: 'Nombre'}

		           ];
	var txtFiltroIzq = Ext.define('Ext.ux.CustomTrigger', {
		extend: 'Ext.form.field.Trigger',
		  alias: 'widget.xtext',
		  triggerTip: 'Click para filtrar.',
		  triggerBaseCls :'x-form-trigger',
		  triggerCls:'x-form-clear-trigger',
		  width:270,
		  emptyText:'Filtrar...',
	    onTriggerClick: function() {
	        this.setValue("");
	    },
	    listeners:{
    		change:function(){
    			if(this.getValue()!=""){
    				GavetasDStore.clearFilter();
    				GavetasDStore.filter({
    	    			 property: 'Nombre',
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
 
    				GavetasDStore.clearFilter();
    			}
    		}
    	}
	});
	var grdIzqU = Ext.create('Ext.grid.Panel', {
		tbar:[txtFiltroIzq],
        multiSelect: true,
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'firstGridDDGroup',
                dropGroup: 'secondGridDDGroup',
                dragText : '{0} gavetas seleccionada(s){1}'
            },
            listeners: {
                drop: function(node, data, dropRec, dropPosition) {
                    Ext.example.msg(data.records[0].get('Nombre'),"Haga clic en guardar para confirmar desasignación");
                }
            }
        },
        store            : GavetasDStore,
        columns          : columasUN,
        stripeRows       : true,
        width			 :280,
        height			 :pantalla.height-180,
        cls				 :'drag',
        title            : 'Gavetas',
        margins			 :'0 0 0 5'
    });
	var txtFiltroDer = Ext.define('Ext.ux.CustomTrigger', {
		extend: 'Ext.form.field.Trigger',
		  alias: 'widget.xtext',
		  triggerTip: 'Click para filtrar.',
		  triggerBaseCls :'x-form-trigger',
		  triggerCls:'x-form-clear-trigger',
		  width:270,
		  emptyText:'Filtrar...',
	    onTriggerClick: function() {
	        this.setValue("");
	    },
	    listeners:{
    		change:function(){
    			if(this.getValue()!=""){
    				GavetasAStore.clearFilter();
    				GavetasAStore.filter({
    	    			 property: 'Nombre',
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
 
    				GavetasAStore.clearFilter();
    			}
    		}
    	}
	});
	var grdDer = Ext.create('Ext.grid.Panel', {
		tbar:[txtFiltroDer],
        multiSelect: true,
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragGroup: 'secondGridDDGroup',
                dropGroup: 'firstGridDDGroup',
                dragText : '{0} gavetas seleccionada(s){1}'
            },
            listeners: {
                drop: function(node, data, dropRec, dropPosition) {
                    Ext.example.msg(data.records[0].get('Nombre'),"Haga clic en guardar para confirmar asignación");
                }
            }
        },
        store            : GavetasAStore,
        columns          : columasUN,
        stripeRows       : true,
        width			 :280,
        height			 :pantalla.height-180,
        cls				 :'drag',
        title            : 'Estructura: '+estructura,
        margins			 : '0 0 0 20'
    });
	var lblMensaje= new Ext.form.Label({
	     id:'lblMensaje',
	     componentCls:'labels',
	      html:'Arrastre las gavetas a la seccion de la estructura para asignarlos, y para desasignarlos arrastrelos de nuevo a la seccion de gavetas',
	      shadow:true
	  });
	var panDrag=Ext.create('Ext.form.Panel',{
		id:'panDrag',
		disabled:false,
		name:'panDrag',
		cls:'panDrag',
		layout:'anchor',
		width:600,
		items:[{xtype:'fieldset',name:'fieldM',title:'Asignacion de gavetas a estructura',height:65,items:[lblMensaje]},{xtype:'panel',name:'field',width:600,border:0,layout:'hbox',items:[grdIzqU,grdDer]}]
	}); 
	Ext.create('Ext.form.Panel',{
		id:'panPrincipal',
		name:'panPrincipal',
		cls:'panPrincipal',
		layout:'anchor',
		title:context_title,
		width:'100%',
		height:pantalla.height,
		items:[{xtype:'toolbar',width:600,items:['->',btnGuardar]},panDrag],renderTo: Ext.getBody()
	}); 
	
	/* 
	 * 
	 * FUNCIONES
	 * 
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
    };
	function guardar(){
		GavetasAStore.clearFilter();
		var gav=new Array();
		for(var i=0;i<GavetasAStore.getCount();i++){
			var ga = Ext.create('gavetas_model', {
 			    Nombre : GavetasAStore.getAt(i).get('Nombre')
 			});	
			gav.push(ga.data);
		}
		gav=Ext.JSON.encode(gav);
		Ext.Ajax.request({  
			url: rutaServlet,  
			method: 'POST',  
			 success: successAjaxFnN,   
			timeout: 30000,  
			params: {  
			action: 'asignaGaveE',
			select:estructura,
			gav:gav
	}});	}
	inicia();
	function inicia(){
		if(estructura!=""){
			GavetasDStore.load({params: {select: estructura}});
			GavetasAStore.load({params: {select: estructura}});
		}
		else{
			Ext.Msg.show({
	   		     title:'Error',
	   		     msg: 'No se recibio ninguna estructura para modificar',
	   		     icon: Ext.Msg.ERROR,
	   		  buttons: Ext.Msg.OK
	   		});
		}
	}
});//Termina funcion principal