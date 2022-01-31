package com.perfaware.automation.oms.sterling.common.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentTestManager;

public class JavascriptExecutorUtilies {
	JavascriptExecutor jse;
	UIUtilities uiUtil = new UIUtilities();
	public JavascriptExecutorUtilies(WebDriver driver) {
		 jse = ((JavascriptExecutor)driver); 
	}
	public void elementSelect(WebDriver driver,WebElement webElement,String keysToSend,String element) {
		try {
			//JavascriptExecutor jse = ((JavascriptExecutor)driver); 
			jse.executeScript("arguments[0].value='';", webElement);
			uiUtil.enterInput(webElement, keysToSend, "Enterprise");

			ExtentTestManager.getTest().log(Status.PASS, element +"- selected as: "+keysToSend);
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Value enter in field: "+element + " is failed due to exception: "+e);
		}
	}
	
	public void elementClick(WebDriver driver,By webElement,String element) {
		jse.executeScript("arguments[0].click();",uiUtil.elementToBeClickable(driver, webElement, element));
	}
	
	public void enterInput(WebDriver driver,By webElement,String input,String element) {
		jse.executeScript("arguments[0].value='"+input+"';",uiUtil.elementToBeClickable(driver, webElement, element));
	}
	
	public void scrollDown(WebDriver driver,By webElement) {
		jse.executeScript("window.scrollBy(0,500)", "");
	}
	
	public void openNewTab(String url) {
		jse.executeScript("window.open('"+url+"');");
	}
}
