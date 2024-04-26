Ext.define('FMX.view.norte.PanelHerramientasPrincipal', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.PanelHerramientasPrincipal',

	id:'PanelHerramientasPrincipal',
	region: 'north',
	unstyled:true,
	dockedItems: [	
	{
		hidden: false,//ifimax,
		dock: 'bottom',
		xtype: 'toolbar',
		items: [							
			//Alinieador botones der.
			'->',
			
			//Boton Expediente
			Ext.create('Ext.Button', {
			id:'btnExpediente',
			text: 'Expediente',
			iconCls: 'btnExpediente',
			scale: 'medium',
			menu : {
			items: [
					{text:'Crear', iconCls: 'btnMas'},
					{text:'Proteger', iconCls: 'btnProteger'}
					]
			},
				handler: function() {
				//alert('Por implementar !');
						}
			}),
			
			//Boton Carpeta
			Ext.create('Ext.Button', {
			id:'btnCarpeta',
			text: 'Carpeta',
			iconCls: 'btnCarpeta',
			scale: 'medium',
			menu : {
			items: [
						{text:'Crear', iconCls: 'btnMas'},
						{text:'Proteger', iconCls: 'btnProteger'}
					]
			},
			handler: function() {
					//alert('Por implementar !');
					}
			}),
			
			//Boton Documento
			Ext.create('Ext.Button', {
			id:'btnDocumento',
			text: 'Documento',
			iconCls: 'btnDocumento',
			scale: 'medium',
			menu : {
			items: [
					{text:'Crear', iconCls: 'btnMas'},
					{text:'Proteger', iconCls: 'btnProteger'}
					]
			},
			handler: function() {
				//alert('Por implementar !');
					}
			}),
			
	
			//Boton Descargar elemento seleccionado
			Ext.create('Ext.Button', {
			id:'btnDescargar',
			text: 'Descargar',
			iconCls: 'btnDescargar',
			scale: 'medium',
			//renderTo: 'center2',
			handler: function() {
			//Descargar(nodo_select);
					}
			}),
			
			//Boton Respaladar
			Ext.create('Ext.Button', {
			id:'btnRespaldar',
			text: 'Respaldar',
			iconCls: 'btnRespaldar',
			scale: 'medium',
			//renderTo: 'center2',
			handler: function() {
				//Respaldar_carpeta(nodo_select);
					}
			}),
			
			//Boton opciones de Usuario
			Ext.create('Ext.Button', {
			id:'btnOpcionesUsuario',
			text: null,
			iconCls: 'btnEngrane',
			scale: 'medium',
			menu : {
			items: [
					{text:'Cambiar Contraseña', iconCls: 'btnMas'},
					{text:'Guia de uso', iconCls: 'btnProteger'},
					{text:'Utileria de digitalizacion', iconCls: 'btnProteger'}
					]
			},
			handler: function() 
				{

				}
			}),
			
			//Separador y alineadores Boton Salir
			' 		','-','		 ',
			
			//Boton Salir de Fortimax
			Ext.create('Ext.Button', {
			id:'btnSalirFortimax',
			text: null,
			iconCls: 'btnSalir',
			scale: 'medium',
			handler: function() {
				document.location.href= "../jsp/Salida.jsp"
				}
			})					
		]
	}],
	initComponent: function() {	

		this.callParent(arguments);
		}
});