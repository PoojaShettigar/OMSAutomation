package com.perfaware.automation.oms.sterling.common.customAssertions;

import java.util.List;

import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import org.testng.collections.Lists;
import com.aventstack.extentreports.Status;

public class HardAssertion extends Assertion{
	private List<String> assert_messages = Lists.newArrayList();
	
	@Override
	public void onAssertSuccess(IAssert<?> assertCommand) {
		//ExtentFactory.getInstance().getExtent().log(Status.PASS, assertCommand.getMessage());
	}

	@Override
	public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
		//System.out.println(ex.toString());
		//ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Assertion Failed for the requested resource");

	}
	public List<String> getAssertMessages() {
		return assert_messages;
	}
	
}