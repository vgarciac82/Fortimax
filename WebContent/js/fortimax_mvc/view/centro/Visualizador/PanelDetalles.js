Ext.define('FMX.view.centro.Visualizador.PanelDetalles', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.paneldetalles',
	autoScroll: true,
	title: 'Detalles',
	border:false,
	layout:'anchor',
	items:[
		{
			xtype:'fieldset',
			cls:'fieldS',
			title:'Datos generales',
			layout:'anchor',
			items:[ 
				{
					xtype:'gridpanel',
					title:'Detalles',
					cls:'gridDetallesCls',
					width:'100%',
					height:300,
					columnLines: true,
					autoScroll:true,
					tbar:[
						Ext.define('Ext.ux.DownloadButton',{
							text: 'Descargar',
							select: 'select',
							nombreReporte:'Detalles del documento',
							columnaBuscar:null,
							Filtro:'txtFiltro',
							action:'getDoumentInfo',
							icon:'../imagenes/iconos/descargar16.png',
							fortimax:true
						}),
						'->',
						Ext.define('Ext.ux.CustomTrigger', {
							extend: 'Ext.form.field.Trigger',
							alias: 'widget.xtext',
							triggerTip: 'Click para filtrar.',
							triggerBaseCls :'x-form-trigger',
							triggerCls:'x-form-clear-trigger',
							width:300,
							emptyText:'Filtrar...'
						})
					],
					columns:[
						{
							xtype: 'rownumberer',
							width:25,
							tdCls: 'numero',
							width:60,
							header:'ID',
							align:'left'
						},
		         		{
		         			header: 'Nombre',
		         			dataIndex: 'nombre',
		         			flex:1
		         		},
		         		{
		         			header: 'Valor',
		         			dataIndex: 'valor',
		         			flex:3
		         		}
		         	]
				}
			]
		},
		{
			xtype:'fieldset',
			title:'Atributos',
			cls:'fieldS',
			//disabled: true,
			//disabledCls:'disabledCls',
			items: [
				{
					xtype:'form',
					cls:'form',
					tbar: [
						{
							xtype: 'button',
							text: 'Exportar',
							iconCls:'btnExportar',
							disabled:true
						},
						'->',
						{
							xtype: 'button',
							text: 'Editar',
							accion: 'EditarAtributosDocumento',
							enableToggle: true,
							iconCls:'btnEditar'
						},
						' ',
						{
							xtype: 'button',
							text: 'Guardar',
							accion: 'GuardarAtributosDocumento',
							iconCls:'btnGuardar',
							disabled : true
						}
					]
				}
			]
		},
		{
			xtype:'fieldset',
			title:'Historico',
			cls:'fieldSetHistoricoCls',
			items: [
				{
					xtype:'gridpanel',
					title: 'Historico',
					cls:'grdHistorico',
					height: 400,
					width: '100%',
					autoScroll:true,
					columns: [
						{ 
							header: 'Version',
							dataIndex: 'Id_Version',
							width:50
						},
				    	{
				    		header: 'Fecha',
				    		dataIndex: 'Fecha_generacion',
				    		flex: 1 
				    	},
				    	{
				    		header: 'Nombre',
				    		dataIndex: 'Nombre_Documento',
				    		flex:1
				    	},
				    	{
				    		header: 'Descripcion',
				    		dataIndex: 'Descripcion',
				    		flex:2
				    	},
				    	{
				    		header: 'Paginas',
				    		dataIndex: 'numero_paginas',
				    		width:50
				    	},
				    	{
				    		header: 'Tamaño',
				    		dataIndex: 'tamano',
				    		width:80
				    	},
				    	{
				    		header: 'Usuario',
				    		dataIndex: 'usuario_generador',
				    		flex:1
				    	},
				    	{
				    		xtype:'actioncolumn',
				    		width:50,
				    		align:'center',
				    		items: [
				    			{
	                				icon: './imagenes/iconos/documentoHistorico16.png', 
	                				tooltip: 'Descargar'
	            				}
	            			]
	            		}
					]
				}
			]
		}
	],
	
	initComponent: function() {	
		console.log('initComponent: PanelDetalles',this);
		this.callParent(arguments);
	}
	
});