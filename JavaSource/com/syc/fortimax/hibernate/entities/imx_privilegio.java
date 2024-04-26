package com.syc.fortimax.hibernate.entities;

public class imx_privilegio implements java.io.Serializable {
	private static final long serialVersionUID = -7714194179796667764L;
	public imx_privilegio_id id;
	public String NombreNivel;
	public int Privilegio;
	
	public imx_privilegio(){
		super();
	}
	public imx_privilegio(imx_privilegio_id id, String NombreNivel, int Privilegio){
		this.id=id;
		this.NombreNivel=NombreNivel;
		this.Privilegio=Privilegio;
	}
	public imx_privilegio_id getid(){
		return this.id;
	}
	public void setid(imx_privilegio_id id){
		this.id=id;
	}
	public String getNombreNivel(){
		return this.NombreNivel;
	}
	public void setNombreNivel(String NombreNivel){
		this.NombreNivel=NombreNivel;
	}
	public int getPrivilegio(){
		return this.Privilegio;
	}
	public void setPrivilegio(int Privilegio){
		this.Privilegio=Privilegio;
	}
	

}
