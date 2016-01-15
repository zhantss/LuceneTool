package pro.zhantss.lucene.search.page;

import com.alibaba.fastjson.JSONArray;

public class ResultPage {
	
	private Integer total;
	
	private Integer pageSize;
	
	private Integer curr;
	
	private JSONArray array;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurr() {
		return curr;
	}

	public void setCurr(Integer curr) {
		this.curr = curr;
	}

	public JSONArray getArray() {
		return array;
	}

	public void setArray(JSONArray array) {
		this.array = array;
	}
	
}
