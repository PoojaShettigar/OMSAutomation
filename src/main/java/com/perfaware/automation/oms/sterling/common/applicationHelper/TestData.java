package com.perfaware.automation.oms.sterling.common.applicationHelper;

public enum TestData {
	STK_HEADER("Order","Order","createOrder.xml"),
	STKSTH("OrderLines","OrderLine","createOrder.xml"),
	STKBOPIS("OrderLines","OrderLine","createOrder.xml"),
	CC("PaymentMethods","PaymentMethod"),
	GC("PaymentMethods","PaymentMethod"),
	PP("PaymentMethods","PaymentMethod"),
	LineCharges_Type_Shipping("LineCharges","LineCharge"),
	LineTaxes_WO_GW_TAX("LineTaxes","LineTax")
	;
		
	private String parentTag;
	public String getParentTag() {
		return parentTag;
	}

	private String actualTag;
	public String getActualTag() {
		return actualTag;
	}
    
	private String masterXmlFilePath;	
	public String getMasterXmlFilePath() {
		return masterXmlFilePath;
	}

	TestData(String p,String a,String m){
		parentTag = p;
		actualTag = a;
		masterXmlFilePath = m;
	}
	
	TestData(String p,String a){
		parentTag = p;
		actualTag = a;
		
	}
}
