package com.syc.dbobject;
import org.apache.log4j.Logger;

public class Campo {

private static final Logger log = Logger.getLogger(Campo.class);	
	private int tipoDato;
	private boolean canNull = false;
	private String nombreCampo;
	private double longitud;
	private String valPredefinido;
	private String nombreColumna = "";
	
	public String getNombreColumna()
   {

   	return nombreColumna;
   }
	public void setNombreColumna( String nombreColumna )
   {

   	this.nombreColumna = nombreColumna;
   }
	public void setNombreCampo( String nombreCampo )
   {

   	this.nombreCampo = nombreCampo;
   }
	public Campo( String nombreCampo, int tipoDato, String valPredefinido )
   {
	   super();
	   this.nombreCampo = nombreCampo;
	   this.tipoDato = tipoDato;
	   this.valPredefinido = valPredefinido;
	   System.out.println( "Valor Predefinido:" + valPredefinido );
	   log.debug("Valor Predefinido:" + this.valPredefinido);
   }
	public Campo( String nombreCampo, int tipoDato, String valPredefinido, String nombreColumna )
   {
	   super();
	   this.nombreCampo = nombreCampo;
	   this.tipoDato = tipoDato;
	   this.valPredefinido = valPredefinido;
	   this.nombreColumna = nombreColumna;
	   System.out.println( "Valor Predefinido:" + valPredefinido );
	   log.debug("Valor Predefinido:" + this.valPredefinido);
   }
	public Campo(String nombreCampo){
		this.nombreCampo = nombreCampo;	
	}
	
	public Campo(String nombreCampo, boolean canNull, int tipoDato, double longitud){
		this.nombreCampo = nombreCampo;
		this.tipoDato = tipoDato;
		this.canNull = canNull;
		this.longitud = longitud;
	}
	
	public void setValPredefinido(String valPredefinido){
		
		this.valPredefinido = valPredefinido;
	}
	
	public String getValPredefinido(){
		
		return valPredefinido;
	}
	
	public void setTipoDato(int tipoDato){
		
		this.tipoDato = tipoDato;
	}
	
	public void setCanNull(boolean canNull){
		
		this.canNull = canNull;
	}
	
	public void setLongitud(double longitud){
		
		this.longitud = longitud;
	}
	
	public int getTipoDato(){
		
		return tipoDato;
	}
	
	public boolean getCanNull(){
		
		return canNull;
	}
	
	public String getNombreCampo(){
		
		return nombreCampo;
	}
	
	public double getLongitud(){
		
		return longitud;
	}
}
