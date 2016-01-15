package pro.zhantss.lucene.search;

public class TermInfo {
	
	private Integer start;

	private Integer end;

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public TermInfo plus(TermInfo info) {
		if (this.start > info.getEnd() || this.end < info.getStart()
				|| (this.start >= info.getStart()
						&& this.end <= info.getEnd())
				|| (info.getStart() >= this.start
						&& info.getEnd() <= this.end)) {
			return null;
		}
		
		Integer start = this.start >= info.getStart() ? info.getStart() : this.start;
		Integer end = this.end <= info.getEnd() ? info.getEnd() : this.end;
		
		TermInfo ti = new TermInfo();
		ti.setStart(start);
		ti.setEnd(end);
		return ti;
	}

}
