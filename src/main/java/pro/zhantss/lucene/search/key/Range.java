package pro.zhantss.lucene.search.key;

import org.apache.lucene.search.Query;

public class Range implements Key {
	
	private String name;
	
	private String start;
	
	private String end;
	
	private String control;
	
	public Range(String name, String start, String end) {
		this.name = name;
		this.start = start.replaceAll("\\s", "");
		this.end = end.replaceAll("\\s", "");
	}

	@Override
	public void setControl(String control) {
		this.control = control;
	}

	@Override
	public String getControl() {
		return this.control;
	}

	@Override
	public String key() {
		return control + name + "{" + start + " TO " + end + "}";
	}

	@Override
	public Query getQuery() {
		// TODO 自动生成的方法存根
		return null;
	}

}
