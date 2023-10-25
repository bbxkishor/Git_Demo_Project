package methods;

import static io.restassured.RestAssured.given; 

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
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.asserts.SoftAssert;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.appium.java_client.windows.WindowsDriver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import pages.BoxillaHeaders;
import pages.LandingPage;
import tests.Ssh;
import tests.TestBase;


public class RA_Methods {
	ArrayList<String> connectionList = new ArrayList<String>();


	public void RAConfigWithBxaIP(WindowsDriver driver, String ip )
	{
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("menuLabel").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Settings").click();
		driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("ipAddressTextBox").sendKeys(ip);
		System.out.println("Changed the Boxilla Ip to "+ ip + " in RA successfully");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Configure").click(); 
	}


	public void RAConnectionLaunch(WindowsDriver driver) throws Exception
	{
		Actions action = new Actions(driver);
		int count = 0;
		int connum = 1;
		connectionList.clear();
		Thread.sleep(5000);
		WebElement allConnectionList = driver.findElementByAccessibilityId("availableConnectionsWinListBox");
		List<WebElement> availableConnection = allConnectionList.findElements(By.xpath("//ListItem"));
		for(WebElement connectionName : availableConnection)
		{
			connectionList.add(connectionName.getText());
			System.out.println("Connection Number " + connum + " is " + connectionName.getText());
			connum++;
		}
		for(String connection : connectionList)
		{
			driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
			Thread.sleep(4000);
			WebElement targetConnection = allConnectionList.findElement(By.name(connection));
			action.moveToElement(targetConnection).
			doubleClick().build().perform();
			System.out.println("Connection Name -  " +connection + " has launched");
			count++;
			Thread.sleep(15000);
		}
	}

	public void RADisconnectConnection(WindowsDriver driver)
	{
		for (String RAconnectionName : connectionList) {
			driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
			driver.findElement(By.name(RAconnectionName)).click();
			driver.findElement(By.name("Disconnect")).click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("connection "+RAconnectionName+" is disconnected");
			driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		}
	}


	public static WindowsDriver launchedVMConnectionWindow(WindowsDriver driver, WindowsDriver driver1) throws Exception
	{
		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) driver1.findElementByClassName("wCloudRDP");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex

		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);
		WindowsDriver RASession;

		//attaching to connection session
		RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);

		WebElement maximise = RASession.findElementByName("Maximise");
		WebElement close = RASession.findElementByName("Close");
		maximise.click();
		System.out.println("Connection Maximised");
		Thread.sleep(5000);
		WebElement restore = RASession.findElementByName("Restore");
		connectionWindow.click();
		Thread.sleep(3000);

		//close.click();
		//System.out.println("Closing Launched Connection");

		//driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		Thread.sleep(2000);

		return RASession;
	}



	public static WindowsDriver launchedConnectionWindow(WindowsDriver driver, WindowsDriver driver1) throws Exception
	{
		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) driver1.findElementByClassName("wCloudBB");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex

		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);
		WindowsDriver RASession;

		//attaching to connection session
		RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);

		Thread.sleep(3000);
		WebElement maximise = RASession.findElementByName("Maximise");
		WebElement close = RASession.findElementByName("Close");
		Thread.sleep(2000);
		maximise.click();
		System.out.println("Connection Maximised");
		Thread.sleep(5000);
		WebElement restore = RASession.findElementByName("Restore");
		Thread.sleep(2000);
		//restore.click();
		System.out.println("Restored connection ");
		connectionWindow.click();
		Thread.sleep(5000);

		//close.click();
		//System.out.println("Closing Launched Connection");

		//driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		Thread.sleep(2000);

		return RASession;
	}

	public static int activeConnectionsfromAPI( String bxamanager, String user, String pass)
	{

		//Asserting the number of connections to be zero
		RestAssured.useRelaxedHTTPSValidation();

		RestAssured.useRelaxedHTTPSValidation();
		String gerdetails = RestAssured.given().auth().preemptive().basic(user, pass).headers("Content-Type", "application/json", "Accept","application/json")
				.when().get("https://"+bxamanager+"/bxa-api/connections/kvm/active")
				.then().assertThat().statusCode(200)
				.extract().response().asString();
		System.out.println("Active connection status"+gerdetails);

		JsonPath js1 = new JsonPath(gerdetails);
		int count=js1.getInt("message.size()");
		int countconnection=js1.getInt("message.active_connections.size()");
		System.out.println("connection size is "+countconnection);
		return countconnection;
		}
	
	public void rebootfromAPI( String bxamanager, String user, String pass)
	{

		RestAssured.useRelaxedHTTPSValidation();
		String response = given().auth().preemptive().basic(user, pass).headers(BoxillaHeaders.getBoxillaHeaders())
				.when().contentType(ContentType.JSON)
				.post("https://" + bxamanager + "/bxa-api/devices/kvm/reboot")
				.then().assertThat().statusCode(200)
				.extract().response().asString();
		System.out.println("Transmitter Reboot status " + response);
		}
	public void rebootspecificdevice(String bxamanager, String user, String pass, String name )
	{
		RestAssured.useRelaxedHTTPSValidation();
		String response = given().auth().preemptive().basic(user, pass).headers(BoxillaHeaders.getBoxillaHeaders())
				.when().contentType(ContentType.JSON)
				.body(name)
				.post("https://" + bxamanager + "/bxa-api/devices/kvm/reboot")
				.then().assertThat().statusCode(200)
				.extract().response().asString();
		System.out.println("Transmitter Reboot status " + response);
	}


	public static String textFromSFTP(String username, String password, String ip, String path) throws Exception
	{
		JSch jsch = new JSch();
		Channel channel = null;
		ChannelSftp sftp = null;
		Session session = jsch.getSession(username, ip, 22);
		session.setPassword(password);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);

		try {
			session.connect();
			System.out.println("Successfully connected to " + ip);
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp)channel;
			sftp.get(path,"C:\\Users\\blackbox\\sftpcopy.txt");
		}catch(Exception e) {
			System.out.println(e.getMessage());
		} 
		finally
		{
			System.out.println("Execution completed !!!!");

			//validating if channel sftp is not null to exit
			if(sftp!=null) {
				sftp.exit();
				System.out.println("Sftp exited !!!");
			}
			if (channel!=null) {
				channel.disconnect();
				System.out.println("Session disconnected !!!!");
			}
		}

		File file = new File("\\Users\\blackbox\\sftpcopy.txt");
		//Scanner scan = new Scanner(file);
		//int numberofLines=0;

		String filecontent = "";
		FileReader fr=new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String text = br.readLine();
		if(text != null) {
			System.out.println("Text found" );
		}else
		{
			System.out.println("Text not found");
		}
		return text;
	}

	public static String NotepadSetupInPCAndCopy(WindowsDriver driver, String path) throws Exception
	{
		
		/*DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Windows\\System32\\notepad.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");
		//capabilities.setCapability("appWorkingDir", "C:\\Windows\\System32\\notepad.exe");


		Thread.sleep(1000);
		driver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("current window is "+driver.getWindowHandles().toArray()[0]);
		System.out.println("Notepad Launched SuccessFully");	

		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Thread.sleep(2000);

		//driver.findElementByName("Text editor").click();
		Actions action = new Actions(driver);
		action.sendKeys(Keys.chord(Keys.CONTROL,"o")).build().perform();

		//action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();


		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys("C:\\Users\\blackbox\\Desktop\\Copy Paste\\"+path+".txt").build().perform();;
		
		//driver.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\Desktop\\Copy Paste\\"+path+".txt");
		action.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		//driver.findElementByName("Text editor").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);*/

		//String text = driver.findElementByName("Text Editor").getText();
		File file = new File("C:\\Users\\blackbox\\Desktop\\Copy Paste\\"+path+".txt");
		//Scanner scan = new Scanner(file);
		//int numberofLines=0;

		String filecontent = "";
		FileReader fr=new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String text = br.readLine();
		if(text != null) {
			System.out.println("Text found" );
		}else
		{
			System.out.println("Text not found");
		}

		/*action.sendKeys(Keys.chord(Keys.CONTROL,"a")).build().perform();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"c")).build().perform();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		action.sendKeys(Keys.chord(Keys.CONTROL,"w")).build().perform();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		action.sendKeys(Keys.chord(Keys.CONTROL,"w")).build().perform();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS); 
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		//driver.findElementByName("Close").click();
*/
		return text;


	}
	public void NotepadSetupInConAndPaste(WindowsDriver driver, String pastekey) throws Exception 
	{

		//String path = "c:\\paste.txt";

		Actions action = new Actions(driver);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		///action.sendKeys(Keys.chord(Keys.COMMAND, "d")).build().perform();;
		//driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		action.sendKeys(Keys.chord(Keys.COMMAND)).build().perform();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Thread.sleep(4000);
		action.sendKeys("paste.txt");
		System.out.println("Notepad opened successfully");
		Thread.sleep(2000);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		Thread.sleep(2000);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		//Clearing previously saved Text
		action.sendKeys(Keys.chord(Keys.CONTROL, "a")).build().perform();;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		Thread.sleep(2000);
		action.sendKeys(Keys.chord(Keys.BACK_SPACE)).build().perform();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		System.out.println("Text cleared");
		Thread.sleep(2000);

		if (pastekey=="Tx")
		{
			action.sendKeys(Keys.chord(Keys.F8)).build().perform();

			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);		
			System.out.println("Text pasted successfully");
		}else
		{
			action.sendKeys(Keys.chord(Keys.CONTROL, "v")).build().perform();
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);		
			System.out.println("Text pasted successfully");
		}
		Thread.sleep(2000);
		action.sendKeys(Keys.chord(Keys.CONTROL,"s")).build().perform();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		Thread.sleep(2000);
		action.sendKeys(Keys.chord(Keys.CONTROL,"w")).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Thread.sleep(2000);
		//action.sendKeys(Keys.chord(Keys.CONTROL,"w")).build().perform();
		//action.sendKeys(Keys.chord(Keys.ALT, Keys.TAB)).build().perform();;
		//driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		System.out.println(driver.getWindowHandles());

	}

	public void NotepadSetupInConAndCopy(WindowsDriver driver) throws Exception 
	{

		//String path = "c:\\paste.txt";

		Actions action = new Actions(driver);
		action.sendKeys(Keys.chord(Keys.COMMAND)).build().perform();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Thread.sleep(4000);
		action.sendKeys("paste.txt");
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		action.sendKeys(Keys.chord(Keys.CONTROL, "a")).build().perform();;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"c")).build().perform();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);		

		action.sendKeys(Keys.chord(Keys.CONTROL,"w")).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}


	public void copyFromNotepad(WindowsDriver driver, String path)
	{
		/*driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			driver.findElementByName("Text Editor").click();
			Actions action = new Actions(driver);
			action.sendKeys(Keys.chord(Keys.CONTROL,"o")).build().perform();

			action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();


			System.out.println(driver.getWindowHandles().size());
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);


			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\Desktop\\Copy Paste\\"+path+".txt");
			action.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
			driver.findElementByName("Text Editor").click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			action.sendKeys(Keys.chord(Keys.CONTROL,"a"),Keys.chord(Keys.CONTROL,"c")).build().perform();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		 */







		/*driver.findElementByName("File").click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.findElementByName("Open...").click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.switchTo().window((String)driver.getWindowHandles().toArray()[1]);
			driver.findElementByName("File name").sendKeys("C:\\Users\\blackbox\\Desktop\\Copy Paste\\"+path+".txt");
			driver.findElementByName("Open").click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
			driver.findElementByName("Text Editor").click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			Actions action = new Actions(driver);
			action.sendKeys(Keys.CONTROL);
			action.sendKeys("A");
			action.sendKeys("C").build().perform();
			//action.sendKeys(Keys.chord(Keys.CONTROL, 'a')).build().perform();
		 */		}

	public void teratermactions(WindowsDriver driver, String IP, String username, String password) throws Exception
	{


		DesiredCapabilities capability = new DesiredCapabilities();
		capability.setCapability("app", "C:\\Program Files\\teraterm\\ttermpro.exe");
		try {

			driver = new WindowsDriver(new URL("http://127.0.0.1:4723"), capability);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		Actions action = new Actions(driver);
		driver.manage().timeouts().implicitlyWait(3,TimeUnit.SECONDS);
		driver.findElementByClassName("ComboBox").clear();
		driver.findElementByClassName("ComboBox").sendKeys(IP);
		driver.findElementByName("OK").click();

		action.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Thread.sleep(5000);
		//driver.findElementByName("Continue").click();

		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		try
		{
			boolean visible = driver.findElement(By.name("SECURITY WARNING")).isDisplayed();
			if(visible == true)
			{
				driver.findElementByName("Continue").click();
			}

		}catch(Exception n)
		{
			
		}

		/*if(driver.findElementByName("SECURITY WARNING").isDisplayed())
		{
			driver.findElementByName("Continue").click();
		}*/

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//driver.findElementByAccessibilityId("1000").clear();
		driver.findElementByAccessibilityId("1000").sendKeys(username);
		//driver.findElementByAccessibilityId("1002").clear();
		driver.findElementByAccessibilityId("1002").sendKeys(password);	
		Thread.sleep(5000);

		driver.findElementByName("OK").click();
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys(" cd /usr/local");
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys("rm output.txt");
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		for (int i=1; i<4 ; i++) {
			Thread.sleep(60000);
			driver.findElementByName(IP+" - Tera Term VT").sendKeys("tail syslog.log | grep fps | awk '{print $10,$11,$13}' > output"+i+".txt");
			Thread.sleep(1000);
			driver.findElementByName(IP+" - Tera Term VT").sendKeys(Keys.ENTER);
			Thread.sleep(1000);
			driver.findElementByName(IP+" - Tera Term VT").sendKeys("cat output"+i+".txt");
			Thread.sleep(1000);
			driver.findElementByName(IP+" - Tera Term VT").sendKeys(Keys.ENTER);
		}
		Thread.sleep(5000);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys("cat output1.txt output2.txt output3.txt > output.txt");
		Thread.sleep(1000);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys(Keys.ENTER);
		Thread.sleep(4000);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys("exit");
		Thread.sleep(2000);
		driver.findElementByName(IP+" - Tera Term VT").sendKeys(Keys.ENTER);
		Thread.sleep(2000);
	}




	public void CMDkeysGen(WindowsDriver driver, WindowsDriver driver2)
	{
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Windows\\System32\\cmd.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");
		
		try
		{
			Thread.sleep(5000);

			driver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			driver2 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);
			driver2.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			
			/*//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("current window is "+driver.getWindowHandles().toArray()[0]);
			System.out.println("Command prompt Launched SuccessFully");*/
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured in Encrypt setup part");
		}


		driver.findElementByName("Text Area").sendKeys("cd C:\\Users\\blackbox\\Automation\\keys");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Text Area").sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("Text Area").sendKeys("openssl genrsa -out privateKeyRsa.key 4096");
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("Text Area").sendKeys(Keys.ENTER);
		System.out.println("Private key generated Succesfully");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("Text Area").sendKeys("openssl rsa -in privateKeyRsa.key -pubout -out publicKey.key");
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("Text Area").sendKeys(Keys.ENTER);
		System.out.println("Public key generated Succesfully");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("Text Area").sendKeys("exit");
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("Text Area").sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}


	public void EncryptRAAppsetup(WindowsDriver driver) throws Exception
	{
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Program Files (x86)\\BlackBox\\EmeraldRAEncrypt\\EmeraldRAEncrypt.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");
		SoftAssert soft = new SoftAssert();
		try
		{
			//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("current window is "+driver.getWindowHandles().toArray()[0]);
			System.out.println("EmeraldRA Encrypt Launched SuccessFully");
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured in Encrypt setup part");
		}
	}


	public void EncryptRAsetup(WindowsDriver driver, WindowsDriver driver1, String boxilla, ArrayList connectionname, String Autouser, String Autopass, String path, String RAuser, String RApass, Boolean entereddetails) throws Exception
	{


		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Program Files (x86)\\BlackBox\\EmeraldRAEncrypt\\EmeraldRAEncrypt.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");
		SoftAssert soft = new SoftAssert();
		try
		{
			Thread.sleep(5000);

			driver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"),capabilities);
			driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
			//Root session (windows)
			DesiredCapabilities appCapabilities = new DesiredCapabilities();
			appCapabilities.setCapability("app", "Root");
			driver1 = new WindowsDriver(new URL("http://127.0.0.1:4723"), appCapabilities);
			driver1.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			
			//driver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("current window is "+driver.getWindowHandles().toArray()[0]);
			System.out.println("EmeraldRA Encrypt Launched SuccessFully");
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception has occured in Encrypt setup part");
		}


		//WebElement temp = driver.findElement(By.xpath(xpathExpression))
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);


		driver.findElementByName("Configuration").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("txtRemoteAppPath").sendKeys("C:\\Program Files\\BlackBox\\EmeraldRA\\EmeraldRA.exe");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("txtBoxillaIp").sendKeys(boxilla);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		String con = connectionname.get(0).toString().replace("[","").replace("]", "");
		driver.findElementByAccessibilityId("txtConnectionName").sendKeys(con);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("cbxCustomTimestamp").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);	

		//Getting Active Time from API
		RestAssured.useRelaxedHTTPSValidation();
		String gerdetails = RestAssured.given().auth().preemptive().basic(Autouser, Autopass).headers("Content-Type", "application/json", "Accept","application/json")
				.when().get("https://"+boxilla+"/bxa-api/time")
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
		System.out.println("Actual Time of Boxilla is - " +formatted);

		Float f = Float.parseFloat(formatted);

		String[] arr=String.valueOf(f).split("\\.");
		int[] intArr=new int[2];
		intArr[0]=Integer.parseInt(arr[0]); 
		intArr[1]=Integer.parseInt(arr[1]); 

		String[] strArray = new String[intArr.length];
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		for (int i = 0; i < intArr.length; i++) {
			strArray[i] = String.valueOf(intArr[i]);
		}

		System.out.println(Arrays.toString(strArray));


		//Entering the time to RAEncrypt App
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		WebElement temp = driver.findElementByAccessibilityId("dtpTime");
		temp.click();
		Actions a = new Actions(driver);
		a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		temp.sendKeys(strArray[0]);
		a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		temp.sendKeys(strArray[1]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		driver.findElementByName("Main").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Set public key").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);



		driver.findElementByAccessibilityId("usernameTxt").sendKeys(RAuser);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("passwordTxt").sendKeys(RApass);

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\Automation\\keys\\"+path+".key");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		a.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		driver.findElementByName("Launch connection").click();
		Thread.sleep(3000);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		System.out.println("Clicked on Launch Connection Button ");
		
		
		
		Thread.sleep(70000);

		if(connectionname.size() >= 2)
		{

			for(int i=1; i< connectionname.size(); i++)
			{
				driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
				driver.findElementByName("Configuration").click();
				Thread.sleep(3000);
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				
				driver.findElementByAccessibilityId("txtConnectionName").click();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.CONTROL,"a")).build().perform();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.BACK_SPACE)).build().perform();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				
				
				//System.out.println("List of Available Connections :" +connectionname);
				String con1 = connectionname.get(i).toString();
				System.out.println("Connection entered is : " +con1);
				driver.findElementByAccessibilityId("txtConnectionName").sendKeys(con1);

				//Getting Active Time from API
				RestAssured.useRelaxedHTTPSValidation();
				String gerdetails1 = RestAssured.given().auth().preemptive().basic(Autouser, Autopass).headers("Content-Type", "application/json", "Accept","application/json")
						.when().get("https://"+boxilla+"/bxa-api/time")
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
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

				for (int j = 0; j < intArr1.length; j++) {
					strArray1[j] = String.valueOf(intArr1[j]);
				}
				System.out.println(Arrays.toString(strArray1));

			
				//Entering the time to RAEncrypt App
				driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
				WebElement temp1 = driver.findElementByAccessibilityId("dtpTime");
				temp1.click();
				a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				temp.sendKeys(strArray1[0]);
				a.sendKeys(Keys.chord(Keys.ARROW_RIGHT)).build().perform();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				temp.sendKeys(strArray1[1]);
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				
				driver.findElementByName("Main").click();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				driver.findElementByName("Launch connection").click();
				Thread.sleep(4000);
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
				System.out.println("Clicked on Launch Connection Button ");
				Thread.sleep(10000);
			}	
		}

		RestAssured.useRelaxedHTTPSValidation();
		String  details = RestAssured.given().auth().preemptive().basic(Autouser, Autopass).headers("Content-Type", "application/json", "Accept","application/json")
				.when().get("https://"+boxilla+"/bxa-api/connections/kvm/active")
				.then().assertThat().statusCode(200)
				.extract().response().asString();

		JsonPath js = new JsonPath(details);
		int	countconnection=js.getInt("message.active_connections.size()");
		System.out.println("Number of Active connections are "+countconnection);

		if(countconnection == 0)
		{
			System.out.println("Connection did not launched Successfully");
			if(entereddetails == true)
			{
				soft.assertEquals(countconnection, connectionname.size(),"Mismatch on the number of launched connections and active connections");
				soft.fail();
			}	
		}
		else {
			if(entereddetails == false)
			{
				System.out.println("Connection Shouldn't Launch but Launched ");
				Assert.fail("Connection Shouldn't Launch but Launched ");	
			}
			//soft.assertEquals(countconnection, connectionname.size(),"Mismatch on the number of launched connections and active connections");
			System.out.println("Connection Launched Successfully as Expected");
		}

		Thread.sleep(3000);
		driver.findElementByName("Terminate connection").click();
		Thread.sleep(10000);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);  
		a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		System.out.println("Clicked on Terminate Connection Button");
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		//a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		Thread.sleep(10000);
		driver.findElementByName("Close").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("Successfully Closed Emerald RA Application");
		driver.quit();
		soft.assertAll();
	}

	public void EncryptRAlaunch(WindowsDriver driver) 
	{
		Actions a = new Actions(driver);
		driver.findElementByName("Launch connection").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		System.out.println("Connection launched Successfully");
	}

	public void EncryptRAterminate(WindowsDriver driver) 
	{
		Actions a = new Actions(driver);
		driver.findElementByName("Terminate connection").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);  
		a.sendKeys(Keys.chord(Keys.ENTER)).build().perform();
		System.out.println("Connection terminated Successfully");
	}

	public void EncryptRAClose(WindowsDriver driver) 
	{
		driver.findElementByName("Close").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
		System.out.println("Successfully Closed Emerald RA Application");
	}
	
	public void checkdeviceonline(WebDriver driver, String IpAddress) throws InterruptedException
	{
			System.out.println("Checking device status");
			System.out.println("Attempting to check if device with IP address " + IpAddress+ " is online");
			LandingPage.devicesTab(driver).click();
			new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(LandingPage.devicesStatus(driver)));
			LandingPage.devicesStatus(driver).click();
			System.out.println("Devices > Settings > Options - Clicked on Status tab");
			Thread.sleep(3000);
			SeleniumActions.seleniumSendKeys(driver, Devices.deviceStatusSearchBox, IpAddress);
			//check if device is online
			int timer = 0;
			int limit = 12;			//12 iterations of 5 seconds = 1 minute
			while(timer <= limit) {
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
	public void enableLaunchMessage(WindowsDriver driver) throws InterruptedException
	{
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.findElementByAccessibilityId("menuLabel").click();
		Thread.sleep(2000);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Settings").click();
		Thread.sleep(2000);
		System.out.println("Clicking on the Connection Window");
		WebElement temp2 = driver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));

		Thread.sleep(3000);
		Actions d = new Actions(driver);
		d.moveToElement(temp2, 160, 15).
		doubleClick().
		build().perform();
		Thread.sleep(2000);
		driver.findElementByName("ConfigurableConnectionLaunchMsgCheckBox").click();
		System.out.println("Click on Connection launch message");
		Thread.sleep(2000);
		driver.findElementByAccessibilityId("applyButton").click();
		Thread.sleep(3000);
	}
	
	public void disableLaunchMessage(WindowsDriver driver) throws InterruptedException
	{
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.findElementByAccessibilityId("menuLabel").click();
		Thread.sleep(2000);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Settings").click();
		Thread.sleep(2000);
		System.out.println("Clicking on the Connection Window");
		WebElement temp2 = driver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));

		Thread.sleep(3000);
		Actions d = new Actions(driver);
		d.moveToElement(temp2, 160, 15).
		doubleClick().
		build().perform();
		Thread.sleep(2000);
		driver.findElementByName("ConfigurableConnectionLaunchMsgCheckBox").click();
		Thread.sleep(2000);
		driver.findElementByAccessibilityId("applyButton").click();
		Thread.sleep(3000);
	}
	
}
