package com.g2forge.gearbox.browser;

import org.junit.Test;

import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.browser.IBrowser;
import com.g2forge.gearbox.browser.operation.LoadWait;
import com.g2forge.gearbox.browser.selenium.SeleniumBrowser;

public class TestBrowser {
	@Test
	public void example() {
		try (final IBrowser browser = new SeleniumBrowser()) {
			browser.operation().wrap(LoadWait.create()).execute(b -> b.go("http://example.com/"));
			HAssert.assertEquals("Example Domain", browser.getTitle());
		}
	}
}
