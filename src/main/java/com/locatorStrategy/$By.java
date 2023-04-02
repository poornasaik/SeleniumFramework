package com.locatorStrategy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

public abstract class $By extends By {

	public static ByShadowRootCss shadowRootCss(String shadowRootCss) {
		return new ByShadowRootCss(shadowRootCss);
	}

	public static class ByShadowRootCss extends By {
		private final String[] shadowRootCssArray;
		private final String shadowRootCss;

		private ByShadowRootCss(String shadowRootCss) {
			this.shadowRootCss = shadowRootCss;
			if (!shadowRootCss.contains("#shadow-root") || shadowRootCss.strip().matches(".*#shadow-root$")) {
				throw new IllegalArgumentException(this.toString() + " is not a valid shadow-root css locator");
			}
			shadowRootCssArray = shadowRootCss.split("#shadow-root");
			Arrays.stream(shadowRootCssArray).map(String::strip).toArray(css -> shadowRootCssArray);
		}

		@Override
		public List<WebElement> findElements(SearchContext context) {
			List<SearchContext> shadowRoots = null;
			int counter = 0;
			for (String shadowCss : shadowRootCssArray) {
				List<WebElement> rootElements;
				if (shadowRoots == null) {
					rootElements = context.findElements(By.cssSelector(shadowCss));
				} else {
					rootElements = shadowRoots.stream()
							.flatMap(root -> root.findElements(By.cssSelector(shadowCss)).stream())
							.collect(Collectors.toList());
				}
				for (WebElement root : rootElements) {
					if (shadowRoots == null) {
						shadowRoots = new LinkedList<>();
					}
					SearchContext searchContextEle = null;
					try {
						searchContextEle = root.getShadowRoot();
					} catch (Exception e) {
					}
					if (searchContextEle != null) {
						shadowRoots.add(searchContextEle);
					}
				}
				counter++;
				if (counter >= shadowRootCssArray.length - 1) {
					break;
				}
			}

			List<WebElement> elements = shadowRoots.stream()
					.flatMap(root -> root
							.findElements(By.cssSelector(shadowRootCssArray[shadowRootCssArray.length - 1])).stream())
					.collect(Collectors.toList());
			return elements;
		}

		@Override
		public String toString() {
			return "By.ShadowRootCss: " + shadowRootCss;
		}

	}

}
