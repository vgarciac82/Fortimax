package com.syc.gavetas;
import java.util.ArrayList;

import org.apache.log4j.Logger;
public class TipoDocumento
{
	private static final Logger log = Logger.getLogger(TipoDocumento.class);
	private String titulo_aplicacion;
	private int id_tipo_docto;
	private int prioridad;
	private String nombre_tipo_docto; 
	private String descripcion;

	
	
	public TipoDocumento( String titulo_aplicacion, int id_tipo_docto, int prioridad, String nombre_tipo_docto, String descripcion )
   {
	   //log.debug("Creando Objeto TipoDocumento");
	   this.titulo_aplicacion = titulo_aplicacion;
	   this.id_tipo_docto = id_tipo_docto;
	   this.prioridad = prioridad;
	   this.nombre_tipo_docto = nombre_tipo_docto;
	   this.descripcion = descripcion;
   }

	public TipoDocumento( String titulo_aplicacion )
   {
		
	   this.titulo_aplicacion = titulo_aplicacion;

   }
	
	public String getDescripcion()
   {
	
   	return descripcion;
   }


	public void setDescripcion( String descripcion )
   {
	
   	this.descripcion = descripcion;
   }


	public int getId_tipo_docto()
   {
	
   	return id_tipo_docto;
   }


	public void setId_tipo_docto( int id_tipo_docto )
   {
	
   	this.id_tipo_docto = id_tipo_docto;
   }


	public String getNombre_tipo_docto()
   {	
	
   	return nombre_tipo_docto;
   }


	public void setNombre_tipo_docto( String nombre_tipo_docto )
   {
	
   	this.nombre_tipo_docto = nombre_tipo_docto;
   }


	public int getPrioridad()
   {
	
   	return prioridad;
   }


	public void setPrioridad( int prioridad )
   {
	
   	this.prioridad = prioridad;
   }


	public String getTitulo_aplicacion()
   {	
	
   	return titulo_aplicacion;
   }


	public void setTitulo_aplicacion( String titulo_aplicacion )
   {
	
   	this.titulo_aplicacion = titulo_aplicacion;
   }


	public ArrayList<TipoDocumento> getTipoDocumentoIMXDefault()
	{	
		ArrayList<TipoDocumento> permisos = new ArrayList<TipoDocumento>();
		
		
		
		TipoDocumento IMX_SIN_TIPO_DOCUMENTO = new TipoDocumento( this.getTitulo_aplicacion(), -1,	3, "IMX_SIN_TIPO_DOCUMENTO",	"SIN TIPO" );
		TipoDocumento EXTERNO = new TipoDocumento( this.getTitulo_aplicacion(), 1, 3,	"EXTERNO",	"Tipo de Documentos Externos" );
		TipoDocumento IMAX_FILE = new TipoDocumento( this.getTitulo_aplicacion(),2, 3, "IMAX_FILE",	"Tipo de Documento Imax_File (imagenes)" );
		
		permisos.add( IMX_SIN_TIPO_DOCUMENTO );
		permisos.add( EXTERNO );
		permisos.add( IMAX_FILE );
		
		return permisos;
	}
}
