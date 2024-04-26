package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_grupo_usuario_id implements Serializable {

	private static final long serialVersionUID = -388735908711004351L;

	private String nombreGrupo;
	private String nombreUsuario;

	public imx_grupo_usuario_id(String nombreGrupo, String nombreUsuario) {
		super();
		this.nombreGrupo = nombreGrupo;
		this.nombreUsuario = nombreUsuario;
	}

	public imx_grupo_usuario_id() {
		super();
	}

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof imx_grupo_usuario_id))
			return false;
		imx_grupo_usuario_id castOther = (imx_grupo_usuario_id) other;

		return ((this.getNombreGrupo() == castOther.getNombreGrupo()) || (this
				.getNombreGrupo() != null
				&& castOther.getNombreGrupo() != null && this.getNombreGrupo()
				.equals(castOther.getNombreGrupo())))
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
				+ (getNombreGrupo() == null ? 0 : this.getNombreGrupo()
						.hashCode());
		result = 37
				* result
				+ (getNombreUsuario() == null ? 0 : this.getNombreUsuario()
						.hashCode());
		return result;
	}

}
