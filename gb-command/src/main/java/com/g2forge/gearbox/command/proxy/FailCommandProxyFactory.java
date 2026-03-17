package com.g2forge.gearbox.command.proxy;

import com.g2forge.alexandria.java.core.marker.ISingleton;

public class FailCommandProxyFactory implements ICommandProxyFactory, ISingleton {
	protected static final FailCommandProxyFactory INSTANCE = new FailCommandProxyFactory();

	public static FailCommandProxyFactory create() {
		return INSTANCE;
	}

	protected FailCommandProxyFactory() {}

	@Override
	public <_T> _T apply(Class<_T> type) {
		throw new IllegalArgumentException(String.format("No proxy available for command interface type %1$s", type.getSimpleName()));
	}
}
