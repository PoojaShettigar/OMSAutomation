package com.perfaware.automation.oms.sterling.common.testreportsUtils;

	import java.util.Map;

	import org.apache.commons.collections.map.HashedMap;
	import com.aventstack.extentreports.ExtentTest;
	import com.aventstack.extentreports.Status;

	/**
	 * This class defines the logging into Extent Reports based upon the current thread execution
	 * @author Nagarro
	 *
	 */
	public class ExtentTestManager {
		
		static Map extentTestMap = new HashedMap();
			
		public static synchronized ExtentTest getTest() {	
//			extentTestMap.get((int)(long)(Thread.currentThread().getId()));
//			ExtentTest etest = (ExtentTest) extentTestMap.get((int)(long)(Thread.currentThread().getId()));
			return (ExtentTest) extentTestMap.get((int)(long)(Thread.currentThread().getId()));
		}
		
		
		public static synchronized ExtentTest startTest(String name) {	
			ExtentTest test=ExtentManager.extentReports.createTest(name);
			extentTestMap.put((int)(long)(Thread.currentThread().getId()), test);
			return test;
		}
		
		
		

	}

