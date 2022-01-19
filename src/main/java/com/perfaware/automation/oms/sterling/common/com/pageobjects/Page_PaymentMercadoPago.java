package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_PaymentMercadoPago {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_PaymentMercadoPago(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//form[@id='group_form_scroller']/ul[2]/li[1]/label/div")
	@CacheLookup
	private WebElement lnkCreditCard;
	
	@FindBy(xpath="//input[@id='card_number']")
	@CacheLookup
	private WebElement txtbxCreditCardNumber;
	
	@FindBy(xpath="//input[@id='input_expiration_date']")
	@CacheLookup
	private WebElement txtbxExpirationDate;
	
	@FindBy(xpath="//input[@id='fullname']")
	@CacheLookup
	private WebElement txtbxFullname;
	
	@FindBy(xpath="//input[@id='cvv']")
	@CacheLookup
	private WebElement txtbxCvv;
	
	@FindBy(xpath="//button[@id='submit']")
	@CacheLookup
	private WebElement btnSubmit;
	
	@FindBy(xpath="//input[@id='number']")
	@CacheLookup
	private WebElement txtbxRut;
	
	@FindBy(xpath="//*[@id='select_installments']/ul/li[1]/label/div")
	@CacheLookup
	private WebElement lnkInstallment;
	
	@FindBy(xpath="//input[@id='email']")
	@CacheLookup
	private WebElement txtbxEmail;
	
	@FindBy(xpath="//button[@id='pay']")
	@CacheLookup
	private WebElement btnPay;
	
	By btnContinue= By.xpath("//a[@id='continue_button']");
	
	public void clickCreditcardLink() {
		uiUtil.elementClick(lnkCreditCard, "Creditcard link");
	}
	
	public void enterCreditcardNumber(String creditcardNumber) {
		uiUtil.enterInput(txtbxCreditCardNumber, creditcardNumber, "Creditcard Number");
	}
	
	public void enterExpirationDate(String date) {
		uiUtil.enterInput(txtbxExpirationDate, date, "Expiration Date");
	}
	
	public void enterFullname(String name) {
		uiUtil.enterInput(txtbxFullname,name, "Fullname");
	}
	
	public void enterCvv(String cvv) {
		uiUtil.enterInput(txtbxCvv,cvv, "CVV");
	}
	
	public void clickSubmitButton() {
		uiUtil.elementClick(btnSubmit, "Submit button");
	}
	
	public void clickSubmit() {
		uiUtil.elementClick(btnSubmit, "Submit button");
	}
	public void enterRut(String rut) {
		uiUtil.enterInput(txtbxRut,rut, "RUT");
		uiUtil.enterKeys(txtbxRut, Keys.RETURN);
		
	}
	
	public void clickInstallments() {
		uiUtil.elementClick(lnkInstallment	, "Installments");
	}
	
	public void enterEmail(String email) {
		uiUtil.enterInput(txtbxEmail,email, "Email");
	}
	
	public void clickPay() {
		uiUtil.elementClick(btnPay,"Pay Button");
	}
	
	public void clickContinue(String parentId) throws InterruptedException {
		jsUtil.elementClick(this.driver, btnContinue, "Continue Button");
		uiUtil.waitForPageLoad(6);
		String url=driver.getCurrentUrl();
		url=url.replace("dev.socoomni.com", "localhost:9443");
		uiUtil.switchToParentWindow(this.driver, parentId);
		jsUtil.openNewTab(url);
	}
	

}
