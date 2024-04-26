package com.syc.fortimax.OCR;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.util.Log;


public class TextUtilities {

    /**
     * Corrects letter cases.
     *
     * @param input
     * @return return
     */
    public static String correctLetterCases(String input) {
        StringBuffer strB = new StringBuffer();

        // lower uppercase letters ended by lowercase letters except the first letter
        Matcher matcher = Pattern.compile("(?<=\\p{L}+)(\\p{Lu}+)(?=\\p{Ll}+)").matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(strB, matcher.group().toLowerCase());
        }
        matcher.appendTail(strB);

        // lower uppercase letters begun by lowercase letters
        matcher = Pattern.compile("(?<=\\p{Ll}+)(\\p{Lu}+)").matcher(strB.toString());
        strB.setLength(0);
        while (matcher.find()) {
            matcher.appendReplacement(strB, matcher.group().toLowerCase());
        }
        matcher.appendTail(strB);

        return strB.toString();
    }

    /**
     * Corrects common Tesseract OCR errors.
     *
     * @param input
     * @return return
     */
    public static String correctOCRErrors(String input) {
        // substitute letters frequently misrecognized by Tesseract 2.03
        return input.replaceAll("\\b1(?=\\p{L}+\\b)", "l") // 1 to l
                .replaceAll("\\b11(?=\\p{L}+\\b)", "n") // 11 to n
                .replaceAll("\\bI(?![mn]+\\b)", "l") // I to l
                .replaceAll("(?<=\\b\\p{L}*)0(?=\\p{L}*\\b)", "o") // 0 to o
                //                .replaceAll("(?<!\\.) S(?=\\p{L}*\\b)", " s") // S to s
                //                .replaceAll("(?<![cn])h\\b", "n")
                ;
    }

    public static Map<String, String> loadMap(String dangAmbigsFile) {
        Map<String, String> map = new LinkedHashMap<String, String>();

        try {
            InputStreamReader stream = new InputStreamReader(new FileInputStream(dangAmbigsFile), "UTF8");
            BufferedReader bs = new BufferedReader(stream);
            String str;
            while ((str = bs.readLine()) != null) {
                int index = str.indexOf('=');
                if (index == -1) {
                    continue;
                }

                String key = str.substring(0, index);
                String value = str.substring(index + 1);
                map.put(key, value);
            }
            bs.close();
            stream.close();
        } catch (Exception e) {
        	Log.error(e,e);
        }

        return map;
    }
}
