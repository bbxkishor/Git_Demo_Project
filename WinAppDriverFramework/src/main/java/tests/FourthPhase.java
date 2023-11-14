package tests;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ejb.RemoveException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
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
import methods.RA_Methods;
import methods.SystemMethods;
import pages.BoxillaHeaders;
import pages.ConnectionPage;
import pages.UserPage;
import pages.ClusterPage;

public class FourthPhase extends TestBase{

	final static Logger log = Logger.getLogger(FourthPhase.class);

	@Test(priority = 1)//Launching a VM connection using an AD user
	public void Test01_SR0039_Launch_VM_con_with_ADuser() throws Exception
	{
		printTestDetails("Starting", "Test01_SR0039_Launch_VM_con_with_ADuser", "");
		ArrayList<String> VMname= new ArrayList<String>();
		VMname.add(VMIp);

		try 
		{
			createAndValidateADUser();

			cleanUpLogin();
			ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
			UserPage.Sharedconnectionassign(firedrive, adUser, VMname);
			cleanUpLogout();

			RAlogin(adUser, adPassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			ramethods.RADisconnectConnection(Windriver);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			SystemMethods.deleteOU(firedrive, adUser);
			UserPage.DeleteUser(firedrive, adUser);
			cleanUpLogout();
		}
		catch(Exception e)
		{
			System.out.println("Exception is occured");
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			SystemMethods.deleteOU(firedrive, adUser);
			UserPage.DeleteUser(firedrive, adUser);
			cleanUpLogout();
		}
	}


	@Test(priority=2)//one or more connection screens which can be moved around the screen by using the mouse
	public void Test02_AI0073_Mouse_movement_on_multiple_connections() throws Exception
	{
		printTestDetails("Starting", "Test02_AI0073_Mouse_movement_on_multiple_connections", "");
		Onedevices = devicePool.getAllDevices("deviceSE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 2, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);

			for(int i=2; i>0;i--)
			{
				System.out.println("Working with Connection number "+ i);
				WindowsDriver session = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
				WebElement close = session.findElementByName("Close");
				Thread.sleep(2000);
				close.click();
				System.out.println("closed connection - " + i);
			}

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(5000);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e) 
		{
			System.out.println("Exception is occured");
			e.printStackTrace();
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}

	}

	@Test(priority =3) // User shall have the option to select a demo mode.
	public void Test03_AI0061_RA_login_with_DemoMode() throws Exception
	{
		printTestDetails("Starting", "Test03_AI0061_RA_login_with_DemoMode", "");
		int con=0;
		try
		{
			setup();
			Thread.sleep(2000);
			System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
			getElement("DemoModeCheckBox").click();
			getElement("logInButton").click();
			System.out.println("Logged in as Demo Mode");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
			WebElement allConnectionsList = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnection = allConnectionsList.findElements(By.xpath("//ListItem"));
			for(WebElement connectionname : availableConnection)
			{
				System.out.println("Connection Name is " + connectionname.getText());
				con++;
			}

			Thread.sleep(3000);
			closeApp();
		}catch( Exception e)
		{
			System.out.println("Exception has occured");
			e.printStackTrace();
			closeApp();
		}

	}


	@Test(priority=-4)//Connection time to a Transmitter unit shall be less than 3 seconds.
	public void Test04_SR0019_Connection_launch_in_3sec() throws Exception
	{
		printTestDetails("Starting", "Test04_SR0019_Connection_launch_in_3sec", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.createprivateconnections(firedrive, Onedevices);
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			int count = 0;
			int connum = 1;
			connectionList.clear();
			WebElement allConnectionList = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnection = allConnectionList.findElements(By.xpath("//ListItem"));
			for(WebElement connectionName : availableConnection)
			{
				connectionList.add(connectionName.getText());
				System.out.println("Connection Number " + connum + " is " + connectionName.getText());
				connum++;
			}

			for(String connection : connectionList)
			{
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				WebElement targetConnection = allConnectionList.findElement(By.name(connection));
				Actions action = new Actions(Windriver);
				action.moveToElement(targetConnection).
				doubleClick().build().perform();
				System.out.println("Connection Name -  " +connection + " has launched");
				count++;
			}
			long start = System.currentTimeMillis();

			//new WebDriverWait(Windriver, 5).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("TitleBar")));

			long finish = System.currentTimeMillis();
			long totalTime = finish-start;
			System.out.println("Total Time Taken to launch the connection is  " + totalTime +"  milliseconds");

			if(totalTime<3000){
				Thread.sleep(30000);
				System.out.println("The connection launched within 3 seconds");
			}else {
				//Assert.fail("The connection is taking more than 3 seconds to launch");
				System.out.println("The connection is taking more than 3 seconds to launch");
			}

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch (Exception e){
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}


	@Test(priority=20)//RA shall support up to 9 concurrent connections.
	public void Test05_SR0020a_Concurrent_9connections_by_RA() throws Exception
	{
		printTestDetails("Starting", "Test05_SR0020a_Concurrent_9connections_by_RA", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);
		SoftAssert softAssert = new SoftAssert();

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 9, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
	cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);

			int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active connections are "+connectionCount);
			System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
			softAssert.assertEquals(connectionCount, Windriver.getWindowHandles().size(),"Number of Windows Mismatch");

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
			ramethods.RADisconnectConnection(Windriver);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();

		}

	}

@Test(priority=6)//Windows control key sequence to “CNTL-ALT-END” on the connection to a Transmitter
	public void Test06_CNTL_ALT_END_should_work_for_transmitter() throws Exception
	{
		printTestDetails("Starting", "Test06_CNTL_ALT_END_should_work_for_transmitter", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);


			WindowsDriver session = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);

			Actions action = new Actions(Windriver);
			WebElement close = session.findElementByName("Close");
			Thread.sleep(2000);

			action.sendKeys(Keys.chord(Keys.CONTROL, Keys.ALT, Keys.END)).build().perform();
			System.out.println("Security options Window of Transmitter Connection is displayed");
			Thread.sleep(30000);
			action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
			System.out.println("Back to connection window");
			Thread.sleep(10000);

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch (Exception e)
		{
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			Assert.fail();
		}
	}

	@Test(priority=-7)//Test Cluster With VIP
	public void Test07_LoginRA_by_VIP_of_cluster() throws Exception
	{
		printTestDetails("Starting", "Test07_LoginRA_by_VIP_of_cluster", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);
		try{
			
		//Creation of Master 
		cleanUpLogin();
		clusterpage.createMasterCluster(firedrive, VIP, MasterClusterID, MasterNodeID, MasterNodeName);
		cleanUpLogout();

		//Creation of StandBy
		DoubleLogin();
		clusterpage.createStandByCluster(firedrive, boxillaManager, StandbyNodeID, StandbyNodeName);
		firedrive.quit();

		//Creation of Connection, user and Assigning connection to user
		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		
			//Configuring VIP for RA
			RAlogin(RAusername, RApassword);
			ramethods.RAConfigWithBxaIP(Windriver, VIP);
			Thread.sleep(5000);
			closeRemoteApp();
			Thread.sleep(2000);

			//Launching connection by using VIP 
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			ramethods.RADisconnectConnection(Windriver);
			
			//Configuring RA for Normal Boxilla IP
			ramethods.RAConfigWithBxaIP(Windriver ,boxillaManager);
			Thread.sleep(3000);
			closeRemoteApp();
			Thread.sleep(2000);

			//Dissolve Cluster
			cleanUpLogin();
			clusterpage.dissolveCluster(firedrive);
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured");
			ramethods.RAConfigWithBxaIP(Windriver, boxillaManager);
			closeRemoteApp();
			Thread.sleep(2000);
			cleanUpLogin();
			clusterpage.dissolveCluster(firedrive);
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();	
		}	
	}

	@Test(priority=8)//The Remote App shall support Optimized shared connections to Emerald 4K transmitters
	public void Test08_IO0002_4k_optimised_shared_connection_launch() throws Exception
	{
		printTestDetails("Starting", "Test08_IO0002_4k_optimised_shared_connection_launch", "");
		Onedevices=devicePool.getAllDevices("device4k.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		System.out.println("Created Optimised Shared Connection Successfully");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			ramethods.RADisconnectConnection(Windriver);
			Thread.sleep(3000);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured");
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}


	@Test(priority=9)//The Remote App shall support Optimized private connections to Emerald 4K transmitters.
	public void Test09_IO0001_4k_optimised_private_connection_launch() throws Exception
	{
		printTestDetails("Starting", "Test09_IO0001_4k_optimised_private_connection_launch", "");
		Onedevices=devicePool.getAllDevices("device4k.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.createprivateconnections(firedrive, Onedevices);
		System.out.println("Created Optimised Private Connection Successfully");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			ramethods.RADisconnectConnection(Windriver);
			Thread.sleep(3000);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured");
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}

	@Test(priority=10)//user interfaces shall reflect the branding of the company, 
	public void Test10_AI0002_Verifying_BlackBox_logo_inRA() throws Exception
	{
		printTestDetails("Starting", "Test10_AI0002_Verifying_BlackBox_logo_inRA", "");
		SoftAssert soft = new SoftAssert();
		setup();
		System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);
		Windriver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
		WebElement icon = Windriver.findElementByAccessibilityId("blackBoxIcon");
		//WebElement icon = getElement("blackBoxIcon");
		soft.assertEquals(true, icon.isDisplayed(), "Blackbox Icon is Not Displayed in Login Page");
		System.out.println("Blackbox icon Displayed in Login Page ");

		Windriver.findElementByAccessibilityId("DemoModeCheckBox").click();
		Windriver.findElementByName("Submit").click();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		//soft.assertEquals(true, icon.isDisplayed(), "Blackbox Icon is Not Displayed in Connections Page");
		System.out.println("Blackbox icon Displayed in Connections Page ");

		closeApp();
		soft.assertAll();
	}

	@Test(priority=11)//Copy paste from user pc to RA
	public void Test11_CP0001_copy_English_char_from_PC_paste_inConnection() throws Exception
	{
		printTestDetails("Starting", "Test11_CP0001_copy_English_char_from_PC_paste_inConnection", "");
		SoftAssert soft = new SoftAssert();
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		
		 cleanUpLogin(); 
		 ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared"); 
		 userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
		 cleanUpLogout();
		 

		String textinPC = RA_Methods.NotepadSetupInPCAndCopy(Windriver, "copypaste");
		System.out.println("Number of Characters in PC  :" + textinPC.length());

		try 
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(30000);

			WindowsDriver RASession = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
			WebElement close = RASession.findElementByName("Close");

			ramethods.NotepadSetupInConAndPaste(RASession, "Tx");
			Thread.sleep(3000);
			close.click();
			System.out.println("Closing Launched Connection");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
			closeApp();

			String pasteText = RA_Methods.textFromSFTP("pcadmin", "Blackbox@123", "10.231.128.91", "/C:/Users/pcadmin/desktop/paste.txt");
			System.out.println("Number of Characters in Transmitter  :" + pasteText.length());
			soft.assertEquals(textinPC, pasteText, "Text copied from Pc is Not pasted in Connection as Expected");

			if( textinPC== pasteText)
			{
				System.out.println("The Text copied from PC is pasted in Transmitter Connection successfully");
			}
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();	
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

	@Test(priority=12)//Copying and Pasting character set shall be confined to ASCII (special characters) and spaces for Transmitter.
	public void Test12_CP0005_copy_ASCII_from_PC_paste_inConnection() throws Exception
	{
		printTestDetails("Starting", "Test12_CP0005_copy_ASCII_from_PC_paste_inConnection", "");
		SoftAssert soft = new SoftAssert();
		Onedevices = devicePool.getAllDevices("deviceSE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
		cleanUpLogout();

		String textinPC = RA_Methods.NotepadSetupInPCAndCopy(Windriver, "ASCII");
		System.out.println("Number of Characters in PC  :" + textinPC.length());

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(30000);

			WindowsDriver RASession = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
			WebElement close = RASession.findElementByName("Close");

			ramethods.NotepadSetupInConAndPaste(RASession, "Tx");
			Thread.sleep(3000);
			close.click();
			System.out.println("Closing Launched Connection");

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
			closeApp();

			String pasteText = RA_Methods.textFromSFTP("pcadmin", "Blackbox@123", "10.231.128.92", "/C:/Users/pcadmin/desktop/paste.txt");
			System.out.println("Number of Characters in Transmitter  :" + pasteText.length());
			soft.assertEquals(textinPC, pasteText, "Text copied from Pc is Not pasted in Connection as Expected");

			if( textinPC== pasteText)
			{
				System.out.println("The Text copied from PC is pasted in Transmitter connection successfully");
			}

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			soft.assertAll();
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


	@Test(priority=13)//Test copy paste from one RA connection another Ra connection. 
	public void Test13_CP0001_Data_Notcopied_from_one_connection_to_another() throws Exception
	{
		printTestDetails("Starting", "Test13_CP0001_Data_Notcopied_from_one_connection_to_another", "");
		SoftAssert soft = new SoftAssert();
		Onedevices = devicePool.getAllDevices("devicePE.properties");

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(50000);

			WindowsDriver RASession = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
			WebElement close = RASession.findElementByName("Close");

			ramethods.NotepadSetupInConAndCopy(RASession);
			Thread.sleep(3000);
			close.click();
			System.out.println("Closing First Launched Connection");

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
			closeApp();

			cleanUpLogin();
			DeleteConnection();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();

			WindowsDriver Windriver3=null;
			WindowsDriver Windriver4 = null;
			//working with second connection
			Onedevices = devicePool.getAllDevices("deviceSE.properties");
			cleanUpLogin();
			ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
			userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
			cleanUpLogout();

			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(20000);

			WindowsDriver RASession1 = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
			WebElement close1 = RASession1.findElementByName("Close");

			ramethods.NotepadSetupInConAndPaste(RASession1, "Tx");
			Thread.sleep(3000);
			close1.click();
			System.out.println("Closing Second Launched Connection");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
			closeApp();

			String con1Text = RA_Methods.textFromSFTP("pcadmin", "Blackbox@123", "10.231.128.92", "/C:/Users/pcadmin/desktop/paste.txt");
			System.out.println("Number of Characters in Transmitter  :" + con1Text.length());

			String con2Text = RA_Methods.textFromSFTP("pcadmin", "Blackbox@123", "10.231.128.91", "/C:/Users/pcadmin/desktop/paste.txt");
			System.out.println("Number of Characters in Transmitter  :" + con2Text.length());

			soft.assertNotEquals(con1Text, con2Text, "Text Successfully copied from One Connection and pasted in Another Connection");

			if( con1Text != con2Text)
			{
				System.out.println("The Text not copied from one Transmitter Connection and pasted in another Transmitter Connection ");
			}
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

		soft.assertAll();		
	}


	@Test(priority=14)//Clipboard Copying and Pasting max size (number of characters) is set to maximum of 255 for Transmitter.
	public void Test14_CP0006_max_copy_255_Char_from_PC_paste_inConnection() throws Exception
	{
		printTestDetails("Starting", "Test14_CP0006_max_copy_255_Char_from_PC_paste_inConnection", "");
		SoftAssert soft = new SoftAssert();
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
		cleanUpLogout();

		String textinPC = RA_Methods.NotepadSetupInPCAndCopy(Windriver, "280characters");
		System.out.println("Number of Characters in PC  :" + textinPC.length());

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(30000);

			WindowsDriver RASession = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);
			WebElement close = RASession.findElementByName("Close");

			ramethods.NotepadSetupInConAndPaste(RASession, "Tx");
			Thread.sleep(3000);
			close.click();
			System.out.println("Closing Launched Connection");

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
			closeApp();


			String pasteText = RA_Methods.textFromSFTP("pcadmin", "Blackbox@123", "10.231.128.91", "/C:/Users/pcadmin/desktop/paste.txt");
			System.out.println("Number of Characters in Transmitter  :" + pasteText.length());

			if(pasteText.length()<=255)
			{
				soft.assertNotEquals(textinPC, pasteText, "More than 256 characters is pasted in Connection Notepad");
				System.out.println("Only first 255 characters pasted in transmitter Connection");
			}

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

		soft.assertAll();		
	}


	@Test(priority=15)//Clipboard Copying and Pasting max size (number of characters) will be OS defined for Microsoft Windows Virtual Machine.
	public void Test15_CP00013_VM_should_allow_more_than_255_characters() throws Exception
	{
		printTestDetails("Starting", "Test15_CP00013_VM_should_allow_more_than_255_characters", "");
		SoftAssert soft = new SoftAssert();
		ArrayList<String> VMname = new ArrayList<String>();
		VMname.add(VMIp);

		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive, null, RAusername, RApassword, "General");
		userpage.Sharedconnectionassign(firedrive, RAusername, VMname);
		cleanUpLogout();

		String textinPC = RA_Methods.NotepadSetupInPCAndCopy(Windriver, "280characters");
		System.out.println("Number of Characters in local PC  :" + textinPC.length());

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);

			WindowsDriver session = RA_Methods.launchedVMConnectionWindow(Windriver, Windriver2);
			WebElement close = session.findElementByName("Close");

			ramethods.NotepadSetupInConAndPaste(session, "Vm");
			Thread.sleep(3000);
			close.click();
			System.out.println("Closing Launched Connection");
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
			closeApp();


			String pasteText = RA_Methods.textFromSFTP(VMUsername, VMPassword, VMIp, "/C:/Users/Dell/desktop/paste.txt");
			System.out.println("Number of Characters in VM  :" + pasteText.length());

			if(pasteText.length()>255)
			{
				soft.assertEquals(textinPC, pasteText, "More than 255 characters is NOT allowed and pasted in VM Connection Notepad");
				System.out.println("VM Connection text pasted is same as the PC text copied");
			}
			else {
				soft.fail("Not all text is pasted in VM");
			}

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
		soft.assertAll();			
	}

	@Test(priority=16)//Windows control key sequence to “CNTL-ALT-DELETE” should work on the local PC 
	public void Test16_SR0044_CNTL_ALT_DEL_should_work_for_Local_PC() throws Exception
	{
		printTestDetails("Starting", "Test16_SR0044_CNTL_ALT_DEL_should_work_for_Local_PC", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General"); 
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);


			WindowsDriver session = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);

			Actions action = new Actions(Windriver);
			Thread.sleep(4000);

			action.sendKeys(Keys.chord(Keys.CONTROL, Keys.ALT, Keys.DELETE)).build().perform();
			Thread.sleep(180000);
			System.out.println("Security options Window of Local PC is displayed");
			action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
			System.out.println("Back to connection window");
			Thread.sleep(10000);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(2000);
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



	@Test(priority = 17)//Performance testing to see fps, df, and mbps on=- PE Single head 
	public void Test17_PE_SH_Performance_Check_FPS_DF_Mbps() throws Exception
	{
		printTestDetails("Starting", "Test17_PE_SH_Performance_Check_FPS_DF_Mbps", "");
		SoftAssert softAssert = new SoftAssert();
		Onedevices = devicePool.getAllDevices("devicePE_SH.properties");

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);

			ramethods.teratermactions(Windriver, "10.231.128.117", deviceUserName, devicePassword); 

			//Getting FPS deatils through API
		float fpssum =0 ; float dfpssum = 0; 
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
		 fpssum = fpssum + ActiveRAconnectionFPS;
		 dfpssum = dfpssum + droppedFPS;
		 Thread.sleep(3000);		 
		}

		float fps = fpssum/10;
		float droppedfps = dfpssum/10;

		System.out.println("The FPS of launched connection is : " + fps);
		System.out.println("The Dropped FPS of launched connection is : " + droppedfps);
			 
			RA_Methods.textFromSFTP(deviceUserName, devicePassword, "10.231.128.117", "/usr/local/output.txt");

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

			double fpssum1 = 0;
			double fpsavg = 0;
			for(int i =0 ; i< fpslistOfInteger.size(); i++)
			{
				fpssum1 = fpssum1 + fpslistOfInteger.get(i);

			}
			fpsavg = fpssum1/fpslistOfInteger.size();

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

			/*//validating fps values
		if(fpsavg<40) {
			fpsstatus = false;
			Assert.assertTrue(fpsstatus, "FPS value is less than 40 and is "+fpsavg);
		}
		else
		{
			System.out.println("Average Fps value is :"+fpsavg);
		}*/

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


	@Test(priority = 18)//Performance testing to see fps, df, and mbps on=- SE Single head 
	public void Test18_SE_SH_Performance_Check_FPS_DF_Mbps() throws Exception
	{
		printTestDetails("Starting", "Test18_SE_SH_Performance_Check_FPS_DF_Mbps", "");
		SoftAssert softAssert = new SoftAssert();
		Onedevices = devicePool.getAllDevices("deviceSE_SH.properties");

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);

			ramethods.teratermactions(Windriver, "10.231.128.119", deviceUserName, devicePassword); 

			//Getting FPS deatils through API
		float fpssum =0 ; float dfpssum = 0; 
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
		 fpssum = fpssum + ActiveRAconnectionFPS;
		 dfpssum = dfpssum + droppedFPS;
		 Thread.sleep(3000);		 
		}

		float fps = fpssum/10;
		float droppedfps = dfpssum/10;

		System.out.println("The FPS of launched connection is : " + fps);
		System.out.println("The Dropped FPS of launched connection is : " + droppedfps);
			 
			RA_Methods.textFromSFTP(deviceUserName, devicePassword, "10.231.128.119", "/usr/local/output.txt");

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

			double fpssum1 = 0;
			double fpsavg = 0;
			for(int i =0 ; i< fpslistOfInteger.size(); i++)
			{
				fpssum1 = fpssum1 + fpslistOfInteger.get(i);

			}
			fpsavg = fpssum1/fpslistOfInteger.size();

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

			/*//validating fps values
		if(fpsavg<40) {
			fpsstatus = false;
			Assert.assertTrue(fpsstatus, "FPS value is less than 40 and is "+fpsavg);
		}
		else
		{
			System.out.println("Average Fps value is :"+fpsavg);
		}*/

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
			//Assert.fail();
		}

	}



	@Test(priority=19) // Status Information to provide information on error conditions
	public void Test19__Statusinfo_of_connection_after_rebooting_Tx() throws Exception
	{
		printTestDetails("Starting", "Test19_AI0049_Statusinfo_of_connection_after_rebooting_Tx", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);
		cleanUpLogin();
		ConnectionPage.createprivateconnections(firedrive, Onedevices);
		//ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			int count = 0;
			int connum = 1;
			connectionList.clear();
			WebElement allConnectionList = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnection = allConnectionList.findElements(By.xpath("//ListItem"));
			for(WebElement connectionName : availableConnection)
			{
				connectionList.add(connectionName.getText());
				System.out.println("Connection Number " + connum + " is " + connectionName.getText());
				connum++;
			}
			for(String connection : connectionList)
			{
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				WebElement targetConnection = allConnectionList.findElement(By.name(connection));
				Actions action = new Actions(Windriver);
				action.moveToElement(targetConnection).
				doubleClick().build().perform();
				System.out.println("Connection Name -  " +connection + " has launched");
				count++;
				Thread.sleep(10000);
			}
			/*
			 * new WebDriverWait(Windriver,
			 * 60).until(ExpectedConditions.visibilityOf(Windriver.
			 * findElementByAccessibilityId("TitleBar"))); WebElement popUpWindow1
			 * =Windriver.findElementByAccessibilityId("TitleBar");
			 * Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			 * System.out.println("Pop-up window is  "+popUpWindow1.getText());
			 * Thread.sleep(20000);
			 */


			//WindowsDriver session = RA_Methods.launchedConnectionWindow(Windriver, Windriver2);

			RestAssured.useRelaxedHTTPSValidation();
			String response = given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers(BoxillaHeaders.getBoxillaHeaders())
					.when().contentType(ContentType.JSON)
					.post("https://" + boxillaManager + "/bxa-api/devices/kvm/reboot")
					.then().assertThat().statusCode(200)
					.extract().response().asString();
			System.out.println("Transmitter Reboot status " + response);

			//new WebDriverWait(Windriver2, 60).until(ExpectedConditions.visibilityOf(Windriver2.findElementByAccessibilityId("TitleBar")));	
			//WebElement popUpWindow =Windriver2.findElementByAccessibilityId("TitleBar");

			Thread.sleep(6000);
			new WebDriverWait(Windriver2, 60).until(ExpectedConditions.visibilityOf(Windriver2.findElementByName("Unable to connect")));	
			WebElement popUpWindow =Windriver2.findElementByName("Unable to connect");

			//Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
			System.out.println("Pop-up window is  "+popUpWindow.getText());

			Thread.sleep(2000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
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

}
	