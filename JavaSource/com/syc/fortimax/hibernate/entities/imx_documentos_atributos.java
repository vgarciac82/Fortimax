package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class imx_documentos_atributos implements Serializable {

	private static final Logger log = Logger.getLogger(imx_documentos_atributos.class);
	
	private static final long serialVersionUID = -193854310375657816L;
	
	protected imx_documentos_atributos_id id;
	protected String valor;


	public imx_documentos_atributos() {
	}

	public imx_documentos_atributos(imx_documentos_atributos_id id, String valor) {
		super();
		this.id = id;
		this.valor = valor;

	}

	public imx_documentos_atributos_id getId() {
		return this.id;
	}

	public void setId(imx_documentos_atributos_id id) {
		this.id = id;
	}

	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}	
	
	@Override
	public String toString() {
		return "imx_documentos_atributos [id=" + id + ", valor=" + valor + "]";
	}
}
