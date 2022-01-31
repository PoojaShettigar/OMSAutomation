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
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.perfaware.automation.oms.sterling.common.applicationHelper.OrderTypes;
import com.perfaware.automation.oms.sterling.common.applicationHelper.RequestHelper;
import com.perfaware.automation.oms.sterling.common.applicationHelper.TestData;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_Login;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_Home;
import com.perfaware.automation.oms.sterling.common.customAssertions.SoftAssertion;
import com.perfaware.automation.oms.sterling.common.driverFactory.BrowserFactory;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.fileReader.ExcelFileReader;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.testcaseUtils.APIMethods;
import com.perfaware.automation.oms.sterling.common.testcaseUtils.COMTestMethods;
import com.perfaware.automation.oms.sterling.common.testcaseUtils.TestCaseBase;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;

import io.restassured.internal.support.FileReader;
import io.restassured.response.Response;


public class COM_Login extends TestCaseBase {
	static String className = new Throwable().getStackTrace()[1].getClassName();
	public static final Logger logger = Logger.getLogger(className);
	private static final TimeUnit SECONDS = null;
	protected Map<String, List<Map<String, String>>> testData;
	protected HashMap<String, Map<String, String>> comData;
	protected Map<String, List<Map<String, String>>> comTestData;
	protected FileReader fileReader;
	RequestHelper helper = null;
	public static FileInputStream file;
	public static XSSFWorkbook workbook;
	public static XSSFSheet worksheet;
	public static String excelFilePath;
	BrowserFactory bf = new BrowserFactory();
	
	@BeforeClass(alwaysRun=true)
	public void initiate() throws Exception {
		helper = new RequestHelper();
		comData=initializeCOMTestData(PropertyFileReader.propertyMap.get("COMOrderTestdata"));
	}

	public HashMap<String, Map<String,String>> initializeCOMTestData(String filePath) throws Exception {		
		excelFilePath = curDir + filePath;
		file = new FileInputStream(excelFilePath);
		workbook = new XSSFWorkbook(file);
		worksheet = workbook.getSheetAt(0);
		HashMap<String, Map<String,String>>  data = new HashMap<String, Map<String,String>> ();
		ExcelFileReader e=new ExcelFileReader();
		List<String> tcNumbers = e.getFisrtColumnData(worksheet);
		for(int i=1;i<=tcNumbers.size();i++) {
			data.put(tcNumbers.get(i-1), e.readEntireExcelData(file, worksheet, i));
		}
		return data;
	}
	
	@BeforeMethod(alwaysRun=true)
	public void driverInitialization() throws MalformedURLException {
		
		String browserName=PropertyFileReader.propertyMap.get("browser");
		DriverFactory.getInstance().setDriver(bf.createBrowserInstance(browserName));
		DriverFactory.getInstance().getDriver().manage().window().maximize();
		DriverFactory.getInstance().getDriver().navigate().to(PropertyFileReader.propertyMap.get("COM_URL"));
	}

	@AfterMethod(alwaysRun=true)
	public void tearDown() {
		DriverFactory.getInstance().closeBrowser();
	}
	
	@Test(groups= {"Regression"})
	public void TC04_COM_LoginFunctionality() throws Exception {
		uiUtil=new UIUtilities();
		COMTestMethods comMethods=new COMTestMethods();
		Page_Login loginObj = new Page_Login(DriverFactory.getInstance().getDriver());
		comMethods.comLogin("admin1",PropertyFileReader.propertyMap.get("com_password"),loginObj);
		uiUtil.waitForPageLoad(10);
		loginObj.captureLoginError();
		
	}
}
