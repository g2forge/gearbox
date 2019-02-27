package com.g2forge.gearbox.browser;

import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction1;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
public class StatefulBrowser {
	protected volatile transient boolean open = true;

	protected final IBrowser browser;

	public StatefulBrowser(IBrowser browser) {
		this.browser = browser;
	}

	public <T> T back(IConsumer1<? super IBrowser> consumer, T value, IFunction1<? super T, ? extends StatefulBrowser> accessor) {
		if (!isOpen()) throw new IllegalStateException();
		final IBrowser browser = getBrowser();
		consumer.accept(browser);

		this.open = false;
		accessor.apply(value).open = true;
		return value;
	}

	public <T> T change(IConsumer1<? super IBrowser> consumer, IFunction1<? super StatefulBrowser, ? extends T> constructor) {
		if (!isOpen()) throw new IllegalStateException();
		final IBrowser browser = getBrowser();
		consumer.accept(browser);

		this.open = false;
		final StatefulBrowser newState = new StatefulBrowser(browser);
		return constructor.apply(newState);
	}

	public <T> T get(IFunction1<? super IBrowser, ? extends T> function) {
		if (!isOpen()) throw new IllegalStateException();
		return function.apply(getBrowser());
	}
}
