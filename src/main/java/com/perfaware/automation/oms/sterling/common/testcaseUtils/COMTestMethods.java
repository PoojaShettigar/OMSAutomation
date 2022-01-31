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
import com.perfaware.automation.oms.sterling.common.applicationHelper.OmsConstants;
import com.perfaware.automation.oms.sterling.common.applicationHelper.OrderTypes;
import com.perfaware.automation.oms.sterling.common.applicationHelper.RequestHelper;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_AddProducts;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_Login;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_OrderSearch;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_CreateConsumer;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_CustomerDetails;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_CustomerSearch;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_FulfillmentSummary;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_Home;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_OrderSummary;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_PaymentConfirmation;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_PaymentMercadoPago;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_ReturnSearch;
import com.perfaware.automation.oms.sterling.common.customAssertions.SoftAssertion;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;
import com.perfaware.automation.oms.sterling.common.utils.Utilities;
import com.perfaware.automation.oms.sterling.common.utils.XMLUtil;
import com.sun.deploy.uitoolkit.impl.fx.ui.UITextArea;

import io.restassured.response.Response;

public class COMTestMethods {

	public  Page_Home comLogin(String userName,String password, Page_Login loginObj) throws Exception {
		//Login Page
		loginObj.enterUsername(userName);
		loginObj.enterPassword(password);
		Page_Home homeObj=loginObj.clickLogin();
		return homeObj;
	}
	
	public void comSelectEnterprise(String enterprise, Page_Home homeObj,UIUtilities uiUtil) throws Exception {
		//Enterprise Page
		homeObj.selectEenterprise(enterprise); 
		homeObj.clickApply(); 
	}
	
	public void comCustomerSearchWithEmail(String emailAddress, Page_CustomerSearch customerSearchObj,UIUtilities uiUtil) throws Exception {
		//Search Customer Page
		customerSearchObj.enterEmailAddress(emailAddress);
		customerSearchObj.clickSearchButton();
		uiUtil.waitForPageLoad(5);
		customerSearchObj.clickNextButton();
	}
	
	public Page_FulfillmentSummary comAddProductsToCart(Page_AddProducts addProductsObj,UIUtilities uiUtil,Map<String, String> data) throws Exception {
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
		return fulfillmentSummaryObj;
	}
	
	public void comSelectfulfillmentSummary(String shippingOptions, Page_FulfillmentSummary fulfillmentSummaryObj,UIUtilities uiUtil) throws Exception {
		//Fulfillemnt Summary Page
		uiUtil.waitForPageLoad(7);
		fulfillmentSummaryObj.clickShippingOptionsButton();
		uiUtil.waitForPageLoad(10);
		fulfillmentSummaryObj.selectShippingOptions(shippingOptions);
		uiUtil.waitForPageLoad(4);
		fulfillmentSummaryObj.clickSaveButton();
		uiUtil.waitForPageLoad(4);
		fulfillmentSummaryObj.clickNextButton();
	}
	
	public Page_OrderSummary comProvidePayment(Map<String, String> data, Page_PaymentConfirmation paymentObj,UIUtilities uiUtil,String parentWindowId) throws Exception {
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
		return summary;
	}
	
	public void COM_CustomerCreation(HashMap<String, Map<String, String>> comData,String tcId,UIUtilities uiUtil, RequestHelper helper, Response response, Logger logger,SoftAssertion softAssert) throws Exception {
		Map<String, String> data = comData.get(tcId);
		uiUtil.waitForPageLoad(3);
		 //Login Page
		Page_Login loginObj = new Page_Login(DriverFactory.getInstance().getDriver());
		Page_Home homeObj=comLogin(PropertyFileReader.propertyMap.get("com_username"),PropertyFileReader.propertyMap.get("com_password"),loginObj);
		uiUtil.waitForPageLoad(20);
		
		//ClickCreateConsumer
		Page_CreateConsumer CreateConsumerObj=homeObj.clickCreateConsumer();
		uiUtil.waitForPageLoad(5); 
		homeObj.selectEenterprise(data.get("Enterprise")); 
		homeObj.clickApply();
		uiUtil.waitForPageLoad(3); 
		
		//Create Customer Page
		String number=Utilities.generateRandomString(4);
		CreateConsumerObj.enterFirstName(data.get("FirstName"));
		CreateConsumerObj.enterLastName(data.get("LastName"));
		CreateConsumerObj.enterEmailAddress(number+data.get("EmailAddress"));
		CreateConsumerObj.enterMobilePhone(data.get("MobilePhone"));
		CreateConsumerObj.enterRUTID(data.get("RUTID"));
		CreateConsumerObj.clickAcceptCheckbox();
		Page_CustomerDetails customerDetails=CreateConsumerObj.clickConfirmButton();
		uiUtil.waitForPageLoad(6);       
		uiUtil.acceptAlertPopup(DriverFactory.getInstance().getDriver());
		uiUtil.waitForPageLoad(10); 
		validateCustomerDetails(number, data, customerDetails,softAssert);
	}
	
	public void validateCustomerDetails(String number,Map<String, String> data,Page_CustomerDetails customerDetails,SoftAssertion softAssert) {
		
		Utilities util=new Utilities();
		String[] name=customerDetails.captureContactName().split(" ");
		String firstName=name[0];
		String lastName=name[1];

		util.resultValidationSoftAssertion(data.get("FirstName"), firstName, "Customer details", softAssert);
		util.resultValidationSoftAssertion(data.get("LastName"), lastName, "Customer details", softAssert);
		util.resultValidationSoftAssertion(number+data.get("EmailAddress"),customerDetails.captureEmailaddress(), "Customer details", softAssert);
		util.resultValidationSoftAssertion(data.get("MobilePhone"),customerDetails.captureMobilePhone(), "Customer details", softAssert);
		util.resultValidationSoftAssertion(data.get("RUTID"),customerDetails.captureRUTID(), "Customer details", softAssert);
		
	}
	
	public void orderSearchByOrderNo(String orderNumber) {
		Page_OrderSearch orderSearchObj = new Page_OrderSearch(DriverFactory.getInstance().getDriver());
		orderSearchObj.enterOrderNumber(orderNumber);
		orderSearchObj.clickSearch();
		
	}
	
	public void orderSearchByCustomerDetails(HashMap<String, Map<String, String>> comData,String tcId) {
		Map<String, String> data = comData.get(tcId);
		Page_OrderSearch orderSearchObj = new Page_OrderSearch(DriverFactory.getInstance().getDriver());
		
		orderSearchObj.enterEmailAddress(data.get("EmailAddress"));
		orderSearchObj.enterFirstName(data.get("FirstName"));
		orderSearchObj.enterLastName(data.get("Lastname"));
		orderSearchObj.clickSearch();
		
	}
	
	public void returnOrderSearchByOrderNo(String returnOrderNumber) {
		Page_ReturnSearch returnOrderSearchObj = new Page_ReturnSearch(DriverFactory.getInstance().getDriver());
		returnOrderSearchObj.enterReturnOrderNumber(returnOrderNumber);
		returnOrderSearchObj.clickSearch();
		
	}
	
	public void returnOrderSearchByCustomerDetails(HashMap<String, Map<String, String>> comData,String tcId) {
		Map<String, String> data = comData.get(tcId);
		Page_ReturnSearch returnOrderSearchObj = new Page_ReturnSearch(DriverFactory.getInstance().getDriver());
		
		returnOrderSearchObj.enterEmailAddress(data.get("EmailAddress"));
		returnOrderSearchObj.enterFirstName(data.get("FirstName"));
		returnOrderSearchObj.enterLastName(data.get("Lastname"));
		returnOrderSearchObj.clickSearch();
		
	}
	
	public String COM_RegisteredUser_OrderCreation(HashMap<String, Map<String, String>> comData,String tcId,UIUtilities uiUtil, RequestHelper helper, OrderTypes orderTypes, String noOfLines, Response response, Map<String, String> tempData,Logger logger, Map<String, List<Map<String, String>>> comTestData, Map<String, String> itemData, SoftAssertion softAssert) throws Exception {		
		Map<String, String> data = comData.get(tcId);
		uiUtil.waitForPageLoad(3);
		Page_Login loginObj = new Page_Login(DriverFactory.getInstance().getDriver());
		Page_Home homeObj=comLogin(PropertyFileReader.propertyMap.get("com_username"),PropertyFileReader.propertyMap.get("com_password"),loginObj);
		
		uiUtil.waitForPageLoad(7);
		//CreateOrder Page
		String parentWindowId=homeObj.clickCreateOrder();
		uiUtil.waitForPageLoad(7); 
		
		//Select Enterprise
		comSelectEnterprise(data.get("Enterprise"),homeObj,uiUtil);
		uiUtil.waitForPageLoad(5);	
		
		//Search Registered Customer
		Page_CustomerSearch customerSearchObj=new Page_CustomerSearch(DriverFactory.getInstance().getDriver());
		comCustomerSearchWithEmail(data.get("EmailAddress"), customerSearchObj, uiUtil);
		uiUtil.waitForPageLoad(6);
		
		//Add Products to cart
		Page_AddProducts addProductsObj=new Page_AddProducts(DriverFactory.getInstance().getDriver());
	
		//Select Fulfillment option
		Page_FulfillmentSummary fulfillmentSummaryObj=comAddProductsToCart(addProductsObj, uiUtil, data);
		comSelectfulfillmentSummary(data.get("ShippingOptions"), fulfillmentSummaryObj, uiUtil);

		//Provide Payment info
		Page_PaymentConfirmation paymentObj=new Page_PaymentConfirmation(DriverFactory.getInstance().getDriver());
		uiUtil.waitForPageLoad(5);
		Page_OrderSummary summary=comProvidePayment(data, paymentObj, uiUtil, parentWindowId);
		
		//OrderSummary Page
		uiUtil.waitForPageLoad(20);
		String orderNo= summary.captureOrderNo();
		
		return orderNo;
	}
	
}



