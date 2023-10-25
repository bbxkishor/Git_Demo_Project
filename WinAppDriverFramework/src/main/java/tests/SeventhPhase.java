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

public class SeventhPhase extends TestBase{

	final static Logger log = Logger.getLogger(SeventhPhase.class);

	@Test(priority=1)
	public void Test01_LS641_Disable_TX_Connection_launch_message() throws Exception
	{
		printTestDetails("Starting", "Test01_LS641_Disable_TX_Connection_launch_message", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);

		
		
		
		cleanUpLogin();
		ArrayList connectionNum = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.enableLaunchMessage(Windriver);

			/*Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			getElement("menuLabel").click();
			Thread.sleep(2000);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByName("Settings").click();
			Thread.sleep(2000);
			System.out.println("Clicking on the Connection Window");
			WebElement temp2 = Windriver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));

			Thread.sleep(3000);
			Actions d = new Actions(Windriver);
			d.moveToElement(temp2, 160, 15).
			doubleClick().
			build().perform();
			Thread.sleep(2000);
			Windriver.findElementByName("ConfigurableConnectionLaunchMsgCheckBox").click();
			Thread.sleep(2000);
			getElement("applyButton").click();
			Thread.sleep(3000);*/

			Actions action = new Actions(Windriver);
			int count = 0;
			int connum = 1;
			connectionList.clear();
			Thread.sleep(5000);
			WebElement allConnectionList = Windriver.findElementByAccessibilityId("availableConnectionsWinListBox");
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
				Thread.sleep(4000);
				WebElement targetConnection = allConnectionList.findElement(By.name(connection));
				action.moveToElement(targetConnection).
				doubleClick().build().perform();
				System.out.println("Connection Name -  " +connection + " has launched");
				count++;
				
				

				try 
				{
					new WebDriverWait(Windriver2, 60).until(ExpectedConditions.visibilityOf(Windriver2.findElementByName("Unable to connect")));	
					WebElement popUpWindow =Windriver2.findElementByName("Unable to connect");
					System.out.println("Pop-up window is  "+popUpWindow.getText());
					if(popUpWindow.isDisplayed())
					{
						Assert.fail("After disabling also, Launch Connection pop-up is displayed");
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}		
			}
			
			Thread.sleep(50000);
			SoftAssert soft = new SoftAssert();
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Launched Connections are : " + connection1);
			soft.assertEquals(connectionNum.size(), connection1, "Connections are not Launched ");
			Thread.sleep(3000);

			/*ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);*/
			Thread.sleep(3000);
			ramethods.disableLaunchMessage(Windriver);
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

	@Test(priority=2)//
	public void Test02_LS641_Disable_VM_Connection_launch_message() throws Exception
	{
		printTestDetails("Starting", "Test02_LS641_Disable_VM_Connection_launch_message", "");
		ArrayList<String> VMname= new ArrayList<String>();
		VMname.add(VMIp);

		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive, null, RAusername, RApassword, "General");
		UserPage.Sharedconnectionassign(firedrive, RAusername, VMname);
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.enableLaunchMessage(Windriver);

			/*Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			getElement("menuLabel").click();
			Thread.sleep(2000);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByName("Settings").click();
			Thread.sleep(2000);
			System.out.println("Clicking on the Connection Window");
			WebElement temp2 = Windriver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));

			Thread.sleep(3000);
			Actions d = new Actions(Windriver);
			d.moveToElement(temp2, 160, 15).
			doubleClick().
			build().perform();
			Thread.sleep(2000);
			Windriver.findElementByName("ConfigurableConnectionLaunchMsgCheckBox").click();
			Thread.sleep(2000);
			getElement("applyButton").click();
			Thread.sleep(3000);*/

			Actions action = new Actions(Windriver);
			int count = 0;
			int connum = 1;
			connectionList.clear();
			Thread.sleep(5000);
			WebElement allConnectionList = Windriver.findElementByAccessibilityId("availableConnectionsWinListBox");
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
				Thread.sleep(4000);
				WebElement targetConnection = allConnectionList.findElement(By.name(connection));
				action.moveToElement(targetConnection).
				doubleClick().build().perform();
				System.out.println("Connection Name -  " +connection + " has launched");
				count++;
			

				try 
				{
				//	new WebDriverWait(Windriver2, 60).until(ExpectedConditions.visibilityOf(Windriver2.findElementByName("Unable to connect")));	
					WebElement popUpWindow =Windriver2.findElementByName("Unable to connect");
					System.out.println("Pop-up window is  "+popUpWindow.getText());
					if(popUpWindow.isDisplayed())
					{
						Assert.fail("After disabling also, Launch Connection pop-up is displayed");
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}		
			}
			
			Thread.sleep(50000);
			SoftAssert soft = new SoftAssert();
			int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Launched Connections are : " + connection1);
			soft.assertEquals(1, connection1, "Connections are not Launched ");
			Thread.sleep(3000);

			/*ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(40000);*/
			Thread.sleep(3000);
			ramethods.disableLaunchMessage(Windriver);
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

	@Test(priority=-3)
	public void Test03_LS599_TX_Connection_CTRL_ALT_END_in_Control_bar() throws Exception
	{
		printTestDetails("Starting", "Test03_LS599_TX_Connection_CTRL_ALT_END_in_Control_bar", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		try
		{
			RAlogin(RAusername, RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(30000);
			
			//Getting window handle of launched connection
			WebElement connectionWindow = (WebElement) Windriver2.findElementByClassName("wCloudBB");
			String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
			String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex

			//Setting capabilities for connection window session
			DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
			connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

			WindowsDriver RASession;

			//attaching to connection session and doing stuff..
			RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
			WebElement minimise = RASession.findElementByName("Minimise");
			WebElement maximise = RASession.findElementByName("Maximise");
			WebElement close = RASession.findElementByName("Close");
			WebElement title = RASession.findElementByAccessibilityId("TitleBar"); 

			System.out.println("Title of launched connection is "+title.getText());
			
			
//			//Getting window handle of launched connection
//			WebElement connectionWindow = (WebElement) Windriver2.findElementByClassName("wCloudBB");
//			String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
//			String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex
//
//			//Setting capabilities for connection window session
//			DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
//			connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);
//			Thread.sleep(2000);
//			WindowsDriver RASession;
//			RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
			
			
			WebElement titlebar = RASession.findElementByAccessibilityId("TitleBar");
			Thread.sleep(2000);
			Actions d = new Actions(Windriver);
			d.contextClick(titlebar).perform();
			
			
			
			
			
			
			
			
			
			ramethods.RADisconnectConnection(Windriver);


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

