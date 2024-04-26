import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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
import com.syc.utils.ParametersInterface;

//TO-DO: Funcionalidad movida a TipoDocumentoServlet.java, marcar para eliminar

public class AddFolderDocumentServlet extends HttpServlet implements ParametersInterface {

	private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger(AddFolderDocumentServlet.class);
    
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
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
		
		if (!treeNode.getType().startsWith("carpeta"))
			treeNode = treeNode.getParent();
					
		Carpeta c = (Carpeta) treeNode.getObject();

		boolean isDocument = "true".equals(req.getParameter("docto"));
		String objName = req.getParameter("nombre");
		String objTipoDoc = req.getParameter("tipodoc");
		String objDesc = req.getParameter("descripcion");

		String idNode = null;
		ArbolManager amd = new ArbolManager(c.getTituloAplicacion(), c.getIdGabinete());

		boolean isMyDocs = "USR_GRALES".equals(c.getTituloAplicacion());

		if (isDocument) {
			Documento d = new Documento(c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta());

			d.setNombreDocumento(objName);
			d.setNombreUsuario(u.getNombreUsuario());
			d.setNombreTipoDocto(objTipoDoc);
			d.setIdTipoDocto(objTipoDoc.equalsIgnoreCase("IMAX_FILE")? 2:1);
			d.setMateria("ORIGINAL");
			d.setClaseDocumento(0);
			d.setDescripcion(objDesc);

			try {
				creaDocumento(d);
			} catch (Exception e) {	log.error(e,e);

				// TODO Auto-generated catch block
				String[] msg = new String[1];
				msg[0] =
					(isDocument ? "Documento FortImax \"" + objName + "\" fall�" : "Nueva Carpeta \"" + objName + "\" fall�")
						+ " al crearse";
				session.setAttribute("msg", msg);

				resp.sendRedirect("jsp/Messages.jsp?ok=false");
				return;
			}
			idNode =
				ArbolManager.generaIdNodeDocumento(
					d.getTituloAplicacion(),
					d.getIdGabinete(),
					d.getIdCarpetaPadre(),
					d.getIdDocumento());
		} else {
			boolean isProtected = "true".equals(req.getParameter("protect"));

			String password = "-1";
			if (isProtected)
				password = req.getParameter("pwdMD5");

			c.setNombreCarpeta(objName);
			c.setDescripcion(objDesc);
			c.setBanderaRaiz("N");
			c.setPassword(password);

			Carpeta cRoot = (Carpeta) tree.getRoot().getObject();
			creaCarpeta(cRoot, c);
			idNode = ArbolManager.generaIdNodeCarpeta(c.getTituloAplicacion(), c.getIdGabinete(), c.getIdCarpeta());
		}

		actualizaArbol(session, u.getNombreUsuario(), tree.getExpandedNodes(), treeNode, idNode, amd, isMyDocs);

		String urlPrefix =
			req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();

		String[] msg = new String[1];
		msg[0] =
			(isDocument ? "Documento FortImax \"" + objName + "\" creado" : "Nueva Carpeta \"" + objName + "\" creada")
				+ " exitosamente";

		String[] scrptSufx = new String[(isDocument ? 2 : 1)];
		scrptSufx[0] =
			"top.left.location.href=\""
				+ urlPrefix
				+ "/jsp/ArbolExpediente.jsp?select="
				+ idNode
				+ "&"
				+ TREE_TYPE_KEY
				+ "="
				+ (isMyDocs ? "d" : "e")
				+ "#"
				+ idNode
				+ "\";";

		if (isDocument) {
			scrptSufx[1] =
				"window.setTimeout(\"top.main.location.href=\\\""
					+ urlPrefix
					+ "/jsp/VisorDeImagenes.jsp?select="
					+ idNode
					+ "\\\"\", 2000);";
		}

		session.setAttribute("msg", msg);
		session.setAttribute("scriptSufix", scrptSufx);

		resp.sendRedirect("jsp/Messages.jsp?ok=true");
	}

	protected void creaCarpeta(Carpeta cRoot, Carpeta c) throws ServletException {
		try {
			CarpetaManager cm = new CarpetaManager(cRoot);
			cm.insertCarpeta(c);
			log.info("Creacion del Carpeta: "+ c.getNombreCarpeta()+ " en la Gavata : "+c.getTituloAplicacion()+" por el usuario: "+c.getNombreUsuario() );
		} catch (CarpetaManagerException e) {	log.error(e,e);

			throw new ServletException(e.getMessage());
		}
	}

	protected void creaDocumento(Documento d) throws Exception {
		try {
			DocumentoManager dm = new DocumentoManager();
			dm.insertDocumento(d);
		} catch (DocumentoManagerException e) {	log.error(e,e);

			throw new ServletException(e.getMessage());
		}
	}

	public void actualizaArbol(
		HttpSession session,
		String nombre_usuario,
		Set<?> expandedNodes,
		ITreeNode treeNode,
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

			tree.expand(treeNode.getId());

			session.setAttribute((isMyDocs ? TREE_MDC_KEY : TREE_EXP_KEY), tree);
		}
	}
}
