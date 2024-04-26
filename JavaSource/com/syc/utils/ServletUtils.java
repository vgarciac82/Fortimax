package com.syc.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletOutputStream;

import org.apache.commons.io.IOUtils;

import com.syc.user.Usuario;

public class ServletUtils {

	public static Usuario obtenerUsuario(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		Usuario usuario = (Usuario) session.getAttribute(ParametersInterface.USER_KEY);
		return usuario;
	}
	
	public static void write(File file, HttpServletResponse response) throws IOException {
		write(file, response, null);
	}
	
	public static void write(File file, HttpServletResponse response, String contentType) throws IOException {
		write(file, response, contentType, null);
	}
	
	public static void write(File file, HttpServletResponse response, String contentType, String characterEncoding) throws IOException {
		if(contentType==null)
			contentType = Utils.getContentType(file);
		
		if(contentType==null)
			contentType = "application/octet-stream";
			
		response.setContentType(contentType);
		
		if(characterEncoding!=null)
			response.setCharacterEncoding(characterEncoding);
		
		response.setContentLength((int)file.length());
		
		FileInputStream fis = new FileInputStream(file);
		try {
			BufferedInputStream bis = new BufferedInputStream(fis);
			try {
				ServletOutputStream sos = response.getOutputStream();
				try {
					byte[] bytes = new byte[1024*1024];
					int bytesRead;
					while ((bytesRead = bis.read(bytes)) != -1) {
					    sos.write(bytes, 0, bytesRead);
					}
				} finally {
					IOUtils.closeQuietly(sos);
				}
			} finally {
				IOUtils.closeQuietly(bis);
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}	
	}
	
	public static void download(File file, String fileName, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Disposition","attachment; filename=\""+fileName+"\"");
		write(file, response);
	}
}
