package com.syc.dbobject;
import org.apache.log4j.Logger;

public class LlavePrimaria {
	
private static final Logger log = Logger.getLogger(LlavePrimaria.class);
	private String campo_pk=null;
	private String nombre_t=null;
	public LlavePrimaria(String nombre_t, String campo_pk){
		this.nombre_t=nombre_t;
		this.campo_pk=campo_pk;
	}
	public void setCampoPK(String campo_pk){
		
		this.campo_pk=campo_pk;
	}
	public String getCampoPK(){
		
		return this.campo_pk;
	}
	public void setNombreTabla(String nombre_t){
		
		this.nombre_t=nombre_t;
	}
	public String getNombreTabla(){
		
		return this.nombre_t;
	}	

}
