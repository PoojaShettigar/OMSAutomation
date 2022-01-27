package com.perfaware.automation.oms.sterling.common.applicationHelper;

/**
 * This class defines the generic enum for the resources to be used across the framework
 * @author Perfaware
 *
 */
public class RequestEnum {
	
	public enum ResourceKey{
		adjustInventory,
		createOrder,
		createOrderBopis,
		getorderholdlist,
		getOrderDetails,
		scheduleOrder,
		releaseOrder,
		preSourcingGetInvAvail,
		manageItem,
		confirmShipment, 
		getOrderReleaseList,
		consolidateToShipment,
		DifarmaGetShipmentListForOrder,
		DifarmaGetShipmentList,
		DifarmaMobileRecordPickCompletion,
		DifarmaMobileRecordPackCompletion,
		DifarmaMobileFinishCustomerPickService,
		DifarmaMobileConfirmShipment,
		changeShipmentStatus,
		changeShipment,
		changeOrder,
		processOrderPayments,
		getATP,
		getShipmentListForOrder,
		getShipmentList,
		syncLoadedInventory,
		loadInventoryMismatch,
		findInventory,
		reserveAvailableInventory;
	
		
		
		}

}