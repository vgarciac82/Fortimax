package com.syc.fortimax.hibernate.entities;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class imx_seguridad_id implements Serializable {

private static final Logger log = Logger.getLogger(imx_seguridad_id.class);

	private static final long serialVersionUID = 7652764128131777797L;

	private String tituloAplicacion;
	private Integer prioridad;

	public imx_seguridad_id() {
		super();
	}

	public imx_seguridad_id(String tituloAplicacion, Integer prioridad) {
		super();
		this.tituloAplicacion = tituloAplicacion;
		this.prioridad = prioridad;
	}

	public String getTituloAplicacion() {
		return tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public Integer getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(Integer prioridad) {
		this.prioridad = prioridad;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
