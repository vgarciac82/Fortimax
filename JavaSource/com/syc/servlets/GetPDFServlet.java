package com.syc.servlets;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.Pagina;
import com.syc.user.Usuario;
import com.syc.utils.ExportFileFormats;
import com.syc.utils.ParametersInterface;
import com.syc.utils.ServletUtils;

public class GetPDFServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(GetPDFServlet.class);

	private static final long serialVersionUID = 909304337728599812L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(USER_KEY);
		if (u == null) {
			response.sendRedirect("index.jsp");
			return;
		} //TODO: Verificar si el usuario tiene privilegios sobre el documento

		String nodoDocumento = request.getParameter("select");
		if (nodoDocumento == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Sin nodo seleccionado");
			return;
		}
		
		@SuppressWarnings("unused")
		String formato = request.getParameter("formato");
		@SuppressWarnings("unused")
		String orientacion = request.getParameter("orientacion");
		boolean descarga = "true".equals(request.getParameter("download"));
		
		try {	
			Documento documento = new DocumentoManager().selectDocumento(nodoDocumento);
			documento.setUsuarioModificacion(u.getNombreUsuario());
			
			if(documento.getTipoDocumento()==Documento.IMAX_FILE||documento.getPaginasDocumento().length==0) {
				log.debug(documento.getNodo()+" es IMAX_FILE, iniciando la rutina de generación del PDF");
				ExportFileFormats exportFileFormats = new ExportFileFormats();
				exportFileFormats.setFormat(ExportFileFormats.PDF);
				ServletOutputStream os = response.getOutputStream();
				response.setContentType("application/pdf");
				if(descarga) {
					response.setHeader("Content-Disposition","attachment; filename=\""+documento.getNombreDocumento()+".pdf"+"\"");
				}
				try {
					ExportFileFormats.writePDF(documento, os);
					//byte[] bytes = ExportFileFormats.getPDF(documento);
					//	response.setContentLength(bytes.length);
					//os.write(bytes);
				} finally {
					IOUtils.closeQuietly(os);
				}
			} else {
				log.debug(documento.getNodo()+" es EXTERNO, obtiene la primera página");
				Pagina pagina = documento.getPaginasDocumento()[0];
				File file = new File(pagina.getAbsolutePath());
				if(descarga) {
					ServletUtils.download(file, documento.getNombreDocumento()+pagina.getPageExtension(), response);
				} else {
					ServletUtils.write(file, response);
				}
			}
		} catch (Exception e) {
			log.error(e,e);
		}
	}
}