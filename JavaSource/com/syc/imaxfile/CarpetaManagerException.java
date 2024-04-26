package com.syc.imaxfile;
import org.apache.log4j.Logger;

public class CarpetaManagerException extends Exception {

private static final Logger log = Logger.getLogger(CarpetaManagerException.class);

	private static final long serialVersionUID = 5226074621838041967L;

	public CarpetaManagerException() {
		super();
	}

	public CarpetaManagerException(String msg) {
		super(msg);
	}
}
