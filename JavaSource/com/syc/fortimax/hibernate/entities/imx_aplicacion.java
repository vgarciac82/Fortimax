package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class imx_aplicacion implements Serializable {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(imx_aplicacion.class);

	private static final long serialVersionUID = -2073887416810991354L;

	private String tituloAplicacion;
	private String tblAplicacion;
	private String descripcion;
	private Set<Object> imxPrivilegios = new HashSet<Object>(0);

	public imx_aplicacion() {
		super();
	}

	public imx_aplicacion(String tituloAplicacion, String tblAplicacion,
			String descripcion) {
		super();
		this.tituloAplicacion = tituloAplicacion;
		this.tblAplicacion = tblAplicacion;
		this.descripcion = descripcion;
	}

	public String getTituloAplicacion() {
		return tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public String getTblAplicacion() {
		return tblAplicacion;
	}

	public void setTblAplicacion(String tblAplicacion) {
		this.tblAplicacion = tblAplicacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Set<Object> getImxPrivilegios() {
		return imxPrivilegios;
	}

	public void setImxPrivilegios(Set<Object> imxPrivilegios) {
		this.imxPrivilegios = imxPrivilegios;
	}

}
