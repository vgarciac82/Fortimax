Ext.define('FMX.view.centro.Visualizador.PanelMiniaturas', {
	extend: 'Ext.form.Panel',
	alias:'widget.panelminiaturas',
	region: 'east',
	collapsible: true,
	//hidden:externo
	width: 116,
	initComponent: function() {
		var me = this;
		Ext.apply(me, {
			title: 'Páginas',
			layout:'vbox',
			tbar: [{
				 xtype: 'button',
				 text: '10',
				 iconCls:'btnCargarSiguienteMiniaturasCls',
				 accion: 'AgregarSiguientesMiniaturas'						 
			 },{
				 xtype: 'button',
				 text: '*',
				 iconCls:'btnCargarSiguienteMiniaturasCls',
				 accion: 'AgregarTodasMiniaturas'	
			 },'->',{
				 xtype: 'button',
				 iconCls:'btnRefrescarArbolIconCls',
				 accion: 'ActualizarMiniaturas'
			 }],
			items: 
				[
				 Ext.widget('dataviewvisualizadorminiaturas')
				 ]
		});
		this.callParent(arguments); //NOTA: con el me no funcionaba. NO mistraba panel.
	}
});