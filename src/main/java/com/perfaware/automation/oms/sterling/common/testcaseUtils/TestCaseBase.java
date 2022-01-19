package com.perfaware.automation.oms.sterling.common.testcaseUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.perfaware.automation.oms.sterling.common.customExceptions.NoSuchResourceException;
import com.perfaware.automation.oms.sterling.common.driverFactory.BrowserFactory;
import com.perfaware.automation.oms.sterling.common.driverFactory.DriverFactory;
import com.perfaware.automation.oms.sterling.common.fileReader.ExcelFileReader;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.pojo.ApiDetails;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentManager;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;
import com.perfaware.automation.oms.sterling.common.utils.Utilities;

import io.restassured.RestAssured;

public class TestCaseBase {
	protected static String curDir = System.getProperty("user.dir");
	public static HashMap<String, String> realAPIResource;
	private static final Logger LOGGER = Logger.getLogger(TestCaseBase.class);
	private static HashMap<String,String> testcase_jiras;
	public  FileInputStream file_jira;
	public  XSSFWorkbook workbook_jira;
	public  XSSFSheet worksheet_jira;
	public  String excelFilePath_jira;
	protected UIUtilities uiUtil;
	protected Utilities util;



	@BeforeSuite(alwaysRun=true)
	public void initialSetup() throws Exception {
	
		initializeKeywords();
		initializePropertyFiles();
		
		String currentLogFolder = curDir + ExtentManager.reportPropertyMap.get("logFolder");
		initializeLogs(currentLogFolder);

		String currentFolder = curDir + ExtentManager.reportPropertyMap.get("htmlReportFolder");
		initializeReports(currentFolder);
		
		String currentScreenshotFolder= curDir + ExtentManager.reportPropertyMap.get("screenshotFolder");
		initializeScreenshots(currentScreenshotFolder);
			
		
		excelFilePath_jira = curDir + PropertyFileReader.propertyMap.get("JiraTickets");
		file_jira = new FileInputStream(excelFilePath_jira);
		workbook_jira = new XSSFWorkbook(file_jira);
		worksheet_jira = this.workbook_jira.getSheetAt(0);
		testcase_jiras=new ExcelFileReader().readExcelJiraTickets(file_jira, worksheet_jira);
		file_jira.close();
		
		PropertyConfigurator.configure(curDir+"/src/main/resources/log4j.properties");
		RestAssured.useRelaxedHTTPSValidation();
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	
	/**
	 * This function will initialize the logs before the suite execution starts
	 * @author Perfaware
	 * @param currentLogFolder
	 */
	public void initializeLogs(String currentLogFolder) {
		File[] logdirectories = new File(currentLogFolder).listFiles(File::isDirectory);
		if (!(new File(currentLogFolder).exists())) {
			new File(currentLogFolder).mkdirs();
		}		
		else if (logdirectories.length > 0) {
			try {
				util.archieveLastLogs(logdirectories[0].getPath());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	/**
	 * This function will initialize the Reports before the suite execution starts
	 * @author Perfaware
	 * @param currentFolder
	 */
	public void initializeReports(String currentFolder) {

		File[] directories = new File(currentFolder).listFiles(File::isDirectory);
		if (!(new File(currentFolder).exists()))
			new File(currentFolder).mkdir();
		else if (directories.length > 0) {
			try {
				util.archieveLastReports(directories[0].getPath());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		ExtentManager.reportFolder = currentFolder + "/"
				+ (util.getCurrentDateTime().replaceAll("/", "-").replaceAll(":", "-"));
	}

	/**
	 * This function will initialize the Screenshots before the suite execution starts
	 * @author Perfaware
	 * @param currentFolder
	 */
	public void initializeScreenshots(String currentFolder) {

		File[] directories = new File(currentFolder).listFiles(File::isDirectory);
		if (!(new File(currentFolder).exists()))
			new File(currentFolder).mkdir();
		else if (directories.length > 0) {
			try {
				util.archieveLatsScreenshots(directories[0].getPath());
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

	public void initializePropertyFiles() throws Exception {
		try {
			
			PropertyFileReader.propertyMap = new PropertyFileReader()
					.getProperties(curDir + "/src/main/resources/configurations.Properties");
			ExtentManager.reportPropertyMap = new PropertyFileReader()
					.getProperties(curDir + "/src/main/resources/extentReport.properties");
			
			realAPIResource = new PropertyFileReader()
					.getProperties(curDir + "/src/main/resources/apiResources.properties");
			
		} catch (Exception e) {
			LOGGER.error("Error while reading config properties.");
			throw e;
		}
	}
	private void initializeKeywords() {
		try {
			uiUtil=new UIUtilities();
			util = new Utilities();
		} catch (Exception e) {
			LOGGER.error("Error while initializing utilities.");
			throw e;
		}
	}
	
	/**
	 * This function will get the resource details based upon if mock or real api is to be called,
	 * get the respective environent API auth credentials
	 * @author Perfaware
	 * @param resourceKey
	 */
	public static ApiDetails getApiDetails(String resourceKey) throws Exception {
		try {
				return new ApiDetails(PropertyFileReader.propertyMap.get("BaseURL"), PropertyFileReader.propertyMap.get("username"),
					PropertyFileReader.propertyMap.get("password"),realAPIResource.get(resourceKey));
		} catch (Exception e) {
			throw new NoSuchResourceException(resourceKey);
		}
	}	
	
	@AfterSuite(alwaysRun=true)
	public void sendReports() throws MessagingException {
		util.sendReportInEmail();
	}
}
