package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_grupo_privilegio_id implements Serializable {

	private static final long serialVersionUID = 2781715459152982629L;

	private String tituloAplicacion;
	private String nombreGrupo;

	public imx_grupo_privilegio_id() {
	}

	public imx_grupo_privilegio_id(String tituloAplicacion, String nombreGrupo) {
		this.tituloAplicacion = tituloAplicacion;
		this.nombreGrupo = nombreGrupo;
	}

	public String getTituloAplicacion() {
		return this.tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public String getNombreGrupo() {
		return this.nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof imx_grupo_privilegio_id))
			return false;
		imx_grupo_privilegio_id castOther = (imx_grupo_privilegio_id) other;

		return ((this.getTituloAplicacion() == castOther.getTituloAplicacion()) || (this
				.getTituloAplicacion() != null
				&& castOther.getTituloAplicacion() != null && this
				.getTituloAplicacion().equals(castOther.getTituloAplicacion())))
				&& ((this.getNombreGrupo() == castOther.getNombreGrupo()) || (this
						.getNombreGrupo() != null
						&& castOther.getNombreGrupo() != null && this
						.getNombreGrupo().equals(castOther.getNombreGrupo())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getTituloAplicacion() == null ? 0 : this
						.getTituloAplicacion().hashCode());
		result = 37
				* result
				+ (getNombreGrupo() == null ? 0 : this.getNombreGrupo()
						.hashCode());
		return result;
	}
}
