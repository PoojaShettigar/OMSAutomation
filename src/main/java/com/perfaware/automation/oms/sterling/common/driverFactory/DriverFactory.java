package com.perfaware.automation.oms.sterling.common.driverFactory;

import org.openqa.selenium.WebDriver;

public class DriverFactory {
	//Singleton design Pattern
	DriverFactory() {
		
	}
	private static  DriverFactory instance  = new DriverFactory();
	public static  DriverFactory getInstance() {
		return instance;
	}
	
	
	//factory design pattern --> define separate factory methods for creating objects and create objects by calling that methods
	ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
	
	public WebDriver getDriver() {
		return this.driver.get();
	}
	public  void setDriver(WebDriver driverParm) {
		this.driver.set(driverParm);
	}
	public void closeBrowser() {
		this.driver.get().close();
		this.driver.get().quit();
		this.driver.remove();
	}
}
