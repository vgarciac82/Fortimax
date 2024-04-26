package com.syc.fortimax.lucene;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.syc.imaxfile.Documento;

public abstract class IndexFiles {

	private static final Logger log = Logger.getLogger(IndexFiles.class);

	private static boolean indexDocs(IndexWriter writer, File file, Documento d)
			throws IOException {
		boolean ok = false;
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						ok = indexDocs(writer, new File(file, files[i]), d);
					}
				}
			} else {
				log.info("Indexando contenido: " + file);
				try {
					writer.addDocument(FileDocument.Document(file, d));
					
					ok = true;
				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException fnfe) {	
					log.error(fnfe,fnfe);
					ok = false;
				}
			}
		}
		return ok;
	}

	/**
	 * Index all text files under a directory.
	 * 
	 * @param d
	 */
	public static boolean indexDocs(String index, String dir, Documento d) {
		boolean ok = true;
	
		if (dir == null || index == null) {
			log.error(" Parametro Invalido ");
			ok = false;
		} else {
			final File INDEX_DIR = new File(index);
			final File docDir = new File(dir);

			if (!docDir.exists() || !docDir.canRead()) {
				log
						.warn("Document directory '"
								+ docDir.getAbsolutePath()
								+ "' does not exist or is not readable, please check the path");
				ok = false;
			}

			if (ok) {
				try {
					
					
					FSDirectory Directorio = FSDirectory.open(INDEX_DIR);

					IndexWriter writer= new IndexWriter(Directorio,new StandardAnalyzer(
							Version.LUCENE_30), !INDEX_DIR.exists(),
							IndexWriter.MaxFieldLength.UNLIMITED);
					
					log.info("Indexing to directory '" + INDEX_DIR + "'...");
					ok = indexDocs(writer, docDir, d);
					log.debug("Optimizing...");
					writer.commit();
					writer.close();
					
					
				} catch (IOException e) {	log.error(e,e);

					log.error(" caught a " + e.getClass() + "\n with message: "
							+ e.getMessage());
					ok = false;
				}
			}
		}
		return ok;
	}

}
