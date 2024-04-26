package com.syc.imaxfile;
import org.apache.log4j.Logger;

public class DocumentoManagerException extends Exception {

private static final Logger log = Logger.getLogger(DocumentoManagerException.class);

	private static final long serialVersionUID = -6025145678212705985L;

	public DocumentoManagerException() {
		super();
	}

	public DocumentoManagerException(String msg) {
		super(msg);
	}
}
