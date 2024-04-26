package com.syc.utils;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.syc.image.ImageManager;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.Pagina;

public class ExportFileFormats {
	private static final Logger log = Logger.getLogger(ExportFileFormats.class);
	public static final String PDF = null;
	
	public static void writePDF(Documento documento, OutputStream outputStream) throws IOException {	
		Pagina[] paginas = documento.getPaginasDocumento();
		if(documento.getTipoDocumento()==Documento.IMAX_FILE||paginas.length==0) {
			int numeroPaginas = 0;
			Document document = new Document();
			try {
				PdfWriter writer = PdfWriter.getInstance(document,outputStream);
				writeDocumentInformation(writer.getInfo(), documento);
				writeDocumentInformation(document, documento);
				document.open();
				for(Pagina pagina : paginas) {
					addPage(document, pagina);
					numeroPaginas++;
				}
				if(numeroPaginas==0) {
					log.debug("No se procesaron páginas añadiendo una página vacía");
					document.newPage();
					numeroPaginas++;
					Chunk chunk = new Chunk("");
                    document.add(chunk);
				}
				log.debug("Generación del PDF Finalizada");
			} catch (Exception e) {
				log.error(e,e);
				if(numeroPaginas==0) {
            		document.newPage();
            		Chunk chunk = new Chunk(e.getMessage());
                    try {
                    	document.add(chunk);
					} catch (DocumentException ex) {
						log.error(ex,ex);
					}
            	}
			} finally {
	            if(document.isOpen())
	            	document.close();
	        }
		} 
	}

	public static byte[] getPDF(Documento documento) throws IOException {
		byte[] salida = new byte[0];	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writePDF(documento, baos);
			salida = baos.toByteArray();
		} finally {
			if (baos!=null)
				baos.close();
		}
		return salida;
	}
	
	private static void writeDocumentInformation(PdfDictionary info, Documento documento) {
		String producer = info.get(PdfName.PRODUCER).toString();
		producer = producer.substring(0,producer.lastIndexOf(" by"));
		info.put(PdfName.PRODUCER,new PdfString("Fortimax ("+producer+")"));
	    
		Calendar calendar = Calendar.getInstance();
	    Date fhCreacion = documento.getFechaCreacion();
	    log.debug("--Fecha Creación: "+fhCreacion);
	    calendar.setTime(fhCreacion);
	    info.put(PdfName.CREATIONDATE, new PdfDate(calendar));
	    
	    Date fhModificacion = documento.getFechaModificacion();
	    log.debug("--Fecha Modificación: "+fhModificacion);
	    calendar.setTime(fhModificacion);
	    info.put(PdfName.MODDATE, new PdfDate(calendar));
	}
	
	private static void writeDocumentInformation(Document document, Documento documento) {
		String title = documento.getNombreDocumento();
		log.debug("--Title: "+title);
		document.addTitle(title);
		
		String subject = documento.getDescripcion();
		log.debug("--Subject: "+subject);
		document.addSubject(subject);
	    
	    String keywords = documento.getNodo();
	    log.debug("--Keywords: "+keywords);
	    document.addKeywords(keywords);
	    
	    String author = documento.getNombreUsuario();
	    log.debug("--Author: "+author);
	    document.addAuthor(author);
	    
	    String creator = documento.getUsuarioModificacion();
	    if(creator==null)
	    	creator = author;
	    log.debug("--Creator: "+creator);
	    document.addCreator(creator);
	}

	private static void addPage(Document document, Pagina pagina) throws DocumentException {
		log.debug("Procesando página "+pagina.getNumeroPagina()+": "+pagina.getNomArchivoOrg()+" - "+pagina.getAbsolutePath());
		Boolean pageCreated = false;
		try {
			Image image = getImage(pagina.getAbsolutePath());
			/*
			if(image.getRawData()!=null) {
				FileUtils.writeByteArrayToFile(new File(pagina.getAbsolutePath()+".raw"), image.getRawData(), false);
			}*/
			document.setPageSize(image);
			pageCreated = document.newPage();
			writeInvisibleText(document,pagina.getPathOCR());
			image.setAbsolutePosition(0f, 0f);
			document.add(image);
		} catch (Exception e) {
			if(!pageCreated);
				document.newPage();
			Chunk chunk = new Chunk(e.getMessage());
            document.add(chunk);
		}
	}
	
	private static Image getImage(String path) throws BadElementException, ImageReadException, IOException {
		try {
			return Image.getInstance(path);
		} catch (Exception e) {
			return Image.getInstance(ImageManager.getBufferedImage(new File(path)),null);
		}
	}
	
	private static void writeInvisibleText(Document document, String path) throws DocumentException, IOException {
		File file = new File(path);
		if(file.exists()) {
			log.debug("--Se anexa el OCR "+path);
			Chunk chunk = new Chunk(Utils.readFile(file,"UTF-8"));
			chunk.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_INVISIBLE, 100f, new Color(0x0c, 0xF0, 0x00));          
			document.add(chunk);
		}
	}
	
	public static List<File> getPages(File file, String path) throws IOException {
		List<File> files = new ArrayList<File>();
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try {
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			PDFFile pdfFile = new PDFFile(buf);
			for (int pagenum = 1; pagenum<pdfFile.getNumPages(); pagenum++) {
				PDFPage pdfPage = pdfFile.getPage(pagenum);
				String nombreTemporal = path+"_"+pagenum+"_"+new Date().getTime();
				File fimage = new File(nombreTemporal + "." + "png");
				ImageIO.write((BufferedImage)getImage(pdfPage), "png", fimage);
				files.add(file);
			}
		} finally {
            raf.close();
        }
   		return files;
	}
	
    private static java.awt.Image getImage(PDFPage page) {
        //get the width and height for the doc at the default zoom
        Rectangle rect = new Rectangle(0,0,(int)page.getBBox().getWidth(),(int)page.getBBox().getHeight());
        //generate the image
        java.awt.Image image = page.getImage(
        		rect.width*1, rect.height*1, //width & height
                rect, // clip rect
                null, // null for the ImageObserver
                true, // fill background with white
                true  // block until drawing is done
        );     
        return image;
    }

	public void setFormat(String format) {
		// TODO Auto-generated method stub
	}
}
