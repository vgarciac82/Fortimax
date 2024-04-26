Ext.onReady(function(){
		/* 
	 * 
	 * VARIABLES
	 * 
	 */
	var context_title='Asignar usuarios perfil';
	var pantalla = Ext.getBody().getViewSize();
	/* 
	 * 
	 * MODELOS Y STORE
	 * 
	 */
	Ext.define('modeloUsuarios', {
	    extend: 'Ext.data.Model',
	    fields: [
	    	{name: 'Icono',  		type: 'string'},
	        {name: 'Nombre',  		type: 'string'},
	        {name: 'Tipo',  		type: 'string'}
	    ]
	});
	var storeUsuariosDisponibles = new Ext.data.Store({ 
        model: 'modeloUsuarios', 
        proxy: { 
            type: 'ajax', 
            url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: 'usuarios'
            },
            extraParams: 
	         {
	              action: 'getUsuariosDisponiblesPerfil',
	              select:nombrePerfil
	          } 
        } ,
	autoLoad:true
    });
	var storeUsuariosAsignados = new Ext.data.Store({ 
        model: 'modeloUsuarios', 
        proxy: { 
            type: 'ajax', 
            url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: 'usuarios'
            },
            extraParams: 
	         {
	              action: 'getUsuariosAsignadosPerfil',
	              select:nombrePerfil
	          } 
        } ,
	autoLoad:true
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
	   		     msg: '¿Deseas guardar los cambios en el perfil: '+nombrePerfil+' ?',
	   		     buttons: Ext.Msg.YESNO,
	   		     icon: Ext.Msg.QUESTION,
	   		     fn: function (btn){
	   		         if(btn=='yes'){ 
	   		        	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	   		        	 	guardar();
	   		         }
	   		     }
	   		});
	        } 
	});
	var columasUN = [
					{text:'',dataIndex:'Icono',width: 20,align:'center',   
				         	  renderer : function (value, cell, doc, idx) {  
				        	      if (value=='usuariosIconCls') {
				        	    	return  "<img src='../imagenes/iconos/usuario.png' width='16'/>";
				        	 	   }
				        	      else{
				        	      	return  "<img src='../imagenes/iconos/grupos.png' width='16'/>";
									}
				        	  }},
		               {text: "Usuarios/Grupos", flex: 1, sortable: true, dataIndex: 'Nombre'}

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
    				storeUsuariosDisponibles.clearFilter();
    				storeUsuariosDisponibles.filter({
    	    			 property: 'Nombre',
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
 
    				storeUsuariosDisponibles.clearFilter();
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
                dragText : '{0} usuarios/grupo seleccionada(s){1}'
            },
            listeners: {
                drop: function(node, data, dropRec, dropPosition) {
                    Ext.example.msg(data.records[0].get('Nombre'),"Eliminado del perfil");
                }
            }
        },
        store            : storeUsuariosDisponibles,
        columns          : columasUN,
        stripeRows       : true,
        width			 :280,
        height			 :pantalla.height-180,
        cls				 :'drag',
        title            : 'Usuarios/Grupos',
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
    				storeUsuariosAsignados.clearFilter();
    				storeUsuariosAsignados.filter({
    	    			 property: 'Nombre',
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
 
    				storeUsuariosAsignados.clearFilter();
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
                dragText : '{0} usuarios/grupo seleccionada(s){1}'
            },
            listeners: {
                drop: function(node, data, dropRec, dropPosition) {
                    Ext.example.msg(data.records[0].get('Nombre'),"Agregado a perfil");
                }
            }
        },
        store            : storeUsuariosAsignados,
        columns          : columasUN,
        stripeRows       : true,
        width			 :280,
        height			 :pantalla.height-180,
        cls				 :'drag',
        title            : 'Perfil: '+nombrePerfil,
        margins			 : '0 0 0 20'
    });
	var lblMensaje= new Ext.form.Label({
	     id:'lblMensaje',
	     componentCls:'labels',
	      html:'Arrastre los usuarios y/o grupos a la seccion del perfil para asignarlos, y para desasignarlos arrastrelos de nuevo a la seccion de usuarios/grupos',
	      shadow:true
	  });
	var panDrag=Ext.create('Ext.form.Panel',{
		id:'panDrag',
		disabled:false,
		name:'panDrag',
		cls:'panDrag',
		layout:'anchor',
		width:600,
		items:[{xtype:'fieldset',name:'fieldM',title:'Asignacion de usuarios/grupos a perfil',height:65,items:[lblMensaje]},{xtype:'panel',name:'field',width:600,border:0,layout:'hbox',items:[grdIzqU,grdDer]}]
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
		storeUsuariosAsignados.clearFilter();
		var datos=new Array();
		for(var i=0;i<storeUsuariosAsignados.getCount();i++){
			var d = Ext.create('modeloUsuarios', {
				Icon:"",
 			    Nombre : storeUsuariosAsignados.getAt(i).get('Nombre'),
 			    Tipo:storeUsuariosAsignados.getAt(i).get('Tipo')
 			});	
			datos.push(d.data);
		}
		datos=Ext.JSON.encode(datos);
		Ext.Ajax.request({  
			url: rutaServlet,  
			method: 'POST',  
			 success: successAjaxFnN,   
			timeout: 30000,  
			params: {  
			action: 'asignaUsuariosPerfil',
			select:nombrePerfil,
			datos:datos
	}});	}

	});