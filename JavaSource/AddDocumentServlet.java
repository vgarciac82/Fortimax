import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.imaxfile.Volumen;
import com.syc.imaxfile.VolumenManager;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.FileStorageCapacityException;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;
import com.syc.utils.Utils;

@SuppressWarnings("deprecation")
public class AddDocumentServlet extends HttpServlet implements
		ParametersInterface {

	private static final Logger log = Logger
			.getLogger(AddDocumentServlet.class);

	private static final long serialVersionUID = 1L;

	private int count;

	private String tempDir = null;

	private boolean nuevoDocumento = false;

	public void init(ServletConfig config) throws ServletException {
		tempDir = config.getInitParameter("tempDir");
		if (tempDir == null) {
			tempDir = config.getServletContext().getRealPath("/") + ".."
					+ File.separator + "upload" + File.separator;

			File fDir = new File(tempDir);
			if (!fDir.exists())
				if (!fDir.mkdirs())
					throw new ServletException(
							"No se pudo crear el directorio " + tempDir);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doWork(req, resp);
	}

	// TODO Toda esta logica debe estar en una clase. URGE
	
	void insertDocto(Usuario u, Documento d, String tmpFile, Object treeNode) throws Exception {
		try{
		if(Utils.isImaxFileFromName(tmpFile)) {
			d.setNombreTipoDocto("IMAX_FILE");
			d.setIdTipoDocto(2);		
			if(d.getIdDocumento()<0){
				DocumentoManager dm=new DocumentoManager();
				d.setNombreUsuario(u.getNombreUsuario());
				dm.insertDocumento(d);
				//d.setIdDocumento(idDocumento);
			}
			AddPageDocumentServlet servlet = new AddPageDocumentServlet();
			servlet.guardaPaginaDocto(u.getCodigo(), d, new File(tempDir, tmpFile));
		} else {
			d.setIdTipoDocto(1); //Agregue esta línea no sé si sea el lugar mas idoneo pero jala
			guardaDocto(u.getCodigo(), treeNode, d, new File(tempDir, tmpFile));
		}
		}
		catch(Exception e){
			log.error(e,e);
		}
	}
	
	public void doWork(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Json json = new Json();
		json.add("success",false);
		try {
			doWorkFortimax(req, resp);
		} catch (Exception e) {
			log.error(e,e);
			json.add("error", e.toString());
			json.add("message", e.toString());
			json.returnJson(resp, "text/html");
		}
	}

	public void doWorkFortimax(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Boolean extjs=req.getParameter("extjs")!=null?Boolean.parseBoolean(req.getParameter("extjs")):false;
		String rutaMainRedirect="";
		String rutaArbolRedirect="";
		System.gc();
		boolean requestFromiFImax = "true".equalsIgnoreCase(req
				.getParameter("ifimax"));

		if (!FileUpload.isMultipartContent(req)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"No es una peticion multipart/form-data");
			return;
		}

		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect("index.jsp");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(USER_KEY);
		if (u == null) {
			resp.sendRedirect("index.jsp");
			return;
		}

		String selectId = req.getParameter("select");
		if (selectId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Sin nodo seleccionado");
			return;
		}

		String esCorreo = req.getParameter("correo");
		//TO-DO: Nadie sabe que hace cuando es correo.
		if (esCorreo != null) {
			Object obj[] = correoOperaciones(session, selectId, u);
			selectId = obj[0].toString();
			//tree = (ITree) obj[1];
			//tree = (ITree) session.getAttribute(TREE_KEY);
		}

		Documento d = null;
		
		GetDatosNodo gdn = new GetDatosNodo(selectId);
		if(gdn.isDocumento()) {
			try {
				imx_documento_manager imx_documento_manager = new imx_documento_manager().select(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta(), gdn.getIdDocumento());
				imx_documento imx_documento = imx_documento_manager.uniqueResult();
				d = new Documento(imx_documento);
			} catch (EntityManagerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			d = new Documento(gdn.getGaveta(), gdn.getGabinete(), gdn.getIdCarpeta());
			
			d.setNombreUsuario(u.getNombreUsuario());
			// d.setIdTipoDocto(1);
			d.setNombreTipoDocto("EXTERNO");
			d.setMateria("ORIGINAL");
			d.setClaseDocumento(0);
			
			d.setPrioridad(3);
		}
		//TO-DO: Seleccionar el camino correcto para documentos nuevos y separarlo de los documentos preexistentes
		
		//boolean isMyDocs = "USR_GRALES".equals(d.getTituloAplicacion());

		// Procesa los datos recibidos
		List<?> fileItems = parseRequest(req);

		count = 0;
		String tmpFile = null;
		boolean insertDoc = false;
		Iterator<?> i = fileItems.iterator();
		while (i.hasNext()) {
			FileItem item = (FileItem) i.next();

			// Primero recibe el documento y despues los campos de la forma
			if (item.isFormField()) {
				if ("docName".equals(item.getFieldName())) {
					String docName = new String (item.getString().getBytes("iso-8859-1"), "UTF-8"); //TODO: Esta corrección debería hacerse desde el form que envía estos campos, no aquí
					d.setNombreDocumento(docName);
				}
				else if ("docDesc".equals(item.getFieldName())) {
					String docDesc = new String (item.getString().getBytes("iso-8859-1"), "UTF-8"); //TODO: Esta corrección debería hacerse desde el form que envía estos campos, no aquí
					d.setDescripcion(docDesc);
				}
			} else { // Recibe documento
				try {
					if (esCorreo != null) {
						d.setNombreDocumento(req.getParameter("docName"));
						d.setDescripcion(req.getParameter("docDesc"));
					}
					
					if (insertDoc) {
						insertDocto(u,d,tmpFile,null);
					}

					insertDoc = true;
					tmpFile = (new File(item.getName())).getName();
					int pos = tmpFile.lastIndexOf('.') + 1;
					String ext = pos != -1 ? tmpFile.substring(pos) : "";
					d.setExtension(ext);

					File upFile = new File(tempDir, tmpFile);

					/*
					 * FileStorageCapacity fsc = new FileStorageCapacity(); if
					 * (fsc.isNotMeetQuota(d.getNombreUsuario(),
					 * item.getSize())) item.write(upFile); else { insertDoc =
					 * false; break; }
					 */
					item.write(upFile);

					item.delete();
				} catch (FileStorageCapacityException fsce) {
					log.error(fsce, fsce);

					throw new ServletException(fsce);
				} catch (Exception e) {
					log.error(e, e);

					throw new ServletException(e);
				}
				count++;
			}
		}

		if (insertDoc) {
			try {
				insertDocto(u, d, tmpFile, null);
			} catch (Exception e) {
				log.error(e, e);

				throw new ServletException(e);
			}
		}

		String urlPrefix = req.getScheme() + "://" + req.getServerName() + ":"
				+ req.getServerPort() + req.getContextPath();
/*
		actualizaArbol(session, u.getNombreUsuario(),
				new ArbolManager(d.getTituloAplicacion(), d.getIdGabinete()),
				null, tree.getExpandedNodes(), tree.getSelectedNodes(),
				isMyDocs);
*/
		String[] msg = new String[1];
		if (insertDoc)
			msg[0] = "Recepción exitosa de " + count + " documento"
					+ ((count > 1) ? "s " : " ") + "en FortImax!!";
		else
			msg[0] = "El documento no se guardo. Se excede el espacio autorizado.";

		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			log.error(e, e);

		}

		if (requestFromiFImax) {
			//tree = (ITree) session.getAttribute(TREE_EXP_KEY);
			//Carpeta c = (Carpeta) tree.getRoot().getObject();
			//ArbolManager amd = new ArbolManager(c.getTituloAplicacion(),
			//		c.getIdGabinete());
			//tree = amd.generaExpediente(u.getNombreUsuario());

			String[] scrptSufx = new String[1];
			scrptSufx[0] = "window.setTimeout(\"top.digDoc.location.href='"
					+ urlPrefix
					+ "/filestore;jsessionid="
					+ session.getId()
					+ "?select="
					+ ArbolManager.generaIdNodeDocumento(
							d.getTituloAplicacion(), d.getIdGabinete(),
							d.getIdCarpetaPadre(), d.getIdDocumento())
					+ "'\", " + (nuevoDocumento ? "2000" : "2000") + ");";

			//session.setAttribute(TREE_KEY, tree);
			session.setAttribute("msg", msg);
			session.setAttribute("scriptSufix", scrptSufx);
		} else {
			String[] scrptSufx = new String[(insertDoc ? 2 : 1)];
			String[] scrptPrfx = new String[1];

			scrptPrfx[0] = "function winClose(){self.close();}";

			 		rutaArbolRedirect=urlPrefix
					+ "/jsp/ArbolExpediente.jsp;jsessionid="
					+ session.getId()
					+ "?select="
					+ ArbolManager.generaIdNodeDocumento(
							d.getTituloAplicacion(), d.getIdGabinete(),
							d.getIdCarpetaPadre(), d.getIdDocumento())
					+ "#"
					+ ArbolManager.generaIdNodeDocumento(
							d.getTituloAplicacion(), d.getIdGabinete(),
							d.getIdCarpetaPadre(), d.getIdDocumento());
			scrptSufx[0] = "top.left.location.href=\""
					+ rutaArbolRedirect + "\";";
			
			if (insertDoc) {
				rutaMainRedirect=urlPrefix
						+ "/filestore;jsessionid="
						+ session.getId()
						+ "?select="
						+ ArbolManager.generaIdNodeDocumento(
								d.getTituloAplicacion(), d.getIdGabinete(),
								d.getIdCarpetaPadre(), d.getIdDocumento());
				scrptSufx[1] = "window.setTimeout(\"top.main.location.href='"
						+ rutaMainRedirect
						+ "'\", " + (nuevoDocumento ? "2000" : "2000") + ");"; // TODO
																				// que
																				// onda
																				// con
																				// esto???
			}
			
			session.setAttribute("msg", msg);
			session.setAttribute("scriptSufix", scrptSufx);
			session.setAttribute("scriptPrefix", scrptPrfx);
		}
		String rutaVisualizador=urlPrefix+"/jsp/Visualizador.jsp?select="+d.getNodo();
		if(extjs){
			log.debug("Peticion recibida desde pantalla Extjs");
			Json json=new Json();
			json.add("success", true);
			json.add("message", "Archivo recibido correctamente espera un momento para ser redireccionado");
			//json.add("rutaMainRedirect", rutaMainRedirect);
			json.add("rutaMainRedirect", rutaVisualizador);
			json.add("rutaArbolRedirect", rutaArbolRedirect);
			json.returnJson(resp, "text/html");
		}
		else{
			resp.sendRedirect("jsp/Messages.jsp?ok=true");
		}
		
	}

	protected List<?> parseRequest(HttpServletRequest req)
			throws ServletException {
		DiskFileUpload upload = new DiskFileUpload();

		upload.setRepositoryPath(tempDir);
		// Directorio temporal de carga de archivos

		// Si el archivo excede este tamaño, ocurre un excepcion
		// FileUploadException
		upload.setSizeMax(-1); // -1 sin limite

		try {
			return upload.parseRequest(req);
		} catch (FileUploadException fe) {
			log.error(fe, fe);

			throw new ServletException("Error de recepcion " + fe.getMessage());
		}
	}

	protected void guardaDocto(String userCode, Object treeNode,
			Documento d, File tmp) throws Exception {
		Volumen v = null;
		DocumentoManager dm = null;
		String newFileName = "00001";

		v = VolumenManager.getVolumen();

		File lastUserDir = new File(v.getUnidad()
				+ ("*".equals(v.getRutaBase()) ? "" : v.getRutaBase())
				+ v.getRutaDirectorio() + v.getVolumen() + File.separator);

		/* String[] lastUserFiles = */lastUserDir
				.list(new AddDocumentFilenameFilter(userCode));

		String s = "" + new Date().getTime();
		newFileName = s.substring(s.length() - 5);

		newFileName = userCode
				+ "00000".substring(0, 5 - newFileName.length())
				+ newFileName;

		File rn = new File(v.getUnidad()
				+ ("*".equals(v.getRutaBase()) ? "" : v.getRutaBase())
				+ v.getRutaDirectorio() + v.getVolumen() + File.separator
				+ newFileName + ".tif");

		String originalName = tmp.getName();
		if (!tmp.renameTo(rn)) {
			try {
				FileInputStream frin = new FileInputStream(tmp);
				FileOutputStream fwout = new FileOutputStream(rn);

				byte buf[] = new byte[1024 * 4];
				while ((frin.read(buf)) != -1)
					fwout.write(buf);

				fwout.flush();
				fwout.close();
				frin.close();
			} catch (FileNotFoundException exc) {
				log.error(exc, exc);

				throw new ServletException(exc);
			} catch (IOException exc) {
				log.error(exc, exc);

				throw new ServletException(exc);
			}
		}

		if (!tmp.delete())
			log.error("[" + this.getClass().getName()
					+ ".guardaDocto] No se logro borrar el archivo "
					+ tmp.getAbsolutePath());
		try {
			d.setTamanoBytes(rn.length());

			dm = new DocumentoManager();

			if (d.getIdDocumento() == -1) {
				dm.insertDocumento(d);
				nuevoDocumento = true;
			}

			dm.insertPaginaDocumento(v, d, originalName, newFileName, "A", rn.length());
		} catch (DocumentoManagerException dme) {
			log.error(dme, dme);

			if (rn.delete()) {
				log.trace("Se elimino exitosamente " + rn);
			}
			throw new ServletException(dme);
		}
	}

	protected void creaCarpeta(Carpeta cRoot, Carpeta c)
			throws ServletException {
		try {
			CarpetaManager cm = new CarpetaManager(cRoot);
			cm.insertCarpeta(c);
		} catch (CarpetaManagerException e) {
			log.error(e, e);

			throw new ServletException(e.getMessage());
		}
	}

	private Object[] correoOperaciones(HttpSession session, String nodo, Usuario u)
			throws ServletException {
		ITree tree = new ArbolManager(nodo).generaExpediente(u.getNombreUsuario());
		ITreeNode node = tree.getRoot();
		List<ITreeNode> l = node.getChildren();
		Iterator<ITreeNode> i = l.iterator();
		boolean existe = false;
		Carpeta c = null;
		String idNode = null;

		while (i.hasNext()) {
			Object obj = i.next().getObject();
			if (obj instanceof Carpeta) {
				c = (Carpeta) obj;
				if (c.getNombreCarpeta().trim().equals(CARPETA_CORREO)) {
					idNode = ArbolManager.generaIdNodeCarpeta(
							c.getTituloAplicacion(), c.getIdGabinete(),
							c.getIdCarpeta());
					existe = true;
					break;
				}
			}
		}

		//ITree treeReturn = tree;

		if (!existe) {
			c = (Carpeta) node.getObject();
			c.setNombreCarpeta(CARPETA_CORREO);
			c.setDescripcion(CARPETA_CORREO);
			c.setBanderaRaiz("N");
			c.setPassword("-1");
			Carpeta cRoot = (Carpeta) node.getObject();

			creaCarpeta(cRoot, c);

			//idNode = null;
			/*
			ArbolManager amd = new ArbolManager(c.getTituloAplicacion(),
					c.getIdGabinete());
*/
			idNode = ArbolManager.generaIdNodeCarpeta(c.getTituloAplicacion(),
					c.getIdGabinete(), c.getIdCarpeta());
/*
			treeReturn = actualizaArbol(session, u.getNombreUsuario(),
					tree.getExpandedNodes(), node, idNode, amd);*/
		}

		return new Object[] { idNode/*, treeReturn */};
	}

	public static class AddDocumentFilenameFilter implements FilenameFilter {
		protected String usrCode;

		public AddDocumentFilenameFilter(String usrCode) {
			this.usrCode = usrCode;
		}

		public boolean accept(File f, String s) {
			return s.startsWith(usrCode);
		}
	}

}
