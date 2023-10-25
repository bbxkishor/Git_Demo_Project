package methods;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;

import io.appium.java_client.windows.WindowsDriver;

public class AutologinAutoConnect {

	public void AutologinEnable(WindowsDriver driver)
	{
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("AutoLogInCheckBox").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("AutoLogin Enabled - Clicked on AutoLogin Button ");	
	}

	public void relogin(WindowsDriver driver, String username, String Password) 
	{
		System.out.println("current window is "+driver.getWindowHandles().toArray()[0]);
		WebElement loginButton = driver.findElementByAccessibilityId("logInButton");
		driver.findElementByAccessibilityId("userNameTextBox").sendKeys(username);
		System.out.println("Username Entered");
		driver.findElementByAccessibilityId("passwordTextBox").sendKeys(Password);
		System.out.println("Password Entered");
		loginButton.click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);	
	}

	public void AutoConnectEnable(WindowsDriver driver)
	{
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("AutoConnectCheckBox").click();
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println(" AutoConnect Enabled - Clicked on AutoConnect Button ");	
	}

	public void saveCurrentLayout(WindowsDriver driver)
	{
		driver.findElementByAccessibilityId("menuLabel").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Settings").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		WebElement temp2 = driver.findElement(By.xpath("//Pane[@Name='kryptonDockableNavigator1'][@AutomationId='settingsNavigation']"));
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Actions d = new Actions(driver);
		d.moveToElement(temp2, 160, 15).
		doubleClick().
		build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("windowSizingCheckBox").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("applyButton").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	public void AutoLoginDisable(WindowsDriver driver)
	{
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("AutoLogInCheckBox").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("AutoLogin Disabled - Clicked on AutoLogin Button ");
	}

	public void AutoConnectDisable(WindowsDriver driver)
	{
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("AutoConnectCheckBox").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println(" AutoConnect Disabled - Clicked on AutoConnect Button ");
	}


	public static int [] TXConnectionSizeandLocationinfo(WindowsDriver driver) throws Exception
	{
		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) driver.findElementByClassName("wCloudBB");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex
		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

		WindowsDriver RASession;

		//attaching to connection session and doing stuff..
		RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
		//RASession.switchTo().window((String)RASession.getWindowHandles().toArray()[0]);

		WebElement ele = RASession.findElementByClassName("wCloudBB");
		Thread.sleep(3000);
		Point location = RASession.manage().window().getPosition();
		System.out.println("location = " + location );
		int con = location.getX();
		int con1 = location.getY();
		//System.out.println("X = " + con);
		//System.out.println("Y = " + con1);

		Dimension currentDimension = RASession. manage(). window(). getSize();
		int height = currentDimension. getHeight();
		int width = currentDimension. getWidth();
		System. out. println("Current height: "+ height);
		System. out. println("Current width: "+width);

		int a[] = {con, con1, width, height};
		return a;
	}

	public static int [] VMConnectionSizeandLocationinfo(WindowsDriver driver) throws Exception
	{
		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) driver.findElementByClassName("wCloudRDP");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex
		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

		WindowsDriver RASession;

		//attaching to connection session and doing stuff..
		RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
		//RASession.switchTo().window((String)RASession.getWindowHandles().toArray()[0]);

		WebElement ele = RASession.findElementByClassName("wCloudRDP");
		Thread.sleep(3000);
		Point location = RASession.manage().window().getPosition();
		System.out.println("location = " + location );
		int con = location.getX();
		int con1 = location.getY();
		//System.out.println("X = " + con);
		//System.out.println("Y = " + con1);

		Dimension currentDimension = RASession. manage(). window(). getSize();
		int height = currentDimension. getHeight();
		int width = currentDimension. getWidth();
		System. out. println("Current height: "+ height);
		System. out. println("Current width: "+width);

		int a[] = {con, con1, width, height};
		return a;
	}

	public static WindowsDriver LaunchedVMConnection(WindowsDriver driver) throws MalformedURLException
	{
		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) driver.findElementByClassName("wCloudRDP");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex
		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

		WindowsDriver RASession;

		//attaching to connection session and doing stuff..
		RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
		//RASession.switchTo().window((String)RASession.getWindowHandles().toArray()[0]);	
		return RASession;
	}
	
	public static WindowsDriver LaunchedTXConnection(WindowsDriver driver) throws MalformedURLException
	{
		//Getting window handle of launched connection
		WebElement connectionWindow = (WebElement) driver.findElementByClassName("wCloudBB");
		String connectionWindowHandle = connectionWindow.getAttribute("NativeWindowHandle");
		String connectionTopLevelWidnowHandle = Integer.toHexString(Integer.parseInt((connectionWindowHandle))); // Convert to Hex
		//Setting capabilities for connection window session
		DesiredCapabilities connectionAppCapabilities = new DesiredCapabilities();
		connectionAppCapabilities.setCapability("appTopLevelWindow", connectionTopLevelWidnowHandle);

		WindowsDriver RASession;

		//attaching to connection session and doing stuff..
		RASession = new WindowsDriver(new URL("http://127.0.0.1:4723"), connectionAppCapabilities);
		//RASession.switchTo().window((String)RASession.getWindowHandles().toArray()[0]);	
		return RASession;
	}

	public static int[] currentTXSize(WindowsDriver driver) throws MalformedURLException
	{
		WindowsDriver RASession = LaunchedTXConnection(driver);
		WebElement ele = RASession.findElementByClassName("wCloudBB");
		Dimension currentDimension = RASession.manage().window().getSize();
		int height = currentDimension.getHeight();
		int width = currentDimension.getWidth();
		System.out.println("Current height: "+ height);
		System.out.println("Current width: "+width);
		int a[] = {width, height};
		return a;
	}
	
	public static int[] currentTXPosition(WindowsDriver driver) throws MalformedURLException
	{
		WindowsDriver RASession = LaunchedTXConnection(driver);
		WebElement ele = RASession.findElementByClassName("wCloudBB");
		Point location = RASession.manage().window().getPosition();
		System.out.println("location = " + location );
		int con = location.getX();
		int con1 = location.getY();
		int a[] = {con, con1};
		return a;
	}
	
	public static int[] currentVMSize(WindowsDriver driver) throws MalformedURLException
	{
		WindowsDriver RASession = LaunchedVMConnection(driver);
		WebElement ele = RASession.findElementByClassName("wCloudRDP");
		Dimension currentDimension = RASession.manage().window().getSize();
		int height = currentDimension.getHeight();
		int width = currentDimension.getWidth();
		System.out.println("Current height: "+ height);
		System.out.println("Current width: "+width);
		int a[] = {width, height};
		return a;
	}
	
	public static int[] currentVMPosition(WindowsDriver driver) throws MalformedURLException
	{
		WindowsDriver RASession = LaunchedVMConnection(driver);
		WebElement ele = RASession.findElementByClassName("wCloudRDP");
		Point location = RASession.manage().window().getPosition();
		System.out.println("location = " + location );
		int con = location.getX();
		int con1 = location.getY();
		int a[] = {con, con1};
		return a;
	}
	
	public static int[] currentVMSizeandPosition(WindowsDriver driver) throws MalformedURLException
	{
		WindowsDriver RASession = LaunchedTXConnection(driver);
		WebElement ele = RASession.findElementByClassName("wCloudRDP");
		Point location = RASession.manage().window().getPosition();
		System.out.println("location = " + location );
		int con = location.getX();
		int con1 = location.getY();
		Dimension currentDimension = RASession.manage().window().getSize();
		int height = currentDimension.getHeight();
		int width = currentDimension.getWidth();
		System.out.println("Current height: "+ height);
		System.out.println("Current width: "+width);
		int a[] = {con, con1, width, height};
		return a;
	}
	
	public void changeTXConnectionPosition(WindowsDriver driver, int x, int y) throws Exception
	{
		WindowsDriver RASession = LaunchedTXConnection(driver);
		RASession.manage().window().setPosition(new Point(x,y));
		Point location = RASession.manage().window().getPosition();
		System.out.println("location = " + location );
		int con = location.getX();
		int con1 = location.getY();
		System.out.println("X = " + con);
		System.out.println("Y = " + con1);
		Thread.sleep(4000);
	}

	public void changeTXConnectionSize(WindowsDriver driver, int newWidth, int newHeight) throws Exception
	{
		
		Thread.sleep(4000);
		WindowsDriver RASession = LaunchedTXConnection(driver);		
		Dimension newDimension = new Dimension(newWidth, newHeight);
		RASession.manage().window().setSize(newDimension);
		Dimension currentDimension1 = RASession.manage().window().getSize();
		int height1 = currentDimension1.getHeight();
		int width1 = currentDimension1.getWidth();
		System.out.println("Changed height: "+ height1);
		System.out.println("Changed width: "+width1);
		Thread.sleep(4000);
	}
	
	public void changeVMConnectionPosition(WindowsDriver driver, int x, int y) throws Exception
	{
		WindowsDriver RASession = LaunchedVMConnection(driver);
		RASession.manage().window().setPosition(new Point(x,y));
		Point location = RASession.manage().window().getPosition();
		System.out.println("location = " + location );
		int con = location.getX();
		int con1 = location.getY();
		System.out.println("X = " + con);
		System.out.println("Y = " + con1);
		Thread.sleep(4000);
	}

	public void changeVMConnectionSize(WindowsDriver driver, int newWidth, int newHeight) throws Exception
	{
		WindowsDriver RASession = LaunchedVMConnection(driver);		
		Dimension newDimension = new Dimension(newWidth, newHeight);
		RASession.manage().window().setSize(newDimension);
		Dimension currentDimension1 = RASession.manage().window().getSize();
		int height1 = currentDimension1.getHeight();
		int width1 = currentDimension1.getWidth();
		System.out.println("Changed height: "+ height1);
		System.out.println("Changed width: "+width1);
		Thread.sleep(4000);
	}

	public void TXConnectionWindowSizeandPositionChange(WindowsDriver driver, ArrayList conName, int newWidth, int newHeight) throws Exception
	{
		WindowsDriver RASession = LaunchedTXConnection(driver);
		WebElement ele = RASession.findElementByClassName("wCloudBB");

		Dimension currentDimension = RASession.manage().window().getSize();
		int height = currentDimension.getHeight();
		int width = currentDimension.getWidth();
		System.out.println("Current height: "+ height);
		System.out.println("Current width: "+width);

		Dimension newDimension = new Dimension(newWidth, newHeight);
		RASession.manage().window().setSize(newDimension);

		Dimension currentDimension1 = RASession.manage().window().getSize();
		int height1 = currentDimension1.getHeight();
		int width1 = currentDimension1.getWidth();
		System.out.println("Current height: "+ height1);
		System.out.println("Current width: "+width1);


		String connection = conName.get(0).toString();
		WebElement ele1 = RASession.findElementByName(connection);
		//WebElement ele1 = RASession.findElementByAccessibilityId("TitleBar");
		Thread.sleep(3000);
		Actions d= new Actions(driver);
		d.moveToElement(ele1).build().perform();;
		d.moveToElement(ele1, 0, -30).build().perform();
		Point location = RASession.manage().window().getPosition();
		int con = location.getX();
		int con1 = location.getY();	

	}


	public void configFileEdit(WindowsDriver driver, ArrayList connections, String user, int x, int y, int width, int height)
	{

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("app", "C:\\Windows\\System32\\notepad.exe");
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("deviceName", "WindowsPC");

		try
		{
			driver = new WindowsDriver<RemoteWebElement>(new URL("http://127.0.0.1:4723"), capabilities);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			System.out.println("current window is "+driver.getWindowHandles().toArray()[0]);
			System.out.println("Notepad Launched SuccessFully");

		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Notepad not Launched, Exception has occured");
		}
		Actions action = new Actions(driver);
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByName("Text Editor").click();
		action.sendKeys(Keys.chord(Keys.CONTROL,"o")).build().perform();
		action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElementByAccessibilityId("1148").sendKeys("C:\\Users\\blackbox\\AppData\\Local\\BlackBox\\EmeraldRA\\windows.config");
		action.sendKeys(Keys.chord(Keys.ALT,"o")).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.switchTo().window((String)driver.getWindowHandles().toArray()[0]);
		driver.findElementByName("Text Editor").click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"a")).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.ESCAPE)).build().perform();

		String con = connections.get(0).toString().replace("[","").replace("]", "");
		action.sendKeys("["+user+"]");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.ENTER)).build().perform();

		for(int i=0; i<connections.size();i++)
		{
			x= x+100;
			y= y+100;
			String con1 = connections.get(i).toString();
			action.sendKeys(con1+", "+x+", "+y+", "+width+", "+height);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			action.sendKeys(Keys.chord(Keys.ENTER)).build().perform();	
		}
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"s")).build().perform();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		action.sendKeys(Keys.chord(Keys.CONTROL,"w")).build().perform();
	}
}
