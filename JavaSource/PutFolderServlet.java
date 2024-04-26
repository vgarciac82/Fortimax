import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import com.syc.fortimax.FortimaxException;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.Volumen;
import com.syc.imaxfile.VolumenManager;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.FileStorageCapacity;
import com.syc.utils.FileStorageCapacityException;
import com.syc.utils.ParametersInterface;
 public class PutFolderServlet extends HttpServlet implements ParametersInterface { 
	
	private static final long serialVersionUID = 1L;
	protected String tempDir = null;
	private static final Logger log = Logger.getLogger(PutFolderServlet.class);

	public void init(ServletConfig config) throws ServletException {
		tempDir = config.getInitParameter("tempDir");
		if (tempDir == null) {
			tempDir = config.getServletContext().getRealPath("/") + ".." + File.separator + "upload" + File.separator;

			File fDir = new File(tempDir);
			if (!fDir.exists())
				if (!fDir.mkdirs())
					throw new ServletException("No se pudo crear el directorio " + tempDir);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		HttpSession session = req.getSession(false);
		
		if (session == null) {
			System.out.println("SESION ES NULA!! TRUENA EN SESION");	
			resp.sendRedirect("index.jsp");
			return;
		}
		

		Usuario u = (Usuario) session.getAttribute(USER_KEY);
		if (u == null) {
			resp.sendRedirect("index.jsp");
			return;
		}


		ITree tree = (ITree) session.getAttribute(TREE_KEY);
		if (tree == null) {
			resp.sendRedirect("index.jsp");
			return;
		}
		

		String selectId = req.getParameter("select");
		if (selectId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
			return;
		}

		ITreeNode treeNode = tree.findNode(selectId);
		if (!treeNode.getType().startsWith("carpeta"))
			treeNode = treeNode.getParent();

		Carpeta c = (Carpeta) treeNode.getObject();

		c.setNombreUsuario(u.getNombreUsuario());

		boolean isMultipart = FileUpload.isMultipartContent(req);

		if (isMultipart == true) {
			DiskFileUpload upload = new DiskFileUpload();

			upload.setSizeThreshold(1);
			upload.setSizeMax(-1);
			upload.setRepositoryPath(tempDir);

			List<?> items = null;
			try {
				items = parseRequest(req, upload);
			} catch (FileUploadException e) {	log.error(e,e);

				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				return;
			}
			
			Map<String, FileItem> param = new LinkedHashMap<String, FileItem>();
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem fi = (FileItem) iter.next();
				param.put(fi.getFieldName(), fi);
			}

			FileItem relativeFilename = (FileItem) param.get("relativefilename");
			FileItem uploadfile = (FileItem) param.get("uploadfile");

			if (uploadfile == null) {
				sendSelectFolderMessage(resp);
				return;
			}
			
			int idxa = relativeFilename.getString().length();
			int idxb = uploadfile.getName().length();
			String basePath = "";
			if (idxa > idxb) {
				basePath = relativeFilename.getString().substring(0, (idxa - idxb) - 1);
				basePath = basePath.replace('\\', '/').replace('/', File.separatorChar);
			}
			
			//vemos si trae ' en el nombre
			if(basePath.indexOf("'")!=-1){
				String baseBack = basePath;				
				//basePath = basePath.replaceAll("'", "\\\\'");
				basePath = basePath.replaceAll("'", "&#39;");
				//cambiamos el archivo de nombre fisicamente
				File fback = new File(baseBack);
				File fnew = new File(basePath);
				fback.renameTo(fnew);				
			}
			
			CarpetaManager cm = null;
			try {
				cm = new CarpetaManager((Carpeta) tree.getRoot().getObject());
			} catch (CarpetaManagerException cme) {	log.error(cme,cme);

				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cme.getMessage());
				return;
			}
			
			String nodeToSelect = null;
			boolean isImaxFile = false;
			boolean isFirstTime = true;
			String docName = uploadfile.getName();
			Carpeta folder = c;
			for (StringTokenizer st = new StringTokenizer(basePath, File.separator); st.hasMoreTokens();) {
				String f = st.nextToken();
				int idx = f.indexOf(".imx");

				if (isImaxFile = (idx != -1)) {
					docName = f.substring(0, idx);
					break;
				}
				
				if(session.getAttribute("isFirstFromUploadCarpetaApplet")==null){
					folder = cm.getFolder(folder, f, true);
					session.setAttribute("isFirstFromUploadCarpetaApplet","true");
				}
				else
					folder = cm.getFolder(folder, f, false);
				
				folder.setNombreUsuario(u.getNombreUsuario());

				if (isFirstTime) {
					isFirstTime = false;
					nodeToSelect =
						ArbolManager.generaIdNodeCarpeta(
							folder.getTituloAplicacion(),
							folder.getIdGabinete(),
							folder.getIdCarpeta());
				}
			}
			
			DocumentoManager dm = null;
			try {
				
				dm = new DocumentoManager();
				
			} catch (DocumentoManagerException dme) {	log.error(dme,dme);

				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, dme.getMessage());
				return;
			}
			
			String ext = uploadfile.getName().substring(uploadfile.getName().lastIndexOf('.') + 1);
			boolean extPerm = extPermitida(isImaxFile, ext);
			Documento d = dm.getDocumento(folder, docName);
					
//d = null;
			if (d == null) {
				int posExt = uploadfile.getName().lastIndexOf('.');
				String filename = (posExt > -1) ? uploadfile.getName().substring(0, posExt) : uploadfile.getName();

				d = new Documento(folder.getTituloAplicacion(), folder.getIdGabinete(), folder.getIdCarpeta());

				d.setNombreDocumento((isImaxFile ? docName : filename));
				d.setExtension((isImaxFile ? "" : ext));
				d.setNombreUsuario(u.getNombreUsuario());
				d.setIdTipoDocto((isImaxFile ? 2 : 1));

				if (isImaxFile)
					d.setNombreTipoDocto("IMAX_FILE");
				else
					d.setNombreTipoDocto("EXTERNO");

				d.setMateria("ORIGINAL");
				d.setClaseDocumento(0);
				d.setDescripcion("");
				d.setTamanoBytes(0);

				if (extPerm){
					try {
						dm.insertDocumento(d);
					} 
					catch(FortimaxException FE){	
						log.error(FE,FE);
					}
					catch(Exception E)
					{	log.error(E,E);

					}	
				}

				if (isImaxFile)
					session.setAttribute("docImaxFileAnt", docName);
			} else {
				String nodeId =
					ArbolManager.generaIdNodeDocumento(
						folder.getTituloAplicacion(),
						folder.getIdGabinete(),
						folder.getIdCarpeta(),
						d.getIdDocumento());
				
				ITreeNode node = tree.findNode(nodeId);
				if (node != null) {
					Documento dt = (Documento) node.getObject();

					d.setPaginasDocumento(dt.getPaginasDocumento());
				}
				
				String docImaxFileAnt = (String) session.getAttribute("docImaxFileAnt");
				if (docImaxFileAnt == null)
					docImaxFileAnt = "";

				if (extPerm && !docImaxFileAnt.equals(docName)) {
					session.setAttribute("docImaxFileAnt", docName);
					try {
						dm.deleteAllPaginasDocumento(d,u.getNombre());
					} catch (DocumentoManagerException dme) {	log.error(dme,dme);

						resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, dme.getMessage());
						return;
					}
				}
							
			}
			boolean isQuotaFull = false;
			String tmpFile = (new File(uploadfile.getName())).getName();
			File upFile = new File(tempDir, tmpFile);
			
			if (extPerm) {
				boolean isNotMeetQuota = false;
				try {
					FileStorageCapacity fsc = new FileStorageCapacity();
					isNotMeetQuota = fsc.isNotMeetQuota(u.getNombreUsuario(), uploadfile.getSize());
				} catch (FileStorageCapacityException fsce) {	log.error(fsce,fsce);

					throw new ServletException(fsce.getMessage());
				}
				if (isNotMeetQuota) {
					try {
						uploadfile.write(upFile);
					} catch (Exception e) {	log.error(e,e);

						throw new ServletException(e.getMessage());
					}
				} else 
				{

					isQuotaFull = true;
				}
			}
			
			uploadfile.delete();
			if (!isQuotaFull) {
				if (extPerm) {
					d.setExtension(ext);
					guardaPaginaDocto(dm, u.getCodigo(), d, upFile, isImaxFile,session);
				}
			}
			
			int pageCount = extPerm ? 1 : 0;
			String strCount = (String) session.getAttribute("pageCount");
			if (strCount != null)
				pageCount += Integer.parseInt(strCount);
			
			session.setAttribute("pageCount", String.valueOf(pageCount));
			session.setAttribute("uploadStatus", "true");

			String urlPrefix =
				req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

			String[] msg = new String[4];
			String[] scrptSufx = new String[3];
			String[] scrptPrfx = new String[1];

			scrptPrfx[0] = "function winClose(){self.close();}";

			msg[0] =
				"Recepción existosa de "
					+ pageCount
					+ " archivo"
					+ (((pageCount == 0) || (pageCount > 1)) ? "s " : " ")
					+ "en FortImax!!";
			msg[1] =
				(!isQuotaFull)
					? " "
					: "La transferencia de archivos se interrumpi� por que exceden su cuota autorizada.";

			msg[2] =
				"Esta ventana se cerrará automaticamente en 2 segundos, "
					+ "si no es as&iacute;, de un clik <a href=\"javascript:winClose()\">aqui</a>";

			if (c.equals(folder)) {
				scrptSufx[0] =
					"self.opener.top.left.location.href=\""
						+ urlPrefix
						+ "/jsp/ArbolExpediente.jsp?select="
						+ ArbolManager.generaIdNodeDocumento(
							d.getTituloAplicacion(),
							d.getIdGabinete(),
							d.getIdCarpetaPadre(),
							d.getIdDocumento())
						+ "#"
						+ ArbolManager.generaIdNodeDocumento(
							d.getTituloAplicacion(),
							d.getIdGabinete(),
							d.getIdCarpetaPadre(),
							d.getIdDocumento())
						+ "\";";

				scrptSufx[1] = "self.opener.top.main.location.href=\"" + urlPrefix + "/jsp/Bienvenida.jsp\"";
			} else {
				scrptSufx[0] =
					"self.opener.top.left.location.href=\""
						+ urlPrefix
						+ "/jsp/ArbolExpediente.jsp?select="
						+ nodeToSelect
						+ "#"
						+ nodeToSelect
						+ "\";";

				scrptSufx[1] = "self.opener.top.main.location.href=\"" + urlPrefix + "/jsp/Bienvenida.jsp\";";
			}

			scrptSufx[2] = "self.setTimeout(\"winClose()\", 2000);";

			session.setAttribute("msg", msg);
			session.setAttribute("scriptPrefix", scrptPrfx);
			session.setAttribute("scriptSufix", scrptSufx);

		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No es un archivo multipart");
		}
	}

	protected List<?> parseRequest(HttpServletRequest req, DiskFileUpload upload)
		throws IOException, FileUploadException {
		return upload.parseRequest(req);
	}

	protected boolean extPermitida(boolean isImaxFile, String ext) {
		String extPerm[] = { "tif", "bmp", "gif", "jpg", "png", "pnm", "wbmp", "fpx" };

		if (isImaxFile == false)
			return true;

		for (int i = 0; i < extPerm.length; i++)
			if (ext.equalsIgnoreCase(extPerm[i]))
				return true;

		return false;
	}

	protected void guardaPaginaDocto(DocumentoManager dm, String userCode, Documento d, File tmp, boolean isImaxFile, HttpSession session)
		throws ServletException {
		Volumen v = VolumenManager.getVolumen();
		
		String newName = "00001";
		
		/*File lastUserDir =
			new File(v.getUnidad() + v.getRutaBase() + v.getRutaDirectorio() + v.getVolumen() + File.separator);
		
		String[] lastUserFiles = lastUserDir.list(new PutFolderFilenameFilter(userCode));
		if (lastUserFiles != null) {
			if (lastUserFiles.length > 0) {
				String fn = lastUserFiles[(lastUserFiles.length - 1)];
				realFilename = String.valueOf(Integer.parseInt(fn.substring(3, fn.lastIndexOf('.'))) + 1);
			}
		}*/
		

		/*try {
			realFilename = dm.selectMaxFilename(d, userCode);
		} catch (DocumentoManagerException dme) {	log.error(dme,dme);

			throw new ServletException(dme);
		}*/

		//String localUserCode = userCode;

		/*if (realFilename != null) {
			localUserCode = realFilename.substring(0, 3);
			realFilename =
				String.valueOf(Integer.parseInt(realFilename.substring(3, realFilename.lastIndexOf("."))) + 1);
		} else {
			realFilename = "00000";
		}

		realFilename = localUserCode + "00000".substring(0, 5 - realFilename.length()) + realFilename;*/
		
		newName = userCode
			+ "00000".substring(0, 5 - newName.length())
			+ newName + session.getId()
			+ new java.util.Date().getTime();

		File rn =
			new File(
				v.getUnidad()
					+ v.getRutaBase()
					+ v.getRutaDirectorio()
					+ v.getVolumen()
					+ File.separator
					+ newName
					+ ".tif");

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
			} catch (FileNotFoundException exc) {	log.error(exc,exc);

				throw new ServletException(exc);
			} catch (IOException exc) {	log.error(exc,exc);

				throw new ServletException(exc);
			}
		}

		if (!tmp.delete())
			System.out.println(
				"["
					+ this.getClass().getName()
					+ ".guardaDocto] No se logro borrar el archivo "
					+ tmp.getAbsolutePath());

		d.setTamanoBytes(d.getTamanoBytes() + rn.length());

		try {
			dm.insertPaginaDocumento(v, d, tmp.getName(), newName, (isImaxFile ? "I" : "A"), rn.length());
		} catch (DocumentoManagerException dme) {	log.error(dme,dme);

			rn.delete();
			throw new ServletException(dme);
		}
	}

	private void sendSelectFolderMessage(HttpServletResponse resp) {
	}

	/*
import org.apache.log4j.Logger;
 	public class PutFolderFilenameFilter implements FilenameFilter {

	private static final Logger log = Logger.getLogger(PutFolderFilenameFilter.class); 

		protected String usrCode;
		public PutFolderFilenameFilter(String usrCode) {
			this.usrCode = usrCode;
		}
	
		public boolean accept(File f, String s) {
			return s.startsWith(usrCode);
		}
	}
	*/
}