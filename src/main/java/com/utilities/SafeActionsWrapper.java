package com.utilities;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SafeActionsWrapper {
    private final ISafeActions safeActions;
    private WebElement visibleElement;
    private final CommonWait wait;

    private SafeActionsWrapper(WebDriver driver, CommonWait wait) {
        this.safeActions = new SafeActions(driver, wait);
        this.wait = wait;
    }

    public static ISafeActions getSafeActions(WebDriver driver, CommonWait wait) {
        return (new SafeActionsWrapper(driver, wait)).getSafeActions();
    }

    private ISafeActions getSafeActions() {
        return (ISafeActions) Proxy.newProxyInstance(ISafeActions.class.getClassLoader(),
                new Class[]{ISafeActions.class}, new SafeActionsInvocationHandler());
    }

    private class SafeActionsInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Object arg : args) {
                if (arg instanceof WebElement) {
                    waitForVisibleElement((WebElement) arg);
                }
            }
            Method realMethod = SafeActions.class.getMethod(method.getName(), method.getParameterTypes());
            try {
                return realMethod.invoke(safeActions, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private void waitForVisibleElement(WebElement element) {
        if (visibleElement == null || !visibleElement.isDisplayed()) {
            wait.waitForCondition(ExpectedConditions.visibilityOf(element));
        }
    }
}
