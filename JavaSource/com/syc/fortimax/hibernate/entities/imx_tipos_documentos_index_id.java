package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_tipos_documentos_index_id extends imx_documento_id implements Serializable { 

	private static final long serialVersionUID = -5716417035999994007L;

	private int idTipoDocumento;
	private String nombreCampo;

	public imx_tipos_documentos_index_id() {
	}

	public imx_tipos_documentos_index_id(String tituloAplicacion, int idGabinete,
			int idCarpetaPadre, int idDocumento, int idTipoDocumento, String nombreCampo) {
		super(tituloAplicacion,idGabinete,idCarpetaPadre,idDocumento);
		this.idTipoDocumento = idTipoDocumento;
		this.nombreCampo = nombreCampo;
	}

	public int getIdTipoDocumento() {
		return idTipoDocumento;
	}
	
	public void setIdTipoDocumento(int idTipoDocumento) {
		this.idTipoDocumento=idTipoDocumento;
	}
	
	public String getNombreCampo() {
		return this.nombreCampo;
	}

	public void setNombreCampo(String nombreCampo) {
		this.nombreCampo = nombreCampo;
	}
	
	//Verificar si hashCode() y equals(Object other) no regresan falsos positivos
}
