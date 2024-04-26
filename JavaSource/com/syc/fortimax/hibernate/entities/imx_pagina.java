package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_pagina implements Serializable {
	private static final long serialVersionUID = -3748397862908561591L;
	public imx_pagina_id Id;
	public imx_volumen Volumen;
	public String TipoVolumen;
	public String NomArchivoVol;
	public String NomArchivoOrg;
	public String TipoPagina;
	public String Anotaciones;
	public String EstadoPagina;
	public int TamanoBytes;
	public int Pagina;
	
	public imx_pagina(){
	}
	public imx_pagina(imx_pagina_id Id,imx_volumen Volumen,String TipoVolumen,String NomArchivoVol,String NomArchivoOrg,String TipoPagina,
			String Anotaciones, String EstadoPagina,int TamanoBytes, int Pagina){
		this.Id=Id;
		this.Volumen=Volumen;
		this.TipoVolumen=TipoVolumen;
		this.NomArchivoVol=NomArchivoVol;
		this.NomArchivoOrg=NomArchivoOrg;
		this.TipoPagina=TipoPagina;
		this.Anotaciones=Anotaciones;
		this.EstadoPagina=EstadoPagina;
		this.TamanoBytes=TamanoBytes;
		this.Pagina=Pagina;
	}
	
	public imx_pagina_id getId(){
		return this.Id;
	}
	public void setId(imx_pagina_id Id){
		this.Id=Id;
	}
	public imx_volumen getVolumen(){
		return this.Volumen;
	}
	public void setVolumen(imx_volumen Volumen){
		this.Volumen=Volumen;
	}
	public String getTipoVolumen(){
		return this.TipoVolumen;
	}
	public void setTipoVolumen(String TipoVolumen){
		this.TipoVolumen=TipoVolumen;
	}
	public String getNomArchivoVol(){
		return this.NomArchivoVol;
	}
	public void setNomArchivoVol(String NomArchivoVol){
		this.NomArchivoVol=NomArchivoVol;
	}
	public String getNomArchivoOrg(){
		return this.NomArchivoOrg;
	}
	public void setNomArchivoOrg(String NomArchivoOrg){
		this.NomArchivoOrg=NomArchivoOrg;
	}
	public String getTipoPagina(){
		return this.TipoPagina;
	}
	public void setTipoPagina(String TipoPagina){
		this.TipoPagina=TipoPagina;
	}
	public String getAnotaciones(){
		return this.Anotaciones;
	}
	public void setAnotaciones(String Anotaciones){
		this.Anotaciones=Anotaciones;
	}
	public String getEstadoPagina(){
		return this.EstadoPagina;
	}
	public void setEstadoPagina(String EstadoPagina){
		this.EstadoPagina=EstadoPagina;
	}
	public int getTamanoBytes(){
		return this.TamanoBytes;
	}
	public void setTamanoBytes(int TamanoBytes){
		this.TamanoBytes=TamanoBytes;
	}
	public int getPagina(){
		return this.Pagina;
	}
	public void setPagina(int Pagina){
		this.Pagina=Pagina;
	}
	public String getAbsolutePath() {
		return getVolumen().getAbsolutePath()+getNomArchivoVol();
	}
	
	public String getPathOCR() {
		return getAbsolutePath().substring(0, getAbsolutePath().lastIndexOf('.'))+".txt";
	}
	
	public String getPageExtension() {
		int index = NomArchivoOrg.lastIndexOf(".");
		if(index>0)
			return NomArchivoOrg.substring(index);
		else return null;
	}
	public String getThumbnailPath() {
		return getAbsolutePath().replace("_volumen", "_cache").replace(".tif","")+".mini";
	}
	public String getPreviewPath() {
		return getAbsolutePath().replace("_volumen", "_cache").replace(".tif","")+".prev";
	}
}
