package com.syc.fortimax.security;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;

public class TokenManager {
	private static Logger log = Logger.getLogger(TokenManager.class);

	/**
	 * 
	 */
	public static final String CREATION_PARAM_NAME = "creacion";
	public static final String EXPIRATION_PARAM_NAME = "duracion";
	public static final String USER_PARAM_NAME = "usuario";

	/**
	 * 
	 * @param user
	 * @return return
	 * @throws FortimaxException
	 */
	
	public static String generateWSAccessToken(String user, long caducidad)
			throws FortimaxException {
		StringBuffer parametrosReales = null;
		String parametrosEncriptados = null;

		try {

			/*
			 * Concatena los parametros. El separador sera el caracter | (pipe)
			 */
			parametrosReales = new StringBuffer();
			parametrosReales.append("usuario=").append(user);
			parametrosReales.append("|");
			parametrosReales.append(TokenManager.CREATION_PARAM_NAME + "=")
					.append(String.valueOf(System.currentTimeMillis()));
			parametrosReales.append("|");
			parametrosReales.append(TokenManager.EXPIRATION_PARAM_NAME + "=")
					.append(String.valueOf(caducidad*1000l));

			parametrosEncriptados = TripleDesEncryption
					.encryptAndMask(parametrosReales.toString());

			return parametrosEncriptados;
		} catch (Exception e) {
			log.error(e, e);
			throw new FortimaxException("FMX-SEC-10001 Error generando Token",
					e.toString());
		}
	}

	public static String consumeToken(String token) throws FortimaxException {
		/* El formato esperado es: encrypt-key:=data-encrypted */
		if (token.indexOf(":=") == -1)
			throw new FortimaxException("FMX-SEC-10002",
					"Cadena de Token mal Formada");

		/*
		 * Se espera que el token contenga informacion, de no ser asi, no puede
		 * continuar
		 */
		String[] b64Array = token.split(":=");
		if (b64Array.length < 2) {
			throw new FortimaxException("FMX-SEC-10003",
					"Cadena de Token mal Formada. Sin datos");
		}

		/* Decodifica la informacion y la llave */
		byte[] bKey = DatatypeConverter.parseBase64Binary(b64Array[0]);
		byte[] bData = DatatypeConverter.parseBase64Binary(b64Array[1]);

		Map<String, String> param = new HashMap<String, String>();
		try {
			/* Convierte el String encrypt-key a SecretKey */
			SecretKey key = TripleDesEncryption.generateKey(bKey);

			/* Des-encripta los datos enviados */
			byte[] clearData = TripleDesEncryption.decrypt(key, bData);

			/* Formato esperado de data-encrypted: name=value[|[name=value...]] */
			String[] valueNamePairs = new String(clearData).split("\\|");
			for (int i = 0; i < valueNamePairs.length; i++) {
				String[] pair = valueNamePairs[i].split("=");
				param.put(pair[0], pair[1]);
			}

			/*
			 * Obtiene las variables de tiempo del token para realizar la
			 * validacion
			 */
			long tokenCreation = Long.parseLong(param
					.get(TokenManager.CREATION_PARAM_NAME));

			long tokenExp = tokenCreation
					+ Long.parseLong(param
							.get(TokenManager.EXPIRATION_PARAM_NAME));

			if (System.currentTimeMillis() > tokenExp)
				throw new FortimaxException("FMX-SEC-10004",
						"El token ha expirado");

			return (String) param.get(TokenManager.USER_PARAM_NAME);
		} catch (FortimaxException e) {
			throw e;
		} catch (Exception exc) {
			log.error(exc, exc);
			throw new FortimaxException(FortimaxException.codeUnknowed,
					exc.toString());
		}

	}

}
