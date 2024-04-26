package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_org_carpeta implements Serializable {

	protected static final long serialVersionUID = -6516428089684984200L;

	protected imx_org_carpeta_id id;
	protected int idCarpetaPadre;
	protected String nombreHija;

	public imx_org_carpeta() {
	}

	public imx_org_carpeta(
			imx_org_carpeta_id id
			) {
		super();
		this.id = id;
	}

	public imx_org_carpeta_id getId() {
		return this.id;
	}

	public void setId(imx_org_carpeta_id id) {
		this.id = id;
	}

	public int getIdCarpetaPadre() {
		return idCarpetaPadre;
	}

	public void setIdCarpetaPadre(int idCarpetaPadre) {
		this.idCarpetaPadre = idCarpetaPadre;
	}

	public String getNombreHija() {
		return nombreHija;
	}

	public void setNombreHija(String nombreHija) {
		this.nombreHija = nombreHija;
	}

}
