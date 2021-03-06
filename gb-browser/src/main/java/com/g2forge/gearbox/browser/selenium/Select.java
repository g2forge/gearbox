package com.g2forge.gearbox.browser.selenium;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

import com.g2forge.alexandria.java.function.IFunction2;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.alexandria.java.type.function.TypeSwitch2.FunctionBuilder;
import com.g2forge.gearbox.browser.ISelect;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;

import lombok.AccessLevel;
import lombok.Getter;

class Select extends Element implements ISelect {
	static class Option extends Element implements ISelect.IOption {
		public Option(WebElement element, SeleniumBrowser browser) {
			super(element, browser);
			assertTag(element, "option");
		}

		@Override
		public String getValue() {
			return getAttribute("value");
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

	protected static final IFunction2<IOptionSelector, Select, List<IOption>> SELECT;

	static {
		final FunctionBuilder<IOptionSelector, Select, List<IOption>> builder = new TypeSwitch2.FunctionBuilder<IOptionSelector, Select, List<IOption>>();
		builder.add(OptionSelectAll.class, Select.class, (selector, select) -> select.getAllOptions());
		builder.add(OptionSelectByIndex.class, Select.class, (selector, select) -> {
			final String string = String.valueOf(selector.getIndex());
			return select.getAllOptions().stream().filter(o -> string.equals(o.getAttribute("index"))).collect(Collectors.toList());
		});
		builder.add(OptionSelectBySelected.class, Select.class, (selector, select) -> {
			final boolean selected = selector.isSelected();
			if (selected) return select.internal.getAllSelectedOptions().stream().map(e -> new Option(e, select.browser)).collect(Collectors.toList());
			return select.getAllOptions().stream().filter(o -> !o.isSelected()).collect(Collectors.toList());
		});
		builder.add(OptionSelectByText.class, Select.class, (selector, select) -> {
			final String text = selector.getText();
			if (!text.contains(" ")) return select.element.findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(text) + "]")).stream().map(e -> new Option(e, select.browser)).collect(Collectors.toList());

			final String longest = Stream.of(text.split(" +")).sorted((s0, s1) -> s1.length() - s0.length()).findFirst().get();
			final Stream<WebElement> stream;
			if (longest.length() < 1) stream = select.element.findElements(By.tagName("option")).stream();
			else stream = select.element.findElements(By.xpath(".//option[contains(., " + Quotes.escape(longest) + ")]")).stream();
			return stream.filter(e -> text.equals(e.getText())).map(e -> new Option(e, select.browser)).collect(Collectors.toList());
		});
		builder.add(OptionSelectByValue.class, Select.class, (selector, select) -> {
			final String value = selector.getValue();
			return select.element.findElements(By.xpath(".//option[@value = " + Quotes.escape(value) + "]")).stream().map(e -> new Option(e, select.browser)).collect(Collectors.toList());
		});
		SELECT = builder.build();
	}

	protected final org.openqa.selenium.support.ui.ISelect internal;

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final List<IOption> allOptions = internal.getOptions().stream().map(e -> new Option(e, browser)).collect(Collectors.toList());

	public Select(WebElement element, SeleniumBrowser browser) {
		super(element, browser);
		assertTag(element, "select");
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
	public List<IOption> getOptions(IOptionSelector selector) {
		return SELECT.apply(selector, this);
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
