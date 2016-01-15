package pro.zhantss.lucene.data.dbimport;

import java.util.List;

public class EntityConfig {
	
	private String name;
	
	private String query;
	
	private DirectoryConfig directory;
	
	private List<FieldConfig> fields;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<FieldConfig> getFields() {
		return fields;
	}

	public void setFields(List<FieldConfig> fields) {
		this.fields = fields;
	}

	public DirectoryConfig getDirectory() {
		return directory;
	}

	public void setDirectory(DirectoryConfig directory) {
		this.directory = directory;
	}

	@Override
	public String toString() {
		return "EntityConfig [name=" + name + ", query=" + query
				+ ", directory=" + directory + ", fields=" + fields + "]";
	}
	
}
