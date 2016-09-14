package com.depies.bdap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.document.*;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.BM25Similarity;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;

public class similarity {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, ParseException {
		String csvFile = "C:/Users/siddharth/Desktop/big data project/graph.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String IndexLocation = "C:/Users/siddharth/Desktop/big data project/index/citing/";
		String queryString = new String();
		Map<String, String> citing_paper_map = new HashMap<String, String>();
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
			String[] paper = line.split(cvsSplitBy);
			paper[5].replaceAll("\\?", " ");
			paper[5].replaceAll("\\?", " ");
			citing_paper_map.put(paper[4], paper[5]);
		}
		int counter = 0;
		for (Map.Entry<String, String> entry : citing_paper_map.entrySet()) {
			BM25sim(IndexLocation, entry.getValue(), entry.getKey());
			counter++;
		}
	}

	public static void BM25sim(String IndexLocation, String queryString, String queryID)
			throws IOException, ParseException {
		// creating index reader 
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(IndexLocation)));
		
		// creating searcher
		IndexSearcher searcher = new IndexSearcher(reader);
		
		//creating standard analyser as standard analyzer was used to create index
		Analyzer analyzer = new StandardAnalyzer();
		
		//creating query parser
		QueryParser parser = new QueryParser("citing_paper_title", analyzer);
		
		//creating boolean query 
		BooleanQuery booleanQuery = new BooleanQuery();
		
		//creating qury term
		Term term = new Term("citing_paper_title", queryString);
		TermQuery queryTerm = new TermQuery(term);
		Query query = queryTerm;
		
		// adding terms to query for searching
		booleanQuery.add(query, BooleanClause.Occur.SHOULD);
		
		// assigning default similarity to searcher
		searcher.setSimilarity(new DefaultSimilarity());
		
		// collecting top 20 scored documents
		TopScoreDocCollector collector_bool = TopScoreDocCollector.create(20);
		
		//removing special charecters from query
		String removedQues = String.valueOf(booleanQuery).replaceAll("\\?", "");
		searcher.search(parser.parse(removedQues), collector_bool);
		org.apache.lucene.search.ScoreDoc[] hits = collector_bool.topDocs().scoreDocs;
		
		//creating output file
		File Default_sim_file = new File("C:/Users/siddharth/Desktop/big data project/result/defaultsim_2.txt");
		
		//defining file writer
		FileWriter BM25fw = new FileWriter(Default_sim_file, false);
		BufferedWriter BWriter = new BufferedWriter(BM25fw);
		
		//creating a list which is used to write to the output file
		ArrayList<String> resultlist = new ArrayList<String>();
		for (int i = 0; i < hits.length; i++) {
			int docnum = hits[i].doc;
			Document doc = searcher.doc(docnum);
			resultlist.add(i, doc.get("citing_paper_id"));
		}
		String related_papers = new String();
		related_papers = "";
		
		//criting top 20 results of each citing paper to  the  output file
		for (String line : resultlist) {
			related_papers = related_papers + "," + line;
		}
		if (!related_papers.isEmpty()) {
			ArrayList<String> data = new ArrayList<>();
			String data_chck = queryID + ',' + related_papers.substring(1);
			List<String> items = Arrays
					.asList(data_chck.split("\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*"
							+ "\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*"));
			@SuppressWarnings("resource")
			Scanner in = new Scanner(items.toString());
			data.add(in.next());
			System.out.println("data is" + data);
			for (String data_1 : data) {
				try {

					File file = new File("C:\\Users\\siddharth\\Desktop\\defaultsim_2.txt");
					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fileWriter = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fileWriter);

					bw.write(data_1);
					bw.newLine();
					bw.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			ArrayList<String> data = new ArrayList<>();
			String data_chck = queryID + ',' + related_papers;
			List<String> items = Arrays
					.asList(data_chck.split("\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*"
							+ "\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*,\\s*"));
			Scanner in = new Scanner(items.toString());
			data.add(in.next());
			System.out.println("data is" + data);
			for (String data_1 : data) {
				try {

					File file = new File("C:\\Users\\siddharth\\Desktop\\defaultsim_2.txt");

					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fileWriter = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fileWriter);

					bw.write(data_1);
					bw.newLine();
					bw.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Done");

		}
		System.out.println("Done");
	}
}
