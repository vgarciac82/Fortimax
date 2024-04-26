package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_tipos_documentos_index implements Serializable {

	protected static final long serialVersionUID = -6516428089684984200L;

	protected imx_tipos_documentos_index_id id;
	protected String valorCampo;

	public imx_tipos_documentos_index() {
	}

	public imx_tipos_documentos_index(imx_tipos_documentos_index_id id, String valorCampo) {
		super();
		this.id = id;
		this.valorCampo = valorCampo;
	}

	public imx_tipos_documentos_index_id getId() {
		return this.id;
	}

	public void setId(imx_tipos_documentos_index_id id) {
		this.id = id;
	}

	public String getValorCampo() {
		return this.valorCampo;
	}

	public void setValorCampo(String valorCampo) {
		this.valorCampo = valorCampo;
	}
}
