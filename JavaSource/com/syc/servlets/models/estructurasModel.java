package com.syc.servlets.models;

public class estructurasModel {
	public String Texto;
	public String Tipo;
	public String Ide;
	public String Parent;
	public String Profundidad;
	public String atributos;
	public estructurasModel(){
		super();
	}
	public estructurasModel(String Texto,String Tipo,String Ide,String Parent, String Profundidad,String atributos){
		super();
		this.Texto=Texto;
		this.Tipo=Tipo;
		this.Ide=Ide;
		this.Parent=Parent;
		this.Profundidad=Profundidad;
		this.atributos=atributos;
	}
	public String getTexto(){
		return this.Texto;
	}
	public void setTexto(String Texto){
		this.Texto=Texto;
	}
	public String getTipo(){
		return this.Tipo;
	}
	public void setTipo(String Tipo){
		this.Tipo=Tipo;
	}
	public String getIde(){
		return this.Ide;
	}
	public void setIde(String Ide){
		this.Ide=Ide;
	}
	public String getParent(){
		return this.Parent;
	}
	public void setParent(String Parent){
		this.Parent=Parent;
	}
	public String getProfundidad(){
		return this.Profundidad;
	}
	public void setProfundidad(String Profundidad){
		this.Profundidad=Profundidad;
	}
	public String getAtributos(){
		return this.atributos;
	}
	public void setAtributos(String atributos){
		this.atributos=atributos;
	}

}
