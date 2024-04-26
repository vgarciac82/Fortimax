package com.syc.fortimax.hibernate.entities;
import java.io.Serializable;

import org.apache.log4j.Logger;


public class imx_seguridad implements Serializable {

private static final Logger log = Logger.getLogger(imx_seguridad.class);

	private static final long serialVersionUID = -6785067686015226379L;

	private imx_seguridad_id id;
	private String nombreNivel;
	private String descripcion;

	public imx_seguridad() {
		super();
	}

	public imx_seguridad(imx_seguridad_id id, String nombreNivel,
			String descripcion) {
		super();
		this.id = id;
		this.nombreNivel = nombreNivel;
		this.descripcion = descripcion;
	}

	public imx_seguridad(Object[] seguridad) {
		this.id = new imx_seguridad_id();
		if (seguridad.length >= 1 && seguridad[0] instanceof String) {
			this.id.setTituloAplicacion((String) seguridad[0]);
		}
		try {
			if (seguridad.length >= 2) {
				this.id.setPrioridad(Integer.parseInt( seguridad[1]+""));
			}
		} catch (NumberFormatException e) {	log.error(e,e);

			this.id.setPrioridad(-1);
		}
		if (seguridad.length >= 3 && seguridad[2] instanceof String) {
			this.nombreNivel =(String) seguridad[2];
		}
		if (seguridad.length >= 4 && seguridad[3] instanceof String) {
			this.descripcion =(String) seguridad[3];
		}
	}

	public imx_seguridad_id getId() {
		return id;
	}

	public void setId(imx_seguridad_id id) {
		this.id = id;
	}

	public String getNombreNivel() {
		return nombreNivel;
	}

	public void setNombreNivel(String nombreNivel) {
		this.nombreNivel = nombreNivel;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
