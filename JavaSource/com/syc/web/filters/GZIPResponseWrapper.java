package com.syc.web.filters;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

public class GZIPResponseWrapper extends HttpServletResponseWrapper {

private static final Logger log = Logger.getLogger(GZIPResponseWrapper.class);
	protected HttpServletResponse origResponse = null;
	protected ServletOutputStream stream = null;
	protected PrintWriter writer = null;

	public GZIPResponseWrapper(HttpServletResponse response) {
		super(response);
		origResponse = response;
	}

	public ServletOutputStream createOutputStream() throws IOException {
		return (new GZIPResponseStream(origResponse));
	}

	public void finishResponse() {
		try {
			if (writer != null) {
				writer.close();
			} else {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (IOException e) {	log.error(e,e);

		}
	}

	public void flushBuffer() throws IOException {
		stream.flush();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() ya fue llamado antes!");
		}

		if (stream == null)
			stream = createOutputStream();
		return (stream);
	}

	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return (writer);
		}

		if (stream != null) {
			throw new IllegalStateException("getOutputStream() ya fue llamado antes!");
		}

		stream = createOutputStream();
		// BUG FIX 2003-12-01 Reuse content's encoding, don't assume UTF-8
		writer =
			new PrintWriter(new OutputStreamWriter(stream, origResponse.getCharacterEncoding()));
		return (writer);
	}

	public void setContentLength(int length) {
	}
}
