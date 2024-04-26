




function mostrarVentana(titulo,mensaje,hCookie,nCookie,passC,accion,parametro){
	Ext.getBody().mask();
	var cont=0;
	var lblMensaje = new Ext.form.Label({
	     id:'lblMensaje',
//	     margin:'0 0 0 7',
	     html:mensaje,
	     shadow:true
	 });
	var txtPass=Ext.create('Ext.form.field.Text',{
		id:'txtPass',
		name:'txtPass',
		margin:'15 0 0 5',
		fieldLabel:'Contrase&ntilde;a',
		inputType: 'password',
		allowBlank:false,
		labelAlign:'top',
		listeners:{  
	        scope:this,  
	        specialkey: function(f,e){  
	           if(e.getKey()==13){
	        	  log();
	           } 
	        }  
	    }  
	});
	var chbC=Ext.create('Ext.form.field.Checkbox',{
		id:'chbC',
		name:'chbC',
		labelAlign:'left',
		checked:true,
		margin:'0 0 0 5',
		labelWidth:120,
		 height: 10,
		 fieldLabel:'Guardar contrase&ntilde;a',
		 disabled:false
	 });
	var btnAceptar=Ext.create('Ext.button.Button',{
		text:'Aceptar',
		iconCls:'aceptar',
		handler:function(){
			log();
		}
	 });
	var ventana=Ext.create('Ext.window.Window', {
	    title: titulo,
	    height: 190,
	    width: 200,
	    flotable:false,
	    draggable:false,
	    layout: 'anchor',
	    items:[lblMensaje,txtPass,chbC],
	    bbar:['->',btnAceptar],
	    listeners:{
	    	beforeclose:function(){
	    		Ext.getBody().unmask();
	    	},
	        afterrender: function(window, eOpts){
	        	Ext.create('Ext.util.KeyNav', window.getEl(), {
	        		"esc" : function(e){
	        			window.close();
	        		},
	        		scope: this
	        	});
	        }
	    }
	}).show();

	function log(){
		if(txtPass.getValue()!=""){
			if(passC==hex_md5(txtPass.getValue())){
//				Ext.getBody().unmask();
				nCookie=hex_md5(nCookie);
				if(chbC.getValue()){
				escribe_cookie(nCookie,hex_md5(nCookie.substring(0,2)));
				}
				window.open(accion,parametro);

			}
			else{
				cont++;
				txtPass.setValue('');
				Ext.Msg.alert({title:'Error', msg:'Contrase&ntilde;a incorrecta, vuelva a intentarlo.',
					height:180,width:170,buttons: Ext.Msg.OK,
				     icon: Ext.Msg.QUESTION});
				if(cont>=3){
					Ext.getBody().unmask();
					ventana.close();
				}
					
			}
		}
	}
	function escribe_cookie(nombre,nombre2){
		var now = new Date();
		var expiry = new Date(now.getTime() + hCookie * 60 * 60 * 1000);
		Ext.util.Cookies.set(nombre,nombre,expiry);
		Ext.util.Cookies.set(nombre2,nombre2,expiry);
		Ext.getBody().unmask();
	}

}
function verificaCookie(nCookie,accion,parametro){
	Ext.getBody().mask();
	var result=false;
	nCookie=hex_md5(nCookie);
	var cok=Ext.util.Cookies.get(nCookie);
	var cok2=Ext.util.Cookies.get(hex_md5(nCookie.substring(0,2)));
	if(cok!=null){
		if(cok2!=null){
			window.open(accion,parametro);
			result=true;
		}
		else{
			result=false;
		}
	}
	else{
		result=false;
	}
	return result;
	Ext.getBody().unmask();
}