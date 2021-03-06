package com.perfaware.automation.oms.sterling.common.testListeners;
import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.testcaseUtils.TestCaseBase;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentManager;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentTestManager;
import com.perfaware.automation.oms.sterling.common.utils.Utilities;

import io.restassured.RestAssured;

public class TestListener implements ITestListener {
	protected static String curDir = System.getProperty("user.dir");
	public static HashMap<String, String> tm4jbaseURL;
	String TestCycleKey;
	public static HashMap<String, String> testcase_jiras;
	
	public FileInputStream file_jira;
	public XSSFWorkbook workbook_jira;
	public XSSFSheet worksheet_jira;
	public String excelFilePath_jira;
	
	private static final Logger LOGGER = Logger.getLogger(TestCaseBase.class);
	private static Logger logger = Logger.getLogger(TestListener.class);
	static ExtentReports report;
	ExtentTest test;
	Utilities util=new Utilities();
	static ThreadLocal<Integer> flag = new ThreadLocal<Integer>();
	
	@Override
	public void onTestStart(ITestResult result) {
		flag.set(1);
		logger.info("Starting new testcase :" + result.getMethod().getMethodName());
		ExtentTestManager.startTest(result.getMethod().getMethodName());
		if (result.getMethod().getDescription() != null) {
			String summary = result.getMethod().getDescription().replaceAll("\n", "<BR>");
			ExtentTestManager.getTest().log(Status.INFO, "Testcase Summary : <BR>" + summary);
		}
	}
	@Override
	public void onTestSuccess(ITestResult result) {
		logger.info("Test successfully passed : " + result.getMethod().getMethodName() + result.getTestClass());
		ExtentTestManager.getTest().log(Status.PASS,MarkupHelper.createLabel(result.getName() + " PASSED.", ExtentColor.GREEN));
		//updateStatusInTM4J(result, "Pass");
		
	}

	@Override
	public void onTestFailure(ITestResult result) {
		
		ExtentTestManager.getTest().log(Status.FAIL,
				MarkupHelper.createLabel(result.getName() + " FAILED ", ExtentColor.RED));
		ExtentTestManager.getTest().fail(result.getThrowable());
		logger.error("Test failed : " + result.getName());
		StringWriter sw = new StringWriter();
		result.getThrowable().printStackTrace(new PrintWriter(sw));
		logger.error(sw.toString());
		sw = null;
		
		try {
			//add screenshot for failed test.
			File srcFile = ((TakesScreenshot)DriverFactory.getInstance().getDriver()).getScreenshotAs(OutputType.FILE);
			System.out.println("srcFile->"+srcFile);
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH-mm-ss");
			Date date = new Date();
			String actualDate = format.format(date);
			
			String screenshotPath = System.getProperty("user.dir")+"/Results/Screenshots/"+actualDate+".jpeg";
			System.out.println(System.getProperty("user.dir"));
			File destFile = new File(screenshotPath);
			util.copyFile(srcFile, destFile);
			ExtentTestManager.getTest().addScreenCaptureFromPath(screenshotPath, "Test case failure screenshot");

		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		//updateStatusInTM4J(result, "Fail");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		if (flag.get() == 0) {
			ExtentTestManager.startTest(result.getMethod().getMethodName());
			if (result.getMethod().getDescription() != null) {
				String summary = result.getMethod().getDescription().replaceAll("\n", "<BR>");
				ExtentTestManager.getTest().log(Status.INFO, "Testcase Summary : <BR>" + summary);
			}
			logger.info("Test skipped : " + result.getName() + result.getTestClass());
			ExtentTestManager.getTest().log(Status.SKIP,
					MarkupHelper.createLabel(result.getName() + " SKIPPED ", ExtentColor.ORANGE));
		} else {
			logger.info("Test skipped : " + result.getName() + result.getTestClass());
			ExtentTestManager.getTest().log(Status.SKIP,
					MarkupHelper.createLabel(result.getName() + " SKIPPED ", ExtentColor.ORANGE));
			ExtentManager.extentReports.removeTest(ExtentTestManager.getTest());
		}
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		
	}

	@Override
	public void onTestFailedWithTimeout(ITestResult result) {
		
	}

	@Override
	public void onStart(ITestContext context) {
		try {
			// report = ExtentManager.setupExtentReport();
			/*try {
			PropertyReader.propertyMap = new PropertyReader()
					.getProperties(curDir + "/src/test/resources/config.Properties");

			excelFilePath_jira = curDir + PropertyReader.propertyMap.get("excelJiraTickets");
			file_jira = new FileInputStream(excelFilePath_jira);
			workbook_jira = new XSSFWorkbook(file_jira);
			worksheet_jira = this.workbook_jira.getSheetAt(0);
			testcase_jiras = new LoadTestData().readExcelJiraTickets(file_jira, worksheet_jira);
			file_jira.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getTM4JProperties();
		
		XmlSuite suite = context.getSuite().getXmlSuite();
		int threads = suite.getThreadCount();


		String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String projectKey = tm4jbaseURL.get("ProjectKey");
		String folderName = tm4jbaseURL.get("SprintName");
		RestAssured.baseURI = tm4jbaseURL.get("BaseURL");
		String response = given().log().all().auth().preemptive()
				.basic(tm4jbaseURL.get("username"), tm4jbaseURL.get("password"))
				.header("Content-Type", "application/json")
				.body("{\n" + "  \"name\": \"Automation" + timeStamp + "\",\n" + "  \"projectKey\": \"" + projectKey
						+ "\",\n" + "  \"folder\": \"" + folderName + "\"\n" + "}")
				.when().post("/testrun").then().assertThat().statusCode(201).extract().response().asString();
		TestCycleKey = JsonUtil.getJsonPath(response).getString("key");*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		flag.set(0);
		Iterator<ITestResult> skippedTestCases = context.getSkippedTests().getAllResults().iterator();
		while (skippedTestCases.hasNext()) {
			ITestResult skippedTestCase = skippedTestCases.next();
			ITestNGMethod method = skippedTestCase.getMethod();
			if (context.getSkippedTests().getResults(method).size() > 0) {
				skippedTestCases.remove();
			}
		}
	}
	
	/**
	 * This function is to create Test Folder in TM4J
	 * 
	 * @author Perfaware
	 */
	public void createTestCycleFolder() {
		RestAssured.baseURI = tm4jbaseURL.get("BaseURL");
		String projectKey = tm4jbaseURL.get("ProjectKey");
		String folderName = tm4jbaseURL.get("SprintName");
		given().log().all().auth().preemptive()
				.basic(tm4jbaseURL.get("username"), tm4jbaseURL.get("password"))
				.header("Content-Type", "application/json")
				.body("{\n" + "  \"projectKey\": \"" + projectKey + "\",\n" + "  \"name\": \"" + folderName + "\",\n"
						+ "  \"type\": \"TEST_RUN\"\n" + "}")
				.when().post("/folder").then().assertThat().statusCode(201).extract().response().asString();
	}



	/**
	 * This function to update the Test result status in TM4J
	 * 
	 * @author Perfaware
	 * @param result - Test result
	 * @param status - status to be updated
	 */
	public void updateStatusInTM4J(ITestResult result, String status) {
		
		
		String groups = "";
		if(testcase_jiras.get(result.getMethod().getMethodName()) != null) {
			groups = testcase_jiras.get(result.getMethod().getMethodName());
		}
		String[] groupsName = groups.split(",");
		List<String> testCaseKeys = new ArrayList<String>();
		for (int i = 0; i < groupsName.length; i++) {
			if (groupsName[i].startsWith(tm4jbaseURL.get("ProjectKey"))) {
				testCaseKeys.add(groupsName[i]);
			}
		}
		for (int i = 0; i < testCaseKeys.size(); i++) {
			RestAssured.baseURI = tm4jbaseURL.get("BaseURL");
			 given().auth().preemptive().basic(tm4jbaseURL.get("username"),tm4jbaseURL.get("password"))
					.header("Content-Type", "application/json")
					.body("{\r\n" + "  \"status\": \"" + status + "\",\r\n" + "  \"scriptResults\": [\r\n" + "    {\r\n"
							+ "      \"index\": 0,\r\n" + "      \"status\": \"" + status + "\"\r\n" + "    }\r\n"
							+ "  ]\r\n" + "}")
					.when().post("/testrun/" + TestCycleKey + "/testcase/" + testCaseKeys.get(i) + "/testresult").then()
					.assertThat().statusCode(201).extract().response().asString();
		}
	}
	
	public HashMap<String, String> getTM4JProperties() {
		try {
			tm4jbaseURL = new PropertyFileReader().getProperties(curDir + "/src/test/resources/tm4j.properties");
			return tm4jbaseURL;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
