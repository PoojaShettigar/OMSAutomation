package com.perfaware.automation.oms.sterling.common.applicationHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.fileReader.ExcelFileReader;
import com.perfaware.automation.oms.sterling.common.pojo.ApiDetails;
import com.perfaware.automation.oms.sterling.common.pojo.TestCaseDetails;
import com.perfaware.automation.oms.sterling.common.requestPayloads.RequestPayload;
import com.perfaware.automation.oms.sterling.common.requestPayloads.RestAssuredRequest;
import com.perfaware.automation.oms.sterling.common.testcaseUtils.TestCaseBase;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentFactory;
import com.perfaware.automation.oms.sterling.common.utils.Utilities;
import com.perfaware.automation.oms.sterling.common.utils.XMLUtil;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApplicationMethods {
	
	public RequestSpecification httpRequest;
	public ExcelFileReader loadTestData = new ExcelFileReader();
	protected static String curDir = System.getProperty("user.dir");
	private static final Logger LOGGER = Logger.getLogger(RequestHelper.class);
	
	private String getFilePath(String testName, String fileName, Boolean isReadFromExcel) {
		String filePath = "";
		if (isReadFromExcel)
			filePath = curDir + "/src/main/resources/xmls/" + fileName;
		return filePath;
	}
	
	/**
	 * This function return date in the format 'yyyy-MM-dd'
	 * 
	 * @author Perfaware
	 *             
	 */
	
	public String getSimpleDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateString = sdf.format(date);
		return dateString;
	}

	/**
	 * This function return the response of the request
	 * 
	 * @author Perfaware
	 * @param key       - resource key to be called 
	 * @param payload   - body of the POST call
	 * 
	 */
	public Response createRequest(RequestEnum.ResourceKey key, String payload) throws Exception {
		try {
			ApiDetails apiDetails = TestCaseBase.getApiDetails(key.toString());
			RequestPayload createRequest = new RequestPayload(Method.POST.name());
			createRequest.resource = apiDetails.getBaseURL() + apiDetails.getResource();
			createRequest.body = payload;	
			createRequest.username = apiDetails.getUserName();
			createRequest.password = apiDetails.getPassword();
			Response res = RestAssuredRequest.sendAPIRequest(createRequest);
			return res;
		} catch (Exception e) {
			LOGGER.error("Error while sending " + key.toString() + " request.");
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Error while sending "+key.toString() + " request.");
			throw e;
		}
	}
	
	/**
	 * This function return the response of the request
	 * 
	 * @author Perfaware
	 * @param key       - resource key to be called 
	 * @param payload   - body of the POST call
	 * 
	 */
	public Response createRequestJson(RequestEnum.ResourceKey key, String payload) throws Exception {
		try {
			ApiDetails apiDetails = TestCaseBase.getApiDetails(key.toString());
			RequestPayload createRequest = new RequestPayload(Method.POST.name(),"application/json");
			createRequest.resource = apiDetails.getBaseURL() + apiDetails.getResource();
			createRequest.body = payload;	
			createRequest.username = apiDetails.getUserName();
			createRequest.password = apiDetails.getPassword();
			Response res = RestAssuredRequest.sendAPIRequest(createRequest);
			return res;
		} catch (Exception e) {
			LOGGER.error("Error while sending " + key.toString() + " request.");
			ExtentFactory.getInstance().getExtent().log(Status.FAIL, "Error while sending "+key.toString() + " request.");
			throw e;
		}
	}
	
	public String manageItem(Map<String, String> items) throws Exception {
		String filePathxml = getFilePath(null, MappedTags.manageItemsstk.getMasterXmlFilePath(), true);
		Document doc = XMLUtil.readXmlFile(filePathxml);
		Node copiedNode;
		Node nodestk = (Element) XMLUtil.getNodeByTagName(doc, MappedTags.manageItemsstk.getActualTag());
		XMLUtil.removeNode(doc.getDocumentElement(),
				XMLUtil.getNodeByTagName(doc, MappedTags.manageItemsstk.getActualTag()));
        if(items != null) {
		for (Map.Entry<String, String> entry : items.entrySet()) {
			if (entry.getKey().contains("STK")) {
				copiedNode = doc.importNode(nodestk, true);
				XMLUtil.setDataInNodeAtt((Element) copiedNode, "Item.ItemID", entry.getValue());
				XMLUtil.setDataInNodeAtt((Element) copiedNode, "Item.ItemAliasList.ItemAlias.AliasValue",
						Utilities.generateRandomString(12));
				XMLUtil.appendNode(doc.getDocumentElement(), copiedNode);
			}

		}
        }else {
        	return null;
        }

		return XMLUtil.convertXMLDocumentToString(doc);
	}
	
	/**
	 * This function return the payload for AdjustInventory request
	 * 
	 * @author Perfaware
	 * @param OrderTypes       - ordertypes for the requested payload 
	 * @param noofItemsOfOrder - no. of items for each order type 
	 * @param testdata         - Data from excel sheet 
	 * @param setInvAsZero     - used to set the qty as '0' if required
	 * 
	 */

	public String createAdjInvXml(OrderTypes order_type, String noofItemsOfOrder,
		Map<String, List<Map<String, String>>> testdata, boolean setInvAsZero) throws Exception {
		String filePathxml = getFilePath(null, MappedTags.adjustInventory.getMasterXmlFilePath(), true);
		Document doc = XMLUtil.readXmlFile(filePathxml);
		Node node = (Element) XMLUtil.getNodeByTagName(doc, MappedTags.adjustInventory.getActualTag());
		XMLUtil.removeNode(doc.getDocumentElement(), node);
		String orderTypes[] = order_type.toString().split("_");
		String noOfItems[] = noofItemsOfOrder.split("_");
		Node copiedNode;
		int noofols;
		NodeList crawlNodeList;
		int startnode = 0;
		boolean created = false;

		for (int i = 0; i < orderTypes.length; i++) {
			if (!orderTypes[i].toString().contains("DYO")) {
				noofols = Integer.valueOf(noOfItems[i]);
				for (int j = 0; j < noofols; j++) {
					copiedNode = doc.importNode(node, true);
					XMLUtil.appendNode(doc.getDocumentElement(), copiedNode);
				}
				crawlNodeList = doc.getElementsByTagName(MappedTags.adjustInventory.getActualTag());
				XMLUtil.setDataToXmlNodesWithDiffHeaders(testdata.get(TestData.valueOf(orderTypes[i]).toString()),
						crawlNodeList, startnode, noofols, 0, MappedTags.adjustInventory.getMappedTags());
				startnode = startnode + noofols;
				created = true;
			}
		}
		crawlNodeList = doc.getElementsByTagName(MappedTags.adjustInventory.getActualTag());
		if (setInvAsZero) {
			for (int i = 0; i < startnode; i++) {
				copiedNode = crawlNodeList.item(i);
				XMLUtil.setDataInNodeAtt((Element) copiedNode, "Item.Quantity", String.valueOf("0"));

			}
		}

		if (created) {
			return XMLUtil.convertXMLDocumentToString(doc);
		}
		else
			return null;

	}
	
	/**
	 * This function return the payload for create order request
	 * 
	 * @author Perfaware
	 * @param order_type       - ordertypes for the requested payload 
	 * @param noofItemsOfOrder - no. of items for each order type 
	 * @param testdata         - Data from excel sheet 
	 * @param paymentType      - payment type of the order
	 * @param orderNo          - orderNo of that request to set reservation id
	 * @param lineTaxes        - line taxes to be applied to the order
	 * @param lineCharges      - line charges to be applied to the order
	 * 
	 */
	
	public TestCaseDetails createOrderXmlBOPIS(OrderTypes order_type, String noofItemsOfOrder,
			Map<String, List<Map<String, String>>> testdata, String paymentType, String orderNo, TestData lineTaxes,
			TestData lineCharges,TestCaseDetails testCaseDetails) throws Exception {
		String orderTypes[] = order_type.toString().split("_");
		String noOfItems[] = noofItemsOfOrder.split("_");

		
		// getting the master xml doc
		String filePath = getFilePath(null, order_type.getHeader_Type().getMasterXmlFilePath(), true);

		Document finalDoc = XMLUtil.readXmlFile(filePath);
		NodeList crawlNodeList;
		Node node;
		Document doc;
		int noofols;
		Node copiedNode;
		double totalOrderamt = 0;
		double singleOLTax = 0;
		double singleOLCharges = 0;

		// Setting the Header Data
		crawlNodeList = finalDoc.getElementsByTagName(order_type.getHeader_Type().getParentTag());
		XMLUtil.setDataToXmlNodes(testdata.get(order_type.getHeader_Type().toString()), crawlNodeList, 0, 1, 0);
		// Setiing the Order header details
		XMLUtil.setDataInNodeAtt(finalDoc.getDocumentElement(), "Order.OrderNo", orderNo);
		
		
//		if(testCaseDetails!=null && testCaseDetails.getNotificationRef()!=null) {
//			XMLUtil.setDataInNodeAtt(finalDoc.getDocumentElement(), "Order.NotificationReference", testCaseDetails.getNotificationRef());
//		}
//		
		
		// Adding OrderLines and setting up orderlines data
		Document docforOrderlines = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(finalDoc, TestData.STKBOPIS.getParentTag()));
		Document docforlinetaxes = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(docforOrderlines, lineTaxes.getParentTag()));

		// Preparing linecharges node Starts
		Document docforlinecharges = null;
		
//		if (lineCharges != null) {
//			docforlinecharges = XMLUtil.createDocFromNode(
//					(Element) XMLUtil.getNodeByTagName(docforOrderlines, lineCharges.getParentTag()));
//			node = (Element) XMLUtil.getNodeByTagName(docforlinecharges, lineCharges.getActualTag());
//			
//			for (int i = 2; i < testdata.get(lineCharges.toString()).size(); i++) {
//				copiedNode = docforlinecharges.importNode(node, true);
//				XMLUtil.appendNode(docforlinecharges.getDocumentElement(), copiedNode);
//			}
//			crawlNodeList = docforlinecharges.getElementsByTagName(lineCharges.getActualTag());
//			XMLUtil.setDataToXmlNodes(testdata.get(lineCharges.toString()), crawlNodeList, 0,
//					testdata.get(lineCharges.toString()).size() - 1, 0);
//			singleOLCharges = loadTestData.calculateTotalAmt(testdata.get(lineCharges.toString()), "count");
//
//		}
		// Preparing linecharges node ends

		XMLUtil.removeNode(finalDoc.getDocumentElement(),
				XMLUtil.getNodeByTagName(finalDoc, TestData.STKBOPIS.getParentTag()));
		XMLUtil.removeNode(docforOrderlines.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforOrderlines, TestData.STKBOPIS.getActualTag()));

		// Preparing linetaxes node starts
//		node = (Element) XMLUtil.getNodeByTagName(docforlinetaxes, lineTaxes.getActualTag());
//		for (int i = 2; i < testdata.get(lineTaxes.toString()).size(); i++) {
//			copiedNode = docforlinetaxes.importNode(node, true);
//			XMLUtil.appendNode(docforlinetaxes.getDocumentElement(), copiedNode);
//		}
//		crawlNodeList = docforlinetaxes.getElementsByTagName(lineTaxes.getActualTag());
//		XMLUtil.setDataToXmlNodes(testdata.get(lineTaxes.toString()), crawlNodeList, 0,
//				testdata.get(lineTaxes.toString()).size() - 1, 0);
//		singleOLTax = loadTestData.calculateTotalAmt(testdata.get(lineTaxes.toString()), "count");

		// Preparing linetaxes node ends

	
		int tempVal = 0;
		Document docforolnode;
		Node cN;
		for (int i = 0; i < orderTypes.length; i++) {
			filePath = getFilePath(null, TestData.valueOf(orderTypes[i]).getMasterXmlFilePath(), true);
			doc = XMLUtil.readXmlFile(filePath);
			node = (Element) XMLUtil.getNodeByTagName(doc, TestData.valueOf(orderTypes[i]).getActualTag());
			noofols = Integer.valueOf(noOfItems[i]);
			for (int j = 0; j < noofols; j++) {
				// Adding Line Taxes To Orderline and converting back orderline doc to node
				docforolnode = XMLUtil.createDocFromNode((Element) node);
				XMLUtil.removeNode(docforolnode.getDocumentElement(),
						XMLUtil.getNodeByTagName(docforolnode, lineTaxes.getParentTag()));
				XMLUtil.removeNode(docforolnode.getDocumentElement(),
						XMLUtil.getNodeByTagName(docforolnode, TestData.LineCharges_Type_Shipping.getParentTag()));
				cN = docforolnode.importNode(
						(Element) XMLUtil.getNodeByTagName(docforlinetaxes, lineTaxes.getParentTag()), true);
				XMLUtil.appendNode(docforolnode.getDocumentElement(), cN);
				node = XMLUtil.getNodeByTagName(docforolnode, TestData.valueOf(orderTypes[i]).getActualTag());
				//
				if (lineCharges != null && docforlinecharges != null) {
					// Adding Line charges To Orderline and converting back orderline doc to node
					docforolnode = XMLUtil.createDocFromNode((Element) node);
					cN = docforolnode.importNode(
							(Element) XMLUtil.getNodeByTagName(docforlinecharges, lineCharges.getParentTag()), true);
					XMLUtil.appendNode(docforolnode.getDocumentElement(), cN);
					node = XMLUtil.getNodeByTagName(docforolnode, TestData.valueOf(orderTypes[i]).getActualTag());
					//
				}
				copiedNode = docforOrderlines.importNode(node, true);
				XMLUtil.setDataInNodeAtt((Element) copiedNode, "OrderLine.PrimeLineNo",
						String.valueOf(j + 1 + tempVal));
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"OrderLineReservations.OrderLineReservation.ReservationID", orderNo);

				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"OrderLineReservations.OrderLineReservation.RequestedReservationDate", getSimpleDate());

				XMLUtil.appendNode(docforOrderlines.getDocumentElement(), copiedNode);

			}
			crawlNodeList = docforOrderlines.getElementsByTagName(TestData.valueOf(orderTypes[i]).getActualTag());
			XMLUtil.setDataToXmlNodes(testdata.get(TestData.valueOf(orderTypes[i]).toString()), crawlNodeList, tempVal,
					noofols, 0);
			tempVal = tempVal + noofols;

		}

		// Calculating total order amount
		crawlNodeList = docforOrderlines.getElementsByTagName(TestData.STKBOPIS.getActualTag());
		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			copiedNode = crawlNodeList.item(i);
			double qty = Double
					.valueOf(XMLUtil.getDataInNodeAtt((Element) copiedNode, "OrderLine.OrderedQty", "OrderedQty"));
			double price = Double.valueOf(
					XMLUtil.getDataInNodeAtt((Element) copiedNode, "OrderLine.LinePriceInfo.ListPrice", "ListPrice"));
			totalOrderamt = totalOrderamt + (qty * price) + singleOLTax + singleOLCharges;
		}

		// Adding the OrderLines To Order xml
		copiedNode = finalDoc.importNode(XMLUtil.getNodeByTagName(docforOrderlines, TestData.STKBOPIS.getParentTag()),
				true);
		XMLUtil.appendNodeBeforeNode(finalDoc.getDocumentElement(), copiedNode,
				XMLUtil.getNodeByTagName(finalDoc, "References"));

		// Adding PaymentMethods Based on paymentMethod Type
		Node nodePaymentMethod = (Element) XMLUtil.getNodeByTagName(finalDoc, TestData.CC.getActualTag());
		Document docforPaymentMethods = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(finalDoc, TestData.CC.getParentTag()));
		XMLUtil.removeNode(finalDoc.getDocumentElement(),
				XMLUtil.getNodeByTagName(finalDoc, TestData.CC.getParentTag()));
		XMLUtil.removeNode(docforPaymentMethods.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforPaymentMethods, TestData.GC.getActualTag()));
		String[] splitPaymentTypes = paymentType.split("_");
        int gccount=1;
		for (int i = 0; i < splitPaymentTypes.length; i++) {
			copiedNode = docforPaymentMethods.importNode(nodePaymentMethod, true);
			XMLUtil.setDataInNodeAtt((Element) copiedNode, "PaymentMethod.MaxChargeLimit",
					String.format("%.2f", totalOrderamt / splitPaymentTypes.length));
			XMLUtil.setDataInNodeAtt((Element) copiedNode,
					"PaymentMethod.PaymentDetailsList.PaymentDetails.ProcessedAmount",
					String.format("%.2f", totalOrderamt / splitPaymentTypes.length));
			XMLUtil.setDataInNodeAtt((Element) copiedNode,
					"PaymentMethod.PaymentDetailsList.PaymentDetails.RequestAmount",
					String.format("%.2f", totalOrderamt / splitPaymentTypes.length));
           //ECOM15-6188 - start
			
			if(splitPaymentTypes[i].equals(PaymentTypes.CC.toString()))
			{
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.AuthorizationID",
						testCaseDetails.getCc_auth_id());
				
			}else if(splitPaymentTypes[i].equals(PaymentTypes.PP.toString()))
			{
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.AuthorizationID",
						testCaseDetails.getCc_auth_id());
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.CreditCardNo",
						testCaseDetails.getCc_auth_id());
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.RequestId",
						testCaseDetails.getPp_request_id());
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.Reference1",
						testCaseDetails.getPp_transactionid());
				
			}
				
			
			//ECOM15-6188 - end
			XMLUtil.appendNode(docforPaymentMethods.getDocumentElement(), copiedNode);
			crawlNodeList = docforPaymentMethods.getElementsByTagName(TestData.GC.getActualTag());
			//ECOM15-6905 - Start
			if(splitPaymentTypes[i].equals(PaymentTypes.GC.toString())) {
				XMLUtil.setDataToXmlNodesForPayments(testdata.get(splitPaymentTypes[i]), crawlNodeList, 0, 1, i,gccount);			
				
				gccount++;
			}else {
				XMLUtil.setDataToXmlNodes(testdata.get(splitPaymentTypes[i]), crawlNodeList, 0, 1, i);
				
			}
			//ECOM15-6905 - end

					
		}
        if(testCaseDetails != null) {
        	testCaseDetails.setTotalOrderAmount(totalOrderamt);
        }
		// Add the final paymentmethods tag to final doc
		copiedNode = finalDoc.importNode(XMLUtil.getNodeByTagName(docforPaymentMethods, TestData.CC.getParentTag()),
				true);
		XMLUtil.appendNode(finalDoc.getDocumentElement(), copiedNode);
        
        
		testCaseDetails.setCreatOrderPayload(XMLUtil.convertXMLDocumentToString(finalDoc));
		
		
		return testCaseDetails;

	}
	
	/**
	 * This function return the payload for create order request
	 * 
	 * @author Perfaware
	 * @param order_type       - ordertypes for the requested payload 
	 * @param noofItemsOfOrder - no. of items for each order type 
	 * @param testdata         - Data from excel sheet 
	 * @param paymentType      - payment type of the order
	 * @param orderNo          - orderNo of that request to set reservation id
	 * @param lineTaxes        - line taxes to be applied to the order
	 * @param lineCharges      - line charges to be applied to the order
	 * 
	 */
	
	public TestCaseDetails createOrderXml(OrderTypes order_type, String noofItemsOfOrder,
			Map<String, List<Map<String, String>>> testdata, String paymentType, String orderNo, TestData lineTaxes,
			TestData lineCharges,TestCaseDetails testCaseDetails) throws Exception {
		String orderTypes[] = order_type.toString().split("_");
		String noOfItems[] = noofItemsOfOrder.split("_");

		// getting the master xml doc
		String filePath = getFilePath(null, order_type.getHeader_Type().getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		NodeList crawlNodeList;
		Node node;
		Document doc;
		int noofols;
		Node copiedNode;
		double totalOrderamt = 0;
		double singleOLTax = 0;
		double singleOLCharges = 0;

		// Setting the Header Data
		crawlNodeList = finalDoc.getElementsByTagName(order_type.getHeader_Type().getParentTag());
		XMLUtil.setDataToXmlNodes(testdata.get(order_type.getHeader_Type().toString()), crawlNodeList, 0, 1, 0);
		// Setiing the Order header details
		XMLUtil.setDataInNodeAtt(finalDoc.getDocumentElement(), "Order.OrderNo", orderNo);
		
//		if(testCaseDetails!=null && testCaseDetails.getNotificationRef()!=null) {
//			XMLUtil.setDataInNodeAtt(finalDoc.getDocumentElement(), "Order.NotificationReference", testCaseDetails.getNotificationRef());
//		}
//		
		
		// Adding OrderLines and setting up orderlines data
		Document docforOrderlines = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(finalDoc, TestData.STKSTH.getParentTag()));
		Document docforlinetaxes = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(docforOrderlines, lineTaxes.getParentTag()));

		// Preparing linecharges node Starts
		Document docforlinecharges = null;
		
//		if (lineCharges != null) {
//			docforlinecharges = XMLUtil.createDocFromNode(
//					(Element) XMLUtil.getNodeByTagName(docforOrderlines, lineCharges.getParentTag()));
//			node = (Element) XMLUtil.getNodeByTagName(docforlinecharges, lineCharges.getActualTag());
//			
//			for (int i = 2; i < testdata.get(lineCharges.toString()).size(); i++) {
//				copiedNode = docforlinecharges.importNode(node, true);
//				XMLUtil.appendNode(docforlinecharges.getDocumentElement(), copiedNode);
//			}
//			crawlNodeList = docforlinecharges.getElementsByTagName(lineCharges.getActualTag());
//			XMLUtil.setDataToXmlNodes(testdata.get(lineCharges.toString()), crawlNodeList, 0,
//					testdata.get(lineCharges.toString()).size() - 1, 0);
//			singleOLCharges = loadTestData.calculateTotalAmt(testdata.get(lineCharges.toString()), "count");
//
//		}
		// Preparing linecharges node ends

		XMLUtil.removeNode(finalDoc.getDocumentElement(),
				XMLUtil.getNodeByTagName(finalDoc, TestData.STKSTH.getParentTag()));
		XMLUtil.removeNode(docforOrderlines.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforOrderlines, TestData.STKSTH.getActualTag()));

		// Preparing linetaxes node starts
//		node = (Element) XMLUtil.getNodeByTagName(docforlinetaxes, lineTaxes.getActualTag());
//		for (int i = 2; i < testdata.get(lineTaxes.toString()).size(); i++) {
//			copiedNode = docforlinetaxes.importNode(node, true);
//			XMLUtil.appendNode(docforlinetaxes.getDocumentElement(), copiedNode);
//		}
//		crawlNodeList = docforlinetaxes.getElementsByTagName(lineTaxes.getActualTag());
//		XMLUtil.setDataToXmlNodes(testdata.get(lineTaxes.toString()), crawlNodeList, 0,
//				testdata.get(lineTaxes.toString()).size() - 1, 0);
//		singleOLTax = loadTestData.calculateTotalAmt(testdata.get(lineTaxes.toString()), "count");

		// Preparing linetaxes node ends

		int tempVal = 0;
		Document docforolnode;
		Node cN;
		for (int i = 0; i < orderTypes.length; i++) {
			filePath = getFilePath(null, TestData.valueOf(orderTypes[i]).getMasterXmlFilePath(), true);
			doc = XMLUtil.readXmlFile(filePath);
			node = (Element) XMLUtil.getNodeByTagName(doc, TestData.valueOf(orderTypes[i]).getActualTag());
			noofols = Integer.valueOf(noOfItems[i]);
			for (int j = 0; j < noofols; j++) {
				// Adding Line Taxes To Orderline and converting back orderline doc to node
				docforolnode = XMLUtil.createDocFromNode((Element) node);
				XMLUtil.removeNode(docforolnode.getDocumentElement(),
						XMLUtil.getNodeByTagName(docforolnode, lineTaxes.getParentTag()));
				XMLUtil.removeNode(docforolnode.getDocumentElement(),
						XMLUtil.getNodeByTagName(docforolnode, TestData.LineCharges_Type_Shipping.getParentTag()));
				cN = docforolnode.importNode(
						(Element) XMLUtil.getNodeByTagName(docforlinetaxes, lineTaxes.getParentTag()), true);
				XMLUtil.appendNode(docforolnode.getDocumentElement(), cN);
				node = XMLUtil.getNodeByTagName(docforolnode, TestData.valueOf(orderTypes[i]).getActualTag());
				//
				if (lineCharges != null && docforlinecharges != null) {
					// Adding Line charges To Orderline and converting back orderline doc to node
					docforolnode = XMLUtil.createDocFromNode((Element) node);
					cN = docforolnode.importNode(
							(Element) XMLUtil.getNodeByTagName(docforlinecharges, lineCharges.getParentTag()), true);
					XMLUtil.appendNode(docforolnode.getDocumentElement(), cN);
					node = XMLUtil.getNodeByTagName(docforolnode, TestData.valueOf(orderTypes[i]).getActualTag());
					//
				}
				copiedNode = docforOrderlines.importNode(node, true);
				XMLUtil.setDataInNodeAtt((Element) copiedNode, "OrderLine.PrimeLineNo",
						String.valueOf(j + 1 + tempVal));
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"OrderLineReservations.OrderLineReservation.ReservationID", orderNo);

				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"OrderLineReservations.OrderLineReservation.RequestedReservationDate", getSimpleDate());

				XMLUtil.appendNode(docforOrderlines.getDocumentElement(), copiedNode);

			}
			crawlNodeList = docforOrderlines.getElementsByTagName(TestData.valueOf(orderTypes[i]).getActualTag());
			XMLUtil.setDataToXmlNodes(testdata.get(TestData.valueOf(orderTypes[i]).toString()), crawlNodeList, tempVal,
					noofols, 0);
			tempVal = tempVal + noofols;

		}

		// Calculating total order amount
		crawlNodeList = docforOrderlines.getElementsByTagName(TestData.STKSTH.getActualTag());
		for (int i = 0; i < crawlNodeList.getLength(); i++) {
			copiedNode = crawlNodeList.item(i);
			double qty = Double
					.valueOf(XMLUtil.getDataInNodeAtt((Element) copiedNode, "OrderLine.OrderedQty", "OrderedQty"));
			double price = Double.valueOf(
					XMLUtil.getDataInNodeAtt((Element) copiedNode, "OrderLine.LinePriceInfo.ListPrice", "ListPrice"));
			totalOrderamt = totalOrderamt + (qty * price) + singleOLTax + singleOLCharges;
		}

		// Adding the OrderLines To Order xml
		copiedNode = finalDoc.importNode(XMLUtil.getNodeByTagName(docforOrderlines, TestData.STKSTH.getParentTag()),
				true);
		XMLUtil.appendNodeBeforeNode(finalDoc.getDocumentElement(), copiedNode,
				XMLUtil.getNodeByTagName(finalDoc, "References"));

		// Adding PaymentMethods Based on paymentMethod Type
		Node nodePaymentMethod = (Element) XMLUtil.getNodeByTagName(finalDoc, TestData.CC.getActualTag());
		Document docforPaymentMethods = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(finalDoc, TestData.CC.getParentTag()));
		XMLUtil.removeNode(finalDoc.getDocumentElement(),
				XMLUtil.getNodeByTagName(finalDoc, TestData.CC.getParentTag()));
		XMLUtil.removeNode(docforPaymentMethods.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforPaymentMethods, TestData.GC.getActualTag()));
		String[] splitPaymentTypes = paymentType.split("_");
        int gccount=1;
		for (int i = 0; i < splitPaymentTypes.length; i++) {
			copiedNode = docforPaymentMethods.importNode(nodePaymentMethod, true);
			XMLUtil.setDataInNodeAtt((Element) copiedNode, "PaymentMethod.MaxChargeLimit",
					String.format("%.2f", totalOrderamt / splitPaymentTypes.length));
			XMLUtil.setDataInNodeAtt((Element) copiedNode,
					"PaymentMethod.PaymentDetailsList.PaymentDetails.ProcessedAmount",
					String.format("%.2f", totalOrderamt / splitPaymentTypes.length));
			XMLUtil.setDataInNodeAtt((Element) copiedNode,
					"PaymentMethod.PaymentDetailsList.PaymentDetails.RequestAmount",
					String.format("%.2f", totalOrderamt / splitPaymentTypes.length));
           //ECOM15-6188 - start
			
			if(splitPaymentTypes[i].equals(PaymentTypes.CC.toString()))
			{
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.AuthorizationID",
						testCaseDetails.getCc_auth_id());
				
			}else if(splitPaymentTypes[i].equals(PaymentTypes.PP.toString()))
			{
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.AuthorizationID",
						testCaseDetails.getCc_auth_id());
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.CreditCardNo",
						testCaseDetails.getCc_auth_id());
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.RequestId",
						testCaseDetails.getPp_request_id());
				XMLUtil.setDataInNodeAtt((Element) copiedNode,
						"PaymentMethod.PaymentDetailsList.PaymentDetails.Reference1",
						testCaseDetails.getPp_transactionid());
				
			}
				
			
			//ECOM15-6188 - end
			XMLUtil.appendNode(docforPaymentMethods.getDocumentElement(), copiedNode);
			crawlNodeList = docforPaymentMethods.getElementsByTagName(TestData.GC.getActualTag());
			//ECOM15-6905 - Start
			if(splitPaymentTypes[i].equals(PaymentTypes.GC.toString())) {
				XMLUtil.setDataToXmlNodesForPayments(testdata.get(splitPaymentTypes[i]), crawlNodeList, 0, 1, i,gccount);			
				
				gccount++;
			}else {
				XMLUtil.setDataToXmlNodes(testdata.get(splitPaymentTypes[i]), crawlNodeList, 0, 1, i);
				
			}
			//ECOM15-6905 - end

					
		}
        if(testCaseDetails != null) {
        	testCaseDetails.setTotalOrderAmount(totalOrderamt);
        }
		// Add the final paymentmethods tag to final doc
		copiedNode = finalDoc.importNode(XMLUtil.getNodeByTagName(docforPaymentMethods, TestData.CC.getParentTag()),
				true);
		XMLUtil.appendNode(finalDoc.getDocumentElement(), copiedNode);
        
		testCaseDetails.setCreatOrderPayload(XMLUtil.convertXMLDocumentToString(finalDoc));
		
		return testCaseDetails;

	}
	/**
	 * This function return payload for getOrderDetails
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String getOrderDetails(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.getOrderDetails.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}

	/**
	 * This function return payload for getOrderDetails
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String getATP(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.getATP.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for getOrderHoldList
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String getOrderHoldList(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.getOrderHoldList.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	/**
	 * This function return payload for Schedule Order
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String scheduleOrder(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.ScheduleOrder.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for Release Order
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */

	public String releaseOrder(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.ReleaseOrder.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	
	/**
	 * This function return payload for getOrderReleaseList
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String getOrderReleaseList(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.getOrderReleaseList.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for consolidateToShipment
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String consolidateToShipment(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.consolidateToShipment.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for DifarmaGetShipmentListForOrder
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String DifarmaGetShipmentListForOrder(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.DifarmaGetShipmentListForOrder.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for syncLoadedInventory
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String syncLoadedInventory(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.syncLoadedInventory.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for DifarmaGetShipmentListForOrder
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String getShipmentListForOrder(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.getShipmentListForOrder.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for DifarmaGetShipmentList
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String DifarmaGetShipmentList(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.DifarmaGetShipmentList.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	
	/**
	 * This function return payload for DifarmaGetShipmentList
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String getShipmentList(Map<String, String> data) throws Exception {
		String filePath = getFilePath(null, MappedTags.getShipmentList.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	
	/**
	 * This function return payload for DifarmaMobileRecordPackCompletion
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String DifarmaMobileRecordPackCompletion(Map<String, String> testdata,String noOfOrderLines) throws Exception {
		String filePath = getFilePath(null, MappedTags.DifarmaMobileRecordPackCompletion.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : testdata.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for DifarmaMobileConfirmShipment
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String changeShipmentStatus(Map<String, String> testdata,String noOfOrderLines) throws Exception {
		String filePath = getFilePath(null, MappedTags.changeShipmentStatus.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : testdata.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for DifarmaMobileConfirmShipment
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String DifarmaMobileConfirmShipment(Map<String, String> testdata,String noOfOrderLines) throws Exception {
		String filePath = getFilePath(null, MappedTags.DifarmaMobileConfirmShipment.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : testdata.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	
	/**
	 * This function return payload for DifarmaMobileRecordPickCompletion
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String DifarmaMobileRecordPickCompletion(Map<String, String> testdata,String noOfOrderLines) throws Exception {
		String filePath = getFilePath(null, MappedTags.DifarmaMobileRecordPickCompletion.getMasterXmlFilePath(), true);
		Document doc = XMLUtil.readXmlFile(filePath);
		Node node = (Element) XMLUtil.getNodeByTagName(doc, MappedTags.DifarmaMobileRecordPickCompletion.getActualTag());
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Shipment.ShipNode", testdata.get("Shipment.ShipNode"));
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Shipment.ShipmentKey", testdata.get("Shipment.ShipmentKey"));
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Shipment.ShipmentNo", testdata.get("Shipment.ShipmentNo"));
		Document docforShipmentlines = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(doc, MappedTags.DifarmaMobileRecordPickCompletion.getParentTag()));
		
		Node copiedNode;
		// Adding OrderLines and setting up orderlines data
		for(int i=2;i<=Integer.parseInt(noOfOrderLines);i++) {
			copiedNode = docforShipmentlines.importNode(node, true);
			XMLUtil.appendNode(docforShipmentlines.getDocumentElement(), copiedNode);
		}
		
		XMLUtil.removeNode(doc.getDocumentElement(),
				XMLUtil.getNodeByTagName(doc, MappedTags.DifarmaMobileRecordPickCompletion.getParentTag()));
		XMLUtil.removeNode(docforShipmentlines.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.DifarmaMobileRecordPickCompletion.getParentTag()));
		
		// Adding the ShipmentLines To ConfirmShipment xml
		copiedNode = doc.importNode(XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.DifarmaMobileRecordPickCompletion.getParentTag()),
				true);
		XMLUtil.appendNodeBeforeNode(doc.getDocumentElement(), copiedNode,
				XMLUtil.getNodeByTagName(doc, "ShipmentLines"));		

		
		return XMLUtil.convertXMLDocumentToString(doc);
	}
	
	/**
	 * This function return payload for confirmShipment
	 * 
	 * @author Perfaware
	 * @param tempData 
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String confirmShipment(Map<String, String> testdata,String noOfOrderLines) throws Exception {
		String filePath = getFilePath(null, MappedTags.DifarmaMobileConfirmShipment.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : testdata.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}
	
	/**
	 * This function return payload for DifarmaMobileRecordPickCompletion
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String DifarmaMobileFinishCustomerPickService(Map<String, String> testdata,String noOfOrderLines) throws Exception {
		String filePath = getFilePath(null, MappedTags.DifarmaMobileFinishCustomerPickService.getMasterXmlFilePath(), true);
		Document doc = XMLUtil.readXmlFile(filePath);
		Node node = (Element) XMLUtil.getNodeByTagName(doc, MappedTags.DifarmaMobileFinishCustomerPickService.getActualTag());
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Shipment.ShipNode", testdata.get("Shipment.ShipNode"));
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Shipment.ShipmentKey", testdata.get("Shipment.ShipmentKey"));
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Shipment.ShipmentNo", testdata.get("Shipment.ShipmentNo"));
		Document docforShipmentlines = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(doc, MappedTags.DifarmaMobileFinishCustomerPickService.getParentTag()));
		
		Node copiedNode;
		// Adding shipmentLines and setting up shipmentLines data
		for(int i=2;i<=Integer.parseInt(noOfOrderLines);i++) {
			copiedNode = docforShipmentlines.importNode(node, true);
			XMLUtil.appendNode(docforShipmentlines.getDocumentElement(), copiedNode);
		}
		
		XMLUtil.removeNode(doc.getDocumentElement(),
				XMLUtil.getNodeByTagName(doc, MappedTags.DifarmaMobileFinishCustomerPickService.getParentTag()));
		XMLUtil.removeNode(docforShipmentlines.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.DifarmaMobileFinishCustomerPickService.getParentTag()));
		
		// Adding the ShipmentLines To ConfirmShipment xml
		copiedNode = doc.importNode(XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.DifarmaMobileFinishCustomerPickService.getParentTag()),
				true);
		XMLUtil.appendNodeBeforeNode(doc.getDocumentElement(), copiedNode,
				XMLUtil.getNodeByTagName(doc, "ShipmentLines"));		

		
		return XMLUtil.convertXMLDocumentToString(doc);
	}

	/**
	 * This function return payload for findInventory
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String findInventory(Map<String, String> testdata,Map<String, String> items) throws Exception {
		String filePath = getFilePath(null, MappedTags.findInventory.getMasterXmlFilePath(), true);
		Document doc = XMLUtil.readXmlFile(filePath);
		Node node = (Element) XMLUtil.getNodeByTagName(doc, MappedTags.findInventory.getActualTag());
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Promise.FulfillmentType", testdata.get("Promise.FulfillmentType"));
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Promise.OrganizationCode", testdata.get("Promise.OrganizationCode"));

		Document docforShipmentlines = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(doc, MappedTags.findInventory.getParentTag()));
		
		Node copiedNode;
		// Adding PromiseLine and setting up shipmentLines data
		for(int i=2;i<=items.size();i++) {
			copiedNode = docforShipmentlines.importNode(node, true);
			XMLUtil.appendNode(docforShipmentlines.getDocumentElement(), copiedNode);
		}
		
		XMLUtil.removeNode(doc.getDocumentElement(),
				XMLUtil.getNodeByTagName(doc, MappedTags.findInventory.getParentTag()));
		XMLUtil.removeNode(docforShipmentlines.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.findInventory.getParentTag()));
		
		// Adding the PromiseLine To findInventory xml
		copiedNode = doc.importNode(XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.findInventory.getParentTag()),
				true);
		XMLUtil.appendNodeBeforeNode(doc.getDocumentElement(), copiedNode,
				XMLUtil.getNodeByTagName(doc, "PromiseLines"));		

		
		return XMLUtil.convertXMLDocumentToString(doc);
	}
	
	/**
	 * This function return payload for findInventory
	 * 
	 * @author Perfaware
	 * @param data  - data to set in the xml              
	 * 
	 */
	public String reserveAvailableInventory(Map<String, String> testdata,Map<String, String> items) throws Exception {
		String filePath = getFilePath(null, MappedTags.reserveAvailableInventory.getMasterXmlFilePath(), true);
		Document doc = XMLUtil.readXmlFile(filePath);
		Node node = (Element) XMLUtil.getNodeByTagName(doc, MappedTags.findInventory.getActualTag());
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Promise.FulfillmentType", testdata.get("Promise.FulfillmentType"));
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Promise.OrganizationCode", testdata.get("Promise.OrganizationCode"));
		XMLUtil.setDataInNodeAtt(doc.getDocumentElement(), "Promise.ReservationParameters.ReservationID", testdata.get("Promise.ReservationParameters.ReservationID"));
		Document docforShipmentlines = XMLUtil
				.createDocFromNode((Element) XMLUtil.getNodeByTagName(doc, MappedTags.reserveAvailableInventory.getParentTag()));
		
		Node copiedNode;
		// Adding PromiseLine and setting up PromiseLines data
		for(int i=2;i<=items.size();i++) {
			copiedNode = docforShipmentlines.importNode(node, true);
			XMLUtil.appendNode(docforShipmentlines.getDocumentElement(), copiedNode);
		}
		
		XMLUtil.removeNode(doc.getDocumentElement(),
				XMLUtil.getNodeByTagName(doc, MappedTags.reserveAvailableInventory.getParentTag()));
		XMLUtil.removeNode(docforShipmentlines.getDocumentElement(),
				XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.reserveAvailableInventory.getParentTag()));
		
		// Adding the PromiseLine To reserveAvailableInventory xml
		copiedNode = doc.importNode(XMLUtil.getNodeByTagName(docforShipmentlines, MappedTags.reserveAvailableInventory.getParentTag()),
				true);
		XMLUtil.appendNodeBeforeNode(doc.getDocumentElement(), copiedNode,
				XMLUtil.getNodeByTagName(doc, "PromiseLines"));		

		
		return XMLUtil.convertXMLDocumentToString(doc);
	}
	
	public String changeOrder(Response response, Map<String, String> tempData) throws Exception {
		String filePath = getFilePath(null, MappedTags.changeOrder.getMasterXmlFilePath(), true);
		Document finalDoc = XMLUtil.readXmlFile(filePath);
		Node node = finalDoc.getDocumentElement();
		for (Map.Entry<String, String> entry : tempData.entrySet()) {
			XMLUtil.setDataInNodeAtt((Element) node, entry.getKey(), entry.getValue());
		}
		
		return XMLUtil.convertXMLDocumentToString(finalDoc);
	}

}
