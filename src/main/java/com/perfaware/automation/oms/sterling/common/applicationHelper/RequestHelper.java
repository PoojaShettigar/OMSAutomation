package com.perfaware.automation.oms.sterling.common.applicationHelper;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.perfaware.automation.oms.sterling.common.utils.XMLUtil;
import com.perfaware.automation.oms.sterling.common.applicationHelper.RequestEnum.ResourceKey;
import com.perfaware.automation.oms.sterling.common.customAssertions.HardAssertion;
import com.perfaware.automation.oms.sterling.common.customAssertions.SoftAssertion;
import com.perfaware.automation.oms.sterling.common.fileReader.ExcelFileReader;
import com.perfaware.automation.oms.sterling.common.pojo.TestCaseDetails;
import com.perfaware.automation.oms.sterling.common.utils.Utilities;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RequestHelper {
	public RequestSpecification httpRequest;
	protected static String curDir = System.getProperty("user.dir");
	private static final Logger LOGGER = Logger.getLogger(RequestHelper.class);
	HardAssertion customAssert = new HardAssertion();
	ApplicationMethods helper = null;
	public RequestHelper() {
		helper = new ApplicationMethods();
	}
	
	ExcelFileReader loadTestData=new ExcelFileReader();
	public List<Map<String, String>> getDetails(FileInputStream file, XSSFSheet worksheet, String orderType)
			throws Exception {
		List<Map<String, String>> details = loadTestData.readTestDataFromExcel(file, worksheet, orderType);
		return details;
	}
	
	public Map<String, String> generateItemIds_ForOrder(OrderTypes order_type, String noofLines) {
		Map<String, String> dataGenerate = new HashMap<String, String>();
		String orderTypes[] = order_type.toString().split("_");
		String noOfItems[] = noofLines.split("_");
		int noofols;
		int startnode = 1;
		String itemId;

		for (int j = 0; j < orderTypes.length; j++) {
			if (orderTypes[j].toString().contains("STK")) {
				noofols = Integer.valueOf(noOfItems[j]);
				for (int i = 0; i < noofols; i++) {
					itemId = createItemSTKId();
					dataGenerate.put("STK_" + startnode, itemId);
					startnode++;
				}
			}
		}

		return dataGenerate;

	}
	public String createItemSTKId() {
		return Utilities.generateRandomString(6);
	}

	public String createItemDYOId() {
		return Utilities.generateRandomString(6);
	}
	
	public Response requestCreationInOMS(OrderTypes orderType, String oLineNos, String paymentType, TestData lineTaxes,
			TestData lineCharges, ResourceKey resourceKey, Response response, Map<String, String> tempData,
			String orderNo, Logger logger, Map<String, List<Map<String, String>>> testData,
			Map<String, String> itemData, TestCaseDetails testCaseDetails) throws Exception {
		String payload;
		
		if (resourceKey.equals(ResourceKey.manageItem)) {
			payload = helper.manageItem(itemData);
			if (payload != null) {
				logger.info("Pre-condition for Test: Load Item ");
				//ExtentFactory.getInstance().getExtent().log(Status.PASS, "Pre-condition: Load Item");				
				response = helper.createRequest(ResourceKey.manageItem, payload);
				
				customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_NO_CONTENT,
						"Status code for " + ResourceKey.manageItem + " matches with expected code"); 
			}
		}
		
		if (resourceKey.equals(ResourceKey.adjustInventory)) {
			// Adjust Inventory
			payload = helper.createAdjInvXml(orderType, oLineNos, testData, false);
			if (payload != null) {
				if (itemData != null) {
					payload = setItemIdsInAdjInvPayload(payload, itemData);
				}
				logger.info("Pre-condition for Test: Adjust Inventory ");
				//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: Adjust Inventory");
				response = helper.createRequest(ResourceKey.adjustInventory, payload);
				customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_NO_CONTENT,
						"Status code for " + ResourceKey.adjustInventory + " matches with expected code");
			}
		}
		
		if (resourceKey.equals(ResourceKey.getATP)) {
			payload = helper.getATP(tempData);
			//logger.info("Pre-condition for Test: getOrderDetails");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderDetails");
			response = helper.createRequest(ResourceKey.getATP, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.getATP + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.createOrder)) {
			// Create Order		
			
			payload = helper.createOrderXml(orderType, oLineNos, testData, paymentType, orderNo, lineTaxes,
					lineCharges,testCaseDetails).getCreatOrderPayload();

			if (itemData != null)
				payload = setItemIdsInOrderPayload(payload, itemData);

			logger.info("Pre-condition for Test: Create Order ");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: Create Order");
			response = helper.createRequest(ResourceKey.createOrder, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.createOrder + " matches with expected code");
			
		}
		
		if (resourceKey.equals(ResourceKey.createOrderBopis)) {
			// Create Order		
			
			payload = helper.createOrderXmlBOPIS(orderType, oLineNos, testData, paymentType, orderNo, lineTaxes,
					lineCharges,testCaseDetails).getCreatOrderPayload();

			if (itemData != null)
				payload = setItemIdsInOrderPayload(payload, itemData);

			logger.info("Pre-condition for Test: Create Order ");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: Create Order");
			response = helper.createRequest(ResourceKey.createOrderBopis, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.createOrderBopis + " matches with expected code");
			
		}
		
		if (resourceKey.equals(ResourceKey.getOrderDetails)) {
			payload = helper.getOrderDetails(tempData);
			logger.info("Pre-condition for Test: getOrderDetails");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderDetails");
			response = helper.createRequest(ResourceKey.getOrderDetails, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.getOrderDetails + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.getorderholdlist)) {

			if (resourceKey.equals(ResourceKey.getorderholdlist)) {
				// Get Order Hold List
				payload = helper.getOrderHoldList(tempData);
				logger.info("Pre-condition for Test: Get the Order Hold Details");
				//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderDetails");
				response = helper.createRequest(ResourceKey.getorderholdlist, payload);
				customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
						"Status code for " + ResourceKey.getorderholdlist + " matches with expected code");
			}
		}

		if (resourceKey.equals(ResourceKey.changeOrder)) {
			// Change order if any holds
			payload = helper.changeOrder(response, tempData);
			if (payload != null) {
				logger.info("Pre-condition for Test: Change Order for resolving the holds");
				//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderDetails");
				response = helper.createRequest(ResourceKey.changeOrder, payload);
				customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
						"Status code for " + ResourceKey.changeOrder + " matches with expected code");

			}
		}
		
		if (resourceKey.equals(ResourceKey.scheduleOrder)) {
			payload = helper.scheduleOrder(tempData);
			logger.info("Pre-condition for Test: Change Order for resolving the holds");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: Change Order for resolving the holds");
			response = helper.createRequest(ResourceKey.scheduleOrder, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_NO_CONTENT,
					"Status code for " + ResourceKey.scheduleOrder + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.releaseOrder)) {
			payload = helper.releaseOrder(tempData);
			logger.info("Pre-condition for Test: Change Order for resolving the holds");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: Change Order for resolving the holds");
			response = helper.createRequest(ResourceKey.releaseOrder, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_NO_CONTENT,
					"Status code for " + ResourceKey.releaseOrder + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.getOrderReleaseList)) {
			payload = helper.getOrderReleaseList(tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.getOrderReleaseList, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.getOrderReleaseList + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.consolidateToShipment)) {
			payload = helper.consolidateToShipment(tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.consolidateToShipment, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.consolidateToShipment + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.DifarmaGetShipmentList)) {
			payload = helper.DifarmaGetShipmentList(tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.DifarmaGetShipmentList, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.DifarmaGetShipmentList + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.getShipmentList)) {
			payload = helper.getShipmentList(tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.getShipmentList, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.getShipmentList + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.DifarmaGetShipmentListForOrder)) {
			payload = helper.DifarmaGetShipmentListForOrder(tempData);
			System.out.println(payload);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.DifarmaGetShipmentListForOrder, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.DifarmaGetShipmentListForOrder + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.getShipmentListForOrder)) {
			payload = helper.getShipmentListForOrder(tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.getShipmentListForOrder, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.getShipmentListForOrder + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.DifarmaMobileRecordPickCompletion)) {
			payload = helper.DifarmaMobileRecordPickCompletion(tempData, oLineNos);
			payload=setDataRecordPickCompletionPayload(payload, tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.DifarmaMobileRecordPickCompletion, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.DifarmaMobileRecordPickCompletion + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.DifarmaMobileRecordPackCompletion)) {
			payload = helper.DifarmaMobileRecordPackCompletion(tempData,oLineNos);
			payload=setDataRecordPackCompletionPayload(payload, tempData);
			logger.info("Pre-condition for Test: DifarmaMobileRecordPickCompletion");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: DifarmaMobileRecordPickCompletion");
			response = helper.createRequest(ResourceKey.DifarmaMobileRecordPackCompletion, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.DifarmaMobileRecordPackCompletion + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.DifarmaMobileConfirmShipment)) {
			payload = helper.DifarmaMobileConfirmShipment(tempData, oLineNos);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.DifarmaMobileConfirmShipment, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.DifarmaMobileConfirmShipment + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.changeShipmentStatus)) {
			payload = helper.changeShipmentStatus(tempData, oLineNos);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.changeShipmentStatus, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.changeShipmentStatus + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.confirmShipment)) {
			payload = helper.confirmShipment(tempData, oLineNos);
			
			payload=setDataInConfirmShipmentPayload(payload, tempData,testData);
			logger.info("Pre-condition for Test: Mock Confirm Shipment");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: Mock Confirm Shipment");
			response = helper.createRequest(ResourceKey.confirmShipment, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.confirmShipment + " matches with expected code");
		}
		if (resourceKey.equals(ResourceKey.DifarmaMobileFinishCustomerPickService)) {
			payload = helper.DifarmaMobileFinishCustomerPickService(tempData, oLineNos);
			payload=setDataFinishCustomerPickPayload(payload, tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.DifarmaMobileFinishCustomerPickService, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.DifarmaMobileFinishCustomerPickService + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.syncLoadedInventory)) {
			payload = helper.syncLoadedInventory(tempData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.syncLoadedInventory, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.syncLoadedInventory + " matches with expected code");
		}
		

		if (resourceKey.equals(ResourceKey.findInventory)) {
			payload = helper.findInventory(tempData, itemData);
			payload=setDatafindInventoryPayload(payload, tempData,itemData);
			payload=setItemIdsInfindInventoryPayloadPayload(payload,itemData);
			//logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.findInventory, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.findInventory + " matches with expected code");
		}
		
		if (resourceKey.equals(ResourceKey.reserveAvailableInventory)) {
			payload = helper.reserveAvailableInventory(tempData, itemData);
			payload=setDatareserveAvailableInventoryPayload(payload, tempData,itemData);
			payload=setItemIdsInreserveAvailableInventoryPayloadPayload(payload,itemData);
			logger.info("Pre-condition for Test: getOrderReleaseList");
			//ExtentFactory.getInstance().getExtent().log(Status.INFO, "Pre-condition: getOrderReleaseList");
			response = helper.createRequest(ResourceKey.reserveAvailableInventory, payload);
			customAssert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK,
					"Status code for " + ResourceKey.reserveAvailableInventory + " matches with expected code");
		}
		
		return response;

	}
	public String setItemIdsInAdjInvPayload(String payload, Map<String, String> items) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.adjustInventory.getActualTag());
		String[] keysplit;
		for (Map.Entry<String, String> entry : items.entrySet()) {
			keysplit = entry.getKey().split("_");
			for (int i = 0; i < crawlNodeList.getLength(); i++) {
				node = crawlNodeList.item(i);
				if (keysplit[1].equals(String.valueOf(i + 1))) {
					XMLUtil.setDataInNodeAtt((Element) node, "Item.ItemID", entry.getValue());
				}
			}
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	public String setItemIdsInOrderPayload(String payload, Map<String, String> items) throws Exception {

		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(TestData.STKSTH.getActualTag());
		String temp;
		String[] keysplit;
		for (Map.Entry<String, String> entry : items.entrySet()) {
			keysplit = entry.getKey().split("_");
			for (int i = 0; i < crawlNodeList.getLength(); i++) {
				node = crawlNodeList.item(i);
				temp = XMLUtil.getDataInNodeAtt((Element) node, "OrderLine.PrimeLineNo", "PrimeLineNo");
				if (keysplit[1].equals(temp)) {
//					XMLUtil.setDataInNodeAtt((Element) node,
//							"OrderLine.OrderLineReservations.OrderLineReservation.ItemID", entry.getValue());
					XMLUtil.setDataInNodeAtt((Element) node, "OrderLine.Item.ItemID", entry.getValue());
				}
			}
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setDataRecordPickCompletionPayload(String payload, Map<String, String> orderData) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.DifarmaMobileRecordPickCompletion.getActualTag());
		String[] keysplit;

		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			node = crawlNodeList.item(i);
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.OrderHeaderKey",orderData.get("ShipmentLine.OrderHeaderKey"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.ShipmentLineKey", orderData.get("ShipmentLine["+i+"].ShipmentLineKey"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.OrderLineKey", orderData.get("ShipmentLine["+i+"].OrderLineKey"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.Quantity", orderData.get("ShipmentLine["+i+"].Quantity"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.BackroomPickedQuantity", orderData.get("ShipmentLine["+i+"].BackroomPickedQuantity"));

		}

		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setDataRecordPackCompletionPayload(String payload, Map<String, String> orderData) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.DifarmaMobileRecordPickCompletion.getActualTag());
		String[] keysplit;

		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			node = crawlNodeList.item(i);
			XMLUtil.setDataInNodeAtt((Element) node, "ContainerDetail.ShipmentLineKey",orderData.get("ContainerDetail.ShipmentLineKey"));
			XMLUtil.setDataInNodeAtt((Element) node, "ContainerDetail.Quantity", orderData.get("ContainerDetail["+i+"].Quantity"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.PrimeLineNo", orderData.get("ShipmentLine["+i+"].PrimeLineNo"));
		}

		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setDataInConfirmShipmentPayload(String payload, Map<String, String> orderData, Map<String, List<Map<String, String>>> testData) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.confirmShipment.getActualTag());
		String[] keysplit;
		System.out.println("orderData-->"+orderData);
		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			node = crawlNodeList.item(i);
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.OrderNo", orderData.get("ShipmentLine.OrderNo"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.OrderReleaseKey", orderData.get("ShipmentLine.OrderReleaseKey"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.PrimeLineNo",Integer.toString(i+1));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.Quantity",orderData.get("ShipmentLine.Quantity"));
		}

		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setDataFinishCustomerPickPayload(String payload, Map<String, String> orderData) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.DifarmaMobileRecordPickCompletion.getActualTag());
		String[] keysplit;

		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			node = crawlNodeList.item(i);
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.OrderHeaderKey",orderData.get("ShipmentLine.OrderHeaderKey"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.ShipmentLineKey", orderData.get("ShipmentLine["+i+"].ShipmentLineKey"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.ShipmentLineNo", Integer.toString(i+1));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.Quantity", orderData.get("ShipmentLine["+i+"].Quantity"));
			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.PickedQty", orderData.get("ShipmentLine["+i+"].PickedQty"));

		}

		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setDatafindInventoryPayload(String payload, Map<String, String> orderData,Map<String, String> itemData) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.findInventory.getActualTag());
		String[] keysplit;

		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			node = crawlNodeList.item(i);;
			XMLUtil.setDataInNodeAtt((Element) node, "PromiseLine.LineId", Integer.toString(i+1));
		}

		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setDatareserveAvailableInventoryPayload(String payload, Map<String, String> orderData,Map<String, String> itemData) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.reserveAvailableInventory.getActualTag());
		String[] keysplit;

		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			node = crawlNodeList.item(i);;
			XMLUtil.setDataInNodeAtt((Element) node, "PromiseLine.LineId", Integer.toString(i+1));
		}

		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	
	public String setItemIdsInfindInventoryPayloadPayload(String payload, Map<String, String> items) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.findInventory.getActualTag());
		String[] keysplit;
		for (Map.Entry<String, String> entry : items.entrySet()) {
			keysplit = entry.getKey().split("_");
			for (int i = 0; i < crawlNodeList.getLength(); i++) {
				node = crawlNodeList.item(i);
				if (keysplit[1].equals(String.valueOf(i + 1))) {
					XMLUtil.setDataInNodeAtt((Element) node, "PromiseLine.ItemID", entry.getValue());
				}
			}
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setItemIdsInreserveAvailableInventoryPayloadPayload(String payload, Map<String, String> items) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.reserveAvailableInventory.getActualTag());
		String[] keysplit;
		for (Map.Entry<String, String> entry : items.entrySet()) {
			keysplit = entry.getKey().split("_");
			for (int i = 0; i < crawlNodeList.getLength(); i++) {
				node = crawlNodeList.item(i);
				if (keysplit[1].equals(String.valueOf(i + 1))) {
					XMLUtil.setDataInNodeAtt((Element) node, "PromiseLine.ItemID", entry.getValue());
				}
			}
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public String setDataloadInventoryMismatch(String payload, Map<String, String> orderData) throws Exception {
		Document finalDoc = XMLUtil.convertStringToXmlDoc(payload);
		Node node;
		NodeList crawlNodeList = finalDoc.getElementsByTagName(MappedTags.DifarmaMobileRecordPickCompletion.getActualTag());
		String[] keysplit;

		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			node = crawlNodeList.item(i);
//			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.OrderHeaderKey",orderData.get("ShipmentLine.OrderHeaderKey"));
//			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.ShipmentLineKey", orderData.get("ShipmentLine["+i+"].ShipmentLineKey"));
//			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.ShipmentLineNo", Integer.toString(i+1));
//			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.Quantity", orderData.get("ShipmentLine["+i+"].Quantity"));
//			XMLUtil.setDataInNodeAtt((Element) node, "ShipmentLine.PickedQty", orderData.get("ShipmentLine["+i+"].PickedQty"));

		}

		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	public void assertValidationsXml(Response res, Map<String, String> data, SoftAssertion softAssert) {
		String tempVal, msg;
		for (Map.Entry<String, String> entry : data.entrySet()) {
			tempVal = XMLUtil.getXmlPath(res.asString()).getString(entry.getKey().split("%MSG%")[0]);
			msg = entry.getKey().split("%MSG%")[1];
			softAssert.assertEquals(tempVal, entry.getValue(), msg + " " + tempVal);

		}
		softAssert.assertAll();
	}


}

