package com.syc.fortimax.lucene;
import java.io.File;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.syc.fortimax.config.Config;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.GetDatosNodo;

/** Deletes documents from an index that do not contain a term. */
public abstract class DeleteFiles {

	private static final Logger log = Logger.getLogger(DeleteFiles.class);

	public static void Delete(String nodo) {
		try {
			Directory directory = FSDirectory.open(new File(Config.getLuceneIndex()));
			IndexReader reader = IndexReader.open(directory, false);

			Term term = new Term("id", nodoToLuceneID(nodo));
			int deleted = reader.deleteDocuments(term);

			log.info("deleted " + deleted + " documents containing " + term);

			reader.commit(reader.getCommitUserData());
			reader.close();
			directory.close();

		} catch (Exception e) {	log.error(e,e);

			log.error(" caught a " + e.getClass() + "\n with message: "
					+ e.getMessage());
		}
	}

	public static void DeleteAll(Documento d) {
		for (int i = 0; i < d.getNumeroPaginas(); i++) {
			Delete(d, d.getPaginaDocumento(i).getNumeroPagina());
		}
	}

	public static void Delete(Documento d, int idPagina) {
		GetDatosNodo gdn = new GetDatosNodo(d.getNodo());
		gdn.setPagina(idPagina);
		Delete(gdn.toString());
	}
	
	private static String nodoToLuceneID(String nodo) {
		GetDatosNodo gdn = new GetDatosNodo(nodo);
		return gdn.getGaveta()+"-"+gdn.getGabinete()+"-"+gdn.getIdCarpeta()+"-"+gdn.getIdDocumento()+"-"+gdn.getPagina();
	}
}
