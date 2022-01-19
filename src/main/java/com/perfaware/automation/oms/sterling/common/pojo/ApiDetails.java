package com.perfaware.automation.oms.sterling.common.pojo;

/**
 * Class description: POJO class to read / set API specific data
 * @author Perfaware
 *
 */
public class ApiDetails {

	private String baseURL;
	private String userName;
	private String password;
	private String resource;

	public ApiDetails() {

	}

	public ApiDetails(String baseURL,String userName, String password,String resource) {
		this.baseURL=baseURL;
		this.userName=userName;
		this.password=password;
		this.resource=resource;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

}