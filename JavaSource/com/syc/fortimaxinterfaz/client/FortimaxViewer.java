package com.syc.fortimaxinterfaz.client;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.utils.Utils;

public class FortimaxViewer {

	private static final Logger log = Logger.getLogger(FortimaxViewer.class);

	// Atributos de configuracion
	private String urlFortimax;

	// private String contexto;
	private String servletName;
	private String paramName;
	private String fimaxUser;
	private String fimaxCDG;
	private boolean CDGEnc;
	private boolean lecturaEscritura = false;
	private String gaveta;

	// CONSTANTES
	private final int VALOR_INEXISTENTE = -1;
	private final long EQUIVALENCIA_SEG_MS = 1000;

	// valores para uso interno
	private int id_carpeta_padre = VALOR_INEXISTENTE;
	private int id_documento = VALOR_INEXISTENTE;
	private int id_gabinete = VALOR_INEXISTENTE;

	/**
	 * Crea una nueva instancia del generador de ligas. Utiliza el valor pre
	 * configurado para los parametros: <code>urlFortimax</code>,
	 * <code>servletName</code> y <code>paramName</code>
	 * 
	 * @param user
	 *            Usuario Fortimax valido y con permisos al menos de lectura
	 *            sobre la gaveta
	 * @param password
	 *            Password del usuario
	 * @param encrypted
	 *            Indica si el password proporcionado se encuentra cifrado.
	 * @param Gaveta
	 *            Aplicación a la que se accederá.
	 * @param lecturaEscritura
	 *            Indica si la interface sera de Lectura/Escritura (
	 *            <code>true</code>) o solo lectura (<code>false</code>).
	 * 
	 * @throws FortimaxException
	 *             si existe alg&uacute;n error de comunicación con la
	 *             base de datos.
	 */
	public FortimaxViewer(String user, String password, boolean encrypted,
			String Gaveta, boolean lecturaEscritura) throws FortimaxException {

		this.urlFortimax = Config.urlFortimax;
		this.servletName = "ifimax";
		this.paramName = "ifimax_token";

		this.fimaxUser = user;
		this.fimaxCDG = password;
		this.CDGEnc = encrypted;
		this.gaveta = Gaveta;

		this.lecturaEscritura = lecturaEscritura;
		IndicesTabla_array = obtieneNombreIndices();

		if (IndicesTabla_array == null) {
			log.warn("No se consiguieron nombre de campos mandatorios de la aplicaci\u00F3n. Tendr\u00E1 que asignar uno manualmente");
		} else
			campoMandatorio = IndicesTabla_array[0];
		try {
			Cipher.getInstance("DESede");
		} catch (NoSuchAlgorithmException nsaex) {
			log.error(nsaex, nsaex);
			log.error("Instalando proveedor SunJCE.");
			//Provider sunjce = new com.sun.crypto.provider.SunJCE();
			//Security.addProvider(sunjce);
		} catch (NoSuchPaddingException nspex) {
			log.error(nspex, nspex);
			log.error("Instalando proveedor SunJCE.");
			//Provider sunjce = new com.sun.crypto.provider.SunJCE();
			//Security.addProvider(sunjce);
		}

	}

	/**
	 * Crea una nueva instancia del generador de ligas. Utiliza el valor pre
	 * configurado para los parametros: <code>urlFortimax</code>,
	 * <code>servletName</code> y <code>paramName</code>
	 * 
	 * @param user
	 *            Usuario Fortimax valido y con permisos al menos de lectura
	 *            sobre la gaveta
	 * @param password
	 *            Password del usuario
	 * @param encrypted
	 *            Indica si el password proporcionado se encuentra cifrado.
	 * @param Gaveta
	 *            Aplicación a la que se accederá.
	 * @throws FortimaxException
	 *             si existe alg&uacute;n error de comunicación con la
	 *             base de datos.
	 */
	public FortimaxViewer(String user, String password, boolean encrypted,
			String Gaveta) throws FortimaxException {

		this.urlFortimax = Config.urlFortimax;
		this.servletName = "ifimax";
		this.paramName = "view";

		this.fimaxUser = user;
		this.fimaxCDG = password;
		this.CDGEnc = encrypted;
		this.gaveta = Gaveta;

		this.lecturaEscritura = false;
		IndicesTabla_array = obtieneNombreIndices();

		if (IndicesTabla_array == null) {
			log.warn("No se consiguieron nombre de campos mandatorios de la aplicaci\u00F3n. Tendr\u00E1 que asignar uno manualmente");
		} else
			campoMandatorio = IndicesTabla_array[0];
		try {
			Cipher.getInstance("DESede");
		} catch (NoSuchAlgorithmException nsaex) {
			log.error(nsaex, nsaex);
			log.error("Instalando proveedor SunJCE.");
			//Provider sunjce = new com.sun.crypto.provider.SunJCE();
			//Security.addProvider(sunjce);
		} catch (NoSuchPaddingException nspex) {
			log.error(nspex, nspex);
			log.error("Instalando proveedor SunJCE.");
			//Provider sunjce = new com.sun.crypto.provider.SunJCE();
			//Security.addProvider(sunjce);
		}

	}

	/**
	 * Procesa los parametros enviados generando asi una liga que despliegue el
	 * visor de documentos mostrando el documento (cheque) especificado en los
	 * parametros.
	 * 
	 * @param expediente
	 *            Numero de cuenta a la que perenece el documento (cheque)
	 * @param nombreDocumento
	 *            Numero de cheque que se desea visualizar.
	 * @param segundosVigencia
	 *            Tiempo de vigencia de la liga.
	 * @return String, liga que al colocarse en un navegador web muestra el
	 *         visualizador de documentos con el documento (cheque) deseado.
	 * @throws Exception
	 */
	public String makeView(String expediente, String nombreDocumento,
			long segundosVigencia) throws FortimaxException, Exception {

		String url_str = null;
		if (ArrayUtils.isEmpty(IndicesTabla_array)) {
			throw new FortimaxException("FMX-VIE-1001",
					"No hay campo para realizar busqueda por Expediente.");
		}
		id_gabinete = obtieneIdGabinete(expediente);
		if (id_gabinete == VALOR_INEXISTENTE) {
			throw new FortimaxException("FMX-VIE-1002",
					"No existe el Expediente (" + expediente + ").");
		}
		obtieneIdsDocumento(nombreDocumento, id_gabinete);
		if (id_documento == VALOR_INEXISTENTE) {
			throw new FortimaxException("FMX-VIE-1003",
					"No existe el Documento (" + nombreDocumento + ").");
		}
		if (id_carpeta_padre == VALOR_INEXISTENTE) {
			throw new FortimaxException("FMX-VIE-1004",
					"No se encontro ninguna carpeta padre. ("
							+ id_carpeta_padre + ").");
		}
		url_str = generaRuta(id_gabinete, id_carpeta_padre, id_documento,
				segundosVigencia);

		return url_str;
	}
	
	/**
	 * Procesa los parametros enviados generando asi una liga que despliegue el
	 * visor de documentos mostrando el documento (cheque) especificado en los
	 * parametros.
	 * 
	 * @param gdn
	 *            GetDatosNodo del nodo
	 * @param segundosVigencia
	 *            Tiempo de vigencia de la liga.
	 * @return String, liga que al colocarse en un navegador web muestra el
	 *         visualizador de documentos con el documento (cheque) deseado.
	 * @throws Exception
	 */
	public String makeView(GetDatosNodo gdn, long segundosVigencia) throws FortimaxException, Exception {
		gdn.separaDatosDocumento();
		id_gabinete = gdn.getGabinete();
		id_carpeta_padre = gdn.getIdCarpeta();
		id_documento = gdn.getIdDocumento();
		String url_str = generaRuta(id_gabinete, id_carpeta_padre, id_documento,segundosVigencia);
		return url_str;
	}

	/**
	 * Procesa los parámetros enviados generando as&iacute; una liga que
	 * despliegue el visor de documentos.
	 * 
	 * @param expediente
	 *            Expediente a mostrar.
	 * @param segundosVigencia
	 *            Vigencia de la liga.
	 * @return Liga a Fortimax&copy;
	 * @throws FortimaxException
	 *             En caso de error relacionado a Fortimax&copy;.
	 * @throws Exception
	 *             Error en general.
	 */
	public String makeView(String expediente, long segundosVigencia) throws FortimaxException, Exception {

		String url_str = null;
		if (ArrayUtils.isEmpty(IndicesTabla_array)) {
			throw new FortimaxException("FMX-VIE-1001",
					"No hay campo para realizar busqueda por Expediente.");
		}

		id_gabinete = obtieneIdGabinete(expediente);

		if (id_gabinete == VALOR_INEXISTENTE) {
			throw new FortimaxException("FMX-VIE-1002",
					"No existe el Expediente (" + expediente + ").");
		}

		url_str = generaRuta(id_gabinete, segundosVigencia);

		return url_str;
	}

	@SuppressWarnings("unchecked")
	private void obtieneIdsDocumento(String nombreDocumento, int id_gabinete) {
		HibernateManager hm = new HibernateManager();
		try{
			String query = "select id_documento, id_carpeta_padre from imx_documento where id_gabinete="
					+ id_gabinete
					+ " and titulo_aplicacion='"
					+ gaveta
					+ "' and nombre_documento='" + nombreDocumento + "'";
			Query sqlQuery = hm.createSQLQuery(query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

			Map<String,Object> ids = (Map<String,Object>)sqlQuery.list().get(0);
			ids = new CaseInsensitiveMap(ids);

			id_documento = Utils.getInteger(ids.get("ID_DOCUMENTO"));
			id_carpeta_padre = Utils.getInteger(ids.get("ID_CARPETA_PADRE"));
			
		} catch (Exception e){
			log.error(e, e);
		} finally {
			hm.close();
		}
	}

	public int obtieneIdGabinete(String expediente) {
		int id_gabinete = VALOR_INEXISTENTE;

		HibernateManager hm = new HibernateManager();
		try {
			StringBuilder query = new StringBuilder("select ID_GABINETE from imx" + gaveta + " where ");

			String campos[] = StringUtils.split(expediente, Config.separator);
			if (campos.length == 1) {
				query.append(campoMandatorio + "='" + expediente + "'");
			} else {
				for (int i = 0; i < campos.length
						&& i < IndicesTabla_array.length; i++) {
					query.append(IndicesTabla_array[i] + "='" + campos[i] + "'");
					if (i + 1 < campos.length
							&& i + 1 < IndicesTabla_array.length) {
						query.append("  AND ");
					}
				}
			}

			id_gabinete = ((BigDecimal)hm.createSQLQuery(query.toString()).uniqueResult()).intValue();

		} catch (Exception e) {
			log.error(e,e);
		} finally {
			hm.close();
		}

		return id_gabinete;
	}

	private String[] obtieneNombreIndices() throws FortimaxException {
		
		HibernateManager hm = new HibernateManager();
		try {
			String query = "select nombre_campo from imx_descripcion where titulo_aplicacion='"
					+ gaveta + "' order by POSICION_CAMPO";

			@SuppressWarnings("unchecked")
			List<String> camposDeGaveta = (List<String>)hm.createSQLQuery(query).list();
			
			return camposDeGaveta.toArray(new String[0]);
			
		} catch(Exception e){
			log.error(e, e);
			throw new FortimaxException(e);
		} finally {
			hm.close();
		}
	}

	private String campoMandatorio = null;
	private String[] IndicesTabla_array = null;

	private String generaRuta(int id_gabinete, int id_carpeta_padre, int id_documento, long segundosVigencia) throws FortimaxException {
		String ruta_str = null;
		String parametrosReales = null;
		String parametrosEncriptados = null;
		String b64Key = null;
		String b64UrlParams = null;

		parametrosReales = unirParametrosRuta(id_gabinete, id_documento,
				id_carpeta_padre, segundosVigencia);
		try {
			SecretKey key = generateKey();
			b64UrlParams = encrypt(key, parametrosReales);
			b64Key = DatatypeConverter.printBase64Binary(key.getEncoded());
			parametrosEncriptados = URLEncoder.encode(b64Key + ":="
					+ b64UrlParams, "UTF-8");
		} catch (Exception exc) {
			log.error(exc, exc);

			throw new FortimaxException(exc);
		}

		if (canMakeURL()) {
			ruta_str = this.urlFortimax + this.servletName + "?"
					+ this.paramName + "=" + parametrosEncriptados;
		}
		return ruta_str;
	}

	/**
	 * Genera la ruta para acceder al visor de Fortimax&copy;
	 * 
	 * @param id_gabinete
	 *            ID Gabinete del expediente a visualizar.
	 * @param segundosVigencia
	 *            Duración de la liga.
	 * @return Ruta de acceso al visor de Fortimax&copy;
	 * @throws FortimaxException
	 *             en caso de error.
	 */
	private String generaRuta(int id_gabinete, long segundosVigencia)
			throws FortimaxException {

		String ruta_str = null;
		String parametrosReales = null;
		String parametrosEncriptados = null;
		String b64Key = null;
		String b64UrlParams = null;

		parametrosReales = unirParametrosRuta(id_gabinete, segundosVigencia);

		try {
			SecretKey key = generateKey();
			b64UrlParams = encrypt(key, parametrosReales);
			b64Key = DatatypeConverter.printBase64Binary(key.getEncoded());
			parametrosEncriptados = URLEncoder.encode(b64Key + ":="
					+ b64UrlParams, "UTF-8");
		} catch (Exception exc) {
			log.error(exc, exc);

			throw new FortimaxException(exc);
		}

		if (canMakeURL()) {
			ruta_str = this.urlFortimax + this.servletName + "?"
					+ this.paramName + "=" + parametrosEncriptados;
		}
		return ruta_str;
	}

	private String unirParametrosRuta(int id_gabinete, long segundosVigencia) {

		StringBuffer params_sb = new StringBuffer();
		GetDatosNodo datosNodo = new GetDatosNodo(this.gaveta, id_gabinete, 0);
		datosNodo.creaNodo();
		params_sb.append("nodo=" + datosNodo.getNodo());
		params_sb.append("&CDGEnc=" + CDGEnc);
		params_sb.append("&txtUsuario=" + fimaxUser);
		params_sb.append("&txtClave=" + fimaxCDG);

		long tiempoCrecion = System.currentTimeMillis();
		long tiempoExpiracion = segundosVigencia * EQUIVALENCIA_SEG_MS;

		params_sb.append("&tiempoCreacion=" + tiempoCrecion);
		params_sb.append("&tiempoExpiracion=" + tiempoExpiracion);
		params_sb.append("&rw=" + lecturaEscritura);

		return params_sb.toString();
	}

	private String unirParametrosRuta(int id_gabinete, int id_documento,
			int id_carpeta_padre, long segundosVigencia) {

		StringBuffer params_sb = new StringBuffer();
		GetDatosNodo datosNodo = new GetDatosNodo(this.gaveta, id_gabinete, id_carpeta_padre, id_documento);
		datosNodo.creaNodo();
		params_sb.append("nodo="+ datosNodo.getNodo());
		params_sb.append("&CDGEnc=" + CDGEnc);
		params_sb.append("&txtUsuario=" + fimaxUser);
		params_sb.append("&txtClave=" + fimaxCDG);

		long tiempoCrecion = System.currentTimeMillis();
		long tiempoExpiracion = segundosVigencia * EQUIVALENCIA_SEG_MS;

		params_sb.append("&tiempoCreacion=" + tiempoCrecion);
		params_sb.append("&tiempoExpiracion=" + tiempoExpiracion);
		params_sb.append("&rw=" + lecturaEscritura);
		return params_sb.toString();
	}

	public String URLXZIP(String Matricula, String Archivo, String user)
			throws FortimaxException {
		String Url_zip = null;
		String URLParametros = null;
		String Parametros = null;

		String b64Key = null;
		String b64UrlParams = null;

		Url_zip = "select=" + Matricula + "&user=" + user;

		try {
			SecretKey key = generateKey();
			b64UrlParams = encrypt(key, Url_zip);
			b64Key = DatatypeConverter.printBase64Binary(key.getEncoded());
			Parametros = URLEncoder.encode(b64Key + ":=" + b64UrlParams,
					"UTF-8");
		} catch (Exception exc) {
			log.error(exc, exc);

			throw new FortimaxException(exc);
		}

		URLParametros = this.urlFortimax + "getfolder?token=" + Parametros;

		return URLParametros;

	}

	/**
	 * Genera la llave privada.
	 * 
	 * @return SecrectKey
	 * @throws java.security.NoSuchAlgorithmException
	 *             si el algoritmo no es soportado por la version de la JVM.
	 */
	private SecretKey generateKey() throws NoSuchAlgorithmException {
		// danny no sabe que es "DESede" =S, por eso no le movio
		return KeyGenerator.getInstance("DESede").generateKey();
	}

	/**
	 * Encripta la informacion contenida en el parametro <code>data</code>.
	 * 
	 * @param key
	 *            Llave privada para generar el encriptamiento.
	 * @param data
	 *            Datos a encriptar.
	 * @return Cadena resultado de aplicar el algoritmo de encriptamiento a la
	 *         cadena <code>data</code>
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.NoSuchPaddingException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	private String encrypt(SecretKey key, String data)
			throws NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, BadPaddingException,
			IllegalBlockSizeException {

		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return DatatypeConverter.printBase64Binary(cipher.doFinal(data.getBytes()));
	}

	private boolean canMakeURL() {
		return ((urlFortimax != null && urlFortimax.length() > 0)
		// && (contexto != null && contexto.length() > 0)
				&& (servletName != null && servletName.length() > 0) && (paramName != null && paramName
				.length() > 0));
	}
}
