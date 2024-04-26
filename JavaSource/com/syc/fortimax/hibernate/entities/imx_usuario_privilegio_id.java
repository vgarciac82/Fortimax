package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_usuario_privilegio_id implements Serializable {

	private static final long serialVersionUID = 1812079853698390468L;

	private String nombreUsuario;
	private String tituloAplicacion;

	public imx_usuario_privilegio_id() {
		super();
	}

	public imx_usuario_privilegio_id(String nombreUsuario,
			String tituloAplicacion) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.tituloAplicacion = tituloAplicacion;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getTituloAplicacion() {
		return tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof imx_usuario_privilegio_id))
			return false;
		imx_usuario_privilegio_id castOther = (imx_usuario_privilegio_id) other;

		return ((this.getTituloAplicacion() == castOther.getTituloAplicacion()) || (this
				.getTituloAplicacion() != null
				&& castOther.getTituloAplicacion() != null && this
				.getTituloAplicacion().equals(castOther.getTituloAplicacion())))
				&& ((this.getNombreUsuario() == castOther.getNombreUsuario()) || (this
						.getNombreUsuario() != null
						&& castOther.getNombreUsuario() != null && this
						.getNombreUsuario()
						.equals(castOther.getNombreUsuario())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getTituloAplicacion() == null ? 0 : this
						.getTituloAplicacion().hashCode());
		result = 37
				* result
				+ (getNombreUsuario() == null ? 0 : this.getNombreUsuario()
						.hashCode());
		return result;
	}

}
