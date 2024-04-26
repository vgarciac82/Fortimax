package com.syc.tree;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.jdom.Attribute;
import org.jdom.Element;

import com.jenkov.prizetags.tree.impl.Tree;
import com.jenkov.prizetags.tree.impl.TreeNode;
import com.jenkov.prizetags.tree.impl.TreeTableMapping;
import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.entities.Privilegio;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_seguridad;
import com.syc.fortimax.hibernate.managers.imx_seguridad_manager;
import com.syc.fortimax.managers.PrivilegioManager;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.utils.DateTimeUtils;
import com.syc.utils.ParametersInterface;
import com.syc.utils.Utils;

public class ArbolManager implements ParametersInterface, ITreeNodeProcessor {
	private static final Logger log = Logger.getLogger(ArbolManager.class);

	private String idColumn = "idColumn";
	private String parentIdColumn = "parentIdColumn";
	private String treeIdColumn = null;
	private String nameColumn = "nameColumn";
	private String typeColumn = "typeColumn";
	private String toolTipColumn = null;

	private String titulo_aplicacion = "";
	private int id_gabinete = -1;
	private int id_carpeta = 0;

	public ArbolManager(String titulo_aplicacion, int id_gabinete) {
		this.titulo_aplicacion = titulo_aplicacion;
		this.id_gabinete = id_gabinete;
	}

	public ArbolManager(String nodo) {
		GetDatosNodo gdn = new GetDatosNodo(nodo);
		gdn.separaDatosCarpeta();
		this.titulo_aplicacion = gdn.getGaveta();
		this.id_gabinete = gdn.getGabinete();
		this.id_carpeta = gdn.getIdCarpeta();
	}

	public static String generaIdNodeCarpeta(String titulo_aplicacion,
			int id_gabinete, int id_carpeta) {
		return titulo_aplicacion + "_G" + id_gabinete + "C" + id_carpeta;
	}

	public static String generaIdNodeDocumento(String titulo_aplicacion,
			int id_gabinete, int id_carpeta_padre, int id_documento) {
		return titulo_aplicacion + "_G" + id_gabinete + "C" + id_carpeta_padre
				+ "D" + id_documento;
	}

	public ITree generaGaveta(String nombre_usuario, String descripcion) {
		long start = System.currentTimeMillis();

		// ResultSet rsCount = null;
		// Aplicacion g = null;
		ITree tree = new Tree();
		ITreeNode rootNode = new TreeNode("Gaveta", descripcion, "gaveta.root");

		tree.setSingleSelectionMode(true);
		tree.addCollapseListener(GavCollapseListener);
		tree.addExpandListener(GavExpandListener);
		tree.addSelectListener(GavSelectListener);
		tree.addUnSelectListener(GavUnselectListener);

		try {

			ArrayList<Privilegio> p = PrivilegioManager
					.getPrivilegios(nombre_usuario);

			for (Privilegio privilegio : p) {
				
				if (privilegio.getTituloAplicacion().equals("USR_GRALES")) {
					log.debug("Se Excluye la gaveta de usr_grales en el arbol");
				} else {

					ITreeNode n = new TreeNode(
							privilegio.getTituloAplicacion(),
							privilegio.getTituloAplicacion(), "gaveta.hija");
					n.setObject(new imx_aplicacion(privilegio.getTituloAplicacion(), null, privilegio.getDescripcion()));
					n.setName(privilegio.getTituloAplicacion());
					n.setToolTip(privilegio.getDescripcion());
					rootNode.addChild(n);
				}

			}

		} catch (Exception se) {
			log.error(se, se);
		} finally {
			tree.setRoot(rootNode);

			if (log.isDebugEnabled()) {
				long end = (System.currentTimeMillis() - start);
				String unit = " mls.";
				if (end >= 1000) {
					unit = " seg.";
					end = (end / 1000);
				}
				log.debug("(" + descripcion + ") Tiempo transcurrido: " + end
						+ unit);
			}
		}
		return tree;
	}

	@SuppressWarnings("unchecked")
	public String ObtenMatriculaDocumento(String nombre_usuario, String Titulo_Aplicacion, int Id_Gabinete, String Nom_Expediente) {

		String Matricula_Armada = null;

		int prioridad_usuario = -1;
		HibernateManager hm = new HibernateManager();
		try {
			// stmnt = conn.createStatement();

			ArrayList<imx_seguridad> seguridad = imx_seguridad_manager.get(
					nombre_usuario, titulo_aplicacion);

			if (seguridad.isEmpty() == false) {
				prioridad_usuario = seguridad.get(0).getId().getPrioridad();
			}

			StringBuffer sql = new StringBuffer();

			if (Config.database.equals(Config.Database.ORACLE)) { // TODO cambiar por HQL
				if (Nom_Expediente == "") {
					sql.append("SELECT CAST(NULL AS VARCHAR2 (99)) AS parentidcolumn, c.titulo_aplicacion || '_G' || RTRIM(CAST(c.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(c.id_carpeta AS CHAR (20))) AS idcolumn, c.nombre_carpeta AS namecolumn, DECODE(1 , DECODE( 1 , DECODE( c.titulo_aplicacion , 'USR_GRALES' ,  1, 0) , 1 , 0 ) , 'carpeta.root',  'carpeta.exproot') AS typecolumn, 0 AS typerow, c.titulo_aplicacion AS titulo_aplicacion, c.id_gabinete AS id_gabinete, 1 AS id_carpeta_padre,  c.id_carpeta AS id_carpeta_hija, 1 AS numero_pagina,  c.nombre_usuario AS nombre_usuario, c.fh_creacion AS fh_creacion, c.fh_modificacion AS fh_modificacion,  c.numero_accesos AS numero_accesos, c.numero_carpetas AS totchildcolumn, c.bandera_raiz AS bandera_raiz, c.numero_documentos AS numero_documentos, c.descripcion AS descripcion, c.password AS password, 0 AS prioridad, 0 AS id_tipo_docto, CAST(NULL AS VARCHAR2 (32)) AS nombre_tipo_docto, CAST(NULL AS VARCHAR2 (25)) AS titulo, CAST(NULL AS VARCHAR2 (25)) AS autor, CAST(NULL AS VARCHAR2 (25)) AS materia,  CAST(NULL AS DECIMAL (31)) AS clase_documento,  CAST(NULL AS CHAR (1)) AS estado_documento, CAST(0 AS DECIMAL (31)) AS tamano_bytes_docto,  CAST(NULL AS VARCHAR2 (1)) AS tipo_pagina,  CAST(NULL AS VARCHAR2 (8)) AS volumen,  CAST(NULL AS VARCHAR2 (1)) AS tipo_volumen, CAST(NULL AS VARCHAR2 (50)) AS unidad_disco, CAST(NULL AS VARCHAR2 (50)) AS ruta_base, CAST(NULL AS VARCHAR2 (70)) AS ruta_directorio, CAST(NULL AS VARCHAR2 (257)) AS nom_archivo_vol, CAST(NULL AS VARCHAR2 (257)) AS nom_archivo_org, CAST(NULL AS VARCHAR2 (1)) AS estado_pagina, CAST(0 AS DECIMAL (31)) AS tamano_bytes,  CAST(NULL AS VARCHAR2 (1)) AS compartir, CAST(NULL AS VARCHAR2 (80)) AS token_compartir, CAST(NULL AS DATE ) AS FECHA_EXPIRA, CAST(NULL AS DATE ) AS FECHA_COMPARTIDO, CAST(NULL AS VARCHAR2(6) ) AS DIAS_PERMITIDOS ");
					sql.append("FROM imx_carpeta c ");
					sql.append("WHERE c.bandera_raiz = 'S' ");
					sql.append(" AND (titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND id_gabinete = "
							+ id_gabinete + " AND    (nombre_usuario = '"
							+ nombre_usuario + "' OR  0 <= "
							+ prioridad_usuario + ") )");

					sql.append(" union ");

					sql.append("SELECT oc.titulo_aplicacion || '_G' || RTRIM(CAST(oc.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(oc.id_carpeta_padre AS CHAR (20))) AS parentidcolumn, oc.titulo_aplicacion || '_G' || RTRIM(CAST(oc.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(oc.id_carpeta_hija AS CHAR (20))) AS idcolumn, oc.nombre_hija AS namecolumn, 'carpeta.' || DECODE(1 , DECODE( 1 , DECODE( c.password , '-1' , 1, 0) , 1 , 0 ) , 'hija', 'prtgda') AS typecolumn, 1 AS typerow, oc.titulo_aplicacion AS titulo_aplicacion, oc.id_gabinete AS id_gabinete, oc.id_carpeta_padre AS id_carpeta_padre, oc.id_carpeta_hija AS id_carpeta_hija, 1 AS numero_pagina, c.nombre_usuario AS nombre_usuario, c.fh_creacion AS fh_creacion, c.fh_modificacion AS fh_modificacion, c.numero_accesos AS numero_accesos, c.numero_carpetas AS totchildcolumn, c.bandera_raiz AS bandera_raiz, c.numero_documentos AS numero_documentos, c.descripcion AS descripcion, c.password AS password, 0 AS prioridad, 0 AS id_tipo_docto, CAST(NULL AS VARCHAR2 (32)) AS nombre_tipo_docto, CAST(NULL AS VARCHAR2 (25)) AS titulo, CAST(NULL AS VARCHAR2 (25)) AS autor, CAST(NULL AS VARCHAR2 (25)) AS materia, CAST(NULL AS DECIMAL (31)) AS clase_documento, CAST(NULL AS CHAR (1)) AS estado_documento, CAST(0 AS DECIMAL (31)) AS tamano_bytes_docto, CAST(NULL AS VARCHAR2 (1)) AS tipo_pagina, CAST(NULL AS VARCHAR2 (8)) AS volumen, CAST(NULL AS VARCHAR2 (1)) AS tipo_volumen, CAST(NULL AS VARCHAR2 (50)) AS unidad_disco, CAST(NULL AS VARCHAR2 (50)) AS ruta_base, CAST(NULL AS VARCHAR2 (70)) AS ruta_directorio, CAST(NULL AS VARCHAR2 (257)) AS nom_archivo_vol, CAST(NULL AS VARCHAR2 (257)) AS nom_archivo_org, CAST(NULL AS VARCHAR2 (1)) AS estado_pagina, CAST(0 AS DECIMAL (31)) AS tamano_bytes, CAST(NULL AS VARCHAR2 (1)) AS compartir, CAST(NULL AS VARCHAR2 (80)) AS token_compartir, CAST(NULL AS DATE ) AS FECHA_EXPIRA, CAST(NULL AS DATE ) AS FECHA_COMPARTIDO, CAST(NULL AS VARCHAR2(6) ) AS DIAS_PERMITIDOS ");
					sql.append("FROM imx_carpeta c,  imx_org_carpeta oc ");
					sql.append("WHERE c.titulo_aplicacion = oc.titulo_aplicacion  AND c.id_gabinete = oc.id_gabinete  AND c.id_carpeta = oc.id_carpeta_hija");

					sql.append(" AND  (oc.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND oc.id_gabinete = "
							+ id_gabinete + " AND    (nombre_usuario = '"
							+ nombre_usuario + "' OR  0 <= "
							+ prioridad_usuario + ") )");

					sql.append(" union ");

					sql.append("SELECT d.titulo_aplicacion || '_G' || RTRIM(CAST(d.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(d.id_carpeta_padre AS CHAR (20))) AS parentidcolumn, d.titulo_aplicacion || '_G' || RTRIM(CAST(d.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(d.id_carpeta_padre AS CHAR (20))) || 'D' || RTRIM(CAST(d.id_documento AS CHAR (20))) AS idcolumn, d.nombre_documento AS namecolumn, 'docto.' || CASE WHEN d.numero_paginas > 1 THEN 'frtimx' ELSE CASE p.tipo_pagina WHEN 'I' THEN 'frtimx' ELSE CASE td.nombre_tipo_docto WHEN 'IMAX_FILE' THEN 'frtimx' ELSE 'externo' END END END AS typecolumn, 2 AS typerow, d.titulo_aplicacion, d.id_gabinete, d.id_carpeta_padre, d.id_documento AS id_carpeta_hija, p.numero_pagina, d.nombre_usuario, d.fh_creacion, d.fh_modificacion, d.numero_accesos, d.numero_paginas AS totchildcolumn, 'N' AS bandera_raiz, 0 AS numero_documentos,  d.descripcion, CAST('-1' AS VARCHAR2 (32)) AS password, d.prioridad, d.id_tipo_docto, td.nombre_tipo_docto, d.titulo, d.autor, d.materia, d.clase_documento, d.estado_documento, d.tamano_bytes AS tamano_bytes_docto, p.tipo_pagina, p.volumen, p.tipo_volumen, v.unidad_disco,  v.ruta_base,  v.ruta_directorio,  p.nom_archivo_vol,  p.nom_archivo_org,   p.estado_pagina,   p.tamano_bytes,  DECODE(d.compartir ,CAST(NULL AS VARCHAR2 (1)) ,'N',d.compartir),   d.token_compartir ,    d.FECHA_EXPIRA,    d.FECHA_COMPARTIDO,   d.DIAS_PERMITIDOS");
					sql.append(" FROM imx_pagina p, imx_documento d, imx_volumen v, imx_tipo_documento td ");
					sql.append("WHERE (p.titulo_aplicacion (+) = d.titulo_aplicacion AND p.id_gabinete (+) = d.id_gabinete AND p.id_carpeta_padre (+) = d.id_carpeta_padre AND p.id_documento (+) = d.id_documento) AND ((v.volumen (+) = p.volumen)) AND (td.titulo_aplicacion = d.titulo_aplicacion AND td.id_tipo_docto = d.id_tipo_docto)");
					sql.append(" AND (d.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND d.id_gabinete = "
							+ id_gabinete + " AND    (nombre_usuario = '"
							+ nombre_usuario + "' OR  d.prioridad <= "
							+ prioridad_usuario + "))");

					sql.append(" ORDER BY typerow, id_carpeta_padre, id_carpeta_hija, numero_pagina");

				}

				else

				{

					sql.append("SELECT oc.titulo_aplicacion || '_G' || RTRIM(CAST(oc.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(oc.id_carpeta_padre AS CHAR (20))) AS parentidcolumn, oc.titulo_aplicacion || '_G' || RTRIM(CAST(oc.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(oc.id_carpeta_hija AS CHAR (20))) AS idcolumn, oc.nombre_hija AS namecolumn, 'carpeta.' || DECODE(1 , DECODE( 1 , DECODE( c.password , '-1' , 1, 0) , 1 , 0 ) , 'hija', 'prtgda') AS typecolumn, 1 AS typerow, oc.titulo_aplicacion AS titulo_aplicacion, oc.id_gabinete AS id_gabinete, oc.id_carpeta_padre AS id_carpeta_padre, oc.id_carpeta_hija AS id_carpeta_hija, 1 AS numero_pagina, c.nombre_usuario AS nombre_usuario, c.fh_creacion AS fh_creacion, c.fh_modificacion AS fh_modificacion, c.numero_accesos AS numero_accesos, c.numero_carpetas AS totchildcolumn, c.bandera_raiz AS bandera_raiz, c.numero_documentos AS numero_documentos, c.descripcion AS descripcion, c.password AS password, 0 AS prioridad, 0 AS id_tipo_docto, CAST(NULL AS VARCHAR2 (32)) AS nombre_tipo_docto, CAST(NULL AS VARCHAR2 (25)) AS titulo, CAST(NULL AS VARCHAR2 (25)) AS autor, CAST(NULL AS VARCHAR2 (25)) AS materia, CAST(NULL AS DECIMAL (31)) AS clase_documento, CAST(NULL AS CHAR (1)) AS estado_documento, CAST(0 AS DECIMAL (31)) AS tamano_bytes_docto, CAST(NULL AS VARCHAR2 (1)) AS tipo_pagina, CAST(NULL AS VARCHAR2 (8)) AS volumen, CAST(NULL AS VARCHAR2 (1)) AS tipo_volumen, CAST(NULL AS VARCHAR2 (50)) AS unidad_disco, CAST(NULL AS VARCHAR2 (50)) AS ruta_base, CAST(NULL AS VARCHAR2 (70)) AS ruta_directorio, CAST(NULL AS VARCHAR2 (257)) AS nom_archivo_vol, CAST(NULL AS VARCHAR2 (257)) AS nom_archivo_org, CAST(NULL AS VARCHAR2 (1)) AS estado_pagina, CAST(0 AS DECIMAL (31)) AS tamano_bytes, CAST(NULL AS VARCHAR2 (1)) AS compartir, CAST(NULL AS VARCHAR2 (80)) AS token_compartir, CAST(NULL AS DATE ) AS FECHA_EXPIRA, CAST(NULL AS DATE ) AS FECHA_COMPARTIDO, CAST(NULL AS VARCHAR2(6) ) AS DIAS_PERMITIDOS ");
					sql.append("FROM imx_carpeta c,  imx_org_carpeta oc ");
					sql.append("WHERE c.titulo_aplicacion = oc.titulo_aplicacion  AND c.id_gabinete = oc.id_gabinete  AND c.id_carpeta = oc.id_carpeta_hija");

					sql.append(" AND  (oc.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND oc.id_gabinete = "
							+ id_gabinete + " AND nombre_carpeta = '"
							+ Nom_Expediente + "' AND (nombre_usuario = '"
							+ nombre_usuario + "' OR  0 <= "
							+ prioridad_usuario + ") )");

					sql.append(" union ");

					sql.append("SELECT d.titulo_aplicacion || '_G' || RTRIM(CAST(d.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(d.id_carpeta_padre AS CHAR (20))) AS parentidcolumn, d.titulo_aplicacion || '_G' || RTRIM(CAST(d.id_gabinete AS CHAR (20))) || 'C' || RTRIM(CAST(d.id_carpeta_padre AS CHAR (20))) || 'D' || RTRIM(CAST(d.id_documento AS CHAR (20))) AS idcolumn, d.nombre_documento AS namecolumn, 'docto.' || CASE WHEN d.numero_paginas > 1 THEN 'frtimx' ELSE CASE p.tipo_pagina WHEN 'I' THEN 'frtimx' ELSE CASE td.nombre_tipo_docto WHEN 'IMAX_FILE' THEN 'frtimx' ELSE 'externo' END END END AS typecolumn, 2 AS typerow, d.titulo_aplicacion, d.id_gabinete, d.id_carpeta_padre, d.id_documento AS id_carpeta_hija, p.numero_pagina, d.nombre_usuario, d.fh_creacion, d.fh_modificacion, d.numero_accesos, d.numero_paginas AS totchildcolumn, 'N' AS bandera_raiz, 0 AS numero_documentos,  d.descripcion, CAST('-1' AS VARCHAR2 (32)) AS password, d.prioridad, d.id_tipo_docto, td.nombre_tipo_docto, d.titulo, d.autor, d.materia, d.clase_documento, d.estado_documento, d.tamano_bytes AS tamano_bytes_docto, p.tipo_pagina, p.volumen, p.tipo_volumen, v.unidad_disco,  v.ruta_base,  v.ruta_directorio,  p.nom_archivo_vol,  p.nom_archivo_org,   p.estado_pagina,   p.tamano_bytes,  DECODE(d.compartir ,CAST(NULL AS VARCHAR2 (1)) ,'N',d.compartir),   d.token_compartir ,    d.FECHA_EXPIRA,    d.FECHA_COMPARTIDO,   d.DIAS_PERMITIDOS");
					sql.append(" FROM imx_pagina p, imx_documento d, imx_volumen v, imx_tipo_documento td ");
					sql.append("WHERE (p.titulo_aplicacion (+) = d.titulo_aplicacion AND p.id_gabinete (+) = d.id_gabinete AND p.id_carpeta_padre (+) = d.id_carpeta_padre AND p.id_documento (+) = d.id_documento) AND ((v.volumen (+) = p.volumen)) AND (td.titulo_aplicacion = d.titulo_aplicacion AND td.id_tipo_docto = d.id_tipo_docto)");
					sql.append(" AND (d.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND d.id_gabinete = "
							+ id_gabinete + " AND d.nombre_documento = '"
							+ Nom_Expediente + "'" + " AND (nombre_usuario = '"
							+ nombre_usuario + "' OR  d.prioridad <= "
							+ prioridad_usuario + "))");

					sql.append(" ORDER BY typerow, id_carpeta_padre, id_carpeta_hija, numero_pagina");

				}

			} 
			else if (Config.database.equals(Config.Database.MSSQL)){
				log.trace("Utiliza MSSQL");
				if (Nom_Expediente == "") {
					
					sql.append("SELECT 		 CAST(NULL  AS VARCHAR (99)) AS parentidcolumn, 		 CAST(c.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(c.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(c.id_carpeta  AS CHAR (20)))  AS VARCHAR) AS idcolumn, 		 c.nombre_carpeta AS namecolumn, 		  		CASE 1  			WHEN  					CASE 1  						WHEN  								CASE c.titulo_aplicacion  									WHEN 'USR_GRALES' THEN 1  									ELSE 0  								END THEN 1  						ELSE 0  					END THEN 'carpeta.root'  			ELSE 'carpeta.exproot'  		END AS typecolumn, 		 0 AS typerow, 		 c.titulo_aplicacion AS titulo_aplicacion, 		 c.id_gabinete AS id_gabinete, 		 1 AS id_carpeta_padre, 		 c.id_carpeta AS id_carpeta_hija, 		 1 AS numero_pagina, 		 c.nombre_usuario AS nombre_usuario, 		 c.fh_creacion AS fh_creacion, 		 c.fh_modificacion AS fh_modificacion, 		 c.numero_accesos AS numero_accesos, 		 c.numero_carpetas AS totchildcolumn, 		 c.bandera_raiz AS bandera_raiz, 		 c.numero_documentos AS numero_documentos, 		 c.descripcion AS descripcion, 		 c.password AS password, 		 0 AS prioridad, 		 0 AS id_tipo_docto, 		 CAST(NULL  AS VARCHAR (32)) AS nombre_tipo_docto, 		 CAST(NULL  AS VARCHAR (25)) AS titulo, 		 CAST(NULL  AS VARCHAR (25)) AS autor, 		 CAST(NULL  AS VARCHAR (25)) AS materia, 		 CAST(NULL  AS DECIMAL (31)) AS clase_documento, 		 CAST(NULL  AS CHAR (1)) AS estado_documento, 		 CAST(0  AS DECIMAL (31)) AS tamano_bytes_docto, 		 CAST(NULL  AS VARCHAR (1)) AS tipo_pagina, 		 CAST(NULL  AS VARCHAR (8)) AS volumen, 		 CAST(NULL  AS VARCHAR (1)) AS tipo_volumen, 		 CAST(NULL  AS VARCHAR (50)) AS unidad_disco, 		 CAST(NULL  AS VARCHAR (50)) AS ruta_base, 		 CAST(NULL  AS VARCHAR (70)) AS ruta_directorio, 		 CAST(NULL  AS VARCHAR (257)) AS nom_archivo_vol, 		 CAST(NULL  AS VARCHAR (257)) AS nom_archivo_org, 		 CAST(NULL  AS VARCHAR (1)) AS estado_pagina, 		 CAST(0  AS DECIMAL (31)) AS tamano_bytes, 		 CAST(NULL  AS VARCHAR (1)) AS compartir, 		 CAST(NULL  AS VARCHAR (80)) AS token_compartir, 		 CAST(NULL  AS DATETIME) AS FECHA_EXPIRA, 		 CAST(NULL  AS DATETIME) AS FECHA_COMPARTIDO, 		 CAST(NULL  AS VARCHAR (6)) AS DIAS_PERMITIDOS FROM  imx_carpeta c  WHERE	 c.bandera_raiz  = 'S'  AND	(titulo_aplicacion  = '"+titulo_aplicacion+"'  AND	id_gabinete  = "+id_gabinete+"  AND	(nombre_usuario  = '"+nombre_usuario+"'  OR	0  <= "+prioridad_usuario+"))");
					sql.append(" union ");
					sql.append("SELECT 		 CAST(oc.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(oc.id_carpeta_padre  AS CHAR (20)))  AS VARCHAR) AS parentidcolumn, 		 CAST(oc.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(oc.id_carpeta_hija  AS CHAR (20)))  AS VARCHAR) AS idcolumn, 		 oc.nombre_hija AS namecolumn, 		 'carpeta.' + CAST( 		CASE 1  			WHEN  					CASE 1  						WHEN  								CASE c.password  									WHEN '-1' THEN 1  									ELSE 0  								END THEN 1  						ELSE 0  					END THEN 'hija'  			ELSE 'prtgda'  		END  AS VARCHAR) AS typecolumn, 		 1 AS typerow, 		 oc.titulo_aplicacion AS titulo_aplicacion, 		 oc.id_gabinete AS id_gabinete, 		 oc.id_carpeta_padre AS id_carpeta_padre, 		 oc.id_carpeta_hija AS id_carpeta_hija, 		 1 AS numero_pagina, 		 c.nombre_usuario AS nombre_usuario, 		 c.fh_creacion AS fh_creacion, 		 c.fh_modificacion AS fh_modificacion, 		 c.numero_accesos AS numero_accesos, 		 c.numero_carpetas AS totchildcolumn, 		 c.bandera_raiz AS bandera_raiz, 		 c.numero_documentos AS numero_documentos, 		 c.descripcion AS descripcion, 		 c.password AS password, 		 0 AS prioridad, 		 0 AS id_tipo_docto, 		 CAST(NULL  AS VARCHAR (32)) AS nombre_tipo_docto, 		 CAST(NULL  AS VARCHAR (25)) AS titulo, 		 CAST(NULL  AS VARCHAR (25)) AS autor, 		 CAST(NULL  AS VARCHAR (25)) AS materia, 		 CAST(NULL  AS DECIMAL (31)) AS clase_documento, 		 CAST(NULL  AS CHAR (1)) AS estado_documento, 		 CAST(0  AS DECIMAL (31)) AS tamano_bytes_docto, 		 CAST(NULL  AS VARCHAR (1)) AS tipo_pagina, 		 CAST(NULL  AS VARCHAR (8)) AS volumen, 		 CAST(NULL  AS VARCHAR (1)) AS tipo_volumen, 		 CAST(NULL  AS VARCHAR (50)) AS unidad_disco, 		 CAST(NULL  AS VARCHAR (50)) AS ruta_base, 		 CAST(NULL  AS VARCHAR (70)) AS ruta_directorio, 		 CAST(NULL  AS VARCHAR (257)) AS nom_archivo_vol, 		 CAST(NULL  AS VARCHAR (257)) AS nom_archivo_org, 		 CAST(NULL  AS VARCHAR (1)) AS estado_pagina, 		 CAST(0  AS DECIMAL (31)) AS tamano_bytes, 		 CAST(NULL  AS VARCHAR (1)) AS compartir, 		 CAST(NULL  AS VARCHAR (80)) AS token_compartir, 		 CAST(NULL  AS DATETIME) AS FECHA_EXPIRA, 		 CAST(NULL  AS DATETIME) AS FECHA_COMPARTIDO, 		 CAST(NULL  AS VARCHAR (6)) AS DIAS_PERMITIDOS FROM  imx_carpeta c, 	 imx_org_carpeta oc  WHERE	 c.titulo_aplicacion  = oc.titulo_aplicacion  AND	c.id_gabinete  = oc.id_gabinete  AND	c.id_carpeta  = oc.id_carpeta_hija  AND	(oc.titulo_aplicacion  = '"+titulo_aplicacion+"'  AND	oc.id_gabinete  = "+id_gabinete+"  AND	(nombre_usuario  = '"+nombre_usuario+"'  OR	0  <= "+prioridad_usuario+")) ");
					sql.append(" union ");
					sql.append("SELECT 		 CAST(d.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(d.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))  AS VARCHAR) AS parentidcolumn, 		 CAST(d.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(d.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))  AS VARCHAR) + 'D' + CAST(RTRIM(CAST(d.id_documento  AS CHAR (20)))  AS VARCHAR) AS idcolumn, 		 d.nombre_documento AS namecolumn, 		 'docto.' + CAST( 			CASE 				 WHEN d.numero_paginas  > 1 THEN 'frtimx' 				 ELSE  					CASE p.tipo_pagina  						 WHEN 'I'  THEN 'frtimx' 						 ELSE  							CASE td.nombre_tipo_docto  								 WHEN 'IMAX_FILE'  THEN 'frtimx' 								 ELSE 'externo' 							 END 					 END 			 END  AS VARCHAR) AS typecolumn, 		 2 AS typerow, 		 d.titulo_aplicacion, 		 d.id_gabinete, 		 d.id_carpeta_padre, 		 d.id_documento AS id_carpeta_hija, 		 p.numero_pagina, 		 d.nombre_usuario, 		 d.fh_creacion, 		 d.fh_modificacion, 		 d.numero_accesos, 		 d.numero_paginas AS totchildcolumn, 		 'N' AS bandera_raiz, 		 0 AS numero_documentos, 		 d.descripcion, 		 CAST('-1'  AS VARCHAR (32)) AS password, 		 d.prioridad, 		 d.id_tipo_docto, 		 td.nombre_tipo_docto, 		 d.titulo, 		 d.autor, 		 d.materia, 		 d.clase_documento, 		 d.estado_documento, 		 d.tamano_bytes AS tamano_bytes_docto, 		 p.tipo_pagina, 		 p.volumen, 		 p.tipo_volumen, 		 v.unidad_disco, 		 v.ruta_base, 		 v.ruta_directorio, 		 p.nom_archivo_vol, 		 p.nom_archivo_org, 		 p.estado_pagina, 		 p.tamano_bytes, 		  		CASE d.compartir  			WHEN CAST(NULL  AS VARCHAR (1)) THEN 'N'  			ELSE d.compartir  		END, 		 d.token_compartir, 		 d.FECHA_EXPIRA, 		 d.FECHA_COMPARTIDO, 		 d.DIAS_PERMITIDOS FROM  imx_pagina p  RIGHT OUTER JOIN  imx_documento d  ON  p.titulo_aplicacion  = d.titulo_aplicacion 	 AND	p.id_gabinete  = d.id_gabinete 	 AND	p.id_carpeta_padre  = d.id_carpeta_padre 	 AND	p.id_documento  = d.id_documento   LEFT OUTER JOIN  imx_volumen v  ON  v.volumen  = p.volumen , 	 imx_tipo_documento td  WHERE	 (td.titulo_aplicacion  = d.titulo_aplicacion  AND	td.id_tipo_docto  = d.id_tipo_docto)  AND	(d.titulo_aplicacion  = '"+titulo_aplicacion+"'  AND	d.id_gabinete  = "+id_gabinete+"  AND	(nombre_usuario  = '"+nombre_usuario+"'  OR	d.prioridad  <= "+prioridad_usuario+")) ORDER BY typerow, 	 id_carpeta_padre, 	 id_carpeta_hija, 	 numero_pagina  ");
				}
				else{
					sql.append("SELECT 		 CAST(oc.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(oc.id_carpeta_padre  AS CHAR (20)))  AS VARCHAR) AS parentidcolumn, 		 CAST(oc.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(oc.id_carpeta_hija  AS CHAR (20)))  AS VARCHAR) AS idcolumn, 		 oc.nombre_hija AS namecolumn, 		 'carpeta.' + CAST( 		CASE 1  			WHEN  					CASE 1  						WHEN  								CASE c.password  									WHEN '-1' THEN 1  									ELSE 0  								END THEN 1  						ELSE 0  					END THEN 'hija'  			ELSE 'prtgda'  		END  AS VARCHAR) AS typecolumn, 		 1 AS typerow, 		 oc.titulo_aplicacion AS titulo_aplicacion, 		 oc.id_gabinete AS id_gabinete, 		 oc.id_carpeta_padre AS id_carpeta_padre, 		 oc.id_carpeta_hija AS id_carpeta_hija, 		 1 AS numero_pagina, 		 c.nombre_usuario AS nombre_usuario, 		 c.fh_creacion AS fh_creacion, 		 c.fh_modificacion AS fh_modificacion, 		 c.numero_accesos AS numero_accesos, 		 c.numero_carpetas AS totchildcolumn, 		 c.bandera_raiz AS bandera_raiz, 		 c.numero_documentos AS numero_documentos, 		 c.descripcion AS descripcion, 		 c.password AS password, 		 0 AS prioridad, 		 0 AS id_tipo_docto, 		 CAST(NULL  AS VARCHAR (32)) AS nombre_tipo_docto, 		 CAST(NULL  AS VARCHAR (25)) AS titulo, 		 CAST(NULL  AS VARCHAR (25)) AS autor, 		 CAST(NULL  AS VARCHAR (25)) AS materia, 		 CAST(NULL  AS DECIMAL (31)) AS clase_documento, 		 CAST(NULL  AS CHAR (1)) AS estado_documento, 		 CAST(0  AS DECIMAL (31)) AS tamano_bytes_docto, 		 CAST(NULL  AS VARCHAR (1)) AS tipo_pagina, 		 CAST(NULL  AS VARCHAR (8)) AS volumen, 		 CAST(NULL  AS VARCHAR (1)) AS tipo_volumen, 		 CAST(NULL  AS VARCHAR (50)) AS unidad_disco, 		 CAST(NULL  AS VARCHAR (50)) AS ruta_base, 		 CAST(NULL  AS VARCHAR (70)) AS ruta_directorio, 		 CAST(NULL  AS VARCHAR (257)) AS nom_archivo_vol, 		 CAST(NULL  AS VARCHAR (257)) AS nom_archivo_org, 		 CAST(NULL  AS VARCHAR (1)) AS estado_pagina, 		 CAST(0  AS DECIMAL (31)) AS tamano_bytes, 		 CAST(NULL  AS VARCHAR (1)) AS compartir, 		 CAST(NULL  AS VARCHAR (80)) AS token_compartir, 		 CAST(NULL  AS DATETIME) AS FECHA_EXPIRA, 		 CAST(NULL  AS DATETIME) AS FECHA_COMPARTIDO, 		 CAST(NULL  AS VARCHAR (6)) AS DIAS_PERMITIDOS FROM  imx_carpeta c, 	 imx_org_carpeta oc  WHERE	 c.titulo_aplicacion  = oc.titulo_aplicacion  AND	c.id_gabinete  = oc.id_gabinete  AND	c.id_carpeta  = oc.id_carpeta_hija  AND	(oc.titulo_aplicacion  = '"+titulo_aplicacion+"'  AND	oc.id_gabinete  = "+id_gabinete+"  AND	nombre_carpeta  = '"+Nom_Expediente+"'  AND	(nombre_usuario  = '"+nombre_usuario+"'  OR	0  <= "+prioridad_usuario+")) ");
					sql.append(" union ");
					sql.append("SELECT 		 CAST(d.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(d.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))  AS VARCHAR) AS parentidcolumn, 		 CAST(d.titulo_aplicacion  AS VARCHAR) + '_G' + CAST(RTRIM(CAST(d.id_gabinete  AS CHAR (20)))  AS VARCHAR) + 'C' + CAST(RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))  AS VARCHAR) + 'D' + CAST(RTRIM(CAST(d.id_documento  AS CHAR (20)))  AS VARCHAR) AS idcolumn, 		 d.nombre_documento AS namecolumn, 		 'docto.' + CAST( 			CASE 				 WHEN d.numero_paginas  > 1 THEN 'frtimx' 				 ELSE  					CASE p.tipo_pagina  						 WHEN 'I'  THEN 'frtimx' 						 ELSE  							CASE td.nombre_tipo_docto  								 WHEN 'IMAX_FILE'  THEN 'frtimx' 								 ELSE 'externo' 							 END 					 END 			 END  AS VARCHAR) AS typecolumn, 		 2 AS typerow, 		 d.titulo_aplicacion, 		 d.id_gabinete, 		 d.id_carpeta_padre, 		 d.id_documento AS id_carpeta_hija, 		 p.numero_pagina, 		 d.nombre_usuario, 		 d.fh_creacion, 		 d.fh_modificacion, 		 d.numero_accesos, 		 d.numero_paginas AS totchildcolumn, 		 'N' AS bandera_raiz, 		 0 AS numero_documentos, 		 d.descripcion, 		 CAST('-1'  AS VARCHAR (32)) AS password, 		 d.prioridad, 		 d.id_tipo_docto, 		 td.nombre_tipo_docto, 		 d.titulo, 		 d.autor, 		 d.materia, 		 d.clase_documento, 		 d.estado_documento, 		 d.tamano_bytes AS tamano_bytes_docto, 		 p.tipo_pagina, 		 p.volumen, 		 p.tipo_volumen, 		 v.unidad_disco, 		 v.ruta_base, 		 v.ruta_directorio, 		 p.nom_archivo_vol, 		 p.nom_archivo_org, 		 p.estado_pagina, 		 p.tamano_bytes, 		  		CASE d.compartir  			WHEN CAST(NULL  AS VARCHAR (1)) THEN 'N'  			ELSE d.compartir  		END, 		 d.token_compartir, 		 d.FECHA_EXPIRA, 		 d.FECHA_COMPARTIDO, 		 d.DIAS_PERMITIDOS FROM  imx_pagina p  RIGHT OUTER JOIN  imx_documento d  ON  p.titulo_aplicacion  = d.titulo_aplicacion 	 AND	p.id_gabinete  = d.id_gabinete 	 AND	p.id_carpeta_padre  = d.id_carpeta_padre 	 AND	p.id_documento  = d.id_documento   LEFT OUTER JOIN  imx_volumen v  ON  v.volumen  = p.volumen , 	 imx_tipo_documento td  WHERE	 (td.titulo_aplicacion  = d.titulo_aplicacion  AND	td.id_tipo_docto  = d.id_tipo_docto)  AND	(d.titulo_aplicacion  = '"+titulo_aplicacion+"'  AND	d.id_gabinete  = "+id_gabinete+"  AND	d.nombre_documento  = '"+Nom_Expediente+"'  AND	(nombre_usuario  = '"+nombre_usuario+"'  OR	d.prioridad  <= "+prioridad_usuario+")) ORDER BY typerow, 	 id_carpeta_padre, 	 id_carpeta_hija, 	 numero_pagina  ");
				}
			}
			else// Para Mysql
			{
				log.info("MYSQL");
				if (Nom_Expediente == "") {
					// Query 1
					sql.append("select cast(null as char charset utf8) AS parentidcolumn, concat(c.TITULO_APLICACION,_utf8'_G',rtrim(cast(c.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(c.ID_CARPETA as char(20) charset utf8))) AS idcolumn,c.NOMBRE_CARPETA AS namecolumn,(case when (c.TITULO_APLICACION = _utf8'USR_GRALES') then _utf8'carpeta.root' else _utf8'carpeta.exproot' end) AS typecolumn,0 AS typerow,c.TITULO_APLICACION AS titulo_aplicacion,c.ID_GABINETE AS id_gabinete,1 AS id_carpeta_padre,c.ID_CARPETA AS id_carpeta_hija,1 AS numero_pagina,c.NOMBRE_USUARIO AS nombre_usuario,c.FH_CREACION AS fh_creacion,c.FH_MODIFICACION AS fh_modificacion,c.NUMERO_ACCESOS AS numero_accesos,c.NUMERO_CARPETAS AS totchildcolumn,c.BANDERA_RAIZ AS bandera_raiz,c.NUMERO_DOCUMENTOS AS numero_documentos,c.DESCRIPCION AS descripcion,c.PASSWORD AS password,0 AS prioridad,0 AS id_tipo_docto,cast(NULL as char charset utf8) AS nombre_tipo_docto,cast(NULL as char charset utf8) AS titulo,cast(NULL as char charset utf8) AS autor,cast(NULL as char charset utf8) AS materia,cast(NULL as decimal(31,0)) AS clase_documento,cast(NULL as char(1) charset utf8) AS estado_documento,cast(0 as decimal(31,0)) AS tamano_bytes_docto,cast(NULL as char charset utf8) AS tipo_pagina,cast(NULL as char charset utf8) AS volumen,cast(NULL as char charset utf8) AS tipo_volumen,cast(NULL as char charset utf8) AS unidad_disco,cast(NULL as char charset utf8) AS ruta_base,cast(NULL as char charset utf8) AS ruta_directorio,cast(NULL as char charset utf8) AS nom_archivo_vol,cast(NULL as char charset utf8) AS nom_archivo_org,cast(NULL as char charset utf8) AS estado_pagina,cast(0 as decimal(31,0)) AS tamano_bytes,cast(NULL as char charset utf8) AS compartir,cast(NULL as char charset utf8) AS token_compartir,cast(NULL as char charset utf8) AS anotaciones,cast(NULL as datetime) AS FECHA_EXPIRA,cast(NULL as datetime) AS FECHA_COMPARTIDO,cast(NULL as char charset utf8) AS DIAS_PERMITIDOS");
					sql.append(" from imx_carpeta c ");
					sql.append("where (c.BANDERA_RAIZ = _utf8'S') AND (titulo_aplicacion = '"
							+ titulo_aplicacion
							+ "' AND id_gabinete = "
							+ id_gabinete
							+ " AND (nombre_usuario = '"
							+ nombre_usuario
							+ "' OR  0 <= "
							+ prioridad_usuario + ") )");

					sql.append(" union ");

					// Query 2
					sql.append("select concat(oc.TITULO_APLICACION,_utf8'_G',rtrim(cast(oc.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(oc.ID_CARPETA_PADRE as char(20) charset utf8))) AS parentidcolumn,concat(oc.TITULO_APLICACION,_utf8'_G',rtrim(cast(oc.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(oc.ID_CARPETA_HIJA as char(20) charset utf8))) AS idcolumn,oc.NOMBRE_HIJA AS namecolumn,concat(_utf8'carpeta.',(case when (c.PASSWORD = _utf8'-1') then _utf8'hija' else _utf8'prtgda' end)) AS typecolumn,1 AS typerow,oc.TITULO_APLICACION AS titulo_aplicacion,oc.ID_GABINETE AS id_gabinete,oc.ID_CARPETA_PADRE AS id_carpeta_padre,oc.ID_CARPETA_HIJA AS id_carpeta_hija,1 AS numero_pagina,c.NOMBRE_USUARIO AS nombre_usuario,c.FH_CREACION AS fh_creacion,c.FH_MODIFICACION AS fh_modificacion,c.NUMERO_ACCESOS AS numero_accesos,c.NUMERO_CARPETAS AS totchildcolumn,c.BANDERA_RAIZ AS bandera_raiz,c.NUMERO_DOCUMENTOS AS numero_documentos,c.DESCRIPCION AS descripcion,c.PASSWORD AS password,0 AS prioridad,0 AS id_tipo_docto,cast(NULL as char charset utf8) AS nombre_tipo_docto,cast(NULL as char charset utf8) AS titulo,cast(NULL as char charset utf8) AS autor,cast(NULL as char charset utf8) AS materia,cast(NULL as decimal(31,0)) AS clase_documento,cast(NULL as char(1) charset utf8) AS estado_documento,cast(0 as decimal(31,0)) AS tamano_bytes_docto,cast(NULL as char charset utf8) AS tipo_pagina,cast(NULL as char charset utf8) AS volumen,cast(NULL as char charset utf8) AS tipo_volumen,cast(NULL as char charset utf8) AS unidad_disco,cast(NULL as char charset utf8) AS ruta_base,cast(NULL as char charset utf8) AS ruta_directorio,cast(NULL as char charset utf8) AS nom_archivo_vol,cast(NULL as char charset utf8) AS nom_archivo_org,cast(NULL as char charset utf8) AS estado_pagina,cast(0 as decimal(31,0)) AS tamano_bytes,cast(NULL as char charset utf8) AS compartir,cast(NULL as char charset utf8) AS token_compartir,cast(NULL as char charset utf8) AS anotaciones,cast(NULL as datetime) AS FECHA_EXPIRA,cast(NULL as datetime) AS FECHA_COMPARTIDO,cast(NULL as char charset utf8) AS DIAS_PERMITIDOS ");
					sql.append(" from (imx_carpeta c join imx_org_carpeta oc on(((c.TITULO_APLICACION = oc.TITULO_APLICACION) and (c.ID_GABINETE = oc.ID_GABINETE) and (c.ID_CARPETA = oc.ID_CARPETA_HIJA)))) ");
					sql.append(" where  (oc.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND oc.id_gabinete = "
							+ id_gabinete + " AND    (nombre_usuario = '"
							+ nombre_usuario + "' OR  0 <= "
							+ prioridad_usuario + ") )");

					sql.append(" union ");

					// Query 3
					sql.append("select concat(d.TITULO_APLICACION,_utf8'_G',rtrim(cast(d.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(d.ID_CARPETA_PADRE as char(20) charset utf8))) AS parentidcolumn,concat(d.TITULO_APLICACION,_utf8'_G',rtrim(cast(d.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(d.ID_CARPETA_PADRE as char(20) charset utf8)),_utf8'D',rtrim(cast(d.ID_DOCUMENTO as char(20) charset utf8))) AS idcolumn,d.NOMBRE_DOCUMENTO AS namecolumn,concat(_utf8'docto.',(case p.TIPO_PAGINA when _utf8'I' then _utf8'frtimx' else _utf8'externo' end)) AS typecolumn,2 AS typerow,d.TITULO_APLICACION AS TITULO_APLICACION,d.ID_GABINETE AS ID_GABINETE,d.ID_CARPETA_PADRE AS ID_CARPETA_PADRE,d.ID_DOCUMENTO AS id_carpeta_hija,p.NUMERO_PAGINA AS NUMERO_PAGINA,d.NOMBRE_USUARIO AS NOMBRE_USUARIO,d.FH_CREACION AS FH_CREACION,d.FH_MODIFICACION AS FH_MODIFICACION,d.NUMERO_ACCESOS AS NUMERO_ACCESOS,d.NUMERO_PAGINAS AS totchildcolumn,_utf8'N' AS bandera_raiz,0 AS numero_documentos,d.DESCRIPCION AS DESCRIPCION,cast(_utf8'-1' as char charset utf8) AS password,d.PRIORIDAD AS PRIORIDAD,d.ID_TIPO_DOCTO AS ID_TIPO_DOCTO,td.NOMBRE_TIPO_DOCTO AS NOMBRE_TIPO_DOCTO,d.TITULO AS TITULO,d.AUTOR AS AUTOR,d.MATERIA AS MATERIA,d.CLASE_DOCUMENTO AS CLASE_DOCUMENTO,d.ESTADO_DOCUMENTO AS ESTADO_DOCUMENTO,d.TAMANO_BYTES AS tamano_bytes_docto,p.TIPO_PAGINA AS TIPO_PAGINA,p.VOLUMEN AS VOLUMEN,p.TIPO_VOLUMEN AS TIPO_VOLUMEN,v.UNIDAD_DISCO AS UNIDAD_DISCO,v.RUTA_BASE AS RUTA_BASE,v.RUTA_DIRECTORIO AS RUTA_DIRECTORIO,p.NOM_ARCHIVO_VOL AS NOM_ARCHIVO_VOL,p.NOM_ARCHIVO_ORG AS NOM_ARCHIVO_ORG,p.ESTADO_PAGINA AS ESTADO_PAGINA,p.TAMANO_BYTES AS TAMANO_BYTES,(case d.COMPARTIR when cast(NULL as char charset utf8) then _utf8'N' else d.COMPARTIR end) AS Expr1,d.TOKEN_COMPARTIR AS TOKEN_COMPARTIR,p.ANOTACIONES AS ANOTACIONES,d.FECHA_EXPIRA AS FECHA_EXPIRA,d.FECHA_COMPARTIDO AS FECHA_COMPARTIDO,d.DIAS_PERMITIDOS AS DIAS_PERMITIDOS ");
					sql.append(" from (((imx_documento d left join imx_pagina p on(((p.TITULO_APLICACION = d.TITULO_APLICACION) and (p.ID_GABINETE = d.ID_GABINETE) and (p.ID_CARPETA_PADRE = d.ID_CARPETA_PADRE) and (p.ID_DOCUMENTO = d.ID_DOCUMENTO)))) left join imx_volumen v on((v.VOLUMEN = p.VOLUMEN))) join imx_tipo_documento td on(((d.TITULO_APLICACION = td.TITULO_APLICACION) and (d.ID_TIPO_DOCTO = td.ID_TIPO_DOCTO)))) ");
					sql.append(" where (d.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND d.id_gabinete = "
							+ id_gabinete + " AND    (nombre_usuario = '"
							+ nombre_usuario + "' OR  d.prioridad <= "
							+ prioridad_usuario + "))");

					sql.append(" ORDER BY typerow, id_carpeta_padre, id_carpeta_hija, numero_pagina");

				} else// Para un documento o carpeta especifico
				{
					// Query 2
					sql.append("select concat(oc.TITULO_APLICACION,_utf8'_G',rtrim(cast(oc.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(oc.ID_CARPETA_PADRE as char(20) charset utf8))) AS parentidcolumn,concat(oc.TITULO_APLICACION,_utf8'_G',rtrim(cast(oc.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(oc.ID_CARPETA_HIJA as char(20) charset utf8))) AS idcolumn,oc.NOMBRE_HIJA AS namecolumn,concat(_utf8'carpeta.',(case when (c.PASSWORD = _utf8'-1') then _utf8'hija' else _utf8'prtgda' end)) AS typecolumn,1 AS typerow,oc.TITULO_APLICACION AS titulo_aplicacion,oc.ID_GABINETE AS id_gabinete,oc.ID_CARPETA_PADRE AS id_carpeta_padre,oc.ID_CARPETA_HIJA AS id_carpeta_hija,1 AS numero_pagina,c.NOMBRE_USUARIO AS nombre_usuario,c.FH_CREACION AS fh_creacion,c.FH_MODIFICACION AS fh_modificacion,c.NUMERO_ACCESOS AS numero_accesos,c.NUMERO_CARPETAS AS totchildcolumn,c.BANDERA_RAIZ AS bandera_raiz,c.NUMERO_DOCUMENTOS AS numero_documentos,c.DESCRIPCION AS descripcion,c.PASSWORD AS password,0 AS prioridad,0 AS id_tipo_docto,cast(NULL as char charset utf8) AS nombre_tipo_docto,cast(NULL as char charset utf8) AS titulo,cast(NULL as char charset utf8) AS autor,cast(NULL as char charset utf8) AS materia,cast(NULL as decimal(31,0)) AS clase_documento,cast(NULL as char(1) charset utf8) AS estado_documento,cast(0 as decimal(31,0)) AS tamano_bytes_docto,cast(NULL as char charset utf8) AS tipo_pagina,cast(NULL as char charset utf8) AS volumen,cast(NULL as char charset utf8) AS tipo_volumen,cast(NULL as char charset utf8) AS unidad_disco,cast(NULL as char charset utf8) AS ruta_base,cast(NULL as char charset utf8) AS ruta_directorio,cast(NULL as char charset utf8) AS nom_archivo_vol,cast(NULL as char charset utf8) AS nom_archivo_org,cast(NULL as char charset utf8) AS estado_pagina,cast(0 as decimal(31,0)) AS tamano_bytes,cast(NULL as char charset utf8) AS compartir,cast(NULL as char charset utf8) AS token_compartir,cast(NULL as char charset utf8) AS anotaciones,cast(NULL as datetime) AS FECHA_EXPIRA,cast(NULL as datetime) AS FECHA_COMPARTIDO,cast(NULL as char charset utf8) AS DIAS_PERMITIDOS ");
					sql.append(" from (imx_carpeta c join imx_org_carpeta oc on(((c.TITULO_APLICACION = oc.TITULO_APLICACION) and (c.ID_GABINETE = oc.ID_GABINETE) and (c.ID_CARPETA = oc.ID_CARPETA_HIJA)))) ");
					sql.append(" where  (oc.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND oc.id_gabinete = "
							+ id_gabinete + " AND nombre_carpeta = '"
							+ Nom_Expediente + "' AND (nombre_usuario = '"
							+ nombre_usuario + "' OR  0 <= "
							+ prioridad_usuario + ") )");

					sql.append(" union ");

					// Query 3
					sql.append("select concat(d.TITULO_APLICACION,_utf8'_G',rtrim(cast(d.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(d.ID_CARPETA_PADRE as char(20) charset utf8))) AS parentidcolumn,concat(d.TITULO_APLICACION,_utf8'_G',rtrim(cast(d.ID_GABINETE as char(20) charset utf8)),_utf8'C',rtrim(cast(d.ID_CARPETA_PADRE as char(20) charset utf8)),_utf8'D',rtrim(cast(d.ID_DOCUMENTO as char(20) charset utf8))) AS idcolumn,d.NOMBRE_DOCUMENTO AS namecolumn,concat(_utf8'docto.',(case p.TIPO_PAGINA when _utf8'I' then _utf8'frtimx' else _utf8'externo' end)) AS typecolumn,2 AS typerow,d.TITULO_APLICACION AS TITULO_APLICACION,d.ID_GABINETE AS ID_GABINETE,d.ID_CARPETA_PADRE AS ID_CARPETA_PADRE,d.ID_DOCUMENTO AS id_carpeta_hija,p.NUMERO_PAGINA AS NUMERO_PAGINA,d.NOMBRE_USUARIO AS NOMBRE_USUARIO,d.FH_CREACION AS FH_CREACION,d.FH_MODIFICACION AS FH_MODIFICACION,d.NUMERO_ACCESOS AS NUMERO_ACCESOS,d.NUMERO_PAGINAS AS totchildcolumn,_utf8'N' AS bandera_raiz,0 AS numero_documentos,d.DESCRIPCION AS DESCRIPCION,cast(_utf8'-1' as char charset utf8) AS password,d.PRIORIDAD AS PRIORIDAD,d.ID_TIPO_DOCTO AS ID_TIPO_DOCTO,td.NOMBRE_TIPO_DOCTO AS NOMBRE_TIPO_DOCTO,d.TITULO AS TITULO,d.AUTOR AS AUTOR,d.MATERIA AS MATERIA,d.CLASE_DOCUMENTO AS CLASE_DOCUMENTO,d.ESTADO_DOCUMENTO AS ESTADO_DOCUMENTO,d.TAMANO_BYTES AS tamano_bytes_docto,p.TIPO_PAGINA AS TIPO_PAGINA,p.VOLUMEN AS VOLUMEN,p.TIPO_VOLUMEN AS TIPO_VOLUMEN,v.UNIDAD_DISCO AS UNIDAD_DISCO,v.RUTA_BASE AS RUTA_BASE,v.RUTA_DIRECTORIO AS RUTA_DIRECTORIO,p.NOM_ARCHIVO_VOL AS NOM_ARCHIVO_VOL,p.NOM_ARCHIVO_ORG AS NOM_ARCHIVO_ORG,p.ESTADO_PAGINA AS ESTADO_PAGINA,p.TAMANO_BYTES AS TAMANO_BYTES,(case d.COMPARTIR when cast(NULL as char charset utf8) then _utf8'N' else d.COMPARTIR end) AS Expr1,d.TOKEN_COMPARTIR AS TOKEN_COMPARTIR,p.ANOTACIONES AS ANOTACIONES,d.FECHA_EXPIRA AS FECHA_EXPIRA,d.FECHA_COMPARTIDO AS FECHA_COMPARTIDO,d.DIAS_PERMITIDOS AS DIAS_PERMITIDOS ");
					sql.append(" from (((imx_documento d left join imx_pagina p on(((p.TITULO_APLICACION = d.TITULO_APLICACION) and (p.ID_GABINETE = d.ID_GABINETE) and (p.ID_CARPETA_PADRE = d.ID_CARPETA_PADRE) and (p.ID_DOCUMENTO = d.ID_DOCUMENTO)))) left join imx_volumen v on((v.VOLUMEN = p.VOLUMEN))) join imx_tipo_documento td on(((d.TITULO_APLICACION = td.TITULO_APLICACION) and (d.ID_TIPO_DOCTO = td.ID_TIPO_DOCTO)))) ");
					sql.append(" where (d.titulo_aplicacion = '"
							+ titulo_aplicacion + "' AND d.id_gabinete = "
							+ id_gabinete + " AND d.nombre_documento = '"
							+ Nom_Expediente + "' AND (nombre_usuario = '"
							+ nombre_usuario + "' OR  d.prioridad <= "
							+ prioridad_usuario + "))");

					sql.append(" ORDER BY typerow, id_carpeta_padre, id_carpeta_hija, numero_pagina");

				}
			}

			Map<String,?> map = (Map<String, ?>) hm.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
			//map = new CaseInsensitiveMap(map);
			
			Matricula_Armada = (String) map.values().toArray()[1];
		} finally {
			hm.close();
		}
		return Matricula_Armada;
	}

	public Element generaExpedienteXML(String nombre_usuario) {
		ITree tree = generaExpediente(nombre_usuario);
		Element root = new Element("Expediente");

		if (tree != null) {
			ITreeNode nodeRoot = tree.getRoot();
			processTreeXML(root, nodeRoot);
		}

		return root;
	}

	public void processTreeXML(Element e, ITreeNode node) {
		String nodeName = node.getName() == null ? "" : node.getName();
		String nodeType = node.getType() == null ? "" : node.getType();
		String nodeID = node.getId() == null ? "" : node.getId();
		String paginas = "";
		String extension = "";
		if (node.getObject() != null && node.getObject() instanceof Carpeta) {
			paginas = String.valueOf(0);
			extension = new String("");
		} else if (node.getObject() != null
				&& node.getObject() instanceof Documento) {
			Documento d = (Documento) node.getObject();
			paginas = String.valueOf(d.getNumero_paginas());
			extension = d.getExtension() == null ? "" : d.getExtension();
		}

		Attribute attrType = new Attribute("type", nodeType);
		Attribute attrID = new Attribute("id", nodeID);
		Attribute attrPaginas = new Attribute("paginas", paginas);
		Attribute attrExtension = new Attribute("extension", extension);

		Element child = new Element("Node");
		child.setAttribute(attrID);
		child.setAttribute(attrType);
		child.setAttribute(attrPaginas);
		child.setAttribute(attrExtension);
		child.setText(nodeName);

		e.addContent(child);
		List<ITreeNode> nodes = (List<ITreeNode>) node.getChildren();
		if (nodes != null) {
			for (Iterator<ITreeNode> i = nodes.iterator(); i.hasNext();) {
				ITreeNode childNode = i.next();
				processTreeXML(child, childNode);
			}
		}
	}
	
	

	public ITree generaExpediente(String nombre_usuario) {
		ITree tree = new Tree();
		int prioridad_usuario = -1;
		ITreeNode root = new TreeNode();

		tree.setSingleSelectionMode(true);
		if ("USR_GRALES".equals(titulo_aplicacion)) {
			tree.addCollapseListener(MyDocCollapseListener);
			tree.addExpandListener(MyDocExpandListener);
			tree.addSelectListener(MyDocSelectListener);
			tree.addUnSelectListener(MyDocUnselectListener);
		} else {
			tree.addCollapseListener(ExpCollapseListener);
			tree.addExpandListener(ExpExpandListener);
			tree.addSelectListener(ExpSelectListener);
			tree.addUnSelectListener(ExpUnselectListener);
		}

		try {
			ArrayList<imx_seguridad> seguridad = imx_seguridad_manager.get(
					nombre_usuario, titulo_aplicacion);

			if (seguridad.isEmpty() == false) {
				prioridad_usuario = seguridad.get(0).getId().getPrioridad();
			}

			StringBuffer sql = new StringBuffer();
			
			//Aquí inicia código carpeta raíz
			sql.append(
					" SELECT" +
			/*0*/	" CAST(NULL  AS CHAR (99)) AS parentidcolumn," +
			/*1*/	" CONCAT(CONCAT(CONCAT(CONCAT(c.titulo_aplicacion,'_G'),RTRIM(CAST(c.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(c.id_carpeta  AS CHAR (20)))) AS idcolumn," +
			/*2*/	" c.nombre_carpeta AS namecolumn," +
			/*3*/	" CASE 1 WHEN CASE 1 WHEN CASE c.titulo_aplicacion WHEN 'USR_GRALES' THEN 1 ELSE 0 END THEN 1 ELSE 0 END THEN 'carpeta.root' ELSE 'carpeta.exproot' END AS typecolumn," +
			/*4*/	" 0 AS typerow," +
			/*5*/	" c.titulo_aplicacion AS titulo_aplicacion," +
			/*6*/	" c.id_gabinete AS id_gabinete," +
			/*7*/	" 1 AS id_carpeta_padre," +
			/*8*/	" c.id_carpeta AS id_carpeta_hija," +
			/*9*/	" c.nombre_usuario AS nombre_usuario," +
			/*10*/	" c.fh_creacion AS fh_creacion," +
			/*11*/	" c.fh_modificacion AS fh_modificacion," +
			/*12*/	" c.numero_accesos AS numero_accesos," +
			/*13*/	" c.numero_carpetas AS totchildcolumn," +
			/*14*/	" c.bandera_raiz AS bandera_raiz," +
			/*15*/	" c.numero_documentos AS numero_documentos," +
			/*16*/	" c.descripcion AS descripcion," +
			/*17*/	" c.password AS password," +
			/*18*/	" 0 AS prioridad," +
			/*19*/	" 0 AS id_tipo_docto," +
			/*20*/	" CAST(NULL AS CHAR (32)) AS nombre_tipo_docto," +
			/*21*/	" CAST(NULL AS CHAR (25)) AS titulo," +
			/*22*/	" CAST(NULL AS CHAR (25)) AS autor," +
			/*23*/	" CAST(NULL AS CHAR (25)) AS materia," +
			/*24*/	" CAST(NULL AS DECIMAL (31)) AS clase_documento," +
			/*25*/	" CAST(NULL AS CHAR (1)) AS estado_documento," +
			/*26*/	" CAST(0  AS DECIMAL (31)) AS tamano_bytes_docto," +
			/*27*/	" CAST(NULL AS CHAR (1)) AS compartir," +
			/*28*/	" CAST(NULL AS CHAR (80)) AS token_compartir," +
			/*29*/	" CAST(NULL AS DATE) AS FECHA_EXPIRA," +
			/*30*/	" CAST(NULL AS DATE) AS FECHA_COMPARTIDO," +
			/*31*/	" CAST(NULL AS CHAR (6)) AS DIAS_PERMITIDOS" +
					" FROM imx_carpeta c" +
					" WHERE" +
					" c.bandera_raiz  = 'S'" +
					" AND (titulo_aplicacion  = '"+titulo_aplicacion+"'" +
					" AND id_gabinete  = "+id_gabinete +
					" AND (nombre_usuario  = '"+nombre_usuario+"'" +
					" OR 0  <= " +prioridad_usuario+"))"
			);
			//Aqui acaba el código de carpeta raíz
			sql.append(" UNION");					
			//Aqui inicia el código de las carpetas hijas
			sql.append(
					" SELECT" +
			/*0*/	" CONCAT(CONCAT(CONCAT(CONCAT(oc.titulo_aplicacion,'_G'),RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(oc.id_carpeta_padre  AS CHAR (20)))) AS parentidcolumn," +
			/*1*/	" CONCAT(CONCAT(CONCAT(CONCAT(oc.titulo_aplicacion,'_G'),RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(oc.id_carpeta_hija  AS CHAR (20)))) AS idcolumn," +
			/*2*/	" oc.nombre_hija AS namecolumn," +
			/*3*/	" CONCAT('carpeta.',CASE 1 WHEN CASE 1 WHEN CASE c.password WHEN '-1' THEN 1 ELSE 0 END THEN 1 ELSE 0 END THEN 'hija' ELSE 'prtgda' END) AS typecolumn," +
			/*4*/	" 1 AS typerow," +
			/*5*/	" oc.titulo_aplicacion AS titulo_aplicacion," +
			/*6*/	" oc.id_gabinete AS id_gabinete," +
			/*7*/	" oc.id_carpeta_padre AS id_carpeta_padre," +
			/*8*/	" oc.id_carpeta_hija AS id_carpeta_hija," +
			/*9*/	" c.nombre_usuario AS nombre_usuario," +
			/*10*/	" c.fh_creacion AS fh_creacion," +
			/*11*/	" c.fh_modificacion AS fh_modificacion," +
			/*12*/	" c.numero_accesos AS numero_accesos," +
			/*13*/	" c.numero_carpetas AS totchildcolumn," +
			/*14*/	" c.bandera_raiz AS bandera_raiz," +
			/*15*/	" c.numero_documentos AS numero_documentos," +
			/*16*/	" c.descripcion AS descripcion," +
			/*17*/	" c.password AS password," +
			/*18*/	" 0 AS prioridad," +
			/*19*/	" 0 AS id_tipo_docto," +
			/*20*/	" CAST(NULL AS CHAR (32)) AS nombre_tipo_docto," +
			/*21*/	" CAST(NULL AS CHAR (25)) AS titulo," +
			/*22*/	" CAST(NULL AS CHAR (25)) AS autor," +
			/*23*/	" CAST(NULL AS CHAR (25)) AS materia," +
			/*24*/	" CAST(NULL AS DECIMAL (31)) AS clase_documento," +
			/*25*/	" CAST(NULL AS CHAR (1)) AS estado_documento," +
			/*26*/	" CAST(0 AS DECIMAL (31)) AS tamano_bytes_docto," +
			/*27*/	" CAST(NULL AS CHAR (1)) AS compartir," +
			/*28*/	" CAST(NULL AS CHAR (80)) AS token_compartir," +
			/*29*/	" CAST(NULL AS DATE) AS FECHA_EXPIRA," +
			/*30*/	" CAST(NULL AS DATE) AS FECHA_COMPARTIDO," +
			/*31*/	" CAST(NULL AS CHAR (6)) AS DIAS_PERMITIDOS" +
					" FROM imx_carpeta c, imx_org_carpeta oc" +
					" WHERE c.titulo_aplicacion = oc.titulo_aplicacion" +
					" AND c.id_gabinete = oc.id_gabinete" +
					" AND c.id_carpeta = oc.id_carpeta_hija" +
					" AND (oc.titulo_aplicacion = '"+titulo_aplicacion+"'" +
					" AND oc.id_gabinete  = "+id_gabinete +
					" AND (nombre_usuario  = '"+nombre_usuario+"'" +
					" OR 0 <= " +prioridad_usuario+"))");
			//Aqui acaba el código de las carpetas hijas
			sql.append(" UNION" +
			//Aqui inicia el código de los documentos y páginas		
					" SELECT" +
			/*0*/	" CONCAT(CONCAT(CONCAT(CONCAT(d.titulo_aplicacion,'_G'),RTRIM(CAST(d.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))) AS parentidcolumn," +
			/*1*/	" CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(d.titulo_aplicacion,'_G'),RTRIM(CAST(d.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))),'D'),RTRIM(CAST(d.id_documento  AS CHAR (20)))) AS idcolumn," +
			/*2*/	" d.nombre_documento AS namecolumn," +
			/*3*/	" CONCAT('docto.',CASE WHEN nombre_tipo_docto = 'IMAX_FILE' THEN 'frtimx' ELSE 'externo' END) AS typecolumn," +
			/*4*/	" 2 AS typerow," +
			/*5*/	" d.titulo_aplicacion," +
			/*6*/	" d.id_gabinete," +
			/*7*/	" d.id_carpeta_padre," +
			/*8*/	" d.id_documento AS id_carpeta_hija," +
			/*9*/	" d.nombre_usuario," +
			/*10*/	" d.fh_creacion," +
			/*11*/	" d.fh_modificacion," +
			/*12*/	" d.numero_accesos," +
			/*13*/	" d.numero_paginas AS totchildcolumn," +
			/*14*/	" 'N' AS bandera_raiz," +
			/*15*/	" 0 AS numero_documentos," +
			/*16*/	" d.descripcion," +
			/*17*/	" CAST('-1'  AS CHAR (32)) AS password," +
			/*18*/	" d.prioridad," +
			/*19*/	" d.id_tipo_docto," +
			/*20*/	" td.nombre_tipo_docto," +
			/*21*/	" d.titulo," +
			/*22*/	" d.autor," +
			/*23*/	" d.materia," +
			/*24*/	" d.clase_documento," +
			/*25*/	" d.estado_documento," +
			/*26*/	" d.tamano_bytes AS tamano_bytes_docto," +
			/*27*/	" CASE d.compartir WHEN CAST(NULL AS CHAR (1)) THEN 'N' ELSE d.compartir END," +
			/*28*/	" d.token_compartir," +
			/*29*/	" d.FECHA_EXPIRA," +
			/*30*/	" d.FECHA_COMPARTIDO," +
			/*31*/	" d.DIAS_PERMITIDOS" +
					" FROM imx_documento d" +
					" INNER JOIN imx_tipo_documento td ON d.id_tipo_docto = td.id_tipo_docto" +
					" AND d.titulo_aplicacion = td.titulo_aplicacion" +  
					" WHERE" +
					" (d.titulo_aplicacion  = '"+titulo_aplicacion+"'" +
					" AND d.id_gabinete ="+id_gabinete +
					" AND (nombre_usuario = '"+nombre_usuario+"'" +
					" OR d.prioridad <= "+prioridad_usuario+"))" +
					" ORDER BY" +
					" typerow, id_carpeta_padre, id_carpeta_hija");
			//Aqui acaba el código de los documentos y páginas	
			
			TreeTableMapping ttm = new TreeTableMapping(idColumn,
					parentIdColumn, treeIdColumn, nameColumn, typeColumn,
					toolTipColumn);

			TreeReader tm = new TreeReader(ttm, this);

			root = tm.readTree(sql.toString());
		} finally {
			tree.setRoot(root);

			log.trace("Regreso el Arbol ");
		}
		return tree;
	}
	
	public ITree getChildren(String nombre_usuario, boolean isSystem) {
		ITree tree = new Tree();
		int prioridad_usuario = -1;
		ITreeNode root = new TreeNode();

		tree.setSingleSelectionMode(true);
		if ("USR_GRALES".equals(titulo_aplicacion)) {
			tree.addCollapseListener(MyDocCollapseListener);
			tree.addExpandListener(MyDocExpandListener);
			tree.addSelectListener(MyDocSelectListener);
			tree.addUnSelectListener(MyDocUnselectListener);
		} else {
			tree.addCollapseListener(ExpCollapseListener);
			tree.addExpandListener(ExpExpandListener);
			tree.addSelectListener(ExpSelectListener);
			tree.addUnSelectListener(ExpUnselectListener);
		}

		try {
			if(isSystem) {
				prioridad_usuario = 1000;
			} else {
				ArrayList<imx_seguridad> seguridad = imx_seguridad_manager.get(
						nombre_usuario, titulo_aplicacion);

				if (seguridad.isEmpty() == false) {
					prioridad_usuario = seguridad.get(0).getId().getPrioridad();
				}
			}
			StringBuffer sql = new StringBuffer();
			
			if(id_carpeta==0) {
			//Aquí inicia código carpeta raíz
			sql.append(
					" SELECT" +
			/*0*/	" CAST(NULL  AS CHAR (99)) AS parentidcolumn," +
			/*1*/	" CONCAT(CONCAT(CONCAT(CONCAT(c.titulo_aplicacion,'_G'),RTRIM(CAST(c.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(c.id_carpeta  AS CHAR (20)))) AS idcolumn," +
			/*2*/	" c.nombre_carpeta AS namecolumn," +
			/*3*/	" CASE 1 WHEN CASE 1 WHEN CASE c.titulo_aplicacion WHEN 'USR_GRALES' THEN 1 ELSE 0 END THEN 1 ELSE 0 END THEN 'carpeta.root' ELSE 'carpeta.exproot' END AS typecolumn," +
			/*4*/	" 0 AS typerow," +
			/*5*/	" c.titulo_aplicacion AS titulo_aplicacion," +
			/*6*/	" c.id_gabinete AS id_gabinete," +
			/*7*/	" 1 AS id_carpeta_padre," +
			/*8*/	" c.id_carpeta AS id_carpeta_hija," +
			/*9*/	" c.nombre_usuario AS nombre_usuario," +
			/*10*/	" c.fh_creacion AS fh_creacion," +
			/*11*/	" c.fh_modificacion AS fh_modificacion," +
			/*12*/	" c.numero_accesos AS numero_accesos," +
			/*13*/	" c.numero_carpetas AS totchildcolumn," +
			/*14*/	" c.bandera_raiz AS bandera_raiz," +
			/*15*/	" c.numero_documentos AS numero_documentos," +
			/*16*/	" c.descripcion AS descripcion," +
			/*17*/	" c.password AS password," +
			/*18*/	" 0 AS prioridad," +
			/*19*/	" 0 AS id_tipo_docto," +
			/*20*/	" CAST(NULL AS CHAR (32)) AS nombre_tipo_docto," +
			/*21*/	" CAST(NULL AS CHAR (25)) AS titulo," +
			/*22*/	" CAST(NULL AS CHAR (25)) AS autor," +
			/*23*/	" CAST(NULL AS CHAR (25)) AS materia," +
			/*24*/	" CAST(NULL AS DECIMAL (31)) AS clase_documento," +
			/*25*/	" CAST(NULL AS CHAR (1)) AS estado_documento," +
			/*26*/	" CAST(0  AS DECIMAL (31)) AS tamano_bytes_docto," +
			/*27*/	" CAST(NULL AS CHAR (1)) AS compartir," +
			/*28*/	" CAST(NULL AS CHAR (80)) AS token_compartir," +
			/*29*/	" CAST(NULL AS DATE) AS FECHA_EXPIRA," +
			/*30*/	" CAST(NULL AS DATE) AS FECHA_COMPARTIDO," +
			/*31*/	" CAST(NULL AS CHAR (6)) AS DIAS_PERMITIDOS" +
					" FROM imx_carpeta c" +
					" WHERE" +
					" c.bandera_raiz  = 'S'" +
					" AND (titulo_aplicacion  = '"+titulo_aplicacion+"'" +
					" AND id_gabinete  = "+id_gabinete +
					" AND (nombre_usuario  = '"+nombre_usuario+"'" +
					" OR 0  <= " +prioridad_usuario+"))"
			);
			//Aqui acaba el código de carpeta raíz
			sql.append(" UNION");	
			}
			
			//Aqui inicia el código de las carpetas hijas
			sql.append(
					" SELECT" +
			/*0*/	" CONCAT(CONCAT(CONCAT(CONCAT(oc.titulo_aplicacion,'_G'),RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(oc.id_carpeta_padre  AS CHAR (20)))) AS parentidcolumn," +
			/*1*/	" CONCAT(CONCAT(CONCAT(CONCAT(oc.titulo_aplicacion,'_G'),RTRIM(CAST(oc.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(oc.id_carpeta_hija  AS CHAR (20)))) AS idcolumn," +
			/*2*/	" oc.nombre_hija AS namecolumn," +
			/*3*/	" CONCAT('carpeta.',CASE 1 WHEN CASE 1 WHEN CASE c.password WHEN '-1' THEN 1 ELSE 0 END THEN 1 ELSE 0 END THEN 'hija' ELSE 'prtgda' END) AS typecolumn," +
			/*4*/	" 1 AS typerow," +
			/*5*/	" oc.titulo_aplicacion AS titulo_aplicacion," +
			/*6*/	" oc.id_gabinete AS id_gabinete," +
			/*7*/	" oc.id_carpeta_padre AS id_carpeta_padre," +
			/*8*/	" oc.id_carpeta_hija AS id_carpeta_hija," +
			/*9*/	" c.nombre_usuario AS nombre_usuario," +
			/*10*/	" c.fh_creacion AS fh_creacion," +
			/*11*/	" c.fh_modificacion AS fh_modificacion," +
			/*12*/	" c.numero_accesos AS numero_accesos," +
			/*13*/	" c.numero_carpetas AS totchildcolumn," +
			/*14*/	" c.bandera_raiz AS bandera_raiz," +
			/*15*/	" c.numero_documentos AS numero_documentos," +
			/*16*/	" c.descripcion AS descripcion," +
			/*17*/	" c.password AS password," +
			/*18*/	" 0 AS prioridad," +
			/*19*/	" 0 AS id_tipo_docto," +
			/*20*/	" CAST(NULL AS CHAR (32)) AS nombre_tipo_docto," +
			/*21*/	" CAST(NULL AS CHAR (25)) AS titulo," +
			/*22*/	" CAST(NULL AS CHAR (25)) AS autor," +
			/*23*/	" CAST(NULL AS CHAR (25)) AS materia," +
			/*24*/	" CAST(NULL AS DECIMAL (31)) AS clase_documento," +
			/*25*/	" CAST(NULL AS CHAR (1)) AS estado_documento," +
			/*26*/	" CAST(0 AS DECIMAL (31)) AS tamano_bytes_docto," +
			/*27*/	" CAST(NULL AS CHAR (1)) AS compartir," +
			/*28*/	" CAST(NULL AS CHAR (80)) AS token_compartir," +
			/*29*/	" CAST(NULL AS DATE) AS FECHA_EXPIRA," +
			/*30*/	" CAST(NULL AS DATE) AS FECHA_COMPARTIDO," +
			/*31*/	" CAST(NULL AS CHAR (6)) AS DIAS_PERMITIDOS" +
					" FROM imx_carpeta c, imx_org_carpeta oc" +
					" WHERE c.titulo_aplicacion = oc.titulo_aplicacion" +
					" AND c.id_gabinete = oc.id_gabinete" +
					" AND c.id_carpeta = oc.id_carpeta_hija" +
					" AND (oc.titulo_aplicacion = '"+titulo_aplicacion+"'" +
					" AND oc.id_gabinete  = "+id_gabinete +
					" AND (oc.id_carpeta_padre = "+id_carpeta +
					" OR c.id_carpeta = "+id_carpeta + ")" +
					" AND (nombre_usuario  = '"+nombre_usuario+"'" +
					" OR 0 <= " +prioridad_usuario+"))");
			//Aqui acaba el código de las carpetas hijas
			sql.append(" UNION" +
			//Aqui inicia el código de los documentos y páginas		
					" SELECT" +
			/*0*/	" CONCAT(CONCAT(CONCAT(CONCAT(d.titulo_aplicacion,'_G'),RTRIM(CAST(d.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))) AS parentidcolumn," +
			/*1*/	" CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(d.titulo_aplicacion,'_G'),RTRIM(CAST(d.id_gabinete  AS CHAR (20)))),'C'),RTRIM(CAST(d.id_carpeta_padre  AS CHAR (20)))),'D'),RTRIM(CAST(d.id_documento  AS CHAR (20)))) AS idcolumn," +
			/*2*/	" d.nombre_documento AS namecolumn," +
			/*3*/	" CONCAT('docto.',CASE WHEN nombre_tipo_docto = 'IMAX_FILE' THEN 'frtimx' ELSE 'externo' END) AS typecolumn," +
			/*4*/	" 2 AS typerow," +
			/*5*/	" d.titulo_aplicacion," +
			/*6*/	" d.id_gabinete," +
			/*7*/	" d.id_carpeta_padre," +
			/*8*/	" d.id_documento AS id_carpeta_hija," +
			/*9*/	" d.nombre_usuario," +
			/*10*/	" d.fh_creacion," +
			/*11*/	" d.fh_modificacion," +
			/*12*/	" d.numero_accesos," +
			/*13*/	" d.numero_paginas AS totchildcolumn," +
			/*14*/	" 'N' AS bandera_raiz," +
			/*15*/	" 0 AS numero_documentos," +
			/*16*/	" d.descripcion," +
			/*17*/	" CAST('-1'  AS CHAR (32)) AS password," +
			/*18*/	" d.prioridad," +
			/*19*/	" d.id_tipo_docto," +
			/*20*/	" td.nombre_tipo_docto," +
			/*21*/	" d.titulo," +
			/*22*/	" d.autor," +
			/*23*/	" d.materia," +
			/*24*/	" d.clase_documento," +
			/*25*/	" d.estado_documento," +
			/*26*/	" d.tamano_bytes AS tamano_bytes_docto," +
			/*27*/	" CASE d.compartir WHEN CAST(NULL AS CHAR (1)) THEN 'N' ELSE d.compartir END," +
			/*28*/	" d.token_compartir," +
			/*29*/	" d.FECHA_EXPIRA," +
			/*30*/	" d.FECHA_COMPARTIDO," +
			/*31*/	" d.DIAS_PERMITIDOS" +
					" FROM imx_documento d" +
					" INNER JOIN imx_tipo_documento td ON d.id_tipo_docto = td.id_tipo_docto" +
					" AND d.titulo_aplicacion = td.titulo_aplicacion" +  
					" WHERE" +
					" (d.titulo_aplicacion  = '"+titulo_aplicacion+"'" +
					" AND d.id_gabinete ="+id_gabinete +
					" AND d.id_carpeta_padre ="+id_carpeta +
					" AND (nombre_usuario = '"+nombre_usuario+"'" +
					" OR d.prioridad <= "+prioridad_usuario+"))" +
					" ORDER BY" +
					" typerow, id_carpeta_padre, id_carpeta_hija");
			//Aqui acaba el código de los documentos y páginas	
			
			TreeTableMapping ttm = new TreeTableMapping(idColumn,
					parentIdColumn, treeIdColumn, nameColumn, typeColumn,
					toolTipColumn);

			TreeReader tm = new TreeReader(ttm, this);

			root = tm.readTree(sql.toString());
		} finally {
			tree.setRoot(root);

			log.trace("Regreso los hijos del nodo selecciondo");
		}
		return tree;
	}

	public void process(Object[] rs, ITreeNode node) {
		if (node.getType().startsWith("carpeta")) {
			Carpeta c = new Carpeta(titulo_aplicacion, id_gabinete,
					Utils.getInteger(rs[8])); //id_carpeta_hija

			c.setIdCarpetaPadre(Utils.getInteger(rs[7])); //id_carpeta_padre
			c.setNombreCarpeta(""+rs[2]); //namecolumn
			c.setNombreUsuario(""+rs[9]); //nombre_usuario
			c.setBanderaRaiz(""+rs[14]); //bandera_raiz
			c.setFechaCreacion(Utils.getDate(rs[10])); //fh_creacion
			c.setFechaModificacion(Utils.getDate(rs[11])); //fh_modificacion
			c.setNumeroAccesos(Utils.getInteger(rs[12])); //numero_accesos
			c.setNumeroCarpetas(Utils.getInteger(rs[13])); //totchildcolumn
			c.setNumeroDocumentos(Utils.getInteger(rs[15])); //numero_documentos
			c.setDescripcion(""+rs[16]); //descripcion
			c.setPassword(""+rs[17]); //password

			node.setToolTip("Contiene: " + c.getNumeroCarpetas() + " Carpetas "
					+ c.getNumeroDocumentos() + " Documentos" + "\nCreada: "
					+ c.getFechaCarpetaCreacion() + "\nProtegida: "
					+ ((c.isProtected()) ? "Sí" : "No"));

			node.setName(c.getNombreCarpeta());
			node.setObject(c);

		} else {
			Documento d = new Documento(titulo_aplicacion, id_gabinete,
					Utils.getInteger(rs[7]), //id_carpeta_padre
					Utils.getInteger(rs[8])); //id_carpeta_hija

			d.setNombreDocumento(""+rs[2]); //namecolumn
			d.setNombreUsuario(""+rs[9]); //nombre_usuario
			d.setPrioridad(Utils.getInteger(rs[18])); //prioridad
			d.setIdTipoDocto(Utils.getInteger(rs[19])); //id_tipo_docto
			d.setNombreTipoDocto(""+rs[20]); //nombre_tipo_docto
			d.setFechaCreacion(Utils.getDate(rs[10])); //fh_creacion
			d.setFechaModificacion(Utils.getDate(rs[11])); //fh_modificacion
			d.setNumeroAccesos(Utils.getInteger(rs[12])); //numero_accesos
			d.setNumeroPaginas(Utils.getInteger(rs[13])); //totchildcolumn
			d.setTitulo(""+rs[21]); //titulo
			d.setAutor(""+rs[22]); //autor
			d.setMateria(""+rs[23]); //materia
			d.setDescripcion(""+rs[16]); //descripcion
			d.setClaseDocumento(Utils.getInteger(rs[24])); //clase_documento
			d.setEstadoDocumento(""+rs[25]); //estado_documento
			d.setTamanoBytes(Utils.getDouble(rs[26])); //tamano_bytes_docto
			d.setCompartir(""+rs[27]); //compartir
			d.setTokenCompartir(""+rs[28]); //token_compartir
						
			Timestamp fecha_expira = (Timestamp)rs[29]; //FECHA_EXPIRA
			if(fecha_expira!=null) {
				d.setDateExp(DateTimeUtils.transformFromSQLDateTime(
						new Date(fecha_expira.getTime()),
						new Time(fecha_expira.getTime())
					));
				d.setHoureExp(DateTimeUtils.transformFromSQLTime(
						new Time(fecha_expira.getTime())
					));
			}

			//TO-DO: Por el momento ningún documento tiene extensión
			/*
			if ((!"IMAX_FILE".equals(d.getNombreTipoDocto())) && (rs[34] != null)) {
				int pos = (""+rs[34]).lastIndexOf(".");
				d.setExtension((pos > -1) ? (""+rs[35]).substring(pos + 1) : "");
			}
			*/
			node.setToolTip("Contiene: " + d.getDescripcionPaginas()
					+ "\nCreado: " + d.getFechaDocumentoCreacion()
					+ "\nModificado: " + d.getFechaDocumentoModificacion()
					+ "\nTamaño: " + d.getTamanoBytesTexto());

			boolean withExt = !"".equals(d.getExtension());
			node.setName(d.getNombreDocumento());
			
			//TO-DO: Arreglar las cosas que requerian paginas del documento
			/*
			Pagina p[] = null;
			int numero_paginas = 0;
			if(rs[9]!=null)
				numero_paginas = Utils.getInt(rs[9]);
			if (numero_paginas > 0) {
				p = new Pagina[1];
				p[0] = new Pagina(
						titulo_aplicacion,
						id_gabinete,
						Utils.getInt(rs[7]), //id_carpeta_padre
						Utils.getInt(rs[8]), //id_carpeta_hija
						Utils.getInt(rs[9]), //numero_pagina
						""+rs[29], //volumen
						""+rs[30], //tipo_volumen
						rs[34].toString().trim(), //nom_archivo_vol
						rs[35].toString().trim(), //nom_archivo_org
						""+rs[28], //tipo_pagina
						null, //null
						""+rs[36], //estado_pagina
						Utils.getInt(rs[37]) //tamano_bytes
						);
				p[0].setUnidadDisco(rs[31].toString().trim());
				p[0].setRutaBase(rs[32].toString().trim());
				p[0].setRutaDirectorio(rs[33].toString().trim());
			} else
				p = new Pagina[0];

			d.setPaginasDocumento(p);
			*/
			
			node.setObject(d);
		}
	}

	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}

	public void setTituloAplicacion(String titulo_aplicacion) {
		this.titulo_aplicacion = titulo_aplicacion;
	}

	public int getIdGabinete() {
		return id_gabinete;
	}

	public void setIdGabinete(int id_gabinete) {
		this.id_gabinete = id_gabinete;
	}

	private int id_gabinete_binding = -1;

	public boolean existeExpediente(String num_expediente, String nombre_campo, String valor_campo) {

		HibernateManager hm = new HibernateManager();
		try {
			Integer result = null;
			if (nombre_campo != null && !"".equals(nombre_campo)) {
				Query query = hm.createSQLQuery("SELECT id_gabinete FROM imx"
								+ titulo_aplicacion + " WHERE " + nombre_campo
								+ " = ?");
				query.setString(0, valor_campo);
				result = Utils.getInteger(query.uniqueResult());
			} else {
				Query query = hm.createSQLQuery("SELECT id_gabinete FROM imx"
						+ titulo_aplicacion + " WHERE id_gabinete = ?");
				query.setString(0, num_expediente);
				result = Utils.getInteger(query.uniqueResult());
			}
			if(result!=null) {
				id_gabinete_binding = result;
				return (id_gabinete_binding > 0);
			}
		} finally {
			hm.close();
		}
		return false;
	}

	public int getIdGabineteBinding() {
		return id_gabinete_binding;
	}
}