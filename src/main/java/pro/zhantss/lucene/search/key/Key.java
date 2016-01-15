package pro.zhantss.lucene.search.key;

import org.apache.lucene.search.Query;

public interface Key {
	
	public void setControl(String control);
	
	public String getControl();
	
	public String key();
	
	public Query getQuery();
	
}
