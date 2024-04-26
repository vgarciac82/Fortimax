/* 
 * 
 * 
 * FUNCION ONREADY
 * 
 * 
 */
Ext.Loader.setConfig({
    enabled: true
});


Ext.require([
     'Ext.form.*',
     'Ext.tip.*',
     'Ext.toolbar.Paging',
     'Ext.grid.feature.RowBody',
     'Ext.grid.feature.RowWrap',
     'Ext.ux.RowExpander',
     'Ext.tree.*',
     'Ext.data.*'
]);
Ext.onReady(function() { 
	
	var TituloPanelPrincipal='Administrador de Privilegios';
	var contextTitle='Usuarios';
	var GavMod=new Array();
	var Gaveta="";
	var GavI="";
	
	var actionU="";
	var actionG="";
	var actionGSe="";
	var actionP="";
	var actionGeneral="";
	var actionGuardar="";
	var contextName="";
	

	/* 
	 * 
	 * 
	 * STORE
	 * 
	 * 
	 */
	Ext.define('PrivilegiosGModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'texto',     type: 'string'}
        ]
    });
	Ext.define('UsuariosModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'nombreControl', type: 'string'}
	     ]
	 });
	 Ext.define('GavetasModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'gaveta', type: 'string'},
	         {name: 'descripcion', type: 'string'}
	     ]
	 });
	 Ext.define('GavetasModelSelect', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'gaveta', type: 'string'}
	     ]
	 });
	 Ext.define('PrivilegiosModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	          {name: 'privilegio', type: 'string'},
	         {name: 'descripcion', type: 'string'},
	         {name:'seleccionado', type:'string'}
	     ]
	 });
	 
	 Ext.define('ExportModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	          {name: 'nombreC', type: 'string'},
	         {name: 'gavetas', type: 'string'},
	         {name:'gavetasE', type:'string'}
	     ]
	 });
	 Ext.define('ExportGModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	          {name: 'gaveta', type: 'string'},
	         {name: 'privilegios', type: 'string'}
	     ]
	 });
	 Ext.define('ExportGEliModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	          {name: 'gaveta', type: 'string'}
	     ]
	 });
	 Ext.define('ExportPModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	          {name: 'privilegio', type: 'string'}
	     ]
	 });
	 Ext.define('UsuariosG_Model', {
		    extend: 'Ext.data.Model',
		    fields: [
		        {name: 'nombreUsuario',  		type: 'string'}
		    ]
		});
	 var UsuariosSStore = new Ext.data.Store({ 
	        model: 'UsuariosG_Model', 
	        proxy: { 
	            type: 'ajax', 
	            url: rutaServlet,  							
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
	 var UsuariosStore = Ext.create('Ext.data.Store', {
	     model: 'UsuariosModel',
	     proxy: {
	         type: 'ajax',
	         url: rutaServlet,
	         reader: {
	             type: 'json',
	             root: 'nControl'
	         },
	         extraParams: 
	         {
	              action: actionU 
	          }
	     },
	     autoLoad: false  
	 });
	 var GavetasStore = Ext.create('Ext.data.Store', {
	     model: 'GavetasModel',
	     proxy: {
	         type: 'ajax',
	         url: rutaServlet,
	         reader: {
	             type: 'json',
	             root: 'gavetas'
	         },
	         extraParams: 
	         {
	              action: actionG
	          }
	     },
	     listeners:{
	    	 load:function(){
	    		 if(!LDAP){//Eliminar el IF una vez este en produccion con los metodos requeridos
	    		 GavetasStoreSelect.load({params: {action:actionGSe,select: Usuario}});
	    		 }
	    		 else{
	    			 GavetasStoreSelect.load({url:'../../jsonPrueba/getGavetasSelectLDAP.json'},{params: {action:actionGSe,select: Usuario}});
	    		 }
	    	 }
	     },
	     autoLoad:false
	 });
	 var GavetasStoreSelect = Ext.create('Ext.data.Store', {
	     model: 'GavetasModelSelect',
	     proxy: {
	         type: 'ajax',
	         url: rutaServlet,
	         reader: {
	             type: 'json',
	             root: 'gavetas'
	         },
	         extraParams: 
	         {
	              action: actionGSe,
	              select: ''
	          }
	     },
	     listeners:{
	    	 load:function(){
	    		 grdGavetas.disable();
	    		 cbu.deselectAll();
	    		 for(var i=0;i<this.getCount();i++){
    				 cbu.select(GavetasStore.findRecord('gaveta',this.getAt(i).get('gaveta'),0),true);
    			 
    		 }
	    		 if(this.getCount()==GavetasStore.getCount()){
	    			 btnSAll.setText('Ninguna');
	    		 }
	    		 else{
	    			 btnSAll.setText('Todas');
	    		 }
	    		 btnGuardar.disable();
	    		 grdGavetas.enable();
	    		 grdGavetas.setScrollTop(0);
	    	 }
	     },
	     autoLoad:false
	 });
	 var PrivilegiosStore = Ext.create('Ext.data.Store', {
	     model: 'PrivilegiosModel',
	     proxy: {
	         type: 'ajax',
	         url: rutaServlet,	//'../JsonPrueba/privilegios.json',
	         reader: {
	             type: 'json',
	             root: 'privilegios'
	         },
	         extraParams: 
	         {
	              action: actionP,
	              usuario:"",
	              gaveta:""
	          }
	     },
	     listeners:{
	    	 load:function(){
	    		 cbp.selectAll();
	    		 for(var i=0;i<this.getCount();i++){
		    		 cbp.deselect(this.findRecord('seleccionado', '0',i));
		    		 }
	        	 CargaLocal(Gaveta);
	        	 if(GavI==""){
	        		 	var S=new Array();
	        			for(var i=0;i<cbp.getSelection().length;i++){
	        				S[i]=cbp.getSelection()[i].raw.privilegio;		        				
	        			}
     				modL(Gaveta,S);
     				GavI="1";
     				
     			}
	        		if(this.getCount()==cbp.getSelection().length){
	   	    			 btnSAllP.setText('Ninguno');
	   	    		 }
	   	    		 else{
	   	    			 btnSAllP.setText('Todos');
	   	    		 }
	        
	        	 btnGuardar.enable();
	    	 }
	     },
	     autoLoad: false
	 });
	 var PrivilegiosGStore = Ext.create('Ext.data.TreeStore', {
	        model: 'PrivilegiosGModel',
	        autoLoad: false,
	        proxy: {
	            type: 'ajax',
	            url: rutaServlet,//'../JsonPrueba/privilegiosGuardados.json',
	            reader: {
		             type: 'json'
		         },
		         extraParams: 
		         {
		              action: '',//getPrivG
		              select:""
		          }
	        },
	        folderSort: true
	        
	    });
	/* 
	 * 
	 * 
	 * OBJETOS
	 * 
	 * 
	 */
	 var txtFiltroUs = Ext.define('Ext.ux.CustomTrigger', {
			extend: 'Ext.form.field.Trigger',
			  alias: 'widget.xtext',
			  triggerTip: 'Click para filtrar.',
			  triggerBaseCls :'x-form-trigger',
			  triggerCls:'x-form-clear-trigger',
			  width:285,
			  emptyText:'Filtrar...',
		    onTriggerClick: function() {
		        this.setValue("");
		    },
		    listeners:{
	    		change:function(){
	    			if(this.getValue()!=""){
	    				UsuariosStore.clearFilter();
	    				UsuariosStore.filter({
	    	    			 property: 'nombreControl',
	    	   	  		     value: this.getValue(),
	    	   	  		     anyMatch: true,
	    	   	  		     caseSensitive: false
	    	    			});
	    			}
	    			else{
	 
	    				UsuariosStore.clearFilter();
	    			}
	    		}
	    	}
		});
		var cbus = Ext.create('Ext.selection.CheckboxModel',{name:'cbus',id:'cbus',mode:'SINGLE',checkOnly:false});
	var grdUsuarios = Ext.create('Ext.grid.Panel', {
	        store: UsuariosStore,
	        selModel:cbus,
	        id:'grdUsuarios',
	        overCls:'over',
	        tbar:[txtFiltroUs],
	        columns: [
	            {xtype: 'rownumberer', width:25},
	            {text: contextTitle, width: 215, dataIndex: 'nombreControl'}],
	            listeners:{
		        	itemclick:function(g,r,i,ind){
		        		if(GavetasStore.getCount()==0){
		        			Limpiar();
		        			GavetasStore.load({params:{action:actionG}});
		        			grdGavetas.disable();
		        			Usuario=UsuariosStore.getAt(ind).get('nombreControl');
		        			PrivilegiosGStore.load({params:{ action:actionGeneral,select:UsuariosStore.getAt(ind).get('nombreControl')}});
			        		grdGavetas.columns[3].setText("Gavetas: "+Usuario);	
			        		grdPrivilegiosGuardados.setTitle('Privilegios de: '+Usuario);
		
		        		}
		        		else{
		        		Limpiar();
		        		Usuario=UsuariosStore.getAt(ind).get('nombreControl');
		        		GavetasStoreSelect.load({params: {action:actionGSe,select: UsuariosStore.getAt(ind).get('nombreControl')}});
		        		PrivilegiosGStore.load({params:{ action:actionGeneral,select:UsuariosStore.getAt(ind).get('nombreControl')}});
		        		grdGavetas.columns[3].setText("Gavetas: "+Usuario);	
		        		grdPrivilegiosGuardados.setTitle('Privilegios de: '+Usuario);
		        		
		        		}
		        	}},
	        columnLines: true,
	        width: 300,
	        height: 300,
	        frame: true
	    });
	var columasUN = [
		               {xtype: 'actioncolumn', width: 20, align  : 'center',
		            items: [{icon:'../resources/icons/fam/user_suit.png'}]},
		               {text: Usuario, flex: 1, sortable: true, dataIndex: 'nombreUsuario'}

		           ];
	var grdUsuariosGrupo = Ext.create('Ext.grid.Panel', {
        store: UsuariosSStore,
        id:'grdUsuariosGrupo',
        overCls:'over',
        columns: [columasUN],
        columnLines: true,
        width: 280,
        hidden :true,
        height: 300,
        frame: true
    });
	var cbu = Ext.create('Ext.selection.CheckboxModel',{name:'cbu',id:'cbu',mode:'MULTI',checkOnly:true,enableKeyNav:false,
	listeners:{
		deselect:function(){
			btnGuardar.enable();
		},
	select:function(){
		if(Usuario==""){
			Ext.Msg.alert('Alert', 'Selecciona un '+contextName);
			this.deselectAll();
		}
	}
	}	
	});
	var txtFiltroGav = Ext.define('Ext.ux.CustomTrigger', {
		extend: 'Ext.form.field.Trigger',
		  alias: 'widget.xtext',
		  triggerTip: 'Click para filtrar.',
		  triggerBaseCls :'x-form-trigger',
		  triggerCls:'x-form-clear-trigger',
		  width:215,
		  emptyText:'Filtrar...',
	    onTriggerClick: function() {
	        this.setValue("");
	    },
	    listeners:{
    		change:function(){
    			if(this.getValue()!=""){
    				GavetasStore.clearFilter();
    				GavetasStore.filter({
    	    			 property: 'gaveta',
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
    				GavetasStore.clearFilter();
    			}
    		}
    	}
	});
	var btnSAll=Ext.create('Ext.button.Button',{
		text: 'Todas',
		 formBind: false,
			 handler:function(){
				 btnGuardar.enable();
				 var S=new Array();
				 if(cbu.getSelection().length==GavetasStore.getCount()){
					 this.setText("Todas");
					cbu.deselectAll();
				 }
				 else{
					 grdGavetas.disable();
					 this.setText("Ninguna");
					 for(var i=0;i<GavetasStore.getCount();i++){
						 if(cbu.isSelected(i)==false){
						 modL(GavetasStore.getAt(i).get('gaveta'),S);
						 cbu.select(i,true);
						 }
					 }
					 grdGavetas.enable();
					 grdGavetas.setScrollTop(0);
					 
				 }
			 }});
	 var grdGavetas = Ext.create('Ext.grid.Panel', {
	        store: GavetasStore,
	        selModel:cbu,
	        id:'grdGavetas',
	        overCls:'over',
	        cls:'custom-grid',
	        tbar:[btnSAll,txtFiltroGav],
	        columns: [
	            {xtype: 'rownumberer', width:25},
	            {text: "Gavetas", width: 215, dataIndex: 'gaveta'}],
	            plugins: [{
		            ptype: 'rowexpander',
		            rowBodyTpl : [
		            "<p><b>Descripcion:</b> {descripcion}</p><br>"
		            ]
		        }] ,
		        listeners:{
		        	itemclick:function(g,r,i,ind){
		        		if(Usuario!=""){
		        		if(cbu.isSelected(ind)==true){
		        			var S=new Array();
		        		if(Gaveta!=""){		        			
		        			for(var i=0;i<cbp.getSelection().length;i++){
		        				S[i]=cbp.getSelection()[i].raw.privilegio;
		        					
		        			}
		        			modL(Gaveta,S);		
		        		}			        		
		        		Gaveta=r.raw.gaveta;
		        		PrivilegiosStore.load({params: {action:actionP,usuario: Usuario,gaveta:r.raw.gaveta}});
		        		grdPrivilegios.columns[3].setText("Privilegios: "+Gaveta);		        		
		        		}
		        		else{
		        			Ext.example.msg('Gaveta no asignada', 'No se pueden leer los privilegios');
		
		        		}
		        		}}},
	        columnLines: true,
	        width: 300,
	        height: 300,
	        frame: true
	    });
	 var txtFiltroPriv = Ext.define('Ext.ux.CustomTrigger', {
			extend: 'Ext.form.field.Trigger',
			  alias: 'widget.xtext',
			  triggerTip: 'Click para filtrar.',
			  triggerBaseCls :'x-form-trigger',
			  triggerCls:'x-form-clear-trigger',
			  width:215,
			  emptyText:'Filtrar...',
		    onTriggerClick: function() {
		        this.setValue("");
		    },
		    listeners:{
	    		change:function(){
	    			if(this.getValue()!=""){
	    				PrivilegiosStore.clearFilter();
	    				PrivilegiosStore.filter({
	    	    			 property: 'privilegio',
	    	   	  		     value: this.getValue(),
	    	   	  		     anyMatch: true,
	    	   	  		     caseSensitive: false
	    	    			});
	    			}
	    			else{
	    				PrivilegiosStore.clearFilter();
	    			}
	    		}
	    	}
		});
	 var btnSAllP=Ext.create('Ext.button.Button',{
			text: 'Todos',
			 formBind: false,
				 handler:function(){

					 if(cbp.getSelection().length==PrivilegiosStore.getCount()){
						 this.setText("Todos");
						cbp.deselectAll();
					 }
					 else{
						 cbp.selectAll();
						 this.setText("Ninguno");
						 grdPrivilegios.setScrollTop(0);						 
					 }
				 }});
	 var cbp = Ext.create('Ext.selection.CheckboxModel',{name:'cbp',id:'cbp',mode:'MULTI',checkOnly:true,enableKeyNav:false});
	 var grdPrivilegios = Ext.create('Ext.grid.Panel', {
	        store: PrivilegiosStore,
	        selModel:cbp,
	        id:'grdPrivilegios',
	        overCls:'over',
	        tbar:[btnSAllP,txtFiltroPriv],
	        columns: [
	            {xtype: 'rownumberer', width:25},
	            {text: "Privilegio", width: 215, dataIndex: 'privilegio'}],
	            plugins: [{
		            ptype: 'rowexpander',
		            rowBodyTpl : [
		            "<p><b>Descripcion:</b> {descripcion}</p>"
		            ]
		        }] ,
		       
	        columnLines: true,
	        width: 300,
	        height: 300,
	        frame: true
	    });


	 var btnGuardar=Ext.create('Ext.button.Button',{
			text: 'Guardar',
			 formBind: false,
			 iconCls:'disk',
			 disabled:true,
				 handler:function(){
					 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		        			var S=new Array();
		        			for(var i=0;i<cbp.getSelection().length;i++){
		        				S[i]=cbp.getSelection()[i].raw.privilegio;	        					
		        			}
		        			modL(Gaveta,S);				        		
		        			Guardar();
		        			for(i in GavMod){
						 GavMod[i]=null;
						 }
						 Gaveta="";
						 PrivilegiosStore.removeAll();
						 cbu.deselectAll();
						 cbus.deselectAll();
						 grdGavetas.columns[3].setText("Gavetas");
						 Usuario="";
						 this.disable();
						 PrivilegiosGStore.load({params:{ action:'',select:''}});
						 grdPrivilegios.columns[3].setText("Privilegios");
						 grdPrivilegiosGuardados.setTitle('Privilegios');
					 	
				 }
		   
		});
	 var grdPrivilegiosGuardados = Ext.create('Ext.tree.Panel', {
		   tbar: [  '->',{itemId: 'Exportar',disabled: true,iconCls: 'export',xtype: 'button', text: 'Exportar'}],
	    width: 900, cls:'grArbol',id:'grdPrivilegiosGuardados',height: 340, collapsible: false, 
	    useArrows: true, rootVisible: false, store: PrivilegiosGStore, multiSelect: false,
	       title:contextTitle, singleExpand: true,
	        columns: [{
	            xtype: 'treecolumn',flex: 2,sortable: true,dataIndex: 'texto'
	        }]
	    });
	var panGrids=Ext.create('Ext.form.Panel',{
		id:'panGrids',
		name:'panGrids',
		cls:'panGrids',
		layout:'hbox',
		width:900,
		items:[
		       		grdUsuariosGrupo,grdUsuarios,grdGavetas,grdPrivilegios
		      ]
	});
	var toolbar=Ext.create('Ext.toolbar.Toolbar', {
	    width   : 900,
	    cls:'tool',
	    height:35,
	    items: ['->',btnGuardar]
	});
	var panUsuarios=Ext.create('Ext.form.Panel',{
		id:'panUsuarios',
		name:'panUsuarios',
		cls:'panUsuarios',
		layout:'hbox',
		width:900,
		items:[
		       grdPrivilegiosGuardados	
		      ]
	});
	//Panel Principal
	var pantalla = Ext.getBody().getViewSize();
	Ext.create('Ext.form.Panel',{
		id:'panPrincipal',
		name:'panPrincipal',
		cls:'panPrincipal',
		layout:'anchor',
		title:TituloPanelPrincipal,
		componentCls:'tamMax',
		width:'100%',
		height:pantalla.height,
		autoScroll : true,
		items:[
		       	panGrids,toolbar,panUsuarios
		      ],renderTo: Ext.getBody()
	}); 
	
	
	/* 
	 * 
	 * 
	 * FUNCIONES
	 * 
	 * 
	 */
	inicia();
	function inicia(){
		if(aGrupo!=""){
			if(!LDAP){
			actionU="getGrupoPriv";
			actionG="getGavetPriv";
			actionP="getPrivGru";
			actionGeneral="getPrivGG";
			actionGuardar="modPriviG";
			actionGSe="getGSelectG";
			contextName="grupo";
			UsuariosSStore.load({params: {select: Usuario}});
			grdUsuarios.setVisible(false);
			grdUsuariosGrupo.setVisible(true);
			}
			else{
				actionU="getGrupoPrivLDAP";
				actionG="getGavetPriv";
				actionP="getPrivGru";//"getPrivGrupoLDAP";
				actionGeneral="getPrivGeneralLDAP";
				actionGuardar="modPriviGLDAP";
				actionGSe="getGSelectLDAP";
				contextName="grupo";
				UsuariosSStore.load({url:'../../jsonPrueba/getUsersLDAP.json'},{params: {select: Usuario,action:'getUsuariosLDAP'}});
				grdUsuarios.setVisible(false);
				grdUsuariosGrupo.setVisible(true);
			}
			if(aGrupo=="true"){	
				if(!LDAP){
				grdUsuarios.columns[2].setText("Grupos");
				UsuariosStore.load({params: {action: actionU}});
				PrivilegiosGStore.load({params:{ action:actionGeneral,select:Usuario}});
				grdUsuarios.disable();
				grdGavetas.enable();
				grdPrivilegios.enable();
				
				if(GavetasStore.getCount()==0){
        			GavetasStore.load({params:{action:actionG}});
        			grdGavetas.disable();
	        		grdGavetas.columns[3].setText("Gavetas: "+Usuario);	
	        		grdPrivilegiosGuardados.setTitle('Privilegios de: '+Usuario);

        		}
        		else{
        		GavetasStoreSelect.load({params: {action:actionGSe,select: Usuario}});
        		grdGavetas.columns[3].setText("Gavetas: "+Usuario);	
        		grdPrivilegiosGuardados.setTitle('Privilegios de: '+Usuario);
        		
        		}
				
			}
				else{
					//alert('GruposLDAP');
					grdUsuarios.columns[2].setText("Grupos LDAP");
					//UsuariosStore.load({url:'../../jsonPrueba/getUsersLDAP.json'},{params: {action: actionU}});
					PrivilegiosGStore.load({url:'../../jsonPrueba/getTreeUsersLDAP.json'},{params:{ action:actionGeneral,select:Usuario}});
					grdUsuarios.disable();
					grdGavetas.enable();
					grdPrivilegios.enable();
					if(GavetasStore.getCount()==0){
						GavetasStore.load({params:{action:actionG}});
						grdGavetas.disable();
		        		grdGavetas.columns[3].setText("Gavetas: "+Usuario);	
		        		grdPrivilegiosGuardados.setTitle('Privilegios de: '+Usuario);
					}
				}
			}
			
			else{
				grdUsuarios.columns[2].setText("Grupos");
				UsuariosStore.load({params: {action: 'getGrupoPriv'}});
			}
		}
		else{
			actionU="getUserPriv";
			actionG="getGavetPriv";
			actionP="getPriv";
			actionGeneral="getPrivG";
			actionGuardar="modPriviU";
			actionGSe="getGSelect";
			contextName="usuario";
			if(Usuario!=""){
				
				UsuariosStore.load({params: {action: actionU}});
//				GavetasStore.load({params: {action:actionG}});
				PrivilegiosGStore.load({params:{ action:actionGeneral,select:Usuario}});
				grdUsuarios.disable();
				grdGavetas.enable();
				grdPrivilegios.enable();
				
				if(GavetasStore.getCount()==0){
        			GavetasStore.load({params:{action:actionG}});
        			grdGavetas.disable();
	        		grdGavetas.columns[3].setText("Gavetas: "+Usuario);	
	        		grdPrivilegiosGuardados.setTitle('Privilegios de: '+Usuario);

        		}
        		else{
        		GavetasStoreSelect.load({params: {action:actionGSe,select: Usuario}});
        		grdGavetas.columns[3].setText("Gavetas: "+Usuario);	
        		grdPrivilegiosGuardados.setTitle('Privilegios de: '+Usuario);
        		
        		}
				
			}
			else{
				UsuariosStore.load({params: {action: actionU}});
//				GavetasStore.load({params: {action:actionG}});
			}
		}
	}
	function modL(gaveta,privilegio){
		GavMod[gaveta]=privilegio;
	}
	function CargaLocal(gav){
		if(gav!=""){
		if(GavMod[gav]!=null){
			cbp.selectAll();
			var arrD=new Array();
			for(var i=0;i<PrivilegiosStore.getCount();i++){
				arrD.push(i);
			}
			for(var i=0;i<GavMod[gav].length;i++){
				arrD[PrivilegiosStore.find('privilegio', GavMod[gav][i])]="n";
			}
			for(var x=0;x<arrD.length;x++){
				if(arrD[x]!="n"){
					cbp.deselect(arrD[x]);
				}
			}
			 			 
		}
	}
	}
	 var successAjaxFnN = function(response, request) {
	        var jsonData = Ext.JSON.decode(response.responseText);
	        if (true == jsonData.success) {
	            Ext.Msg.show({
	                title: 'Asignación de  privilegios',
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
		var DatosG=new Array();
		for(i in GavMod){
			if(cbu.isSelected(GavetasStore.find('gaveta',i))==true){
				if(GavMod[i]!=null){
				var DatosD = new Array();				
				for(var x=0;x<GavMod[i].length;x++){
					var prilegioD = Ext.create('ExportPModel', {
		 			    privilegio : GavMod[i][x]
		 			});	
					DatosD.push(prilegioD.data); 
				}
				DatosD = Ext.JSON.encode(DatosD);
				var GavetasD=Ext.create('ExportGModel',{
					gaveta:i,
					privilegios:DatosD
				});
				DatosG.push(GavetasD.data);
			}
			}
		}
		
		var Eli=new Array();
		for(var y=0;y<GavetasStore.getCount();y++){
			if(cbu.isSelected(y)==false){
//				Eli.push(GavetasStore.getAt(y).get('gaveta'));
				var GaveEli = Ext.create('ExportGEliModel', {
	 			    gaveta : GavetasStore.getAt(y).get('gaveta')
	 			});	
				Eli.push(GaveEli.data);
			}			
		}
		Eli=Ext.JSON.encode(Eli);
		DatosG=Ext.JSON.encode(DatosG);
		var Cambios=Ext.create('ExportModel',{
			nombreC:Usuario,
			gavetas:DatosG,
			gavetasE:Eli
		});
		Cambios=Ext.JSON.encode(Cambios.data);
		Ext.Ajax.request({  
				url: rutaServlet,  
				method: 'POST',  
				 success: successAjaxFnN,   
				timeout: 30000,  
				params: {  
				action: actionGuardar,  				
				Modificaciones:Cambios
				 }});
	
	}
	function Limpiar(){
		for(i in GavMod){
			 GavMod[i]=null;
			 }
			 Gaveta="";
			 PrivilegiosStore.removeAll();
			 cbu.deselectAll();
			 grdGavetas.columns[3].setText("Gavetas");
			 Usuario="";
			 btnGuardar.disable();
			 PrivilegiosGStore.load({params:{ action:'',select:''}});
			 grdPrivilegios.columns[3].setText("Privilegios");			  
	}
});//Termina OnReady