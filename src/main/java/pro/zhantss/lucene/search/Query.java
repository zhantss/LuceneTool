package pro.zhantss.lucene.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pro.zhantss.lucene.search.key.Key;
import pro.zhantss.lucene.search.sort.Sort;
import pro.zhantss.lucene.search.sort.SortType;

public class Query implements Iterable<Key> {
	
	private List<Key> keys = new ArrayList<Key>();
	
	private List<Sort> sorts = new ArrayList<Sort>();

	@Override
	public Iterator<Key> iterator() {
		return keys.iterator();
	}
	
	public Iterator<Sort> sortIterator() {
		return sorts.iterator();
	}
	
	public Integer keySize() {
		return this.keys.size();
	}
	
	public Integer sortSize() {
		return this.sorts.size();
	}
	
	public Query sort(Sort sort, SortType type) {
		sort.setType(type);
		sorts.add(sort);
		return this;
	}
	
	public Query asc(Sort sort) {
		sort.setType(SortType.ASC);
		sorts.add(sort);
		return this;
	}
	
	public Query desc(Sort sort) {
		sort.setType(SortType.ASC);
		sorts.add(sort);
		return this;
	}
	
	public Query must(Key key) {
		key.setControl("+");
		keys.add(key);
		return this;
	}
	
	public Query not(Key key) {
		key.setControl("-");
		keys.add(key);
		return this;
	}
	
	public Query should(Key key) {
		key.setControl("");
		keys.add(key);
		return this;
	}
	
	public String query() {
		StringBuilder builder = new StringBuilder();
		Iterator<Key> it = this.iterator();
		while(it.hasNext()) {
			Key key = it.next();
			builder.append(key.key() + " ");
		}
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return query();
	}

}
