package com.syc.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_usuario;
import com.syc.fortimax.hibernate.entities.imx_usuario_expediente;
import com.syc.utils.Encripta;
import com.syc.utils.Utils;

public class UsuarioManager {

	private static Logger log = Logger.getLogger(UsuarioManager.class);

	public synchronized boolean creaUsuario(Usuario usuario) {
		boolean retVal = false;
		HibernateManager hm = new HibernateManager();
		try {
			int id_gabinete = 1;
			String sCount = "SELECT count(id_gabinete) FROM imxusr_grales";
			String sSelect = "SELECT MAX(id_gabinete)+1 FROM imxusr_grales";
			
			if(Utils.getInteger(hm.createSQLQuery(sCount).uniqueResult())>0)
				id_gabinete = Utils.getInteger(hm.createQuery(sSelect).uniqueResult());
//			id_gabinete = (id_gabinete == 0 ? 1 : id_gabinete);
			
			List<Query> sqlQueries = new ArrayList<Query>();
			String sInsert = "INSERT INTO imx_usuario (nombre_usuario,bandera_conexion,descripcion,"
					+ "codigo,tipo_operacion,cdg,nombre,apellido_paterno,apellido_materno,"
					+ "tipo_usuario,cambio_cdg,activo,administrador) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLQuery sqlQuery = hm.createSQLQuery(sInsert);
			sqlQuery.setString(0, usuario.getNombreUsuario());
			sqlQuery.setString(1, Character.toString(usuario.getBanderaConexion()));
			sqlQuery.setString(2, usuario.getDescripcion());
			sqlQuery.setString(3, usuario.getCodigo());
			sqlQuery.setString(4, Character.toString(usuario.getTipoOperacion()));
			sqlQuery.setString(5, usuario.getCdg());
			sqlQuery.setString(6, usuario.getNombre());
			sqlQuery.setString(7, usuario.getApellidoPaterno());
			sqlQuery.setString(8, usuario.getApellidoMaterno());
			sqlQuery.setInteger(9, usuario.getImxTipoUsuario().getTipoUsuario());
			sqlQuery.setString(10, String.valueOf(usuario.getCambioCdg()));
			sqlQuery.setInteger(11, usuario.getActivo());
			sqlQuery.setInteger(12, usuario.getAdministrador());
			sqlQueries.add(sqlQuery);

			// sInsert =
			// "INSERT INTO imx_usuario_expediente (nombre_usuario,id_gabinete,fecha_registro,"
			// +
			// "fecha_inicial_vigencia,fecha_termino_vigencia,genero,fecha_nacimiento,correo,"
			// +
			// "pregunta_secreta,respuesta_secreta,firma_acuerdo,bytes_autorizados,bytes_usados,"
			// +
			// "tipo_vigencia,locale_default) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			// prep = conn.prepareStatement(sInsert);
			sInsert = "INSERT INTO imx_usuario_expediente (nombre_usuario,id_gabinete,fecha_registro,"
					+ "fecha_inicial_vigencia,fecha_termino_vigencia,genero,fecha_nacimiento,correo,"
					+ "pregunta_secreta,respuesta_secreta,firma_acuerdo,bytes_autorizados,bytes_usados"
					+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
			sqlQuery = hm.createSQLQuery(sInsert);
			sqlQuery.setString(0, usuario.getNombreUsuario());
			sqlQuery.setInteger(1, id_gabinete);

			Calendar calendar = Calendar.getInstance();

			sqlQuery.setDate(2, new java.sql.Date(calendar.getTimeInMillis()));
			sqlQuery.setDate(3, new java.sql.Date(calendar.getTimeInMillis()));

			calendar.add(Calendar.YEAR, 30);

			sqlQuery.setDate(4, new java.sql.Date(calendar.getTimeInMillis()));

			sqlQuery.setString(5, usuario.getGenero());
			if (usuario.getFechaNacimiento() == null) {
				sqlQuery.setDate(6, new java.sql.Date(0L));
			} else {
				sqlQuery.setDate(6, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
			}
			sqlQuery.setString(7, usuario.getCorreo());
			sqlQuery.setInteger(8, usuario.getPreguntaSecreta());
			sqlQuery.setString(9, usuario.getRespuestaSecreta());
			sqlQuery.setString(10, usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno());
			sqlQuery.setLong(11, usuario.getBytesAutorizados());
			sqlQuery.setLong(12, 0);
			// prep.setString(15, usuario.getLocaleDefault());
			
			sqlQueries.add(sqlQuery);

			sInsert = "INSERT INTO imxusr_grales (id_gabinete,activo,propietario) VALUES (?,?,?)";
			sqlQuery = hm.createSQLQuery(sInsert);
			sqlQuery.setInteger(0, id_gabinete);
			sqlQuery.setString(1, "S");
			sqlQuery.setString(2, usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno());

			sqlQueries.add(sqlQuery);

			sInsert = "INSERT INTO imx_privilegio (titulo_aplicacion,nombre_usuario,nombre_nivel,privilegio) VALUES (?,?,?,?)";
			sqlQuery = hm.createSQLQuery(sInsert);
			sqlQuery.setString(0, "USR_GRALES");
			sqlQuery.setString(1, usuario.getNombreUsuario());
			sqlQuery.setString(2, "TODOS");
			sqlQuery.setInteger(3, 255);

			sqlQueries.add(sqlQuery);
			
			sInsert = "INSERT INTO imx_carpeta (titulo_aplicacion,id_gabinete,id_carpeta,nombre_carpeta,"
					+ " nombre_usuario,bandera_raiz,fh_creacion,fh_modificacion,numero_accesos,numero_carpetas,"
					+ " numero_documentos,descripcion) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
			sqlQuery = hm.createSQLQuery(sInsert);
			sqlQuery.setString(0, "USR_GRALES");
			sqlQuery.setInteger(1, id_gabinete);
			sqlQuery.setInteger(2, 0);
			sqlQuery.setString(3,usuario.getNombre() + " " + usuario.getApellidoPaterno());
			sqlQuery.setString(4, usuario.getNombreUsuario());
			sqlQuery.setString(5, "S");
			sqlQuery.setDate(6, new java.util.Date(System.currentTimeMillis()));
			sqlQuery.setDate(7, new java.util.Date(System.currentTimeMillis()));
			sqlQuery.setInteger(8, 0);
			sqlQuery.setInteger(9, 0);
			sqlQuery.setInteger(10, 0);
			sqlQuery.setString(11,"Carpeta raiz Mis Documentos de " + usuario.getNombre() + " " + usuario.getApellidoPaterno());

			sqlQueries.add(sqlQuery);
			
			hm.executeQueries(sqlQueries);
			
			retVal = true;
		} catch (Exception e) {
			log.error(e,e);
		} finally {
			hm.close();
		}
		return retVal;
	}

	public Usuario selectUsuario(String nombre_usuario) {
		Usuario u = null;
		HibernateManager hm = new HibernateManager();
		try {
			Query query = hm.createQuery("FROM imx_usuario WHERE nombreUsuario = :nombre_usuario");
			query.setString("nombre_usuario",nombre_usuario);
			imx_usuario imx_usuario = (imx_usuario)query.uniqueResult();
			
			u = new Usuario(nombre_usuario);

			//Los siguientes vienen de imx_usuario
			u.setBanderaConexion(imx_usuario.getBanderaConexion());
			u.setDescripcion(imx_usuario.getDescripcion());
			u.setCodigo(imx_usuario.getCodigo());
			u.setTipoOperacion(imx_usuario.getTipoOperacion());
			u.setCdg(imx_usuario.getCdg());
			u.setNombre(imx_usuario.getNombre());
			u.setApellidoPaterno(imx_usuario.getApellidoPaterno());
			u.setApellidoMaterno(imx_usuario.getApellidoMaterno());
			u.setTipo_usuario(imx_usuario.getImxTipoUsuario().getTipoUsuario());
			u.setCambioCdg(imx_usuario.getCambioCdg());
			u.setIntentosAcceso(imx_usuario.getIntentosAcceso());
			u.setActivo(imx_usuario.getActivo());
			u.setAdministrador(imx_usuario.getAdministrador());
			
			//Lo siguiente viene de imx_privilegio
			int count = Utils.getInteger(hm.createSQLQuery(
					  " SELECT COUNT(nombre_usuario) AS numero"
					+ " FROM imx_privilegio"
					+ " WHERE nombre_usuario = '" + nombre_usuario+ "'"
					+ " AND titulo_aplicacion != 'USR_GRALES'"
					).uniqueResult());
			u.setPyME((count > 0) ? true : false);
			
			query = hm.createQuery("FROM imx_usuario_expediente WHERE id.nombre_usuario = :nombre_usuario");
			query.setString("nombre_usuario",nombre_usuario);
			imx_usuario_expediente imx_usuario_expediente = (imx_usuario_expediente)query.uniqueResult();

			//Los siguientes vienen de imx_usuario_expediente
			u.setIdGabinete(imx_usuario_expediente.getId().getId_gabinete());
			u.setFechaRegistro(imx_usuario_expediente.getFecha_registro());
			u.setFechaInicialVigencia(imx_usuario_expediente.getFecha_inicial_vigencia());
			u.setFechaTerminoVigencia(imx_usuario_expediente.getFecha_termino_vigencia());
			u.setGenero(imx_usuario_expediente.getGenero());
			u.setFechaNacimiento(imx_usuario_expediente.getFecha_nacimiento());
			u.setOcupacion(imx_usuario_expediente.getOcupacion());
			u.setCorreo(imx_usuario_expediente.getCorreo());
			u.setPreguntaSecreta(imx_usuario_expediente.getPregunta_secreta());
			u.setRespuestaSecreta(imx_usuario_expediente.getRespuesta_secreta());
			u.setFirmaAcuerdo(imx_usuario_expediente.getFirma_acuerdo());
			u.setAutorizaCortesia(imx_usuario_expediente.getAutoriza_cortesia());
			u.setBytesAutorizados(imx_usuario_expediente.getBytes_autorizados());
			u.setBytesUsados(imx_usuario_expediente.getBytes_usados());
			u.setVerificadoPayPal(imx_usuario_expediente.getVerificado_paypal());
		} catch (Exception e) {
			log.info("No se pudo cargar el usuario '" + nombre_usuario + "'");
			log.error(e,e);
		} finally {
			hm.close();
		}
		return u;
	}

	public boolean updateUsuario(Usuario usr) {
		boolean retVal = false;

		HibernateManager hm = new HibernateManager();
		try {
			SQLQuery sqlQuery = hm.createSQLQuery(
					" UPDATE imx_usuario"
				  + " SET"
				  + " bandera_conexion = ?, descripcion = ?, codigo = ?, tipo_operacion = ?,"
				  + " cdg = ?, nombre = ?, apellido_paterno = ?, apellido_materno = ?" 
				  + " WHERE nombre_usuario = '" + usr.getNombreUsuario() + "'");

			sqlQuery.setString(0, Character.toString(usr.getBanderaConexion()));
			sqlQuery.setString(1, usr.getDescripcion());
			sqlQuery.setString(2, usr.getCodigo());
			sqlQuery.setString(3, Character.toString(usr.getTipoOperacion()));
			sqlQuery.setString(4, usr.getCdg());
			sqlQuery.setString(5, usr.getNombre());
			sqlQuery.setString(6, usr.getApellidoPaterno());
			sqlQuery.setString(7, usr.getApellidoMaterno());

			int rowCount = hm.executeQuery(sqlQuery);

			if (rowCount == 1) {
				retVal = true;
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return retVal;
	}

	public int validaUsuario(Usuario u, String password, boolean isEncrypted)
	 {

		if (!isPasswordChange(u.getNombreUsuario(), password)) {
			return 4;
		}

		if (!isPasswordValid(u.getNombreUsuario(), password, isEncrypted)) {
			return 1;
		}
		if(!isActive(u.getNombreUsuario(),password,isEncrypted)){
			return 7;
		}
		/*
		 * if (!testValidity(u.getNombreUsuario())) { return 2; }
		 */

		if (!validaPermisoWeb(u)) {
			return 3;
		}

		return 0;
	}

//	private boolean isPasswordValid(String nombre_usuario, String password) {
//		return isPasswordValid(nombre_usuario, password, false);
//	}

	public boolean isPasswordValid(String nombre_usuario, String password, boolean passwordEncriptado) {
		HibernateManager hm = new HibernateManager();
		boolean retVal = false;
		try {
			Query query = hm.createQuery("SELECT count(*) FROM imx_usuario WHERE nombreUsuario = :nombre_usuario AND cdg = :cdg");
			query.setString("nombre_usuario",nombre_usuario);
			query.setString("cdg", passwordEncriptado ? password : Encripta.code32(password));
			Long count = (Long)query.uniqueResult();
			retVal = count>0 ? true : false; 
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return retVal;
	}
	public boolean isActive(String nombre_usuario, String password, boolean passwordEncriptado) {
		HibernateManager hm = new HibernateManager();
		boolean retVal = false;
		try {
			Query query = hm.createQuery("SELECT count(*) FROM imx_usuario WHERE activo = 0 AND nombreUsuario = :nombre_usuario AND cdg = :cdg");
			query.setString("nombre_usuario",nombre_usuario);
			query.setString("cdg", passwordEncriptado ? password : Encripta.code32(password));
			Long count = (Long)query.uniqueResult();
			retVal = count>0 ? true : false; 
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return retVal;
	}
	private boolean isPasswordChange(String nombre_usuario, String password) {
		HibernateManager hm = new HibernateManager();
		boolean retVal = false;
		try {
			Query query = hm.createQuery("SELECT cambioCdg FROM imx_usuario WHERE nombreUsuario = :nombre_usuario");
			query.setString("nombre_usuario",nombre_usuario);
			Character cambioCdg = (Character) query.uniqueResult();
			retVal = cambioCdg.equals('1'); 
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			hm.close();
		}
		return retVal;
	}

	private boolean validaPermisoWeb(Usuario u) {
		return ((Character.getNumericValue(u.getTipoOperacion()) == 4) || Character.
				getNumericValue(u.getTipoOperacion()) == 7);
	}

	public Boolean verificarPassword(String usuario, String passwordMD5){
		if(usuario==null||passwordMD5==null)
			return false;
		else {
			HibernateManager hm = new HibernateManager();
			Conjunction conjuction = Restrictions.conjunction();
			conjuction.add(Restrictions.eq("nombreUsuario", usuario));
			hm.setCriterion(conjuction);
			imx_usuario u = (imx_usuario) hm.uniqueResult(imx_usuario.class);
			if(u==null)
				return false;
			else {
				log.trace(usuario);
				log.trace(passwordMD5);
				log.trace(u.getCdg());
				return passwordMD5.equals(u.getCdg());
			}
		}
	}
	public Boolean cambiarPasswors(String usuario,String passwordMD5){
		if(usuario==null)
			return false;
		else {
			HibernateManager hibernateManager = new HibernateManager();
			hibernateManager.setCriterion(Restrictions.eq("nombreUsuario", usuario));
			imx_usuario u = (imx_usuario) hibernateManager.uniqueResult(imx_usuario.class);
			if(u==null)
				return false;
			else {
				u.setCambioCdg('1');
				u.setCdg(passwordMD5);
				hibernateManager.update(u);
				return true;
			}
		}
	}

/*Lista de métodos de UsuarioManager*/
	
	/** Arreglo para preparar lista de usuarios */
	public ArrayList<Usuario> listUsuarios() throws Exception {
		log.debug("Recibiendo usuarios:");
		ArrayList<Usuario> retVal = new ArrayList<Usuario>();
		HibernateManager hm = new HibernateManager();
		try {
			String sQuery = "SELECT nombreUsuario FROM imx_usuario ORDER BY 1";
			@SuppressWarnings("unchecked")
			List<String> usuarios = (List<String>) hm.createQuery(sQuery).list();

			for(String usuario : usuarios) {
				retVal.add(new Usuario(usuario));
			}
		} finally {
			hm.close();
		}
		return retVal;
	}


	/**
	 * ExisteUsuario
	 *
	 * @param 
	 *            nombre_usuario
	 *
	 * @return retval true/false
	 */
	public boolean existeUsuario(String nombre_usuario) {
		log.debug("Revisando si existe el usuario : " + nombre_usuario);
		boolean retVal = false;

		Session session = HibernateUtils.getSession();
		try {
			session = HibernateUtils.getSession();
			Query q = session
					.createQuery("FROM imx_usuario WHERE nombreUsuario = :nombre_usuario");
			q.setString("nombre_usuario", nombre_usuario);

			if ((q.uniqueResult() != null))
				retVal = true;

		} finally {
			try {
				session.close();
			} catch (Exception exc) {
				log.error(exc, exc);

			}
		}
		return retVal;
	}

	/**
	 * CreaPassword
	 *
	 * @param nombre_usuario
	 *
	 * @exception Exception
	 *                Se genera una excepción genérica.
	 * @return Encripta.code32(newPassword.toString().toUpperCase())
	 */
	public String creaPassword(String nombre_usuario) throws Exception {
		log.debug("Creando Password");

		StringBuffer newPassword = new StringBuffer();
		for (int i = nombre_usuario.length() - 1; i >= 0; i--) {
			newPassword.append(nombre_usuario.charAt(i));
		}
		return Encripta.code32(newPassword.toString());
	}

	/**
	 * borraUsuario
	 *
	 * @param  nombre_usuario
	 *            
	 *
	 *                
	 * @return retval
	 */
	@SuppressWarnings("unchecked")
	public boolean borraUsuario(String nombre_usuario) {
		boolean retVal = false;
		HibernateManager hm = new HibernateManager();
		try {
			Query query = hm.createQuery("SELECT id.id_gabinete FROM imx_usuario_expediente WHERE id.nombre_usuario = :nombre_usuario");
			query.setString("nombre_usuario",nombre_usuario);
			int id_gabinete = (Integer) query.uniqueResult();
			
			String sSelect = "SELECT p.nom_archivo_vol,v.volumen,v.unidad_disco,v.ruta_base,v.ruta_directorio "
					+ " FROM imx_pagina p, imx_volumen v "
					+ " WHERE id_gabinete = ? "
					+ " AND p.volumen = v.volumen AND p.titulo_aplicacion='USR_GRALES'";
			SQLQuery sqlQuery = hm.createSQLQuery(sSelect);
			sqlQuery.setInteger(0, id_gabinete);
			sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> archivos = sqlQuery.list();
			
			List<Query> queries = new ArrayList<Query>();
			
			String sDelete = "DELETE FROM imx_pagina WHERE titulo_aplicacion = ? AND id_gabinete = ?";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, "USR_GRALES");
			sqlQuery.setInteger(1, id_gabinete);
			queries.add(sqlQuery);

			sDelete = "DELETE FROM imx_documento WHERE titulo_aplicacion = ? AND id_gabinete = ?";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, "USR_GRALES");
			sqlQuery.setInteger(1, id_gabinete);
			queries.add(sqlQuery);

			sDelete = "DELETE FROM imx_org_carpeta WHERE titulo_aplicacion = ? AND id_gabinete = ?";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, "USR_GRALES");
			sqlQuery.setInteger(1, id_gabinete);
			queries.add(sqlQuery);

			sDelete = "DELETE FROM imx_carpeta WHERE titulo_aplicacion = ? AND id_gabinete = ?";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, "USR_GRALES");
			sqlQuery.setInteger(1, id_gabinete);
			queries.add(sqlQuery);
			
			sDelete = "DELETE FROM imx_privilegio WHERE nombre_usuario = ? ";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, nombre_usuario);
			queries.add(sqlQuery);

			sDelete = "DELETE FROM imx_grupo_usuario WHERE nombre_usuario = ? ";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, nombre_usuario);
			queries.add(sqlQuery);

			sDelete = "DELETE FROM imxusr_grales WHERE id_gabinete = ? ";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setInteger(0, id_gabinete);
			queries.add(sqlQuery);

			sDelete = "DELETE FROM imx_usuario_expediente WHERE nombre_usuario = ? ";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, nombre_usuario);
			queries.add(sqlQuery);

			sDelete = "DELETE FROM imx_usuario WHERE nombre_usuario = ? ";
			sqlQuery = hm.createSQLQuery(sDelete);
			sqlQuery.setString(0, nombre_usuario);
			queries.add(sqlQuery);
			
			hm.executeQueries(queries);
			
			//Borrar hasta que la transacción se haya completado exitosamente.
			for(Map<String,String> archivo : archivos) {
				archivo = new CaseInsensitiveMap(archivo);
				String path = archivo.get("unidad_disco") + archivo.get("ruta_base") + archivo.get("ruta_directorio") + archivo.get("nom_archivo_vol");
				File f = new File(path);
				if (f.exists()) {
					log.debug("Se borrara el archivo "+path);
					if (!f.delete())
						f.deleteOnExit();
				}
			}
			
			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	/**
	 * actualizaUsuario
	 *
	 * @return retval
	 */
	public boolean actualizaUsuario(Usuario usuario) {
		boolean retVal = false;
		HibernateManager hm = new HibernateManager();
		try {
			List<Query> queries = new ArrayList<Query>();
			
			boolean cambiaPassword = !"".equals(usuario.getCdg());
			String sUpdate = "UPDATE imx_usuario SET " + " descripcion = ?,"
					+ (cambiaPassword ? " cdg = ?," : "") + " nombre = ?,"
					+ " apellido_paterno = ?," + " apellido_materno = ?,"
					+ " tipo_usuario = ?," + "activo = ?," + "administrador = ?"
					+ " WHERE nombre_usuario = ?";
			SQLQuery sqlQuery = hm.createSQLQuery(sUpdate);
			sqlQuery.setString(0, usuario.getDescripcion());
			int num_campo = 1;
			if (cambiaPassword) {
				sqlQuery.setString(1, usuario.getCdg());
				num_campo++;
			}
			sqlQuery.setString(num_campo++, usuario.getNombre());
			sqlQuery.setString(num_campo++, usuario.getApellidoPaterno());
			sqlQuery.setString(num_campo++, usuario.getApellidoMaterno());
			sqlQuery.setInteger(num_campo++, usuario.getImxTipoUsuario().getTipoUsuario());
			sqlQuery.setInteger(num_campo++, usuario.getActivo());
			sqlQuery.setInteger(num_campo++, usuario.getAdministrador());
			sqlQuery.setString(num_campo, usuario.getNombreUsuario());
			
			queries.add(sqlQuery);

			sUpdate = "UPDATE imx_usuario_expediente SET " + " genero = ?,"
					+ " fecha_nacimiento = ?," + " correo = ?,"
					+ " pregunta_secreta = ?," + " respuesta_secreta = ?,"
					+ " bytes_autorizados = ?"
					+ " WHERE nombre_usuario = ?";

			sqlQuery = hm.createSQLQuery(sUpdate);
			sqlQuery.setString(0, usuario.getGenero());
			sqlQuery.setDate(1, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
			sqlQuery.setString(2, usuario.getCorreo());
			sqlQuery.setInteger(3, usuario.getPreguntaSecreta());
			sqlQuery.setString(4, usuario.getRespuestaSecreta());
			sqlQuery.setLong(5, usuario.getBytesAutorizados());
			sqlQuery.setString(6, usuario.getNombreUsuario());
			
			queries.add(sqlQuery);

			hm.executeQueries(queries);
			
			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

}