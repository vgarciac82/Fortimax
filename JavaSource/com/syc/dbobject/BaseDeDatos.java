package com.syc.dbobject;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class BaseDeDatos {

private static final Logger log = Logger.getLogger(BaseDeDatos.class);
	private ArrayList<Tabla> tablas;
	
	public BaseDeDatos(){
		this.tablas=new ArrayList<Tabla>();
	}
	
	public void addTabla(Tabla tabla){
		
		this.tablas.add(tabla);
	}

	public Tabla getTabla(int indice){
		
		return this.tablas.get(indice);
	}
	
	public ArrayList<Tabla> getTablas(){

		return this.tablas;
	}
}
