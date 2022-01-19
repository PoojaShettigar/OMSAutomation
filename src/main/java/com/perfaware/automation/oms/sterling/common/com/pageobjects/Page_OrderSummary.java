package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentFactory;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_OrderSummary {

	private WebDriver driver;
	UIUtilities uiUtil;
	
	public Page_OrderSummary(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//span[@uid='lblOrderNo']")
	@CacheLookup
	private WebElement txtOrderNo;

	@FindBy(xpath="//a[text()='View Order Summary']")
	@CacheLookup
	private WebElement lnkViewOrderSummary;

	@FindBy(xpath="//span[@uid='lblStatus']")
	@CacheLookup
	private WebElement txtOrderStauts;
	
	public String captureOrderNo() {
		String orderNo= uiUtil.webElementGetText(txtOrderNo, "OrderNumber");
		ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(orderNo, ExtentColor.BLUE));
		return orderNo;
		
	}
	
	public void clickViewOrderSummary() {
		uiUtil.elementClick(lnkViewOrderSummary, "ViewOrderSummary");
	}
	
	public void captureOrderStatus() {
		String orderStatus="";
		for(int i=0; i<=2;i++){
			  try{
				   orderStatus= uiUtil.webElementGetText(txtOrderStauts, "OrderStatus");
			     break;
			  }
			  catch(Exception e){
			    
			  }
			}
		
		ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(orderStatus, ExtentColor.BLUE));
		
	} 
	
	
}
