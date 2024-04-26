Ext.define('FMX.view.este.TabPanelPropiedadesPrincipal', {
    extend: 'Ext.tab.Panel',
    alias : 'widget.TabPanelPropiedadesPrincipal',

	id:'TabPanelPropiedadesPrincipal',
	activeTab: 0,
	region: 'east',
	title: 'Propiedades',
	animCollapse: true,
	collapsible: true, // !ifimax,
	collapsed : true, //ifimax,
	disabled:  false,// ifimax,
	split: true,
	width: 225, 
	minSize: 200,
	maxSize: 800,
	margins: '0 5 0 0',
	activeTab: 0,
	tabPosition: 'bottom',
	dockedItems: [{
		dock: 'top',
		xtype: 'toolbar',
		items: [ '->', '-',		
			//Boton btnModificarPropiedades
			Ext.create('Ext.Button', {
			id:'btnModificarPropiedades',
			hidden: false,//ifimax,
			text: null,
			iconCls: 'btnmodificar',
			scale: 'medium',
			handler: function() {
				alert('Por implementar !');;
				}
			})
		
		]
	}],

	    initComponent: function() {
            this.items = [          
							Ext.create('Ext.grid.Panel', {
							title: 'Detalles',
							store: 'PropiedadesNodo',
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
							})
						];
        this.callParent(arguments);
		}
});