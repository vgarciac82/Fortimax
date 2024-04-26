package com.syc.fortimax.hibernate.entities;

public class imx_unidad_volumen_id implements java.io.Serializable{
	private static final long serialVersionUID = -8496021938092798354L;
	public String Unidad;
	public String RutaBase;
	
	public imx_unidad_volumen_id(){
		super();
}
	public imx_unidad_volumen_id(String Unidad, String RutaBase){
		super();
		this.Unidad=Unidad;
		this.RutaBase=RutaBase;
}
	public String getUnidad(){
		return this.Unidad;
	}
	public void setUnidad(String Unidad){
		this.Unidad=Unidad;
	}
	public String getRutaBase(){
		return this.RutaBase;
	}
	public void setRutaBase(String RutaBase){
		this.RutaBase=RutaBase;
	}
	public boolean equals(Object other) {
		return super.equals(other);
	}

	public int hashCode() {
		return super.hashCode();
	}
}
