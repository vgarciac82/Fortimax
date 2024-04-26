var generadorExtjs=new function(){
	//Variables
	 this.anchoTextBoxDefault=250;  		//Aplica Combobox,
	 this.anchoLabelTextBoxDefault=100;		//Aplica Combobox
	 
	 this.anchoTextAreaDefault=300;
	 this.altoTextAreaDefault=50;
	 this.anchoLabelTextAreaDefault=100;
	 
	 this.margenesDefault='5 0 0 5';
	 
	 this.anchoCheckBoxDefault=200;  		
	 this.anchoLabelCheckBoxDefault=100;
	 
	 this.anchoButtonDefault=80;
	 //Crear textBox
	 this.getTextBox=function(id,texto,ancho,anchoLabel,textoVacio,margenes){
	 	var textBox=new Ext.form.field.Text({
	 		id:id,
	 		name:id,
	 		width:!(ancho==null)?ancho:this.anchoTextBoxDefault,
	 		labelWidth:!(anchoLabel==null)?anchoLabel:this.anchoLabelTextBoxDefault,
	 		emptyText:textoVacio,
	 		fieldLabel:texto,
	 		margin:!(margenes==null)?margenes:this.margenesDefault
	 	});
	 	return textBox;
	 }
	 //Crear textArea
	 this.getTextArea=function(id,texto,ancho,alto,anchoLabel,textoVacio,margenes){
	 
	 	var textArea=new Ext.form.field.TextArea({
	 		id:id,
	 		name:id,
	 		width:!(ancho==null)?ancho:this.anchoTextAreaDefault,
	 		height:!(alto==null)?alto:this.altoTextAreaDefault,
	 		labelWidth:!(anchoLabel==null)?anchoLabel:this.anchoLabelTextAreaDefault,
	 		emptyText:textoVacio,
	 		fieldLabel:texto,
	 		margin:!(margenes==null)?margenes:this.margenesDefault
	 	});
	 	return textArea;
	 }
	 //Obtener datePicker
	 this.getDatePicker=function(id,texto,ancho,anchoLabel,margenes,valor){
	 	var datePicker=new Ext.form.field.Date({
	 		id:id,
	 		name:id,
	 		width:!(ancho==null)?ancho:this.anchoTextBoxDefault,
	 		labelWidth:!(anchoLabel==null)?anchoLabel:this.anchoLabelTextBoxDefault,
	 		fieldLabel:texto,
	 		margin:!(margenes==null)?margenes:this.margenesDefault,
	 		value:!(valor==null)?valor:''
	 	});
	 	return datePicker;
	 }
	 //Crear Combobox
	 this.getComboBox=function(id,texto,ancho,anchoLabel,margenes,textoVacio,campoMostrar,campoValor,valor,store){
	 	var comboBox=new Ext.form.ComboBox({
	 		id:id,
	 		name:id,
		    fieldLabel: texto,
		    store: store,
		    emptyText:textoVacio,
		    queryMode: 'local',
		    displayField: campoMostrar,
		    editable: false,
		    valueField: campoValor,
		    margin:!(margenes==null)?margenes:this.margenesDefault,
		    width:!(ancho==null)?ancho:this.anchoTextBoxDefault,
	 		labelWidth:!(anchoLabel==null)?anchoLabel:this.anchoLabelTextBoxDefault,
	 		value:!(valor==null)?valor:''
		});
		return comboBox;
	 }
	 //Crea Checkbox
	 this.getCheckBox=function(id,texto,margenes,valor){
	 	var checkBox=new Ext.form.field.Checkbox({
	 		id:id,
	 		name:id,
	 		boxLabel:texto,
	 		margin:!(margenes==null)?margenes:this.margenesDefault,
	 		checked:!(valor==null)?valor:false
	 	});
	 	return checkBox;
	 }
	 
	 //Crea boton
	 this.getButton=function(id,texto,ancho,margenes,icono,escala,ligadoForm){
	 	var Button=new Ext.button.Button({
	 		id:id,
	 		width:!(ancho==null)?ancho:this.anchoButtonDefault,
	 		scale:!(escala==null)?escala:"small",
	 		text:texto,
	 		margin:!(margenes==null)?margenes:this.margenesDefault,
	 		iconCls:icono,
	 		formBind:ligadoForm
	 	});
	 	return Button;
	 }
}