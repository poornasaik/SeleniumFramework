package com.utilities;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;

public class CommonWait {
    private final WebDriver driver;
    private FluentWait<WebDriver> fluentWait;

    public CommonWait(WebDriver driver) {
        this.driver = driver;
        this.fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(WebDriverException.class);
    }

    public void waitForCondition(ExpectedCondition<WebElement> condition, int timeoutSeconds, int pollingIntervalMillis) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingIntervalMillis))
                .ignoring(WebDriverException.class);
        fluentWait.until(condition);
    }

    public WebElement waitForCondition(ExpectedCondition<WebElement> condition) {
        return fluentWait.until(condition);
    }
}
