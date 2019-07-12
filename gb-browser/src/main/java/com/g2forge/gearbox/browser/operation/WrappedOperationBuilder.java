package com.g2forge.gearbox.browser.operation;

import com.g2forge.alexandria.java.function.IConsumer1;
import com.g2forge.alexandria.java.function.IFunction1;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WrappedOperationBuilder<T> implements IOperationBuilder<T> {
	protected final IFunction1<? super T, IOperationWrapper> factory;

	protected final IOperationBuilder<T> operation;

	@Override
	public T execute(IConsumer1<? super T> operation) {
		return this.operation.execute(t -> {
			final IOperationWrapper wrapper = factory.apply(t);
			operation.curry(t).wrap(wrapper::pre, wrapper::post).run();
		});
	}

	@Override
	public <V> V until(int seconds, IFunction1<? super T, ? extends V> function) {
		return this.operation.until(seconds, t -> {
			final IOperationWrapper wrapper = factory.apply(t);
			return function.curry(t).wrap(wrapper::pre, wrapper::post);
		}).get();
	}

	@Override
	public IOperationBuilder<T> wrap(IFunction1<? super T, IOperationWrapper> factory) {
		return new WrappedOperationBuilder<>(factory, this);
	}
}