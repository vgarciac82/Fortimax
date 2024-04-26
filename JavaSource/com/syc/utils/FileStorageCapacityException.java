package com.syc.utils;
import org.apache.log4j.Logger;

public class FileStorageCapacityException extends Exception {

@SuppressWarnings("unused")
private static final Logger log = Logger.getLogger(FileStorageCapacityException.class);

	private static final long serialVersionUID = 2952062102007330653L;

	public FileStorageCapacityException() {
		super();
	}

	public FileStorageCapacityException(String msg) {
		super(msg);
	}
}
