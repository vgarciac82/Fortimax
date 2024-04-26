Ext.define('FMX.view.centro.Busqueda.WindowEdicion', {
    extend: 'Ext.window.Window',
    alias : 'widget.windowedicionbusqueda',
    
	title: '[ EDICIÓN ]',
	height: 309,
	width: 500,
	layout: 'fit',
	minimizable: true,
	constrain:true,
	autoScroll:false,
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
			xtype: 'button',
			accion: 'CancelarEdicion',
			iconCls: 'btnCancelarEdicionIconCls',
			scale: 'medium',
			//tooltip: 'Cancelar',
			text: 'Cancelar'
		},
		{
			xtype: 'button',
			accion: 'GuardarEdicion',
			iconCls: 'btnGuardarEdicionIconCls',
			scale: 'medium',
			//tooltip : 'Guardar',
			text : 'Guardar'
			//formBind:true	
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
	],

	initComponent: function() {	
		//console.log('initComponent: WindowEdicion',this);
		this.callParent(arguments);
		}
});