package com.syc.fortimax.OCR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOImage;

import org.apache.log4j.Logger;

import com.syc.fortimax.config.Config;
import com.syc.fortimax.hibernate.entities.imx_pagina_index;
import com.syc.fortimax.hibernate.managers.imx_pagina_index_manager;
import com.syc.fortimax.lucene.IndexFiles;
import com.syc.imaxfile.Documento;


public  class ocrManager implements Runnable {
	private static final Logger log = Logger.getLogger(ocrManager.class);
    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    static final String UTF8 = "UTF-8";
    
    boolean retVal = false;
    String img_input="";
    String txt_output="";
    File ocr_vol=null;
    Boolean BORRAR_IMG_TEMP_OCR = true;
    Documento documento = new Documento();
    boolean idx_ocr_ok =false;
    imx_pagina_index pagina_index = new imx_pagina_index();
	imx_pagina_index_manager pagina_index_manager = new imx_pagina_index_manager();
	
	ArrayList<String> reg_archivo= new ArrayList<String>();
	ArrayList<String> reg_ocr = new ArrayList<String>();
	ArrayList<Documento> reg_documentos = new ArrayList<Documento>();

    
    public ocrManager(String absolutePath, String nameocr, Documento d) {
         this.reg_archivo.add(absolutePath);
         this.reg_ocr.add(nameocr);
         this.reg_documentos.add(d);
	}


	public ocrManager(ArrayList<String> reg_img, ArrayList<String> reg_ocr, ArrayList<Documento> reg_documentos) {
		this.reg_archivo = reg_img;
		this.reg_ocr = reg_ocr;
		this.reg_documentos = reg_documentos;
	}

	 public  File performOCR()  {
	        
	     File outputFile = null;
		 try {

	            final File imageFile = new File(img_input);
	            outputFile = new File(txt_output);

	            if (!imageFile.exists()) {
	            	log.error("El archivo no existe. " + "[ "+imageFile+"] ");
	                return null;
	            }

	            String curLangCode = Config.LANG;

	            log.debug("Lenguaje OCR: [ "+curLangCode+" ]");
	            List<IIOImage> iioImageList = ImageIOHelper.getIIOImageList(imageFile);
	            
	            OCRImageEntity entity;
	            
	            if(Config.ConverttoGray)
	            	entity = new OCRImageEntity(imageFile, -1);
	            else	
	            	entity = new OCRImageEntity(iioImageList, -1);
	            
	            final List<File> tempImageFiles = entity.getClonedImageFiles();

	            String tessPath = null;
	            tessPath = Config.tessPath;

	            ocr ocrEngine = new ocr(tessPath);
	            String result = ocrEngine.recognizeText(tempImageFiles, curLangCode);

	            // postprocess to correct common OCR errors
	            result = Processor.postProcess(result, curLangCode);
	            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile.getPath()), ocrManager.UTF8));
	            out.write(result);
	            out.close();
	            log.debug("Creado OCR: [ "+ outputFile.getPath() + " ]");
	            
	            
	            if(BORRAR_IMG_TEMP_OCR)
	            {
	            	if(imageFile.delete()){
		            	log.debug("Imagen temporal OCR borrada : "+imageFile.getAbsolutePath());
		            	while(tempImageFiles.iterator().hasNext())
		            		tempImageFiles.iterator().next().delete();
	            	}
	            	else
	            	{
	            		log.warn("No se borro Imagen temporal OCR  : "+imageFile.getAbsolutePath());
	            	}
	            	
	            }
	            
	        } catch (Exception e) {
	        	log.error(e,e);
	        }	            
	        
	        return outputFile;
	        
	    }
	 
	 //Sincronizado para evitar encontrar el indece de Lucene Lockeado
	 private synchronized boolean indexar_documento(File ocr_to_indexing, Documento documento)
	 {
		 
      
        	retVal = IndexFiles.indexDocs(Config.getLuceneIndex(), ocr_to_indexing.getPath(), documento);
        	if (retVal)
        	{
        		log.debug("OCR indexado correctamente!");
        		
        	}
        	
        	return retVal;
	 }


	 
	@Override
	public void run() {
		
		if(reg_archivo!=null & reg_ocr!=null & reg_documentos!=null)
		{
	    	init_ocr_index_batch();			
		}

	}
		
	
	//Metodo experimental
	private synchronized void init_ocr_index_batch()
	{
				
    	for(int i=0;i <reg_documentos.size();i++)
		{

            this.img_input=reg_archivo.get(i);
            this.txt_output=reg_ocr.get(i);
            this.documento =reg_documentos.get(i);
            
            //Verifica que sea una IMX_FILE (Imagenes)
            if(documento.getIdTipoDocto() == 2)
            {
            	ocr_vol = performOCR();
				idx_ocr_ok = indexar_documento(ocr_vol, documento);
            }
            else
            	idx_ocr_ok = indexar_documento(ocr_vol, documento);
				

			if(ocr_vol != null && idx_ocr_ok)
				log.debug("El proceso de OCR e Indexacion termino exitosamente de : "+ txt_output);
			else
				log.warn("Fallo el proceso de OCR e Indexacion de : "+ img_input);
			
    		
		}
		
	}
	
	
	
}
