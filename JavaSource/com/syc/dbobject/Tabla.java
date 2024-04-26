package com.syc.dbobject;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Tabla {

private static final Logger log = Logger.getLogger(Tabla.class);
	
	private String nombreTabla;
	private ArrayList<Campo> campos;
	private ArrayList<LlaveForanea> foraneas;
	private ArrayList<LlavePrimaria> primarias;
	private ArrayList<Indice> indices;
	
	public Tabla(String nombreTabla){
		this.nombreTabla = nombreTabla;
		campos = new ArrayList<Campo>();
		foraneas = new ArrayList<LlaveForanea>();
		primarias = new ArrayList<LlavePrimaria>();
		indices = new ArrayList<Indice>();
	}
	
	public String getNombreTabla(){
		
		return nombreTabla;
	}
	
	public void addCampo(Campo campo){
		
		this.campos.add(campo);
	}
	
	public Campo getCampo(int indice){
		
		return this.campos.get(indice);
	}
	
	public ArrayList<Campo> getCampos(){
		return campos;
	}
	
	public void addLlaveForanea(LlaveForanea foranea){
		
		foraneas.add(foranea);
	}
	
	public LlaveForanea getLlaveForanea(int indice){
		
		return foraneas.get(indice);
	}
	
	public ArrayList<LlaveForanea> getLlavesForaneas(){
		return foraneas;
	}
	
	public void addLlavePrimaria(LlavePrimaria primaria){
		primarias.add(primaria);
	}
	
	public LlavePrimaria getLlavePrimaria(int indice){
		return primarias.get(indice);
	}
	
	public ArrayList<LlavePrimaria> getLlavesPrimarias(){
		return primarias;
	}
	
	public void addIndice(Indice indice){
		this.indices.add(indice);
	}
	public Indice getIndice(int indice){
		
		return indices.get(indice);
	}
	public ArrayList<Indice> getIndices(){
		return indices;
	}
	


}
