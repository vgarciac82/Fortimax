Ext.onReady(function(){
/*
 * Variables
 */
	var privilegios = Ext.JSON.decode(jsonPrivilegios);
	var actionStore='getDatosDocumento';
	var tiempoRedireccion=1000;
/*
 * Store y modelos
 */
	Ext.define('modeloDetalles', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name:'Nombre',type:'string'},
	     {name: 'Descripcion', type: 'string'}
	    ]
	});
	
	var storeDocumento = new Ext.data.Store({ 
		        model: 'modeloDetalles', 
		        proxy: { 
		            type: 'ajax', 
		            url: rutaServlet,		
		            	reader: { 										
		                type: 'json', 
		                root: 'datos'
		            },
		            extraParams: 
			         {
		            	action: actionStore,
			            select:select
			          } 
		        },
		        listeners:{
			    	 load:function(_this,e){
			    	 	if(_this.getCount()>0){
				    		 lblNombre.setText('<font class="fontTitulo">Nombre: </font><font class="fontTexto">'+_this.getAt(0).get('Nombre')+'</font> <br /><br />',false);
				    		 lblDescripcion.setText('<font class="fontTitulo">Descripcion: </font><font class="fontTexto">'+_this.getAt(0).get('Descripcion')+'</font>',false);
				    		 txtNombre.setValue(_this.getAt(0).get('Nombre'));
				    		 txtDescripcion.setValue(_this.getAt(0).get('Descripcion'));
				    		 
				    		 if(!editable){
				    		 	panelPrincipal.setHeight(325);
				    		 }
				    		 
				    		 panelPrincipal.el.unmask();				    		 
			    	 	}

			    	 }
			     },
		        autoLoad:false
		    });
/*
 * Objetos
 */
	var lblNombre=new Ext.form.Label({
		id:'lblNombre',
		cls:'lblNombreCls',
		disabled:false,
		html:'<font>Nombre: </font>',
		hidden:editable
	});
	var lblDescripcion=new Ext.form.Label({
		id:'lblDescripcion',
		cls:'lblDescripcionCls',
		disabled:false,
		html:'<font class="fontTitulo">Descripcion:</font>',
		hidden:editable
	});
	var subirArchivo=new Ext.form.field.File({
		id:'archivo',
		name: 'archivo',
		disabled: !privilegios.crear,
        fieldLabel: 'Archivo',
        labelWidth: 50,
        msgTarget: 'under',
        allowBlank: false,
        cls:'subirArchivoCls',
        width:350,
        buttonText: '',
        listeners:{
        	change:function( _this, value, eOpts ){
        		if(nuevo){
        			var n = value.substring(value.lastIndexOf("\\") + 1, value.lastIndexOf(".")).substring(0, 32);
        			txtNombre.setValue(n);
        		}
        	}
        },
        buttonConfig:{
     		xtype: "button",
    		id: "btnSubirArchivo",
    		iconCls:'btnSubirArchivoIconCls'
     }
	});
	var txtNombre=new Ext.form.field.Text({
		id:'docName',
		name:'docName',
		cls:'txtNombreCls',
		fieldLabel:'Nombre',
		width:350,
		labelWidth:150,
		hidden:!editable,
//		maxLength:32,
//		enforceMaxLength:true,
		allowBlank:false,
		disabled:!editable,
		readOnly : true
	});
	
	var txtDescripcion=new Ext.form.field.TextArea({
		id:'docDesc',
		name:'docDesc',
		cls:'txtDescripcionCls',
		grow      : false,
        fieldLabel: 'Descripcion',
        width:350,
        height:90,
        hidden:!editable
        
	});

	var btnRespaldarArchivo=new Ext.button.Button({
		id:'btnRespaldarArchivo',
		name:'btnRespaldarArchivo',
		cls:'btnRespaldarArchivoCls',
		text:'Respaldar archivo',
		scale:'medium',
		iconCls:'btnRespaldarArchivoIconCls',
		formBind:true,
		handler:function(){
			if(privilegios.crear){
				var form = panelPrincipal.getForm();
				if(form.isValid()){
					form.submit({
						url: rutaServletAgregar+'?select='+select+'&extjs=true',
						waitMsg: 'Cargando archivo...',
						success: function (formPanel, action) {
							panelPrincipal.setDisabled(true);
							var data = Ext.decode(action.response.responseText);
							Ext.example.msg("Cargado",data.message);
							top.left.location.href=data.rutaArbolRedirect;
							setTimeout(function() {
								document.location.href=data.rutaMainRedirect;
							}, tiempoRedireccion);
						},
						failure: function (formPanel, action) {
							var data = Ext.decode(action.response.responseText);
							Ext.example.msg("Error",data.message);
						}
					});
				}
			}
		}
	});
	var btnDigitalizar=new Ext.button.Button({
		id:'btnDigitalizar',
		name:'btnDigitalizar',
		disabled: !privilegios.crear,
		cls:'btnDigitalizarCls',
		text:'Digitalizar',
		scale:'medium',
		iconCls:'btnDigitalizarIconCls',
		handler:function(){
			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			document.location.href="../jsp/Visualizador.jsp?select="+select;
		}
	});
	var btnSubirVarios=new Ext.button.Button({
		id:'btnSubirVarios',
		name:'btnSubirVarios',
		disabled: !privilegios.crear,
		text:'Subir varios',
		iconCls:'btnSubirVariosIconCls',
		handler:function(){
//			document.location.href=basePath+'jsp/PageDocumentUpload.jsp?select='+select+'&redirect=true';
		}
	});
	var bbar=new Ext.toolbar.Toolbar({
		id:'bbar',
		name:'bbar',
		items:[btnSubirVarios]
	});
/*
 * Paneles
 */
	var panelDigitalizar=new Ext.panel.Panel({
		id:'panelDigitalizar',
		name:'panelDigitalizar',
		cls:'panelDigitalizarCls',
		width:'100%',
		height:95,
		border:0,
		title:'<div style="text-align:center;">Digitalizar</div>',
		items:[{xtype:'fieldset',id:'FieldSetbtnD',cls:'fieldSetTxtCls',items:[btnDigitalizar]}]
	
	});
	var panelPrincipal=new Ext.form.Panel({
		id:'panelPrincipal',
		name:'panelPrincipal',
		disabled: !privilegios.crear,
		width:450,
		height:425,
		autoScroll:true,
		tbar:!nuevo?bbar:null,
		title:'Respaldar archivo',
		cls:'panelPrincipalCls',
		renderTo:Ext.getBody(),
		items:[{xtype:'fieldset',id:'FieldSetlbl',cls:'fieldSetCls',items:[lblNombre,lblDescripcion]},
		subirArchivo,txtNombre,txtDescripcion,
		{xtype:'fieldset',id:'FieldSetbtn',cls:'fieldSetTxtCls',items:[btnRespaldarArchivo]},
		panelDigitalizar],
		listeners:{
			afterrender:function(_this,e){
				_this.el.mask('Espere por favor...', 'x-mask-loading');
			}
		}
	});
/*
 * Funciones
 */
	function load(){
			if(nuevo){
				panelDigitalizar.setVisible(false);
				panelPrincipal.setHeight(280);
				panelPrincipal.el.unmask();
				lblNombre.setVisible(false);
				lblDescripcion.setVisible(false);
				txtNombre.setVisible(true);
				txtNombre.setDisabled(false);
				txtDescripcion.setVisible(true);
			}
			else{
				storeDocumento.load();
			}
	}
	load();
});