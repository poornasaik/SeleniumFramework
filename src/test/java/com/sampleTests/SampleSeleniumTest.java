package com.sampleTests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.utilities.CommonWait;
import com.utilities.ISafeActions;
import com.utilities.SafeActionsWrapper;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SampleSeleniumTest {
	
	public static void main(String[] args) {
		
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		
		driver.get("https://www.google.com");
		CommonWait wait = new CommonWait(driver);
		ISafeActions safeActions = SafeActionsWrapper.getSafeActions(driver,wait);
		safeActions.sendKeys(driver.findElement(By.cssSelector("[name=\"q\"]")), "sample text");
	}

}
