package com.g2forge.gearbox.browser.selenium;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import com.g2forge.alexandria.java.core.helpers.HStream;
import com.g2forge.gearbox.browser.ISelect;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

import lombok.AccessLevel;
import lombok.Getter;

class Select extends Element implements ISelect {
	class Option extends Element implements ISelect.IOption {
		public Option(WebElement element, SeleniumBrowser browser) {
			super(element, browser);
			if (!"option".equals(element.getTagName().toLowerCase())) throw new IllegalArgumentException();
		}

		@Override
		public boolean isSelected() {
			return element.isSelected();
		}

		@Override
		public void setSelected(boolean selected) {
			if (isSelected() != selected) {
				click();
			}
		}
	}

	protected final org.openqa.selenium.support.ui.ISelect internal;

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final List<IOption> allOptions = internal.getOptions().stream().map(e -> new Option(e, browser)).collect(Collectors.toList());

	public Select(WebElement element, SeleniumBrowser browser) {
		super(element, browser);
		if (!"select".equals(element.getTagName().toLowerCase())) throw new IllegalArgumentException();
		internal = new org.openqa.selenium.support.ui.Select(element);
	}

	@Override
	public void deselectAll() {
		internal.deselectAll();
	}

	@Override
	public IOption getFirstSelected() {
		return new Option(internal.getFirstSelectedOption(), browser);
	}

	@Override
	public IOption getOptionByIndex(int index) {
		final String string = String.valueOf(index);
		return HStream.findOne(getOptions(false).stream().filter(o -> string.equals(o.getAttribute("index"))));
	}

	@Override
	public List<IOption> getOptions(boolean selected) {
		if (!selected) return getAllOptions();
		return internal.getAllSelectedOptions().stream().map(e -> new Option(e, browser)).collect(Collectors.toList());
	}

	@Override
	public List<IOption> getOptionsByText(String text) {
		if (!text.contains(" ")) return element.findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(text) + "]")).stream().map(e -> new Option(e, browser)).collect(Collectors.toList());

		final String longest = Stream.of(text.split(" +")).sorted((s0, s1) -> s1.length() - s0.length()).findFirst().get();
		final Stream<WebElement> stream;
		if (longest.length() < 1) stream = element.findElements(By.tagName("option")).stream();
		else stream = element.findElements(By.xpath(".//option[contains(., " + Quotes.escape(longest) + ")]")).stream();
		return stream.filter(e -> text.equals(e.getText())).map(e -> new Option(e, browser)).collect(Collectors.toList());
	}

	@Override
	public List<IOption> getOptionsByValue(String value) {
		return element.findElements(By.xpath(".//option[@value = " + Quotes.escape(value) + "]")).stream().map(e -> new Option(e, browser)).collect(Collectors.toList());
	}

	@Override
	public boolean isMultiple() {
		return internal.isMultiple();
	}

	@Override
	public IOperationBuilder<? extends ISelect> operation() {
		return new OperationBuilder<>(this);
	}
}
