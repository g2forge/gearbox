package com.g2forge.gearbox.browser.example;

import org.openqa.selenium.By;

import com.g2forge.gearbox.browser.IElement;
import com.g2forge.gearbox.browser.StatefulBrowser;
import com.g2forge.gearbox.browser.operation.ChangeWait;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class ExampleHome {
	protected final StatefulBrowser browser;

	public ExampleMoreInformation openMoreInformation() {
		return getBrowser().change(browser -> browser.getBrowser().find(By.partialLinkText("More information")).operation().wrap(ChangeWait.create()).execute(IElement::click), ExampleMoreInformation::new);
	}

	public String getTitle() {
		return getBrowser().get(browser -> browser.getTitle());
	}
}
