package com.syc.fortimax.hibernate.entities;

import java.util.Date;


public class imx_pagina_index {
	private int ID;
	private String TITULO_APLICACION;
	private int ID_CARPETA_PADRE;
	private int ID_DOCUMENTO;
	private int ID_GABINETE;
	private int NUMERO_PAGINA;
	private String DOCUMENTO_ORIGINAL;
	private String PROCESADO;
	private String TIPO_DOCUMENTO;
	private String EXTENSION;
	private Date FHMODIFICACION;
	//bandera de indexacion
	public imx_pagina_index()
	{
		
		
	}


	public imx_pagina_index(String TITULO_APLICACION, int ID_CARPETA_PADRE , 
			int ID_DOCUMENTO,int ID_GABINETE,int NUMERO_PAGINA, 
			String DOCUMENTO_ORIGINAL,String PROCESADO,
			String TIPO_DOCUMENTO, String EXTENSION, Date FHMODIFICACION) {
		super();
		
		this.TITULO_APLICACION = TITULO_APLICACION;
		this.ID_CARPETA_PADRE = ID_CARPETA_PADRE;
		this.ID_DOCUMENTO = ID_DOCUMENTO;
		this.ID_GABINETE=ID_GABINETE;
		this.NUMERO_PAGINA=NUMERO_PAGINA;
		this.DOCUMENTO_ORIGINAL=DOCUMENTO_ORIGINAL;
		this.PROCESADO=PROCESADO;
		this.TIPO_DOCUMENTO=TIPO_DOCUMENTO;
		this.EXTENSION=EXTENSION;
		this.FHMODIFICACION=FHMODIFICACION;
	}


	public int getID() {
		return ID;
	}


	public void setID(int id) {
		this.ID = id;
	}


	public String getTITULO_APLICACION() {
		return TITULO_APLICACION;
	}


	public void setTITULO_APLICACION(String TITULO_APLICACION) {
		this.TITULO_APLICACION = TITULO_APLICACION;
	}


	public int getID_CARPETA_PADRE() {
		return ID_CARPETA_PADRE;
	}


	public void setID_CARPETA_PADRE(int iD_CARPETA_PADRE) {
		this.ID_CARPETA_PADRE = iD_CARPETA_PADRE;
	}


	public int getID_DOCUMENTO() {
		return ID_DOCUMENTO;
	}


	public void setID_DOCUMENTO(int iD_DOCUMENTO) {
		this.ID_DOCUMENTO = iD_DOCUMENTO;
	}


	public int getID_GABINETE() {
		return ID_GABINETE;
	}


	public void setID_GABINETE(int iD_GABINETE) {
		this.ID_GABINETE = iD_GABINETE;
	}


	public int getNUMERO_PAGINA() {
		return NUMERO_PAGINA;
	}


	public void setNUMERO_PAGINA(int nUMERO_PAGINA) {
		this.NUMERO_PAGINA = nUMERO_PAGINA;
	}


	public String getDOCUMENTO_ORIGINAL() {
		return DOCUMENTO_ORIGINAL;
	}


	public void setDOCUMENTO_ORIGINAL(String DOCUMENTO_ORIGINAL) {
		this.DOCUMENTO_ORIGINAL = DOCUMENTO_ORIGINAL;
	}


	public String getPROCESADO() {
		return PROCESADO;
	}


	public void setPROCESADO(String PROCESADO) {
		this.PROCESADO = PROCESADO;
	}

	public String getTIPO_DOCUMENTO(){
		return TIPO_DOCUMENTO;
	}
	
	public void setTIPO_DOCUMENTO(String TIPO_DOCUMENTO){
		this.TIPO_DOCUMENTO=TIPO_DOCUMENTO;
	}
	
	public String getEXTENSION() {
		return EXTENSION;
	}

	public void setEXTENSION(String EXTENSION) {
		this.EXTENSION = EXTENSION;
	}

	public Date getFHMODIFICACION() {
		return FHMODIFICACION;
	}

	public void setFHMODIFICACION(Date FHMODIFICACION) {
		this.FHMODIFICACION = FHMODIFICACION;
	}
}
