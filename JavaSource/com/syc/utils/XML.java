package com.syc.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class XML {
	Object object;
	
	public XML(Object object) {
		this.object = object;
	}

	public void printXML(HttpServletResponse response) throws JAXBException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/xml");      
		out.print(getString());
		out.flush();
	}
	
	private String getString() throws JAXBException {
		StringWriter sw = new StringWriter();
        JAXBContext jc = JAXBContext.newInstance(object.getClass());        
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(object,sw);
		return sw.toString();
	}
	
	@Override
	public String toString() {
		try {
			return getString();
		} catch (JAXBException e) {
			return null;
		}
	}
}
