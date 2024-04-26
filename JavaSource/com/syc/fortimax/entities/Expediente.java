package com.syc.fortimax.entities;

import java.io.Serializable;
import java.util.HashMap;

public class Expediente extends HashMap<String, Object> implements Serializable {

	private static final long serialVersionUID = 7384833145937672043L;

	private String tituloAplicacion;
	private int idGainete;
	private String activo;

	public Expediente() {
		super();
	}

	
	
	public Expediente(String tituloAplicacion, int idGainete, String activo) {
		super();
		this.tituloAplicacion = tituloAplicacion;
		this.idGainete = idGainete;
		this.activo = activo;
	}



	public Expediente(String tituloAplicacion, String[] field,
			Object[] value) {
		super();
		this.tituloAplicacion = tituloAplicacion;
		for (int i = 0; i < value.length && i < field.length; i++) {
			this.put(field[i].toUpperCase(), value[i]);
		}
	}

	public String getTituloAplicacion() {
		return tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public int getIdGainete() {
		return idGainete;
	}

	public void setIdGainete(int idGainete) {
		this.idGainete = idGainete;
	}



	public String getActivo() {
		return activo;
	}



	public void setActivo(String activo) {
		this.activo = activo;
	}



}