package pro.zhantss.lucene.orm;

import java.util.HashMap;

public class Target {
	
	private String name;
	
	private String className;
	
	private HashMap<String, Value> infos;
	
	public Target(String name, String className) {
		this.name = name;
		this.className = className;
		infos = new HashMap<String, Value>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void putInfo(Value value) {
		infos.put(value.getName(), value);
	}
	
	public Value getInfoByName(String name) {
		return infos.get(name);
	}

}
