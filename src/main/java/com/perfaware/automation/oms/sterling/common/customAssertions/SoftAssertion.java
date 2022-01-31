package com.perfaware.automation.oms.sterling.common.customAssertions;

import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;
import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentTestManager;



public class SoftAssertion extends SoftAssert{
	
	@Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
		ExtentTestManager.getTest().log(Status.PASS, "Assertion Passed for " +assertCommand.getMessage());
    }
    
    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
    	System.out.println(ex.toString());
    	ExtentTestManager.getTest().log(Status.FAIL, "Assertion Failed due to "+assertCommand.getMessage());        
    }

}
