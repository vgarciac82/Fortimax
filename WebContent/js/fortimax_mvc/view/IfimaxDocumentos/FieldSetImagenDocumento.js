Ext.define('FMX.view.IfimaxDocumentos.FieldSetImagenDocumento', {
	extend: 'Ext.form.FieldSet',
	margin: '5 45 5 5',
	cls:'fieldSetNoBorder',
	items:[{
		xtype: 'button',
		iconCls: 'btnFlechaIzquierda',
		accion: 'verImagenAnterior',
		margin: '0 3 140 0'
	},{
		xtype: 'image',
		accion: 'imageDocumento',
		posicion: 0,
		height: 150,
		width: 100
//		src: ''
	},{
		xtype: 'button',
		accion: 'verImagenSiguiente',
		iconCls: 'btnFlechaDerecha',
		margin: '0 0 140 3'
	}]
});