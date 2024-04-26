package com.syc.fortimax.websevices.mailService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.configuracion.VariablesEntornoManager;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_seguridad;
import com.syc.fortimax.hibernate.managers.imx_seguridad_manager;
import com.syc.fortimax.security.TokenManager;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.imaxfile.Pagina;
import com.syc.servlets.models.CorreoBasicoModel;
import com.syc.servlets.models.CorreoOpcionalModel;
import com.syc.utils.ExportFileFormats;
import com.syc.utils.Json;
import com.syc.zip.MakeZip;

public class MailService {
	
	private static final Logger log = Logger.getLogger(MailService.class);
	
	public String[] Send(String token, String perfil, String[] variables, String[] nodos, String[] to, String[] cc, String[] bcc) {
		boolean success = false;
		String idEnvio = null;
		String errorCode = "";
		String errorMessage = "";
		
		Map<String, String> mapaVariables = new HashMap<String,String>();
		for(String variable : variables) {
			int index = variable.indexOf("="); //Separador de variables
			if(index!=-1) {
				String nombre = variable.substring(0, index);
				String valor = variable.substring(index+1);
				mapaVariables.put(nombre, valor);
			}
		}
		
		try {
			idEnvio = sendMail(token,perfil,mapaVariables,nodos,to,cc,bcc);
			success = true;
		} catch (Exception e) {
			log.error(e,e);
			if (e instanceof FortimaxException) {
				errorCode = ((FortimaxException)e).getCode();
			} else {
				errorCode = e.getClass().getName();
			}
			errorMessage = e.getMessage(); 
		}
		return new String[] { String.valueOf(success), idEnvio, errorCode, errorMessage };
	}
	
	Map<String,String> variables = new HashMap<String,String>();
	
	private String sendMail(String token, String perfil, Map<String,String> variables, String[] nodos, String[] to, String[] cc, String[] bcc) throws EmailException, DocumentoManagerException, IOException, EntityManagerException, CarpetaManagerException, FortimaxException {
		String usuario = TokenManager.consumeToken(token);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Se esta preparando un envío de correo: ");
		sb.append("{");
		sb.append("usuario="+usuario);
		sb.append(", perfil="+perfil);
		sb.append(", variables="+variables.toString());
		sb.append(", nodos="+Arrays.toString(nodos));
		sb.append(", to="+Arrays.toString(to));
		sb.append(", cc="+Arrays.toString(cc));
		sb.append(", bcc="+Arrays.toString(bcc));
		sb.append("}");
		log.info(sb.toString());
		
		String contenido = VariablesEntornoManager.getValue("fortimax.correo.conexion");
		CorreoBasicoModel config = Json.getObject(contenido, CorreoBasicoModel.class);
		
		String variablePerfil = VariablesEntornoManager.getValue("fortimax.correo.perfil."+perfil);
		
		if(variablePerfil==null)
			throw new FortimaxException(FortimaxException.codeUnknowed,"No existe el perfil "+perfil);	
		
		CorreoOpcionalModel configPerfil = null;
		try {
			configPerfil = Json.getObject(variablePerfil, CorreoOpcionalModel.class);
		} catch (Exception e) {
			throw new FortimaxException(FortimaxException.codeUnknowed,"Hubo un error al tratar de cargar el perfil "+perfil);
		}
		
		this.variables = variables;
		
		// Crea el email
		HtmlEmail email = new HtmlEmail();
		//Establece las configuraciones del servidor
		email.setCharset(EmailConstants.UTF_8);
		email.setHostName(config.getServer());
		email.setSmtpPort(config.getPort());
		if(null!=config.getUser())
			if(!"".equals(config.getUser()))
				email.setAuthenticator(new DefaultAuthenticator(config.getUser(), config.getPassword()));
		email.setSSLOnConnect(config.isSsl());
		
		//Carga los destinatarios ingresados al webservice
		for(String destinatario : to)
			if(!"".equals(destinatario))
				email.addTo(destinatario);
		
		for(String destinatario : cc)
			if(!"".equals(destinatario))
				email.addCc(destinatario);
		
		for(String destinatario : bcc)
			if(!"".equals(destinatario))
				email.addBcc(destinatario);
		
		//Carga los destinatarios por defecto del perfil
		try {
			if(!"".equals(configPerfil.getTo()))
				email.addTo(configPerfil.getTo());
			if(!"".equals(configPerfil.getCc()))
				email.addCc(configPerfil.getCc());
			if(!"".equals(configPerfil.getCco()))
				email.addBcc(configPerfil.getCco());
		} catch (Exception e){
			log.error("Hay un problema con el perfil de correo "+perfil+" verifique los destinatarios");
			log.error(e,e);
		}
		
		//Carga el resto de los parametros del perfil
		email.setFrom(configPerfil.getFrom(), configPerfil.getTitle());
		
		String subject = procesaVariables(configPerfil.getSubject());
		email.setSubject(subject);
		
		// Carga el mensaje Html
		String mensaje = procesaVariables(configPerfil.getMessage());	
		email.setMsg(mensaje);
		email.setTextMsg(removeHtmlTags(mensaje));
		
		//TO-DO: Casi estoy seguro que no funciona como fue pensado
		if(configPerfil.isReceived()) {
			email.addHeader("Notice-Requested-Upon-Delivery-To", configPerfil.getFrom()); //Me quedan dudas con este
			email.addHeader("Return-Receipt-To", configPerfil.getFrom());
		}
		
		if(configPerfil.isReaded()) {
			email.addHeader("Disposition-Notification-To", configPerfil.getFrom());
		}
		
		for(String nodo : nodos) {
			if(!"".equals(nodo)) {
				GetDatosNodo gdn = new GetDatosNodo(nodo);
				gdn.separaDatosGabinete();
				ArrayList<imx_seguridad> seguridad = imx_seguridad_manager.get(usuario, gdn.getGaveta());
				if(seguridad.isEmpty()) {
					throw new FortimaxException("FMX-DOC-WS-UNDEFINED","El usuario no tiene privilegios sobre la gaveta "+gdn.getGaveta());
				} else if(gdn.isDocumento()) {
					Documento documento = new DocumentoManager().selectDocumento(nodo);
					attach(email, documento);
				} else {
					attachZIP(email, usuario, nodo);
				}
			}
		 }
		  // send the email
		  log.info("El correo se preparo correctamente, se procede a su envío");
		  String idEnvio = email.send();
		  log.info("Envío exitoso con el ID: "+idEnvio);
		  return idEnvio;
	}
	
	private String removeHtmlTags(String html) {
		String text = html.replaceAll("<br\\s*/?>", "\n"); //Cambia todos los <br> por saltos de línea
		text = text.replaceAll("<img\\s+[^>]*\\s+alt\\s*=\\s*['\"]?([^'\">]*)['\"]?\\s+[^>]*>", "$1"); //Sustituye todas las imagenes por su texto alternativo
		text = text.replaceAll("<p\\s*[^>]*\\s*>([\\s\\S]*?)<\\/p\\s*>", "\n$1\n"); //Cambia los parrafos.
		text = text.replaceAll("<li\\s*[^>]*\\s*>([\\s\\S]*?)<\\/li\\s*>", " * $1\n"); //Cambia todos los <li> por *
		text = text.replaceAll("<[^>]*>",""); //Elimina todos los tags html. 
		return text;
	}

	private String procesaVariables(String text) {
		for(Entry<String, String> entry : this.variables.entrySet()){
			text = text.replaceAll("@"+entry.getKey()+"@", entry.getValue()); //Cambiar el regex para que no de match en html tags
		}
		return text;
	}

	private void attachZIP(MultiPartEmail email, String usuario, String nodo) throws EmailException, IOException, CarpetaManagerException, DocumentoManagerException {
		byte[] bytes = MakeZip.getZip(usuario, nodo, true);
		// Crear el adjunto
		ByteArrayDataSource baos = new ByteArrayDataSource(bytes, "application/zip");
		String name = nodo+".zip";
		String description = nodo;
		String disposition = EmailAttachment.ATTACHMENT;
		email.attach(baos, name, description, disposition);
	}

	private void attach(MultiPartEmail email, Documento documento) throws EmailException, IOException {
		if(documento.getTipoDocumento()==Documento.IMAX_FILE||documento.getPaginasDocumento().length==0) {
			log.debug(documento.getNodo()+" es IMAX_FILE, iniciando la rutina de generación del PDF");
			ExportFileFormats exportFileFormats = new ExportFileFormats();
			exportFileFormats.setFormat(ExportFileFormats.PDF);
			// Crear el adjunto
			ByteArrayDataSource baos = new ByteArrayDataSource(ExportFileFormats.getPDF(documento), "application/pdf");
			String name = documento.getNombreDocumento()+".pdf";
			String description = documento.getDescripcion();
			String disposition = EmailAttachment.ATTACHMENT;
			email.attach(baos, name, description, disposition);
		} else {
			log.debug(documento.getNodo()+" es EXTERNO, obtiene la primera página");
			Pagina pagina = documento.getPaginasDocumento()[0];
			// Crear el adjunto
			String path = pagina.getAbsolutePath();
			String name = documento.getNombreDocumento()+pagina.getPageExtension();
			String description = documento.getDescripcion();
			String disposition = EmailAttachment.ATTACHMENT;
			email.attach(new PathNameDataSource(path,name), name, description, disposition);
		}	 
	}

	private class PathNameDataSource extends FileDataSource {

		private String name = null;
		private FileTypeMap typeMap = null;

		public PathNameDataSource(String path, String name) {
			super(path);
			this.name = name;
		}
		
		public String getContentType() {
			if (this.typeMap == null) {
				return FileTypeMap.getDefaultFileTypeMap().getContentType(this.name);
			}
			return this.typeMap.getContentType(this.name);
		}
		
		 public void setFileTypeMap(FileTypeMap paramFileTypeMap) {
			 this.typeMap = paramFileTypeMap;
		 }		
	}
}
