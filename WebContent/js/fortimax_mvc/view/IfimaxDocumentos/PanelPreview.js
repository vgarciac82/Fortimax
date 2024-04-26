Ext.define('FMX.view.IfimaxDocumentos.PanelPreview', {
	extend: 'Ext.panel.Panel',
	title: 'Previsualizador',
	bodyPadding: 10,
	frame: true,
	layout: 'anchor',
	items: [{
		xtype: 'filefield',
		accion: 'SeleccionarImagenDeDisco',
		name: 'photo',
		fieldLabel: 'Imagen',
		labelWidth: 50,
		msgTarget: 'side',
		allowBlank: true,
		anchor: '100%',
		buttonText: 'Seleccionar imagen...',
		margin: '1 15 10 5'
	}],
	buttons:	[{
		text: 'Eliminar selección',
		accion: 'EliminarMiniaturas'
	},{
		text: 'Agregar a documento',
		accion: 'AgregarMiniaturas',
		handler: function() {
			
		}
	},{
		text: 'Cancelar',
		accion: 'CancelarCambiosMiniaturas'
	}]
});