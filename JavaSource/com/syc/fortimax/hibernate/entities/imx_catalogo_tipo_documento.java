package com.syc.fortimax.hibernate.entities;

public class imx_catalogo_tipo_documento  implements java.io.Serializable {
	private static final long serialVersionUID = -7714194179796667764L;
	public int Id;
	public String Nombre;
	public String Descripcion;
	public String EstructuraFormulario;
	
	public imx_catalogo_tipo_documento(){
		super();
	}
	public imx_catalogo_tipo_documento(int Id,String Nombre,String Descripcion,String EstructuraFormulario){
		super();
		this.Id=Id;
		this.Nombre=Nombre;
		this.Descripcion=Descripcion;
		this.EstructuraFormulario=EstructuraFormulario;
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
	public String getEstructuraFormulario(){
		return this.EstructuraFormulario;
	}
	public void setEstructuraFormulario(String EstructuraFormulario){
		this.EstructuraFormulario=EstructuraFormulario;
	}


}
