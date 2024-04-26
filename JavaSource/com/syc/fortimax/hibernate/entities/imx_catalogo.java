package com.syc.fortimax.hibernate.entities;

import java.util.ArrayList;
import java.util.List;

public class imx_catalogo implements java.io.Serializable {
	private static final long serialVersionUID = -7714194179796667764L;
	private String TblCatalogo;
	private String NombreCatalogo;
	private int LongitudCampo;
	private String Definicion;
	
	public imx_catalogo(){	
	}
	
	public imx_catalogo(String TblCatalogo,String NombreCatalogo, int LongitudCampo, String Definicion){
		this.TblCatalogo=TblCatalogo;
		this.NombreCatalogo=NombreCatalogo;
		this.LongitudCampo=LongitudCampo;
		this.Definicion=Definicion;
	}
	public String getTblCatalogo(){
		return this.TblCatalogo;
	}
	public void setTblCatalogo(String TblCatalogo){
		this.TblCatalogo=TblCatalogo;
	}
	public String getNombreCatalogo(){
		return this.NombreCatalogo;
	}
	public void setNombreCatalogo(String NombreCatalogo){
		this.NombreCatalogo=NombreCatalogo;
	}
	public int getLongitudCampo(){
		return this.LongitudCampo;
	}
	public void setLongitudCampo(int LongitudCampo){
		this.LongitudCampo=LongitudCampo;
	}
	public String getDefinicion() {
		return Definicion;
	}
	public void setDefinicion(String definicion) {
		Definicion = definicion;
	}
	
	public List<?> getDatos(String filtro){
		List<Object> list = new ArrayList<Object>();
		
		return list;
	}
}
