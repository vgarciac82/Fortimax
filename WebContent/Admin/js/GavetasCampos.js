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

var context_title= "Detalles de la Gaveta";
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
     {name: 'orden', type: 'string'},
     {name: 'nombre', type: 'string'},
     {name: 'etiqueta', type: 'string'},
     {name: 'longitud', type: 'int'},
     {name: 'valor', type: 'string'},
     {name: 'tipo'},
     {name: 'indice'},
     {name: 'requerido', type: 'bool'},
     {name: 'editable', type: 'bool'},
     {name: 'lista'}
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
             property: 'orden',
             direction:'ASC'
         }],
         listeners: {
             load: function(store, records, successful) {
            	 gavetaInfo = store.proxy.reader.jsonData; 
            	 nombre_gav.setValue(gavetaInfo.Nombre_gav);
            	 descripcion_gav.setValue(gavetaInfo.Descripcion_gav);
             }
         }
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
	 
		if(action=='getgaveta' )
		{
			context_op = 'Actualizar';
			Ext.Ajax.request({  
				url: base_path,  
				method: 'POST',  
				 success: function(response, resquest)
				 { 
					 //var jsonData = Ext.JSON.decode(response.responseText);
					 //Campos_gav=jsonData.Campos;
					 //alert(jsonData.Nombre_gav);
					 
					 //nombre_gav:nombre_gav.setValue(jsonData.Nombre_gav);
					 //descripcion_gav:descripcion_gav.setValue(jsonData.Descripcion_gav);
 
					 store.load(function(records, operation, success) {
				         lbltotalrows.setText('Total de Campos: ' + grid.store.getCount());
						 });
				 },  
				 failure: failureAjaxFn,  
				timeout: 30000,  
				params: {
		        	action: action,
		            select: select
				 }});
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
       		 id: 'orden',
       		 header: 'Orden',
             dataIndex: 'orden',
             width: 50,
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
            id: 'nombre',
            header: 'Nombre',
            dataIndex: 'nombre',
            width: 100,
            flex: 1,
            
            editor: {
	        	allowBlank: false,
	            readOnly: context_op == 'Actualizar',
	        	invalidText:"EL valor no puede ser vacio.",
	        	regex:new RegExp("^[A-Za-z]{1,}\\w+"),
			  	regexText :'El campo debe ser alfanumerico.',
			  	listeners:{
				  		change:function(_this,e){
				  			_this.setValue(_this.getValue().toUpperCase());
				  		}
			  		}
            		}
          }, 
          {
              id: 'etiqueta',
              header: 'Etiqueta',
              dataIndex: 'etiqueta',
              width: 100,
              flex: 1,
              field: {allowBlank: false},
			  regex:new RegExp("^[A-Za-z]{1,}\\w+"),
			  regexText :'El campo debe ser alfanumerico.'
            },
            {
                header: 'Longitud',
                dataIndex: 'longitud',
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
                id: 'valor',
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
        title: 'Detalles de la gaveta',
        frame: true,
        tbar: [{
            itemId: 'Agregar-campo',
            text: 'Agregar Campo',
            iconCls: 'add',
            handler : function(){
            	if(store.getCount()==0){
            		Ext.Msg.show({
                        title: 'Atención',
                        msg: 'Asegúrese de que el primer campo sea<br>el <b>ID</b> (índice único).',
                        buttons: Ext.Msg.OK,
                        animEl: 'elId',
                        icon: Ext.MessageBox.WARNING
                    });            		
            	}
                // Create a record instance through the ModelManager
                var r = Ext.ModelManager.create({
                	orden: store.getCount()+1,
                    nombre: 'CAMPO'+ (store.getCount()+1),
                    etiqueta: 'Nuevo Campo '+ (store.getCount()+1),
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
            disabled: false,
            
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
        forceFit:true,
        viewConfig: {
            getRowClass: function(record, index) {
                if (record.data.nombre == 'ID_GABINETE') {
                    return 'display-false';
                } 
            }
        }
    });
    
    //Maneja la respuesta del servidor
    var successAjaxFn = function(response, request) {

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
	        	
	        	//Creacion de Gaveta
	        	var store_json = getJsonOfStore(store);
	        	
				Ext.Ajax.request({  
					url: base_path,  
					method: 'POST',  
					 success: successAjaxFn,  
					 failure: failureAjaxFn,  
					timeout: 30000,  
					params: {  
					action: 'create',
					nombre_gav:nombre_gav.getValue(),
					descripcion_gav:descripcion_gav.getValue(),
					gavetamodel: store_json  
					 }  
				});  
	
	        	
	            Ext.MessageBox.show({
	                title: 'Gaveta creada',
	                msg: 'Operacion realizada',
	                buttons: Ext.Msg.OK,
	                icon: Ext.Msg.INFO,
	                width:300,
	                closable:false
	            });
	        }     	
	      	
    }
	        
    //Modificacion de Gaveta
    function ActualizarGaveta(btn){
    	        if(btn=='yes'){
    	        	var updated_records = store.getUpdatedRecords();  
    	        	var deleted_records = store.getRemovedRecords();
    	        	
    				if (updated_records.length > 0 ||
    					deleted_records.length||
    					descripcion_gav.getValue()!=gavetaInfo.Descripcion_gav) {   
    					var changes = new Array();
    					var deletes = new Array();
    					
    					for (var i=0; i < updated_records.length; i++)
    					{  
    						changes.push(updated_records[i].data);  
    					}
    					
    					for (var i=0; i < deleted_records.length; i++)
    					{  
    						deletes.push(deleted_records[i].data);  
    					}
    					
    					changes = Ext.JSON.encode(changes);
    					deletes = Ext.JSON.encode(deletes);
    				
//    	        	alert("Modificados: "+changes);
//    	        	alert("Eliminados: "+deletes);
    	        	
    				Ext.Ajax.request({  
    					url: base_path,  
    					method: 'POST',  
    					 success: successAjaxFn,  
    					 failure: failureAjaxFn,  
    					timeout: 30000,  
    					params: {  
    					action: 'editgaveta',  
    					nombre_gav:nombre_gav.getValue(),
    					descripcion_gav:descripcion_gav.getValue(),
    					gavetamodelchanges: changes,
    					gavetamodeldeletes: deletes
    					 }});
    				
    			
    				}
    	            Ext.MessageBox.show({
    	                title: 'Creacion de gaveta',
    	                msg: 'No se realizara la creacion.',
    	                buttons: Ext.Msg.OK,
    	                icon: Ext.Msg.WARNING,
    	                width:300,
    	                closable:false
    	            });
    	           
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
     
     var nombre_gav = Ext.create('Ext.form.field.Text',{
	    	xtype:'textfield',
	    	name: 'nombre_gav',
            fieldLabel: 'Nombre',
            cls:'nombre_gav',
            emptyText: 'Nombre real de la Gaveta.',
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
        	emptyText: 'Nombre a desplegar en Fortimax',
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
	         		Ext.MessageBox.confirm('Creacion de nueva Gaveta', 
		            'Esta a punto de crear la Gaveta: "'+nombre_gav.getValue()+'", Desea continuar?', CrearGaveta);
	         	}
	         	else if(context_op=='Actualizar')
	         	{
	         		Ext.MessageBox.confirm('Actualizar los detalles  gaveta', 
	    		            'Esta a punto de actualizar  la Gaveta: "'+nombre_gav.getValue()+'", Desea continuar?', ActualizarGaveta);
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
					            	nombre_gav, descripcion_gav ,btn_guardar_cambios
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

