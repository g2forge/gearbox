package com.g2forge.gearbox.command.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.proxy.transformers.DefaultMethodInvocationTransformer;
import com.g2forge.gearbox.command.proxy.transformers.MetadataDispatchInvocationTransformer;
import com.g2forge.gearbox.command.proxy.transformers.MethodToCommandInvocationTransformer;
import com.g2forge.habitat.metadata.IMetadata;
import com.g2forge.habitat.metadata.Metadata;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandProxyFactory implements ICommandProxyFactory {
	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private static final IMetadata metadata = Metadata.getStandard();

	@Getter(AccessLevel.PROTECTED)
	protected final ICommandConverterR_ renderer;

	@Getter(AccessLevel.PROTECTED)
	protected final IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> runner;

	@Getter(value = AccessLevel.PROTECTED, lazy = true)
	private final ProxyInvocationHandler handler = new ProxyInvocationHandler(new DefaultMethodInvocationTransformer(new MetadataDispatchInvocationTransformer(new MethodToCommandInvocationTransformer(getRenderer()))), getRunner());

	@Override
	public <_T> _T apply(Class<_T> type) {
		final IMetadata metadata = getMetadata();
		final CommandFactory annotation = metadata.of(type).get(CommandFactory.class);
		if (annotation != null) {
			final ICommandFactory<?> commandFactory;
			try {
				commandFactory = annotation.value().getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(String.format("Failed to instantiate command factory - found in @%2$s(%3$s.class) on %1$s", type, CommandFactory.class, annotation.value()), e);
			}
			final Object command = commandFactory.create(this);
			return type.cast(command);
		}

		@SuppressWarnings("unchecked")
		final _T retVal = (_T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, getHandler());
		return retVal;
	}
}
