Ext.define('FMX.view.oeste.GraficaEspacioUsuario', {
	extend:'Ext.panel.Panel',
    alias : 'widget.GraficaEspacioUsuario',

	hidden: false, //ifimax,
	margin:'10 5 3 10',
	unstyled:false,
	border:true,
	                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
	title: 'Espacio disponible',
	collapsible: false,
	//height: 200,
	flex: 1,
	//region: 'west',
	autoScroll: false,
	width: 400,
	height: 130,
	
	    initComponent: function() {
		var me = this;
		
		me.items = [
			{
				xtype:'chart',
				animate: true,
				width: me.width,
				height: me.height,
				store: 'EspacioAsignado',
				axes: [{
					type: 'Numeric',
					position: 'bottom',
					fields: ['espacio_disponible'],
					label: {
						renderer: Ext.util.Format.numberRenderer('0,0')
					},
					title: 'Espacio disponible',
					grid: true,
					minimum: 0
				}
				/*, {
					type: 'Category',
					position: 'left',
					fields: ['nombre'],
					title: ''
				}*/],
				background: {
				  gradient: {
					id: 'backgroundGradient',
					angle: 90,
					stops: {
					  0: {
						color: '#ffffff'
					  },
					  60: {
						color: '#dce8f4'
					  }
					}
				  }
				},
				series: [{
					type: 'bar',
					axis: 'bottom',
					//highlight: true,
					tips: {
					  trackMouse: true,
					  width: 230,
					  height: 28,
					  renderer: function(storeItem, item) {
						this.setTitle(storeItem.get('nombre') 
						+ ': ' 
						+ storeItem.get('espacio_utilizado') 
						+ ' MB utilizados de ' 
						+ storeItem.get('espacio_disponible') + ' MB.');
					  }
					},
					label: {
						display: 'insideEnd',
						field: 'espacio_utilizado',
						renderer: Ext.util.Format.numberRenderer('0'),
						orientation: 'horizontal',
						color: '#333',
						'text-anchor': 'middle',
						contrast: true
					},
					renderer: function(sprite, record, attr, index, store) {
					var fieldValue = Math.random() * 20 + 10;
					var value = (record.get('espacio_utilizado')*fieldValue >> 0) % 5;
					var color = ['rgb(213, 70, 121)', 
								 'rgb(44, 153, 201)', 
								 'rgb(146, 6, 157)', 
								 'rgb(49, 149, 0)', 
								 'rgb(249, 153, 0)'][value];
					return Ext.apply(attr, {
					fill: color
					});
							},
					xField: 'nombre',
					yField: ['espacio_utilizado']
				}]
			}
		];
        this.callParent(arguments);
    }
	
});