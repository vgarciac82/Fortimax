/*
 * Variables
 */
var camposArr = new Array();
function creaTxt(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel){
	camposArr.push(NombreO);
	var requerido=RequeridoO;
	requerido=!requerido;
	var editable=EditableO;
	editable=!editable;
	var ast='';
	if(!requerido)
		ast="<span class='red'>* </span>";
	PanelRenderO.add(
		Ext.create('Ext.form.field.Text', {	
		id:NombreO,
		name:NombreO,
		cls:'textoForm',
		fieldLabel:EtiquetaO+ast,
    	allowBlank:requerido, 	
    	emptyText:NombreO,
    	maxLength:TamanoO,
    	value:ValorO,
    	enforceMaxLength:false,
    	readOnly:editable,
    	labelWidth:200,
    	labelAlign:'left',
		 width:TamanoO<36?300+(TamanoO*10):650,
		 maxWidth:anchoPanel,
		 margin:'5 0 0 20'
}));
}
function creaDate(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel){
	camposArr.push(NombreO);
	var editable=EditableO;
	editable=!editable;
	var ast='';
	if(RequeridoO)
		ast="<span class='red'>* </span>";
	var fech;
	if(ValorO=="")
		fech=null;
	else{
		var fe=new Date();
		var fechaString = ValorO.split(' ');
		fe=Ext.Date.parse(fechaString[0], 'Y-m-d');
		fech=fe;
	}
	PanelRenderO.add(Ext.create('Ext.form.field.Date',{
		id:NombreO,
        name:NombreO,
		fieldLabel:EtiquetaO+ast,
		 disabled:editable,
		 altFormats:'Y-m-d',
		 submitFormat: 'Y-m-d',
		 value: fech,//fech,
		 format:'Y/m/d',
		 cls:'textoForm',
		 editable:false,
		 labelWidth:200,
		 width:320,
		 readOnly:editable,
		 margin:'5 0 0 20',
		 maxWidth:anchoPanel,
		 labelAlign:'left'
	}));

}


function creaLista(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,ListaO,anchoPanel,rutaAServlet){
	
	camposArr.push(NombreO);
	Ext.define(NombreO+'Model', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'id', type: 'string'},     
	         {name: 'nombre', type: 'string'}
	     ]
	 });
	    Ext.create('Ext.data.Store', { 
		id:NombreO+'Store',
		name:NombreO+'Store',
        model: NombreO+'Model', 
        proxy: { 
            type: 'ajax', 
            url: rutaAServlet,						
            	reader: { 										
                type: 'json', 
                root: 'lista'
            },
            extraParams: 
	         {
	              action: 'getListas',
	              select:ListaO
	          } 
        } ,
        autoLoad:true
    });
	    var ast='';
		if(RequeridoO)
			ast="<span class='red'>* </span>";
				
		RequeridoO=!RequeridoO;		
		var editable=EditableO;
		editable=!editable;
		
	PanelRenderO.add(
			Ext.create('Ext.form.ComboBox', {
				id:NombreO,
				name:NombreO,
				cls:'textoForm',
			    fieldLabel:EtiquetaO+ast,
			    store:NombreO+'Store',
			    emptyText:'Selecciona...',
			    displayField: 'nombre',
			    editable: false,
			    valueField: 'nombre',
			    allowBlank:RequeridoO,
			    labelWidth:200,
				 width:350,
				 value:ValorO,
				// readOnly:editable,
				 margin:'5 0 0 20',
				 maxWidth:anchoPanel,
				 labelAlign:'left'
			})
	);
}
function creaNumber(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,TipoO,anchoPanel){
	camposArr.push(NombreO);
	var requerido=RequeridoO;
	requerido=!requerido;
	var editable=EditableO;
	editable=!editable;
	var ast='';
	if(!requerido)
		ast="<span class='red'>* </span>";
	
	var Decimales=false;
	if(TipoO=='Decimal'||TipoO=='Doble'){
		Decimales=true;
	}
	PanelRenderO.add(
			Ext.create('Ext.form.field.Number', {	
			id:NombreO,
			name:NombreO,
			hideTrigger:true,
			decimalPrecision:2,
			decimalSeparator:'.',
			allowDecimals:Decimales,
			fieldLabel:EtiquetaO+ast,
	    	allowBlank:requerido,
	    	emptyText:NombreO,
	    	maxLength:TamanoO,
	    	value:ValorO,
	    	cls:'textoForm',
	    	enforceMaxLength:true,    	
	    	readOnly:editable,
	    	labelWidth:200,
	    	width:TamanoO<36?300+(TamanoO*10):650,
			 //regex:Decimales,
			 margin:'5 0 0 20',
			 maxWidth:anchoPanel,
			 labelAlign:'left'
	}));
//	PanelRenderO.add(
//		Ext.create('Ext.form.field.Number',{
//        name: NombreO,
//        fieldLabel:ast+EtiquetaO,
//        minValue: 0, 
//        hideTrigger: true,
//        keyNavEnabled: false,
//        mouseWheelEnabled: false,
//        allowDecimals:true,
//        decimalPrecision:2,
//        allowBlank:RequeridoO,
//	    labelWidth:200,
//		 width:400
//		})	
//	);	
}
function creaMemo(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel){
		camposArr.push(NombreO);
		var requerido=RequeridoO;
		requerido=!requerido;
		var editable=EditableO;
		editable=!editable;
		var ast='';
		if(!requerido)
			ast="<span class='red'>* </span>";
		PanelRenderO.add(
	Ext.create('Ext.form.field.TextArea',{
	 	grow      : true,
	 	id:NombreO,
		name:NombreO,
		fieldLabel:EtiquetaO+ast,
		allowBlank:requerido,
    	emptyText:NombreO,
    	cls:'textoForm',
    	maxLength:TamanoO,
    	value:ValorO,
    	enforceMaxLength:false,
    	readOnly:editable,
    	labelWidth:200,
		 width:600,
		 height:100,
		 margin:'5 0 0 20',
		 maxWidth:anchoPanel,
		 labelAlign:'left'
 }));
}
function creaCheck(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel){
	camposArr.push(NombreO);
	var requerido=RequeridoO;
	requerido=!requerido;
	var editable=EditableO;
	editable=!editable;
	var ast='';
	if(!requerido)
		ast="<span class='red'>* </span>";
	var val=true;		
	if(ValorO!="true")
		val=false;
	PanelRenderO.add(
	Ext.create('Ext.form.field.Checkbox',{
		id:NombreO,
		name:NombreO,
		checked:val,
		 fieldLabel:EtiquetaO+ast,
		 disabled:editable,
	    	labelWidth:200,
	    	cls:'textoForm',
			 width:400,
			 margin:'5 0 0 20',
			 maxWidth:anchoPanel,
			 labelAlign:'left'
	 }));
}
/*
 * Funcion Externas
 */
function creaFormulario(TipoO,NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,ListaO,PanelRenderO,anchoPanel,rutaAServlet){
	if(ListaO=='-Ninguna-')
	{
		switch (TipoO) {
			case 'Texto (VarChar)':
				creaTxt(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel);
				break;
			case 'Fecha':
				creaDate(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel);
				break;
			case 'Memo':
				creaMemo(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel);
				break;
			case 'Check':
				creaCheck(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,anchoPanel);
				break;
			default:
				creaNumber(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,TipoO,anchoPanel);
		}
	}
	else{
		creaLista(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,ListaO,anchoPanel,rutaAServlet);
	}	
}
function getJsonForm(){
	if(camposArr.length>0){
		var dat="[";
		for(var i=0;i<camposArr.length;i++){
			dat=dat+"{'nombre':'"+camposArr[i]+"','valor':'"+Ext.getCmp(camposArr[i]).getValue()+"'}";
			if(i<camposArr.length-1){
				dat=dat+",";
			}
			else{
				dat=dat+"]";
			}
		}
		return dat;
	}
	else{
		return null;
	}
	
}
function getJsonTipoForm(){
	if(camposArr.length>0){
		var dat="[";
		var p=true;
		for(var i=0;i<camposArr.length;i++){
				if(Ext.getCmp(camposArr[i]).getValue()!=''&&Ext.getCmp(camposArr[i]).getValue()!=null){
					if(!p)
						dat+=",";
					else
						p=false;
					dat=dat+"{'nombre':'"+camposArr[i]+"','valor':'"+Ext.getCmp(camposArr[i]).getValue()+"','tipo':'"+Ext.getCmp(camposArr[i]).getXType()+"'}";
				}
				
				if(i==(camposArr.length-1)){
					dat=dat+"]";
				}
			
		}
		return dat;
	}
	else{
		return null;
	}
	
}
function reiniciaForm(){
	camposArr=[];
}
/*function obtenerDatos(){
	if(camposArr.length>0){
		var dato=new Array();
		for(var x=0;x<camposArr.length;x++){
			dato[camposArr[x].toLowerCase()]=Ext.getCmp(camposArr[x]).getValue();
		}
		return dato;
	}
	else{
		return null;
	}
}*/

