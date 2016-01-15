package pro.zhantss.lucene.search.dir;

public class MemoryIndexConfig extends IndexConfig {

	public MemoryIndexConfig() {
		super();
		setLocation(DataLocation.MEMORY);
	}

	@Override
	public String serialCode() {
		return getLocation().toString();
	}

}
