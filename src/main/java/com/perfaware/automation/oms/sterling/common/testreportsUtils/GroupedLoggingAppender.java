package com.perfaware.automation.oms.sterling.common.testreportsUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import com.perfaware.automation.oms.sterling.common.utils.Utilities;



/**
 *  This appender logs groups test outputs by test method
 *  so they don't mess up each other even they runs in parallel.
 *  magic is done by output file into different file for different threads
 *  then merge them in the end
 *
 * Set -Doutputdir=/x/y/z/ org.testng.TestNG -reporter com.fayaa.testnglog.GroupedLoggingAppender
 *    when you run the test, and make sure the outputdir exists
 *   
 * if you don't set anything, by default the reporter does nothing
 *
 */
public class GroupedLoggingAppender extends AppenderSkeleton implements IReporter {


	private final String outputDir ;
	private  String outputFile;
	private final String ext = ".threadlog.log";
	Utilities utilities;
	
	public GroupedLoggingAppender() {
		
		String outdir = System.getProperty("outputdir");
		//System.out.println("Output directory - "+outdir);
		if (!outdir.endsWith("/"))
			outdir += "/";
		outputDir = outdir;
		utilities=new Utilities();
		outputFile = "RegressionSuite_TestResults.log";
		try {
			if (outputDir != null) {
				Files.deleteIfExists(FileSystems.getDefault().getPath(outputFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		//		System.out.println("Reporter getting called! " + outputDir);
		// we don't do any report generation here, just clean up the log files
		mergeLogFiles();
	}
	private void mergeLogFiles() {
		try {
			File file = new File(outputDir);

			File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(ext);
				}
			});

			List<Path> paths = new ArrayList<Path>();
			for (File f : files) {
				Path path = f.toPath();
				paths.add(path);

			}
			Collections.sort(paths);
			String timeStampFolder = utilities.formatTimeStamp("");
			new File(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("logFolder")+"/"+timeStampFolder).mkdirs();
			Path pathAll = FileSystems.getDefault().getPath(System.getProperty("user.dir")+ExtentManager.reportPropertyMap.get("logFolder")+timeStampFolder+"/"+outputFile);
			for (Path path : paths) {
				Files.write(pathAll, Files.readAllBytes(path), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
				Files.delete(path);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	@Override
	public void close() {

		
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (outputDir == null)
			return; // by default nothing appended, see comments on top
		try {
			long tid = Thread.currentThread().getId();
			Path path = FileSystems.getDefault().getPath(getFileNameFromThreadID(tid)); 
			BufferedWriter bw = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.APPEND, StandardOpenOption.CREATE);    
			String content = event.getMessage().toString();
			bw.write(content, 0, content.length());
			bw.write("\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private String getFileNameFromThreadID(long tid) {
		return String.format("%sthread_output_%04d%s", outputDir, tid, ext);
	}
}
