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
	var context_title='Estructuras';
	var pantalla = Ext.getBody().getViewSize();
	var contextTitle="Estructuras";
	var contador=0;
	var actionGetEstructura="";
	var atributosGuardados=false;
	var atributoEditando="";
	var valorEditando="";
	/* 
	 * 
	 * MODELOS Y STORE
	 * 
	 */
	Ext.define('ArbolModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'ide',     type: 'string'},
            {name: 'texto',     type: 'string'},
            {name:'tipo', type:'string'},
            {name:'atributos', type:'string'}
        ]
    });
	Ext.define('exportModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'Nombre',     type: 'string'},
            {name:'Descripcion', type:'string'},
            {name:'Definicion', type:'string'}
        ]
    });
    Ext.define('atributoDocumentoModel', {
        extend: 'Ext.data.Model',
        fields: [
            {name:'atributo', type:'string'},
            {name:'activo', type:'bool'},
            {name:'valor', type:'string'},
            {name:'modificable', type:'bool'},
            {name:'descripcion', type:'string'}
        ]
    });
	var EstructuraStore = new Ext.data.Store({ 
        model: 'exportModel', 
        proxy: { 
            type: 'ajax', 
            	url: rutaServlet,  							
            	reader: { 										
                type: 'json', 
                root: 'estructura'
            },
            extraParams: 
	         {
	              action: 'getEstructuraDatos',
	              select:''
	          }
        },
        listeners:{
	    	 load:function(){
	    		 if(this.getCount()>0){
	    			 txtNombre.setValue(this.getAt(0).get('Nombre'));
	    			 txtDescripcion.setValue(this.getAt(0).get('Descripcion'));
	    		 }
	    	 }
	     },
	autoLoad:false
    });
    var atributosStore = new Ext.data.Store({ 
        model: 'atributoDocumentoModel', 
        proxy: {
	        type: 'memory',
	        reader: {
	            type: 'json',
	            root: 'atributos'
	        }
    	},
    	data:{'atributos':[
        { 'atributo': 'Requerido',  "activo":"false",  "valor":"false","modificable":"false","descripcion":"Requerido"  },
        { 'atributo': 'Vigencia',  "activo":"true",  "valor":"10d","modificable":"true","descripcion":""  },
        { 'atributo': 'Vencimiento',  "activo":"false",  "valor":"11/02/2013","modificable":"false","descripcion":""  },
        { 'atributo': 'Historico',  "activo":"false",  "valor":"false","modificable":"false","descripcion":""  },
        { 'atributo': 'Existencia fisica',  "activo":"true",  "valor":"DESCONOCIDA","modificable":"true","descripcion":""  }
    ]},
	autoLoad:false
    });
	Ext.define('exportModelArbol', {
        extend: 'Ext.data.Model',
        fields: [
            {name: 'Texto',     type: 'string'},
            {name:'Tipo', type:'string'},
            {name:'Ide', type:'string'},
            {name:'Parent', type:'string'},
            {name:'Profundidad', type:'string'},
            {name:'atributos', type:'string'}
        ]
    });
	 var ArbolStore = Ext.create('Ext.data.TreeStore', {
	        model: 'ArbolModel',
	        autoLoad: false,
	        proxy: {
	            type: 'ajax',
	            url: rutaServlet,//'../JsonPrueba/estructura.json',
	            reader: {
		             type: 'json'
		         },
		         extraParams: 
		         {
		              action: actionGetEstructura,//getPrivG
		              select:""
		          }
	        },
	        listeners:{
		    	 load:function(){
		    		 if(ArbolStore.getRootNode().childNodes.length>0){
		    		 setMax(0);
		    		 contador=parseInt(contador)+1;
		    		 }
		    	 }
		     },
	        folderSort: true,
	        autoLoad:false
	        
	    });
	 Ext.override(Ext.data.AbstractStore,{
		    indexOf: Ext.emptyFn
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
    	emptyText:'Estructura',
    	maxLength:16,
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
    	emptyText:'Descripcion de la estructura',
    	maxLength:80,
    	enforceMaxLength:true
});
	var btnGuardar=Ext.create('Ext.button.Button',{
		text: 'Guardar',
		 formBind: false, 
		 iconCls: 'save',
		 disabled: true,
		 handler: function() {
			 Ext.Msg.show({
				 title:'Guardar',
				 msg: actualizar?'Los cambios afectarán sólo a los expedientes <b>creados a partir de ahora</b>. ¿Desea continuar?':'&iquestDesea guardar estructura?',
				 buttons: Ext.Msg.OKCANCEL,
				 icon: Ext.Msg.WARNING,
				 fn: function(btn){
					 if(btn=='ok'){
						 if(txtNombre.getValue()!=""){
							 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
							 Guardar();
						 }
						 else{
							 Ext.example.msg("Error","Nombre de la estructura no puede ser vacio.");
						 }
					 }
				 }
			 });
		 } 
	});
	var panDatos=Ext.create('Ext.form.Panel',{
		//title:'Informacion de la estructura',
		id:'pnDatos',
		name:'pnDatos',
		cls:'pnDatos',
		layout:'anchor',
		//width:pantalla.width/2,
		height:120,
		items:[txtNombre,txtDescripcion]
	}); 
	
	var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
    	clicksToEdit: 2,
    	listeners: {
            beforeedit: function(e, editor){
                if (e.record.data.ide == '0')
                    return false;
            }
        }
    });
	var grdArbol = Ext.create('Ext.tree.Panel', {
		   tbar: [  '->',{itemId: 'Exportar',disabled: true,iconCls: 'export',xtype: 'button', text: 'Exportar'}],
	   // width: 600, 
	    cls:'grdArbol',
	    id:'grdArbol',
	    height: pantalla.height-260,
	    autoScroll:true,
	    viewConfig:{plugins:{
	    	ptype:'treeviewdragdrop',
	    	dragText : 'Modificar estructura'
	    },
	    toggleOnDblClick: false,singleExpand:false },
	    collapsible: false, 
	    useArrows: true, 	    
	    store: ArbolStore,
	    multiSelect: false, 
	    hideHeaders:true,
	        columns: [{
	            xtype: 'treecolumn',flex: 2,sortable: true,dataIndex: 'texto',editor:{xtype:'textfield',allowBlank:false}
	        }],
	        plugins: [cellEditing],
			listeners:{
				itemcontextmenu:function( gr, record, item, index, e, eOpts ){
					 e.stopEvent();
					 context(e.xy,record,item,index);
//					contextMenu.showAt(e.xy);
				},
				beforeitemmouseup:function( _this, record, item, index, e, eOpts ){
					if(record.data.tipo=='documento'){
						mostrarAtributos(record);
						
					}
					else{
						panelDer.setDisabled(true);
					}
				}
			}, rootVisible: false
			
	    });
	   /*var grdAtributos=Ext.create('Ext.grid.Panel', {
		    store: atributosStore,
		    columns: [
		        { header: 'Atributo',  dataIndex: 'atributo' ,flex: 2},
		        { header: 'Activo', dataIndex: 'activo', flex: 1 },
		        { header: 'Valor', dataIndex: 'valor',flex: 2 },
		        { header: 'Modificable', dataIndex: 'modificable',flex: 1 },
		        { header: 'Descripcion', dataIndex: 'descripcion',flex: 2 }
		    ],
		    listeners:{
		    	beforerender:function(){
		   
		    	}
		    }
		});*/
		var proAtributos=Ext.create('Ext.grid.property.Grid', {
		    source: {
		        "Requerido": false,
		        "Vigencia":'',
		        "Vencimiento": Ext.Date.parse('01/01/2013', 'd/m/Y'),
		        "Historico": false,
		        "Existencia fisica": ''
		    },
		    listeners:{
		    	beforepropertychange:function( source, recordId, value, oldValue, eOpts ){
		    		if(!atributosGuardados){
		    			if(recordId=='Vencimiento'){
							var f=new Date(value);
							var d=f.getDate().toString().length==2?f.getDate():'0'+f.getDate();
							var m=(f.getMonth()+1).toString().length>1?(f.getMonth()+1).toString():'0'+(f.getMonth()+1).toString();
							value=(d+'/'+m+'/'+f.getFullYear()).toString();
						}	
		    			modificarAtributosDeFila(recordId,value);
		    		}
		    	},
		    	itemclick:function( _this, record, item, index, e, eOpts ){
		    		Ext.getCmp('fieldSetExtras').setTitle(record.data.name+':');
		    		Ext.getCmp('fieldSetExtras').setDisabled(false);
		    		valorEditando=record.data.value;
		    		atributoEditando=record.data.name;
		    		var a=new Array();
		    		var m = grdArbol.getSelectionModel().getSelection();
					a=Ext.JSON.decode(m[0].data.atributos);
					for(var i=0;i<a.length;i++){
						if(a[i].atributo==record.data.name){
							chbActivo.setValue(a[i].activo);
							chbModificable.setValue(a[i].modificable);
							txtDescripcionAtributo.setValue(a[i].descripcion);
							break;
						}
					}
		    	}
		    }
		});
	var lblMensaje= new Ext.form.Label({
	     id:'lblMensaje',
	     componentCls:'labels',
	      html:'Para empezar a crear/modificar la estructura coloquese en la raiz <font class="bold">"/"</font> o en el elemento deseado y '+ 
	    	  'de click derecho, en el menu emergente seleccione <font class="bold">"Agregar Documento"</font>'+
	    	  ', <font class="bold">"Agregar Carpeta"</font> o <font class="bold">"Eliminar elemento"</font> '+ 
	          'hasta formar la estrucutura requerida. Tambien puede arrastrar y reacomodar los elementos',
	      shadow:true
	  });
	  var chbActivo = new Ext.form.Checkbox({  
		    columns:1,
		    boxLabel: 'Activo', 
		    name: 'chbActivo', 
		    checked: true   ,
		    margin:'5 0 0 ',
		    listeners:{
		    	change:function(_this, newValue, oldValue, eOpts ){
		    		if(atributoEditando!="")
		    			modificarAtributosDeFila(atributoEditando,valorEditando);
		    	}
		    	//  
		    }
		});  
		 var chbModificable = new Ext.form.Checkbox({  
		    columns:1,
		    boxLabel: 'Modificable', 
		    name: 'chbModificable', 
		    checked: true,
		    margin:'5 0 0 15',
		     listeners:{
		    	change:function(_this, newValue, oldValue, eOpts ){
		    		if(atributoEditando!="")
		    			modificarAtributosDeFila(atributoEditando,valorEditando);
		    	}
		    }
		});
		var txtDescripcionAtributo=new Ext.form.TextArea({
			id:'txtDescripcionAtributo',
			name:'txtDescripcionAtributo',
			width:300,
			fieldLabel:'Descripcion',
			margin:'5 0 10 10',
			labelAlign:'top',
			listeners:{
				change:function(_this, newValue, oldValue, eOpts ){
					var t=window.setTimeout(function(){
						if(Ext.getCmp('txtDescripcionAtributo').getValue()==newValue){
    						if(atributoEditando!="")
		    					modificarAtributosDeFila(atributoEditando,valorEditando);
						}
    				},700);
				}
			}
		});
	var panArbol=Ext.create('Ext.form.Panel',{
		id:'panArbol',
		title:contextTitle,
		name:'panArbol',
		cls:'panArbol',
		layout:'anchor',
		width:pantalla.width/2,
		height:pantalla.height-150,
		
		//height:'100%',
		items:[]
	}); 
	
	var panelIzq=new Ext.panel.Panel({
		id:'panPrincipal',
		name:'panPrincipal',	
		layout:'anchor',
		region:'center',
		title:context_title,
		bbar:['->',btnGuardar],
		items:[panDatos,{xtype:'fieldset',name:'fieldM',title:'Creacion de estructura',height:75,items:[lblMensaje]},grdArbol]
	});
	var panelDer=new Ext.panel.Panel({
		id:'panelDer',
		name:'panelDer',
		cls:'panelDer',
		layout:'anchor',
		region:'east',
		title:'Formulario',
		width:350,
		collapsible: false,
        autoScroll: true,
        disabled:true,
		items:[proAtributos,
			{xtype:'fieldset',border:0,cls:'fieldSetExtrasCls',layout:'anchor',id:'fieldSetExtras',disabled:true,items:[
			{xtype:'fieldset',border:0,cls:'fieldSetActivoModificableCls',layout:'hbox',id:'fieldSetActivoModificable',items:[chbActivo,chbModificable]},txtDescripcionAtributo
			]}
			]
	});
	var viewport = new Ext.Viewport({
	    layout: 'border',
	    items: [panelIzq]});
	/* 
	 * 
	 * FUNCIONES
	 * 
	 */
	//grdArbol
	    Ext.EventManager.onWindowResize(function(w, h){
			grdArbol.setHeight(pantalla.height-260);
	});
	function mostrarAtributos(record){
		atributosGuardados=true;
		var a=new Array();
		a=Ext.JSON.decode(record.data.atributos);
		for(var i=0;i<a.length;i++){
			var v="";
			if(a[i].atributo=="Requerido"||a[i].atributo=="Historico")
				v=a[i].valor=="true"?true:false;
			else if(a[i].atributo=="Vencimiento")
				v=Ext.Date.parse(a[i].valor, 'd/m/Y')
			else
				v=a[i].valor;
				
			proAtributos.setProperty( a[i].atributo, v, false );
		}
		
		atributosGuardados=false;
		panelDer.setDisabled(false);
		
	}
	function modificarAtributosDeFila(atributo,valor){
		var m = grdArbol.getSelectionModel().getSelection();
		var a=new Array();
		var aF=new Array();
		if(m!=null){
			a=Ext.JSON.decode(m[0].data.atributos);
			for(var i=0;i<a.length;i++){
				if(a[i].atributo==atributo){
					var atributos=Ext.create('atributoDocumentoModel',{
					atributo:atributo,
					activo:chbActivo.getValue(),
					valor:valor,
					modificable:chbModificable.getValue(),
					descripcion:txtDescripcionAtributo.getValue()
				});
					atributos=Ext.JSON.encode(atributos.data);
					aF.push(atributos);
				}
				else{
					var atributos=Ext.create('atributoDocumentoModel',{
					atributo:a[i].atributo,
					activo:a[i].activo,
					valor:a[i].valor,
					modificable:a[i].modificable,
					descripcion:a[i].descripcion
					});
						atributos=Ext.JSON.encode(atributos.data);
						aF.push(atributos);
				}
			}
			//alert(aF.toString());
			var root = ArbolStore.getRootNode().findChild('ide',m[0].data.ide,true);
			root.set('atributos','['+aF.toString()+']');
			//m[0].data.atributos=aF.toString();
		}
		
		//a=Ext.JSON.decode(m[0].data.atributos);
			//alert('t');
		/*if(!atributosGuardados){
		    			alert(atributosGuardados);
		    			alert(recordId+" : "+value); //Detonar modificacion de store de arbol
		    		}*/
	}
	function addC(record,item,index){
		if(record.data.tipo!='documento'){
		var root = ArbolStore.getRootNode().findChild('ide',record.data.ide,true);
		 root.set('leaf',false);
		var hijo=({texto: 'Carpeta '+contador.toString(),leaf:true,ide:contador,iconCls:'task-folder',expanded: true ,cls:'carpeta' , tipo:'carpeta'});
		var hi=root.appendChild(hijo);
		hi.set('id',contador);
		hi.set('leaf',false);
		contador++;
		
		}
		else{
			Ext.example.msg("Error","No se puede agregar mas elementos dentro de un documento.");
		}
	}
	function addD(record,item,index){
		if(record.data.tipo!='documento'){
			//Crear Modelo de configuracion y agregar datos.
			var at=new Array();
			for(var i=0;i<atributosStore.getCount();i++){
					var atributos=Ext.create('atributoDocumentoModel',{
					atributo:atributosStore.getAt(i).get('atributo'),
					activo:atributosStore.getAt(i).get('activo'),
					valor:atributosStore.getAt(i).get('valor'),
					modificable:atributosStore.getAt(i).get('modificable'),
					descripcion:atributosStore.getAt(i).get('descripcion')
				});
				at.push(atributos.data);
			}
		at=Ext.JSON.encode(at);
		var root = ArbolStore.getRootNode().findChild('ide',record.data.ide,true);
		 root.set('leaf',false);
		var hijo=({texto: 'Documento '+contador.toString(), iconCls:'documento',cls:'documentoCls' ,ide:contador, tipo:'documento',leaf: true, atributos:at});
		var hi=root.appendChild(hijo);
		hi.set('id',contador);
		contador++;
		}
		else{
			Ext.example.msg("Error","No puedes crear nada dentro de un documeto");
		}

	}
	function del(record,item,index){
		if(record.data.ide!='0'){
		var root = ArbolStore.getRootNode().findChild('ide',record.data.parentId,true);
		 root.removeChild(root.findChild('ide',record.data.ide,true));
		}
	}

	function context(pos,record, item, index){
	var contextMenu = new Ext.menu.Menu({
		  items: [
		   {text: 'Agregar carpeta',iconCls: 'addC',handler:function(){addC(record,item,index);}},
		   {text: 'Agregar documento',iconCls: 'addD',handler:function(){addD(record,item,index);}},
		   {text: 'Eliminar elemento',iconCls: 'del',handler:function(){del(record,item,index);}}
		          
		          
	       ]
		});
	contextMenu.showAt(pos);
	}
	var successAjaxFnN = function(response, request) {
		Ext.getBody().unmask();
	    var jsonData = Ext.JSON.decode(response.responseText);
	    if (true == jsonData.success) {
	        Ext.Msg.show({
	            title: 'Correcto',
	            msg: 'Actualizado correctamente',
	            buttons: Ext.Msg.OK,
	            icon: Ext.MessageBox.INFO
	        });	  
	        actualizar=true;
//		    estructura=txtNombre.getValue();
//		    actionGetEstructura='getEstructuraArbol';
//			EstructuraStore.load({params: {select: estructura}});
//			ArbolStore.load({params: {action:actionGetEstructura,select: estructura}});	
//			txtNombre.disable();
//			btnGuardar.disable();
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
	if(!Array.indexOf){
	    Array.prototype.indexOf = function(obj){
	        for(var i=0; i<this.length; i++){
	            if(this[i]==obj){
	                return i;
	            }
	        }
	        return -1;
	    };
	};
	
	function verifica(linea){
		var delR=ArbolStore.getRemovedRecords();
		var result=delR.indexOf(linea);
		return result;
	}
	function verificaParent(ide,eli){	
		var result=eli.indexOf(ide);
		return result;
	}
	/*Array.prototype.unique=function(a){
		  return function(){return this.filter(a)}}(function(a,b,c){return c.indexOf(a,b+1)<0
		});*/
	Array.prototype.unique = function(){
	    var r, o, i, j, t, tt;
	    r = [];
	    o = {};
	    for(i = 0; i < this.length; i++){
	       t = this[i];
	       tt = o[t] = o[t] || [];
	       for(j = 0; j < tt.length; j++)
	           if(tt[j] === this[i])
	               break;
	       if(j == tt.length)
	           r.push(tt[j] = t);
	     }
	     return r;
	};
		  function setMax(ide){
			  if(ArbolStore.getRootNode().childNodes.length>0){
			  var root = ArbolStore.getRootNode().findChild('ide',ide,true);
			  for(var i=0;i<root.childNodes.length;i++){
				  var nodo=ArbolStore.getRootNode().findChild('ide',root.childNodes[i].data.ide,true);
				  if(nodo.childNodes.length>0){
					  if(nodo.data.ide>contador){
						  contador=nodo.data.ide;
					  }
					  setMax(nodo.data.ide);
				  }
				  else{
					  if(nodo.data.ide>contador){
						  contador=nodo.data.ide;
					  }
				  }}
			  }
		  }
		  function setDirtyAll(id){
			  var root = ArbolStore.getRootNode().findChild('id',id,true);
			  for(var i=0;i<root.childNodes.length;i++){
				  var nodo=ArbolStore.getRootNode().findChild('id',root.childNodes[i].data.id,true);
				  if(nodo.childNodes.length>0){
					  setDirtyAll(nodo.data.id);
				  }
				  else{
					  nodo.setDirty();
				  }
			  }
  	
			  root.setDirty();
		  }
	function Guardar(){
		if(actualizar==false){
			var delR=ArbolStore.getRemovedRecords();
			var arre=new Array();
			for(var i=0;i<delR.length;i++){
				arre.push(delR[i].data.ide);
			}
			var DatosAct = new Array();
			
			var FilasActualizadas=ArbolStore.getUpdatedRecords();
			FilasActualizadas=FilasActualizadas.unique();
			 for (var i=0; i < FilasActualizadas.length; i++)
	        {  
				 if(verifica(FilasActualizadas[i])<0){
					 if(verificaParent(FilasActualizadas[i].data.parentId,arre)<0){
					var est = Ext.create('exportModelArbol', {
		 			    Texto : FilasActualizadas[i].data.texto,
		 			    Tipo:FilasActualizadas[i].data.tipo,
		 			    Ide:FilasActualizadas[i].data.ide,
		 			    Parent:FilasActualizadas[i].data.parentId,
		 			    Profundidad:FilasActualizadas[i].data.depth,
		 			    atributos:FilasActualizadas[i].data.atributos
		 			});	
					 DatosAct.push(est.data);
	        }}
	        }
			 DatosAct=Ext.JSON.encode(DatosAct);

			var exportM = Ext.create('exportModel', {
 			    Nombre : txtNombre.getValue(),
 			    Descripcion  : txtDescripcion.getValue(),
 			    Definicion:DatosAct
 			});	
			exportM=Ext.JSON.encode(exportM.data);
			Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: successAjaxFnN,   
					timeout: 30000,  
					params: {  
					action: 'creaEstructura',//Eliminar  				
					estruc:exportM
			}});
		}
		else{
			if(ArbolStore.getRemovedRecords().length>0||ArbolStore.getUpdatedRecords().length>0||ArbolStore.getNewRecords().length>0){				
			var delR=ArbolStore.getRemovedRecords();
			var arre=new Array();
			for(var i=0;i<delR.length;i++){
				arre.push(delR[i].data.ide);
			}
			var DatosAct = new Array();	
			setDirtyAll(0);
			var FilasActualizadas=ArbolStore.getUpdatedRecords();
			FilasActualizadas=FilasActualizadas.unique();
			 for (var i=0; i < FilasActualizadas.length; i++)
	        {  
				 if(verifica(FilasActualizadas[i])<0){
					 if(verificaParent(FilasActualizadas[i].data.parentId,arre)<0){
					var est = Ext.create('exportModelArbol', {
		 			    Texto : FilasActualizadas[i].data.texto,
		 			    Tipo:FilasActualizadas[i].data.tipo,
		 			    Ide:FilasActualizadas[i].data.ide,
		 			    Parent:FilasActualizadas[i].data.parentId,
		 			    Profundidad:FilasActualizadas[i].data.depth,
		 			    atributos:FilasActualizadas[i].data.atributos
		 			});	
					 DatosAct.push(est.data);
	        }}
	        }
			 var FilasNuevas=ArbolStore.getNewRecords();
			 FilasNuevas=FilasNuevas.unique();
			 for (var i=0; i < FilasNuevas.length; i++)
	        {  
				 if(verifica(FilasNuevas[i])<0){
					 if(verificaParent(FilasNuevas[i].data.parentId,arre)<0){
					var est = Ext.create('exportModelArbol', {
		 			    Texto : FilasNuevas[i].data.texto,
		 			    Tipo:FilasNuevas[i].data.tipo,
		 			    Ide:FilasNuevas[i].data.ide,
		 			    Parent:FilasNuevas[i].data.parentId,
		 			    Profundidad:FilasNuevas[i].data.depth
		 			});	
					 DatosAct.push(est.data);
	        }}
	        }
			 DatosAct=Ext.JSON.encode(DatosAct);
			var exportM = Ext.create('exportModel', {
 			    Nombre : txtNombre.getValue(),
 			    Descripcion  : txtDescripcion.getValue(),
 			    Definicion:DatosAct
 			});	
			exportM=Ext.JSON.encode(exportM.data);
			Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: successAjaxFnN,   
					timeout: 30000,  
					params: {  
					action: 'actualizaEstructura',  				
					estruc:exportM,
					select:txtNombre.getValue()
			}});
		}
			else{
				
				Ext.getBody().unmask();
				Ext.example.msg("Error","No se detecto ninguna modificacion");
			}
	}
		
	}
	inicia();
	function inicia(){
		if(actualizar==true){
			if(estructura!=null){
				actionGetEstructura='getEstructuraArbol';
				EstructuraStore.load({params: {select: estructura}});
				ArbolStore.load({params: {action:actionGetEstructura,select: estructura}});	
				txtNombre.disable();
				
			}
		}
		else{
			var root = ArbolStore.getRootNode();
			var hijo=({texto: '/ ',leaf:true,ide:'0',expandable:false,iconCls:'carpetaIcon',expanded: true ,cls:'carpeta' , tipo:'carpeta'});
			var hi=root.appendChild(hijo);
			hi.set('id','0');
		}
	}
});//Termina Onready