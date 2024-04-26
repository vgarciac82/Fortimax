package com.syc.fortimax.hibernate.entities;
import org.apache.log4j.Logger;


public class imx_tipo_documento implements java.io.Serializable {

private static final Logger log = Logger.getLogger(imx_tipo_documento.class);

	private static final long serialVersionUID = -2971641591399866010L;

	private imx_tipo_documento_id id;
	private Integer prioridad;
	private String nombreTipoDocto;
	private String descripcion;

	public imx_tipo_documento() {
		super();

	}

	public imx_tipo_documento(imx_tipo_documento_id id, Integer prioridad,
			String nombreTipoDocto, String descripcion) {
		super();
		this.id = id;
		this.prioridad = prioridad;
		this.nombreTipoDocto = nombreTipoDocto;
		this.descripcion = descripcion;
	}

	public imx_tipo_documento_id getId() {
		return id;
	}

	public void setId(imx_tipo_documento_id id) {
		this.id = id;
	}

	public Integer getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(Integer prioridad) {
		this.prioridad = prioridad;
	}

	public String getNombreTipoDocto() {
		return nombreTipoDocto;
	}

	public void setNombreTipoDocto(String nombreTipoDocto) {
		this.nombreTipoDocto = nombreTipoDocto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
