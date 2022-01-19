package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_CustomerSearch {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_CustomerSearch(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath = "//div[@uid='txt_customerFirstNameAdvanced']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxFirstName;
	
	@FindBy(xpath = "//div[@uid='txt_customerLastNameAdvanced']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxLastName;
	
	private By txtEmailAddress=By.xpath("//div[@uid='txt_customerEmailAdvanced']//input[@class='dijitReset dijitInputInner']");
	
	@FindBy(xpath = "//span[@uid='SST_SearchButton']//*[text()='Search']")
	@CacheLookup
	private WebElement btnSearch;
	
	@FindBy(xpath = "//span[contains(@uid,'nextBttn')]//*[text()='Next']")
	@CacheLookup
	private WebElement btnNext;
	
	@FindBy(xpath = "//a[text()='Create New Registered Customer']")
	@CacheLookup
	private WebElement lnkCreateNewRegCustomer;
	
	public WebElement txtbxFirstName() {
		return txtbxFirstName;
	}
	
	public WebElement txtbxLastName() {
		return txtbxLastName;
	}
	
	public void enterEmailAddress(String emailAddress) {
		jsUtil.elementClick(this.driver, txtEmailAddress, "Email Address");
		jsUtil.enterInput(this.driver, txtEmailAddress, emailAddress, "Email Address");

	}
	
	public void clickSearchButton() {
		uiUtil.elementClick(btnSearch, "Search");

	}
	
	public Page_AddProducts clickNextButton() {
		uiUtil.elementClickAction(this.driver, btnNext, "Next");
		return new Page_AddProducts(this.driver);
	}
	
	public WebElement lnkCreateNewRegCustomer() {
		return lnkCreateNewRegCustomer;
	}
}
