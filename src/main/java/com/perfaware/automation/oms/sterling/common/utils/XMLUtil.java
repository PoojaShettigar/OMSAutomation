package com.perfaware.automation.oms.sterling.common.utils;

import java.io.File;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.restassured.path.xml.XmlPath;

public class XMLUtil {
	/**
	 * This function reads a XML file and returns a XML document
	 * @author Perfaware
	 * @param fileName - XML file

	 */
	public static Document readXmlFile(String fileName) throws ParserConfigurationException {
		File file = new File(fileName);
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
		try {
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement();
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Node getNodeByTagName(Document doc,String tagname) throws ParserConfigurationException {
		try {
			doc.getDocumentElement();
			NodeList nodes = doc.getElementsByTagName(tagname);
			Node node = nodes.item(0);
			return node;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String removeNode(Element element, Node node) throws ParserConfigurationException {
		try {
			//Node node = nodetobeclone.cloneNode(true);	
			if(node != null)
		    element.removeChild(node);
			return convertXMLDocumentToString(element.getOwnerDocument());
		} catch (Exception e) {
			
		}
		return null;
	}
	
	public static void setDataInNodeAtt(Element element,String nodePath,String attName,String attValue) throws Exception {
		
		String[] keySplit = nodePath.split("\\.");
		String attn = keySplit[keySplit.length -1];
		Node finalNode = getNodeByTravellingIntoArray((Element)element, keySplit);
		if(finalNode != null) {
        Node nodeatt = finalNode.getAttributes().getNamedItem(attn);
		if (nodeatt != null) {
			nodeatt.setNodeValue(attValue);
		}
		}
	}
	/**
	 * This function reads a XML document and converts it to String
	 * @author Perfaware
	 * @param document - XML document
	 */
	public static String convertXMLDocumentToString(Document document) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			// below code to remove XML declaration
			// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			return output;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setDataInNodeAtt(Element element,String nodePath,String attValue) throws Exception {		
		//Element element = doc.getDocumentElement();
		String[] keySplit = nodePath.split("\\.");

		Node finalNode = getNodeByTravellingIntoArray((Element)element, keySplit);
		if(finalNode != null) {
			Node nodeatt = finalNode.getAttributes().getNamedItem(keySplit[keySplit.length - 1]);
			if (nodeatt != null) {
				nodeatt.setNodeValue(attValue);
			}
		}
	}
	
	public static Node getNodeByTravellingIntoArray(Element element, String nodePathTotravel[]) throws ParserConfigurationException {
		try {
			  Node finalNode = element; 
			  for(int i=1; i < nodePathTotravel.length - 1 ; i++)
			  {
				NodeList node = element.getElementsByTagName(nodePathTotravel[i]);
				finalNode = node.item(0);

			  }
			  return finalNode;
			} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String appendNode(Element element, Node nodetobeclone) throws ParserConfigurationException {
		try {
			Node node = nodetobeclone.cloneNode(true);		     
		    element.appendChild(node);
			return convertXMLDocumentToString(element.getOwnerDocument());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setDataToXmlNodesWithDiffHeaders(List<Map<String, String>> testdata,NodeList crawlNodeList,int startupnodesettingdata,int nooflines ,int startupnodeval,Map<String, String> mappedTags  ) throws Exception {
			
			for (int k = 1; k < testdata.size(); k++) {
				Map<String, String> header = testdata.get(0);
				for (int i = 0; i < nooflines; i++) {
					Node subNodeMap = crawlNodeList.item(i+ startupnodesettingdata + startupnodeval);
					Map<String, String> apiData = testdata.get(i+1);
					for (Map.Entry<String,String> entry : apiData.entrySet()) 
					{
						if(mappedTags.get(header.get(entry.getKey())) != null) {
						String nodePath = mappedTags.get(header.get(entry.getKey()));
						String[] keySplit = nodePath.split("\\.");
						Node finalNode = getNodeByTravellingIntoArray((Element)subNodeMap, keySplit);
			            Node nodeatt = finalNode.getAttributes().getNamedItem(keySplit[keySplit.length - 1]);
							if (nodeatt != null) {
								nodeatt.setNodeValue(apiData.get(entry.getKey()));
							}					
						}
					}				
				}
			}
		}

	//Perfaware  --	Start
	public static Document convertStringToXmlDoc(String tag) throws ParserConfigurationException {
		//File file = new File(fileName);
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
		try {
			Document doc = dBuilder.parse(new StringBufferInputStream(tag));
			doc.getDocumentElement();
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document createDocFromNode(Element node) throws ParserConfigurationException {
		DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
		try {
			Document doc = dBuilder.newDocument();
			Node importedNode = doc.importNode(node, true);
			doc.appendChild(importedNode);
			return doc;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 public static String getDataInNodeAtt(Element element,String nodePath,String attName) throws Exception {
			String val = null;
			String[] keySplit = nodePath.split("\\.");
			Node finalNode = getNodeByTravellingIntoArray((Element)element, keySplit);
	        Node nodeatt = finalNode.getAttributes().getNamedItem(attName);
			if (nodeatt != null) {
				val = nodeatt.getNodeValue();
			}
			return val;
	   }
	 
	 public static String appendNodeBeforeNode(Element element, Node nodetobeclone,Node beforeNode) throws ParserConfigurationException {
			try {
				Node node = nodetobeclone.cloneNode(true);		     
			    element.insertBefore(node, beforeNode);
				return convertXMLDocumentToString(element.getOwnerDocument());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	 
	public static void setDataToXmlNodes(List<Map<String, String>> testdata,NodeList crawlNodeList,int startupnodesettingdata,int nooflines ,int startupnodeval  ) throws Exception {
		
		for (int k = 1; k < testdata.size(); k++) {
			Map<String, String> header = testdata.get(0);
			for (int i = 0; i < nooflines; i++) {
				Node subNodeMap = crawlNodeList.item(i+ startupnodesettingdata + startupnodeval);
				Map<String, String> apiData = testdata.get(i+1);
				for (Map.Entry<String,String> entry : apiData.entrySet()) 
				{
					String nodePath = header.get(entry.getKey());
					//String[] keySplit = nodePath.split("\\.");
					/*Node finalNode = getNodeByTravellingIntoArray((Element)subNodeMap, keySplit);
		            Node nodeatt = finalNode.getAttributes().getNamedItem(entry.getKey());
					if (nodeatt != null) {
						nodeatt.setNodeValue(apiData.get(entry.getKey()));
					}*/
					
					setDataInNodeAtt((Element)subNodeMap,nodePath,entry.getKey(),apiData.get(entry.getKey()));        
				}
				
			}
		}
	}
	
	public static void setDataToXmlNodesForPayments(List<Map<String, String>> testdata,NodeList crawlNodeList,int startupnodesettingdata,int nooflines ,int startupnodeval, int row  ) throws Exception {
		
		for (int k = 1; k < testdata.size(); k++) {
			Map<String, String> header = testdata.get(0);
			for (int i = 0; i < nooflines; i++) {
				Node subNodeMap = crawlNodeList.item(i+ startupnodesettingdata + startupnodeval);
				Map<String, String> apiData = testdata.get(i+row);
				for (Map.Entry<String,String> entry : apiData.entrySet()) 
				{
					String nodePath = header.get(entry.getKey());
					//String[] keySplit = nodePath.split("\\.");
					/*Node finalNode = getNodeByTravellingIntoArray((Element)subNodeMap, keySplit);
		            Node nodeatt = finalNode.getAttributes().getNamedItem(entry.getKey());
					if (nodeatt != null) {
						nodeatt.setNodeValue(apiData.get(entry.getKey()));
					}*/
					
					setDataInNodeAtt((Element)subNodeMap,nodePath,entry.getKey(),apiData.get(entry.getKey()));        
				}
				
			}
		}
	}
	
	/**
	 * This function returns the xml Path object to traverse through an XML document
	 * @author Perfaware
	 */
	public static XmlPath getXmlPath(String result) {
		XmlPath xmlpath = new XmlPath(result);
		return xmlpath;
	}

}
