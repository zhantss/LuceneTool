package pro.zhantss.lucene.search.sort;

public interface Sort {
	
	public ValueParse getParse();
	
	public void setParse(ValueParse parse);
	
	public SortType geType();
	
	public void setType(SortType type);
	
	public String getSortName();
	
}
