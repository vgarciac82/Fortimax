package com.syc.fortimax.websevices.expedienteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.entities.Expediente;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.managers.imx_aplicacion_manager;
import com.syc.imaxfile.Descripcion;
import com.syc.imaxfile.DescripcionManager;
import com.syc.imaxfile.ExpedienteManager;

public class ExpedienteService {

	private static final Logger log = Logger.getLogger(ExpedienteService.class);

	/**
	 * Clona un expediente de manera total (Informacion e imagenes) o parcial
	 * (Solo informacion)
	 *
	 * @param drawerOriginal
	 *            Titulo Aplicacion del ORIGEN
	 * @param fieldsOriginal
	 *            Arreglo con el nombre de los campos a buscar del expediente
	 *            Original
	 * @param valuesOriginal
	 *            Arreglo con los valores de los campos a buscar del expediente
	 *            Original
	 * @param drawerCopy
	 *            Titulo Aplicacion del DESTINO
	 * @param fieldsCopy
	 *            Arreglo con el nombre de los campos a buscar del expediente a
	 *            Copiar
	 * @param valuesCopy
	 *            Arreglo con los valores de los campos a buscar del expediente
	 *            a Copiar
	 * @param totalClone
	 *            Boolean para indicar si la clonacion es total o parcial
	 * @return Object con la siguiente informacion:
	 *         <table summary="" border="1">
	 *         <tr>
	 *         <td>Campo</td>
	 *         <td>Descripción</td>
	 *         <td>Tipo</td>
	 *         </tr>
	 *         <tr>
	 *         <td>exito</td>
	 *         <td>Se creó correctamente</td>
	 *         <td>boolean</td>
	 *         </tr>
	 *         <tr>
	 *         <td>errorCode</td>
	 *         <td>Codigo de Error</td>
	 *         <td>String</td>
	 *         </tr>
	 *         <tr>
	 *         <td>errorMessage</td>
	 *         <td>El mensaje que corresponde al codigo de error</td>
	 *         <td>String</td>
	 *         </table>
	 */
	public String[] cloneExpedient(String access, String drawerOriginal,
			String[] fieldsOriginal, String[] valuesOriginal,
			String drawerCopy, String[] fieldsCopy, String[] valuesCopy,
			Boolean totalClone) {
		boolean ok = false;
		String errorCode = null;
		String errorMessage = null;
		log.debug("Se solicito clonacion de expediente con los siguientes datos:\nUsuario["
				+ access
				+ "]\nTitulo Aplicacion Origen["
				+ drawerOriginal
				+ "]");
		if (fieldsOriginal != null) {
			log.debug("\nCampos Origen[" + fieldsOriginal.length + "]");
			for (int i = 0; i < fieldsOriginal.length; i++)
				log.debug("\nCampo Origen[" + i + "][" + fieldsOriginal[i]+ "]");
		}
		if (valuesOriginal != null) {
			log.debug("\nValores Origen[" + valuesOriginal.length + "]");
			for (int i = 0; i < valuesOriginal.length; i++)
				log.debug("\nValor Origen[" + i + "][" + valuesOriginal[i]+ "]");
		}
		log.debug("\nTitulo Aplicacion Destino[" + drawerCopy + "]");
		if (fieldsCopy != null) {
			log.debug("\nCampos Destino[" + fieldsCopy.length + "]");
			for (int i = 0; i < fieldsCopy.length; i++)
				log.debug("\nCampo Detino[" + i + "][" + fieldsCopy[i] + "]");
		}
		if (valuesCopy != null) {
			log.debug("\nValores Destino[" + valuesCopy.length + "]");
			for (int i = 0; i < valuesCopy.length; i++)
				log.debug("\nValor Destino[" + i + "][" + valuesCopy[i] + "]");
		}
		try {
			if (drawerOriginal == null) {
				throw new FortimaxException("FMX-EXP-WS-1001","El expediente original debe tener el Titulo de la Aplicacion");
			}
			if (drawerCopy == null) {
				throw new FortimaxException("FMX-EXP-WS-1002","El expediente a copiar debe tener el Titulo de la Aplicacion");
			}

			if (fieldsOriginal == null || valuesOriginal == null
					|| fieldsOriginal.length == 0 || valuesOriginal.length == 0) {
				throw new FortimaxException("FMX-EXP-WS-1003","Expediente Original vacio");
			}

			if (fieldsOriginal.length != valuesOriginal.length) {
				throw new FortimaxException("FMX-EXP-WS-1004","El numero de campos no concuerda con el nuemero de valores en el Expediente Original");
			}

			if (fieldsCopy == null || valuesCopy == null
					|| fieldsCopy.length == 0 || valuesCopy.length == 0) {
				throw new FortimaxException("FMX-EXP-WS-1005","Expediente a Copiar vacio");
			}

			if (fieldsCopy.length != valuesCopy.length) {
				throw new FortimaxException(
						"FMX-EXP-WS-1006","El numero de campos no concuerda con el nuemero de valores en el Expediente a Copiar");
			}

			if (totalClone == null) {
				throw new FortimaxException("FMX-EXP-WS-1007","Parametro Tipo de Clonacion Vacio");
			}

				try {

					Session sess = HibernateUtils.getSession();
					ArrayList<Expediente> listexOriginal = ExpedienteManager
							.getExpedients(sess, drawerOriginal,
									fieldsOriginal, valuesOriginal);
					sess.close();

					if (listexOriginal.size() == 0) {
						throw new FortimaxException("FMX-EXP-WS-1008","Ninguna coinciden con los datos del expediente Original");
					}

					if (listexOriginal.size() > 1) {
						throw new FortimaxException("FMX-EXP-WS-1009","Mas de un Expediente coinciden con los datos del expediente Original");
					}

					Expediente exOriginal = listexOriginal.get(0);

					Expediente exCopy = new Expediente(drawerCopy, fieldsCopy,
							valuesCopy);

					Iterator<String> keys = exOriginal.keySet().iterator();
					while (keys.hasNext()) {
						String field = keys.next().toUpperCase();
						if (exCopy.get(field) == null) {
							exCopy.put(field, exOriginal.get(field));
						}
					}
					ok = ExpedienteManager.clonacion(access, exOriginal,exCopy, totalClone);

					ok = true;

				} catch (Exception e) {
					log.error(e, e);

				} finally {
					
				}

		} catch (FortimaxException fe) {
			log.error(fe, fe);
			errorCode = fe.getCode();
			errorMessage = fe.getMessage();
		} catch (Exception e) {
			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.getMessage();
		} finally {

		}

		return new String[] { String.valueOf(ok),
				errorCode == null ? new String("") : errorCode,
				errorMessage == null ? new String("") : errorMessage };

	}

	/**
	 * Action que genera un nuevo expediente en FortImax. En caso de existir el
	 * expediente del que se solicita la creación no se genera uno nuevo.
	 *
	 * @param access
	 *            Usuario de la operación
	 * @param titulo_aplicacion
	 *            Nombre de la aplicación
	 * @param carpetaRaiz
	 *            Nombre que tendrá la carpeta ra&iacute;z, al no
	 *            env&iacute;ar este campo se tomará el campo configurado
	 *            como &uacute;nico para la gaveta
	 * @param fieldName
	 *            Arreglo de campos a insertar
	 * @param fieldValue
	 *            Arreglo de los valores a insertar en los campos
	 * @param createEstructura
	 *            Booleano para crear o no la estructura predefinida en el
	 *            administrador para el expediente
	 * @return <table summary="" border="1">
	 *         <tr>
	 *         <td colspan="5" align="center">Campos de Salida</td>
	 *         </tr>
	 *         <tr>
	 *         <td>Campo</td>
	 *         <td>Descripción</td>
	 *         <td>Tipo</td>
	 *         <td>Variable Mapeada</td>
	 *         <td>Valor Ejemplo</td>
	 *         </tr>
	 *         <tr>
	 *         <td>exito</td>
	 *         <td>Se creó correctamente</td>
	 *         <td>boolean</td>
	 *         <td>&nbsp;</td>
	 *         <td>false</td>
	 *         </tr>
	 *         <tr>
	 *         <td>errorCode</td>
	 *         <td>Codigo de Error</td>
	 *         <td>String</td>
	 *         <td>"FMX-EXP-WS-1011"</td>
	 *         </tr>
	 *         <tr>
	 *         <td>errorMessage</td>
	 *         <td>El mensaje que corresponde al codigo de error</td>
	 *         <td>String</td>
	 *         <td>&nbsp;</td>
	 *         <td>"No se pudo obtener la descripci&ntilde;n de la gaveta"</td>
	 *         </tr>
	 *         </table>
	 */
	public String[] generateExpediente(String access, String titulo_aplicacion,
			String carpetaRaiz, String[] fieldName, String[] fieldValue,
			boolean createEstructura) {

		if (log.isDebugEnabled()) {
			String logdbg = "Solicitud de creacion de Expedientes recibida. Parametros:\n[access:"
					+ access
					+ "]\n[titulo_aplicacion:"
					+ titulo_aplicacion
					+ "]\n[carpetaRaiz:"
					+ carpetaRaiz
					+ "]\n[createEstructura:" + createEstructura + "]";

			if (fieldName != null)
				for (int i = 0; i < fieldName.length; i++) {
					logdbg += "\nCampo[" + i + "]=" + fieldName[i];
				}
			if (fieldValue != null)
				for (int i = 0; i < fieldValue.length; i++) {
					logdbg += "\nValor[" + i + "]=" + fieldValue[i];
				}
			log.debug(logdbg);
		}
		// ================ Inicializacion de variables ================ //
		boolean exito = false;
		String errorCode = null;
		String errorMessage = null;
		String nombre_carpeta = (carpetaRaiz == null || "".equals(carpetaRaiz) ? null
				: carpetaRaiz);

		Map<String, String> mapValores = null;
		Map<String, String[]> map = new HashMap<String, String[]>();
		DescripcionManager dm = null;
		Descripcion[] dsc = null;

		ExpedienteManager em = null;

		try {

			// ================ Validaciones ================ //

			if (titulo_aplicacion == null || "".equals(titulo_aplicacion))
				throw new FortimaxException("FMX-EXP-WS-1015",
						"El titulo de aplicacion es null o vacio");

			if (fieldName == null || fieldName.length == 0) {
				throw new FortimaxException("FMX-EXP-WS-1016",
						"Parametro fieldName nulo o vacio");
			}

			if (fieldValue == null || fieldValue.length == 0) {
				throw new FortimaxException("FMX-EXP-WS-1017",
						"Parametro fieldValue nulo o vacio");
			}

			if (fieldName.length != fieldValue.length) {
				throw new FortimaxException("FMX-EXP-WS-1018",
						"No concuerda el tama\u00F1o de  fieldName con  fieldValue");
			}

			// ================ Valida que exista la aplicacion(Gaveta) destino
			// ================ //
			titulo_aplicacion = titulo_aplicacion.toUpperCase();
			imx_aplicacion app = imx_aplicacion_manager.select(titulo_aplicacion);

			if (app == null)
				throw new FortimaxException(
						"FMX-EXP-WS-1016",
						"No existe aplicaci\u00F3n identificada por el titulo_aplicacion proporcionado.");

			// ================ Obtener datos de entrada al WS. ================
			mapValores = new HashMap<String, String>();

			for (int i = 0; i < fieldName.length; i++) {
				String key = fieldName[i];
				if (key == null || "".equals(key))
					throw new FortimaxException("Unknow",
							"Se recibio un nombre de campo vacio. Indice vacio: "
									+ i);

				mapValores.put(key.toUpperCase(), fieldValue[i]);

			}

			// ================ Mapear datos recibidos contra datos definidos en
			// la gaveta ================ //

			map = new HashMap<String, String[]>();
			dm = new DescripcionManager();
			dsc = dm.selectDescripcion(titulo_aplicacion);
			for (int i = 0; i < dsc.length; i++) {

				String name = dsc[i].getNombreCampoLower();
				String value = mapValores.get(name.toUpperCase());

				if (dsc[i].getRequeridoBoolean()
						&& (value == null || "".equals(value)))
					throw new FortimaxException("FMX-EXP-WS-1013",
							"Faltan campos que son requeridos: " + name);

				String[] values = { String.valueOf(dsc[i].getIdTipoDatos()),
						String.valueOf(dsc[i].getIndiceTipo()),
						((value == null) ? "" : value),
						dsc[i].getNombreColumna() };

				map.put(name, values);

				if (i == 0 && nombre_carpeta == null)
					nombre_carpeta = value;
			}

			// ================ Crear expediente ================ //
			em = new ExpedienteManager();
			String[] retVal = em.insertExpediente(titulo_aplicacion, access, nombre_carpeta, map, createEstructura);
			if("Expediente creado correctamente.".equals(retVal[2]))
				exito = true;
			else {
				errorCode = FortimaxException.codeUnknowed;
				errorMessage = retVal[2];
			}

			// ================ FIN ================ //

		} catch (FortimaxException fe) {
			log.error(fe, fe);
			errorCode = fe.getCode();
			errorMessage = fe.getMessage();
			exito = false;
		} catch (Exception e) {
			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.getMessage();
			exito = false;
		} finally {
			mapValores = null;
			map = null;
			dm = null;
			dsc = null;
			nombre_carpeta = null;
		}
		return new String[] { String.valueOf(exito),
				errorCode == null ? new String("") : errorCode,
				errorMessage == null ? new String("") : errorMessage };
	}

	public String[] modifyExpediente(String access, String tituloAplicacion,
			String[] fieldsOriginal, String[] valuesOriginal,
			String[] fieldsCopy, String[] valuesCopy) {

		boolean exito = false;
		String errorCode = null;
		String errorMessage = null;
		DescripcionManager dm = null;
		Descripcion[] dsc = null;
		ExpedienteManager em = null;

		try {
			/* Validacion de datos de entrada */
			validaModifyExpediente(access, tituloAplicacion, fieldsOriginal,
					valuesOriginal, fieldsCopy, valuesCopy);

			/*
			 * Carga el expediente a modificar, Si no existe o si existe mas de
			 * uno, lanzara una excepcion.
			 */
			Session sess = HibernateUtils.getSession();
			ArrayList<Expediente> listexOriginal = ExpedienteManager
					.getExpedients(sess, tituloAplicacion, fieldsOriginal,
							valuesOriginal);
			sess.close();

			if (listexOriginal.size() == 0) {
				throw new FortimaxException(
						"FMX-EXP-WS-2006",
						"No se encontr\u00F3 alg\u00FAn expediente que coincida con los criterios de b\u00FAsqueda.");
			}

			if (listexOriginal.size() > 1) {
				throw new FortimaxException("FMX-EXP-WS-2007",
						"M\u00E1s de un expediente coincide con los criterios de b\u00FAsqueda.");
			}

			Expediente exOriginal = listexOriginal.get(0);

			/*
			 * Creamos un nuevo expediente con el ID Gabinete y Titulo
			 * Aplicacion Originales, para despues mapear los datos a actualizar
			 * en el expediente.
			 */
			Expediente expToUpdate = new Expediente(
					exOriginal.getTituloAplicacion(), fieldsCopy, valuesCopy);
			expToUpdate.setIdGainete(exOriginal.getIdGainete());

			dm = new DescripcionManager();
			dsc = dm.selectDescripcion(exOriginal.getTituloAplicacion());

			Map<String, Object> camposParaActualizar = new HashMap<String, Object>(); 
			for (int i = 0; i < dsc.length; i++) {
				String name = dsc[i].getNombreCampoLower().toUpperCase();

				if (!expToUpdate.containsKey(name))
					continue;

				String value = (String) expToUpdate.get(name.toUpperCase());

				if (dsc[i].getRequeridoBoolean() && StringUtils.isBlank(value))
					throw new FortimaxException("FMX-EXP-WS-2008",
							"Intenta actualizar el campo requerido " + name
									+ " con valor vacio.");

				String[] values = { String.valueOf(dsc[i].getIdTipoDatos()),
						String.valueOf(dsc[i].getIndiceTipo()),
						((value == null) ? "" : value),
						dsc[i].getNombreColumna() };

				expToUpdate.put(name, values);
				camposParaActualizar.put(name, values[2]);
			}

			em = new ExpedienteManager();
			exito = em.updateExpedientes(tituloAplicacion,
					expToUpdate.getIdGainete(), camposParaActualizar);

		} catch (FortimaxException e) {
			log.error(e, e);
			errorCode = e.getCode();
			errorMessage = e.getMessage();
			exito = false;
		} catch (Exception e) {
			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.toString();
		}
		return new String[] { String.valueOf(exito),
				errorCode == null ? new String("") : errorCode,
				errorMessage == null ? new String("") : errorMessage };

	}

	private void validaModifyExpediente(String access, String drawerOriginal,
			String[] fieldsOriginal, String[] valuesOriginal,
			String[] fieldsCopy, String[] valuesCopy) throws FortimaxException {
		/*
		 * Valida que se encuentren los parametros requeridos completos. (En
		 * este caso particular todos son requeridos). Primero las validaciones
		 * basicas, como que el numero de campos corresponda con el numero de
		 * valores, posteriormente las validaciones de contenido
		 */
		if (StringUtils.isBlank(access) || StringUtils.isBlank(drawerOriginal)
				|| fieldsOriginal == null || valuesOriginal == null
				|| fieldsCopy == null || valuesCopy == null)
			throw new FortimaxException("FMX-EXP-WS-2000",
					"Se recibieron valores nulos en campos requeridos.");

		if (fieldsOriginal.length != valuesOriginal.length)
			throw new FortimaxException(
					"FMX-EXP-WS-2001",
					"No corresponde el n\u00FAmero de campos de b\u00FAsqueda con el n\u00FAmero de valores.");

		if (fieldsCopy.length != valuesCopy.length)
			throw new FortimaxException(
					"FMX-EXP-WS-2002",
					"No corresponde el n\u00FAmero de campos a actualizar con el n\u00FAmero de valores.");

		if (fieldsOriginal.length <= 0)
			throw new FortimaxException("FMX-EXP-WS-2003",
					"No se especificaron campos de b\u00FAsqueda");

		if (fieldsCopy.length <= 0)
			throw new FortimaxException("FMX-EXP-WS-2004",
					"No se especificaron campos para actualizar");

		validaLista(fieldsCopy, true, false);

		validaLista(fieldsOriginal, true, false);

	}

	private void validaLista(String[] arr, boolean isField, boolean allowEmpty)
			throws FortimaxException {
		for (int i = 0; i < arr.length; i++)
			if (StringUtils.isBlank(arr[i]) && !allowEmpty)
				throw new FortimaxException("FMX-EXP-WS-2005",
						"Se encontro un nombre de "
								+ (isField ? "campo" : "valor") + " vacio");
	}
	
	public String[] getExpedientes(String gaveta, String[] campo, String[] valor) {
		List<String> salida = new ArrayList<String>();
		try {
			ExpedienteManager expedienteManager = new ExpedienteManager();
			List<Map<String, Object>> expedientes = expedienteManager.getExpedientes(gaveta, campo, valor);
			for(Map<String, Object> expediente : expedientes) {
				salida.add(expediente.toString());
			}
			salida.add(null);
			salida.add(null);
		/*} catch (FortimaxException e){
			log.error(e, e);
			salida.add(e.getCode());
			salida.add(e.getMessage()); */
		} catch (Exception e) {
			log.error(e, e);
			salida.add(FortimaxException.codeUnknowed);
			salida.add(e.toString());
		}
		return salida.toArray(new String[0]);
	}
}