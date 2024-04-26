package com.syc.logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.syc.fortimax.config.Config;

public class LoggerListener implements ServletContextListener {

private static final Logger log = Logger.getLogger(LoggerListener.class);
	public void contextInitialized(ServletContextEvent event) {
		DOMConfigurator.configure(LoggerListener.class.getClassLoader()
				.getResource(Config.log4jPath + "log4j.xml"));
	}

	public void contextDestroyed(ServletContextEvent arg0) {
	}
}
