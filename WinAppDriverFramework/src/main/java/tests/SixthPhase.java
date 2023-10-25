package tests;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.log4testng.Logger;

import com.google.common.collect.Lists;

import groovy.util.logging.Log4j;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import methods.AppliancePool;
import methods.AutologinAutoConnect;
import methods.Device;
import methods.Devices;
import methods.DiscoveryMethods;
import methods.RA_Methods;
import methods.SeleniumActions;
import methods.SystemAll;
import methods.SystemMethods;
import methods.UserMethods;
import pages.BoxillaHeaders;
import pages.ConnectionPage;
import pages.LandingPage;
import pages.UserPage;
import static io.restassured.RestAssured.given;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import pages.ConnectionPage;
import pages.UserPage;

public class SixthPhase extends TestBase {



	@Test(priority =1) //Automatically Logging into Remote App and Auto Connect with TX Connection
	public void Test01_AC0002_AutoLogin_and_AutoConnect_ShouldSupport_TX_Connection() throws Exception
	{
		printTestDetails("Starting", "Test01_AC0002_AutoLogin_and_AutoConnect_ShouldSupport_TX_Connection", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoLogin and AutoConnect are : " + connection);
			
			
			WebElement autoConnectCheckbox = Windriver.findElementByAccessibilityId("AutoConnectCheckBox");
			if (autoConnectCheckbox.isSelected()) {
				System.out.println("autoConnect is enabled");
				} else {
				System.out.println("autoConnect is disabled");
				}
			autoConnectCheckbox.click();
			
			if (autoConnectCheckbox.isSelected()) {
				System.out.println("autoConnect is enabled");
				} else {
				System.out.println("autoConnect is disabled");
				}
			
			boolean autoConnectStatus = Boolean.parseBoolean(autoConnectCheckbox.getAttribute("Toggle.ToggleState"));

			if (autoConnectStatus == true) {
			System.out.println("autoConnect is enabled");
			} else {
			System.out.println("autoConnect is disabled");
			}
			
			
			
			
			
			

			//Enabling AutoConnect and AutoLogin
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, RAusername, RApassword);
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Again Launching RA 
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("ms:waitForAppLaunch", 40);
			capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(60000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoLogin and AutoConnect are : " + connection1);
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
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}



	@Test(priority =2) //Automatically Logging into Remote App-relaunch the app
	public void Test02_AC0002_AutoLogin_Should_Support() throws Exception
	{
		printTestDetails("Starting", "Test02_AC0002_AutoLogin_Should_Support", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);

			//Enabling AutoLogin
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, RAusername, RApassword);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Again Launching RA 
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("ms:waitForAppLaunch", 40);
			capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(10000);


			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("exitMenuItems")));
			System.out.println("AutoLogin is Working as Expected");
			autologin.AutoLoginDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}



	@Test(priority =3) //Should not Support Automatically Logging into Remote App-incorrect user credentials-incorrect user name
	public void Test03_AC0002_AutoLogin_with_invalid_username_Shouldnot_Support() throws Exception
	{
		printTestDetails("Starting", "Test03_AC0002_AutoLogin_with_invalid_username_Shouldnot_Support", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		userpage.createUser(firedrive, Onedevices, AutomationUsername, AutomationPassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);

			//Enabling AutoLogin

			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, AutomationUsername, RApassword);
			Thread.sleep(4000);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByName("Log In: Invalid Login Credentials")));
			WebElement popUpWindow1 =Windriver.findElementByName("Log In: Invalid Login Credentials");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
			System.out.println("Pop-up window is  "+popUpWindow1.getText());
			Thread.sleep(3000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Again Launching RA 
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("ms:waitForAppLaunch", 40);
			capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(10000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("LogInScreenTitleLabel")));
			System.out.println("AutoLogin is Not Supported with invalid Username");
			Thread.sleep(3000);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			UserPage.DeleteUser(firedrive, AutomationUsername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			UserPage.DeleteUser(firedrive, AutomationUsername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority =4) //Should not Support Automatically Logging into Remote App-incorrect user credentials-incorrect password
	public void Test04_AC0002_AutoLogin_with_invalid_password_Shouldnot_Support() throws Exception
	{
		printTestDetails("Starting", "Test04_AC0002_AutoLogin_with_invalid_password_Shouldnot_Support", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		userpage.createUser(firedrive, Onedevices, AutomationUsername, AutomationPassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			//Enabling AutoLogin
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, RAusername, AutomationPassword);
			Thread.sleep(4000);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByName("Log In: Invalid Login Credentials")));
			WebElement popUpWindow1 =Windriver.findElementByName("Log In: Invalid Login Credentials");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
			//System.out.println("Pop-up window is  "+popUpWindow1.getText());
			Thread.sleep(3000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Again Launching RA 
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("ms:waitForAppLaunch", 40);
			capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(5000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("LogInScreenTitleLabel")));
			System.out.println("AutoLogin is Not Supported with invalid Username");
			Thread.sleep(3000);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			UserPage.DeleteUser(firedrive, AutomationUsername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			UserPage.DeleteUser(firedrive, AutomationUsername);
			cleanUpLogout();
			Assert.fail();
		}
	}


	@Test(priority =5) //Should not Support Automatically Logging into Remote App-disable the Auto login
	public void Test05_AC0002_Not_enabling_AutoLogin_Btn_Shouldnot_Support() throws Exception
	{
		printTestDetails("Starting", "Test05_AC0002_Not_enabling_AutoLogin_Btn_Shouldnot_Support", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			autologin.AutologinEnable(Windriver);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Again Launching RA 
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("ms:waitForAppLaunch", 50);
			capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(10000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("LogInScreenTitleLabel")));
			System.out.println("AutoLogin is Not Supported with enabling AutoLogin Checkbox");
			Thread.sleep(3000);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception is Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}


	@Test(priority =6) //Should not Support Automatically Logging into Remote App-Invalid users
	public void Test06_AutoLogin_with_invalidusers_Shouldnot_Support() throws Exception
	{
		printTestDetails("Starting", "Test06_AutoLogin_with_invalidusers_Shouldnot_Support", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);

			//Enabling AutoLogin
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, VMUsername, VMPassword);
			Thread.sleep(5000);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByName("Boxilla Error (500).")));
			WebElement popUpWindow1 =Windriver.findElementByName("Boxilla Error (500).");
			//WebElement popUpWindow1 =Windriver.findElementByName("Log In: Boxilla Error");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
			System.out.println("Pop-up window is  "+popUpWindow1.getText());
			Thread.sleep(5000);
			//Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Again Launching RA 
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("ms:waitForAppLaunch", 50);
			capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(10000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("LogInScreenTitleLabel")));
			System.out.println("AutoLogin is Not Supported with invalid Users");
			Thread.sleep(3000);
			Windriver.findElementByAccessibilityId("closeLogInScreen").click();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority = 7)//Unable to Connect to the connection with invalid Boxilla Time.
	public void Test07_CLI0003_Validate_Connection_by_sending_Invalid_Time() throws Exception
	{
		printTestDetails("Starting", "Test07_CLI0003_Validate_Connection_by_sending_Invalid_Time", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.Sharedconnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout(); 

		try
		{
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("app", "C:\\Program Files (x86)\\BlackBox\\EmeraldRAEncrypt\\EmeraldRAEncrypt.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			SoftAssert soft = new SoftAssert();
			try
			{
				Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);
				System.out.println("EmeraldRA Encrypt Launched SuccessFully");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Exception has occured in Encrypt setup part");
			}

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.findElementByName("Configuration").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByAccessibilityId("txtRemoteAppPath").sendKeys("C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByAccessibilityId("txtBoxillaIp").sendKeys(boxillaManager);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			String con = connectionName.toString().replace("[","").replace("]", "");
			Windriver.findElementByAccessibilityId("txtConnectionName").sendKeys(con);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByAccessibilityId("cbxCustomTimestamp").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);	

			//Getting Active Time from API
			RestAssured.useRelaxedHTTPSValidation();
			String gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
					.when().get("https://"+boxillaManager+"/bxa-api/time")
					.then().assertThat().statusCode(200)
					.extract().response().asString();
			System.out.println("Active connection status"+gerdetails);

			JsonPath js1 = new JsonPath(gerdetails);
			long Bxatime = js1.getLong("message.local_time");
			System.out.println("Local Time in Epoch Language is : " + Bxatime);

			long b = Bxatime*1000;
			Date date = new Date(b);
			DateFormat format = new SimpleDateFormat("HH.mm");
			format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
			String formatted = format.format(date);
			System.out.println(formatted);

			Float f = Float.parseFloat(formatted);
			String[] arr=String.valueOf(f).split("\\.");
			int[] intArr=new int[2];
			intArr[0]=Integer.parseInt(arr[0]); 
			intArr[1]=Integer.parseInt(arr[1]); 

			String[] strArray = new String[intArr.length];
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			for (int i = 0; i < intArr.length; i++) {
				strArray[i] = String.valueOf(intArr[i]+1);
			}

			System.out.println(Arrays.toString(strArray));


			//Entering the time to RAEncrypt App
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement temp = Windriver.findElementByAccessibilityId("dtpTime");
			temp.click();
			Actions a = new Actions(Windriver);
			a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			temp.sendKeys(strArray[0]);
			a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			temp.sendKeys(strArray[1]);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			Windriver.findElementByName("Main").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByName("Set public key").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			Windriver.findElementByAccessibilityId("usernameTxt").sendKeys(RAusername);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByAccessibilityId("passwordTxt").sendKeys(RApassword);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\Automation\\keys\\"+"publicKey"+".key");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			a.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			Windriver.findElementByName("Launch connection").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
			System.out.println("Clicked on Launch Connection Button ");
			Thread.sleep(20000);

			RestAssured.useRelaxedHTTPSValidation();
			String  details = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
					.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
					.then().assertThat().statusCode(200)
					.extract().response().asString();

			JsonPath js = new JsonPath(details);
			int	countconnection=js.getInt("message.active_connections.size()");
			System.out.println("Number of Active connections are "+countconnection);
			soft.assertNotEquals(countconnection, connectionName.size(),"Connections Launched which shouldn't be launched with Invalid Boxilla Time");

			Thread.sleep(10000);
			Windriver.findElementByName("Close").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("Successfully Closed Emerald RA Application");

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured, Conection not launched and terminated Successfuly ");
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority = 8)//Invalid Time Stamp Expiry value.
	public void Test08_Check_with_Invalid_TimeStamp_Expiry_in_Boxilla() throws Exception
	{
		printTestDetails("Starting", "Test08_Check_with_Invalid_TimeStamp_Expiry_in_Boxilla", "");
		SoftAssert soft = new SoftAssert();

		try
		{
			cleanUpLogin();

			firedrive.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			LandingPage.systemTab(firedrive).click();
			System.out.println("System Dropdown clicked");
			firedrive.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			LandingPage.systemSettings(firedrive).click();
			firedrive.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			climethods.RemoteAppbtn(firedrive).click();
			firedrive.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			climethods.Timestamp(firedrive).clear();
			firedrive.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			climethods.Timestamp(firedrive).sendKeys("15");
			firedrive.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			climethods.Applybtn(firedrive).click();
			firedrive.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			String expt = "Invalid timestamp expiry value. Valid range: 1-10.";
			WebElement ele = firedrive.findElement(By.xpath("//div[@class='toast-message']"));
			String actual = ele.getText();
			System.out.println("Error Message is : "+ actual);
			soft.assertEquals(actual, expt, "Expected Error Message is not Displayed");
			Thread.sleep(15000);
			cleanUpLogout();
			soft.assertAll();
		}catch(Exception e )
		{
			e.printStackTrace();
			System.out.println("Exception has occured ");
			cleanUpLogout();
			Assert.fail();
		}
	}


	@Test(priority=9)   //Validating the Autologin feature for Second User 
	public void Test09_AutoLogin_Should_Support_for_Second_User() throws Exception
	{
		printTestDetails("Starting", "Test09_AutoLogin_Should_Support_for_Second_User", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ArrayList Firstusercon = ConnectionPage.CreateConnection(firedrive, Onedevices, 2, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");

		ArrayList Secondusercon = ConnectionPage.CreateViewOnlyConnection(firedrive, Onedevices, 3, "Shared");
		userpage.createUser(firedrive, Onedevices, AutomationUsername, AutomationPassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			int connum = 0;
			connectionList.clear();
			WebElement allConnectionList1 = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnection1 = allConnectionList1.findElements(By.xpath("//ListItem"));
			for(WebElement connectionName : availableConnection1)
			{
				connum++;
				connectionList.add(connectionName.getText());
				System.out.println("Connection Number " + connum + " is " + connectionName.getText());
			}

			System.out.println("Total Number of connections are - " +connum);
			soft.assertEquals(Firstusercon.size(), connum, "With Proper User not logged into RemoteApp ");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);

			//Enabling Autologin
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, AutomationUsername, AutomationPassword);
			Thread.sleep(5000);
			closeApp();
			Thread.sleep(15000);

			//Relaunching Remote App
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("ms:waitForAppLaunch", 50);
			capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
			capabilities.setCapability("platformName", "Windows");
			capabilities.setCapability("deviceName", "WindowsPC");
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(10000);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			new WebDriverWait(Windriver, 20).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("exitMenuItems")));
			int con = 0;
			connectionList.clear();
			WebElement allConnectionList2 = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnection2 = allConnectionList2.findElements(By.xpath("//ListItem"));
			for(WebElement connectionName : availableConnection2)
			{
				con++;
				connectionList.add(connectionName.getText());
				System.out.println("Connection Number " + con + " is " + connectionName.getText());

			}
			int totalcon = Firstusercon.size() + Secondusercon.size();
			System.out.println("Total Number of connections are - " +totalcon);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			soft.assertEquals(totalcon , con);			
			System.out.println("AutoLogin for Second User is Working as Expected");
			Thread.sleep(5000);
			autologin.AutoLoginDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			UserPage.DeleteUser(firedrive, AutomationUsername);
			cleanUpLogout();
			soft.assertAll();

		}catch(Exception e)
		{
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			System.out.println("Exception has occured");
			autologin.AutoLoginDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			UserPage.DeleteUser(firedrive, AutomationUsername);
			cleanUpLogout();
			Assert.fail();
		}

	}


	@Test(priority = 10)//Performance testing to see fps, df, and mbps on=- PE Single head 
	public void Test10_SE_SH_Performance_Check_FPS_DF_Mbps() throws Exception
	{
		printTestDetails("Starting", "Test10_SE_SH_Performance_Check_FPS_DF_Mbps", "");
		SoftAssert softAssert = new SoftAssert();
		Onedevices = devicePool.getAllDevices("deviceSE.properties");

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);

			ramethods.teratermactions(Windriver, "10.231.128.101", deviceUserName, devicePassword); 

			//Getting FPS deatils through API
			float fpssum1 =0 ; float dfpssum1 = 0; 
			for(int i=1;i<=10;i++)
			{
				RestAssured.useRelaxedHTTPSValidation();
				String gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
						.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
						.then().assertThat().statusCode(200)
						.extract().response().asString();
				System.out.println("Active connection status"+gerdetails);

				System.out.println("********************checking the connection status  ******************");
				JsonPath js1 = new JsonPath(gerdetails);
				int	RAcountconnection=js1.getInt("message.active_connections.size()");


				float ActiveRAconnectionFPS = js1.get("message.active_connections[0].fps");
				float droppedFPS = js1.get("message.active_connections[0].dropped_fps");

				log.info("Number of Active connections are "+RAcountconnection);
				log.info("Number of expected active connections to be "+connectionList.size());
				fpssum1 = fpssum1 + ActiveRAconnectionFPS;
				dfpssum1 = dfpssum1 + droppedFPS;
				Thread.sleep(3000);		 
			}

			float fps = fpssum1/10;
			float droppedfps = dfpssum1/10;

			System.out.println("The FPS of launched connection is : " + fps);
			System.out.println("The Dropped FPS of launched connection is : " + droppedfps);
			//

			RA_Methods.textFromSFTP(deviceUserName, devicePassword, "10.231.128.101", "/usr/local/output.txt");

			File file = new File("\\Users\\blackbox\\sftpcopy.txt");
			Scanner scan = new Scanner(file);
			int numberofLines=0;

			String filecontent = "";
			while (scan.hasNext()) {
				numberofLines=numberofLines+1;
				filecontent = filecontent.concat(scan.next()+"\n");

			}
			System.out.println("Total number of line is "+numberofLines);
			FileWriter writer = new FileWriter("C:\\Users\\blackbox\\Final.txt");
			writer.write(filecontent);
			writer.close();
			FileReader fr=new FileReader("C:\\Users\\blackbox\\Final.txt");
			BufferedReader br = new BufferedReader(fr);

			String filesplit;
			String unique_fps;
			String unique_df;
			String unique_mbps;
			int count_fps=0;
			int count_DF=0;
			int count_mbps=0;
			String line;
			boolean fpsstatus;
			boolean DTstatus;
			boolean mbpsstatus;
			ArrayList<Float> FPSList = new ArrayList<Float>();
			ArrayList<Float> DFList = new ArrayList<Float>();
			ArrayList<Float> mbpsList = new  ArrayList<Float>();

			String[] fpsvalues=new String[numberofLines];
			String[] DFvalues = new String[numberofLines];
			String[] mbpsvalues = new String[numberofLines];

			while((line=br.readLine())!=null) {
				// For FPS
				if(line.contains("fps"))
				{
					unique_fps=line.split("=")[1].trim();

					fpsvalues[count_fps]=unique_fps;
					count_fps++;
					System.out.println("FPS value for "+count_fps+" is "+unique_fps);

				}

				//for DF

				else if(line.contains("DF"))
				{
					unique_df=line.split("=")[1].trim();

					DFvalues[count_DF]=unique_df;
					count_DF++;
					System.out.println("DF value for "+count_DF+" is "+unique_df);

				}
				else if(line.contains("Mbps"))
				{
					unique_mbps=line.split("=")[1].trim();
					mbpsvalues[count_mbps] = unique_mbps;
					count_mbps++;
					System.out.println("Mbps value for " +count_mbps + " is " + unique_mbps);
				}
			}

			//Averaging FPS
			List<String> fpslist = new ArrayList<String>();

			for(String i : fpsvalues)
			{
				if(i != null) 
				{
					fpslist.add(i);
				}
			}
			// String list to Array list conversion
			List<Float> fpslistOfInteger = Lists.transform(fpslist, Float::parseFloat);
			System.out.println(fpslistOfInteger);

			double fpssum = 0;
			double fpsavg = 0;
			for(int i =0 ; i< fpslistOfInteger.size(); i++)
			{
				fpssum = fpssum + fpslistOfInteger.get(i);

			}
			fpsavg = fpssum/fpslistOfInteger.size();

			System.out.println("Average FPS is :" + fpsavg);


			//Averaging DF
			List<String> dflist = new ArrayList<String>();

			for(String i : DFvalues)
			{
				if(i != null) 
				{
					dflist.add(i);
				}
			}
			// String list to Array list conversion
			List<Float> dflistOfInteger = Lists.transform(dflist, Float::parseFloat);
			System.out.println(dflistOfInteger);

			double dfsum = 0;
			double dfavg = 0;
			for(int i =0 ; i< dflistOfInteger.size(); i++)
			{
				dfsum = dfsum + dflistOfInteger.get(i);

			}
			dfavg = dfsum/dflistOfInteger.size();

			System.out.println("Average DF is :" + dfavg);

			//Averaging MBPS
			List<String> mbpslist = new ArrayList<String>();

			for(String i : mbpsvalues)
			{
				if(i != null) 
				{
					mbpslist.add(i);
				}
			}
			// String list to Array list conversion
			List<Float> mbpslistOfInteger = Lists.transform(mbpslist, Float::parseFloat);
			System.out.println(mbpslistOfInteger);

			double mbpssum = 0;
			double mbpsavg = 0;
			for(int i =0 ; i< mbpslistOfInteger.size(); i++)
			{
				mbpssum = mbpssum + mbpslistOfInteger.get(i);

			}
			mbpsavg = mbpssum/mbpslistOfInteger.size();

			System.out.println("Average mbps is :" + mbpsavg);

			ramethods.RADisconnectConnection(Windriver);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception is occured");
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}

	}

	@Test(priority =11) //Automatically Logging into Remote App and Auto Connect with VM Connection
	public void Test11_AC0002_AutoLogin_and_AutoConnect_ShouldSupport_VM_Connection() throws Exception
	{
		printTestDetails("Starting", "Test11_AC0002_AutoLogin_and_AutoConnect_ShouldSupport_VM_Connection", "");
		ArrayList<String> VMname= new ArrayList<String>();
		VMname.add(VMIp);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		UserPage.Sharedconnectionassign(firedrive, RAusername, VMname);
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoLogin and AutoConnect are : " + connection);

			//Enabling AutoConnect and AutoLogin
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, RAusername, RApassword);
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Again Launching RA 
			setup1();

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(50000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoLogin and AutoConnect are : " + connection1);
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
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Windriver.quit();
			Assert.fail();
		}
	}


	@Test(priority=12)//Automatically Logging into Remote App and Auto Connect with AD user
	public void Test12_Autologin_and_AutoConnect_should_support_with_ADUser() throws Exception
	{
		printTestDetails("Starting", "Test12_Autologin_and_AutoConnect_should_support_with_ADUser", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		createAndValidateADUser();

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		UserPage.Sharedconnectionassign(firedrive, adUser, connectionName);
		cleanUpLogout();

		try
		{
			RAlogin(adUser, adPassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(70000);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoLogin and AutoConnect are : " + connection);

			//Enabling AutoConnect and AutoLogin
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, adUser, adPassword );
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(30000);

			//Again Launching RA 
			setup1();

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(60000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after Relaunching RA  are : " + connection1);
			soft.assertEquals(connection, connection1, "Connections are not Launched after enabling AutoLogin and AutoConnect, are not working ");
			Thread.sleep(3000);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
			autologin.AutoLoginDisable(Windriver);
			autologin.AutoConnectDisable(Windriver);
			closeApp();

			cleanUpLogin();
			SystemMethods.deleteOU(firedrive, adUser);
			UserPage.DeleteUser(firedrive, adUser);
			cleanUpLogout();	
			soft.assertAll();

		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			SystemMethods.deleteOU(firedrive, adUser);
			UserPage.DeleteUser(firedrive, adUser);
			cleanUpLogout();
			Windriver.quit();
			Assert.fail();
		}
	}

	@Test(priority=13)//Auto connect should  work after the relaunch for TX Connection .
	public void Test13_AutoConnect_should_support_for_TX_Connection() throws Exception
	{
		printTestDetails("Starting", "Test13_AutoConnect_should_support_for_TX_Connection", " ");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			int a[] = AutologinAutoConnect.TXConnectionSizeandLocationinfo(Windriver2);
			System.out.println(Arrays.toString(a) +" is the size and position of connection before enabling Auto Connect");
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoConnect are : " + connection);

			//Enabling AutoConnect
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			//Relaunching RA
			RAloginforAutologin(RAusername, RApassword);
			Thread.sleep(50000);
			int b[] = AutologinAutoConnect.TXConnectionSizeandLocationinfo(Windriver2);
			System.out.println(Arrays.toString(b) +" is the size and position of connection After Relaunching RA");
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after Relaunching RA are : " + connection1);
			soft.assertEquals(connection, connection1, "Connections are not Launched after enabling AutoLogin and AutoConnect, are not working ");
			//soft.assertEquals(Arrays.toString(a), Arrays.toString(b), "Size and position of Connections didn't matched in Auto Connect Feature");
			Thread.sleep(3000);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Windriver.quit();
			Assert.fail();
		}		
	}

	@Test(priority=14)//Auto connect should  work after the relaunch for VM Connection .
	public void Test14_AutoConnect_should_support_for_VM_Connection() throws Exception
	{
		printTestDetails("Starting", "Test14_AutoConnect_should_support_for_VM_Connection", " ");
		SoftAssert soft = new SoftAssert();
		ArrayList<String> VMname= new ArrayList<String>();
		VMname.add(VMIp);

		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		UserPage.Sharedconnectionassign(firedrive, RAusername, VMname);
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			int a[] = AutologinAutoConnect.VMConnectionSizeandLocationinfo(Windriver2);
			System.out.println(Arrays.toString(a) +" is the size and position of connection before enabling Auto Connect");
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoConnect are : " + connection);

			//Enabling AutoConnect
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(20000);

			//Relaunching RA
			RAloginforAutologin(RAusername, RApassword);
			Thread.sleep(50000);
			int b[] = AutologinAutoConnect.VMConnectionSizeandLocationinfo(Windriver2);
			System.out.println(Arrays.toString(b) +" is the size and position of connection After Relaunching RA");
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after Relaunching RA are : " + connection1);
			soft.assertEquals(connection, connection1, "Connections are not Launched after enabling AutoLogin and AutoConnect, are not working ");
			//soft.assertEquals(Arrays.toString(a), Arrays.toString(b), "Size and position of Connections didn't matched in Auto Connect Feature");
			Thread.sleep(3000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}	
	}

	@Test(priority=15) //Auto Connect for Remote App-offline device
	public void Test15_AutoConnect_shouldnot_support_for_Offline_TX() throws Exception
	{
		printTestDetails("Starting", "Test15_AutoConnect_shouldnot_support_for_Offline_TX", " ");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			//Rebooting device to make Device offline
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			/*for (Device deviceList : Onedevices)
			{
				ramethods.rebootspecificdevice(boxillaManager, AutomationUsername, AutomationPassword, deviceList.getIpAddress());
				break;
			}*/
			//ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			//int a[] = AutologinAutoConnect.TXConnectionSizeandLocationinfo(Windriver2);
			//System.out.println(Arrays.toString(a) +" is the size and position of connection before enabling Auto Connect");
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoConnect are : " + connection);

			//Enabling AutoConnect
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			//Thread.sleep(20000);

			///Relaunching RA
			RAloginforAutologin(RAusername, RApassword);
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			//Rebooting device to make Device offline
			Thread.sleep(50000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after Relaunching RA are : " + connection1);
			soft.assertEquals(connection, connection1, "Connections are Launched after enabling AutoLogin and AutoConnect, are not working ");
			Thread.sleep(3000);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}		
	}

	@Test(priority=16) //Connection windows size and position-different resolution
	public void Test16_Tx_Connection_Window_size_and_position_check_after_relaunching() throws Exception
	{
		printTestDetails("Starting", "Test16_Tx_Connection_Window_size_and_position_check_after_relaunching", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();
		int size[] = {width_con, height_con};
		int position[] = {x_coordiate, y_coordiate};

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			//Changing Size and position of Connection.
			autologin.changeTXConnectionSize(Windriver2, width_con, height_con);
			autologin.changeTXConnectionPosition(Windriver2, x_coordiate, y_coordiate);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections are : " + connection);
			int sizebefore[] = AutologinAutoConnect.currentTXSize(Windriver2);
			int positionbefore [] = AutologinAutoConnect.currentTXPosition(Windriver2);
			System.out.println(Arrays.toString(sizebefore)+ Arrays.toString(positionbefore) +" is the size and position of TX connection before Relaunching RA");
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			///Relaunching RA
			RAlogin(RAusername, RApassword); 
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after relaunching RA are : " + connection1);
			int sizeafter[] = AutologinAutoConnect.currentTXSize(Windriver2);
			int positionafter[] = AutologinAutoConnect.currentTXPosition(Windriver2);
			System.out.println(Arrays.toString(sizeafter)+ Arrays.toString(positionafter) +" is the size and position of TX connection After Relaunching RA");

			//validating size of the Connection
			for(int i=0; i<size.length; i++)
			{
				int[] result = new int[size.length];
				result [i] = sizeafter[i] - sizebefore[i];
				if(result[i] >= size[i]/10)
				{
					Assert.fail("The Size of the Connection didn't match");
				}
			}

			//validating position of the Connection
			for(int i=0; i<position.length; i++)
			{
				int[] result = new int[position.length];
				result [i] = positionafter[i] - positionbefore[i];
				if(result[i] >= position[i]/10)
				{
					Assert.fail("The Position of the Connection didn't match");
				}
			}
			System.out.println("Both size and Position of TX Connection is Working as expected after Relaunching the RA");
			soft.assertEquals(connection, connection1, "Connections are not Launched as expected ");
			Thread.sleep(3000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority=17) //Connection windows size and position-different resolution
	public void Test17_VM_Connection_Window_size_and_position_check_after_relaunching() throws Exception
	{
		printTestDetails("Starting", "Test17_VM_Connection_Window_size_and_position_check_after_relaunching", "");
		ArrayList<String> VMname= new ArrayList<String>();
		VMname.add(VMIp);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		UserPage.Sharedconnectionassign(firedrive, RAusername, VMname);
		cleanUpLogout();
		int size[] = {width_con, height_con}; int position[] = {x_coordiate, y_coordiate};

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			//Changing Size and position of Connection.
			autologin.changeVMConnectionSize(Windriver2, width_con, height_con);
			autologin.changeVMConnectionPosition(Windriver2, x_coordiate, y_coordiate);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections are : " + connection);
			int sizebefore[] = AutologinAutoConnect.currentVMSize(Windriver2);
			int positionbefore [] = AutologinAutoConnect.currentVMPosition(Windriver2);
			System.out.println(Arrays.toString(sizebefore)+ Arrays.toString(positionbefore) +" is the size and position of VM connection before Relaunching RA");
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			///Relaunching RA
			RAlogin(RAusername, RApassword); 
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after relaunching RA are : " + connection1);
			int sizeafter[] = AutologinAutoConnect.currentVMSize(Windriver2);
			int positionafter[] = AutologinAutoConnect.currentVMPosition(Windriver2);
			System.out.println(Arrays.toString(sizeafter)+ Arrays.toString(positionafter) +" is the size and position of VM connection After Relaunching RA");

			for(int i=0; i<size.length; i++)
			{
				int[] result = new int[size.length];
				result [i] = sizeafter[i] - sizebefore[i];
				if(result[i] >= size[i]/10)
				{
					Assert.fail("The Size of the Connection didn't match");
				}
			}
			for(int i=0; i<position.length; i++)
			{
				int[] result = new int[position.length];
				result [i] = positionafter[i] - positionbefore[i];
				if(result[i] >= position[i]/10)
				{
					Assert.fail("The Position of the Connection didn't match");
				}
			}
			System.out.println("Both size and Position of VM Connection is Working as expected after Relaunching the RA");
			soft.assertEquals(connection, connection1, "Connections are not Launched as expected ");
			Thread.sleep(3000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority=18) //Connection windows size and position-different resolution
	public void Test18_Connection_Window_size_and_position_with_AutoConnect_check_after_relaunching() throws Exception
	{
		printTestDetails("Starting", "Test18_Connection_Window_size_and_position_with_AutoConnect_check_after_relaunching", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();
		int size[] = {width_con, height_con};
		int position[] = {x_coordiate, y_coordiate};

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			//Changing Size and position of Connection.
			autologin.changeTXConnectionSize(Windriver2, width_con, height_con);
			autologin.changeTXConnectionPosition(Windriver2, x_coordiate, y_coordiate);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections are : " + connection);
			int sizebefore[] = AutologinAutoConnect.currentTXSize(Windriver2);
			int positionbefore [] = AutologinAutoConnect.currentTXPosition(Windriver2);
			System.out.println(Arrays.toString(sizebefore)+ Arrays.toString(positionbefore) +" is the size and position of TX connection before Relaunching RA");
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(10000);

			///Relaunching RA
			RAloginforAutologin(RAusername, RApassword); 
			//ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after relaunching RA are : " + connection1);
			int sizeafter[] = AutologinAutoConnect.currentTXSize(Windriver2);
			int positionafter[] = AutologinAutoConnect.currentTXPosition(Windriver2);
			System.out.println(Arrays.toString(sizeafter)+ Arrays.toString(positionafter) +" is the size and position of TX connection After Relaunching RA");

			//validating size of the Connection
			for(int i=0; i<size.length; i++)
			{
				int[] result = new int[size.length];
				result [i] = sizeafter[i] - sizebefore[i];
				if(result[i] >= size[i]/10)
				{Assert.fail("The Size of the Connection didn't match");
				}}

			//validating position of the Connection
			for(int i=0; i<position.length; i++)
			{
				int[] result = new int[position.length];
				result [i] = positionafter[i] - positionbefore[i];
				if(result[i] >= position[i]/10)
				{Assert.fail("The Position of the Connection didn't match");
				}}
			soft.assertEquals(connection, connection1, "Connections are not Launched as expected ");
			Thread.sleep(3000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority=19) //Connection windows size and position-different resolution
	public void Test19_Connection_Window_size_and_position_with_AutoConnect_and_Autologin_check_after_relaunching() throws Exception
	{
		printTestDetails("Starting", "Test19_Connection_Window_size_and_position_with_AutoConnect_and_Autologin_check_after_relaunching", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();
		int size[] = {width_con, height_con};
		int position[] = {x_coordiate, y_coordiate};

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			//Changing Size and position of Connection.
			autologin.changeTXConnectionSize(Windriver2, width_con, height_con);
			autologin.changeTXConnectionPosition(Windriver2, x_coordiate, y_coordiate);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections are : " + connection);
			int sizebefore[] = AutologinAutoConnect.currentTXSize(Windriver2);
			int positionbefore [] = AutologinAutoConnect.currentTXPosition(Windriver2);
			System.out.println(Arrays.toString(sizebefore)+ Arrays.toString(positionbefore) +" is the size and position of TX connection before Relaunching RA");
			Thread.sleep(3000);
			autologin.AutologinEnable(Windriver);
			autologin.relogin(Windriver, RAusername, RApassword);
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);
			closeApp();
			Thread.sleep(30000);

			//Again Launching RA 
			setup1();
			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			Thread.sleep(50000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after relaunching RA are : " + connection1);
			int sizeafter[] = AutologinAutoConnect.currentTXSize(Windriver2);
			int positionafter[] = AutologinAutoConnect.currentTXPosition(Windriver2);
			System.out.println(Arrays.toString(sizeafter)+ Arrays.toString(positionafter) +" is the size and position of TX connection After Relaunching RA");

			//validating size of the Connection
			for(int i=0; i<size.length; i++)
			{
				int[] result = new int[size.length];
				result [i] = sizeafter[i] - sizebefore[i];
				if(result[i] >= size[i]/10)
				{Assert.fail("The Size of the Connection didn't match");
				}}

			//validating position of the Connection
			for(int i=0; i<position.length; i++)
			{
				int[] result = new int[position.length];
				result [i] = positionafter[i] - positionbefore[i];
				if(result[i] >= position[i]/10)
				{Assert.fail("The Position of the Connection didn't match");
				}}
			soft.assertEquals(connection, connection1, "Connections are not Launched as expected ");
			Thread.sleep(3000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoLoginDisable(Windriver);
			autologin.AutoConnectDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoLoginDisable(Windriver);
			autologin.AutoConnectDisable(Windriver);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority=20)//Max numbers of connections through Auto Connect.
	public void Test20_Validating_AutoConnect_for_9Connections() throws Exception
	{
		printTestDetails("Starting", "Test20_Validating_AutoConnect_for_9Connections", " ");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ArrayList con = ConnectionPage.CreateConnection(firedrive, Onedevices, 9, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);
			int connection = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections before enabling AutoConnect are : " + connection);

			//Enabling AutoConnect
			Thread.sleep(3000);
			autologin.AutoConnectEnable(Windriver);
			autologin.saveCurrentLayout(Windriver);
			Thread.sleep(3000);

			/*for(int i=1;i<con.size(); i++)
			{
				WebElement popUpWindow1 =Windriver.findElementByAccessibilityId("TitleBar");
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
				Thread.sleep(10000);
				System.out.println("Working with Connection  "+ popUpWindow1.getText());
				WindowsDriver session = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
				WebElement minimize = session.findElementByName("Minimise");
				Thread.sleep(2000);
				minimize.click();
				System.out.println("Minimized connection - " + popUpWindow1.getText());
				int a[] = AutologinAutoConnect.TXConnectionSizeandLocationinfo(Windriver2);
				System.out.println(Arrays.toString(a) +" is the size and position of connection before enabling Auto Connect");
			}*/
			Thread.sleep(10000);
			closeApp();
			Thread.sleep(10000);

			//Relaunching RA
			RAloginforAutologin(RAusername, RApassword);
			Thread.sleep(50000);
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active Connections after Relaunching RA are : " + connection1);
			/*for(int i=1;i<con.size(); i++)
			{
				WebElement popUpWindow1 =Windriver.findElementByAccessibilityId("TitleBar");
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
				Thread.sleep(10000);
				System.out.println("Working with Connection  "+ popUpWindow1.getText());
				WindowsDriver session = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
				WebElement minimize = session.findElementByName("Minimise");
				Thread.sleep(2000);
				minimize.click();
				System.out.println("Minimized connection - " + popUpWindow1.getText());
				int b[] = AutologinAutoConnect.TXConnectionSizeandLocationinfo(Windriver2);
				System.out.println(Arrays.toString(b) +" is the size and position of connection After Relaunching RA");
			}*/
			soft.assertEquals(connection, connection1, "Connections are not Launched after enabling AutoLogin and AutoConnect, are not working ");
			//soft.assertEquals(Arrays.toString(a), Arrays.toString(b), "Size and position of Connections didn't matched in Auto Connect Feature");
			Thread.sleep(3000);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has Occured");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoConnectDisable(Windriver);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}		
	}




}