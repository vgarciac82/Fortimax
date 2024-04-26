/* Autor: Angel Ramirez */

Ext.Loader.setConfig({
    enabled: true
});
//Ext.Loader.setPath('Ext.ux', 'Admin/js/extjs4/ux'); //Esta Ruta no es Relativa pero si indicaba la ruta relativa
												//correcta no me la reconocia
Ext.require([
    'Ext.selection.CellModel',
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.util.*',
    'Ext.state.*',
    'Ext.form.*',
    'Ext.JSON.*',
    'Ext.ux.CheckColumn',
    'Ext.grid.property.Grid',
	'Ext.button.Button',
    'Ext.ux.RowExpander',
    'Ext.tip.*'
]);

var context_title= "Configuraciones";
var context_op = ' Guardar ';
var Campos_gav='{}';




Ext.onReady(function(){

	Ext.MessageBox.msgButtons['ok'].text = "Aceptar";
	Ext.MessageBox.msgButtons['cancel'].text = "Cancelar";
	Ext.MessageBox.msgButtons['yes'].text = "S&#237;";
	Ext.MessageBox.msgButtons['no'].text = "No";

	
	/* 
	 * 
	 * MODELOS Y STORE
	 * 
	 */


    Ext.define('Parametros', {
        extend: 'Ext.data.Model',
        fields: 
    [
     {name: 'ID', type: 'int'},
     {name: 'category', type: 'String'},
     {name: 'name', type: 'string'},
     {name: 'value', type: 'string'},
     {name: 'description', type: 'string'}
    ],
    idProperty:'name'

 
    });
	

    // create the Data Store
     var store = Ext.create('Ext.data.Store', {
         // destroy the store if the grid is destroyed
         autoDestroy: true,
         model: 'Parametros',
         proxy: {
             type: 'ajax',
             url: base_path,//'Admin/Configuracion/develop/VariablesEntorno.json',
             method: "POST",
             reader: 
            {
                 type: 'json',
                 root: 'Parametros'
             },
             extraParams: 
            {
                 action: action,
                 select: select
             }
         }, 
        sorters: [{
             property: 'Nombre',
             direction:'ASC'
         }]
     });
 
			Ext.Ajax.request({  
				url: base_path,  
				method: 'POST',  
				 success: function(response, resquest)
				 { 
 
					 store.load(function(records, operation, success) {
				         lbltotalrows.setText('Total de Parametros: ' + grid.store.getCount());
						 });
				 },  
				 failure: failureAjaxFn,  
				timeout: 30000,  
				params: {
		        	action: action,
		            select: select
				 }});
			
			 /* 
			 * 
			 * FUNCIONES
			 * 
			 */	
			
			function GuardarCambios(){
				//if(store.getUpdatedRecords().length>0 || store.getRemovedRecords().length>0){
					Ext.Msg.show({
             		     title:'Guardar Cambios',
             		     msg: '&iquestGuardar cambios?',
             		     buttons: Ext.Msg.OKCANCEL,
             		     icon: Ext.Msg.QUESTION,
             		     fn: function (btn){
             		    	 if(btn=='ok'){
             		    		//var FilasActualizadas = store.getUpdatedRecords();
             		    		Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
             		    		var FilasActualizadas = store.getRange();
                  		    	var FilasBorradas=store.getRemovedRecords();
                  		    	var ArregloActualizadas = new Array();
                  		    	var ArregloBorrados = new Array();
                  		    	for (var i=0; i < FilasActualizadas.length; i++)
            					{  
                  		    		ArregloActualizadas.push(FilasActualizadas[i].data);  
            					}
                  		    	for (var i=0; i < FilasBorradas.length; i++)
            					{  
                  		    		ArregloBorrados.push(FilasBorradas[i].data);  
            					}
                  		    	ArregloActualizadas=Ext.JSON.encode(ArregloActualizadas);
                  		    	ArregloBorrados=Ext.JSON.encode(ArregloBorrados);
 
                  		    	Ext.Ajax.request({  
        		 					url: base_path,  
        		 					method: 'POST',  
        		 					 success: function(response, request){
        		 						successAjaxFn(response, request);      		 						
        		 					 },  
        		 					 failure: failureAjaxFn,  
        		 					timeout: 30000,  
        		 					params: {  
        		 					action: 'ActualizaVariablesG',
        		 					ActFil:ArregloActualizadas,
        		 					EliFil:ArregloBorrados
        		 					 }});
             		    	 }
             		     }
					});
				//}
			}
			
		
			
			/* 
			 * 
			 * OBJETOS
			 * 
			 */
		
    var lbltotalrows = Ext.create('Ext.form.Label',	{
        xtype: 'label'
    });
    
    var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
    	clicksToEdit: 1
    });
  
    lbltotalrows.setText('Total de Parametros: 0');
    var grid = Ext.create('Ext.grid.Panel', {
    	store: store,
        columns: [
          {
            id: 'ID',
            header: 'ID',
            dataIndex: 'ID',
            flex: 1,

            editor: {
  	          allowBlank: false,
  	          readOnly:true,
  	          invalidText:"EL valor no puede ser vacio."
            }
          },
          {
            id: 'Categoria',
            header: 'Categoria',
            dataIndex: 'category',
            width: 75,
            flex: 3,

            editor: {
	        	allowBlank: false,
	            readOnly:true,
	        	invalidText:"EL valor no puede ser vacio.",
	        	regex:new RegExp("^[A-Za-z]{1,}\\w+"),
			  	regexText :'El campo debe ser alfanumerico.'
            		}
          }, 
          {
              id: 'Nombre',
              header: 'Nombre',
              dataIndex: 'name',
              width: 100,
              flex: 5,

              editor: {
  	        	allowBlank: false,
  	            readOnly:true,
  	        	invalidText:"EL valor no puede ser vacio.",
  	        	regex:new RegExp("^[A-Za-z]{1,}\\w+"),
  			  	regexText :'El campo debe ser alfanumerico.'
              		}
            },
            {
                id: 'Valor',
                header: 'Valor',
                dataIndex: 'value',
                width: 275,
                flex: 10,

                editor: {
    	        	allowBlank: false,
    	        	readOnly:false,
    	        	invalidText:"EL valor no puede ser vacio."
                		}
              }
           
            ],
//        selModel: {
//            selType: 'cellmodel'
//        },
        renderTo: 'editor-grid',
        width: 850,
        height: 400,
        title: 'Detalles de la Configuracion',
        frame: true,
        tbar: [/*{
            itemId: 'Agregar-campo',
            text: 'Agregar Campo',
            iconCls: 'add',
            handler : function(){
                // TODO: solicitar esta informacion al usuario, en ventana.
                var r = Ext.ModelManager.create({
                	ID:0,
                	category: 'Personalizada',
                	description: 'Variable agregada por el Administrador',
                	name: 'Variable '+ (store.getCount()+1),
                    value: 'Nuevo Valor '+ (store.getCount()+1)
                }, 'Parametros');
                r.setDirty(); //Indicamos que los datos son Nuevos y estan sucios (modificados) con los triangulos rojos.
                store.insert(store.getCount(), r);
//              store.insert(0, r);
                cellEditing.startEditByPosition({row: store.getCount()-1, column: 0});
                lbltotalrows.setText('Total de Parametros: ' + store.getCount());
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
            
        },*/     
        '->',
        {
        	xtype: 'button',
            text: 'Importar',
            itemId: 'Importar',
            iconCls: 'import',
            disabled: true,
            handler : function(){
            	Ext.MessageBox.alert('Importar', 
                	   	'No implementado aun.');
            }
        },
        {
            itemId: 'Exportar',
            iconCls: 'export',
        	xtype: 'button',
            text: 'Exportar',
            disabled: true,
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
						gavetamodel: store_json 
					}});
            	}

            }
        }
        ],
        collapsible: false,
        animCollapse: false,
        plugins: [
                  {
            ptype: 'rowexpander',
            rowBodyTpl : [
                '<p><b>Descripcion:</b> {description}</p><br>'
            ]
        },
        cellEditing],

        listeners: {
            selectionchange: function(view, records) {
               //grid.down('#Eliminar-campo').setDisabled(!records.length);
            }
        },
        bbar:[lbltotalrows, '->',         
        {
        	xtype: 'button',
            text: 'Guardar',
            itemId: 'Guardar',
            id: 'Guardar',
            iconCls: 'disk',
            disabled: false,
            handler : GuardarCambios
        }],
        forceFit:true
    });
    
    //Maneja la respuesta del servidor
    var successAjaxFn = function(response, request) {
    	Ext.getBody().unmask();
        var jsonData = Ext.JSON.decode(response.responseText);

        if (false == jsonData.success) {
        	Ext.Msg.show({
                title: 'Error',
                msg: jsonData.message,
                buttons: Ext.Msg.OK,
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });

			//Quita los triangulos rojos indicando que no hay cambios pendientes.
			Ext.each(store.getUpdatedRecords(), function(rec) {
				  rec.commit();
				}); //Solo si todo fue bien los desmarca.

        } 
        else {
        	Ext.Msg.show({
                title: 'Resultado',
                msg: jsonData.message,
                buttons: Ext.Msg.OK,
                animEl: 'elId',
                icon: Ext.MessageBox.INFO
                
            
            });
        }
        store.load();
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
        
     var group_det_gav = Ext.create('Ext.form.FieldSet',{
    	    xtype: 'fieldset',
    	    title: context_title,
    	    //collapsible: true,
    	    width:'98%',
    	    defaults: {
    	        labelWidth: 89,
    	        layout: {
    	            defaultMargins: {top: 10, right: 10, bottom: 10, left: 10}
    	        }

    	    },
    	    items: [
					{
					    xtype: 'fieldcontainer',
					    //fieldLabel: 'Name',
					    layout: {
					        type: 'hbox'
					        //align: 'left'
					    },
					    combineErrors: true,
					    defaultType: 'textfield',
					    defaults: {
					    margin: {top: 0, right: 3, bottom: 0, left: 0},
					    allowBlank: false,
					    blankText:'El valor no puede estar vacio.',
		            	minLength:2,
		            	minLengthText:'El "Nombre" y la "Descripcion" debe ser mayor a 2 caracteres.'

					 }
				},
					            
            grid]});
     var viewSize = Ext.getBody().getViewSize();
    	//Genera una ventana que contiene al grid
    	Ext.create('Ext.panel.Panel', {
    	    title: context_title,
    	    frame:true,
    	    height: viewSize.height,
    	    //width: 600,
    	    //width:'100%',
    	    //margin: {top: 0, right: 10, bottom: 10, left: 10},
    	    layout: 
    	    {
//    			defaultMargins: {top: 10, right: 10, bottom: 10, left: 10},
    		    type: 'anchor'
    	    },
    	    html: '<p>Lista de '+context_title+' en Fortimax.</p>',
    	    items: [group_det_gav],
    	    renderTo: Ext.getBody()
    	});
    	
    	function validateInfoBeforeSend(){

    		
    		var success = false;
    		
         	if(store.getCount()!=0)
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
			   }
    	 
    	 /**
          * Se crean los tooltips para los campos
          * */
     	var tooltips = [{
     		target: 'Guardar',
     		html: 'Guardar registros en Base de Datos.',
     		trackMouse: true,
     		width: 195
     	}];

     	Ext.each(tooltips, function(config) {
     		Ext.create('Ext.tip.ToolTip', config);
     	}); 
});

