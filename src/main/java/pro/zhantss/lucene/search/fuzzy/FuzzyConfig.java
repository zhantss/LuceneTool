package pro.zhantss.lucene.search.fuzzy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FuzzyConfig extends HashMap<String, Float>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean change = false;
	
	private String[] fields = null;
	
	public Boolean getChange() {
		return change;
	}
	
	public String[] getFields() {
		if (change) {
			Set<String> keys = this.keySet();
			this.fields = (String[]) keys.toArray(new String[]{});
			change = false;
		}
		return fields;
	}
	
	private void change() {
		change = true;
	}
	
	@Override
	public Float put(String key, Float value) {
		change();
		return super.put(key, value);
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Float> m) {
		change();
		super.putAll(m);
	}
	
	@Override
	public Float remove(Object key) {
		change();
		return super.remove(key);
	}

}
