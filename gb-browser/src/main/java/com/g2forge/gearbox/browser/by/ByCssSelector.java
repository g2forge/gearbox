package com.g2forge.gearbox.browser.by;

import org.openqa.selenium.By;

public class ByCssSelector extends By.ByCssSelector {
	private static final long serialVersionUID = 5902571384794214553L;

	public static String idToSelector(String identifier) {
		if (Character.isDigit(identifier.charAt(0))) return "#\\" + Integer.toHexString(identifier.charAt(0)) + " " + identifier.substring(1);
		return "#" + identifier;
	}

	public ByCssSelector(String cssSelector) {
		super(cssSelector);
	}
}
