import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.entities.imx_pagina;
import com.syc.fortimax.hibernate.managers.imx_pagina_manager;
import com.syc.image.ImageManager;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.utils.ParametersInterface;
import com.syc.utils.ServletUtils;

 public class GetImageServlet extends HttpServlet implements ParametersInterface {

	private static final Logger log = Logger.getLogger(GetImageServlet.class); 

	private static final long serialVersionUID = 3769989237252918945L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		if (session == null) {
			String path = req.getContextPath();
			String basePath = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+path+"/";
			resp.sendRedirect(basePath+"imagenes/acceso_no_autorizado.jpg");
			return;
		}

		String select = req.getParameter("select");
		if (select == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se recibio nodo");
			return;
		}

		imx_pagina imx_pagina = null;
		try {
			GetDatosNodo gdn = new GetDatosNodo(select);
			gdn.separaDatosPagina();
			imx_pagina_manager imx_pagina_manager = new imx_pagina_manager();
			imx_pagina_manager.select(gdn.getGaveta(),gdn.getGabinete(),gdn.getIdCarpeta(),gdn.getIdDocumento(),gdn.getPagina());
			imx_pagina = imx_pagina_manager.uniqueResult();
		} catch (Exception e) {
			log.error(e,e);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se pudo cargar la pagina");
		}

		boolean isThumbnail = false;
		String thumbnailVal = req.getParameter(THUMBNAIL_KEY);
		if (thumbnailVal != null) {
			isThumbnail = "true".equals(thumbnailVal);
		}
		
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Expires", "-1");

		try {
			if(!"I".equals(imx_pagina.getTipoPagina())) 
				returnEmptyImage(resp);
			else	
				returnNuevoVisualizador(resp, imx_pagina, isThumbnail);
		} catch (Exception e) {
			if(!"ClientAbortException".equals(e.getClass().getSimpleName())) {
				log.error(e,e);
				returnEmptyImage(resp);
			}
		} finally {
			IOUtils.closeQuietly(resp.getOutputStream());
		}
	}
	
	private void returnEmptyImage(HttpServletResponse resp) throws IOException {
		resp.setContentType("image/gif");	
		byte[] bytes = Base64.decodeBase64("R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==".getBytes());
		resp.setContentLength(bytes.length);
		resp.getOutputStream().write(bytes);
	}

	private boolean isCompatible(String contentType) {
		if(contentType==null) return false;
		if(contentType=="image/tiff") return false;
		if(contentType=="application/octet-stream") return false;
//		if(contentType.equals("image/png")) return true;
//		if(contentType.equals("image/gif")) return true;
//		if(contentType.equals("image/jpeg")) return true;
//		if(contentType.equals("image/bmp")) return true;
		return true;
	}
	
	private void returnNuevoVisualizador(HttpServletResponse response, imx_pagina imx_pagina, boolean isThumbnail) throws IOException, ImageReadException, ImageWriteException {
		File sourceFile = new File(imx_pagina.getAbsolutePath());
		
		String format = "JPEG";
		String mimetype = "application/octet-stream";
		boolean isCompatible = false;
		try{
			ImageInfo imageInfo = Imaging.getImageInfo(sourceFile);
			mimetype = imageInfo.getMimeType();
			log.trace("El contentType de "+imx_pagina.getNomArchivoOrg()+" es "+mimetype);
			isCompatible = isCompatible(mimetype);
			if(imageInfo.getBitsPerPixel()==1)
				format = "PNG";
		} catch (Exception e) {
			log.debug("No se pudo obtener la metainformación de "+sourceFile.getAbsolutePath(),e);
		}	
		
		if(isThumbnail||!isCompatible) {
			String targetPath = null;
			if(isThumbnail)
				targetPath = imx_pagina.getThumbnailPath();
			else
				targetPath = imx_pagina.getPreviewPath();
			File targetFile = new File(targetPath);
			if(targetFile.length()==0 || targetFile.lastModified()<sourceFile.lastModified()) {
				if (!targetFile.exists()) {
					targetFile.getParentFile().mkdirs();
					targetFile.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(targetFile, false);
				try {
						BufferedImage bufferedImage = ImageManager.getBufferedImage(sourceFile);
						if(isThumbnail) {
							bufferedImage = ImageManager.getThumbnail(bufferedImage, 106, 148);
						ImageManager.write(bufferedImage, format, fos);
						//ImageManager.write(bufferedImage, format, fos,0.7f);
					} else {
						if(bufferedImage.getHeight()>2000)
							bufferedImage = ImageManager.resize(bufferedImage, null, 2000);
						ImageManager.write(bufferedImage, format, fos);
						//ImageManager.write(bufferedImage, format, fos,0.7f);
					}
				} catch (Exception e) {
					log.trace(e,e);
					FileUtils.deleteQuietly(targetFile);
				} finally {
					IOUtils.closeQuietly(fos);
				}
			}
			if(targetFile.length()==0)
				returnEmptyImage(response);
			else
				ServletUtils.write(targetFile, response, "image/png");
		} else
			ServletUtils.write(sourceFile, response, mimetype);
	}
}
