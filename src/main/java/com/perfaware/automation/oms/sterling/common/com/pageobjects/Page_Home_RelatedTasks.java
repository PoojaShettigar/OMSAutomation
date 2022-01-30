package com.perfaware.automation.oms.sterling.common.com.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.perfaware.automation.oms.sterling.common.utils.JavascriptExecutorUtilies;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

public class Page_Home_RelatedTasks {
	private WebDriver driver;
	UIUtilities uiUtil;
	JavascriptExecutorUtilies jsUtil;
	public Page_Home_RelatedTasks(WebDriver driver) {
		this.driver=driver;
		uiUtil=new UIUtilities();
		jsUtil=new JavascriptExecutorUtilies(this.driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//div[@uid='lnk_RT_OrderSearch']//a[text()='Order Search']")
	@CacheLookup
	private WebElement lnkOrderSearch;
	
	@FindBy(xpath="//div[@uid='lnk_RT_createOrder']//a[text()='Create Order']")
	@CacheLookup
	private WebElement lnkCreateOrder;
	
	@FindBy(xpath="//div[@uid='lnk_RT_CustomerSearch']//a[text()='Customer Search']")
	@CacheLookup
	private WebElement lnkCustomerSearch;
	
	@FindBy(xpath="//div[@uid='lnk_RT_CreateConsumer']//a[text()='Create Consumer']")
	@CacheLookup
	private WebElement lnkCreateCustomer;
	
	@FindBy(xpath="//div[@uid='lnk_RT_ReturnSearch']//a[text()='Return Search']")
	@CacheLookup
	private WebElement lnkReturnSearch;
	
	@FindBy(xpath="//div[@uid='createReturn']//a[text()='Create Return']")
	@CacheLookup
	private WebElement lnkCreateReturn;
	
	
}
