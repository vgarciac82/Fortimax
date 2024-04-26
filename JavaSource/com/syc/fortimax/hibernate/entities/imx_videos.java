package com.syc.fortimax.hibernate.entities;

public class imx_videos {
	public int Id;
	public String Seccion;
	public String NombreVideo;
	public String Descripcion;
	public String Ruta;
	public String Administrador;
	
	public imx_videos() {
	}
	
	public imx_videos(int Id, String Seccion, String NombreVideo, String Descripcion,
			String Ruta, String Administrador) {
		this.Id = Id;
		this.Seccion = Seccion;
		this.NombreVideo = NombreVideo;
		this.Descripcion = Descripcion;
		this.Ruta = Ruta;
		this.Administrador = Administrador;
	}
	public int getId(){
		return this.Id;
	}
	public void setId(int Id){
		this.Id=Id;
	}
	public String getSeccion(){
		return this.Seccion;
	}
	public void setSeccion(String Seccion){
		this.Seccion=Seccion;
	}
	public String getNombreVideo(){
		return this.NombreVideo;
	}
	public void setNombreVideo(String NombreVideo){
		this.NombreVideo=NombreVideo;
	}
	public String getDescripcion(){
		return this.Descripcion;
	}
	public void setDescripcion(String Descripcion){
		this.Descripcion=Descripcion;
	}
	public String getRuta(){
		return this.Ruta;
	}
	public void setRuta(String Ruta){
		this.Ruta=Ruta;
	}
	public String getAdministrador(){
		return this.Administrador;
	}
	public void setAdministrador(String Administrador){
		this.Administrador=Administrador;
	}
}
