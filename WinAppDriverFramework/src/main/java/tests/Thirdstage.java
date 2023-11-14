package tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.log4testng.Logger;



import groovy.util.logging.Log4j;
import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.WindowsElement;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import methods.AppliancePool;
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

public class Thirdstage extends TestBase{


	final static Logger log = Logger.getLogger(Thirdstage.class);



	@Test(priority = 1 )//Launch dualHead without Extended Desktop for different users
	public void Test01_SR0018_No_Extended_Desktop() throws Exception {
		printTestDetails("STARTING ", "Test01_SR0018_NO_Extended_Desktop", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<String> DualHeadList = new ArrayList<String>();
		WebDriverWait wait=new WebDriverWait(firedrive, 20);
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		System.out.println(Onedevices);

		//Logging to boxilla to create a connection and 3 user's and assigning them
		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		for (int i=1;i<4;i++) {
			Thread.sleep(2000);
			userpage.createUser(firedrive,Onedevices,"TestUser"+i,"TestUser"+i,"General");
		}
		
		cleanUpLogout();

		//Launching RemoteApp for each user one at a time and launching connections 
		for(int j=1;j<4;j++) {	
			RAlogin("TestUser"+j,"TestUser"+j);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
			List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
			DualHeadList.clear();
			for (WebElement connection : availableConnections) {
				DualHeadList.add(connection.getText());
			}
			System.out.println("List is "+DualHeadList);
			for (String connectionName : DualHeadList) {
				Actions a = new Actions(Windriver);
				WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
				a.moveToElement(targetConnection).
				doubleClick().
				build().perform();
				System.out.println("connection named "+connectionName+" has been launched by user "+"TestUser"+j);
				Thread.sleep(30000);
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			}
			closeApp();
		}
		Thread.sleep(90000);   


		//Asserting the number of connections to be zero
		RestAssured.useRelaxedHTTPSValidation();

		String gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
				.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
				.then().assertThat().statusCode(200)
				.extract().response().asString();

		System.out.println("Response is "+gerdetails);

		JsonPath js1 = new JsonPath(gerdetails);
		int count=js1.getInt("message.size()");
		int countconnection=js1.getInt("message.active_connections.size()");
		System.out.println("connection size is "+countconnection);
		softAssert.assertEquals(countconnection, 0," Active connection expected to be Zero but found "+countconnection);


		//deleting the users and connections created for this test
		cleanUpLogin();
		for(int user=1;user<4;user++) {
			UserPage.DeleteUser(firedrive, "TestUser"+user);
		}
		cleanUpLogout();
		softAssert.assertAll();
	}

	@Test (priority = 2) //Log out of the VM while in connection
	public void Test02_SR0024_Logout_from_VM() throws Exception {
		printTestDetails("STARTING ", "Test02_SR0024_Logout_from_VM", "");
		ArrayList<String> RAVM = new ArrayList<String>();
		RAVM.add(VMIp);
		SoftAssert softAssert = new SoftAssert();
		
		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, RAVM);
		cleanUpLogout();
		
		RAlogin(RAusername, RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		Thread.sleep(60000);
		
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
		Thread.sleep(2000);
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
	}


	@Test(priority = 3 )//fps and dropped frame per second check for launched connection
	public void Test03__PE_DH_Performance_Check_FPS_DF_Mbps() throws Exception {
		printTestDetails("STARTING ", "Test03_PE_DH_Performance_Check_FPS_DF_Mbps", "");
		SoftAssert softAssert = new SoftAssert();
		float ActiveconnectionFPS = 0;
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		
		cleanUpLogin();
		ArrayList connectName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");//createprivateconnections(firedrive,Onedevices);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, connectName);
		cleanUpLogout();
		
		RAlogin(RAusername,RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		for (WebElement connection : availableConnections) {
			System.out.println("connections number is "+connection.getText());
			connectionList.add(connection.getText());
		}

		for (String connectionName : connectionList) {
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
			Actions a = new Actions(Windriver);
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			System.out.println(connectionName+" has been launched");
			Thread.sleep(90000);

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
			System.out.println("Number of Active connections are "+RAcountconnection);
			System.out.println("Number of expected active connections to be "+connectionList.size());
			//log.info("FPS of the launched connection "+connectionName+ " is "+ActiveRAconnectionFPS);
			//log.info("Dropped FPS of the launched connection "+connectionName+ " is "+droppedFPS);
			softAssert.assertEquals(RAcountconnection, 1," Number of active connection didn't match with the number of connections before changing credentials");	
		}	
		
		Thread.sleep(2000);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(3000);
		//ramethods.RADisconnectConnection(Windriver);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(2000);
		closeApp();
		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		softAssert.assertAll();
	}	
	

@Test (priority = 4)// launch a shared connection with RA and TX RX
	public void Test04_SR0001_Con_Launch_in_RAandRX() throws Exception {
		printTestDetails("STARTING ", "Test04_SR0001_Con_Launch_in_RAandRX", "");
		SoftAssert softAssert = new SoftAssert();
		float ActiveconnectionFPS = 0;
		ArrayList connectionName=null;
		Onedevices = devicePool.getAllDevices("deviceSE.properties");
		cleanUpLogin();
		ArrayList<String> list = new ArrayList<String>();

		list.add(RecieverName);
		JSONObject reqparam = new JSONObject();
		reqparam.put("device_names", list);

		try { 
			RestAssured.useRelaxedHTTPSValidation();

			String response = given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers(BoxillaHeaders.getBoxillaHeaders())
					.when().contentType(ContentType.JSON)
					.body(reqparam.toJSONString())
					.post("https://" + boxillaManager + "/bxa-api/devices/kvm/reboot")
					.then().assertThat().statusCode(200)
					.extract().response().asString();  

			System.out.println("Reboot Transmitter status "+response);
			Thread.sleep(10000);
			connectionName= ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");//createprivateconnections(firedrive,Onedevices);
			ConnectionPage.launchSharedConnection(firedrive,connectionName,RecieverName);
			//ConnectionPage.launchSharedConnection(firedrive,connectionName,"Test_RX_Dual_Pe");
			userpage.createUser(firedrive,Onedevices,RAusername,RApassword,"General");
			cleanUpLogout();
			Thread.sleep(110000);
			RestAssured.useRelaxedHTTPSValidation();
			String bxaActiveDetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
					.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
					.then().assertThat().statusCode(200)
					.extract().response().asString();
			System.out.println("Active connection status"+bxaActiveDetails);

			System.out.println("********************checking the connection status  ******************");
			JsonPath js = new JsonPath(bxaActiveDetails);
			int	countconnection=js.getInt("message.active_connections.size()");
			ActiveconnectionFPS = js.get("message.active_connections[0].fps");
			System.out.println("TX-RX Active connection FPS is : "+ActiveconnectionFPS);
			Thread.sleep(3000);
			
			int count=0;
			int connectionNumber=1;
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(90000);

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
			System.out.println("RA-Tx Active connection FPS is : "+ActiveRAconnectionFPS);
			System.out.println("Number of Active connections are "+RAcountconnection);
			System.out.println("Number of expected active connections to be "+connectionList.size());
			//softAssert.assertEquals(RAcountconnection, 1," Number of active connection didn't match with the number of connections before changing credentials");	
			softAssert.assertEquals(RAcountconnection, 2," Number of active connection didn't match with the number of connections before changing credentials");	

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
			ramethods.RADisconnectConnection(Windriver);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			
			
			//softAssert.assertEquals(ActiveconnectionFPS, ActiveRAconnectionFPS,"Mismatch in FPS values in TX-RX and RA-Tx connections");
			closeApp();
			Windriver.quit();
			cleanUpLogin();
			UserPage.DeleteUser(firedrive, RAusername);
			ConnectionPage.BreakboxillaConnection(firedrive);
			cleanUpLogout();
			softAssert.assertAll();
			RestAssured.useRelaxedHTTPSValidation();
		}
		catch(Exception e){
			e.printStackTrace();
			closeApp();
			cleanUpLogin();
			ConnectionPage.BreakboxillaConnection(firedrive);
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			RestAssured.useRelaxedHTTPSValidation();
		}
	}

	@Test (priority = 5) //set smart sizing and launch VM connection
	public void Test05_VI0007_SmartSizing_VM_launch() throws Exception {
		printTestDetails("STARTING ", "Test05_VI0007_SmartSizing_VM_launch", "");
		ArrayList<String> RAVM = new ArrayList<String>();
		RAVM.add(VMIp);
		
		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, RAVM);
		cleanUpLogout();
		
		RAlogin(RAusername, RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		int connectionNumber = 1;
		for (WebElement connection : availableConnections) {
			System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
			connectionList.add(connection.getText());
			connectionNumber++;
		}

		getElement("menuLabel").click();
		Windriver.findElementByName("Settings").click();
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(2000);

		WebElement temp2 = Windriver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));
		Thread.sleep(3000);
		Actions d = new Actions(Windriver);
		d.moveToElement(temp2, 160, 15).
		doubleClick().
		build().perform();

		Windriver.findElementByAccessibilityId("smartSizingCheckBox").click();
		getElement("menuLabel").click();
		Windriver.findElementByName("Connections").click();
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);

		for (String connectionName : connectionList) {
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
			Actions a = new Actions(Windriver);
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			Thread.sleep(90000);
		}

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);	
		closeApp();
		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();

	}

	@Test (priority = 6) // Launch a RA TX and then reboot the Tx to ensure the connection isterminated
	public void Test06_SR0025_Reboot_Tx_to_validate_drop_in_connection() throws Exception {
		printTestDetails("STARTING ", "Test06_SR0025_Reboot_Tx_to_validate_drop_in_connection", "");
		SoftAssert softAssert = new SoftAssert();
		float ActiveconnectionFPS = 0;
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		
		cleanUpLogin();
		ArrayList<String> RAconnectionName=ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		userpage.createUser(firedrive,Onedevices,RAusername,RApassword,"General");
		cleanUpLogout();

		RAlogin(RAusername,RApassword);
		ramethods.RAConnectionLaunch(Windriver);
		Thread.sleep(30000);

			ramethods.rebootfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			Thread.sleep(90000);
			
		
			int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
			System.out.println("Number of Active connections are "+connectionCount);
			System.out.println("Number of Active Windows are "+Windriver.getWindowHandles().size());
			softAssert.assertEquals(connectionCount, 0,"Number to active connection expected to be zero but found "+connectionCount);
	
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Thread.sleep(3000);
			closeApp();
			cleanUpLogin();
			
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
			softAssert.assertAll();			
	}

	@Test (priority = 7 )// change the RDP resolution settings
	public void Test07_AI0036_RDP_Resolution() throws Exception{
		printTestDetails("STARTING ", "Test07_AI0036_RDP_Resolution", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		
		cleanUpLogin();
		ArrayList<String> RAconnectionName=ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");//createprivateconnections(firedrive,Onedevices);
		userpage.createUser(firedrive,Onedevices,RAusername,RApassword,"General");
		cleanUpLogout();

		//Login to RA and change the RDP resolution in settings
		RAlogin(RAusername,RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		//Launch the connection
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		for (WebElement connection : availableConnections) {
			System.out.println("connections number is "+connection.getText());
			connectionList.add(connection.getText());
		}
		getElement("menuLabel").click();
		Windriver.findElementByName("Settings").click();
		System.out.println("Clicking on the Connection Window");

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(2000);
		WebElement temp2 = Windriver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));
		Thread.sleep(3000);
		Actions d = new Actions(Windriver);
		d.moveToElement(temp2, 160, 15).
		doubleClick().
		build().perform();
		Thread.sleep(2000);
		WebElement comboBoxElement = Windriver.findElement(By.xpath("//ComboBox[starts-with(@ClassName,\"WindowsForms10\")]"));
		comboBoxElement.click();
		comboBoxElement.sendKeys("1920x1080");
		Thread.sleep(2000);

		getElement("applyButton").click();
		Thread.sleep(3000);
		getElement("menuLabel").click();
		Windriver.findElementByName("Connections").click();
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);

		for (String connectionName : connectionList) {
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
			Actions a = new Actions(Windriver);
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			Thread.sleep(90000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		}
		int connectionCount = RA_Methods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
		System.out.println("Number of Active connections are "+connectionCount);
		
		
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();
		cleanUpLogin();
		
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
	}


	@Test(priority =8) // Launch private connection with media file 1920*1080
	public void Test08_VI0010_resolution_1920X1080_with_40fps() throws Exception{
		printTestDetails("STARTING ", "Test08_resolution_1920X1080_with_40fps", "");
		Onedevices = devicePool.getAllDevices("devicePE.properties");
		
		cleanUpLogin();
		for(Device deviceList : Onedevices) {
			System.out.println("Checking device status");
			System.out.println("Attempting to check if device with IP address " + deviceList.getIpAddress() + " is online");
			LandingPage.devicesTab(firedrive).click();
			new WebDriverWait(firedrive, 60).until(ExpectedConditions.elementToBeClickable(LandingPage.devicesStatus(firedrive)));
			LandingPage.devicesStatus(firedrive).click();
			System.out.println("Devices > Settings > Options - Clicked on Status tab");
			timer(firedrive);
			SeleniumActions.seleniumSendKeys(firedrive, Devices.deviceStatusSearchBox, deviceList.getIpAddress());
			//check if device is online
			int timer = 0;
			int limit = 12;			//12 iterations of 5 seconds = 1 minute
			while(timer  <= limit) {
				System.out.println("Checking if device is online");
				String isOnline = SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable);
				System.out.println("Is Online:" + isOnline);
				if(SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable).contains("OnLine")) {
					System.out.println("Device is online");
					break;
				}else if(timer < limit) {
					timer++;
					System.out.println("Device is offline. Rechecking " + timer);
					firedrive.navigate().refresh();
					Thread.sleep(5000);
				}else if (timer == limit) {
					Assert.assertTrue(1 == 0, "Device is not online");
				}
			}
			System.out.println("Successfully checked if device is online");
			if (!SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable).contains("RX")) {

				if (SeleniumActions.seleniumGetText(firedrive, Devices.applianceTable).contains(deviceList.getIpAddress())) {
					Thread.sleep(3000);
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

				Devices.uniqueEdid1Dropdown(firedrive,"1920x1080");
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
			ArrayList<String> RAconnectionName=ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");//createprivateconnections(firedrive,Onedevices);
			userpage.createUser(firedrive,Onedevices,RAusername,RApassword,"General");
			cleanUpLogout();

			//Login to RA and change the RDP resolution in settings
			RAlogin(RAusername,RApassword);
			WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");


			//Launch the connection
			List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
			connectionList.clear();
			for (WebElement connection : availableConnections) {
				System.out.println("connections number is "+connection.getText());
				connectionList.add(connection.getText());

			}

			for (String connectionName : connectionList) {
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
				Actions a = new Actions(Windriver);
				a.moveToElement(targetConnection).
				doubleClick().
				build().perform();
				System.out.println(connectionName+" has been launched");
				Thread.sleep(90000);

			}
			RestAssured.useRelaxedHTTPSValidation();
			String gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
					.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
					.then().assertThat().statusCode(200)
					.extract().response().asString();
			System.out.println("Active connection status"+gerdetails);

			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();

		}
	}




	@Test (priority = 9)// check the presence of licence
	public void Test09_CL0005a_RA_License_check() throws Exception {
		printTestDetails("STARTING ", "Test09_CL0005a_RA_Licensecheck", "");
		cleanUpLogin();
		log.info("Deleting license");
		log.info("Attempting navigate to BOXILLA license");

		//Navigate to Applicense part in boxilla
		SeleniumActions.seleniumClick(firedrive, SystemAll.licenseTab);
		log.info("License tab clicked");

		new WebDriverWait(firedrive, 60).until(ExpectedConditions.elementToBeClickable(SystemAll.getAppLicenseLink(firedrive)));
		SeleniumActions.seleniumClick(firedrive, SystemAll.appLicenseLink);

		Thread.sleep(4000);
		new WebDriverWait(firedrive, 60).until(ExpectedConditions.elementToBeClickable(SystemAll.getGenerateInfoFile(firedrive)));
		//Asserting if license is present in boxilla or not
		log.info("Checking if additional licenses available");
		Assert.assertTrue(SystemAll.getDeleteAppLicenseButton(firedrive).isDisplayed(),"License is not present");
		cleanUpLogout();

	}





	@Test (priority = 10)//Check the connections are in alphabetical order
	public void Test10_AI0020_Connection_in_AlphabeticalOrder() throws Exception {
		printTestDetails("STARTING ", "Test10_AI0020_Connection_in_AlphabeticalOrder", "");
		Onedevices = devicePool.getAllDevices("device.properties");
		cleanUpLogin();
		SharedNames = ConnectionPage.CreateConnection(firedrive, Onedevices, 4, "Shared");//createprivateconnections(firedrive, Onedevices);//CreateConnection(firedrive, Onedevices, 1, "Private");//createprivateconnections(firedrive,Onedevices);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, SharedNames);
		cleanUpLogout();

		//Login to RA and change the RDP resolution in settings
		RAlogin(RAusername,RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");



		//Launch the connection
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		for (WebElement connection : availableConnections) {
			System.out.println("connections number is "+connection.getText());
			connectionList.add(connection.getText());

		}


		isSorted(connectionList);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();
		cleanUpLogin();
		Thread.sleep(2000);
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();

	}

	boolean isSorted(Iterable<String> coll) {
		String prev = null;
		for (String value : coll) {
			if (prev != null && prev.compareTo(value) > 0)
				return false;
			prev = value;
		}
		return true;
	}

	@Test (priority = 11)// Navigate connections with keyboard
	public void Test11_AI0050_Con_Navigation_from_Keyboard() throws Exception {
		printTestDetails("STARTING ", "Test11_Con_Navigation_from_Keyboard", "");
		Onedevices = devicePool.getAllDevices("device.properties");
		cleanUpLogin();
		SharedNames = ConnectionPage.CreateConnection(firedrive, Onedevices, 4, "Shared");//createprivateconnections(firedrive, Onedevices);//CreateConnection(firedrive, Onedevices, 1, "Private");//createprivateconnections(firedrive,Onedevices);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, SharedNames);
		cleanUpLogout();

		//Login to RA and save the list of connections in list
		RAlogin(RAusername,RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		for (WebElement connection : availableConnections) {
			System.out.println("connections number is "+connection.getText());
			connectionList.add(connection.getText());

		}

		//choose the connection name from  list and use down arrow key
		for (String connectionName : connectionList) {
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
			Actions a = new Actions(Windriver);
			a.moveToElement(targetConnection);
			Windriver.getKeyboard().sendKeys(Keys.ARROW_DOWN);
			System.out.println("Down key has been pressed");
			break;


		}

		//close the RemoteApp and delete the connection and user

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();
		cleanUpLogin();
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();

	}

	@Test (priority = 12)// create AD User and launch a connection
	public void Test12_AI0028_AD_User_LaunchConnection() throws Exception {
		printTestDetails("STARTING ", "Test12_AI0028_AD_User_LaunchConnection", "");
		Onedevices = devicePool.getAllDevices("Onedevice.properties");
		cleanUpLogin();
		SystemMethods.turnOnActiveDirectorySupport(firedrive);
		SystemMethods.enterActiveDirectorySettings(adIp, adPort, adDomain, adUsername, adPassword, firedrive);

		//get the settings and assert
		String[] settings = SystemMethods.getCurrentADSettings(firedrive);
		log.info("Checking Active Diretory IP");
		Assert.assertTrue(settings[0].equals(adIp), "Active directory IP was not set. Excepted: " + adIp + " actual:" + settings[0]);
		log.info("Checking Active directory port");
		Assert.assertTrue(settings[1].equals(adPort), "Port was not set. Excepted:" + adPort + " actual:" + settings[1]);
		log.info("Checking active directory domain");
		Assert.assertTrue(settings[2].equals(adDomain), "Domain was not set. Expected:" + adDomain + " actual:" + settings[2]);
		log.info("Checking active directory username");
		Assert.assertTrue(settings[3].equals(adUsername), "Username was not set. Excpeted:" + adUsername + " actual:" + settings[3]);


		String username = "kishorAD";
		UserMethods.addUserAD(firedrive, username);
		//wait for AD to syncss
		Thread.sleep(60000);
		String status = UserMethods.getActiveUserStatus(firedrive, username);
		log.info("Checking username");
		Assert.assertTrue(status.contains(username), "Status did not contain username. Excepted:" + username + " Actual:" + status );
		log.info("Checking Authorized by");
		Assert.assertTrue(status.contains("Active Directory"), "Authorized by did not contain Active Directory. Actual:" + status);
		log.info("Checking Domain");
		Assert.assertTrue(status.contains(adDomain),"Status did not contain domain. Expected:" + adDomain + " actual:" + status);
		log.info("Checking AD status");
		Assert.assertTrue(status.contains("OU Status"), "Status did not contain OD Status. Expected: ADtrue, actual: " + status );
		ArrayList<String> names=ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Private");
		UserPage.Sharedconnectionassign(firedrive, username, names);
		cleanUpLogout();

		RAlogin(username,adPassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		for (WebElement connection : availableConnections) {
			System.out.println("connections number is "+connection.getText());
			connectionList.add(connection.getText());

		}
		for (String connectionName : connectionList) {
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
			Actions a = new Actions(Windriver);
			a.moveToElement(targetConnection)
			.doubleClick().build().perform();
			System.out.println(" Active directory Connection launched "+targetConnection);
			Thread.sleep(60000);

		}

		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();
		cleanUpLogin();
		SystemMethods.deleteOU(firedrive, username);
		
		UserPage.DeleteUser(firedrive, username);
		cleanUpLogout();


	}

	@Test (priority = 13) // Move mouse and keyboard on an active connection window
	public void Test13_CL0007_Key_mouse_MoveonActiveconnection() throws Exception {
		printTestDetails("STARTING ", "Test13_CL0007_Key_mouse_MoveonActiveconnection", "");
		float ActiveconnectionFPS = 0;

		Onedevices = devicePool.getAllDevices("devicePE.properties");

		cleanUpLogin();
		ConnectionPage.createprivateconnections(firedrive, Onedevices);//Sharedconnection(firedrive, Onedevices, 1, "shared");
		userpage.createUser(firedrive,Onedevices,RAusername,RApassword,"General");
		cleanUpLogout();



		int count=0;
		int connectionNumber=1;
		RAlogin(RAusername,RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		for (WebElement connection : availableConnections) {
			System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
			connectionList.add(connection.getText());
			connectionNumber++;
		}

		for (String RAconnectionName : connectionList) {

			WebElement targetConnection = availableConnectionsList.findElement(By.name(RAconnectionName));
			Actions a = new Actions(Windriver);
			count++;
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			System.out.println(RAconnectionName+" has been launched through RemoteApp");
			Thread.sleep(70000);

		}

		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) Windriver2.findElementByClassName("wCloudBB");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex

		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

		WindowsDriver RASession;

		//attaching to connection session and doing stuff..
		try {
			RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
			//RASession.switchTo().window((String)RASession.getWindowHandles().toArray()[0]);

			WebElement minimise = RASession.findElementByName("Minimise");
			WebElement maximise = RASession.findElementByName("Maximise");
			WebElement close = RASession.findElementByName("Close");

			maximise.click();
			Thread.sleep(3000);
			WebElement restore = RASession.findElementByName("Restore");
			restore.click();
			Thread.sleep(2000);
			close.click();
			//Window windowControl = RASession.manage().window();

			//windowControl.maximize();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
			
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(2000);
		closeApp();
		cleanUpLogin();
		
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();

	}



	@Test (priority = 14) //close a connection using the X button on the top right hand side of a connection window
	public void Test14_SR0023_close_connection() throws Exception {
		printTestDetails("STARTING ", "Test14_SR0023_close_"
				+ "connection", "");
		float ActiveconnectionFPS = 0;

		Onedevices = devicePool.getAllDevices("devicePE_SH.properties");

		cleanUpLogin();
		ConnectionPage.createprivateconnections(firedrive, Onedevices);//Sharedconnection(firedrive, Onedevices, 1, "shared");
		userpage.createUser(firedrive,Onedevices,RAusername,RApassword,"General");
		cleanUpLogout();



		int count=0;
		int connectionNumber=1;
		RAlogin(RAusername,RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		for (WebElement connection : availableConnections) {
			System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
			connectionList.add(connection.getText());
			connectionNumber++;
		}

		for (String RAconnectionName : connectionList) {

			WebElement targetConnection = availableConnectionsList.findElement(By.name(RAconnectionName));
			Actions a = new Actions(Windriver);
			count++;
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			System.out.println(RAconnectionName+" has been launched through RemoteApp");
			Thread.sleep(90000);

		}

		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) Windriver2.findElementByClassName("wCloudBB");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex

		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

		WindowsDriver RASession;

		//attaching to connection session and doing stuff..
		try {
			RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
			//RASession.switchTo().window((String)RASession.getWindowHandles().toArray()[0]);

			WebElement minimise = RASession.findElementByName("Minimise");
			WebElement maximise = RASession.findElementByName("Maximise");
			WebElement close = RASession.findElementByName("Close");


			Thread.sleep(2000);
			close.click();
			Thread.sleep(5000);
			//Window windowControl = RASession.manage().window();

			//windowControl.maximize();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			closeApp();
			cleanUpLogin();
	
			UserPage.DeleteUser(firedrive, RAusername);
			cleanUpLogout();
		}
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		closeApp();
		cleanUpLogin();
		
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();

	}

	@Test(priority = 15) //This test is to check the mouse movement on the launched VM connection through RemoteApp
	public void Test15_US0001_MouseMovement_inVM() throws Exception {

		printTestDetails("STARTING ", "Test15_US0001_MouseMovement_inVM", "");
		ArrayList<String> RAVM = new ArrayList<String>();
		RAVM.add(VMIp);
		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, RAVM);
		cleanUpLogout();
		RAlogin(RAusername, RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		int connectionNumber = 0;
		for (WebElement connection : availableConnections) {
			System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
			connectionList.add(connection.getText());
			connectionNumber++;
		}

		for (String connectionName : connectionList) {
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
			Actions a = new Actions(Windriver);
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			Thread.sleep(90000);
		}

		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) Windriver2.findElementByClassName("wCloudRDP");
		//WebElement connectionWindow = (WebElement) Windriver2.findElementByClassName("FreeRDP");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex

		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

		WindowsDriver RASession;

		//attaching to connection session and doing stuff..
		try {
			RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
			//RASession.switchTo().window((String)RASession.getWindowHandles().toArray()[0]);

			WebElement minimise = RASession.findElementByName("Minimise");
			Thread.sleep(3000);
			WebElement maximise = RASession.findElementByName("Maximise");
			Thread.sleep(3000);
			WebElement close = RASession.findElementByName("Close");

			Thread.sleep(2000);
			maximise.click();
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

	@Test (priority = 16) //Rebooting the VM server after a connection is launched
	public void Test16_SR0026_Networkloss_should_terminateConnec() throws Exception {
		printTestDetails("STARTING ", "Test16_SR0026_Networkloss_should_terminateConnec", "");
		SoftAssert softAssert = new SoftAssert();
		ArrayList<String> RAVM = new ArrayList<String>();
		RAVM.add(VMIp);
		cleanUpLogin();
		ConnectionPage.CreateRDPconnection(firedrive, VMIp, VMUsername, VMPassword, VMDomainName);
		userpage.createUser(firedrive,null,RAusername,RApassword,"General");
		userpage.Sharedconnectionassign(firedrive, RAusername, RAVM);
		cleanUpLogout();
		RAlogin(RAusername, RApassword);
		WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
		List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
		connectionList.clear();
		int connectionNumber = 0;
		for (WebElement connection : availableConnections) {
			System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
			connectionList.add(connection.getText());
			connectionNumber++;
		}

		for (String connectionName : connectionList) {
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
			Actions a = new Actions(Windriver);
			a.moveToElement(targetConnection).
			doubleClick().
			build().perform();
			Thread.sleep(90000);
		}



		//Getting back to the RA window
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		RestAssured.useRelaxedHTTPSValidation();

		//checking the API status of an active connections
		String gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
				.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
				.then().assertThat().statusCode(200)
				.extract().response().asString();

		System.out.println("Response is "+gerdetails);

		//Getting the connection count 
		JsonPath js1 = new JsonPath(gerdetails);
		int count=js1.getInt("message.size()");
		int countconnection=js1.getInt("message.active_connections.size()");
		System.out.println("connection size is "+countconnection);



		RestAssured.useRelaxedHTTPSValidation();
			String postrequest = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
								.when().body("")
								.post("https://" + boxillaManager + "/bxa-api/devices/kvm/reboot")
								.then().assertThat().statusCode(200)
								.extract().response().asString();

			 System.out.println("Active connection status"+postrequest);

		Thread.sleep(90000);


		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);	
		closeApp();
		cleanUpLogin();

		
		UserPage.DeleteUser(firedrive, RAusername);
		cleanUpLogout();
		softAssert.assertAll();

	}
}

