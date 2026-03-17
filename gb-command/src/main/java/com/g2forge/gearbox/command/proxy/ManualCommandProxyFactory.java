package com.g2forge.gearbox.command.proxy;

import com.g2forge.alexandria.java.function.builder.IBuilder;
import com.g2forge.alexandria.java.function.type.MapTypeFunction1;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class ManualCommandProxyFactory implements ICommandProxyFactory {
	public static class ManualCommandProxyFactoryBuilder implements IBuilder<ManualCommandProxyFactory> {
		protected final MapTypeFunction1<Object> function = new MapTypeFunction1<>();

		protected ICommandProxyFactory fallback = null;

		@Override
		public ManualCommandProxyFactory build() {
			return new ManualCommandProxyFactory(function, fallback);
		}

		public ManualCommandProxyFactoryBuilder fallback(ICommandProxyFactory fallback) {
			this.fallback = fallback;
			return this;
		}

		public <T> ManualCommandProxyFactoryBuilder proxy(final Class<T> type, final T value) {
			function.put(type, value);
			return this;
		}
	}

	public static ManualCommandProxyFactoryBuilder builder() {
		return new ManualCommandProxyFactoryBuilder();
	}

	protected final MapTypeFunction1<Object> function;

	protected final ICommandProxyFactory fallback;

	@Override
	public <_T> _T apply(Class<_T> type) {
		final _T retVal = getFunction().apply(type);
		if (retVal != null) return retVal;
		return getFallback().apply(type);
	}
}
