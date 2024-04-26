Ext.define('FMX.view.IfimaxDocumentos.FieldSetTextoDocumento', {
	extend: 'Ext.form.FieldSet',
	cls:'fieldSetNoBorder',
	margin: '5 5 5 5',
	items:[{
		xtype: 'fieldset',
		title: 'Descripción',
		width: 300,
		height: 50,
		margin: '5 0 0 0',
		items:{
			xtype: 'label',
			accion: 'labelDescripcion',
			text: ''
		}
	},{
		xtype: 'fieldset',
		title: 'Estatus',
		width: 300,
		height: 50,
		margin: '5 0 0 0',
		items:{
			xtype: 'label',
			text: 'OK'
		}
	}]
});