package com.syc.gavetas;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.syc.dbobject.BaseDeDatos;
import com.syc.dbobject.Campo;
import com.syc.dbobject.CatalogoDatos;
import com.syc.dbobject.Indice;
import com.syc.dbobject.LlaveForanea;
import com.syc.dbobject.LlavePrimaria;
import com.syc.dbobject.Tabla;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_campo_catalogo;
import com.syc.fortimax.hibernate.entities.imx_campo_catalogo_id;
import com.syc.fortimax.hibernate.entities.imx_descripcion;
import com.syc.fortimax.hibernate.entities.imx_descripcion_id;
import com.syc.fortimax.hibernate.entities.imx_seguridad;
import com.syc.fortimax.hibernate.entities.imx_seguridad_id;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento_id;
import com.syc.utils.Utils;

public class GavetaManager {


	private static final Logger log = Logger.getLogger(GavetaManager.class);

	public ArrayList<Gaveta> listGavetas() {
		ArrayList<Gaveta> retVal = new ArrayList<Gaveta>();
		HibernateManager hm = new HibernateManager();
		try {
			String sQuery = "SELECT titulo_aplicacion FROM imx_aplicacion WHERE titulo_aplicacion!='USR_GRALES' ORDER BY 1";
			@SuppressWarnings("unchecked")
			List<String> gavetas = hm.createSQLQuery(sQuery).list();
			for (String gaveta : gavetas)
				retVal.add(new Gaveta(gaveta));
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean existeGaveta(String nombre_gaveta) {
		log.debug("Revisando si existe la gaveta : " + nombre_gaveta);
		boolean retVal = false;
			
		HibernateManager hm = new HibernateManager();
		Query q = hm.createQuery("FROM imx_aplicacion WHERE titulo_aplicacion = :nombre_gaveta");
		q.setString("nombre_gaveta", nombre_gaveta);

		if ((q.uniqueResult() != null))
			retVal = true;

		hm.close();
		return retVal;
	}
	
	
	public List<String> getQueriesCreacionGaveta(BaseDeDatos bd) throws Exception {
		List<String> queries = new ArrayList<String>();
			CatalogoDatos cat_datos = new CatalogoDatos();

			// ArrayList<Indice> indices = new ArrayList<Indice>();
			List<LlaveForanea> foraneas = new ArrayList<LlaveForanea>();
			List<LlavePrimaria> primarias = new ArrayList<LlavePrimaria>();

			List<Tabla> tablas = bd.getTablas();

			String sCreate = "CREATE TABLE ";
			for (int i = 0; i < tablas.size(); i++) {
				Tabla tbl = (Tabla) tablas.get(i);

				sCreate += tbl.getNombreTabla() + " ("; // toda la sintaxis debe
				// ser lo mas ANSI

				ArrayList<Campo> campos = tbl.getCampos();
				for (int j = 0; j < campos.size(); j++) {
					Campo cmp = (Campo) campos.get(j);
					if (j > 0)
						sCreate += ",";

					sCreate += cmp.getNombreCampo() + " "
							+ cat_datos.getNombreTipoCampo(cmp.getTipoDato());
					if (cmp.getLongitud() != -1
							&& cat_datos.llevaLongitud(cmp.getTipoDato())) {
						String valueof = String.valueOf(cmp.getLongitud());
						if (valueof.indexOf(".0") == -1) {
							sCreate += " ("
									+ valueof
											.substring(0, valueof.indexOf("."))
									+ ","
									+ valueof
											.substring(valueof.indexOf(".") + 1)
									+ ")";
						} else {
							sCreate += " ("
									+ valueof
											.substring(0, valueof.indexOf("."))
									+ ")";
						}
					}

					sCreate += !cmp.getCanNull() ? " NOT NULL " : "";

					String val_default = cmp.getValPredefinido();
					if (val_default != null && !"".equals(val_default)
							&& !"null".equalsIgnoreCase(val_default)) {
						sCreate += " DEFAULT '" + val_default + "'";
					}

				}

				sCreate += ")";
				queries.add(sCreate);
				sCreate = "CREATE TABLE ";

				// indices.addAll(tbl.getIndices());
				foraneas.addAll(tbl.getLlavesForaneas());
				primarias.addAll(tbl.getLlavesPrimarias());

			}

			if (primarias.size() > 0) {
				sCreate = null;
				String strTabla = null;
				for (int j = 0; j < primarias.size(); j++) {

					LlavePrimaria objPK = (LlavePrimaria) primarias.get(j);
					if (!objPK.getNombreTabla().equals(strTabla)) {// si es la
						// primera
						// tabla o
						// cambio de
						// tabla
						if (j != 0) {
							sCreate += ")";
							queries.add(sCreate);
						}

						sCreate = "ALTER TABLE " + objPK.getNombreTabla()
								+ " ADD PRIMARY KEY(" + objPK.getCampoPK();
					} else { // si es la misma tabla
						sCreate += "," + objPK.getCampoPK();

					}
					strTabla = objPK.getNombreTabla();
				}
				sCreate += ")";
				queries.add(sCreate);			 
			}

			if (foraneas.size() > 0) {
				sCreate = null;
				String strCampoOrigen = null; // para saber cuando el campo
				// origen es el mismo y entonces
				// separar el fk
				String strTabla = null; // para saber cuando cambie el nombre de
				// la tabla a la que se lestan
				// construyendo las fks
				String strTablaOrigen = null; // para saber cuando cambie de
				// tabla origen (reference)
				String strCamposOrigen = null;
				for (int j = 0; j < foraneas.size(); j++) {

					LlaveForanea objFK = (LlaveForanea) foraneas.get(j);
					// Si cambio de tablaorigen(tabla referencia) o de
					// nombretabla(tabla a la que se le estan haciendo las fks)
					if (!objFK.getTablaOrigen().equals(strTablaOrigen)
							|| !objFK.getNombreTabla().equals(strTabla)
							|| objFK.getCampoOrigen().equals(strCampoOrigen)) { //
						if (strTablaOrigen != null) {
							sCreate += ") REFERENCES " + strTablaOrigen + "("
									+ strCamposOrigen + ")";
							queries.add(sCreate);
							// strCamposOrigen="";
						}
						strCamposOrigen = objFK.getCampoOrigen();
						sCreate = "ALTER TABLE " + objFK.getNombreTabla()
								+ " ADD FOREIGN KEY(" + objFK.getCampoFK();
					} else { // si es la misma tabla ya sea de referencia o a
						// la que se le estan haciendo las fks
						sCreate += "," + objFK.getCampoFK();
						strCamposOrigen += "," + objFK.getCampoOrigen();

					}
					strTablaOrigen = objFK.getTablaOrigen();
					strTabla = objFK.getNombreTabla();
					strCampoOrigen = objFK.getCampoOrigen();
				}
				sCreate += ") REFERENCES " + strTablaOrigen + "("
						+ strCamposOrigen + ")";
				queries.add(sCreate);			
			}
		return queries;
	}
	
	public boolean creaGaveta(Gaveta g) throws Exception {
		if(g.getNombre().startsWith("_"))
			throw new HibernateException("El nombre de una gaveta no puede iniciar con el caracter reservado '_'");
		if(g.getNombre().equalsIgnoreCase("USR_GRALES"))
			throw new HibernateException("El nombre de una gaveta no puede ser el nombre reservado 'USR_GRALES'");
		boolean retVal = false;
		HibernateManager hm = new HibernateManager();
		log.info("----------BEGIN TRANSACTION----------");
		Transaction tx = hm.getSession().beginTransaction();
		try {

			Tabla tbl_gaveta = new Tabla("IMX" + g.getNombre());

			Campo cmp = new Campo("ID_GABINETE", false, 3, 4);
			tbl_gaveta.addCampo(cmp);
			
			LlavePrimaria pk = new LlavePrimaria(tbl_gaveta.getNombreTabla(),cmp.getNombreCampo());
			tbl_gaveta.addLlavePrimaria(pk); 
			
			tbl_gaveta.addCampo(new Campo("ACTIVO", true, 10, 1));
			
			Campo _fecha_creacion = new Campo("fmx_fecha_creacion",true,8,10);
			tbl_gaveta.addCampo(_fecha_creacion);
			
			Campo _fecha_mantenimiento = new Campo("fmx_fecha_mantenimiento",true,8,10);
			tbl_gaveta.addCampo(_fecha_mantenimiento);
			
			Campo _fecha_OK = new Campo("fmx_fecha_OK",true,8,10);
			tbl_gaveta.addCampo(_fecha_OK);

			ArrayList<GavetaCampo> agc = g.getCampos();

			for (int i = 0; i < agc.size(); i++) {
				GavetaCampo gc = (GavetaCampo) agc.get(i);
				cmp = new Campo(gc.getNombreCampo(), "S".equals(gc
						.getRequerido()) ? false : true, gc.getTipoDato(), gc
						.getTamano());
				
				/*
				 * if(gc.getIndice()==2){ LlavePrimaria pk = new
				 * LlavePrimaria(tbl_gaveta
				 * .getNombreTabla(),gc.getNombreCampo());
				 * tbl_gaveta.addLlavePrimaria(pk); }
				 */
				tbl_gaveta.addCampo(cmp);
			}

			BaseDeDatos bdd = new BaseDeDatos();
			bdd.addTabla(tbl_gaveta);

			List<String> queries = getQueriesCreacionGaveta(bdd);
			for(String query : queries)
				hm.createSQLQuery(query).executeUpdate();
			
			//log.trace("Inserción de "+g.getNombre()+" en imx_aplicacion");
			imx_aplicacion imx_aplicacion = new imx_aplicacion(g.getNombre(),"IMX"+g.getNombre(),g.getDescripcion());
			hm.getSession().save(imx_aplicacion);

			for (int i = 0; i < agc.size(); i++) {
				// creamos primero el indice para traer el nombre
				String nombre_indice = "";
				GavetaCampo gc = (GavetaCampo) agc.get(i);
				if (gc.getIndice() > 0) {
					//log.trace("Se creara indice "+gc.getIndice()+" a "+gc.getNombreCampo());
					Indice indice = new Indice(tbl_gaveta.getNombreTabla(), gc.getNombreCampo(), gc.getIndice());
					String query = getQueryCreateIndex(indice);
					if(query!=null) {
						hm.createSQLQuery(query).executeUpdate();
						nombre_indice = indice.getNombre();
					}
				}
				
				//log.trace("Inserción de "+gc.getNombreCampo()+" en imx_descripcion");
				imx_descripcion imx_descripcion = new imx_descripcion();
				imx_descripcion.setId(new imx_descripcion_id(g.getNombre(),gc.getNombreCampo()));
				imx_descripcion.setNombreColumna(gc.getNombre_desplegar());
				imx_descripcion.setPosicionCampo(i+1);
				imx_descripcion.setNombreTipoDatos(CatalogoDatos.getNombreCampoIMAX(gc.getTipoDato()));
				imx_descripcion.setIdTipoDatos(gc.getTipoDato());
				imx_descripcion.setLongitudCampo((int)gc.getTamano());
				imx_descripcion.setValorDefCampo(gc.getValPredefinido());
				imx_descripcion.setIndiceTipo(gc.getIndice());
				imx_descripcion.setMultivaluado('N');
				imx_descripcion.setRequerido(gc.getRequerido().charAt(0));
				imx_descripcion.setEditable(gc.getEditable().charAt(0));
				imx_descripcion.setLista(gc.getLista().charAt(0));
				imx_descripcion.setNombreIndice(nombre_indice);
				
				hm.getSession().save(imx_descripcion);
				
				// Asocia el campo con el catalogo.
				if ("S".equals(gc.getLista())) {
					log.trace("Asociación de "+gc.getNombreCampo()+" con catalogo "+gc.getNombreCatalogo());
					asociaGavetaCatalogo(g.getNombre(), gc.getNombreCampo(), gc.getNombreCatalogo());
				}
			}

			//log.trace("Sección de Seguridad");
			SeguridadGavetas sec = new SeguridadGavetas(g.getNombre().toUpperCase());
			ArrayList<SeguridadGavetas> permisos = sec.getSeguridadIMXDefault();

			for (int i = 0; i < permisos.size(); i++) {
				SeguridadGavetas nivel = permisos.get(i);
				//log.trace("Inserción de "+nivel.getNombre_nivel()+" en IMX_SEGURIDAD");
				imx_seguridad_id imx_seguridad_id = new imx_seguridad_id(nivel.getTitulo_aplicacion(),nivel.getPrioridad());
				imx_seguridad imx_seguridad = new imx_seguridad(imx_seguridad_id,nivel.getNombre_nivel(),nivel.getDescripcion());
				hm.getSession().save(imx_seguridad);
			}

			TipoDocumento tDocto = new TipoDocumento(g.getNombre().toUpperCase());
			ArrayList<TipoDocumento> doctos = tDocto.getTipoDocumentoIMXDefault();

			for (int i = 0; i < doctos.size(); i++) {
				TipoDocumento tipoDocumento = doctos.get(i);
				
				imx_tipo_documento_id imx_tipo_documento_id = new imx_tipo_documento_id(tipoDocumento.getTitulo_aplicacion(),tipoDocumento.getId_tipo_docto());
				imx_tipo_documento imx_tipo_documento = new imx_tipo_documento(imx_tipo_documento_id,tipoDocumento.getPrioridad(),tipoDocumento.getNombre_tipo_docto(),tipoDocumento.getDescripcion());
				hm.getSession().save(imx_tipo_documento);
			}
			hm.getSession().flush();
			log.info("----------COMMIT TRANSACTION----------");
			tx.commit();
			retVal = true;
		} catch (Exception e) {
			log.error(e, e);
			log.info("----------ROLLBACK TRANSACTION----------");
			tx.rollback();
			
			log.info("Borrando subproductos de la transacción");
			try{
				BigDecimal numero_registros = (BigDecimal)hm.createSQLQuery("SELECT COUNT(*) FROM IMX"+g.getNombre()).uniqueResult();
				if(numero_registros.longValue()==0&&!existeGaveta(g.getNombre()))
					hm.createSQLQuery("DROP TABLE IMX"+g.getNombre()).executeUpdate();
			} catch (Exception ex) {
				log.error(ex,ex);
			}	
		} finally {
			hm.close();
		}
		return retVal;
	}

	/**
	 * Actualiza la estructura de una gaveta. Identifica los cambios a la gaveta
	 * original, agregando/modificando los campos que lo requieran.
	 * 
	 * @param original
	 *            Gaveta original.
	 * @param modificada
	 *            Gaveta con modificaciones.
	 * @return El numero de campos actualizados/insertados.
	 * @throws Exception
	 *             Si no es posible actualizar/insertar los campos de la gaveta
	 *             por problemas con la base de datos. 4421280007
	 */
	public int actualizaGaveta(Gaveta original, Gaveta modificada, String[] nombresAnteriores) throws Exception {

		int totalModificados = 0;
		ArrayList<GavetaCampo> camposOriginales = original.getCampos();
		ArrayList<GavetaCampo> camposAgregados = modificada.getCampos();

		try{
		if (original.getDescripcion() == null)
			original.setDescripcion("");

		if (!original.getDescripcion().trim().equals(
				modificada.getDescripcion().trim()))
			modificaDescripcionGvta(modificada, modificada.getNombre());

		for (int i = 0; i < camposAgregados.size(); i++) {
			int idxOriginal = original.contieneCampo(nombresAnteriores[i]);

			if (idxOriginal >= 0) // Si el original ya contenia un campo con
									// este
			// nombre, lo modificamos.
			{
				GavetaCampo campoOriginal = camposOriginales.get(idxOriginal);
				GavetaCampo campoModificado = camposAgregados.get(i);

				if (!(campoOriginal.getNombre_campo().trim()
						.equalsIgnoreCase(campoModificado.getNombre_campo()
								.trim()))) {
					modficaNombreCampo(campoOriginal, campoModificado, original
							.getNombre());
				}
				if (!(campoOriginal.getNombre_desplegar().trim()
						.equalsIgnoreCase(campoModificado.getNombre_desplegar()
								.trim()))) {
					this.modficaNombreDesplegar(campoOriginal, campoModificado,
							original.getNombre());
				}
				String valorPredefinido = (campoOriginal.getVal_predefinido() == null ? ""
						: campoOriginal.getVal_predefinido());

				if (!valorPredefinido.trim().equalsIgnoreCase(
						campoModificado.getVal_predefinido().trim())) {
					this.modificaValPredefinido(campoOriginal, campoModificado,
							original.getNombre());
				}
				if (!(campoOriginal.getIndice() == campoModificado.getIndice())) {
					this.modificaIndice(campoOriginal, campoModificado,
							original.getNombre());
				}
				if (!(campoOriginal.getTipo_dato() == campoModificado
						.getTipo_dato()))/*
										 * || !( campoOriginal.getTamano() !=
										 * campoModificado.getTamano() ) )
										 */
				{
					log.debug("Cambio el tipo de dato "
							+ campoModificado.getTipo_dato());
					modificaTipoDatos(original, campoModificado);
				}
				if (!(campoOriginal.getTamano() == campoModificado.getTamano()))/*
																				 * ||
																				 * !
																				 * (
																				 * campoOriginal
																				 * .
																				 * getTamano
																				 * (
																				 * )
																				 * !=
																				 * campoModificado
																				 * .
																				 * getTamano
																				 * (
																				 * )
																				 * )
																				 * )
																				 */
				{
					log.debug("Cambio el tipo de dato "
							+ campoModificado.getTipo_dato());
					modificaTipoDatos(original, campoModificado);
				}

				if (!campoOriginal.getRequerido().equalsIgnoreCase(
						campoModificado.getRequerido())
						|| !campoOriginal.getEditable().equalsIgnoreCase(
								campoModificado.getEditable())
						|| !campoOriginal.getLista().equalsIgnoreCase(
								campoModificado.getLista())) {
					this.modificaRequeridoEditable(campoOriginal,
							campoModificado, original.getNombre());
				}
				
				//Para un campo modificado, siempre asociar/desasociar con lista.
				asociaGavetaCatalogo(original.getNombre(), campoModificado
						.getNombreCampo(), campoModificado
						.getNombreCatalogo());

			} 
			else {
				agregaNuevoCampo(original, camposAgregados.get(i), i);
				if ("S".equals(camposAgregados.get(i).getLista()))
					asociaGavetaCatalogo(original.getNombre(), camposAgregados
							.get(i).getNombreCampo(), camposAgregados.get(i)
							.getNombreCatalogo());
			}
		}
		}catch (Exception e) {
			log.error(e,e); 
		}finally{

		}
		
		return totalModificados;

	}

	@SuppressWarnings("unchecked")
	public boolean borraGaveta(String titulo_aplicacion) {
		boolean retVal = false;
		if (titulo_aplicacion.equalsIgnoreCase("USR_GRALES")) {
			throw new RuntimeException("Gaveta no elegible para ser eliminada");
		}
		HibernateManager hm = new HibernateManager();
		try {
			String sSelect = "SELECT p.nom_archivo_vol,v.volumen,v.unidad_disco,v.ruta_base,v.ruta_directorio "
					+ " FROM imx_pagina p, imx_volumen v "
					+ " WHERE titulo_aplicacion = ? "
					+ " AND p.volumen = v.volumen";

			Query query = hm.createSQLQuery(sSelect);
			query.setString(0, titulo_aplicacion);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String,String>> archivos = query.list();
			
			List<Query> queries = new ArrayList<Query>();

			String sDelete = "DELETE FROM imx_campo_catalogo WHERE titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);
			
			sDelete = "DELETE FROM imx_pagina WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_documento WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_org_carpeta WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_carpeta WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_descripcion WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_estruc_doctos WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_tipo_documento WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_seguridad WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_grupo_privilegio WHERE" + " titulo_aplicacion = ?";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);
			
			//TODO esto deberia hacerse por constraints on cascade
			//Ya se agrego para que borre privilegios de los grupos
			sDelete = "DELETE FROM imx_privilegio WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DELETE FROM imx_aplicacion WHERE " + " titulo_aplicacion = ? ";
			query = hm.createSQLQuery(sDelete);
			query.setString(0, titulo_aplicacion);
			queries.add(query);

			sDelete = "DROP TABLE IMX" + titulo_aplicacion;
			query = hm.createSQLQuery(sDelete);
			queries.add(query);
			
			hm.executeQueries(queries);

			retVal = true;
			//Ciclo para borrar los archivos al final de una transacción exitosa.
			for (Map<String,String> map : archivos) {
				map = new CaseInsensitiveMap(map);
				String directorio = map.get("unidad_disco")
						+ map.get("ruta_base")
						+ map.get("ruta_directorio")
						+ map.get("nom_archivo_vol");
				File f = new File(directorio);
				if (f.exists()) {
					if (!f.delete())
						f.deleteOnExit();
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		} finally {
			hm.close();
		}
		return retVal;
	}

	public Gaveta selectGaveta(String titulo_aplicacion) {
		Gaveta gaveta = null;
		HibernateManager hm = new HibernateManager();
		try {
			String sSelect = "FROM imx_aplicacion WHERE titulo_aplicacion = ? ";
			Query query = hm.createQuery(sSelect);
			query.setString(0, titulo_aplicacion);
			imx_aplicacion imx_aplicacion = (imx_aplicacion) query.uniqueResult();
			gaveta = new Gaveta(titulo_aplicacion, imx_aplicacion.getDescripcion());

			sSelect = "FROM imx_descripcion WHERE id.tituloAplicacion = ? ORDER BY posicion_campo";
			query = hm.createQuery(sSelect);
			query.setString(0, titulo_aplicacion);

			@SuppressWarnings("unchecked")
			List<imx_descripcion> list = query.list();
			for(imx_descripcion imx_descripcion : list) {
				// Agregamos el nombre del catalogo si lo tiene asociado
				String nombreCatalogo = null;

				if ("S".equals(""+imx_descripcion.getLista())) {
					String catQuery = "SELECT NOMBRE_CATALOGO "
							+ "FROM imx_campo_catalogo "
							+ "WHERE TITULO_APLICACION='" + titulo_aplicacion
							+ "' AND NOMBRE_CAMPO='"
							+ imx_descripcion.getId().getNombreCampo() + "' ";
					query = hm.createSQLQuery(catQuery);
					nombreCatalogo = (String) query.uniqueResult();
				}

				gaveta.addCampo(new GavetaCampo(
						imx_descripcion.getId().getNombreCampo(),
						imx_descripcion.getNombreColumna(),
						imx_descripcion.getValorDefCampo(),
						Utils.getDouble(imx_descripcion.getLongitudCampo()),
						imx_descripcion.getIdTipoDatos(),
						imx_descripcion.getIndiceTipo(),
						""+imx_descripcion.getRequerido(),
						""+imx_descripcion.getEditable(),
						""+imx_descripcion.getLista(),
						imx_descripcion.getPosicionCampo(),
						nombreCatalogo
						)
				);
			}
		} finally {
			hm.close();
		}
		return gaveta;
	}

	public boolean modficaNombreCampo(GavetaCampo campoAnterior, GavetaCampo campoActual, String titulo_aplicacion) {
		boolean retVal = false;
		String nombreTabla = "IMX" + titulo_aplicacion.toUpperCase();

		HibernateManager hm = new HibernateManager();
		try {
			List<Query> queries = new ArrayList<Query>();
			
			String sUpdate = "UPDATE IMX_DESCRIPCION "
					+ "SET NOMBRE_CAMPO = ? " + "WHERE NOMBRE_CAMPO = ? "
					+ "AND TITULO_APLICACION = ?";
			
			Query query = hm.createSQLQuery(sUpdate);
			query.setString(0, campoActual.getNombre_campo());
			query.setString(1, campoAnterior.getNombre_campo());
			query.setString(2, titulo_aplicacion);
			queries.add(query);

			String sAlter = "ALTER TABLE " + nombreTabla + " RENAME COLUMN "
					+ campoAnterior.getNombre_campo() + " to "
					+ campoActual.getNombre_campo();
			query = hm.createSQLQuery(sAlter);
			queries.add(query);
			/*
			 * prep.setString( 1, nombreTabla ); prep.setString( 2,
			 * campoAnterior.getNombre_campo() ); prep.setString( 3,
			 * campoActual.getNombre_campo() );
			 */
			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean modficaNombreDesplegar(GavetaCampo campoAnterior, GavetaCampo campoActual, String titulo_aplicacion) {
		boolean retVal = false;
		titulo_aplicacion = titulo_aplicacion.toUpperCase();

		HibernateManager hm = new HibernateManager();
		try {
			String sUpdate = "UPDATE IMX_DESCRIPCION "
					+ "SET NOMBRE_COLUMNA = ? " + "WHERE NOMBRE_COLUMNA = ? "
					+ "AND TITULO_APLICACION = ?";

			Query query = hm.createSQLQuery(sUpdate);
			query.setString(0, campoActual.getNombre_desplegar());
			query.setString(1, campoAnterior.getNombre_desplegar());
			query.setString(2, titulo_aplicacion);
			hm.executeQuery(query);

			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean modificaValPredefinido(GavetaCampo campoAnterior, GavetaCampo campoActual, String titulo_aplicacion) {
		boolean retVal = false;
		titulo_aplicacion = titulo_aplicacion.toUpperCase();

		HibernateManager hm = new HibernateManager();
		try {
			String sUpdate = "UPDATE IMX_DESCRIPCION "
					+ "SET valor_def_campo = ? " + "WHERE nombre_campo = ? "
					+ "AND TITULO_APLICACION = ?";

			Query query = hm.createSQLQuery(sUpdate);
			query.setString(0, campoActual.getVal_predefinido());
			query.setString(1, campoAnterior.getNombre_campo());
			query.setString(2, titulo_aplicacion);
			hm.executeQuery(query);

			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean modificaIndice(GavetaCampo campoAnterior, GavetaCampo campoActual, String titulo_aplicacion) throws Exception {
		boolean retVal = false;
		titulo_aplicacion = titulo_aplicacion.toUpperCase();

		HibernateManager hm = new HibernateManager();
		try {
			List<Query> queries = new ArrayList<Query>();
			// Verificar primero si tiene indice, si es asi, eliminarlo.
			if (campoAnterior.getIndice() > 0) {
				Indice indice = new Indice("IMX" + titulo_aplicacion,
						campoAnterior.getNombreCampo(), campoAnterior
								.getIndice());
				String nombre_indice = (String) hm.createSQLQuery(getQueryNombreIndex(indice)).uniqueResult();
				indice.setNombre(nombre_indice);
				if(nombre_indice!=null && !"".equals(nombre_indice))
					for (String sqlQuery : getQueryDropIndex(indice))
						queries.add(hm.createSQLQuery(sqlQuery));
			}

			Indice indice = new Indice("IMX" + titulo_aplicacion, campoActual
					.getNombreCampo(), campoActual.getIndice());
			String createIndex = getQueryCreateIndex(indice);
			
			if (createIndex != null) {
				queries.add(hm.createSQLQuery(createIndex));
				
				String sUpdate = "UPDATE IMX_DESCRIPCION "
						+ "SET NOMBRE_INDICE = '" + indice.getNombre()
						+ "', INDICE_TIPO = " + campoActual.getIndice() + " "
						+ "WHERE NOMBRE_CAMPO =  '"
						+ campoActual.getNombre_campo() + "' "
						+ "AND UPPER(TITULO_APLICACION) = UPPER('"
						+ titulo_aplicacion + "')";
				Query query = hm.createSQLQuery(sUpdate);
				queries.add(query);
			}
			hm.executeQueries(queries);
			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean modificaRequeridoEditable(GavetaCampo campoAnterior, GavetaCampo campoActual, String titulo_aplicacion) {
		boolean retVal = false;
		titulo_aplicacion = titulo_aplicacion.toUpperCase();

		HibernateManager hm = new HibernateManager();
		try {
			String sUpdate = "UPDATE IMX_DESCRIPCION "
					+ "SET REQUERIDO = ?, EDITABLE = ?, LISTA = ? "
					+ "WHERE (TITULO_APLICACION) = (?) "
					+ "AND (NOMBRE_CAMPO)=  (?)";

			Query query = hm.createSQLQuery(sUpdate);
			query.setString(0, campoActual.getRequerido());
			query.setString(1, campoActual.getEditable());
			query.setString(2, campoActual.getLista());
			query.setString(3, titulo_aplicacion);
			query.setString(4, campoActual.getNombre_campo());
			hm.executeQuery(query);
			
			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean modificaDescripcionGvta(Gaveta cActual, String titulo_aplicacion) {
		boolean retVal = false;
		titulo_aplicacion = titulo_aplicacion.toUpperCase();

		HibernateManager hm = new HibernateManager();
		try {
			String sUpdate = "UPDATE IMX_APLICACION SET DESCRIPCION = ? "
					+ "WHERE UPPER(TITULO_APLICACION) = UPPER(?)";

			Query query = hm.createSQLQuery(sUpdate);
			query.setString(0, cActual.getDescripcion());
			query.setString(1, titulo_aplicacion);
			hm.executeQuery(query);

			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean agregaNuevoCampo(Gaveta gaveta, GavetaCampo campo, int posicion) throws Exception {
		boolean retVal = false;

		HibernateManager hm = new HibernateManager();
		try {
			String sSelect = "SELECT max(posicion_campo) "
					+ "  FROM imx_descripcion "
					+ " WHERE titulo_aplicacion = '" + gaveta.getNombre() + "'";
			Query query = hm.createSQLQuery(sSelect);
			Integer maxCampo = Utils.getInteger(query.uniqueResult());
			if (maxCampo!=null)
				posicion = ((maxCampo > 0) ? maxCampo : 0);
			
			CatalogoDatos catalogo = new CatalogoDatos();

			List<Query> queries = new ArrayList<Query>();
			
			String sInsert = "ALTER TABLE IMX"
					+ gaveta.getNombre()
					+ " ADD ( "
					+ campo.getNombre_campo()
					+ "  "
					+ catalogo.getNombreTipoCampo(campo.getTipo_dato())
					+ (catalogo.llevaLongitud(campo.getTipo_dato()) ? ("("
							+ Math.round(campo.getTamano()) + ") ") : "")
					+ ("S".equalsIgnoreCase(campo.getRequerido()) ? " NOT NULL "
							: "") + " )";

			query = hm.createSQLQuery(sInsert);
			queries.add(query);

			String nombre_indice = "";
			if (campo.getIndice() > 0) {
				Indice indice = new Indice("IMX"
						+ gaveta.getNombre().toUpperCase(), campo
						.getNombreCampo(), campo.getIndice());
				String createIndex = getQueryCreateIndex(indice);
				if(createIndex!=null) {
					query = hm.createSQLQuery(createIndex);
					queries.add(query);
					nombre_indice = indice.getNombre();
				}
				
			}

			sInsert = "INSERT INTO imx_descripcion (titulo_aplicacion,nombre_campo,nombre_columna,"
					+ " posicion_campo,nombre_tipo_datos,id_tipo_datos,longitud_campo,valor_def_campo, "
					+ " indice_tipo,multivaluado,requerido,editable,lista,nombre_indice) "
					+ " VALUES ('"
					+ gaveta.getNombre()
					+ "', '"
					+ campo.getNombreCampo()
					+ "', '"
					+ campo.getNombreDesplegar()
					+ "' ,"
					+ (posicion + 1)
					+ ", '"
					+ CatalogoDatos.getNombreCampoIMAX(campo.getTipoDato())
					+ "', '"
					+ campo.getTipoDato()
					+ "' ,"
					+ campo.getTamano()
					+ ", '"
					+ campo.getValPredefinido()
					+ "',"
					+ campo.getIndice()
					+ ",'N', '"
					+ campo.getRequerido()
					+ "', '"
					+ campo.getEditable()
					+ "', '"
					+ campo.getLista()
					+ "', '" + nombre_indice + "' )";

			query = hm.createSQLQuery(sInsert);
			queries.add(query);
			
			hm.executeQueries(queries);

			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean eliminaCampo(Gaveta gaveta, GavetaCampo campo) {
		boolean retVal = false;

		HibernateManager hm = new HibernateManager();
		try {
			/* BaseDeDatosManager bddm = */ //new BaseDeDatosManager();
			/* String vend = */ // conn.getMetaData().getDatabaseProductName();

			// CatalogoDatos catalogo = new CatalogoDatos(vend);

			List<Query> queries = new ArrayList<Query>();
			
			// eliminamos la asociacion con la lista si existe
			if ("S".equalsIgnoreCase(campo.getLista()))
				retVal = this.eliminaGavetaCatalogo(gaveta.getNombre(), campo
						.getNombre_campo(), campo.getLista());

			String sDrop = "ALTER TABLE IMX" + gaveta.getNombre()
					+ " DROP COLUMN " + campo.getNombre_campo();

			Query query = hm.createSQLQuery(sDrop);
			queries.add(query);

			sDrop = "DELETE FROM IMX_DESCRIPCION "
					+ "WHERE TITULO_APLICACION = '" + gaveta.getNombre() + "' "
					+ "AND NOMBRE_CAMPO = '" + campo.getNombre_campo() + "'";

			query = hm.createSQLQuery(sDrop);
			queries.add(query);
			
			hm.executeQueries(queries);

			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	public boolean modificaTipoDatos(Gaveta gaveta, GavetaCampo campo) throws Exception {
		boolean retVal = false;

		HibernateManager hm = new HibernateManager();
		try {
			/* BaseDeDatosManager bddm = */ //new BaseDeDatosManager();
			CatalogoDatos catalogo = new CatalogoDatos();
			
			
			List<Query> queries = new ArrayList<Query>();

			String sInsert = "ALTER TABLE IMX"
					+ gaveta.getNombre()
					+ " modify "
					+ campo.getNombre_campo()
					+ " "
					+ catalogo.getNombreTipoCampo(campo.getTipo_dato())
					+ (catalogo.llevaLongitud(campo.getTipo_dato()) ? ("("
							+ campo.getTamano() + ") ") : "");


			Query query = hm.createSQLQuery(sInsert);
			queries.add(query);

			sInsert = "UPDATE IMX_DESCRIPCION SET " + "NOMBRE_TIPO_DATOS = '"
					+ catalogo.getNombreTipoCampo(campo.getTipo_dato()) + "', "
					+ "ID_TIPO_DATOS = " + campo.getTipo_dato() + ", "
					+ "LONGITUD_CAMPO = " + campo.getTamano() + " "
					+ "WHERE TITULO_APLICACION = '" + gaveta.getNombre() + "' "
					+ "AND NOMBRE_CAMPO = '" + campo.getNombreCampo() + "'";

			query = hm.createSQLQuery(sInsert);
			queries.add(query);
			
			hm.executeQueries(queries);

			retVal = true;
		} finally {
			hm.close();
		}
		return retVal;
	}

	private boolean asociaGavetaCatalogo(String gaveta, String campo, String catalogo) {
		boolean exito = true;
		// Primero borramos lista asociada anterior
		exito = this.eliminaGavetaCatalogo(gaveta, campo, catalogo);
		
		if(!catalogo.isEmpty()){
			HibernateManager hm = new HibernateManager();
			try {
				imx_campo_catalogo_id imx_campo_catalogo_id = new imx_campo_catalogo_id(gaveta, campo, catalogo);
				imx_campo_catalogo imx_campo_catalogo = new imx_campo_catalogo(imx_campo_catalogo_id);
				hm.save(imx_campo_catalogo);
			} catch (Exception e) {
				exito = false;
				log.error(e,e);
				log.error("Campo no asociado al catalogo. Causa: "+ e.getMessage());
			} finally {
				hm.close();
			}
		}

		return exito;
	}

	private boolean eliminaGavetaCatalogo(String gaveta, String campo, String catalogo) {
		boolean exito = false;
		HibernateManager hm = new HibernateManager();
		try {
			hm.executeQuery(hm.createSQLQuery("DELETE FROM IMX_CAMPO_CATALOGO "
					+ "WHERE TITULO_APLICACION='" + gaveta + "' AND NOMBRE_CAMPO='"
					+ campo + "'"));
			exito = true;
		} catch (Exception e) {
			log.error(e,e);
			log.error("No se puede borrar la lista asociada anterior. Causa: "
					+ e.getMessage());
		} finally {
			hm.close();
		}
		return exito;
	}
	
	public String getQueryCreateIndex(Indice indice) throws Exception {
		int tipo_indice = indice.getTipoIndice();

		if (tipo_indice < 1)
			return null;

			String sCreate = "CREATE ";

			if (tipo_indice == 2) {// unique
				sCreate += " UNIQUE ";
			}

			sCreate += " INDEX " + indice.getNombre() + " ON " + indice.getTabla()
					+ " (" + indice.getCampo() + ")";

		return sCreate;
	}
	
	public String getQueryNombreIndex(Indice indice) {
		String sWhere = "WHERE NOMBRE_CAMPO = '"
				+ indice.getCampo()
				+ "' "
				+ "AND UPPER( TITULO_APLICACION )= UPPER('"
				+ indice.getTabla().replace("IMX", "") + "')";
		String sNombreIndice = "SELECT NOMBRE_INDICE FROM IMX_DESCRIPCION "+sWhere;
		return sNombreIndice;
	}

	public List<String> getQueryDropIndex(Indice indice) {
		List<String> queries = new ArrayList<String>();

			String sDrop = "DROP INDEX ";	
			String sWhere = "WHERE NOMBRE_CAMPO = '"
				+ indice.getCampo()
				+ "' "
				+ "AND UPPER( TITULO_APLICACION )= UPPER('"
				+ indice.getTabla().replace("IMX", "") + "')";
			
			String sUpdate = "UPDATE IMX_DESCRIPCION SET NOMBRE_INDICE = '', INDICE_TIPO = 0 "+sWhere;
			
			if(Config.database.equals(Config.Database.MYSQL))
				sDrop += indice.getNombre()+" ON "+indice.getTabla();
			else
				sDrop += indice.getTabla()+".\""+indice.getNombre()+"\"";
			
			queries.add(sUpdate);
			queries.add(sDrop);
			return queries;
	}

}