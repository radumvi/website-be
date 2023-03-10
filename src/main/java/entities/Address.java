package entities;

/**
 * Class that models the address of a user.
 */
public class Address {
	
	private int id;
	private int userId;
	private String county;
	private String city;
	private String exactAddress;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getExactAddress() {
		return exactAddress;
	}
	public void setExactAddress(String exactAddress) {
		this.exactAddress = exactAddress;
	}
}
