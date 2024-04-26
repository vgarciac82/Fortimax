package com.syc.tree.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.syc.imaxfile.GetDatosNodo;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.ParametersInterface;

public class TreeServlet extends HttpServlet implements ParametersInterface {

	private static final long serialVersionUID = -5731671693337715550L;
	private static final Logger log = Logger.getLogger(TreeServlet.class);
	private static boolean debug = false; //Verdadero permite debugear el applet desde eclipse pero elimina la seguridad, adicionalmente hay que pasarle el usuario que utiliza el applet.

	public TreeServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		doPost(req,resp);
	}

	private void sendXMLTree(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
		String select = req.getParameter("select");
		outputXML(req,resp,select,usuario);
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
			closeWindow(resp);
			return null;
		}
		Usuario usuario = (Usuario) session.getAttribute(USER_KEY);
		return usuario;
	}
	
	private void outputXML (HttpServletRequest req,	HttpServletResponse resp, String select, Usuario usuario) throws IOException {
		GetDatosNodo gdn = new GetDatosNodo(select);
		gdn.separaDatosGabinete();

		ArbolManager am = new ArbolManager(gdn.getGaveta(), gdn.getGabinete());
		Element e = am.generaExpedienteXML(usuario.getNombreUsuario());

		resp.setContentType("text/xml");
		resp.addHeader("Pragma", "no-cache");
		resp.addHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);

		ServletOutputStream out = resp.getOutputStream();
		XMLOutputter outXML = new XMLOutputter();
		outXML.output(e, out);
		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		sendXMLTree(request,response);
	}

	public void init() throws ServletException {
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
}
