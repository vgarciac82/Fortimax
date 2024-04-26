package com.syc.fortimax.websevices;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.entities.Privilegio;
import com.syc.fortimax.hibernate.entities.imx_pagina;
import com.syc.fortimax.hibernate.managers.imx_pagina_manager;
import com.syc.fortimax.managers.PrivilegioManager;
import com.syc.fortimax.security.TokenManager;
import com.syc.fortimaxinterfaz.client.FortimaxViewer;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.zip.MakeZip;

public class MakeZipService {
	
	private static final Logger log = Logger.getLogger(MakeZipService.class);
	
	public byte[] descargaBytesPagina(String token, String nodo, int numero_pagina) {
		try{
			token = StringUtils.stripToNull(token);
			if (token == null)
				throw new FortimaxException("FMX-DOC-WS-1018","Parametro Token Vacio");
		
			nodo = StringUtils.stripToNull(nodo);
			if (nodo == null)
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED","Parametro nodo vacio");
		
			String nombreUsuario = TokenManager.consumeToken(token);
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			
			List<Privilegio> privilegios = PrivilegioManager.getPrivilegios(nombreUsuario, gdn.getGaveta());
			if (privilegios.isEmpty())
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED","El usuario no tiene privilegios sobre la gaveta");
		
			
			imx_pagina_manager imx_pagina_manager = new imx_pagina_manager().select(gdn);
			imx_pagina_manager.setFirstResult(numero_pagina);
			imx_pagina_manager.setMaxResults(1);
			List<imx_pagina> imx_paginas = imx_pagina_manager.list();
			if(imx_paginas.isEmpty())
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED","No se encontro la página "+numero_pagina);
			URL url = new URL("file://"+imx_paginas.get(0).getAbsolutePath());
			return IOUtils.toByteArray(new DataHandler(url).getInputStream());
		} catch (FortimaxException fe) {
			log.error(fe, fe);
			return null;
		} catch (Exception e) {
			log.error(e, e);
			return null;
		}
	}
	
	public DataHandler descargaBytesZIP(String token, String nodo, Boolean convertToPDF) {
		try{
			token = StringUtils.stripToNull(token);
			if (token == null)
				throw new FortimaxException("FMX-DOC-WS-1018","Parametro Token Vacio");
		
			nodo = StringUtils.stripToNull(nodo);
			if (nodo == null)
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED","Parametro nodo vacio");
			
			if (convertToPDF == null) {
				throw new FortimaxException("FMX-LNK-WS-1003",
						"Parametro convertToPDF vacio");
			}
		
			String nombreUsuario = TokenManager.consumeToken(token);
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			ArrayList<Privilegio> privilegios = PrivilegioManager.getPrivilegios(nombreUsuario, gdn.getGaveta());
		 	
			if (privilegios.isEmpty())
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED","El usuario no tiene privilegios sobre la gaveta");
		
			byte[] bytes = MakeZip.getZip(nombreUsuario, nodo, convertToPDF);
			ByteArrayDataSource dataSource = new ByteArrayDataSource(bytes,"application/zip");
			DataHandler fileDataHandler = new DataHandler(dataSource);
			return fileDataHandler;
		//} catch (FortimaxException e) {
		//	throw new AxisFault(e.getMessage(),e.getCode());
		} catch (Exception e) {
			//throw e;
			return null;
		} finally {
			
		}
	}
	
	public Object[] GeneraZip(String user, String pass, Boolean encrypted,String gaveta, String expediente, String documento)
	{	
		String url = null;
		String errorCode = null;
		String errorMessage = null;
		int id_gabinete=-1;
		String Matricula_Buscada=null;	
		
		try {

			Usuario u =new Usuario(user);
			user = StringUtils.stripToNull(user);
			if (user == null) {
				throw new FortimaxException("FMX-LNK-WS-1001",
						"Parametro Usuario vacio");
			}
			pass = StringUtils.stripToNull(pass);
			if (pass == null) {
				throw new FortimaxException("FMX-LNK-WS-1002",
						"Parametro Password vacio");
			}
			if (encrypted == null) {
				throw new FortimaxException("FMX-LNK-WS-1003",
						"Parametro Encrypted vacio");
			}
			
			gaveta = StringUtils.stripToNull(gaveta);
			if (gaveta == null) {
				throw new FortimaxException("FMX-LNK-WS-1004",
						"Parametro Gaveta vacio");
			}
			expediente = StringUtils.stripToNull(expediente);
			if (expediente == null) {
				throw new FortimaxException("FMX-LNK-WS-1005",
						"Parametro Expediente vacio");
			}
			documento = StringUtils.stripToNull(documento);
			if (documento == null) {
				documento="";
//				throw new FortimaxException("FMX-LNK-WS-1006",
//						"Parametro Documento vacio");
			}
			
			
			
			//Inicializa la estructura de Fortimax
			FortimaxViewer fvv = new FortimaxViewer(user, pass, encrypted,
					gaveta);
			
			
			id_gabinete=fvv.obtieneIdGabinete(expediente);
			if (id_gabinete!=-1 )
			{
				ArbolManager am = new ArbolManager(gaveta,id_gabinete);
				Matricula_Buscada= am.ObtenMatriculaDocumento(user, gaveta, id_gabinete, documento);
				
				if(Matricula_Buscada!=null)
				{
					url=fvv.URLXZIP(Matricula_Buscada, documento ,user);
				}
				else
				{
					throw new FortimaxException("FMX-LNK-WS-1006",
					"La informaci\u00F3n no concuerda con un Documento existente");
				}
			}
			else
			{
				throw new FortimaxException("FMX-LNK-WS-1006",
				"La informaci\u00F3n no concuerda con un Documento existente");
			}
			
			
			
			
	} 
	catch (FortimaxException wse) {	log.error(wse,wse);

		log.error(wse,wse);
		errorCode = wse.getCode();
		errorMessage = wse.getMessage();
		
	}
	catch (Exception e) {	log.error(e,e);

		log.error(e,e);
		errorCode = FortimaxException.codeUnknowed;
		errorMessage = e.getMessage();
	}

		
		
		
		
		//Regresa el objeto 
		return new Object[] { url, errorCode, errorMessage };
		
	}

}
