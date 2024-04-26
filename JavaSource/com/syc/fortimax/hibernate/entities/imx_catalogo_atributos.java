package com.syc.fortimax.hibernate.entities;

public class imx_catalogo_atributos {
	public int Id;
	public String etiqueta;
	public int posicion;
	public String tipo;
	public int longitud;
	public String descripcion;
	
	public imx_catalogo_atributos() {
	}
	
	public imx_catalogo_atributos(int Id, String etiqueta, int posicion, String tipo, int longitud, String descripcion) {
		this.Id = Id;
		this.etiqueta = etiqueta;
		this.posicion = posicion;
		this.tipo = tipo;
		this.longitud = longitud;
		this.descripcion = descripcion;

	}
	
	public int getId(){
		return this.Id;
	}
	public void setId(int Id){
		this.Id=Id;
	}
	
	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


}