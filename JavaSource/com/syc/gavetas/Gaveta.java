package com.syc.gavetas;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.syc.dbobject.Indice;
public class Gaveta {
	
	private static final Logger log = Logger.getLogger(Gaveta.class);
	private String nombre;
	private String descripcion;
	private ArrayList<GavetaCampo> campos = new ArrayList<GavetaCampo>();
	private ArrayList<Indice> indices = new ArrayList<Indice>();
	
	public Gaveta(String nombre){
		log.debug("Creando Objeto gaveta: "+ nombre);
		this.nombre = nombre;
	}
	
	public Gaveta(String nombre, String descripcion){
		log.debug("Creando Objeto gaveta: "+ nombre);		
		this.nombre = nombre;
		this.descripcion = descripcion;
	}
	
	public void setNombre( String nombre )
   {
	
   	this.nombre = nombre;
   }

	public void addIndice(Indice indice){
		
		indices.add(indice);
	}
	
	public ArrayList<Indice> getIndices(){
		
		return indices;
	}
	
	public Indice getIndice(int idx){
		
		return indices.get(idx);
	}
	
	public String getNombre(){
		
		return nombre;
	}

	public String getDescripcion(){
		
		return descripcion;
	}	
	
	public void addCampo(GavetaCampo campo){
		
		campos.add(campo);
	}
	
	public GavetaCampo get(int idx){
		
		return (GavetaCampo)campos.get(idx);
	}
	
	public ArrayList<GavetaCampo> getCampos(){
		
		return campos;
	}
	
	/**
	 * Busca si ya existe un campo definido con el nombre <code>campoNombre</code>
	 * en esta gaveta. 
	 * 
	 * @param campoNombre Nombre del campo a buscar
	 * @return <code>true</code> Si el campo existe. <br><code>false en caso contrario</code>
	 * 
	 */
	
	public int contieneCampo( String campoNombre )
	{
		
		int indice = -1;
		int counter = -1;
		for( Iterator<GavetaCampo> it = this.getCampos().iterator(); it.hasNext(); )
		{
			counter++;
			if( it.next().getNombre_campo().trim().equals( campoNombre.trim() ) )
			{
				indice = counter;
				break;
			}
		}
		
		return indice;
		
	}
	
	public String[] loadFromRequest( HttpServletRequest request, int total, boolean actualizar )
	{
		
		log.debug("Verificando request");
		
		Enumeration<?> e = request.getParameterNames();
		while(e.hasMoreElements()){
			String paramName = (String)e.nextElement();
			String valueParam = request.getParameter(paramName);
			log.debug("Parametro[" + paramName + "]"  + "Valor[" + valueParam + "]");
		}
		
		
		
		String prefijoParametro = "campos_creados[";
		//String nombresAnteriores[] = new String[(total-1)*2];
		String nombresAnteriores[] = new String[total-1];
		
		
		for( int i = 1; i < total; i++ )
		{
			GavetaCampo campo = new GavetaCampo();
		
			
			for( int j = 0; j <= 12; j++ )
			{
				String nombreParametro = prefijoParametro + i + "," + j + "]";
				
				String value = request.getParameter( nombreParametro );

				switch( j )
				{
					case 0:
						
						campo.setNombre_campo( value.trim() );
					break;
					case 1:
						
						campo.setNombre_desplegar( value.trim() );
					break;
					case 2:
						
						campo.setTamano( Double.parseDouble( value.trim() ) );
					break;
					case 3:
						
						campo.setVal_predefinido( value.trim() );
					break;
					case 12:
						
						campo.setTipo_dato( Integer.parseInt( value ) );
					break;
					case 11:
						
						campo.setIndice(   Integer.parseInt( value ) );
					break;
					case 6:
						
						campo.setRequerido( value.trim() );
					break;
					case 7:
						
						campo.setEditable( value.trim() );
					break;
					case 8:
						
						campo.setLista( value.trim() );
					break;
					case 9:
						
						//nombresAnteriores[(i*2)+1] = value;
						//nombresAnteriores[((i-1)*2)+1] = value;
					break;
					case 10:
						
					     //nombresAnteriores[(i-1)*2] = value;
						nombresAnteriores[i-1] = value;
					break;
				}
			}
			
			//Agregamos la lista
			if("S".equalsIgnoreCase(campo.getLista())){
				campo.setNombreCatalogo(request.getParameter(prefijoParametro + i + ",13]"));
			}
			
			this.addCampo( campo );
		}
		
		return nombresAnteriores;
	}
	
	public int getIDTipoDatoFromName( String nombreDato )
	{

		int idTipoDato = -1;

		if( nombreDato.indexOf( "Entero" ) != -1 )
			idTipoDato = 3;
		else if( nombreDato.indexOf( "Doble" ) != -1 )
			idTipoDato = 7;
		else if( nombreDato.indexOf( "Fecha" ) != -1 )
			idTipoDato = 8;
		else if( nombreDato.indexOf( "Texto" ) != -1 )
			idTipoDato = 10;
		
		return idTipoDato;
	}
	
	public void setDescripcion( String descripcion )
   {
	
   	this.descripcion = descripcion;
   }

	public int getIDIndiceFromName( String nombreIndice )
	{

		int idIndice = -1;

		if( nombreIndice.indexOf( "Ninguno" ) != -1 )
			idIndice = 0;
		else if( nombreIndice.indexOf( "No \u00DAnico" ) != -1 )
			idIndice = 1;
		else if( nombreIndice.indexOf( "\u00DAnico" ) != -1 )
			idIndice = 2;
				
		return idIndice;
	}
	
	/**
	 * Devuelve el numero de campos definidos en esta gaveta.
	 * 
	 * @return Numero de campos de la gaveta.
	 */
	public int getNumeroDeCampos()
	{
		
		return ( getCampos() != null ? getCampos().size(): 0 );
	}
	
}