package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_historico_documento_id extends imx_documento_id implements Serializable { 

	private static final long serialVersionUID = -5716417035999994007L;

	private Integer idVersion;

	public imx_historico_documento_id() {
	}
	
	public imx_historico_documento_id(imx_documento_id imx_documento_id, Integer idVersion) {
		this(imx_documento_id.getTituloAplicacion(),imx_documento_id.getIdGabinete(),imx_documento_id.getIdCarpetaPadre(),imx_documento_id.getIdDocumento(),idVersion);
	}

	public imx_historico_documento_id(String tituloAplicacion, int idGabinete, int idCarpetaPadre, int idDocumento, Integer idVersion) {
		super(tituloAplicacion,idGabinete,idCarpetaPadre,idDocumento);
		this.idVersion = idVersion;
	}

	public Integer getIdVersion() {
		return idVersion;
	}
	
	public void setIdVersion(Integer idVersion) {
		this.idVersion=idVersion;
	}
	
	//Verificar si hashCode() y equals(Object other) no regresan falsos positivos
}
