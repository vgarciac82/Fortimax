import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManager;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.FileStorageCapacity;
import com.syc.utils.FileStorageCapacityException;
import com.syc.utils.ParametersInterface;
 public class DelFolderDocumentServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(DelFolderDocumentServlet.class); 


	private static final long serialVersionUID = 9105763795310968406L;
	private Carpeta cRoot = null;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

		if ("carpeta.root".equals(treeNode.getType())) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se puede borrar el nodo ra�z");
			return;
		}

		cRoot = (Carpeta) tree.getRoot().getObject();

		boolean isFolder =
			"carpeta.hija".equals(treeNode.getType())
				? true
				: "carpeta.prtgda".equals(treeNode.getType())
				? true
				: false;

		boolean isMyDocs = false;

		if (isFolder) {
			Carpeta c = (Carpeta) treeNode.getObject();

			procesaCarpetas(c, treeNode);

			isMyDocs = "USR_GRALES".equals(c.getTituloAplicacion());

			actualizaArbol(
				session,
				u.getNombreUsuario(),
				tree,
				treeNode,
				new ArbolManager(c.getTituloAplicacion(), c.getIdGabinete()),
				isMyDocs);
		} else {
			Documento d = (Documento) treeNode.getObject();
			try {
				d = new DocumentoManager().selectDocumento(d.getTituloAplicacion(),d.getIdGabinete(),d.getIdCarpetaPadre(),d.getIdDocumento());
			} catch (DocumentoManagerException e) {
				log.error("No se pudo obtener el documento de la base de datos.");
			}
			eliminaDocto(d, true);

			isMyDocs = "USR_GRALES".equals(d.getTituloAplicacion());

			actualizaArbol(
				session,
				u.getNombreUsuario(),
				tree,
				treeNode,
				new ArbolManager(d.getTituloAplicacion(), d.getIdGabinete()),
				isMyDocs);
		}

		String urlPrefix =
			req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

		String[] msg = new String[1];
		String[] scrptSufx = new String[1];

		msg[0] =
			(isFolder ? "La Carpeta " : "El Documento ")
				+ "<i>"
				+ treeNode.getName()
				+ "</i>"
				+ (isFolder ? " y su descendencia eliminada " : " eliminado ")
				+ "exitosamente";

		scrptSufx[0] =
			"top.left.location.href=\""
				+ urlPrefix
				+ "/jsp/ArbolExpediente.jsp?select="
				+ treeNode.getParent().getId()
				+ "#"
				+ treeNode.getParent().getId()
				+ "\"";

		session.setAttribute("msg", msg);
		session.setAttribute("scriptSufix", scrptSufx);

		resp.sendRedirect("jsp/Messages.jsp?ok=true");
	}

	private void eliminaDocto(Documento d, boolean updDb) throws ServletException {
		if (updDb) {
			try {
				DocumentoManager dm = new DocumentoManager();
				dm.deleteDocumento(d);
			} catch (DocumentoManagerException dme) {	log.error(dme,dme);

				throw new ServletException(dme);
			}
		}

		if (d.getNumeroPaginas() > 0) {
			for (int i = 0; i < d.getFullPathFilesNames().length; i++) {
				if (d.getFullPathFilesNames()[i] != null) {
					File file2del = new File(d.getFullPathFilesNames()[i]);
					long l = file2del.length();
					System.gc(); // No me gusta pero funciona
					if (file2del.delete()) {
						try {
							FileStorageCapacity fsc = new FileStorageCapacity();
							fsc.decrementQuota(d.getNombreUsuario(), l);
						} catch (FileStorageCapacityException fsce) {	log.error(fsce,fsce);

							throw new ServletException(fsce);
						}
					}
				}
			}
		}
	}

	private void procesaCarpetas(Carpeta c, ITreeNode treeNode) throws ServletException {
		if (treeNode.hasChildren()) {
			List<ITreeNode> cl = treeNode.getChildren();
			Iterator<ITreeNode> i = cl.iterator();
			while (i.hasNext()) {
				ITreeNode tn = i.next();

				if (tn.getType().startsWith("carpeta")) {
					Carpeta ch = (Carpeta) tn.getObject();
					procesaCarpetas(ch, tn);
				} else {
					eliminaDocto((Documento) tn.getObject(), false);
				}
			}
		}

		eliminaCarpeta(c);
	}

	private void eliminaCarpeta(Carpeta c) throws ServletException {
		try {
			CarpetaManager cm = new CarpetaManager(cRoot);
			cm.deleteCarpeta(c);
		} catch (CarpetaManagerException cme) {	log.error(cme,cme);

			throw new ServletException(cme);
		}
	}

	public void actualizaArbol(
		HttpSession session,
		String nombre_usuario,
		ITree currentTree,
		ITreeNode treeNode,
		ArbolManager amd,
		boolean isMyDocs) {
		ITree tree = amd.generaExpediente(nombre_usuario);

		if (tree != null) {
			Iterator<?> i = currentTree.getExpandedNodes().iterator();
			while (i.hasNext()) {
				ITreeNode tn = (ITreeNode) i.next();
				tree.expand(tn.getId());
			}

			tree.select(treeNode.getParent().getId());

			session.setAttribute((isMyDocs ? TREE_MDC_KEY : TREE_EXP_KEY), tree);
		}
	}
}
