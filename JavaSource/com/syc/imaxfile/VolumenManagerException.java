package com.syc.imaxfile;

import org.apache.log4j.Logger;

public class VolumenManagerException extends Exception {

	private static final Logger log = Logger
			.getLogger(VolumenManagerException.class);

	private static final long serialVersionUID = -9094413841574157551L;

	public VolumenManagerException() {
		super();
	}

	public VolumenManagerException(String msg) {
		super(msg);
	}

	public VolumenManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public VolumenManagerException(Throwable cause) {
		super(cause);
	}

}
