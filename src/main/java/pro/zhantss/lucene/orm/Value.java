package pro.zhantss.lucene.orm;

import pro.zhantss.lucene.orm.convert.Converter;

public class Value {
	
	private String name;
	
	private String indexName;
	
	private String className;
	
	private Converter converter;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}
	
}
