package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_usuario_expediente_id implements Serializable {

	private static final long serialVersionUID = -7713302530363515977L;

	private String nombre_usuario;
	private Integer id_gabinete;

	public imx_usuario_expediente_id() {
		super();
	}

	public imx_usuario_expediente_id(imx_usuario_expediente_id r) {
		super();
		this.nombre_usuario = r.nombre_usuario;
		this.id_gabinete = r.id_gabinete;
	}
	
	public imx_usuario_expediente_id(String nombre_usuario, Integer id_gabinete) {
		super();
		this.nombre_usuario = nombre_usuario;
		this.id_gabinete = id_gabinete;
	}
	
	public String getNombre_usuario() {
		return nombre_usuario;
	}

	public void setNombre_usuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}

	public Integer getId_gabinete() {
		return id_gabinete;
	}

	public void setId_gabinete(Integer id_gabinete) {
		this.id_gabinete = id_gabinete;
	}
	
	@Override
	public String toString() {
		return "imx_usuario_expediente_id [nombre_usuario=" + nombre_usuario
				+ ", id_gabinete=" + id_gabinete + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id_gabinete == null) ? 0 : id_gabinete.hashCode());
		result = prime * result
				+ ((nombre_usuario == null) ? 0 : nombre_usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		imx_usuario_expediente_id other = (imx_usuario_expediente_id) obj;
		if (id_gabinete == null) {
			if (other.id_gabinete != null)
				return false;
		} else if (!id_gabinete.equals(other.id_gabinete))
			return false;
		if (nombre_usuario == null) {
			if (other.nombre_usuario != null)
				return false;
		} else if (!nombre_usuario.equals(other.nombre_usuario))
			return false;
		return true;
	}
}
