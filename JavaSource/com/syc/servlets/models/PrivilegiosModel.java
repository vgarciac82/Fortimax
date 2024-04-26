package com.syc.servlets.models;

public class PrivilegiosModel {
	public String nombreC;
	public String gavetas;
	public String gavetasE;
	public String gaveta;
	public String privilegio;
	public String privilegios;
	
	public PrivilegiosModel(){
		super();
		this.nombreC="";
		this.gavetas="";
		this.gavetasE="";
		this.gaveta="";
		this.privilegio="";
		this.privilegios="";
	}
	
	public String getnombreC(){
		return this.nombreC;
	}
	public void setnombreC(String nombreC){
		this.nombreC=nombreC;
	}
	public String getgavetas(){
		return this.gavetas;
	}
	public void setgavetas(String gavetas){
		this.gavetas=gavetas;
	}
	public String getgavetasE(){
		return this.gavetasE;
	}
	public void setgavetasE(String gavetasE){
		this.gavetasE=gavetasE;
	}
	public String getgaveta(){
		return this.gaveta;
	}
	public void setgaveta(String gaveta){
		this.gaveta=gaveta;
	}
	public String getprivilegio(){
		return this.privilegio;
	}
	public void setprivilegio(String privilegio){
		this.privilegio=privilegio;
	}
	public String getprivilegios(){
		return this.privilegios;
	}
	public void setprivilegios(String privilegios){
		this.privilegios=privilegios;
	}

}
