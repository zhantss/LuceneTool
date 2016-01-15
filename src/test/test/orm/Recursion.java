package test.orm;

import java.util.Date;
import java.util.List;

public class Recursion {
	
	private String name;
	
	private String idcard;
	
	private Date birthday;
	
	private Sex sex;
	
	private List<String> tool;
	
	private List<Float> score;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public Sex getSex() {
		return sex;
	}
	
	public void setSex(Sex sex) {
		this.sex = sex;
	}
	
	public List<String> getTool() {
		return tool;
	}
	
	public void setTool(List<String> tool) {
		this.tool = tool;
	}
	
	public List<Float> getScore() {
		return score;
	}

	public void setScore(List<Float> score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Recursion [name=" + name + ", idcard=" + idcard + ", birthday="
				+ birthday + ", sex=" + sex + ", tool=" + tool + ", score="
				+ score + "]";
	}

}
