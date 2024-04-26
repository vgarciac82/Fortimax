package com.syc.ifimax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.DesEncrypter;
import com.syc.utils.ParametersInterface;

public class IfimaxServlet extends HttpServlet implements ParametersInterface {
	private static final Logger log = Logger.getLogger(IfimaxServlet.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doWork(request,response);
	}

	private void doWork(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String ifimax_token = req.getParameter("ifimax_token");
		String action = req.getParameter("accion");
		Usuario u = null;
		String titulo_aplicacion;
		String id_documento;
		String nodo;
		int id_gabinete;
		long tiempoCreacion = 0;
		long tiempoExpiracion = 0;
		boolean privilegioEditarExpediente = false;
		boolean isEncrypted;

		if ((ifimax_token != null) && (ifimax_token.length() > 0)) {
			HttpSession session = req.getSession(true);

			String strBase64 = req.getParameter("ifimax_token");
			Map<String, String> token = DesEncrypter.decripth(strBase64);

			String nombre_usuario = (String) token.get("txtUsuario");
			String cdg = (String) token.get("txtClave");

			nodo = (String) token.get("nodo");

			tiempoCreacion = Long.parseLong(token.get("tiempoCreacion"));
			tiempoExpiracion = Long.parseLong(token.get("tiempoExpiracion"));

			if(tiempoCreacion + tiempoExpiracion < System.currentTimeMillis()){
				resp.sendRedirect("jsp/Messages.jsp?ifimax=true&msg=Enlace ifimax expirado");
				return;
			}

			privilegioEditarExpediente = "true".equals(((String) token.get("rw")));
			session.setAttribute("rw", privilegioEditarExpediente);
			
			isEncrypted = "true".equals(((String) token.get("CDGEnc")));

			try {
				u = obtieneUsuarioValido(nombre_usuario, cdg, isEncrypted, session, resp);
			} catch (ServletException e) {
				log.error(e,e);
				log.error("Ocurrio un error al tratar de obtener el usuario.");
			}

			if (u == null)
				return;

			session.setAttribute(USER_KEY, u);
			mostrarIfimax(resp, nodo);
		} else if ( "ifimax_viewer".equals(action)){
			HttpSession session = req.getSession(false);
			if (session == null)
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sin sesion");

			u = (Usuario) session.getAttribute(USER_KEY);
			if (u == null) {
				log.error("Usuario nulo.");
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sin usuario en sesion");
				return;
			}

			nodo = (String) req.getParameter("select");

			GetDatosNodo datosNodo = new GetDatosNodo(nodo);

			if(datosNodo.isDocumento()){
				datosNodo.separaDatosDocumento();
			} else {
				datosNodo.separaDatosCarpeta();
			}

			titulo_aplicacion = datosNodo.getGaveta();
			id_gabinete = datosNodo.getGabinete();
			id_documento = Integer.toString(datosNodo.getIdDocumento());

			privilegioEditarExpediente = (Boolean)(session.getAttribute("rw"));
			mostrarVisualizadorIfimax(resp, session, u, titulo_aplicacion, id_gabinete, nodo, privilegioEditarExpediente);
		}
	}

	private Usuario obtieneUsuarioValido(String nombre_usuario, String cdg, boolean isEncrypted,
			HttpSession session, HttpServletResponse resp) throws IOException,
			ServletException {
		UsuarioManager um = null;
		try {
			um = new UsuarioManager();
			Usuario u = um.selectUsuario(nombre_usuario);

			if (u == null) {
				String[] msgPwd = { "El usuario no existe.",
				"Inténtelo nuevamente." };
				session.setAttribute("msg", msgPwd);
				resp.sendRedirect("jsp/Messages.jsp");
				return null;
			}
			switch (um.validaUsuario(u, cdg, isEncrypted)) {
			case 0:
				return u;
			case 1:
				String[] msgPwd = {
						"La combinaci\u00F3n de usuario y contrase&ntilde;a no existe.",
				"Inténtelo nuevamente." };
				session.setAttribute("msg", msgPwd);
				resp.sendRedirect("jsp/Messages.jsp");
				break;
			case 2:
				String[] msgVgn = { "Su periodo de vigencia a caducado" };
				session.setAttribute("msg", msgVgn);
				resp.sendRedirect("jsp/Messages.jsp");
				break;
			case 3:
				String[] msgWeb = { "El usuario no esta habilitado para consulta o digitalizaci\u00F3n web" };
				session.setAttribute("msg", msgWeb);
				resp.sendRedirect("jsp/Messages.jsp?msg=");
				break;
			case 4:
				String[] msgWeb2 = { "Error general" };
				session.setAttribute("msg", msgWeb2);
				resp.sendRedirect("jsp/Messages.jsp?msg=");
			}
		} catch (Exception ue) {
			log.error(ue,ue);
			throw new ServletException(ue.getMessage());
		}
		return null;
	}

	/**
	 * Muestra en pantalla Ifimax.
	 */
	private void mostrarIfimax(HttpServletResponse resp, String nodo){
		try {
			resp.sendRedirect("jsp/ifimax.jsp?select=" + nodo);
		} catch (IOException e) {
			log.error(e,e);
		}
	}

	/**
	 * Muestra en pantalla el visualizador en Ifimax.
	 */
	private void mostrarVisualizadorIfimax(HttpServletResponse resp, HttpSession session, Usuario u, 
			String titulo_aplicacion, int id_gabinete, String nodo, boolean privilegioEditarExpediente){
		try {
			ArbolManager amd = new ArbolManager(titulo_aplicacion, id_gabinete);
			if ((titulo_aplicacion != null) && (id_gabinete != 0)) {
				if (amd.existeExpediente(String.valueOf(id_gabinete), null, null)) {
					ITree tree = amd.generaExpediente(u.getNombreUsuario());
					tree.select(nodo);
					session.setAttribute(ParametersInterface.TREE_KEY, tree);
					Documento d = (Documento) ((ITreeNode) tree.getSelectedNodes().toArray()[0]).getObject();

					//TODO: Parche que hace que un imax con 0 páginas muestre Preguarda. Es un rollback (comportamiento retrotraido).
					if (d.getTipoDocumento()==Documento.IMAX_FILE && d.getNumeroPaginas()==0) {
						if (privilegioEditarExpediente){
							resp.sendRedirect("jsp/PreGuardaDocto.jsp?select=" + nodo + "&nuevo=false&editable=true");
							log.info("Ifimax: Documento IMAX sin contenido.");
						} else {
							PrintWriter out = resp.getWriter();
							out.println("");
							out.flush();
							out.close();
						}
					} else
					//Termina Parche
					if (d.getTipoDocumento()==Documento.IMAX_FILE||d.getNumeroPaginas()>0) {
						log.info("Ifimax: Mostrando contenido de documento.");
						resp.sendRedirect("jsp/Visualizador.jsp?select=" + nodo+"&compartido="+!privilegioEditarExpediente);
					}
					else{ 
						if (privilegioEditarExpediente){
							resp.sendRedirect("jsp/PreGuardaDocto.jsp?select=" + nodo + "&nuevo=false&editable=true");
							log.info("Ifimax: Documento sin contenido.");							
						} else {
							PrintWriter out = resp.getWriter();
							out.println(""); //TODO: Enviar un JSON válido para ExtJS
							out.flush();
							out.close();
						}
					}
				}
			}
		} catch (IOException e) {
			log.error(e,e);
		}
	}
}