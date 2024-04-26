package com.syc.servlets.models;

public class AtributosModel {
	
	int id_atributo;
	Object valor_default;
	boolean activo;
	boolean modificable;
	
	public int getIdAtributo() {
		return id_atributo;
	}
	public void setIdAtributo(int idAtributo) {
		this.id_atributo = idAtributo;
	}
	
	public Object getValorDefault() {
		return valor_default;
	}
	public void setValorDefault(Object valorDefault) {
		this.valor_default = valorDefault;
	}

	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	public boolean isModificable() {
		return modificable;
	}
	public void setModificable(boolean modificable) {
		this.modificable = modificable;
	}

}
