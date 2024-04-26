package com.syc.fortimax.OCR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.syc.fortimax.config.Config;

public class ocr {
	
	private static final Logger log = Logger.getLogger(ocr.class);
    private final String LANG_OPTION = Config.LANG_OPTION;
    private final String EOL = Config.EOL;
    final static String OUTPUT_FILE_NAME = Config.PREFIX_OCR;
    final static String FILE_EXTENSION = Config.FILE_EXTENSION_OCR;
    String LANG = Config.LANG;
    private String tessPath;

    /** Creates a new instance of OCR */
    public ocr(String tessPath) {
        this.tessPath = tessPath;
    }

    /**
     * @param tempImageFiles
     * @param lang
     * @return String
     * @throws java.lang.Exception
     */
    String recognizeText(final List<File> tempImageFiles, final String lang) throws Exception {
        File tempTessOutputFile = File.createTempFile(OUTPUT_FILE_NAME, FILE_EXTENSION);
        String outputFileName = tempTessOutputFile.getPath().substring(0, tempTessOutputFile.getPath().length() - FILE_EXTENSION.length()); // chop the .txt extension
        //Idioma por Default
        if(!lang.isEmpty())
        	LANG=lang;
        
        List<String> cmd = new ArrayList<String>();
        cmd.add(tessPath + "tesseract");
        cmd.add(""); // placeholder for inputfile
        cmd.add(outputFileName);
        cmd.add(LANG_OPTION);
        cmd.add(LANG);

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(System.getProperty("user.home")));
        pb.redirectErrorStream(true);

        StringBuffer result = new StringBuffer();

        for (File tempImageFile : tempImageFiles) {
//            ProcessBuilder pb = new ProcessBuilder(tessPath + "/tesseract", tempImageFile.getPath(), outputFileName, LANG_OPTION, lang);

            cmd.set(1, tempImageFile.getPath());
            
            pb.command(cmd);
            log.debug("Comando OCR = " + cmd);
            Process process = pb.start();

            int w = process.waitFor();
            log.debug("El proceso Termino con Estatus = " + w);

            if (w == 0) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tempTessOutputFile), "UTF-8"));

                String str;

                while ((str = in.readLine()) != null) {
                    result.append(str).append(EOL);
                }
                
                int length = result.length();
                if (length >= EOL.length()) {
                    result.setLength(length - EOL.length()); // remove last EOL
                }
                in.close();
            } else {
                String msg;
                switch (w) {
                    case 1:
                        msg = "Error accesando los archivos.";
                        break;
                    case 29:
                        msg = "No se pudo reconocer la imagen.";
                        break;
                    case 31:
                        msg = "Formato de imagen no reconocido.";
                        break;
                    default:
                        msg = "Error desconocido.";
                }

                tempTessOutputFile.delete();
                log.error("Estatus OCR: " + msg);
                           
            }
        }

        tempTessOutputFile.delete();
        log.trace("==================================================[OCR]==================================================");
        log.trace(result.toString());
        log.trace("==================================================[OCR]==================================================");
        return result.toString();
    }
}
