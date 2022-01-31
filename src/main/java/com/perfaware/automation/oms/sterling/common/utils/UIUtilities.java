package com.perfaware.automation.oms.sterling.common.utils;

import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentTestManager;

public class UIUtilities {
	WebDriverWait wait;
	Actions actions;
	public void enterInput(WebElement webElement,String keysToSend,String element) {
		webElement.clear();
		webElement.sendKeys(keysToSend);
		ExtentTestManager.getTest().log(Status.PASS, element +"- Ented value as: "+keysToSend);
//		try {
//			webElement.clear();
//			webElement.sendKeys(keysToSend);
//			//log success message in exgent report
//			ExtentTestManager.getTest().log(Status.PASS, element +"- Ented value as: "+keysToSend);
//		} catch (Exception e) {
//			//log failure in extent
//			ExtentTestManager.getTest().log(Status.FAIL, "Value enter in field: "+element + " is failed due to exception: "+e);
//			
//		}
	}
	
	public void enterKeys(WebElement webElement,Keys keysToSend) {
		try {
			webElement.sendKeys(keysToSend);
		} catch (Exception e) {
		}
	}

	public void elementClick(WebElement webElement,String element) {
		webElement.click();
		ExtentTestManager.getTest().log(Status.PASS, element+"- Clicked Successfully! ");
//		try {
//			webElement.click();
//			//log success message in exgent report
//			ExtentTestManager.getTest().log(Status.PASS, element+"- Clicked Successfully! ");
//			//ExtentTestManager.getTest().pass(MarkupHelper.createLabel("Clicked Successfully", ExtentColor.BLUE));
//		} catch (Exception e) {
//			//log failure in extent
//			ExtentTestManager.getTest().log(Status.FAIL, "Unable to click on field: " +element +" due to exception: "+e);	
//		}
	}
	
	public void elementClickAction(WebDriver driver,WebElement webElement,String element) {
		try {
			actions = new Actions(driver); 
		    actions.moveToElement(webElement).click().perform(); 
			ExtentTestManager.getTest().log(Status.PASS, element+"- Clicked Successfully! ");
		} catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "Unable to click on field: " +element +" due to exception: "+e);
		}
	}
	
	public WebElement elementToBeClickable(WebDriver driver,By webElement,String element) {
		try {
			wait = new WebDriverWait(driver,100);
			return wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(webElement)));

		}
		catch (Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, element+" not vosible "+e);
			
		}
		return null;
		
	}
	
	
	public String webElementGetText(WebElement webElement,String element) {
		String text = "";
		text = webElement.getText();
		ExtentTestManager.getTest().pass(MarkupHelper.createLabel(element+" retrived is: "+text, ExtentColor.BLUE));

		return text;
	}
	
	public String getPageTitle(WebDriver driver) {
		try {
			return driver.getTitle();
		}
		catch(Exception e) {
			ExtentTestManager.getTest().log(Status.FAIL, "getPageTitle failed due to "+ e);
			return null;
		}	
	}
	
	public void acceptAlertPopup(WebDriver driver) {
		try {
			driver.switchTo().alert().accept();
		}
		catch(Exception e) {
			//ExtentFactory.getInstance().getExtent().log(Status.FAIL, "No such alert to accept"+ e);
			
		}	
	}
	
	public void closeAlertPopup(WebDriver driver) {
		try {
			driver.switchTo().alert().dismiss();
		}
		catch(Exception e) {
			//ExtentFactory.getInstance().getExtent().log(Status.FAIL, "No such alert to close"+ e);
			
		}
	}
	
	public void waitForPageLoad(int seconds) throws InterruptedException {
		Thread.sleep(seconds*1000);
	}
	
	public String getWindowHandle(WebDriver driver) {
		return driver.getWindowHandle();
	}
	
	public Set<String> getWindowHandles(WebDriver driver) {
		return driver.getWindowHandles();
	}
	
	public void switchToChildWindow(WebDriver driver) {
		String parentId=getWindowHandle(driver);
		Set<String> childIds=getWindowHandles(driver);
		Iterator<String> I1= childIds.iterator();
		while(I1.hasNext())
		{
			String child_window=I1.next();
			if(!parentId.equals(child_window))
			{
				driver.switchTo().window(child_window);
				System.out.println(driver.switchTo().window(child_window).getTitle());
			}
				
		}
	}
	
	public void switchToParentWindow(WebDriver driver, String parentId) {
		driver.switchTo().window(parentId);
	}
	
}
