package com.syc.image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.log4j.Logger;

public class ImageManager {

@SuppressWarnings("unused")
private static final Logger log = Logger.getLogger(ImageManager.class);
	
	public synchronized static BufferedImage getThumbnail(BufferedImage bufferedImage, int targetWidth, int targetHeight) throws IOException {
		int type = bufferedImage.getType();
		if(type==BufferedImage.TYPE_BYTE_GRAY||type==BufferedImage.TYPE_USHORT_GRAY||type==BufferedImage.TYPE_BYTE_BINARY)
			type = BufferedImage.TYPE_BYTE_BINARY;
		else
			type = BufferedImage.TYPE_BYTE_INDEXED;
		float sourceAspectRatio = bufferedImage.getWidth()*1.0f/bufferedImage.getHeight();
		float targetAspectRatio = targetWidth*1.0f/targetHeight;	
		BufferedImage thumbnail = new BufferedImage(targetWidth, targetHeight, type);     
		Graphics2D g = thumbnail.createGraphics();	
		if(sourceAspectRatio>targetAspectRatio) {
			float scaledWidth = targetHeight*sourceAspectRatio;
			g.drawImage(bufferedImage, Math.round((targetWidth-scaledWidth)/2), 0, Math.round(scaledWidth), targetHeight, null);
		} else {
			float scaledHeight = targetWidth/sourceAspectRatio;
			g.drawImage(bufferedImage, 0, Math.round((targetHeight-scaledHeight)/2), targetWidth, Math.round(scaledHeight), null);
		}		
		g.dispose();
		return thumbnail;
	}
	
	public synchronized static BufferedImage resize(BufferedImage bufferedImage, Integer targetWidth, Integer targetHeight) throws IOException {
		Integer width=targetWidth;
		Integer height=targetHeight;
		if(width==null&&height==null)
			return bufferedImage;
		float sourceAspectRatio = bufferedImage.getWidth()*1.0f/bufferedImage.getHeight();
		if(width==null)
			width = Math.round(sourceAspectRatio*height);
		if(height==null)
			height = Math.round(1/sourceAspectRatio*width);
		int type = bufferedImage.getType();
		BufferedImage newBufferedImage = new BufferedImage(width, height, type);     
		Graphics2D g = newBufferedImage.createGraphics();
		g.drawImage(bufferedImage,0,0,width,height, null);
		g.dispose();
		return newBufferedImage;
	}
	
	public synchronized static BufferedImage changeType(BufferedImage bufferedImage, int bufferedImageType) throws IOException {
		BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImageType);     
		Graphics2D g = newBufferedImage.createGraphics();
		g.drawImage(bufferedImage,0,0,bufferedImage.getWidth(),bufferedImage.getHeight(), null);
		g.dispose();
		return newBufferedImage;
	}

	public static synchronized BufferedImage getBufferedImage(File file) throws ImageReadException, IOException{
		try {
			return ImageIO.read(file);	//Obligatorio para JPEG y los demás formatos que sean cargables con un plugin de ImageIO, algunos TIFF no funcionan con este metodo
		} catch (IIOException e) {
			return Imaging.getBufferedImage(file);	//Para los formatos no compatibles con el método anterior.
		}
	}
	
	public static synchronized List<BufferedImage> getBufferedImages(File file) throws ImageReadException, IOException{
		ImageFormat imageFormat = Imaging.guessFormat(file);
		if(imageFormat==ImageFormats.JPEG) {
			ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
			list.add(ImageIO.read(file));
			return list;
		} else
			return Imaging.getAllBufferedImages(file); //TODO: Hay que hacer un intento con ImageIO.read en caso de error porque no todos los formatos funcionan con este esquema.
	}

	public static synchronized boolean write(RenderedImage renderedImage, String format, OutputStream outputStream) throws IOException {
		return ImageIO.write(renderedImage,format,outputStream);
	}
	
	//Este método hace exactamente lo que el anterior pero permite el uso de ImageWriteParam para especificar parametros adicionales como la compresión.
	public static synchronized boolean write(RenderedImage renderedImage, String format, OutputStream outputStream, Float compression) throws IOException {
		ImageOutputStream imageOutputStream =  null;
	    try {
	    	imageOutputStream = ImageIO.createImageOutputStream(outputStream);
	    } catch (IOException e) {
	    	throw new IIOException("Can't create output stream!", e);
	    }
	    try {
			return write(renderedImage, format, imageOutputStream, compression);
	    } finally {
	    	imageOutputStream.close(); 
	    }
	}
	
	private static boolean write(RenderedImage renderedImage, String format, ImageOutputStream imageOutputStream, Float compression) throws IOException {
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
		if(!writers.hasNext())
			return false;
		ImageWriter imageWriter = writers.next();
		ImageWriteParam imageWriteParam = getImageWriteParam(imageWriter, format, compression);
		imageWriter.setOutput(imageOutputStream);
        try {
        	imageWriter.write(null,new IIOImage(renderedImage,null,null),imageWriteParam); 
        } finally {
        	imageWriter.dispose();
        	imageOutputStream.flush();
        }
        return true;
	}
	
	private static ImageWriteParam getImageWriteParam(ImageWriter imageWriter, String format, Float compression) {
		ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
		if("JPG".equals(format)||"JPEG".equals(format)) {
			imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			if(compression!=null)
				imageWriteParam.setCompressionQuality(compression);
		}
		return imageWriteParam;
	}
}