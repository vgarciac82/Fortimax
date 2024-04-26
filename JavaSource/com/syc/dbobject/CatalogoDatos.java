package com.syc.dbobject;

import org.apache.log4j.Logger;

import com.syc.fortimax.config.Config;


public class CatalogoDatos {
	
	private static final Logger log = Logger.getLogger(CatalogoDatos.class);
	
	public CatalogoDatos(){
	}
	
	public static String getNombreCampoIMAX(int type){
		String retVal = "";
		//log.debug("Obteniendo " + type);
		
		switch(type){
				
			case 3:
				retVal = "Entero (Smallint)";
				break;
			case 4:
				retVal = "Entero Largo (Int)";
				break;
			case 5:
				retVal = "Decimal";
				break;
			case 7:
				retVal = "Doble Precisi\u00F3n";
				break;
			case 8:
				retVal = "Fecha/Hora";
				break;
			case 10:
				retVal = "Texto (VarChar)";
				break;
			case 12:
				retVal = "Memo";
				break;
		/*	case 10:
				type=12;//mapeo de imax
				break;
			case 3:
				type=java.sql.Types.INTEGER;
				break;
				
			case 8: case java.sql.Types.TIME: case java.sql.Types.TIMESTAMP:
				type=java.sql.Types.DATE;
				break;
			case 5:
				type=java.sql.Types.NUMERIC;
				break;
			case 7:
				type=java.sql.Types.NUMERIC;
				break;
				*/
		}
		
		/*
		switch(type){
			case java.sql.Types.VARCHAR:
				retVal =  "Texto (VarChar)";
				break;
			case java.sql.Types.CHAR:
				retVal =  "Caracter (Char)";
				break;
			case java.sql.Types.INTEGER:
				retVal = "Entero (int)";
				break;
			case java.sql.Types.NUMERIC:
				retVal = "Doble";
				break;
			case java.sql.Types.DATE: 
				retVal = "Fecha";
				break;
			case java.sql.Types.DECIMAL:
				retVal = "Decimal";
				break; 
					
		}
		*/
		
		return retVal;

	}
	
	public boolean llevaLongitud(int type){
		boolean retVal = true;
		/* 
		switch(type){
			case 10:
				type=12;//mapeo de imax
				break;
			case 3:
				type=java.sql.Types.INTEGER;
				break;
			case 8:
				type=java.sql.Types.DATE;
				break;
		}
		*/
		if(Config.database.equals(Config.Database.ORACLE)){
			retVal =  llevaLongitudOracle(type);
		}
		else if(Config.database.equals(Config.Database.MYSQL))
		{
			retVal =  llevaLongitudOracle(type);
		}
		else if(Config.database.equals(Config.Database.MSSQL))
		{
			retVal =  llevaLongitudMSSQL(type);
		}
		
		return retVal;
		
		
	}
	
	private boolean llevaLongitudOracle(int type){
		boolean retVal = true;
		switch(type){
			case -4:
				retVal = false;
				break;
			case 7://java.sql.Types.DATE: 
				retVal = false;
				break;
			case 8://java.sql.Types.DATE: 
				retVal = false;
				break;
			case 12://java.sql.Types.LONGVARBINARY:
				retVal = false;
				break;
			case 3: case 4://java.sql.Types.INTEGER:
				retVal = false;
				break;
				
		
		}
		//log.debug("Longitud Oracle " + retVal);
		return retVal;
		
	}
	
	private boolean llevaLongitudMSSQL(int type){
		boolean retVal = true;
		switch(type){
			case -4:
				retVal = false;
				break;
			case 7://java.sql.Types.DATE: 
				retVal = false;
				break;
			case 8://java.sql.Types.DATE: 
				retVal = false;
				break;
			case 12://java.sql.Types.LONGVARBINARY:
				retVal = false;
				break;
			case 3: case 4://java.sql.Types.INTEGER:
				retVal = false;
				break;
				
		}
		log.debug("Longitud MSSQL" + retVal);
		return retVal;
	}
	
	
	
	public String getNombreTipoCampo(int type) throws Exception {
		String retVal = "";
/*
		switch(type){
			case 10:
				type=12;//mapeo de imax
				break;
			case 3: //decimal
				type=java.sql.Types.INTEGER;
				break;
		}
	*/	
		if(Config.database.equals(Config.Database.ORACLE)){
			retVal =  getNombreTipoCampoOracle(type);
		}
		else if(Config.database.equals(Config.Database.MYSQL)){
			retVal =  getNombreTipoCampoMySQL(type);
		}
		else if(Config.database.equals(Config.Database.MSSQL)){
			retVal =  getNombreTipoCampoMSSQL(type);
		}
		
		//log.debug("Nombre tipo campos" + retVal );
		return retVal;
		//TODO: agregar mas BD
	}
	
	private String getNombreTipoCampoMSSQL(int type) throws Exception {
		String retVal = "";
		/*
		switch(type){
			case 7:
				type =  java.sql.Types.NUMERIC;
				break;
			case 8: case java.sql.Types.TIME: case java.sql.Types.TIMESTAMP:
				type=java.sql.Types.DATE;
				break;
		}
		*/
		switch(type){
			case -4:
				retVal = "BINARY";
				break;
			case 1:
				retVal = "CHAR";
				break;
			case 2:
				retVal =  "INT"; //en ocasiones el generador anterior le ponia integer
				break;
			case 3: case 4:
				retVal =  "INT";
				break;
			case 5:
				retVal =  "INT";
				break;
			case 7:
				retVal = "FLOAT";
				break;
			case 8:
				retVal = "SMALLDATETIME";
				break;
			case 10:
				retVal = "VARCHAR";
				break;
			case 12:
				retVal = "TEXT";
				break;				
				
			default:
				log.trace("No esta mapeado" + type);
				throw new Exception("No esta mapeado el tipo de dato: "+type);
				
		}
		return retVal;
	}
	
	private String getNombreTipoCampoOracle(int type) throws Exception{
		String retVal = "";
		/*
		switch(type){
			case 7:
				type =  java.sql.Types.NUMERIC;
				break;
			case 8: case java.sql.Types.TIME: case java.sql.Types.TIMESTAMP:
				type=java.sql.Types.DATE;
				break;
		}
		*/
		switch(type){
			case -4:
				retVal = "LONG RAW";
				break;
			case 1:
				retVal = "CHAR";
				break;
			case 2:
				retVal =  "NUMBER"; //en ocasiones el generador anterior le ponia integer
				break;
			case 3: case 4:
				retVal =  "NUMBER";
				break;
			case 5:
				retVal =  "NUMBER";
				break;
			case 7:
				retVal = "FLOAT";
				break;
			case 8:
				retVal = "DATE";
				break;
			case 10:
				retVal = "VARCHAR2";
				break;
			case 12:
				retVal = "LONG";
				break;				
				
			default:
				throw new Exception("No esta mapeado el tipo de dato: "+type);
		}
		return retVal;
	}
	
	/**
	 * Devuelve el nombre del tipo de datos para el DMBS MySQL.
	 * @param type ID del tipo de datos
	 * @return Nombre del tipo de datos
	 * @throws Exception Si no encuentra el nombre del tipo de datos.
	 */
	private String getNombreTipoCampoMySQL(int type) throws Exception{
		String retVal = "";
		
		switch(type){
			case -4:
				retVal = "LONGBLOB";
				break;
			case 1:
				retVal = "CHAR";
				break;
			case 2:
				retVal =  "NUMERIC"; //en ocasiones el generador anterior le ponia integer
				break;
			case 3: case 4:
				retVal =  "NUMERIC";
				break;
			case 5:
				retVal =  "NUMERIC";
				break;
			case 7:
				retVal = "FLOAT";
				break;
			case 8:
				retVal = "DATE";
				break;
			case 10:
				retVal = "VARCHAR";
				break;
			case 12:
				retVal = "DOUBLE";
				break;				
				
			default:
				log.trace("No esta mapeado el campo" + type);
				throw new Exception("No esta mapeado el tipo de dato: "+type);
		}
		return retVal;
	}
}
