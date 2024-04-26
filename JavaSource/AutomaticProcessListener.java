import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.syc.fortimax.OCR.ocrManager;
import com.syc.fortimax.config.Config;

/**
 * 	Listener, para la automatizacion de procesos automaticos de Fortimax
 *	-Obtencion de OCR
 *	-Indexacion en Lucene
 *	-Obtencion de miniaturas
 */
public class AutomaticProcessListener extends TimerTask implements ServletContextListener {
	
	private Thread Process_OCR = null;
	
	private static final Logger log = Logger.getLogger(AutomaticProcessListener.class);
	
	private Timer timer;
	private long Inicio = 0L;
	private long Final = 0L;
	
    public void contextInitialized(ServletContextEvent arg0) 
    {
		//Inicializamos las variables con los parametros del Config.java
    	log.debug("Inicializando parametros de AutomaticProcessListener");
    	this.Inicio = Config.START;
    	this.Final = Config.END;
    	    	
        // Iniciamos el timer
        timer = new Timer();
        Long desde= Inicio;
        Long hasta = Final;
        timer.schedule(this, desde, hasta); 

    }
    public void contextDestroyed(ServletContextEvent arg0) 
    {
    	timer.cancel();
    }
    
	@Override
	public void run() 
	{
		      
			if (Config.ActivarOCR) {
		    	log.debug("Ejecutando cada: ["+Final/60000+"] minutos proceso automatico de Indexacion de OCR "); 
				//Process_OCR = new Thread(new ocrManager()); 				
				//Process_OCR.setPriority(Thread.MIN_PRIORITY);
				//Process_OCR.start();
				
	        }
	}
	
}
