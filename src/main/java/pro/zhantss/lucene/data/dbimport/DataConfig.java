package pro.zhantss.lucene.data.dbimport;

import java.util.List;

public class DataConfig {
	
	private DataSourceConfig dsc;
	
	private AnalyzerType analyzerType;
	
	private List<EntityConfig> entities;

	public DataSourceConfig getDsc() {
		return dsc;
	}

	public void setDsc(DataSourceConfig dsc) {
		this.dsc = dsc;
	}

	public List<EntityConfig> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityConfig> entities) {
		this.entities = entities;
	}

	public AnalyzerType getAnalyzerType() {
		return analyzerType;
	}

	public void setAnalyzerType(AnalyzerType analyzerType) {
		this.analyzerType = analyzerType;
	}

}
