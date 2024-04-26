package com.syc.fortimax.lucene;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import com.syc.imaxfile.Documento;

public class FileDocument {

private static final Logger log = Logger.getLogger(FileDocument.class);

	private enum EXTENSION {
		PDF, JPEG, XML, SPF, UNKNOW;

		public static EXTENSION fromString(String Str) {
			try {
				return valueOf(Str);
			} catch (Exception ex) {	
				
				log.warn(ex);

				return UNKNOW;
			}
		}
	};

	/**
	 * Makes a document for a File.
	 * <p>
	 * The document has three fields:
	 * <code>path</code>--containing the pathname of the file, as a stored,
	 * untokenized field;
	 * <code>modified</code>--containing the last modified date of the file
	 * as a field as created by <a
	 * href="lucene.document.DateTools.html">DateTools</a>; and
	 * <code>contents</code>--containing the full contents of the file, as a
	 * Reader field;
	 * 
	 * @param d
	 */
	public static Document Document(File f, Documento d)
			throws java.io.FileNotFoundException {

		// make a new, empty document
		Document doc = new Document();

		// Add the path of the file as a field named "path". Use a field that is
		// indexed (i.e. searchable), but don't tokenize the field into words.

		doc.add(new Field("path", f.getPath(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		// Llave de los documentos de IMAX
		// indexed (i.e. searchable), but don't tokenize the field into words.
		doc.add(new Field("aplicacion", d.getTituloAplicacion(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("gabinete", d.getIdGabinete() + "", Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("carpeta", d.getIdCarpetaPadre() + "",
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("documento", d.getIdDocumento() + "",
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("pagina", d.getNumeroPaginas() + "", Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		doc.add(new Field("id", d.getTituloAplicacion() + "-"
				+ d.getIdGabinete() + "-" + d.getIdCarpetaPadre() + "-"
				+ d.getIdDocumento() + "-" + d.getNumeroPaginas(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		boolean unknow = false;
		ContentHandler textHandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();
		Parser parser = null;
		switch (EXTENSION.fromString(d.getExtension().toUpperCase())) {
		case PDF:
			parser = new PDFParser();
			break;
		case JPEG:
		case XML:
		case SPF:
		case UNKNOW:
		default:
			unknow = true;
			break;
		}
		if (unknow) {
			doc.add(new Field("contents", new FileReader(f)));
		} else {
			InputStream input = null;
			try {
				input = new FileInputStream(f);
				parser.parse(input, textHandler, metadata, pcontext);
			} catch (Exception e) {	log.error(e,e);

			}
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {	log.error(e,e);

			}
			doc.add(new Field("contents", textHandler.toString(),
					Field.Store.NO, Field.Index.ANALYZED));
		}

		// return the document
		return doc;
	}
	
	

	/**
	 * Makes a document for a File.
	 * <p>
	 * The document has three fields:
	 * <code>path</code>--containing the pathname of the file, as a stored,
	 * untokenized field;
	 * <code>modified</code>--containing the last modified date of the file
	 * as a field as created by <a
	 * href="lucene.document.DateTools.html">DateTools</a>; and
	 * <code>contents</code>--containing the full contents of the file, as a
	 * Reader field;
	 * 
	 * @param d
	 */
	public static Document Document(File f, Documento d, String nextruta)
			throws java.io.FileNotFoundException {

		// make a new, empty document
		Document doc = new Document();

		// Add the path of the file as a field named "path". Use a field that is
		// indexed (i.e. searchable), but don't tokenize the field into words.

		doc.add(new Field("path", nextruta, Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		// Llave de los documentos de IMAX
		// indexed (i.e. searchable), but don't tokenize the field into words.
		doc.add(new Field("aplicacion", d.getTituloAplicacion(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("gabinete", d.getIdGabinete() + "", Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("carpeta", d.getIdCarpetaPadre() + "",
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("documento", d.getIdDocumento() + "",
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("pagina", d.getNumeroPaginas() + "", Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		doc.add(new Field("id", d.getTituloAplicacion() + "-"
				+ d.getIdGabinete() + "-" + d.getIdCarpetaPadre() + "-"
				+ d.getIdDocumento() + "-" + d.getNumeroPaginas(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		boolean unknow = false;
		ContentHandler textHandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		ParseContext pcontext = new ParseContext();
		Parser parser = null;
		switch (EXTENSION.fromString(d.getExtension().toUpperCase())) {
		case PDF:
			parser = new PDFParser();
			break;
		case JPEG:
		case XML:
		case SPF:
		case UNKNOW:
		default:
			unknow = true;
			break;
		}
		if (unknow) {
			doc.add(new Field("contents", new FileReader(f)));
		} else {
			InputStream input = null;
			try {
				input = new FileInputStream(f);
				parser.parse(input, textHandler, metadata, pcontext);
			} catch (Exception e) {	log.error(e,e);

			}
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {	log.error(e,e);

			}
			doc.add(new Field("contents", textHandler.toString(),
					Field.Store.NO, Field.Index.ANALYZED));
		}

		// return the document
		return doc;
	}
	
}
