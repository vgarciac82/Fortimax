package com.syc.tree;
import org.apache.log4j.Logger;

public class PasteNodeManagerException extends Exception {

private static final Logger log = Logger.getLogger(PasteNodeManagerException.class);

	private static final long serialVersionUID = 934065744716526483L;

	public PasteNodeManagerException() {
		super();
	}

	public PasteNodeManagerException(String msg) {
		super(msg);
	}
}
