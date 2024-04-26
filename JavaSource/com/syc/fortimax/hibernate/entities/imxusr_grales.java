package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imxusr_grales implements Serializable {

	private static final long serialVersionUID = -7713302530363515977L;

	private Integer id_gabinete;
	private String activo;
	private String propietario;

	public imxusr_grales() {
		super();
	}

	public imxusr_grales(imxusr_grales r) {
		super();
		this.id_gabinete = r.id_gabinete;
		this.activo = r.activo;
		this.propietario = r.propietario;
	}
	
	public imxusr_grales(Integer id_gabinete, String activo, String propietario) {
		super();
		this.id_gabinete = id_gabinete;
		this.activo = activo;
		this.propietario = propietario;
	}
	
	public Integer getId_gabinete() {
		return id_gabinete;
	}

	public void setId_gabinete(Integer id_gabinete) {
		this.id_gabinete = id_gabinete;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public String getPropietario() {
		return propietario;
	}

	public void setPropietario(String propietario) {
		this.propietario = propietario;
	}
	
	@Override
	public String toString() {
		return "imxusr_grales [id_gabinete=" + id_gabinete + ", activo="
				+ activo + ", propietario=" + propietario + "]";
	}
}
