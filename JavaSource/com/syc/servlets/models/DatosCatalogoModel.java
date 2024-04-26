package com.syc.servlets.models;

public class DatosCatalogoModel {
		private int id;
		private String nombre;
		
		public DatosCatalogoModel(){
			super();
			this.id=0;
			this.nombre="";
		}
		
		public int getId(){
			return this.id;
		}
		public void setId(int Id){
			this.id=Id;
		}
		public String getNombre(){
			return this.nombre;
		}
		public void setNombre(String Nombre){
			this.nombre=Nombre;
		}
}
