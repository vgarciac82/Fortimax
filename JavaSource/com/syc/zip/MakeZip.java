package com.syc.zip;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tika.io.IOUtils;

import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.GetDatosNodo;
import com.syc.imaxfile.Pagina;
import com.syc.tree.ArbolManager;
import com.syc.utils.ExportFileFormats;
import com.syc.utils.Utils;

public class MakeZip {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(MakeZip.class);
	
	public static byte[] getZip(String usuario, String nodo, boolean convertPDF) throws IOException, CarpetaManagerException, DocumentoManagerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writeZip(usuario, nodo, convertPDF, baos);
		} finally {
			IOUtils.closeQuietly(baos);
		}
		return baos.toByteArray();
	}
	
	public static void writeZip(String usuario, String nodo, boolean convertPDF, OutputStream outputStream) throws IOException, CarpetaManagerException, DocumentoManagerException {
		ZipOutputStream zos = new ZipOutputStream(outputStream);
		try{
			GetDatosNodo gdn = new GetDatosNodo(nodo);
			if(gdn.isDocumento()) {
				Documento documento = new DocumentoManager().selectDocumento(nodo);
				zipEntry(documento, convertPDF, zos);
			} else if (gdn.isCarpeta()) {
				zipEntryCarpeta(usuario, nodo, convertPDF, zos);
			} else {
				zipEntryExpediente(usuario, nodo, convertPDF, zos);
			}
		} finally {
			IOUtils.closeQuietly(zos);
		}
	}
	
	public static void writeZip(Documento documento, String path, boolean convertPDF, OutputStream outputStream) throws IOException, CarpetaManagerException, DocumentoManagerException {
		ZipOutputStream zos = new ZipOutputStream(outputStream);
		try{
			zipEntry(documento, path, convertPDF, zos);
		} finally {
			IOUtils.closeQuietly(zos);
		}
	}
	
	private static void zipEntry(Documento documento, boolean convertPDF, ZipOutputStream zos) throws IOException, CarpetaManagerException {
		zipEntry(documento, null, convertPDF, zos);
	}

	private static void zipEntry(Documento documento, String path, boolean convertPDF, ZipOutputStream zos) throws IOException, CarpetaManagerException {
		if(documento.getTipoDocumento()==Documento.IMAX_FILE) {
			zipEntryIMX(documento, path, convertPDF, zos);
		} else {
			zipEntryEXTERNO(documento, path, zos);
		}
	}
	
	private static void zipEntryIMX(Documento documento, String path, boolean convertPDF, ZipOutputStream zos) throws IOException, CarpetaManagerException {
		if(path == null)
			path = documento.getPath();
		if(path.startsWith("/"))
			path = path.substring(1);
		if(convertPDF) {
			path+=".pdf";
			zipEntry(path, ExportFileFormats.getPDF(documento), zos);
		} else if (documento.getPaginasDocumento().length>0) {
			DecimalFormat df = formatoPagina(documento.getPaginasDocumento().length);
			int numero_pagina = 1;
			for(Pagina pagina : documento.getPaginasDocumento()) {
				String paginaPath = path+".imx/"+documento.getNombreDocumento()+"_"+df.format(numero_pagina)+pagina.getPageExtension();
				try {
					zipEntry(paginaPath,pagina.getAbsolutePath(),zos);
				} catch (Exception e) {
					zipEntry(paginaPath,new byte[0], zos);
				}
				numero_pagina++;
			}
		} else {
				path+=".imx/vacio";
				zipEntry(path,new byte[0], zos);
		}
	}

	private static DecimalFormat formatoPagina(int numeroPaginas) {
		int longitud = (""+numeroPaginas).length();
		String ceros = StringUtils.repeat("0", longitud);
		DecimalFormat df = new DecimalFormat(ceros);
		return df;
	}

	private static void zipEntryEXTERNO(Documento documento, String path, ZipOutputStream zos) throws IOException, CarpetaManagerException {
		if(path == null)
			path = documento.getPath();
		if(documento.getPaginasDocumento().length>0) {
			Pagina pagina = documento.getPaginasDocumento()[0];
			path+=pagina.getPageExtension();
			try {
				zipEntry(path,pagina.getAbsolutePath(),zos);
			} catch (Exception e) {
				zipEntry(path,new byte[0], zos);
			}
		} else {
			zipEntry(path,new byte[0], zos);		
		}
		
	}

	private static void zipEntry(String path, String sourcePath, ZipOutputStream zos) throws IOException  {
		FileInputStream inputStream = new FileInputStream(sourcePath);
		ZipEntry entry = new ZipEntry(path);
		zos.putNextEntry(entry);
		try {
			Utils.write(inputStream, zos);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
        zos.closeEntry();		
	}

	private static void zipEntry(String path, byte[] bytes, ZipOutputStream zos) throws IOException, ZipException {
		ZipEntry entry = new ZipEntry(path);
		entry.setSize(bytes.length);
		try {
			zos.putNextEntry(entry);
			zos.write(bytes);
			zos.closeEntry();
		} catch (ZipException e) {
			if (e.getMessage().startsWith("duplicate entry"))
				zipEntry(path+"_", bytes, zos);
			else
				throw e;
		}
	}

	private static void zipEntryCarpeta(String usuario, String nodo, boolean convertPDF, ZipOutputStream zos) throws IOException, CarpetaManagerException, DocumentoManagerException {
		ITreeNode nodoExpediente = nodoExpediente(usuario, nodo);
		ITreeNode nodoCarpeta = searchNode(nodoExpediente,nodo);
		zipEntry(nodoCarpeta, convertPDF, zos);
	}
	
	private static ITreeNode searchNode(ITreeNode parent, String nodo) {
		if(parent.getId().equals(nodo))
			return parent;
		for(ITreeNode child : (List<ITreeNode>)parent.getChildren()) {
			child = searchNode(child, nodo);
			if(child!=null)
				return child;
		}
		return null;
	}
	
	private static ITreeNode nodoExpediente(String usuario, String nodo) {
		GetDatosNodo gdn = new GetDatosNodo(nodo);
		gdn.separaDatosGabinete();

		ArbolManager am = new ArbolManager(gdn.getGaveta(), gdn.getGabinete());
		return am.generaExpediente(usuario).getRoot();
	}
	
	private static void zipEntryExpediente(String usuario, String nodo, boolean convertPDF, ZipOutputStream zos) throws IOException, CarpetaManagerException, DocumentoManagerException {
		zipEntry(nodoExpediente(usuario, nodo), convertPDF, zos);
	}

	private static void zipEntry(ITreeNode node, boolean convertPDF, ZipOutputStream zos) throws IOException, CarpetaManagerException, DocumentoManagerException {
		if(node.getObject() instanceof Carpeta) {
			List<ITreeNode> childrenNodes = node.getChildren();
			if(childrenNodes.isEmpty()) {
				String path = getPath(node)+"/vacio";
				if(path.startsWith("/"))
					path = path.substring(1);
				zipEntry(path, new byte[0], zos);
			} else {
				for(ITreeNode childNode : childrenNodes)
					zipEntry(childNode, convertPDF, zos);
			}			
		} else if(node.getObject() instanceof Documento) {
			zipEntryDocumento(node, convertPDF, zos); 
		}
	}

	private static void zipEntryDocumento(ITreeNode node, boolean convertPDF, ZipOutputStream zos) throws IOException, CarpetaManagerException, DocumentoManagerException {
		Documento documento = new DocumentoManager().selectDocumento(node.getId());
		

		
		String path = getPath(node);
		if(path.startsWith("/"))
			path = path.substring(1);
		zipEntry(documento, path, convertPDF, zos);
	}
	
	public static String getPath(ITreeNode node) {
		if(node.getParent()==null)
			return "/"+node.getName();
		else
			return node.getParent().getName()+"/"+node.getName();
	}
	

}