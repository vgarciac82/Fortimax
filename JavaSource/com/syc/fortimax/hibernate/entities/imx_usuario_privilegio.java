package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_usuario_privilegio implements Serializable {

	private static final long serialVersionUID = 8069803302129439480L;

	private imx_usuario_privilegio_id id;
	private imx_usuario imxUsuario;
	private imx_aplicacion imxAplicacion;
	private String nombreNivel;
	private int privilegio;

	public imx_usuario_privilegio() {
		super();
	}

	public imx_usuario_privilegio(imx_usuario_privilegio_id id,
			String nombreNivel, Integer privilegio) {
		super();
		this.id = id;
		this.nombreNivel = nombreNivel;
		this.privilegio = privilegio;
	}

	public imx_usuario_privilegio_id getId() {
		return id;
	}

	public void setId(imx_usuario_privilegio_id id) {
		this.id = id;
	}

	public String getNombreNivel() {
		return nombreNivel;
	}

	public void setNombreNivel(String nombreNivel) {
		this.nombreNivel = nombreNivel;
	}

	public Integer getPrivilegio() {
		return privilegio;
	}

	public void setPrivilegio(Integer privilegio) {
		this.privilegio = privilegio;
	}

	public imx_usuario getImxUsuario() {
		return imxUsuario;
	}

	public void setImxUsuario(imx_usuario imxUsuario) {
		this.imxUsuario = imxUsuario;
	}

	public imx_aplicacion getImxAplicacion() {
		return imxAplicacion;
	}

	public void setImxAplicacion(imx_aplicacion imxAplicacion) {
		this.imxAplicacion = imxAplicacion;
	}

	public void setPrivilegio(int privilegio) {
		this.privilegio = privilegio;
	}

}
