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
import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;
public class Page_CustomerDetails {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_CustomerDetails(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//span[contains(@uid,'lblContactName')]")
	@CacheLookup
	private WebElement lblContactName;
	
	@FindBy(xpath="//div[contains(@uid,'lnkEmailAddress')]//div[@class='scLinkAlignment dijitInline']//div[@role='presentation']//a[@class='idxLinkDerived idxLink']")
	@CacheLookup
	private WebElement lblEmailaddress;
	
	@FindBy(xpath="//div[contains(@uid,'lblMobilePhone')]//div[@class='dijitReset dijitInputField dijitInputContainer']//span[@class='scDataValue']")
	@CacheLookup
	private WebElement lblMobilePhone;
	
	@FindBy(xpath="//div[contains(@uid,'extn_RUTID')]//span[@class='scDataValue']")
	@CacheLookup
	private WebElement lblRUTID;
	
	public void captureContactName() {
		uiUtil.webElementGetText(lblContactName,"Contact Name");
	}
	
	public void captureEmailaddress() {
		uiUtil.webElementGetText(lblEmailaddress,"Email address");
	}
	
	public void captureMobilePhone() {
		uiUtil.webElementGetText(lblMobilePhone,"Mobile Phone");
	}
	
	public void captureRUTID() {
		uiUtil.webElementGetText(lblRUTID,"RUT ID");
	}
}