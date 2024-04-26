import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.OCR.ocrManager;
import com.syc.fortimax.config.Config;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.imaxfile.Volumen;
import com.syc.imaxfile.VolumenManager;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.ExportFileFormats;
import com.syc.utils.FileStorageCapacityException;
import com.syc.utils.Json;
import com.syc.utils.ParametersInterface;

@SuppressWarnings("deprecation")
public class AddPageDocumentServlet extends HttpServlet implements
		ParametersInterface {

	private static final long serialVersionUID = 1L;
	private String dirTempUp = null;
	private Thread Process_OCR = null;
	// protected Documento d = null;
			
	private static Logger log = Logger.getLogger(AddPageDocumentServlet.class);

	public void init(ServletConfig config) throws ServletException {
		dirTempUp = config.getInitParameter("tempDir");
		if (dirTempUp == null) {
			dirTempUp = config.getServletContext().getRealPath("/") + ".."
					+ File.separator + "upload" + File.separator;
		
			
			File fDir = new File(dirTempUp);
			if (!fDir.exists())
				if (!fDir.mkdirs())
					throw new ServletException(
							"No se pudo crear el directorio " + dirTempUp);
		}
	}

	public void doWork(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doWorkFortimax(req, resp);
	}
	
	private void doWorkFortimax(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Boolean redirecciona=req.getParameter("redirect")!=null?Boolean.parseBoolean(req.getParameter("redirect")):false;
		if (!FileUpload.isMultipartContent(req)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"No es una peticion multipart/form-data");
			return;
		}

		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay sesion");
			System.out.println("[" + getClass().getName() + "] No hay sesion");
			closeWindow(resp);
			return;
		}

		boolean requestFromiFImax = "true".equalsIgnoreCase((String) session
				.getAttribute("FROM_IFIMAX"));
		// boolean cfd = Boolean.parseBoolean();
		// TODO: rediseñar este mecanismo.

		try {
			if (req.getParameter("cfd") != null
					&& req.getParameter("cfd").toString() != "true") {
				sendcfdvalidaty(req, resp);
				return;
			}
		} catch (NullPointerException e) {
			log.error(e, e);

		}

		Usuario u = (Usuario) session.getAttribute(USER_KEY);

		if (u == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay sesion");
			System.out.println("[" + getClass().getName() + "] No hay usuario");
			closeWindow(resp);
			return;
		}

		ITree tree = (ITree) session.getAttribute(TREE_KEY);
		if (tree == null) {
			System.out.println("[" + getClass().getName() + "] No hay arbol");
			closeWindow(resp);
			return;
		}

		String selectId = req.getParameter("select");
		if (selectId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Sin nodo seleccionado");
			return;
		}
		
		ITreeNode treeNode = tree.findNode(selectId);
		Documento d = (Documento) treeNode.getObject();

		boolean isMyDocs = "USR_GRALES".equals(d.getTituloAplicacion());
		boolean refresh = !"true".equals(req.getParameter("norefresh"));

		// Procesa los datos recibidos
		List<?> fileItems = parseRequest(req);

		int count = 0;
		File upFile=null;
//		String unix_time="";
		FileItem item=null;
		boolean inWhile = false;
		int totFiles = fileItems.size();
//		String tmpFile = null;
		boolean insertPageDoc = false;
		Iterator<?> i = fileItems.iterator();
//		File forig = null;
//		File fdest=null;
		while (i.hasNext()) {
			inWhile = true;
			item = (FileItem) i.next();

			// Primero recibe la pagina del documento y despues los campos de la forma
			if (!item.isFormField()) { // Recibe pagina documento
				count++;
				try {
									
					//Crear nombre del documento en base la usuario,idgabinete,id,doc,idpagina
					insertPageDoc = true;
					File archivo = new File(item.getName());
//					String extension = archivo.getName().substring(archivo.getName().lastIndexOf('.') + 1);
					d.setExtension(FilenameUtils.getExtension(archivo.getName()));
//					unix_time = "" + new Date().getTime();
//					forig = new File(nombreArchivoTemp);
//					fdest = new File(unix_time+"."+ extension);
//					forig.renameTo(fdest);
//					nombreArchivoTemp = fdest.getName();
					upFile = new File(dirTempUp, archivo.getName());
					item.write(upFile);
					
					if (insertPageDoc) {
						log.debug("Guardando Pagina: " + d.getNombreDocumento());
					    guardaPaginaDocto(u.getCodigo(), d, upFile);
					}
					 				
				} catch (FileStorageCapacityException fsce) {
					log.error(fsce, fsce);				
					throw new ServletException(fsce);
				} catch (Exception e) {
					log.error(e, e);
					throw new ServletException(e);
				}
			}
		}

		synchronized (this) {
			if (refresh) {
				actualizaArbol(session, u.getNombreUsuario(), treeNode,
						tree.getExpandedNodes(), tree.getSelectedNodes(),
						isMyDocs, d);
			} else {
				int pageCount = count;
				String strCount = (String) session.getAttribute("pageCount");
				if (strCount != null)
					pageCount += Integer.parseInt(strCount);

				count = pageCount;
				session.setAttribute("pageCount", String.valueOf(pageCount));
				session.setAttribute("uploadStatus", (insertPageDoc ? "true"
						: "false"));
			}
		}

		String urlPrefix = req.getScheme() + "://" + req.getServerName() + ":"
				+ req.getServerPort() + req.getContextPath();

		String[] msg = new String[4];
		String[] scrptSufx = new String[3];
		String[] scrptPrfx = new String[1];

		scrptPrfx[0] = "function winClose(){self.close();}";

		if (count > 0)
			msg[0] = "Recepción exitosa de " + count + " página"
					+ (((count == 0) || (count > 1)) ? "s " : " ")
					+ "en Fortimax.";

		if (!insertPageDoc) {
			if (count > 0)
				msg[1] = "<br>";

			msg[2] = (totFiles - count)
					+ " página"
					+ ((((totFiles - count) == 0) || ((totFiles - count) > 1)) ? "s no se guardaron. "
							: " no se guardo. ")
					+ ((inWhile) ? "Se excede el espacio autorizado."
							: "No se recibio archivo alguno.");
		}

		if (insertPageDoc)
			msg[3] = "";

		if (requestFromiFImax) {
			tree = (ITree) session.getAttribute(TREE_EXP_KEY);
			Carpeta c = (Carpeta) tree.getRoot().getObject();
			ArbolManager amd = new ArbolManager(c.getTituloAplicacion(),
					c.getIdGabinete());
			tree = amd.generaExpediente(u.getNombreUsuario());
			scrptSufx[0] = "self.opener.top.digDoc.location.href=\""
					+ urlPrefix
					+ "/jsp/VisorDeImagenes.jsp?select="
					+ treeNode.getId()
					+ "&"
					+ INDEX_KEY
					+ "="
					+ (
							(d.getFilesNames() != null) 
							? d.getFilesNames().length
							: -1
					  ) 
					+ "\"";
			scrptSufx[1] = "";
			scrptSufx[2] = "";

			session.setAttribute(TREE_KEY, tree);

		} else {
			scrptSufx[0] = "self.opener.top.left.location.href=\"" + urlPrefix
					+ "/jsp/ArbolExpediente.jsp?select=" + treeNode.getId()
					+ "\"";

			scrptSufx[1] = "self.opener.top.main.location.href=\""
					+ urlPrefix
					+ "/jsp/VisorDeImagenes.jsp?select="
					+ treeNode.getId()
					+ "&"
					+ INDEX_KEY
					+ "="
					+ (((d.getFilesNames() != null) ? d.getFilesNames().length - 1
							: -1) + count) + "\"";
		}
		if (insertPageDoc)
			scrptSufx[2] = "self.setTimeout(\"winClose()\", "+ (insertPageDoc ? "2000" : "10000") + ");";

		session.setAttribute("msg", msg);
		session.setAttribute("scriptPrefix", scrptPrfx);
		session.setAttribute("scriptSufix", scrptSufx);
		
		String rutaArbolRedirect=urlPrefix
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
		
		String rutaRedirectMain=urlPrefix+"/jsp/Visualizador.jsp?select="+selectId+"&refreshtree=true";
		
		if(!redirecciona){
			if (refresh) //Nos indica cuando es la ultima peticion y hay que refrescar
			{
				resp.sendRedirect("jsp/Messages.jsp?ok=" + (insertPageDoc ? "true" : "false"));
			}
		}
		else{
			log.debug("Peticion realizada fuera del visualizador");
			
			/*session.setAttribute("rutaArbol", rutaArbolRedirect);
			resp.sendRedirect("jsp/Visualizador.jsp?select="+selectId+"&refreshtree=true");*/
			
			Json json=new Json();
			json.add("success", true);
			json.add("rutaArbolRedirect", rutaArbolRedirect);
			json.add("rutaMainRedirect",rutaRedirectMain);
			json.returnJson(resp, "text/html");
		}
	}

	protected List<?> parseRequest(HttpServletRequest req)
			throws ServletException {
		DiskFileUpload upload = new DiskFileUpload();

		upload.setRepositoryPath(dirTempUp); // Directorio temporal de carga de
											// archivos
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

	synchronized void guardaPaginaDocto(String userCode, Documento d,File uploadFile) throws ServletException {
		Volumen v = null;
		DocumentoManager dm = null;
		String nombreModificado = "00001";
		String nombreOriginal = uploadFile.getName();
		
		v = VolumenManager.getVolumen();

		try {
			dm = new DocumentoManager();

			nombreModificado = dm.getNextFilename(d, userCode);

		} catch (DocumentoManagerException dme) {
			log.error(dme, dme);

			throw new ServletException(dme);
		}
		
		String pathFileVol = v.getUnidad()+ ("*".equals(v.getRutaBase()) ? "" : v.getRutaBase())
		+ v.getRutaDirectorio() + v.getVolumen() + File.separator + nombreModificado;
		
		File volumenFile = new File(pathFileVol + ".tif"); 

		
		if (Config.ActivarOCR) {
			File tmpImgOCR = new File(uploadFile.getParent()+File.separator+Config.PREFIX_OCR+ uploadFile.getName());
			copiadoAlterno(uploadFile,tmpImgOCR);
			
			Process_OCR = new Thread(new ocrManager(tmpImgOCR.getAbsolutePath(), pathFileVol + Config.FILE_EXTENSION_OCR,d)); 				
			Process_OCR.setPriority(Thread.MIN_PRIORITY);
			Process_OCR.start();
        }
				
			Boolean renameTo = false;
			//Se intenta mover al volumen.
			log.debug("Moviendo al Volumen: "+ uploadFile.getAbsolutePath() );
			renameTo= uploadFile.renameTo(volumenFile); 
					
			//Si no se pudo renombrar(mover) y si existe el archivo.
			if(!renameTo && uploadFile.exists() )
			{
				log.warn("No se pudo mover al volumen: "+ uploadFile.getAbsolutePath() );
				log.debug("Intentando copiar con metodo Alterno:  "+ uploadFile.getAbsolutePath() );
				if(copiadoAlterno(uploadFile,volumenFile))
				{
					log.debug("Copiado al volumen con Exito:  "+ uploadFile.getAbsolutePath() );
					if (!uploadFile.delete())
						log.warn("No se logro borrar el archivo temporal  : " + uploadFile.getAbsolutePath());
				}
				else
					log.error("No se pudo copiar al volumen:  "+ uploadFile.getAbsolutePath() );				
			}

		try {
			d.setTamanoBytes(d.getTamanoBytes() + volumenFile.length());
			d.setIdTipoDocto(Documento.IMAX_FILE);
			dm.insertPaginaDocumento(v, d, nombreOriginal, nombreModificado, "I", volumenFile.length());
		} catch (DocumentoManagerException dme) {
			log.error(dme, dme);

		}
		
		log.info("[Agregada pagina: " + uploadFile.getName() + "] Documento: "
		+ d.getNombreDocumento() + " Gaveta: "
		+ d.getTituloAplicacion() + " Usuario: " + d.getNombreUsuario());

	}

	private Boolean copiadoAlterno(File uploadFile, File volumenFile) 
	{
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

	public void actualizaArbol(HttpSession session, String nombre_usuario,
			ITreeNode treeNode, Set<?> expandedNodes, Set<?> selectedNodes,
			boolean isMyDocs, Documento d) {

		ArbolManager amd = new ArbolManager(d.getTituloAplicacion(),
				d.getIdGabinete());
		ITree tree = amd.generaExpediente(nombre_usuario);
		if (tree != null) {
			Iterator<?> i = expandedNodes.iterator();
			while (i.hasNext()) {
				ITreeNode tn = (ITreeNode) i.next();
				tree.expand(tn.getId());
			}

			i = selectedNodes.iterator();
			while (i.hasNext()) {
				ITreeNode tn = (ITreeNode) i.next();
				tree.select(tn.getId());
			}

			session.setAttribute((isMyDocs ? TREE_MDC_KEY : TREE_EXP_KEY), tree);
		}

	}



	private void sendcfdvalidaty(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException {

		List<?> fileItems = parseRequest(req);

		//int count = 0;
		String tmpFile = null;
		Iterator<?> i = fileItems.iterator();
		while (i.hasNext()) {
			FileItem item = (FileItem) i.next();

			if (!item.isFormField()) {
				//count++;
				try {
					tmpFile = (new File(item.getName())).getName();
					String s = "" + new Date().getTime();
					File forig = new File(tmpFile);
					File fdest = new File(s);
					forig.renameTo(fdest);
					tmpFile = fdest.getName();
					File upFile = new File(dirTempUp, tmpFile);
					item.write(upFile);
				} catch (FileStorageCapacityException fsce) {
					log.error(fsce, fsce);

				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}
	}

	private void closeWindow(HttpServletResponse resp) {
		PrintWriter out = null;

		try {
			out = resp.getWriter();
			out.println("<html>");
			out.println("<head>");
			out.println("<link rel=\"stylesheet\" href=\"css/fortimax_sistema.css\" type=\"text/css\">");
			out.println("	<script type=\"text/javascript\">");
			out.println("		self.close();");
			out.println("	</script>");
			out.println("</head>");
			out.println("</html>");
		} catch (IOException ioe) {
			log.error(ioe, ioe);

		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
	
//--------------------------AddPageAppletDocumentServlet--------------------------------------
	
	private Element xmlTree;
	// protected Documento d = null;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		if("applet".equals(req.getParameter("action"))) {
			doApplet(req,resp);
		} else {
			doWork(req, resp);
		}	
	}
	
	public void doApplet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (req.getContentType() == null)
			log.debug("Preparado para recibir archivos");
		else
			doMultipart(req, resp);
	}
	
	private Usuario obtenerUsuarioDebug(HttpServletRequest req,	HttpServletResponse resp) {
		String stringUsuario = req.getParameter("usuario");
		UsuarioManager usuarioManager = new UsuarioManager();
		Usuario usuario = usuarioManager.selectUsuario(stringUsuario);
		return usuario;
	}

	private Usuario obtenerUsuarioProduccion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay sesion");
			System.out.println("[" + getClass().getName() + "] No hay sesion");
			return null;
		}

		Usuario u = (Usuario) session.getAttribute(USER_KEY);

		if (u == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay sesion");
			System.out.println("[" + getClass().getName() + "] No hay usuario");
			return null;
		}

		ITree tree = (ITree) session.getAttribute(TREE_KEY);
		if (tree == null) {
			System.out.println("[" + getClass().getName() + "] No hay arbol");
			return null;
		}
		return u;
	}
	
	private static boolean debug = false; //Verdadero permite debugear el applet desde eclipse pero elimina la seguridad, adicionalmente hay que pasarle el usuario que utiliza el applet.

	public void doMultipart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (!FileUpload.isMultipartContent(req)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"No es una peticion multipart/form-data");
			return;
		}

		Usuario usuario = null;
		if(debug) {
			usuario = obtenerUsuarioDebug(req,resp);
		}
		if(usuario == null) {
			usuario = obtenerUsuarioProduccion(req,resp);
		}
		if (usuario == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay sesion");
				System.out.println("[" + getClass().getName() + "] No hay usuario");
				closeWindow(resp);
				return;
		}

		String selectId = req.getParameter("select");
		if (selectId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Sin nodo seleccionado");
			return;
		}

		GetDatosNodo gdn = new GetDatosNodo(selectId);
		gdn.separaDatosGabinete();

		ArbolManager am = new ArbolManager(gdn.getGaveta(), gdn.getGabinete());
		this.xmlTree = am.generaExpedienteXML(usuario.getNombreUsuario());

		XMLOutputter outXML = new XMLOutputter();
		outXML.output(xmlTree, System.out);
		
		// Procesa los datos recibidos

		System.out.println(req);

		List<?> fileItems = parseRequest(req);

		File upFile = null;
		//String unix_time = "";
		FileItem item = null;
		String tmpFile = null;
		
		Iterator<?> i = fileItems.iterator();
		//File forig = null;
		//File fdest = null;

		while (i.hasNext()) {
			item = (FileItem) i.next();

			// Primero recibe la pagina del documento y despues los campos de la
			// forma
			if(item.isFormField()) {
				log.debug(item.getFieldName()+" = "+item.getString());
				
			} else { // Recibe pagina documento
				try {

					// Crear nombre del documento en base la
					// usuario,idgabinete,id,doc,idpagina
					boolean isPDF = item.getContentType().indexOf("pdf") > 0;
					tmpFile = (new File(item.getName())).getName();
					String fileExt = tmpFile.substring(tmpFile.lastIndexOf('.') + 1);
					//item.
					upFile = new File(dirTempUp, tmpFile);
					item.write(upFile);
					
					String fileRelativePath = ((FileItem) i.next()).getString();
					String parentID = getParentID(fileRelativePath,this.xmlTree);
					Documento d = new DocumentoManager().selectDocumento(parentID);
					
					
					if(isPDF) {
						//List<File> pages = ExportFileFormats.getImages(dirTempUp,upFile);
						List<File> pages = ExportFileFormats.getPages(upFile, tmpFile);
						upFile.delete();
						for(File page : pages) {
							 d.setExtension("png");
							 log.debug("Guardando Pagina: " + d.getNombreDocumento());
							 guardaPaginaDocto(usuario.getCodigo(), d, page);
						}
						/*
						for(File page : pages) {
							fileExt = page.getName().substring(tmpFile.lastIndexOf('.') + 1);
							d.setExtension(fileExt);
							log.debug("Guardando Pagina: " + d.getNombreDocumento());
							guardaPaginaDocto(u.getCodigo(), d, page);
						}*/
					} else {
						d.setExtension(fileExt);
						//unix_time = "" + new Date().getTime();
						//forig = new File(tmpFile);
						//fdest = new File(unix_time + "." + d.getExtension());
						//forig.renameTo(fdest);
						//tmpFile = fdest.getName();
						log.debug("Guardando Pagina: " + d.getNombreDocumento());
						guardaPaginaDocto(usuario.getCodigo(), d, upFile);
					}
					PrintWriter out = resp.getWriter();
					resp.setContentType("text/plain");      
					out.print("\""+tmpFile+"\" - OK");
					out.flush();
					out.close();
				} catch (Exception e) {
					log.error(e, e);
					//FileUtils.readFileToString(file, encoding)
					
					PrintWriter out = resp.getWriter();
					resp.setContentType("text/plain");      
					out.print("\""+tmpFile+"\" - FAIL : "+e.getMessage());
					out.flush();
					out.close();
				}
			}
		}
	}
	

	private String getParentID(String relativeFilePath, Element element) {
		String[] tokens = StringUtils.splitPreserveAllTokens(relativeFilePath,
				'/');
		for (int i = 1; i < tokens.length; i++) {
			@SuppressWarnings("unchecked")
			List<Element> listElements = element.getChildren("Node");
			Iterator<Element> iterator = listElements.iterator();
			Element tempElement = null;
			while (iterator.hasNext()) {
				tempElement = iterator.next();
				if (tempElement.getText().equals(tokens[i])) {
					String id = tempElement.getAttributeValue("id");
					String type = tempElement.getAttributeValue("type");
					if (type != null)
						if (type.startsWith("docto"))
							return id;
						else {
							relativeFilePath = "";
							for (int j = i + 1; j < tokens.length; j++)
								relativeFilePath += "/" + tokens[j];
							return getParentID(relativeFilePath, tempElement);
						}
				}
			}
		}
		return null;
	}
}
