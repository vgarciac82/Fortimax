
Ext.Loader.setConfig({
	enabled: true
});
Ext.Loader.setPath('Ext.ux', '../js/extjs4/ux');

Ext.onReady(function() {
	Ext.QuickTips.init();

	/* 
	 * Store y Modelos
	 */
	Ext.define('MailBasicElementsModel', {
		extend: 'Ext.data.Model',
		fields: [
		         {name: 'mailSending', type: 'string'},
		         {name: 'user', type: 'string'},
		         {name: 'password', type: 'string'},
		         {name: 'server', type: 'string'},
		         {name: 'port', type: 'integer'},
		         {name: 'ssl', type: 'string'}
		         ]
	});

	Ext.define('MailOptionalElementsModel', {
		extend: 'Ext.data.Model',
		fields: [
		         {name: 'from', type: 'string'},
		         {name: 'title', type: 'string'},
		         {name: 'to', type: 'string'},
		         {name: 'cc', type: 'string'},
		         {name: 'cco', type: 'string'},
		         {name: 'subject', type: 'string'},
		         {name: 'message', type: 'string'},
		         {name: 'attachment', type: 'string'},
		         {name: 'received', type: 'string'},
		         {name: 'readed', type: 'string'}
		         ]
	});

	Ext.define('PerfilesModel', {
		extend: 'Ext.data.Model',
		fields: [
		         {name: 'nombre', type: 'string'}
		         ]
	});

	var storeMailBasicElements = Ext.create('Ext.data.Store', {
		model: 'MailBasicElementsModel',
		proxy: {
			type: 'ajax',
			url: rutaServlet,
			reader: {
				type: 'json',
				root: 'config'
			},
			extraParams: 
			{
				action: 'getMailElements',
				select: 'fortimax.correo.conexion'
			},
			noCache: 'false',
			pageParam: 'false',
			limitParam: 'false',
			startParam: 'false'
		}
	});

	//El siguiente store es imposible definirlo antes de seleccionar un elemento del combobox de perfiles.
	//En caso de exisitir una forma de setear el select del proxy de dicho store después de creado, que se implemente.
	var storeMailOptionalElements = null;

	var storePerfiles = Ext.create('Ext.data.Store', {
		model: 'PerfilesModel',
		proxy: {
			type: 'ajax',
			url: rutaServlet,
			reader: {
				type: 'json',
				root: 'lista'
			},
			extraParams: 
			{
				action: 'getPerfilesCorreo',
				select: ''
			},
			noCache: 'false',
			pageParam: 'false',
			limitParam: 'false',
			startParam: 'false'
		},
		autoLoad: false
	});

	var viewSize = Ext.getBody().getViewSize();
	
	var chbEnableMailSending = new Ext.form.Checkbox({  
		id: 'chbEnableMailSending',
		columns:1,
		margin:'2 0 25 10',	    
		boxLabel: '<b>Habilitar envío de correos.</b>', 
		name: 'chbReaded', 
		checked: true,
		listeners: {
	        click: {
	            element: 'el', //bind to the underlying el property on the panel
	            fn: function(){ 
	            	cambiaChbEnableMailSending(); 
	            }
	        }
	    }
	}); 
	
	var lblGrupo= new Ext.form.Label({
		id:'lblGrupo',
		componentCls:'labels',
		html:'Parámetros de configuración para el envío de correo electrónico a través de <b> servidor SMTP</b>:<br><br>',
		shadow:true
	});

	var txtTo = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Destinatario',
		componentCls:'labels',
		width:300,
		margin:'0 0 0 100',
		name:'txtTo',
		emptyText:'Destinatario '
	});

	var txtFrom = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Emisor',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtFrom',
		emptyText:'Remitente '
	});

	var txtCC = Ext.create('Ext.form.field.Text', {
		fieldLabel:'CC',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtCC',
		emptyText:'Copia '
	});

	var txtCCO = Ext.create('Ext.form.field.Text', {
		fieldLabel:'CCO',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtCCO',
		emptyText:'Copia Oculta '
	});

	var lblMessage = new Ext.form.Label({
		fieldLabel:'Mensaje',
		componentCls:'labels',
		html:'Mesaje:<br><br>',
		margin:'0 0 0 100',
		shadow:true
	});
	
	var txtMessage = new Ext.form.HtmlEditor({
		id:'editorTexto',
		width:viewSize.width*(.50),
		height:viewSize.height*(.40),
		margin:'2 0 5 100',
		emptyText:'Escriba aquí su mensaje... '
	});

	var fileAdjunto = new Ext.form.field.File({
		id:'archivo',
		name: 'archivo',
		fieldLabel: 'Archivo adjunto',
		margin:'2 0 5 100',
		//labelWidth: 50,
		msgTarget: 'side',
		emptyText:'',
		cls:'subirArchivoCls',
		width:500,
		buttonText: 'Examinar...',
		listeners:{
			change:function( _this, value, eOpts ){
				//var n = value.substring(value.lastIndexOf("\\") + 1, value.lastIndexOf(".")).substring(0, 32);
				//txtNombre.setValue(n);
			}
		},
		buttonConfig:{
			xtype: "button",
			id: "btnSubirArchivo",
			iconCls:'btnSubirArchivoIconCls'
		}
	});

	var txtUser = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Usuario',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtUser',
		emptyText:'Usuario ',
		allowBlank : false
	});

	var txtPassword = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Contraseña',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtPassword',
		allowBlank : false,
		emptyText:'Contraseña ',
		inputType: 'password',
	});

	var chbReceived = new Ext.form.Checkbox({  
		columns:1,
		margin:'2 0 5 100',	    
		boxLabel: 'Verificar de Recibido', 
		name: 'chbReceived', 
		checked: false 
	}); 

	var chbReaded = new Ext.form.Checkbox({  
		columns:1,
		margin:'2 0 5 100',	    
		boxLabel: 'Verificar de Leído', 
		name: 'chbReaded', 
		checked: false 
	}); 

	var chbSSL = new Ext.form.Checkbox({  
		columns:1,
		margin:'2 0 5 100',	    
		boxLabel: 'Conexión segura (SSL)', 
		name: 'chbReaded', 
		checked: false,
		allowBlank : false
	}); 

	var txtPort = Ext.create('Ext.form.field.Number', {
		fieldLabel : 'Puerto',
		componentCls:'labels',
		name:'txtPort',
		margin:'2 0 5 100',
		width: 160,
        minValue: 0,
        allowDecimals: false,
        emptyText:'587 ',
        allowBlank : false
	});

	var txtServer = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Servidor',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtServer ',
		emptyText:'smtp.domain.net ',
		allowBlank : false
	});

	var txtSubject = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Asunto',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtSubject',
		emptyText:''
	});

	var txtTitle = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Titulo del emisor',
		componentCls:'labels',
		width:300,
		margin:'2 0 5 100',
		name:'txtTitle',
		emptyText:'Título del remitente '
	});

	var cmbPerfiles = Ext.create('Ext.form.field.ComboBox', {
		fieldLabel : 'Perfiles',
		name : 'perfil',
		xtype: 'combobox',
		displayField: 'nombre',
		allowBlank : false,
		store: storePerfiles,
		editable: true,
		margin:'2 0 5 100',
		width:300,
		triggerAction: 'all',
		selectOnTab: true,
		queryMode: 'remote',
		queryParam: false,
		queryCaching: true,
		autoSelect : false,
		allowBlank : false,
		regex: new RegExp("^[a-zA-Z0-9]*$"),
		regexText:'Solo números y letras permitidos.',
		emptyText : 'Seleccionar / crear perfil...',
		listeners: {
			select: function() {
				loadOptionalValues(this.getValue());
			},
			specialkey: function (field, e) {
				if (e.getKey() === e.ENTER)
					loadOptionalValues(this.getValue());
			}
		}
	});

	var btnGuardarCambiosBasico = Ext.create('Ext.button.Button',{
		text: 'Guardar',
		formBind: false,
		iconCls:'save',
		scale: 'large',
		margin:'2 0 5 610',
		handler:function(){
			if(	txtUser.isValid() &&
				txtPassword.isValid() &&
				txtServer.isValid() &&
				txtPort.isValid() &&
				chbSSL.isValid())
			{	
				Ext.Msg.show({
					title:'Guardar',
					msg: '&iquestModificar datos de conexión?',
					buttons: Ext.Msg.OKCANCEL,
					icon: Ext.Msg.QUESTION,
					fn: function(btn){
						if(btn=='ok')
							saveConfigBasico();
					}

				});
			} else {
				Ext.Msg.show({
					title:'Advertencia',
					msg: 'Datos inválidos.',
					buttons: Ext.Msg.OK,
					icon: Ext.Msg.WARNING

				});
			}
		}

	});

	var btnGuardarCambiosOpcional = Ext.create('Ext.button.Button',{
		text: 'Guardar',
		disabled:false,
		iconCls:'save',
		scale: 'large',
		margin:'2 0 5 610',
		handler:function(){
			if(cmbPerfiles.isValid()){
				Ext.Msg.show({
					title:'Guardar',
					msg: storePerfiles.findExact("nombre", cmbPerfiles.getValue())!=-1?'¿Desea sobreescribir datos de perfil?':'¿Guardar nuevo perfil?',
							buttons: Ext.Msg.OKCANCEL,
							icon: Ext.Msg.QUESTION,
							fn: function(btn){
								if(btn=='ok')
									saveConfigOpcional(cmbPerfiles.getValue());
							} 
				});
			} else {
				Ext.Msg.show({
					title:'Advertencia',
					msg: 'Datos inválidos.',
					buttons: Ext.Msg.OK,
					icon: Ext.Msg.WARNING

				});
			}
		}

	});
	
	var btnTestConnection = Ext.create('Ext.button.Button',{
		text: 'Probar conexión',
		scale: 'small',
		margin:'2 0 5 100',
		handler:function(){
			if(txtUser.isValid() && txtPassword.isValid() &&
					txtServer.isValid() && txtPort.isValid() && chbSSL.isValid()){
				if(testConexion()){
					Ext.Msg.show({
						title:'Conexión establecida',
						msg: 'Se estableció exitosamente la conexión con el servidor.',
						buttons: Ext.Msg.OK,
						icon: Ext.Msg.INFO
						
					});
				} else {
					Ext.Msg.show({
						title:'Conexión no establecida',
						msg: 'No se pudo establecer la conexión con el servidor.',
						buttons: Ext.Msg.OK,
						icon: Ext.Msg.WARNING
						
					});
				}				
			} else {
				Ext.Msg.show({
					title:'Datos inválidos',
					msg: 'Verifique los datos ingresados.',
					buttons: Ext.Msg.OK,
					icon: Ext.Msg.WARNING
					
				});
			}
		}
	});
	

	/*
	 * FIELDSET
	 */
	var grupo = Ext.create('Ext.form.FieldSet', {
		xtype : 'fieldset',
		title : 'Servidor de correo',
		collapsible : false,
		autoScroll : false,
		//width : '30%',
		//height : 850,
		margin : {
			top : 0,
			right : 10,
			bottom : 10,
			left : 10
		},
		defaults : {
			labelWidth : 50,
			//anchor: '300',
			layout : {
				type : 'anchor',
				defaultMargins : {
					top : 0,
					right : 10,
					bottom : 10,
					left : 10
				}
			}
		},
		items : [ 
		         lblGrupo,
		         {
		        	 xtype: 'fieldset',
		        	 title: 'Elementos básicos',
		        	 defaultType: 'textfield',
		        	 layout: 'anchor',
		        	 width:(viewSize.width*.63),
		        	 collapsible : true,
		        	 items: [
		        	         chbEnableMailSending,
		        	         txtUser,
		        	         txtPassword,
		        	         txtServer,
		        	         txtPort,
		        	         chbSSL,
		        	         btnTestConnection,
							 btnGuardarCambiosBasico
		        	         ]
		         },
		         {
		        	 xtype: 'fieldset',
		        	 title: 'Elementos opcionales',
		        	 id: 'elemOpcionales',
		        	 defaultType: 'textfield',
		        	 layout: 'anchor',
		        	 width:(viewSize.width*.63),
		        	 collapsible : true,
		        	 //anchor: '-50',
		        	 items: [
		        	         cmbPerfiles,
		        	         txtFrom,
		        	         txtTitle,
		        	         txtTo,
		        	         txtCC,
		        	         txtCCO,
		        	         txtSubject,
		        	         lblMessage,
		        	         txtMessage,
		        	         //fileAdjunto,
		        	         chbReceived,
		        	         chbReaded,
		        	         btnGuardarCambiosOpcional
		        	         ]
		         }
		        ]
	});

	/* 
	 * PANEL PRINCIPAL
	 */	
	
	Ext.create('Ext.form.Panel', {
		//buttonAlign: 'right' ,
		title : 'Configuración de Correo',
		frame : true,
		width : '100%',
		autoScroll : true,
		margin : {
			top : 0,
			right : 0,
			bottom : 0,
			left : 0
		},
		layout : {
			defaultMargins : {
				top : 0,
				right : 0,
				bottom : 0,
				left : 10
			},
			type : 'hbox'
		},
		fieldDefaults: {
			blankText : 'Campo requerido.',
			msgTarget: 'under',
			autoFitErrors: false
		},
		items : [grupo],
		renderTo : Ext.getBody()
	});

	/* 
	 * Funciones
	 */
	
	/**
	 * Establece los valores básicos obtenidos del servidor en los campos de la pantalla
	 */
	function setBasicValues() {
		var conf=storeMailBasicElements.getAt(0);
		if(conf!=null){
			console.info(conf);
			txtUser.setValue(conf.get('user'));
			txtPassword.setValue(conf.get('password'));
			txtServer.setValue(conf.get('server'));
			txtPort.setValue(conf.get('port'));
			chbSSL.setValue(conf.get('ssl'));
			chbEnableMailSending.setValue(conf.get('mailSending'));
			cambiaChbEnableMailSending();
		} else {
			console.log('Sin datos de conexion de servidor de correo en base de datos.');
		}
	}

	/**
	 * Establece los valores opcionales obtenidos del servidor en los campos de la pantalla
	 */
	function setOptionalValues(){
		var conf = storeMailOptionalElements.getAt(0);
		if(conf!=null){
			console.info(conf);
			
			txtFrom.setValue(conf.get('from'));
			txtTitle.setValue(conf.get('title'));
			txtTo.setValue(conf.get('to'));
			txtCC.setValue(conf.get('cc'));
			txtCCO.setValue(conf.get('cco'));
			txtSubject.setValue(conf.get('subject'));
			txtMessage.setValue(conf.get('message'));
			//fileAdjunto.setValue(conf.get('attachment'));
			chbReceived.setValue(conf.get('received'));
			chbReaded.setValue(conf.get('readed'));
		} else { 
			console.log('Sin datos de perfiles de correo en base de datos.'); 
		}
	}


	/**
	 * Hace la petición de los valores opcionales en base a un nombre de perfil.
	 * Crea un store en base a un modelo preestablecido.
	 */
	function loadOptionalValues(perfil){
		storeMailOptionalElements = Ext.create('Ext.data.Store', {
			model: 'MailOptionalElementsModel',
			proxy: {
				type: 'ajax',
				url: rutaServlet,
				reader: {
					type: 'json',
					root: 'config'
				},
				extraParams: 
				{
					action: 'getMailElements',
					select: perfil
				},
				noCache: 'false',
				pageParam: 'false',
				limitParam: 'false',
				startParam: 'false'
			}
		});

		storeMailOptionalElements.load({
			callback: function() {
				setOptionalValues();
			}
		});
	}
	
	/**
	 * Al enviar variables para que el servidor las almacene y retorne mensaje de éxito.
	 */
	var successAjaxFnN = function(response, request) {
		var jsonData = Ext.JSON.decode(response.responseText);
		if (jsonData.success) {
			Ext.Msg.show({
				title: 'Correcto',
				msg: jsonData.message,
				buttons: Ext.Msg.OK,
				icon: Ext.MessageBox.INFO
			});

		} else {
			Ext.Msg.show({
				title: 'Error',
				msg: jsonData.message,
				buttons: Ext.Msg.OK,
				animEl: 'elId',
				icon: Ext.MessageBox.ERROR
			});
		}
	};

	/**
	 * Método que envía los valores básicos al servidor para ser almacenados.
	 */
	function saveConfigBasico(){	
		if(isNaN(parseInt(txtPort.getValue())))
			txtPort.setValue("587");//TODO

		var MailBasicElementsModel = Ext.create('MailBasicElementsModel', {
			mailSending: chbEnableMailSending.getValue(),
			user: txtUser.getValue(),
			password: txtPassword.getValue(),
			server: txtServer.getValue(),
			port: txtPort.getValue(),
			ssl: chbSSL.getValue()
		});
		MailBasicElementsModelJson = Ext.JSON.encode(MailBasicElementsModel.data);			
		Ext.Ajax.request({  
			url: rutaServlet,  
			method: 'POST',  
			success: successAjaxFnN,   
			timeout: 30000,  
			params: {  
				action: 'editMailElements',
				select: "basico",
				model: MailBasicElementsModelJson,
				name: 'fortimax.correo.conexion'
			}});
	}

	/**
	 * Método que envía los valores opcionales al servidor para ser almacenados.
	 */
	function saveConfigOpcional(nombrePerfil){
		var MailOptionalElementsModel = Ext.create('MailOptionalElementsModel', {
			from: txtFrom.getValue(),
			title: txtTitle.getValue(),
			to: txtTo.getValue(),
			cc: txtCC.getValue(),
			cco: txtCCO.getValue(),
			subject: txtSubject.getValue(),
			message: txtMessage.getValue(),
			//attachment: fileAdjunto.getRawValue(),
			received: chbReceived.getValue(),
			readed: chbReaded.getValue()
		});
		MailOptionalElementsModel = Ext.JSON.encode(MailOptionalElementsModel.data);			
		Ext.Ajax.request({  
			url: rutaServlet,  
			method: 'POST',  
			success: successAjaxFnN,   
			timeout: 30000,  
			params: {  
				action: 'editMailElements',
				select: "opcional",
				model: MailOptionalElementsModel,
				name: nombrePerfil
			}});
	}

	/**
	 * Carga el store de elementos básicos después de dibujar la pantalla.
	 * Al cargar correctamente el store, se llama la funcion setBasicValues()
	 */
	storeMailBasicElements.load({
		callback: function() {
			setBasicValues();
		}
	});
	
	/**
	 * Funcion para probar la conexión con el servidor de correo.
	 */
	
	function testConexion(){
		var exito = false;
		Ext.Ajax.request({  
			url: rutaServlet,  
			method: 'POST',  
			success: exito = true,
			timeout: 10000,  
			params: {  
				action: 'mailServerConnectionTest',
				user: txtUser.getValue(),
				password: txtPassword.getValue(),
				server: txtServer.getValue(),
				port: txtPort.getValue(),
				ssl: chbSSL.getValue()
			}});
		return exito;
	}
	
	function cambiaChbEnableMailSending(){
		if(chbEnableMailSending.getValue() == true){
			txtUser.enable();
			txtPassword.enable();
			txtServer.enable();
			txtPort.enable();
			chbSSL.enable();
			btnTestConnection.enable();
			Ext.getCmp('elemOpcionales').enable();
		} else {
			txtUser.disable();
			txtPassword.disable();
			txtServer.disable();
			txtPort.disable();
			chbSSL.disable();
			btnTestConnection.disable();
			Ext.getCmp('elemOpcionales').disable();
		}
	}
});