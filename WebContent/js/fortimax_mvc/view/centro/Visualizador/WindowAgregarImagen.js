Ext.define('FMX.view.centro.Visualizador.WindowAgregarImagen',{
	extend: 'Ext.window.Window',
	alias: 'widget.windowagregarimagen',
	title: 'Agregar Imagen',
	height: 500,
	constrain: true,
	width: 500,
	layout: 'fit',
	draggable: true,
//	closeAction : 'hide',
	resizable: false,
	
	items:[{
		xtype:'tabpanel',
		items:[{
			xtype: 'panel',
			title: 'Seleccion individual',
			autoScroll:true,
			items:[{
				xtype: 'form',
				accion: 'FormAreaCamposArchivo',
				border: 0
			}],
			tbar:[{
				text: 'Agregar fila',
				accion: 'AgregarFila',
				iconCls:'btnAgregarFila'
			},'-'
			,{
				xtype: 'label',
				text: 'Total filas:'
			}],
			bbar:[{
				xtype: 'progressbar',
				width: 250,
		  		value:0,
		  		text: '...'
			},'->',{
				text: 'Enviar',
				accion: 'EnviarImagenes'
			}]
		},{
			xtype: 'panel',
			title: 'Seleccion multiple'
		}]
	}]
});