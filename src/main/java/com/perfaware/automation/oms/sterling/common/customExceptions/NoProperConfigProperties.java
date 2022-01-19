package com.perfaware.automation.oms.sterling.common.customExceptions;

@SuppressWarnings("serial")
public class NoProperConfigProperties extends Exception { 
	
    public NoProperConfigProperties() {
        super("Properties not set ");
    }
}