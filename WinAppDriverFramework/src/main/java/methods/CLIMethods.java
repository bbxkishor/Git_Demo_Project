package methods;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.awt.event.KeyEvent;
import io.appium.java_client.windows.WindowsDriver;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import pages.LandingPage;
import tests.Ssh;
import tests.TestBase;


public class CLIMethods {

	private static WebElement ele = null;

	public static WebElement SystemDropdown(WebDriver driver)
	{
		ele=driver.findElement(By.xpath("(.//span[@class='list-group-item-value dropdown-btn'])[6]"));
		//span[@class='list-group-item-value dropdown-btn'])[6]"
		return ele;
	}

	public static WebElement Settingsbtn(WebDriver driver)
	{
		ele = driver.findElement(By.xpath("//span[text()= 'Settings'][@class = 'list-group-item-value'][2]"));
		return ele;
	}

	public static WebElement RemoteAppbtn(WebDriver driver)
	{
		ele = driver.findElement(By.xpath("//div[@class = 'bb-tab'][text()='Remote App']"));
		return ele;
	}

	public static WebElement Timestamp(WebDriver driver)
	{
		ele = driver.findElement(By.xpath("//input[@name='cli-timestamp-expiry-input']"));
		return ele;
	}

	public static WebElement Applybtn(WebDriver driver)
	{
		ele = driver.findElement(By.xpath("//button[@class='btn btn-default fav-save'][@style ='float: right; padding: 2px 22px;']"));
		return ele;
	}

	public static WebElement Choosefile(WebDriver driver)
	{
		ele = driver.findElement(By.xpath("//input[@id = 'cli-key']"));
		return ele;
	}

	public static WebElement Uploadbtn(WebDriver driver)
	{
		ele = driver.findElement(By.xpath("//button[@class='btn btn-default fav-save'][@style ='float: right; padding: 2px 12px;']"));
		return ele;
	}
	





	/*public static WebElement (WebDriver driver)
	{
		ele = driver.findElement(By.xpath(""));
		return ele;
	}*/
	public void clikeyupload(WebDriver driver, String path, String timestamp) throws Exception
	{
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LandingPage.systemTab(driver).click();
		System.out.println("System Dropdown clicked");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LandingPage.systemSettings(driver).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		RemoteAppbtn(driver).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Timestamp(driver).clear();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Timestamp(driver).sendKeys(timestamp);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Applybtn(driver).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//Choosefile(driver).sendKeys("C:\\Users\\blackbox\\Automation\\keys" + path +".key");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

		WebElement fileupload = Choosefile(driver);
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", fileupload);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		Robot rb = new Robot();
		rb.delay(3000);
		
		//copying to clipboard ctrl+C
		StringSelection ss = new StringSelection("C:\\Users\\blackbox\\Automation\\keys\\"+path+".key");
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		rb.delay(3000);
		
		 // ctrl+v
		rb.keyPress(KeyEvent.VK_CONTROL);
		rb.keyPress(KeyEvent.VK_V);
		rb.keyRelease(KeyEvent.VK_CONTROL);
		rb.keyRelease(KeyEvent.VK_V);
		rb.delay(3000);
		
		//Enter
		rb.keyPress(KeyEvent.VK_ENTER);
		rb.keyRelease(KeyEvent.VK_ENTER);
		rb.delay(3000);
		

	/*	try {
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability("app", "Root");
			WindowsDriver session;
			session = new WindowsDriver<>(new URL("http://127.0.0.1:4723"), cap);
			
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	session.findElementByName("File name:").click();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			session.findElementByName("File name:").sendKeys("C:\\Users\\blackbox\\Automation\\keys\\"+path+".key");
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			session.findElementByName("Open").click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			

		} catch (MalformedURLException e) {
			System.out.println("Exception is occured");
			e.printStackTrace();
		}*/
		
		
		
		
		
		
		
		
		
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Uploadbtn(driver).click();
		Thread.sleep(10000);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

	}
}
