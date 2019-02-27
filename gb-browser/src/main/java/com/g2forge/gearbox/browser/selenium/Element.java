package com.g2forge.gearbox.browser.selenium;

import java.util.Objects;

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
		public <V> V until(IFunction1<? super T, ? extends V> function) {
			return value.getBrowser().operation().until(b -> function.apply(value));
		}

		@Override
		public IOperationBuilder<T> wrap(IFunction1<? super T, IOperationWrapper> factory) {
			return new WrappedOperationBuilder<>(factory, this);
		}
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
