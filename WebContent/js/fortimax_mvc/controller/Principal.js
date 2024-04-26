Ext.define('FMX.controller.Principal', {
    extend: 'Ext.app.Controller',
	
	requires: ['FMX.controller.VariablesEntorno'],
	
	models: ['FortimaxNodo',
	'EspacioAsignadoUsuario'],
	
	stores:[
	'NodosExpedientes',
	'NodosGavetas',
	'NodosMisDocumentos',
	'PropiedadesNodo',
	'EspacioAsignado'
	],
	
	views:[
	'norte.PanelHerramientasPrincipal',
	'sur.PanelInferiorPrincipal',
	'este.TabPanelPropiedadesPrincipal',
	'oeste.PanelArbolesPrincipal',
	'centro.TabPanelVisorPrincipal',
	'oeste.GraficaEspacioUsuario'
    ],
	
	refs: [{
		ref : 'TabPanelVisorPrincipal',
        selector: 'TabPanelVisorPrincipal'
	}],	
	
    init: function() {
        //console.log('Inicializa usuarios, esto pasa antes de que la funciona "launch" sea llamada');		
		 this.control({
            'viewport > panel': {
                render: this.cuandoDibujePanel
            },
			'button[accion=toolBarBuscarDocumento]':{
				click: this.crearTabVisor	
			},
			'PanelArbolesPrincipal button[accion=toolBarActualizarArbol]':{
				click: this.actualizarArbol	
			},
			'button[accion=toolbarActualizartArboles]':{
				click: this.actualizarArboles	
			},
			'PanelArbolesPrincipal treepanel':{
				itemdblclick: this.abrirPestaña
			},
			'gridexpedientes gridview': {
             	expandbody: this.abrirExpediente,
             	collapsebody: this.cerrarExpediente
            },
            'TabPanelVisorPrincipal': {
             	tabchange: this.cambiarTabVisorPrincipal
            }
        });		
    },
    
    test:function(panel) {
        console.log('test funcionando');
    },
	
    cambiarTabVisorPrincipal:function(tabPanel, newCard, oldCard, eOpts) {
        console.log('Tab Cambiado '+newCard.nodo);
    },
    
    abrirPestaña:function(view,record,item,index,event,options ) {
		//console.log(arguments);
        //console.log('Abriendo NODO: :' + panel.id);
		FMX.controller.VariablesEntorno.nodoSeleccionado = record.data;

		
		if(record.data.leaf==true)
		{
			if(record.data.type=='document' )
				{
					console.log('Abriendo Documento: ' + record.data.id);
					this.getTabPanelVisorPrincipal().add(
					
						Ext.create('FMX.view.centro.Visualizador.TabPanelVisualizador',{
							title: 'D: '+record.data.text,
							nodo: record.data.id,
							iconCls:record.data.iconCls
						})
					).show();
				}
				
			if(record.data.type=='gaveta.hija') //TODO: Agregar atributo type a record.data.type !
				{
					console.log('Abriendo Gaveta: ' + record.data.id);
					this.getTabPanelVisorPrincipal().add(
					
						Ext.create('FMX.view.centro.Expedientes.PanelExpedientes',{
							title: 'G: '+record.data.text,
							gaveta: record.data.id,
							iconCls:record.data.iconCls
						})
					).show();
				}
    	}
    },
    
	cuandoDibujePanel:function(panel) {
		//console.log(arguments);
        console.log('Panel cargado :' + panel.id);
    },
    
    abrirExpediente:function(rowNode, record, expandRow) {
		panel = Ext.getCmp('ArbolExpediente');
		panel.expand();
		tree = panel.down('treepanel');
		tree.getRootNode().removeAll();
		tree.store.getProxy().extraParams.select = FMX.controller.VariablesEntorno.nodoSeleccionado.id+'_G'+record.get('ID_GABINETE');
		tree.store.load();
        console.log('Expediente AbiertoS ' +record.get('ID_GABINETE'));
    },
    
    cerrarExpediente:function(rowNode, record, expandRow) {
		panel = Ext.getCmp('ArbolExpediente');
		tree = panel.down('treepanel');
		tree.getRootNode().removeAll();
        console.log('Expediente Cerrado');
    },
	
	actualizarArbol:function(button,e,eOpts) {
			tree = button.up('treepanel');
			tree.getDockedItems('toolbar[dock="top"]').forEach(function(c){c.setDisabled(true);});
			tree.getRootNode().removeAll();
			//tree.store.getProxy().extraParams.usuario = usuario;
			tree.store.load();
			console.log('Arbol Actualizado: ' + tree.id);
    },
	
	actualizarArboles:function(button, e, eOpts) {
		var me=this;	
		var botones = Ext.ComponentQuery.query('button[accion=toolBarActualizarArbol]');
		botones.forEach(function(button){me.actualizarArbol(button);});	
    },
	
	crearTabVisor:function() {	
		console.log('entro');
		//la manera ruda.
		//var view = Ext.ComponentQuery.query('TabPanelVisorPrincipal')[0];
		var nodo = FMX.controller.VariablesEntorno.nodoSeleccionado;
		var tabsprincipales = this.getTabPanelVisorPrincipal();
		
		this.getTabPanelVisorPrincipal().add(			
			Ext.create('FMX.view.centro.Documentos.PanelDocumentos',{
				title: 'B: '+nodo.id,
				nodo: nodo,
				id: 'busqueda_'+nodo.id,
				iconCls: nodo.iconCls
			})
		).show();
	
		if(!nodo.leaf)
		return;

		/*fmx_page='ifimax';

		switch(fmx_page)
			 {
		 	case 'ifimax':
				url = rutaServletIfimax;
				break;			
		 	default:
		   	url = rutaPageError404;
		 	}*/
	
	var id = 'tab_'+Nodo.id;
	var tab = tabsprincipales.items.findBy(function(i){
		return i.id == id;
	});
	
	if(tab) {
		tabsprincipales.remove(tab.id); 
	}

	/*if(Nodo.iconCls == 'nodo_empty' && !ifimax_privilegioEditarExpediente && ifimax){
		Ext.MessageBox.alert('Notificacion', 'Documento vacio');
		console.log('Documento sin contenido');
		return;
	}

	desplegar_propiedades(false);*/
/*
	if(Nodo.type=='document')
		tabsprincipales.add({
			id:id,
			title: 'D: '+Nodo.text,
			iconCls: Nodo.iconCls,		
			items:[	
					{
						xtype : 'component',
						region: 'west',
						autoScroll: false,
						style: {
								border:'none'
						},
						autoEl : {
									tag : "iframe",
									src : '?accion=ifimax_viewer&select='+Nodo.id
								}
					}
			
			],
			layout: 'fit',
			closable:true
		}).show();    
	
	else if(Nodo.type=='gaveta.hija')
		tabsprincipales.add(
					
						Ext.create('FMX.view.centro.Documentos.PanelDocumentos',{
							title: 'G: '+record.data.text,
							iconCls:record.data.iconCls
						})
					).show();*/
/*	
		tabsprincipales.add({
		id:id,
    	title: 'E: '+Nodo.text,
    	iconCls: Nodo.iconCls,	
		items:
		[
			{
	        	xtype : 'component',
	        	region: 'west',
	        	autoScroll: false,
		        style: {
		        			border:'none'
			    },
	        	autoEl : {
	            			tag : "iframe", //cambiar esto por una peticion a un servlet !
	            			src : '../jsp/ResultadosBusquedaExpedientes.jsp?select='+Nodo.id+'&tipoAccion=expedientes'
	        			}
			}
		],
		layout: 'fit',
    	closable:true
	}).show(); */
			
    },
	
    ActualizaArbol: function(idtree){
    	tree = Ext.getCmp(idtree);
    	if(tree!=null){
    		//Inhabilita todos los controles
    		tree.getDockedItems('toolbar[dock="top"]').forEach(function(c){c.setDisabled(true);});
    		tree.getRootNode().removeAll();
    		tree.store.load();
    		console.log('Arbol Actualizado: '+idtree);
    	}
    }
});