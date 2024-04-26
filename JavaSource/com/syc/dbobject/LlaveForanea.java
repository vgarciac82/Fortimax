package com.syc.dbobject;
import org.apache.log4j.Logger;



public class LlaveForanea {

	private static final Logger log = Logger.getLogger(LlaveForanea.class);
	
	private String nombre_t=null;
	private String campo_fk=null;
	private String tabla_origen=null;
	private String campo_origen=null;
	
	public LlaveForanea(String nombre_t,String campo_fk){
		this.campo_fk=campo_fk;
		this.nombre_t=nombre_t;
	}
	public LlaveForanea(String nombre_t,String campo_fk,String tabla_origen,String campo_origen){
		this.nombre_t=nombre_t;
		this.campo_fk=campo_fk;
		this.tabla_origen=tabla_origen;
		this.campo_origen=campo_origen;
	}
	public void setNombreTabla(String nombre_t){
		
		
		this.nombre_t=nombre_t;
	}
	public void setCampoFK(String campo_fk){
		
		this.campo_fk=campo_fk;
	}
	public void setTablaOrigen(String tabla_origen){
		
		this.tabla_origen=tabla_origen;
	}
	public void setCampoOrigen(String campo_origen){
		
		this.campo_origen=campo_origen;
	}
	public String getNombreTabla(){
		
		return this.nombre_t;
	}	
	public String getCampoFK(){
		
		return this.campo_fk;
	}
	public String getTablaOrigen(){
		
		return this.tabla_origen;
	}
	public String getCampoOrigen(){
		
		return this.campo_origen;
	}

}