package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_documento_id implements Serializable {

	private static final long serialVersionUID = -5716417035999994388L;

	private String tituloAplicacion;
	private int idGabinete;
	private int idCarpetaPadre;
	private int idDocumento;

	public imx_documento_id() {
	}

	public imx_documento_id(String tituloAplicacion, int idGabinete,
			int idCarpetaPadre, int idDocumento) {
		this.tituloAplicacion = tituloAplicacion;
		this.idGabinete = idGabinete;
		this.idCarpetaPadre = idCarpetaPadre;
		this.idDocumento = idDocumento;
	}

	public String getTituloAplicacion() {
		return this.tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public int getIdGabinete() {
		return this.idGabinete;
	}

	public void setIdGabinete(int idGabinete) {
		this.idGabinete = idGabinete;
	}

	public int getIdCarpetaPadre() {
		return this.idCarpetaPadre;
	}

	public void setIdCarpetaPadre(int idCarpetaPadre) {
		this.idCarpetaPadre = idCarpetaPadre;
	}

	public int getIdDocumento() {
		return this.idDocumento;
	}

	public void setIdDocumento(int idDocumento) {
		this.idDocumento = idDocumento;
	}
	
	@Override
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString(){
		return tituloAplicacion+"_G"+idGabinete+"C"+idCarpetaPadre+"D"+idDocumento;
	}

}
