package com.perfaware.automation.oms.sterling.common.applicationHelper;

public enum OmsConstants {
	
	OrderStatus_Created("Created"),
	HoldStatus_Created("Created"),
	HoldStatus_Resolved("Resolved"),
	OrderStatus_Scheduled("Scheduled"),
	OrderStatus_Released("Released"),
	OrderStatus_ReleaseToStore("Released to store"),
	OrderStatus_BackroomPick("Ready for Backroom pick"),
	OrderStatus_Shipped("Shipped"),
	OrderStatus_Cancelled("Cancelled"),
	OrderStatus_ReadyForPacking("pickComplete"),
	OrderStatus_PackComplete("Packing Complete"),
	OrderStaus_InTransitToCustomer("In Transit to Customer"),
	OrderStatus_ReadyforCustomer("Ready for Customer"),
	OrderStatus_CustomerPickedUp("Customer Picked Up"),
	OrderStatus_DeliveredToCustomer("Delivered To Customer"),
	InventoryReservation_DemandType("RESERVED");



	String value;
	public String getValue() {
		return value;
	}
	OmsConstants(String v){
		value = v;
	}
}
