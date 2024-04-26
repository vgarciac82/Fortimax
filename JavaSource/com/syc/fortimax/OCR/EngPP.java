package com.syc.fortimax.OCR;

public class EngPP implements IPostProcessor {

    @Override
    public String postProcess(String text) {
        // No hay un especial tratado para el Ingles
    	
//    	Esta clase esta pensada para cada idioma que pueda sacar el OCR, para corregir errores comun
//    	en el interpretado de carcateres, pj, en español no se utilizan cierto simbolos, estosp uede ser cambiado por
//    	simbolos que si sean utilizados como: "" por "" , CON UN SIMPLE "REMPLACEALL"
        return text;
    }
}
