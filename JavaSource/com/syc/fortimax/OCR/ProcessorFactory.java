package com.syc.fortimax.OCR;

public class ProcessorFactory {
    
     public static IPostProcessor createProcessor(ISO639 code) {
        IPostProcessor processor;
        
        switch (code) {
            case eng:
                processor = new EngPP();
                break;
            default:
                processor = new EngPP();
                break;
        }
        
        return processor;
    }
}
