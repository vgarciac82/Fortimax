Ext.define('FMX.view.oeste.Arbol', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.Arbol',
    
    requires: ['FMX.store.NodosMisDocumentos',
    			'FMX.store.NodosExpedientes',
    			'FMX.store.NodosGavetas'],

		
	config: {
	//id:'Arbol',
	title: 'Arbol',
	iconCls: 'nav',
	layout: 'fit',
	border:false
	},
	
	initComponent: function() {
	var me = this;	
	
	 var nodos = null;
	
	if(me.id=='ArbolGavetas')
		nodos = Ext.create('FMX.store.NodosGavetas');

	if(me.id=='ArbolMisDocumentos')
		nodos = Ext.create('FMX.store.NodosMisDocumentos');
		
	if(me.id=='ArbolExpediente')
   		nodos = Ext.create('FMX.store.NodosExpedientes');
	
	me.items=[
		{
			xtype:'treepanel',
			//id:'treepanel'+ me.id,
			cls:'nodoselectedbold',
			useArrows: true,
			rootVisible: false,	
			border:false,
			height: 420,
			store: nodos,	
			viewConfig: {
				plugins: {
					ptype: 'treeviewdragdrop'
				}
			},
			dockedItems: [{
				//id:'docktreeExpediente',
				dock: 'top',
				xtype: 'toolbar',
				items: ['->',{
					text: null, //Expandir
					iconCls: 'expandir',
					handler: function(){
						this.up('treepanel').collapseAll();
						this.up('treepanel').expandAll();
					}
				}, 
				{
					text: null, //Colapsar
					iconCls: 'colapsar',
					handler: function(){
						this.up('treepanel').collapseAll();
					}
				},
				{
					xtype:'button',
					accion:'toolBarActualizarArbol',
					text: null,
					iconCls:'btnActualizar16x16'
				}
				
				]
			}],			
			listeners:{
				itemcontextmenu:function( gr, record, item, index, e, eOpts ){
					 e.stopEvent();
					 if(FMX.controller.VariablesEntorno.esFortimax)
						 	var contextMenu = Ext.create('Ext.menu.Menu',{
								  items: [
								   {text: 'Nueva carpeta',iconCls: 'menucontextnuevacarpeta',handler:function(){addC(record,item,index);}},
								   {text: 'Nuevo documento',iconCls: 'menucontextnuevodocumento',handler:function(){addD(record,item,index);}},
								   {text: 'Modificar',iconCls: 'menucontextmodificar',handler:function(){addD(record,item,index);}},
								   {text: 'Cortar',iconCls: 'menucontextcortar',handler:function(){addD(record,item,index);}},
								   {text: 'Copiar',iconCls: 'menucontextcopiar',handler:function(){addD(record,item,index);}},
								   {text: 'Pegar',iconCls: 'menucontextpegar',handler:function(){addD(record,item,index);}},						   
								   {text: 'Eliminar',iconCls: 'menucontexteliminar',handler:function(){del(record,item,index);}}	          
								   ]
								});
							contextMenu.showAt(e.xy);
				}, 				
				load: function() {
					var dt = this.down('toolbar');
					dt.setDisabled(false);
				},
				
				select: function() {
					//Establece el arbol activo.
					FMX.controller.VariablesEntorno.arbolSeleccionado = this.id;
					console.log('Arbol activo: ' + FMX.controller.VariablesEntorno.arbolSeleccionado);
					//tree_select=this.id;
				}
			}
		}
	
	];
	
	
	
	this.callParent(arguments);
	}

});