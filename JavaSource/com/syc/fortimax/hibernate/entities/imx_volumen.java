package com.syc.fortimax.hibernate.entities;

public class imx_volumen implements java.io.Serializable {
	private static final long serialVersionUID = -7714194179796667764L;
	public String Volumen;
	public String UnidadDisco;
	public String RutaBase;
	public String RutaDirectorio;
	public char Capacidad;
	public char TipoVolumen;
	
	public imx_volumen(){
		super();
	}
	public imx_volumen(String Volumen, String UnidadDisco, String RutaBase, String RutaDirectorio, char Capacidad, char TipoVolumen){
		this.Volumen=Volumen;
		this.UnidadDisco=UnidadDisco;
		this.RutaBase=RutaBase;
		this.RutaDirectorio=RutaDirectorio;
		this.Capacidad=Capacidad;
		this.TipoVolumen=TipoVolumen;
	}
	
	public String getVolumen(){
		return this.Volumen;
	}
	public void setVolumen(String Volumen){
		this.Volumen=Volumen;
	}
	public String getUnidadDisco(){
		return this.UnidadDisco;
	}
	public void setUnidadDisco(String UnidadDisco){
		this.UnidadDisco=UnidadDisco;
	}
	public String getRutaBase(){
		return this.RutaBase;
	}
	public void setRutaBase(String RutaBase){
		this.RutaBase=RutaBase;
	}
	public String getRutaDirectorio(){
		return this.RutaDirectorio;
	}
	public void setRutaDirectorio(String RutaDirectorio){
		this.RutaDirectorio=RutaDirectorio;
	}
	public char getCapacidad(){
		return this.Capacidad;
	}
	public void setCapacidad(char Capacidad){
		this.Capacidad=Capacidad;
	}
	public char getTipoVolumen(){
		return this.TipoVolumen;
	}
	public void setTipoVolumen(char TipoVolumen){
		this.TipoVolumen=TipoVolumen;
	}
	public String getAbsolutePath() {
		return getUnidadDisco()+getRutaBase()+getRutaDirectorio();
	}
}
