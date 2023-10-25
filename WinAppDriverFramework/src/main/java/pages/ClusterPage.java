package pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import methods.Discovery;
import methods.SeleniumActions;
import methods.Users;
import tests.TestBase;
import methods.Switch;



public class ClusterPage {
	private static WebElement webEle = null;


	public static WebElement cluster(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//span[@class='list-group-item-value'][text()='Cluster']"));
		return webEle;
	}


	public static WebElement prepareMasterlink(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//span[text()='Prepare Master']"));
		return webEle;
	}

	public static WebElement masterClusterID(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath(".//input[@id='preparemaster-cluster-id']"));
		return webEle;
	}


	public static WebElement virtualIP(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath(".//input[@id='preparemaster-vip']"));
		return webEle;
	}

	public static WebElement masterNodeID(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//input[@id='preparemaster-node-id']"));
		return webEle;
	}

	public static WebElement masterNodeName(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//input[@id='preparemaster-node-name']"));
		return webEle;
	}

	public static WebElement prepareMasterBtn(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//button[@id='btn-prepare-master']"));
		return webEle;
	}


	//XPath's for StandBy Cluster

	public static WebElement prepareStandByLink(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//span[text()='Prepare Standby']"));
		return webEle;
	}

	public static WebElement masterBxlaIP(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//input[@id='preparestandby-mip']"));
		return webEle;
	}

	public static WebElement standbyNodeID(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//input[@id='preparestandby-node-id']"));
		return webEle;
	}

	public static WebElement standbyNodeName(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//input[@id='preparestandby-node-name']"));
		return webEle;
	}

	public static WebElement prepareStandByBtn(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//button[@id='btn-prepare-standby']"));
		return webEle;
	}
	
	
	//Xpath's for Dissolve Cluster
	public static WebElement dropdownbuttonForDissolve(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//div[@class='dropdown dropdown-kebab-pf']"));
		return webEle;
	}
	
	public static WebElement detachButton(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//a[text()='Detach']"));
		return webEle;
	}
	
	public static WebElement makeStandalone(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//a[text()='Make Standalone']"));
		return webEle;
	}
	
	public static WebElement dissolveButton(WebDriver webdriver)
	{
		webEle=webdriver.findElement(By.xpath("//span[text()='Dissolve Cluster']"));
		return webEle;
	}
	










	public void createMasterCluster(WebDriver driver, String vip, String mclusterid, String mnodeid, String mnodename ) throws Exception
	{
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("Master Boxilla URL is  " + driver.getCurrentUrl());
		cluster(driver).click();
		Thread.sleep(20000);
		
		if(dissolveButton(driver).isDisplayed())
		{
			dissolveButton(driver).click();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			driver.switchTo().alert().accept();
			new WebDriverWait(driver, 120).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
			new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Prepare Master']")));
		}
		
		
		new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Prepare Master']")));
		prepareMasterlink(driver).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		masterClusterID(driver).sendKeys(mclusterid);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		virtualIP(driver).sendKeys(vip);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		masterNodeID(driver).sendKeys(mnodeid);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		masterNodeName(driver).sendKeys(mnodename);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		prepareMasterBtn(driver).click();
		new WebDriverWait(driver, 120).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
		new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Dissolve Cluster']")));
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("Master Created Successfully");
	}

	public void createStandByCluster(WebDriver driver, String masterip, String standbynodeid, String standbynodename )
	{
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("StandBy Boxilla URL is  " + driver.getCurrentUrl());
		cluster(driver).click();
		new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Prepare Master']")));
		prepareStandByLink(driver).click();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		masterBxlaIP(driver).sendKeys(masterip);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		standbyNodeID(driver).sendKeys(standbynodeid);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		standbyNodeName(driver).sendKeys(standbynodename);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		prepareStandByBtn(driver).click();
		new WebDriverWait(driver, 120).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
		
		new WebDriverWait(driver, 80).until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Login']")));
		driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
		System.out.println("Standby Created Successfully");
		System.out.println(" Cluster Created Successfully");
	}
	
	public void dissolveCluster(WebDriver driver)
	{
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		System.out.println("Entered into Master with URL  " + driver.getCurrentUrl());
		cluster(driver).click();
		new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='dropdown dropdown-kebab-pf']")));
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		dropdownbuttonForDissolve(driver).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		makeStandalone(driver).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.switchTo().alert().accept();
		new WebDriverWait(driver, 120).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
		
		new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Dissolve Cluster']")));
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		///new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(LandingPage.devicesStatus(driver)));
		dissolveButton(driver).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.switchTo().alert().accept();
		new WebDriverWait(driver, 120).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
		new WebDriverWait(driver, 120).until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Prepare Master']")));
		
		
		///new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(LandingPage.devicesStatus(driver)));
		
		//new WebDriverWait(driver, 120).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
		//new WebDriverWait(driver, 120).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Switch.spinnerXpath)));
		//new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(By.xpath(Devices.getsystemPropertiesButtonXpath())));
		System.out.println(" Cluster is Dissolved");
	}






	/*
	public static WebElement (WebDriver driver)
	{
		webEle=driver.findElement(By.xpath(""));
		return webEle;
	}

	 */



}
