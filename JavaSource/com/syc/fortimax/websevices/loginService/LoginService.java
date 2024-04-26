package com.syc.fortimax.websevices.loginService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.config.Config;
import com.syc.fortimax.security.TokenManager;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;

public class LoginService {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(LoginService.class);

	/**
	 * Verifica el acceso del usuario a Fortimax. Si se ha enviado el
	 * usuario/password correcto entonces se genera un token con vigencia
	 * temporal para realizar una tarea.
	 *
	 * @param user
	 *            Nombre de usuario registrado en Fortimax.
	 * @param password
	 *            Password del usuario, puede estar encriptado.
	 * @param passwordCifrado
	 *            <code>true</code> si el password esta cifrado.,
	 * @param caducidad
	 *            caducidad en milisegundos del token
	 * @return Arreglo con 3 elementos:
	 *         <table summary="" align="center" border="1">
	 *         <tr>
	 *         <td><b>Indice</b></td>
	 *         <td><b>Descripcion</b></td>
	 *         </tr>
	 *         <tr>
	 *         <td>1</td>
	 *         <td>Token generado, solo en caso de ser correcto el nombre de
	 *         usuario y password. Si ocurre algun error este indice sera vacio</td>
	 *         </tr>
	 *         <tr>
	 *         <td>2</td>
	 *         <td>Codigo de Error. No es nulo si y solo si ocurrio un error en
	 *         la generacion del token.</td>
	 *         </tr>
	 *         <tr>
	 *         <td>3</td>
	 *         <td>Descripcion del Error. No es nulo si y solo si ocurrio un
	 *         error en la generacion del token.</td>
	 *         </tr>
	 *         </table>
	 */
	
	public static String[] login(String user, String password, boolean passwordCifrado, long caducidad) {
		Usuario u = null;
		UsuarioManager um = null;
		String result[];
		String token = null;
		String errorCode = null;
		String errorMessage = null;
		try {
			/*
			 * Validacion de datos (Parametros de entrada)
			 */
			
			if (StringUtils.isBlank(user))
				throw new FortimaxException("FMX-LGIN-WS-101",
						"No se recibio el parametro requerido: Nombre de Usuario");

			if (StringUtils.isBlank(password))
				throw new FortimaxException("FMX-LGIN-WS-102",
						"No se recibio el parametro requerido: Password de Usuario");

			u = (um = new UsuarioManager()).selectUsuario(user);

			if (u == null)
				throw new FortimaxException("FMX-LGIN-WS-103",
						"Usuario/Password invalido");

			if (!um.isPasswordValid(u.getNombreUsuario(), password, passwordCifrado)) {
				throw new FortimaxException("FMX-LGIN-WS-103",
						"Usuario/Password invalido");
			}

			/*
			 * Genera token. El token se guardara hasta su consumo o hasta que
			 * expire por tiempo.
			 */
			if(caducidad<0) {
				token = TokenManager.generateWSAccessToken(u.getNombreUsuario(), Config.DURACION_TOKEN);
			} else {
				token = TokenManager.generateWSAccessToken(u.getNombreUsuario(), caducidad);
			}

		} catch (FortimaxException fe) {
			errorCode = fe.getCode();
			errorMessage = fe.getMessage();
		} catch (Exception e) {
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.toString();
		}

		result = new String[] { (token == null ? new String("") : token),
				(errorCode == null ? new String("") : errorCode),
				(errorMessage == null ? new String("") : errorMessage) };
		return result;
	}
}
