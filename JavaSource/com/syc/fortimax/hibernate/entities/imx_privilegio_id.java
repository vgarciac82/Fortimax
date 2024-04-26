package com.syc.fortimax.hibernate.entities;

public class imx_privilegio_id implements java.io.Serializable{
	private static final long serialVersionUID = -8496021938092798354L;
	public String TituloAplicacion;
	public String NombreUsuario;
	
	public imx_privilegio_id(){
		super();
	}
	public imx_privilegio_id(String TituloAplicacion, String NombreUsuario){
		super();
		this.TituloAplicacion=TituloAplicacion;
		this.NombreUsuario=NombreUsuario;
	}
	public String getTituloAplicacion(){
		return this.TituloAplicacion;
	}
	public void setTituloAplicacion(String TituloAplicacion){
		this.TituloAplicacion=TituloAplicacion;
	}
	public String getNombreUsuario(){
		return this.NombreUsuario;
	}
	public void setNombreUsuario(String NombreUsuario){
		this.NombreUsuario=NombreUsuario;
	}
	public boolean equals(Object other) {
		return super.equals(other);
	}

	public int hashCode() {
		return super.hashCode();
	}

}
