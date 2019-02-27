package com.g2forge.gearbox.browser.example;

import org.junit.Test;

import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.browser.IBrowser;
import com.g2forge.gearbox.browser.selenium.SeleniumBrowser;

public class TestExample {
	@Test
	public void test() {
		try (final IBrowser browser = new SeleniumBrowser()) {
			final ExampleSite site = new ExampleSite(browser);
			final ExampleHome home = site.open();
			HAssert.assertEquals("Example Domain", home.getTitle());
			final ExampleMoreInformation moreInformation = home.openMoreInformation();
			HAssert.assertEquals("IANA — IANA-managed Reserved Domains", moreInformation.getTitle());
		}
	}
}
