/*
 * 
 */
 var selectD="";
 var nombreReporteD="";
 var columnaBuscarD="";
 var filtroD="";
 var actionD="";
 var fortimaxD="";
 var iconUrl="";
Ext.ux.DownloadButton = Ext.extend(Ext.Button, {
	action:"",
    select:"",
    nombreReporte:"",
    columnaBuscar:"",
    Filtro:"",
    alias:'Descargar',
    disabled:false,//Eliminar cuando este listo el servlet
    fortimax:false,
    listeners:{
    	afterrender:function(){
    		if(this.fortimax){
    			iconUrl='../imagenes/iconos';
    		}
    		else{
    			iconUrl='../../imagenes/iconos'
    		}
    	}
    },
    handler:function(){
    	 selectD=this.select;
 		nombreReporteD=this.nombreReporte;
 		columnaBuscarD=this.columnaBuscar
 		var txtFiltro=Ext.getCmp(this.Filtro);
 		filtroD=txtFiltro.getValue();
 		actionD=this.action;
 		
    },
     menu: new Ext.menu.Menu({
        items: [{
            text: 'Exportar a PDF',
            listeners:{
            	beforerender:function(_this,e){
            		_this.icon=iconUrl+'/pdf.png';
            	}
            },
            handler:function(){
            	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
            	var parametros="?action="+actionD+"&select="+selectD+"&extencion=pdf&nombre=" +
            			""+nombreReporteD+"&buscar="+columnaBuscarD+"&filtro="+filtroD;
            			
            var url=basePath+"/ReporteServlet"+parametros;
			var downloadFrame = document.createElement("iframe"); 
			downloadFrame.setAttribute('src',url);
			downloadFrame.setAttribute('class',"frameOculto"); 
			downloadFrame.setAttribute('style',"width:0px;heigth:0px;visibility:hidden;display:none;"); 
			document.body.appendChild(downloadFrame)
			Ext.getBody().unmask();
            }
        },
        {
            text: 'Exportar a Excel',
             listeners:{
            	beforerender:function(_this,e){
            		_this.icon=iconUrl+'/xls.png';
            	}
            },
            handler:function(){
            	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
            	var parametros="?action="+actionD+"&select="+selectD+"&extencion=xls&nombre=" +
            			""+nombreReporteD+"&buscar="+columnaBuscarD+"&filtro="+filtroD;
            			
            var url=basePath+"/ReporteServlet"+parametros;
			var downloadFrame = document.createElement("iframe"); 
			downloadFrame.setAttribute('src',url);
			downloadFrame.setAttribute('class',"frameOculto"); 
			downloadFrame.setAttribute('style',"width:0px;heigth:0px;visibility:hidden;display:none;"); 
			document.body.appendChild(downloadFrame)
			Ext.getBody().unmask();
            }
        },
        {
            text: 'Exportar a Word',
             listeners:{
            	beforerender:function(_this,e){
            		_this.icon=iconUrl+'/doc.png';
            	}
            },
            handler:function(){
            	Ext.getBody().mask('Espere por favor...', 'x-mask-loading');
            	var parametros="?action="+actionD+"&select="+selectD+"&extencion=doc&nombre=" +
            			""+nombreReporteD+"&buscar="+columnaBuscarD+"&filtro="+filtroD;
            			
            var url=basePath+"/ReporteServlet"+parametros;
			var downloadFrame = document.createElement("iframe"); 
			downloadFrame.setAttribute('src',url);
			downloadFrame.setAttribute('class',"frameOculto"); 
			downloadFrame.setAttribute('style',"width:0px;heigth:0px;visibility:hidden;display:none;"); 
			document.body.appendChild(downloadFrame)
			Ext.getBody().unmask();
            }
        }]
    })
}); 


