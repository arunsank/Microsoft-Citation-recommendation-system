package com.depies.bdap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.BytesRef;

public class Indexing {

	public static void main(String[] args) throws IOException, CorruptIndexException, LockObtainFailedException {
		Indexing obj = new Indexing();
		obj.gen_index();
	}

	@SuppressWarnings("resource")
	public void gen_index() throws IOException, CorruptIndexException, LockObtainFailedException {
		//file to index
		String csvFile = "C:/Users/siddharth/Desktop/big data project/graph.csv";
		//location to build the index
		String citingindexOutput = "C:/Users/siddharth/Desktop/big data project/index/citing";
		String citedindexOutput = "C:/Users/siddharth/Desktop/big data project/index/cited";
		String relindexOutput = "C:/Users/siddharth/Desktop/big data project/index/rel";
		//defining index directories
		Directory citingIndexDir = FSDirectory.open(Paths.get(citingindexOutput));
		Directory citedIndexDir = FSDirectory.open(Paths.get(citedindexOutput));
		Directory relIndexDir = FSDirectory.open(Paths.get(relindexOutput));
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		//defining standard analyzer
		Analyzer stdanalyzer = new StandardAnalyzer();
		
		//defining Index Writer Configurations
		IndexWriterConfig citingiwconfig = new IndexWriterConfig(stdanalyzer);
		IndexWriterConfig citediwconfig = new IndexWriterConfig(stdanalyzer);
		IndexWriterConfig reliwconfig = new IndexWriterConfig(stdanalyzer);
		citingiwconfig.setOpenMode(OpenMode.CREATE);
		citediwconfig.setOpenMode(OpenMode.CREATE);
		reliwconfig.setOpenMode(OpenMode.CREATE);
		
		//defining index writers
		IndexWriter citingIndwriter = new IndexWriter(citingIndexDir, citingiwconfig);
		IndexWriter citedIndwriter = new IndexWriter(citedIndexDir, citediwconfig);
		IndexWriter relIndwriter = new IndexWriter(relIndexDir, reliwconfig);
		//creating maps for citing paper and cited papers to be indexed
		Map<String, String> citing_paper_map = new HashMap<String, String>();
		Map<String, String> cited_paper_map = new HashMap<String, String>();
		
		//creating a map to load the citing paper and cited paper relation
		Map<String, String> relation_map = new HashMap<String, String>();
		
		String key;
		String value;
		// loading  the  maps from the file
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
			String[] paper = line.split(cvsSplitBy);
			citing_paper_map.put(paper[4].replaceAll("\\?", " "), paper[5].replaceAll("\\?", " "));
			cited_paper_map.put(paper[9].replaceAll("\\?", " "), paper[10].replaceAll("\\?", " "));
			key = paper[4];
			value = paper[9];
			relation_map.put(key, relation_map.get(key) == null ? value : relation_map.get(key) + "," + value);
		}
		int counter = 1;
		// indexing the relations
		for (Map.Entry<String, String> entry : relation_map.entrySet()) {
			Document relDoc = new Document();
			relDoc.add(new TextField("rel_citing_paper_id", entry.getKey().toString(), Field.Store.YES));
			relDoc.add(new TextField("rel_cited_paper_ids", entry.getValue().toString(), Field.Store.YES));
			relIndwriter.addDocument(relDoc);
			counter++;
		}
		//mering all the index to make a large index of relations
		relIndwriter.forceMerge(1);
		relIndwriter.commit();
		relIndwriter.close();
		System.out.println("INDEXING SUCCESS FOR REL");
		counter = 1;
		// indexing the citing papers
		for (Map.Entry<String, String> entry : citing_paper_map.entrySet()) {
			Document citingDoc = new Document();
			citingDoc.add(new TextField("citing_paper_id", entry.getKey().toString(), Field.Store.YES));
			citingDoc.add(new TextField("citing_paper_title", entry.getValue().toString(), Field.Store.YES));
			citingIndwriter.addDocument(citingDoc);
			counter++;
		}
		//mering all the index to make a large index of citing papers
		citingIndwriter.forceMerge(1);
		citingIndwriter.commit();
		citingIndwriter.close();
		System.out.println("INDEXING SUCCESS FOR CITING");
		// indexing the cited papers
		for (Map.Entry<String, String> entry : cited_paper_map.entrySet()) {
			Document citedDoc = new Document();
			citedDoc.add(new TextField("cited_paper_id", entry.getKey().toString(), Field.Store.YES));
			citedDoc.add(new TextField("cited_paper_title", entry.getKey().toString(), Field.Store.YES));
			citedIndwriter.addDocument(citedDoc);
			counter++;
		}
		//mering all the index to make a large index of cited papers
		citedIndwriter.forceMerge(1);
		citedIndwriter.commit();
		citedIndwriter.close();
		System.out.println("INDEXING SUCCESS FOR CITED");
		System.out.println("Done");
	}
}
