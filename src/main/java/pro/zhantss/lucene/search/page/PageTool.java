package pro.zhantss.lucene.search.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.alibaba.fastjson.JSONObject;

import pro.zhantss.lucene.orm.ORMConvert;
import pro.zhantss.lucene.search.Query;
import pro.zhantss.lucene.search.SearcherFactory.Searcher;

public class PageTool<T> {
	
	private final Searcher searcher;
	
	private final Query query;
	
	private final Integer pageSize;
	
	private final Class<T> cls;
	
	private ScoreDoc lastDoc;
	
	private CommonPage<T> currPage;
	
	private Integer currNumber;
	
	private Integer totalNumber;
	
	public PageTool(Searcher searcher, Query query, Integer pageSize, Class<T> cls) {
		this.searcher = searcher;
		this.query = query;
		this.pageSize = pageSize;
		this.cls = cls;
	}
	
	public CommonPage<T> curr() {
		return currPage;
	}
	
	private void toolInstall() throws ParseException, IOException {
		first();
		allHits();
	}
	
	private void allHits() {
		if (this.totalNumber == null) {
			Integer total = currPage.getTotal();
			this.totalNumber = total / pageSize + (total % pageSize == 0 ? 0 : 1);
		}
	}
	
	public CommonPage<T> first() throws ParseException, IOException {
		IndexSearcher is = searcher.getSearcher();
		String squence = query.query();
		org.apache.lucene.search.Query q = searcher.getParser().parse(squence);
		TopDocs hits = is.search(q, pageSize);
		ScoreDoc[] scoreDocs = hits.scoreDocs;
		this.currPage = docToPage(is, scoreDocs, hits.totalHits, 1, this.pageSize);
		this.lastDoc = scoreDocs[scoreDocs.length - 1];
		this.currNumber = 1;
		return curr();
	}
	
	public CommonPage<T> next() throws ParseException, IOException {
		if (this.currNumber * this.pageSize >= currPage.getTotal()) {
			return null;
		}
		IndexSearcher is = searcher.getSearcher();
		String squence = query.query();
		org.apache.lucene.search.Query q = searcher.getParser().parse(squence);
		TopDocs hits = is.searchAfter(this.lastDoc, q, pageSize);
		ScoreDoc[] scoreDocs = hits.scoreDocs;
		this.currPage = docToPage(is, scoreDocs, hits.totalHits, this.currNumber + 1, pageSize);
		this.lastDoc = scoreDocs[scoreDocs.length - 1];
		this.currNumber = this.currNumber + 1;
		return curr();
	}
	
	public CommonPage<T> last() throws ParseException, IOException {
		if (this.currNumber <= 1) {
			return null;
		}
		if (currNumber == 2) {
			return first();
		}
		IndexSearcher is = searcher.getSearcher();
		String squence = query.query();
		org.apache.lucene.search.Query q = searcher.getParser().parse(squence);
		Integer start = (this.currNumber - 2) * this.pageSize;
		TopDocs pre = is.search(q, start);
		ScoreDoc startDoc = null;
		if (pre.scoreDocs.length > 0) {
			startDoc = pre.scoreDocs[pre.scoreDocs.length - 1];
		}
		TopDocs hits = is.searchAfter(startDoc, q, pageSize);
		ScoreDoc[] scoreDocs = hits.scoreDocs;
		this.currPage = docToPage(is, scoreDocs, hits.totalHits, this.currNumber - 1, pageSize);
		this.lastDoc = scoreDocs[scoreDocs.length - 1];
		this.currNumber = this.currNumber - 1;
		return curr();
	}
	
	public CommonPage<T> seek(Integer number) throws ParseException, IOException {
		if (this.totalNumber == null) {
			toolInstall();
		}
		if (number == null || number <= 0 || number > this.totalNumber) {
			return null;
		}
		if (number == 1) {
			return first();
		}
		if (number == this.currNumber) {
			return curr();
		}
		IndexSearcher is = searcher.getSearcher();
		String squence = query.query();
		org.apache.lucene.search.Query q = searcher.getParser().parse(squence);
		Integer start = (number - 1) * this.pageSize;
		TopDocs pre = is.search(q, start);
		ScoreDoc startDoc = null;
		if (pre.scoreDocs.length > 0) {
			startDoc = pre.scoreDocs[pre.scoreDocs.length - 1];
		}
		TopDocs hits = is.searchAfter(startDoc, q, pageSize);
		ScoreDoc[] scoreDocs = hits.scoreDocs;
		this.currPage = docToPage(is, scoreDocs, hits.totalHits, this.currNumber - 1, pageSize);
		this.lastDoc = scoreDocs[scoreDocs.length - 1];
		this.currNumber = this.currNumber - 1;
		return curr();
	}
	
	public CommonPage<T> lastOnePage() throws ParseException, IOException {
		if (this.totalNumber == null) {
			toolInstall();
		}
		return seek(this.totalNumber);
	}
	
	private CommonPage<T> docToPage(IndexSearcher searcher, ScoreDoc[] scoreDocs, Integer total, Integer curr, Integer pageSize) throws IOException {
		List<T> array = new ArrayList<T>();
		for (ScoreDoc doc : scoreDocs) {
			Document document = searcher.doc(doc.doc);
			JSONObject object = new JSONObject();
			List<IndexableField> fields = document.getFields();
			for (IndexableField field : fields) {
				object.put(field.name(), field.stringValue());
			}
			ORMConvert<T> oc = new ORMConvert<T>();
			T item = oc.convert(this.cls, object);
			if (item != null) {
				array.add(item);
			}
		}
		
		CommonPage<T> page = new CommonPage<T>();
		page.setTotal(total);
		page.setCurr(curr);
		page.setPageSize(pageSize);
		page.setArray(array);
		return page;
	}

}
