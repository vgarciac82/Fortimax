package com.syc.tree;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_carpeta;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_org_carpeta;
import com.syc.fortimax.hibernate.entities.imx_pagina;
import com.syc.fortimax.hibernate.entities.imx_tipo_documento;
import com.syc.fortimax.hibernate.entities.imx_volumen;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.user.Usuario;
import com.syc.utils.CopyPage;
import com.syc.utils.ParametersInterface;
import com.syc.utils.Utils;

public class PasteNodeManager implements Serializable, ParametersInterface {

private static final Logger log = Logger.getLogger(PasteNodeManager.class);

	private static final long serialVersionUID = -5732738898891471287L;

	protected Usuario u;

	protected ITree treeSrc;
	protected ITreeNode nodeSource;
	protected ITreeNode nodeTarget;
	protected boolean isCopy;

	protected String startNode = "";

	protected Map<?, ?> tipoDocto = null;
	
	private HibernateManager hm = new HibernateManager();

	public PasteNodeManager(boolean isCopy, Usuario u, ITreeNode nodeSource, ITreeNode nodeTarget)
		throws PasteNodeManagerException {

		if (nodeTarget == null)
			throw new PasteNodeManagerException("El nodo destino no debe ser nulo");

		

		this.u = u;
		this.isCopy = isCopy;
		this.nodeSource = nodeSource;
		this.nodeTarget = nodeTarget;
	}

	public Documento crearLayerDocumento(HttpSession session,Documento docLayer,Carpeta carpetaLayer) throws Exception
	{
		Documento docGenerated=createNewDocto(docLayer, carpetaLayer);
		
		boolean isMyDocsSrc = "USR_GRALES".equals(docLayer.getTituloAplicacion());
		boolean isMyDocsTrg = "USR_GRALES".equals(carpetaLayer.getTituloAplicacion());

		ITree treeTrg = (ITree) session.getAttribute((isMyDocsTrg ? TREE_MDC_KEY : TREE_EXP_KEY));
		try{
			List<Query> queries = copiaPaginas(
					docLayer.getTituloAplicacion(),
					docLayer.getIdGabinete(),
					docLayer.getIdCarpetaPadre(),
					docLayer.getIdDocumento(),
					docGenerated.getTituloAplicacion(),
					docGenerated.getIdGabinete(),
					docGenerated.getIdCarpetaPadre(),
					docGenerated.getIdDocumento());
			hm.executeQueries(queries);
			
			actualizaArbol(
				session,
				u.getNombreUsuario(),
				treeTrg.getExpandedNodes(),
				ArbolManager.generaIdNodeCarpeta(carpetaLayer.getTituloAplicacion()
						,carpetaLayer.getIdGabinete(), carpetaLayer.getIdCarpeta()),
						ArbolManager.generaIdNodeDocumento(docLayer.getTituloAplicacion()
								,docLayer.getIdGabinete(), docLayer.getIdCarpetaPadre(),docLayer.getIdDocumento()),
				new ArbolManager(carpetaLayer.getTituloAplicacion(), carpetaLayer.getIdGabinete()),
				isMyDocsSrc);

			return docGenerated;
		} catch (Exception e) {
			new DocumentoManager().deleteDocumento(docGenerated);
			return null;
		} finally{
			hm.close();
		}
	}
	public String doPaste(HttpSession session) throws Exception {

		if (session == null)
			throw new PasteNodeManagerException("La session no debe ser nula");

		tipoDocto = new Hashtable<Object, Object>();
		String retval = null;
		Carpeta cTrg = (Carpeta) nodeTarget.getObject();
		boolean isMyDocsSrc = false;
		boolean isMyDocsTrg = "USR_GRALES".equals(cTrg.getTituloAplicacion());

		if (nodeSource.getObject() instanceof Carpeta) {
			Carpeta cSrc = (Carpeta) nodeSource.getObject();

			if (cTrg.equals(cSrc))
				return ArbolManager.generaIdNodeCarpeta(
					cTrg.getTituloAplicacion(),
					cTrg.getIdGabinete(),
					cTrg.getIdCarpeta());

			isMyDocsSrc = "USR_GRALES".equals(cSrc.getTituloAplicacion());
			treeSrc = (ITree) session.getAttribute((isMyDocsSrc ? TREE_MDC_KEY : TREE_EXP_KEY));

			retval = (isCopy) ? copyFolder(cSrc, cTrg) : cutFolder(treeSrc, cSrc, cTrg);

			if (isCopy) {
				ITree treeTrg = (ITree) session.getAttribute((isMyDocsTrg ? TREE_MDC_KEY : TREE_EXP_KEY));

				actualizaArbol(
					session,
					u.getNombreUsuario(),
					treeTrg.getExpandedNodes(),
					nodeTarget,
					retval,
					new ArbolManager(cTrg.getTituloAplicacion(), cTrg.getIdGabinete()),
					isMyDocsTrg);
			} else {
				if (cTrg.getTituloAplicacion().equals(cSrc.getTituloAplicacion())) {
					ITree treeTrg = (ITree) session.getAttribute((isMyDocsTrg ? TREE_MDC_KEY : TREE_EXP_KEY));

					actualizaArbol(
						session,
						u.getNombreUsuario(),
						treeTrg.getExpandedNodes(),
						nodeTarget,
						retval,
						new ArbolManager(cTrg.getTituloAplicacion(), cTrg.getIdGabinete()),
						isMyDocsTrg);
				} else {
					actualizaArbol(
						session,
						cSrc.getNombreUsuario(),
						treeSrc.getExpandedNodes(),
						nodeSource,
						retval,
						new ArbolManager(cSrc.getTituloAplicacion(), cSrc.getIdGabinete()),
						isMyDocsSrc);

					ITree treeTrg = (ITree) session.getAttribute((isMyDocsTrg ? TREE_MDC_KEY : TREE_EXP_KEY));

					actualizaArbol(
						session,
						u.getNombreUsuario(),
						treeTrg.getExpandedNodes(),
						nodeTarget,
						retval,
						new ArbolManager(cTrg.getTituloAplicacion(), cTrg.getIdGabinete()),
						isMyDocsTrg);
				}
			}
		} else {
			Documento dSrc = (Documento) nodeSource.getObject();

			isMyDocsSrc = "USR_GRALES".equals(dSrc.getTituloAplicacion());

			ITree tree = (ITree) session.getAttribute((isMyDocsSrc ? TREE_MDC_KEY : TREE_EXP_KEY));

			retval = (isCopy) ? copyDocto(dSrc, cTrg) : cutDocto(dSrc, cTrg);

			if (isCopy) {
				ITree treeTrg = (ITree) session.getAttribute((isMyDocsTrg ? TREE_MDC_KEY : TREE_EXP_KEY));

				actualizaArbol(
					session,
					u.getNombreUsuario(),
					treeTrg.getExpandedNodes(),
					nodeTarget,
					retval,
					new ArbolManager(cTrg.getTituloAplicacion(), cTrg.getIdGabinete()),
					isMyDocsSrc);
			} else {
				if (cTrg.getTituloAplicacion().equals(dSrc.getTituloAplicacion())) {
					ITree treeTrg = (ITree) session.getAttribute((isMyDocsTrg ? TREE_MDC_KEY : TREE_EXP_KEY));

					actualizaArbol(
						session,
						u.getNombreUsuario(),
						treeTrg.getExpandedNodes(),
						nodeTarget,
						retval,
						new ArbolManager(cTrg.getTituloAplicacion(), cTrg.getIdGabinete()),
						isMyDocsTrg);
				} else {
					actualizaArbol(
						session,
						dSrc.getNombreUsuario(),
						tree.getExpandedNodes(),
						nodeSource,
						retval,
						new ArbolManager(dSrc.getTituloAplicacion(), dSrc.getIdGabinete()),
						isMyDocsSrc);

					ITree treeTrg = (ITree) session.getAttribute((isMyDocsTrg ? TREE_MDC_KEY : TREE_EXP_KEY));

					actualizaArbol(
						session,
						u.getNombreUsuario(),
						treeTrg.getExpandedNodes(),
						nodeTarget,
						retval,
						new ArbolManager(cTrg.getTituloAplicacion(), cTrg.getIdGabinete()),
						isMyDocsTrg);
				}
			}
		}

		return retval;
	}

	private void doPaste(ITree tree) throws Exception {
		tipoDocto = new Hashtable<Object, Object>();
		Carpeta cTrg = (Carpeta) nodeTarget.getObject();

		treeSrc = tree;

		if (nodeSource.getObject() instanceof Carpeta) {
			Carpeta cSrc = (Carpeta) nodeSource.getObject();

			if (cTrg.equals(cSrc))
				return;

			if (isCopy)
				copyFolder(cSrc, cTrg);
			else
				cutFolder(treeSrc, cSrc, cTrg);
		} else {
			Documento dSrc = (Documento) nodeSource.getObject();

			if (isCopy)
				copyDocto(dSrc, cTrg);
			else
				cutDocto(dSrc, cTrg);
		}
	}

	private String getMessageError() {
		if (tipoDocto != null) {
			if (!tipoDocto.isEmpty()) {
				StringBuffer msg = new StringBuffer("No existe tipo documento:\\n");

				for (Iterator<?> iter = tipoDocto.keySet().iterator(); iter.hasNext();) {
					String nombreTipoDocto = (String) iter.next();
					String nombreGaveta = (String) tipoDocto.get(nombreTipoDocto);

					msg.append(nombreTipoDocto + " " + nombreGaveta + "\\n");
				}

				return msg.toString();
			}
		}

		return null;
	}

	/* copyFolder
	 * 1 Crear nueva carpeta bajo carpeta destino, con base en carpeta origen
	 * 2 Crear nuevas carpeta/documentos con base en carpetas/documentos origen (recursivo)
	 * 3 Crear nuevas paginas con base en paginas origen (copiando archivos)
	 * 4 Regenerar arbol destino (TREE_EXP_KEY o TREE_MDC_KEY)
	 */
	private String copyFolder(Carpeta cSrc, Carpeta cTrg) throws HibernateException, IOException {
		Carpeta c = createNewFolder(cSrc, cTrg);
		startNode = c.getTituloAplicacion() + c.getIdGabinete() + c.getIdCarpeta();

		try {
			hm.executeQueries(
				copiaCarpetasHijas(
				cSrc.getTituloAplicacion(),
				cSrc.getIdGabinete(),
				cSrc.getIdCarpeta(),
				cTrg.getTituloAplicacion(),
				cTrg.getIdGabinete(),
				c.getIdCarpeta(),
				true).queries
			);
		} finally {
			hm.close();			
		}
		return ArbolManager.generaIdNodeCarpeta(c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta());
	}
	

	/* cutFolder
	 * 1 Crear nueva carpeta bajo carpeta destino, con base en carpeta origen
	 * 2 Crear nuevas carpeta/documentos con base en carpetas/documentos origen (recursivo)
	 * 3 Actualizar paginas origen con nuevo id_carpeta y nuevos id_documento's
	 * 4 Borrar documentos origen (recursivo)
	 * 5 Borrar carpetas origen (recursivo)
	 * 6 Regenerar arbol
	 * 6.1 Si son iguales regenerar arbol destino (TREE_EXP_KEY o TREE_MDC_KEY)
	 * 6.2 Si son distintos regenerar arboles origen y destino (TREE_EXP_KEY y TREE_MDC_KEY)
	 */
	private String cutFolder(ITree tree, Carpeta cSrc, Carpeta cTrg) throws CarpetaManagerException, DocumentoManagerException, IOException {
		Carpeta c = createNewFolder(cSrc, cTrg);
		startNode = c.getTituloAplicacion() + c.getIdGabinete() + c.getIdCarpeta();

		try {
			hm.executeQueries(
				copiaCarpetasHijas(
				cSrc.getTituloAplicacion(),
				cSrc.getIdGabinete(),
				cSrc.getIdCarpeta(),
				cTrg.getTituloAplicacion(),
				cTrg.getIdGabinete(),
				c.getIdCarpeta(),
				false).queries
			);

			String nodeId = ArbolManager.generaIdNodeCarpeta(cSrc.getTituloAplicacion(), cSrc.getIdGabinete(), cSrc.getIdCarpeta());

			ITreeNode tni = tree.findNode(nodeId);

			Carpeta cRoot = (Carpeta) tni.getParent().getObject();
			procesaCarpetas(cRoot, cSrc, tni);

		}  finally {
			hm.close();
		}
		return ArbolManager.generaIdNodeCarpeta(c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta());
	}

	/* copyDocto
	 * 1 Crear nuevo documento bajo carpeta destino, con base en documento origen
	 * 2 Crear nuevas paginas con base en paginas origen (copiando archivos)
	 * 3 Regenerar arbol destino (TREE_EXP_KEY o TREE_MDC_KEY)
	 */
	private String copyDocto(Documento dSrc, Carpeta cTrg) throws Exception {
		Documento d = createNewDocto(dSrc, cTrg);

		try {
			hm.executeQueries(
				copiaDocumentos(
				dSrc.getTituloAplicacion(),
				dSrc.getIdGabinete(),
				dSrc.getIdCarpetaPadre(),
				dSrc.getIdDocumento(),
				d.getTituloAplicacion(),
				d.getIdGabinete(),
				d.getIdCarpetaPadre(),
				d.getIdDocumento(),
				true
				)
			);

		} finally {
			hm.close();
		}
		return ArbolManager.generaIdNodeDocumento(
				d.getTituloAplicacion(),
				d.getIdGabinete(),
				d.getIdCarpetaPadre(),
				d.getIdDocumento());
	}

	/* cutDocto
	 * 1 Crear nuevo documento bajo carpeta destino, con base en documento origen
	 * 2 Actualizar paginas origen con nuevo id_documento
	 * 3 Borrar documento origen
	 * 4 Regenerar arbol
	 * 4.1 Si son iguales regenerar arbol destino (TREE_EXP_KEY o TREE_MDC_KEY)
	 * 4.2 Si son distintos regenerar arboles origen y destino (TREE_EXP_KEY y TREE_MDC_KEY)
	 */
	private String cutDocto(Documento dSrc, Carpeta cTrg) throws Exception {
		Documento d = createNewDocto(dSrc, cTrg);

		try {
			hm.executeQueries(
				copiaDocumentos(
				dSrc.getTituloAplicacion(),
				dSrc.getIdGabinete(),
				dSrc.getIdCarpetaPadre(),
				dSrc.getIdDocumento(),
				d.getTituloAplicacion(),
				d.getIdGabinete(),
				d.getIdCarpetaPadre(),
				d.getIdDocumento(),
				false
				)
			);

			eliminaDocto(dSrc);

		} finally {
			hm.close();
		}
		return ArbolManager.generaIdNodeDocumento(
				d.getTituloAplicacion(),
				d.getIdGabinete(),
				d.getIdCarpetaPadre(),
				d.getIdDocumento());
	}

	private Carpeta createNewFolder(Carpeta cSrc, Carpeta cTrg) {
		Carpeta c = new Carpeta(cTrg.getTituloAplicacion(), cTrg.getIdGabinete(), cTrg.getIdCarpeta());

		c.setNombreCarpeta(cSrc.getNombreCarpeta());
		c.setNombreUsuario(cSrc.getNombreUsuario());
		c.setBanderaRaiz("N");
		c.setFechaCreacion(new java.sql.Date(new Timestamp(System.currentTimeMillis()).getTime()));
		c.setFechaModificacion(new java.sql.Date(new Timestamp(System.currentTimeMillis()).getTime()));
		c.setNumeroAccesos(0);
		c.setNumeroCarpetas(cSrc.getNumeroCarpetas());
		c.setNumeroDocumentos(cSrc.getNumeroDocumentos());
		c.setDescripcion(cSrc.getDescripcion());
		c.setPassword(cSrc.getPassword());

		try {
			CarpetaManager cm = new CarpetaManager(cTrg);
			cm.insertCarpeta(c);
		} catch (CarpetaManagerException cme) {	
			log.error(cme,cme);
		}
		return c;
	}

	private Documento createNewDocto(Documento dSrc, Carpeta cTrg) throws Exception {
		Documento d =
			new Documento(
				cTrg.getTituloAplicacion(),
				cTrg.getIdGabinete(),
				cTrg.getIdCarpeta(),
				-1,
				dSrc.getNombreDocumento(),
				dSrc.getNombreUsuario(),
				dSrc.getUsuarioModificacion(),
				dSrc.getPrioridad(),
				dSrc.getIdTipoDocto(),
				dSrc.getNombreTipoDocto(),
				new java.sql.Date(new Timestamp(System.currentTimeMillis()).getTime()),
				new java.sql.Date(new Timestamp(System.currentTimeMillis()).getTime()),
				0,
				dSrc.getNumeroPaginas(),
				dSrc.getTitulo(),
				dSrc.getAutor(),
				dSrc.getMateria(),
				dSrc.getDescripcion(),
				dSrc.getClaseDocumento(),
				dSrc.getEstadoDocumento(),
				dSrc.getExtension());

		d.setTamanoBytes(dSrc.getTamanoBytes());
		try {
			DocumentoManager dm = new DocumentoManager();
			dm.insertDocumento(d);
			//d.setIdDocumento(dm.getLastIdInserted());
		} catch (DocumentoManagerException dme) {	
			log.error(dme,dme);
		}
		return d;
	}
	
	private class ControlCarpetas {
		protected int id_carpeta;
		protected List<Query> queries;
		
		protected ControlCarpetas(int id_carpeta, List<Query> queries) {
			this.id_carpeta = id_carpeta;
			this.queries = queries;
		}
	}

	private ControlCarpetas copiaCarpetasHijas(String srcTituloAplicacion, int srcIdGabinete, int srcIdCarpeta,
		String trgTituloAplicacion, int trgIdGabinete, int trgIdCarpeta, boolean copyPages) throws IOException {

		int id_carpeta = trgIdCarpeta;

		List<Query> queries = new ArrayList<Query>();
		
		Query pstmnt0 = hm.createSQLQuery(
				"INSERT INTO imx_carpeta "
					+ "( titulo_aplicacion, id_gabinete, id_carpeta"
					+ ", nombre_carpeta, nombre_usuario, bandera_raiz"
					+ ", fh_creacion, fh_modificacion, numero_accesos"
					+ ", numero_carpetas, numero_documentos, descripcion, password) "
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		Query pstmnt1 = hm.createSQLQuery(
				"INSERT INTO imx_org_carpeta "
					+ "( titulo_aplicacion, id_gabinete, id_carpeta_hija, id_carpeta_padre, nombre_hija) "
					+ "VALUES (?, ?, ?, ?, ?)");

			@SuppressWarnings("unchecked")
			List<Map<String,?>> list =
				hm.createSQLQuery(
					"SELECT {o.*}, {c.*}"
						+ " FROM imx_org_carpeta o, imx_carpeta c"
						+ " WHERE c.titulo_aplicacion = o.titulo_aplicacion"
						+ " AND c.id_gabinete = o.id_gabinete"
						+ " AND c.id_carpeta = o.id_carpeta_hija"
						+ " AND o.titulo_aplicacion = '" + srcTituloAplicacion
						+ "' AND o.id_gabinete = " + srcIdGabinete
						+ " AND o.id_carpeta_padre = " + srcIdCarpeta
						+ " ORDER BY o.id_carpeta_hija")
						.addEntity("o", imx_org_carpeta.class)
						.addEntity("c", imx_carpeta.class)
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();

			for(Map<String,?> map : list) {
				imx_org_carpeta imx_org_carpeta = (imx_org_carpeta) map.get("o");
				imx_carpeta imx_carpeta = (imx_carpeta) map.get("c");
				String currNode = srcTituloAplicacion + srcIdGabinete + imx_org_carpeta.getId().getIdCarpetaHija();

				if (startNode.equals(currNode))
					break;

				Carpeta tmpC =
					(Carpeta) treeSrc
						.findNode(ArbolManager.generaIdNodeCarpeta(srcTituloAplicacion, srcIdGabinete, imx_org_carpeta.getId().getIdCarpetaHija()))
						.getObject();
				if ((tmpC.isProtected()) && (!tmpC.isOpen()))
					continue;

				// imx_carpeta
				pstmnt0.setString(0, trgTituloAplicacion); // titulo_aplicacion
				pstmnt0.setInteger(1, trgIdGabinete); // id_gabinete
				pstmnt0.setInteger(2, ++id_carpeta); // id_carpeta
				pstmnt0.setString(3, imx_carpeta.getNombreCarpeta()); // nombre_carpeta
				pstmnt0.setString(4, u.getNombreUsuario()); // nombre_usuario
				pstmnt0.setString(5, ""+imx_carpeta.getBanderaRaiz()); // bandera_raiz
				pstmnt0.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // fh_creacion
				pstmnt0.setTimestamp(7, new Timestamp(System.currentTimeMillis())); // fh_modificacion
				pstmnt0.setInteger(8, 0); // numero_accesos
				pstmnt0.setInteger(9, imx_carpeta.getNumeroCarpetas()); // numero_carpetas
				pstmnt0.setInteger(10, imx_carpeta.getNumeroDocumentos()); // numero_documentos
				pstmnt0.setString(11, imx_carpeta.getDescripcion()); // descripcion
				pstmnt0.setString(12, imx_carpeta.getPassword()); // password

				queries.add(pstmnt0);

				// imx_org_carpeta
				pstmnt1.setString(0, trgTituloAplicacion); // titulo_aplicacion
				pstmnt1.setInteger(1, trgIdGabinete); // id_gabinete
				pstmnt1.setInteger(2, id_carpeta); // id_carpeta_hija
				pstmnt1.setInteger(3, trgIdCarpeta); // id_carpeta_padre
				pstmnt1.setString(4, imx_carpeta.getNombreCarpeta()); // nombre_hija

				queries.add(pstmnt1);

				ControlCarpetas controlCarpeta = copiaCarpetasHijas(
					srcTituloAplicacion,
					srcIdGabinete,
					imx_org_carpeta.getId().getIdCarpetaHija(),
					trgTituloAplicacion,
					trgIdGabinete,
					id_carpeta,
					copyPages);
				
				id_carpeta = controlCarpeta.id_carpeta;
				queries.addAll(controlCarpeta.queries);
			}

			queries.addAll(
				copiaDocumentos(
					srcTituloAplicacion,
					srcIdGabinete,
					srcIdCarpeta,
					-1,
					trgTituloAplicacion,
					trgIdGabinete,
					trgIdCarpeta,
					-1,
					copyPages
				)
			);

		return new ControlCarpetas(id_carpeta, queries);
	}

	@SuppressWarnings("unchecked")
	private List<Query> copiaDocumentos(String srcTituloAplicacion, int srcIdGabinete, int srcIdCarpeta, int srcIdDocumento,
		String trgTituloAplicacion, int trgIdGabinete, int trgIdCarpeta, int trgIdDocumento, boolean copyPages) throws IOException {

		List<Query> queries = new ArrayList<Query>();

			List<Map<String,?>> list = new ArrayList<Map<String,?>>();
			if (srcIdDocumento == -1) {
				list = hm.createSQLQuery(
						"SELECT {documento.*}, {tipo_documento.*}"
							+ " FROM imx_documento documento, imx_tipo_documento tipo_documento"
							+ " WHERE tipo_documento.titulo_aplicacion = documento.titulo_aplicacion"
							+ " AND tipo_documento.id_tipo_docto = documento.id_tipo_docto"
							+ " AND documento.titulo_aplicacion = '" + srcTituloAplicacion
							+ "' AND documento.id_gabinete = " + srcIdGabinete
							+ " AND documento.id_carpeta_padre = " + srcIdCarpeta)
							.addEntity("documento", imx_documento.class)
							.addEntity("tipo_documento", imx_tipo_documento.class)
							.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
							.list();
			} else {
				list = hm.createSQLQuery(
						"SELECT {documento.*}, {tipo_documento.*}"
							+ " FROM imx_documento documento, imx_tipo_documento tipo_documento"
							+ " WHERE tipo_documento.titulo_aplicacion = documento.titulo_aplicacion"
							+ " AND tipo_documento.id_tipo_docto = documento.id_tipo_docto"
							+ " AND documento.titulo_aplicacion = '" + srcTituloAplicacion
							+ "' AND documento.id_gabinete = " + srcIdGabinete
							+ " AND documento.id_carpeta_padre = " + srcIdCarpeta
							+ " AND documento.id_documento = " + srcIdDocumento)
							.addEntity("documento", imx_documento.class)
							.addEntity("tipo_documento", imx_tipo_documento.class)
							.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
							.list();
			}

			for(Map<String,?> map : list) {
				imx_documento imx_documento = (imx_documento) map.get("documento");
				imx_tipo_documento imx_tipo_documento = (imx_tipo_documento) map.get("tipo_documento");
				
				queries.addAll(
					existeTipoDocto(
							trgTituloAplicacion,
							imx_documento.getIdTipoDocumento(),
							imx_documento.getNombreTipoDocto(),
							imx_documento.getPrioridad())
					);

				// TODO Validar y actualizar espacio de usuario en caso de copia

				if (srcIdDocumento == -1) {
					Query query = hm.createSQLQuery(
							"INSERT INTO imx_documento "
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
								+ ", tamano_bytes)"
								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					
					query.setString(0, trgTituloAplicacion);
					query.setInteger(1, trgIdGabinete);
					query.setInteger(2, trgIdCarpeta);
					query.setInteger(3, imx_documento.getId().getIdDocumento()); // id_documento
					query.setString(4, imx_documento.getNombreDocumento()); // nombre_documento
					query.setString(5, u.getNombreUsuario());
					query.setInteger(6, imx_documento.getPrioridad()); // prioridad
					query.setInteger(7, imx_documento.getIdTipoDocumento()); // id_tipo_docto
					query.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
					query.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
					query.setInteger(10, 0);
					query.setInteger(11, imx_documento.getNumeroPaginas()); // numero_paginas
					query.setString(12, imx_documento.getTitulo()); // titulo
					query.setString(13, imx_documento.getAutor()); // autor
					query.setString(14, imx_documento.getMateria()); // materia
					query.setString(15, imx_documento.getDescripcion()); // descripcion
					query.setInteger(16, imx_documento.getClaseDocumento()); // clase_documento
					query.setString(17, ""+imx_documento.getEstadoDocumento()); // estado_documento
					query.setDouble(18, imx_documento.getTamanoBytes()); // tamano_bytes

					queries.add(query);
				}

				if (copyPages) {
					queries.addAll(
						copiaPaginas(
								srcTituloAplicacion,
								srcIdGabinete,
								srcIdCarpeta,
								imx_documento.getId().getIdDocumento(),
								trgTituloAplicacion,
								trgIdGabinete,
								trgIdCarpeta,
								trgIdDocumento
						)
					);
				} else {
					queries.add(
						muevePaginas(
								srcTituloAplicacion,
								srcIdGabinete,
								srcIdCarpeta,
								imx_documento.getId().getIdDocumento(),
								trgTituloAplicacion,
								trgIdGabinete,
								trgIdCarpeta,
								(srcIdDocumento == -1) ? imx_documento.getId().getIdDocumento() : trgIdDocumento
						)
					);
				}
			}

			if (!copyPages) {
				String addStmnt = (srcIdDocumento == -1 ? "" : " AND id_documento = " + srcIdDocumento);

				queries.add(
					hm.createSQLQuery(
						"DELETE FROM imx_documento"
						+ " WHERE titulo_aplicacion = '" + srcTituloAplicacion
						+ "' AND id_gabinete = " + srcIdGabinete
						+ " AND id_carpeta_padre = " + srcIdCarpeta
						+ addStmnt
					)
				);
			}
			
		return queries;
	}

	private List<Query> existeTipoDocto(String titulo_aplicacion, int id_tipo_docto, String nombre_tipo_docto, int prioridad) {
		List<Query> queries = new ArrayList<Query>();
			boolean existe = hm.createSQLQuery(
				"SELECT 1"
					+ " FROM imx_tipo_documento"
					+ " WHERE titulo_aplicacion = '"
					+ titulo_aplicacion
					+ "' AND id_tipo_docto = "
					+ id_tipo_docto).uniqueResult()!=null;

			if (!existe) {
				queries.add(hm.createSQLQuery(
					"INSERT INTO imx_tipo_documento"
						+ " (titulo_aplicacion, id_tipo_docto, prioridad, nombre_tipo_docto, descripcion)"
						+ " VALUES ('"
						+ titulo_aplicacion
						+ "', "
						+ id_tipo_docto
						+ ", "
						+ prioridad
						+ ", '"
						+ nombre_tipo_docto
						+ "', 'Creada por el sistema')"));
			}
		return queries;
	}

	private List<Query> copiaPaginas(String srcTituloAplicacion, int srcIdGabinete, int srcIdCarpeta, int srcIdDocumento,
		String trgTituloAplicacion, int trgIdGabinete, int trgIdCarpeta, int trgIdDocumento) throws IOException {

		List<Query> queries = new ArrayList<Query>();
		
		Integer numeroPagina = 0;

			if (trgIdDocumento == -1) { //TO-DO debo revisar el original, no estoy seguro si inverti la lógica de esta línea
				numeroPagina = Utils.getInteger(
						hm.createSQLQuery(
						"SELECT MAX(numero_pagina)"
							+ " FROM imx_pagina"
							+ " WHERE titulo_aplicacion = '" + srcTituloAplicacion
							+ "' AND id_gabinete = " + srcIdGabinete
							+ " AND id_carpeta_padre = " + srcIdCarpeta
							+ " AND id_documento = " + srcIdDocumento
							).uniqueResult()
						);

					if(numeroPagina==null)
						numeroPagina = 0;
			}

			@SuppressWarnings("unchecked")
			List<Map<String,?>> list = hm.createSQLQuery(
					"SELECT {pagina.*}, {volumen.*}"
						+ " FROM imx_pagina pagina, imx_volumen volumen"
						+ " WHERE pagina.volumen = volumen.volumen"
						+ " AND pagina.titulo_aplicacion = '" + srcTituloAplicacion
						+ "' AND pagina.id_gabinete = " + srcIdGabinete
						+ " AND pagina.id_carpeta_padre = " + srcIdCarpeta
						+ " AND pagina.id_documento = " + srcIdDocumento)
						.addEntity("pagina",imx_pagina.class)
						.addEntity("volumen",imx_volumen.class)
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();

			for (Map<String,?> map : list) {
				imx_pagina imx_pagina = (imx_pagina)map.get("pagina");
				imx_volumen imx_volumen = (imx_volumen)map.get("volumen");
				
				Query query = hm.createSQLQuery(
						"INSERT INTO imx_pagina "
							+ "(  titulo_aplicacion"
							+ ", id_gabinete"
							+ ", id_carpeta_padre"
							+ ", id_documento"
							+ ", numero_pagina"
							+ ", volumen"
							+ ", tipo_volumen"
							+ ", nom_archivo_vol"
							+ ", nom_archivo_org"
							+ ", tipo_pagina"
							+ ", anotaciones"
							+ ", estado_pagina"
							+ ", tamano_bytes"
							+ ", pagina) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
				String fullSourcePath = imx_volumen.getUnidadDisco()+imx_volumen.getRutaBase()+imx_volumen.getRutaDirectorio()+imx_pagina.getNomArchivoVol();

				CopyPage cp = new CopyPage(u);
				cp.copyPageFile(fullSourcePath);

				query.setString(0, trgTituloAplicacion);
				query.setInteger(1, trgIdGabinete);
				query.setInteger(2, trgIdCarpeta);
				query.setInteger(3, ((trgIdDocumento <= 0) ? srcIdDocumento : trgIdDocumento));
				query.setInteger(4, ((trgIdDocumento <= 0) ? imx_pagina.getId().getNumeroPagina() : ++numeroPagina));
				query.setString(5, cp.getVolumen().getVolumen());
				query.setString(6, cp.getVolumen().getTipoVolumen());
				query.setString(7, cp.getFile().getName());
				query.setString(8, imx_pagina.getNomArchivoOrg());
				query.setString(9, imx_pagina.getTipoPagina());
				// Se asignaran anotaciones o siempre nulas?
				query.setParameter (10, null);
				query.setString(11, imx_pagina.getEstadoPagina());
				query.setDouble(12, imx_pagina.getTamanoBytes());
				query.setInteger(13, imx_pagina.getPagina());
				
				queries.add(query);
			}

		return queries;
	}

	private Query muevePaginas(String srcTituloAplicacion, int srcIdGabinete, int srcIdCarpeta, int srcIdDocumento,
		String trgTituloAplicacion, int trgIdGabinete, int trgIdCarpeta, int trgIdDocumento) {

		Query query = hm.createSQLQuery(
				"UPDATE imx_pagina"
					+ " SET titulo_aplicacion = ?,"
					+ " id_gabinete = ?,"
					+ " id_carpeta_padre = ?,"
					+ " id_documento = ?"
					+ " WHERE titulo_aplicacion = ?"
					+ " AND id_gabinete = ?"
					+ " AND id_carpeta_padre = ?"
					+ " AND id_documento = ?");
		
		query.setString(0, trgTituloAplicacion);
		query.setInteger(1, trgIdGabinete);
		query.setInteger(2, trgIdCarpeta);
		query.setInteger(3, trgIdDocumento);
		query.setString(4, srcTituloAplicacion);
		query.setInteger(5, srcIdGabinete);
		query.setInteger(6, srcIdCarpeta);
		query.setInteger(7, srcIdDocumento);
		
		return query;
	}

	private void actualizaArbol(
			HttpSession session,
			String nombre_usuario,
			Set<?> expandedNodes,
			ITreeNode treeNode,
			String idNode,
			ArbolManager amd,
			boolean isMyDocs) {
		
		actualizaArbol(
				 session,
				 nombre_usuario,
				expandedNodes,
				 treeNode.getId(),
				idNode,
				 amd,
				isMyDocs) ;

		}
	private void actualizaArbol(
			HttpSession session,
			String nombre_usuario,
			Set<?> expandedNodes,
			String treeNode,
			String idNode,
			ArbolManager amd,
			boolean isMyDocs) {
			ITree tree = amd.generaExpediente(nombre_usuario);

			if (tree != null) {
				Iterator<?> i = expandedNodes.iterator();
				while (i.hasNext()) {
					ITreeNode tn = (ITreeNode) i.next();
					tree.expand(tn.getId());
				}

				tree.expand(treeNode);
				tree.select(idNode);
				session.setAttribute((isMyDocs ? TREE_MDC_KEY : TREE_EXP_KEY), tree);
			}
		}

	private void procesaCarpetas(Carpeta cRoot, Carpeta c, ITreeNode treeNode) throws CarpetaManagerException, DocumentoManagerException {

		if (treeNode.hasChildren()) {
			List<ITreeNode> cl = treeNode.getChildren();
			 Iterator<ITreeNode> i = cl.iterator();
			while (i.hasNext()) {
				ITreeNode tn =  i.next();

				if (tn.getType().startsWith("carpeta")) {
					Carpeta ch = (Carpeta) tn.getObject();
					Carpeta p = (Carpeta) tn.getParent().getObject();
					procesaCarpetas(p, ch, tn);
				} else {
					eliminaDocto((Documento) tn.getObject());
				}
			}
		}

		eliminaCarpeta(cRoot, c);
	}

	private void eliminaCarpeta(Carpeta cRoot, Carpeta c) throws CarpetaManagerException {
		CarpetaManager cm = new CarpetaManager(cRoot);
		cm.deleteCarpeta(c);
	}

	private void eliminaDocto(Documento d) throws DocumentoManagerException {
		DocumentoManager dm = new DocumentoManager();
		dm.deleteDocumento(d);
	}
}
