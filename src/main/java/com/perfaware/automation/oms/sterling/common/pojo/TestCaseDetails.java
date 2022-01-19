package com.perfaware.automation.oms.sterling.common.pojo;

import java.util.Map;

import com.perfaware.automation.oms.sterling.common.applicationHelper.OrderTypes;

import io.restassured.response.Response;

public class TestCaseDetails {
	String orderNo;
	String noOfLines;
	Map<String, String> itemData;
	String ohk;
	double totalOrderAmount;
	OrderTypes orderType;
	Map<String, String> orderQty;
	Map<String, String> linePriceSingleQty;
	Response response;
	String creatOrderPayload;
	String cc_auth_id;// using for both cc auth id and pp auth id
	String pp_request_id;
	String pp_token;
	String pp_transactionid;
	String notificationRef;
	String ExtnEnvironment;
	
	public String getExtnEnvironment() {
		return ExtnEnvironment;
	}
	public void setExtnEnvironment(String extnEnvironment) {
		ExtnEnvironment = extnEnvironment;
	}
	public String getExtnFrontEndEnvironment() {
		return ExtnFrontEndEnvironment;
	}
	public void setExtnFrontEndEnvironment(String extnFrontEndEnvironment) {
		ExtnFrontEndEnvironment = extnFrontEndEnvironment;
	}
	String ExtnFrontEndEnvironment;
	
	public String getNotificationRef() {
		return notificationRef;
	}
	public void setNotificationRef(String notificationRef) {
		this.notificationRef = notificationRef;
	}
	public String getPp_transactionid() {
		return pp_transactionid;
	}
	public void setPp_transactionid(String pp_transactionid) {
		this.pp_transactionid = pp_transactionid;
	}
	public String getPp_request_id() {
		return pp_request_id;
	}
	public void setPp_request_id(String pp_request_id) {
		this.pp_request_id = pp_request_id;
	}
	public String getPp_token() {
		return pp_token;
	}
	public void setPp_token(String pp_token) {
		this.pp_token = pp_token;
	}
	public String getCc_auth_id() {
		return cc_auth_id;
	}
	public void setCc_auth_id(String cc_auth_id) {
		this.cc_auth_id = cc_auth_id;
	}
	public String getCreatOrderPayload() {
		return creatOrderPayload;
	}
	public void setCreatOrderPayload(String creatOrderPayload) {
		this.creatOrderPayload = creatOrderPayload;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getNoOfLines() {
		return noOfLines;
	}
	public void setNoOfLines(String noOfLines) {
		this.noOfLines = noOfLines;
	}
	public Map<String, String> getItemData() {
		return itemData;
	}
	public void setItemData(Map<String, String> itemData) {
		this.itemData = itemData;
	}
	public String getOhk() {
		return ohk;
	}
	public void setOhk(String ohk) {
		this.ohk = ohk;
	}
	public double getTotalOrderAmount() {
		return totalOrderAmount;
	}
	public void setTotalOrderAmount(double totalOrderAmount) {
		this.totalOrderAmount = totalOrderAmount;
	}
	public OrderTypes getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderTypes orderType) {
		this.orderType = orderType;
	}
	public Map<String, String> getOrderQty() {
		return orderQty;
	}
	public void setOrderQty(Map<String, String> orderQty) {
		this.orderQty = orderQty;
	}
	public Map<String, String> getLinePriceSingleQty() {
		return linePriceSingleQty;
	}
	public void setLinePriceSingleQty(Map<String, String> linePriceSingleQty) {
		this.linePriceSingleQty = linePriceSingleQty;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
}

