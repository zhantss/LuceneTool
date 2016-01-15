package pro.zhantss.lucene.data.dbimport;

public class FieldConfig {
	
	private String cloumn;
	
	private String name;
	
	private Boolean segmented;
	
	private Boolean indexed;

	private Boolean stored;

	public String getCloumn() {
		return cloumn;
	}

	public void setCloumn(String cloumn) {
		this.cloumn = cloumn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSegmented() {
		return segmented;
	}

	public void setSegmented(Boolean segmented) {
		this.segmented = segmented;
	}

	public Boolean getIndexed() {
		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public Boolean getStored() {
		return stored;
	}

	public void setStored(Boolean stored) {
		this.stored = stored;
	}
	
}
