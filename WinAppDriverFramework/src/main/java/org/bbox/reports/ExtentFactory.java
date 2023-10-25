/*package org.bbox.reports;

import com.aventstack.extentreports.ExtentTest;


// If we running the multiple test case parallely with reference to same object,
// We need to have Thread Safe


public class ExtentFactory {
	//Singleton design Pattern
	//private constructor so that no one else can create object of this class
	private ExtentFactory() {

	}

	private static ExtentFactory instance  = new ExtentFactory();

	public static ExtentFactory getInstance() {
		return instance;
	}


	//factory design pattern --> define separate factory methods for creating objects and create objects by calling that methods
	ThreadLocal<ExtentTest> extent = new ThreadLocal<ExtentTest>();

	public ExtentTest getExtent() {
		return extent.get();
	}

	public void setExtent(ExtentTest extentTestObject) {
		extent.set(extentTestObject);
	}

	public void removeExtentObject() {
		extent.remove();
	}
}
*/