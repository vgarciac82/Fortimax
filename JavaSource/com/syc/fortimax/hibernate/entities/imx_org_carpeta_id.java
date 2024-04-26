package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_org_carpeta_id implements Serializable {

	private static final long serialVersionUID = -5716417035999994388L;

	private String tituloAplicacion;
	private int idGabinete;
	private int idCarpetaHija;

	public imx_org_carpeta_id() {
	}

	public imx_org_carpeta_id(String tituloAplicacion, int idGabinete, int idCarpetaHija) {
		this.tituloAplicacion = tituloAplicacion;
		this.idGabinete = idGabinete;
		this.idCarpetaHija = idCarpetaHija;
	}

	public String getTituloAplicacion() {
		return this.tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}
	
	public int getIdGabinete() {
		return idGabinete;
	}

	public void setIdGabinete(int idGabinete) {
		this.idGabinete = idGabinete;
	}

	public int getIdCarpetaHija() {
		return idCarpetaHija;
	}

	public void setIdCarpetaHija(int idCarpetaHija) {
		this.idCarpetaHija = idCarpetaHija;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
