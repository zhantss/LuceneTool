package pro.zhantss.lucene.search.key;

import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import pro.zhantss.lucene.data.Resources;
import pro.zhantss.lucene.search.sort.Sort;
import pro.zhantss.lucene.search.sort.SortType;
import pro.zhantss.lucene.search.sort.ValueParse;

public class Term implements Key, Sort {
	
	private String name;
	
	private String exp;

	private String control;
	
	private ValueParse parse;
	
	private SortType type;
	
	public Term(String name, String exp) {
		this.name = name;
		this.exp = exp;
	}
	
	public Term(String name, ValueParse parse) {
		this.name = name;
		this.parse = parse;
	}

	@Override
	public void setControl(String control) {
		this.control = control;
	}

	@Override
	public String getControl() {
		return control;
	}

	@Override
	public String key() {
		return control + name + ":" + "\"" + exp + "\"";
	}
	
	@Override
	public SortType geType() {
		return this.type;
	}

	@Override
	public void setType(SortType type) {
		this.type = type;
	}

	@Override
	public String getSortName() {
		return this.name;
	}

	@Override
	public ValueParse getParse() {
		return this.parse;
	}

	@Override
	public void setParse(ValueParse parse) {
		this.parse = parse;
	}
	
	public Query getQuery() {
		if (exp.endsWith("*")) {
			PrefixQuery query = new PrefixQuery(new org.apache.lucene.index.Term(name, exp.substring(0, exp.length() - 1)));
			return query;
		}
		
		if (exp.startsWith("*") || exp.indexOf("?") != -1) {
			WildcardQuery query = new WildcardQuery(new org.apache.lucene.index.Term(name, exp));
			return query;
		}
		
		if (exp.indexOf("*") != -1) {
			String[] exps = exp.split("\\*");
			PhraseQuery query = new PhraseQuery();
			for (String e : exps) {
				query.add(new org.apache.lucene.index.Term(name, e));
			}
			query.setSlop(Resources.stop);
			return query;
		}
		
		return new TermQuery(new org.apache.lucene.index.Term(name, exp));
	}

}
