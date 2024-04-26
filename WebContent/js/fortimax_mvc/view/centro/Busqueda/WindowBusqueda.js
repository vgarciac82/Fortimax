Ext.define('FMX.view.centro.Busqueda.WindowBusqueda', {
    extend: 'Ext.window.Window',
    alias : 'widget.windowbusquedabusqueda',
    
	title: 'Buscar/Crear',
	height: 309,
	width: 500,
	layout: 'fit',
	minimizable: true,
	constrain:true,
	autoScroll:false,
	closeAction:'hide',
	tbar:[
			{
				xtype: 'label',
				html:'Transparencia: 100%'		
			},
			{
				xtype: 'multislider',
				accion:'ControlOpacidad',
	   			width: 75,
	   			values: [100],
	   			increment: 1,
		   		minValue: 30,
	 			maxValue: 100,
	 			constrainThumbs: false
		    },
		    '->',
		    {
		    	xtype:'button',
		    	accion:'CrearFila',
				iconCls:'btnCrearIconCls',
				scale:'medium',
				//formBind:true,
				text:'Crear',
				disabled: !permisoCrear
			},
		   	{
		   		xtype:'button',
				accion:'LimpiarCampos',
				iconCls:'btnLimpiarIconCls',
				scale:'medium',
				text:'Limpiar'
			},
		    {
		    	xtype:'button',
		    	accion:'Buscar',
				iconCls:'btnBuscarIconCls',
				scale:'medium',
				text:'Buscar'
			}
	],
	items:[
		{
			xtype:'form',
			autoScroll:true,
			cls:'form'
		}
	],
	bbar:[
		{
			xtype: 'triggerfieldfiltro',
			fieldLabel:'Refinar',
			disabled: true
		}
	],

	initComponent: function() {	
		//console.log('initComponent: WindowBusqueda',this);
		this.callParent(arguments);
	}
});