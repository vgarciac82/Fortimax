<%@page import="com.syc.fortimax.hibernate.managers.imx_usuario_manager"%>
<%@page import="com.syc.fortimax.config.HibernateUtils"%>
<%@page import="com.syc.fortimax.config.Config"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page contentType="text/html; charset=UTF-8" language="java"%>
<html>
<head>
  <title>Administrador de Fortimax</title>
<!-- Framework:Extjs -->
<link rel="stylesheet" type="text/css" href="<%=Config.AdminCSS%>" />
<script type="text/javascript" src="<%=Config.AdminExt%>"></script>
<script type="text/javascript" src="<%=Config.AdminLo%>"></script>
<!-- /Framework:Extjs -->

<link href="../css/estilo-admin.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript">

<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
out.println("var basePath='"+basePath+"';");

//Valores por default para el control de las opciones del administrador.
//Estose cambiara por peticiones ajax con estas opciones en JSON
out.println("var MsjErrorBD=false;");
out.println("var MsjGenerandoBD=false;");
out.println("var MGavetas=false;");
out.println("var MUsuarios=false;");
out.println("var MVolumenes=false;");
out.println("var MEstructura=false;");
out.println("var MCatalogos=false;");
out.println("var MBitacora=false;");
out.println("var MInformacion=false;");
out.println("var MGruposLDAP=false;");
out.println("var MGruposLDAP=false;");
out.println("var MReporteMensual=false;");
out.println("var MGruposLDAP=false;");
out.println("var MGrupos=false;");
out.println("var MReporteMensual=false;");
out.println("var MConfiguracion=false;");

out.println("MGavetas=true;");
out.println("MUsuarios=true;");
out.println("MVolumenes=true;");
out.println("MEstructura=true;");
out.println("MCatalogos=true;");
out.println("MBitacora=true;");
out.println("MInformacion=true;");
out.println("MGruposLDAP=false;");
out.println("MGruposLDAP=false;");
out.println("MReporteMensual=false;");
out.println("MConfiguracion=true;");

if (Config.activeDirectoryConfigs.isUsed()) {
	out.println("MGruposLDAP=true;");
} else {
	out.println("MGrupos=true;");
} 
%>


	Ext.require(['*']);	
    Ext.onReady(function(){
        Ext.QuickTips.init();
        Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));
			
         var tabs= new Ext.tab.Panel ({
                region: 'center', 
                deferredRender: false,
                activeTab: 0, 
				margins: '0 5 0 5',

                items: [{
                    contentEl: 'Inicio',
                    title: 'Inicio',
                    closable: true,
                    autoScroll: true
                }]
            });
        var viewport = Ext.create('Ext.Viewport', {
            id: 'border-example',
            layout: 'border',
            items: [
            Ext.create('Ext.Component', {
                region: 'north',
                height: 75, 
                autoEl: {
                    tag: 'div',
                    html:'<div id="encabezado"><div id="logotipos"><img src="../imagenes/logotipo-usuario.png" alt="Logotipo"  height="75" align="middle" /> <span class="titulo">Bienvenido al Administrador de Fortimax</span></div></div>'

                }
            }), {
                region: 'south',
				title: 'Acerca de Fortimax ®',
                contentEl: 'piedepagina',
                split: true,
                iconCls: 'Informacion',
                height: 100,
                minSize: 100,
                maxSize: 200,
                collapsible: true,
                collapsed: true,
                margins: '0 0 0 0'
            },		
			{
                region: 'west',
                id: 'west-panel', // see Ext.getCmp() below
                title: 'Administracion',
                split: true,
                width: 200,
                minWidth: 175,
                maxWidth: 200,
                collapsible: true,
                margins: '0 0 0 5',
	            dockedItems: {
                    itemId: 'toolbar',
                    xtype: 'toolbar',
   			                items: [
										{ 
   										    text: 'Verificar BD',
   										 	iconCls:'Database',
   										 	iconAlign: 'left', 
   									        handler: function()
   									        {  	
   									        	var url = basePath+'/Admin/OperacionesServlet';
   									        	Ext.Ajax.request({  
   									        		url: url,
   													method: 'POST',
   													success: function(response, opts) {
   														var jsonData = Ext.JSON.decode(response.responseText);
   														var existBD = jsonData.exist;
   														
   														if(existBD) {
   		   									        		Ext.Ajax.request({  
   		   	   									        		url: url,
   		   	   													method: 'POST',
   		   	   													success: function(response, opts) {
   		   	   														var jsonData = Ext.JSON.decode(response.responseText);
   		   	   														var validBD = jsonData.success;
   		   	   														var validationMessage = jsonData.message;
   		   	   														
   		   	   														if (validBD) {	
   		   									        					alert('Base de datos correcta');
   		   									     					} else {
   		   									     						Ext.MessageBox.confirm('Creacion de BD Fortimax', 
   											        		    	   		'La base de datos de Fortimax no se encuentra creada o se encuentra incompleta, ¿Desea crearla?\n<br /><br />'+validationMessage, CrearBD);
   		   									     					}
   		   	   													},   
   		   	   													timeout: 30000,  
   		   	   													params: {  
   		   	   														action: "validateBD"
   		   	   													}
   		   	   									        	});
   		   									        	} else {
   		   									        		alert('No se pudo establecer conexion con la BD, verifique lo siguiente:\n'+
   		   									         			'-El servidor de BD esta encendido y disponible en red.\n'+
   		   									                 	'-Los datos de acceso son correctos.\n'+
   		   									                 	'-Se tienen privilegios de creacion/modificacion de Instancias de BD.\n');
   		   									        	}
   													},   
   													timeout: 30000,  
   													params: {  
   														action: "existBD"
   													}
   									        	});
   	    			                 		}
   										},
   	    			                	'->',
   										{ 
   										    text: 'Salir',
   										 	iconCls:'Cerrarsession',
   										 	iconAlign: 'right', 
   									        handler: function()
   									        {
   	    			                			window.open('login.jsp', '_top'); 
   	    			                 		}
   										    	
   										}
   					                ]
    				            },
                
                	items: [							
							{
								region: 'west',
								title: 'Gavetas',
								iconCls: 'Gavetas',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Crear", leaf: true },                       
								{ text: "Modificar", leaf: true }        
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);
									//Ext.Msg.alert(this.title, 'Click en: "' + record.data.value + '"');			
									} }
							},
							{
								region: 'west',
								title: 'Usuarios',
								iconCls: 'Usuarios',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Crear", leaf: true },                       
								{ text: "Modificar", leaf: true }        
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);		
									} }
							},
							{
								region: 'west',
								title: 'Grupos',
								iconCls: 'Grupos',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Crear", leaf: true },                       
								{ text: "Modificar", leaf: true }        
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);		
									} }
							},
							{
								region: 'west',
								title: 'Perfiles',
								iconCls: 'Perfiles',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Crear", leaf: true },
								{ text: "Modificar", leaf: true }                           
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);			
									} }
							},
							{
								region: 'west',
								title: 'Privilegios',
								iconCls: 'Privilegios',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Asignar", leaf: true },
								{ text: "LDAP", leaf: true }                           
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);			
									} }
							},
							{
								region: 'west',
								title: 'Configuracion',
								iconCls: 'Configuracion',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 85, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Variables", leaf: true },
								{ text: "Applets", leaf: true },
								{ text: "Correo", leaf: true }
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);			
									} }
							},
							{
								region: 'west',
								title: 'Catalogos',
								iconCls: 'Catalogos',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Crear", leaf: true },                       
								{ text: "Modificar", leaf: true }        
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);		
									} }
							},
							{
								region: 'west',
								title: 'Estructuras',
								iconCls: 'Estructuras',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [         
								//{ text: "Crear", leaf: true },
								{ text: "Creacion", leaf: true },
								{ text: "Asignar", leaf: true }
								//{ text: "Modificar", leaf: true }
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);			
									} }
							},
							{
								region: 'west',
								title: 'Volumenes',
								iconCls: 'Volumenes',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Crear", leaf: true }       
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);		
									} }
							},
							{
								region: 'west',
								title: 'Plantillas',
								iconCls: 'Documentos', 
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Crear", leaf: true },                       
								{ text: "Modificar", leaf: true }        
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);		
									} }
							},
							{
								region: 'west',
								title: 'Bitacora',
								iconCls: 'Bitacora',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "Consultar", leaf: true }                         
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);		
									} }
							},
							{
								region: 'west',
								title: 'Informacion',
								iconCls: 'Informacion',
								xtype: 'treepanel',
								collapsible: true,
								autoScroll: true,
								collapsed : true,
								split: true,							 
								width: 200,    
								height: 65, 
								rootVisible: false,
								lines: false,
								root: {        
								expanded: true,         
								children: [            
								{ text: "General", leaf: true },
								{ text: "Version", leaf: true }
								
								]},
								listeners : {  
									itemclick : function(view,record,item,index,event,options ){  
									addTab(this.title, record.data.text);			
									} }
							}
					]

            },
				
				tabs]//Tabs dinamicos de las posibles operaciones.
		
        });
		
		//Generacion de Tabs dinamicamente
		var index = 0;
		function addTab(categoria, opcion){
		
		fmx_page=opcion+categoria;

		switch(fmx_page)
			 {
			 case 'CrearGavetas':
				fmx_page="GavetasCampos.jsp";
				break;
			 	case 'ModificarGavetas':
				fmx_page="Gavetas.jsp";
				break;
			   	case 'CrearUsuarios':
				fmx_page="UsuariosCrear.jsp";
				break;
			   	case 'ModificarUsuarios':
				fmx_page="Usuarios.jsp";
				break;
			   	case 'CrearGrupos':
				fmx_page="GruposCrear.jsp";
				break;
			   	case 'ModificarGrupos':
				fmx_page="Grupos.jsp";
				break;
				case 'AsignarPrivilegios':
				fmx_page="UsuariosPrivilegios.jsp?privilegiosPage=true";
				break;
				case 'LDAPPrivilegios':
				fmx_page="GruposLDAP.jsp";
				break;
				case 'CrearCatalogos':
				fmx_page="catalogos.jsp";
				break;
				case 'ModificarCatalogos':
				fmx_page="catalogosList.jsp";
				break;
				case 'AsignarEstructuras':
				fmx_page="Estructuras.jsp";
				break;
				case 'CrearEstructuras':
				fmx_page="EstructurasCrear.jsp";
				break;
				case 'CreacionEstructuras':
				fmx_page="EstructurasCrearMVC.jsp";
				break;
				case 'ModificarEstructuras':
				fmx_page="Estructuras.jsp";
				break;
				case 'CrearVolumenes':
				fmx_page="Volumenes.jsp";
				break;
				case 'ConsultarBitacora':
				fmx_page="MostrarBitacora.jsp";
				break;
				case 'GeneralInformacion':
				fmx_page="Informacion.jsp";
				break;
				case 'VariablesConfiguracion':
				fmx_page="VariablesEntorno.jsp";
				break;
				case 'AppletsConfiguracion':
				fmx_page="ConfiguraPDFUpload.jsp";
				break;
				case 'CorreoConfiguracion':
				fmx_page="ConfiguraCorreo.jsp";
				break;
				case 'VersionInformacion':
				fmx_page="Version.jsp";
				break;
				case 'CrearPlantillas':
				fmx_page="documentosCrear.jsp";
				break;
				case 'ModificarPlantillas':
				fmx_page="documentosCatalogo.jsp";
				break;
				case 'ModificarPerfiles':
				fmx_page="catalogoPerfiles.jsp";
				break;
				case 'CrearPerfiles':
				fmx_page="crearPerfil.jsp";
				break;
			 default:
			   fmx_page="404.jsp";
			 }
		
		//Verificamos si el tab ya existe, no queremos más de un tab existente
		//que realize la misma función, de encontrarlo lo guardamos como 'tab'
		
		var title = opcion +' '+ categoria;
		var tab = tabs.items.findBy(function(i){
			return i.title == title;
		});
		var url = basePath+'/Admin/jsp/'+fmx_page;
		
		if(tab) {
			tabs.remove(tab.id); //El comportamiento actual si el tab ya existe es
			//eliminarlo y volverlo añadir, otro comportamiento contemplado pero que
			//da un comportamiento extraño es sencillamente darle foco, es curioso
			//porque si queremos recargar el tab forzosamente tenemos que cerrarlo
			//y volverlo a abrir, obviamente este caso es mejor pero hay que añadir un
			//botón de recarga en algún lado del tab para no tener que cerrarlo.
		}
			
        tabs.add({
            	title: title,
            	iconCls: categoria.toString(),
            	
				items:[	{
						region: 'west',
						xtype: 'panel',
						autoScroll: false,
						html: '<div style="height:100%; scrolling="yes" overflow:scroll !important;"><iframe src="'+basePath+'/Admin/jsp/'+fmx_page+'"  style="width:100%;height:100%;border:none;"></iframe></div>'  						
						}	
				],
				layout: 'fit',
            	closable:true
        	}).show();    
        } 
		
        function CrearBD(btn){
        if(btn=='yes'){
            Ext.MessageBox.show({
                title: 'BD Fortimax',
                msg: 'Creando la BD de Fortimax...',
                progressText: 'Inicializando DBMS...',
                width:300,
                progress:true,
                closable:false
            });
            
            var retJson=null;
            
            var progress = function(step){
                return function(){
                   if(step==51){
                        if (retJson==null) {
                        	Ext.MessageBox.hide();
                        	Ext.MessageBox.show({
                                title: 'Timeout',
                                msg: 'El servicio ya tardo mas de lo esperado',
                                width:300,
                                closable:true,
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.WARNING
                            });
                        }
                    }else{
                        var i = step/50;
                        Ext.MessageBox.updateProgress(i, Math.round(100*i)+'% Completado');
                        if(step==1) {
                        	Ext.Ajax.request({
                            	url: '../ConstructorDBServlet?action=build_db',
                                success: function(a){
                                retJson = Ext.decode(a.responseText);
                                },failure: function(f,a){
                                	Ext.MessageBox.hide();
                                	Ext.MessageBox.show({
                                        title: 'Error: No se puede conectar',
                                        msg: 'No se puede conectar al servicio de actualización de la Base de Datos',
                                        width:300,
                                        closable:true,
                                        buttons: Ext.MessageBox.OK,
                                        icon: Ext.MessageBox.WARNING
                                    });
                                }
                            });
                        }
                    }
               };
           };
           Ext.Ajax.on('requestcomplete', function (conn, response, options) {
        	   retJson = Ext.decode(response.responseText);
        	   Ext.MessageBox.hide();
        	   if (retJson.success) {
   				var width=800;            		
   				Ext.MessageBox.show({
       				title: 'BD Creada!',
       				msg: retJson.message,
       				width:width,
       				closable:true,
       				buttons: Ext.MessageBox.OK
   				});
   			} else {
   				Ext.MessageBox.show({
               		title: 'Error',
               		msg: retJson.message+'<br /><br />'+retJson.error,
               		width:300,
               		closable:true,
               		buttons: Ext.MessageBox.OK,
               		icon: Ext.MessageBox.WARNING
           		});
   			}  
           });
           for(var i=1;i<52;i++){
        	   setTimeout(progress(i), i*100);   
           }
        }
        else
            Ext.MessageBox.show({
                title: 'BD Fortimax',
                msg: 'No se creara la BD de Fortimax!',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING
            });
                       
        };
		
    });//Fin Ext js

    
</script>
</head>
<body>
    <div id="Inicio" class="x-hide-display">
    	<img src="../imagenes/banner2.jpg" align="right">
		<br>
		<p style="font-size: 2.2em"><b><br>Bienvenido al Administrador de Fortimax</b></p>
		<hr>
		<p>En esta seccion podra administrar y configurar Fortimax, para que se adecue a sus necesidades,
		entre las operaciones mas comunes que puede querer realizar estan: </p>
		<br><br>
		<p><b>Gavetas</b></p>
		<p>Crear y definir las gavetas.</p>
		<p>Crear gavetas.</p>
		<p>Seleccionar una gaveta.</p>
		<p>Actualizar y eliminar campos.</p>
		<p>Modificar gavetas.</p>
		<br>
		<p><b>Usuarios</b></p>
		<p>Administrar usuarios.</p>
		<p>Establecer cuotas por usuario.</p>
		<br>
		<p><b>Grupos</b></p>
		<p>Administracion de grupos de usuarios y sus privilegios.</p>
		<br>
		<p><b>Privilegios</b></p>
		<p>Administrar los privilegios de los usuarios.</p>
		<p>Otorgar privilegios por usuario.</p>
		<p>Otorgar privilegios por gaveta.</p>
		<br>
		<p><b>Catalogos</b></p>
		<p>Creacion de catalogos para asociacion con gavetas.</p>
		<br>
		<p><b>Estructuras</b></p>
		<p>Crear la estructura de las gavetas.</p>
		<p>Crear una estructura.</p>
		<p>Crear carpetas.</p>
		<p>Crear documentos.</p>
		<p>Eliminar documentos.</p>
		<br>
		<p><b>Volumenes</b></p>
		<p>Creacion de unidades de almacenamiento.</p>
		<br>
		<p><b>Bitacora</b></p>
		<p>Consulta de bitacora de operaciones de usuarios.</p>
		<br>
		<p><b>Informacion</b></p>
		<p>Informacion del sistema.</p>
    </div>
    <div id="piedepagina" class="x-hide-display">
        <p>D.R. ©2005-2011 SYC Constructores de Sistemas S.A. de C.V.| www.fortimax.com </p>
    </div>
</body>
</html>