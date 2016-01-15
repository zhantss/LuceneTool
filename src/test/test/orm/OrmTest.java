package test.orm;

public class OrmTest {
	
	private String province;
	
	private String city;
	
	private Recursion recursion;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	
	public Recursion getRecursion() {
		return recursion;
	}
	
	public void setRecursion(Recursion recursion) {
		this.recursion = recursion;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "OrmTest [province=" + province + ", city=" + city
				+ ", recursion=" + recursion + "]";
	}
	
}
