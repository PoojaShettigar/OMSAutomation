package com.perfaware.automation.oms.sterling.common.testcases;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.perfaware.automation.oms.sterling.common.applicationHelper.OrderTypes;
import com.perfaware.automation.oms.sterling.common.applicationHelper.RequestHelper;
import com.perfaware.automation.oms.sterling.common.applicationHelper.TestData;
import com.perfaware.automation.oms.sterling.common.customAssertions.SoftAssertion;
import com.perfaware.automation.oms.sterling.common.driverFactory.BrowserFactory;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.fileReader.ExcelFileReader;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.testcaseUtils.APIMethods;
import com.perfaware.automation.oms.sterling.common.testcaseUtils.TestCaseBase;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentManager;
import com.perfaware.automation.oms.sterling.common.utils.Utilities;

import io.restassured.internal.support.FileReader;
import io.restassured.response.Response;

public class XML_BOPIS_OrderFulfillment extends TestCaseBase {
	static String className = new Throwable().getStackTrace()[1].getClassName();
	public static final Logger logger = Logger.getLogger(className);
	protected Map<String, List<Map<String, String>>> testData;
	protected FileReader fileReader;
	RequestHelper helper = null;
	public static FileInputStream file;
	public static XSSFWorkbook workbook;
	public static XSSFSheet worksheet;
	public static String excelFilePath;


	@BeforeClass(alwaysRun=true)
	public void initiate() throws Exception {

		helper = new RequestHelper();
		excelFilePath = curDir + PropertyFileReader.propertyMap.get("XMLOrderTestdata");
		file = new FileInputStream(excelFilePath);
		workbook = new XSSFWorkbook(file);
		worksheet = workbook.getSheetAt(0);
		initializeTestData();
	}

	public void initializeTestData() throws Exception {
		testData = new HashMap<String, List<Map<String, String>>>();
		TestData[] testDataTypes = TestData.values();
		for (TestData testDataType : testDataTypes) {
			testData.put(testDataType.name(), helper.getDetails(file, worksheet, testDataType.name()));
		}
	}
	
	@Test(groups= {"Regression"})
	public void TC01_XML_BOPIS_SL_OrderFulfillment() throws Exception {
		Response response = null;
		String orderNo;
		SoftAssertion softAssert = new SoftAssertion();
		OrderTypes orderTypes = OrderTypes.STKSTH;
		String noOfLines = "1";
		Map<String, String> itemData = helper.generateItemIds_ForOrder(OrderTypes.STKSTH, noOfLines);
		Map<String, String> tempData = new HashMap<String, String>();
		orderNo = "COCC"+Utilities.generateRandomString(8);
		APIMethods apiMethods=new APIMethods();
		
		int nooflines = 0;
		String[] split = noOfLines.split("_");
		for(int i =0;i<split.length;i++) {
			nooflines = nooflines+Integer.valueOf(split[i]);
		}
		System.out.println(orderNo);
		apiMethods.manageItem(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.adjustInventory(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		response=apiMethods.createOrder(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.resolveHold(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.scheduleAndReleaseOrder(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.BopisOrderFulfillment(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
	}
	
	
	@Test (groups = { "Regression", "Smoke" })
	public void TC02_XML_BOPIS_ML_OrderFulfillment() throws Exception {
		Response response = null;
		String orderNo;
		SoftAssertion softAssert = new SoftAssertion();
		OrderTypes orderTypes = OrderTypes.STKSTH;
		String noOfLines = "3";
		Map<String, String> itemData = helper.generateItemIds_ForOrder(OrderTypes.STKSTH, noOfLines);
		Map<String, String> tempData = new HashMap<String, String>();
		orderNo = "COCC"+Utilities.generateRandomString(8);
		APIMethods apiMethods=new APIMethods();
		
		int nooflines = 0;
		String[] split = noOfLines.split("_");
		for(int i =0;i<split.length;i++) {
			nooflines = nooflines+Integer.valueOf(split[i]);
		}
		apiMethods.manageItem(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.adjustInventory(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		response=apiMethods.createOrder(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.resolveHold(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.scheduleAndReleaseOrder(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
		apiMethods.BopisOrderFulfillment(helper, orderTypes, noOfLines, response, tempData, orderNo, logger, testData, itemData, softAssert);
	}	
}
