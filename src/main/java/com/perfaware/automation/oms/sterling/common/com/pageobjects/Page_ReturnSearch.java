package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_ReturnSearch {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_ReturnSearch(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[contains(@uid,'cb_orderEnterprise')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxEnterpriseCode;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderReturnNo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxReturnOrderNo;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderNo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxOriginalOrderNo;
	
	@FindBy(xpath="//div[contains(@uid,'txt_returnOrderName')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxReturnName;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderDateFrom')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxReturnDateFrom;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderDateTo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxReturnrDateTo;
	
	@FindBy(xpath="//div[@uid='SST_OrderBy']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxOrderBy;
	
	@FindBy(xpath="//div[@uid='radDraftOrderFlag']//label[text()='Confirmed returns']")
	@CacheLookup
	private WebElement rbtnConfirmedReturns;
	
	@FindBy(xpath="//div[@uid='radDraftOrderFlag']//label[text()='Draft returns']")
	@CacheLookup
	private WebElement rbtnDraftReturns;
	
	@FindBy(xpath="//div[@uid='radReadFromHistory']//label[text()='Recent returns']")
	@CacheLookup
	private WebElement rbtnRecentReturns;
	
	@FindBy(xpath="//div[@uid='radReadFromHistory']//label[text()='Archived returns']")
	@CacheLookup
	private WebElement rbtnArchivedReturns;
	
	@FindBy(xpath="//div[contains(@uid,'txt_custEmailId')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxEmailAddress;
	
	@FindBy(xpath="//div[contains(@uid,'txt_custPhone')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxCustTelephone;
	
	@FindBy(xpath="//div[contains(@uid,'txt_fName')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxFirstname;
	
	@FindBy(xpath="//div[contains(@uid,'txt_lName')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxLastname;
	
	@FindBy(xpath="//div[contains(@uid,'txt_postalCode')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxPostalCode;
	
	@FindBy(xpath="//div[contains(@uid,'txtDisplayCreditCardNo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxLastFourDigitCC;
	
	@FindBy(xpath="//span[@uid='SST_SearchButton']//span[@role='button']")
	@CacheLookup
	private WebElement rbtnSearch;
	
	public void enterEnterpriseCode(String enterpriseCode) {
		uiUtil.enterInput(txtbxEnterpriseCode, enterpriseCode, "Enterprise Code");
	}
	
	public void enterReturnOrderNumber(String retunrOrderNumber) {
		uiUtil.enterInput(txtbxOriginalOrderNo, retunrOrderNumber, "Return OrderNumber");
	}
	
	public void enterOriginalOrderNumber(String originalOrderNumber) {
		uiUtil.enterInput(txtbxOriginalOrderNo, originalOrderNumber, "Original OrderNumber");
	}
	
	public void enterReturnrDateFrom(String returnDateFrom) {
		uiUtil.enterInput(txtbxReturnDateFrom, returnDateFrom, "ReturnOrder Date From");
	}
	
	public void enterReturnDateTo(String returnDateTo) {
		uiUtil.enterInput(txtbxReturnrDateTo, returnDateTo, "ReturnOrder Date To");
	}
	
	public void enterReturnName(String returnName) {
		uiUtil.enterInput(txtbxReturnName, returnName, "Return name");
	}
	
	public void enterOrderBy(String orderBy) {
		uiUtil.enterInput(txtbxOrderBy, orderBy, "orderBy ");
	}
	
	public void clickConfirmedReturns() {
		uiUtil.elementClick(rbtnConfirmedReturns , "ConfirmedReturns radio button");
	}
	
	public void clickDraftdReturns() {
		uiUtil.elementClick(rbtnDraftReturns , "DraftReturns radio button");
	}
	
	public void clickRecentReturns() {
		uiUtil.elementClick(rbtnRecentReturns , "RecentReturns radio button");
	}
	
	public void clickArchivedReturns() {
		uiUtil.elementClick(rbtnArchivedReturns , "ArchivedReturns radio button");
	}
	
	public void enterEmailAddress(String emailAddress) {
		uiUtil.enterInput(txtbxEmailAddress, emailAddress, "Email Address");
	}
	
	public void enterCustomerTelephone(String customerTelephone) {
		uiUtil.enterInput(txtbxEmailAddress, customerTelephone, "Customer Telephone");
	}
	
	public void enterFirstName(String firstName) {
		uiUtil.enterInput(txtbxFirstname, firstName, "First name");
	}
	
	public void enterLastName(String lastName) {
		uiUtil.enterInput(txtbxLastname, lastName, "Last name");
	}
	
	public void enterPostalCode(String postalCode) {
		uiUtil.enterInput(txtbxPostalCode, postalCode, "Postal Code");
	}
	
	public void enterLastFourDigitCreditCardNo(String creditCardNo) {
		uiUtil.enterInput(txtbxLastFourDigitCC, creditCardNo, "Last four digits of credit card");
	}

	public void clickSearch() {
		uiUtil.elementClick(rbtnSearch , "Search button");
	}
}
