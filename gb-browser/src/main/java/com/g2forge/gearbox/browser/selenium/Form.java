package com.g2forge.gearbox.browser.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.g2forge.gearbox.browser.IForm;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

class Form extends Element implements IForm {
	public Form(WebElement element, SeleniumBrowser browser) {
		super(element, browser);
		assertTag(element, "form");
	}

	@Override
	public IOperationBuilder<? extends IForm> operation() {
		return new OperationBuilder<>(this);
	}

	@Override
	public IForm set(By by, boolean selected) {
		final WebElement input = element.findElement(by);
		assertInput(input, "checkbox");
		if (input.isSelected() != selected) input.click();
		return this;
	}

	@Override
	public IForm set(By by, String text) {
		final WebElement input = element.findElement(by);
		assertInput(input, "email", "number", "text");
		input.clear();
		input.sendKeys(text);
		return this;
	}

	@Override
	public IForm submit() {
		element.submit();
		return this;
	}
}
