package com.g2forge.gearbox.browser.operation;

import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction1;

public interface IOperationBuilder<T> {
	public T execute(IConsumer1<? super T> operation);

	public default <V> V until(IFunction1<? super T, ? extends V> function) {
		return until(30, function);
	}

	public <V> V until(int seconds, IFunction1<? super T, ? extends V> function);

	public default IOperationBuilder<T> wrap(IFunction1<? super T, IOperationWrapper> factory) {
		return new WrappedOperationBuilder<>(factory, this);
	}
}
