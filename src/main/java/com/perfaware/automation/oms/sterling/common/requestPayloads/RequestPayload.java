package com.perfaware.automation.oms.sterling.common.requestPayloads;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.log4j.Logger;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentFactory;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;


public class RequestPayload {
	public String resource;
	public String pathParams;
	public HashMap<String, String> headers;
	public String body;
	public ContentType contentType;
	public RequestSpecification httpRequest;
	public String requestType;
	public Map<String, String> queryParam;
	static ThreadLocal<StringWriter> requestWriter, responseWriter, errorWriter;
	static ThreadLocal<PrintStream> requestCapture, responseCapture, errorCapture;
	public String username;
	public String password;
	
	private static final Logger LOGGER = Logger.getLogger(RequestPayload.class);

	public RequestPayload(String requestType) {
		httpRequest = createAPIRequest();
		resource = "";
		headers = new HashMap<String, String>();
		this.requestType = requestType;
		body = "";
		pathParams = "";
		queryParam = new HashMap<String, String>();
		
	}

	public RequestPayload(String requestType,String contentType) {
		httpRequest = createAPIRequestJson();
		resource = "";
		headers = new HashMap<String, String>();
		this.requestType = requestType;
		body = "";
		pathParams = "";
		queryParam = new HashMap<String, String>();
		
	}
	
	public RequestPayload() {
		httpRequest = createAPIRequest();
		resource = "";
		headers = new HashMap<String, String>();
		this.requestType = "";
		body = "";
		pathParams = "";
		queryParam = new HashMap<String, String>();
	}
	
	
	public RequestPayload(Boolean basicAuthRequired,String requestType) {
		
		httpRequest = createAPIRequestWithOutBasicAuth();
		resource = "";
		headers = new HashMap<String, String>();
		this.requestType = requestType;
		body = "";
		pathParams = "";
		queryParam = new HashMap<String, String>();
		
	}
	

	static {
		requestWriter=new ThreadLocal<StringWriter>();
		requestCapture=new ThreadLocal<PrintStream>();
		responseWriter=new ThreadLocal<StringWriter>();
		responseCapture=new ThreadLocal<PrintStream>();		
	}
	
	/**
	 * This function will initialize Rest Assured request content-type, basic authentication and initiate filters
	 * @author Perfaware
	 */
	public RequestSpecification createAPIRequest() {
		 RequestSpecification httRequestSpecification = RestAssured.given()
				 	.config(RestAssured.config().encoderConfig(RestAssured.config().getEncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));		 
		httRequestSpecification.contentType("application/xml").accept("application/xml")
		.auth().preemptive().basic(PropertyFileReader.propertyMap.get("username"),
				PropertyFileReader.propertyMap.get("password"));
//		 RequestSpecification httRequestSpecification = RestAssured.given()
//				 	.config(RestAssured.config().encoderConfig(RestAssured.config().getEncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));		 
//		httRequestSpecification.contentType("application/xml").accept("application/xml");
		
		
		httRequestSpecification = initiateFilters(httRequestSpecification);
		
		return httRequestSpecification;
	}

	
	/**
	 * This function will initialize Request and Response Filters to be utilized while
	 * logging request and response in log file. 
	 * @author Perfaware
	 * @Param : Request Specification request
	 */

	public RequestSpecification initiateFilters(RequestSpecification httpRequest) {
		try {
			requestWriter.set(new StringWriter());
			requestCapture.set(new PrintStream(new WriterOutputStream(requestWriter.get()), true));
			responseWriter.set(new StringWriter());
			responseCapture.set(new PrintStream(new WriterOutputStream(responseWriter.get()), true));
			
			httpRequest.filter(new RequestLoggingFilter(requestCapture.get()))
					.filter(new ResponseLoggingFilter(responseCapture.get()));
			return httpRequest;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	

	/**
	 * This function will initialize Rest Assured request content-type, basic authentication and initiate filters
	 * @author Perfaware
	 */
	
	public RequestSpecification createAPIRequestJson() {

		 RequestSpecification httRequestSpecification = RestAssured.given()
				 	.config(RestAssured.config().encoderConfig(RestAssured.config().getEncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));		 
		httRequestSpecification.contentType("application/json").accept("*/*")
		.auth().preemptive().basic(PropertyFileReader.propertyMap.get("username"),
				PropertyFileReader.propertyMap.get("password"));
		httRequestSpecification = initiateFilters(httRequestSpecification);
		return httRequestSpecification;
	}
	
	/**
	 * This function will initialize Rest Assured request content-type without basic authentication and initiate filters
	 * @author Perfaware
	 */
	public RequestSpecification createAPIRequestWithOutBasicAuth() {

		 RequestSpecification httRequestSpecification = RestAssured.given()
				.config(RestAssured.config().encoderConfig(RestAssured.config().getEncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));		 
		httRequestSpecification.contentType("application/xml");
		httRequestSpecification = initiateFilters(httRequestSpecification);
		return httRequestSpecification;
	}
	
	/**
	 * This function logs the request and response of the API
	 * @author Perfaware
	 */
	public void logRequestResponse() {
		LOGGER.info("API Request sent : ");
		LOGGER.info(requestWriter.get().toString());
		ExtentFactory.getInstance().getExtent().log(Status.INFO, MarkupHelper.createCodeBlock(requestWriter.get().toString()));
		LOGGER.info("API Response received : ");
		LOGGER.info(responseWriter.get().toString());
		ExtentFactory.getInstance().getExtent().log(Status.INFO, MarkupHelper.createCodeBlock(responseWriter.get().toString()));
	}
}
