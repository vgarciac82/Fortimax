package com.syc.fortimax.OCR;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class Processor {

    public static String postProcess(String text, String langCode) {
        try {
            IPostProcessor processor = ProcessorFactory.createProcessor(ISO639.valueOf(langCode.substring(0, 3)));
            return processor.postProcess(text);
        } catch (Exception exc) {
            return text;
        }
    }

    public static String postProcess(String text, String langCode, String dangAmbigsPath, boolean dangAmbigsOn) throws Exception {
        if (text.trim().length() == 0) {
            return text;
        }

        // correct using external x.DangAmbigs.txt file first, if enabled
        if (dangAmbigsOn) {
            StringBuffer strB = new StringBuffer(text);

            // replace text based on entries read from an x.DangAmbigs.txt file
            Map<String, String> replaceRules = TextUtilities.loadMap(new File(dangAmbigsPath, langCode + ".DangAmbigs.txt").getPath());
            if (replaceRules.size() == 0 && langCode.length() > 3) {
                replaceRules = TextUtilities.loadMap(new File(dangAmbigsPath, langCode.substring(0, 3) + ".DangAmbigs.txt").getPath()); // falls back on base
            }

            if (replaceRules.size() == 0) {
                throw new UnsupportedOperationException(langCode);
            }

            Iterator<String> iter = replaceRules.keySet().iterator();

            while (iter.hasNext()) {
                String key = iter.next();
                String value = replaceRules.get(key);
                strB = StringUtils.replaceAll(strB, key, value);
            }
            text = strB.toString();
        }

        // postprocessor
        text = postProcess(text, langCode);
        
        // correct common errors caused by OCR
        text = TextUtilities.correctOCRErrors(text);

        // correct letter cases
        return TextUtilities.correctLetterCases(text);
    }
}
