package com.perfaware.automation.oms.sterling.common.som.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_Home;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_SOM_Login {
	private WebDriver driver;
	UIUtilities uiUtil;
	
	public Page_SOM_Login(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "username")
	@CacheLookup
	private WebElement txtbxUserName;
	
	@FindBy(name="password")
	@CacheLookup
	private WebElement txtbxPassword;
	
	@FindBy(xpath="//span[@id='loginBtn' and text()='Login']")
	@CacheLookup
	private WebElement btnLogin;
	
	public void enterUsername(String username) {
		uiUtil.enterInput(txtbxUserName, username, "Username");
		
	}
	
	public void enterPassword(String password) {
		uiUtil.enterInput(txtbxPassword, password, "Password");
	}
	
	public Page_Home clickLogin() {
		try {
			uiUtil.elementClick(btnLogin, "Login");
			return new Page_Home(this.driver);
		} catch (Exception e) {
			//log failure in extent
			//ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Unable to click on Login button due to exception: "+e);
			return null;
		}		
	}
}
