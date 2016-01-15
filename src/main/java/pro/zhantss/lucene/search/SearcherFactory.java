package pro.zhantss.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pro.zhantss.lucene.store.database.DatabaseDirectory;

import pro.zhantss.lucene.data.Resources;
import pro.zhantss.lucene.data.dbimport.AnalyzerType;
import pro.zhantss.lucene.orm.ORMConvert;
import pro.zhantss.lucene.search.dir.DatabaseIndexConfig;
import pro.zhantss.lucene.search.dir.FileIndexConfig;
import pro.zhantss.lucene.search.dir.IndexConfig;
import pro.zhantss.lucene.search.dir.MemoryIndexConfig;
import pro.zhantss.lucene.search.fuzzy.FuzzyConfig;
import pro.zhantss.lucene.search.key.Key;
import pro.zhantss.lucene.search.page.CommonPage;
import pro.zhantss.lucene.search.page.ResultPage;
import pro.zhantss.lucene.search.sort.Sort;
import pro.zhantss.lucene.search.sort.SortType;
import pro.zhantss.lucene.search.sort.ValueParse;

public class SearcherFactory {

	private final Map<IndexConfig, Searcher> sm = new HashMap<IndexConfig, Searcher>();

	public Searcher getSearcher(IndexConfig config, AnalyzerType type,
			FuzzyConfig fuzzy) throws IOException {
		if (config == null) {
			return null;
		}

		if (sm.containsKey(config)) {
			Searcher searcher = sm.get(config);
			searcher.refresh(fuzzy);
			sm.put(config, searcher);
			return sm.get(config);
		}

		if (type == null) {
			type = AnalyzerType.COMPLEX;
		}

		Properties prop = System.getProperties();
		String systemName = prop.getProperty("os.name");
		Boolean isWindows = systemName.startsWith("Win");

		Directory directory = null;
		if (config instanceof MemoryIndexConfig) {
			directory = new RAMDirectory();
		} else if (config instanceof FileIndexConfig) {
			FileIndexConfig fic = (FileIndexConfig) config;
			if (isWindows) {
				directory = FSDirectory.open(fic.getDirpath());
			} else {
				directory = NIOFSDirectory.open(fic.getDirpath());
			}
		} else if (config instanceof DatabaseIndexConfig) {
			DatabaseIndexConfig dic = (DatabaseIndexConfig) config;
			directory = new DatabaseDirectory(dic.getControl(), dic.getDc(),
					dic.getIndexTableName());
		}
		if (directory != null) {
			Analyzer analyzer = getAnalyze(type);
			Searcher searcher = new Searcher(directory, analyzer, fuzzy);
			sm.put(config, searcher);
		}
		return sm.get(config);
	}

	public class Searcher {

		private QueryParser parser;

		private Boolean canFuzzy;

		private Analyzer analyzer;

		private QueryParser fuzzyParser;

		private final Directory directory;

		private final DirectoryReader reader;

		private final IndexSearcher searcher;

		private Searcher(Directory directory, Analyzer analyzer,
				FuzzyConfig fuzzy) throws IOException {
			this.directory = directory;
			this.reader = DirectoryReader.open(this.directory);
			this.parser = new QueryParser(Resources.DEFAULT_VERSION,
					Resources.FNAME, analyzer);
			this.searcher = new IndexSearcher(this.reader);
			this.analyzer = analyzer;
			if (fuzzy != null) {
				this.fuzzyParser = new MultiFieldQueryParser(
						Resources.DEFAULT_VERSION, fuzzy.getFields(), analyzer,
						fuzzy);
				canFuzzy = true;
			} else {
				canFuzzy = false;
			}
		}

		public QueryParser getParser() {
			return parser;
		}

		public void refresh(FuzzyConfig fuzzy) {
			if (fuzzy == null) {
				canFuzzy = false;
				return;
			}
			this.fuzzyParser = new MultiFieldQueryParser(
					Resources.DEFAULT_VERSION, fuzzy.getFields(), this.analyzer,
					fuzzy);
			canFuzzy = true;
		}

		public IndexSearcher getSearcher() {
			return searcher;
		}

		public <T> CommonPage<T> fuzzySearch(String info, String input,
				Integer curr, Integer pageSize, Class<T> c)
						throws ParseException, IOException {
			if (pageSize <= 0) {
				return null;
			}
			String squence = queryHandler(info, input);
			org.apache.lucene.search.Query q = this.parser.parse(squence);

			if (q == null) {
				return null;
			}
			if (curr < 1) {
				curr = 1;
			}
			Integer start = (curr - 1) * pageSize;
			if (curr == 1) {
				start = 1;
			}
			TopDocs pre = null;
			pre = this.searcher.search(q, start);
			ScoreDoc startDoc = null;
			if (pre.totalHits <= start) {
				Integer in_curr = pre.scoreDocs.length / pageSize
						+ pre.scoreDocs.length % pageSize == 0 ? 0 : 1;
				return getCommonFromDocs(this.searcher, pre, in_curr, pageSize,
						c);
			}
			
			if (start == 1 && pre.totalHits <= pageSize) {
				pre = this.searcher.search(q, pageSize);
				return getCommonFromDocs(this.searcher, pre, start, pageSize, c);
			}

			if (pre.scoreDocs.length > 0) {
				startDoc = pre.scoreDocs[pre.scoreDocs.length - 1];
			}

			TopDocs hits = null;
			hits = this.searcher.searchAfter(startDoc, q, pageSize);

			return getCommonFromDocs(this.searcher, hits, curr, pageSize, c);
		}

		public ResultPage fuzzySearch(String info, String input, Integer curr,
				Integer pageSize) throws ParseException, IOException {
			if (pageSize <= 0) {
				return null;
			}
			String squence = queryHandler(info, input);
			org.apache.lucene.search.Query q = this.parser.parse(squence);

			if (q == null) {
				return null;
			}
			if (curr < 1) {
				curr = 1;
			}
			Integer start = (curr - 1) * pageSize;
			if (curr == 1) {
				start = 1;
			}
			TopDocs pre = null;
			pre = this.searcher.search(q, start);
			ScoreDoc startDoc = null;
			if (pre.totalHits <= start) {
				Integer in_curr = pre.scoreDocs.length / pageSize
						+ pre.scoreDocs.length % pageSize == 0 ? 0 : 1;
				return getResultFromDocs(this.searcher, pre, in_curr, pageSize);
			}
			
			if (start == 1 && pre.totalHits <= pageSize) {
				pre = this.searcher.search(q, pageSize);
				return getResultFromDocs(this.searcher, pre, start, pageSize);
			}

			if (pre.scoreDocs.length > 0) {
				startDoc = pre.scoreDocs[pre.scoreDocs.length - 1];
			}

			TopDocs hits = null;
			hits = this.searcher.searchAfter(startDoc, q, pageSize);

			return getResultFromDocs(this.searcher, hits, curr, pageSize);
		}

		public ResultPage search(Query query, Integer curr, Integer pageSize)
				throws ParseException, IOException {
			if (pageSize <= 0) {
				return null;
			}
			String squence = query.query();
			org.apache.lucene.search.Query q = this.parser.parse(squence);
			// org.apache.lucene.search.Query q = getQuery(query);
			if (q == null) {
				return null;
			}
			if (curr < 1) {
				curr = 1;
			}
			Integer start = (curr - 1) * pageSize;
			if (curr == 1) {
				start = 1;
			}
			TopDocs pre = null;
			org.apache.lucene.search.Sort sort = getSort(query);
			if (sort != null) {
				pre = this.searcher.search(q, start, sort);
			} else {
				pre = this.searcher.search(q, start);
			}
			ScoreDoc startDoc = null;
			if (pre.totalHits <= start) {
				Integer in_curr = pre.scoreDocs.length / pageSize
						+ pre.scoreDocs.length % pageSize == 0 ? 0 : 1;
				return getResultFromDocs(this.searcher, pre, in_curr, pageSize);
			}
			
			if (start == 1 && pre.totalHits <= pageSize) {
				pre = this.searcher.search(q, pageSize);
				return getResultFromDocs(this.searcher, pre, start, pageSize);
			}

			if (pre.scoreDocs.length > 0) {
				startDoc = pre.scoreDocs[pre.scoreDocs.length - 1];
			}

			TopDocs hits = null;
			if (sort != null) {
				hits = this.searcher.searchAfter(startDoc, q, pageSize, sort);
			} else {
				hits = this.searcher.searchAfter(startDoc, q, pageSize);
			}

			return getResultFromDocs(this.searcher, hits, curr, pageSize);
		}

		public ResultPage searchLimit(Query query, Integer start, Integer limit)
				throws ParseException, IOException {
			if (start < 1 || limit <= 0) {
				return null;
			}
			Integer pageSize = limit;
			Integer curr = start / limit + 1;
			return search(query, curr, pageSize);
		}

		public <T> CommonPage<T> search(Query query, Integer curr,
				Integer pageSize, Class<T> c)
						throws ParseException, IOException {
			if (pageSize <= 0) {
				return null;
			}
			String squence = query.query();
			org.apache.lucene.search.Query q = this.parser.parse(squence);
			// org.apache.lucene.search.Query q = getQuery(query);
			if (q == null) {
				return null;
			}
			if (curr < 1) {
				curr = 1;
			}
			Integer start = (curr - 1) * pageSize;
			if (curr == 1) {
				start = 1;
			}

			TopDocs pre = null;
			org.apache.lucene.search.Sort sort = getSort(query);
			if (sort != null) {
				pre = this.searcher.search(q, start, sort);
			} else {
				pre = this.searcher.search(q, start);
			}
			ScoreDoc startDoc = null;
			if (pre.totalHits <= start) {
				Integer in_curr = pre.scoreDocs.length / pageSize
						+ pre.scoreDocs.length % pageSize == 0 ? 0 : 1;
				return getCommonFromDocs(this.searcher, pre, in_curr, pageSize,
						c);
			}
			
			if (start == 1 && pre.totalHits <= pageSize) {
				pre = this.searcher.search(q, pageSize);
				return getCommonFromDocs(this.searcher, pre, start, pageSize, c);
			}
			
			if (pre.scoreDocs.length > 0) {
				startDoc = pre.scoreDocs[pre.scoreDocs.length - 1];
			}

			TopDocs hits = null;
			if (sort != null) {
				hits = this.searcher.searchAfter(startDoc, q, pageSize, sort);
			} else {
				hits = this.searcher.searchAfter(startDoc, q, pageSize);
			}

			return getCommonFromDocs(this.searcher, hits, curr, pageSize, c);
		}

		public <T> CommonPage<T> searchLimit(Query query, Integer start,
				Integer limit, Class<T> c) throws ParseException, IOException {
			if (limit <= 0) {
				return null;
			}
			Integer pageSize = limit;
			Integer curr = start / limit + 1;
			return searchLimit(query, curr, pageSize, c);
		}

		@Deprecated
		public <T> CommonPage<T> fuzzy(String input, Integer curr,
				Integer pageSize, Class<T> c)
						throws ParseException, IOException {
			if (curr < 1 || pageSize <= 0 || !canFuzzy) {
				return null;
			}

			org.apache.lucene.search.Query q = fuzzyParser.parse(input);
			Integer start = (curr - 1) * pageSize;
			if (curr == 1) {
				start = 1;
			}
			TopDocs pre = this.searcher.search(q, start);
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

	private static String queryHandler(String info, String input)
			throws IOException {
		if (info == null || input == null) {
			return input;
		}
		input = input.trim();

		if (input.indexOf(" ") != -1) {
			StringBuilder builder = new StringBuilder();
			String[] ins = input.split(" ");
			for (String in : ins) {
				builder.append(" ");
				if (in.length() > 2) {
					if (in.startsWith("+")) {
						String lastIn = in.substring(1);
						if (isNumeric(lastIn)) {
							builder.append(
									"+" + info + ":" + in.substring(1) + "*");
						} else {
							builder.append("+" + info + ":\"" + in.substring(1)
									+ "\"");
						}
					} else if (in.startsWith("-")) {
						String lastIn = in.substring(1);
						if (isNumeric(lastIn)) {
							builder.append(
									"-" + info + ":" + in.substring(1) + "*");
						} else {
							builder.append("-" + info + ":\"" + in.substring(1)
									+ "\"");
						}
					} else {
						if (isNumeric(in)) {
							builder.append(info + ":" + in + "*");
						} else {
							builder.append(info + ":\"" + in + "\"");
						}
					}
				} else {
					builder.append(info + ":" + in);
				}
			}
			String res = builder.toString().trim();
			return res;
		} else {
			// 分词
			// String[] tokens = TokenTools.complexTokens(input);
			String[] tokens = TokenTools.complexTokens(input);
			String[] groups = TokenTools.complexGroup(input);
			StringBuilder append = new StringBuilder();
			for (String token : tokens) {
				if (isNumeric(token)) {
					append.append(" +" + info + ":" + token + "*");
				} else {
					append.append(" +" + info + ":\"" + token + "\"");
				}
			}

			for (String gt : groups) {
				if (isNumeric(gt)) {
					append.append(" " + info + ":" + gt + "*");
				} else {
					append.append(" " + info + ":\"" + gt + "\"");
				}
			}

			return append.toString().trim();
		}
	}

	private static <T> CommonPage<T> getCommonFromDocs(IndexSearcher searcher,
			TopDocs hits, Integer curr, Integer pageSize, Class<T> c)
					throws IOException {
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

	private static ResultPage getResultFromDocs(IndexSearcher searcher,
			TopDocs hits, Integer curr, Integer pageSize) throws IOException {
		ScoreDoc[] scoreDocs = hits.scoreDocs;
		JSONArray array = new JSONArray();

		for (ScoreDoc doc : scoreDocs) {
			Document document = searcher.doc(doc.doc);
			JSONObject object = new JSONObject();
			List<IndexableField> fields = document.getFields();
			for (IndexableField field : fields) {
				object.put(field.name(), field.stringValue());
			}
			array.add(object);
		}

		ResultPage rp = new ResultPage();
		rp.setTotal(hits.totalHits);
		rp.setCurr(curr);
		rp.setPageSize(pageSize);
		rp.setArray(array);
		return rp;
	}

	@SuppressWarnings("unused")
	private static org.apache.lucene.search.Query getQuery(Query query) {
		if (query.keySize() >= 1) {
			BooleanQuery bq = new BooleanQuery();
			Iterator<Key> it = query.iterator();
			while (it.hasNext()) {
				Key key = it.next();
				String control = key.getControl();
				org.apache.lucene.search.Query q = key.getQuery();
				if ("+".equals(control)) {
					bq.add(q, Occur.MUST);
				} else if ("-".equals(control)) {
					bq.add(q, Occur.MUST_NOT);
				} else {
					bq.add(q, Occur.SHOULD);
				}
			}
			return bq;
		}
		return null;
	}

	private static org.apache.lucene.search.Sort getSort(Query query) {
		if (query.sortSize() >= 1) {
			SortField[] sfs = new SortField[query.sortSize()];
			Iterator<Sort> it = query.sortIterator();
			Integer si = 0;
			while (it.hasNext()) {
				Sort ss = it.next();
				String sn = ss.getSortName();
				SortType st = ss.geType();
				ValueParse parse = ss.getParse();
				sfs[si] = new SortField(sn, Type.valueOf(parse.name()),
						st.equals(SortType.DESC));
				si++;
			}
			org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(
					sfs);
			return sort;
		}
		return null;
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

	private static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
