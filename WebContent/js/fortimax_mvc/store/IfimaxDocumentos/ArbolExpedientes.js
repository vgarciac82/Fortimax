Ext.define('FMX.store.IfimaxDocumentos.ArbolExpedientes', {
	extend: 'Ext.data.TreeStore',
	fields: ['ide', 'text', 'nodo', 'tipo'], 
	root: {
		expanded: true,
		children: [{
			text: 'GAVETA01',
			expanded: true,
			tipo: 'Gaveta',
			nodo: 'USR_GRALES', 
			children: [{
				text: "Expediente01",
				leaf: false,
				expanded: true,
				tipo: 'Expediente',
				nodo: 'CORPORACIONES_G33',
				children: [{
					text: "carpeta0101",
					nodo: 'USR_GRALES_G1C1',
					leaf: false,
					expanded: true,
					tipo: '',
					children: [{
						text: "doc-USR_GRALES_G1C1D10",
						leaf: true, tipo: 'Documento',  nodo: "USR_GRALES_G1C1D10"
					},
					{
						text: "doc-USR_GRALES_G1C1D11",
						leaf: true, tipo: 'Documento',  nodo: "USR_GRALES_G1C1D11"
					}]
				},
				{
					text: "doc-USR_GRALES_G1C1D12",
					leaf: true, tipo: 'Documento',  nodo: "USR_GRALES_G1C1D12"
				}]
			}]
		},
		{
			text: 'GAVETA02',
			tipo: 'Gaveta',
			children: [{
				text: "Expediente02",
				leaf: false,
				expanded: true,
				tipo: 'Expediente',
				id: 'EXP2_20160209',
				children: [{
					text: "doc-USR_GRALES_G1C1D13",
					leaf: true, tipo: 'Documento',  nodo: "USR_GRALES_G1C1D13"
				},
				{
					text: "doc-USR_GRALES_G1C1D14",
					leaf: true, tipo: 'Documento',  nodo: "USR_GRALES_G1C1D14"
				}]
			}]
		}]
	}
//	proxy: { 
//		type: 'ajax', 
//		url: './IfimaxDocumentosResourceTest/tree.json',
//		reader: { 										
//			type: 'json', 
//			root: 'expedientes'
//		}
//	}
});