package com.perfaware.automation.oms.sterling.common.testreportsUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;

public class ExtentManager {
	public static ExtentReports extentReports;
	private static ExtentSparkReporter sparkReport;
	public static String reportFolder;
	public static HashMap<String, String> reportPropertyMap;
	private static final Logger LOGGER = Logger.getLogger(ExtentManager.class);
	
	public synchronized static void getReporter() {
		try {
			if (extentReports == null) {
				String reportPath=reportFolder+ExtentManager.reportPropertyMap.get("htmlReportName");
				extentReports = generateReport(reportPath);
			}
		} catch (Exception e) {
			LOGGER.info("Not able to generate Extent HTML Reports");
			System.exit(0);
		}
	}
	
	private static ExtentReports generateReport(String reportPath) throws IOException {
		ExtentReports extent;
		new File(reportFolder).mkdir();
		sparkReport = new ExtentSparkReporter(reportPath);
		extent = new ExtentReports();
		extent.attachReporter(sparkReport);		
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
		LOGGER.info("HTML report created : " + reportPropertyMap.get("htmlReportTitle"));
		return extent;
	}
	
	/**
	 * This function writes the HTML report created
	 * @author Perfaware
	 */
	public static synchronized void printResults() {
		extentReports.flush();
		LOGGER.info("HTML report saved at "+reportFolder+"/"+ExtentManager.reportPropertyMap.get("htmlReportName"));
	}
	
	

}
