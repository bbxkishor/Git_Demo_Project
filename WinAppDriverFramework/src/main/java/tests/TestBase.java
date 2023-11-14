package tests;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.log4testng.Logger;

import tests.Ssh;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/*import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;*/

import extra.RESTStatistics;
import io.appium.java_client.windows.WindowsDriver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import methods.AppliancePool;
import methods.AutologinAutoConnect;
import methods.CLIMethods;
import methods.Device;
import methods.Devices;
import methods.DiscoveryMethods;
import methods.RA_Methods;
import methods.SeleniumActions;
import methods.Switch;
import methods.SystemAll;
import methods.SystemMethods;
import methods.UserMethods;
import pages.BoxillaHeaders;
import pages.ClusterPage;
import pages.ConnectionPage;
import pages.LandingPage;
import pages.UserPage;
import pages.boxillaElements;

public class TestBase extends RESTStatistics {


	public static WindowsDriver Windriver;
	public static WindowsDriver Windriver2=null;
	public static WebDriver firedrive;
	public static Properties prop = new Properties();
	private Properties deviceProperties = new Properties();
	protected static Device txSingle, rxSingle, txDual, rxDual, txEmerald, rxEmerald, shTx, dhTx, shRx, dhRx;
	public static int waitTime=30;
	Method method = null;
	public static String singleTxName;
	public static String singleRxName;
	protected static String txIp = prop.getProperty("txIP");
	protected static String txIpDual = prop.getProperty("txIPDual");
	public static String dualTxName;
	protected static String adIp = "10.231.128.7";
	protected static String adPort = "389";
	protected static String adDomain = "tpstest.blr";
	protected static String adUsername = "Administrator";
	protected static String adPassword = "Tpstest@123";	
	protected static String adUser = "kishorAD";
	public static String boxillaManager;
	public static String boxillaManager2;
	public static String RAusername;
	public static String RApassword;
	public static String AutomationUsername;
	public static String AutomationPassword;
	public static String boxillaUsername;
	public static String boxillaPassword;
	public static String deviceUserName, devicePassword;
	protected AppliancePool devicePool = new AppliancePool();
	boolean fpsstatus=true;
	public static String url;
	private static int testCounter;
	private static long splitTime;
	private static long startTime;
	public static String VMUsername;
	public static String VMPassword;
	public static String VMDomainName;
	public static String VMIp;

	public static String VIP;
	public static String MasterClusterID;
	public static String MasterNodeID;
	public static String MasterNodeName;
	public static String StandbyNodeID;
	public static String StandbyNodeName;

	public static String RecieverName;

	//For Size and Position declaring Values
	protected static int x_coordiate = 200;
	protected static int y_coordiate = 200;
	protected static int width_con = 1280;
	protected static int height_con = 800;

	ArrayList<String> connectionList = new ArrayList<String>();
	ArrayList<Device> devices;
	ArrayList<Device> Onedevices;
	ArrayList<String> SharedNames;
	UserPage userpage = new UserPage();
	ClusterPage clusterpage = new ClusterPage();
	RA_Methods ramethods = new RA_Methods();
	CLIMethods climethods = new CLIMethods();
	AutologinAutoConnect autologin = new AutologinAutoConnect();

	final static Logger log = Logger.getLogger(TestBase.class);

	@BeforeSuite
	public void login() throws InterruptedException {
		loadProperties();
		devices = devicePool.getAllDevices("device.properties");
		//Assigning test.properties parameters
		boxillaUsername = prop.getProperty("boxillaUsername");
		boxillaPassword = prop.getProperty("boxillaPassword");
		RAusername = prop.getProperty("RAusername");
		RApassword = prop.getProperty("RAusername");
		AutomationUsername = prop.getProperty("AutomationUsername");
		AutomationPassword = prop.getProperty("AutomationPassword");
		deviceUserName = prop.getProperty("deviceUserName");
		devicePassword = prop.getProperty("devicePassword");
		boxillaManager=prop.getProperty("boxillaManager");
		boxillaManager2=prop.getProperty("boxillaManager2");
		VMUsername=prop.getProperty("VMUsername");
		VMPassword=prop.getProperty("VMPassword");
		VMDomainName=prop.getProperty("VMDomainName");
		VMIp=prop.getProperty("VMIp");
		MasterClusterID=prop.getProperty("MasterClusterID");
		VIP=prop.getProperty("VIP");
		MasterNodeID=prop.getProperty("MasterNodeID");
		MasterNodeName=prop.getProperty("MasterNodeName");
		StandbyNodeID=prop.getProperty("StandbyNodeID");
		StandbyNodeName=prop.getProperty("StandbyNodeName");
		RecieverName=prop.getProperty("RecieverName");


		System.out.println("loaded username is "+boxillaUsername);
		System.out.println("loaded password is "+boxillaPassword);
		System.out.println("VMIP is  "+VMIp);

		startTime = System.currentTimeMillis();
		Thread.sleep(2000);

		try {			
			System.out.println("Attempting to manage devices");
			System.out.println("BoxillaManager is "+boxillaManager);
			cleanUpLogin();
			enableNorthboundAPI(firedrive);
			//Managedevices();
			cleanUpLogout(); 
		}
		catch(Exception e) {
			Utilities.captureScreenShot(firedrive, this.getClass().getName() + "_beforeClass", "Before Class");
			e.printStackTrace();
			cleanUpLogout();
		}
	}


	public class Reboot {
		String [] device_names;
	}

	public interface Callback {
		public void cleanUp();
	}

	public void cleanUpLogin() throws Exception {

		String url = "https://"+boxillaManager+"/";
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\blackbox\\eclipse-workspace\\geckodriver.exe");
		//	 	System.setProperty("webdriver.chrome.driver", "C:\\Users\\\\eclipse-workspace\\WinAppDriverFramework\\chromedriver.exe");
		//		 WebDriverManager.chromedriver().setup();	
		//		 WebDriver chromedriver = new ChromeDriver();
		//		 	DesiredCapabilities caps = new DesiredCapabilities();
		//			caps.setCapability("acceptInsecureCerts", true);
		//			chromedriver.get(url);

		//			WebDriverManager.firefoxdriver().setup();

		//Manage firefox specific settings in a way that geckodriver can understand
		FirefoxOptions ffoptions = new FirefoxOptions();

		firedrive = new FirefoxDriver(ffoptions);
		DesiredCapabilities handleError = new DesiredCapabilities();
		handleError.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		handleError.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 			
		ffoptions.merge(handleError);
		firedrive.get(url);

		boxillaElements.username(firedrive).sendKeys(boxillaUsername);
		boxillaElements.password(firedrive).sendKeys(boxillaPassword);
		boxillaElements.Login(firedrive).click();
		System.out.println("Logged In to boxilla");
		Thread.sleep(3000);
	}

	public void DoubleLogin() throws Exception {

		String url = "https://"+boxillaManager2+"/";
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\blackbox\\eclipse-workspace\\geckodriver.exe");


		FirefoxOptions ffoptions = new FirefoxOptions();

		firedrive = new FirefoxDriver(ffoptions);

		DesiredCapabilities handleError = new DesiredCapabilities();
		handleError.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		handleError.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		ffoptions.merge(handleError);
		firedrive.get(url);


		boxillaElements.username(firedrive).sendKeys(boxillaUsername);
		boxillaElements.password(firedrive).sendKeys(boxillaPassword);
		boxillaElements.Login(firedrive).click();
		System.out.println("Logged In to  boxilla");
		Thread.sleep(3000);

	}
	private void getApplianceVersion(String deviceIp) {
		Ssh ssh = new Ssh(deviceUserName, devicePassword, deviceIp);
		ssh.loginToServer();
		String applianceBuild = ssh.sendCommand("cat /VERSION");
		System.out.println("removing all logs before starting tests");
		ssh.sendCommand("rm /usr/local/syslog.log*");
		ssh.disconnect();
		System.out.println("Appliance build: " + applianceBuild);
	}



	public void setup() throws Exception {

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("ms:waitForAppLaunch", 10);
		capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");

		try {
			Thread.sleep(3000);

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


	}


	public void setup1() {

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("ms:waitForAppLaunch", 30);
		capabilities.setCapability("app", "C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");


		try {
			Thread.sleep(3000);

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


	}

	public WebDriver getdriver() {
		return firedrive;
	}

	public void CleanUp(){

		Windriver.quit();
		if(Windriver.findElementByName("Cancel").isDisplayed()) {
			Windriver.findElementByName("Cancel").click();
		}

	}

	public void RAlogin(String username, String Password) throws Exception {
		setup(); // Checks whether Autologin is Enabled, if Enabled it will Disable it
		// closes the current RA window and relaunches the RA.
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
				closeApp();
				Thread.sleep(6000);
				setup();
			}

		}catch(Exception n)
		{
			System.out.println(" ");
		}
		//hold focus on the active window
		System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);


		//	Windriver.switchTo().frame(0);
		WebElement loginButton = getElement("logInButton");
		getElement("userNameTextBox").sendKeys(username);
		Thread.sleep(3000);
		System.out.println("Username Entered");
		getElement("passwordTextBox").sendKeys(Password);
		Thread.sleep(3000);
		System.out.println("Password Entered");
		loginButton.click();
		Thread.sleep(20000);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(3000);
	}
	public void RAloginforAutologin(String username, String Password) throws Exception {
		setup();
		Thread.sleep(3000);
		// Checks whether Autologin is Enabled, if Enabled it will Disable it,
		// closes the current RA window and relaunches the RA.
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
				closeApp();
				Thread.sleep(6000);
				setup();
			}

		}catch(Exception n)
		{
			System.out.println(" ");
		}
		//hold focus on the active window
		System.out.println("current window is "+Windriver.getWindowHandles().toArray()[0]);


		//	Windriver.switchTo().frame(0);
		WebElement loginButton = getElement("logInButton");
		getElement("userNameTextBox").sendKeys(username);
		Thread.sleep(3000);
		System.out.println("Username Entered");
		getElement("passwordTextBox").sendKeys(Password);
		Thread.sleep(3000);
		System.out.println("Password Entered");
		loginButton.click();
		Thread.sleep(50000);
		Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		Thread.sleep(3000);
	}

	public void closeRemoteApp() {
		//	Windriver.switchTo().window((String)Windriver.getWindowHandles().toArray()[0]);
		//	if((Windriver.findElementById("kryptonPanel").isDisplayed())){
		//		getElement("closeLogInScreen").click();
		//	}
		if((getElement("closeLogInScreen").isDisplayed())){
			getElement("closeLogInScreen").click();
		}
		if(getElement("menuLabel").isDisplayed()) {
			getElement("menuLabel").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			Windriver.findElementByName("Close").click();
			Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		}
		System.out.println("RemoteApp Closed");
		Windriver.quit();
	}

	public void closeApp() {
		System.out.println("Closing RemoteApp....");
		Windriver.findElementByAccessibilityId("menuLabel").click();
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Windriver.findElementByName("Close").click();
		System.out.println("RemoteApp Closed");
		Windriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		Windriver.quit();
	}

	public WebElement getElement(String name) {

		return Windriver.findElementByAccessibilityId(name);
	}


	public List<?> getElementId(String name) {
		System.out.println(Windriver.findElementByAccessibilityId(name));
		return Windriver.findElementsByAccessibilityId(name);
	}
	/**
	 * Will load the property file into memory for use in test cases
	 */
	public void loadProperties() {
		System.out.println("Loading properties");
		try {
			InputStream in = new FileInputStream("C:\\Users\\blackbox\\eclipse-workspace\\WinAppDriverFramework\\test.properties");
			prop.load(in);
			in.close();
			System.out.println("Properties loaded successfully");
		} catch (IOException e) {
			System.out.println("Properties file failed to load");

		}
	}

	/**
	 * 
	 * @return the property file object
	 */
	public Properties getProp() {
		return prop;
	}



	public void Managedevices() throws InterruptedException {

		System.out.println("Attempting to manage devices for RemoteApp");	
		DiscoveryMethods discoveryMethods = new DiscoveryMethods();		

		for(Device deviceList : devicePool.allDevices()) {
			System.out.println("Adding the device "+deviceList);
			discoveryMethods.addDeviceToBoxilla(firedrive, deviceList.getMac(), deviceList.getIpAddress(),
					deviceList.getGateway(),deviceList.getNetmask(), deviceList.getDeviceName(), 10);
		}
		System.out.println("*************All Devices are Managed***************");

	}

	public void timer(WebDriver driver) throws InterruptedException { // Method for thread sleep
		Thread.sleep(2000);
	}

	public void unManageDevice(WebDriver driver, ArrayList<Device> dualHeadDevices) throws InterruptedException {
		for (Device ipaddress:dualHeadDevices) {
			navigateToOptions(driver, ipaddress.getIpAddress());
			timer(driver);
			SeleniumActions.seleniumClick(driver, Devices.unManageTab);
			System.out.println("UnManage Device -  Clicked on Unmanage Tab");
			Alert alert = driver.switchTo().alert();
			alert.accept();
			int counter = 0;
			System.out.println("Waiting for spinner to appear.");
			new WebDriverWait(driver, 60).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
			System.out.println("The spinner has appeared, waiting for spinner to disappear.");
			new WebDriverWait(driver, 60).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
			Thread.sleep(3000);
			SeleniumActions.seleniumSendKeys(driver, Devices.deviceStatusSearchBox, ipaddress.getDeviceName());
			System.out.println("UnManage Device - Device name entered in search box");
			timer(driver);
			String deviceApplianceTable = SeleniumActions.seleniumGetText(driver, Devices.applianceTable);
			Assert.assertFalse(deviceApplianceTable.contains(ipaddress.getDeviceName()),
					"Device appliance table did not contain: " + dualHeadDevices + ", actual text: " + dualHeadDevices);
		}
	}
	public void navigateToOptions(WebDriver driver, String ipAddress) throws InterruptedException {
		checkDeviceOnline(driver, ipAddress);
		timer(driver);
		if (SeleniumActions.seleniumGetText(driver, Devices.applianceTable).contains(ipAddress)) {
			SeleniumActions.seleniumClick(driver, Devices.breadCrumbBtn);
			System.out.println("Devices > Status > Options - Clicked on breadcrumb");
		} else {
			System.out.println("Devices > Status > Options - Searched device not found");
			throw new SkipException("***** Searched device - " + ipAddress + " not found *****");
		}

	}

	public void checkDeviceOnline(WebDriver driver, String ipAddress) throws InterruptedException {
		System.out.println("Attempting to check if device with IP address " + ipAddress + " is online");
		timer(driver);
		LandingPage.devicesTab(driver).click();
		new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(LandingPage.devicesStatus(driver)));
		LandingPage.devicesStatus(driver).click();
		System.out.println("Devices > Status > Options - Clicked on Status tab");
		timer(driver);
		SeleniumActions.seleniumSendKeys(driver, Devices.deviceStatusSearchBox, ipAddress);
		//check if device is online
		int timer = 0;
		int limit = 12;			//12 iterations of 5 seconds = 1 minute
		while(timer  <= limit) {
			System.out.println("Checking if device is online");
			String isOnline = SeleniumActions.seleniumGetText(driver, Devices.applianceTable);
			System.out.println("Is Online:" + isOnline);
			if(SeleniumActions.seleniumGetText(driver, Devices.applianceTable).contains("OnLine")) {
				System.out.println("Device is online");
				break;
			}else if(timer < limit) {
				timer++;
				System.out.println("Device is offline. Rechecking " + timer);
				driver.navigate().refresh();
				Thread.sleep(5000);
			}else if (timer == limit) {
				Assert.assertTrue(1 == 0, "Device is not online");
			}
		}
		System.out.println("Successfully checked if device is online");
	}



	public static int getWaitTime() {
		return waitTime;
	}


	public void cleanUpLogout() {
		try {
			//			Thread.sleep(1000);
			//			firedrive.getCurrentUrl();
			//			firedrive.get(url);
			//			Thread.sleep(2000);
			LandingPage.logoutDropdown(firedrive).click();
			Thread.sleep(2000);

			Actions action = new Actions(firedrive);
			action.moveToElement(LandingPage.logoutbtn(firedrive)).build().perform();
			Thread.sleep(3000);

			LandingPage.logoutbtn(firedrive).click();
			System.out.println("Logged out of Boxilla");
			Thread.sleep(2000);
			firedrive.quit();
			Thread.sleep(2000);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Logged out of Boxilla"
					+ "");
			firedrive.quit();

		}
	}



	public void enableNorthboundAPI(WebDriver driver) throws InterruptedException {
		navigateToSystemSettings(driver);
		Thread.sleep(2000);
		SeleniumActions.seleniumClick(driver, SystemAll.restApiTab);
		Thread.sleep(2000);
		boolean isOff = SeleniumActions.seleniumIsDisplayed(driver, SystemAll.restApiSwitchOff);
		System.out.println("Is off:"  + isOff);
		if(isOff) {
			System.out.println("Northbound API is disabled. Enabling");
			SeleniumActions.seleniumClick(driver, SystemAll.restApiSwitchOff);
		}else {
			System.out.println("Northbound API is already enabled. Doing nothing");
		}
	}


	public void navigateToSystemSettings(WebDriver driver) throws InterruptedException {
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		LandingPage.systemTab(driver).click();
		System.out.println("System Dropdown clicked");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LandingPage.systemSettings(driver).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}


	@AfterSuite
	public void closeoff() throws Exception {

		String url = "https://"+boxillaManager+"/";
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\blackbox\\eclipse-workspace\\geckodriver.exe");
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("acceptInsecureCerts", true);
		firedrive = new FirefoxDriver(caps);
		firedrive.get(url);
		boxillaElements.username(firedrive).sendKeys(boxillaUsername);
		boxillaElements.password(firedrive).sendKeys(boxillaPassword);
		boxillaElements.Login(firedrive).click();
		devices = devicePool.getAllDevices("device.properties");
		//				System.out.println("Devices are "+devicePool.allDevices());
		//				ConnectionPage.DeleteConnection(firedrive,devices);
		//				UserPage.DeleteUser(firedrive,RAusername);
		//unManageDevice(firedrive,devices);
		//				DeleteConnection();
		cleanUpLogout();
		PrintRestStatistics();
		//firedrive.close();


		//To get the reports
		//extent.flush();
	}



	@AfterMethod(alwaysRun = true)
	public void logout(ITestResult result) throws InterruptedException {

		System.out.println("In log out method");
		// Taking screen shot on failure
		//String url = "https://" + boxillaManager + "/";
		String results = "";
		//print result
		if(ITestResult.FAILURE == result.getStatus())
			results = "FAIL";

		if(ITestResult.SKIP == result.getStatus())
			results = "SKIP";

		if(ITestResult.SUCCESS == result.getStatus())
			results = "PASS";

		if (ITestResult.FAILURE == result.getStatus() || ITestResult.SKIP == result.getStatus()) {
			Throwable failReason = result.getThrowable();
			System.out.println("FAIL REASON:" + failReason.toString());
			String screenShotName = result.getName() + Utilities.getDateTimeStamp();
			Utilities.captureScreenShot(firedrive, screenShotName, result.getName());
			try {
				String gifName = "";
				if(ITestResult.SKIP == result.getStatus()) {
					gifName = result.getName() + "_skip";
				}else {
					gifName = result.getName() + "_fail";
				}
				//					 List<JavaScriptError> jsErrors = JavaScriptError.readErrors(firedrive); 
				//					System.out.println("************* JAVA SCRIPT ERRORS **************");
				//					for(JavaScriptError e : jsErrors) {
				//						System.out.println(e.getErrorMessage() + "Line Number:" + e.getLineNumber());
				//						
				//					}
				System.out.println("*************** END JAVA SRIPT ERRORS *************");

				//					GifSequenceWriter.createGif(SeleniumActions.screenshotList, gifName);
				//					clearGif();
				//				Utilities.captureLog(boxillaManager, boxillaUsername, boxillaPassword,
				//						 "./test-output/Screenshots/LOG_" + result.getName() + Utilities.getDateTimeStamp() + ".txt");
			}catch(Exception e) {
				System.out.println("Error when trying to capture log file. Catching error and continuing");
				e.printStackTrace();
			}

			//collectLogs(result);
		}
		try {

			firedrive.get(url);

			LandingPage.logoutDropdown(firedrive).click();

			LandingPage.logoutbtn(firedrive).click();
			firedrive.quit();


		} catch (Exception e) {
			// TODO: handle exception
			firedrive.quit();
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Regression running for : " + getTimeFromMilliSeconds(duration));
		long singleTestTime = endTime - splitTime;
		System.out.println(result.getName() + " took : " + getTimeFromMilliSeconds(singleTestTime));

		printTestDetails("FINISHING", result.getName(), results);
		System.out.println("Tests Completed:" + ++testCounter);
	}

	public void printSuitetDetails(boolean end) {
		String text = "";
		if(end) {
			text = "FINISHING";
		}else {
			text = "STARTING";
		}
		System.out.println(System.getProperty("line.separator"));
		System.out.println(System.getProperty("line.separator"));
		System.out.println("***************************************************************************************");
		System.out.println("*                                                                                     *");
		System.out.println("                         " + text + " SUITE " + this.getClass().getSimpleName());
		System.out.println("*                                                                                     *");
		System.out.println("***************************************************************************************");
		System.out.println(System.getProperty("line.separator"));
		System.out.println(System.getProperty("line.separator"));
	}

	/**
	 * Utility method used to bring the test details
	 * @param end 
	 * @param testName
	 * @param result
	 */
	public void printTestDetails(String end, String testName, String result) {
		System.out.println(System.getProperty("line.separator"));
		System.out.println(System.getProperty("line.separator"));
		System.out.println("***************************************************************************************");
		System.out.println("                         " + end + " TEST " + testName + ":" + result);
		System.out.println("***************************************************************************************");
		System.out.println(System.getProperty("line.separator"));
		System.out.println(System.getProperty("line.separator"));
	}

	public String getTimeFromMilliSeconds(long time) {
		return new SimpleDateFormat("mm:ss").format(new Date(time));

	}


	@BeforeMethod
	public void DeleteConnection() throws InterruptedException {     

		try {
			RestAssured.useRelaxedHTTPSValidation();

			String response = given().auth().preemptive().basic(AutomationUsername, AutomationPassword).headers(BoxillaHeaders.getBoxillaHeaders())
					.when().contentType(ContentType.JSON)
					.delete("https://" + boxillaManager + "/bxa-api/connections/kvm/all")
					.then().extract().response().asString();  


			System.out.println("Connection Delete status"+response);

			Thread.sleep(2000);
			if(response.contains("200"))
			{System.out.println(" ");}
			else{
				Thread.sleep(2000);
				cleanUpLogin();
				ConnectionPage.BreakboxillaConnection(firedrive);
				cleanUpLogout();
			} 
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		splitTime = System.currentTimeMillis();
	}
	public void createAndValidateADUser() throws Exception
	{
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


		//String username = "kishorAD";
		UserMethods.addUserAD(firedrive, adUser);
		//wait for AD to sync
		Thread.sleep(60000);
		String status = UserMethods.getActiveUserStatus(firedrive, adUser);
		log.info("Checking username");
		Assert.assertTrue(status.contains(adUser), "Status did not contain username. Excepted:" + adUser + " Actual:" + status );
		log.info("Checking Authorized by");
		Assert.assertTrue(status.contains("Active Directory"), "Authorized by did not contain Active Directory. Actual:" + status);
		log.info("Checking Domain");
		Assert.assertTrue(status.contains(adDomain),"Status did not contain domain. Expected:" + adDomain + " actual:" + status);
		log.info("Checking AD status");
		Assert.assertTrue(status.contains("OU Status"), "Status did not contain OD Status. Expected: ADtrue, actual: " + status );
		Thread.sleep(3000);
		cleanUpLogout();
	}



}


