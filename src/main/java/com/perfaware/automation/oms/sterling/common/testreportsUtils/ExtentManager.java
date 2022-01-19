package com.perfaware.automation.oms.sterling.common.testreportsUtils;

import java.util.HashMap;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;

public class ExtentManager {
	public static ExtentReports extentReports;
	public static HashMap<String, String> reportPropertyMap;
	public static String reportFolder;
	
	public static ExtentReports setupExtentReport() throws Exception {
		String reportPath=reportFolder+ExtentManager.reportPropertyMap.get("htmlReportName");
		ExtentSparkReporter sparkReport = new ExtentSparkReporter(reportPath);
		
		extentReports = new ExtentReports();
		extentReports.attachReporter(sparkReport);
		extentReports.setSystemInfo("Executed on Environment: ", PropertyFileReader.propertyMap.get("environment"));
		extentReports.setSystemInfo("Executed on Browser: ", PropertyFileReader.propertyMap.get("browser"));
		extentReports.setSystemInfo("Executed on OS: ", System.getProperty("os.name"));
		extentReports.setSystemInfo("Executed by User: ", reportPropertyMap.get("client"));
		
		sparkReport.config().setDocumentTitle(reportPropertyMap.get("htmlReportTitle"));
		sparkReport.config().setTheme(Theme.STANDARD);
		sparkReport.config().setReportName(reportPropertyMap.get("client"));
		sparkReport.config().setTimeStampFormat(reportPropertyMap.get("TimeStampFormat"));
		
		return extentReports;
	}

}
