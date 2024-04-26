package com.syc.imaxfile;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.entities.Expediente;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_catalogo_atributos;
import com.syc.fortimax.hibernate.entities.imx_catalogo_estructuras;
import com.syc.fortimax.hibernate.entities.imx_descripcion;
import com.syc.fortimax.hibernate.entities.imx_documentos_atributos;
import com.syc.fortimax.hibernate.entities.imx_documentos_atributos_id;
import com.syc.fortimax.hibernate.entities.imx_estruc_doctos;
import com.syc.fortimax.hibernate.managers.NotMappedCondition;
import com.syc.fortimax.hibernate.managers.NotMappedManager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_atributos_manager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_estructuras_manager;
import com.syc.fortimax.hibernate.managers.imx_documentos_atributos_manager;
import com.syc.servlets.models.AtributosModel;
import com.syc.servlets.models.NodoModel;
import com.syc.utils.Json;
import com.syc.utils.Utils;

public class ExpedienteManager {

	private static final Logger log = Logger.getLogger(ExpedienteManager.class);

	/**
	 * Crea un nuevo expediente Fortimax completo, es decir, incluyendo la
	 * estructura de documentos completa definida para la aplicacion.
	 *
	 * @param titulo_aplicacion
	 *            Aplicacion en la que se insertara el nuevo expediente
	 * @param nombre_usuario
	 *            Usuario creador del expediente
	 * @param nombre_carpeta_raiz
	 *            Nombre de la carpeta raiz de la estructura de documentos
	 * @param map
	 *            Elementos del expediente
	 * @return Arreglo con el resultado.
	 */
	public String[] insertExpediente(String titulo_aplicacion,
			String nombre_usuario, String nombre_carpeta_raiz, Map<?, ?> map) {
		return insertExpediente(titulo_aplicacion,
				nombre_usuario, nombre_carpeta_raiz, map, true, true);
	}

	/**
	 * Crea un nuevo expediente Fortimax.
	 *
	 * @param titulo_aplicacion
	 *            Aplicacion en la que se insertara el nuevo expediente
	 * @param nombre_usuario
	 *            Usuario creador del expediente
	 * @param nombre_carpeta_raiz
	 *            Nombre de la carpeta raiz de la estructura de documentos
	 * @param map
	 *            Elementos del expediente
	 * @param creaEstructura
	 *            <code>true</code> Para crear la estructura completa definida
	 *            para la aplicacion
	 *            <code>false</code> Crea solo la carpeta raiz.
	 * @return Arreglo con el resultado.
	 */
	public String[] insertExpediente(String titulo_aplicacion,
			String nombre_usuario, String nombre_carpeta_raiz,
			Map<String, String[]> map, boolean creaEstructura) {
		return insertExpediente(titulo_aplicacion,
				nombre_usuario, nombre_carpeta_raiz, map, creaEstructura, true);
	}

	/**
	 * Crea un nuevo expediente Fortimax.
	 *
	 * @param titulo_aplicacion
	 *            Aplicación en la que se insertara el nuevo expediente
	 * @param nombre_usuario
	 *            Usuario creador del expediente
	 * @param nombre_carpeta_raiz
	 *            Nombre de la carpeta raiz de la estructura de documentos
	 * @param map
	 *            Elementos del expediente
	 * @param createEstruc
	 *            <code>true</code> Para crear la estructura completa definida
	 *            para la aplicación
	 *            <code>false</code> Crea solo la carpeta raiz.
	 * @return Arreglo con el resultado.
	 */
	public String[] insertExpediente(String titulo_aplicacion,
			String nombre_usuario, String nombre_carpeta_raiz, Map<?, ?> map,
			boolean createEstruc, boolean closeConnectionOnFinish) {
		StringBuffer fldsList = null;
		String[] retVal = new String[3];
		HibernateManager hm = new HibernateManager();
		
		try {
			int id_gabinete = getNewIdGabinete(titulo_aplicacion);
			if (id_gabinete == -1)
				throw new Exception("No se logro obtener un nuevo Id de Gabinete");

			retVal[0] = String.valueOf(id_gabinete);
			/*
			map.put("fmx_fecha_creacion", new Date());
			map.put("fmx_fecha_mantenimiento", value);
			*/
			Set<?> fields = map.keySet();
			Iterator<?> fldsIter = fields.iterator();

			boolean idxValidate = false;
			String idxToken = "";
			String token = " WHERE ";
			StringBuffer sqlSelect = new StringBuffer("SELECT 1 FROM imx"
					+ titulo_aplicacion.toLowerCase());

			StringBuffer sqlInsert = new StringBuffer("INSERT INTO imx"
					+ titulo_aplicacion.toLowerCase() + " (id_gabinete, activo");
			StringBuffer values = new StringBuffer(" VALUES (" + id_gabinete
					+ ", 'N'");

			while (fldsIter.hasNext()) {
				String field = (String) fldsIter.next();
				String[] fldsData = (String[]) map.get(field);
				int idxTipo = Integer.parseInt(fldsData[1]);
				String fldName = fldsData[3];

				if (idxTipo == 2) {
					if (!idxValidate)
						fldsList = new StringBuffer();

					idxValidate = true;

					fldsList.append(idxToken + fldName);
					idxToken = "|";

					sqlSelect.append(token + field + " = ?");
					token = " AND ";
				}

				sqlInsert.append(", " + field);
				values.append(", ?");
			}

			sqlInsert.append(")");
			values.append(")");
			sqlInsert.append(values);

			Query queryInsert = hm.createSQLQuery(sqlInsert.toString());
			Query querySelect = hm.createSQLQuery(sqlSelect.toString());
			
			int colInsert = 0;
			int colSelect = 0;
			
			Set<?> keys = map.keySet();
			Iterator<?> keyIter = keys.iterator();

			while (keyIter.hasNext()) {
				String key = (String) keyIter.next();
				String[] keyValues = (String[]) map.get(key);
				int tipoDato = Integer.parseInt(keyValues[0]);
				int idxTipo = Integer.parseInt(keyValues[1]);
				String value = keyValues[2];
	
				if(value.isEmpty()&&tipoDato!=8) {
					queryInsert.setString(colInsert++, null);
					if (idxValidate && (idxTipo == 2)) {
						querySelect.setString(colSelect++, null);
					}
				} else
				switch (tipoDato) {
				case 3: // Small Integer
				case 4: // Long Integer
					queryInsert.setInteger(colInsert++, Integer.parseInt(value));
					if (idxValidate && (idxTipo == 2)){
						querySelect.setInteger(colSelect++, Integer.parseInt(value));
					}
					break;
				case 5: // Decimal
				case 7: // Double, Float
					queryInsert.setDouble(colInsert++, Double.parseDouble(value));
					if (idxValidate && (idxTipo == 2)){
						querySelect.setDouble(colSelect++, Double.parseDouble(value));
					}
					break;
				case 10: // String
					queryInsert.setString(colInsert++, value);
					if (idxValidate && (idxTipo == 2)){
						querySelect.setString(colSelect++, value);
					}
					break;
				case 12: // Long String
					// TODO Falta implementar Long String
					/*pstmnt0.setString(colInsert++, value);
					if (idxValidate && (idxTipo == 2))
						pstmnt1.setString(colSelect++, value);*/  //Cuando creas campos memos se crean con tipo double
					break;
				case 8: // Fecha
					queryInsert.setDate(colInsert++, value.equals("")?null:Date.valueOf(value));
					if (idxValidate && (idxTipo == 2)){
						querySelect.setDate(colSelect++, Date.valueOf(value));
					}
					break;
				}
			}

			retVal[1] = (fldsList == null) ? null : fldsList.toString();

			
			if (idxValidate) {
				
				@SuppressWarnings("unchecked")
				List<Integer> camposSeleccionados = querySelect.list();
				if(!camposSeleccionados.isEmpty()){
					retVal[2] = "Error. El expediente ya existe.";
					return retVal;
				}
				
			}

			hm.executeQuery(queryInsert);

			if (createEstruc) {
				if (creaEstructuraExpediente(titulo_aplicacion,
						id_gabinete, nombre_usuario, nombre_carpeta_raiz)) {
					retVal[1] = null;
				}
			} else {

				/*
				 * No es posible crear solo el expediente en fortimax sin
				 * estructura, al menos debe contener la carpeta raiz. Valida
				 * que exista al menos esta carpeta, en caso contrario la crea.
				 */

				// Por definicion la carpeta raiz tiene el id 0
				Carpeta c = new Carpeta(titulo_aplicacion, id_gabinete, 0);
				CarpetaManager cm = new CarpetaManager();
				c = cm.selectCarpeta(c);
				if (c == null) {
					c = new Carpeta(titulo_aplicacion, id_gabinete, 0);
					nombre_carpeta_raiz = (nombre_carpeta_raiz == null) ? ((String[])map.values().toArray()[0])[2] : nombre_carpeta_raiz;
					nombre_carpeta_raiz = (nombre_carpeta_raiz == null) ? "" : nombre_carpeta_raiz;
					
					c.setNombreCarpeta(nombre_carpeta_raiz);
					c.setNombreUsuario(nombre_usuario);
					c.setBanderaRaiz("S");
					c.setFechaCreacion(new Date(System.currentTimeMillis()));
					c.setFechaModificacion(new Date(System.currentTimeMillis()));
					c.setNumeroAccesos(0);
					c.setNumeroCarpetas(0);
					c.setNumeroDocumentos(0);

					cm.insertSingleFolder(c);

				}

				retVal[1] = null;

			}
			retVal[2]= "Expediente creado correctamente.";
		} catch (Exception e) {
			log.error(e, e);
			retVal[2] = e.getCause().toString();
		} finally {
			hm.close();
		}
		return retVal;
	}
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private static void insertNodoModel(NodoModel nodoModel, Integer id_carpeta_padre, String titulo_aplicacion, int id_gabinete, String nombre_usuario, List<imx_catalogo_atributos> imx_catalogo_atributos) throws Exception {
		GetDatosNodo nodo = new GetDatosNodo(titulo_aplicacion+"_G"+id_gabinete+nodoModel.id);
		if(nodo.isCarpeta()) {
			CarpetaManager cm = new CarpetaManager();
			Carpeta c = new Carpeta(titulo_aplicacion,id_gabinete,nodo.getIdCarpeta());
			c.setNombreCarpeta(nodoModel.text);
			c.setNombreUsuario(nombre_usuario);
			if(id_carpeta_padre==null)
				c.setBanderaRaiz("S");
			else {
				c.setIdCarpetaPadre(id_carpeta_padre);
				c.setIdCarpetaHija(nodo.getIdCarpeta());
			}
			cm.insertSingleFolder(c);
			if(nodoModel.children!=null)
				for(NodoModel nodoHijo: nodoModel.children) {
					insertNodoModel(nodoHijo, nodo.getIdCarpeta(), titulo_aplicacion, id_gabinete, nombre_usuario, imx_catalogo_atributos);
				}
		} else if(nodo.isDocumento()) {
			DocumentoManager dm = new DocumentoManager();
			Documento d = new Documento(titulo_aplicacion, id_gabinete, id_carpeta_padre, nodo.getIdDocumento(), nodoModel.text);
			d.setNombreUsuario(nombre_usuario);
			d.setUsuarioModificacion(nombre_usuario);
			d.setIdTipoDocto(-1);
			d.setNombreTipoDocto("IMX_SIN_TIPO_DOCUMENTO");
			dm.insertDocumento(d);
			
			List<imx_documentos_atributos> atributos = new ArrayList<imx_documentos_atributos>();
			if(nodoModel.atributos!=null)
				for(AtributosModel atributo: nodoModel.atributos) {
					if(atributo.isActivo()) {
						imx_catalogo_atributos catalogo_atributo = new imx_catalogo_atributos();
						for(imx_catalogo_atributos atributoActual : imx_catalogo_atributos) {
							if(atributoActual.getId()==atributo.getIdAtributo()) {
								catalogo_atributo = atributoActual;
							}
						}
						String valor = atributo.getValorDefault().toString(); 
						if("Date".equals(catalogo_atributo.getTipo())) {
							valor = sdf.format(sdf.parse(valor));
						} else if("Boolean".equals(catalogo_atributo.getTipo())) {
							valor = ("false".equals(valor)||"0".equals(valor))?"0":"1";
						}
						imx_documentos_atributos_id imx_documentos_atributos_id = new imx_documentos_atributos_id(titulo_aplicacion,id_gabinete,id_carpeta_padre,d.getIdDocumento(),atributo.getIdAtributo());
						imx_documentos_atributos imx_documentos_atributos = new imx_documentos_atributos(imx_documentos_atributos_id, valor);
						atributos.add(imx_documentos_atributos);
					}
				}
			if(atributos.size()>0) {
				imx_documentos_atributos_manager idam = new imx_documentos_atributos_manager();
				idam.save(atributos);
			}
		}
	}
	
	private static boolean createEstructuraExpedienteAtributos(String nombre_estructura, String titulo_aplicacion, int id_gabinete, String nombre_usuario, String nombre_carpeta_raiz) {
		imx_catalogo_estructuras_manager imx_catalogo_estructuras_manager = new imx_catalogo_estructuras_manager();
		try {
			imx_catalogo_estructuras imx_catalogo_estructuras = imx_catalogo_estructuras_manager.select(null, nombre_estructura, null, null).uniqueResult();
			if(imx_catalogo_estructuras==null) {
				log.error("No existe la estructura '"+nombre_estructura+"'");
				return false;
			} else {
				NodoModel nodoModel = Json.getObject(imx_catalogo_estructuras.getDefinicion(), NodoModel.class);
				nodoModel.text = nombre_carpeta_raiz;
				List<imx_catalogo_atributos> imx_catalogo_atributos = new imx_catalogo_atributos_manager().list();
				insertNodoModel(nodoModel, null, titulo_aplicacion, id_gabinete, nombre_usuario, imx_catalogo_atributos);
			}
		} catch (Exception e) {
			log.error(e,e);
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean creaEstructuraExpediente(String titulo_aplicacion, int id_gabinete, String nombre_usuario, String nombre_carpeta_raiz) {

		Query query = null;
		String stringQuery = null;
		int countIdCarpeta = -1;
		boolean retVal = false;
		
		HibernateManager hm = new HibernateManager();
		stringQuery = "FROM imx_estruc_doctos WHERE titulo_aplicacion = '"+titulo_aplicacion+"' AND posicion_elemento = -1";
		imx_estruc_doctos imx_estruc_doctos = (imx_estruc_doctos) hm.createQuery(stringQuery).uniqueResult();
		if(imx_estruc_doctos!=null)
			retVal = createEstructuraExpedienteAtributos(imx_estruc_doctos.getid().getNombreEstructura(), titulo_aplicacion, id_gabinete, nombre_usuario, nombre_carpeta_raiz);
		else
		try {
			stringQuery = "SELECT COUNT(NOMBRE_ELEMENTO) FROM IMX_ESTRUC_DOCTOS WHERE TITULO_APLICACION = '"
					+ titulo_aplicacion + "'";
			
			int numeroDeNodosEnEstructura = Utils.getInteger(hm.createSQLQuery(stringQuery).uniqueResult());
			
			if(numeroDeNodosEnEstructura > 0){
				int[] arrIdCarpetaPadre = new int[numeroDeNodosEnEstructura];
				int[] arrIdDocumento = new int[numeroDeNodosEnEstructura];
				
				stringQuery = "SELECT prioridad, profundidad, nombre_elemento, descripcion"
						+ " FROM imx_estruc_doctos"
						+ " WHERE titulo_aplicacion = ? ORDER BY nombre_estructura, posicion_elemento";
				
				query = hm.createSQLQuery(stringQuery).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				
				query.setString(0, titulo_aplicacion);
				
				List<Map<String, ?>> elementosDeEstructura = query.list();
				
				List<Query> listaQueries = new ArrayList<Query>();
				
				for (Map<String, ?> elemento : elementosDeEstructura) {
					elemento = new CaseInsensitiveMap(elemento);
					int prioridad = Utils.getInteger(elemento.get("prioridad"));
					int profundidad = Utils.getInteger(elemento.get("profundidad")) - 1;
					String nombre_elemento = Utils.getString(elemento.get("nombre_elemento"));
					String descripcion = Utils.getString(elemento.get("descripcion"));

					if (prioridad == -1) {
						arrIdCarpetaPadre[profundidad] = ++countIdCarpeta;

						stringQuery = "INSERT INTO imx_carpeta "
								+ "( titulo_aplicacion, id_gabinete, id_carpeta"
								+ ", nombre_carpeta, nombre_usuario, bandera_raiz"
								+ ", fh_creacion, fh_modificacion, numero_accesos"
								+ ", numero_carpetas, numero_documentos, descripcion) "
								+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
						
						query = hm.createSQLQuery(stringQuery);
						
						query.setString(0, titulo_aplicacion);
						query.setInteger(1, id_gabinete);
						query.setInteger(2, countIdCarpeta);
						query.setString(3,
								(countIdCarpeta == 0) ? nombre_carpeta_raiz
										: nombre_elemento);
						query.setString(4, nombre_usuario);
						query.setString(5, (countIdCarpeta == 0) ? "S" : "N");
						query.setTimestamp(6,
								new Timestamp(System.currentTimeMillis()));
						query.setTimestamp(7,
								new Timestamp(System.currentTimeMillis()));
						query.setInteger(8, 0);
						query.setInteger(9, 0);
						query.setInteger(10, 0);
						query.setString(11, descripcion);

						listaQueries.add(query);

						if (countIdCarpeta > 0) {
							
							stringQuery = "INSERT INTO imx_org_carpeta "
											+ "( titulo_aplicacion, id_gabinete, id_carpeta_hija,"
											+ " id_carpeta_padre, nombre_hija) "
											+ "VALUES (?, ?, ?, ?, ?)";

							query = hm.createSQLQuery(stringQuery);
							
							query.setString(0, titulo_aplicacion);
							query.setInteger(1, id_gabinete);
							query.setInteger(2, countIdCarpeta);
							query.setInteger(3, arrIdCarpetaPadre[profundidad - 1]);
							query.setString(4, nombre_elemento);

							listaQueries.add(query);
						}
					} else if (prioridad >= 0) { // Documento
						int id_tipo_docto = -1;
						
						stringQuery = "SELECT id_tipo_docto"
								+ " FROM imx_tipo_documento"
								+ " WHERE titulo_aplicacion = '"
								+ titulo_aplicacion
								+ "' AND nombre_tipo_docto = '"
								+ nombre_elemento + "'";

						query = hm.createSQLQuery(stringQuery).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

						List<Map<String, ?>> tiposDeDocumento = query.list();
						
						if(!tiposDeDocumento.isEmpty()){
							Map<String, ?> tipo = new CaseInsensitiveMap(tiposDeDocumento.get(0));
							id_tipo_docto = Utils.getInteger(tipo.get("id_tipo_docto"));
						}
						
						arrIdDocumento[arrIdCarpetaPadre[profundidad - 1]]++;

						stringQuery = "INSERT INTO imx_documento "
										+ "( titulo_aplicacion"
										+ ", id_gabinete"
										+ ", id_carpeta_padre"
										+ ", id_documento"
										+ ", nombre_documento"
										+ ", nombre_usuario"
										+ ", prioridad"
										+ ", id_tipo_docto"
										+ ", fh_creacion"
										+ ", fh_modificacion"
										+ ", numero_accesos"
										+ ", numero_paginas"
										+ ", titulo"
										+ ", autor"
										+ ", materia"
										+ ", descripcion"
										+ ", clase_documento"
										+ ", estado_documento"
										+ ", tamano_bytes"
										+ ", usuario_modificacion"
										+ ")"
										+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
						
						query = hm.createSQLQuery(stringQuery);
										
						query.setString(0, titulo_aplicacion);
						query.setInteger(1, id_gabinete);
						query.setInteger(2, arrIdCarpetaPadre[profundidad - 1]);
						query.setInteger(
								3,
								arrIdDocumento[arrIdCarpetaPadre[profundidad - 1]]);
						query.setString(4, nombre_elemento);
						query.setString(5, nombre_usuario);
						query.setInteger(6, prioridad);
						query.setInteger(7, id_tipo_docto);
						query.setTimestamp(8,
								new Timestamp(System.currentTimeMillis()));
						query.setTimestamp(9,
								new Timestamp(System.currentTimeMillis()));
						query.setInteger(10, 0);
						query.setInteger(11, 0);
						query.setParameter(12, null);
						query.setParameter(13, null);
						query.setString(14, "ORIGINAL");
						query.setString(15, descripcion);
						query.setInteger(16, 0);
						query.setString(17, "V");
						query.setDouble(18, 0);
						query.setString(19, nombre_usuario);
						listaQueries.add(query);
					}
				
				}
				hm.executeQueries(listaQueries);
				retVal = true;
			} else {

				stringQuery = "INSERT INTO imx_carpeta "
						+ "( titulo_aplicacion, id_gabinete, id_carpeta"
						+ ", nombre_carpeta, nombre_usuario, bandera_raiz"
						+ ", fh_creacion, fh_modificacion, numero_accesos"
						+ ", numero_carpetas, numero_documentos, descripcion) "
						+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
				query = hm.createSQLQuery(stringQuery);

				query.setString(0, titulo_aplicacion);
				query.setInteger(1, id_gabinete);
				query.setInteger(2, 0);
				query.setString(3, nombre_carpeta_raiz);
				query.setString(4, nombre_usuario);
				query.setString(5, "S");
				query.setTimestamp(6,
						new Timestamp(System.currentTimeMillis()));
				query.setTimestamp(7,
						new Timestamp(System.currentTimeMillis()));
				query.setInteger(8, 0);
				query.setInteger(9, 0);
				query.setInteger(10, 0);
				query.setParameter(11, null);

				hm.executeQuery(query);

				retVal = true;
			}
			
		} catch (Exception e) {
			log.error(e, e);
		} finally{
			hm.close();
		}
		
		return retVal;
	}

	/**
	 * Retorna los id_gabinete de los registros cuyo campo fieldName tiene el valor fieldValue
	 * @param titulo_aplicacion
	 * @param fieldName
	 * @param fieldValue
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getIdExpedientes(String titulo_aplicacion, String fieldName, String fieldValue)
			throws Exception {

		titulo_aplicacion = StringUtils.trimToNull(titulo_aplicacion);
		if (titulo_aplicacion == null)
			throw new Exception(
					"Parametro titulo_aplicacion invalido en getIdExpedientes");

		fieldName = StringUtils.trimToNull(fieldName);
		if (fieldName == null)
			throw new Exception(
					"Parametro fieldName invalido en getIdExpedientes");

		fieldValue = StringUtils.trimToNull(fieldValue);
		if (fieldValue == null)
			throw new Exception(
					"Parametro fieldValue invalido en getIdExpedientes");

		ArrayList<Integer> array_ids_gabinete = new ArrayList<Integer>();

		HibernateManager hm = new HibernateManager();
		
		try{
			
			String stringSQL = "SELECT * FROM IMX" + titulo_aplicacion.trim()
					+ " WHERE " + fieldName.trim() + "= ?";
			
			Query query = hm.createSQLQuery(stringSQL).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			
			query.setString(0, fieldValue.trim());
			
			List<Map<String, ?>> ids_gabinete = query.list();
			
			for (Map<String, ?> id : ids_gabinete) {
				id = new CaseInsensitiveMap(id);
				array_ids_gabinete.add(Utils.getInteger(id.get("id_gabinete")));
			}
		} finally {
			hm.close();
		}
		
		return array_ids_gabinete;
	}

	public static ArrayList<Integer> getIdExpediente(
			String gaveta, ExpedienteCampo[] expediente) throws Exception {

		ArrayList<Integer> ids = new ArrayList<Integer>();

		StringBuilder stringQuery = new StringBuilder();
		stringQuery.append("SELECT id_gabinete FROM imx");
		stringQuery.append(gaveta.toLowerCase());
		stringQuery.append(" WHERE ");

		for (int i = 0; i < expediente.length; i++) {
			if (expediente[i] != null
					&& StringUtils.isNotBlank(expediente[i].getValue())) {

				switch (expediente[i].getIdTipoDato()) {
				// Si es float decimal o double
				case 5:
				case 7:
					stringQuery.append(" round(");
					stringQuery.append(expediente[i].getColumnName());
					stringQuery.append(") = round(");
					stringQuery.append(expediente[i].getValue());
					stringQuery.append(") ");
					break;
				default:
					stringQuery.append(expediente[i].getColumnName());
					stringQuery.append(" = '");
					stringQuery.append(expediente[i].getValue());
					stringQuery.append("'");
					break;
				}
				if ((i + 1) < expediente.length && expediente[i + 1] != null) {
					stringQuery.append(" AND ");
				}
			}
		}

		HibernateManager hm = new HibernateManager();
		
		try{
			Query query = hm.createSQLQuery(stringQuery.toString());
			
			if(!query.list().isEmpty()){
				for (Object id : query.list()) {
					ids.add(Utils.getInteger(id));
				}
			}
		} finally{
			hm.close();
		}

		return ids;
	}

	private synchronized int getNewIdGabinete(String titulo_aplicacion) {
		int maxId = 0;
		HibernateManager hm = new HibernateManager();
		try {
			Query query = hm.createSQLQuery("SELECT MAX(id_gabinete) FROM imx"
					+ titulo_aplicacion.toLowerCase());

			if(query.uniqueResult()!=null){
				maxId = ((BigDecimal)query.uniqueResult()).intValue();
			}

		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		
		return ++maxId;
	}

	public boolean deleteExpediente(String titulo_aplicacion, int id_gabinete) {
		boolean retVal = true;
		HibernateManager hm = new HibernateManager();
		try {
			List<String> listaStringQueries = new ArrayList<String>();
			
			listaStringQueries.add("DELETE FROM imx_pagina"
					+ " WHERE titulo_aplicacion = '" + titulo_aplicacion
					+ "' AND id_gabinete = " + id_gabinete);

			listaStringQueries.add("DELETE FROM imx_documento"
					+ " WHERE titulo_aplicacion = '" + titulo_aplicacion
					+ "' AND id_gabinete = " + id_gabinete);

			listaStringQueries.add("DELETE FROM imx_org_carpeta"
					+ " WHERE titulo_aplicacion = '" + titulo_aplicacion
					+ "' AND id_gabinete = " + id_gabinete);

			listaStringQueries.add("DELETE FROM imx_carpeta"
					+ " WHERE titulo_aplicacion = '" + titulo_aplicacion
					+ "' AND id_gabinete = " + id_gabinete);

			listaStringQueries.add("UPDATE imx_carpeta"
					+ " SET numero_carpetas = numero_carpetas - 1"
					+ " WHERE titulo_aplicacion = '" + titulo_aplicacion
					+ "' AND id_gabinete = " + id_gabinete);

			listaStringQueries.add("DELETE FROM imx" + titulo_aplicacion
					+ " WHERE id_gabinete = " + id_gabinete);

			hm.executeSQLQueries(listaStringQueries);

			retVal = true;
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		
		return retVal;
	}

	/**
	 * Clonación parcial de expedientes entre gavetas. Es posible clonar
	 * expedientes incluso entre gavetas diferentes (Origen &lt;&gt; Destino)
	 * incluyendo el copiado lógico de las imágenes. Es decir no
	 * se copia la imagen almacenada en el volumen.
	 *
	 * @param exOriginal
	 *            Expediente original (Origen)
	 * @param exCopy
	 *            Expediente copia (Destino)
	 * @return <code>true</code> Si la transacción de copiado de
	 *         información y copiado lógico de imágenes
	 *         (Estructura) termina con <b>éxito</b>
	 *         <code>false</code> en caso contrario.
	 *
	 */
	public static boolean clonacion(String access,
			Expediente exOriginal, Expediente exCopy, boolean total)
			throws FortimaxException {
		boolean ok = false;
		Map<String, String[]> map = null;
		ExpedienteManager em = null;
		List<Carpeta> foldersOr = null;
		List<Documento> documentsOr = null;
		List<Pagina> pagesOr = null;
		DocumentoManager dm = null;
		CarpetaManager cm = null;

		try {
			if (validaExpediente(exOriginal)) {

				cm = new CarpetaManager();
				foldersOr = cm.getExpedientFolders(exOriginal.getTituloAplicacion(),
						exOriginal.getIdGainete(),
						false);

				dm = new DocumentoManager();
				documentsOr = dm.selectDocumentosExpediente(
						exOriginal.getTituloAplicacion(),
						exOriginal.getIdGainete());

				pagesOr = PaginaManager.getPaginas(exOriginal.getTituloAplicacion(),
						exOriginal.getIdGainete());
				// ========== ========== ========== ========== ==========
				// == Crea un expediente con estructura vacia para insertar las
				// == referencias a las imagenes
				// ========== ========== ========== ========== ==========

				map = ExpedienteManager.expedientToMap(exCopy);
				em = new ExpedienteManager();
				
				String nombre_raiz = getNombreCarpetaRaiz(exOriginal.getTituloAplicacion(), exCopy);
						
				String[] result = em.insertExpediente(
						exCopy.getTituloAplicacion(), access,
						nombre_raiz, map, false,
						false);

				if (result[1] != null)
					throw new FortimaxException("UNKNOW",
							"Se intento clonar el expediente con el campo existente["
									+ result[1] + "] etiquetado como UNICO");

				int newIdGabinete = Integer.parseInt(result[0]);

				// ========== ========== ========== ========== ==========
				// == Inserta las Carpetas y la jerarquia de Carpetas en el
				// expediente destino
				// ========== ========== ========== ========== ==========
				log.debug("Se clonaran " + foldersOr.size() + " carpetas");
				for (Iterator<?> i = foldersOr.iterator(); i.hasNext();) {
					Carpeta cTmp = (Carpeta) i.next();

					cTmp.setTituloAplicacion(exCopy.getTituloAplicacion());
					cTmp.setIdGabinete(newIdGabinete);

					cm.insertSingleFolder(cTmp);
				}

				log.debug("Se clonaran " + documentsOr.size() + " documentos");
				for (Iterator<Documento> i = documentsOr.iterator(); i
						.hasNext();) {
					Documento d = i.next();
					d.setTituloAplicacion(exCopy.getTituloAplicacion());
					d.setIdGabinete(newIdGabinete);

					dm.insertDocumento(d);

				}

				log.debug("Se clonaran " + pagesOr.size() + " paginas");
				for (Iterator<Pagina> i = pagesOr.iterator(); i.hasNext();) {
					Pagina p = i.next();
					p.setTitulo_aplicacion(exCopy.getTituloAplicacion());
					p.setId_gabinete(newIdGabinete);
					PaginaManager.clonePage(p, total);
				}

			}

		} catch (FortimaxException fe) {
			throw fe;
		} catch (Exception e) {
			throw new FortimaxException("UNKNOW", e.toString());
		}

		return ok;
	}
	
	public static String getNombreCarpetaRaiz(String titulo_aplicacion, Expediente nuevoExp){
		Descripcion[] desc = new DescripcionManager().selectDescripcion(titulo_aplicacion);

		String campo_raiz = null;
		
		for (Descripcion descripcion : desc) {
			if(descripcion.getIndiceTipo()==2){
				campo_raiz = descripcion.getNombreCampo();
				break;
			}
		}
		
		if(campo_raiz==null){
			for (Descripcion descripcion : desc) {
				if(descripcion.getPosicionCampo()==1){
					campo_raiz = descripcion.getNombreCampo();
					break;
				}
			}
		}
		
		return (String) nuevoExp.get(campo_raiz);
	}

	public static ArrayList<Expediente> getExpedients(Session sess, String drawer, String[] fields, String[] values) {

		ArrayList<Expediente> exp = new ArrayList<Expediente>();
		
		ArrayList<NotMappedCondition> condicionesCamposGaveta = new ArrayList<NotMappedCondition>();
		for(int i=0; i<fields.length; i++) {
			condicionesCamposGaveta.add(new NotMappedCondition(fields[i],"=",values[i]));
		}

		List<Object[]> rows = NotMappedManager.get(sess, "imx" + drawer, null, condicionesCamposGaveta);

		Descripcion[] desc = new DescripcionManager().selectDescripcion(drawer);

		for (Object[] row : rows) {

			Expediente ex = new Expediente(drawer, Integer.parseInt(row[0]
					.toString()), row[1].toString());
			for (int i = 0; i < desc.length && i < row.length + 5; i++) {
				ex.put(desc[i].getNombreCampo(), row[i + 5]);
			}
			exp.add(ex);
		}

		return exp;
	}

	/**
	 * Validación de información de expedientes y devuelve un mapa
	 * con la descripción y valores del expediente. Valida que el
	 * expediente cuente con la información minima requerida (Definida en
	 * <i>IMX_DESCRIPCION</i> en el campo <i>REQUERIDO</i>) y que al menos
	 * contenga la carpeta ra&iacute;z.
	 *
	 * @param connection
	 *            Conexion activa a base de datos. Es responsabilidad del
	 *            llamante cerrar la conexion. Este metodo no lo hace.
	 * @param exp
	 *            Expediente a validar
	 * @return <code>true</code> Si y solo si el expediente cuenta con valores
	 *         para cada campo requerido y cuenta con una carpeta ra&iacute;z.
	 *
	 *
	 */
	private static boolean validaExpediente(Expediente exp)
			throws CarpetaManagerException {

		DescripcionManager dm = null;
		CarpetaManager cm = null;
		Descripcion desc[] = null;
		Carpeta carpetaRaiz = null;

		dm = new DescripcionManager();

		desc = dm.selectDescripcion(exp.getTituloAplicacion());

		// Itera sobre los campos de descripcion de la gaveta. Si el campo
		// es obligatorio, valida que el expediente contenga esa informacion.
		for (int i = 0; i < desc.length; i++) {

			String name = desc[i].getNombreCampoLower();
			String value = (String) exp.get(name.toUpperCase());

			if (desc[i].getRequeridoBoolean()
					&& (value == null || "".equals(value)))
				return false;
		}

		// Valida que al menos tenga carpeta raiz.
		carpetaRaiz = new Carpeta(exp.getTituloAplicacion(), exp.getIdGainete(), 0);

		cm = new CarpetaManager();
		carpetaRaiz = cm.selectCarpeta(carpetaRaiz);

		return carpetaRaiz != null
				&& "S".equalsIgnoreCase(carpetaRaiz.getBanderaRaiz());
	}

	/**
	 * Regresa un Expediente en un mapa, como se requiere para su insercion.
	 *
	 * @param exp
	 *            Expediente a transformar
	 * @return Map String,String[] Con los elementos del expediente.
	 */
	public static Map<String, String[]> expedientToMap(Expediente exp) throws FortimaxException {
		Descripcion[] dsc = null;
		DescripcionManager dm = null;
		Map<String, String[]> map = null;

		try {
			dm = new DescripcionManager();
			dsc = dm.selectDescripcion(exp.getTituloAplicacion());

			if (dsc != null && dsc.length > 0) {

				map = new HashMap<String, String[]>();

				for (int i = 0; i < dsc.length; i++) {

					String name = dsc[i].getNombreCampoLower();
					String value = (String) exp.get(name.toUpperCase());

					if (dsc[i].getRequeridoBoolean()
							&& (value == null || "".equals(value)))
						throw new FortimaxException("FMX-EXP-WS-1013",
								"Faltan campos que son requeridos: " + name);

					String[] values = {
							String.valueOf(dsc[i].getIdTipoDatos()),
							String.valueOf(dsc[i].getIndiceTipo()),
							((value == null) ? "" : value),
							dsc[i].getNombreColumna() };

					map.put(name, values);
				}
			} else
				throw new FortimaxException("FMX-EXP-WS-1011",
						"No se pudo obtener la descripci�n de la gaveta destino de la clonacion.");
			return map;
		} finally {
			dsc = null;
			dm = null;
		}
	}
	/*
	 * Funcione para busqueda de expedientes con interfaz Extjs
	 */
	static class nombreValor{
		private String nombre;
		private String valor;	
		private String tipo;
		private nombreValor(){
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		public String getTipo() {
			return tipo;
		}
		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
		
	}
	private String generaQuerySelect(String[] campos, String[] valores) {
		String hquery = "";
		Boolean first = true;
		String condicional =" WHERE ";
		for(int i=0;i<campos.length;i++) {
			if(!first)
				condicional=" AND ";
			else
				first = false;
			hquery += condicional;
			hquery += campos[i]+" LIKE '"+valores[i]+"'";
		}
		return hquery;
	}
	
	
	public String generaQuerySelect(String jsonBusqueda){
		if(jsonBusqueda==null||"".equals(jsonBusqueda))
			return "";
		try{
		String hquery="";
		Boolean f=true;
		Map<String, Object> map = Json.getMap(jsonBusqueda);
		String condicional=" WHERE ";
		for(String key : map.keySet()) {
			String value = map.get(key).toString();
			if(!value.isEmpty()) {
				if(!f){
					condicional=" AND ";
				}
				else{
					condicional=" WHERE ";
					f=false;
				}
					hquery+=condicional;
					hquery+=key+" LIKE '"+value+"'";
			}
		}
		return hquery;
		}
		catch(Exception e){
			log.error(e,e);
			return "";
		}
	}
	
	private String generaQueryLiveSearch(String aplicacion, String jsonBusqueda, String liveSearch) {
		if(liveSearch==null||"".equals(liveSearch))
			return "";

		String query = " AND (";
		if(jsonBusqueda==null||"".equals(jsonBusqueda)||"{}".equals(jsonBusqueda))
			query = "WHERE (";
		
		HibernateManager hm = new HibernateManager();
		String camposQuery= "FROM imx_descripcion WHERE TITULO_APLICACION = :titulo_aplicacion";
		Query hqlQuery = hm.createQuery(camposQuery);
		hqlQuery.setString("titulo_aplicacion",aplicacion);
		
		@SuppressWarnings("unchecked")
		List<imx_descripcion> campos = hqlQuery.list();
		if (campos.isEmpty())
			return "";
		
		for(imx_descripcion campo : campos) {
			query+="LOWER("+campo.getId().getNombreCampo()+") LIKE '%"+liveSearch.toLowerCase()+"%' OR ";
		}
		
		query = query.substring(0, query.length()-4)+")";
		return query;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String,Object> selectExpediente(String titulo_aplicacion, int id_gabinete){
			String hquery="SELECT * FROM IMX"+titulo_aplicacion+" WHERE id_gabinete="+id_gabinete;
			
				Session session=HibernateUtils.getSession();
				Query query=session.createSQLQuery(hquery);
				query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
				Object object = query.uniqueResult();
				return (HashMap<String,Object>)object;
	}
	
	@SuppressWarnings({ "unchecked"})
	public List<HashMap<String,Object>> getExpedientes(String aplicacion, String jsonBusqueda, String liveSearch, Integer start, Integer limit){
		try{
			String hquery="SELECT * FROM IMX"+aplicacion+" "+generaQuerySelect(jsonBusqueda)+" "+generaQueryLiveSearch(aplicacion,jsonBusqueda,liveSearch)+" ORDER BY ID_GABINETE DESC";
			
				Session session=HibernateUtils.getSession();
				Query query=session.createSQLQuery(hquery);
				if(start!=null)
					query.setFirstResult(start);
				if(limit!=null)
					query.setMaxResults(limit);
				query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
				List<HashMap<String,Object>> lista=query.list();
				return lista;
	
		}
		catch(Exception e){
			log.error(e.toString());
			return null;
		}
	}
	
	public List<Map<String,Object>> getExpedientes(String gaveta, String[] campos, String[] valores) {
		Session session = HibernateUtils.getSession();
		try{
			String hquery="SELECT * FROM IMX"+gaveta+" "+generaQuerySelect(campos,valores)+" ORDER BY ID_GABINETE";
			Query query=session.createSQLQuery(hquery);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> lista=query.list();
			return lista;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public int getTotalExpedientes(String aplicacion,String jsonBusqueda, String liveSearch){
		int result=0;
		try{
			String hquery="SELECT COUNT(*) FROM IMX"+aplicacion+" "+generaQuerySelect(jsonBusqueda)+" "+generaQueryLiveSearch(aplicacion,jsonBusqueda,liveSearch);
			
			Session session=HibernateUtils.getSession();
			Query query=session.createSQLQuery(hquery);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List<Object> lista=query.list();
			Map<String,Object> map= new HashMap<String,Object>();
			for(Object o:lista){
				map= (Map<String,Object>)o;
				String c=(String)map.get("COUNT(*)").toString();
				result=Integer.parseInt(c);
			}
			
			
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return result;
	}

	public Map<String, String[]> creaMapaParametros(String aplicacion,Map<String, Object> mapaCampos){
			Map<String, String[]> mapaParametros = new HashMap<String,String[]>();
			DescripcionManager dm = new DescripcionManager();
			Descripcion[] dsc = dm.selectDescripcion(aplicacion);
			for (int i = 0;i < dsc.length; i++) {
				String value = "";
				String name = dsc[i].getNombreCampoLower();
				if ( dsc[i].getIdTipoDatos()==8 ) { //Si es fecha
					value = mapaCampos.get(dsc[i].getNombreCampo()).toString();
				} else {
					value = mapaCampos.get(dsc[i].getNombreCampo()).toString();
				}
				String[] values = {
					String.valueOf(dsc[i].getIdTipoDatos()),
					String.valueOf(dsc[i].getIndiceTipo()),
					((value == null) ? "" : value),
					dsc[i].getNombreColumna()
				};
				mapaParametros.put(name, values);
			}
		return mapaParametros;
	}
	public String getNombreCarpeta(Map<String, Object> mapaCampos){
		for (Entry<String, Object> entry : mapaCampos.entrySet()) {
			if(!entry.getKey().toUpperCase().equals("ID_GABINETE"))
				if(entry.getValue()!=null) {
					String value = entry.getValue().toString();
					if(!value.isEmpty())
						return value;
				}
		}
		return "";
	}
	public Boolean updateExpedientes(String aplicacion, int idGabinete,Map<String,Object> valores){
		Boolean result=false;
		log.info("Editando expediente "+aplicacion+"_"+idGabinete+" "+valores);
		try{
			HibernateManager hm=new HibernateManager();
			hm.setCriterion(Restrictions.eq("id.tituloAplicacion", aplicacion));
			@SuppressWarnings("unchecked")
			List<imx_descripcion>  descripcion=(List<imx_descripcion>) hm.list(imx_descripcion.class);
			String cambiosQuery=" SET ";
			for(int i=0;i<descripcion.size();i++){
				imx_descripcion des=descripcion.get(i);
				if(i>0)
					cambiosQuery+=", ";
				
				Object value = valores.get(des.getId().getNombreCampo());
				if(value==null||"".equals(value)) {
					cambiosQuery+=des.getId().getNombreCampo()+"=NULL";
				} else switch (descripcion.get(i).getIdTipoDatos()){
					case 4:
						//Entero
						cambiosQuery+=des.getId().getNombreCampo()+"="+value;
						break;
					case 5:
						//Entro
						cambiosQuery+=des.getId().getNombreCampo()+"="+value;
						break;
					case 7:
						//Doble
						cambiosQuery+=des.getId().getNombreCampo()+"="+value;
						break;
					case 8:
						//Fecha
						if(Config.database.equals(Config.Database.ORACLE))//TODO:Crear este query con ANSI-SQL
							value="TO_DATE('"+value+"','YYYY-MM-DD')";
						else 
							value="'"+value+"'";
						cambiosQuery+=des.getId().getNombreCampo()+"="+value;						
						break;
					default:
						cambiosQuery+=des.getId().getNombreCampo()+"='"+value+"'";
						break;
				}
			}
			 cambiosQuery="UPDATE IMX"+aplicacion+cambiosQuery+" WHERE ID_GABINETE="+String.valueOf(idGabinete);
			 hm.executeSQLQuery(cambiosQuery);
			 result=true;
		}
		catch(Exception e){
			log.error(e,e);
		}
		return result;
	}
	
	public static int createExpediente(String gaveta,
			ExpedienteCampo[] exp) throws Exception {
		int id = -1;

		HibernateManager hm = new HibernateManager();
		try{
			StringBuilder stringQuery = new StringBuilder();
			stringQuery.append("INSERT INTO imx");
			stringQuery.append(gaveta.toLowerCase());
			stringQuery.append(" (id_gabinete,");
			for (int i = 0; i < exp.length; i++) {
				if (exp[i] != null && StringUtils.isNotBlank(exp[i].getValue())) {
					stringQuery.append(exp[i].getColumnName());
					if (i + 1 < exp.length && exp[i + 1] != null) {
						stringQuery.append(",");
					} else {
						stringQuery.append(")");
					}
				}
			}
			stringQuery.append(" VALUES ");
			stringQuery.append(" (");
			ExpedienteManager em = new ExpedienteManager();
			int tmp_newid = em.getNewIdGabinete(gaveta);
			stringQuery.append(tmp_newid);
			stringQuery.append(",'");
			for (int i = 0; i < exp.length; i++) {
				if (exp[i] != null && StringUtils.isNotBlank(exp[i].getValue())) {
					stringQuery.append(exp[i].getValue());
					if (i + 1 < exp.length && exp[i + 1] != null) {
						stringQuery.append("','");
					} else {
						stringQuery.append("')");
					}
				}
			}
			
			Query queryInsert = hm.createSQLQuery(stringQuery.toString());
			hm.executeQuery(queryInsert);
			id = tmp_newid;
		
		} finally {
			hm.close();
		}

		// TODO revisar esto de abajito
		StringBuffer carpeta_raiz = new StringBuffer();
		for (int i = 0; i < exp.length; i++) {
			if (exp[i].isPrimaryKey()) {
				carpeta_raiz.append(exp[i].getValue());
				if (carpeta_raiz.length() >= 2) {
					break;
				}
			}
		}

		//no crea estructura si carpeta_raiz va nulo, es decir, cuando
		//exp no tiene primary keys
		//tampoco crea estructura en oracle, pues no lleva nombre de usuario (campo no nulo en imx_carpeta)
		creaEstructuraExpediente(gaveta, id, "", carpeta_raiz.toString());

		return id;
	}

}
