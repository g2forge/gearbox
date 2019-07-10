package com.g2forge.gearbox.browser.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.g2forge.gearbox.browser.IForm;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

class Form extends Element implements IForm {
	public Form(WebElement element, SeleniumBrowser browser) {
		super(element, browser);
		if (!"form".equals(element.getTagName().toLowerCase())) throw new IllegalArgumentException();
	}

	@Override
	public IOperationBuilder<? extends IForm> operation() {
		return new OperationBuilder<>(this);
	}

	@Override
	public IForm set(By by, String text) {
		final WebElement field = element.findElement(by);
		field.clear();
		field.sendKeys(text);
		return this;
	}

	@Override
	public IForm submit() {
		element.submit();
		return this;
	}
}
