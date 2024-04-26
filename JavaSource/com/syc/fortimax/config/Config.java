package com.syc.fortimax.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.syc.fortimax.ldap.ActiveDirectoryConfigs;
import com.syc.utils.Json;
import com.syc.utils.Utils;

public abstract class Config {
	private static final Logger log = Logger.getLogger(Config.class);
	private static final Element config;
	
	public static Database database = getDataBase();
	
	//La siguiente linea se ocupa si el JRE no tiene a la librería nativa libxtst6:i386, otras forma de corregirlo es correr el servidor con -Djava.awt.headless=true
	@SuppressWarnings("unused")
	private static final String javaAwtHeadless = System.setProperty("java.awt.headless", "true");
	public static final Map<String, String> mimeTypes = getMimeTypes();
	public static final List<String> imaxExtensions = getImaxExtensions(mimeTypes);
	
	public enum Database {ORACLE, MYSQL, MSSQL, ANSISQL};

	public static final String cofigsPaths = getXMLConfigElem("General",
			"CofigsPaths", null, "/");
	public static final String log4jPath = getXMLConfigElem("General",
			"Log4jPath", null, "/");
	public static final String hibernatePath = getXMLConfigElem("General",
			"HibernatePath", null, "/");

	static {
		config = loadConfig();
	}

	public static final String workPath = getXMLConfigElem("WebServices", null,
			"workdir", ".");
	public static final ActiveDirectoryConfigs activeDirectoryConfigs = initActiveDirectoryCongs();

	public static final char separator = getXMLConfigElem("General",
			"Separator", null, "|").toCharArray()[0];
	public static final String urlFortimax = getXMLConfigElem("General",
			"UrlFortimax", null, "http://localhost:8080/Fortimax/");
	private static final String luceneIndex = getXMLConfigElem("General",
			"LuceneIndex", null, null);
	public static final boolean deleteFromVolumen = "true".equals(getXMLConfigElem("General",
			"DeleteFromVolumen", null, "false"));

	// Variables Procesamiento OCR
	// VGC La configuracion de Tesseract se define en el XML. Se definen valores
	// DEFAULT;
	public static final boolean ActivarOCR = Boolean
			.parseBoolean(getXMLConfigElem("OCR", null, "activarOCR", "true"));
	public static final String LANG_OPTION = getXMLConfigElem("OCR",
			"LangOption", null, "-1");
	public static final String LANG = getXMLConfigElem("OCR", "Lang", null,
			"spa");
	// ESTA RUTA DEBE TERMINAR CON /
	public static final String tessPath = getXMLConfigElem("OCR", "TessPath",
			null, "/tesseract3/");
	
	public static final String fortimaxPath = getInstallationPath();
	public final static String PREFIX_OCR = getXMLConfigElem("OCR",
			"PrefixOCR", null, "FMX_OCR_");
	public final static String FILE_EXTENSION_OCR = getXMLConfigElem("OCR",
			"FileExtensionOCR", null, ".txt");
	public final static boolean BORRAR_IMG_TEMP_OCR = Boolean
			.parseBoolean(getXMLConfigElem("OCR", "BorrarImgTempOCR", null,
					"true"));
	public final static boolean FIT_IMAGE = Boolean
			.parseBoolean(getXMLConfigElem("General", "FitImage", null, null));

	public static final String EOL = System.getProperty("line.separator");

	public static final boolean WINDOWS = System.getProperty("os.name")
			.toLowerCase().startsWith("windows");
	public static final String UTF8 = "UTF-8";
	// Si esta en false delega la conversion a tesseract, si es true la realiza
	// java.
	// En windows hay buenos resultados con true y tesseract 3.0 con lib
	// leptonica.

	public static final boolean ConverttoGray = Boolean
			.parseBoolean(getXMLConfigElem("OCR", "ConvertToGray", null, "true"));

	// Inicio Parametros de ejecucion AutomaticProcessListener
	public static final long START = Long.parseLong(getXMLConfigElem("OCR",
			"Start", null, "100000000000000000")); // En segundos
	public static final long END = Long.parseLong(getXMLConfigElem("OCR",
			"End", null, String.valueOf(5 * 60 * 1000L))); // Cada 5 minuto para
															// pruebas
	public static final int CANT_REG_PROC = Integer.parseInt(getXMLConfigElem(
			"OCR", "CantRegProc", null, "5")); // Cantidad de registros a
	// procesar por batch se sugiere
	// 1 x min.

	// Fin Parametros de ejecucion AutomaticProcessListener

	// Configuracion de WS

	public static boolean WS_DOCUMENT_CREATE_VERSIONS = Boolean
			.parseBoolean(getXMLConfigElem("General",
					"WSDocumentCreateVersions", null, "false"));

	//Variables de Entorno
	public static List<Element> variablesEntorno = getVariablesEntorno();
	/*
	public static final String fortimax_login_password_intentos = getXMLConfigElem("VariablesEntorno", "fortimax.login.password.intentos", null, "0");
	public static final String fortimax_login_password_caducidad = getXMLConfigElem("VariablesEntorno", "fortimax.login.password.caducidad", null, "0");
	public static final String fortimax_login_password_maxlongitud = getXMLConfigElem("VariablesEntorno", "fortimax.login.password.maxlongitud", null, "0");
	public static final String fortimax_login_password_minlongitud = getXMLConfigElem("VariablesEntorno", "fortimax.login.password.minlongitud", null, "0");
	public static final String fortimax_login_password_regex = getXMLConfigElem("VariablesEntorno", "fortimax.login.password.regex", null, "0");
	*/
	
	/**
	 * Duracion máxima del Token. Por default 10s
	 */
	public static final long DURACION_TOKEN = Long.parseLong(getXMLConfigElem(
			"Token", null, "expires", String.valueOf((long) (30l))));

	/**
	 * Indica si se permite o no el clic derecho sobre las imagenes.
	 */
	public static boolean ALLOW_RIGHT_CLIC = "true".equals(getXMLConfigElem(
			"General", "AllowRightClic", null, "true"));

	public static final String WS_ATTRIBUTE_SEPARATOR = getXMLConfigElem(
			"General", "WSAttributeSeparator", null, "|");

	
	/**
	 * Obtener Usuario y Password de web.xml
	 */
	public static String USR=getXMLConfigElem("usuario","usr",null,"/");
	public static String PASS=getXMLConfigElem("usuario","password",null,"/");
	public static List countV(String root,String child){return getXMLElements(root,child);}
	/**
	 * Obtener Ruta a Extjs
	 */
	public static String AdminExt=getXMLConfigElem("ExtjsAdmin","ext",null,"/");
	public static String AdminCSS=getXMLConfigElem("ExtjsAdmin","css",null,"/");
	public static String AdminLo=getXMLConfigElem("ExtjsAdmin","locale",null,"/");
	public static String AdminUx=getXMLConfigElem("ExtjsAdmin","ux",null,"/");
	
	public static String ForExt=getXMLConfigElem("ExtjsF","ext",null,"/");
	public static String ForCSS=getXMLConfigElem("ExtjsF","css",null,"/");
	public static String ForLo=getXMLConfigElem("ExtjsF","locale",null,"/");
	public static String ForUx=getXMLConfigElem("ExtjsF","ux",null,"/");
	
	public static String VersionVisualizador=getXMLConfigElem("Visualizador","version",null,"/");
	
	//Checa BD
	private static Database getDataBase(){
		Connection con = HibernateUtils.getSession().connection();
		try {
			String dbURL = con.getMetaData().getURL();
			if (StringUtils.containsIgnoreCase(dbURL,"ORACLE"))
				return Database.ORACLE;
			else if (StringUtils.containsIgnoreCase(dbURL,"MYSQL"))
				return Database.MYSQL;
			else if (StringUtils.containsIgnoreCase(dbURL,"MSSQL"))
				return Database.MSSQL;
		
		} catch (HibernateException sqle) {
			log.error(sqle,sqle);
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (Exception sqle) {	
				log.error(sqle,sqle);
			}
		}
		con = null;
		return Database.ANSISQL;
	}
	
	/**
	 * Obtener Variables de Entorno
	 * */
	private static List<Element> getVariablesEntorno(){
		List<Element> listaVariablesEntorno = getXMLElements("VariablesEntorno",null);
		
		for(Element el : listaVariablesEntorno){
			el.setText(Config.getXMLConfigElem("VariablesEntorno", el.getName(), null, "-1"));
			el.setAttribute("descripcion", Config.getXMLConfigElem("VariablesEntorno", el.getName(), "descripcion", "Variable obtenida de archivo Config."));
			
			log.debug(el.toString());
		}
		return listaVariablesEntorno;
	}
	
	public static String getValorVariableEntorno(String nombre) {
		for(Element element : variablesEntorno)
			if(element.getName().equals(nombre))
				return element.getText();
		return null;
	}

	private static Element loadConfig() {
		Element e = null;
		try {
			e = new SAXBuilder().build(
					Config.class.getClassLoader().getResource(
							cofigsPaths + "Config.xml")).getRootElement();
		} catch (Exception ex) {
			log.error(ex, ex);
		}
		return e;
	}

	public static String getLuceneIndex() {
		return luceneIndex;
	}

	// TODO: Mover este metodo a algun lugar adecuado y accesible estaticamente.
	public static Boolean copiadoAlterno(File uploadFile, File volumenFile) {
		Boolean copiadoExitoso = false;
		try {
			FileInputStream frin = new FileInputStream(uploadFile);
			FileOutputStream fwout = new FileOutputStream(volumenFile);

			try {
				byte buf[] = new byte[1024 * 4];
				while ((frin.read(buf)) != -1)
					fwout.write(buf);
				copiadoExitoso = true;
			} catch (IOException exc) {
				log.error(exc, exc);
			} finally {
				try {
					if (fwout != null) {
						fwout.flush();
						fwout.close();
					}

					if (frin != null)
						frin.close();
				} catch (IOException exc) {
					log.error(exc, exc);

				}
			}
		} catch (FileNotFoundException exc) {
			log.error(exc, exc);
		}

		return copiadoExitoso;

	}


	private static ActiveDirectoryConfigs initActiveDirectoryCongs() {

		ActiveDirectoryConfigs confgs = new ActiveDirectoryConfigs();

		Element ad = config.getChild("ActiveDirectory");

		if (ad != null) {
			String aux = ad.getAttributeValue("port");
			if (aux != null) {
				confgs.setPort(Integer.parseInt(aux));
			}
			aux = ad.getAttributeValue("host");
			if (aux != null) {
				confgs.setHost(aux);
			}
			aux = ad.getAttributeValue("dominio");
			if (aux != null) {
				confgs.setDominio(aux);
			}
			aux = ad.getAttributeValue("usersOU");
			if (aux != null) {
				confgs.setUsersOU(aux);
			}
			aux = ad.getAttributeValue("groupsOU");
			if (aux != null) {
				confgs.setGroupsOU(aux);
			}

			aux = ad.getAttributeValue("user");
			if (aux != null) {
				confgs.setUser(aux);
			}

			aux = ad.getAttributeValue("psswd");
			if (aux != null) {
				confgs.setPassd(aux);
			}

			DirContext context = confgs.getContext();
			if (context != null) {
				log.error("La configuracion de Active Directory es Correcta");
				try {
					context.close();
				} catch (NamingException e) {
					log.error(e, e);
				}
			} else {
				log.error("La configuracion de Active Directory es incorrecta");
			}

		} else {
			log.debug("No se tiene configuracion de Actived Directory");
		}
		return confgs;
	}
	private static List getXMLElements(String rootE,String Child){
		try{
			Element root=null;
			Element child = null;
			if(Child==null){
					root = config.getChild(rootE);
					return root.getChildren();
			}
			else{
				root = config.getChild(rootE);
				child = root.getChild(Child);
				return child.getChildren();
			}
		}
		catch (Exception e){
			log.error(e,e);
			return null;
		}
	}
	private static String getXMLConfigElem(String root, String elem,
			String attr, String defaultValue) {
		String val = "";
		try {

			Element child = null;
			Element rootEl = null;
			if (config != null) {

				rootEl = config.getChild(root);
				if (rootEl != null) {// SI HAY ELEMENTO ROOT
					if (elem == null && attr == null) {// SI NO HAY STRINGS ELEM
														// NI ATTR
						val = rootEl.getText();
					} else if (elem != null) {// SI HAY STRING ELEM
						child = rootEl.getChild(elem);
						if (child == null)
							throw new RuntimeException(
									"No se encontr\u00F3 el elemento " + elem
											+ " en el nodo" + root);
						else {
							if (attr == null)
								val = child.getText();
							else {
								Attribute attribute = child.getAttribute(attr);
								if (attribute == null)
									throw new RuntimeException(
											"No se encontr\u00F3 el atributo "
													+ attr + "en el elemento "
													+ elem + " del nodo" + root);
								else
									val = attribute.getValue();
							}
						}
					} else if (attr != null) {// Si hay attrib
						Attribute attribute = rootEl.getAttribute(attr);
						if (attribute == null)
							throw new RuntimeException(
									"No se encontr\u00F3 el atributo " + attr
											+ " del nodo" + root);
						else
							val = attribute.getValue();
					}
				} else {// SI NO HAY ELEM ROOT
					throw new RuntimeException(
							"No se encontr\u00F3 el elemento " + root
									+ " en el archivo de configuraci\u00F3n");
				}
			}
		} catch (Exception e) {
			log.warn(
					"No se pudo obtener valor de Config.xml, se utilizara valor por default: "
							+ defaultValue, e);
			val = defaultValue;
		}
		return val == "" ? defaultValue : val;
	}

	public static boolean changeConfigValue(String elementName,
			String childElementName, String attributeName, String value) {
		if (StringUtils.isBlank(elementName))
			return false;
		else {

			/*
			 * No existe un elemento de nivel 1 con el nombre especificado en el
			 * config. Se crea el elemento. Como no existia el padre, se asume
			 * que no existen hijos, por lo que si se especifican
			 * nombres/atributos se agregaran
			 */
			if (config.getChild(elementName) == null) {
				Element e = new Element(elementName);
				// SI ESTA EL NOMBRE DEL HIJO
				if (StringUtils.isNotBlank(childElementName)) {
					// SE CREA EL HIJO
					Element eChld = new Element(childElementName);
					// SI ESTA EL NOMBRE DE ATR
					if (StringUtils.isNotBlank(attributeName)) {
						// SE A�ADE ATR A HIJO
						Attribute attr = new Attribute(attributeName, value);
						eChld.setAttribute(attr);
						// SI NO ESTA EL NOMBRE DEL ATR
					} else {
						// SE ESTABLECE TEXTO A HIJO
						eChld.setText(value);
					}
					// SE A�ADE HIJO A PADRE
					e.addContent(eChld);
				}
				// SI NO ESTA EL NOMBRE DEL HIJO
				else {
					// SI ESTA EL NOMBRE DEL ATR
					if (StringUtils.isNotBlank(attributeName)) {
						// SE CREA ATR Y SE A�ADE A PADRE
						Attribute attr = new Attribute(attributeName, value);
						e.setAttribute(attr);
						// SI NO ESTA NOMBRE DE ATR
					} else {
						e.setText(value);
					}

				}
				config.addContent(e);
			}
			/*
			 * SI EXISTE EL PADRE
			 */
			else {
				// SI EL NOMBRE DEL HIJO ESTA
				if (StringUtils.isNotBlank(childElementName)) {

					// SI EL ELEMENTO HIJO NO EXISTE
					if (config.getChild(elementName).getChild(childElementName) == null) {
						// SE CREA EL ELEMENTO HIJO
						config.getChild(elementName).addContent(
								new Element(childElementName));
						// SI EL NOMBRE DEL ATR ESTA
						if (StringUtils.isNotBlank(attributeName)) {
							// SE CREA ATR Y ESTABLECE VALOR
							config.getChild(elementName)
									.getChild(childElementName)
									.setAttribute(
											new Attribute(attributeName, value));
							// SI EL NOMBRE DEL ATR NO ESTA
						} else {
							// SE ESTABLECE TEXTO DEL ELEMENTO HIJO
							config.getChild(elementName)
									.getChild(childElementName).setText(value);
						}
						// SI EXISTE EL ELEMENTO HIJO
					} else {
						// SI ESTA EL NOMBRE DEL ATR
						if (StringUtils.isNotBlank(attributeName)) {
							// SI NO EXISTE ATR DEL ELEMENTO HIJO
							if (config.getChild(elementName)
									.getChild(childElementName)
									.getAttribute(attributeName) == null) {
								// SE CREA ATR Y SE ESTABLECE EL VALOR DEL ATR
								// DEL ELEMENTO HIJO
								config.getChild(elementName)
										.getChild(childElementName)
										.setAttribute(
												new Attribute(attributeName,
														value));
								// SI YA EXISTE EL ATR DEL ELEMENTO HIJO
							} else {
								// SOLO SE ESTABLECE VALOR AL ATRIBUTO DEL
								// ELEMENTO HIJO
								config.getChild(elementName)
										.getChild(childElementName)
										.getAttribute(attributeName)
										.setValue(value);
							}
							// SI NO ESTA EL NOMBRE DEL ATR DEL ELEMENTO HIJO
						} else {
							// SE ESTABLECE TEXTO DEL ELEMENTO HIJO
							config.getChild(elementName)
									.getChild(childElementName).setText(value);
						}
					}

					// SI EL NOMBRE DEL HIJO NO ESTA
				} else { // SI EL NOMBRE DE ATR ESTA
					if (StringUtils.isNotBlank(attributeName)) {
						// SI NO EXISTE ATR DEL ELEMENTO PADRE
						if (config.getChild(elementName).getAttribute(
								attributeName) == null) {
							// SE CREA ATR Y SE ESTABLECE EL VALOR DEL ATR DEL
							// ELEMENTO PADRE
							config.getChild(elementName).setAttribute(
									new Attribute(attributeName, value));
							// SI EXISTE ATR DEL ELEMENTO PADRE
						} else {
							// SOLO SE ESTABLECE VALOR A ATR DE ELEMENTO PADRE
							config.getChild(elementName)
									.getAttribute(attributeName)
									.setValue(value);
						}
						// SI EL NOMBRE DEL ATR NO ESTA
					} else {
						// SE ESTABLECE TEXTO DEL ELEMENTO PADRE
						config.getChild(elementName).setText(value);
					}
				}
			}
			return true;
		}
	}

	public static void writeConfigFile() throws IOException {
		File f = new File(cofigsPaths + "Config.xml");

		FileWriter fwrirter = new FileWriter(f);
		XMLOutputter outputter = new XMLOutputter();
		outputter.output(config, fwrirter);
		/*
		 * FileWriter fwrirter = new FileWriter(new File(cofigsPaths +
		 * "Config.xml")); XMLOutputter outputter = new XMLOutputter();
		 * outputter.output(config, fwrirter);
		 */
	}
/*
	private static ApplicationConfig loadSubSistemConfig() {
		ApplicationConfig appConfig = null;
		try {
			appConfig = new ApplicationConfig(Config.class.getClassLoader()
					.getResource("/Config.xml").openStream());
		} catch (Exception e) {
			log.warn("No fue posible cargar el archivo de configuracion", e);
		}
		return appConfig;
	}
*/
	
	private static String getInstallationPath() {
		String path = null;
		try {
			Class<Config> cls = Config.class;
			ProtectionDomain pDomain = cls.getProtectionDomain();
			CodeSource cSource = pDomain.getCodeSource();
			URL loc = cSource.getLocation();
			path = loc.toString();
			path = path.substring(path.indexOf("file:")+"file:".length(), path.lastIndexOf("WEB-INF"));
		} catch (Exception e) {
		}
		return path;
	}
	
	private static Map<String, String> getMimeTypes() {
		URL url = Utils.class.getResource("mimetypes.json");
		Map<String,String> mimetypes = new TreeMap<String,String>();
		try {
			String json = FileUtils.readFileToString(new File(url.getFile()));	
			for (Map<String,Object> mapMimeType : Json.getList(json)) 
				for(Entry<String, ?> entry : mapMimeType.entrySet()) {
					String extension = entry.getKey();
					String mimetype = (String)entry.getValue();
					mimetypes.put(extension, mimetype);
				}
			return mimetypes;
		} catch (IOException e) {
			log.error("No se pudo obtener "+url+", se regresara null");
		}
		return null;
	}
	
	private static List<String> getImaxExtensions(Map<String, String> mimeTypes) {
		List<String> extensiones = new ArrayList<String>();
		log.trace("Cargando extensiones validas como IMAX:");
		for(Entry<String, String> mimeType :mimeTypes.entrySet()) {
			if(mimeType.getValue().startsWith("image/")) {
				extensiones.add(mimeType.getKey());
				log.trace(mimeType.getKey()+"\t| "+mimeType.getValue());
			}
		}
		return extensiones;
	}
}