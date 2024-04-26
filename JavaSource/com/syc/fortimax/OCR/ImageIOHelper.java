package com.syc.fortimax.OCR;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.log4j.Logger;

import com.syc.fortimax.config.Config;
import com.syc.image.ImageManager;

public class ImageIOHelper {

	final static String OUTPUT_FILE_NAME = "FMX_OCR_TIF_";
	final static String TIFF_EXT = ".tif";
	final static String TIFF_FORMAT = "tiff";

	private static final Logger log = Logger.getLogger(ImageIOHelper.class);

	public static List<File> createImageFiles(File imageFile, int index)
			throws Exception {
		List<File> tempImageFiles = new ArrayList<File>();				
		
		List<BufferedImage> bufferedImages = ImageManager.getBufferedImages(imageFile);
		final Map<String, Object> params = new HashMap<String, Object>();

		// Read the stream metadata
		// IIOMetadata streamMetadata = reader.getStreamMetadata();

		// Set up the writeParam

		// Get tif writer and set output to file

		// Read the stream metadata

		if (index == -1) {
			
			if(Config.ConverttoGray)
			{	    
					for (BufferedImage bi : bufferedImages) 
					{
						//Convierte las imagenes a Escala de grises y retorna un List de ellas.
					    int w = bi.getWidth();
					    int h = bi.getHeight();
					    //Genera la mascara en escala de grises
					    BufferedImage image = new BufferedImage(w, h,  
					    BufferedImage.TYPE_BYTE_GRAY);  
					    Graphics g = image.getGraphics();
					    g.drawImage(bi, 0, 0, null);
					    //Guarda la imagen
					    File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
					    try {
					    	//El formato puede ser JPG, PNG, TIFF
					    	Imaging.writeImage(bi, tempFile, ImageFormats.TIFF, params);
							tempImageFiles.add(tempFile);
					    }
					    catch ( IOException x ) {
					        log.error(x,x);
					    }
					}
			}
			else
			{
				params.put(ImagingConstants.PARAM_KEY_COMPRESSION, new Integer(
		                TiffConstants.TIFF_COMPRESSION_UNCOMPRESSED));
				for (BufferedImage bi : bufferedImages) {
					// BufferedImage bi = reader.read(i);
					// IIOImage image = new IIOImage(bi, null,
					// reader.getImageMetadata(i));
					File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
					Imaging.writeImage(bi, tempFile, ImageFormats.TIFF, params);
					tempImageFiles.add(tempFile);
				}
			}
			
			
		} else {
			// BufferedImage bi = reader.read(index);
			// IIOImage image = new IIOImage(bi, null,
			// reader.getImageMetadata(index));
			File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
			Imaging.writeImage(bufferedImages.get(0), tempFile, ImageFormats.TIFF, params);
			tempImageFiles.add(tempFile);
		}
		return tempImageFiles;
	}

	public static List<File> createImageFiles(List<IIOImage> imageList,
			int index) throws Exception {
		List<File> tempImageFiles = new ArrayList<File>();

		// Set up the writeParam
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put(ImagingConstants.PARAM_KEY_COMPRESSION, new Integer(
                TiffConstants.TIFF_COMPRESSION_CCITT_GROUP_4));

		if (index == -1) {
			for (IIOImage image : imageList) {
				File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);	
				//image.setRenderedImage(toBilevel(image.getRenderedImage()));
				BufferedImage bi = (BufferedImage)image.getRenderedImage();
				image.setRenderedImage(ColorToGray(bi));
				Imaging.writeImage(bi, tempFile, ImageFormats.TIFF, params);
				tempImageFiles.add(tempFile);
			}
		} else {
			IIOImage image = imageList.get(index);
			File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
			BufferedImage bi = (BufferedImage)image.getRenderedImage();
			image.setRenderedImage(bi);
			Imaging.writeImage(bi, tempFile, ImageFormats.TIFF, params);
			tempImageFiles.add(tempFile);
		}
		return tempImageFiles;
	}
	
    public static BufferedImage ColorToGray(BufferedImage img) {
    	BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = gray.createGraphics();
        g.drawImage(img, 0, 0, null);

        return gray;
      } // public static RenderedImage ColorToGray(RenderedOp i)


	public static List<IIOImage> getIIOImageList(File imageFile)
			throws Exception {
		File workingTiffFile = null;

		ImageReader reader = null;
		ImageInputStream iis = null;

		try {
			// convert PDF to TIFF
			if (imageFile.getName().toLowerCase().endsWith(".pdf")) {
				// workingTiffFile = ImageHelper.convertPdf2Tiff(imageFile);
				imageFile = workingTiffFile;
			}

			List<IIOImage> iioImageList = new ArrayList<IIOImage>();

			String imageFileName = imageFile.getName();
			String imageFormat = imageFileName.substring(imageFileName
					.lastIndexOf('.') + 1);
			if (imageFormat.matches("(pbm|pgm|ppm)")) {
				imageFormat = "pnm";
			} else if (imageFormat.equals("jp2")) {
				imageFormat = "jpeg2000";
			}
			Iterator<ImageReader> readers = ImageIO
					.getImageReadersByFormatName(imageFormat);
			reader = readers.next();

			if (reader == null) {
				throw new RuntimeException(
						"Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
			}

			iis = ImageIO.createImageInputStream(imageFile);
			reader.setInput(iis);

			int imageTotal = reader.getNumImages(true);

			for (int i = 0; i < imageTotal; i++) {
				// IIOImage image = new IIOImage(reader.read(i), null,
				// reader.getImageMetadata(i));
				IIOImage image = reader
						.readAll(i, reader.getDefaultReadParam());
				iioImageList.add(image);
			}

			return iioImageList;
		} finally {
			try {
				if (iis != null) {
					iis.close();
				}
				if (reader != null) {
					reader.dispose();
				}
			} catch (Exception e) {
				log.error(e);
			}
			if (workingTiffFile != null && workingTiffFile.exists()) {
				workingTiffFile.delete();
			}
		}
	}
}
