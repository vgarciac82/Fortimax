package com.syc.web.filters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ZIPResponseStream extends ServletOutputStream {

private static final Logger log = Logger.getLogger(ZIPResponseStream.class);
	// abstraction of the output stream used for compression
	protected OutputStream bufferedOutput = null;
	// state keeping variable for if close() has been called
	protected boolean closed = false;
	// reference to original response
	protected HttpServletResponse response = null;
	// reference to the output stream to the client's browser
	protected ServletOutputStream output = null;
	// default size of the in-memory buffer
	private int bufferSize = 50000;

	public ZIPResponseStream(HttpServletResponse response) throws IOException {
		super();
		closed = false;
		this.response = response;
		this.output = response.getOutputStream();
		bufferedOutput = new ByteArrayOutputStream();
	}

	public void close() throws IOException {
		// This hack shouldn't be needed, but it seems to make JBoss and Struts
		// like the code more without hurting anything.
		/*
		// verify the stream is yet to be closed
		if (closed) {
		  throw new IOException("Este output stream ya fue cerrado!");
		}
		*/
		// if we buffered everything in memory, zip it
		if (bufferedOutput instanceof ByteArrayOutputStream) {
			// get the content
			ByteArrayOutputStream baos = (ByteArrayOutputStream) bufferedOutput;
			// prepare a zip stream
			ByteArrayOutputStream compressedContent = new ByteArrayOutputStream();
			ZipOutputStream zipstream = new ZipOutputStream(compressedContent);
			//zipstream.putNextEntry(new ZipEntry("fortimax"));
			byte[] bytes = baos.toByteArray();
			zipstream.write(bytes);
			zipstream.finish();
			// get the compressed content
			byte[] compressedBytes = compressedContent.toByteArray();
			// set appropriate HTTP headers
			response.setContentLength(compressedBytes.length);
			response.addHeader("Content-Encoding", "x-compress");
			output.write(compressedBytes);
			output.flush();
			output.close();
			closed = true;
		}
		// if things were not buffered in memory, finish the ZIP stream and response
		else if (bufferedOutput instanceof ZipOutputStream) {
			// cast to appropriate type
			ZipOutputStream zipstream = (ZipOutputStream) bufferedOutput;
			// finish the compression
			zipstream.finish();
			// finish the response
			output.flush();
			output.close();
			closed = true;
		}
	}

	public void flush() throws IOException {
		if (closed) {
			throw new IOException("No se puede vaciar un output stream cerrado");
		}
		bufferedOutput.flush();
	}

	public void write(int b) throws IOException {
		if (closed) {
			throw new IOException("No se puede escribir a un output stream cerrado");
		}
		// make sure we aren't over the buffer's limit
		checkBufferSize(1);
		// write the byte to the temporary output
		bufferedOutput.write((byte) b);
	}

	private void checkBufferSize(int length) throws IOException {
		// check if we are buffering too large of a file
		if (bufferedOutput instanceof ByteArrayOutputStream) {
			ByteArrayOutputStream baos = (ByteArrayOutputStream) bufferedOutput;
			if (baos.size() + length > bufferSize) {
				// files too large to keep in memory are sent to the client without Content-Length specified
				response.addHeader("Content-Encoding", "x-compress");
				// get existing bytes
				byte[] bytes = baos.toByteArray();
				// make new zip stream using the response output stream
				ZipOutputStream zipstream = new ZipOutputStream(output);
				//zipstream.putNextEntry(new ZipEntry("fortimax"));
				zipstream.write(bytes);
				// we are no longer buffering, send content via gzipstream
				bufferedOutput = zipstream;
			}
		}
	}

	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) throws IOException {
		System.out.println("Escribiendo...");
		if (closed) {
			throw new IOException("No se puede escribir a un output stream cerrado");
		}
		// make sure we aren't over the buffer's limit
		checkBufferSize(len);
		// write the content to the buffer
		bufferedOutput.write(b, off, len);
	}

	public boolean closed() {
		return (this.closed);
	}

	public void reset() {
		//noop
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		// TODO Auto-generated method stub
		
	}
}
