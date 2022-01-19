package com.perfaware.automation.oms.sterling.common.applicationHelper;


public enum OrderTypes {
	STKSTH("STK_HEADER"),
	;

	 private TestData header_Type;
		

		
		public TestData getHeader_Type() {
			return header_Type;
		}
		OrderTypes(String m){
			header_Type = TestData.valueOf(m);
		}

}
