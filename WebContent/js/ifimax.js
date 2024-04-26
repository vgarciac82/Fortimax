    Ext.require(['*']);
    
    //Variable para activar o descativar Features de Fortimax y mostrarlos como ifimax
    var nodo_select=null;
	//Por default se selecciona el Arbol de Expedientes.
	var tree_select='treeExpediente';
	var ifimax=	true; 

    Ext.onReady(function() {
        Ext.QuickTips.init();
        Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));

        //OVERRIDE necesario para hacer funcionar el load en arboles en EXTJS 4.0.7
        Ext.override(Ext.data.TreeStore, {
            load: function(options) {
                options = options || {};
                options.params = options.params || {};
                var me = this, node = options.node || me.tree.getRootNode(); //,root;
         
                // If there is not a node it means the user hasnt defined a rootnode yet. In this case lets just
                // create one for them.
                if (!node) {
                    node = me.setRootNode({
                        expanded: true
                    });
                }
                if (me.clearOnLoad) {
                    // this is what we changed.  added false
                    node.removeAll(false);
                }
                Ext.applyIf(options, {
                    node: node
                });
                options.params[me.nodeParam] = node ? node.getId() : 'root';
         
                if (node) {
                    node.set('loading', true);
                }
                return me.callParent([options]);
            }
        });
        
    //Modelo espacio de usuario.
     
    var privilegios = Ext.JSON.decode(jsonPrivilegios)    
    //Modelo del Store para Expedientes
    Ext.define('TreeModel', {
        extend: 'Ext.data.Model',
        fields: [
            { name: 'id', type: 'string', defaultValue: null },
            { name: 'text', type: 'string', defaultValue: null },
            { name: 'type', type: 'string', defaultValue: null },
            { name: 'extension', type: 'string', defaultValue: null },
            { name: 'expanded', type: 'bool', defaultValue: false, persist: false },
            { name: 'expandable', type: 'bool', defaultValue: true, persist: false },
            { name: 'checked', type: 'auto', defaultValue: null },
            { name: 'leaf', type: 'bool', defaultValue: null, persist: false },
            { name: 'cls', type: 'string', defaultValue: null, persist: false },
            { name: 'iconCls', type: 'string', defaultValue: null, persist: false },
            { name: 'allowDrop', type: 'boolean', defaultValue: true, persist: false },
            { name: 'allowDrag', type: 'boolean', defaultValue: true, persist: false },
            { name: 'qtip', type: 'string', defaultValue: null, persist: false }
        ]
    });
        		
        //Modelo del Store para propiedades de nodo seleccionado
    Ext.define('PropiedadesModel', {
        extend: 'Ext.data.Model',
        fields: [
			        {name: 'name',  type: 'string'},
			        {name: 'value',   type: 'string'}
        	]
    });
        
		var txtFiltro = Ext.define('Ext.ux.CustomTrigger', {
		  id:'txtFiltro',
		  extend: 'Ext.form.field.Trigger',
		  alias: 'widget.xtext',
		  triggerTip: 'Click para filtrar.',
		  triggerBaseCls :'x-form-trigger',
		  triggerCls:'x-form-clear-trigger',
		  width:180,
		  emptyText:'Filtrar...',
	      onTriggerClick: function() {
	        this.setValue("");
	    },
	    listeners:{
    		change:function(){
    			if(this.getValue()!=""){
					//Modificar para crear filtro del arbol
    				storeGavetas.clearFilter();
    				storeGavetas.filter({
    	    			 property: text,
    	   	  		     value: this.getValue(),
    	   	  		     anyMatch: true,
    	   	  		     caseSensitive: false
    	    			});
    			}
    			else{
    				storeGavetas.clearFilter();
    			}
    		}
    	}
	});
	
	
	//Store espacio del usuario.
	var GraficaEspacioStore = Ext.create('Ext.data.JsonStore', {
    fields: ['nombre', 'espacio_utilizado', 'espacio_disponible', 'unidades'],
    data: [
        { 'nombre': 'Espacio',   'espacio_utilizado':101, 'espacio_disponible': 160, 'unidades': 'MB' }
    ]
	});
	
	//Store propiedades de nodo seleccionado.
	
		var PropiedadesStore = new Ext.data.Store({ 
        model: 'PropiedadesModel', 
        proxy: { 
            type: 'ajax', 
            url: rutaArbolServlet,
            	reader: { 										
                type: 'json', 
                root: 'items'
            },
            extraParams: 
	         {
	              action: 'getDatosNodo',
	              select:''
	          } 
        } ,
	autoLoad:true
    });

	//Store Arbol de Mis Documentos		
    var storeMidocs = Ext.create('Ext.data.TreeStore', {
    	autoLoad:ifimax,
        proxy: {
            type: 'ajax',
            url: rutaArbolServlet+'?action=getMisDocumentos&usuario=aramirez'
        },
        root: {
        	children : ifimax? []: '', //para realizar o no la peticion (Autoload, lo requiere.)
        	expanded: true
        },
        folderSort: true,
        sorters: [{
            property: 'text',
            direction: 'ASC'
        }]
    });
    //Arbol de Mis Documentos
    var treeMidocs = Ext.create('Ext.tree.Panel', {
        id:'treeMisdocumentos',
        rootVisible: false,
    	hidden : ifimax,
        store: storeMidocs,
        viewConfig: {
            plugins: {
                ptype: 'treeviewdragdrop'
            }
        },
        width: 250,
        title: 'Mis Documentos',
		iconCls: 'misdoc',
        useArrows: true,
        dockedItems: [{
        	id:'docktreeMisdocumentos',
			dock: 'top',
            xtype: 'toolbar',
            items: ['->',{
                text: null, //Expandir
                iconCls: 'expandir',
                handler: function(){
                	//Se accede al treepanel de forma relativa.
                	this.up().up().collapseAll();
                	this.up().up().expandAll();
                }
            }, {
                text: null, //Colapsar
                iconCls: 'colapsar',
                handler: function(){
                	this.up().up().collapseAll();
                }
            },
            	{
                text: null,
                iconCls:'btnActualizar16x16',
                handler: function(){
                	
                	ActualizaArbol(this.up().up().id);
                	
                }
            }
            
            ]
        }],
		listeners:{

			itemcontextmenu:function( gr, record, item, index, e, eOpts ){
				 e.stopEvent();
				 if(!ifimax)
					 MenuContextualArbol(e.xy,record,item,index);
			}, 
				itemclick : function(view,record,item,index,event,options ){  
				//Esto se usara para setear una variable global del ID del elemento que se "opere"
				 //console.log('Elemento selecionado: ' + record.data.id);
				 nodo_select=record.data;
				 desplegar_propiedades(true);
				recargar_propiedades(nodo_select);
			},
				itemdblclick : function(view,record,item,index,event,options ){  
				//console.log('Elemento selecionado: ' + record.data.id);
				nodo_select=record.data;
				AbrirTab(record.data);
			},
			
			load: function() {
				var dt = Ext.getCmp('docktreeMisdocumentos');
				dt.setDisabled(false);
			},
			
			select: function() {
				//Establece el arbol activo.
				tree_select=this.id;
			}

		}
    });
	
	
	//Store Arbol de Gavetas
    var storeGavetas = Ext.create('Ext.data.TreeStore', {
    	model:'TreeModel', //cambiar esto a un modelo propio de gavetas..
    	autoLoad:ifimax,
        proxy: {
            type: 'ajax',
            //url: '../js/gavetas.json'
            url: rutaArbolServlet+'?action=getGavetas'
        },
        root: {
        	children : ifimax? []: '',
        	expanded: true
        },
        folderSort: true,
        sorters: [{
            property: 'text',
            direction: 'ASC'
        }]
    });

    //Arbol de Gavetas
    var treeGavetas = Ext.create('Ext.tree.Panel', {
        id:'treeGavetas',
        rootVisible: false,
    	hidden : ifimax,
        store: storeGavetas,
        viewConfig: {
            plugins: {
                ptype: 'treeviewdragdrop'
            }
        },
        width: 250,
        title: 'Gavetas',
		iconCls: 'gaveta',
        useArrows: true,
        dockedItems: [{
        	id:'docktreeGaveta',
			dock: 'top',
            xtype: 'toolbar',
            items: ['->',{
                text: null, //Expandir
                iconCls: 'expandir',
                handler: function(){
                	//Se accede al treepanel de forma relativa.
                	this.up().up().collapseAll();
                	this.up().up().expandAll();
                }
            }, {
                text: null, //Colapsar
                iconCls: 'colapsar',
                handler: function(){
                	this.up().up().collapseAll();
                }
            },
            	{
                text: null,
                iconCls:'btnActualizar16x16',
                handler: function(){
                	
                	ActualizaArbol(this.up().up().id);
                	
                }
            }
            
            ]
        }],
		listeners:{

			itemcontextmenu:function( gr, record, item, index, e, eOpts ){
				 e.stopEvent();
				 if(!ifimax)
					 MenuContextualArbol(e.xy,record,item,index);
			}, 
				itemclick : function(view,record,item,index,event,options ){  
				//Esto se usara para setear una variable global del ID del elemento que se "opere"
				 //console.log('Elemento selecionado: ' + record.data.id);
				 nodo_select=record.data;
				 desplegar_propiedades(true);
				 recargar_propiedades(nodo_select);
				
			},
				itemdblclick : function(view,record,item,index,event,options ){  
				//console.log('Elemento selecionado: ' + record.data.id);
				nodo_select=record.data;
				AbrirTab(record.data);
			},
			
			load: function() {
				var dt = Ext.getCmp('docktreeGaveta');
				dt.setDisabled(false);
			},
			
			select: function() {
				//Establece el arbol activo.
				tree_select=this.id;
			}

		}
    });

	//Store de Arbol de Expediente
    var storeExpediente = Ext.create('Ext.data.TreeStore', {
        model:'TreeModel',
    	proxy: {
        	requestMethod: 'GET',
            reader: {
                type: 'json'    
            },
            type: 'ajax',
            url: rutaExpedienteServlet+'?action=getArbolExpedienteJson&titulo_aplicacion='+titulo_aplicacion+'&id_gabinete='+id_gabinete
            //url: '../js/expediente.json'
        },
        root: {
            //text: titulo_aplicacion, prueba
            //id: titulo_aplicacion +'G'+id_gabinete,
            expanded: true
        },
        folderSort: true,
        sorters: [{
            property: 'text',
            direction: 'ASC'
        }],
        listeners:{
        	load: function(store, records, successful, operation, eOpts) {
				var record = treeExpediente.getStore().getNodeById(nodo);
//				treeExpediente.getSelectionModel().select(record);
				treeExpediente.selectPath(record.getPath());
				treeExpediente.fireEvent('itemclick',treeExpediente,record);
				AbrirTab(record.data);
			}
        }
    });
    //Arbol de Expediente
    var treeExpediente = Ext.create('Ext.tree.Panel', {
        id:'treeExpediente',
        cls:'nodoselectedbold',
        rootVisible: false,
    	store: storeExpediente,
        viewConfig: {
            plugins: {
                ptype: 'treeviewdragdrop'
            }
        },
        width: 250,
        title: 'Expediente',
		iconCls: 'expediente',
        useArrows: true,
        dockedItems: [{
        	id:'docktreeExpediente',
			dock: 'top',
            xtype: 'toolbar',
            items: ['->',{
                text: null, //Expandir
                iconCls: 'expandir',
                handler: function(){
                	//Se accede al treepanel de forma relativa.
                	this.up().up().collapseAll();
                	this.up().up().expandAll();
                }
            }, {
                text: null, //Colapsar
                iconCls: 'colapsar',
                handler: function(){
                	this.up().up().collapseAll();
                }
            },
            	{
                text: null,
                iconCls:'btnActualizar16x16',
                handler: function(){
                	
                	ActualizaArbol(this.up().up().id);
                	
                }
            }
            
            ]
        }],
		listeners:{

			itemcontextmenu:function( gr, record, item, index, e, eOpts ){
				 e.stopEvent();
				 if(!ifimax)
					 MenuContextualArbol(e.xy,record,item,index);
			}, 
				itemclick : function(view,record,item,index,event,options ){  
				//Esto se usara para setear una variable global del ID del elemento que se "opere"
				 //console.log('Elemento selecionado: ' + record.data.id);
				 nodo_select=record.data;
				desplegar_propiedades(true);
				recargar_propiedades(nodo_select);
			},
				itemdblclick : function(view,record,item,index,event,options ){  
				//console.log('Elemento selecionado: ' + record.data.id);
				nodo_select=record.data;
				AbrirTab(record.data);
			},
			
			load: function() {
				var dt = Ext.getCmp('docktreeExpediente');
				dt.setDisabled(false);
			},
			
			select: function() {
				//Establece el arbol activo.
				tree_select=this.id;
			}

		}
    });
    
	//Inicio Botones Panel de Arboles Principal
	
	//Botones Arbol
	//Boton Buscar
	var btnBuscar = Ext.create('Ext.Button', {
	hidden: ifimax,
    text: null,
	iconCls: 'btnbuscar',
	scale: 'medium',
    handler: function() {
        alert('Por implementar !');
    }
	});
	
	//Boton Applet
	var btnappletfortimaxupload = Ext.create('Ext.Button', {
	disabled: !privilegios.digitalizar,
	hidden: !ifimax_privilegioEditarExpediente,
    text: null,
	iconCls: 'btnappletfortimaxupload',
	scale: 'medium',
    handler: function() {
    	crearVentana('../jsp/PageDocumentUploadApplet.jsp?select='+titulo_aplicacion+'_G'+id_gabinete+'C1D0&appletExtended=true', "Applet de carga",500, 600);
    }
	});
	
	var btnlimpiardocumento = Ext.create('Ext.Button', {
	disabled: !privilegios.eliminar,
	hidden: !ifimax_privilegioEditarExpediente,
	text: null,
	iconCls: 'btnlimpiardocumento',
	scale: 'medium',
    handler: function() {
        if (nodo_select.type=='document')//revisamos que sea un documento. y no que sea Imaxfile (&& nodo_select.iconCls=='nodo_imx')
        {
        	if(nodo_select.iconCls!='nodo_imx')
        		CerrarTab(nodo_select);//Cerramos tab por que IE9 no muestra el sig message si no está empotrado el archivo correctamente (p. ej. si no hay visor de PDF)
            Ext.MessageBox.confirm('Confirmacion', 'Desea limpiar el contenido del documento?', function(btn, text){
                if (btn == 'yes')
                {
                    Ext.Ajax.request({
                        url: '../delpagekeeper?action=deleteAllPages',
                        params: {
                            select: nodo_select.id
                        }, //Por implementar respuesta del servidor !
                        success: function(response){
                            var text = Ext.decode(response.responseText);
                            Ext.MessageBox.alert('Confirmacion', text.msg);
                        	ActualizaArbol(tree_select);
                        	AbrirTab(nodo_select); //Actualiza el tab.
                        }
                    });
                }
            });
        }
        else
        {
            Ext.MessageBox.alert('Notificacion', 'Seleccione un documento.');
        }
	}
	});
	
	//Boton Compartir
	var btncompartir = Ext.create('Ext.Button', {
	hidden: ifimax,
    text: null,
	iconCls: 'btncompartir',
	scale: 'medium',
    handler: function() {
        alert('Por implementar !');
    }
	});
	
	
	//Boton Actualizar
	var btnactualizar = Ext.create('Ext.Button', {
	hidden: ifimax,
	text: null,
	iconCls: 'btnactualizar',
	scale: 'medium',
    handler: function() {
    	//Obtiene todos los arboles y los actualiza
    	Ext.ComponentQuery.query('treepanel').forEach(function(tree){ActualizaArbol(tree.id);});   
    }
	});
	
	//Boton Modificar
	var btnmodificar = Ext.create('Ext.Button', {
	hidden: ifimax,
    text: null,
	iconCls: 'btnmodificar',
	scale: 'medium',
    handler: function() {
        alert('Por implementar !');;
    }
	});
	
	//Boton Reporte
	var btnReporte = Ext.create('Ext.Button', {
	hidden: ifimax,
    text: null,
	iconCls: 'btnReporte',
	scale: 'medium',
    handler: function() {
        alert('Por implementar !');;
    }
	});
		
	//Boton mas
	var btnmas = Ext.create('Ext.Button', {
	hidden: ifimax,
    text: null,
	iconCls: 'btnEngrane',
	scale: 'medium',
	menu : {
		items: [{text:'Copiar', iconCls: 'btncopiar'},
				{text:'Cortar', iconCls: 'btncortar'},
				{text:'Pegar', iconCls: 'btnpegar'},
				{text:'Otras', iconCls: 'settings'}
				]
	},
    handler: function() {
        //alert('Por implementar !');
    }
	});
	
	//Fin Botones Panel de Arboles Principal
	
	//Tabs principales
	var tabsprincipal = new Ext.create('Ext.tab.Panel', {
	region: 'center', // a center region is ALWAYS required for border layout
	deferredRender: false,
	activeTab: 0,     // first tab initially active
	items: [{
		//id:'panel_principal',
		//xtype: 'panel',
		contentEl: 'center1',
		title: 'Principal',
		closable: false,
		autoScroll: true
	}]
});
	
	
//Botones Panel  Superior	
var btnSalir = Ext.create('Ext.Button', {
	text: null,
	iconCls: 'btnSalir',
	scale: 'medium',
	handler: function() {
		
    	document.location.href= "../jsp/Salida.jsp"
}
});

//Botones Panel  Superior	
var btnOpciones = Ext.create('Ext.Button', {
	text: null,
	iconCls: 'btnEngrane',
	scale: 'medium',
	menu : {
		items: [
		        {text:'Cambiar Contraseña', iconCls: 'btnMas'},
		        {text:'Guia de uso', iconCls: 'btnProteger'},
		        {text:'Utileria de digitalizacion', iconCls: 'btnProteger'}
		       ]
	},
    handler: function() 
    {
    	
    }
});


var btnDescargar = Ext.create('Ext.Button', {
    text: 'Descargar',
	iconCls: 'btnDescargar',
	scale: 'medium',
	//renderTo: 'center2',
    handler: function() {
    	
    	Descargar(nodo_select);
        
    }
	});

var btnRespaldar = Ext.create('Ext.Button', {
    text: 'Respaldar',
	iconCls: 'btnRespaldar',
	scale: 'medium',
	//renderTo: 'center2',
    handler: function() {
        Respaldar_carpeta(nodo_select);
    }
	});

var btnExpediente = Ext.create('Ext.Button', {
    text: 'Expediente',
	iconCls: 'btnExpediente',
	scale: 'medium',
	menu : {
		items: [
		        {text:'Crear', iconCls: 'btnMas'},
		        {text:'Proteger', iconCls: 'btnProteger'}
		       ]
	},
    handler: function() {
        //alert('Por implementar !');
    }
	});

var btnDocumento = Ext.create('Ext.Button', {
    text: 'Documento',
	iconCls: 'btnDocumento',
	scale: 'medium',
	menu : {
		items: [
		        {text:'Crear', iconCls: 'btnMas'},
		        {text:'Proteger', iconCls: 'btnProteger'}
		       ]
	},
    handler: function() {
        //alert('Por implementar !');
    }
	});

var btnCarpeta = Ext.create('Ext.Button', {
    text: 'Carpeta',
	iconCls: 'btnCarpeta',
	scale: 'medium',
	menu : {
		items: [
		        {text:'Crear', iconCls: 'btnMas'},
		        {text:'Proteger', iconCls: 'btnProteger'}
		       ]
	},
    handler: function() {
        //alert('Por implementar !');
    }
	});
	
//Indicador de espacio de usuario.	   
var grafica_espacio = Ext.create('Ext.chart.Chart', {
    width: 260,
    height: 110,
    animate: true,
    store: GraficaEspacioStore,
    axes: [{
        type: 'Numeric',
        position: 'bottom',
        fields: ['espacio_disponible'],
        label: {
            renderer: Ext.util.Format.numberRenderer('0,0')
        },
        title: 'Espacio disponible',
        grid: false,
        minimum: 0
    }
    /*, {
        type: 'Category',
        position: 'left',
        fields: ['nombre'],
        title: ''
    }*/],
    background: {
      gradient: {
        id: 'backgroundGradient',
        angle: 90,
        stops: {
          0: {
            color: '#ffffff'
          },
          60: {
            color: '#dce8f4'
          }
        }
      }
    },
    series: [{
        type: 'bar',
        axis: 'bottom',
        //highlight: true,
        tips: {
          trackMouse: true,
          width: 230,
          height: 28,
          renderer: function(storeItem, item) {
            this.setTitle(storeItem.get('nombre') 
            + ': ' 
            + storeItem.get('espacio_utilizado') 
            + ' MB utilizados de ' 
            + storeItem.get('espacio_disponible') + ' MB.');
          }
        },
        label: {
          	display: 'insideEnd',
            field: 'espacio_utilizado',
            renderer: Ext.util.Format.numberRenderer('0'),
            orientation: 'horizontal',
            color: '#333',
            'text-anchor': 'middle',
            contrast: true
        },
        renderer: function(sprite, record, attr, index, store) {
        var fieldValue = Math.random() * 20 + 10;
        var value = (record.get('espacio_utilizado')*fieldValue >> 0) % 5;
        var color = ['rgb(213, 70, 121)', 
                     'rgb(44, 153, 201)', 
                     'rgb(146, 6, 157)', 
                     'rgb(49, 149, 0)', 
                     'rgb(249, 153, 0)'][value];
        return Ext.apply(attr, {
        fill: color
        });
                },
        xField: 'nombre',
        yField: ['espacio_utilizado']
    }]
});
	
//Contenedor principal Viewport
Ext.create('Ext.Viewport', {
    id: 'border-example',
    layout: 'border',
    items: [
            
		//Panel superior para banner y controles.
		{
            //xtype: 'box',
            id: 'header',
            region: 'north',
			unstyled:true,
					dockedItems: [{
					hidden: ifimax,
                    dock: 'bottom',
                    xtype: 'toolbar',
                    items: ['->','-',btnExpediente,btnCarpeta,btnDocumento,btnRespaldar,btnDescargar,btnOpciones,'     ','-','     ',btnSalir]
                }]
        },
			
        //Panel inferior usado como espaciador.
		Ext.create('Ext.Component', {
                region: 'south',
                height: 70, // give north and south regions a height
                autoEl: {
                    tag: 'div'
                    //html:'<p></p>'
                }
         }),
		
         //Panel derecho de Propiedades del documento
		{
			id:'panel_propiedades',
            xtype: 'tabpanel',
            region: 'east',
            title: 'Propiedades',
            dockedItems: [{
                dock: 'top',
                xtype: 'toolbar',
                items: [ '->', btnmodificar]
            }],
            animCollapse: true,
            collapsible: !ifimax,
            collapsed : ifimax,
            hidden: ifimax,
            disabled: ifimax,
            split: true,
            width: 225, // give east and west regions a width
            minSize: 200,
            maxSize: 800,
            margins: '0 5 0 0',
            activeTab: 0,
            tabPosition: 'bottom',
            items: [
            
            Ext.create('Ext.grid.Panel', {
            		title: 'Detalles',
				    store: PropiedadesStore,
				    columns: [
				        { header: 'Nombre',  dataIndex: 'name', width:125, tdCls:'column_bold' },
				        { header: 'Valor', dataIndex: 'value', flex: 1}
				    ],
				    layout:'fit'
				}),
            
            Ext.create('Ext.grid.PropertyGrid', {
                    title: 'Atributos',
                    closable: false,
                    //store: PropiedadesStore, 
                    source: {
                        	"Nombre": "Docuemento 1",
                        	"Descripcion": "Midocumento",
                        	"Contiene": 12,
                        	"Creado": Ext.Date.parse('10/15/2006', 'd/m/Y'),
							"Modificado": Ext.Date.parse('10/15/2006', 'd/m/Y'),
                        	"Protegido": false
                    		}
                })]
        }, 
            
        //Panel  izquierdo del Arbol de Fortimax
        {
            region: 'west',
            stateId: 'navigation-panel',
            id: 'west-panel', // see Ext.getCmp() below
            title: 'Bienvenido: '+ usuario,
            split: true,
            width: 260,
            minWidth: 260,
            //maxWidth: 250,
            collapsible: true,
            animCollapse: true,
            margins: '0 0 0 0',
			
			dockedItems: [{
                dock: 'top',
                xtype: 'toolbar',
                items: [ btnBuscar, btnappletfortimaxupload, btnlimpiardocumento,btncompartir,btnReporte,btnmas,'->','-',btnactualizar]
            }],
            //items: [treeMidocs,treeGavetas,treeExpediente]
			items: [ 
			
		          {
                    height: 420,
                    border:false,
                    margin: '0 0 0 0',
					layout: 'accordion',
					items: [treeExpediente, treeGavetas,treeMidocs]
					
                },{
                    hidden: ifimax,
                	xtype: 'panel',
                	unstyled:true,
                	//border:true,
                	layout: 'fit',
                    //title: 'Espacio disponible',
                    //collapsible: false,
					//height: 200,
                    flex: 1,
                    //region: 'west',
                    items:[grafica_espacio],
                    autoScroll: false
                }	
			
			
        ]
			
        },
		//Tabs principales con el contenido abierto desde el arbol
        tabsprincipal ]
   
});//cierre Viewport

//-------------------
//Incio de Funciones
//-------------------
        
function MenuContextualArbol(pos,record, item, index){
	var contextMenu = new Ext.menu.Menu({
		  items: [
		   {text: 'Nueva carpeta',iconCls: 'menucontextnuevacarpeta',handler:function(){addC(record,item,index);}},
		   {text: 'Nuevo documento',iconCls: 'menucontextnuevodocumento',handler:function(){addD(record,item,index);}},
		   {text: 'Modificar',iconCls: 'menucontextmodificar',handler:function(){addD(record,item,index);}},
		   {text: 'Cortar',iconCls: 'menucontextcortar',handler:function(){addD(record,item,index);}},
		   {text: 'Copiar',iconCls: 'menucontextcopiar',handler:function(){addD(record,item,index);}},
		   {text: 'Pegar',iconCls: 'menucontextpegar',handler:function(){addD(record,item,index);}},						   
		   {text: 'Eliminar',iconCls: 'menucontexteliminar',handler:function(){del(record,item,index);}}	          
	       ]
		});
	contextMenu.showAt(pos);
	}
    	
function ActualizaArbol(idtree){
	tree = Ext.getCmp(idtree);
	if(tree!=null){
		//Inhabilita todos los controles
		tree.getDockedItems('toolbar[dock="top"]').forEach(function(c){c.setDisabled(true);});
		tree.store.load();
		//console.log('Arbol Actualizado: '+idtree);
	}
}
    	
    	
function crearVentana(url,titulo,w,h){
	if(Ext.getCmp('ventana')==null){			
		new Ext.window.Window({
		id:'ventana',
		title: titulo,
		height: h,
		constrain:true,
		width: w,
		layout:'anchor',
		draggable:true,
		resizable:false,
		 html: '<div style="height:100%; scrolling="yes" overflow:scroll !important;"><iframe id="framePDF" src="'+url+'"  style="width:100%;height:100%;border:none;"></iframe></div>'
		}).show();
	}
}
    	
function CerrarTab(Nodo){
	var id = 'tab_'+Nodo.id;
	var tab = tabsprincipal.items.findBy(function(i){
		return i.id == id;
	});
	if(tab) {
		tabsprincipal.remove(tab.id); 
	}
}

function AbrirTab(Nodo){

if(!Nodo.leaf)
	return;

fmx_page='ifimax';

switch(fmx_page)
	 {
	 case 'ifimax':
		url = rutaServletIfimax;
		break;			
	 default:
	   url = rutaPageError404;
	 }
	
var id = 'tab_'+Nodo.id;
var tab = tabsprincipal.items.findBy(function(i){
	return i.id == id;
});
if(tab) {
	tabsprincipal.remove(tab.id); 
}

if(Nodo.iconCls == 'nodo_empty' && !ifimax_privilegioEditarExpediente && ifimax){
	Ext.MessageBox.alert('Notificacion', 'Documento vacio');
	//console.log('Documento sin contenido');
	return;
}

desplegar_propiedades(false);

if(Nodo.type=='document')
	tabsprincipal.add({
		id:id,
    	title: 'D: '+Nodo.text,
    	iconCls: Nodo.iconCls,
    	
		items:[	
				{
		        	xtype : 'component',
		        	region: 'west',
		        	autoScroll: false,
		        	style: {
		        			border:'none'
			    	},
		        	autoEl : {
		            			tag : "iframe",
		            			src : url+'?accion=ifimax_viewer&select='+Nodo.id
		        			}
				}
		
		],
		layout: 'fit',
    	closable:true
	}).show();    
	
else if(Nodo.type=='gaveta.hija')
	tabsprincipal.add({
		id:id,
    	title: 'E: '+Nodo.text,
    	iconCls: Nodo.iconCls,	
		items:
		[
			{
	        	xtype : 'component',
	        	region: 'west',
	        	autoScroll: false,
		        style: {
		        			border:'none'
			    },
	        	autoEl : {
	            			tag : "iframe", //cambiar esto por una peticion a un servlet !
	            			src : '../jsp/ResultadosBusquedaExpedientes.jsp?select='+Nodo.id+'&tipoAccion=expedientes'
	        			}
			}
		],
		layout: 'fit',
    	closable:true
	}).show(); 
}


function Descargar(Nodo){
	
desplegar_propiedades(false);

var id = 'tab_'+Nodo.id;
var tab = tabsprincipal.items.findBy(function(i){
	return i.id == id;
});
if(tab) {
	tabsprincipal.remove(tab.id); 
}
	
	tabsprincipal.add({
		id:id,
    	title: 'Descargar: '+Nodo.text,
    	iconCls: Nodo.iconCls,	
		items:
		[
			{
	        	xtype : 'component',
	        	region: 'west',
	        	autoScroll: false,
		        style: {
		        			border:'none'
			    },
	        	autoEl : {
	            			tag : "iframe", //cambiar esto por una peticion a un servlet !
	            			src : '../jsp/GetFolder.jsp?opcion=versiones&accion=abrir&select='+Nodo.id
	        			}
			}
		],
		layout: 'fit',
    	closable:true
	}).show(); 
}


function Respaldar_carpeta(Nodo){
	
desplegar_propiedades(false);

var id = 'tab_'+Nodo.id;
var tab = tabsprincipal.items.findBy(function(i){
	return i.id == id;
});
if(tab) {
	tabsprincipal.remove(tab.id); 
}
	
	tabsprincipal.add({
		id:id,
    	title: 'Respaldar: '+Nodo.text,
    	iconCls: Nodo.iconCls,	
		items:
		[
			{
	        	xtype : 'component',
	        	region: 'west',
	        	autoScroll: false,
		        style: {
		        			border:'none'
			    },
	        	autoEl : {
	            			tag : "iframe", //cambiar esto por una peticion a un servlet !
	            			src : '../jsp/FolderUploadApplet.jsp?select='+Nodo.id
	        			}
			}
		],
		layout: 'fit',
    	closable:true
	}).show(); 
}

function desplegar_propiedades(expandir)
{
	//colapso del panel de propiedades al abrir el visor.
	var panel_propiedades = Ext.getCmp('panel_propiedades');
	if(!panel_propiedades.isHidden())
		expandir ? panel_propiedades.expand() : panel_propiedades.collapse();
}

function recargar_propiedades(nodo_select)
{
	PropiedadesStore.load({params: {select: nodo_select.id}});
	//console.log("propiedades cargadas: " + nodo_select );
	
}
	
//-------------------
//Fin de Funciones
//-------------------
        
    });//Finaliza funcion OnReady