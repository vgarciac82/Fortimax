package com.syc.fortimax.hibernate.entities;

public class imx_estruc_doctos_id implements java.io.Serializable{
	private static final long serialVersionUID = -8496021938092798354L;
	
	public String TituloAplicacion;
	public String NombreEstructura;
	public int PosicionElemento;
	
	public imx_estruc_doctos_id(){
		super();
	}
	public imx_estruc_doctos_id(String TituloAplicacion,String NombreEstructura, int PosicionElemento){
		super();
		this.TituloAplicacion=TituloAplicacion;
		this.NombreEstructura=NombreEstructura;
		this.PosicionElemento=PosicionElemento;
	}
	public String getTituloAplicacion(){
		return this.TituloAplicacion;
	}
	public void setTituloAplicacion(String TituloAplicacion){
		this.TituloAplicacion=TituloAplicacion;
	}
	public String getNombreEstructura(){
		return this.NombreEstructura;
	}
	public void setNombreEstructura(String NombreEstructura){
		this.NombreEstructura=NombreEstructura;
	}
	public int getPosicionElemento(){
		return this.PosicionElemento;
	}
	public void setPosicionElemento(int PosicionElemento){
		this.PosicionElemento=PosicionElemento;
	}
	public boolean equals(Object other) {
		return super.equals(other);
	}

	public int hashCode() {
		return super.hashCode();
	}

}
