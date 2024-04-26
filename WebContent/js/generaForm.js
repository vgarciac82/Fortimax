/*
 * Variables
 */
var camposArr = new Array();
function creaTxt(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO){
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
		fieldLabel:EtiquetaO+ast,
    	allowBlank:requerido, 	
    	emptyText:NombreO,
    	maxLength:TamanoO,
    	value:ValorO,
    	cls:'textoForm',
    	enforceMaxLength:true,
    	//readOnly:editable,
    	labelWidth:200,
    	width:TamanoO<36?300+(TamanoO*10):650,
		 margin:'5 0 0 20'
}));
}
function creaDate(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO){
	camposArr.push(NombreO);
	var editable=EditableO;
	editable=!editable;
	var ast='';
	if(RequeridoO)
		ast="<span class='red'>* </span>";
	
	var fech;
	if(ValorO=="")
		fech=new Date();
	else
		fech=new Date(ValorO);
	PanelRenderO.add(Ext.create('Ext.form.field.Date',{
		id:NombreO,
        name:NombreO,
		fieldLabel:EtiquetaO+ast,
		 disabled:editable,
		 value: fech,
		 editable:false,
		 labelWidth:200,
		 width:300,
		 cls:'textoForm',
		 readOnly:editable,
		 altFormats:'Y-m-d',
		 submitFormat: 'Y-m-d',
		 format:'Y-m-d',
		 margin:'5 0 0 20'
	}));

}


function creaLista(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,ListaO){
	
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
            url: rutaServlet,//'../jsonPrueba/lista.json',  							
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
			    fieldLabel: EtiquetaO+ast,
			    store:NombreO+'Store',
			    emptyText:'Selecciona...',
			    displayField: 'nombre',
			    editable: false,
			    cls:'textoForm',
			    valueField: 'nombre',
			    allowBlank:RequeridoO,
			    labelWidth:200,
				 width:350,
				 value:ValorO,
				 readOnly:editable,
				 margin:'5 0 0 20'
			})
	);
}
function creaNumber(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,TipoO){
	camposArr.push(NombreO);
	var requerido=RequeridoO;
	requerido=!requerido;
	var editable=EditableO;
	editable=!editable;
	var ast='';
	if(!requerido)
		ast="<span class='red'>* </span>";
	
	PanelRenderO.add(
			Ext.create('Ext.form.field.Number', {	
			id:NombreO,
			name:NombreO,
			fieldLabel:EtiquetaO+ast,
	    	allowBlank:requerido,
	    	emptyText:NombreO,
	    	maxLength:TamanoO,
	    	value:ValorO,
	    	enforceMaxLength:true,    	
	    	readOnly:editable,
	    	cls:'textoForm',
	    	labelWidth:200,
	    	width:TamanoO<36?300+(TamanoO*10):650,
	    	keyNavEnabled: false,
			mouseWheelEnabled: false,
			decimalSeparator:'.',
			decimalPrecision:2,
			allowDecimals : TipoO=='Decimal'||TipoO=='Doble',
			margin:'5 0 0 20'
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
function creaMemo(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO){
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
    	maxLength:TamanoO,
    	value:ValorO,
    	enforceMaxLength:true,
    	cls:'textoForm',
    	readOnly:editable,
    	labelWidth:200,
		 width:600,
		 height:100,
		 margin:'5 0 0 20'
 }));
}
function creaCheck(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO){
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
			 margin:'5 0 0 20'
	 }));
}
/*
 * Funcion Externas
 */
function creaFormulario(TipoO,NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,ListaO,PanelRenderO){
	if(ListaO=='-Ninguna-'||ListaO=='') {
		switch (TipoO) {
			case 'String':
			case 'Texto': 
			case 'Texto (VarChar)':
				creaTxt(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO);
				break;
			case 'Date':
			case 'Fecha':
			case 'Fecha/Hora':
				creaDate(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO);
				break;
			case 'Memo':
				creaMemo(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO);
				break;
			case 'Boolean':
			case 'Check':
				creaCheck(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO);
				break;
			default:
				creaNumber(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,TipoO);
		}
	} else {
//		alert('El campo '+NombreO+' es lista');
		creaLista(NombreO,EtiquetaO,TamanoO,ValorO,RequeridoO,EditableO,PanelRenderO,ListaO);
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
function reiniciaForm(){
	camposArr=[];
}
