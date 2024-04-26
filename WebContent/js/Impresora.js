Ext.onReady(function(){
	/*var boton=new Ext.button.Button({
		id:'boton',
		text:'boton SyC',
		renderTo:Ext.getBody(),
		handler:function(){listarImpresoras();}
	});
	function lista(){
		var applet = document.getElementById('qz');
		if (applet != null) {
		if (!applet.isDoneFinding()) {
		      window.setTimeout(lista, 100);
		  }
		else{
			 var printersCSV = applet.getPrinters();
             var printers = printersCSV.split(",");
             for (p in printers) {
                 alert(printers[p]);
             }
		}
		}
		else{
			alert('E');
		}
	}
	function listarImpresoras(){
		var applet = document.getElementById('qz');
        if (applet != null) {
           applet.findPrinter("\\{dummy printer name for listing\\}");
        }
        lista();
	}*/
	
	/*
	 * Variables
	 */
	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	var actionStore='getPrintFiles';
	var arreglo=new Array();
	var contador=0;
	var ajusta=false;
	var contadorCopias=1;
	var verifico=false;
	var msjFHoja='Esta configuración enviará una impresión por hoja, lo que agiliza el proceso, ' +
	'pero puede ocasionar conflictos de colas de impresión.';
	var msjFDoc='Esta configuración enviará solo una impresión con todo el documento, lo que hará ' +
	'un poco más lento el proceso, pero será hecho en una sola transacción.';
	var msjCalidad='La calidad de impresión depende específicamente de la impresora utilizada.';
	var arregloVacio=new Array();
	/*
	 * Store y modelos
	 */
	var storeCalidad = Ext.create('Ext.data.Store', {
    fields: ['nombre', 'valor'],
    data : [
        {"nombre":"300 DPI", "valor":"300"},
        {"nombre":"600 DPI", "valor":"600"}
        //...
    ]
});
//applet.setPaperSize("8.5in", "11.0in");
	var storeHojas = Ext.create('Ext.data.Store', {
    fields: ['idT', 'tam','tama'],
    data : [
        {"idT":"LETTER", "tam":"Carta","tama":"8.5inx11.0in"},
        {"idT":"A4", "tam":"A4","tama":"210mmx297mm"}
        //...
    ]
});
		Ext.define('impresionModelo', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name:'idDocumento',type:'string'},
	     {name:'pdf',type:'boolean'},
	     {name:'calidad',type:'string'},
	     {name:'paginas',type:'string'},
	     {name:'formato',type:'string'},
	     {name:'orientacion',type:'string'}
	    ]
	});
	Ext.define('impresoraModelo', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name:'nombre',type:'string'}
	    ]
	});
	var storeImpresoras = Ext.create('Ext.data.Store', {
	    model: 'impresoraModelo',
	    proxy: {
	        type: 'ajax',
	        //url : '/users.json',
	        reader: {
	            type: 'json'
	         //   root: 'users'
	        }
	    },
	    autoLoad: false
	});
	Ext.define('modeloImpresion', {
	        extend: 'Ext.data.Model',
	        fields: 
	    [
	     {name:'id',type:'string'},
	     {name: 'ruta', type: 'string'}
	    ]
	});
	
	var storeImpresion = new Ext.data.Store({ 
		        model: 'modeloImpresion', 
		        proxy: { 
		            type: 'ajax', 
		            url: rutaServlet,		
		            	reader: { 										
		                type: 'json', 
		                root: 'archivos'
		            },
		            extraParams: 
			         {
		            	action: actionStore,
			            select:select,
			            pages:''
			          } 
		        },
		        listeners:{
			    	 load:function(_this,e){
			    		 if(_this.getCount()>0){
			    				var total=_this.getCount();
			    				arreglo=new Array();
			    				contador=0;
			    				contadorCopias=1;
			    				for(var i=0;i<total;i++){
			    					arreglo.push(_this.getAt(i).get('ruta'));		
			    				}
			    				imprimir(contador);
			    		 }
			    	 }
			     },
		        autoLoad:false
		    });
	    
	/*
	 * Objetos
	 */
	/*var gridImpresoras=new Ext.grid.Panel({
		id:'gridImpresoras',
		width:445,
		height:120,
		store:storeImpresoras,
		columnLines: true,*/
		//tbar:[/*btnDescargar,*/'->',txtFiltro],
		/*autoScroll:true,
		columns:[
			  { header: 'Nombre',  dataIndex: 'nombre', flex:1}
		],
		listeners:{
			select:function( _this, record, index, eOpts ){
				//
			}
		}
	});*/
	var comboImpresoras=new Ext.form.ComboBox({
    fieldLabel: 'Nombre de impresora',
    store: storeImpresoras,
    queryMode: 'local',
    displayField: 'nombre',
    valueField: 'nombre',
    width:400,
    labelWidth:150,
    labelPad:50,
    editable:false,
    listeners:{
    	change:function(_this, newValue, oldValue, eOpts){
    		fieldSetImpresoras.el.mask('Espere por favor...', 'x-mask-loading');
    		btnImprimir.setDisabled(true);
    		setPrinter(newValue);
    	}
    }
	});

	var btnPreferencias=new Ext.button.Button({
		id:'btnPreferencias',
		text:'Preferencias',
		cls:'btnPreferenciasCls',
		iconCls:'btnPreferenciasIconCls',
		handler:function(){
			if(!windowPreferencias.isVisible()){
				Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
				if(setPreferencias()){
					abrirPreferencias();	
				}
					
			}
			
		}
	});
	var txtPaginas=new Ext.form.field.Text({
		id:'txtPaginas',
		disabled:true,
		maskRe:/[0-9,-]/
	});
	var radPaginas = new Ext.form.RadioGroup({  
	    columns:1,
	    id:'radPaginas',
	    //margin:'15 0 0 150',    
	    items:[  
	        {boxLabel: 'Todas',  			name: 'radPag',inputValue:'1',checked: false},
	        {boxLabel: 'Pagina actual',  	name: 'radPag',inputValue:'2',checked: true},
	        {boxLabel: 'Paginas', 			name: 'radPag',inputValue:'3', checked: false}
	    ],
	   listeners:{
	   		change:function( _this, newValue, oldValue, eOpts ){
	   			if(newValue.radPag==3){
	   				txtPaginas.setDisabled(false);
	   				lblPaginas.setDisabled(false);
	   			}
	   			else{
	   				txtPaginas.setDisabled(true);
	   				lblPaginas.setDisabled(true);
	   			}
	   		}
	   }
	}); 
	var lblPaginas=new Ext.form.Label({
		id:'lblPaginas',
		cls:'lblPaginasCls',
		disabled:true,
		html:'Introduzca un número de página única o un rango de páginas, por ejemplo: 5-12 , 1,2,4'
	});
	var txtCopias=new Ext.form.field.Number({
		id: 'txtCopias',
		cls:'txtCopiasCls',
        fieldLabel: 'Numero de copias',
        value: 1,
        maxValue: 99,
        minValue: 1,
        width:185,
        editable:false
	});
	var comboHojas=new Ext.form.ComboBox({
    fieldLabel: 'Tamaño de hoja',
    store: storeHojas,
    cls:'comboHojasCls',
    queryMode: 'local',
    displayField: 'tam',
    valueField: 'idT',
    width:185,
    value:'LETTER',
    editable:false
	});
	var radOrientacion = new Ext.form.RadioGroup({  
	    columns:1,
	    id:'radOrientacion',
	    cls:'radOrientacionCls', 
	    width:185,
	    layout:'hbox',
	    items:[  
	        {boxLabel: 'Vertical&nbsp;&nbsp;&nbsp;',  	name: 'radOr',inputValue:'1',checked: true},
	        {boxLabel: 'Horizontal',  	name: 'radOr',inputValue:'2',checked: false}
	    ]
	}); 
	var btnImprimir=new Ext.button.Button({
		id:'btnImprimir',
		text:'Imprimir',
		scale:'medium',
		iconCls:'btnImprimirIconCls',
		cls:'btnImprimirCls',
		handler:function(){
			panelPrincipal.el.mask('Espere por favor...', 'x-mask-loading');
			var pag;
			if(radPaginas.getValue().radPag==1)
				pag='todas';
			else if(radPaginas.getValue().radPag==2)
				pag=pagina;
			else if(radPaginas.getValue().radPag==3)
				pag=txtPaginas.getValue()!=''?txtPaginas.getValue():pagina
			else
				pag=pagina;
			var datos=Ext.create('impresionModelo',{
					idDocumento:select,
					pdf:getPreferencias(1)==2?true:false,
					calidad:getPreferencias(2),
					paginas:pag,
					formato:comboHojas.getValue(),
					orientacion:radOrientacion.getValue().radOr
				});
				
				datos = Ext.JSON.encode(datos.data);
			storeImpresion.load({params:{action:actionStore,select:datos},
				callback: function(records, operation, success){
					if(!success){
						var msj=records.data.message;
						errorImpresionFn(msj);
					}
				}
			});
		}
	});
	var btnCancelar=new Ext.button.Button({
		id:'btnCancelar',
		text:'Cancelar',
		scale:'medium',
		iconCls:'btnCancelarIconCls',
		cls:'btnCancelarCls',
		handler:function(){
			self.close();
		}
	});
	//Objetos preferencias
	var btnCancelarPreferencias=new Ext.button.Button({
		id:'btnCancelarPreferencias',
		text:'Cancelar',
		scale:'small',
		iconCls:'btnCancelarPreferenciasIconCls',
		cls:'btnCancelarPreferenciasCls',
		handler:function(){
			windowPreferencias.hide();
		}
	});
	var btnGuardarPreferencias=new Ext.button.Button({
		id:'btnGuardarPreferencias',
		text:'Guardar',
		scale:'small',
		iconCls:'btnGuardarPreferenciasIconCls',
		cls:'btnGuardarPreferenciasCls',
		handler:function(){
			Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
			guardaPreferencias(radFormatoImpresion.getValue().radFo,comboCalidad.getValue());
		}
	});
	var lblMsjFormato=new Ext.form.Label({
		id:'lblMsjFormato',
		cls:'lblMsjFormatoCls',
		disabled:false,
		html:msjFHoja
	});
	var radFormatoImpresion = new Ext.form.RadioGroup({  
	    columns:1,
	    id:'radFormatoImpresion',
	    cls:'radFormatoImpresionCls', 
	    width:300,
	    layout:'hbox',
	    items:[  
	        {boxLabel: 'Por hoja&nbsp;&nbsp;&nbsp;',  	name: 'radFo',inputValue:'1',checked: true},
	        {boxLabel: 'Documento completo',  			name: 'radFo',inputValue:'2',checked: false}
	    ],
	    listeners:{
	   		change:function( _this, newValue, oldValue, eOpts ){
	   			if(newValue.radFo==1){
	   				lblMsjFormato.setText(msjFHoja,false);
	   			}
	   			else{
	   				lblMsjFormato.setText(msjFDoc,false);
	   			}
	   		}
	   }
	});
	var lblMsjCalidad=new Ext.form.Label({
		id:'lblMsjCalidad',
		cls:'lblMsjCalidadCls',
		disabled:false,
		html:msjCalidad
	});
	var comboCalidad=new Ext.form.ComboBox({
		id:'comboCalidad',
	    fieldLabel: 'DPI',
	    store: storeCalidad,
	    queryMode: 'local',
	    displayField: 'nombre',
	    valueField: 'valor',
	    value:'300',
	    width:200,
	    editable:false
	});
	var panelGeneral=new Ext.panel.Panel({
			id:'panelGeneral',
			title:'General',
			width:400,
			height:300,
			items:[
			{xtype:'fieldset',height:90,title:'Configuracion de impresión',id:'fieldSetCGI',cls:'fieldSetCGICls',layout:'vbox',items:[radFormatoImpresion,lblMsjFormato]},
			{xtype:'fieldset',height:90,title:'Calidad de impresión',id:'fieldSetCGC',cls:'fieldSetCGICls',layout:'vbox',items:[comboCalidad,lblMsjCalidad]}
			]
	});		
	var tabPreferencias=new Ext.tab.Panel({
   		id:'tabPreferencias',
    	items: [panelGeneral]
	});

	var windowPreferencias=new Ext.window.Window({
		id:'windowPreferencias',
	    title: 'Preferencias',
	    height: 300,
	    width: 400,
	    layout: 'fit',
	    closable:false,
	    resizable:false,
	    bbar:['->',btnGuardarPreferencias,'-',btnCancelarPreferencias],
	    items:[tabPreferencias],
	    listeners:{
	    	show:function(_this,e){
	    		Ext.getBody().unmask();
	    	}
	    }
	});
	var lblStatus=new Ext.form.Label({
		id:'lblStatus',
		cls:'lblStatusCls',
		disabled:false,
		html:'&nbsp;'
	});
	var chbAjustar=new Ext.form.field.Checkbox({
		id:'chbAjustar',
		hidden:false,
		boxLabel  : 'Ajustar a pagina',
        name      : 'chbAjustar',
        handler:function(_this,e){
        	if(_this.getValue())
        		ajusta=true;
        	else
        		ajusta=false;
        }
	});
	/*
	 * Paneles
	 */
		var fieldSetImpresoras=new Ext.form.FieldSet({
			id:'fieldSetImpresoras',
			cls:'fieldSetImpresorasCls',
			title:'Selecciona impresora',
			width:470,
			height:90,
			items:[comboImpresoras,btnPreferencias],
			listeners:{
				afterrender:function(_this,a,b){
					Ext.getBody().unmask();
					btnImprimir.setDisabled(true);
					_this.el.mask('Espere por favor...', 'x-mask-loading');
				}
			}
		});
		var fieldSetPaginas=new Ext.form.FieldSet({
			id:'fieldSetPaginas',
			cls:'fieldSetPaginasCls',
			title:'Paginas',
			width:220,
			height:160,
			items:[radPaginas,txtPaginas,lblPaginas]
		});
		var fieldSetConfiguracion=new Ext.form.FieldSet({
			id:'fieldSetConfiguracion',
			cls:'fieldSetConfiguracionCls',
			title:'Configuración',
			width:220,
			height:160,
			items:[txtCopias,comboHojas,radOrientacion,chbAjustar]
		});
		var panelPrincipal=new Ext.panel.Panel({
			id:'panelPrincipal',
			width:500,
			height:370,
			//title:'Impresora',
			items:[fieldSetImpresoras,{xtype:'fieldset',id:'fieldSetH',cls:'fieldSetHCls',layout:'hbox',items:[fieldSetPaginas,fieldSetConfiguracion]},
			{xtype:'fieldset',id:'fieldSetB',cls:'fieldSetHCls',layout:'hbox',items:[btnImprimir,btnCancelar]}],
			bbar:[lblStatus],
			renderTo:Ext.getBody()
		});
	/*
	 * Funciones
	 */
		function setPrinter(nombre){
			var applet = document.getElementById('qz');
         if (applet != null) {
            applet.findPrinter(nombre);
         }
         _setDefaultPrinter();
		}
		function _setDefaultPrinter() {
			var applet = document.getElementById('qz');
			if (applet != null) {
			   if (!applet.isDoneFinding()) {
			      window.setTimeout(_setDefaultPrinter, 100);
			   } else {
			      	  var printer = applet.getPrinter();
		              //gridImpresoras.getSelectionModel().select(storeImpresoras.indexOf(storeImpresoras.findRecord('nombre',printer)),true);
		              comboImpresoras.setValue(storeImpresoras.findRecord('nombre',printer));
		             	fieldSetImpresoras.el.unmask();
		             	btnImprimir.setDisabled(false);
			   }
			} else {
		            logError('Error al detectar el applet');
		        }
      }
		function setDefaultPrinter(){
			 var applet = document.getElementById('qz');
		         if (applet != null) { 
		         	applet.findPrinter();
		         }
         	_setDefaultPrinter();
		}
		function listaI(){
			var applet = document.getElementById('qz');
			if (applet != null) {
				if (!applet.isDoneFinding()) {
				      window.setTimeout(listaI, 100);
				  }
				else{
					 var printersCSV = applet.getPrinters();
		             var printers = printersCSV.split(",");
		             for (p in printers) {
		             	var i = Ext.create('impresoraModelo', {
						    nombre : printers[p]
						});	
						storeImpresoras.add(i);
		             }
		             setDefaultPrinter(applet.getPrinter());
		             //setPrinter('192_168_15_87');
				}
			}
			else{
				 logError('Error al detectar el applet');
			}
		}
		function listarImpresoras(){
			var applet = document.getElementById('qz');
		        if (applet != null) {
		           applet.findPrinter("\\{Buscando...\\}");
		        }
	        listaI();
		}
		function logError(mensaje){
			Ext.Msg.show({
		                title: 'Error',
		                msg: mensaje,
		                buttons: Ext.Msg.OK,
		                animEl: 'elId',
		                icon: Ext.MessageBox.ERROR
		            });
		}
		function abrirPreferencias(){
			windowPreferencias.show();
		}
		function guardaPreferencias(formato,calidad){
			if(setCookie('_FPreferenciasImpresion',formato+','+calidad,'15d')){
				Ext.getBody().unmask();
				Ext.example.msg("Correcto","Configuracion guardada");
				windowPreferencias.hide();
			}
			else{
				logError('Ocurrio un error al guardar la configuracion');
			}
		}
		function setPreferencias(){
			var pref=getCookie('_FPreferenciasImpresion');
			if(pref!=null){
				pref=pref.split(',');
				radFormatoImpresion.setValue({radFo:pref[0]});
				comboCalidad.setValue(pref[1]);
			}
			return true;
		}
		function getPreferencias(n){
			var pref=getCookie('_FPreferenciasImpresion');
			if(pref!=null){
				pref=pref.split(',');
				if(n==1){
					return pref[0];
				}
				else{
					return pref[1];
				}
			}
			else{
				if(n==1){
					return 1;
				}
				else{
					return 300;
				}
			}
		}
		function imprimir(_contador){
			lblStatus.setText('Imprimiendo '+((_contador+1)+((contadorCopias-1)*arreglo.length)).toString()+' de '+(arreglo.length*parseInt(txtCopias.getValue())).toString(),false);
			var applet=document.getElementById('qz');
			 var t=storeHojas.findRecord('idT', comboHojas.getValue()).get('tama');
			 t=t.split('x');			
			 //logError(radOrientacion.getValue().radOr); 
			 if(getPreferencias(1)==1){
			 	applet.appendImage(arreglo[_contador]);
			 	if(ajusta){
			 		applet.setAutosize(false);
				 }
				 else{
				 	applet.setAutosize(true);
				 }
			 }
			 else{
			 	applet.appendPDF(arreglo[_contador]);
			 }
			 
			 if(radOrientacion.getValue().radOr==2){
			 	applet.setOrientation("landscape");
			 }	
			 else{
			 	applet.setOrientation("portrait");
			 }
			 
			 //applet.setAutosize(true);
			 applet.setPaperSize(t[0], t[1]);
			 imprimiendo();
		}
			function imprimiendo() {
				var applet = document.getElementById('qz');
				if (applet != null) {
				   if (!applet.isDoneAppending()) {
				      window.setTimeout(imprimiendo, 100);
				   } else {
				      applet.printPS(); 
			          monitorImpresion();
				   }
				} else {
			            logError('Error al detectar el applet');
			        }
	      }
	       function monitorImpresion() {
				var applet = document.getElementById('qz');
				if (applet != null) {
				   if (!applet.isDonePrinting()) {
				      window.setTimeout(monitorImpresion, 100);
				   } else {
				      var e = applet.getException();
				      if(e == null ){
					   if(contador<arreglo.length-1){
					      	contador++;
					      	imprimir(contador);
					   }
					   else if(parseInt(txtCopias.getValue())>contadorCopias){
					   	 contador=0;
					   	 contadorCopias++;
					   	 imprimir(contador);
					   }
					   else{
					   		lblStatus.setText('Finalzado...',false);
					   		panelPrincipal.el.unmask();
					   }
					      
				      }
				      else{
				      	logError(e.getLocalizedMessage());
				      }
				   }
				} else {
			            logError('Error al detectar el applet');
			        }
      }
      
      function errorImpresionFn(mensaje) {
      				panelPrincipal.el.unmask();
		            Ext.Msg.show({
		                title: 'Error',
		                msg: mensaje,
		                buttons: Ext.Msg.OK,
		                animEl: 'elId',
		                icon: Ext.MessageBox.ERROR
		            });
		}
		
		function qzReady(object) {
      		console.log(object);
		}
      
		listarImpresoras();
});