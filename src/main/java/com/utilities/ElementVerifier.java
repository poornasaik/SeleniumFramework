package com.utilities;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class ElementVerifier {
	private WebElement element;
	private List<VerificationType> verifications = new ArrayList<>();
	private String attributeName, attributeValue, expectedText, expectedColorHex;

	private enum VerificationType {
		DISPLAYED, NOT_DISPLAYED, ENABLED, NOT_ENABLED, SELECTED, NOT_SELECTED, HAS_ATTRIBUTE, HAS_TEXT, HAS_COLOR
	}

	private ElementVerifier(WebElement element) {
		this.element = element;
	}

	public static ElementVerifier forElement(WebElement element) {
		return new ElementVerifier(element);
	}

	public ElementVerifier isDisplayed() {
		verifications.add(VerificationType.DISPLAYED);
		return this;
	}

	public ElementVerifier isNotDisplayed() {
		verifications.add(VerificationType.NOT_DISPLAYED);
		return this;
	}

	public ElementVerifier isEnabled() {
		verifications.add(VerificationType.ENABLED);
		return this;
	}

	public ElementVerifier isNotEnabled() {
		verifications.add(VerificationType.NOT_ENABLED);
		return this;
	}

	public ElementVerifier isSelected() {
		verifications.add(VerificationType.SELECTED);
		return this;
	}

	public ElementVerifier isNotSelected() {
		verifications.add(VerificationType.NOT_SELECTED);
		return this;
	}

	public ElementVerifier hasAttribute(String attributeName, String expectedValue) {
		this.attributeName = attributeName;
		this.attributeValue = expectedValue;
		verifications.add(VerificationType.HAS_ATTRIBUTE);
		return this;
	}

	public ElementVerifier hasText(String expectedText) {
		this.expectedText = expectedText;
		verifications.add(VerificationType.HAS_TEXT);
		return this;
	}

	public ElementVerifier hasColor(String expectedColorHex) {
		this.expectedColorHex = expectedColorHex;
		verifications.add(VerificationType.HAS_COLOR);
		return this;
	}

	private class DisplayedVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			assert element.isDisplayed() : "Element is not displayed.";
		}
	}

	private class NotDisplayedVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			boolean[] isDisplayed = { false };
			try {
				isDisplayed[0] = element.isDisplayed();
			} catch (WebDriverException e) {
				isDisplayed[0] = false;
			}
			assert !isDisplayed[0] : "Element is not displayed.";
		}
	}

	private class EnabledVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			assert element.isEnabled() : "Element is not enabled.";
		}

	}

	private class NotEnabledVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			assert !element.isEnabled() : "Element is enabled.";
		}

	}

	private class SelectedVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			assert element.isSelected() : "Element is not selected.";
		}
	}

	private class NotSelectedVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			assert !element.isSelected() : "Element is selected.";
		}
	}

	private class AttributeVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			String actualValue = element.getAttribute(attributeName);
			assert actualValue.equals(attributeValue) : "Element attribute " + attributeName + " has value "
					+ actualValue + ", but expected value is " + attributeValue;
		}
	}

	private class TextVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			String actualText = element.getText();
			assert actualText.equals(expectedText) : "Element has text " + actualText + ", but expected text is "
					+ expectedText;
		}
	}

	private class ColorVerificationExecutor implements VerificationExecutor {
		@Override
		public void execute() {
			String actualColorHex = element.getCssValue("color");
			Color actualColor = Color.decode(actualColorHex);
			Color expectedColor = Color.decode(expectedColorHex);
			assert actualColor.equals(expectedColor) : "Element color is " + actualColorHex + ", but expected color is "
					+ expectedColorHex;
		}
	}

	public class NotObscuredVerificationExecutor implements VerificationExecutor {
		private JavascriptExecutor js;

		public NotObscuredVerificationExecutor(JavascriptExecutor js) {
			this.js = js;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void execute() {
			String script = "var element = arguments[0];" + "var clientRect = element.getBoundingClientRect();"
					+ "var points = [{" + "    x: clientRect.left," + "    y: clientRect.top" + "}, {"
					+ "    x: clientRect.right," + "    y: clientRect.top" + "}, {" + "    x: clientRect.right,"
					+ "    y: clientRect.bottom" + "}, {" + "    x: clientRect.left," + "    y: clientRect.bottom"
					+ "}];" + "return points.map(point => document.elementsFromPoint(point.x, point.y));";
			List<List<WebElement>> elementsAtPoints = (List<List<WebElement>>) js.executeScript(script, element);

			boolean isObscured = false;
			outerLoop: for (List<WebElement> elementsAtPoint : elementsAtPoints) {
				for (WebElement el : elementsAtPoint) {
					if (!el.equals(element) && el.isDisplayed()) {
						isObscured = true;
						break outerLoop;
					}
				}
			}
			assert !isObscured : "Element is obscured by other elements.";
		}
	}

	public class AttributePresenceVerificationExecutor implements VerificationExecutor {
		private String attributeName;

		public AttributePresenceVerificationExecutor(String attributeName) {
			this.attributeName = attributeName;
		}

		@Override
		public void execute() {
			String attributeValue = element.getAttribute(attributeName);
			assert attributeValue != null : "Element does not have the attribute: " + attributeName+" has value";
		}
	}

	public class SizeInRangeVerificationExecutor implements VerificationExecutor {
		private Dimension minSize;
		private Dimension maxSize;

		public SizeInRangeVerificationExecutor(Dimension minSize, Dimension maxSize) {
			this.minSize = minSize;
			this.maxSize = maxSize;
		}

		@Override
		public void execute() {
			Dimension size = element.getSize();
			assert size.width >= minSize.width && size.width <= maxSize.width && size.height >= minSize.height
					&& size.height <= maxSize.height : "Element's size is not within the specified range.";
		}
	}

	public class LocatedInAreaVerificationExecutor implements VerificationExecutor {
		private Rectangle area;

		public LocatedInAreaVerificationExecutor(Rectangle area) {
			this.area = area;
		}

		@Override
		public void execute() {
			org.openqa.selenium.Rectangle elementLoc = element.getRect();
			assert area.contains(toAwtRectangle(elementLoc)) : "Element is not located within the specified area."; 
		}
	}
	public class LocatedInsideElementsVerificationExecutor implements VerificationExecutor {
		private List<WebElement> otherElements;
		
		public LocatedInsideElementsVerificationExecutor(List<WebElement> otherElements) {
			this.otherElements = otherElements;
		}
		
		@Override
		public void execute() {
			StringBuilder builder = new StringBuilder();
			Rectangle elementRect = toAwtRectangle(element.getRect());
            for (WebElement parent : otherElements) {
                if (!toAwtRectangle(parent.getRect()).contains(elementRect)) {
                    builder.append("Element is not inside the provided Element: "+parent+"\n");
                }
            }
            assert builder.toString().isEmpty(): builder.toString();
		}
	}
	public class CollistionElementsVerificationExecutor implements VerificationExecutor {
		private List<WebElement> otherElements;
		
		public CollistionElementsVerificationExecutor(List<WebElement> otherElements) {
			this.otherElements = otherElements;
		}
		
		@Override
		public void execute() {
			StringBuilder builder = new StringBuilder();
			Rectangle elementRect = toAwtRectangle(element.getRect());
            for (WebElement parent : otherElements) {
                if (toAwtRectangle(parent.getRect()).intersects(elementRect)) {
                    builder.append("Element is intersecting with the provided Element: "+parent+"\n");
                }
            }
            assert builder.toString().isEmpty(): builder.toString();
		}
	}
	public class TextAreaVerificationExecutor implements VerificationExecutor {
		WebDriver driver;
		public TextAreaVerificationExecutor(WebDriver driver) {
			this.driver = driver;
		}
		
		@Override
		public void execute() {
			String script = "var rect = arguments[0].getBoundingClientRect();" +
                    "var textRect = arguments[0].querySelector('text').getBoundingClientRect();" +
                    "return rect.contains(textRect);";
            JavascriptExecutor js = (JavascriptExecutor) driver;
            boolean textInside = (Boolean) js.executeScript(script, element);
            assert textInside : "Element's text is not inside its container";
            }
	}

	public class CssStyleVerificationExecutor implements VerificationExecutor {
		private String cssProperty;
		private String expectedCSSValue;

		public CssStyleVerificationExecutor(String cssProperty, String expectedValue) {
			this.cssProperty = cssProperty;
			this.expectedCSSValue = expectedValue;
		}

		@Override
	    public void execute() {
	        String actualValue = element.getCssValue(cssProperty);
	        assert actualValue.equals(expectedCSSValue) : "Element's CSS style for Property: "+ cssProperty+" has value "
			+ actualValue + ", but expected value is " + expectedCSSValue;
	    }
	}

	public void verify() {
		List<String> assertionMessages = new ArrayList<>();
		for (VerificationType verification : verifications) {
			switch (verification) {
			case DISPLAYED:
				executeVerification(DisplayedVerificationExecutor::new, assertionMessages);
				break;
			case NOT_DISPLAYED:
				executeVerification(NotDisplayedVerificationExecutor::new, assertionMessages);
				break;
			case ENABLED:
				executeVerification(EnabledVerificationExecutor::new, assertionMessages);
				break;
			case NOT_ENABLED:
				executeVerification(NotEnabledVerificationExecutor::new, assertionMessages);
				break;
			case SELECTED:
				executeVerification(SelectedVerificationExecutor::new, assertionMessages);
				break;
			case NOT_SELECTED:
				executeVerification(NotSelectedVerificationExecutor::new, assertionMessages);
				break;
			case HAS_ATTRIBUTE:
				executeVerification(AttributeVerificationExecutor::new, assertionMessages);
				break;
			case HAS_TEXT:
				executeVerification(TextVerificationExecutor::new, assertionMessages);
				break;
			case HAS_COLOR:
				executeVerification(ColorVerificationExecutor::new, assertionMessages);
				break;
			default:
				throw new IllegalArgumentException("Unknown verification type: " + verification);
			}
		}

		if (!assertionMessages.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("The following assertions failed:\n");
			for (String message : assertionMessages) {
				errorMessage.append("- ").append(message).append("\n");
			}
			throw new AssertionError(errorMessage.toString());
		}
	}

	private void executeVerification(Supplier<VerificationExecutor> executorSupplier, List<String> assertionMessages) {
		try {
			VerificationExecutor executor = executorSupplier.get();
			executor.execute();
		} catch (AssertionError | WebDriverException e) {
			assertionMessages.add(e.getMessage());
		}
	}

	private interface VerificationExecutor {
		void execute();
	}
	
	
	private java.awt.Rectangle toAwtRectangle(org.openqa.selenium.Rectangle seleniumRect) {
	    return new java.awt.Rectangle(seleniumRect.x, seleniumRect.y, seleniumRect.width, seleniumRect.height);
	}

}
