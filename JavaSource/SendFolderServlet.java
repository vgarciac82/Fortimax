import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.user.Usuario;
import com.syc.utils.DesEncrypter;
import com.syc.utils.ParametersInterface;
import com.syc.zip.MakeZip;

 public class SendFolderServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(SendFolderServlet.class); 
	
	private static final long serialVersionUID = 1856716283580125995L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ITree tree=null;
		String usuario=null;
		
		String opcionVersiones=null;
		
		String token = req.getParameter("token");
		String selectId = req.getParameter("select");
		
	    if (token!=null){
	    	String strBase64 = req.getParameter("token");
		    Map<?, ?> param = DesEncrypter.decripth(strBase64);
	    	selectId = (String)param.get("select");
	    	usuario = (String)param.get("user");
	    	
	    	opcionVersiones = "versiones";
	    	String nombre = selectId;
	    	try {
				nombre = new GetDatosNodo(selectId).getName();
			} catch (Exception e) {
				log.error(e,e);
			}
			resp.setContentType("application/zip");
			resp.setHeader("Content-Disposition","attachment; filename=\""+nombre+".zip"+"\"");
			try {
				MakeZip.writeZip(usuario, selectId, false, resp.getOutputStream());
			} catch (CarpetaManagerException e) {
				log.error(e,e);
			} catch (DocumentoManagerException e) {
				log.error(e,e);
			} finally {
				IOUtils.closeQuietly(resp.getOutputStream());
			} 
			
	    }
	    else{		
				if (selectId == null) {
					resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
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

				tree = (ITree) session.getAttribute(TREE_KEY);
				if (tree == null) {
					resp.sendRedirect("index.jsp");
					return;
				}
		
				opcionVersiones = req.getParameter("opcion");
				if (opcionVersiones == null) {
					opcionVersiones = "versiones";
				}
				
				String nombre = selectId;
		    	try {
					nombre = new GetDatosNodo(selectId).getName();
				} catch (Exception e) {
					log.error(e,e);
				}
				
				resp.setContentType("application/zip");
				resp.setHeader("Content-Disposition","attachment; filename=\""+nombre+".zip"+"\"");
				try {
					MakeZip.writeZip(u.getNombreUsuario(), selectId, false, resp.getOutputStream());
				} catch (CarpetaManagerException e) {
					log.error(e,e);
				} catch (DocumentoManagerException e) {
					log.error(e,e);
				} finally {
					IOUtils.closeQuietly(resp.getOutputStream());
				}
				
	    }
		
	}	
}
