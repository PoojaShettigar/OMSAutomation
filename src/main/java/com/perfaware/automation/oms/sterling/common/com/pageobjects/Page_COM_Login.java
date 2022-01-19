package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentFactory;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;
public class Page_COM_Login {
	private WebDriver driver;
	UIUtilities uiUtil;
	
	public Page_COM_Login(WebDriver driver) {
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
	
	@FindBy(xpath="//div[@class='idxLoginFrameButtonBar']//span[@role='button']")
	@CacheLookup
	private WebElement btnLogin;
	
	@FindBy(xpath="//span[@class='idxDialogIconText']")
	@CacheLookup
	private WebElement txtLoginError;

	
	@FindBy(xpath="//a[@title='Administrator']")
	@CacheLookup
	private WebElement lnkAdministrator;
	
	@FindBy(xpath="//tr[contains(@aria-label,'Logout')]//td[1]")
	@CacheLookup
	private WebElement lnkLogout;
	
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
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Unable to click on Login button due to exception: "+e);
			return null;
		}
		
	}
	
	public void captureLoginError() {
		//uiUtil.webElementGetText(txtLoginError,"Login error message");
		Assert.assertEquals(uiUtil.webElementGetText(txtLoginError,"Login error message"), "Invalid Credentials passed. Please try again...");
	}
	
	public WebElement lnkAdministrator() {
		return this.lnkAdministrator;
	}
	
	public WebElement lnkLogout() {
		return lnkLogout;
	}
	
}
