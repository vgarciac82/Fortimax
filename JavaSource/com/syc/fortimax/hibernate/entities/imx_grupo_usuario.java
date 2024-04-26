package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_grupo_usuario implements Serializable {

	private static final long serialVersionUID = -2472220923167353671L;

	private imx_grupo_usuario_id id;
	private imx_grupo imxGrupo;

	public imx_grupo_usuario() {
		super();
	}

	public imx_grupo_usuario(imx_grupo_usuario_id id, imx_grupo imxGrupo) {
		super();
		this.id = id;
		this.imxGrupo = imxGrupo;
	}

	public imx_grupo_usuario(imx_grupo_usuario_id id) {
		super();
		this.id = id;
	}

	public imx_grupo_usuario_id getId() {
		return id;
	}

	public void setId(imx_grupo_usuario_id id) {
		this.id = id;
	}

	public imx_grupo getImxGrupo() {
		return imxGrupo;
	}

	public void setImxGrupo(imx_grupo imxGrupo) {
		this.imxGrupo = imxGrupo;
	}

}
