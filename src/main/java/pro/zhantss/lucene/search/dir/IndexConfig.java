package pro.zhantss.lucene.search.dir;

public abstract class IndexConfig {
	
	private DataLocation location;
	
	public abstract String serialCode();
	
	public DataLocation getLocation() {
		return location;
	}
	
	public void setLocation(DataLocation location) {
		this.location = location;
	}
	
	@Override
	public int hashCode() {
		return serialCode().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IndexConfig) {
			IndexConfig ic = (IndexConfig) obj;
			return serialCode().equals(ic.serialCode());
		}
		return false;
	}
	
}
