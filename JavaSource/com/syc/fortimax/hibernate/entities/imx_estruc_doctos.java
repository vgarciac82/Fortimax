package com.syc.fortimax.hibernate.entities;

public class imx_estruc_doctos implements java.io.Serializable {
	private static final long serialVersionUID = -7714194179796667764L;
	
	public imx_estruc_doctos_id id;
	public String NombreElemento;
	public int Profundidad;
	public int Prioridad;
	public String Descripcion;
	public String LNombreEstructura;
	
	public imx_estruc_doctos(){
		super();
	}
	public imx_estruc_doctos(imx_estruc_doctos_id id, String NombreElemento, int Profundidad,int Prioridad, String Descripcion, String LNombreEstructura){
		super();
		this.id=id;
		this.NombreElemento=NombreElemento;
		this.Profundidad=Profundidad;
		this.Prioridad=Prioridad;
		this.Descripcion=Descripcion;
		this.LNombreEstructura=LNombreEstructura;
	}
	public imx_estruc_doctos_id getid(){
		return this.id;
	}
	public imx_estruc_doctos setid(imx_estruc_doctos_id id){
		this.id=id;
		return this;
	}
	public String getNombreElemento(){
		return this.NombreElemento;
	}
	public void setNombreElemento(String NombreElemento){
		this.NombreElemento=NombreElemento;
	}
	public int getProfundidad(){
		return this.Profundidad;
	}
	public void setProfundidad(int Profundidad){
		this.Profundidad=Profundidad;
	}
	public int getPrioridad(){
		return this.Prioridad;
	}
	public void setPrioridad(int Prioridad){
		this.Prioridad=Prioridad;
	}
	public String getDescripcion(){
		return this.Descripcion;
	}
	public void setDescripcion(String Descripcion){
		this.Descripcion=Descripcion;
	}
	public String getLNombreEstructura(){
		return this.LNombreEstructura;
	}
	public void setLNombreEstructura(String LNombreEstructura){
		this.LNombreEstructura=LNombreEstructura;
	}
}
