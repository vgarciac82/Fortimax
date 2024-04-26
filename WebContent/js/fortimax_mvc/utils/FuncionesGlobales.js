Ext.define('FMX.utils.FuncionesGlobales', {
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
		},
		
		restoreWindow:
			function( window, eOpts ) {
				window.expand('',false);
				if(window.collapseMemento!=null) {
					window.setHeight(window.collapseMemento.height);
					window.setWidth(window.collapseMemento.width);
				}
				window.center();
				/*
				var parent = typeof(window.parent) == 'undefined' ? Ext.getBody() : window.parent;	
				parent.minimizedWindows = typeof(parent.minimizedWindows) == 'undefined' ? [] : parent.minimizedWindows;
				for(var i=0;i<parent.minimizedWindows.length;i++) {
					if(parent.minimizedWindows[i]==window) {
						parent.minimizedWindows.splice(i,1);
					}
				}
				*/
			}, 
	
		minimizeWindow:
			function( window, eOpts ) {
				var parent = typeof(window.parent) == 'undefined' ? Ext.getBody() : window.parent;	
				if(window.collapsed) {
					FMX.utils.FuncionesGlobales.restoreWindow(window, eOpts);
				} else {
		    		window.collapse();
		    		window.setWidth( 150 );
		    		/*
					parent.minimizedWindows = typeof(parent.minimizedWindows) == 'undefined' ? [] : parent.minimizedWindows;	
					parent.minimizedWindows.push(window);	
					window.alignTo(parent, 'bl-bl?', [50*(parent.minimizedWindows.length-1),0]);
					*/
		    		window.alignTo(parent, 'br-br?', [0,0]);
					/*
					window.on({
						beforeclose : function(window, eOpts) {
							console.log("beforeclose",window,eOpts);
							FMX.utils.FuncionesGlobales.restoreWindow(window, eOpts);
						},
						scope : this
					});
					*/
				}
		},
		
		mostrarMensajeEmergente: function(parametros){
			var element = typeof(parametros.componente.body) == 'undefined' ? Ext.getBody() : parametros.componente.body;	
            var msgCt = Ext.core.DomHelper.insertFirst(element, {id:'msg-div'}, true);
            var s = Ext.String.format.apply(String, [parametros.mensaje]);
            var m = Ext.core.DomHelper.append(msgCt, this.createBox(parametros.titulo, s), true);
            m.hide();
            m.slideIn('t').ghost("t", { delay: 1000, remove: true});
   	 	},
    	//Ver si lo siguiente se puede implentar con un xtemplate
    	createBox: function (t, s){
       	// return ['<div class="msg">',
       	//         '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
       	//         '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>', t, '</h3>', s, '</div></div></div>',
      	 //         '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
       	//         '</div>'].join('');
       		return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
    	},
    	
    	/*
		* Recursively merge properties of two objects 
		*/
		mergeRecursive: function (obj1, obj2) {
			for (var p in obj2) {
    			try {
			      	// Property in destination object set; update its value.
      				if ( typeof obj2[p] == 'object' && obj2[p] != null) {
        				obj1[p] = this.mergeRecursive(obj1[p], obj2[p]);
			        } else {
        				obj1[p] = obj2[p];
      				}
    			} catch(e) {
      				// Property in destination object not set; create it and set its value.
      				obj1[p] = obj2[p];
    			}
  			}
  			return obj1;
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