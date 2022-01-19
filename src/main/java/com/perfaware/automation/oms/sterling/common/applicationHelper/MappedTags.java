package com.perfaware.automation.oms.sterling.common.applicationHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MappedTags {
	manageItemsstk("ItemList","Item","manageItem_STK.xml"),
	adjustInventory("Items","Item","adjustInventory.xml", "OrderLine.Item.ItemID" , "Item.ItemID" ,"OrderLine.OrderedQty", "Item.Quantity","OrderLine.OrderLineReservations.OrderLineReservation.Node","ShipNode"),
	ScheduleOrder("ScheduleOrder","ScheduleOrder","ScheduleOrder.xml"),
	ReleaseOrder("ScheduleOrder","ScheduleOrder","ReleaseOrder.xml"),
	getOrderDetails("Order","Order","getOrderDetails.xml"),
	getOrderHoldList("OrderHoldType","OrderHoldType","getOrderHoldList.xml"),
	getOrderReleaseList("OrderRelease","OrderRelease","getOrderReleaseList.xml"),	
	confirmShipment_header("Shipment","Shipment","confirmShipment.xml"),
	confirmShipment("ShipmentLines","ShipmentLine","confirmShipment.xml"),
	consolidateToShipment("OrderRelease","OrderRelease","consolidateToShipment.xml"),
	DifarmaGetShipmentListForOrder("Order","Order","difarmaGetShipmentListForOrder.xml"),
	DifarmaGetShipmentList("Shipment","Shipment","difarmaGetShipmentList.xml"),
	DifarmaMobileRecordPickCompletion("ShipmentLines","ShipmentLine","difarmaMobileRecordPickCompletion.xml"),
	DifarmaMobileRecordPackCompletion("Shipment","ContainerDetail","difarmaMobileRecordPackCompletion.xml"),
	DifarmaMobileFinishCustomerPickService("ShipmentLines","ShipmentLine","difarmaMobileFinishCustomerPickService.xml"),
	DifarmaMobileConfirmShipment("Shipment","Shipment","difarmaMobileConfirmShipment.xml"),
	changeShipmentStatus("Shipment","Shipment","changeShipmentStatus.xml"),
	changeOrder("Order","Order","changeOrder.xml");
	
	MappedTags(String p,String a,String m,String... keysAndValues ){
		parentTag = p;
		actualTag = a;
		masterXmlFilePath =  m;
		    if ((keysAndValues.length & 1) != 0)
	            throw new IllegalArgumentException("keysAndValues has odd size: " + Arrays.toString(keysAndValues));
	            this.mappedTags = new HashMap<>();
	        for (int i = 0; i < keysAndValues.length; i += 2) {
	            if (keysAndValues[i] == null || keysAndValues[i + 1] == null)
	                throw new NullPointerException();
	            if (this.mappedTags.put(keysAndValues[i], keysAndValues[i + 1]) != null)
	                throw new IllegalArgumentException("keysAndValues has duplicate key named '" + keysAndValues[i] + "': " + Arrays.toString(keysAndValues));
	        }
	    
		
	}
	
	
	private String parentTag;
	public String getParentTag() {
		return parentTag;
	}

	private String actualTag;
	public String getActualTag() {
		return actualTag;
	}
	
	private Map<String,String> mappedTags;
	public Map<String, String> getMappedTags() {
		return mappedTags;
	}
	
    private String masterXmlFilePath;
	public String getMasterXmlFilePath() {
		return masterXmlFilePath;
	}
}
