package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_OrderSearch {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_OrderSearch(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[contains(@uid,'cb_orderEnterprise')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxEnterpriseCode;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderNo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxOrderNo;
	
	@FindBy(xpath="//div[contains(@uid,'txt_custPoNo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxCustPONo;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderDateFrom')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxOrderDateFrom;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderDateTo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxOrderDateTo;
	
	@FindBy(xpath="//div[contains(@uid,'txt_orderDateTo')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxHoldTypes;
	
	@FindBy(xpath="//div[@uid='SST_OrderBy']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxOrderBy;
	
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
	
	@FindBy(xpath="//div[contains(@uid,'txt_postalCode')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement rbtnConfirmedorder;
	
	@FindBy(xpath="//div[contains(@uid,'txt_postalCode')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement rbtnDraftOrder;
	
	@FindBy(xpath="//div[contains(@uid,'txt_postalCode')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement rbtnRecentOrder;
	
	@FindBy(xpath="//div[contains(@uid,'txt_postalCode')]//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement rbtnArchivedOrder;
	
	public void enterEnterpriseCode(String enterpriseCode) {
		uiUtil.enterInput(txtbxEnterpriseCode, enterpriseCode, "Enterprise Code");
	}
		
	public void enterOrderNumber(String orderNumber) {
		uiUtil.enterInput(txtbxOrderNo, orderNumber, "OrderNumber");
	}
	
	public void enterCustomerPONo(String customerPONo) {
		uiUtil.enterInput(txtbxCustPONo, customerPONo, "CustomerPONo");
	}
	
	public void enterOrderDateFrom(String orderDateFrom) {
		uiUtil.enterInput(txtbxOrderDateFrom, orderDateFrom, "Order Date From");
	}
	
	public void enterOrderDateTo(String orderDateTo) {
		uiUtil.enterInput(txtbxOrderDateTo, orderDateTo, "Order Date To");
	}
	
	public void enterHoldTypes(String holdTypes) {
		uiUtil.enterInput(txtbxHoldTypes, holdTypes, "Hold Types");
	}
	
	public void enterOrderBy(String orderBy) {
		uiUtil.enterInput(txtbxOrderBy, orderBy, "orderBy ");
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
	
	public void clickConfirmedOrder() {
		uiUtil.elementClick(rbtnConfirmedorder , "ConfirmedOrder radio button");
	}
	
	public void clickDraftdOrder() {
		uiUtil.elementClick(rbtnDraftOrder , "DraftOrder radio button");
	}
	
	public void clickRecentOrder() {
		uiUtil.elementClick(rbtnRecentOrder , "RecentOrder radio button");
	}
	
	public void clickArchivedOrder() {
		uiUtil.elementClick(rbtnArchivedOrder , "ArchivedOrder radio button");
	}
}
