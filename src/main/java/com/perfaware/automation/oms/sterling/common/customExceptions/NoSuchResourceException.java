package com.perfaware.automation.oms.sterling.common.customExceptions;

@SuppressWarnings("serial")
public class NoSuchResourceException extends Exception { 
	public NoSuchResourceException(String resourceKey) {
        super("No such resource with key : "+resourceKey);
    }
}