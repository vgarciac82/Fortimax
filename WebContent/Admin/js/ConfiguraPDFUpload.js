/* 
 * 
 * REQUIRE Y FUNCIO ONREADY
 * 
 */
Ext.Loader.setConfig({
    enabled: true
});
Ext.Loader.setPath('Ext.ux', '../js/extjs4/ux');


Ext.onReady(function() {
	Ext.QuickTips.init();
	/* 
	 * 
	 * Variables
	 * 
	 */
	var Gaveta="";
	var dise=1;
	/* 
	 * 
	 * Store y Modelos
	 * 
	 */
	
	 Ext.define('PDFModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'titulo', type: 'string'},
	         {name: 'descripcion', type: 'string'}

	     ]
	 });
	 var Store = Ext.create('Ext.data.Store', {
	     model: 'PDFModel',
	     proxy: {
	         type: 'ajax',
	         url: rutaServlet, 
	         reader: {
	             type: 'json',
	             root: 'pdf'
	         },
	         extraParams: 
	         {
	              action: 'getGavetasPDF'
	          }
	     },

	     autoLoad: true
	 });
	 Ext.define('ConfigModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'instaladorAF', type: 'boolean'},
	         {name: 'separarPDF', type: 'boolean'},
	         {name: 'url', type: 'string'},
	         {name: 'jpg', type: 'boolean'},
	         {name: 'bmp', type: 'boolean'},
	         {name: 'jpeg', type: 'boolean'},
	         {name: 'png', type: 'boolean'},
	         {name: 'gif', type: 'boolean'},
	         {name: 'tiff', type: 'boolean'},
	         {name: 'tif', type: 'boolean'},
	         {name: 'pdf', type: 'boolean'},
	         {name: 'tamKB', type: 'integer'},
	         {name: 'orden', type: 'string'},
	         {name: 'salidaI', type: 'string'},
	         {name: 'salidaT', type: 'string'},
	         {name: 'espacioCI', type: 'string'},
	         {name: 'espacioCT', type: 'string'},
	         {name: 'maxAltoI', type: 'integer'},
	         {name: 'maxAnchoI', type: 'integer'},
	         {name: 'maxAltoT', type: 'integer'},
	         {name: 'maxAnchoT', type: 'integer'},
	         {name: 'transI', type: 'string'},
	         {name: 'transT', type: 'string'},
	         {name: 'tiposColor', type: 'string'},
	         {name: 'espaciosColor', type: 'string'},
	         {name: 'responsableEsc', type: 'string'},
	         {name: 'metodoEsc', type: 'string'},
	         {name: 'hints', type: 'boolean'},
	         {name: 'escritor', type: 'string'},
	         {name: 'calidad', type: 'string'}
	     ]
	 });
	 var storeConfig = Ext.create('Ext.data.Store', {
	     model: 'ConfigModel',
	     proxy: {
	         type: 'ajax',
	         url: rutaServlet,//'../JsonPrueba/config.json',   //rutaServlet,
	         reader: {
	             type: 'json',
	             root: 'config'
	         },
	         extraParams: 
	         {
	              action: 'getConfigPDF',
	              select: Gaveta
	          }
	     },
	     listeners: {
	         'load' :  function(store,records,options) {
	             onLoadStoreConfig();
	         }
	     },
	     autoLoad: false
	 });
	var storeFormatoImagen = Ext.create('Ext.data.Store', {
	    fields: ['formato','texto'],
	    data : [
	        {"formato":"jpg","texto":"Imagen JPG"},
	        {"formato":"bmp","texto":"Imagen BMP"},
	        {"formato":"jpeg","texto":"Imagen JPEG"},
	        {"formato":"png","texto":"Imagen PNG"},
	        {"formato":"gif","texto":"Imagen GIF"},
	        {"formato":"tiff","texto":"Imagen TIFF"},
	        {"formato":"tif","texto":"Imagen TIF"}
	    ]
	});

	var storeTransformacion = Ext.create('Ext.data.Store', {
	    fields: ['id','texto','desa','msj'],
	    data : [
	        {"id":"","texto":"Blanco y Negro",'desa': true,'msj': 'Blanco y Negro'},
	        {"id":"std","texto":"-Transformacion Estandar",'desa': false,'msj': 'Transformacion Estandar'},
	        {"id":"","texto":"Escala de Grises",'desa': true,'msj': 'Escala de Grises'},
	        {"id":"op","texto":"-ColorConvertOp",'desa': false,'msj': 'Color Convert Op'},
	        {"id":"2d","texto":"-Graphics 2D",'desa': false,'msj': '2D'}
	    ]
	});
	var storeMapeoTipos = Ext.create('Ext.data.Store', {
	    fields: ['id','texto','desa','msj'],
	    data : [
	        {"id":"bb","texto":"Type Byte Binary",'desa': false,'msj': 'Binario'},
	        {"id":"bg","texto":"Type Byte Gray",'desa': false,'msj': 'Gris'},
	        {"id":"bi","texto":"Type Byte Indexed",'desa': false,'msj': 'Indexado'},
	        {"id":"ug","texto":"Type Ushort Binary",'desa': false,'msj': 'Binario corto'}

	    ]
	});
	var storeMapeoEspacios = Ext.create('Ext.data.Store', {
	    fields: ['id','texto','desa','msj'],
	    data : [
	        {"id":"cg","texto":"CS Gray",'desa': false,'msj': 'Gris'}
	    ]
	});
	var storeEscResponsable = Ext.create('Ext.data.Store', {
	    fields: ['id','texto','desa','msj'],
	    data : [
	        {"id":"tba","texto":"Transformacion Bilinear Afin",'desa': false,'msj': 'Bilinear Afin'},
	        {"id":"ta","texto":"Transformacion Afin",'desa': false,'msj': 'Afin'},
	        {"id":"pbi","texto":"Escalamiento propio de BUFFERED_IMAGE",'desa': false,'msj': 'Buffered Image'},
	        {"id":"pg2","texto":"Escalamiento propio de GRAPHICS2D",'desa': false,'msj': 'Graphics 2D'},
	        {"id":"pmt","texto":"Escalamiento propio MEDIA_TRACKER",'desa': false,'msj': 'Media Tracker'}
	    ]
	});
	var storeEscMetodo = Ext.create('Ext.data.Store', {
	    fields: ['id','texto','desa','msj'],
	    data : [
	        {"id":"def","texto":"Default",'desa': false,'msj': 'Defecto'},
	        {"id":"rap","texto":"Rapida",'desa': false,'msj': 'Rapida'},
	        {"id":"sua","texto":"Suave",'desa': false,'msj': 'Suave'},
	        {"id":"rep","texto":"Replica",'desa': false,'msj': 'Replica'}
	    ]
	});
	var storeEscritor = Ext.create('Ext.data.Store', {
	    fields: ['id','texto','desa','msj'],
	    data : [
	        {"id":"def","texto":"Escritor por defecto",'desa': false,'msj': 'Defecto'}
	    ]
	});

	
	/* 
	 * 
	 * OBJETOS
	 * 
	 */
	var lblDescargaArchivos= new Ext.form.Label({
	     id:'lblDescargaArchivos',
	     componentCls:'labels',
	      html:'Algunas implementaciones de la JVM no incluyen todos los archivos necesarios para la correcta ejecucion de este sistema <br /><br /> Si desea que se descargue automaticamente el instalador seleccione la casilla siguiente.',
	      shadow:true
	  });
	var chbDescargaArchivos = new Ext.form.Checkbox({  
	    columns:1,
	    margin:'15 0 0 150',
	    boxLabel: 'Descargar instalador de archivos faltantes automaticamente', 
	    name: 'cbDA', 
	    checked: false   	    
	});  
	var lblSepararTipos= new Ext.form.Label({
	     id:'lblSepararTipos',
	     componentCls:'labels',
	      html:'Al activar esta opcion se hara distincion entre PDF que contienen mayormente TEXTO de aquellos que contienen mayormente imagenes. <br /> <br /> Nota: En caso de no seleccionar esta opcion, todas los archivos PDF se trataran como de contenido de imágenes',
	      shadow:true
	  });
	var chbSepararTipos = new Ext.form.Checkbox({  
	    columns:1,
	    margin:'15 0 0 150',
	    boxLabel: 'Separar PDF por Tipos', 
	    name: 'cbST', 
	    checked: false      
	}); 
	var lblURLDescarga= new Ext.form.Label({
	     id:'lblURLDescarga',
	     componentCls:'labels',
	      html:'Especificar el URL de donde se descargara el instalador. Debe escribir el URL completo',
	      shadow:true
	  });
	var txtURL = Ext.create('Ext.form.field.Text', {
		fieldLabel:'URL:',
		componentCls:'labels',
		width:300,
		 margin:'15 0 0 150',
    	name:'txtURL',
    	emptyText:'ejm: http://fortimax.com.mx/jars/installer.jar'
});
	//PANEL DATOS
	var panGeneral=Ext.create('Ext.form.Panel',{
		id:'panGeneral',
		bodyCls:'panGeneral',
		name:'panGeneral',
		layout:'anchor',
		scroll:true,
		autoScroll:true,
		title:'Configuracion general',
		hidden:false,
		items:[
		       {xtype:'toolbar',layout:'anchor',cls:'redondeaBordes',margin:'10 0 0 0',width:'100%',name:'fieldsetGeneral1', items:[lblDescargaArchivos,chbDescargaArchivos]},
		       {xtype:'toolbar',layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'100%',name:'fieldsetGeneral2', items:[lblSepararTipos,chbSepararTipos]},
		       {xtype:'toolbar',layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'100%',name:'fieldsetGeneral3', items:[lblURLDescarga,txtURL]}
		       ]
	});
	//TERMINA PANEL DATOS
	var lblFormatosE= new Ext.form.Label({
	     id:'lblFormatosE',
	     componentCls:'labels',
	      html:'Seleccione los formatos de imagen <b>permitidos</b>.<br /><br /><b>Nota:</b> Los archivos de imagen no seleccionados se <b>rechazaran</b> en Fortimax',
	      shadow:true
	  });
	var chbFormatosE = new Ext.form.CheckboxGroup({ 
		id:'chbFormatosE',
	    columns:4,
	    margin:'15 0 0 150',    
	    items:[  
	        {boxLabel: 'JPG', name: 'jpg', checked: false},
	        {boxLabel: 'BMP', name: 'bmp', checked: false},
	        {boxLabel: 'JPEG', name: 'jpeg', checked: false},
	        {boxLabel: 'PNG', name: 'png', checked: false},
	        {boxLabel: 'GIF', name: 'gif', checked: false},
	        {boxLabel: 'TIFF', name: 'tiff', checked: false},
	        {boxLabel: 'TIF', name: 'tif', checked: false},
	        {boxLabel: 'PDF', name: 'pdf', checked: false}
	    ]  
	}); 
	var lblTamanoImagen= new Ext.form.Label({
	     id:'lblTamanoImagen',
	     componentCls:'labels',
	      html:'Ingrese el tama&ntildeo maximo por cada imagen.<br /><br /><b>Nota:</b> Si la imagen supera este tama&ntildeo sera <b>compactada</b>.',
	      shadow:true
	  });
	var txtTamano = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Tama&ntildeo maximo en <b>KiloBytes</b>:',
		componentCls:'labels',
		width:400,
		labelWidth:200,
		 margin:'15 0 0 150',
    	name:'txtTamano',
    	maskRe : /^[0-9]*$/,
    	emptyText:'Tamano ilimitado = 0'
});
	var lblOrden= new Ext.form.Label({
	     id:'lblOrden',
	     componentCls:'labels',
	      html:'Cuando envia mas de un archivo, elija el orden en que se procesaran.',
	      shadow:true
	  });
	var radOrden = new Ext.form.RadioGroup({  
	    columns:2,
	    id:'radOrden',
	    margin:'15 0 0 150',    
	    items:[  
	        {boxLabel: 'Ordenar archivos por <b>Fecha</b>',  name: 'orden',inputValue:'1',checked: false},
	        {boxLabel: 'Ordenar archivos por <b>Nombre</b>', name: 'orden',inputValue:'2', checked: false}
	    ]  
	}); 
	//PANEL ENTRADA
	var panEntrada=Ext.create('Ext.form.Panel',{
		id:'panEntrada',
		bodyCls:'panEntrada',
		name:'panEntrada',
		layout:'anchor',
		scroll:true,
		autoScroll:true,
		title:'Formato de entrada',
		items:[
		       {xtype:'toolbar',layout:'anchor',cls:'redondeaBordes',margin:'10 0 0 0',width:'100%',name:'fieldsetEntrada1',items:[lblFormatosE,chbFormatosE]},
		       {xtype:'toolbar',layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'100%',name:'fieldsetEntrada2',items:[lblTamanoImagen,txtTamano]}
		    //TODO:{xtype:'toolbar',layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'100%',name:'fieldsetEntrada3',items:[lblOrden,radOrden]}		       
		       ]
	});
	//TERMINA PANEL ENTRADA
	var lblFormatoImagenes= new Ext.form.Label({
	     id:'lblFormatoImagenes',
	     componentCls:'labels',
	      html:'Formato de imagen de salida <b>(IMAGEN)</b>: Seleccione el formato de salida para las imagenes extraidas del archivo PDF.<br /><br /><b>Nota:</b> Este formato aplica para cada imagen en el archivo.',
	      shadow:true
	  });
	var cmbFormatoImagenes=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: '<b>Formato</b>',
	    store: storeFormatoImagen,
	    emptyText:'Selecciona...',
	    queryMode: 'local',
	    displayField: 'texto',
	    editable: false,
	    valueField: 'formato',
	    margin:'10 0 0 0'
	});
	var lblFormatoTexto= new Ext.form.Label({
	     id:'lblFormatoTexto',
	     componentCls:'labels',
	      html:'Formato de imagen de salida <b>(TEXTO)</b>: Seleccione el formato de salida para las imagenes de texto extraidas del archivo PDF.<br /><br /><b>Nota:</b> Este formato aplica para cada imagen en el archivo.',
	      shadow:true
	  });
	var cmbFormatoTexto=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: '<b>Formato</b>',
	    store: storeFormatoImagen,
	    emptyText:'Selecciona...',
	    queryMode: 'local',
	    displayField: 'texto',
	    editable: false,
	    valueField: 'formato',
	    margin:'10 0 0 0'
	});
	var lblPixelesI= new Ext.form.Label({
	     id:'lblPixelesI',
	     componentCls:'labels',
	      html:'Maximo de Pixeles por Imagen <b>(IMAGEN)</b>: Ingrese el maximo ancho y alto en pixeles de la imagen. <br /><br /><b>Nota:</b> Las imagenes que sobrepasen este limite se escalaran.',
	      shadow:true
	  });
	var txtAnchoPixelesI = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Maximo <b>Ancho</b> en pixeles:',
		componentCls:'labels',
		width:250,
		labelWidth:150,
		 margin:'5 0 0 0',
    	name:'txtAnchoPixelesI',
    	emptyText:'Ilimitado= -1',
    	name:'txtAltoPixelesI',
    	maskRe : /^[0-9-]*$/,
    	regex :/^[-]?\d{1,}$/, 	
    	listeners:{
    		change:function(){
    			if(this.isValid()==false&& this.getValue().length>1)
    			this.setValue("-1");
    			if(parseInt(this.getValue())<-1)
    				this.setValue("-1");
    		}	
	}
	});
	var txtAltoPixelesI = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Maximo <b>Alto</b> en pixeles:',
		componentCls:'labels',
		width:250,
		labelWidth:150,
		 margin:'5 0 0 0',
		 emptyText:'Ilimitado= -1',
    	name:'txtAltoPixelesI',
    	maskRe : /^[0-9-]*$/,
    	regex :/^[-]?\d{1,}$/, 	
    	listeners:{
    		change:function(){
    			if(this.isValid()==false&& this.getValue().length>1)
    			this.setValue("-1");
    			if(parseInt(this.getValue())<-1)
    				this.setValue("-1");
    		}	
	}
	});
	var lblEspacioImagenes= new Ext.form.Label({
	     id:'lblEspacioImagenes',
	     componentCls:'labels',
	      html:'Espacio de color <b>Imagen</b>: Seleccione el espacio de color de la imagen. <br /><br /><b>Nota:</b> El espacio de color no garantiza un tama&ntildeo de imagen menor.',
	      shadow:true
	  });
	var radEspacioImagenes = new Ext.form.RadioGroup({  
	    columns:3,
	    id:'radEspacioImagenes',
	    margin:'10 0 0 0',    
	    items:[  
	        {boxLabel: 'Escala de grises', name: 'espI',inputValue:'grises',checked: false},
	        {boxLabel: 'Color', name: 'espI',inputValue:'color', checked: false},
	        {boxLabel: 'Blanco y Negro', name: 'espI',inputValue:'byn', checked: false,hidden:true}
	    ]  
	}); 
	var lblEspacioTexto= new Ext.form.Label({
	     id:'lblEspacioTexto',
	     componentCls:'labels',
	      html:'Espacio de color <b>Texto</b>: Seleccione el espacio de color de la imagen. <br /><br /><br /><b>Nota:</b> El espacio de color no garantiza un tama&ntildeo de imagen menor.',
	      shadow:true
	  });
	var radEspacioTexto = new Ext.form.RadioGroup({  
	    columns:3,
	    id:'radEspacioTexto',
	    margin:'10 0 0 0',    
	    items:[  
	        {boxLabel: 'Escala de grises', name: 'espT',inputValue:'grises',checked: false},
	        {boxLabel: 'Color', name: 'espT',inputValue:'color', checked: false},
	        {boxLabel: 'Blanco y Negro', name: 'espT',inputValue:'byn', checked: false}
	    ]  
	}); 
	var lblPixelesT= new Ext.form.Label({
	     id:'lblPixelesT',
	     componentCls:'labels',
	      html:'Maximo de Pixeles por Imagen <b>(TEXTO)</b>: Ingrese el maximo ancho y alto en pixeles de la imagen. <br /><br /><b>Nota:</b> Las imagenes que sobrepasen este limite se escalaran.',
	      shadow:true
	  });
	var txtAnchoPixelesT = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Maximo <b>Ancho</b> en pixeles:',
		componentCls:'labels',
		width:250,
		labelWidth:150,
		 margin:'5 0 0 0',
    	name:'txtAnchoPixelesT',
    	emptyText:'Ilimitado= -1',
    	name:'txtAltoPixelesI',
    	maskRe : /^[0-9-]*$/,
    	regex :/^[-]?\d{1,}$/, 	
    	listeners:{
    		change:function(){
    			if(this.isValid()==false&& this.getValue().length>1)
    			this.setValue("-1");
    			if(parseInt(this.getValue())<-1)
    				this.setValue("-1");
    		}	
	}
	});
	var txtAltoPixelesT = Ext.create('Ext.form.field.Text', {
		fieldLabel:'Maximo <b>Alto</b> en pixeles:',
		componentCls:'labels',
		width:250,
		labelWidth:150,
		 margin:'5 0 0 0',
    	name:'txtAltoPixelesT',
    	emptyText:'Ilimitado= -1',
    	name:'txtAltoPixelesI',
    	maskRe : /^[0-9-]*$/,
    	regex :/^[-]?\d{1,}$/, 	
    	listeners:{
    		change:function(){
    			if(this.isValid()==false&& this.getValue().length>1)
    			this.setValue("-1");
    			if(parseInt(this.getValue())<-1)
    				this.setValue("-1");
    		}	
	}
	});
	//PANEL SALIDA
	var panSalida=Ext.create('Ext.form.Panel',{
		id:'panSalida',
		bodyCls:'panSalida',
		name:'panSalida',
		layout: {
	        type: 'anchor',
	        align: 'stretch'
	    },
	    autoScroll:true,
		scroll:true,
		title:'Configuracion de salida',
		items:[
		       {xtype:'toolbar',flex:2,autoScroll:true,layout:'anchor',cls:'redondeaBordes',margin:'2 0 0 0',name:'fieldsetSalida1',items:[lblFormatoImagenes,cmbFormatoImagenes]},
		       {xtype:'toolbar',flex:2,autoScroll:true,layout:'anchor',cls:'redondeaBordes',margin:'2 0 0 0',name:'fieldsetSalida2',items:[lblEspacioImagenes,radEspacioImagenes]},
		       {xtype:'toolbar',flex:2,autoScroll:true,layout:'anchor',cls:'redondeaBordes',margin:'2 0 0 0',name:'fieldsetSalida3',items:[lblPixelesI,txtAnchoPixelesI,txtAltoPixelesI]}
		     //TODO:{xtype:'toolbar',flex:2,autoScroll:true,layout:'anchor',cls:'redondeaBordes',margin:'2 0 0 0',width:'50%',name:'fieldsetSalida4',items:[lblFormatoTexto,cmbFormatoTexto]},     
		     //TODO:{xtype:'toolbar',flex:2,autoScroll:true,layout:'anchor',cls:'redondeaBordes',margin:'2 0 0 0',width:'50%',name:'fieldsetSalida5',items:[lblEspacioTexto,radEspacioTexto]},
		     //TODO: {xtype:'toolbar',flex:2,autoScroll:true,layout:'anchor',cls:'redondeaBordes',margin:'2 0 0 0',width:'50%',name:'fieldsetSalida6',items:[lblPixelesT,txtAnchoPixelesT,txtAltoPixelesT]}
	
		       ]
	});
	//TERMINA PANEL SALIDA
	var lblTransCI= new Ext.form.Label({
	     id:'lblTransCI',
	     componentCls:'labels',
	      html:'<b>Transformacion de color Imagenes:</b><br /> Seleccione el metodo de transformacion entre espacios de color de imagenes.<br /><br /><b>Nota:</b> Dependiendo del tipo de archivo recibido, un algoritmo puede trabajar mejor que otro.',
	      shadow:true
	  });

	var cmbTransI=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Metodo de transformacion para <b>Imagenes</b>',
	    store: storeTransformacion,
	    emptyText:'Selecciona...',
	    displayField: 'texto',
	    width:500,
	    labelWidth:250,
	    editable: false,
	    valueField: 'id',
	    margin:'10 0 0 0',
	    triggerAction: 'all',
	      plugins: [
                    {
                        ptype: 'bannedcomboitems',
                        booleanField: 'desa',
                        reasonField: 'msj'
                    }
                ],
                listeners:{select:{fn:function(combo, value) {
                    if(combo.getValue()==""){
                    	this.setValue('std');
                    }
                    }}}
	});
	var lblTransCT= new Ext.form.Label({
	     id:'lblTransCT',
	     componentCls:'labels',
	      html:'<b>Transformacion de color Texto:</b><br /> Seleccione el metodo de transformacion entre espacios de color de texto.<br /><br /><b>Nota:</b> Dependiendo del tipo de archivo recibido, un algoritmo puede trabajar mejor que otro.',
	      shadow:true
	  });
	var cmbTransT=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Metodo de transformacion para <b>Texto</b>',
	    store: storeTransformacion,
	    emptyText:'Selecciona...',
	    displayField: 'texto',
	    width:500,
	    labelWidth:250,
	    editable: false,
	    valueField: 'id',
	    margin:'10 0 0 0',
	    triggerAction: 'all',
	      plugins: [
                    {
                        ptype: 'bannedcomboitems',
                        booleanField: 'desa',
                        reasonField: 'msj'
                    }
                ],
                listeners:{select:{fn:function(combo, value) {
                    if(combo.getValue()==""){
                    	this.setValue('std');
                    }
                    }}}
	});
	//PANEL TRANSFORMACION
	var panTransformacion=Ext.create('Ext.form.Panel',{
		id:'panTransformacion',
		bodyCls:'panTransformacion',
		name:'panTransformacion',
		layout: {
	        type: 'anchor',
	        align: 'stretch'
	    },
		scroll:true,
		autoScroll:true,
		title:'Metodos de transformacion',
		items:[
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'10 0 0 0',width:'50%',name:'fieldsetTrans1',items:[lblTransCI,cmbTransI]},
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'50%',name:'fieldsetTrans2',items:[lblTransCT,cmbTransT]}
		       ]
	});
	//TERMINA PANEL TRANSFORMACION
	var lblMapeoTipos= new Ext.form.Label({
	     id:'lblMapeoTipos',
	     componentCls:'labels',
	      html:'<b>Tipos de Color (Escala de Grises):</b><br /><br /> Elija el tipo de color para las imagenes en escala de grises.',
	      shadow:true
	  });
	var cmbMapeoT=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Tipo de color',
	    store: storeMapeoTipos,
	    emptyText:'Selecciona...',
	    displayField: 'texto',
	    width:300,
	    labelWidth:100,
	    editable: false,
	    valueField: 'id',
	    margin:'10 0 0 0',
	    triggerAction: 'all',
	      plugins: [
                    {
                        ptype: 'bannedcomboitems',
                        booleanField: 'desa',
                        reasonField: 'msj'
                    }
                ]
	});
	var lblMapeoEspacios= new Ext.form.Label({
	     id:'lblMapeoEspacios',
	     componentCls:'labels',
	      html:'<b>Espacios de Color (Escala de Grises):</b><br /><br /> Elija el espacio de color para las imagenes en escala de grises.',
	      shadow:true
	  });
	var cmbMapeoE=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Tipo de color',
	    store: storeMapeoEspacios,
	    emptyText:'Selecciona...',
	    displayField: 'texto',
	    width:300,
	    labelWidth:100,
	    editable: false,
	    valueField: 'id',
	    margin:'10 0 0 0',
	    triggerAction: 'all',
	      plugins: [
                    {
                        ptype: 'bannedcomboitems',
                        booleanField: 'desa',
                        reasonField: 'msj'
                    }
                ]
	});
	//PANEL MAPEO
	var panMapeo=Ext.create('Ext.form.Panel',{
		id:'panMapeo',
		bodyCls:'panMapeo',
		name:'panMapeo',
		layout: {
	        type: 'anchor',
	        align: 'stretch'
	    },
		scroll:true,
		autoScroll:true,
		title:'Mapeo de escala de grises',
		items:[
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'10 0 0 0',width:'50%',name:'fieldsetMapeo1',items:[lblMapeoTipos,cmbMapeoT]},
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'50%',name:'fieldsetMapeo2',items:[lblMapeoEspacios,cmbMapeoE]}
		       ]
	});
	//TERMINA PANEL MAPEO
	
	var lblEscResponsable= new Ext.form.Label({
	     id:'lblEscResponsable',
	     componentCls:'labels',
	      html:'<b>Responsable de Escalamiento:</b><br /><br /> Cada objeto listado a continuacion implementa un algoritmo diferente para el escalamiento de imagenes. Dependiendo del formato de entrada un algoritmo puede variar la relacion velocidad/calidad.',
	      shadow:true
	  });
	var cmbEscResponsable=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Responsable de escalamiento',
	    store: storeEscResponsable,
	    emptyText:'Selecciona...',
	    displayField: 'texto',
	    width:500,
	    labelWidth:200,
	    editable: false,
	    valueField: 'id',
	    margin:'10 0 0 0',
	    triggerAction: 'all',
	      plugins: [
                    {
                        ptype: 'bannedcomboitems',
                        booleanField: 'desa',
                        reasonField: 'msj'
                    }
                ]
	});
	var lblEscMetodo= new Ext.form.Label({
	     id:'lblEscMetodo',
	     componentCls:'labels',
	      html:'<b>Metodo de Escalamiento:</b><br /><br /> El metodo de escalamiento permite dar mayor importancia a uno de los parametros velocidad/calidad',
	      shadow:true
	  });
	var cmbEscMetodo=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Metodo de escalamiento',
	    store: storeEscMetodo,
	    emptyText:'Selecciona...',
	    displayField: 'texto',
	    width:500,
	    labelWidth:200,
	    editable: false,
	    valueField: 'id',
	    margin:'10 0 0 0',
	    triggerAction: 'all',
	      plugins: [
                    {
                        ptype: 'bannedcomboitems',
                        booleanField: 'desa',
                        reasonField: 'msj'
                    }
                ]
	});
	var lblEscHints= new Ext.form.Label({
	     id:'lblEscHints',
	     componentCls:'labels',
	      html:'<b>Usar Hints:</b><br /><br /> Establece parametros para optimizar la calidad en el resultado de la operacion de escalamiento.',
	      shadow:true
	  });
	var chbEscHints = new Ext.form.Checkbox({  
	    columns:1,
	    margin:'15 0 0 150',	    
	    boxLabel: 'Usar HINTS', 
	    name: 'hint', 
	    checked: false 
	}); 
	//PANEL ESCALAMIENTO
	var panEscalamiento=Ext.create('Ext.form.Panel',{
		id:'panEscalamiento',
		bodyCls:'panEscalamiento',
		name:'panEscalamiento',
		layout: {
	        type: 'anchor',
	        align: 'stretch'
	    },
		scroll:true,
		autoScroll:true,
		title:'Escalamiento de imagenes',
		items:[
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'10 0 0 0',width:'50%',name:'fieldsetEscala1',items:[lblEscResponsable,cmbEscResponsable]},
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'50%',name:'fieldsetEscala2',items:[lblEscMetodo,cmbEscMetodo]},
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'50%',name:'fieldsetEscala3',items:[lblEscHints,chbEscHints]}
		       ]
	});
	//TERMINA PANEL MAESCALAMIENTOPEO
	var lblEscritor= new Ext.form.Label({
	     id:'lblEscritor',
	     componentCls:'labels',
	      html:'<b>Escritor:</b><br /><br /> Implementacion para la escritura de imagenes.',
	      shadow:true
	  });
	var cmbEscritor=Ext.create('Ext.form.ComboBox', {
	    fieldLabel: 'Escritor de Imagenes',
	    store: storeEscritor,
	    emptyText:'Selecciona...',
	    displayField: 'texto',
	    width:300,
	    labelWidth:150,
	    valueField: 'id',
	    margin:'10 0 0 0',
	    triggerAction: 'all',
	      plugins: [
                    {
                        ptype: 'bannedcomboitems',
                        booleanField: 'desa',
                        reasonField: 'msj'
                    }
                ]
	});
	var lblEscCalidad= new Ext.form.Label({
	     id:'lblElblEscCalidadscritor',
	     componentCls:'labels',
	      html:'<b>Calidad de Imagen:</b><br /><br /> La calidad con la que la imagen se guarda. Es un valor numerico entre 0 y 1. Valores mas cercanos a 0 no ganrantizan un tama&ntildeo menor de archivo, sino una calidad baja.',
	      shadow:true
	  });

	var txtEscCalidad=new Ext.define('Ext.ux.CustomSpinner', {
	    extend: 'Ext.form.field.Spinner',
	    alias: 'widget.customspinner',
	    fieldLabel: 'Calidad',
	    componentCls:'labels',
	    width:250,
	    margin:'5 0 0 0',
	    id:'txtEscCalidad',
	    name:'txtEscCalidad',
        step: .1,
        value:0,
        editable:false,
	    onSpinUp: function() {
	        var me = this;
	        if (!me.readOnly) {
	            var val = parseFloat(me.getValue());
	            if(val<1){
	            if(me.getValue() !== '') {
	                val = val+me.step; 
	            }
	            me.setValue(val.toFixed(1));
	            }
	        }
	    },
	    onSpinDown:  function() {
        var me = this;
        if (!me.readOnly) {
            var val = parseFloat(me.getValue());
            if(val>0){
            if(me.getValue() !== '') {
                val = val-me.step; 
            }
            me.setValue(val.toFixed(1));
            }
        }
    }
	});
	//PANEL ESCRITURA
	var panEscri=Ext.create('Ext.form.Panel',{
		id:'panEscri',
		bodyCls:'panEscri',
		name:'panEscri',
		layout: {
	        type: 'anchor',
	        align: 'stretch'
	    },
		scroll:true,
		title:'Escritura de archivos',
		autoScroll:true,
		items:[
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'10 0 0 0',width:'50%',name:'fieldsetEscri1',items:[lblEscritor,cmbEscritor]},
		       {xtype:'toolbar',flex:1,layout:'anchor',cls:'redondeaBordes',margin:'15 0 0 0',width:'50%',name:'fieldsetEscri2',items:[lblEscCalidad,txtEscCalidad]}
		       ]
	});
	//TERMINA PANEL ESCRITURA
	var btnGuardarCambios=Ext.create('Ext.button.Button',{
		text: 'Guardar',
		 formBind: false,
		 iconCls:'disk',
		 disabled:true,
			 handler:function(){
				 Ext.Msg.show({
        		     title:'Guardar',
        		     msg: '&iquestGuardar cambios?',
        		     buttons: Ext.Msg.OKCANCEL,
        		     icon: Ext.Msg.QUESTION,
        		     fn: function (btn){
        		         if(btn=='ok'){  
        		        	 panPrin.disable();
        					 cmbGavetas.enable();
        					 btnIr.enable();
        					 btnGuardarCambios.disable();
        					 btnCancelar.disable();
        					 lblToolbar.setText('-');
        					 radOrden.setValue({orden:"nombre"});
        					 saveConfig(); 
        		         }}});
			 }
	   
	});
	
	var btnCancelar=Ext.create('Ext.button.Button',{
		text: 'Cancelar',
		 formBind: false,
		 iconCls:'cancel',
		 disabled:true,
		 handler:function(){
			 panPrin.disable();
			 cmbGavetas.enable();
			 btnIr.enable();
			 this.disable();
			 btnGuardarCambios.disable();
			 lblToolbar.setText('-');
		 }
	});
	var lblToolbar= new Ext.form.Label({
	     id:'lblToolbar',
	     text:'-',
	     componentCls:'labelGav',
	      shadow:true
	  });
	var cmbGavetas=Ext.create('Ext.form.ComboBox', {
		name:'cmbGavetas',
		id:'cmbGavetas',
	    fieldLabel: 'Selecciona <b>gaveta</b> a configurar.',
	    store: Store,
	    emptyText:'Selecciona...',
	    displayField: 'titulo',
	    width:400,
	    allowBlank:false,
	    labelWidth:200,
	    editable: false,
	    valueField: 'titulo',
	    margin:'10 0 0 0',
	    triggerAction: 'all'
	});
	var btnIr=Ext.create('Ext.button.Button',{
		text: 'Configurar',
		formBind: true,
		 iconCls:'config',
		 formBind:true,
		 handler:function(){
			 	if(cmbGavetas.getValue()!=null){
//			 		Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
	            	 cmbGavetas.disable();
	    			 this.disable();
	    			 btnCancelar.enable();
	    			 btnGuardarCambios.enable();
	    			 lblToolbar.setText('Configurando: '+cmbGavetas.getValue());
	    			 panPrin.enable();
	    			 loadConfig();
			 	}
		 }	   
	});
	/* 
	 * 
	 * PANEL PRINCIPAL
	 * 
	 */	
	
	var toolbar=Ext.create('Ext.toolbar.Toolbar', {
	    width   : '100%',
	    height:35,
	    items: [cmbGavetas,btnIr,lblToolbar,'->',btnCancelar,btnGuardarCambios],
	    renderTo: Ext.getBody()
	});
	
	var viewSize = Ext.getBody().getViewSize();
	if(dise==1){
	var panPrin=Ext.create('Ext.panel.Panel', {
		name:'panPrin',
	    title: 'Configuración de recepción de archivos',
	    width: '100%',
	    height: viewSize.height-60,
	    layout:'accordion',
	    disabled:true,
	    defaults: {
	        bodyStyle: 'padding:15px'
	    },
	    layoutConfig: {
	        titleCollapse: false,
	        animate: false,
	        activeOnTop: true
	    },
	    items: [//TODO:panGeneral,
	            panEntrada,panSalida
	          //TODO:,panTransformacion,panMapeo,panEscalamiento,panEscri
	    ],
	    renderTo: Ext.getBody()
	});
	}
	if(dise==2){
	var panPrin=Ext.create('Ext.tab.Panel', {
		name:'panPrin',
	    width: '100%',
	    title: 'Configuración de recepción de archivos',
	    disabled:true,
	    height: viewSize.height-60,
	    
	    items: [//TODO:panGeneral,
	            panEntrada,panSalida
	          //TODO:,panTransformacion,panMapeo,panEscalamiento,panEscri
	    ],
	    renderTo: Ext.getBody()
	});
	}

	/* 
	 * 
	 * Funciones
	 * 
	 */
	   var successAjaxFnN = function(response, request) {
	        var jsonData = Ext.JSON.decode(response.responseText);
	        if (true == jsonData.success) {
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
	    
		function loadConfig(){		
			Gaveta = cmbGavetas.getValue();
			storeConfig.load({
				  params: {
					  select: cmbGavetas.getValue()
				  }
				});
		}
		
		function onLoadStoreConfig() {
			 var conf=storeConfig.getAt(0);
//			 console.info(conf);
			 chbDescargaArchivos.setValue(conf.get('instaladorAF'));
			 chbSepararTipos.setValue(conf.get('separarPDF'));
			 txtURL.setValue(conf.get('url'));
			 chbFormatosE.setValue({jpg:conf.get('jpg'),bmp:conf.get('bmp'),jpeg:conf.get('jpeg'),png:conf.get('png'),gif:conf.get('gif'),tiff:conf.get('tiff'),tif:conf.get('tif'),pdf:conf.get('pdf')});
			 txtTamano.setValue(conf.get('tamKB'));
			 radOrden.setValue({orden:conf.get('orden')=='fecha'?'1':'2'});
			 cmbFormatoImagenes.setValue(conf.get('salidaI'));
			 cmbFormatoTexto.setValue(conf.get('salidaT'));
			 radEspacioImagenes.setValue({espI:conf.get('espacioCI')});
			 radEspacioTexto.setValue({espT:conf.get('espacioCT')});
			 txtAnchoPixelesI.setValue(conf.get('maxAnchoI'));
			 txtAltoPixelesI.setValue(conf.get('maxAltoI'));
			 txtAnchoPixelesT.setValue(conf.get('maxAnchoT'));
			 txtAltoPixelesT.setValue(conf.get('maxAltoT'));
			 cmbTransI.setValue(conf.get('transI'));
			 cmbTransT.setValue(conf.get('transT'));
			 cmbMapeoT.setValue(conf.get('tiposColor'));
			 cmbMapeoE.setValue(conf.get('espaciosColor'));
			 cmbEscResponsable.setValue(conf.get('responsableEsc'));
			 cmbEscMetodo.setValue(conf.get('metodoEsc'));
			 chbEscHints.setValue(conf.get('hints'));
			 cmbEscritor.setValue(conf.get('escritor'));
			 Ext.getCmp('txtEscCalidad').setValue(conf.get('calidad'));	
			 Ext.getBody().unmask();
		}
		
		function saveConfig(){
			var formatos=new Array();			
			chbFormatosE.items.each(function(i) {
					formatos[i.getName()]=i.getValue();
			});
			var radio = Ext.getCmp('radOrden').getValue();
			radio=radio.orden=='1'?'fecha':'nombre';
			var radEI=Ext.getCmp('radEspacioImagenes').getValue();
			var radET=Ext.getCmp('radEspacioTexto').getValue();
			if(!isNaN(parseInt(txtAltoPixelesI.getValue()))==false)
				txtAltoPixelesI.setValue("-1");
			if(!isNaN(parseInt(txtAnchoPixelesI.getValue()))==false)
				txtAnchoPixelesI.setValue("-1");
			if(!isNaN(parseInt(txtAnchoPixelesT.getValue()))==false)
				txtAnchoPixelesT.setValue("-1");
			if(!isNaN(parseInt(txtAltoPixelesT.getValue()))==false)
				txtAltoPixelesT.setValue("-1");
			var configModel = Ext.create('ConfigModel', {
				 instaladorAF:chbDescargaArchivos.getValue(),
		         separarPDF:chbSepararTipos.getValue(),
		         url:txtURL.getValue(),
		         jpg:formatos['jpg'],
		         bmp:formatos['bmp'],
		         jpeg:formatos['jpeg'],
		         png:formatos['png'],
		         gif:formatos['gif'],
		         tiff:formatos['tiff'],
		         tif:formatos['tif'],
		         pdf:formatos['pdf'],
		         tamKB:txtTamano.getValue(),
		         orden:radio,
		         salidaI:cmbFormatoImagenes.getValue(),
		         salidaT:cmbFormatoTexto.getValue(),
		         espacioCI:radEI.espI,
		         espacioCT:radET.espT,
		         maxAltoI:txtAltoPixelesI.getValue(),
		         maxAnchoI:txtAnchoPixelesI.getValue(),
		         maxAltoT:txtAltoPixelesT.getValue(),
		         maxAnchoT:txtAnchoPixelesT.getValue(),
		         transI:cmbTransI.getValue(),
		         transT:cmbTransT.getValue(),
		         tiposColor:cmbMapeoT.getValue(),
		         espaciosColor:cmbMapeoE.getValue(),
		         responsableEsc:cmbEscResponsable.getValue(),
		         metodoEsc:cmbEscMetodo.getValue(),
		         hints:chbEscHints.getValue(),
		         escritor:cmbEscritor.getValue(),
		         calidad:Ext.getCmp('txtEscCalidad').getValue()	
			});
			configModel = Ext.JSON.encode(configModel.data);			
			Ext.Ajax.request({  
					url: rutaServlet,  
					method: 'POST',  
					 success: successAjaxFnN,   
					timeout: 30000,  
					params: {  
					action: 'editConfigPDF',
					select: cmbGavetas.getValue(),
					configModel:configModel
					 }});
		}
		
});//Termina Funcion OnReady