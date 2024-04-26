package com.syc.fortimax.websevices.linkService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimaxinterfaz.client.FortimaxViewer;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.user.UsuarioManager;

public class LinkService {

	private static final Logger log = Logger.getLogger(LinkService.class);

	public String[] generateLink(String user, String pass, Boolean encrypted,
			String gaveta, String expediente, String documento,
			Long segundosVigencia, Boolean LecturaEscritura, String nodo) {

		log.trace("Call a generateLink con los parametros:\n[user:" + user
				+ "]\n[pass:" + StringUtils.stripToNull(pass)
				+ "]\n[encrypted:" + encrypted + "]\n[gaveta:" + gaveta
				+ "]\n[expediente:" + expediente + "]\n[documento:"
				+ (documento == null ? "NULL" : documento)
				+ "]\n[segundosVigencia:" + segundosVigencia
				+ "]\n[LecturaEscritura:" + LecturaEscritura + "]");

		String url = null;
		String errorCode = null;
		String errorMessage = null;

		try {

			user = StringUtils.stripToNull(user);
			if (user == null) {
				throw new FortimaxException("FMX-LNK-WS-1001",
						"Par\u00E1metro Usuario inv\u00E1lido");
			}
			pass = StringUtils.stripToNull(pass);
			if (pass == null) {
				throw new FortimaxException("FMX-LNK-WS-1002",
						"Parametro Password vacio");
			}
			if (encrypted == null) {
				throw new FortimaxException("FMX-LNK-WS-1003",
						"Parametro Encrypted vacio");
			}
			if (!new UsuarioManager().isPasswordValid(user, pass, encrypted)) {
				throw new FortimaxException("FMX-LGIN-WS-103",
						"Usuario/Password invalido");
			}
			nodo = StringUtils.stripToNull(nodo);
			gaveta = StringUtils.stripToNull(gaveta);
			if (gaveta == null) {
				throw new FortimaxException("FMX-LNK-WS-1004",
						"Parametro Gaveta vacio");
			}
			expediente = StringUtils.stripToNull(expediente);
			if (expediente == null && nodo == null) {
				throw new FortimaxException("FMX-LNK-WS-1005",
						"Parametro Expediente vacio");
			}

			if (segundosVigencia == null) {
				throw new FortimaxException("FMX-LNK-WS-1007",
						"Parametro Segundos de Vigencia vacio");
			}

			documento = StringUtils.stripToNull(documento);
			
			FortimaxViewer fvv = new FortimaxViewer(user, pass, encrypted, gaveta, LecturaEscritura.booleanValue());
			if (nodo != null) {
				url = fvv.makeView(new GetDatosNodo(nodo), segundosVigencia);
			} else if (documento == null) {
				url = fvv.makeView(expediente, segundosVigencia);
			} else {
				url = fvv.makeView(expediente, documento, segundosVigencia);
			}

		} catch (FortimaxException wse) {

			log.error(wse, wse);
			errorCode = wse.getCode();
			errorMessage = wse.getMessage();
		} catch (Exception e) {

			log.error(e, e);
			errorCode = FortimaxException.codeUnknowed;
			errorMessage = e.getMessage();
		}
		log.debug("Se genero " + url);
		String[] resp = new String[3];

		resp[0] = (url == null ? new String("") : url);
		resp[1] = (errorCode == null ? new String("") : errorCode);
		resp[2] = (errorMessage == null ? new String("") : errorMessage);

		return resp;
	}
}
