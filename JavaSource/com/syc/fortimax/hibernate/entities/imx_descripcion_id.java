package com.syc.fortimax.hibernate.entities;

public class imx_descripcion_id implements java.io.Serializable {
	private static final long serialVersionUID = 3272825206225340609L;
	private String tituloAplicacion;
	private String nombreCampo;
	public imx_descripcion_id(){
		
	}
	public imx_descripcion_id(String tituloAplicacion, String nombreCampo) {
		this.tituloAplicacion = tituloAplicacion;
		this.nombreCampo = nombreCampo;
	}

	public String getTituloAplicacion() {
		return tituloAplicacion;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.tituloAplicacion = tituloAplicacion;
	}

	public String getNombreCampo() {
		return nombreCampo;
	}

	public void setNombreCampo(String nombreCampo) {
		this.nombreCampo = nombreCampo;
	}

	@Override
	public int hashCode() {
		return this.tituloAplicacion.hashCode() * this.nombreCampo.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj instanceof imx_descripcion_id) {
			if (this.nombreCampo.equals(((imx_descripcion_id) obj).nombreCampo)
					&& this.tituloAplicacion
							.equals(((imx_descripcion_id) obj).tituloAplicacion)) {
				equal = true;
			}
		} else {
			equal = false;
		}
		return equal;
	}

}
