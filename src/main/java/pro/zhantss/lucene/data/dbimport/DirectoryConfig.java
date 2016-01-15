package pro.zhantss.lucene.data.dbimport;

public class DirectoryConfig {
	
	private String name;
	
	private DirectoryType type;
	
	private Boolean clean = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DirectoryType getType() {
		return type;
	}

	public void setType(DirectoryType type) {
		this.type = type;
	}
	
	public Boolean getClean() {
		return clean;
	}
	
	public void setClean(Boolean clean) {
		this.clean = clean;
	}
	
}
