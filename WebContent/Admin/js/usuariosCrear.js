Ext.require([
     'Ext.form.*',
     'Ext.tip.*'
]);

var passDeshabilitado = true;

if(action == "update" && select!=""){
	action = "editusuario"; 
	passDeshabilitado = false; 
}else{
	action="createusuario";
}

Ext.onReady(function() {
	
		/**
		 *  Se define el MODELO. Hasta el momento no se utiliza!
		*/
        Ext.define('usuario_model', {
            extend : 'Ext.data.Model',
            fields : [ 
                {name : 'nombre',                   type : 'string'},
                {name : 'apellido_paterno',         type : 'string'},
                {name : 'apellido_materno',         type : 'string'},
                {name : 'descripcion',              type : 'string'},
                {name : 'genero',                   type : 'string'},
                {name : 'fecha_nac',                type : 'date'},
                {name : 'correo',                   type : 'string'}, 
                {name : 'nombre_usuario',           type : 'string'}, 
                {name : 'cuota_autorizada',        	type : 'int'}, 
                {name : 'pregunta_secreta',         type : 'string'},
                {name : 'respuesta_secreta',        type : 'string'},
                {name : 'cdg',                      type : 'string'},
                {name : 'cdg_confirm',              type : 'string'},
                {name : 'tipo_usuario',             type : 'string'},
                {name : 'activo',            		type : 'string'},
                {name : 'nivel',            		type : 'string'}
            ]
        });

        /**
         * Modelo para los Catalogos con los que se cargan los ComboBox 
         */
        Ext.define('Catalogo', {
            extend: 'Ext.data.Model',
            fields: [
                     {type: 'string', name: 'ID'},
                     {type: 'string', name: 'Valor'}
            ],
            idProperty:'ID'
        });

        /**
         * Modelo para las variables de entorno 
         */
        Ext.define('VariablesEntorno', {
            extend: 'Ext.data.Model',
            fields: [
                     {type: 'string', name: 'ID'},
                     {type: 'string', name: 'category'},
                     {type: 'string', name: 'name'},
                     {type: 'string', name: 'value'},
                     {type: 'string', name: 'description'}
            ],
            idProperty:'ID'
        });
        
        /**
         * Funcion que cada combobox invoca para obtener su catalogo 
         */
        function getCatalogo(catalogToSelect){

        	var catalogoStore = null;
        	
        	var catalogoStoreAux = Ext.create('Ext.data.Store', {
                model: 'Catalogo',
                sorters: [
                          {
                              property : 'ID',
                              direction: 'ASC'
                          }
                ],
                proxy: {
                	limitParam: undefined,
                    startParam: undefined,
                    paramName: undefined,
                    pageParam: undefined,
                    //noCache: false,
                    type: 'ajax',
                    url: link_opGav_servlet,
                    extraParams:{
                    	action: 'getcatalogo',
                    	select: catalogToSelect
                    },
                    actionMethods: 'POST',
                    reader: {
                        type: 'json',
                        root: 'catalogo'
                    }
                }
            });
        	
        	catalogoStoreAux.load();
        	
        	catalogoStore = catalogoStoreAux;
        	
        	catalogoStoreAux = null;
        	
        	return catalogoStore;
        }

        var passMinLength;
        var passMaxLength;
        var passMinMayus;
        var passMaxMayus;
        var passMinDigitos;
        var passMaxDigitos;
        var regexTextPassReglas;
        var paramsPasswordStore = Ext.create('Ext.data.Store', {
	            model: 'VariablesEntorno',
	            proxy: {
	            	limitParam: undefined,
	                startParam: undefined,
	                paramName: undefined,
	                pageParam: undefined,
	                //noCache: false,
	                type: 'ajax',
	                url: link_opGav_servlet,
	                extraParams:{
	                	action: 'getVariablesEntorno'
	                },
	                actionMethods: 'POST',
	                reader: {
	                    type: 'json',
	                    root: 'Parametros'
	                }
	            }	            
	        });
		        
		paramsPasswordStore.load({
			callback: function() {
				//TODO: automatizar con arreglos.
			    passMinLength 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.minlongitud').get('value');
			    passMaxLength 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.maxlongitud').get('value');
			    passMinMayus 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.minmayusculas').get('value');
			    passMaxMayus 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.maxmayusculas').get('value');
			    passMinDigitos	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.mindigitos').get('value');
			    passMaxDigitos	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.maxdigitos').get('value');
			    regexPassword 	= new RegExp("^(?=[\\w\\x2E\\x2D]{"+passMinLength+"," +passMaxLength+ "})(?=([0-9a-z_]*[A-Z][0-9a-z_]*){" +passMinMayus+ "," +passMaxMayus+ "})(?=([A-Za-z_]*[0-9][A-Za-z_]*){"+passMinDigitos+","+passMaxDigitos+"})[\\w\\x2E\x2D]*$");
			    regexTextPassReglas =  'Longitud: De '+ passMinLength + ' a ' + passMaxLength + ' caracteres.<br>Mayusculas: De ' + passMinMayus + ' a ' +  passMaxMayus + '.<br>Digitos: De '+passMinDigitos+' a '+passMaxDigitos+'.';
			    txt_cdg.regex = regexPassword;
			    txt_cdg.regexText = regexTextPassReglas;
			}
		});
	        
		function LlenarCampos(usuario)
		{          				
			  txt_nombre.setValue(usuario.nombre);
			  txt_apellido_paterno.setValue(usuario.apellido_paterno);
			  txt_apellido_materno.setValue(usuario.apellido_materno);
			  txt_descripcion.setValue(usuario.descripcion);
			  txt_genero.setValue(usuario.genero);
			  date_fecha_nac.setValue(usuario.fecha_nac);
			  txt_correo.setValue(usuario.correo);
			  txt_nombre_usuario.setValue(usuario.nombre_usuario);
			  number_cuota_autorizada.setValue(usuario.cuota_autorizada);
			  txt_pregunta_secreta.setValue(usuario.pregunta_secreta+'');
			  txt_respuesta_secreta.setValue(usuario.respuesta_secreta);
			  txt_tipo_usuario.setValue(usuario.tipo_usuario+'');
	          txt_activo.setValue(usuario.activo+'');
	          txt_administrador.setValue(usuario.administrador+'');
		}
		
		/** 
		 * Se añade el VType 'avanzado' para los passwords.
		 * Se sobreescriben los textos error de las funciones de valicacion básicas: alpha, alphanum, email, url
		 * */
	    Ext.apply(Ext.form.field.VTypes, {
	        password: function(val, field) {
	            if (field.initialPassField) {
	                var pwd = field.up('form').down('#' + field.initialPassField);
	                return (val == pwd.getValue());
	            }
	            return true;
	        },
	        dateText: 'wdfw',
	        passwordText: 'Passwords no coinciden.',
	        emailText : 'Este campo debe tener un e-mail con el formato: usuario@ejemplo.com',
	        alphaText: 'Este campo sólo debe contener letras y _',
	        alphanumText : 'Este campo sólo debe contener letras, números y _',
	        urlText: 'Este campo debe ser un URL con el formato: http:/'+'/www.example.com'
	    });

	    /**
	     * Se crean los campos
	     * */
		var txt_nombre = Ext.create('Ext.form.field.Text', {
                width: 450,
                fieldLabel : 'Nombre(s)',
                name : 'nombre',
                id: 'nombre',
                emptyText: 'Nombre',
                allowBlank : false
        });
        var txt_apellido_paterno = Ext.create('Ext.form.field.Text', {
                width: 300,
                fieldLabel : 'Apellido paterno',
                name : 'apellido_paterno',
                emptyText: 'Apellido Paterno',
                allowBlank : false
        });
        var txt_apellido_materno = Ext.create('Ext.form.field.Text', {
                width: 300,
                fieldLabel : 'Apellido materno',
                name : 'apellido_materno',
                emptyText: 'Apellido Materno'
        });
        var txt_descripcion = Ext.create('Ext.form.field.TextArea', {
                width: 400,
                fieldLabel : 'Descripcion',
                name : 'descripcion',
                emptyText: 'Nombre de pila o descripicion breve.',
                allowBlank : true
        });
        var txt_genero = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel : 'Genero',
            name : 'genero',
            xtype: 'combobox',
            store: [
                    ['m','Masculino'],
                    ['f','Femenino']
                ],
            forceSelection: true,
            editable: false
    	});
        var date_fecha_nac = Ext.create('Ext.form.field.Date', {
    			value: new Date(),
                fieldLabel : 'Fecha de nacimiento',
                name : 'fecha_nac',
                alowBlank : false,
                format : 'd/m/Y',
                editable: true
        });
        var txt_correo = Ext.create('Ext.form.field.Text', {
                width: 400,
        		fieldLabel: 'Especifique una direccion de email.',
                vtype: 'email',  
                fieldLabel : 'Correo',
                name : 'correo',
                id: 'correo',
                emptyText: 'usuario@ejemplo.com'
        });
        var txt_nombre_usuario = Ext.create('Ext.form.field.Text', {
        		width: 300,
                fieldLabel : 'Nombre de usuario',
                name : 'nombre_usuario',
                emptyText: 'Nombre_de_Usuario',
                allowBlank : false
        });
        var number_cuota_autorizada = Ext.create('Ext.form.field.Number', {
        		minText: 'La cuota mínima es de 1MB',
        		width: 200,
                value: 50,
                minValue: 1,
                allowDecimals: false,
                xtype: 'numberfield',
                align: 'right',
                fieldLabel : 'Espacio de almacenamiento (MB)',
                name : 'cuota_autorizada',
                id : 'cuota_autorizada',
                allowBlank : false
        });
        var txt_pregunta_secreta = Ext.create('Ext.form.field.ComboBox', {
        		width:400,
                fieldLabel : 'Pregunta secreta',
                name : 'pregunta_secreta',
                id: 'pregunta_secreta',
                editable: false,
                allowBlank : false,
                xtype: 'combobox',
                typeAhead: false,
                triggerAction: 'all',
                selectOnTab: true,
                queryMode: 'remote',
                //queryParam: 'select',
                //allQuery: 'pregunta_secreta',
                store: getCatalogo('pregunta_secreta'),
                displayField: 'Valor',
                valueField: 'ID',
                forceSelection: true
        });
        var txt_respuesta_secreta = Ext.create('Ext.form.field.Text', {
        		width: 350,
                fieldLabel : 'Respuesta secreta',
                name : 'respuesta_secreta',
                emptyText: 'Respuesta Secreta',
                allowBlank : false
        });
        var txt_cdg = Ext.create('Ext.form.field.Text', {
        		width: 300,
                fieldLabel : 'Contraseña',
                inputType: 'password',
                disabled: passDeshabilitado,
                name : 'cdg',
                id: 'cdg',
                regex: /^[A]$/,
                regexText : regexTextPassReglas
        });
        var txt_cdg_confirm = Ext.create('Ext.form.field.Text', {
        		inputType: 'password',
        		vtype: 'password',
        		disabled: passDeshabilitado,
        		width: 300,
                fieldLabel : 'Confirmar contraseña',
                name : 'cdg_confirm',
                id : 'cdg_confirm',
                initialPassField: 'cdg' // id del campo password inicial
        });
        var txt_tipo_usuario = Ext.create('Ext.form.field.ComboBox', {
            	fieldLabel : 'Tipo de usuario',
                name : 'tipo_usuario',
                allowBlank : false,
                xtype: 'combobox',
                typeAhead: true,
                selectOnTab: true,
                triggerAction: 'all',
                queryMode: 'remote',
                //queryParam: 'select',
                //allQuery: 'tipo_usuario',
                store: getCatalogo('tipo_usuario'),
                displayField: 'Valor',
                valueField: 'ID',
                forceSelection: true,
                editable: false	
   		 });
        var txt_activo = Ext.create('Ext.form.field.ComboBox', {
        	fieldLabel : 'Estado',
            name : 'activo',
            allowBlank : false,
            xtype: 'combobox',
            typeAhead: true,
            selectOnTab: true,
            triggerAction: 'all',
            queryMode: 'remote',
            //queryParam: 'select',
            //allQuery: 'tipo_usuario',
            store: getCatalogo('activo'),
            displayField: 'Valor',
            valueField: 'ID',
            forceSelection: true,
            editable: false
		 });
        var txt_administrador = Ext.create('Ext.form.field.ComboBox', {
        	fieldLabel : 'Cuenta',
            name : 'administrador',
            allowBlank : false,
            xtype: 'combobox',
            typeAhead: true,
            selectOnTab: true,
            triggerAction: 'all',
            queryMode: 'remote',
            //queryParam: 'select',
            //allQuery: 'tipo_usuario',
            store: getCatalogo('nivel'),
            displayField: 'Valor',
            valueField: 'ID',
            forceSelection: true,
            editable: false	
		 });
        
        var btnGuardar = Ext.create('Ext.button.Button',{
        	text : 'Guardar',
        	iconCls: 'save',
        	margin:'2 50 5 500',
        	//width: '50%',
        	disabled : false,
        	scale: 'large',
        	formBind : true,
        	handler : function() {
        		var forma = panel.getForm();
        		if(forma.isValid()){
        			panel.el.mask('Espere por favor...', 'x-mask-loading');
        			var jsonData = Ext.JSON.encode(forma.getValues());
        			Ext.Ajax.request({
        				url: link_opGav_servlet+'?action='+action,
        				method: 'POST',
        				headers: {
        					'Content-Type':'application/json'
        				},
        				params: {  
        					usuariomodel: jsonData
        				},
        				failure: function(response, options){
        					var textObject = Ext.decode(response.responseText);
        					Ext.Msg.alert('Error al crear usuario', textObject.message);
        				},
        				success: function(response, options){
        					var textObject = Ext.decode(response.responseText);
        					if(textObject.success==true){
        						Ext.Msg.alert('Usuario creado', textObject.message);
        					}else{
        						Ext.Msg.alert('Error al crear usuario', textObject.message);
        					}
        				},
        				callback: panel.el.unmask()
        			});

        		}else{
        			Ext.Msg.show({
        				title: 'Advertencia',
        				msg:'Datos incompletos',
        				buttons: Ext.Msg.OK,
        				icon: Ext.MessageBox.WARNING
        			});
        		}
        	}
        });
        
    	/** 
         * Se crea el FieldSet o grupo de campos
         * */
        var pantalla=Ext.getBody().getViewSize();
        var group = Ext.create('Ext.form.FieldSet', {
                xtype : 'fieldset',
                title : context_title,
                collapsible : false,
                autoScroll : false,
                //width : '90%',
                //height : 850,
                margin : {
                        top : 0,
                        right : 10,
                        bottom : 10,
                        left : 10
                },
                defaults : {
                        labelWidth : 89,
                       // anchor: '75%',
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
                         
						{
						    xtype: 'fieldset',
						    title: 'Informacion del Usuario',
						    defaultType: 'textfield',
						    layout: 'anchor',
						    width:(pantalla.width*.75),
						    collapsible : true,
						    //anchor: '-50',
						    items: [
						    	  txt_nombre,
		                          txt_apellido_paterno,
		                          txt_apellido_materno,
		                          txt_descripcion,
		                          txt_genero,
		                          date_fecha_nac,
		                          txt_correo
						    ]
						},
						{
						    xtype: 'fieldset',
						    title: 'Informacion de la Cuenta',
						    defaultType: 'textfield',
						    layout: 'anchor',
						    width:(pantalla.width*.75),
						    collapsible : true,
						    //anchor: '-50',
						    items: [
								txt_nombre_usuario,
								number_cuota_autorizada,
								txt_pregunta_secreta,
								txt_respuesta_secreta,
								txt_cdg,
								txt_cdg_confirm
						    ]
						},
						{
						    xtype: 'fieldset',
						    title: 'Privilegios de la Cuenta',
						    defaultType: 'textfield',
						    layout: 'anchor',
						    width:(pantalla.width*.75),
						    collapsible : true,
						    //anchor: '-50',
						    items: [
								txt_tipo_usuario,
								txt_activo,
								txt_administrador
						    ]
						},
						btnGuardar
						
                ]
        });

        /**
         * Crea el panel que contiene al grupo de campos y el boton
         */
        var panel = Ext.create('Ext.form.Panel', {
        		//buttonAlign: 'right' ,
                title : context_title,
                frame : true,
                width : '100%',
                autoScroll : true,
                margin : {
                        top : 0,
                        right : 0,
                        bottom : 10,
                        left : 0
                },
                layout : {
                        defaultMargins : {
                                top : 0,
                                right : 0,
                                bottom : 10,
                                left : 10
                        },
                        type : 'hbox'
                },
                fieldDefaults: {
                	blankText : 'Campo requerido.',
                    msgTarget: 'under',
                    autoFitErrors: false
                },
                items : [ group ],
                renderTo : Ext.getBody()
        });
        
        /**
         * Se crean los tooltips para los campos
         * */
    	var tooltips = [{
    		target: 'cuota_autorizada',
    		html: 'Espacio de almacenamiento habilitado para el usuario.',
    		trackMouse: true,
    		width: 210
    	},{
    		target: 'cdg',
    		html: 'Dejar en blanco si no pretende modificar la contraseña.',
    		trackMouse: true,
    		width: 210
    	},{
    		target: 'cdg_confirm',
    		html: 'Dejar en blanco si no pretende modificar la contraseña.',
    		trackMouse: true,
    		width: 210
    	},{
    		target: 'activo',
    		html: 'Define si el usuario es miembro activo de Fortimax.',
    		trackMouse: true,
    		width: 210
    	}];

    	Ext.each(tooltips, function(config) {
    		Ext.create('Ext.tip.ToolTip', config);
    	}); 
    	
        /** 
         * Se llama al Servlet en caso de ACTUALIZAR usuario.
         * 
         * NOTA: Lo colocamos despues de que se crea el Panel pues de otra manera en Chrome
         * se presentan problemas a la hora de setear los valores en los combobox
         * obtenidos del JSON (proveniente de OperacionesGavetaServlet).
         * 
         * De esta manera primero se crea el Panel con sus combobox donde, para obtener 
         * su catalogo, cada uno invoca la funcion getCatalogo (la cual crea un Store 
         * que obtiene un JSON de OperacionesGavetaServlet).
         * 
         * Ya con sus store establecidos, si se pretende modificar un usuario, se hace 
         * la siguiente petición Ajax. Al obtener el JSON con los datos del usuario, se 
         * invoca LlenarCampos para setear valores.
         * 
         * */
        
        if(action == "editusuario"){
			Ext.Ajax.request({
				url: link_opGav_servlet,
				method: 'POST',
				timeout: 30000,  
				params: {  
					action: 'getusuario',
					select: select
				 },
				success: function(response, request) {
			    	var data = Ext.JSON.decode(response.responseText);				
			    	LlenarCampos(data.usuario);
			    },
				failure: function(response, request) {
			        Ext.Msg.show({
			            title: 'Error',
			            msg: 'Datos no cargados. Status:' + response.status,
			            buttons: Ext.Msg.OK,
			            animEl: 'elId',
			            icon: Ext.MessageBox.ERROR
			        });
			    }
			});
        }
});