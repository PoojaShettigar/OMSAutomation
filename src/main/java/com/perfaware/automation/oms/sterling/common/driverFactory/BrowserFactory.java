package com.perfaware.automation.oms.sterling.common.driverFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BrowserFactory {
	//create webdriver object for given browser
		@SuppressWarnings("deprecation")
		public WebDriver createBrowserInstance(String browser) throws MalformedURLException {

			RemoteWebDriver driver = null;
			if(browser.equalsIgnoreCase("Chrome")) {
				WebDriverManager.chromedriver().setup();
				System.setProperty("webdriver.chrome.silentOutput", "true");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--incognito");
				//options.addArguments("--headless");
				options.addArguments("ignore-certificate-errors");
				options.addArguments("lang=en-GB");
				driver = new ChromeDriver(options);
			}
			else if (browser.equalsIgnoreCase("firefox")) {


			} if (browser.equalsIgnoreCase("ie")) {

			}
			return driver;
		}
}
