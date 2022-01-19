package com.perfaware.automation.oms.sterling.common.testcaseUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.applicationHelper.OrderTypes;
import com.perfaware.automation.oms.sterling.common.applicationHelper.RequestHelper;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_AddProducts;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_COM_Login;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_CustomerSearch;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_FulfillmentSummary;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_Home;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_OrderSummary;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_PaymentConfirmation;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_PaymentMercadoPago;
import com.perfaware.automation.oms.sterling.common.customAssertions.SoftAssertion;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentFactory;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

import io.restassured.response.Response;

public class COMTestMethods {
	public  void COM_RegisteredUser_OrderCreation(HashMap<String, Map<String, String>> comData,String tcId,UIUtilities uiUtil, RequestHelper helper, OrderTypes orderTypes, String noOfLines, Response response, Map<String, String> tempData,Logger logger, Map<String, List<Map<String, String>>> comTestData, Map<String, String> itemData, SoftAssertion softAssert) throws Exception {
		
		Map<String, String> data = comData.get(tcId);
		uiUtil.waitForPageLoad(3);
		Page_COM_Login loginObj = new Page_COM_Login(DriverFactory.getInstance().getDriver());
		Page_Home homeObj=COM_PerformLogin(PropertyFileReader.propertyMap.get("com_username"),PropertyFileReader.propertyMap.get("com_password"),loginObj);
		
		uiUtil.waitForPageLoad(7);
		//CreateOrder Page
		String parentWindowId=homeObj.clickCreateOrder();
		uiUtil.waitForPageLoad(7); 
		homeObj.selectEenterprise(data.get("Enterprise")); 
		Page_CustomerSearch customerSearchObj = homeObj.clickApply(); 
		uiUtil.waitForPageLoad(3);	
		
		//Search Customer Page
		customerSearchObj.enterEmailAddress(data.get("EmailAddress"));
		customerSearchObj.clickSearchButton();
		uiUtil.waitForPageLoad(3);
		Page_AddProducts addProductsObj=customerSearchObj.clickNextButton();
		uiUtil.waitForPageLoad(6);
			
		//Add Products Page
		for(int i=1;i<=Integer.parseInt(data.get("NumberOfProducts"));i++) {
			addProductsObj.enterProductId(data.get("ProductId"+i));
			addProductsObj.clickAddButton();
			uiUtil.waitForPageLoad(3);
			addProductsObj.enterQuantity(data.get("Quantity"+i));
			addProductsObj.selectDeliveryMethods(data.get("DeliveryMethod"));
			addProductsObj.selectDeliveryMethods(data.get("DeliveryMethod"));
			uiUtil.waitForPageLoad(1);
		}
		uiUtil.waitForPageLoad(2);
		Page_FulfillmentSummary fulfillmentSummaryObj=addProductsObj.clickNextButton();	
		
		//Fulfillemnt Summary Page
		uiUtil.waitForPageLoad(7);
		fulfillmentSummaryObj.clickShippingOptionsButton();
		uiUtil.waitForPageLoad(10);
		fulfillmentSummaryObj.selectShippingOptions(data.get("ShippingOptions"));
		uiUtil.waitForPageLoad(4);
		fulfillmentSummaryObj.clickSaveButton();
		uiUtil.waitForPageLoad(4);
		Page_PaymentConfirmation paymentObj=fulfillmentSummaryObj.clickNextButton();
		uiUtil.waitForPageLoad(5);

		//Payment Page
		if(data.get("PaymentMethod").contentEquals("Add Payment Method")) {
			paymentObj.clickAddPaymentMethodButton();
			uiUtil.waitForPageLoad(5);
			paymentObj.enterPaymentType(data.get("PaymentType"));
			//paymentObj.enterPaymentReference(data.get("PaymentReferrence1"));
			paymentObj.clickApplyButton();
		}
		if(data.get("PaymentMethod").contentEquals("Pay Using MercadoPago")) {
			Page_PaymentMercadoPago mercadoObj = paymentObj.clickPayUsingMercadoPagoButton();
			uiUtil.waitForPageLoad(15);
			mercadoObj.clickCreditcardLink();
			uiUtil.waitForPageLoad(4);
			mercadoObj.enterCreditcardNumber(data.get("CreditcardNumber"));
			mercadoObj.enterExpirationDate(data.get("ExpirationDate"));
			mercadoObj.enterFullname(data.get("FirstName"));
			mercadoObj.enterCvv(data.get("CVV"));
			mercadoObj.clickSubmitButton();
			uiUtil.waitForPageLoad(15);
			mercadoObj.enterRut(data.get("RUT"));
			uiUtil.waitForPageLoad(7);
			mercadoObj.clickInstallments();
			uiUtil.waitForPageLoad(6);
			mercadoObj.enterEmail(data.get("EmailAddress"));
			mercadoObj.clickPay();
			uiUtil.waitForPageLoad(10);
			mercadoObj.clickContinue(parentWindowId);
		}
		
		uiUtil.waitForPageLoad(3);
		Page_OrderSummary summary=paymentObj.clickConfirmButton();
	
		//OrderSummary Page
		uiUtil.waitForPageLoad(20);
		tempData.put("Enterprise", comData.get(tcId).get("Enterprise"));
		String orderNo= summary.captureOrderNo();
		APIMethods apiMethods=new APIMethods();
		apiMethods.StoreOrderFulfillment(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, comTestData, itemData, softAssert,summary);
		
	}
	
	public  Page_Home COM_PerformLogin(String userName,String password, Page_COM_Login loginObj) throws Exception {
		//Login Page
		loginObj.enterUsername(userName);
		loginObj.enterPassword(password);
		Page_Home homeObj=loginObj.clickLogin();
		return homeObj;
	}
}



