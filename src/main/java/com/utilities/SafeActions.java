package com.utilities;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SafeActions implements ISafeActions {
    private WebDriver driver;
    private Actions actions;
    private CommonWait wait;

    public SafeActions(WebDriver driver, CommonWait wait) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = wait;
    }

    @Override
    public void click(WebElement element) {
        try {
            wait.waitForCondition(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    @Override
    public void sendKeys(WebElement element, String text) {
        try {
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", element, text);
        }
    }

    @Override
    public void hoverOn(WebElement element) {
        actions.moveToElement(element).perform();
    }
    @Override
    public Actions actionsBuilder() {
    	return actions;
    }

    @Override
    public void hoverAway(WebElement element) {
        Dimension elementSize = element.getSize();
        Point elementLocation = element.getLocation();
        int centerX = elementLocation.getX() + elementSize.getWidth() / 2;
        int centerY = elementLocation.getY() + elementSize.getHeight() / 2;
        Dimension viewportSize = driver.manage().window().getSize();
        int desiredDistance = 50;
        int xOffset = centerX + elementSize.getWidth() / 2 + desiredDistance;
        int yOffset = centerY + elementSize.getHeight() / 2 + desiredDistance;

        if (xOffset > viewportSize.getWidth()) {
            xOffset = centerX - elementSize.getWidth() / 2 - desiredDistance;
        }
        if (yOffset > viewportSize.getHeight()) {
            yOffset = centerY - elementSize.getHeight() / 2 - desiredDistance;
        }
        actions.moveByOffset(centerX, centerY).perform();
        actions.moveByOffset(xOffset - centerX, yOffset - centerY).perform();
    }

    @Override
    public String getText(WebElement element) {
        return element.getText();
    }

    @Override
    public String getAttribute(WebElement element, String attributeName) {
        return element.getAttribute(attributeName);
    }
}
