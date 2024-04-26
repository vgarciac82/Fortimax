package com.syc.gavetas;
import org.apache.log4j.Logger;

public class GavetaCampo {

private static final Logger log = Logger.getLogger(GavetaCampo.class);
	private String nombre_campo;
	private String nombre_desplegar;
	private String val_predefinido;
	private double tamano;
	private int tipo_dato;
	private int indice;
	private String requerido;
	private String editable;
	private String lista;
	private int posicion_campo;
	private String nombreCatalogo;
	
	public String getNombreCatalogo() {
		return nombreCatalogo;
	}
	public void setNombreCatalogo(String nombreCatalogo) {
		this.nombreCatalogo = nombreCatalogo;
	}
	public String getNombre_campo()
   {
   	return nombre_campo;
   }
	public void setNombre_campo( String nombre_campo )
   {
   	this.nombre_campo = nombre_campo;
   }
	public String getNombre_desplegar()
   {
   	return nombre_desplegar;
   }
	public void setNombre_desplegar( String nombre_desplegar )
   {
   	this.nombre_desplegar = nombre_desplegar;
   }
	public int getPosicion_campo()
   {
   	return posicion_campo;
   }
	public void setPosicion_campo( int posicion_campo )
   {
   	this.posicion_campo = posicion_campo;
   }
	public int getTipo_dato()
   {
   	return tipo_dato;
   }
	public void setTipo_dato( int tipo_dato )
   {
   	this.tipo_dato = tipo_dato;
   }
	public String getVal_predefinido()
   {
   	return val_predefinido;
   }
	public void setVal_predefinido( String val_predefinido )
   {
   	this.val_predefinido = val_predefinido;
   }
	public void setEditable( String editable )
   {
   	this.editable = editable;
   }
	public void setIndice( int indice )
   {
   	this.indice = indice;
   }
	public void setLista( String lista )
   {
   	this.lista = lista;
   }
	public void setRequerido( String requerido )
   {
   	this.requerido = requerido;
   }
	public void setTamano( double tamano )
   {
   	this.tamano = tamano;
   }
	public GavetaCampo()
	{}
	public GavetaCampo(String nombre_campo,String nombre_desplegar,String val_predefinido,double tamano,
			int tipo_dato,int indice,String requerido,String editable,String lista){
		this.nombre_campo = nombre_campo;
		this.nombre_desplegar = nombre_desplegar;
		this.val_predefinido = val_predefinido;
		this.tamano = tamano;
		this.tipo_dato = tipo_dato;
		this.indice = indice;
		this.requerido = requerido;
		this.editable = editable;
		this.lista = lista;
	}
	
	public GavetaCampo(String nombre_campo,String nombre_desplegar,String val_predefinido,String tamano,
			String tipo_dato, String indice,String requerido,String editable,String lista){
		this.nombre_campo = nombre_campo;
		this.nombre_desplegar = nombre_desplegar;
		this.val_predefinido = val_predefinido;
		this.tamano = (tamano != null)? (Double.parseDouble( tamano )): 0.0;
		this.tipo_dato = ( tipo_dato!= null)? (Integer.parseInt( tipo_dato )): 0;
		this.indice = ( indice!= null)? (Integer.parseInt( indice )): 0;
		this.requerido = requerido;
		this.editable = editable;
		this.lista = lista;
	}
	public GavetaCampo(String nombre_campo,String nombre_desplegar,String val_predefinido,String tamano,
			String tipo_dato, String indice,String requerido,String editable,String lista, String nombreCatalogo ){
		this.nombre_campo = nombre_campo;
		this.nombre_desplegar = nombre_desplegar;
		this.val_predefinido = val_predefinido;
		this.tamano = (tamano != null)? (Double.parseDouble( tamano )): 0.0;
		this.tipo_dato = ( tipo_dato!= null)? (Integer.parseInt( tipo_dato )): 0;
		this.indice = ( indice!= null)? (Integer.parseInt( indice )): 0;
		this.requerido = requerido;
		this.editable = editable;
		this.lista = lista;
		this.nombreCatalogo = nombreCatalogo;
	}
	public GavetaCampo(String nombre_campo,String nombre_desplegar,String val_predefinido,double tamano,
			int tipo_dato,int indice,String requerido,String editable,String lista, int posicion_campo){
		this.nombre_campo = nombre_campo;
		this.nombre_desplegar = nombre_desplegar;
		this.val_predefinido = val_predefinido;
		this.tamano = tamano;
		this.tipo_dato = tipo_dato;
		this.indice = indice;
		this.requerido = requerido;
		this.editable = editable;
		this.lista = lista;
		this.posicion_campo = posicion_campo;
	}
	
	public GavetaCampo(String nombre_campo,String nombre_desplegar,String val_predefinido,double tamano,
			int tipo_dato,int indice,String requerido,String editable,String lista, int posicion_campo,String nombreCatalogo){
		this.nombre_campo = nombre_campo;
		this.nombre_desplegar = nombre_desplegar;
		this.val_predefinido = val_predefinido;
		this.tamano = tamano;
		this.tipo_dato = tipo_dato;
		this.indice = indice;
		this.requerido = requerido;
		this.editable = editable;
		this.lista = lista;
		this.posicion_campo = posicion_campo;
		this.nombreCatalogo = nombreCatalogo;
	}
	
	public void setPosicionCampo(int posicion_campo){
		this.posicion_campo =  posicion_campo;
	}
	
	public int getPosicionCampo(){
		return posicion_campo;
	}
	
	public String getNombreCampo(){
		return nombre_campo;
	}
	public String getNombreDesplegar(){
		return nombre_desplegar;
	}
	public String getValPredefinido(){
		return val_predefinido;
	}
	public double getTamano(){
		return tamano;
	}
	public int getTipoDato(){
		return tipo_dato;
	}
	public int getIndice(){
		return indice;
	}
	public String getRequerido(){
		return requerido;
	}
	public String getEditable(){
		return editable;
	}
	public String getLista(){
		return lista;
	}
	
	
}
