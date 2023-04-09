package com.utilities;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public interface ISafeActions {
    void click(WebElement element);
    void sendKeys(WebElement element, String text);
    void hoverOn(WebElement element);
    void hoverAway(WebElement element);
    String getText(WebElement element);
    String getAttribute(WebElement element, String attributeName);
    public Actions actionsBuilder();
}
