/*
 * File: gruposLDAPStore.js
 * Date: Thu Dec 29 2011 13:06:30 GMT-0600 (Hora estándar de México)
 * 
 * This file was generated by Ext Designer version xds-1.0.3.2.
 * http://www.extjs.com/products/designer/
 *
 * This file will be auto-generated each and everytime you export.
 *
 * Do NOT hand edit this file.
 */

//Todos los campos con un '+' fuer�n a�adidos posteriormente a Ext Designer

gruposLDAPStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function(cfg) {
        cfg = cfg || {};
        gruposLDAPStore.superclass.constructor.call(this, Ext.apply({
            storeId: 'gruposLDAPStore',
            url: '../LDAPServlet?action=obtenerGrupos', //+ Usar este en producci�n
            // url: '../js/gruposLDAP/grupos.json', //+ Usar para pruebas si no se cuenta con acceso a un servidor LDAP
            root: 'grupos',
            fields: ['name'] //+
        }, cfg));
    }
});
new gruposLDAPStore();