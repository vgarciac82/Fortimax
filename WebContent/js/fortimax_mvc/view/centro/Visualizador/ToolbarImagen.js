Ext.define('FMX.view.centro.Visualizador.ToolbarImagen', {
	extend:'Ext.toolbar.Toolbar',
	alias: 'widget.toolbarimagen',
	region: 'north',
	height:40,
	border:0,
	hidden:externo,
	cls:'toolbarVisorCls',
	
	initComponent: function() {
		var me = this;

		Ext.apply(me, {
			items:[{ 
				xtype: 'tbspacer',  
				width: 50 
			},{
				scale: 'medium',
				iconAlign: 'top',
				iconCls:'btnEscanearIcon',
				//hidden:visualizadorCompartido, //TODO
				accion: 'EscanearDocumento'
			},
				'-'
			,{
				scale: 'medium',
				iconAlign: 'top',
				iconCls:'btnAgregarImagenIcon',
				accion: 'AgregarImagen'
				//hidden:visualizadorCompartido, //TODO
			},
				'-'
			,{
				scale: 'medium',
				iconAlign: 'top',
				iconCls:'btnImprimirIcon',
				accion: 'ImprimirDocumento'
					//hidden:visualizadorCompartido, //TODO
			},
			{ 
				xtype: 'tbspacer',  
				width: 50 
			},
			{
				scale: 'medium',
				iconAlign: 'top',
				iconCls:'btnInicioIcon', 
				accion: 'PaginaInicial'
			},{
				scale: 'medium',
				iconAlign: 'top',
				iconCls:'btnAnteriorIcon',
				accion: 'PaginaAnterior'
			},
				'-'
			,{
				xtype: 'textfield',
				width:30,
				maxLength:4,
				enforceMaxLength:true,
				value:'1',
				regex: /^[0-9]+$/,
				maskRe: /^[0-9]+$/,
				accion: 'PaginaActual'
			},{
				xtype: 'label',
				accion: 'CantidadPaginas',
				shadow:true
			},
				'-'
			,{
				scale: 'medium',
				iconAlign: 'top',
				iconCls:'btnSiguienteIcon',
				accion: 'PaginaSiguiente'
			},{
				scale: 'medium',
				iconAlign: 'top',
				iconCls:'btnFinalIcon',
				accion: 'PaginaFinal'
			},
				'-'
			,{
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnZoomOutIcon',
				accion: 'AlejarImagen'
			},{
				xtype: 'slider',
				width: 75,
				minValue: 25,
				maxValue: 400,
				labelSeparator:'',
				increment: 1,
				labelWidth:50,
				labelAlign :'bottom',
				labelCls:'slideZoomLabelCls',
				value: 100,
				accion: 'ZoomImagen'
			},{
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnZoomInIcon',
				accion: 'AcercarImagen'
			}, '-', {
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnRotarIzquierdaIcon',
				accion: 'RotarIzquierdaImagen'
			},{
				xtype: 'slider',
				width: 75,
				minValue: -180,
				maxValue: 180,
				increment: 1,
				labelSeparator:'',
				labelWidth:50,
				labelAlign :'bottom',
				labelCls:'slideRotarLabelCls',
				value: 0,
				accion: 'RotarImagen'
			},{
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnRotarDerechaIcon',
				accion: 'RotarDerechaImagen'
			},
			'-'
			,{
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnAjustarIcon',
				enableToggle: true,
                toggleGroup: 'ajustar',
				accion: 'AjustarAreaImagen'
			},{
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnAjustarAnchoIcon',
				enableToggle: true,
                toggleGroup: 'ajustar',
				accion: 'AjustarAnchoImagen'
			},{
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnAjustarAltoIcon',
				enableToggle: true,
				toggleGroup: 'ajustar',
				accion: 'AjustarAltoImagen'
			},
			{
				scale: 'small',
				iconAlign: 'top',
				iconCls:'btnRestaurarTamanoOriginalIcon',
				enableToggle: true,
                toggleGroup: 'ajustar',
				accion: 'MostrarTamanoOriginalImagen'
			},
			{ 
				xtype: 'tbspacer',  
				width: 50 
			},{
//				hidden: false, //ifimax,
				iconCls: 'btncompartir',
				scale: 'medium',
				accion: 'CompartirDocumento'
			},{
				//hidden: !ifimax,
				iconCls: 'btnlimpiardocumento',
				scale: 'medium',
				accion: 'LimpiarDocumento'
			},
			{
					scale: 'medium',
					iconAlign: 'top',
					//hidden:visualizadorCompartido, TODO
					iconCls:'btnEliminarPaginaIcon',
					accion: 'EliminarPagina'
			}]
		});
		
        me.callParent(arguments);
    }
});