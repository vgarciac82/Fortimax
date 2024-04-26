package com.syc.imaxfile;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento;

public class TipoDocumentoManager {

	private static final Logger log = Logger.getLogger(TipoDocumentoManager.class);
	public TipoDocumentoManager(){

	}

	//TODO: Post Migracion Simplicar estas llamadas entre metodos, solo esta rebuscado
	public imx_tipo_documento getTiposDocumento(String titulo_aplicacion, String nombreTipo) throws Exception
	{
		imx_tipo_documento tipoDocumento = null;
		
		try{
			tipoDocumento = selectTiposDocumento(titulo_aplicacion, nombreTipo);
		}

		catch(Exception E)
		{	
			log.error(E,E);
		}
		return tipoDocumento;
	}
	
	private static imx_tipo_documento selectTiposDocumento(String titulo_aplicacion, String nombreTipo) throws Exception
	{
		imx_tipo_documento tipodocumento=null;
		
		boolean byName = false;
        nombreTipo = StringUtils.trimToNull(nombreTipo); 
        byName = !(nombreTipo == null);
				
		HibernateManager hm = new HibernateManager();
		try 
		{
			
			@SuppressWarnings("unchecked")
			List<imx_tipo_documento> lista_td = 
					(List<imx_tipo_documento>) hm.createSQLQuery("SELECT * FROM imx_tipo_documento"
					+ " WHERE TITULO_APLICACION ='"+titulo_aplicacion+"'" 
					+ (byName ? " AND NOMBRE_TIPO_DOCTO='"+nombreTipo+"'" : "") 
					+ " ORDER BY NOMBRE_TIPO_DOCTO").addEntity(imx_tipo_documento.class).list();
			
			tipodocumento=lista_td.get(0);
			
		} 
		catch (Exception e) {
			log.error(e,e);
		} 
		finally {
			hm.close();
		}
		
		return tipodocumento;
		
	}
}