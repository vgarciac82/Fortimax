Ext.onReady(function(){
	var pantalla=Ext.getBody().getViewSize();
	/*
	 * Objetos
	 */
	var lblTexto= new Ext.form.Label({
	     id:'lblTexto',
	     componentCls:'lblTextoComponentCls',
	     cls:'lblTextoCls',
	      html:'<br />Al descargar la utilería para digitalización aparecerá un cuadro de diálogo con las siguientes opciones: <br /><br />' +
	      		'* Abrir. <br />* Guardar.<br />* Cancelar.<br /><br />' +
	      		'Seleccione el botón <strong>Guardar</strong> e indique la ubicación donde desea almacenarlo, ' +
	      		'posteriormente si ya tiene instalado el <i>Java Runtime Enviroment (JRE)  1.4 o mayor</i> de ' +
	      		'<strong>doble clic</strong> sobre el <strong>archivo</strong> descargado (<strong>scansetup.jar</strong>). <br /><br />' +
	      		'<strong>Nota:</strong> También puede ejecutarse por línea de comando: <i>javaw -jar	Ubicación_de_su_descarga\scansetup.jar</i><br />&nbsp; ',
	      shadow:true
	  });
	  var btnDescargar=new Ext.button.Button({
		id:'btnDescargar',
		scale: 'medium',
		iconAlign:'top',
		iconCls:'btnDescargarCls',
		text:'Descargar archivo',
		handler:function(){
			panelPrincipal.el.mask('Espere por favor...', 'x-mask-loading');
			descargarArcvivo();
		}
	});
	/*
	 * Paneles
	 */
	var fieldSet=new Ext.form.FieldSet({
		id:'fieldSet',
		name:'fieldSet',
		cls:'fieldSetCls',
		items:[lblTexto]
	});
	var panelPrincipal=new Ext.panel.Panel({
		id:'panelPrincipal',
		name:'panelPrincipal',
		cls:'panelPrincipalCls',
		region:'center',
		title:'Descarga utileria de digitalizacion',
		height:350,
		width:pantalla.width-30,
		autoScroll:true,
		items:[fieldSet,
		{xtype:'fieldset',cls:'fieldSetBotonCls',border:0,items:[btnDescargar]}],
		renderTo:Ext.getBody()
	});
	/*
	 * Funciones
	 */
	Ext.EventManager.onWindowResize(function () {
       panelPrincipal.setWidth(Ext.getBody().getViewSize().width-30);
    });
    function descargarArcvivo(){
    	var url=basePath+"/jars/scansetup.jar";
			var downloadFrame = document.createElement("iframe"); 
			downloadFrame.setAttribute('src',url);
			downloadFrame.setAttribute('class',"frameOculto"); 
			downloadFrame.setAttribute('style',"width:0px;heigth:0px;visibility:hidden;display:none;"); 
			document.body.appendChild(downloadFrame)
			panelPrincipal.el.unmask();
    }
});