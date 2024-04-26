package com.syc.fortimax.hibernate.entities;
import org.apache.log4j.Logger;

public class imx_carpeta_id implements java.io.Serializable {

private static final Logger log = Logger.getLogger(imx_carpeta_id.class);

	private static final long serialVersionUID = -2228788109425470265L;

	private String tituloAplicacion;
	private int idGabinete;
	private int idCarpeta;

	public imx_carpeta_id() {
	}

	public imx_carpeta_id(String tituloAplicacion, int idGabinete, int idCarpeta,
			int idDocumento) {
		this.tituloAplicacion = tituloAplicacion;
		this.idGabinete = idGabinete;
		this.idCarpeta = idCarpeta;

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

	public int getIdCarpeta() {
		return idCarpeta;
	}

	public void setIdCarpeta(int idCarpeta) {
		this.idCarpeta = idCarpeta;
	}

	public boolean equals(Object other) {
		return super.equals(other);
	}

	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString(){
		return tituloAplicacion+"_G"+idGabinete+"C"+idCarpeta;
	}
}
