package com.g2forge.gearbox.browser.example;

import com.g2forge.gearbox.browser.StatefulBrowser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class ExampleMoreInformation {
	protected final StatefulBrowser browser;

	public String getTitle() {
		return getBrowser().get(browser -> browser.getTitle());
	}
}
