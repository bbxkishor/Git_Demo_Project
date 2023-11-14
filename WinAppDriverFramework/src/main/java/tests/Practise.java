package tests;

import static io.restassured.RestAssured.given; 
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;

import org.apache.logging.log4j.core.config.ConfigurationFileWatcher;
import org.apache.maven.model.Build;
import org.apache.poi.ss.formula.functions.Replace;
import org.apache.poi.xwpf.usermodel.Document;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.DoubleClickAction;
import org.openqa.selenium.interactions.KeyDownAction;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.log4testng.Logger;

import io.appium.java_client.windows.WindowsDriver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import methods.AppliancePool;
import methods.AutologinAutoConnect;
import methods.Device;
import methods.Devices;
import methods.Discovery;
import methods.DiscoveryMethods;
import methods.RA_Methods;
import methods.SeleniumActions;
import methods.SystemAll;
import methods.SystemMethods;
import methods.UserMethods;
import methods.Users;
import pages.BoxillaHeaders;
import pages.ConnectionPage;
import pages.LandingPage;
import pages.UserPage;
import pages.boxillaElements;

public class Practise extends TestBase{
	public static void main(String args[])  
	{  
		int[] arr=getArray();           
		for (int i = 0; i < arr.length; i++) 
			System.out.print( arr[i]+ " ");     
	}  
	public static int[] getArray()  
	{  
		int[] arr={17,18,19,20,21};   
		return arr;  
	}  

	//@Test
	public void practiseCode() throws Exception
	{
		
		
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Windows\\System32\\notepad.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");
		//capabilities.setCapability("appWorkingDir", "C:\\Windows\\System32\\notepad.exe");


		Thread.sleep(1000);
		
		Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		//Root session (windows)
		DesiredCapabilities appCapabilities = new DesiredCapabilities();
		appCapabilities.setCapability("app", "Root");
		Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);
		Windriver2.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);
		System.out.println("Notepad Launched SuccessFully");	

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Thread.sleep(2000);

		Windriver.findElementByName("Text editor").sendKeys("hh:");
		Actions action = new Actions(Windriver);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		printTestDetails("Starting", "Test15_AutoConnect_shouldnot_support_for_Offline_TX", " ");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();
		
		
		cleanUpLogin();
		ArrayList connections = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();
		
		
		RAlogin(RAusername, RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		//Thread.sleep(50000);
		
		autologin.AutologinEnable(Windriver);
		autologin.relogin(Windriver, RAusername, RApassword);
		Thread.sleep(3000);
		//autologin.dragConnection(Windriver2, connections);
		autologin.AutoConnectEnable(Windriver);
		autologin.saveCurrentLayout(Windriver);
		int a[] = AutologinAutoConnect.TXConnectionSizeandLocationinfo(Windriver2);
		System.out.println(Arrays.toString(a) +" is the size and position of connection before enabling Auto Connect");
		int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active Connections before enabling AutoConnect are : " + connection);
		
		
		Thread.sleep(3000);
		closeApp();
		Thread.sleep(10000);
	
		autologin.configFileEdit(Windriver, connections, RAusername, 500, 500, 1040, 800);
		
		setup();

		//Root session (windows)
		DesiredCapabilities appCapabilities = new DesiredCapabilities();
		appCapabilities.setCapability("app", "Root");
		Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

		int b[] = AutologinAutoConnect.TXConnectionSizeandLocationinfo(Windriver2);
		System.out.println(Arrays.toString(b) +" is the size and position of connection After Relaunching RA");
		int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active Connections after Relaunching RA are : " + connection1);
		soft.assertEquals(connection, connection1, "Connections are not Launched after enabling AutoLogin and AutoConnect, are not working ");
		soft.assertEquals(Arrays.toString(a), Arrays.toString(b), "Size and position of Connections didn't matched in Auto Connect Feature");
		Thread.sleep(3000);

		System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
		Thread.sleep(50000);
		//int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		//System.out.println("Number of Active Connections before enabling AutoLogin and AutoConnect are : " + connection1);
		soft.assertEquals(connection, connection1, "Connections are not Launched after enabling AutoLogin and AutoConnect, are not working ");
		Thread.sleep(3000);

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		autologin.AutoLoginDisable(Windriver);
		autologin.AutoConnectDisable(Windriver);
		closeApp();

		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();	
		soft.assertAll();
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Windows\\System32\\notepad.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");

		try
		{
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);
			System.out.println("Notepad Launched SuccessFully");

		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Notepad not Launched, Exception has occured");
		}
		Actions action = new Actions(Windriver);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.findElementByName("Text Editor").click();
		action.sendKeys(Keys.chord(Keys.CONTROL,"o")).build().perform();
		action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\AppData\\Local\\BlackBox\\EmeraldRA\\windows.config");
		action.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Windriver.findElementByName("Text Editor").click();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"a")).build().perform();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();

		String con = connections.get(0).toString().replace("[","").replace("]", "");
		action.sendKeys("["+RAusername+"]");
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.ENTER)).build().perform();

		for(int i=0; i<connections.size();i++)
		{
			int x = 100, y=100, width=100, height =100;
			
			x= x+100;
			y= y+100;
			String con1 = connections.get(i).toString();
			action.sendKeys(con1+", "+x+", "+y+", "+width+", "+height);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			action.sendKeys(Keys.chord(Keys.ENTER)).build().perform();	
		}
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"s")).build().perform();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"w")).build().perform();

*/
		
		/*DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Windows\\System32\\notepad.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");

		Thread.sleep(1000);
		Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);
		System.out.println("Notepad Launched SuccessFully");	

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		Windriver.findElementByName("Text Editor").click();
		Actions action = new Actions(Windriver);
		action.sendKeys(Keys.chord(Keys.CONTROL,"o")).build().perform();

		action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();


		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\AppData\\Local\\BlackBox\\EmeraldRA\\windows.config");
		action.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Windriver.findElementByName("Text Editor").click();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		String s = "pe,";

		List<WebElement> text = (List<WebElement>) Windriver.findElementsByName("Text Editor");
		System.out.println(text);

		//Document doc = new Document();
		//DocumentBuilder builder = new DocumentBuilder(doc);



		String[] words = text.split(" ");

		if(text.contains(s))
		{
			text.replace(s, " ");
		}*/



		/*for (WebElement device : text) 
			String 
		{
			if(device.getText().contains(s))
			{
				System.out.println("text is present");
				System.out.println(device);
				device.click();

				action.sendKeys(Keys.chord(Keys.HOME)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				action.sendKeys(Keys.chord(Keys.SHIFT, Keys.END)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				action.sendKeys("vce").build().perform();
			}
		}

		 */


		/*


		String att = Windriver.findElementByName("Text Editor").getAttribute("Value.Value");
		System.out.println(text);





		if(text.contains(s))
		{


			Document doc = new Document();
			DocumentBuilder builder = new DocumentBuilder(doc);

			text.

			att.replace("pe," , " ");
			text.replace("pe, "	," ");
			action.sendKeys(Keys.chord(Keys.HOME)).build().perform();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			action.sendKeys(Keys.chord(Keys.SHIFT, Keys.END)).build().perform();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			action.sendKeys("vce").build().perform();
			//Windriver.
			System.out.println("tezt contains");
		}
		System.out.println(text);*/
	}
}



