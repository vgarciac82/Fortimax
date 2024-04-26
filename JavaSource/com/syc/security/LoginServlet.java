package com.syc.security;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.jenkov.prizetags.tree.itf.ITree;
import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_grupo;
import com.syc.fortimax.hibernate.entities.imx_grupo_usuario;
import com.syc.fortimax.hibernate.entities.imx_grupo_usuario_id;
import com.syc.fortimax.hibernate.managers.imx_grupo_usuario_manager;
import com.syc.fortimax.ldap.ActiveDirectoryConfigs;
import com.syc.fortimax.ldap.ActiveDirectoryManager;
import com.syc.tree.ArbolManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.Encripta;
import com.syc.utils.ParametersInterface;
import com.syc.security.Login;



public class LoginServlet extends HttpServlet implements ParametersInterface {

	private static final long serialVersionUID = 6106076956800295413L;
	private static final Logger log = Logger.getLogger(LoginServlet.class);
	private boolean conMisDocumentos = true;
	// For Admin
	private String init_usuario = "";
	private String init_password = "";

	public void init() throws ServletException {

		super.init();

		try {
			init_usuario = getServletConfig().getInitParameter("usuario");
			init_password = getServletConfig().getInitParameter("password");
			String strConMisDocumentos = getServletConfig().getInitParameter(
					"conMisDocumentos");
			if (strConMisDocumentos != null)
				conMisDocumentos = Boolean.valueOf(strConMisDocumentos);
		} catch (Exception e) {
			log.error(e, e);
		}

	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String admin = "false";
		admin = (req.getParameter("Admin") == null) ? "false" : req
				.getParameter("Admin");

		if (Boolean.parseBoolean(admin)) {
			AdminLogin(req, resp);
		} else {
			Login(req, resp);
		}

	}

	private boolean isTreeNull(HttpServletResponse resp, HttpSession session,
			ITree tree, String docDesc) throws IOException {
		if (tree == null) {
			String[] msg = {
					"No se logró crear el arbol de " + docDesc + ".",
					"Re-intente nuevamente, si el problema persiste contacte al Administrador." };
			session.setAttribute("msg", msg);
			resp.sendRedirect("jsp/Messages.jsp");

			return true;
		}

		return false;
	}

	private void AdminLogin(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
//
//		init_usuario = getServletConfig().getInitParameter("usuario");
//		init_password = getServletConfig().getInitParameter("password");

		log.trace("Iniciando Admin Login");
		HttpSession session = null;
		String txtUser = null;
		String txtPwd = null;

		session = req.getSession(false);
		if (session != null) {
			txtUser = (String) session.getAttribute("txtUser");
			txtPwd = (String) session.getAttribute("txtPwd");
			// Causa Problemas con el Admin
			//session.invalidate();
		}
		session = req.getSession(true);
		resp.setContentType("text/html");

		String nombre_usuario = req.getParameter("usuario");
		if (nombre_usuario == null) {
			nombre_usuario = txtUser;
			if (nombre_usuario == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Usuario inválido");
				return;
			}
		}

		String password = req.getParameter("password");
		if (password == null) {
			password = txtPwd;
			if (password == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Usuario inválido");
				return;
			}
		}
		password=Encripta.code32(password);
		Login login=new Login();
		if(login.Autenticacion(nombre_usuario, password)==true){
			session.setAttribute("logged", "true");
			resp.sendRedirect("Admin/jsp/Main.jsp");
			log.trace("Usuario: " + nombre_usuario + " VALIDO");
		}
		else{
			resp.sendRedirect("Admin/jsp/login.jsp?invalid=true");
			log.trace("Usuario: " + nombre_usuario + " INVALIDO");
		}
		// verificando, falta detallar combinaciones
//		if (init_usuario.equals(nombre_usuario)
//				&& init_password.equals(password)) {
//			session.setAttribute("logged", "true");
//			resp.sendRedirect("Admin/jsp/Main.jsp");
//			log.trace("Usuario: " + nombre_usuario + " VALIDO");
//		} else {
//			resp.sendRedirect("Admin/jsp/login.jsp?invalid=true");
//			log.trace("Usuario: " + nombre_usuario + " INVALIDO");
//		}

	}

	private void Login(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		HttpSession session = null;
		String txtUser = null;
		String txtPwd = null;
		session = req.getSession(false);
		if (session != null) {
			txtUser = (String) session.getAttribute("txtUser");
			txtPwd = (String) session.getAttribute("txtPwd");
			//session.invalidate();
		}
		session = req.getSession(true);
		// session.setMaxInactiveInterval((60 * 60 * 30)); // 30 minutos
		session.setMaxInactiveInterval(-1);

		resp.setContentType("text/html");

		String nombre_usuario = req.getParameter("txtUsuario");
		if (nombre_usuario == null) {
			nombre_usuario = txtUser;
			if (nombre_usuario == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Usuario inválido");
				return;
			}
		}

		String cdg = req.getParameter("txtClave");

		if (cdg == null) {
			cdg = txtPwd;
			if (cdg == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Usuario inválido");
				return;
			}
		}

		UsuarioManager um = null;

		try {
			um = new UsuarioManager();
			// Usuario u = um.selectUsuario(nombre_usuario,cdgldap);
			Usuario u = um.selectUsuario(nombre_usuario);
//			log.debug(nombre_usuario);
//			log.debug(u.getNombreUsuario());
//			log.debug(cdg);
//			log.debug(u.getCDG());
			if (Config.activeDirectoryConfigs.isUsed()) {
			
				ActiveDirectoryConfigs configs = new ActiveDirectoryConfigs(
						Config.activeDirectoryConfigs);

				configs.setUser(nombre_usuario);
				configs.setPassd(cdg);
				DirContext context = configs.getContext();

				if (context != null) {

					log.info("Usuario '" + nombre_usuario + "' Valido en LDAP");

					if (u == null) { // si el usuario no existe lo inserta
						String hash = nombre_usuario.hashCode() + "";

						u = new Usuario(nombre_usuario, "N".charAt(0), nombre_usuario,
								hash.substring(hash.length() - 3), "4".charAt(0),
								Encripta.code32(cdg),
								nombre_usuario, "LDAP", "Auto");
						u.setTipo_usuario(1);
						u.setCambioCdg("1".charAt(0));
						// u.setPyME(true);
						um.creaUsuario(u);
					} else { // si existe lo actualiza
						u.setCdg(Encripta.code32(cdg));
						u.setTipo_usuario(1);
						u.setCambioCdg("1".charAt(0));
						// u.setPyME(true);
						um.updateUsuario(u);
					}

					List<String> grupos = ActiveDirectoryManager.getGroups(
							context, configs.getUser());

					Session sess = HibernateUtils.getSession();

					if (sess != null) {
						Transaction tran = null;
						try {
							tran = sess.beginTransaction();

							ArrayList<imx_grupo_usuario> gu = imx_grupo_usuario_manager
									.getByUser(sess, u.getNombreUsuario());

							for (String g : grupos) {

								boolean found = false;
								for (int i = 0; i < gu.size(); i++) {
									if (gu.get(i).getId().getNombreGrupo()
											.equals(g)) {
										// Si ya esta la relacion no hace nada
										// solo lo kita del arreglo
										found = true;
										gu.remove(i);
										i--;
										break;
									}
								}
								if (found == false) {
									// Si no encuentra la relacion la crea en BD

									imx_grupo grup = (imx_grupo) sess.get(
											imx_grupo.class, g);
									if (grup != null) {// Si el grupo existe en
														// fortimax
										sess.save(new imx_grupo_usuario(
												new imx_grupo_usuario_id(g, u
														.getNombreUsuario())));
									}
								}
							}
							for (imx_grupo_usuario imxGrupoUsuario : gu) {
								// Borro la relacion con los grupos k ia no
								// estan relacionado
								sess.delete(imxGrupoUsuario);
							}

							if (tran != null) {
								tran.commit();
							}
						} catch (HibernateException e) {
							log.error(e, e);
							if (tran != null) {
								tran.rollback();
							}
						} finally {
							if (sess.isOpen()) {
								sess.close();
							}
						}
					}

					try {
						context.close();
					} catch (NamingException e) {
						log.error(e, e);
					}

				} else {
					log.info("Usuario '" + nombre_usuario + "' INVALIDO");
					String[] msgPwd = { "El usuario no existe.",
							"Inténtelo nuevamente." };
					session.setAttribute("msg", msgPwd);
					resp.sendRedirect("jsp/Messages.jsp");
					return;
				}
			}

			if (u == null) {
				String[] msgPwd = { "El usuario no existe.",
						"Inténtelo nuevamente." };
				session.setAttribute("msg", msgPwd);
				resp.sendRedirect("jsp/Messages.jsp");
				return;
			}

			// TODO no se bien como afecta esta variable, pero sin ella no
			// funciona bien el agrupamiento de usuarios.
			u.setPyME(true);

			// switch (um.validaUsuarioLDAP(u, cdgldap)) {
			switch (um.validaUsuario(u, cdg, false)) {
			case 0: // OK
				ITree tree = null;

				session.setAttribute(USER_KEY, u);
				u.setBanderaConexion("W".charAt(0));
				um.updateUsuario(u);

				ArbolManager amd = new ArbolManager("USR_GRALES",
						u.getIdGabinete());
				String select = "";
				int nHijos = 0;
				log.info("[Login correcto: " + u.getNombreUsuario() + " ]");
				if (u.isPyME()) {
					tree = amd.generaGaveta(u.getNombreUsuario(),
							u.getDescripcion());
					if (isTreeNull(resp, session, tree, "Gavetas"))
						return;

					tree.select(tree.getRoot().getId());
					tree.expand(tree.getRoot().getId());
					select = tree.getRoot().getId();
					session.setAttribute(TREE_APP_KEY, tree);
					ITreeNode raiz = tree.getRoot();
					List<ITreeNode> arr = raiz.getChildren();
					nHijos = arr.size();
					log.trace("Hijos:" + nHijos);

					tree = null;
				}

				tree = amd.generaExpediente(u.getNombreUsuario());
				if (isTreeNull(resp, session, tree, "Mis documentos"))
					return;
				if (tree.getRoot() != null) {
					tree.select(tree.getRoot().getId());
					tree.expand(tree.getRoot().getId());
				}

				//conMisDocumentos = u.getTipo_usuario() == 1;

				session.setAttribute("conMisDocumentos", "" + conMisDocumentos);
				session.setAttribute(TREE_MDC_KEY, tree);
				if (!conMisDocumentos) {
					session.setAttribute(TREE_TYPE_KEY, "g");
				}
				if (conMisDocumentos || (!conMisDocumentos && nHijos > 0))
					resp.sendRedirect("jsp/Principal.jsp?select="
							+ (conMisDocumentos ? tree.getRoot().getId()
									: select + "&" + TREE_TYPE_KEY + "=g"));
				else {
					String[] msg = {
							"Usted no cuenta con gavetas asignadas ni con carpeta de documentos personales.",
							"Contacte al Administrador para que se asigne una gaveta." };
					session.setAttribute("msg", msg);
					resp.sendRedirect("jsp/Messages.jsp");
				}
				break;
			case 1: // Password invalido
				String[] msgPwd = {
						"La combinación de usuario y contrase&ntilde;a no existe.",
						"Inténtelo nuevamente." };
				session.setAttribute("msg", msgPwd);
				resp.sendRedirect("jsp/Messages.jsp");
				log.info("[Login Incorrecto: " + u.getNombreUsuario() + " ]");
				break;
			case 2: // Vigencia terminada
				String[] msgVgn = { "Su periodo de vigencia a caducado" };
				session.setAttribute("msg", msgVgn);
				resp.sendRedirect("jsp/Messages.jsp");
				break;
			case 3: // El usuario no tiene permisos para esta opcion (WEB)
				String[] msgWeb = { "El usuario no esta habilitado para consulta o digitalización web" };
				session.setAttribute("msg", msgWeb);
				resp.sendRedirect("jsp/Messages.jsp?msg=");
				break;
			case 4: // El usuario aun no cambia a 32 posiciones su contrase�a
				session.setAttribute("txtUser", nombre_usuario);
				session.setAttribute("txtPwd", cdg);
				session.setAttribute(USER_KEY, u);
				resp.sendRedirect("jsp/cambiarpassword.jsp");
				break;
			case 5: // password incorrecto o bloqueado
				String[] msgLDAP = { "El password es incorrecto o la cuenta esta bloqueada por intentos fallidos, verifique su password o consulte al administrador." };
				session.setAttribute("msg", msgLDAP);
				resp.sendRedirect("jsp/Messages.jsp");
				break;
			case 6: // si no existe o no esta activo en LDAP
				String[] otro = { "El password ha expirado o la cuenta se encuentra inactiva." };
				session.setAttribute("msg", otro);
				resp.sendRedirect("jsp/Messages.jsp");
				break;
			case 7: // si no esta activo
				String[] activo = { "El password ha expirado o la cuenta se encuentra inactiva." };
				session.setAttribute("msg", activo);
				resp.sendRedirect("jsp/Messages.jsp");
				break;

			}
			
		} catch (Exception e) {
			log.error(e, e);
			throw new ServletException(e.getMessage());
		} /*
		 * catch (Exception e) { log.error(e, e); throw new
		 * ServletException(e.getMessage()); }
		 */

	}


}
