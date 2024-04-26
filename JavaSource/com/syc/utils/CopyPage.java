package com.syc.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.syc.imaxfile.Volumen;
import com.syc.imaxfile.VolumenManager;
import com.syc.user.Usuario;

public class CopyPage {

private static final Logger log = Logger.getLogger(CopyPage.class);
	protected Usuario u;
	protected String filename;
	protected Volumen v;

	public CopyPage(Usuario u) {
		this.u = u;
	}

	public File getFile() {
		return new File(filename);
	}

	public Volumen getVolumen() {
		return v;
	}

	public synchronized void copyPageFile(String fullSourcePath) throws IOException {

		String newFilename = "00001";

		v = VolumenManager.getVolumen();

		/*File lastUserDir =
			new File(v.getUnidad() + v.getRutaBase() + v.getRutaDirectorio() + v.getVolumen() + File.separator);
*/
		//String[] lastUserFiles = lastUserDir.list(new CopyPageFilenameFilter(u.getCodigo()));
		/*if (lastUserFiles != null) {
			if (lastUserFiles.length > 0) {
				String fn = lastUserFiles[(lastUserFiles.length - 1)];
				newFilename = String.valueOf(Integer.parseInt(fn.substring(3, fn.lastIndexOf('.'))) + 1);
			}
		}*/
		
		/*newFilename =
			v.getUnidad()
				+ v.getRutaBase()
				+ v.getRutaDirectorio()
				+ v.getVolumen()
				+ File.separator
				+ (u.getCodigo() + "00000".substring(0, 5 - newFilename.length()) + newFilename)
				+ ".tif";*/
		
		/*realFilename = userCode
		+ "00000".substring(0, 5 - realFilename.length())+ realFilename + session.getId() + new java.util.Date().getTime();
		*/
		newFilename =
			v.getUnidad()
				+ v.getRutaBase()
				+ v.getRutaDirectorio()
				+ v.getVolumen()
				+ File.separator
				//+ (u.getCodigo() + "00000".substring(0, 5 - newFilename.length()) + newFilename)
				+ (u.getCodigo() + "00000".substring(0, 5 - newFilename.length()) + newFilename + new java.util.Date().getTime())
				+ ".tif";		
		
		/*newFilename =
			v.getUnidad()
				+ v.getRutaBase()
				+ v.getRutaDirectorio()
				+ v.getVolumen()
				+ File.separator
				+ (u.getCodigo() + "00000".substring(0, 5 - newFilename.length()) + newFilename)
				+ ".tif";*/

		filename = new File(newFilename).getAbsolutePath();
		
		FileInputStream srcInputStream = null;
		FileChannel srcChannel = null;
		FileOutputStream trgOutputStream = null;
		FileChannel trgChannel = null;

		try {
			srcInputStream = new FileInputStream(fullSourcePath);
			trgOutputStream = new FileOutputStream(newFilename);
			
			srcChannel = srcInputStream.getChannel();
			trgChannel = trgOutputStream.getChannel();

			trgChannel.transferFrom(srcChannel, 0, srcChannel.size());
		} finally {
			if(srcInputStream!=null)
				srcInputStream.close();
			if(srcChannel!=null)
				srcChannel.close();
			if(trgOutputStream!=null)
				trgOutputStream.close();
			if(trgChannel!=null)
				trgChannel.close();
		}
	}
}
