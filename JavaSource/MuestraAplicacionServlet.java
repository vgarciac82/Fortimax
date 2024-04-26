import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.ParametersInterface;
 public class MuestraAplicacionServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(MuestraAplicacionServlet.class); 


	private static final long serialVersionUID = -8829861006007904447L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		int image_index = 0;
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

		String titulo_aplicacion = req.getParameter("select");
		if (titulo_aplicacion == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
			return;
		}

		String idGabineteValue = req.getParameter("id_gabinete");
		if (idGabineteValue == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin Id de Gabinete");
			return;
		}

		String select = null;
		String toSelect = req.getParameter("toSelect");
		boolean isMyDocs = "USR_GRALES".equals(titulo_aplicacion);

		int id_gabinete = Integer.parseInt(idGabineteValue);

		ArbolManager am = new ArbolManager(titulo_aplicacion, id_gabinete);

		ITree tree = am.generaExpediente(u.getNombreUsuario());

		if (toSelect == null) {
			select = tree.getRoot().getId();
			tree.select(tree.getRoot().getId());
			tree.expand(tree.getRoot().getId());
		} else {
			select = toSelect;
			tree.select(toSelect);

			ITreeNode n = tree.findNode(toSelect);
			ITreeNode p = n.getParent();
			while (p != null) {
				tree.expand(p.getId());
				p = p.getParent();
			}
		}

		session.setAttribute((isMyDocs ? TREE_MDC_KEY : TREE_EXP_KEY), tree);

		//LHMJ 
		if(req.getParameter("image.index")!=null)
			image_index=Integer.parseInt(req.getParameter("image.index"));
		// END LHMJ
		
		resp.sendRedirect(
			"jsp/ArbolExpediente.jsp?"+(req.getParameter("cambio")!=null && "si".equalsIgnoreCase(req.getParameter("cambio")) ? "cambio=si&" :"")
				+(req.getParameter("fromBusqueda")!=null && "si".equalsIgnoreCase(req.getParameter("fromBusqueda")) ? "fromBusqueda=si&" :"")
				+(req.getParameter("fromBusqueda")!=null && "ocr".equalsIgnoreCase(req.getParameter("fromBusqueda")) ? "fromBusqueda=ocr&image.index=" + image_index + "&" :"")
				+ "select="
				+ select
				+ "&"
				+ TREE_TYPE_KEY
				+ (isMyDocs ? "=d" : "=e")
				+ "&nada=0#"
				+ select);
	}
}
