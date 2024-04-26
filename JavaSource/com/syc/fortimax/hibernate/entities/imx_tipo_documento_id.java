package com.syc.fortimax.hibernate.entities;
import org.apache.log4j.Logger;

public class imx_tipo_documento_id implements java.io.Serializable {

private static final Logger log = Logger.getLogger(imx_tipo_documento_id.class);

	private static final long serialVersionUID = -8496021938092798354L;

	private String tituloAplicacion;
	private int idTipoDocto;

	public imx_tipo_documento_id() {
		super();
	}

	public imx_tipo_documento_id(String tituloAplicacion, int idTipoDocto) {
		super();
		this.tituloAplicacion = tituloAplicacion;
		this.idTipoDocto = idTipoDocto;
	}

	public int getIdTipoDocto() {
		return idTipoDocto;
	}

	public void setIdTipoDocto(int idTipoDocto) {
		this.idTipoDocto = idTipoDocto;
	}

	public String getTituloAplicacion() {
		return this.tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public boolean equals(Object other) {
		return super.equals(other);
	}

	public int hashCode() {
		return super.hashCode();
	}

}
