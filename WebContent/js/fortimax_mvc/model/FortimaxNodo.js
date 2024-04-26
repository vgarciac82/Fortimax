Ext.define('FMX.model.FortimaxNodo', {
	extend: 'Ext.data.Model',
	
	fields: [
		{ name: 'id', type: 'string', defaultValue: null },
		{ name: 'text', type: 'string', defaultValue: null },
		{ name: 'type', type: 'string', defaultValue: null },
		{ name: 'extension', type: 'string', defaultValue: null },
		{ name: 'expanded', type: 'bool', defaultValue: false, persist: false },
		{ name: 'expandable', type: 'bool', defaultValue: true, persist: false },
		{ name: 'checked', type: 'auto', defaultValue: null },
		{ name: 'leaf', type: 'bool', defaultValue: null, persist: false },
		{ name: 'cls', type: 'string', defaultValue: null, persist: false },
		{ name: 'iconCls', type: 'string', defaultValue: null, persist: false },
		{ name: 'allowDrop', type: 'boolean', defaultValue: true, persist: false },
		{ name: 'allowDrag', type: 'boolean', defaultValue: true, persist: false },
		{ name: 'qtip', type: 'string', defaultValue: null, persist: false }
	]
	
});