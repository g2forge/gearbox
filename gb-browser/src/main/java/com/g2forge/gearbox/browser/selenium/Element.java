package com.g2forge.gearbox.browser.selenium;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.browser.IBrowsable;
import com.g2forge.gearbox.browser.IBrowser;
import com.g2forge.gearbox.browser.IElement;
import com.g2forge.gearbox.browser.operation.IOperationBuilder;
import com.g2forge.gearbox.browser.operation.IOperationWrapper;
import com.g2forge.gearbox.browser.operation.WrappedOperationBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Element implements IElement {
	@RequiredArgsConstructor
	protected static class OperationBuilder<T extends IBrowsable> implements IOperationBuilder<T> {
		protected final T value;

		@Override
		public T execute(IConsumer1<? super T> operation) {
			operation.accept(value);
			return value;
		}

		@Override
		public <V> V until(int seconds, IFunction1<? super T, ? extends V> function) {
			return value.getBrowser().operation().until(seconds, b -> function.apply(value));
		}

		@Override
		public IOperationBuilder<T> wrap(IFunction1<? super T, IOperationWrapper> factory) {
			return new WrappedOperationBuilder<>(factory, this);
		}
	}

	protected static void assertInput(WebElement element, String... types) {
		assertTag(element, "input");
		final String actual = element.getAttribute("type");
		if (types.length == 1) {
			final String expected = types[0];
			if (!expected.toLowerCase().equals(actual.toLowerCase())) throw new IllegalArgumentException(String.format("Expected input type %1$s, found %2$s", expected, actual));
		} else {
			final Set<String> set = Stream.of(types).map(String::toLowerCase).collect(Collectors.toSet());
			if (!set.contains(actual.toLowerCase())) throw new IllegalArgumentException(String.format("Expected input type to be one of %1$s, found %2$s", set, actual));
		}
	}

	protected static void assertTag(WebElement element, String tag) {
		if (!tag.toLowerCase().equals(element.getTagName().toLowerCase())) throw new IllegalArgumentException(String.format("Expected tag %1$s, found %2$s", tag, element.getTagName()));
	}

	protected final WebElement element;

	protected final SeleniumBrowser browser;

	@Override
	public IElement clear() {
		element.clear();
		return this;
	}

	@Override
	public IElement click() {
		element.click();
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		final Element other = (Element) obj;
		return Objects.equals(element, other.element);
	}

	@Override
	public String getAttribute(String attribute) {
		return element.getAttribute(attribute);
	}

	@Override
	public IBrowser getBrowser() {
		return browser;
	}

	@Override
	public String getText() {
		return element.getText();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(element);
	}

	@Override
	public boolean isDisplayed() {
		return element.isDisplayed();
	}

	@Override
	public IElement moveTo() {
		new Actions(browser.driver).moveToElement(element).build().perform();
		return this;
	}

	@Override
	public IOperationBuilder<? extends IElement> operation() {
		return new OperationBuilder<>(this);
	}

	@Override
	public IElement send(String text) {
		element.sendKeys(text);
		return this;
	}
}
