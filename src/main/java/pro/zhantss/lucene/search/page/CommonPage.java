package pro.zhantss.lucene.search.page;

import java.util.List;

public class CommonPage<T> {
	
	private Integer total;
	
	private Integer pageSize;
	
	private Integer curr;
	
	private List<T> array;

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

	public List<T> getArray() {
		return array;
	}

	public void setArray(List<T> array) {
		this.array = array;
	}

}
