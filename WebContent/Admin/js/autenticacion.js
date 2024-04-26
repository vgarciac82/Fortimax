/*
	Para mandarlo llamar Incluir el js.
	
	Funcion que realiza cuando das click en Autenticarse  accionLogin();   
	
	Obtener datos llenados: 					txtUsuario.getValue();
												txtContrase.getValue();
												
	Modificar Mensaje de Aviso:					
												Asignarle el mensaje.	
												var msgAdvertencia="";
	
	Mostrarlo:
												Llamarlo con page1Aut.show();
												Ocultar la segunta pagina page2Aut.hide();

*/

var lblAdvertencia = new Ext.form.Label({
     id:'lblAdvertencia',
     html:msgAdvertencia,
     shadow:true
 });
 var btnSiguiente=Ext.create('Ext.button.Button',{
		text: 'Aceptar',
     handler: function() {
    	 page1Aut.hide();
			page2Aut.show();
     }
	});
 var btnCancelarAceptarUnidad=Ext.create('Ext.button.Button',{
		text: 'Cancelar',
  handler: function() {
	  txtUsuario.setValue('');
		txtContrase.setValue('');
      page1Aut.hide();
  }
	});
 var btnCancelarLogin=Ext.create('Ext.button.Button',{
	text:'Cancelar',
	handler:function(){
		page2Aut.hide();
		txtUsuario.setValue('');
		txtContrase.setValue('');
	}
 });
 var btnLogin=Ext.create('Ext.button.Button',{
		text:'Proceder',
		formBind: true,
		handler:function(){
			accionLogin();
		}
	 });	
 var txtUsuario= Ext.create('Ext.form.field.Text',{
		id:'txtUsuario',
		name:'txtUsuario',
		fieldLabel:'Usuario',
		margin:'20 0 0 0',
		allowBlank:false
	});
	var txtContrase= Ext.create('Ext.form.field.Text',{
		id:'txtContrase',
		name:'txtContrase',
		margin:'5 0 0 0',
		fieldLabel:'Password',
		inputType: 'password',
		allowBlank:false
	});
var page1Aut = Ext.create('Ext.window.Window', {
	    title: 'Activar Volumen',
	    height: 400,
	    width: 300,
	    closable : false,
	    layout: 'anchor',
	    resizable :false,
	    items: [
	           { xtype: 'fieldset',layout:'fit',height:325,style:{borderStyle:'none'},name:'fieldsetMensaje' ,items:[lblAdvertencia] },
	           { xtype: 'toolbar' ,layout:'hbox',style:{borderStyle:'none'},items:[btnCancelarAceptarUnidad,'->',btnSiguiente] }//Propiedades Basicas de FieldSet 
	           ]
	});
	var page2Aut = Ext.create('Ext.window.Window', {
	    title: 'Autenticarse',
	    height: 200,
	    width: 300,
	    closable : false,
	    layout: 'anchor',
	    resizable :false,
	    items: [Ext.create('Ext.form.Panel',{ baseCls:'fmrLog', items:[
	            { xtype: 'fieldset',layout:'anchor',height:130,style:{borderStyle:'none'},name:'fieldsetMensaje' ,items:[txtUsuario,txtContrase] },
	            { xtype: 'toolbar' ,layout:'hbox',style:{borderStyle:'none'},items:[btnCancelarLogin,'->',btnLogin] }
	    ]})]
	});