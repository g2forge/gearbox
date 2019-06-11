package com.g2forge.gearbox.functional.v2.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.functional.v2.process.IProcess;
import com.g2forge.gearbox.functional.v2.process.ProcessInvocation;
import com.g2forge.gearbox.functional.v2.process.ProcessInvocationException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
class ProxyInvocationHandler implements InvocationHandler {
	protected final IFunction1<? super MethodInvocation, ? extends ProcessInvocation<?>> transform;

	protected final IFunction1<? super ProcessInvocation<?>, ? extends IProcess> runner;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final MethodInvocation methodInvocation = new MethodInvocation(proxy, method, HCollection.asList(args));
		final ProcessInvocation<?> processInvocation = getTransform().apply(methodInvocation);

		final IFunction1<? super ProcessInvocation<?>, ? extends IProcess> runner = getRunner();
		if (runner != null) {
			final IProcess process = runner.apply(processInvocation);
			return processInvocation.getResultSupplier().apply(process);
		} else throw new ProcessInvocationException(processInvocation);
	}
}
