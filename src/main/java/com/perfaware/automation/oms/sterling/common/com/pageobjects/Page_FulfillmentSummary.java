package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_FulfillmentSummary {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_FulfillmentSummary(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	private By btnShippingOptions=By.xpath("//span[text()='Shipping options']");
	private By btnSelectStore=By.xpath("//span[@uid='extn_PickLinesStoreSelect']//input");
	private By tdstore=By.xpath("//div[@uid='extn_gridStore']//td");
	private String checkbox="//div[@uid='SlotGrid']//div[@class='gridxRowHeaderBody']//span[@role='checkbox']";
	
	@FindBy(xpath = "//span[contains(@uid,'nextBttn')]//*[text()='Next']")
	@CacheLookup
	private WebElement btnNext;
	
	@FindBy(xpath="//span[@uid='Popup_btnNext']//span[text()='Save']")
	@CacheLookup
	private WebElement btnSave;
	
	public void clickShippingOptionsButton() {
		jsUtil.elementClick(this.driver, btnShippingOptions, "Shipping Options");
		//uiUtil.elementClick(btnShippingOptions, "Shipping Options");
	}
	
	public void clickStoregOptionsButton() {
		jsUtil.elementClick(this.driver, btnSelectStore, "Shipping Options");
		//uiUtil.elementClick(btnSelectStore, "Shipping Options");
	}
	
	public void selectStoregOptionsButton() {
		jsUtil.elementClick(this.driver, tdstore, "Store Options");
		//uiUtil.elementClick(btnSelectStore, "Shipping Options");
	}
	
	public void selectShippingOptions(String shippingOptions) {
		List<WebElement> options = this.driver.findElements(By.xpath(checkbox));
		System.out.println(options.size());
		System.out.println(shippingOptions);
		uiUtil.elementClick(options.get(0), "SameDay Shipping option");
//		if(shippingOptions.equalsIgnoreCase("SameDay")) {
//			System.out.println(options.get(0).getText());
//			uiUtil.elementClick(options.get(0), "SameDay Shipping option");
//		}
//		else if(shippingOptions.equalsIgnoreCase("NextDay")) {
//			uiUtil.elementClick(options.get(1), "NextDay Shipping option");
//		}
//		else {
//			System.out.println("No options available");
//			ExtentFactory.getInstance().getExtent().log(Status.FAIL, shippingOptions+" is not visible.");
//		}
		
	}
	
	public void clickSaveButton() {
		uiUtil.elementClick(btnSave, "Save");
	}
	
	public Page_PaymentConfirmation clickNextButton() {
		uiUtil.elementClickAction(this.driver, btnNext, "Next");
		return new Page_PaymentConfirmation(this.driver);
	}
	
}

