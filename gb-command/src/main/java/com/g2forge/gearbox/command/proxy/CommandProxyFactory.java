package com.g2forge.gearbox.command.proxy;

import java.lang.reflect.Proxy;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.proxy.transformers.DefaultMethodInvocationTransformer;
import com.g2forge.gearbox.command.proxy.transformers.MetadataDispatchInvocationTransformer;
import com.g2forge.gearbox.command.proxy.transformers.MethodToCommandInvocationTransformer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandProxyFactory implements ICommandProxyFactory {
	@Getter(AccessLevel.PROTECTED)
	protected final ICommandConverterR_ renderer;
	
	@Getter(AccessLevel.PROTECTED)
	protected final IFunction1<? super ProcessInvocation<?>, ? extends IProcess> runner;

	@Getter(value = AccessLevel.PROTECTED, lazy = true)
	private final ProxyInvocationHandler handler = new ProxyInvocationHandler(new DefaultMethodInvocationTransformer(new MetadataDispatchInvocationTransformer(new MethodToCommandInvocationTransformer(getRenderer()))), getRunner());

	@Override
	public <_T> _T apply(Class<_T> type) {
		@SuppressWarnings("unchecked")
		final _T retVal = (_T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, getHandler());
		return retVal;
	}
}
