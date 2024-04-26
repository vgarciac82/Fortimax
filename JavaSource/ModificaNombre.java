import java.io.IOException;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Transaction;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.imaxfile.Documento;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.utils.ParametersInterface;
 public class ModificaNombre extends HttpServlet implements Servlet, ParametersInterface {

	private static final long serialVersionUID = -7126094022814017849L;
	private static Logger log = Logger.getLogger(ModificaNombre.class);
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);

		Usuario u = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
		if (u == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		ITree tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);
		if (tree == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		String titulo_aplicacion = req.getParameter("titulo_aplicacion");
		String id_gabinete = req.getParameter("id_gabinete");
		String select = req.getParameter("select");
		String id_carpeta = null;
		String id_documento = null;
		String id_carpeta_padre = null;

		Iterator<?> i = tree.getSelectedNodes().iterator();
		Object obj = null;
		if (i.hasNext()) {
			ITreeNode n = (ITreeNode) i.next();
			obj = n.getObject();
		}

		id_carpeta = req.getParameter("id_carpeta");
		if (obj instanceof Documento) {
			id_documento = req.getParameter("id_documento");
		} else {
			id_carpeta_padre = req.getParameter("id_carpeta_padre");
		}
		
		
		HibernateManager hm = new HibernateManager();
		Query queryD;			
		try {
			Transaction trans=hm.getSession().beginTransaction();
			if (obj instanceof Documento)
			{
				     hm.executeSQLQuery("UPDATE imx_documento SET nombre_documento = '"
						+ req.getParameter("nombre")
						+"', DESCRIPCION='"+req.getParameter("descripcion")
						+ "' WHERE titulo_aplicacion='"
						+ titulo_aplicacion
						+ "' "
						+ " AND id_gabinete="
						+ id_gabinete
						+ " AND id_carpeta_padre="
						+ id_carpeta
						+ " AND id_documento="
						+ id_documento);
			}
			else 
			{
				      hm.executeSQLQuery(
						"UPDATE imx_carpeta SET nombre_carpeta = '"
							+ req.getParameter("nombre")
							+"', DESCRIPCION='"+req.getParameter("descripcion")
							+ "' WHERE titulo_aplicacion='"
							+ titulo_aplicacion
							+ "' "
							+ " AND id_gabinete="
							+ id_gabinete
							+ " AND id_carpeta="
							+ id_carpeta);

				      hm.executeSQLQuery(
						"UPDATE imx_org_carpeta SET nombre_hija = '"
							+ req.getParameter("nombre")
							+ "' WHERE titulo_aplicacion='"
							+ titulo_aplicacion
							+ "' "
							+ " AND id_gabinete="
							+ id_gabinete
							+ " AND id_carpeta_hija="
							+ id_carpeta
							+ " AND id_carpeta_padre="
							+ id_carpeta_padre);
			}			
			trans.commit();
		} catch (Exception e) {
			log.error(e,e);
		} finally {
			hm.close();
		}

		
		ArbolManager a = new ArbolManager(titulo_aplicacion, new Integer(id_gabinete).intValue());
		tree = a.generaExpediente(u.getNombreUsuario());

		if ("USR_GRALES".equals(titulo_aplicacion)) {
			session.setAttribute(ParametersInterface.TREE_MDC_KEY, tree);
		} else {
			session.setAttribute(ParametersInterface.TREE_EXP_KEY, tree);
		}

		RequestDispatcher dispatcher =
			getServletContext().getRequestDispatcher(
				"/jsp/ExitoAtributoDocumento.jsp?titulo_aplicacion="
					+ titulo_aplicacion
					+ "&id_gabinete="
					+ id_gabinete
					+ "&select="
					+ select
					+ "&accion=mn");
		dispatcher.forward(req, resp);

		log.info("[Modificacion Nombre Expediente] Gaveta: Usuario"+ u.getNombreUsuario() );
	}

}
