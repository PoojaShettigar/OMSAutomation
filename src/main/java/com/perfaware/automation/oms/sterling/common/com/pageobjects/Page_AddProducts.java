package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_AddProducts {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	
	public Page_AddProducts(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath = "//div[@uid='txt_ItemID']//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxProductId;
	
	@FindBy(xpath = "//span[@uid='add_item']//span[text()='Add']")
	@CacheLookup
	private WebElement btnAdd;
	
	@FindBy(xpath="//div[@uid='quantity']//div//input[@class='dijitReset dijitInputInner']")
	@CacheLookup
	private WebElement txtbxQuantity;
	
	@FindBy(xpath ="//span[@uid='Popup_btnNext']//span[@role='button']")
	@CacheLookup
	private WebElement btnApply;
	
	@FindBy(xpath="//div[@uid='updateButtonContainer']//span[@role='button']")
	@CacheLookup
	private WebElement btnSave;
	
	@FindBy(xpath = "//span[contains(@uid,'nextBttn')]//*[text()='Next']")
	@CacheLookup
	private WebElement btnNext;
	
	public void enterProductId(String productId) {
		uiUtil.enterInput(txtbxProductId, productId, "Product Id");
	}
	
	public void clickAddButton() {
		uiUtil.elementClick(btnAdd, "Add");
	}
	
	public void enterQuantity(String quantity) {
		uiUtil.enterInput(txtbxQuantity, quantity, "Quantity");
	}
	
	public void selectDeliveryMethods(String deliveryMethods) {
		System.out.println(deliveryMethods);
		String deliverMethod="//div[@uid='deliverytOptions']//label[text()='"+deliveryMethods+"']";
		//uiUtil.elementClick(this.driver.findElement(By.xpath(deliverMethod)), "deliveryMethods");	
		jsUtil.elementClick(this.driver, By.xpath(deliverMethod), "deliveryMethods");
	}
	
	public Page_FulfillmentSummary clickNextButton() {
		uiUtil.elementClickAction(this.driver, btnNext, "Next");
		return new Page_FulfillmentSummary(this.driver);
	}

}
