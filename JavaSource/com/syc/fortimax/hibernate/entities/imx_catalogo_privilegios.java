package com.syc.fortimax.hibernate.entities;

public class imx_catalogo_privilegios implements java.io.Serializable {
	private static final long serialVersionUID = -7714194179796667764L;
	public int Id;
	public String Nombre;
	public int Valor;
	public String Descripcion;
	
	public imx_catalogo_privilegios(){
		super();
	}
	public imx_catalogo_privilegios(int Id,String Nombre,int Valor,String Descripcion){
		this.Id=Id;
		this.Nombre=Nombre;
		this.Valor=Valor;
		this.Descripcion=Descripcion;
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
	public int getValor(){
		return this.Valor;
	}
	public void setValor(int Valor){
		this.Valor=Valor;
	}
	public String getDescripcion(){
		return this.Descripcion;
	}
	public void setDescripcion(String Descripcion){
		this.Descripcion=Descripcion;
	}

}
