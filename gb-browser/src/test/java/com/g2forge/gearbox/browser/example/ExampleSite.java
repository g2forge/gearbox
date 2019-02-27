package com.g2forge.gearbox.browser.example;

import com.g2forge.gearbox.browser.IBrowser;
import com.g2forge.gearbox.browser.StatefulBrowser;
import com.g2forge.gearbox.browser.operation.LoadWait;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
public class ExampleSite {
	protected final StatefulBrowser browser;

	public ExampleSite(IBrowser browser) {
		this.browser = new StatefulBrowser(browser);
	}

	public ExampleHome open() {
		return getBrowser().change(browser -> browser.operation().wrap(LoadWait.create()).execute(b -> b.go("http://example.com/")), ExampleHome::new);
	}
}
