package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_Home {
	
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_Home(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	private By lnkCreateOrder=By.xpath("//div[@uid='bCreate']//a[text()='Create Order']");
	//private By drpEnterprise=By.xpath("//div[@uid='orgList']//input[@class='dijitReset dijitInputInner']");
	
	private By lnkCreateConsumer=By.xpath("//div[@uid='pnlCreateLinks']//div[@uid='bCreateConsumer']//a");
	
	@FindBy(xpath="//div[@uid='orgList']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement drpEnterprise;
	
	@FindBy(xpath="//span[text()='Apply']")
	@CacheLookup
	private WebElement btnApply;
	
	public String clickCreateOrder() {
		jsUtil.elementClick(this.driver, lnkCreateOrder, "Creare Order link");
		return uiUtil.getWindowHandle(this.driver);
	}
	
	public Page_CreateConsumer clickCreateConsumer() {
		jsUtil.elementClick(this.driver,lnkCreateConsumer , "Creare Consumer link");
		return new Page_CreateConsumer(this.driver);
	}
	
	public void selectEenterprise(String input) throws InterruptedException {
		drpEnterprise.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
		uiUtil.enterInput(drpEnterprise, input, "Enterprise");
		drpEnterprise.sendKeys(Keys.BACK_SPACE);
		uiUtil.waitForPageLoad(2);
	    drpEnterprise.sendKeys(Keys.ARROW_DOWN);
	    drpEnterprise.sendKeys(Keys.ENTER);		
	}
	
	public Page_CustomerSearch clickApply() {
		//uiUtil.elementClick(btnApply, "Apply");
		return new Page_CustomerSearch(this.driver);
	}
}
