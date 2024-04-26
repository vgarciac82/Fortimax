package com.syc.fortimax.websevices.documentoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento;
import com.syc.fortimax.security.TokenManager;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.Descripcion;
import com.syc.imaxfile.DescripcionManager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.ExpedienteCampo;
import com.syc.imaxfile.ExpedienteManager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.imaxfile.PaginaManager;
import com.syc.imaxfile.TipoDocumentoManager;
import com.syc.utils.PathManager;
import com.syc.utils.Utils;

public class DocumentoService {

	private static final Logger log = Logger.getLogger(DocumentoService.class);

	/**
	 * Devuelve una lista con las propiedades de los documentos de un
	 * expediente.
	 *
	 * @param token
	 *            Token de autorizacion para el consumo de este servicio.
	 * @param application
	 *            Titulo de aplicacion.
	 * @param primaryKeyFieldName
	 *            Nombre del campo de busqueda unico (llave primaria)
	 * @param primaryKeyFieldValue
	 *            Valor de busqueda.
	 * @param allDocs
	 *            <code>true</code> regresara la totalidad de documentos en el
	 *            expediente. En caso contrario solo devolvera los documentos
	 *            con al menos una pagina.
	 * @return Devueve una arreglo, donde cada entrada es una cadena concatenada
	 *         de valores atributo=valor|atributo=valor... Los atributos son
	 *         como se indica a continuacion:<br>
	 * <br>
	 *         <table summary="" border="1">
	 *         <thead>
	 *         <tr>
	 *         <th>
	 *         Campo</th>
	 *         <th>
	 *         Contenido</th>
	 *         </tr>
	 *         </thead> <tbody>
	 *         <tr>
	 *         <td>
	 *         <b>IDENTIFICADOR_FORTIMAX</b></td>
	 *         <td>
	 *         Clave que identifica de manera unica al documento.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <b>NOMBRE_DOCUMENTO</b></td>
	 *         <td>
	 *         Nombre del documento.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <b>NOMBRE_USUARIO</b></td>
	 *         <td>
	 *         Usuario que creo el documento.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <b>FH_CREACION</b></td>
	 *         <td>
	 *         Fecha en que se creo el documento.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <b>FH_MODIFICACION</b></td>
	 *         <td>
	 *         Fecha ultima en que se realizo una actualizacion al documento.
	 *         Como actualizacion se entiende cualquier operacion que altere el
	 *         contenido del documento (Agregar/Eliminar paginas)</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <strong>NUMERO_PAGINAS</strong></td>
	 *         <td>
	 *         Numero de paginas que contiene el documento.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <strong> TAMANO_BYTES </strong></td>
	 *         <td>
	 *         Tama&ntilde;o del archivo en Bytes.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <strong> COMPARTIR</strong></td>
	 *         <td>
	 *         Bandera que indica si el documento esta siendo compartido (S|N).</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <strong>TOKEN_COMPARTIR </strong></td>
	 *         <td>
	 *         Token generado para el acceso al documento compartido via remota.
	 *         </td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <strong>FECHA_COMPARTIDO</strong></td>
	 *         <td>
	 *         Fecha en la que inicio a compartir el documento.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>
	 *         <strong>DIAS_PERMITIDOS</strong></td>
	 *         <td>
	 *         Dias que se compartira el documento antes de su expiracion.</td>
	 *         </tr>
	 *
	 *         </tbody>
	 *         </table>
	 */
	public String[] getDocumentsCompleteInfo(String token, String application,
			String primaryKeyFieldName, String primaryKeyFieldValue,
			boolean allDocs) {

		String[] docs = null; // Resultado
		String errorCode = null; // Codigo de error
		String errorMessage = null; // Mensaje de error
		ArrayList<Integer> ListIdGabinete = null; // Lista de expedientes que
													// concuerdan con la
													// busqueda
		int idGabinete = -1;
		try {

			/*
			 * Validacion de parametros obligatorios Provisionalmente el Token
			 * queda sin uso,
			 */
			@SuppressWarnings("unused")
			String usuario = TokenManager.consumeToken(token);

			application = StringUtils.stripToNull(application);
			if (application == null) {
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

			ExpedienteManager em = new ExpedienteManager();
			ListIdGabinete = em.getIdExpedientes(application,primaryKeyFieldName, primaryKeyFieldValue);

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
			DocumentoManager dm = new DocumentoManager();
			List<Documento> doctos = dm.selectDocumentosExpediente(
					application, idGabinete);
			List<String> doctosFinal = new ArrayList<String>();

			/* ****************************************************************
			 * GENERA LA LISTA DE CONCATENACIONES DISCRIMINANDO, SI APLICA, LOS
			 * DOCUMENTOS VACIOS
			 * ****************************************************************
			 */
			if (doctos.size() > 0) {

				for (Iterator<Documento> i = doctos.iterator(); i.hasNext();) {
					Documento d = i.next();
					if (!allDocs && d.getNumero_paginas() <= 0)
						continue;

					doctosFinal.add(DocumentoManager
							.concatenateDocumentAttributes(d,
									Config.WS_ATTRIBUTE_SEPARATOR));
				}

			}

			if (doctosFinal.size() > 0) {
				docs = doctosFinal.toArray(new String[] {});
			}

		} catch (FortimaxException wse) {
			log.error(wse, wse);
			errorCode = wse.getCode();
			errorMessage = wse.getMessage();
		} catch (Exception e) {
			log.error(e, e);

			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.getMessage();
		}
		if (log.isDebugEnabled()) {
			log.debug(ArrayUtils.toString(docs));
		}

		if (docs != null && docs.length > 0) {
			String retArray[] = new String[docs.length + 2];
			for (int i = 0; i < docs.length; i++) {
				retArray[i] = docs[i];
			}
			retArray[retArray.length - 1] = errorCode;
			retArray[retArray.length - 2] = errorMessage;
			return retArray;
		} else {
			return new String[] { new String(""), errorCode, errorMessage };
		}
	}

	public String[] generateDocument(String access, String titulo_aplicacion,
			String fieldName, String fieldValue, String fileType,
			String folderName, String documentName) {
		boolean exito = false;
		titulo_aplicacion = titulo_aplicacion.toUpperCase();
		String errorCode = null;
		String errorMessage = null;
		ArrayList<Integer> ListIdGabinete = null;
		int idGabinete = -1;
		try {
			log.info("Intentando crear documento " + documentName
					+ " en la carpeta " + folderName + " de tipo " + fileType
					+ "con las siguientes credenciales: User " + access
					+ " aplicacion " + titulo_aplicacion
					+ " Campo de busqueda " + fieldName + " valor buscado "
					+ fieldValue);
		} catch (Exception e) {
			log.warn("Intentando escribir log de metodo generateDocument");
		}

		try {
			// ***************************************
			// ******SE BUSCA EL EXPEDIENTE***********
			// ***************************************
			ExpedienteManager em = new ExpedienteManager();
			ListIdGabinete = em.getIdExpedientes(titulo_aplicacion,fieldName, fieldValue);

			if (ListIdGabinete.size() == 0)
				throw new FortimaxException("FMX-DOC-WS-1011",
						"No existe el Expediente con [" + fieldName + "="
								+ fieldValue + "]");
			else if (ListIdGabinete.size() > 1)
				throw new FortimaxException("FMX-DOC-WS-1012",
						"Existe mas de un Expediente con [ " + fieldName + "="
								+ fieldValue + "]");
			else
				idGabinete = ListIdGabinete.get(0);

			// ***************************************
			// *********SE BUSCA LA CARPETA***********
			// ***************************************
			CarpetaManager cm = new CarpetaManager();
			Object[] orgCarpeta = cm.getOrgCarpetaOnName(titulo_aplicacion,
					idGabinete, folderName);

			int idCP = Integer.parseInt(orgCarpeta[0].toString());// idCarpetaPadre
			int idCH = Integer.parseInt(orgCarpeta[2].toString());// idCarpetaPadre

			if (idCP == -1 || idCH == -1)
				throw new FortimaxException("FMX-DOC-WS-1013",
						"No existe la carpeta[" + folderName
								+ "] para colocar el documento");

			// ***************************************
			// ***SE OBTIENE PRIORIDAD Y ID TIPO DOC**
			// ***************************************
			imx_tipo_documento entidad_td = null;

			TipoDocumentoManager tdm = new TipoDocumentoManager();
			entidad_td = tdm.getTiposDocumento(titulo_aplicacion, fileType);
			// regresa una lista de arreglo de objetos en este orden
			// id_tipo_docto, prioridad, nombre_tipo_docto

			if (entidad_td == null)
				throw new FortimaxException("FMX-DOC-WS-1015",
						"Se produjo un error al obtener el tipo de documento["
								+ fileType + "]");


			// ***************************************
			// ***Valida que NO exista el documento **
			// ***************************************
			DocumentoManager dm = new DocumentoManager();

			Documento d = new Documento(titulo_aplicacion, idGabinete, idCH);
			d.setNombreDocumento(documentName);
			d.setNombreUsuario(access);
			d.setPrioridad(entidad_td.getPrioridad());
			d.setIdTipoDocto(entidad_td.getId().getIdTipoDocto());
			d.setNombreTipoDocto(entidad_td.getNombreTipoDocto());
			d.setFechaCreacion(new java.sql.Date(new java.util.Date().getTime()));
			d.setFechaModificacion(new java.sql.Date(new java.util.Date().getTime()));
			d.setNumeroAccesos(0);
			d.setNumeroPaginas(0);
			d.setEstadoDocumento("V");
			Documento documento = dm.findDocumentsByName(titulo_aplicacion, idGabinete, idCH, documentName);
			if (documento != null) {
				exito = false;
				errorCode = "FMX-DOC-WS-1050";
				errorMessage = "Ya existe el documento que desea crear.";
			} else {

				// ***************************************
				// ******SE INSERTA EL DOCUMENTO**********
				// ***************************************
				Boolean idNewDocto = dm.insertDocumento(d);

				if (!idNewDocto)
					throw new FortimaxException("FMX-DOC-WS-1017",
							"No se pudo crear el documento[" + documentName
									+ "] correctamente en la carpeta["
									+ folderName + "], aplicacion["
									+ titulo_aplicacion + "]");

				exito = true;
				log.info("El documento " + d.getNombreDocumento()
						+ " se creo exitosamente con el ID " + idNewDocto
						+ " en el gabinete " + d.getIdGabinete());
			}
		} catch (FortimaxException FE) {
			log.error(FE, FE);
			errorCode = FE.getCode();
			errorMessage = FE.getMessage();
		} catch (Exception E) {
			log.error(E, E);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = E.getMessage();
		} finally {

		}

		return new String[] { String.valueOf(exito),
				errorCode == null ? new String("") : errorCode,
				errorMessage == null ? new String("") : errorMessage };
	}

	public String[] getDocuments(String titulo_aplicacion, String campo,
			String valor) {

		String[] docs = null;
		String errorCode = null;
		String errorMessage = null;

		try {
			titulo_aplicacion = StringUtils.stripToNull(titulo_aplicacion);
			if (titulo_aplicacion == null) {
				throw new FortimaxException("FMX-DOC-WS-1001",
						"Parametro Titulo de Aplicacion vacio");
			}
			campo = StringUtils.stripToNull(campo);
			if (campo == null) {
				throw new FortimaxException("FMX-DOC-WS-1002",
						"Parametro Campo vacio");
			}
			if (valor == null) {
				throw new FortimaxException("FMX-DOC-WS-1003",
						"Parametro Valor vacio");
			}

			ArrayList<String> aux = DocumentoManager.findDocuments(
					titulo_aplicacion, campo, valor);

			if (aux.size() > 0) {
				docs = aux.toArray(new String[] {});
			}

		} catch (FortimaxException wse) {
			log.error(wse, wse);

			log.error(wse, wse);
			errorCode = wse.getCode();
			errorMessage = wse.getMessage();
		} catch (Exception e) {
			log.error(e, e);

			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.getMessage();
		}
		if (log.isDebugEnabled()) {
			log.debug(ArrayUtils.toString(docs));
		}

		if (docs != null && docs.length > 0) {
			String retArray[] = new String[docs.length + 2];
			for (int i = 0; i < docs.length; i++) {
				retArray[i] = docs[i];
			}
			retArray[retArray.length - 1] = errorCode;
			retArray[retArray.length - 2] = errorMessage;
			return retArray;
		} else {
			return new String[] { new String(""), errorCode, errorMessage };
		}

	}


	public String[] uploadFileNodo(String fileName, DataHandler fileData,
			String gaveta, String[] expediente, String carpeta) {
		
			boolean ok = false;
			String nodo = "";
			String errorCode = null;
			String errorMessage = null;
			DocumentoManager dm = null;
			PaginaManager pm = null;

			
			try {	
				dm = new DocumentoManager();
				pm = new PaginaManager();
				fileName = StringUtils.trimToNull(fileName);
				if (fileName == null) {
					throw new FortimaxException("FMX-DOC-WS-1004",
							"Parametro Nombre de Archivo Vacio");
				}
				if (fileData == null) {
					throw new FortimaxException("FMX-DOC-WS-1005",
							"Parametro Datos de Archivo Vacio");
				}
				gaveta = StringUtils.trimToNull(gaveta);
				if (gaveta == null) {
					throw new FortimaxException("FMX-DOC-WS-1006",
							"Parametro Gaveta Vacio");
				}
				if (ArrayUtils.isEmpty(expediente)) {
					throw new FortimaxException("FMX-DOC-WS-1007",
							"Parametro Expediente Vacio");
				}
				carpeta = StringUtils.trimToNull(carpeta);
				if (carpeta == null) {
					throw new FortimaxException("FMX-DOC-WS-1008",
							"Parametro Carpeta Vacio");
				}
				
				//creamos el folder si no existe.
				File folder = new File(Config.workPath);
				folder.mkdirs();
				
				//FIX: Agregamos String aleatorio para evitar error en concurrencia:
				
				String nombreSinExtension = "";
				String extension="";
				int idx = fileName.lastIndexOf('.');
				if (idx > 0) {
					nombreSinExtension = fileName.substring(0, idx);
					extension = fileName.substring(idx+1);
				}	
				
				String randomSubName = generarRandomString();
				
				fileName = nombreSinExtension + generarRandomString() + "." + extension;
				
				
				File file = new File(Config.workPath, fileName);
				writeFile(file, fileData);

				// TODO revisar esto
				Descripcion[] desc = new DescripcionManager().selectDescripcion(gaveta);

				ExpedienteCampo[] exp = new ExpedienteCampo[desc.length];

				for (int i = 0; i < exp.length && i < expediente.length; i++) {
					exp[i] = new ExpedienteCampo();
					exp[i].setColumnName(desc[i].getNombreCampo());
					exp[i].setPrimaryKey(desc[i].getRequeridoBoolean());
					exp[i].setValue(expediente[i]);
					exp[i].setIdTipoDato(desc[i].getIdTipoDatos());

				}
				// Hasta aki	

				ArrayList<Integer> id_expediente = ExpedienteManager.getIdExpediente(gaveta, exp);

				if (id_expediente == null || id_expediente.isEmpty()) {
					int id = ExpedienteManager.createExpediente(gaveta, exp);
					if (id == -1) {
						throw new FortimaxException("FMX-DOC-WS-1009",
								"No se pudo crear el Expediente");
					} else {
						id_expediente.add(id);
					}
				}

				if (id_expediente.size() > 2) {
					log.warn("Hay "
							+ id_expediente.size()
							+ " expedientes con las caracteristicas enviadas, solo se procesara el primero");
				}

				if (carpeta.contains("?")) {
					carpeta = StringUtils.substringBefore(carpeta, "?");
				}
				carpeta = carpeta.replace(Config.separator, '/');
				log.trace("Carpeta: "+carpeta);
				PathManager pathManager = new PathManager(new GetDatosNodo(gaveta+"_G"+id_expediente.get(0)+"C0"));
				pathManager.setUsuario("ws");
				List<Integer> id_carpeta = new ArrayList<Integer>();
				id_carpeta.add(pathManager.creaCarpeta(carpeta).getIdCarpeta());
				
				//ArrayList<Integer> id_carpeta = CarpetaManager.getIdCarpeta(gaveta,id_expediente.get(0), carpeta);

				if (id_carpeta == null || id_carpeta.isEmpty()) {
					int id = CarpetaManager.createCarpeta(gaveta, id_expediente.get(0),
							carpeta);
					if (id == -1) {
						throw new FortimaxException("FMX-DOC-WS-1010",
								"No se pudo crear la Carpeta");
					} else {
						id_carpeta.add(id);
					}
				}

				if (id_carpeta.size() > 2) {
					log.warn("Hay "
							+ id_carpeta.size()
							+ " carpetas con las caracteristicas enviadas, solo se procesara el primero");
				}
				
				if(!fileName.contains("."))
					throw new FortimaxException("FMX-DOC-WS-1019","Debe especificarse una extensión.");
				
//				String nombreSinExtension = StringUtils.substringBeforeLast(fileName, ".");
				
				Documento doc = new Documento(gaveta, id_expediente.get(0),id_carpeta.get(0));

				//TODO: Verificar este procedimiento si es realmente necesario....
				int id_doc=-1;
				imx_documento imxd = dm.getDocumentbyName(gaveta, id_expediente.get(0), id_carpeta.get(0), nombreSinExtension);
				if(imxd!=null)
					id_doc = imxd.getId().getIdDocumento();
				log.trace("El nuevo documento tendrá un id_doc = "+id_doc);
				doc.setIdDocumento(id_doc);

				String tipoPagina = null;
				
				int idTipoDocto = -1;
				
				if (Utils.isImaxFileFromName(fileName)) {
					tipoPagina = "I";
					idTipoDocto = 2;
				} else {
					tipoPagina = "A";
					idTipoDocto = 1;
				}
				
				int idDocumento = doc.getIdDocumento();
				if (idDocumento == -1) {
					// TODO revisar esto del tipo de docto
					doc.setIdTipoDocto(idTipoDocto);
					doc.setNombreDocumento(nombreSinExtension);
					doc.setNombreUsuario("ws");
					dm.insertDocumento(doc);
				} else {
					if(doc.getIdTipoDocto() == -1){
						doc.setIdTipoDocto(idTipoDocto);
						doc.setNombreDocumento(nombreSinExtension);
						doc.setNombreUsuario("ws");
						dm.updateDocumento(doc);
					}
				}
				
				imxd = dm.getDocumentbyName(gaveta, id_expediente.get(0), id_carpeta.get(0), nombreSinExtension);
				id_doc = imxd.getId().getIdDocumento();
				
				Documento documento = dm.selectDocumento(doc.getTituloAplicacion(), doc.getIdGabinete(), doc.getIdCarpetaPadre(), id_doc);
				
				if (documento.getNumeroPaginas()>0&&tipoPagina.equals("A")){
					throw new FortimaxException("FMX-DOC-WS-1020","Solo los documentos IMAX_FILE pueden tener múltiples páginas y que sean del tipo imágenes.");	
				}
				
				log.debug("Tipo de Pagina: "+tipoPagina);
				int id_pagina = pm.insertPagina(gaveta, id_expediente.get(0), id_carpeta.get(0),
						doc.getIdDocumento(), tipoPagina, file);
				if (id_pagina != -1) {
					ok = true;
					nodo = documento.getNodo();
				}

			} catch (FortimaxException e) {
				log.error(e, e);

				errorCode = e.getMessage();
				errorMessage = e.getMessage();
			} catch (Exception e) {
				log.error(e, e);
				errorCode = FortimaxException.codeUnknowed;
				errorMessage = e.getMessage();

			} finally {
				 
				
			}

			return new String[] { String.valueOf(ok),
					nodo,
					errorCode == null ? new String("") : errorCode,
					errorMessage == null ? new String("") : errorMessage };
	}
	
	private synchronized String generarRandomString() {
		int time = (int)System.currentTimeMillis();
		String hex = Integer.toHexString(time);
		if(hex.length()%2 != 0)
			hex = "0"+hex;
		byte[] bytes = DatatypeConverter.parseHexBinary(hex);
		String token = new String(Base64.encodeBase64(bytes)).replace('+','-').replace('/','_');
		
		SecureRandom random = new SecureRandom();
		String randomString = new BigInteger(130, random).toString(32);
		
		return token+randomString;
	}
	
	//Metodo que se utiliza en producción:
	public String[] uploadFile(String fileName, DataHandler fileData,
			String gaveta, String[] expediente, String carpeta) {
		String[] resultado = uploadFileNodo(fileName, fileData, gaveta, expediente, carpeta);
		return new String[] { resultado[0], resultado[2], resultado[3] }; //No agregar resultado[1] a este return por cuestiones de retrocompatibilidad
	}

	private void writeFile(File file, DataHandler fileData) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileData.writeTo(fileOutputStream);
		fileOutputStream.flush();
		fileOutputStream.close();
		log.trace("Se escribio el archivo " + file);
	}
}
