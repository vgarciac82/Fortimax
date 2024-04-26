package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_pagina_id implements Serializable {
	private static final long serialVersionUID = -3358458607017396836L;
	public String TituloAplicacion;
	public int IdGabinete;
	public int IdCarpetaPadre;
	public int IdDocumento;
	public int NumeroPagina;
	
	public imx_pagina_id(){
	}
	
	public imx_pagina_id(String TituloAplicacion,int IdGabinete,int IdCarpetaPadre,int IdDocumento,int NumeroPagina){
		this.TituloAplicacion=TituloAplicacion;
		this.IdGabinete=IdGabinete;
		this.IdCarpetaPadre=IdCarpetaPadre;
		this.IdDocumento=IdDocumento;
		this.NumeroPagina=NumeroPagina;
	}
	
	public String getTituloAplicacion(){
		return this.TituloAplicacion;
	}
	public void setTituloAplicacion(String TituloAplicacion){
		this.TituloAplicacion=TituloAplicacion;
	}
	public int getIdGabinete(){
		return this.IdGabinete;
	}
	public void setIdGabinete(int IdGabinete){
		this.IdGabinete=IdGabinete;
	}
	public int getIdCarpetaPadre(){
		return this.IdCarpetaPadre;
	}
	public void setIdCarpetaPadre(int IdCarpetaPadre){
		this.IdCarpetaPadre=IdCarpetaPadre;
	}
	public int getIdDocumento(){
		return this.IdDocumento;
	}
	public void setIdDocumento(int IdDocumento){
		this.IdDocumento=IdDocumento;
	}
	public int getNumeroPagina(){
		return this.NumeroPagina;
	}
	public void setNumeroPagina(int NumeroPagina){
		this.NumeroPagina=NumeroPagina;
	}
	
	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return TituloAplicacion+"_G"+IdGabinete+"C"+IdCarpetaPadre+"D"+IdDocumento+"P"+NumeroPagina;
	}
}
