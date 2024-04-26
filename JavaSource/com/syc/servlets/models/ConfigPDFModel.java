package com.syc.servlets.models;

public class ConfigPDFModel {
	
	/*
	    Ext.define('ConfigModel', {
	     extend: 'Ext.data.Model',
	     fields: [
	         {name: 'instaladorAF', type: 'boolean'},
	         {name: 'separarPDF', type: 'boolean'},
	         {name: 'url', type: 'string'},
	         {name: 'jpg', type: 'boolean'},
	         {name: 'bmp', type: 'boolean'},
	         {name: 'jpeg', type: 'boolean'},
	         {name: 'png', type: 'boolean'},
	         {name: 'gif', type: 'boolean'},
	         {name: 'tiff', type: 'boolean'},
	         {name: 'tif', type: 'boolean'},
	         {name: 'pdf', type: 'boolean'},
	         {name: 'tamKB', type: 'integer'},
	         {name: 'orden', type: 'string'},
	         {name: 'salidaI', type: 'string'},
	         {name: 'salidaT', type: 'string'},
	         {name: 'espacioCI', type: 'string'},
	         {name: 'espacioCT', type: 'string'},
	         {name: 'maxAltoI', type: 'integer'},
	         {name: 'maxAnchoI', type: 'integer'},
	         {name: 'maxAltoT', type: 'integer'},
	         {name: 'maxAnchoT', type: 'integer'},
	         {name: 'transI', type: 'string'},
	         {name: 'transT', type: 'string'},
	         {name: 'tiposColor', type: 'string'},
	         {name: 'espaciosColor', type: 'string'},
	         {name: 'responsableEsc', type: 'string'},
	         {name: 'metodoEsc', type: 'string'},
	         {name: 'hints', type: 'boolean'},
	         {name: 'escritor', type: 'string'},
	         {name: 'calidad', type: 'string'}
	     ]
	 });
	 */
	
	 boolean instaladorAF = false;
     boolean separarPDF = false;
     String url;
     boolean jpg = true;
     boolean bmp = true;
     boolean jpeg = true;
     boolean png = true;
     boolean gif = true;
     boolean tiff = true;
     boolean tif = true;
     boolean pdf = true;
     int tamKB = 2048;
     String orden = "nombre";
     String salidaI = "jpg";
     String salidaT = "jpg";
     String espacioCI = "color";
     String espacioCT = "byn";
     int maxAltoI = -1;
     int maxAnchoI = -1;
     int maxAltoT = -1;
     int maxAnchoT = -1;
     String transI;
     String transT;
     String tiposColor;
     String espaciosColor;
     String responsableEsc;
     String metodoEsc;
     boolean hints = false;
     String escritor;
     String calidad = "1.0";
     
	public boolean isInstaladorAF() {
		return instaladorAF;
	}
	public void setInstaladorAF(boolean instaladorAF) {
		this.instaladorAF = instaladorAF;
	}
	public boolean isSepararPDF() {
		return separarPDF;
	}
	public void setSepararPDF(boolean separarPDF) {
		this.separarPDF = separarPDF;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isJpg() {
		return jpg;
	}
	public void setJpg(boolean jpg) {
		this.jpg = jpg;
	}
	public boolean isBmp() {
		return bmp;
	}
	public void setBmp(boolean bmp) {
		this.bmp = bmp;
	}
	public boolean isJpeg() {
		return jpeg;
	}
	public void setJpeg(boolean jpeg) {
		this.jpeg = jpeg;
	}
	public boolean isPng() {
		return png;
	}
	public void setPng(boolean png) {
		this.png = png;
	}
	public boolean isGif() {
		return gif;
	}
	public void setGif(boolean gif) {
		this.gif = gif;
	}
	public boolean isTiff() {
		return tiff;
	}
	public void setTiff(boolean tiff) {
		this.tiff = tiff;
	}
	public boolean isTif() {
		return tif;
	}
	public void setTif(boolean tif) {
		this.tif = tif;
	}
	public boolean isPdf() {
		return pdf;
	}
	public void setPdf(boolean pdf) {
		this.pdf = pdf;
	}
	public int getTamKB() {
		return tamKB;
	}
	public void setTamKB(int tamKB) {
		this.tamKB = tamKB;
	}
	public String getOrden() {
		return orden;
	}
	public void setOrden(String orden) {
		this.orden = orden;
	}
	public String getSalidaI() {
		return salidaI;
	}
	public void setSalidaI(String salidaI) {
		this.salidaI = salidaI;
	}
	public String getSalidaT() {
		return salidaT;
	}
	public void setSalidaT(String salidaT) {
		this.salidaT = salidaT;
	}
	public String getEspacioCI() {
		return espacioCI;
	}
	public void setEspacioCI(String espacioCI) {
		this.espacioCI = espacioCI;
	}
	public String getEspacioCT() {
		return espacioCT;
	}
	public void setEspacioCT(String espacioCT) {
		this.espacioCT = espacioCT;
	}
	public int getMaxAltoI() {
		return maxAltoI;
	}
	public void setMaxAltoI(int maxAltoI) {
		this.maxAltoI = maxAltoI;
	}
	public int getMaxAnchoI() {
		return maxAnchoI;
	}
	public void setMaxAnchoI(int maxAnchoI) {
		this.maxAnchoI = maxAnchoI;
	}
	public int getMaxAltoT() {
		return maxAltoT;
	}
	public void setMaxAltoT(int maxAltoT) {
		this.maxAltoT = maxAltoT;
	}
	public int getMaxAnchoT() {
		return maxAnchoT;
	}
	public void setMaxAnchoT(int maxAnchoT) {
		this.maxAnchoT = maxAnchoT;
	}
	public String getTransI() {
		return transI;
	}
	public void setTransI(String transI) {
		this.transI = transI;
	}
	public String getTransT() {
		return transT;
	}
	public void setTransT(String transT) {
		this.transT = transT;
	}
	public String getTiposColor() {
		return tiposColor;
	}
	public void setTiposColor(String tiposColor) {
		this.tiposColor = tiposColor;
	}
	public String getEspaciosColor() {
		return espaciosColor;
	}
	public void setEspaciosColor(String espaciosColor) {
		this.espaciosColor = espaciosColor;
	}
	public String getResponsableEsc() {
		return responsableEsc;
	}
	public void setResponsableEsc(String responsableEsc) {
		this.responsableEsc = responsableEsc;
	}
	public String getMetodoEsc() {
		return metodoEsc;
	}
	public void setMetodoEsc(String metodoEsc) {
		this.metodoEsc = metodoEsc;
	}
	public boolean isHints() {
		return hints;
	}
	public void setHints(boolean hints) {
		this.hints = hints;
	}
	public String getEscritor() {
		return escritor;
	}
	public void setEscritor(String escritor) {
		this.escritor = escritor;
	}
	public String getCalidad() {
		return calidad;
	}
	public void setCalidad(String calidad) {
		this.calidad = calidad;
	}
}
