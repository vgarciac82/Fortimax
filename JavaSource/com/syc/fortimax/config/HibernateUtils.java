package com.syc.fortimax.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_catalogo_atributos;
import com.syc.fortimax.hibernate.entities.imx_catalogo_privilegios;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento_id;
import com.syc.fortimax.hibernate.entities.imx_tipo_usuario;
import com.syc.fortimax.hibernate.entities.imx_videos;
import com.syc.fortimax.hibernate.validation.CustomSchemaValidationConfiguration;

public abstract class HibernateUtils {

	private static final Logger log = Logger.getLogger(HibernateUtils.class);

	private static final SessionFactory sessionFactory = initHibernate();

	private static SessionFactory initHibernate() {
		SessionFactory sess = null;
		try { // Create the SessionFactory from hibernate.cfg.xml
			sess = getHibernateConfig().buildSessionFactory();
		} catch (HibernateException ex) {
			log.error(ex, ex);
		} catch (NullPointerException ex) {
			log.error(ex, ex);
		}

		return sess;
	}

	public static Configuration getHibernateConfig() {
		Configuration c = null;
		try {
			c = new Configuration().configure(HibernateUtils.class.getClassLoader()
					.getResource("hibernate.cfg.xml"));
		} catch (Throwable e) {
			log.error(e, e);
		}
		return c;
	}

	/**
	 * Crea una nueva Sesssion de Hbiernate
	 *
	 * CUIDADO!!! LAS SESSIONES TIENEN QUE SER CERRADAS POR EL DESARROLLADOR
	 *
	 * @return una nueva Session
	 */
	public static Session getSession() {
		Session sess = null;
		if (sessionFactory != null && sessionFactory.isClosed() == false) {
			sess = sessionFactory.openSession();
		}
		return sess;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static boolean existBD() {
		boolean exist = false;

		if (HibernateUtils.getSessionFactory() != null) {
			exist = true;
		}

		return exist;
	}

	public static boolean validateBD() {
		boolean valid = false;

		SessionFactory sessionFactory = null;
		try { // Create the SessionFactory from hibernate.cfg.xml
			CustomSchemaValidationConfiguration configuration =  new CustomSchemaValidationConfiguration();
			configuration.configure(HibernateUtils.class.getClassLoader().getResource("hibernate.cfg.xml"));
			configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
			sessionFactory = configuration.buildSessionFactory();
			
			validationException = configuration.getSchemaViolations();
			if(configuration.getSchemaViolations().isEmpty())
				valid=true;
		} finally {
			if(sessionFactory!=null&&!sessionFactory.isClosed())
				sessionFactory.close();
		}
		return valid;
	}

	private static List<Exception> validationException= new ArrayList<Exception>();

	public static List<Exception> getValidationException() {
		return validationException;
	}

	public static List<Exception> importInitialData() {
		List<Exception> exceptions = new ArrayList<Exception>();
		try {
			ArrayList<Object> objects = new ArrayList<Object>();
			
			objects.add(new imx_tipo_usuario(1,"Normal"));
			objects.add(new imx_tipo_usuario(2,"VIP"));
			objects.add(new imx_tipo_usuario(3,"Promocion"));
			objects.add(new imx_tipo_usuario(4,"Cortesia"));
			
			objects.add(new imx_catalogo_privilegios(1,"Eliminar",1,"Eliminar"));
			objects.add(new imx_catalogo_privilegios(2,"Modificar",2,"Modificar"));
			objects.add(new imx_catalogo_privilegios(3,"Crear",4,"Crear"));
			objects.add(new imx_catalogo_privilegios(4,"Imprimir",16,"Imprimir"));
			objects.add(new imx_catalogo_privilegios(5,"Compartir",32,"Compartir"));
			objects.add(new imx_catalogo_privilegios(6,"Digitalizar",64,"Digitalizar"));
			objects.add(new imx_catalogo_privilegios(7,"Descargar",128,"Descargar"));
			
			objects.add(new imx_videos(1, "Fortimax", "Introduccion", "Video de introduccion a las Guias de usuario.", "/videos/S1C1.mp4", "0"));
			objects.add(new imx_videos(2, "Fortimax", "Crear carpeta", "En este video se muestra la manera de como crear una carpeta en Fortimax", "/videos/S2C2.mp4", "0"));
			objects.add(new imx_videos(3, "Fortimax", "Digitalizar documento", "En este video se muestra la manera correcta de digitalizar un documento en Fortimax", "/videos/S1C3.mp4", "0"));
			objects.add(new imx_videos(4, "Fortimax", "Album de fotos", "Album de fotos", "/videos/S1C4.mp4", "0"));
			objects.add(new imx_videos(5, "Fortimax", "Respaldar archivo", "Ejemplo de como respaldar un archivo", "/videos/S1C5.mp4", "0"));
			objects.add(new imx_videos(6, "Fortimax", "Respaldar carpeta", "En este video se muestra la manera de respaldar una carpeta en Fortimax", "/videos/S1C6.mp4", "0"));
			objects.add(new imx_videos(7, "Fortimax", "Recuperar a mi PC", "Ejemplo de como recuperar a tu computadora un documento existente en Fortimax", "/videos/S1C7.mp4", "0"));
						
			objects.add(new imx_aplicacion("USR_GRALES","IMXUSR_GRALES","Gaveta para Usuarios Generales"));
	
			objects.add(new imx_tipo_documento(new imx_tipo_documento_id("USR_GRALES", -1), 3, "IMX_SIN_TIPO_DOCUMENTO", "SIN TIPO"));
			objects.add(new imx_tipo_documento(new imx_tipo_documento_id("USR_GRALES", 1), 3, "EXTERNO", "Tipo de Documentos Externos"));
			objects.add(new imx_tipo_documento(new imx_tipo_documento_id("USR_GRALES", 2), 3, "IMAX_FILE", "Tipo de documento Imax_File (Imagenes)"));
			
			objects.add(new imx_catalogo_atributos(1, "Requerido", 1, "Boolean", 1, "El documento NO debe estar vacio, para ser válido"));
			objects.add(new imx_catalogo_atributos(2, "Historico", 2, "Boolean", 1, "El documento genera copias en cada modificacion del documento"));
			objects.add(new imx_catalogo_atributos(3, "Vigencia", 3, "Integer", 3, "Cantidad de dias en que el documento es válido a partir de su alta"));
			objects.add(new imx_catalogo_atributos(4, "Vencimiento", 4, "Date", 10, "Fecha especifica en la que el documento deja de ser válido"));
			objects.add(new imx_catalogo_atributos(5, "Existencia Física", 5, "String", 200, "Es donde se encuentra fisicamente el documento ( Edificio, archivo, piso, gaveta, seccion, etc. )."));
			
			HibernateManager hibernateManager = new HibernateManager();
			hibernateManager.saveOrUpdate(objects);
		} catch(Exception e) {
			exceptions.add(e);
		}
		return exceptions;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Exception> updateSchema() throws HibernateException {
		SchemaUpdate schemaUpdate = new SchemaUpdate(getHibernateConfig());
		schemaUpdate.execute(true,true);
		return schemaUpdate.getExceptions();
	}
	
	private static PersistentClass getClassMapping(Class<?> classOfEntity) {
		return getClassMapping(classOfEntity.getName());
	}

	private static PersistentClass getClassMapping(String className) {
		Configuration config = getHibernateConfig();
		config.buildMappings();//must be called
		PersistentClass mapping = config.getClassMapping(className);
		return mapping;
	}
	
	private static List<String> getPropertyNames(ClassMetadata hibernateMetadata) {
		List<String> list = new ArrayList<String>();	
		list.add(hibernateMetadata.getIdentifierPropertyName());
		list.addAll(Arrays.asList(hibernateMetadata.getPropertyNames()));
		return list;
	}
	
	public static List<String> getPropertyNames(Class<?> classOfEntity) {
		return getPropertyNames(classOfEntity.getName());
	}

	private static List<String> getPropertyNames(String className) {
		ClassMetadata hibernateMetadata = sessionFactory.getClassMetadata(className);
		return getPropertyNames(hibernateMetadata);
	}
	
	public static String getIdentifierPropertyName(Class<?> classOfEntity) {
		ClassMetadata hibernateMetadata = sessionFactory.getClassMetadata(classOfEntity.getName());
		return hibernateMetadata.getIdentifierPropertyName();
	}
	
	public static List<String> getColumnNames(Class<?> classOfEntity) {
		List<String> columnNames = new ArrayList<String>();		
		List<String> propertyNames = getPropertyNames(classOfEntity);
		PersistentClass persistentClass = getClassMapping(classOfEntity);
		for (String property : propertyNames) {
		    @SuppressWarnings("unchecked")
			Iterator<Column> columns = persistentClass.getProperty(property).getColumnIterator();
		    while(columns.hasNext()) {
		    	columnNames.add(columns.next().getName());
		    }
		}
		return columnNames;
	}
}