package pro.zhantss.lucene.data.dbimport;

import java.util.List;

public class MergeConfig {
	
	private AnalyzerType analyzerType;
	
	private DirectoryConfig target;
	
	private List<DirectoryConfig> dcs;

	public DirectoryConfig getTarget() {
		return target;
	}

	public void setTarget(DirectoryConfig target) {
		this.target = target;
	}

	public List<DirectoryConfig> getDcs() {
		return dcs;
	}

	public void setDcs(List<DirectoryConfig> dcs) {
		this.dcs = dcs;
	}
	
	public AnalyzerType getAnalyzerType() {
		return analyzerType;
	}
	
	public void setAnalyzerType(AnalyzerType analyzerType) {
		this.analyzerType = analyzerType;
	}
	
}
