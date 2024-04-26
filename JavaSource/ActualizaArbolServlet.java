import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.imaxfile.Carpeta;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.ParametersInterface;

public class ActualizaArbolServlet extends HttpServlet implements
		ParametersInterface {

	private static final Logger log = Logger
			.getLogger(ActualizaArbolServlet.class);

	public static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			PrintWriter out = resp.getWriter();
			out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
			out.flush();
			out.close();
			return;
		}

		Usuario u = (Usuario) session.getAttribute(USER_KEY);
		if (u == null) {
			PrintWriter out = resp.getWriter();
			out.println("<script language=\"javascript\">top.location.href(\"index.jsp\")</script>");
			out.flush();
			out.close();
			return;
		}

		ITree currTree = (ITree) session.getAttribute(TREE_KEY);

		// Actualiza arbol Mis Documentos
		ArbolManager amd = new ArbolManager("USR_GRALES", u.getIdGabinete());
		ITree tree = amd.generaExpediente(u.getNombreUsuario());
		if (tree == null) {
			log.error("["
					+ this.getClass().getName()
					+ ".doGet] Error al actualizar arbol Mis Documentos (TA:USR_GRALES, G:"
					+ u.getIdGabinete() + ", U:" + u.getNombreUsuario() + ")");
			return;
		} else {
			tree.select(tree.getRoot().getId());
			tree.expand(tree.getRoot().getId());
			session.setAttribute(TREE_MDC_KEY, tree);
		}

		// Si el usuario es PyME
		if (u.isPyME()) {
			// Actualiza arbol Gavetas
			tree = amd.generaGaveta(u.getNombreUsuario(), u.getDescripcion());

			if (tree == null) {
				log.error("[" + this.getClass().getName()
						+ ".doGet] Error al actualizar arbol Gavetas (U:"
						+ u.getNombreUsuario() + ", D:" + u.getDescripcion()
						+ ")");
			} else {
				tree.select(tree.getRoot().getId());
				tree.expand(tree.getRoot().getId());
				session.setAttribute(TREE_APP_KEY, tree);
			}

			// Actualiza arbol Expediente
			tree = (ITree) session.getAttribute(TREE_EXP_KEY);
			if (tree != null) {
				Carpeta c = (Carpeta) tree.getRoot().getObject();
				amd = new ArbolManager(c.getTituloAplicacion(),
						c.getIdGabinete());
				tree = amd.generaExpediente(u.getNombreUsuario());

				if (tree == null) {
					log.error("["
							+ this.getClass().getName()
							+ ".doGet] Error al actualizar arbol Expediente (TA:"
							+ c.getTituloAplicacion() + ", G:"
							+ c.getIdGabinete() + ", U:" + u.getNombreUsuario()
							+ ")");
					return;
				} else {
					tree.select(tree.getRoot().getId());
					tree.expand(tree.getRoot().getId());
					session.setAttribute(TREE_EXP_KEY, tree);
				}
			}
		}

		boolean noHaySeleccionados = true;
		StringBuffer queryString = new StringBuffer();

		if (currTree != null) {
			for (Iterator<?> iter = currTree.getSelectedNodes().iterator(); iter
					.hasNext();) {
				noHaySeleccionados = false;
				ITreeNode n = (ITreeNode) iter.next();
				queryString.append("?select=" + n.getId());
				break;
			}

			if (noHaySeleccionados)
				queryString.append("?select=" + currTree.getRoot().getId());
		}

		resp.sendRedirect("jsp/ArbolExpediente.jsp" + queryString.toString());
	}
}
