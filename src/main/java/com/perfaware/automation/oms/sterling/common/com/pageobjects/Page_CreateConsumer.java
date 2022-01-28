package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_CreateConsumer {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_CreateConsumer(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@uid='txtFirstName']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtFirstName;
	
	@FindBy(xpath="//div[@uid='txtMiddleName']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtMiddleName;
	
	@FindBy(xpath="//div[@uid='txtLastName']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtLastName;
	
	@FindBy(xpath="//div[@uid='txtEmailAddress']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtEmailAddress;
	
	//private By txtEmailAddress=By.xpath("//div[@uid='txtEmailAddress']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']");
	
	@FindBy(xpath="//div[@uid='txtMobilePhone']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtMobilePhone ;
	
	//private By txtMobilePhone=By.xpath("//div[@uid='txtMobilePhone']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']");
	
	@FindBy(xpath="//div[@uid='extn_RUTID']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtRUTID ;
	
	//private By txtRUTID=By.xpath("//div[@uid='extn_RUTID']//div[@class='dijitInline']//input[@class='dijitReset dijitInputInner']");

	@FindBy(xpath="//div[@uid='extn_checkboxLoyalty']//input[@class='dijitReset dijitCheckBoxInput']")
	@CacheLookup
	private WebElement chkAcceptCheckbox;
	
	
	@FindBy(xpath="//span[@uid='confirmBttn2']//span[@data-dojo-attach-point='containerNode']")
	@CacheLookup
	private WebElement btnConfirm;
	
	@FindBy(xpath="//div[contains(@uid,'singlemessagelabel')]")
	@CacheLookup
	private WebElement lblAlertMessage;
	
	public void enterFirstName(String firstname) {
		uiUtil.elementClick(txtFirstName, "FirstName");
		uiUtil.enterInput(txtFirstName, firstname,"First Name");

	}
	
	public void enterMiddleName(String middlename) {
		uiUtil.elementClick(txtMiddleName, "MiddleName");
		uiUtil.enterInput(txtMiddleName, middlename,"Middle Name");

	}
	
	public void enterLastName(String lastname) {
		uiUtil.elementClick(txtLastName, "LastName");
		uiUtil.enterInput(txtLastName, lastname,"Last Name");

	}
	
	public void enterEmailAddress(String emailAddress) {
		uiUtil.elementClick(txtEmailAddress, "Email Address");
		uiUtil.enterInput(txtEmailAddress, emailAddress, "Email Address");

	}
	
	public void enterMobilePhone(String MobilePhone) {
		uiUtil.elementClick(txtMobilePhone, "Mobile Phone");
		uiUtil.enterInput(txtMobilePhone,MobilePhone, "Mobile Phone");

	}
	
	public void enterRUTID(String RUTID) {
		uiUtil.elementClick(txtRUTID, "RUTID");
		uiUtil.enterInput(txtRUTID,RUTID, "RUTID");

	}
	
	public void clickAcceptCheckbox() {
		uiUtil.elementClick(chkAcceptCheckbox, "Accept Check Box");	
	}
		
		public Page_CustomerDetails clickConfirmButton() {
			uiUtil.elementClick(btnConfirm,"Confirm");
			uiUtil.acceptAlertPopup(this.driver);
			return new Page_CustomerDetails(this.driver);
			
		}
}
