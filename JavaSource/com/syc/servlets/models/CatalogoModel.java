package com.syc.servlets.models;

import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.syc.fortimax.hibernate.entities.imx_catalogo;
import com.syc.fortimax.hibernate.managers.imx_catalogo_manager;

public class CatalogoModel {
	private String nombre;
	private String tipo;
	private String datos;
	private String definicion;
	private String datosBorrados;

	public CatalogoModel() {
		super();
		this.nombre = "";
		this.tipo = "";
		this.datos = "";
		this.definicion = "";
		this.datosBorrados="";
	}

	public String getNombreCat() {
		return nombre;
	}

	public void setNombreCat(String nombreCat) {
		this.nombre = nombreCat;
	}

	public String getTipoCat() {
		return tipo;
	}

	public void setTipoCat(String tipoCat) {
		this.tipo = tipoCat;
	}

	public String getDatosCat() {
		return datos;
	}

	public void setDatosCat(String datosCat) {
		this.datos = datosCat;
	}
	public String getDatosBorradosCat() {
		return datosBorrados;
	}

	public void setDatosBorradosCat(String datosBorradosCat) {
		this.datosBorrados = datosBorradosCat;
	}
	
	public static String getBlankCatalogo() {
		return "-Ninguna-";
	}
	
	public static BidiMap getCatalogos() throws Exception {
		BidiMap bidiMapCatalogos = new DualHashBidiMap();
		bidiMapCatalogos.put(0, getBlankCatalogo());
		
		List<imx_catalogo> catalogos = new imx_catalogo_manager().list();
		for (int i = 0; i < catalogos.size(); i++) {
			imx_catalogo catalogo = catalogos.get(i);
			bidiMapCatalogos.put(i+1,catalogo.getNombreCatalogo());
		}	
		return bidiMapCatalogos;
	}

	public String getDefinicion() {
		return definicion;
	}

	public void setDefinicion(String definicion) {
		this.definicion = definicion;
	}
}