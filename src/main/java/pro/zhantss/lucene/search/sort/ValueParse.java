package pro.zhantss.lucene.search.sort;

/**
 * 该枚举与{@link org.apache.lucene.search.SortField.Type} 值是绑定关系
 * 后续维护勿随意更改
 */
public enum ValueParse {
	
	SCORE,
	
	DOC,
	
	STRING,
	
	INT,
	
	FLOAT,
	
	LONG,
	
	DOUBLE,
	
	BYTES

}
