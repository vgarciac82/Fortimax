package com.syc.servlets.XMLConfig;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;

import com.syc.servlets.models.ConfigPDFModel;

public class XMLConfig {
	
	public Config getConfig(ConfigPDFModel configPDFModel) {
		Config config = new Config();
		config.version = 2.1;
		config.MaxWidthImage = configPDFModel.getMaxAnchoI();
		config.MaxHeightImage = configPDFModel.getMaxAltoI();
		config.FormatoSalidaImagen = configPDFModel.getSalidaI();
		config.ColorSpaceImage = getEspacioColor(configPDFModel.getEspacioCI());
		config.MaxWidthText = configPDFModel.getMaxAnchoT();
		config.MaxHeightText = configPDFModel.getMaxAltoT();
		config.FormatoSalidaTexto = configPDFModel.getSalidaT();
		config.ColorSpaceText = getEspacioColor(configPDFModel.getEspacioCT());
		config.quality = getQuality(configPDFModel.getCalidad());
		config.SortBy = getSort(configPDFModel.getOrden());
		config.MaxFileSizeKB = configPDFModel.getTamKB();
		config.GrayscaleColorSpace = "";
		config.ExtensionsAllowed = getExtensionsAllowed(configPDFModel);
		config.SeparaPDF = configPDFModel.isSepararPDF();
		config.UseHints = configPDFModel.isHints();
		config.InstallerUrl = configPDFModel.getUrl();
		return config;
	}
	
	private int getEspacioColor(String espacioColor) {
		if("color".equals(espacioColor))
			return 0;
		if("grises".equals(espacioColor))
			return 1;
		if("byn".equals(espacioColor))
			return 2;
		return Integer.parseInt(espacioColor);
	}
	
	private String getQuality(String calidad) {
		return Float.parseFloat(calidad)+"f";
	}

	private String getSort(String orden) {
		if("nombre".equals(orden))
			return "NAME";
		return orden;
	}

	private List<ExtensionAllowed> getExtensionsAllowed(
			ConfigPDFModel configPDFModel) {
		List<ExtensionAllowed> extensionsAllowed = new ArrayList<ExtensionAllowed>();
		if(configPDFModel.isBmp())
			extensionsAllowed.add(new ExtensionAllowed("bmp","Image BMP Format"));
		if(configPDFModel.isGif())
			extensionsAllowed.add(new ExtensionAllowed("gif","Image GIF Format"));
		if(configPDFModel.isJpeg())
			extensionsAllowed.add(new ExtensionAllowed("jpeg","Image JPEG Format"));
		if(configPDFModel.isJpg())
			extensionsAllowed.add(new ExtensionAllowed("jpg","Image JPG Format"));
		if(configPDFModel.isPdf())
			extensionsAllowed.add(new ExtensionAllowed("pdf","Application PDF"));
		if(configPDFModel.isPng())
			extensionsAllowed.add(new ExtensionAllowed("png","Image PNG Format"));
		if(configPDFModel.isTif())
			extensionsAllowed.add(new ExtensionAllowed("tif","Image TIF Format"));
		if(configPDFModel.isTiff())
			extensionsAllowed.add(new ExtensionAllowed("tiff","Image TIFF Format"));
		return extensionsAllowed;
	}

	@XmlRootElement(name="Config")
	public static class Config {
		@XmlAttribute
		double version;
		
		@XmlElement
		int MaxWidthImage;
		
		@XmlElement
		int MaxHeightImage;
		
		@XmlElement
		String FormatoSalidaImagen;
		
		@XmlElement
		int ColorSpaceImage;
		
		@XmlElement
		int MaxWidthText;
		
		@XmlElement
		int MaxHeightText;
		
		@XmlElement
		String FormatoSalidaTexto;
		
		@XmlElement
		int ColorSpaceText;
		
		@XmlElement
		String quality;
		
		@XmlElement
		String SortBy;
		
		@XmlElement
		int MaxFileSizeKB;
		
		@XmlElement
		String GrayscaleColorSpace;
		
		@XmlElement(name="ExtensionAllowed")
	    List<ExtensionAllowed> ExtensionsAllowed;
		
		@XmlElement
		boolean SeparaPDF;
		
		@XmlElement
		boolean UseHints;
		
		@XmlElement
		String InstallerUrl;
	}
	
	@XmlRootElement
	public static class ExtensionAllowed {
		public ExtensionAllowed() {
		}
		
		public ExtensionAllowed(String extension, String description) {
			this.Extension = extension;
			this.Description = description;
		}

		@XmlElement
		String Extension;
		
		@XmlElement
		String Description;
	}
}
