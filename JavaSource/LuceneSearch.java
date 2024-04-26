import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.lucene.SearchFiles;
import com.syc.user.Usuario;
import com.syc.utils.ParametersInterface;
 public class LuceneSearch extends HttpServlet implements Servlet,ParametersInterface {

	private static final Logger log = Logger.getLogger(LuceneSearch.class); 


	private static final long serialVersionUID = 1352847771875982634L;
	private ArrayList<Document> resultados = null;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession(false);

		Usuario u = (Usuario) session
				.getAttribute(ParametersInterface.USER_KEY);
		if (u == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		ITree tree = (ITree) session.getAttribute(ParametersInterface.TREE_KEY);
		if (tree == null) {
			resp.sendRedirect("../index.jsp");
			return;
		}

		String pag = req.getParameter("pag");
		String toPag = "";
		String palabraClave = req.getParameter("palabra_clave");
		String pathToDir = Config.getLuceneIndex();

		int page = 0;
		try {
			page = Integer.parseInt(pag);
		} catch (Exception e) {	
			log.error(e,e);
		}

		if (pag == null) {
			try {
				resultados = SearchFiles.search(pathToDir, palabraClave, 100,
						page);
			} catch (ParseException e) {	
				log.error(e,e);
			}

			if (session.getAttribute("sQueryLucene") != null) {
				session.removeAttribute("sQueryLucene");
				session.removeAttribute("documentos");
			}

			session.setAttribute("sQueryLucene", req
					.getParameter("palabra_clave"));
			session.setAttribute("documentos", resultados);
		} else {
			toPag = "?pag=" + pag;
		}

		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(
						"/jsp/ResultadosBusquedaLucene.jsp" + toPag);
		dispatcher.forward(req, resp);
	}

}
