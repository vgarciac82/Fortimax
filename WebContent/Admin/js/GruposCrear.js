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
	var context_title='Modificar grupos';
	var pantalla = Ext.getBody().getViewSize();
	/* 
	 * 
	 * MODELOS Y STORE
	 * 
	 */

	Ext.define('grupos_model', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {name: 'nombreGrupo',  		type: 'string'},
	        {name: 'descripcion',    type: 'string'}
	    ]
	});
	
	var GruposStore = new Ext.data.Store({ 
        model: 'grupos_model', 
        proxy: { 
            type: 'ajax', 
            	url: link_get_store,  							
            	reader: { 										
                type: 'json', 
                root: 'grupos'
            },
            extraParams: 
	         {
	              action: 'getGrupos',
	              select:''
	          }
        },
        listeners:{
	    	 load:function(){
	    		 if(this.getCount()>0){
	    			 txtNombre.setValue(this.getAt(0).get('nombreGrupo'));
	    			 txtDescripcion.setValue(this.getAt(0).get('descripcion'));
	    		 }
	    	 }
	     },
	autoLoad:false
    });
	Ext.define('UsuariosG_Model', {
	    extend: 'Ext.data.Model',
	    fields: [
	        {name: 'nombreUsuario',  		type: 'string'}
	    ]
	});
	
	var UsuariosGStore = new Ext.data.Store({ 
        model: 'UsuariosG_Model', 
        proxy: { 
            type: 'ajax', 
            url: link_get_store,  							
            	reader: { 										
                type: 'json', 
                root: 'usuarios'
            },
            extraParams: 
	         {
	              action: 'getUsD',
	              select:''
	          } 
        } ,
	autoLoad:false
    });
	var UsuariosSStore = new Ext.data.Store({ 
        model: 'UsuariosG_Model', 
        proxy: { 
            type: 'ajax', 
            url: link_get_store,  							
            	reader: { 										
                type: 'json', 
                root: 'usuarios'
            },
            extraParams: 
	         {
	              action: 'getUsA',
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
	var txtNombre = Ext.create('Ext.form.field.Text', {
		id:'txtNombre',
		name:'txtNombre',
		fieldLabel:'Nombre',
    	allowBlank:false,
    	margin:'10 0 0 20',
    	width:350,
    	emptyText:'Grupo',
    	maxLength:32,
    	enforceMaxLength:true
});
	var txtDescripcion = Ext.create('Ext.form.field.TextArea', {
		id:'txtDescripcion',
		name:'txtDescripcion',
		fieldLabel:'Descripcion',
    	allowBlank:true,
    	margin:'10 0 0 20',
    	width:350,
    	height:50,
    	emptyText:'Descripcion del grupo',
    	maxLength:255,
    	enforceMaxLength:true
});
	var btnGuardar=Ext.create('Ext.button.Button',{
		text: 'Guardar',
		 formBind: false, 
		 iconCls: 'save',
	        handler: function() {
	           if(txtNombre.getValue()!=""){
	        	   Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	        	   Guardar();
	           }
	           else{
	        	   Ext.example.msg("Error","Nombre de grupo no puede ser vacio.");
	           }
	        } 
	});
	
	var columasUN = [
	               {xtype: 'actioncolumn', width: 20, align  : 'center',
	            items: [{icon:'../resources/icons/fam/user_suit.png'}]},
	               {text: "Usuarios", flex: 1, sortable: true, dataIndex: 'nombreUsuario'}

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
    				UsuariosGStore.clearFilter();
    				UsuariosGStore.filter({
    	    			 property: 'nombreUsuario',
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
 
    				UsuariosGStore.clearFilter();
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
                dragText : '{0} usuario seleccionado{1}'
            },
            listeners: {
                drop: function(node, data, dropRec, dropPosition) {
                    Ext.example.msg(data.records[0].get('nombreUsuario'),"Eliminado de grupo");
                }
            }
        },
        store            : UsuariosGStore,
        columns          : columasUN,
        stripeRows       : true,
        width			 :280,
        height			 :pantalla.height-284,
        cls				 :'drag',
        title            : 'Usuarios',
        margins          : '5 0 0 5'
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
    				UsuariosSStore.clearFilter();
    				UsuariosSStore.filter({
    	    			 property: 'nombreUsuario',
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
 
    				UsuariosSStore.clearFilter();
    			}
    		}
    	}
	});
	 var grdDerU = Ext.create('Ext.grid.Panel', {
		 tbar:[txtFiltroDer],
		 multiSelect: true,
	        viewConfig: {
	            plugins: {
	                ptype: 'gridviewdragdrop',
	                dragGroup: 'secondGridDDGroup',
	                dropGroup: 'firstGridDDGroup',
	                dragText : '{0} usuario seleccionado{1}'
	            },
	            listeners: {
	                drop: function(node, data, dropRec, dropPosition) {
	                    Ext.example.msg(data.records[0].get('nombreUsuario'),"Argegado a grupo");
	                }
	            }
	        },
	        store            : UsuariosSStore,
	        columns          : columasUN,
	        stripeRows       : true,
	        width			 :280,
	        cls				 :'drag',
	        height			 :pantalla.height-284,
	        title            : 'Grupo '+grupo,
	        margins          : '5 0 0 20'
	    });
	 var lblMensaje= new Ext.form.Label({
	     id:'lblMensaje',
	     componentCls:'labels',
	      html:'Arrastre los usuarios a la seccion del grupo para asignarlos, y para desasignarlos arrastrelos de nuevo a la seccion de usuarios',
	      shadow:true
	  });
	var panDrag=Ext.create('Ext.form.Panel',{
		id:'panDrag',
		disabled:true,
		name:'panDrag',
		cls:'panDrag',
		layout:'anchor',
		width:600,
		items:[{xtype:'fieldset',name:'fieldM',title:'Asignacion de usuarios al grupo',height:55,items:[lblMensaje]},{xtype:'panel',name:'field',width:600,border:0,layout:'hbox',items:[grdIzqU,grdDerU]}]
	}); 
	var panDatos=Ext.create('Ext.form.Panel',{
		id:'pnDatos',
		name:'pnDatos',
		cls:'pnDatos',
		layout:'anchor',
		title:'Informacion del grupo',
		width:600,
		height:160,
		items:[txtNombre,txtDescripcion]
	}); 

	Ext.create('Ext.form.Panel',{
		id:'panPrincipal',
		name:'panPrincipal',
		cls:'panPrincipal',
		layout:'anchor',
		title:context_title,
		width:'100%',
		height:pantalla.height,
		items:[panDatos,{xtype:'toolbar',width:600,items:['->',btnGuardar]},panDrag],renderTo: Ext.getBody()
	}); 
	/* 
	 * 
	 * FUNCIONES
	 * 
	 */
	inicia();
	function inicia(){
		if(actualizar==true){
			if(grupo!=null){
				GruposStore.load({params: {select: grupo}});
				UsuariosGStore.load({params: {select: grupo}});
				UsuariosSStore.load({params: {select: grupo}});
				panDrag.enable();	
				txtNombre.disable();
			}
		}
		else{
			
		}
	}
	  var successAjaxFnNNew = function(response, request) {
	        var jsonData = Ext.JSON.decode(response.responseText);
	        if (true == jsonData.success) {
	            Ext.Msg.show({
	                title: 'Correcto',
	                msg: jsonData.message,
	                buttons: Ext.Msg.OK,
	                icon: Ext.MessageBox.INFO
	            });
	            actualizar=true;
	            panDrag.enable();
	            panDatos.disable();
	            grupo=txtNombre.getValue();
	            UsuariosGStore.load({params: {select: grupo}});
				UsuariosSStore.load({params: {select: grupo}});
	            
	        } else {
	            Ext.Msg.show({
	                title: 'Error',
	                msg: jsonData.message,
	                buttons: Ext.Msg.OK,
	                animEl: 'elId',
	                icon: Ext.MessageBox.ERROR
	            });
	        }
	        Ext.getBody().unmask();
	    };
	    var successAjaxFnN = function(response, request) {
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
	        Ext.getBody().unmask();
	    };
	function Guardar(){
		if(actualizar==false){
			var grupoModel = Ext.create('grupos_model', {
 			    nombreGrupo : txtNombre.getValue(),
 			    descripcion  : txtDescripcion.getValue()
 			});	
			grupoModel=Ext.JSON.encode(grupoModel.data);
			Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: successAjaxFnNNew,   
					timeout: 30000,  
					params: {  
					action: 'crearGrupo',  				
					grupo:grupoModel
			}});
		}
		else{			
			UsuariosSStore.clearFilter();
			var usrs=new Array();
			for(var i=0;i<grdDerU.store.getCount();i++){
				var usr = Ext.create('UsuariosG_Model', {
	 			    nombreUsuario : grdDerU.store.getAt(i).get('nombreUsuario')
	 			});	
				usrs.push(usr.data);
			}
			usrs=Ext.JSON.encode(usrs);
			var update = Ext.create('grupos_model', {
				nombreGrupo: txtNombre.getValue(),
				descripcion: txtDescripcion.getValue()
 			});	
			update=Ext.JSON.encode(update.data);
			Ext.Ajax.request({  
				url: rutaServlet,  
				method: 'POST',  
				 success: successAjaxFnN,   
				timeout: 30000,  
				params: {  
				action: 'updateGrupo',
				usuarios:usrs,
				mods:update
		}});
		}
	}
});//Termina funcion onReady