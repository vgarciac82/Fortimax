Ext.define('FMX.view.ContainerPrincipal', {
    extend: 'Ext.container.Container',
    alias : 'widget.containerprincipal',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    items:[{
    	xtype: 'panel',
    	accion: 'PanelGlobal',
        flex:2,
        border: 0,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        bbar: ['->', {
        	xtype: 'button',
        	text: actualizar?'Actualizar':'Guardar',
        	accion: 'GuardarCambiosEstructura',
        	iconCls: 'save'
        }],
        items: [{
        	xtype: 'panel',
        	flex: 4,
        	title: 'Estructuras',
        	layout: {
                type: 'vbox',
                align: 'stretch'
            },
        	items: [{
        		xtype: 'fieldset',
        		margin: '0 5 0 5',
        		title: 'Datos de estructura',
        		items: [{
        			xtype: 'textfield',
        			fieldLabel: 'Nombre',
        			allowBlank: false,
        			regex: /^^[^\ \\\/\:\*\?\"\<\>\|][^\\\/\:\*\?\"\<\>\|]*[^\ \\\/\:\*\?\"\<\>\|]$|^[^\ \\\/\:\*\?\"\<\>\|]*$/,
			        regexText: 'No se permiten espacios al inicio o al final ni los siguientes caracteres: \\ / : * ? " < > |',
        			emptyText : 'Estructura',
        			value: estructura,
        			disabled: actualizar,
        			width: 350,
        			margin: '0 0 10 10'
        		},{
        			xtype: 'textarea',
        			fieldLabel: 'Descripcion',
        			emptyText : 'Descripcion de la estructura',
        			width: 350,
        			height: 50,
        			margin: '0 0 10 10'
        		}]
        	},{
        		xtype: 'fieldset',
        		title: 'Cómo crear estructura',
        		margin: '0 5 2 5',
        		items: [{
        			xtype: 'label',
        			html: 'Para empezar a crear/modificar la estructura coloquese en la raiz "/" o en el elemento deseado y de click derecho, en el menu emergente seleccione "Agregar Documento", "Agregar Carpeta" o "Eliminar elemento" hasta formar la estrucutura requerida. Tambien puede arrastrar y reacomodar los elementos'
        		}]
        	}]
        },{
        	xtype: 'treepanel',
        	accion: 'ArbolEstructura',
        	rootVisible: true,
        	flex: 6,
        	tbar: ['->', {
        		xtype: 'button',
        	    text: 'Exportar',
        	    accion: 'ExportarEstructura',
        	    iconCls: 'export'
        	},{
        		xtype: 'button',
        	    text: 'Importar',
        	    accion: 'ImportarEstructura',
        	    iconCls: 'import'
        	}],
        	store: Ext.create('Ext.data.TreeStore', {
        		fields:['id', 'text', 'type', 'leaf', 'atributos', 'expanded'],
        	    root: {
        	    	text: '/',
        	    	id: 'root',
        	        expanded: true,
        	        type: 'folder',
        	        root: true,
        	        atributos: [],
        	        children: []
        	    },
        	    proxy: {
        	    	type: 'ajax',
            		url: '../js/MVC/EstructurasMVC/jsons/arbol.json',
            	    reader: {
            	        type: 'json'
            	    }
            	},
        		autoLoad: false        	
        		,folderSort: true,
        		sorters: [{
        			property: 'text',
        			direction: 'ASC'
        		}]
        	
        	}),
        	viewConfig:{
    	    	toggleOnDblClick: false
//    	    	singleExpand:false 
    	    },
        	hideHeaders: true,
        	columns: [{
        		xtype: 'treecolumn',
        		flex: 1,
        		dataIndex: 'text',
        		editor: {
        			xtype: 'textfield',
        			allowBlank: false
        		}
        	}],
        	plugins:Ext.create('Ext.grid.plugin.CellEditing', {
	        	clicksToEdit: 2,
	        	listeners: {
	                beforeedit: function(cell, e){
	                    if (cell.record.data.root)
	                        return false;
	                }
	            }
	        })
        }]
    },{
    	xtype: 'panel',
    	title: 'Formulario',
    	accion: 'PanelAtributos',
    	disabled: true,
        flex: 1,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items:[{
        	xtype: 'propertygrid',
        	border: 0,
        	accion: 'PropertyGridAtributos',
        	source: {}
        },{
        	xtype: 'fieldset',
//        	disabled: true,
        	title: 'Atributo',
        	accion: 'FieldSetMetadatosAtributo',
        	margin: '0 2 10 2',
        	items:[{
        		xtype: 'form',
        		accion: 'MetadatosAtributoForm',
        		margin: '0 0 10 0',
        		border: 0,
        		items:[{
        			xtype: 'container',
        			layout: 'hbox',
        			items:[{
                		xtype: 'checkbox',
                		name: 'activo',
                		accion: 'AtributoActivo',
                		margin: '0 10 0 0',
                		boxLabel: 'Activo',
                		columns: 1
                	},{
                		xtype: 'checkbox',
                		name: 'modificable',
                		accion: 'AtributoModificable',
                		margin: '0 0 0 0',
                		boxLabel: 'Modificable',
                		columns: 1
                	}]
        		},{
            		xtype: 'textarea',
            		name: 'descripcion',
                    fieldLabel: 'Descripcion',
                    accion: 'DescripcionAtributo',
                    readOnly: true,
                    labelAlign:'top',
            		height: 60,
            		width: 300
            	}]
        	}]
        }]
    }]
});