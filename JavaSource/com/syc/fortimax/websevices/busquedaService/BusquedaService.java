package com.syc.fortimax.websevices.busquedaService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.entities.Privilegio;
import com.syc.fortimax.hibernate.entities.imx_carpeta;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.managers.imx_carpeta_manager;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.fortimax.managers.PrivilegioManager;
import com.syc.fortimax.security.TokenManager;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.ExpedienteManager;
import com.syc.imaxfile.GetDatosNodo;

public class BusquedaService {

	private static final Logger log = Logger.getLogger(BusquedaService.class);

	/**
	 * Devuelve una lista con las propiedades de los documentos de un
	 * expediente.
	 *
	 * @param token
	 *            Token de autorizacion para el consumo de este servicio.
	 * @param gaveta
	 *            Titulo de aplicacion.
	 * @param primaryKeyFieldName
	 *            Nombre del campo de busqueda unico (llave primaria)
	 * @param primaryKeyFieldValue
	 *            Valor de busqueda.
	 * @param nombreElemento
	 *            Valor de busqueda.
	 * @param tipoElemento
	 *            Valor de busqueda.     
	 * @return Devueve una arreglo, donde cada entrada es una cadena concatenada
	 *         de valores atributo=valor|atributo=valor... Los atributos son
	 *         como se indica a continuacion:<br>
	 */
	
	public String[] getNodo(String token, String gaveta,
			String primaryKeyFieldName, String primaryKeyFieldValue,
			String nombreElemento, char tipoElemento) {

		String[] resultados = null; // Resultado
		String errorCode = null; // Codigo de error
		String errorMessage = null; // Mensaje de error
		
		ArrayList<Integer> ListIdGabinete = null; // Lista de expedientes que
													// concuerdan con la
													// busqueda
		int idGabinete = -1;
		try {

			/*
			 * Validacion de parametros obligatorios
			 */

			 token = StringUtils.stripToNull(token);
			if (token == null) {
				throw new FortimaxException("FMX-DOC-WS-1018",
						"Parametro Token Vacio");
			}
			
			String nombreUsuario = TokenManager.consumeToken(token);
			ArrayList<Privilegio> privilegios = PrivilegioManager.getPrivilegios(nombreUsuario, gaveta);
			if (privilegios.isEmpty()) {
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED",
						"El usuario no tiene privilegios sobre la gaveta");
			}

			gaveta = StringUtils.stripToNull(gaveta);
			if (gaveta == null) {
				throw new FortimaxException("FMX-DOC-WS-1001",
						"Parametro Titulo de Aplicacion vacio");
			}

			primaryKeyFieldName = StringUtils.stripToNull(primaryKeyFieldName);
			if (primaryKeyFieldName == null) {
				throw new FortimaxException("FMX-DOC-WS-1002",
						"Parametro primaryKeyFieldName vacio");
			}

			if (primaryKeyFieldValue == null) {
				throw new FortimaxException("FMX-DOC-WS-1003",
						"Parametro primaryKeyFieldValue vacio");
			}

			nombreElemento = StringUtils.stripToNull(nombreElemento);
			if (nombreElemento == null) {
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED",
						"Parametro nombreElemento vacio");
			}
			
			/* *************************************
			 * ******* SE BUSCA EL EXPEDIENTE ******
			 * ************************************
			 */
			ExpedienteManager em = new ExpedienteManager();
			ListIdGabinete = em.getIdExpedientes(gaveta,
					primaryKeyFieldName, primaryKeyFieldValue);

			if (ListIdGabinete.size() == 0)
				throw new FortimaxException("FMX-DOC-WS-1011",
						"No existe el Expediente con [" + primaryKeyFieldName
								+ "=" + primaryKeyFieldValue + "]");
			else if (ListIdGabinete.size() > 1)
				throw new FortimaxException("FMX-DOC-WS-1012",
						"Existe mas de un Expediente con [ "
								+ primaryKeyFieldName + "="
								+ primaryKeyFieldValue + "]");
			else
				idGabinete = ListIdGabinete.get(0);

			/* ******************************************
			 * SE LISTAN LOS DOCUMENTOS EN EL EXPEDIENTE
			 * *****************************************
			 */
			if(tipoElemento=='D') {
				DocumentoManager dm = new DocumentoManager();
				List<Documento> doctos = dm.selectDocumentosExpediente(
						gaveta, idGabinete);
				List<String> doctosFinal = new ArrayList<String>();

				/* ****************************************************************
				 * GENERA LA LISTA DE CONCATENACIONES DISCRIMINANDO, SI APLICA, LOS
				 * DOCUMENTOS VACIOS
				 * ****************************************************************
				 */
				if (doctos.size() > 0) {

					for (Iterator<Documento> i = doctos.iterator(); i.hasNext();) {
						Documento d = i.next();
						//if (!allDocs && d.getNumero_paginas() <= 0)
						//	continue;

						//doctosFinal.add(DocumentoManager
						//		.concatenateDocumentAttributes(d,
						//				Config.WS_ATTRIBUTE_SEPARATOR));
						if(d.getNombreDocumento().equals(nombreElemento))
							doctosFinal.add(d.getNodo()+" "+d.getTreePath());
					}

				}

				if (doctosFinal.size() > 0) {
					resultados = doctosFinal.toArray(new String[] {});
				}
			} else if (tipoElemento=='C') {
				log.trace("gaveta "+gaveta);
				log.trace("idGabinete "+idGabinete);
				List<Carpeta> carpetas = new CarpetaManager().getExpedientFolders(gaveta, idGabinete, true);
				List<String> carpetasFinal = new ArrayList<String>();
				for(Carpeta carpeta : carpetas) {
					if(carpeta.getNombreCarpeta().equals(nombreElemento))
							carpetasFinal.add(carpeta.getNodo()+" "+carpeta.getTreePath());
				}
				
				if (carpetasFinal.size() > 0) {
					resultados = carpetasFinal.toArray(new String[] {});
				}
			} else
				throw new FortimaxException("FMX-DOC-WS-UNDEFINED",
						"Se ingreso un tipoElemento inválido");		
		} catch (FortimaxException wse) {
			log.error(wse, wse);
			errorCode = wse.getCode();
			errorMessage = wse.getMessage();
		} catch (Exception e) {
			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.getMessage();
		}
		if (log.isDebugEnabled()) {
			log.debug(ArrayUtils.toString(resultados));
		}

		if (resultados != null && resultados.length > 0) {
			String retArray[] = new String[resultados.length + 2];
			for (int i = 0; i < resultados.length; i++) {
				retArray[i] = resultados[i];
			}
			retArray[retArray.length - 1] = errorCode;
			retArray[retArray.length - 2] = errorMessage;
			return retArray;
		} else {
			return new String[] { new String(""), errorCode, errorMessage };
		}
	}
	
	/**
	 * Devuelve una lista con las propiedades de los documentos de un
	 * expediente.
	 *
	 * @param token
	 *            Token de autorizacion para el consumo de este servicio.
	 * @param nodoPadre
	 *            Nodo de Expediente
	 * @param tipo
	 *            Valor de busqueda.
	 * @param nombre
	 *            Valor de busqueda.     
	 * @return Devueve una arreglo de nodos
	 */
	
	public List<String> getNodos(String token, String nodoPadre, String tipo, String nombre) {
		String errorCode = null; // Codigo de error
		String errorMessage = null; // Mensaje de error
		List<String> resultados = new ArrayList<String>(); // Lista de nodos que concuerdan con la busqueda
		try {
			token = StringUtils.stripToNull(token);
			if (token == null) {
				throw new FortimaxException("FMX-BUS-WS-1001", "Parametro token vacio");
			}
			
			GetDatosNodo gdn = new GetDatosNodo();
			nodoPadre = StringUtils.stripToNull(nodoPadre);
			if (nodoPadre==null) {
				throw new FortimaxException("FMX-BUS-WS-1002","Parametro nodoPadre vacio");
			} else {
				gdn = new GetDatosNodo(nodoPadre);
				if(gdn.getGaveta()==null) {
					throw new FortimaxException("FMX-BUS-WS-1003", "Parametro nodoPadre incorrecto");
				}
			}

			tipo = StringUtils.stripToNull(tipo);
			if (tipo!=null) {
				tipo = tipo.toUpperCase();
				if(!"C".equals(tipo)&&!"D".equals(tipo)) {
					throw new FortimaxException("FMX-BUS-WS-1004", "Parametro tipoNodo debe ser C, D o null");
				}
			}
			//TODO: Añadir los siguientes parametros: Descripcion
			
			nombre = StringUtils.stripToNull(nombre);
			
			//TODO: Consultas en USR_GRALES permiten buscar en expedientes de otros usuarios. 
			String nombreUsuario = TokenManager.consumeToken(token);
			List<Privilegio> privilegios = PrivilegioManager.getPrivilegios(nombreUsuario, gdn.getGaveta());
			if (privilegios.isEmpty()) {
				throw new FortimaxException("FMX-BUS-WS-1005", "El usuario no tiene privilegios sobre la gaveta");
			}
			
			//TODO: lo .list() de hibernate solo toman 65536 registros, limitando los resultados artificialmente.
			if("C".equals(tipo)||tipo==null) {
				imx_carpeta_manager imx_carpeta_manager = new imx_carpeta_manager();
				imx_carpeta_manager.select(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta(), nombre);
				for(imx_carpeta imx_carpeta: imx_carpeta_manager.list()) {
					resultados.add(imx_carpeta.getId().toString());
				}
			}
			if("D".equals(tipo)||tipo==null) {
				imx_documento_manager imx_documento_manager = new imx_documento_manager();
				imx_documento_manager.select(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta(), gdn.getIdDocumento()).selectNombreDocumento(nombre);
				imx_documento_manager.list();
				for(imx_documento imx_documento: imx_documento_manager.list()) {
					resultados.add(imx_documento.getId().toString());
				}
			}
		} catch (FortimaxException wse) {
			log.error(wse, wse);
			errorCode = wse.getCode();
			errorMessage = wse.getMessage();
		} catch (Exception e) {
			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.getMessage();
		}
		resultados.add(errorCode);
		resultados.add(errorMessage);
		return resultados;
	}
}
