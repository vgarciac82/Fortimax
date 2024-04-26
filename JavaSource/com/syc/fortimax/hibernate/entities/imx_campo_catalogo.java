package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_campo_catalogo implements Serializable {

	protected static final long serialVersionUID = -6516428089684984200L;

	protected imx_campo_catalogo_id id;

	public imx_campo_catalogo() {
	}

	public imx_campo_catalogo(
			imx_campo_catalogo_id id
			) {
		super();
		this.id = id;
	}

	public imx_campo_catalogo_id getId() {
		return this.id;
	}

	public void setId(imx_campo_catalogo_id id) {
		this.id = id;
	}

}
