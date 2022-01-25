package com.perfaware.automation.oms.sterling.common.utils;

import java.io.File;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;

import com.perfaware.automation.oms.sterling.common.fileReader.PropertyFileReader;
import com.perfaware.automation.oms.sterling.common.testreportsUtils.ExtentManager;

public class Utilities {
	
	
	/**
   	 * This function archive the execution logs to an archived folder
   	 * @author Perfaware
   	 */
   	public void archieveLastLogs(String src) throws IOException{
        new File(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("archieveLogFolder")).mkdirs();
        copyDirectory(new File(src),new File(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("archieveLogFolder")));
        deleteDirectory(new File(src));
      }
   	
     /**
      * This function archive the execution reports to an archived folder
     * @author Perfaware
     */
    public void archieveLastReports(String src) throws IOException{
         new File(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("archieveFolder")).mkdirs();
          copyDirectory(new File(src),new File(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("archieveFolder")));
          deleteDirectory(new File(src));
      }
   	
    /**
     * This function archive the execution screenshots to an archived folder
    * @author Perfaware
    */
   public void archieveLatsScreenshots(String src) throws IOException{
        new File(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("archieveScreenshotFolder")).mkdirs();
         copyDirectory(new File(src),new File(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("archieveScreenshotFolder")));
         deleteDirectory(new File(src));
     }
   
   /**
    * This function is to copy a source directory with its contents to a destination
    * @author Perfaware
    */
   public  void copyDirectory(File srcDir, File dstDir) throws IOException {
       FileUtils.copyDirectoryToDirectory(srcDir, dstDir);
   }
   
   /**
    * This function is to copy a source file with its contents to a destination
    * @author Perfaware
    */
   public  void copyFile(File srcDir, File dstDir) throws IOException {
	   FileUtils.copyFile(srcDir, dstDir);
   }
   
   /**
    * This function is to delete a directory
    * @author Perfaware
    */
   public  void deleteDirectory(File srcDir) throws IOException {
       FileUtils.deleteDirectory(srcDir);

   } 
   
  	
  	/**
   	 * This function returns the current timeStamp in the specified format
   	 * @author Perfaware
   	 */
   	public String getCurrentDateTime() {
   		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
   		Date date = new Date();   		
   		return dateFormat.format(date);
   	}
   	
   	/**
     * This function renames the folder as per the current time stamp
     * @author Perfaware
     */
    public String formatTimeStamp(String folder) {
        return folder+ "/"+ (getCurrentDateTime().replaceAll("/", "-").replaceAll(":", "-"));
    }
    
    /**
   	 * This function generates a random String of a specified length
   	 * @author Perfaware
   	 */
   	public static String generateRandomString(int n) {
   		String str = "012345689";
   		StringBuilder sb = new StringBuilder(n);
   		for (int i = 0; i < n; i++) { 
   			int index = (int)(str.length()* Math.random());
   			sb.append(str.charAt(index)); 
   		}
   		return sb.toString();
   	}
   	
    public void sendReportInEmail() throws MessagingException {
    	Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		// get Session
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("testerof2021@gmail.com", "Welcome@123");
			}
		});

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

			MimeMessage msg = new MimeMessage(session);
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setReplyTo(InternetAddress.parse("to email id", false));
			String subject="Test Report of OMS Automation Run_"+getCurrentDateTime().replaceAll("/", "-")+"_"+PropertyFileReader.propertyMap.get("environment");
			msg.setSubject(subject, "UTF-8");
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("perfomsautomation@gmail.com"));

	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	
	        Multipart multipart = new MimeMultipart();
	         messageBodyPart = new MimeBodyPart();
	         String reportFolder=ExtentManager.reportFolder;
	         String reportName=ExtentManager.reportPropertyMap.get("htmlReportName");
	         String reportPath=reportFolder+reportName;
	         System.out.println("Report folder----> "+reportPath);
	         DataSource source = new FileDataSource(reportPath);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName("RegressionSuite_TestResults.html");
	         multipart.addBodyPart(messageBodyPart);
	
	         msg.setContent(multipart);
	         //send the message  
	         Transport.send(msg);
	         System.out.println("Email sent");
    }
  	
}
