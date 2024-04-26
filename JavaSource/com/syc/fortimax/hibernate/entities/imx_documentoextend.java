package com.syc.fortimax.hibernate.entities;
//TODO:Se mapea la tabla documento extend pero esta debe ser eliminada en un futuro y agregar esos campos en la tabla imx_documento
public class imx_documentoextend {
	private String tokenCompartir;
	private String banderaDescarga;
	
	public imx_documentoextend(){	
	}

	public String getTokenCompartir() {
		return tokenCompartir;
	}

	public void setTokenCompartir(String tokenCompartir) {
		this.tokenCompartir = tokenCompartir;
	}

	public String getBanderaDescarga() {
		return banderaDescarga;
	}

	public void setBanderaDescarga(String banderaDescarga) {
		this.banderaDescarga = banderaDescarga;
	}
	
	
}
