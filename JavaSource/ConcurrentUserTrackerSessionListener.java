import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.ParametersInterface;

public class ConcurrentUserTrackerSessionListener implements
		ParametersInterface, HttpSessionListener, HttpSessionAttributeListener {

	static int users = 0;
	static int sessionCount = 0;
	private static Logger log = Logger
			.getLogger(ConcurrentUserTrackerSessionListener.class);

	public void sessionDestroyed(HttpSessionEvent event) {
		users--;

		HttpSession s = event.getSession();
		try {
			UsuarioManager um = new UsuarioManager();
			Usuario u = (Usuario) s.getAttribute(USER_KEY);
			String id = s.getId();
			if (u != null) {
				u.setBanderaConexion("N".charAt(0));
				um.updateUsuario(u);
				log.debug("Session Destruida,  Usuario : "
						+ u.getNombreUsuario() + " Session ID: " + id);
				synchronized (this) {
					--sessionCount;
				}
				log.debug("Cantidad de sessiones: " + sessionCount);
			}

		} catch (Exception e) {
			log.warn(e.getMessage());
		}

	}

	public void sessionCreated(HttpSessionEvent event) {
		users++;

		HttpSession session = event.getSession();
		Usuario u = (Usuario) session.getAttribute(USER_KEY);
		String id = session.getId();
		if (u != null)
			log.debug("Session Creada,  Usuario : " + u.getNombreUsuario()
					+ " Session ID: " + id);

		synchronized (this) {
			sessionCount++;
		}

		log.debug("Cantidad de sessiones: " + sessionCount);
	}

	public static int getConcurrentUsers() {
		return users;
	}

	public void attributeAdded(HttpSessionBindingEvent se) {

		HttpSession session = se.getSession();
		Usuario u = (Usuario) session.getAttribute(USER_KEY);

		String id = session.getId();
		String name = se.getName();
		String value = "";
		String source = "";
		try {
			source = se.getSource().getClass().getName();
			value = (se.getValue() != null ? se.getValue().toString() : "NULL");

		} catch (Exception e) {
			log.warn(e.getMessage());

		}

		log.trace("Atributo Agregado a la Session ID: " + id + " Usuario: ,"
				+ (u != null ? u.getNombreUsuario() : "") + " [" + name + " : "
				+ value + "], desde: " + source);

	}

	public void attributeRemoved(HttpSessionBindingEvent se) {
		try {
			HttpSession session = se.getSession();
			if (session != null) {
				Usuario u = (Usuario) session.getAttribute(USER_KEY);

				String id = session.getId();
				String name = se.getName();
				if (name == null)
					name = "Desconocido";

				String value = "";
				String source = "";

				source = se.getSource().getClass().getName();
				value = (se.getValue() != null ? se.getValue().toString()
						: "NULL");

				log.trace("Atributo Removido a la Session ID: " + id
						+ " Usuario: ,"
						+ (u != null ? u.getNombreUsuario() : "") + " [" + name
						+ " : " + value + "], desde: " + source);
			} else {
				log.debug("Session Terminada");
			}
		} catch (Exception e) {
			log.warn(e.getMessage());

		}

	}

	public void attributeReplaced(HttpSessionBindingEvent se) {

		HttpSession session = se.getSession();
		Usuario u = (Usuario) session.getAttribute(USER_KEY);

		String id = session.getId();
		String name = se.getName();
		String value = "";
		String source = "";
		try {
			source = se.getSource().getClass().getName();
			value = (se.getValue() != null ? se.getValue().toString() : "NULL");
		} catch (Exception e) {
			log.warn(e.getMessage());

		}
		log.trace("Atributo Remplazado a la Session ID: " + id + " Usuario: ,"
				+ (u != null ? u.getNombreUsuario() : "") + " [" + name + " : "
				+ value + "], desde: " + source);

	}

	public ConcurrentUserTrackerSessionListener() {
		// this.sessionCount = 0;
	}

}