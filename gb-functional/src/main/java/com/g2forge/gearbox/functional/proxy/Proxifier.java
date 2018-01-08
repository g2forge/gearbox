package com.g2forge.gearbox.functional.proxy;

import java.lang.reflect.Proxy;

import com.g2forge.gearbox.functional.runner.IRunner;

public class Proxifier implements IProxifier {
	@Override
	public <T> T generate(IRunner runner, Class<T> type) {
		@SuppressWarnings("unchecked")
		final T retVal = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, new ProxyInvocationHandler(runner));
		return retVal;
	}
}
