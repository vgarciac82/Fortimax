Ext.define('FMX.controller.FuncionesGlobales', {
    extend: 'Ext.app.Controller',
	
	statics: {
		ActualizaArbol: function(idtree){
			tree = Ext.getCmp(idtree);
			if(tree!=null){
				//Inhabilita todos los controles
				tree.getDockedItems('toolbar[dock="top"]').forEach(function(c){c.setDisabled(true);});
				tree.store.load();
				console.log('Arbol Actualizado: '+idtree);
			}
		}
    },
    config: {
        idtree: null
    },
    constructor: function(config) {
        this.initConfig(config);
        return this;
    }
});