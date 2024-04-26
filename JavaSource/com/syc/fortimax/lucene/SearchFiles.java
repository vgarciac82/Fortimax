package com.syc.fortimax.lucene;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public abstract class SearchFiles {

	private static final Logger log = Logger.getLogger(SearchFiles.class);

	private static ArrayList<Document> doPagingSearch(String in,
			Searcher searcher, Query query, int hitsPerPage, int page)
			throws IOException {
		ArrayList<Document> docs = new ArrayList<Document>();

		if (page < 1) {
			page = 1;
		}
		if (hitsPerPage < 1) {
			hitsPerPage = 1;
		}

		int hitspage = page * hitsPerPage;

		TopScoreDocCollector collector = TopScoreDocCollector.create(hitspage,
				true);
		searcher.search(query, collector);

		int numTotalHits = collector.getTotalHits();
		log.info(numTotalHits + " total matching documents");

		int start = hitspage - hitsPerPage;
		int end = Math.min(numTotalHits, hitspage);
		// start = end; Si se desea mostrar las ultimas hitsperpage por pagina

		while (start >= end) {
			start -= hitsPerPage;
		}

		if (start < 0) {
			start = 0;
		}
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			docs.add(doc);
			String path = doc.get("path");
			if (path != null) {
				log.info((i + 1) + ". " + path);
			} else {
				log.info((i + 1) + ". " + "No path for this document");
			}

		}
		return docs;
	}

   public static void createIndex(File indexDir) throws IOException {
		    // the boolean true parameter means to create a new index everytime, 
		    // potentially overwriting any existing files there.
		    FSDirectory dir = FSDirectory.open(indexDir);

		    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

		    IndexWriter writer = new IndexWriter(dir, analyzer,true, IndexWriter.MaxFieldLength.UNLIMITED);
		    
		    writer.close();
		  }

	
	public static ArrayList<Document> search(String index, String queries,
			int hitsPerPage, int Page) throws CorruptIndexException,
			IOException, ParseException {
		ArrayList<Document> docs = new ArrayList<Document>();

		String field = "contents";
		int repeat = 0;
		
		File indexDir = new File(index);
		
		if(!indexDir.exists())
			createIndex(indexDir);
		else if (indexDir.isDirectory())
			if(indexDir.listFiles().length==0)
				createIndex(indexDir);
		
		IndexReader reader = IndexReader.open(
				FSDirectory.open(indexDir), true);

		Searcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

		QueryParser parser = new QueryParser(Version.LUCENE_30, field, analyzer);

		boolean cont = true;

		if (queries == null || queries.length() == -1)
			cont = false;

		if (queries != null) {
			queries = queries.toLowerCase().trim();
		}
		if (queries != null && queries.length() == 0)
			cont = false;

		if (cont) {
			Query query = parser.parse(queries);
			log.debug("Searching for: " + query.toString(field));

			if (repeat > 0) { // repeat & time as benchmark
				for (int i = 0; i < repeat; i++) {
					searcher.search(query, null, 100);
				}
			}

			docs = doPagingSearch(queries, searcher, query, hitsPerPage, Page);

			reader.close();
		}

		return docs;
	}

	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {

		search("/home/bubuntux/workspace/Volumens/_Index", "caja toreo depura",
				2, 1);

	}
}
