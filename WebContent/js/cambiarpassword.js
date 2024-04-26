Ext.onReady(function(){
	/*
	 * Variables
	 */
	
		Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
		var ancho=400;
		var alto=270;
		
		var pantalla=Ext.getBody().getViewSize();
		var passMinLength;
        var passMaxLength;
        var passMinMayus;
        var passMaxMayus;
        var passMinDigitos;
        var passMaxDigitos;
        var regexTextPassReglas;
	/*
	* Store y modelos
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
        Ext.define('modeloUsuario', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'nombreUsuario',  	type: 'string'},
        {name: 'cdg',  			type: 'string'}
        
    ]
	});
		var paramsPasswordStore = Ext.create('Ext.data.Store', {
	            model: 'VariablesEntorno',
	            proxy: {
	               
	                type: 'ajax',
	                url: rutaServlet,
	                extraParams:{
	                	action: 'getVariablesEntorno'
	                },
	                actionMethods: 'POST',
	                reader: {
	                    type: 'json',
	                    root: 'Parametros'
	                }
	            },
	            listeners:{
	            	load:function(){
	            		 passMinLength 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.minlongitud').get('value');
			    	passMaxLength 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.maxlongitud').get('value');
			    	passMinMayus 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.minmayusculas').get('value');
			    	passMaxMayus 	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.maxmayusculas').get('value');
			    	passMinDigitos	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.mindigitos').get('value');
			    	passMaxDigitos	= paramsPasswordStore.findRecord('name', 'fortimax.login.password.maxdigitos').get('value');
			    	regexPassword 	= new RegExp("^(?=\\w{"+passMinLength+"," +passMaxLength+ "}\\b)(?=([0-9a-z_\\-\\.]*[A-Z][0-9a-z_\\-\\.]*){" +passMinMayus+ "," +passMaxMayus+ "}\\b)(?=([A-Za-z_\\-\\.]*[0-9][A-Za-z_\\-\\.]*){"+passMinDigitos+","+passMaxDigitos+"}\\b)\\w*$");
			    	regexTextPassReglas =  'Longitud: De '+ passMinLength + ' a ' + passMaxLength + ' caracteres.<br>Mayusculas: De ' + passMinMayus + ' a ' +  passMaxMayus + '.<br>Digitos: De '+passMinDigitos+' a '+passMaxDigitos+'.';
			    	txtNueva.regex = regexPassword;
			    	txtNueva.regexText = regexTextPassReglas;
	            		Ext.getBody().unmask();
	            	}
	            },
	            autoLoad:true
	        });
	/*
	 * Objetos
	 */
		var txtActual=new Ext.form.field.Text({
			id:'txtActual',
			fieldLabel:'Contraseña',
			allowBlank:true,
			cls:'txtActualCls',
			inputType: 'password',
			labelWidth:130
		});
		var txtNueva=new Ext.form.field.Text({
			id:'txtNueva',
			fieldLabel:'Nueva contraseña',
			allowBlank:false,
			cls:'txtNuevaCls',
			inputType: 'password',
			msgTarget:'side',
			autoFitErrors:false,
			labelWidth:130
		});
		var txtNuevaConfirmar=new Ext.form.field.Text({
			id:'txtNuevaConfirmar',
			fieldLabel:'Confirmar contraseña',
			allowBlank:false,
			cls:'txtNuevaCls',
			inputType: 'password',
			vtype: 'password',
			initialPassField: 'txtNueva',
			labelWidth:130
		});
		var btnCambiar=new Ext.button.Button({
			text:'Guardar',
			cls:'btnCambiar',
			formBind:true,
			iconCls:'btnCambiarIconCls',
			handler:function(){
				var pass=hex_md5(txtNueva.getValue());
				cambiarPassword(pass);
			}
		});
		var fieldSetActual=new Ext.form.FieldSet({
			title:'Contraseña actual',
			cls:'fieldSetTextos',
			items:[txtActual]
		});
		var fieldSetNueva=new Ext.form.FieldSet({
			title:'Nueva contraseña',
			cls:'fieldSetTextos',
			items:[txtNueva,txtNuevaConfirmar]
		});
		var fieldSetBoton=new Ext.form.FieldSet({
			cls:'fieldSetBoton',
			items:[btnCambiar]
		});
		var ventana=new Ext.window.Window({
		id:'ventana',
		title:'Cambiar contraseña: '+select,
		width:ancho,
		height:alto,
		closable:false,
		layout: 'anchor',
		autoScroll:false,
		resizable:false,
		items:[		
		{xtype:'form',baseCls:'panelForm',items:[fieldSetActual,fieldSetNueva,fieldSetBoton]}
			],
		listeners:{
			close:function(){
				top.main.location.replace('Bienvenida.jsp');
			}
		}
	}).show();
	/*
	 * Funciones
	 */
	Ext.apply(Ext.form.field.VTypes, {
	        password: function(val, field) {
	            if (field.initialPassField) {
	                var pwd = field.up('form').down('#' + field.initialPassField);
	                return (val == pwd.getValue());
	            }
	            return true;
	        },
	        passwordText: 'Passwords no coinciden.'
	    });

	    var successAjaxFnN = function(response, request) {
		Ext.getBody().unmask();
	    var jsonData = Ext.JSON.decode(response.responseText);
	    if (true == jsonData.success) {
	    	Ext.example.msg("Realizado correctamente",jsonData.message);
	    	if (top == self) 
			{ 
				 window.setTimeout(function() {
    			 window.location.href=basePath;
			}, 2000);
			} else { 
				window.setTimeout(function() {
    			 parent.location.href=basePath;
			}, 2000);
			}
			
           
	    } else {
	        Ext.Msg.show({
	            title: 'Error',
	            msg: jsonData.message,
	            buttons: Ext.Msg.OK,
	            animEl: 'elId',
	            icon: Ext.MessageBox.ERROR
	        });
	    }
	    
	    //field.up('form').reset();
	};

	    function cambiarPassword(pass){
	    	 Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	    	 var datos=new Array();
	    	for(var i=0;i<2;i++){
	    		var pass=i==0?hex_md5(txtActual.getValue()):hex_md5(txtNueva.getValue());
	    		var da=Ext.create('modeloUsuario',{
	    			nombreUsuario:select,
	    			cdg:pass
	    		});
	    		datos.push(da.data);
	    	}
		datos=Ext.JSON.encode(datos);		        	
					Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
			 		success: successAjaxFnN,   
					timeout: 30000,  
					params: {  
					action: 'CambiarPassword',
					datos:datos
					}});
	    }
});