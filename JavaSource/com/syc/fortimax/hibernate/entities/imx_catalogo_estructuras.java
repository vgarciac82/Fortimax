package com.syc.fortimax.hibernate.entities;

public class imx_catalogo_estructuras implements java.io.Serializable {
	private static final long serialVersionUID = -7714194179796667764L;
	public int Id;
	public String Nombre;
	public String Descripcion;
	public String Definicion;
	
	public imx_catalogo_estructuras(){
		super();
	}
	public imx_catalogo_estructuras(int Id, String Nombre,String Descripcion ,String Definicion){
		super();
		this.Id=Id;
		this.Nombre=Nombre;
		this.Descripcion=Descripcion;
		this.Definicion=Definicion;
	}
	public int getId(){
		return this.Id;
	}
	public void setId(int Id){
		this.Id=Id;
	}
	public String getNombre(){
		return this.Nombre;
	}
	public void setNombre(String Nombre){
		this.Nombre=Nombre;
	}
	public String getDescripcion(){
		return this.Descripcion;
	}
	public void setDescripcion(String Descripcion){
		this.Descripcion=Descripcion;
	}
	public String getDefinicion(){
		return this.Definicion;
	}
	public void setDefinicion(String Definicion){
		this.Definicion=Definicion;
	}

}
