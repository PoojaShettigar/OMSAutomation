package com.perfaware.automation.oms.sterling.common.fileReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelFileReader {

	private static final ArrayList String = null;

	public  List<Map<String, String>> readTestDataFromExcel(FileInputStream fis,XSSFSheet sheet,String key) throws IOException {
		int headerIndex=0;
		int endIndex=0;
		List<Map<String, String>> data = new ArrayList<>();
		try 
		{	
			final Iterator<Row> rowIterator = sheet.rowIterator() ;
			headerIndex=getKeyIndex(key, rowIterator);
			endIndex=getKeyIndex("END", rowIterator);
			Row row = sheet.getRow(headerIndex+1);
			List<String> headers = getSubHeaderNames(row);
			data = readData(headerIndex,endIndex,headers,sheet);
			fis.close();
			return data;	
		}
		catch (final Exception e) {
			fis.close();
			return data;
		}
	
	}
	
	
	public static synchronized Integer getKeyIndex(String key, Iterator<Row> rowIterator) {
		String excelKey = "";
		while(rowIterator.hasNext()) {
			XSSFRow row=(XSSFRow) rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while(cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if(cell.getColumnIndex()==0) {
					excelKey=convertToString(cell);
					if (key.equalsIgnoreCase(excelKey)) {
						return cell.getRowIndex();
					}
				}
			}
		}
		return null;
	}
	
	public static List<Map<String, String>> readData(int headerIndex , int endIndex, List<String> headers, XSSFSheet sheet ) {
		List<Map<String, String>> headersAndValues = new ArrayList<>();
		
		for (int i = headerIndex+1  ; i < endIndex; i++) {
			Map<String, String> headerAndValue = new HashMap<>();
			Row rowData = sheet.getRow(i);
			if (rowData != null) {
				for (int j = 0; j < rowData.getLastCellNum(); j++) {
					Cell cell = rowData.getCell(j);
					if (cell != null) {
						String header = "";
						header=convertToString(cell);
						if (!header.equalsIgnoreCase("")) {
							String[] headerSplit = headers.get(j).split("\\.");
							if(headerSplit.length > 1) {
								headerAndValue.put(headerSplit[headerSplit.length - 2]+"_"+headerSplit[headerSplit.length - 1], header);
							}
							else {
								headerAndValue.put(headerSplit[headerSplit.length - 1], header);
							}								
						}
					}
				}

				if (!headerAndValue.isEmpty()) {
					headersAndValues.add(headerAndValue);
				}
			}
		}
		return headersAndValues;
	}

	public HashMap<String, String> readExcelJiraTickets(FileInputStream file, XSSFSheet worksheet) {
		try 
		{
			Iterator<Row> rowIterator = worksheet.iterator();			
			HashMap<String, String> headerAndValue = new HashMap<>();	
			while (rowIterator.hasNext()) {
				String testcase_name = "";
				String jira_id = "";
				Row rowData = rowIterator.next();
				if (rowData != null) {
					
					boolean testname=true;
					for (int j = 0; j < 2; j++) {
						Cell cell = rowData.getCell(j);
						String value="";
						if (cell != null) {
							convertToString(cell);
							if(testname) {
								testcase_name =  value;
								testname=false;
							}else {
								jira_id = value;
							}
						}
					}	
				}
				headerAndValue.put(testcase_name, jira_id);
			}
			file.close();
			return headerAndValue;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String convertToString(Cell cell) {
		String excelKey="";
		try {
			switch (cell.getCellType()) {
			case STRING:
				excelKey=cell.getStringCellValue();
				break;
			case NUMERIC:
				excelKey=NumberToTextConverter.toText(cell.getNumericCellValue());
				break;
			case BOOLEAN:
				excelKey=Boolean.toString(cell.getBooleanCellValue());
				break;
			case BLANK:
				break;
			default:
				break;
			}
		}
		catch(Exception e) {
			return null;
		}
		return excelKey;
	}
	
	public static List<String> getSubHeaderNames(Row row) {
		List<String> headers = new ArrayList<>();
		for (int i = 0; i < row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			String header = "";
			header=convertToString(cell);
			headers.add(header);
		}

		return headers;
	}
	public double  calculateTotalAmt(List<Map<String, String>> data,String colName) {
		double amt=0;
		for (int k = 1; k < data.size(); k++) {
			Map<String, String> values = data.get(k);
			for (Map.Entry<String,String> entry : values.entrySet()) 
			{
				if(entry.getKey().equalsIgnoreCase(colName)) {
					amt = amt+Double.parseDouble(entry.getValue());
				}
				
			}
		}
		return amt;
	}
	
	public Map<String, String> readEntireExcelData(FileInputStream file, XSSFSheet worksheet, int i) {
		Map<String, String> headerAndValue = new HashMap<>();
		List<Map<String, String>> data1 = new ArrayList<>();
		HashMap<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		List<String> headers = getSubHeaderNames(worksheet.getRow(0));
		int colCount=worksheet.getRow(0).getLastCellNum();
		XSSFRow row = worksheet.getRow(i);	
		for(int j=0;j<colCount;j++) {
			XSSFCell cell = row.getCell(j);
			headerAndValue.put(headers.get(j),convertToString(cell));	
		}
		return headerAndValue;
		
		
		
	}
	
	public List<String> getFisrtColumnData(XSSFSheet worksheet) {
		int rowCount = worksheet.getPhysicalNumberOfRows();
		List<String> tcNo=new ArrayList<String>();
	    for (int i = 1; i < rowCount; i++) 
	    {
	    	XSSFRow row = worksheet.getRow(i);
	        XSSFCell cell = row.getCell(0);
	        tcNo.add(cell.getStringCellValue());
	        
	     }
	    return tcNo;
	}
	

}
