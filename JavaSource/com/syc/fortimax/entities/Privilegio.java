package com.syc.fortimax.entities;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class Privilegio implements Serializable {

	private static final Logger log = Logger.getLogger(Privilegio.class);

	private static final long serialVersionUID = -8753199100581811307L;

	private String tituloAplicacion;
	private String descripcion;
	private String nombreNivel;
	private Integer privilegio;

	public Privilegio() {
		super();
	}

	public Privilegio(String tituloAplicacion, String descripcion,
			String nombreNivel, Integer privilegio) {
		super();
		this.tituloAplicacion = tituloAplicacion;
		this.descripcion = descripcion;
		this.nombreNivel = nombreNivel;
		this.privilegio = privilegio;
	}

	public Privilegio(Object[] row) {
		super();
		if (row != null) {
			if (row.length >= 1 && row[0] instanceof String) {
				this.tituloAplicacion = (String) row[0];
			}
			if (row.length >= 2 && row[1] instanceof String) {
				this.descripcion = (String) row[1];
			}
			if (row.length >= 3 && row[2] instanceof String) {
				this.nombreNivel = (String) row[2];
			}
			if (row.length >= 4) {
				try {
					this.privilegio = Integer.parseInt(row[3] + "");
				} catch (Exception E) {
					log.error(E,E);					
					this.privilegio = -1;
				}
			}
		}
	}

	public String getTituloAplicacion() {
		return tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNombreNivel() {
		return nombreNivel;
	}

	public void setNombreNivel(String nombreNivel) {
		this.nombreNivel = nombreNivel;
	}

	public Integer getPrivilegio() {
		return privilegio;
	}

	public void setPrivilegio(Integer privilegio) {
		this.privilegio = privilegio;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Titulo Aplicacion: ");
		sb.append(this.tituloAplicacion);
		sb.append(" - Privilegio :");
		sb.append(this.privilegio);
		return sb.toString();
	}

	
	
}
