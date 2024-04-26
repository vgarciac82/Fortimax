package com.syc.fortimax;
import org.apache.log4j.Logger;

public class FortimaxException extends Exception {

private static final Logger log = Logger.getLogger(FortimaxException.class);

	private static final long serialVersionUID = -4934853587524639252L;

	public static final String codeUnknowed = "Unknown";
	private String code =  codeUnknowed;

	public FortimaxException(String code, String msg) {
		super(msg);
		this.code = code;
	}

	public FortimaxException(Exception ex) {
		super(ex.getMessage());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "Code: " + code + " Message: " + super.getMessage();
	}

}
