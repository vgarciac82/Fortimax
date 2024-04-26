package com.syc.imaxfile;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.syc.utils.FileStorageCapacity;

public class Pagina implements Serializable {

@SuppressWarnings("unused")
private static final Logger log = Logger.getLogger(Pagina.class);

	private static final long serialVersionUID = -6343025068921237661L;
	private String titulo_aplicacion = null;
	private int id_gabinete = -1;
	private int id_carpeta_padre = -1;
	private int id_documento = -1;
	private int numero_pagina = -1;
	private String volumen = null;
	private String tipo_volumen = null;
	private String nom_archivo_vol = null;
	private String nom_archivo_org = null;
	private String tipo_pagina = "A"; // A=Archivo, I=ImaxFile
	private String anotaciones = null;
	private String estado_pagina = "V"; // V=Vigente, T=Transitorio, H=Historico
	private double tamano_bytes = 0d;

	private String unidad_disco = null;
	private String ruta_base = null;
	private String ruta_directorio = null;

	public Pagina(){
		super();
	}
	
	public Pagina(String titulo_aplicacion, int id_gabinete,
			int id_carpeta_padre, int id_documento, int numero_pagina) {
		this.titulo_aplicacion = titulo_aplicacion;
		this.id_gabinete = id_gabinete;
		this.id_carpeta_padre = id_carpeta_padre;
		this.id_documento = id_documento;
		this.numero_pagina = numero_pagina;
	}

	public Pagina(String titulo_aplicacion, int id_gabinete,
			int id_carpeta_padre, int id_documento, int numero_pagina,
			String volumen, String tipo_volumen, String nom_archivo_vol,
			String nom_archivo_org, String tipo_pagina, String anotaciones,
			String estado_pagina, double tamano_bytes) {
		this(titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento,
				numero_pagina);
		this.volumen = volumen;
		this.tipo_volumen = tipo_volumen;
		this.nom_archivo_vol = nom_archivo_vol;
		this.nom_archivo_org = nom_archivo_org;
		this.tipo_pagina = tipo_pagina;
		this.anotaciones = anotaciones;
		this.estado_pagina = estado_pagina;
		this.tamano_bytes = tamano_bytes;
	}

	public void setTitulo_aplicacion(String tituloAplicacion) {
		titulo_aplicacion = tituloAplicacion;
	}

	public void setId_gabinete(int idGabinete) {
		id_gabinete = idGabinete;
	}


	public void setId_carpeta_padre(int idCarpetaPadre) {
		id_carpeta_padre = idCarpetaPadre;
	}

	public void setId_documento(int idDocumento) {
		id_documento = idDocumento;
	}

	public void setNumero_pagina(int numeroPagina) {
		numero_pagina = numeroPagina;
	}

	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}
	
	public int getIdGabinete() {
		return id_gabinete;
	}

	public int getIdCarpetaPadre() {
		return id_carpeta_padre;
	}

	public int getIdDocumento() {
		return id_documento;
	}

	public int getNumeroPagina() {
		return numero_pagina;
	}

	public String getVolumen() {
		return volumen;
	}

	public void setVolumen(String volumen) {
		this.volumen = volumen;
	}

	public String getTipoVolumen() {
		return tipo_volumen;
	}

	public void setTipoVolumen(String tipo_volumen) {
		this.tipo_volumen = tipo_volumen;
	}

	public String getNomArchivoVol() {
		return nom_archivo_vol;
	}

	public void setNomArchivoVol(String nom_archivo_vol) {
		this.nom_archivo_vol = nom_archivo_vol;
	}

	public String getNomArchivoOrg() {
		return nom_archivo_org;
	}

	public void setNomArchivoOrg(String nom_archivo_org) {
		this.nom_archivo_org = nom_archivo_org;
	}

	public String getTipoPagina() {
		return tipo_pagina;
	}

	public void setTipoPagina(String tipo_pagina) {
		this.tipo_pagina = tipo_pagina;
	}

	public String getAnotaciones() {
		return anotaciones;
	}

	public void setAnotaciones(String anotaciones) {
		this.anotaciones = anotaciones;
	}

	public String getEstadoPagina() {
		return estado_pagina;
	}

	public void setEstadoPagina(String estado_pagina) {
		this.estado_pagina = estado_pagina;
	}

	public double getTamanoBytes() {
		return tamano_bytes;
	}

	public String getTamanoBytesTexto() {
		return FileStorageCapacity.conversion(tamano_bytes);
	}

	public void setTamanoBytes(double tamano_bytes) {
		this.tamano_bytes = tamano_bytes;
	}

	public String getUnidadDisco() {
		return unidad_disco;
	}

	public void setUnidadDisco(String unidad_disco) {
		this.unidad_disco = unidad_disco;
	}

	public String getRutaBase() {
		return ruta_base;
	}

	public void setRutaBase(String ruta_base) {
		this.ruta_base = ruta_base;
	}

	public String getRutaDirectorio() {
		return ruta_directorio;
	}

	public void setRutaDirectorio(String ruta_directorio) {
		this.ruta_directorio = ruta_directorio;
	}

	public String getPageExtension() {
		int posicion_punto = nom_archivo_org.lastIndexOf(".");
		if(posicion_punto<0)
			return "imx";
		else
			return nom_archivo_org.substring(nom_archivo_org.lastIndexOf("."));
	}
	
	public String getAbsolutePath() {
		return getUnidadDisco()+getRutaBase()+getRutaDirectorio()+getNomArchivoVol();
	}
	
	public String getPathOCR() {
		return getAbsolutePath().substring(0, getAbsolutePath().lastIndexOf('.'))+".txt";
	}
}
