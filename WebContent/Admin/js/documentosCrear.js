/* Autor: Angel Ramirez */

Ext.Loader.setConfig({
    enabled: true
});


Ext.require([
    'Ext.selection.CellModel',
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.util.*',
    'Ext.state.*',
    'Ext.form.*',
    'Ext.JSON.*',
    'Ext.ux.CheckColumn'
]);

var context_title= "Detalles de la Plantilla";
var context_op = 'Crear';
var Campos_gav='{}';




Ext.onReady(function(){
	

    Ext.define('Gaveta', {
        extend: 'Ext.data.Model',
        fields: 
    [
     {name: 'Nombre_gav', type: 'string'},
     {name: 'Descripcion_gav', type: 'string'}
    ],
    hasMany: { model: 'Campos', name: 'Campos' },
    idProperty:'Nombre_gav'
    });
    
    Ext.define('Campos', {
        extend: 'Ext.data.Model',
        fields: 
    [
     {name: 'nombre', type: 'string'},
     {name: 'etiqueta', type: 'string'},
     {name: 'orden', type: 'int'},
     {name: 'longitud', type: 'int'},
     {name: 'valor', type: 'string'},
     {name: 'tipo'},
     {name: 'indice'},
     {name: 'requerido', type: 'bool'},
     {name: 'editable', type: 'bool'},
     {name: 'lista'},
     {name:'NombreD', type:'String'},
     {name:'DescripcionD',type:'String'},
     {name:'Id',type:'int'}
    ],
    idProperty:'nombre'
   	//belongsTo: 'Gaveta' 
 
    });
    
    Ext.define('Ext.data.reader.JsonPWithoutRoot', {
        extend: 'Ext.data.reader.Json',
        read: function(response) {
            return this.callParent([ { root: response } ]);
        },
        root: 'root'
    });
    
    var gavetaInfo;
    
    // create the Data Store
     var store = Ext.create('Ext.data.Store', {
         // destroy the store if the grid is destroyed
         autoDestroy: true,
         model: 'Campos',
         proxy: {
             type: 'ajax',
             url: base_path,
             method: "POST",
             reader: 
            {
                 type: 'json',
                 root: 'Campos'
             },
             extraParams: 
            {
                 action: action,
                 select: select
             }
         }, 
        sorters: [{
             property: 'Orden',
             direction:'ASC'
         }],
         listeners: {
             load: function(stor, records, successful) {
            	 if(store.getCount()>0){
            		 txtId.setValue(store.getAt(0).get('Id'));
            		 nombre_gav.setValue(store.getAt(0).get('NombreD'));
                	 descripcion_gav.setValue(store.getAt(0).get('DescripcionD'));
            	 }
            	 Ext.getBody().unmask();
               
             }
         },
         autoLoad:false
     });
     
     Ext.define('ComboBox', {
         extend: 'Ext.data.Model',
         fields: [
             {name:'value', type:'String'},
             {name:'text', type:'String'}
         ]
     });

     var storeIndices = Ext.create('Ext.data.Store', {
     	model: 'ComboBox',
      	proxy: {
              type: 'ajax',
              url: base_path,
              method: "POST",
              reader: {
                  type: 'json',
                  root: 'indices'
              },
              extraParams: {
                  action: 'getIndicesListJson'
              }
          },
      	autoLoad: true
      });
     
     var storeTipos = Ext.create('Ext.data.Store', {
     	model: 'ComboBox',
      	proxy: {
              type: 'ajax',
              url: base_path,
              method: "POST",
              reader: {
                  type: 'json',
                  root: 'tipos'
              },
              extraParams: {
                  action: 'getTiposListJson'
              }
          },
      	autoLoad: true
      });
     
     var storeCatalogos = Ext.create('Ext.data.Store', {
    	model: 'ComboBox',
     	proxy: {
             type: 'ajax',
             url: base_path,
             method: "POST",
             reader: {
                 type: 'json',
                 root: 'catalogos'
             },
             extraParams: {
                 action: 'getCatalogosListJson'
             }
         },
     	autoLoad: true
     });
	 
		if(action=='getDocumento' )
		{
			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			context_op = 'Actualizar';
			store.load(function(records, operation, success) {
		         lbltotalrows.setText('Total de Campos: ' + grid.store.getCount());
				 });
//			Ext.Ajax.request({  
//				url: base_path,  
//				method: 'POST',  
//				 success: function(response, resquest)
//				 { 
//
//					 store.load(function(records, operation, success) {
//				         lbltotalrows.setText('Total de Campos: ' + grid.store.getCount());
//						 });
//				 },  
//				 failure: failureAjaxFn,  
//				timeout: 30000,  
//				params: {
//		        	action: action,
//		            select: select
//				 }});
		}

	    
  

    var lbltotalrows = Ext.create('Ext.form.Label',	{
        xtype: 'label'
    });
    
    var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
    	clicksToEdit: 2
    });
  
    lbltotalrows.setText('Total de Campos: 0');
    var grid = Ext.create('Ext.grid.Panel', {
    	store: store,
    	
        columns: [    
          {
            id: 'Nombre',
            header: 'Nombre',
            dataIndex: 'nombre',
            width: 100,
            flex: 1,
            
            editor: {
	        	allowBlank: false,
//	            readOnly: context_op == 'Actualizar',
	        	invalidText:"EL valor no puede ser vacio.",
	        	regex:new RegExp("^[A-Za-z]{1,}\\w+"),
			  	regexText :'El campo debe ser alfanumerico.'
            		}
          }, 
          {
              id: 'Etiqueta',
              header: 'Etiqueta',
              dataIndex: 'etiqueta',
              width: 100,
              flex: 1,
              field: {allowBlank: false},
			  regex:new RegExp("^[A-Za-z]{1,}\\w+"),
			  regexText :'El campo debe ser alfanumerico.'
            },
            {
                header: 'Orden',
                dataIndex: 'orden',
                width: 70,
                align: 'left',
                field: {
                    xtype: 'numberfield',
                    allowBlank: false,
                    value: 10,
                    minValue: 1,
                    maxValue: 1000
                }
            },
            {
                header: 'Tamano',
                dataIndex: 'tamano',
                width: 70,
                align: 'left',
                field: {
                    xtype: 'numberfield',
                    allowBlank: false,
                    value: 10,
                    minValue: 1,
                    maxValue: 1000
                }
            },
            {
                id: 'Valor',
                header: 'Valor',
                dataIndex: 'valor',
                width: 100,
                flex: 1,
                field: {allowBlank: true}
             },
	          {
	            header: 'Tipo',
	            dataIndex: 'tipo',
	            width: 90,
	            field: {
	                xtype: 'combobox',
	                typeAhead: false,
	                triggerAction: 'all',
	                selectOnTab: true,
	                store: storeTipos,
	                displayField: 'text',
	                valueField: 'value',
	                lazyRender: false,
	                listClass: 'x-combo-list-small'
	            }
	        },
          {
            header: 'Indice',
            dataIndex: 'indice',
            width: 90,
            field: {
                xtype: 'combobox',
                typeAhead: true,
                triggerAction: 'all',
                selectOnTab: true,
                store: storeIndices,
                displayField: 'text',
                valueField: 'value',
                lazyRender: false,
                listClass: 'x-combo-list-small'
            }
        },
        {
            xtype: 'checkcolumn',
            header: 'Requerido',
            dataIndex: 'requerido',
            width: 60
        },       
        {
            xtype: 'checkcolumn',
            header: 'Editable',
            dataIndex: 'editable',
            width: 55
        },
        {
            header: 'Lista',
            dataIndex: 'lista',
            width: 150,
            field: {
                xtype: 'combobox',
                typeAhead: true,
                triggerAction: 'all',
                selectOnTab: true,
                store: storeCatalogos,
                displayField: 'text',
                valueField: 'value',
                lazyRender: false,
                listClass: 'x-combo-list-small'
            }
        }],
//        selModel: {
//            selType: 'cellmodel'
//        },
        renderTo: 'editor-grid',
        width: '100%',
        height: 400,
        title: 'Detalles de la plantilla',
        frame: true,
        tbar: [{
            itemId: 'Agregar-campo',
            text: 'Agregar Campo',
            iconCls: 'add',
            handler : function(){
                // Create a record instance through the ModelManager
                var r = Ext.ModelManager.create({
                    nombre: 'Campo'+ (store.getCount()+1),
                    etiqueta: 'Nuevo Campo '+ (store.getCount()+1),
                    orden:store.getCount()+1,
                    longitud: 10,
                    valor: '',
                    tipo: 'Texto (VarChar)',
                    indice: 'Ninguno',
                    requerido: false,
                    editable: true,
                    lista: '-Ninguna-'
                }, 'Campos');
                r.setDirty(); //Indicamos que los datos son Nuevos y estan sucios (modificados) con los triangulos rojos.
                store.insert(store.getCount(), r);
//              store.insert(0, r);
                if(store.getCount()-1>0){
                cellEditing.startEditByPosition({row: store.getCount()-1, column: 0});
                }
                lbltotalrows.setText('Total de Campos: ' + store.getCount());

            }
        },
        {
            itemId: 'Eliminar-campo',
            text: 'Eliminar campo',
            iconCls: 'delete',
            handler: function() {
                var sm = grid.getSelectionModel();
                //alert(sm +' '+ sm.getSelection());
                //cellEditing.cancelEdit();
                //rowEditing.cancelEdit();
                store.remove(sm.getSelection());
                lbltotalrows.setText('Total de Campos: ' + store.getCount());
                if (store.getCount() > 0) {
                    sm.select(store.getCount()-1);
                }
            },
            disabled: false
            
        },
        {
            itemId: 'Eliminar-todo',
            text: 'Eliminar todo',
            iconCls: 'deleteall',
            handler: function() {
            	store.removeAll();
            	lbltotalrows.setText('Total de Campos: ' + store.getCount());
            },
            disabled: false
        },
        '->',
        {
        	xtype: 'button',
            text: 'Importar',
            itemId: 'Importar',
            iconCls: 'import',
            disabled: true,
            handler : function(){
            	Ext.MessageBox.alert('Importar Gaveta', 
                	   	'No implementado aun.');
            }
        },
        {
            itemId: 'Exportar',
            iconCls: 'export',
        	xtype: 'button',
            text: 'Exportar',
            handler : function(){
            	if(validateInfoBeforeSend())
            	{
	 			    var store_json = getJsonOfStore(store);
					Ext.Ajax.request({  
						url: base_path,  
						method: 'POST',   
						timeout: 30000,
						success: DownloadFile,  
						failure: failureAjaxFn, 
						params: {  
						action: 'export',
						download: 'false',
						nombre_gav:nombre_gav.getValue(),
						gavetamodel: store_json 
					}});
            	}

            }
        }
        ],
        plugins: [cellEditing],
        listeners: {
            'selectionchange': function(view, records) {
                grid.down('#Eliminar-campo').setDisabled(!records.length);
                grid.down('#Eliminar-todo').setDisabled(!records.length);
            }
        },
        bbar:[lbltotalrows],
        forceFit:true
    });
    
    //Maneja la respuesta del servidor
    var successAjaxFn = function(response, request) {
    	Ext.getBody().unmask();
        var jsonData = Ext.JSON.decode(response.responseText);

        if (true == jsonData.success) {
            Ext.Msg.show({
                title: 'Operacion completada con exito!',
                msg: jsonData.message,
                buttons: Ext.Msg.OK,
                animEl: 'elId',
                icon: Ext.MessageBox.INFO
            });

			//Quita los triangulos rojos indicando que no hay cambios pendientes.
			Ext.each(store.getUpdatedRecords(), function(rec) {
				  rec.commit();
				}); //Solo si todo fue bien los desmarca.

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
    
	//Maneja los errores si falla la peticion ajax
    var failureAjaxFn = function(response, request) {
        Ext.Msg.show({
            title: 'Error',
            msg: 'Cambios no guardados. Status:' + response.status,
            buttons: Ext.Msg.OK,
            animEl: 'elId',
            icon: Ext.MessageBox.ERROR
        });
    };
    
    function CrearGaveta(btn){
    	        if(btn=='yes'){
    	        Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	        	//Creacion de Gaveta
	        	var store_json = getJsonOfStore(store);
	        	
				Ext.Ajax.request({  
					url: base_path,  
					method: 'POST',  
					 success: successAjaxFn,  
					 failure: failureAjaxFn,  
					timeout: 30000,  
					params: {  
					action: 'createDoc',
					nombre_doc:nombre_gav.getValue(),
					descripcion_doc:descripcion_gav.getValue(),
					docmodel: store_json  
					 }  
				});  
	
	        }     	
	      	
    }
	        
    //Modificacion de Gaveta
    function ActualizarGaveta(btn){
    	        if(btn=='yes'){	   
    	        	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
    	        	var store_json = getJsonOfStore(store);
    				Ext.Ajax.request({  
    					url: base_path,  
    					method: 'POST',  
    					 success: successAjaxFn,  
    					 failure: failureAjaxFn,  
    					timeout: 30000,  
    					params: {  
    					action: 'editDoc',  
    					nombre_doc:nombre_gav.getValue(),
    					descripcion_doc:descripcion_gav.getValue(),
    					docmodel: store_json  
    					 }});
	
	        }

    }
    
    //Convierte un store a JSON
     function getJsonOfStore(store){ 
            var datar = new Array(); 
            var jsonDataEncode = ""; 
            var records = store.getRange(); 
            for (var i = 0; i < records.length; i++) { 
                datar.push(records[i].data); 
            } 
            jsonDataEncode = Ext.JSON.encode(datar); 
     
            return jsonDataEncode; 
        }
     var txtId = Ext.create('Ext.form.field.Text',{
	    	xtype:'textfield',
	    	name: 'txtId',
         width: 200,
         hidden:true
     });
     var nombre_gav = Ext.create('Ext.form.field.Text',{
	    	xtype:'textfield',
	    	name: 'nombre_gav',
            fieldLabel: 'Nombre',
            cls:'nombre_gav',
            emptyText: 'Nombre de la plantilla',
            width: 200,
            readOnly: context_op == 'Actualizar',
            labelAlign: 'top',
            listeners:{
                change: function(field, newValue, oldValue){
                    field.setValue(newValue.toUpperCase());
                }
             },
        	regex:new RegExp("^[A-Za-z]{1,}\\w+"),
    		regexText :'El "Nombre" debe ser alfanumerico y no pueden empezar con un digito.'
        });
     
     var descripcion_gav = Ext.create('Ext.form.field.Text',{
        	xtype:'textfield',
            name: 'descripcion_gav',
        	fieldLabel: 'Descripcion',
        	emptyText: 'Descripcion de la plantilla',
        	width: 400,
        	labelAlign: 'top',
        	regex:new RegExp("^\\w{2,}"),
    		regexText :'La "Descripcion" debe ser alfanumerica.'
        	
        });
     
     var btn_guardar_cambios = Ext.create('Ext.Button', {
         text: context_op,
         scale: 'large',
         iconCls: 'save',
         iconAlign: 'top',
         width:60,
         margin: {top: 0, right: 5, bottom: 5, left: 5},
         //renderTo: Ext.getBody(),
         handler: function() {
        	 
         	if( validateInfoBeforeSend())
        	{
         		//alert(context_op);
	         	if(context_op=='Crear')
	         	{
	         		Ext.MessageBox.confirm('Creacion de nueva plantilla', 
		            'Esta a punto de crear la plantilla: "'+nombre_gav.getValue()+'", &iquest;Desea continuar?', CrearGaveta);
	         	}
	         	else if(context_op=='Actualizar')
	         	{
	         		Ext.MessageBox.confirm('Actualizar los detalles  de plantilla', 
	    		            'Esta a punto de actualizar la plantilla: "'+nombre_gav.getValue()+'", &iquest;Desea continuar?', ActualizarGaveta);
	         	}
        	}	
         }
         	
     });
     
     var group_det_gav = Ext.create('Ext.form.FieldSet',{
    	    xtype: 'fieldset',
    	    title: context_title,
    	    //collapsible: true,
    	    width:'93%',
    	    defaults: {
    	        labelWidth: 89,
    	        margin: {top: 10, right: 10, bottom: 10, left: 10},
    	        layout: {
    	            defaultMargins: {top: 10, right: 10, bottom: 10, left: 10},
     				type: 'fit'
    	        }

    	    },
    	    items: [
					{
					    xtype: 'fieldset',
					    //fieldLabel: 'Name',
					    layout: {
					        type: 'hbox'
					        //align: 'left'
					    },
					    width:grid.getWidth(),
					    combineErrors: true,
					    style: 'border: none;',
					    autoScroll:true,
					    defaultType: 'textfield',
					    defaults: {
					    margin: {top: 0, right: 3, bottom: 0, left: 0},
					    allowBlank: false,
					    blankText:'El valor no puede estar vacio.',
		            	minLength:2,
		            	minLengthText:'El "Nombre" y la "Descripcion" debe ser mayor a 2 caracteres.'
					 },
					    items: [
					            	txtId,nombre_gav, descripcion_gav ,btn_guardar_cambios
					            ]},
					            
            grid]});
     
    	//Genera una ventana que contiene al grid
    	Ext.create('Ext.panel.Panel', {
    	    title: context_title,
    	    frame:true,
    	    //height: 300,
    	    //width: 600,
    	    width:'100%',
//    	    margin: {top: 0, right: 0, bottom: 10, left: 0},
    	    layout: 
    	    {
//    			defaultMargins: {top: 0, right:0, bottom: 0, left: 0},
    		    type: 'hbox'
    	    },
    	    html: '<p>Lista de '+context_title+' en Fortimax.</p>',
    	    items: [group_det_gav],
    	    renderTo: Ext.getBody()
    	});
    	
    	function validateInfoBeforeSend(){

    		
    		var success = false;
    		
         	if(store.getCount()!=0 && nombre_gav.getValue()!='' && descripcion_gav.getValue()!='' )
        	{ 
         		//Valida que la tabla tengfa registros, y que los campos Nombre y Descripcion esten llenos.
         		//Otras validaciones aqui        		
         		//TODO: Agregar vlidacion para evitar que se repitan datos en la tabla
         		success = true;
                
        	}
         	else
         	{
                Ext.MessageBox.show({
                    title: 'Verifique la Informacion',
                    msg: 'Debe de llenar los campos "Nombre", "Descripcion" y al menos una fila de la tabla. ',
                    buttons: Ext.Msg.OK,
                    icon: Ext.Msg.WARNING,
                    width:300,
                    closable:false
                });
         	}
    		
    		return success;
    	}
    	
    	 var DownloadFile = function(response, request) {
			   
			   var jsonData = Ext.JSON.decode(response.responseText);
			   //alert (jsonData.file);
			   var body = Ext.getBody();                
			   // create a hiddle frame               
			   body.createChild({                   
			   tag: 'iframe',                   
			   cls: 'x-hidden',                   
			   id: 'iframe',                   
			   name: 'iframe'               
			   });                
			   //create hidden form with download as action                
			   var form = body.createChild({                   
			   tag: 'form',                  
			   cls: 'x-hidden',                   
			   id: 'form', 
			   method: 'POST',  
			   action: base_path +'?action=export',                   
			   target: 'iframe'   
 				   
			   });                
			   // to set the parameter through a hidden field               
			   form.createChild({                   
			   tag: 'input',                   
			   type: 'hidden',                   
			   name: 'download', // parameter name                   
			   value: 'true'   // parameter value               
			   });	
			   
			   form.createChild({                   
				   tag: 'input',                   
				   type: 'hidden',                   
				   name: 'file', // parameter name                   
				   value: jsonData.file   // parameter value               
				   });	
			   
			   // submit the form to initiate the downloading               
			   form.dom.submit();            
			   };
			   
     
});

