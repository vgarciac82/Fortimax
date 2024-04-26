package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;
import java.util.Date;

public class imx_bitacora implements Serializable {

	private static final long serialVersionUID = 9068078765700605025L;
	private long id;
	private Date fecha;
	private String clase;
	private String prioridad;
	private String mensaje;

	public imx_bitacora() {
		super();
	}
	
	public String getClase() {
		return clase;
	}

	public Date getFecha() {
		return fecha;
	}
    
	public String getMensaje() {
		return mensaje;
	}

	public String getPrioridad() {
		return prioridad;
	}

	public void setClase(String clase) {
		this.clase=clase;
	}

	public void setFecha(Date fecha) {
		this.fecha=fecha;
	}

	public void setMensaje(String mensaje) {
		this.mensaje=mensaje;
	}

	public void setPrioridad(String prioridad) {
		this.prioridad=prioridad;
	}
	
	@Override
	public String toString() {
		return "imx_bitacora [id="+ id +", fecha=" + fecha + ", clase="
				+ clase + ", prioridad=" + prioridad + ", mensaje=" + mensaje + "]";
	}

	@Override
	public boolean equals(Object other) {
		return this.toString().equals(other.toString());
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
