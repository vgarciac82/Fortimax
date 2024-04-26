package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_campo_catalogo_id implements Serializable {

	private static final long serialVersionUID = -5716417035999994388L;

	private String tituloAplicacion;
	private String nombreCampo;
	private String nombreCatalogo;

	public imx_campo_catalogo_id() {
	}

	public imx_campo_catalogo_id(String tituloAplicacion, String nombreCampo, String nombreCatalogo) {
		this.tituloAplicacion = tituloAplicacion;
		this.nombreCampo = nombreCampo;
		this.nombreCatalogo = nombreCatalogo;
	}

	public String getTituloAplicacion() {
		return this.tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}
	
	public String getNombreCampo() {
		return nombreCampo;
	}

	public void setNombreCampo(String nombreCampo) {
		this.nombreCampo = nombreCampo;
	}

	public String getNombreCatalogo() {
		return nombreCatalogo;
	}

	public void setNombreCatalogo(String nombreCatalogo) {
		this.nombreCatalogo = nombreCatalogo;
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
