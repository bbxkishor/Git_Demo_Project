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

public class PractiseJunk extends TestBase{

	@Test
	public void practiseCode() throws Exception
	{
		
		printTestDetails("Starting", "Test08_IO0002_4k_optimised_shared_connection_launch", "");
		Onedevices=devicePool.getAllDevices("devicePE.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		System.out.println("Created Optimised Shared Connection Successfully");
		userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
		cleanUpLogout();

		
			RAlogin(RAusername,RApassword);
			ramethods.RAConnectionLaunch(Windriver);
			Thread.sleep(60000);
			
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

				WebElement ele = RASession.findElementByClassName("wCloudBB");
				Thread.sleep(3000);
				int gX = ele.getLocation().getX();
				System.out.println("X = " + gX);
				int gY = ele.getLocation().getY();
				System.out.println("Y = " + gY);
				Point location = RASession.manage().window().getPosition();
				System.out.println("location = " + location );
				int con = location.getX();
				int con1 = location.getY();
				System.out.println("X = " + con);
				System.out.println("Y = " + con1);
				
				Dimension currentDimension = RASession. manage(). window(). getSize();
				int height = currentDimension. getHeight();
				int width = currentDimension. getWidth();
				System. out. println("Current height: "+ height);
				System. out. println("Current width: "+width);
				WebElement maximise = RASession.findElementByName("Maximise");
				
				Thread.sleep(3000);
				maximise.click();
				Thread.sleep(3000);
				int gX1 = ele.getLocation().getX();
				System.out.println("X = " + gX1);
				int gY1 = ele.getLocation().getY();
				System.out.println("Y = " + gY1);
				Point location1 = RASession.manage().window().getPosition();
				System.out.println("location = " + location1 );
				int con2 = location1.getX();
				int con3 = location1.getY();
				System.out.println("X = " + con2);
				System.out.println("Y = " + con3);
				Dimension currentDimension1 = RASession. manage(). window(). getSize();
				int height1 = currentDimension1. getHeight();
				int width1 = currentDimension1. getWidth();
				System. out. println("Current height: "+ height1);
				System. out. println("Current width: "+width1);
				
				//var e1 = AutomationEle
				
				
				/*
				 * 
				 * 
				WebElement minimise = RASession.findElementByName("Minimise");
				WebElement maximise = RASession.findElementByName("Maximise");
				WebElement close = RASession.findElementByName("Close");

				maximise.click();
				Thread.sleep(3000);
				WebElement restore = RASession.findElementByName("Restore");
				restore.click();
				Thread.sleep(2000);
				close.click();*/
				//Window windowControl = RASession.manage().window();

				//windowControl.maximize();
			
			
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
		
		


			/*Thread.sleep(15000);
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			if(Windriver.findElementByAccessibilityId("connectionsPage").isEnabled())
			{
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				autologin.AutoLoginDisable(Windriver);
				autologin.AutoConnectDisable(Windriver);
				closeApp();
			}


		
		
		
		
		

		/*DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("ms:waitForAppLaunch", 30);
		capabilities.setCapability("app", "C:\\Program Files (x86)\\BlackBox\\EmeraldRA.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");


		try {
			Thread.sleep(1000);

			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);
			Windriver2.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

		}
		catch(Exception e){
			System.out.println("Exception has occured ");
			e.printStackTrace();
		} 


		try
		{
			boolean visible = Windriver.findElement(By.name("EMERALD Remote App")).isDisplayed();
			if(visible == true)
			{
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				Windriver.findElementByAccessibilityId("AutoLogInCheckBox").click();
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				System.out.println("AutoLogin Disabled - Clicked on AutoLogin Button ");
			}

		}
		catch(NoSuchElementException n)
		{
			n.printStackTrace();
		}*/

		/*



		boolean visible = Windriver.findElement(By.name("EMERALD Remote App Log-In")).isDisplayed();




		List<WebElement> el = Windriver.findElements(By.name("EMERALD Remote App Log-In"));
		//el.add

		if(el.size()!=0)
		{
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);
			Windriver2.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);			
		}
		//ArrayList<WebElement> elements = new ArrayList<WebElement>();
		//elements.add(ele);
		 */




		//List<WebElement> el = Windriver.findElementsByAccessibilityId("exitMenuItems");

		//WebElement ele = Windriver.findElementByAccessibilityId("exitMenuItems");




		//WebElement popupwindow = Windriver.findElement(By.name("EMERALD Remote App Log-In"));
		boolean visible = Windriver.findElement(By.name("EMERALD Remote App Log-In")).isDisplayed();
		//WebElement loginpage = Windriver.findElementByAccessibilityId("kryptonPanel");


		if(visible==true)
		{
			Windriver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);
			Windriver2.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

		}
		else
		{
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByAccessibilityId("AutoLogInCheckBox").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("AutoLogin Disabled - Clicked on AutoLogin Button ");
		}





		/*File file1 = new File("C:\\Users\\blackbox\\AppData\\Local\\BlackBox\\EmeraldRA\\windows.config"); //Creation of File Descriptor for input file
		String[] words = null;  //Intialize the word Array
		FileReader fr = new FileReader(file1); //Creation of File Reader object
		BufferedReader br = new BufferedReader(fr); //Creation of BufferedReader object(it can be used to read data from any character stream)
		String s;
		String input = "pe, "; // Input word to be searched
		int count = 0; //Initialize the word to zero
		while((s=br.readLine())!=null)   //Reading Content from the file
		{
			words=s.split(" ");  //Split the word using space
			for (String word : words) 
			{
				if (word.equals(input))   //Search for the given word
				{
					count++;    //If Present increase the count by one
				}
			}
		}
		if(count!=0)  //Check for count not equal to zero
		{
			System.out.println("The given word is present for "+count+ " Times in the file");
		}
		else
		{
			System.out.println("The given word is not present in the file");
		}

		fr.close();*/






		/*

		//@Test(priority=10)//Connection windows size and position without auto connect &auto login-(1-8 VM & TX Connections)
		//public void Test10_Size_and_position_for_9_Connections() throws Exception
		{
			printTestDetails("Starting", "Test09_Connection_launch_in_3sec_AutoLogin_and_AutoConnect", "");
			Onedevices = devicePool.getAllDevices("devicePE.properties");
			System.out.println(Onedevices);
			SoftAssert soft = new SoftAssert();

			cleanUpLogin();
			ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 9, "Shared");
			userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
			cleanUpLogout();

			autologin.configFileEdit(Windriver, connectionName, RAusername, 0, 0, 1920, 1080);




		}
		 */








		/*//@Test(priority =9) //Automatically Logging into Remote App and Auto Connect
		//public void Test09_Connection_launch_in_3sec_AutoLogin_and_AutoConnect() throws Exception
		{
			printTestDetails("Starting", "Test09_Connection_launch_in_3sec_AutoLogin_and_AutoConnect", "");
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

				//Enabling AutoConnect and AutoLogin
				autologin.AutologinEnable(Windriver);
				autologin.relogin(Windriver, RAusername, RApassword);
				Thread.sleep(3000);
				autologin.AutoConnectEnable(Windriver);
				autologin.saveCurrentLayout(Windriver);
				Thread.sleep(3000);
				closeApp();
				Thread.sleep(10000);

				WindowsDriver Windriver3 = null;
				//Again Launching RA 
				DesiredCapabilities capabilities = new DesiredCapabilities();
				capabilities.setCapability("ms:waitForAppLaunch", 30);
				capabilities.setCapability("app", "C:\\Program Files (x86)\\BlackBox\\EmeraldRA.exe");
				capabilities.setCapability("platformName", "Windows");
				capabilities.setCapability("deviceName", "WindowsPC");
				Windriver3 = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);

				long start = System.currentTimeMillis();
				System.out.println("Current time before launching connection is - "+start);
				Thread.sleep(4000);
				new WebDriverWait(Windriver3, 60).until(ExpectedConditions.visibilityOf(Windriver3.findElementByClassName("wCloudBB")));

				long finish = System.currentTimeMillis();
				long totalTime = finish-start;
				System.out.println("Total Time Taken to launch the connection is  " + totalTime +"  milliseconds");

				if(totalTime<3000){
					Thread.sleep(30000);
					System.out.println("The connection launched within 3 seconds");
				}else 
				{
					Assert.fail("The connection is taking more than 3 seconds to launch");
					System.out.println("The connection is taking more than 3 seconds to launch");
				}


				Windriver3.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);

				//Root session (windows)
				DesiredCapabilities appCapabilities = new DesiredCapabilities();
				appCapabilities.setCapability("app", "Root");
				Windriver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);

				System.out.println("Current RA window is "+ Windriver.getWindowHandles().toArray()[0]); 
				Thread.sleep(50000);
				int connection1 = ramethods.activeConnectionsfromAPI(boxillaManager, AutomationUsername, AutomationPassword);
				System.out.println("Number of Active Connections before enabling AutoLogin and AutoConnect are : " + connection1);
				//soft.assertEquals(connection, connection1, "Connections are not Launched after enabling AutoLogin and AutoConnect, are not working ");
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
			}
		}
















	DesiredCapabilities capabilities = new DesiredCapabilities();
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

		 */


		/*String[] words = text.split(" ");

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





		/*Thread.sleep(15000);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		if(Windriver.findElementByAccessibilityId("connectionsPage").isEnabled())
		{
			Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			autologin.AutoLoginDisable(Windriver);
			autologin.AutoConnectDisable(Windriver);
			closeApp();
		}





		System.out.println("Starting Test");

		Onedevices = devicePool.getAllDevices("devicePE_SH.properties");
		System.out.println(Onedevices);

		cleanUpLogin();
		ArrayList connectionName = ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
		//ConnectionPage.CreateConnection(firedrive, Onedevices, 1, "Shared");
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
			Windriver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);

			try
			{
				Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);
				System.out.println("EmeraldRA Encrypt Launched SuccessFully");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Exception has occured in Encrypt setup part");
			}
			Actions action = new Actions(Windriver);
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

			Windriver.findElementByAccessibilityId("btnCopyLaunchConnection").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();

			//pasting in notepad

			DesiredCapabilities capabilities1 = new DesiredCapabilities();
			capabilities1.setCapability("app", "C:\\Windows\\System32\\notepad.exe");
			capabilities1.setCapability("platformName", "Windows");
			capabilities1.setCapability("deviceName", "WindowsPC");

			WindowsDriver Windriver1 = null ; 
			Thread.sleep(1000);
			Windriver1 = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities1);
			Windriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("current window is "+Windriver1.getWindowHandles().toArray()[0]);
			System.out.println("Notepad Launched SuccessFully");	

			Windriver1.switchTo().window((String)Windriver1.getWindowHandles().toArray()[0]);
			Windriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			Windriver1.findElementByName("Text Editor").click();
			action.sendKeys(Keys.chord(Keys.CONTROL,"o")).build().perform();

			action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();


			Windriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver1.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\Desktop\\Copy Paste\\Demo.txt");
			action.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
			Windriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			Windriver1.switchTo().window((String)Windriver1.getWindowHandles().toArray()[0]);
			Windriver1.findElementByName("Text Editor").click();
			Windriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			action.sendKeys(Keys.chord(Keys.CONTROL,"a")).build().perform();
			Windriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
			Windriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			action.sendKeys(Keys.chord(Keys.CONTROL, "v")).build().perform();

			StringBuilder text = new StringBuilder(Windriver.findElementByName("Text Editor").getText());
			System.out.println(text);
			if(text.indexOf(con) !=  -1 )
			{
				//text
			}
			{

			}



















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



		 */



















		/*RestAssured.useRelaxedHTTPSValidation();
		String gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
				.when().get("https://"+boxillaManager+"/bxa-api/time")
				.then().assertThat().statusCode(200)
				.extract().response().asString();
		System.out.println("Active connection status"+gerdetails);

		JsonPath js1 = new JsonPath(gerdetails);
		long Bxatime = js1.getLong("message.local_time");
		System.out.println("Local Time in Epoch Language is : " + Bxatime);


		String date = new java.text.SimpleDateFormat("dd/MMMM/yyyy HH:mm:ss").format(new java.util.Date (Bxatime*1000)); 

		System.out.println(date);
		 */























		/*//Blocked//@Test () //Remote app maximum limit to user
		public void Test06_CL0005a_Maximum_connectionLimit() throws Exception {
			printTestDetails("STARTING ", "Test06_CL0005a_Maximum_connectionLimit", "");

			Onedevices = devicePool.getAllDevices("Onedevice.properties");
			cleanUpLogin();
			userpage.createUser(firedrive,null,RAusername,RApassword,"General");
			SharedNames=ConnectionPage.Sharedconnection(firedrive,Onedevices,5,"shared");
			UserPage.Sharedconnectionassign(firedrive, RAusername, SharedNames);

			cleanUpLogout();
			int count=0;
			int connectionNumber=1;
			try {
				RAlogin(RAusername,RApassword);
				WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
				List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));

				for (WebElement connection : availableConnections) {
					System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
					connectionList.add(connection.getText());
					connectionNumber++;
				}

				for (String connectionName : SharedNames) {

					WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
					Actions a = new Actions(Windriver);
					count++;
					a.moveToElement(targetConnection).
					doubleClick().
					build().perform();


					if (count==5) {
						new WebDriverWait(firedrive, 60).until(ExpectedConditions.elementToBeClickable(Windriver.findElementByName("Maximum Number of Connections Reached")));
						WebElement windowsPopupOpenButton = Windriver.findElementByName("Maximum Number of Connections Reached");
						String text= windowsPopupOpenButton.getText();
						System.out.println("Alert text is "+text);
						Assert.assertTrue(text.equalsIgnoreCase("Maximum Number of Connections Reached"), "Maximum Number of Connections Reached Message has not been displayed ");
						break;
					}
					Thread.sleep(20000);
					System.out.println(connectionName+" has been launched");
					Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				}


				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				for (String connectionName : SharedNames) {
					Windriver.findElement(By.name(connectionName)).click();
					Windriver.findElement(By.name("Disconnect")).click();
					Thread.sleep(3000);
					System.out.println("connection "+connectionName+" is disconnected");
					Windriver.switchTo().window(Windriver.getWindowHandle());


				}
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				closeApp();
				cleanUpLogin();
				ConnectionPage.DeleteSharedConnection(firedrive, SharedNames);

				UserPage.DeleteUser(firedrive, RAusername);
				cleanUpLogout();
			} catch(Exception e) {
				e.printStackTrace();
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				closeApp();
				cleanUpLogin();
				Thread.sleep(2000);
				ConnectionPage.DeleteSharedConnection(firedrive, SharedNames);
				UserPage.DeleteUser(firedrive, RAusername);
				cleanUpLogout();

			}
		}

		//not_included//@Test //Log on to a TX managed by another boxilla
		public void Test27_SR0036_TX_of_Otherboxila() throws Exception {
			printTestDetails("STARTING ", "Test27_SR0036__TX_of_Otherboxila", "");

			ArrayList<String> viewList = new ArrayList<String>();
			WebDriverWait wait=new WebDriverWait(firedrive, 20);
			ArrayList<Device> DualHeadDevices = new ArrayList<Device>();
			DiscoveryMethods discoveryMethods = new DiscoveryMethods();	
			AppliancePool applian = new AppliancePool();
			ArrayList<String> connectionDualList = new ArrayList<String>();
			ArrayList<Device> remotedevice=applian.getAllDevices("Onedevice.properties");
			System.out.println(remotedevice);
			DualHeadDevices.add(remotedevice.get(0));
			//			cleanUpLogin();
			//			unManageDevice(firedrive,remotedevice);
			//			cleanUpLogout();
			//			DoubleLogin();
			//			for(Device deviceList : remotedevice) {
			//				System.out.println("Adding the device "+deviceList);
			//				discoveryMethods.addDeviceToBoxilla(firedrive, deviceList.getMac(), deviceList.getIpAddress(),
			//						deviceList.getGateway(),deviceList.getNetmask(), deviceList.getDeviceName(), 10);
			//				}
			//				System.out.println("*************Device is Managed***************");
			//				cleanUpLogout();	

			cleanUpLogin();
			ConnectionPage.createprivateconnections(firedrive, remotedevice);
			userpage.createUser(firedrive,remotedevice,RAusername,RApassword,"General");
			cleanUpLogout();



			RAlogin(RAusername,RApassword);
			//Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			//Thread.sleep(1500);
			WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
			//Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
			List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));
			for (WebElement connection : availableConnections) {
				connectionDualList.add(connection.getText());
			}

			for (String connectionName : connectionDualList) {
				Actions a = new Actions(Windriver);
				WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
				a.moveToElement(targetConnection).
				doubleClick().
				build().perform();
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				new WebDriverWait(firedrive, 60).until(ExpectedConditions.visibilityOfAllElements(Windriver.findElementByName("Unable to connect")));
				//				      System.out.println("connection named "+connectionName+" has been launched");
				//Thread.sleep(3000);
				WebElement windowsPopupOpenButton = Windriver.findElementByName("Unable to connect");
				String text= windowsPopupOpenButton.getText();
				System.out.println(text);
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);


			}

			//  closeApp();
			//  Thread.sleep(10000);
			Thread.sleep(2000);
			closeApp();
			cleanUpLogin();
			Thread.sleep(2000);
			ConnectionPage.DeleteConnection(firedrive, remotedevice);
			UserPage.DeleteUser(firedrive, RAusername);
			DoubleLogin();	
			unManageDevice(firedrive,remotedevice);
			cleanUpLogout();
			cleanUpLogin();
			for(Device deviceList : remotedevice) {
				System.out.println("Adding the device "+deviceList);
				discoveryMethods.addDeviceToBoxilla(firedrive, deviceList.getMac(), deviceList.getIpAddress(),
						deviceList.getGateway(),deviceList.getNetmask(), deviceList.getDeviceName(), 10);
			}
			System.out.println("*************Device is Managed***************");
			cleanUpLogout();	

		}

		//not_included//@Test //Launch 4 shared connection at a time
		public void Test26_CL0006b_4connectionSupport_inRA() throws Exception {
			printTestDetails("STARTING ", "Test26_CL0006b_4connectionSupport_inRA", "");
			SoftAssert softAssert = new SoftAssert();
			Onedevices = devicePool.getAllDevices("Onedevice.properties");
			cleanUpLogin();
			userpage.createUser(firedrive,null,RAusername,RApassword,"General");
			SharedNames=ConnectionPage.Sharedconnection(firedrive,Onedevices,4,"shared");
			UserPage.Sharedconnectionassign(firedrive, RAusername, SharedNames);

			cleanUpLogout();
			int count=0;
			int connectionNumber=1;
			try {
				RAlogin(RAusername,RApassword);
				WebElement availableConnectionsList = getElement("availableConnectionsWinListBox");
				List<WebElement> availableConnections = availableConnectionsList.findElements(By.xpath("//ListItem"));

				for (WebElement connection : availableConnections) {
					System.out.println("connections number  "+connectionNumber+" is "+connection.getText());
					connectionList.add(connection.getText());
					connectionNumber++;
				}

				for (String connectionName : SharedNames) {

					WebElement targetConnection = availableConnectionsList.findElement(By.name(connectionName));
					Actions a = new Actions(Windriver);
					count++;
					a.moveToElement(targetConnection).
					doubleClick().
					build().perform();
					Thread.sleep(20000);
					System.out.println(connectionName+" has been launched");
					Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				}


				RestAssured.useRelaxedHTTPSValidation();
				String gerdetails = RestAssured.given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers("Content-Type", "application/json", "Accept","application/json")
						.when().get("https://"+boxillaManager+"/bxa-api/connections/kvm/active")
						.then().assertThat().statusCode(200)
						.extract().response().asString();
				System.out.println("Active connection status"+gerdetails);

				System.out.println("********************checking the connection status  ******************");
				JsonPath js = new JsonPath(gerdetails);
				int	countconnection=js.getInt("message.active_connections.size()");
				System.out.println("Number of Active connections are "+countconnection);
				System.out.println("Number of expected active connections to be "+connectionList.size());
				softAssert.assertEquals(countconnection,connectionList.size()," Number of active connection didn't match with the number of connections before changing credentials");	

				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				for (String connectionName : SharedNames) {
					Windriver.findElement(By.name(connectionName)).click();
					Windriver.findElement(By.name("Disconnect")).click();
					Thread.sleep(3000);
					System.out.println("connection "+connectionName+" is disconnected");
					Windriver.switchTo().window(Windriver.getWindowHandle());
				}
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				closeApp();
				cleanUpLogin();		
				UserPage.DeleteUser(firedrive, RAusername);
				cleanUpLogout();
			} catch(Exception e) {
				e.printStackTrace();
				Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
				closeApp();
				cleanUpLogin();
				Thread.sleep(2000);
				UserPage.DeleteUser(firedrive, RAusername);
				cleanUpLogout();

			}
		}

*/





		/*Actions action = new Actions(firedrive);
//		action.sendKeys(Keys.chord(Keys.CONTROL, Keys.ALT, Keys.DELETE)).perform();

		//RA_Methods.cmdsetup(Windriver);

		ramethods.teratermactions(Windriver, "10.231.128.117", deviceUserName, devicePassword);


		ProfilesIni prof = new ProfilesIni();				
				FirefoxProfile ffProfile= prof.getProfile ("myProfile");
		 */






		/*profile = webdriver.FirefoxProfile();
				profile.accept_untrusted_certs = True;

				driver = webdriver.Firefox(firefox_profile=profile);
				driver.get("https://cacert.org/");

				driver.close();*/

		/*Ssh ssh = new Ssh(deviceUserName, devicePassword, "10.231.128.117");
				ssh.loginToServer();

				ssh.sendCommand("cd /usr/local");
				Thread.sleep(3000);
				ssh.sendCommand("rm output.txt");
				//	Thread.sleep(120000);
				String store = ssh.sendCommand("tail syslog.log | grep fps | awk '{print $10,$11,$13}' > output.txt");
				//Thread.sleep(100000);
				System.out.println(store);
				String fps = ssh.sendCommand("cat output.txt");
		 */


		/*JSch jsch = new JSch();
				Channel channel = null;
				ChannelSftp sftp = null;
				Session session = jsch.getSession("root", "10.231.128.117", 22);
				session.setPassword("barrow1admin_12");
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				try {
					session.connect();
					System.out.println("Successfully connected to " + "10.231.128.117");

					Channel channel1 = session.openChannel("exec");
					InputStream ini = channel1.getInputStream();
					((ChannelExec) channel1).setCommand("cd /usr/local");
					((ChannelExec) channel1).setCommand("rm output.txt");
					((ChannelExec) channel1).setCommand("tail syslog.log | grep fps | awk '{print $10,$11,$13}' > output.txt");
					((ChannelExec) channel1).setCommand("cat Output.txt");
					//channel1.getExitStatus();
					//((ChannelExec) channel1).setErrStream(System.err);

					//channel.getExitStatus();
					//((ChannelExec) channel1).setErrStream(System.err);
					//channel1.getExitStatus();
					//((ChannelExec) channel1).setErrStream(System.err);
					BufferedReader reader = new BufferedReader(new InputStreamReader(ini));
					String line1;
					line1=reader.readLine();
					System.out.println(line1);
					while((line1=reader.readLine()) != null)
					{
						System.out.println(line1);
					}



					channel = session.openChannel("sftp");
					channel.connect();
					sftp = (ChannelSftp)channel;
					sftp.get("/usr/local/output.txt","C:\\Users\\blackbox\\output.txt");

		 */

		/*Ssh ssh = new Ssh("root", "barrow1admin_12", "10.231.128.96");
		ssh.loginToServer();
		ssh.sendCommand("cat /VERSION");
		ssh.sendCommand("/reboot");
		String store = ssh.sendCommand("/tail syslog.log | grep fps | awk '{print $10,$11,$13}' > output.txt");
		Thread.sleep(40000);
		System.out.println(store);
		String fps = ssh.sendCommand("cat output.txt");*/
		/*Ssh ssh = new Ssh("pcadmin", "Blackbox@123", "10.231.128.94");
		ssh.loginToServer();
		ssh.sendCommand("shutdown /r");
		String store = ssh.sendCommand("tail syslog.log | grep fps | awk '{print $10,$11,$13}' > output.txt");
		Thread.sleep(40000);
		System.out.println(store);
		String fps = ssh.sendCommand("cat output.txt");*/



		/*float fpssum =0 ; float dfpssum = 0; 
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
		System.out.println("The Dropped FPS of launched connection is : " + droppedfps);*/



		/*Ssh ssh = new Ssh(deviceUserName, devicePassword, "10.231.128.117");
		ssh.loginToServer();
		ssh.sendCommand("cd /usr/local");
		String store = ssh.sendCommand("tail syslog.log | grep fps | awk '{print $10,$11,$13}' > output.txt");
		Thread.sleep(40000);
		System.out.println(store);
		String fps = ssh.sendCommand("cat output.txt");
		 */



		//Set<String> getWindowHandles = RASession.getWindowHandles();
		//System.out.println(getWindowHandles);
		//System.out.println(getWindowHandles.toArray());


		/*connectionWindow.sendKeys(Keys.CONTROL);
		connectionWindow.sendKeys( Keys.ALT);
		connectionWindow.sendKeys(Keys.END);
		Thread.sleep(10000);
		connectionWindow.sendKeys(Keys.ESCAPE);
		Thread.sleep(10000);


		connectionWindow.sendKeys(Keys.CONTROL);
		connectionWindow.sendKeys( Keys.ALT);
		connectionWindow.sendKeys(Keys.DELETE);
		Thread.sleep(10000);
		connectionWindow.sendKeys(Keys.ESCAPE);
		Thread.sleep(10000);*/


		//action.keyDown(Keys.CONTROL).keyDown(Keys.ALT).keyDown(Keys.END).build().perform();
		//Thread.sleep(2000);
		//action.keyUp(connectionWindow, Keys.CONTROL).keyUp(Keys.ALT).keyUp(Keys.END).build().perform();
		//action.sendKeys(maximise, Keys.CONTROL,Keys.ALT,Keys.END).perform();
		//action.sendKeys(Keys.chord(Keys.CONTROL, Keys.ALT, Keys.DELETE)).perform();

		/*action.sendKeys(Keys.CONTROL, Keys.ALT, Keys.DELETE).build().perform();
		log.info("Ctrl+Alt+Del gives Security options of Local PC");
		Thread.sleep(10000);
		action.sendKeys(Keys.ESCAPE).build().perform();
		Thread.sleep(5000);*/

		/*action.sendKeys(Keys.chord(Keys.CONTROL, Keys.ALT, Keys.END)).build().perform();
		//action.sendKeys(Keys.chord(Keys.CONTROL, Keys.ALT, Keys.END)).build().perform();
		log.info("Ctrl+Alt+End gives Security options of Transmitter connection");
		Thread.sleep(10000);
		//action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
		Thread.sleep(5000);*/	

	}




	/*

//Marking it for Enhanced  @Test(priority = 22)//The Administrator shall be able to force an active connection to close on any managed device.
public void Test22_EM0019_Close_RA_Launched_Connection_from_Boxilla() throws Exception
{
	printTestDetails("Starting", "Test22_EM0019_Close_RA_Launched_Connection_from_Boxilla", "");
	SoftAssert soft = new SoftAssert();
	Onedevices = devicePool.getAllDevices("deviceSE_SH.properties");
	System.out.println(Onedevices);

	cleanUpLogin();
	ConnectionPage.CreateConnection(firedrive, Onedevices, 2, "Shared");
	userpage.createUser(firedrive, Onedevices, RAusername, RApassword, "General");
	cleanUpLogout();

	RAlogin(RAusername, RApassword);
	ramethods.RAConnectionLaunch(Windriver);
	Thread.sleep(40000);

	int a = ramethods.activeConnectionsfromAPI(boxillaManager,RAusername,RApassword);
	System.out.println("Active Connections before Disconnecting connection  "+a);
	soft.assertEquals(a, 2," Active connection expected to be 2 but found "+a);

	cleanUpLogin();
	ConnectionPage.BreakboxillaConnection(firedrive);

	int b = ramethods.activeConnectionsfromAPI(boxillaManager,RAusername,RApassword);
	System.out.println("Active Connections After Disconnecting connection  "+b);
	soft.assertEquals(b, 0," Active connection expected to be 0 but found "+b);

	closeApp();
	UserPage.DeleteUser(firedrive, RAusername);
	cleanUpLogout();
	soft.assertAll();
}



	 */





	/*
// same as test case 11 in first phase
//@Test(priority=8) //Log onto a TX appliance managed by another boxilla
public void Test8_CL0004_Managed_connec_display_inRA() throws Exception
{
	printTestDetails("Starting", "Test8_CL0004_Managed_connec_display_inRA", "");

	ArrayList<Device> device = new ArrayList<Device>();
	device = devicePool.getAllDevices("device.properties");
	SoftAssert softassert = new SoftAssert();

	cleanUpLogin();
	ArrayList createconnection = ConnectionPage.CreateConnection(firedrive, device, 5, "Shared");
	userpage.createUser(firedrive, device,RAusername,RApassword,"General");
	//userpage.Sharedconnectionassign(firedrive, RAusername, createconnection);
	cleanUpLogout();
	Thread.sleep(5000);

	RAlogin(RAusername, RApassword);
	int con = 0;
	WebElement allConnectionList = getElement("availableConnectionsWinListBox");
	List<WebElement> availableConnection = allConnectionList.findElements(By.xpath("//ListItem"));
	for(WebElement connectionname : availableConnection)
	{
		System.out.println("Connection Name is " + connectionname.getText());
		con++;
	}
	log.info("Validating all created connections are displayed in RA");

	/// assert it
		softassert.assertEquals(con, createconnection.size(),"  All created connections are not Displayed in RemoteApp");

	closeApp();

	cleanUpLogin();
	Thread.sleep(2000);
	UserPage.DeleteUser(firedrive, RAusername);
	cleanUpLogout();
}*/
}



