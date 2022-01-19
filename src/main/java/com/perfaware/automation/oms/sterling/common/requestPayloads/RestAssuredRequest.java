package com.perfaware.automation.oms.sterling.common.requestPayloads;

import org.apache.log4j.Logger;
import com.aventstack.extentreports.Status;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentFactory;
import io.restassured.response.Response;

public class RestAssuredRequest {
	private static final Logger LOGGER = Logger.getLogger(RestAssuredRequest.class);	
	
	/**
	  * This Function will create a Rest API request and perform operations based on RequestPayload object
	  * @author Perfaware
	  *
	 * */
	   public final static Response sendAPIRequest(RequestPayload payload) {
		   try {
				Response resp=null;	 				
				payload.httpRequest.headers(payload.headers);			
			   	LOGGER.info("Calling "+payload.requestType+" request with end point : "+payload.resource);	   	
			    ExtentFactory.getInstance().getExtent().log(Status.PASS, "Calling "+payload.requestType+" request with end point : "+payload.resource);
			    
				switch (payload.requestType.toLowerCase()) {
			   	case "get":
					resp = payload.httpRequest
					  .queryParams(payload.queryParam)
		  	   		  .when().get(payload.resource);
					break;
	
				case "post":
					payload.httpRequest.body(payload.body).queryParams(payload.queryParam);	
					System.out.println(payload.httpRequest.body(payload.body).queryParams(payload.queryParam));
			   		resp = payload.httpRequest
			  	   		  .when().post(payload.resource);
			   		System.out.println(resp);
					break;
					
				case "put":
					payload.httpRequest.body(payload.body);
			   		resp = payload.httpRequest
			  	   		  .when().put(payload.resource);		   	
					break;
					
				case "patch":
					payload.httpRequest.body(payload.body);						
			   		resp = payload.httpRequest
			  	   		  .when().patch(payload.resource);
					break;
					
				case "delete":
					payload.httpRequest.queryParams(payload.queryParam);
			   		resp = payload.httpRequest
			  	   		  .when().delete(payload.resource);
					
					break;
					
				default:
					break;
				} 
				payload.logRequestResponse();
				ExtentFactory.getInstance().getExtent().log(Status.PASS, "API Status Code received: "+String.valueOf(resp.getStatusCode()));
				return resp;
				
			
		   }
		   catch (Exception e) {
				e.printStackTrace();	
				throw e;
		   }		   
		}
}
