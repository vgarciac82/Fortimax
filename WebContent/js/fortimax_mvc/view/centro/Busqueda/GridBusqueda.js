Ext.define('FMX.view.centro.Busqueda.GridBusqueda', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.gridbusqueda',
    
	cls:'GridBusqueda',
	region:'center',
	columnLines: true,
	autoScroll: true,
	campos: {},
	liveSearch: "",
	windowedicionbusqueda: [],
	tbar:[
		{
			xtype: 'button',
			accion:'DescargarReporte',
			tooltip: 'Descargar Reporte',
			iconCls:'btnXLSIconCls',
			scale: 'medium'
		},
		'-',
		{
			xtype: 'label',
			html:'Resultados: '		
		},
		{
			xtype: 'label',
			accion: 'MostrarFiltro',
			html:'<b>Todos</b>'		
		},
		'->',
		'-',
		{	
			xtype: 'triggerfieldfiltro',
			fieldLabel:'Buscar'
		},
		'-',
		'->',
		{
				xtype: 'combo',
				accion: "CantidadRegistrosMostrados",
			    fieldLabel: 'Registros',
			    store: 'Busqueda.CantidadRegistrosMostrados',
			    queryMode: 'local',
			    width:130,
			    labelWidth:70,
			    displayField: 'texto',
			    valueField: 'valor',
			    value: 25,
			    editable:false
		},
		'-',
		{
			xtype: 'button',
			accion:'MostrarVentanaBusqueda',
			tooltip : 'Buscar/Crear',
			iconCls:'btnMostrarIconCls',
			scale: 'medium'
		},
		'-',
		{
			xtype: 'button',
			accion: 'RecargarBusqueda',
			iconCls:'btnRecargarIconCls',
			scale: 'medium',
			tooltip : 'Recargar todo'
		},
		'-',
		{
			xtype: 'button',
			accion: 'EditarFila',
			iconCls:'btnEditarIconCls',
			scale: 'medium',
			tooltip : 'Editar',
			hidden:!permisoModificar,
			disabled: true
		},
		'-',
		{
			xtype: 'button',
			accion: 'EliminarFila',
			iconCls:'btnEliminarIconCls',
			scale: 'medium',
			tooltip : 'Eliminar',
			hidden:!permisoEliminar,
			disabled:!permisoEliminar
		}
	],
	columns:[],

	initComponent: function() {	
		//console.log('initComponent: GridBusqueda',this);
		this.callParent(arguments);
	}
});