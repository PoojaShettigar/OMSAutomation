package com.perfaware.automation.oms.sterling.common.testcaseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.perfaware.automation.oms.sterling.common.applicationHelper.OmsConstants;
import com.perfaware.automation.oms.sterling.common.applicationHelper.OrderTypes;
import com.perfaware.automation.oms.sterling.common.applicationHelper.PaymentTypes;
import com.perfaware.automation.oms.sterling.common.applicationHelper.RequestHelper;
import com.perfaware.automation.oms.sterling.common.applicationHelper.TestData;
import com.perfaware.automation.oms.sterling.common.com.pageobjects.Page_OrderSummary;
import com.perfaware.automation.oms.sterling.common.applicationHelper.RequestEnum.ResourceKey;
import com.perfaware.automation.oms.sterling.common.customAssertions.SoftAssertion;
import com.perfaware.automation.oms.sterling.common.pojo.TestCaseDetails;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentFactory;
import com.perfaware.automation.oms.sterling.common.utils.UIUtilities;
import com.perfaware.automation.oms.sterling.common.utils.XMLUtil;

import io.restassured.response.Response;

public class APIMethods {
	public void StoreOrderFulfillment(RequestHelper helper,OrderTypes orderTypes, String noOfLines,Response response, Map<String, String> tempData,
			String orderNo, Logger logger, Map<String, List<Map<String, String>>> testData,
			Map<String, String> itemData,SoftAssertion softAssert,Page_OrderSummary summary) throws Exception   
	{
		String enterprise=tempData.get("Enterprise");
		
		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterprise);
		tempData.put("Order.OrderNo", orderNo);

		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_Created.getValue())||XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_Scheduled.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		
		
		//-resolveHold-----------------------------------------------------------------------------------------------------------------------------
		
		String isHoldApplied=XMLUtil.getXmlPath(response.asString()).getString("Order[0].@HoldFlag");
		String ohk = XMLUtil.getXmlPath(response.asString()).getString("Order[0].@OrderHeaderKey");
		String sellerOrganizationCode=XMLUtil.getXmlPath(response.asString()).getString("Order[0].@SellerOrganizationCode");
		String enterpriseCode = XMLUtil.getXmlPath(response.asString()).getString("Order[0].@EnterpriseCode");
		
		if(isHoldApplied.equalsIgnoreCase("Y")) {
			tempData.put("OrderHoldType.OrderHeaderKey", ohk);
			response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
					TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getorderholdlist, response, tempData, orderNo, logger,
					testData, itemData,null);
			String holdValue = XMLUtil.getXmlPath(response.asString()).getString("OrderHoldTypes[0].OrderHoldType.@HoldType");
			System.out.println(holdValue);
			tempData.clear();
			tempData.put("Order.OrderNo", orderNo);
			tempData.put("Order.EnterpriseCode", enterprise);
			tempData.put("Order.OrderHoldTypes.OrderHoldType.HoldType", holdValue);
			
			response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
					TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.changeOrder, response, tempData, orderNo, logger,
					testData, itemData,null);

			
		}

		//-scheduleOrder----------------------------------------------------------------------------------------------------------------------------
		
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.scheduleOrder, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		//-getOrderDetails----------------------------------------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("ScheduleOrder.EnterpriseCode",enterpriseCode);
		tempData.put("ScheduleOrder.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_Scheduled.getValue()) || XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_Released.getValue())||XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_BackroomPick.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		
		UIUtilities uiUtil=new UIUtilities();
		summary.clickViewOrderSummary();
		

		//-releaseOrder-------------------------------------------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("ReleaseOrder.EnterpriseCode", enterpriseCode);
		tempData.put("ReleaseOrder.OrderNo", orderNo);

		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.releaseOrder, response, tempData, orderNo, logger,
				testData, itemData,null);

		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		Boolean scheduled=true;
		int count=0;
		while(scheduled&&count!=5) {
			response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
					TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
					testData, itemData,null);
			if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_ReleaseToStore.getValue())||XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_BackroomPick.getValue())) {
				scheduled=false;	
				count=5;
			}else {
				scheduled=true;
				count++;
				Thread.sleep(10000);
			}
		}
		summary.clickViewOrderSummary();
		
		//-BackroomPick------------------------------------------------------------------------------------------------------------------------------
		ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		Boolean released=true;
		count=0;
		while(released&&count<=5) {
			
			response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
					TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
					testData, itemData,null);
			if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_BackroomPick.getValue())) {
				released=false;	
				count=5;
			}else {
				released=true;
				count++;
				Thread.sleep(10000);
			}
		}
		
		summary.clickViewOrderSummary();
		ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));

		//-DifarmaGetShipmentListForOrder------------------------------------------------------------------------------------------------------------------------------------

		tempData.clear();
		tempData.put("Order.OrderHeaderKey", ohk);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
		TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaGetShipmentListForOrder, response, tempData, orderNo, logger,
		testData, itemData,null);
		
		String shipmentKey=XMLUtil.getXmlPath(response.asString()).getString("ShipmentList[0].Shipment.@ShipmentKey");

		
		//-DifarmaGetShipmentList-----------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Shipment.ShipmentKey", shipmentKey);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaGetShipmentList, response, tempData, orderNo, logger,
				testData, itemData,null);

		//-DifarmaMobileRecordPickCompletion-----------------------------------------------------------------------------------------------
		
		String shipNode=XMLUtil.getXmlPath(response.asString()).getString("Shipments[0].Shipment.@ShipNode");
		String shipmentNo=XMLUtil.getXmlPath(response.asString()).getString("Shipments[0].Shipment.@ShipmentNo");
		String primeLineNo=XMLUtil.getXmlPath(response.asString()).getString("Shipments[0].Shipment.ShipmentLines[0].ShipmentLine.@PrimeLineNo");
		tempData.clear();
		tempData.put("Shipment.ShipmentKey",shipmentKey);
		tempData.put("ShipmentLine.OrderHeaderKey",ohk);
		tempData.put("Shipment.ShipNode", shipNode);
		
		Map<String, String> shipmentData = new HashMap<String, String>();
		shipmentData.put("Shipment.ShipmentKey",shipmentKey);
		shipmentData.put("Shipment.ShipmentNo",shipmentNo);
		shipmentData.put("Shipment.ShipNode", shipNode);
		
		for(int i=0;i<Integer.parseInt(noOfLines);i++) {
			tempData.put("ShipmentLine["+i+"].ShipmentLineKey",XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@ShipmentLineKey"));
			tempData.put("ShipmentLine["+i+"].OrderLineKey",XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@OrderLineKey"));
			tempData.put("ShipmentLine["+i+"].Quantity", XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@Quantity"));
			tempData.put("ShipmentLine["+i+"].BackroomPickedQuantity", XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@Quantity"));
			
			shipmentData.put("Shipment.Containers.Container.ContainerDetails.ContainerDetail.ShipmentLineKey",XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@ShipmentLineKey"));
			shipmentData.put("Shipment.Containers.Container.ContainerDetails.ContainerDetail.Quantity", XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@Quantity"));
			shipmentData.put("Shipment.Containers.Container.ContainerDetails.ContainerDetail.ShipmentLine.PrimeLineNo", Integer.toString(i+1));
			shipmentData.put("Shipment.Containers.Container.ContainerDetails.ContainerDetail.ShipmentLine.ShipmentLineNo", Integer.toString(i+1));
		}
		
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaMobileRecordPickCompletion, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		if(XMLUtil.getXmlPath(response.asString()).getString("Shipment[0].@ShipmentValidationStatus").equals(OmsConstants.OrderStatus_ReadyForPacking.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Shipment[0].@ShipmentValidationStatus"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Shipment[0].@ShipmentValidationStatus"), ExtentColor.BLUE));
		}
		else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Shipment[0].@ShipmentValidationStatus").equals(OmsConstants.OrderStatus_ReadyForPacking.getValue()));
		}

		//-DifarmaMobileRecordPackCompletion----------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Shipment.ShipmentNo", shipmentNo);
		tempData.put("Shipment.ShipmentKey",shipmentKey);
		tempData.put("Shipment.ShipNode", shipNode);
		tempData=shipmentData;

		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaMobileRecordPackCompletion, response, tempData, orderNo, logger,
				testData, itemData,null);
		summary.clickViewOrderSummary();
		
		//-getOrderDetails----------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
				testData, itemData,null);
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_PackComplete.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		
		//-confirmShipment----------------------------------------------------------------------------------------------
		tempData.clear();
		tempData.put("Shipment.ShipNode",shipNode);
		tempData.put("Shipment.ShipmentKey", shipmentKey);
		tempData.put("Shipment.EnterpriseCode", enterpriseCode);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.confirmShipment, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		summary.clickViewOrderSummary();
		
		//-getOrderDetails----------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
						TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
						testData, itemData,null);
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStaus_InTransitToCustomer.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		
		//-changeShipmentStatus----------------------------------------------------------------------------------------------
		tempData.clear();
		tempData.put("Shipment.ShipNode",shipNode);
		tempData.put("Shipment.ShipmentKey", shipmentKey);
		tempData.put("Shipment.ShipmentNo", shipmentNo);
		tempData.put("Shipment.SellerOrganizationCode", sellerOrganizationCode);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.changeShipmentStatus, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		summary.clickViewOrderSummary();
		
		//-getOrderDetails----------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
						TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
						testData, itemData,null);
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_DeliveredToCustomer.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		
	}
	
	public Map<String, String> XMLOrderCreation(RequestHelper helper,OrderTypes orderTypes, String noOfLines,Response response, Map<String, String> tempData,
			String orderNo, Logger logger, Map<String, List<Map<String, String>>> testData,
			Map<String, String> itemData,SoftAssertion softAssert) throws Exception   {

		TestCaseDetails testCaseDetails = new TestCaseDetails();

		helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
		TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.manageItem, response, tempData, orderNo, logger,
		testData, itemData,null);
		
		helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.adjustInventory, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.createOrder, response, tempData, orderNo, logger,
				testData, itemData,testCaseDetails);
		
		String ohk = XMLUtil.getXmlPath(response.asString()).getString("Order[0].@OrderHeaderKey");
		String enterpriseCode = XMLUtil.getXmlPath(response.asString()).getString("Order[0].@EnterpriseCode");
		
		tempData.clear();
		tempData.put("Enterprise", enterpriseCode);

		
		return tempData;
	}
	
	public Map<String, String> BopisOrderFulfillment(RequestHelper helper,OrderTypes orderTypes, String noOfLines,Response response, Map<String, String> tempData,
			String orderNo, Logger logger, Map<String, List<Map<String, String>>> testData,
			Map<String, String> itemData,SoftAssertion softAssert) throws Exception {
		String enterprise=tempData.get("Enterprise");

		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterprise);
		tempData.put("Order.OrderNo", orderNo);

		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_Created.getValue())||XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_Scheduled.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		
		//-scheduleOrder----------------------------------------------------------------------------------------------------------------------------
		
		String ohk = XMLUtil.getXmlPath(response.asString()).getString("Order[0].@OrderHeaderKey");
		String enterpriseCode = XMLUtil.getXmlPath(response.asString()).getString("Order[0].@EnterpriseCode");
		
		tempData.clear();
		tempData.put("ScheduleOrder.EnterpriseCode",enterpriseCode);
		tempData.put("ScheduleOrder.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.scheduleOrder, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		//-getOrderDetails----------------------------------------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Order.EnterpriseCode",enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_Scheduled.getValue()) || XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_ReleaseToStore.getValue())||XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_BackroomPick.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status" + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status" + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}

		//-releaseOrder-------------------------------------------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("ReleaseOrder.EnterpriseCode", enterpriseCode);
		tempData.put("ReleaseOrder.OrderNo", orderNo);

		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.releaseOrder, response, tempData, orderNo, logger,
				testData, itemData,null);

		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		Boolean scheduled=true;
		int count=0;
		while(scheduled&&count!=5) {
			response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
					TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
					testData, itemData,null);
			if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_ReleaseToStore.getValue())||XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_BackroomPick.getValue())) {
				scheduled=false;	
				count=5;
			}else {
				scheduled=true;
				count++;
				Thread.sleep(10000);
			}
		}
		

		//-BackroomPick------------------------------------------------------------------------------------------------------------------------------

		Boolean released=true;
		tempData.clear();
		tempData.put("Order.EnterpriseCode", enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		count=0;
		while(released&&count<=5) {
			
			response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
					TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
					testData, itemData,null);
			if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_BackroomPick.getValue())) {
				released=false;	
				count=5;
			}else {
				released=true;
				count++;
				Thread.sleep(10000);
			}
		}
		
		//-DifarmaGetShipmentListForOrder------------------------------------------------------------------------------------------------------------------------------------

		tempData.clear();
		tempData.put("Order.OrderHeaderKey", ohk);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
		TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaGetShipmentListForOrder, response, tempData, orderNo, logger,
		testData, itemData,null);
		
		String shipmentKey=XMLUtil.getXmlPath(response.asString()).getString("ShipmentList[0].Shipment.@ShipmentKey");

		
		//-DifarmaGetShipmentList-----------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Shipment.ShipmentKey", shipmentKey);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaGetShipmentList, response, tempData, orderNo, logger,
				testData, itemData,null);

		//-DifarmaMobileRecordPickCompletion-----------------------------------------------------------------------------------------------
		Map<String, String> shipData=new HashMap<String, String>();
		String shipNode=XMLUtil.getXmlPath(response.asString()).getString("Shipments[0].Shipment.@ShipNode");
		String shipmentNo=XMLUtil.getXmlPath(response.asString()).getString("Shipments[0].Shipment.@ShipmentNo");
		String primeLineNo=XMLUtil.getXmlPath(response.asString()).getString("Shipments[0].Shipment.ShipmentLines[0].ShipmentLine.@PrimeLineNo");
		
		shipData.put("Shipment.ShipmentKey",shipmentKey);
		shipData.put("Shipment.ShipmentNo",shipmentNo);
		shipData.put("Shipment.ShipNode", shipNode);
		
		tempData.clear();
		tempData.put("Shipment.ShipmentKey",shipmentKey);
		tempData.put("ShipmentLine.OrderHeaderKey",ohk);
		tempData.put("Shipment.ShipNode", shipNode);
		for(int i=0;i<Integer.parseInt(noOfLines);i++) {
			tempData.put("ShipmentLine["+i+"].ShipmentLineKey",XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@ShipmentLineKey"));
			tempData.put("ShipmentLine["+i+"].OrderLineKey",XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@OrderLineKey"));
			tempData.put("ShipmentLine["+i+"].Quantity", XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@Quantity"));
			tempData.put("ShipmentLine["+i+"].BackroomPickedQuantity", XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@Quantity"));
			
			shipData.put("ShipmentLine["+i+"].ShipmentLineKey",XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@ShipmentLineKey"));
			shipData.put("ShipmentLine["+i+"].Quantity", XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@Quantity"));
			shipData.put("ShipmentLine["+i+"].PickedQty", XMLUtil.getXmlPath(response.asString()).getString("Shipments.Shipment.ShipmentLines.ShipmentLine["+i+"].@Quantity"));
		}

		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaMobileRecordPickCompletion, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		
		if(XMLUtil.getXmlPath(response.asString()).getString("Shipment[0].@ShipmentValidationStatus").equals(OmsConstants.OrderStatus_ReadyForPacking.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Shipment[0].@ShipmentValidationStatus"));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Shipment[0].@ShipmentValidationStatus").equals(OmsConstants.OrderStatus_ReadyForPacking.getValue()));
		}

		//-getOrderDetails----------------------------------------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Order.EnterpriseCode",enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
								TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
								testData, itemData,null);
						
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_ReadyforCustomer.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		
		//-DifarmaMobileFinishCustomerPickService----------------------------------------------------------------------------------------------------------------------------
		tempData.clear();
		tempData.put("Shipment.ShipmentKey",shipmentKey);
		tempData.put("Shipment.AssignedToUserId", shipNode);
//		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
//				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.changeShipment, response, tempData, orderNo, logger,
//				testData, itemData,null);
		
		Thread.sleep(10000);
		tempData.clear();
		tempData=shipData;
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
				TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.DifarmaMobileFinishCustomerPickService, response, tempData, orderNo, logger,
				testData, itemData,null);
		
		//-getOrderDetails----------------------------------------------------------------------------------------------------------------------------
		
		tempData.clear();
		tempData.put("Order.EnterpriseCode",enterpriseCode);
		tempData.put("Order.OrderNo", orderNo);
		response = helper.requestCreationInOMS(orderTypes, noOfLines, PaymentTypes.CC.toString(),
						TestData.LineTaxes_WO_GW_TAX, null, ResourceKey.getOrderDetails, response, tempData, orderNo, logger,
						testData, itemData,null);
				
		if(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status").equals(OmsConstants.OrderStatus_CustomerPickedUp.getValue())) {
			softAssert.assertTrue(true,"Order is in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
			ExtentFactory.getInstance().getExtent().pass(MarkupHelper.createLabel(XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"), ExtentColor.BLUE));
		}else {
			softAssert.assertTrue(false,"Order is not in valid status " + XMLUtil.getXmlPath(response.asString()).getString("Order[0].@Status"));
		}
		return tempData;
	}
	
	
}
