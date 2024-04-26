package com.syc.fortimax.hibernate.entities;

public class imx_perfiles_privilegios {
	public int Id;
	public String Nombre;
	public int Valor;
	public String Descripcion;
	
	public imx_perfiles_privilegios(){
		super();
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	public int getValor() {
		return Valor;
	}
	public void setValor(int valor) {
		Valor = valor;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	
}
