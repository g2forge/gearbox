package com.g2forge.gearbox.browser.selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.g2forge.gearbox.browser.IForm;
import com.g2forge.gearbox.browser.ISelect;
import com.g2forge.gearbox.browser.ISelect.IOptionSelector;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

class Form extends Element implements IForm {
	public Form(WebElement element, SeleniumBrowser browser) {
		super(element, browser);
		assertTag(element, "form");
	}

	@Override
	public boolean getBoolean(By by) {
		final WebElement input = element.findElement(by);
		assertInput(input, "checkbox");
		return input.isSelected();
	}

	@Override
	public List<ISelect.IOption> getOptions(By by) {
		final Select select = new Select(element.findElement(by), browser);
		return select.getOptions(ISelect.bySelected(true));
	}

	@Override
	public String getText(By by) {
		final WebElement input = element.findElement(by);
		if (!"textarea".equals(input.getTagName().toLowerCase())) assertInput(input, "email", "number", "text", "password", "file");
		return input.getAttribute("value");
	}

	@Override
	public IOperationBuilder<? extends IForm> operation() {
		return new OperationBuilder<>(this);
	}

	@Override
	public IForm select(By by, IOptionSelector... selectors) {
		final Select select = new Select(element.findElement(by), browser);
		for (IOptionSelector selector : selectors) {
			select.getOption(selector).setSelected(true);
		}
		return this;
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
		if (!"textarea".equals(input.getTagName().toLowerCase())) assertInput(input, "email", "number", "text", "password", "file");
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
