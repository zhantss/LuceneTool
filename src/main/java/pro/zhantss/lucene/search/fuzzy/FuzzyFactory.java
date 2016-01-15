
package pro.zhantss.lucene.search.fuzzy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.alibaba.fastjson.JSONObject;
import pro.zhantss.lucene.store.database.DatabaseDirectory;

import pro.zhantss.lucene.data.Resources;
import pro.zhantss.lucene.data.dbimport.AnalyzerType;
import pro.zhantss.lucene.orm.ORMConvert;
import pro.zhantss.lucene.search.dir.DatabaseIndexConfig;
import pro.zhantss.lucene.search.dir.FileIndexConfig;
import pro.zhantss.lucene.search.dir.IndexConfig;
import pro.zhantss.lucene.search.dir.MemoryIndexConfig;
import pro.zhantss.lucene.search.page.CommonPage;

public class FuzzyFactory {
	
	private final Map<IndexConfig, FuzzySearcher> sm = new HashMap<IndexConfig, FuzzySearcher>();
	
	public FuzzySearcher getFuzzy(IndexConfig config, AnalyzerType type, FuzzyConfig fuzzy) throws IOException {
		if (config == null) {
			return null;
		}
		
		if (sm.containsKey(config)) {
			return sm.get(config);
		}
		
		if (type == null) {
			type = AnalyzerType.COMPLEX;
		}
		
		Directory directory = null;
		if (config instanceof MemoryIndexConfig) {
			directory = new RAMDirectory();
		} else if (config instanceof FileIndexConfig) {
			FileIndexConfig fic = (FileIndexConfig) config;
			directory = FSDirectory.open(fic.getDirpath());
		} else if (config instanceof DatabaseIndexConfig) {
			DatabaseIndexConfig dic = (DatabaseIndexConfig) config;
			directory = new DatabaseDirectory(dic.getControl(), dic.getDc(), dic.getIndexTableName());
		}
		if (directory != null) {
			Analyzer analyzer = getAnalyze(type);
			FuzzySearcher searcher = new FuzzySearcher(directory, analyzer, fuzzy);
			sm.put(config, searcher);
		}
		return sm.get(config);
	}
	
	public class FuzzySearcher {
		
		private QueryParser parser;
		
		private final Directory directory;
		
		private final DirectoryReader reader;
		
		private final IndexSearcher searcher;
		
		private final FuzzyConfig fuzzy;
		
		private FuzzySearcher(Directory directory, Analyzer analyzer, FuzzyConfig fuzzy) throws IOException {
			this.directory = directory;
			this.reader = DirectoryReader.open(this.directory);
			this.fuzzy = fuzzy;
			this.parser = new MultiFieldQueryParser(Resources.DEFAULT_VERSION, fuzzy.getFields(), analyzer, fuzzy);
			this.searcher = new IndexSearcher(this.reader);
		}
		
		public FuzzyConfig getFuzzy() {
			return fuzzy;
		}
		
		public <T> CommonPage<T> fuzzy(String input, Integer curr, Integer pageSize, Class<T> c) throws ParseException, IOException {
			if (curr < 1 || pageSize <= 0) {
				return null;
			}
			
			org.apache.lucene.search.Query q = parser.parse(input);
			Integer start = (curr - 1) * pageSize;
			if (curr == 1) {
				start = 1;
			}
			TopDocs pre  = this.searcher.search(q, start);
			ScoreDoc startDoc = null;
			if (pre.scoreDocs.length > 0) {
				startDoc = pre.scoreDocs[pre.scoreDocs.length - 1];
			}
			
			TopDocs hits = this.searcher.searchAfter(startDoc, q, pageSize);
			ScoreDoc[] scoreDocs = hits.scoreDocs;
			
			List<T> array = new ArrayList<T>();
			for (ScoreDoc doc : scoreDocs) {
				Document document = searcher.doc(doc.doc);
				JSONObject object = new JSONObject();
				List<IndexableField> fields = document.getFields();
				for (IndexableField field : fields) {
					object.put(field.name(), field.stringValue());
				}
				ORMConvert<T> oc = new ORMConvert<T>();
				T item = oc.convert(c, object);
				if (item != null) {
					array.add(item);
				}
			}
			
			CommonPage<T> page = new CommonPage<T>();
			page.setTotal(hits.totalHits);
			page.setCurr(curr);
			page.setPageSize(pageSize);
			page.setArray(array);
			return page;
		}
		
	}
	
	private static Analyzer getAnalyze(AnalyzerType type) {
		switch (type) {
			case COMPLEX :
				return Resources.complex;
			case MAXWORD : 
				return Resources.maxWord;
			case SIMPLE : 
				return Resources.simple;
			default :
				return Resources.complex;
		}
	}
	
}
