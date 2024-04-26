package com.syc.fortimax.hibernate.entities;

import java.util.HashSet;
import java.util.Set;

public class imx_tipo_usuario implements java.io.Serializable {

	private static final long serialVersionUID = -7714194179796667764L;

	private int tipoUsuario;
	private String tuDescripcion;
	private Set imxUsuarios = new HashSet(0);

	public imx_tipo_usuario() {
	}

	public imx_tipo_usuario(int tipoUsuario, String tuDescripcion) {
		this.tipoUsuario = tipoUsuario;
		this.tuDescripcion = tuDescripcion;
	}

	public imx_tipo_usuario(int tipoUsuario, String tuDescripcion,
			Set imxUsuarios) {
		this.tipoUsuario = tipoUsuario;
		this.tuDescripcion = tuDescripcion;
		this.imxUsuarios = imxUsuarios;
	}

	public int getTipoUsuario() {
		return this.tipoUsuario;
	}

	public void setTipoUsuario(int tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getTuDescripcion() {
		return this.tuDescripcion;
	}

	public void setTuDescripcion(String tuDescripcion) {
		this.tuDescripcion = tuDescripcion;
	}

	public Set getImxUsuarios() {
		return this.imxUsuarios;
	}

	public void setImxUsuarios(Set imxUsuarios) {
		this.imxUsuarios = imxUsuarios;
	}

}
