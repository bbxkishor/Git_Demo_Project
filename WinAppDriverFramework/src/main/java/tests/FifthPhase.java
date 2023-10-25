package tests;


import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.ejb.RemoveException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.log4testng.Logger;

import com.google.common.collect.Lists;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import groovyjarjarantlr.collections.Stack;
import tests.Ssh;
import io.appium.java_client.windows.WindowsDriver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import methods.Device;
import methods.Devices;
import methods.RA_Methods;
import methods.SeleniumActions;
import methods.SystemMethods;
import pages.BoxillaHeaders;
import pages.ConnectionPage;
import pages.LandingPage;
import pages.UserPage;
import pages.ClusterPage;

public class FifthPhase extends TestBase {

	final static Logger log = Logger.getLogger(FourthPhase.class);


	@Test(priority=1) //Shared Connection launch and Terminate by Using CLI
	public void Test01_CLI0001_and_CLI0002_Tx_SharedConnection_launch_and_Terminate_by_CLI() throws Exception {

		printTestDetails("Starting", "Test01_CLI0001_and_CLI0002_Tx_SharedConnection_launch_and_Terminate_by_CLI", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();


		//ramethods.CMDkeysGen(Windriver, Windriver2);
		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionName, AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, true );


			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			
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



	@Test(priority=2) //Private Connection launch and Terminate by Using CLI
	public void Test02_CLI0001_and_CLI0002_Tx_PrivateConnection_launch_and_Terminate_by_CLI() throws Exception{

		printTestDetails("Starting", "Test02_CLI0001_and_CLI0002_Tx_PrivateConnection_launch_and_Terminate_by_CLI", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);


		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();

		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionName, AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, true);

			/*ramethods.EncryptRAlaunch(Windriver);
			Thread.sleep(60000);
			ramethods.EncryptRAterminate(Windriver);
			Thread.sleep(10000);
			ramethods.EncryptRAClose(Windriver);*/

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
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


	@Test(priority=3) //VM Connection launch and Terminate by Using CLI
	public void Test03_CLI0001_and_CLI0002_VM_Connection_launch_and_Terminate_by_CLI() throws Exception{

		printTestDetails("Starting", "Test03_CLI0001_and_CLI0002_VM_Connection_launch_and_Terminate_by_CLI", "");
		ArrayList<String> RAVM = new ArrayList<String>();
		RAVM.add(VMIp);

		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive, null, RAusername, RApassword, "General");
		userpage.Sharedconnectionassign(firedrive, RAusername, RAVM);
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();

		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, RAVM, AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, true);

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured, Conection not launched and terminated Successfuly ");
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority=4) //View only Connection launch and Terminate by Using CLI
	public void Test04_CLI0001_and_CLI0002_Viewonly_Tx_Connection_launch_and_Terminate_by_CLI() throws Exception{

		printTestDetails("Starting", "Test04_CLI0001_and_CLI0002_Viewonly_Tx_Connection_launch_and_Terminate_by_CLI", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateViewOnlyConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();

		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionName, AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, true);

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured, Conection not launched and terminated Successfuly ");
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}


	@Test(priority=5) //Unable to contact the Enterprise Manager.
	public void Test05_CLI0003_Validate_Connection_with_invalid_Boxilla_IP() throws Exception {

		printTestDetails("Starting", "Test05_CLI0003_Validate_Connection_with_invalid_Boxilla_IP", "");
		Onedevices = devicePool.getAllDevices("devicePE_SH.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		//ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();
		
		DoubleLogin();
		enableNorthboundAPI(firedrive);
		firedrive.quit();

		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager2, connectionName , AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, false);


			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			
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



	@Test(priority = 6) //Unable to connect with invalid Username.
	public void Test06_CLI0003_Validate_Connection_with_invalid_username() throws Exception
	{
		printTestDetails("Starting", "Test06_CLI0003_Validate_Connection_with_invalid_username", "");
		Onedevices = devicePool.getAllDevices("devicePE_SH.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();

		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionName , AutomationUsername, AutomationPassword,
					"publicKey", AutomationUsername, RApassword, false);


			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
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


	@Test(priority = 7) //Unable to connect with invalid Password.
	public void Test07_CLI0003_Validate_Connection_with_invalid_password() throws Exception
	{
		printTestDetails("Starting", "Test07_CLI0003_Validate_Connection_with_invalid_password", "");
		Onedevices = devicePool.getAllDevices("devicePE_SH.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();

		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionName , AutomationUsername, AutomationPassword,
					"publicKey", RAusername, AutomationPassword, false);

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured, Conection not launched and terminated Successfuly ");
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}

	}

	@Test(priority = 8) //Unable to connect with Not Assigned Connection for Boxilla.
	public void Test08_CLI0003_Validate_Connection_with_Not_Assigned_Connection() throws Exception
	{
		printTestDetails("Starting", "Test08_CLI0003_Validate_Connection_with_Not_Assigned_Connection", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		connectionList.clear();
		connectionList.add("pe");

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout();

		try
		{

			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionList, AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, false);

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured, Conection not launched and terminated Successfuly ");
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		
		}

	}


	@Test(priority = -9)//
	public void Test09_CLI0003_Validate_Connection_with_existing_Private_Connection() throws Exception
	{
		printTestDetails("Starting", "Test09_CLI0003_Validate_Connection_with_existing_Private_Connection", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.Sharedconnection(firedrive, Onedevices, 2, "Private");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout(); 
		//ramethods.CMDkeysGen(Windriver);

		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionName, AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, true);

			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured, Conection not launched and terminated Successfuly ");
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			Assert.fail();
		}


	}


	@Test(priority = 10)//
	public void Test10_CLI0003_Validate_Connection_by_sending_same_Command() throws Exception
	{
		printTestDetails("Starting", "Test10_CLI0003_Validate_Connection_by_sending_same_Command", "");
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
			Windriver.findElementByAccessibilityId("txtRemoteAppPath").sendKeys("C:\\Program Files (x86)\\BlackBox\\EmeraldRA.exe");
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
				strArray[i] = String.valueOf(intArr[i]);
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
			Windriver.findElementByName("Launch connection").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			RestAssured.useRelaxedHTTPSValidation();
			String  details = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
					.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
					.then().assertThat().statusCode(200)
					.extract().response().asString();

			JsonPath js = new JsonPath(details);
			int	countconnection=js.getInt("message.active_connections.size()");
			System.out.println("Number of Active connections are "+countconnection);
			
			Thread.sleep(3000);
			Windriver.findElementByName("Terminate connection").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);  
			a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
			System.out.println("Clicked on Terminate Connection Button");
			Thread.sleep(10000);
			Windriver.findElementByName("Close").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("Successfully Closed Emerald RA Application");

			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
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


	@Test(priority = 15)//
	public void Test11_CLI0001_Validate_10th_Connection_with_existing_9Connections() throws Exception
	{
		printTestDetails("Starting", "Test11_CLI0001_Validate_10th_Connection_with_existing_9Connections", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.Sharedconnection(firedrive, Onedevices, 11, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
		cleanUpLogout(); 
		//ramethods.CMDkeysGen(Windriver);


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
			Windriver.findElementByAccessibilityId("txtRemoteAppPath").sendKeys("C:\\Program Files (x86)\\BlackBox\\EmeraldRA.exe");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByAccessibilityId("txtBoxillaIp").sendKeys(boxillaManager);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			String con = connectionName.get(0).toString().replace("[","").replace("]", "");
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
				strArray[i] = String.valueOf(intArr[i]);
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
			
			for(int i=1; i<connectionName.size(); i++)
			{
				if(i%3==0)
				{
					cleanUpLogin();
					climethods.clikeyupload(firedrive, "privateKeyRsa", "10");
					cleanUpLogout(); 
				}
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				Windriver.findElementByName("Configuration").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
				Thread.sleep(5000);

				Windriver.findElementByAccessibilityId("txtConnectionName").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.CONTROL,"a")).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.BACK_SPACE)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


				//System.out.println("List of Available Connections :" +connectionname);
				String con1 = connectionName.get(i).toString();
				System.out.println("Connection entered is : " +con1);
				Windriver.findElementByAccessibilityId("txtConnectionName").sendKeys(con1);

				//Getting Active Time from API
				RestAssured.useRelaxedHTTPSValidation();
				String gerdetails1 = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
						.when().get("https://"+boxillaManager+"/bxa-api/time")
						.then().assertThat().statusCode(200)
						.extract().response().asString();

				JsonPath js2 = new JsonPath(gerdetails1);
				long Bxatime1 = js2.getLong("message.local_time");

				long c = Bxatime1*1000;

				Date date1 = new Date(c);
				DateFormat format1 = new SimpleDateFormat("HH.mm");
				format1.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
				String formatted1 = format1.format(date1);
				System.out.println("Actual Time of Boxilla is - " + formatted1);

				Float f1 = Float.parseFloat(formatted1);

				String[] arr1=String.valueOf(f1).split("\\.");
				int[] intArr1=new int[2];
				intArr1[0]=Integer.parseInt(arr1[0]); 
				intArr1[1]=Integer.parseInt(arr1[1]); 

				String[] strArray1 = new String[intArr1.length];
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				for (int j = 0; j < intArr1.length; j++) {
					strArray1[j] = String.valueOf(intArr1[j]);
				}
				System.out.println(Arrays.toString(strArray1));


				//Entering the time to RAEncrypt App
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				WebElement temp1 = Windriver.findElementByAccessibilityId("dtpTime");
				temp1.click();
				a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				temp.sendKeys(strArray1[0]);
				a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				temp.sendKeys(strArray1[1]);
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				Windriver.findElementByName("Main").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				Windriver.findElementByName("Launch connection").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
				System.out.println("Clicked on Launch Connection Button ");
				Thread.sleep(20000);
			}	

			RestAssured.useRelaxedHTTPSValidation();
			String  details = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
					.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
					.then().assertThat().statusCode(200)
					.extract().response().asString();

			JsonPath js = new JsonPath(details);
			int	countconnection=js.getInt("message.active_connections.size()");
			System.out.println("Number of Active connections are "+countconnection);
			//soft.assertNotEquals(countconnection, connectionName.size(),"the number of launched connections and active connections");
			//soft.assertEquals(countconnection, 9,"Not all Nine Connections are Launched");
			Thread.sleep(3000);
	
			for(int i=1; i<connectionName.size() ; i++)
			{
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				Windriver.findElementByName("Configuration").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				Windriver.findElementByAccessibilityId("txtConnectionName").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.CONTROL,"a")).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.BACK_SPACE)).build().perform();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				//System.out.println("List of Available Connections :" +connectionname);
				String con1 = connectionName.get(i).toString();
				System.out.println("Connection entered is : " +con1);
				Windriver.findElementByAccessibilityId("txtConnectionName").sendKeys(con1);

				Windriver.findElementByName("Main").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);				
				Windriver.findElementByName("Terminate connection").click();
				Windriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);  
				a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
				System.out.println("Clicked on Terminate Connection Button");
				Thread.sleep(15000);	
			}	
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByName("Close").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("Successfully Closed Emerald RA Application");

			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
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




		try
		{
			ramethods.EncryptRAsetup(Windriver, Windriver2, boxillaManager, connectionName, AutomationUsername, AutomationPassword,
					"publicKey", RAusername, RApassword, true);

			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured, Conection not launched and terminated Successfuly ");
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			Assert.fail();
		}
	}




	@Test(priority=12) //Reboot TX device during active connection in CLI.
	public void Test12_CLI0004_Reboot_Tx_during_Active_Connection() throws Exception {

		printTestDetails("Starting", "Test12_CLI0004_Reboot_Tx_during_Active_Connection", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
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
			Windriver.findElementByAccessibilityId("txtRemoteAppPath").sendKeys("C:\\Program Files (x86)\\BlackBox\\EmeraldRA.exe");
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
				strArray[i] = String.valueOf(intArr[i]);
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
			Thread.sleep(40000);

			//Rebooting Device From API
			RestAssured.useRelaxedHTTPSValidation();
			String response = given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers(BoxillaHeaders.getBoxillaHeaders())
					.when().contentType(ContentType.JSON)
					.post("https://" + boxillaManager + "/bxa-api/devices/kvm/reboot")
					.then().assertThat().statusCode(200)
					.extract().response().asString();
			System.out.println("Transmitter Reboot status " + response);
			Thread.sleep(130000);



			//Getting Active Connection From API 
			int	countconnection = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active connections are "+countconnection);
			soft.assertEquals(countconnection, 0,"Mismatch on the number of launched connections and active connections");

			Thread.sleep(5000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
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
			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(10000);
			Assert.fail();
		}
	}


	@Test(priority = 13)//The Emerald RemoteApp shall not store any User information locally on the host PC drives.
	public void Test13_RA_shouldnot_store_User_info() throws Exception
	{
		printTestDetails("Starting", "Test13_RA_shouldnot_store_User_info", "");
		Onedevices = devicePool.getAllDevices("deviceSE.properties");
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
			soft.assertEquals(Firstusercon.size(), connum);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();

			RAlogin(AutomationUsername, AutomationPassword);
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

			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			UserPage.DeleteUser(firedrive, AutomationUsername);
			cleanUpLogout();
			Assert.fail();
		}

	}



	@Test(priority = 14) //The Emerald RemoteApp does not support persistence connections
	public void Test14_RA_Doesnot_Support_Persistent_Mode() throws Exception
	{
		printTestDetails("Starting", "Test14_RA_Doesnot_Support_Persistent_Mode", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		SoftAssert soft = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreatePersistantEnabledConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(10000);

			//rebooting devices
			RestAssured.useRelaxedHTTPSValidation();
			String response = given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers(BoxillaHeaders.getBoxillaHeaders())
					.when().contentType(ContentType.JSON)
					.post("https://" + boxillaManager + "/bxa-api/devices/kvm/reboot")
					.then().assertThat().statusCode(200)
					.extract().response().asString();
			System.out.println("Transmitter Reboot status " + response);
			Thread.sleep(70000);

			//Getting connection status
			RestAssured.useRelaxedHTTPSValidation();
			String  gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
					.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
					.then().assertThat().statusCode(200)
					.extract().response().asString();

			System.out.println(gerdetails);
			JsonPath js = new JsonPath(gerdetails);
			int	countconnection=js.getInt("message.active_connections.size()");
			System.out.println("Number of Active connections are "+countconnection);
			System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());

			soft.assertEquals(countconnection, 0,"Connection Launched Even though Persistant Mode is not Supporrted");
			Thread.sleep(3000);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured");
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}

	}
}
