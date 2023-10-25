/*package org.bbox.reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportNG {
	
	static ExtentReports extent;

	public static ExtentReports setupExtentReport() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH-mm-ss");
		Date date = new Date();
		String actualDate = format.format(date);
		
		String reportPath = System.getProperty("user.dir")+
				"/Reports/AutomationReport_"+actualDate+".html";
		
		ExtentSparkReporter sparkReport = new ExtentSparkReporter(reportPath);
		
		 extent = new ExtentReports();
		 extent.attachReporter(sparkReport);
		
		sparkReport.config().setDocumentTitle("Remote App Automation");
		sparkReport.config().setTheme(Theme.STANDARD);
		sparkReport.config().setReportName("Automation Test Results");
		
		extent.setSystemInfo("Organisation Name", "BlackBox");
		extent.setSystemInfo("Executed by User: ", System.getProperty("user.name"));
		extent.setSystemInfo("Executed on Browser: ", "Firefox");
		extent.setSystemInfo("Executed on OS: ", System.getProperty("os.name"));

		return extent;
	}


}*/