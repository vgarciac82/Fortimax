package com.syc.dbobject;
import org.apache.log4j.Logger;


public class Indice {


	private static final Logger log = Logger.getLogger(Indice.class);
	private String tabla;
	private String campo;
	//private String nombre_indice;
	private int tipo_indice;
	private String nombre;
	
	public Indice(String tabla, String campo, int tipo_indice){
		this.tabla = tabla;
		this.campo = campo;
		this.tipo_indice = tipo_indice;
		createNombre();
		//log.debug("Indice " + tipo_indice);
	}
	
	private void createNombre() {
		String nombre_indice = "I_";
		if (tipo_indice == 2) {// unique
			nombre_indice += "U_";
		}
		this.nombre = nombre_indice += (getTabla().length() > 10 ? getTabla().substring(0, 10) : getTabla())
				+ "_"
				+ System.currentTimeMillis();
	}
	
	public int getTipoIndice(){
		
		return tipo_indice;
	}
	
	public String getTabla(){
		
		return tabla;
	}
	
	public String getCampo(){
		
		return campo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
