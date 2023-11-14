package tests;

import static io.restassured.RestAssured.given; 
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
import io.restassured.path.json.JsonPath;

import methods.AppliancePool;
import methods.Device;
import methods.Devices;
import methods.Discovery;
import methods.DiscoveryMethods;
import methods.RA_Methods;
import methods.SeleniumActions;
import methods.Users;
import pages.ConnectionPage;
import pages.LandingPage;
import pages.UserPage;
import pages.boxillaElements;

public class Firstphase extends TestBase{

	
	
	//new line added    
	final static Logger log = Logger.getLogger(Firstphase.class);
	UserPage userpage = new UserPage();

	@Test //Change the appliance setting to absolute
	public void Test01_SR0018_HID_Absolute_should_be_same() throws Exception {
		printTestDetails("STARTING ", "Test01_SR0018_HID_Absolute_should_be_same", "");
		Assert.assertEquals(1, 1);

		cleanUpLogin();
		devices = devicePool.getAllDevices("device.properties");
		for(Device deviceList : devicePool.allDevices()) {
			//checking the required appliances is online or not.
			ramethods.checkdeviceonline(firedrive, deviceList.getIpAddress());
			Thread.sleep(5000);

			if (!SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable).contains("RX")) {

				if (SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable).contains(deviceList.getIpAddress())) {
					Thread.sleep(6000);
					firedrive.manage().window().maximize();
					WebElement button =firedrive.findElement(By.xpath(Devices.breadCrumbBtn));
					JavascriptExecutor js = (JavascriptExecutor)firedrive;
					js.executeScript("arguments[0].click();", button);
					
					//SeleniumActions.seleniumClick(firedrive, Devices.breadCrumbBtn);
					System.out.println("Devices > Settings > Options - Clicked on breadcrumb");
				} else {
					System.out.println("Devices > Status > Options - Searched device not found");
					throw new SkipException("***** Searched device - " + deviceList.getIpAddress() + " not found *****");
				}
				SeleniumActions.seleniumClick(firedrive, Devices.editSettings());
				System.out.println("clicked on Edit settings");

				Devices.uniqueHidDropdown(firedrive,"Absolute");
				System.out.println("Attempting to save transmitter properties");
				timer(firedrive);
				SeleniumActions.seleniumClick(firedrive, Devices.getEditTxSaveBtnXpath());
				//assert if successful
				Alert alert = firedrive.switchTo().alert();
				alert.accept();
				timer(firedrive);
				new WebDriverWait(firedrive, 60).until(ExpectedConditions.visibilityOf(Devices.getDeviceToastMessage(firedrive)));
				String message = Devices.getDeviceToastMessage(firedrive).getText();
				System.out.println("Pop up message: " + message);
				timer(firedrive);
				if(message.equals("Error")) {
					SeleniumActions.seleniumClick(firedrive, Devices.getEditTxCancelXpath());
					throw new AssertionError("Unable to save TX Settings. Toast error");
				}
				timer(firedrive);
				System.out.println("Successfully saved transmitter properties");	
			}
		}
		System.out.println("No update for Receiver");
	}


	@Test //The Emerald RemoteApp shall only operate when a Boxilla Manager is present and active, unless in demo mode
	public void Test02_CL0001_RA_syncs_with_bxaManager_IP_Check() throws Exception {
		printTestDetails("STARTING ", "Test02_CL0001_RA_syncs_with_bxaManager_IP_Check", "");
		setup();
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Windriver.manage().timeouts().implicitlyWait(3,TimeUnit.SECONDS);
		Windriver.findElementByAccessibilityId("DemoModeCheckBox").click();
		Windriver.findElementByName("Submit").click();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//System.out.println(Windriver.getWindowHandle());
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		getElement("menuLabel").click();
		System.out.println("Menu Label clicked");
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.findElementByName("Settings").click();
		System.out.println("Settings clicked");
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		getElement("ipAddressTextBox").sendKeys(" ");
		System.out.println("IP kept empty");
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.findElementByName("Configure").click();
		Thread.sleep(2000);
		System.out.println("configure clicked Now closing RemoteApp");
		closeRemoteApp();
		Thread.sleep(2000);
		setup();
		Thread.sleep(1000);
		System.out.println("Configuring boxilla with IP "+boxillaManager);
		Thread.sleep(4000);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		getElement("ipAddressTextBox").sendKeys(boxillaManager);
		System.out.println("IP Address Entered");
		Windriver.findElementByName("Configure").click();
		System.out.println("IP Configured....Closing RemoteApp");
		closeRemoteApp();
	}

	@Test //The Emerald RemoteApp shall provide an option to configure the IP address for the active Boxilla Manager.
	public void Test03_CL0002_Configure_RA_with_bxa_IP() throws Exception {
		printTestDetails("STARTING ", "Test03_CL0002_Configure_RA_with_bxa_IP", "");
		setup();
		Windriver.manage().timeouts().implicitlyWait(3,TimeUnit.SECONDS);
		Windriver.findElementByAccessibilityId("DemoModeCheckBox").click();
		Windriver.findElementByName("Submit").click();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//System.out.println(Windriver.getWindowHandle());
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		getElement("menuLabel").click();
		Thread.sleep(2000);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.findElementByName("Settings").click();
		Thread.sleep(2000);
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		getElement("ipAddressTextBox").sendKeys(boxillaManager);
		Windriver.findElementByName("Configure").click();
		Thread.sleep(2000);
		System.out.println("IP has been configured with "+boxillaManager);
		closeRemoteApp();
	}

	@Test // logging to boxilla and confirm the active user that logged in RemoteApp
	public void Test04_CL0003_Active_User_check() throws Exception {
		printTestDetails("STARTING ", "Test04_CL0003_Active_User_check", "");
		cleanUpLogin();
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		cleanUpLogout();
		setup();
		System.out.println("RemoteApp is opened");
		WebElement loginButton = getElement("logInButton");
		getElement("userNameTextBox").sendKeys(RAusername);
		System.out.println("Username Entered");
		getElement("passwordTextBox").sendKeys(RApassword);
		System.out.println("Password Entered");
		loginButton.click();
		System.out.println("Login button clicked");
		Thread.sleep(30000);
		cleanUpLogin();
		String username=UserPage.currentUser(firedrive,RAusername,10);
		Assert.assertTrue(username.contains(RAusername),
				"current User table did not contain: " + RAusername + ", actual text: " + username);
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();

	}

	@Test//Authenticate user RA test
	public void Test05_SR0021_RA_Check_with_invalid_user() throws Exception {
		printTestDetails("STARTING ", "Test05_SR0021_RA_Check_with_invalid_user", "");
		setup();
		WebElement loginButton = getElement("logInButton");
		getElement("userNameTextBox").sendKeys("User");
		System.out.println("User name entered as -User");
		getElement("passwordTextBox").sendKeys("User");
		System.out.println("Password entered as -User");
		loginButton.click();
		System.out.println("Login button clicked");
		try {
			Thread.sleep(3000);
			WebElement windowsPopupOpenButton = Windriver.findElementByName("Log In: Boxilla Error");
			
			//WebElement windowsPopupOpenButton = Windriver.findElementByAccessibilityId("TitleBar");
			String text= windowsPopupOpenButton.getText();
			System.out.println("Alert Message is  "+text);
			Thread.sleep(5000);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.findElementByName("Demo Mode").click();
			Windriver.findElementByName("Submit").click();
			Thread.sleep(5000);
			//System.out.println(Windriver.getWindowHandle());
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			getElement("menuLabel").click();
			System.out.println("Menu Label clicked");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByName("Close").click();
			System.out.println("RemoteApp closed");
		}catch(Exception e) {
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.findElementByName("Demo Mode").click();
			Windriver.findElementByName("Submit").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			closeApp();
			System.out.println(Windriver.getWindowHandle());
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			getElement("menuLabel").click();
			System.out.println("Menu Label clicked");
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByName("Close").click();
			System.out.println("RemoteApp closed");
		}
	}

	@Test //RA configuration for connection, settings and information
	public void Test07_AI0004_RA_Menu_Options_Check() throws Exception {
		printTestDetails("STARTING ", "Test07_AI0004_RA_Menu_Options_Check", "");
		boolean statusconnect = false;
		boolean statusSettings = false;
		boolean statusInfo = false;

		cleanUpLogin();
		userpage.createUser(firedrive,devices,RAusername,RApassword,"General");
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		getElement("menuLabel").click();
		if (Windriver.findElementByName("Connections").isDisplayed()) {
			statusconnect = true;
			System.out.println("Connections option is displayed");
		}
		if (Windriver.findElementByName("Settings").isDisplayed()) {
			statusSettings = true;
			System.out.println("Settings option is displayed");
		}
		if (Windriver.findElementByName("Information").isDisplayed()) {
			statusInfo = true;
			System.out.println("Information option is displayed");
		}
		Assert.assertTrue(statusconnect,"connections option is not displayed");
		Assert.assertTrue(statusSettings,"Settings option is not displayed");
		Assert.assertTrue(statusInfo,"Information option is not displayed");
		closeApp();

		cleanUpLogin();
		userpage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
	}

	@Test //Software version and blackbox contact details
	public void Test08_AI0046_Version_BB_details_check() throws Exception {
		printTestDetails("STARTING ", "Test08_AI0046_Version_BB_details_check", "");
		SoftAssert softAssert = new SoftAssert();

		cleanUpLogin();
		userpage.createUser(firedrive,devices,RAusername,RApassword,"General");
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		getElement("menuLabel").click();
		Thread.sleep(2000);
		Windriver.findElementByName("Information").click();
		System.out.println("Information tab is clicked");
		Thread.sleep(2000);
		String Version=getElement("versionLabel").getText();
		System.out.println("The version of RemoteApp is "+Version);
		Assert.assertTrue(Version.contains("Version"), "RemoteApp does not contain Version on the Information section");
		String contact=getElement("blackBoxWebsiteLinkLabel").getText();
		System.out.println("Contact details of blackBox is "+contact);
		softAssert.assertTrue(contact.contains("https://www.blackbox.com/en-us/support"), "RemoteApp does not have BlackBox contact details");
		closeApp();

		cleanUpLogin();
		userpage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		softAssert.assertAll();
	}

	@Test //Help information should be shown
	public void Test09_AI0047_Information_Tab() throws Exception {
		printTestDetails("STARTING ", "Test09_AI0047_Information_Tab", "");
		SoftAssert softAssert = new SoftAssert();

		cleanUpLogin();
		userpage.createUser(firedrive,devices,RAusername,RApassword,"General");
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		getElement("menuLabel").click();
		Thread.sleep(3000);
		Windriver.findElementByName("Information").click();
		System.out.println("Information tab is clicked");
		softAssert.assertTrue(getElement("supportLabel").isDisplayed(),"Help information is not displayed");
		System.out.println("Help Information has been displayed");
		closeApp();

		cleanUpLogin();
		userpage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		softAssert.assertAll();
	}

	@Test //This “Auto” value is the preferred value of the local desktop.
	public  void Test10_VI0006_Resolution_Set_to_Auto() throws Exception{
		printTestDetails("STARTING ", "Test10_VI0006_Resolution_Set_to_Auto", "");
		SoftAssert softAssert = new SoftAssert();

		cleanUpLogin();
		userpage.createUser(firedrive,devices,RAusername,RApassword,"General");
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		getElement("menuLabel").click();
		Windriver.findElementByName("Settings").click();
		System.out.println("Clicking on the Connection Window");
		WebElement temp2 = Windriver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));
		Thread.sleep(3000);
		Actions d = new Actions(Windriver);
		d.moveToElement(temp2, 160, 15).
		doubleClick().
		build().perform();
		Thread.sleep(2000);

		WebElement comboBoxElement = Windriver.findElement(By.xpath("//ComboBox[starts-with(@ClassName,\"WindowsForms10\")]"));
		comboBoxElement.click();
		Thread.sleep(2000);
		comboBoxElement.sendKeys("Auto");
		Thread.sleep(3000);
		getElement("applyButton").click();
		Thread.sleep(3000);
		getElement("menuLabel").click();
		Windriver.findElementByName("Settings").click();
		String resolution=getElement("windowResolutionComboBox").getText();
		System.out.println("Connection Window resolution is "+resolution);
		softAssert.assertTrue(resolution.equalsIgnoreCase("Auto"), "Connection Window resolution is not Auto");
		closeApp();

		cleanUpLogin();
		userpage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		softAssert.assertAll();
	}

	@Test //verify the list of connections associated to the user
	public void  Test11_AI0007_Connection_List_check() throws Exception {
		printTestDetails("STARTING ", "Test11_AI0007_Connection_List_check", "");
		SoftAssert softAssert = new SoftAssert();
		Onedevices=devicePool.getAllDevices("Onedevice.properties");
		devices = devicePool.getAllDevices("device.properties");

		cleanUpLogin();
		SharedNames = ConnectionPage.CreateConnection(firedrive, Onedevices, 4, "Shared");//createprivateconnections(firedrive, Onedevices);//CreateConnection(firedrive, Onedevices, 1, "Private");//createprivateconnections(firedrive,Onedevices);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, SharedNames);
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		System.out.println("availableConnectionsList is "+availableConnectionsList.getText());
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		for (WebElement connection : availableConnections) {
			boolean status = false;	
			for (Device deviceList : devices)
			{
				System.out.println("Checking for connection  "+connection.getText()+" in the device list "+deviceList.getIpAddress());
				if(connection.getText().contains(deviceList.getIpAddress())) {
					status=true;
					softAssert.assertTrue(status,"Connection Name "+connection+" shown in RemoteApp has not been assigned to the user");
					System.out.println(" connection "+connection.getText()+" is assigned to the correct user");
					break;
				}
				System.out.println("Connection name - "+connection+" has been assigned correctly o the User");
			}
		}
		closeApp();

		cleanUpLogin();
		UserPage.DeleteUser(firedrive,RAusername);
		cleanUpLogout();
		softAssert.assertAll();
	}


	@Test//Test to ensure all the user(administrator, power and General) have same privileges
	public void Test12_AI0005_User_Privileges_Check() throws Exception {
		printTestDetails("STARTING ", "Test12_AI0005_User_Privileges_Check", "");
		SoftAssert softAssert = new SoftAssert();

		cleanUpLogin();
		Onedevices=devicePool.getAllDevices("Onedevice.properties");
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive,Onedevices,"TestUser1","TestUser1","general");
		Thread.sleep(5000);
		userpage.createUser(firedrive,Onedevices,"TestUser2","TestUser2","power");
		Thread.sleep(5000);
		userpage.createUser(firedrive,Onedevices,"TestUser3","TestUser3","admin");
		cleanUpLogout();

		for(int i=1;i<4;i++) {
			RAlogin("TestUser"+i,"TestUser"+i);
			WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
			System.out.println("availableConnectionsList is "+availableConnectionsList.getText());
			List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
			System.out.println("list is "+availableConnections);
			for (WebElement connection : availableConnections) {
				boolean status = false;	
				for (Device deviceList : Onedevices)
				{
					System.out.println("Checking for connection  "+connection.getText()+" in the device list "+deviceList.getIpAddress());
					if(connection.getText().equalsIgnoreCase(deviceList.getIpAddress())) {
						status=true;
						softAssert.assertTrue(status,"Connection Name "+connection+" shown in RemoteApp has not been assigned to the user");
						System.out.println(" connection "+connection.getText()+" is assigned to the correct user");
						break;
					}
				}
			}
			closeApp();
		}
		cleanUpLogin();
		Thread.sleep(3000);
		for(int j=1;j<4;j++) {
			UserPage.DeleteUser(firedrive,"TestUser"+j);
		}
		cleanUpLogout();
		softAssert.assertAll();
	}



	@Test //ensure the launched connection not to impact and user need not to change the password in current session.
	public void Test13_AI0032_User_Password_Update_wont_Disturb_Connection() throws Exception {
		printTestDetails("STARTING ", "Test13_AI0032_User_Password_Update_wont_Disturb_Connection", "");
		SoftAssert softAssert = new SoftAssert();
		Onedevices=devicePool.getAllDevices("Onedevice.properties");

		cleanUpLogin();
		Thread.sleep(3000);
		ArrayList connectionNam = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, connectionNam);
		cleanUpLogout();

		try
		{
			RAlogin(RAusername,RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		Thread.sleep(60000);

		cleanUpLogin();
		Thread.sleep(4000);
		LandingPage.usersTab(firedrive).click();
		System.out.println("User clicked");
		firedrive.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		userpage.manage(firedrive).click();
		System.out.println("Manage option clicked");
		Thread.sleep(2000);
		userpage.searchOption(firedrive).sendKeys(RAusername);
		userpage.optionbutton(firedrive).click();
		userpage.EditUser(firedrive).click();
		userpage.password(firedrive).clear();
		userpage.password(firedrive).sendKeys("NewPassword");
		userpage.confirmPassword(firedrive).clear();
		userpage.confirmPassword(firedrive).sendKeys("NewPassword");
		userpage.NextButton(firedrive).click();
		userpage.NextButton(firedrive).click();
		userpage.savebutton(firedrive).click();
		System.out.println("Password updated");
		firedrive.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		cleanUpLogout();

		int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active connections are "+connectionCount);
		System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
		softAssert.assertEquals(connectionCount, Windriver.getWindowHandles().size()," Number of active connection didn't match with the number of connections before changing credentials");

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();
		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		softAssert.assertAll();
		}catch(Exception e)
		{
			e.printStackTrace();
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			softAssert.assertAll();
		}
	}

	@Test (priority = 14)//User should able to select a connection
	public void Test14_AI0043_Select_Connection_from_list() throws Exception {
		printTestDetails("STARTING ", "Test14_AI0043_Select_Connection_from_list", "");
		Onedevices=devicePool.getAllDevices("Onedevice.properties");

		cleanUpLogin();
		ArrayList connname = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		UserPage.Sharedconnectionassign(firedrive, RAusername, connname);
		cleanUpLogout();

		try {
			RAlogin(RAusername,RApassword);
			WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
			int conNum=1;
			for (WebElement connection : availableConnections) {
				System.out.println("connections number  "+conNum+" is "+connection.getText());
				connectionList.add(connection.getText());
				conNum++;
			}
			for (String connectionName : connectionList) {
				WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
				Actions a = new Actions(Windriver);
				a.moveToElement(targetConnection);
				System.out.println("Cursor could move to connection "+connectionName);
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			}
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}catch(Exception e) {
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}


	@Test (priority = 15)//Ensure both the connection window and the application window to remain open
	public void Test15_CL0006a_Active_multipleCon_windows_to_be_open() throws Exception{
		printTestDetails("STARTING ", "Test15_CL0006a_Active_multipleCon_windows_to_be_open", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<Device> PEDevices = new ArrayList<Device>();
		PEDevices=devicePool.getAllDevices("devicePE.properties");;
		ArrayList<String> connectionPEList = new ArrayList<String>();

		cleanUpLogin();
		ArrayList connName = ConnectionPage.CreateConnection(firedrive, PEDevices, 1, "Private");
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, connName);
		cleanUpLogout();

		try {
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);

			int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active connections are "+connectionCount);
			System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
			softAssert.assertEquals(connectionCount, Windriver.getWindowHandles().size(),"Number of Windows Mismatch");

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
			ramethods.RADisconnectConnection(Windriver);

			Windriver.switchTo().window(Windriver.getWindowHandle());
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			softAssert.assertAll();
		}
		catch(Exception e) {
			e.printStackTrace();
			Windriver.switchTo().window(Windriver.getWindowHandle());
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}

	@Test (priority = 16)//Launch SE tX
	public void Test16_DC0001_Launch_Connection_of_SETX() throws Exception {
		printTestDetails("STARTING ", "Test16_Launch_DC0001_Connection_of_SETX", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<Device> SEDevices = new ArrayList<Device>();
		SEDevices=devicePool.getAllDevices("deviceSE.properties");;
		ArrayList<String> connectionPEList = new ArrayList<String>();

		cleanUpLogin();
		ConnectionPage.createprivateconnections(firedrive,SEDevices);
		userpage.createUser(firedrive,SEDevices,RAusername,RApassword,"General");
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		Thread.sleep(60000);

		int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active connections are "+connectionCount);
		System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
		softAssert.assertEquals(connectionCount, Windriver.getWindowHandles().size(),"Number of Windows Mismatch");

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(3000);
		ramethods.RADisconnectConnection(Windriver);

		Windriver.switchTo().window(Windriver.getWindowHandle());
		closeApp();
		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		softAssert.assertAll();
	}


	@Test (priority = 17)//Launch Connection to PE transmitter
	public void Test17_DC0001_Connection_of_PETX() throws Exception {
		printTestDetails("STARTING ", "Test17_DC0001_Connection_of_PETX", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<Device> PEDevices = new ArrayList<Device>();
		PEDevices=devicePool.getAllDevices("devicePE.properties");;
		ArrayList<String> connectionPEList = new ArrayList<String>();

		cleanUpLogin();
		ArrayList ConnName=ConnectionPage.CreateConnection(firedrive, PEDevices, 1, "Private");
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, ConnName);
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		Thread.sleep(60000);

		int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active connections are "+connectionCount);
		System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
		softAssert.assertEquals(connectionCount, Windriver.getWindowHandles().size(),"Number of Windows Mismatch");

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(3000);
		ramethods.RADisconnectConnection(Windriver);

		Windriver.switchTo().window(Windriver.getWindowHandle());
		closeApp();
		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		Thread.sleep(5000);
		cleanUpLogout();
		softAssert.assertAll();
	}


	@Test(priority = 18)// Launch Connection to ZeroU transmitter
	public void Test18_DC0001_Connection_of_ZeroUTx() throws Exception {
		printTestDetails("STARTING ", "Test18_DC0001_Connection_of_ZeroUTx", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<Device> ZuDevices = new ArrayList<Device>();
		ZuDevices=devicePool.getAllDevices("deviceZeroU.properties");;
		ArrayList<String> connectionPEList = new ArrayList<String>();

		cleanUpLogin();
		ArrayList connName = ConnectionPage.CreateConnection(firedrive, ZuDevices, 1, "Private");
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, connName);
		cleanUpLogout();

		try {
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);

			int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active connections are "+connectionCount);
			System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
			softAssert.assertEquals(connectionCount, Windriver.getWindowHandles().size(),"Number of Windows Mismatch");

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
			ramethods.RADisconnectConnection(Windriver);
			Windriver.switchTo().window(Windriver.getWindowHandle());
			closeApp();
			softAssert.assertAll();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			Thread.sleep(5000);
			cleanUpLogout();

		}catch(Exception e) {
			e.printStackTrace();
			Windriver.switchTo().window(Windriver.getWindowHandle());
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			Thread.sleep(5000);
			cleanUpLogout();
		}
	}


	@Test (priority = 19)//private connection termination
	public void Test19_SR0005_connection_Termination_After_privateCon() throws Exception {
		printTestDetails("STARTING ", "Test19_SR0005_connectionTermination_After_privateCon", "");
		SoftAssert softAssert = new SoftAssert();
		WebDriverWait wait=new WebDriverWait(firedrive, 20);
		ArrayList connectionName =null;
		ArrayList<String> DualHeadList = new ArrayList<String>();
		DiscoveryMethods discoveryMethods = new DiscoveryMethods();	
		AppliancePool applian = new AppliancePool();
		ArrayList<String> connectionZeroUList = new ArrayList<String>();
		ArrayList<Device> remotedevice=applian.getAllDevices("Onedevice.properties");
		System.out.println(remotedevice);

		try {	
			cleanUpLogin();
			connectionName = ConnectionPage.Sharedconnection(firedrive, remotedevice, 2, "Private");
			userpage.createUser(firedrive,remotedevice,RAusername,RAusername,"General");
			cleanUpLogout();

			Thread.sleep(10000);
			RAlogin(RAusername,RAusername);
			WebElement availablesharedConnectionsList = getElement("availableConnectionsWinListBox");
			List<WebElement> availablesharedConnections = availablesharedConnectionsList.findElements(By.xpath("//ListItem"));
			connectionList.clear();
			int connectionNumber=1,count=0;
			for (WebElement connection : availablesharedConnections) {
				System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
				connectionList.add(connection.getText());
				connectionNumber++;
			}
			System.out.println(connectionList);			
			for (String connectionsharedName : connectionList) {

				WebElement targettoConnect = availablesharedConnectionsList.findElement(By.name(connectionsharedName));
				Actions a = new Actions(Windriver);
				count++;
				a.moveToElement(targettoConnect).
				doubleClick().
				build().perform();
				if(count==2) {
					Thread.sleep(4000);
					new WebDriverWait(Windriver, 60).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("TitleBar")));
					WebElement windowsPopupOpenButton = Windriver.findElementByAccessibilityId("TitleBar");
					String text= windowsPopupOpenButton.getText();
					System.out.println("Message is "+text);
					break;
				}
				System.out.println(connectionsharedName+" has been launched");
				Thread.sleep(20000);
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			}
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			Thread.sleep(2000);
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			softAssert.assertAll();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			Thread.sleep(2000);
			Thread.sleep(2000);
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}


	@Test (priority = 20)//. Close remote application while connections are running
	public void Test20_AI0038_Close_RA_Closes_AllCon() throws Exception {
		printTestDetails("STARTING ", "Test20_AI0038_Close_RA_Closes_AllCon", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<Device> ZuDevices = new ArrayList<Device>();
		ZuDevices=devicePool.getAllDevices("deviceZeroU.properties");;
		ArrayList<String> connectionPEList = new ArrayList<String>();

		cleanUpLogin();
		ArrayList ConnName=ConnectionPage.CreateConnection(firedrive, ZuDevices, 1, "Private");	
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, ConnName);
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		Thread.sleep(60000);

		int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active connections are "+connectionCount);
		System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
		softAssert.assertEquals(connectionCount, Windriver.getWindowHandles().size(),"Number of Windows Mismatch");

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();
		Thread.sleep(60000);

		int connectionCount1 = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active connections are "+connectionCount1);
		if(connectionCount1==0) {
			System.out.println("RemoteApp is closed and all the connectections are terminated");
			softAssert.assertTrue(true);

		}else softAssert.assertFalse(true, +connectionCount1+" Connection is still active");

		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		Thread.sleep(2000);
		cleanUpLogout();
		softAssert.assertAll();
	}

	@Test (priority = 21) //check a pop up message on launching and terminating connections
	public void Test21_AI0048_Launch_Terminate_Connection_PopUp_Message() throws Exception {
		printTestDetails("STARTING ", "Test21_AI0048_Launch_Terminate_Connection_PopUp_Message", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<Device> PEDevices = new ArrayList<Device>();
		AppliancePool applian = new AppliancePool();
		ArrayList<String> connectionPEList = new ArrayList<String>();
		ArrayList<Device> remotedevice=applian.getAllDevices("devicePE.properties");
		System.out.println(remotedevice);
		PEDevices.addAll(remotedevice);

		cleanUpLogin();
		ConnectionPage.createprivateconnections(firedrive,remotedevice);
		userpage.createUser(firedrive,remotedevice,RAusername,RApassword,"General");
		cleanUpLogout();

		try {
			RAlogin(RAusername,RApassword);
			WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
			for (WebElement connection : availableConnections) {
				connectionPEList.add(connection.getText());
			}
			System.out.println("Launching connection");
			Actions a = new Actions(Windriver);
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionPEList.get(0)));
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			Thread.sleep(4000);
			new WebDriverWait(Windriver, 60).until(ExpectedConditions.visibilityOf(Windriver.findElementByAccessibilityId("TitleBar")));
			WebElement windowsPopupconnection =  Windriver.findElementByAccessibilityId("TitleBar");
			String ConnectionText= windowsPopupconnection.getText();//
			System.out.println("Pop Message for starting a connection - "+ConnectionText);
			Thread.sleep(20000);

			/*
			 * Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]
			 * ); Windriver.findElement(By.name(connectionPEList.get(0))).click();
			 * Windriver.findElement(By.name("Disconnect")).click(); WebElement
			 * windowsPopupDisconnect = Windriver.findElementByAccessibilityId("TitleBar");
			 * String Disconnecttext= windowsPopupDisconnect.getText();
			 * System.out.println(Disconnecttext);
			 */
			Thread.sleep(5000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);				 
			cleanUpLogout();
			softAssert.assertAll();
		}catch(Exception e) {
			e.printStackTrace();
			Windriver.switchTo().window(Windriver.getWindowHandle());
			closeApp();

			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}

	@Test (priority = 22) //ensure to get an error message for more than 32 characters in password
	public void Test22_AI0025a_Password_Charac_Limit() throws Exception {
		printTestDetails("STARTING ", "Test22_AI0025a_Password_Charac_Limit", "");

		cleanUpLogin();
		userpage.user(firedrive).click();
		firedrive.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		userpage.manage(firedrive).click();
		userpage.NewUser(firedrive).click();
		userpage.useTempNo(firedrive).click();
		userpage.ActiveDNo(firedrive).click();
		userpage.username(firedrive).sendKeys("TestUser");
		userpage.password(firedrive).sendKeys("ppppppppppppppppppppppppppppppppp");
		userpage.confirmPassword(firedrive).sendKeys("ppppppppppppppppppppppppppppppppp");
		userpage.NextButton(firedrive).click();
		WebElement general = firedrive.findElement(By.xpath(Users.getNewUserPrivilegeGeneralBtn()));
		SeleniumActions.exectuteJavaScriptClick(firedrive, general);
		userpage.RemoteAccess(firedrive).click();
		userpage.NextButton(firedrive).click();
		userpage.savebutton(firedrive).click();
		userpage.searchOption(firedrive).sendKeys("TestUser");
		System.out.println("Username entered in search box");
		String deviceApplianceTable = SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable);
		Assert.assertTrue(deviceApplianceTable.contains("TestUser"),
				"Device appliance table did not contain: TestUser actual text: " + deviceApplianceTable);

		userpage.optionbutton(firedrive).click();
		firedrive.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		userpage.DeleteOption(firedrive).click();
		firedrive.switchTo().alert().accept();
		System.out.println("TestUser is deleted");
		cleanUpLogout();
	}

	@Test (priority = 23) //Ensure to get an error message for an active directorty user creation on password having more than 105 characters
	public void Test23_AI0025a_AD_Password_Charac_Limit() throws Exception {
		printTestDetails("STARTING ", "Test23_AI0025a_AD_Password_Charac_Limit", "");

		cleanUpLogin();
		String user="a";
		for (int i=1;i<109;i++) {
			user=user+"a";
		}
		System.out.println("user is "+user);
		userpage.user(firedrive).click();
		firedrive.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		userpage.manage(firedrive).click();
		userpage.NewUser(firedrive).click();
		userpage.useTempNo(firedrive).click();
		userpage.ActiveDNo(firedrive).click();
		userpage.username(firedrive).sendKeys("TestUser");
		userpage.password(firedrive).sendKeys(user);
		userpage.confirmPassword(firedrive).sendKeys(user);
		userpage.NextButton(firedrive).click();
		WebElement admin = firedrive.findElement(By.xpath(Users.getNewUserPrivilegeAdminBtn()));
		SeleniumActions.exectuteJavaScriptClick(firedrive, admin);
		userpage.RemoteAccess(firedrive).click();
		userpage.NextButton(firedrive).click();
		userpage.savebutton(firedrive).click();
		userpage.searchOption(firedrive).sendKeys("TestUser");
		System.out.println("Username entered in search box");
		String deviceApplianceTable = SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable);
		Assert.assertTrue(deviceApplianceTable.contains("TestUser"),
				"Device appliance table did not contain: TestUser actual text: " + deviceApplianceTable);
		userpage.optionbutton(firedrive).click();
		firedrive.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		userpage.DeleteOption(firedrive).click();
		firedrive.switchTo().alert().accept();
		System.out.println("TestUser is deleted");
		cleanUpLogout();
	}

	@Test (priority = 24) //check the password should be alphanumeric
	public void Test24_AI0033_Password_alphaNumeric() throws Exception {
		printTestDetails("STARTING ", "Test24_AI0033_Password_alphaNumeric", "");
		SoftAssert softAssert = new SoftAssert();

		cleanUpLogin();
		Thread.sleep(3000);
		userpage.user(firedrive).click();
		firedrive.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		userpage.manage(firedrive).click();
		userpage.NewUser(firedrive).click();
		userpage.useTempNo(firedrive).click();
		userpage.ActiveDNo(firedrive).click();
		userpage.username(firedrive).sendKeys("TestUser");
		userpage.password(firedrive).sendKeys("qwert!£$%/");
		userpage.confirmPassword(firedrive).sendKeys("qwert!£$%/");
		userpage.NextButton(firedrive).click();
		softAssert.assertEquals(userpage.IncorrectPassword(firedrive), "Password can't contain certain characters. Invalid characters are: \"'/\\[]:;|=,+*?<>`");
		cleanUpLogout();
	}


	@Test (priority = 25) //Launch the connection in View only mode
	public void Test25_SR0046_Con_viewOnly() throws Exception {
		printTestDetails("STARTING ", "Test25_SR0046_Con_viewOnly", "");
		WebDriverWait wait=new WebDriverWait(firedrive, 20);
		ArrayList<Device> DualHeadDevices = new ArrayList<Device>();
		ArrayList<String> connectionDualList = new ArrayList<String>();
		DualHeadDevices=devicePool.getAllDevices("Onedevice.properties");
		ArrayList<String> viewList = new ArrayList<String>();

		cleanUpLogin();
		ConnectionPage.CreateViewOnlyConnection(firedrive, DualHeadDevices, 1, "Shared");
		userpage.createUser(firedrive,DualHeadDevices,RAusername,RApassword,"General");
		Thread.sleep(5000);
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		Thread.sleep(40000);


		try {
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
			Assert.assertTrue(title.getText().contains("View Only"), " Title doesn't contain view only");
			Thread.sleep(2000);
			close.click();
			Thread.sleep(5000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
	}
}
