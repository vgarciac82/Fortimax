package com.syc.gavetas;
import java.util.ArrayList;

import org.apache.log4j.Logger;
public class SeguridadGavetas
{
	private static final Logger log = Logger.getLogger(SeguridadGavetas.class);
	private String titulo_aplicacion; 
	private int prioridad; 
	private String nombre_nivel; 
	private String descripcion;
	
	public SeguridadGavetas( String titulo_aplicacion )
	{
		
		this.titulo_aplicacion = titulo_aplicacion;
	}
	public SeguridadGavetas( String titulo_aplicacion, int prioridad, String nombre_nivel, String descripcion )
	{	
		
		this.titulo_aplicacion = titulo_aplicacion;
		this.prioridad = prioridad;
		this.nombre_nivel = nombre_nivel;
		this.descripcion  = descripcion;
		
	}
	public String getDescripcion()
   {
	
   	return descripcion;
   }
	public void setDescripcion( String descripcion )
   {
	
   	this.descripcion = descripcion;
   }
	public String getNombre_nivel()
   {	
	
   	return nombre_nivel;
   }
	public void setNombre_nivel( String nombre_nivel )
   {
	
   	this.nombre_nivel = nombre_nivel;
   }
	public String getTitulo_aplicacion()
   {	
	
   	return titulo_aplicacion;
   }
	public void setTitulo_aplicacion( String titulo_aplicacion )
   {
	
   	this.titulo_aplicacion = titulo_aplicacion;
   }
	public int getPrioridad()
   {	
	
   	return prioridad;
   }
	public void setPrioridad( int prioridad )
   {
	
   	this.prioridad = prioridad;
   }
	
	public ArrayList<SeguridadGavetas> getSeguridadIMXDefault()
	{
		//log.debug("Creando Seguridad en Gavetas por Default");
		ArrayList<SeguridadGavetas> permisos = new ArrayList<SeguridadGavetas>();
		
		SeguridadGavetas IMX_SIN_NIVEL = new SeguridadGavetas( this.getTitulo_aplicacion(), -1,	"IMX_SIN_NIVEL",	"SIN_NIVEL" );
		SeguridadGavetas PRIVADO= new SeguridadGavetas( this.getTitulo_aplicacion(), 0,	"PRIVADO",	"NIVEL DE PRIORDAD PRIVADO" );
		SeguridadGavetas PROPIETARIO= new SeguridadGavetas( this.getTitulo_aplicacion(),1,	"PROPIETARIO",	"NIVEL DE PRIORDAD PROPIETARIO" );
		SeguridadGavetas GRUPO= new SeguridadGavetas( this.getTitulo_aplicacion(), 2,	"GRUPO",	"NIVEL DE PRIORDAD GRUPO" );
		SeguridadGavetas TODOS= new SeguridadGavetas( this.getTitulo_aplicacion(), 3,	"TODOS",	"NIVEL DE PRIORDAD TODOS" );
		//LHMJ Se agrego un perfil para evitar problemas con los permisos de usuarios
		SeguridadGavetas PERSONALIZADO= new SeguridadGavetas( this.getTitulo_aplicacion(), 4,	"PERSONALIZADO",	"NIVEL DE PRIORDAD PERSONALIZADO" );
		
		permisos.add( IMX_SIN_NIVEL );
		permisos.add( PRIVADO );
		permisos.add( PROPIETARIO );
		permisos.add( GRUPO );
		permisos.add( TODOS );
		permisos.add( PERSONALIZADO );
		//log.debug("Lista de permisos: "+ permisos );
		
		return permisos;
	}
}
