Ext.define('FMX.view.centro.Visualizador.WindowCompartirDocumento',{
	extend: 'Ext.window.Window',
	alias: 'widget.windowcompartirdocumento',
	title: 'Compartir Documento',
	height: 450,
	width: 500,
	layout: 'anchor',
	draggable: true,
	resizable: false,
	closeAction : 'hide',
//	autoScroll: true,
	
//	minimizable: true,
	constrain:true,
	items:[{
		xtype:'fieldset',
	   	cls:'fieldSetNoBorder',
		width:'100%',
		layout: 'hbox',
		margin:'10 0 0 0',
		items:[{
			xtype:'fieldset',
			cls:'fieldSetNoBorder',
			width:'100%',
	    	items:[{
	    		xtype: 'label',
	    		shadow: true,
	    		html:'Documento: '		
	    	},{
	    		xtype: 'label',
	    	    componentCls: 'textoEncabezadoDocumentoCompartido',
	    	    accion: 'NombreDocumento',
	    	    shadow: true
	    	}]
		},{
			xtype:'fieldset',
			cls:'fieldSetNoBorderRight',
	    	width:'100%',
	    	items:[{
	    		xtype: 'label',
	    		shadow: true,
	    	    html:'Estatus: '
	    	},{
				xtype: 'label',
				cls:'labelEstatusDocumentoCompartido',
				accion: 'EstatusDocumento',
	     		shadow: true		
	    	}]
	    }]
	},{
		xtype:'fieldset',
		cls:'fieldSetNoBorderRight',
		layout:'fit',
		margin:'5 0 0 0',
		items:[{
			xtype: 'textareafield',
			readOnly: true,
			bodyPadding: 10,
			accion: 'MostrarLiga'
		}]
	},{
		xtype:'fieldset',
		cls:'fieldSetNoBorderRight',
		layout:'anchor',
		margin:'5 10 0 0',
		items:[{
			xtype: 'button',
			iconCls:'btnCopiarIcon',
			text:'Copiar',
			align:'right',
		    scale: 'medium',
			accion: 'CopiarLiga'
		}]
	},{
		xtype: 'checkboxfield',
		boxLabel  : 'Compartir',
		name      : 'chbCompartirDocumento',
//		inputValue: '0',
		margin:'0 0 0 30',
		accion: 'CompartirDocumento'
	},{
		xtype: 'form',
		accion: 'ConfiguracionesLiga',
		bodyStyle: 'background-color:#dfe8f5;',
		border:0,
		items:[{
			xtype:'fieldset',
			accion: 'PropiedadesLiga',
			title:'Propiedades de la liga',
			cls:'fieldSetExpira',
			items:[{
				xtype:'fieldset',
				cls:'fieldSetNoBorderRight',
				layout:'hbox',
				items:[{
					xtype: 'datefield',
					fieldLabel: 'Fecha',
					labelPad:0,
					labelWidth:80,
					width:180,
					name: 'fechaExpira',
					margin:'5 35 0 0',
					accion: 'FechaDeVencimiento',
//					editable:false,
					minValue: new Date()
				},{
					xtype: 'checkboxfield',
					boxLabel  : 'Enviar por e-mail',
					hidden: true,
					margin:'5 35 0 0',
					accion: 'EnviarLigaPorCorreo'
				}]
			},{
				xtype:'fieldset',
				cls:'fieldSetNoBorderRight',
				layout:'hbox',
				items:[{
					xtype: 'spinnerfield',
					fieldLabel:'Hora',
					labelWidth:80,
					width:180,
//					editable:false,
					allowBlank : false,
					value:'00:00',
					vtype: 'time',
					accion: 'HoraDeVencimiento',
					margin:'5 35 0 0'
				},{
					xtype: 'checkboxfield',
					boxLabel  : 'Descargar en zip',
					name      : 'chbPermitirZip',
//					inputValue: '0',
					margin:'5 35 0 0',
					accion: 'PermitirDescargarZip'
				}]
			},{
				xtype: 'label',
				accion: 'TextoInformativo',
				componentCls:'textoInformativoDocumentoCompartido',
				formBind: true,
//				shadow:true,
				margin:'10 0 0 10'

			}]
		},{
			xtype: 'button',
			text:'Guardar',
			scale: 'large',
			formBind: true,
			iconCls:'btnGuardar',
			accion: 'GuardarCambios',
			margin:'0 0 0 385'
		}]
	}],
	
	initComponent: function() {
		var me = this;
//		var tpl = new Ext.XTemplate(
//			'<tpl for=".">',
//			'<div class="miniatura" id="{pagina}" align="center">',
//			(!Ext.isIE6? '<img width="65" height="91" src="{imagen}" />' :
//			'<div style="width:99px;height:95px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'{imagen}\',sizingMethod=\'scale\')"></div>'),
//			'<br /><span>Pagina: {pagina}</span><br/>',
//			'</div>',
//			'</tpl>'
//		);
		
		var timeTest = /^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/i;
		Ext.apply(Ext.form.field.VTypes, {
		    time: function(val, field) {
		        return timeTest.test(val);
		    },
		    timeText: 'Tiempo no valido.  Formato de 24 horas requerido.',
		    timeMask: /[\d\s:amp]/i
		});

		Ext.apply(me, {
			store: Ext.create('FMX.store.Visualizador.DocumentosCompartidos')
		});

		me.callParent(arguments);
	}
});