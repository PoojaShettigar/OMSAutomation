package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_PaymentConfirmation {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_PaymentConfirmation(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//span[@uid='extn_addPaymentMethod']//span[@role='button']")
	@CacheLookup
	private WebElement btnAddPaymentMethod;
	
	@FindBy(xpath="//span[@uid='extn_StartPayment']//span[text()='Pay Using MercadoPago']")
	@CacheLookup
	private WebElement btnMercadoPago;
	
	@FindBy(xpath ="//span[@uid='Popup_btnNext']//span[@role='button']")
	@CacheLookup
	private WebElement btnApply;
	
	@FindBy(xpath="//span[contains(@uid,'confirmBttn')]//span[@role='button']")
	@CacheLookup
	private WebElement btnConfirm;
	
	@FindBy(xpath="//div[@uid='pnlPaymentCapture']//div[@uid='cmbPaymentType']//input[@role='textbox']")
	@CacheLookup
	private WebElement txtbPaymentType;
	
	@FindBy(xpath="//div[@uid='pnlPaymentCapture']//div[@uid='txtPaymentReference1']//input[@type='text']")
	@CacheLookup
	private WebElement txtbPaymentRef1;
	
	public void clickAddPaymentMethodButton() throws InterruptedException {
		uiUtil.elementClick(btnAddPaymentMethod, "Add Payment Method");
	}
	
	public Page_PaymentMercadoPago clickPayUsingMercadoPagoButton() throws InterruptedException {
		uiUtil.elementClick(btnMercadoPago, "Pay using MercadoPagoButton");
		uiUtil.waitForPageLoad(5);
		uiUtil.acceptAlertPopup(this.driver);
		uiUtil.waitForPageLoad(2);
		uiUtil.switchToChildWindow(this.driver);
		return new Page_PaymentMercadoPago(this.driver);
	}
	
	public void clickApplyButton() {
		uiUtil.elementClick(btnApply, "Apply");
	}
	
	public Page_OrderSummary clickConfirmButton() {
		uiUtil.elementClick(btnConfirm, "Confirm");
		return new Page_OrderSummary(this.driver);
	}
	
	public void enterPaymentType(String paymentType) {
		uiUtil.enterInput(txtbPaymentType, paymentType, "PaymentType");
		
	}
	public void enterPaymentReference(String paymentType) {
		
		uiUtil.enterInput(txtbPaymentRef1, paymentType, "PaymentReference");
		
	}
}
