package com.g2forge.gearbox.command.v2.converter.dumb;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.g2forge.alexandria.annotations.message.TODO;
import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.alexandria.java.type.ref.ATypeRef;
import com.g2forge.alexandria.java.type.ref.ATypeRefIdentity;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.command.runner.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.v2.converter.IMethodArgument;
import com.g2forge.gearbox.command.v2.converter.MethodArgument;
import com.g2forge.gearbox.command.v2.process.IProcess;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation.ProcessInvocationBuilder;
import com.g2forge.gearbox.command.v2.proxy.result.BooleanResultSupplier;
import com.g2forge.gearbox.command.v2.proxy.result.IResultSupplier;
import com.g2forge.gearbox.command.v2.proxy.result.IntegerResultSupplier;
import com.g2forge.gearbox.command.v2.proxy.result.ProcessResultSupplier;
import com.g2forge.gearbox.command.v2.proxy.result.StreamResultSupplier;
import com.g2forge.gearbox.command.v2.proxy.result.StringResultSupplier;
import com.g2forge.gearbox.command.v2.proxy.result.VoidResultSupplier;

import lombok.Builder;
import lombok.Data;

public class DumbCommandConverter implements ICommandConverterR_ {
	@Data
	@Builder(toBuilder = true)
	protected static class ArgumentContext {
		protected final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> command;

		protected final IMethodArgument<Object> argument;
	}

	protected static final IConsumer2<ArgumentContext, Object> ARGUMENT_BUILDER = new TypeSwitch2.ConsumerBuilder<ArgumentContext, Object>().with(builder -> {
		builder.add(ArgumentContext.class, String[].class, (c, v) -> {
			if (c.getArgument().getMetadata().isMetadataPresent(Named.class)) throw new IllegalArgumentException("Named string arrays are not supported!");
			for (String value : v) {
				c.getCommand().argument(value);
			}
		});
		builder.add(ArgumentContext.class, String.class, (c, v) -> {
			final Named named = c.getArgument().getMetadata().getMetadata(Named.class);
			c.getCommand().argument(named != null ? named.value() + v : v);
		});
		builder.add(ArgumentContext.class, Path.class, (c, v) -> {
			final Working working = c.getArgument().getMetadata().getMetadata(Working.class);
			if (working != null) {
				if (c.getArgument().getMetadata().isMetadataPresent(Named.class)) throw new IllegalArgumentException("Working directory arguments cannot also be named!");
				c.getCommand().working(v);
			} else {
				final Named named = c.getArgument().getMetadata().getMetadata(Named.class);
				final String string = v.toString();
				c.getCommand().argument(named != null ? named.value() + string : string);
			}
		});

		final IConsumer2<? super ArgumentContext, ? super Boolean> bool = (c, v) -> {
			final Flag flag = c.getArgument().getMetadata().getMetadata(Flag.class);
			final Named named = c.getArgument().getMetadata().getMetadata(Named.class);
			if (flag != null) {
				if (named != null) throw new IllegalArgumentException("Flags cannot also be named!");
				if (v) c.getCommand().argument(flag.value());
				return;
			} else {
				final String string = Boolean.toString(v);
				c.getCommand().argument(named != null ? named.value() + string : string);
			}
		};
		builder.add(ArgumentContext.class, Boolean.class, bool);
		builder.add(ArgumentContext.class, Boolean.TYPE, bool);
	}).build();

	@SuppressWarnings("unchecked")
	protected static <T> ITypeRef<T> computeReturnTypeRef(Method method) {
		return (ITypeRef<T>) new ATypeRefIdentity<Object>() {
			@Override
			public Class<Object> getErasedType() {
				return (Class<Object>) method.getReturnType();
			}

			@Override
			public Type getType() {
				return method.getGenericReturnType();
			}
		};
	}

	@SuppressWarnings("unchecked")
	@TODO(value = "Use static type switch", link = "G2-432")
	protected static <T> IResultSupplier<T> getStandard(ITypeRef<T> type) {
		if (type.getErasedType().isAssignableFrom(Boolean.class) || type.getErasedType().isAssignableFrom(Boolean.TYPE)) return (IResultSupplier<T>) BooleanResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(Integer.class) || type.getErasedType().isAssignableFrom(Integer.TYPE)) return (IResultSupplier<T>) IntegerResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(Void.class) || type.getErasedType().isAssignableFrom(Void.TYPE)) return (IResultSupplier<T>) VoidResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(String.class)) return (IResultSupplier<T>) StringResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(IProcess.class)) return (IResultSupplier<T>) ProcessResultSupplier.create();
		if (new ATypeRef<Stream<String>>() {}.getType().equals(type.getType())) return (IResultSupplier<T>) StreamResultSupplier.create();
		throw new IllegalArgumentException(String.format("Return type \"%1$s\" is not supported!", type.getType()));
	}

	@Override
	public <T> ProcessInvocation<T> apply(ProcessInvocation<T> processInvocation, MethodInvocation methodInvocation) {
		final ProcessInvocationBuilder<T> processInvocationBuilder = processInvocation.toBuilder();
		final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> commandInvocationBuilder = processInvocation.getCommandInvocation() != null ? processInvocation.getCommandInvocation().toBuilder() : CommandInvocation.builder();

		// Compute the command name
		commandInvocationBuilder.clearArguments();
		final Command command = IMetadata.of(methodInvocation.getMethod()).getMetadata(Command.class);
		if (command != null) Stream.of(command.value()).forEach(commandInvocationBuilder::argument);
		else commandInvocationBuilder.argument(methodInvocation.getMethod().getName());

		// Compute the result generator
		if (processInvocation.getResultSupplier() == null) {
			final ITypeRef<T> typeRef = computeReturnTypeRef(methodInvocation.getMethod());
			final IResultSupplier<T> standard = getStandard(typeRef);
			processInvocationBuilder.resultSupplier(standard);
		}

		// Sort out all the arguments
		final Parameter[] parameters = methodInvocation.getMethod().getParameters();
		for (int i = 0; i < parameters.length; i++) {
			final Object value = methodInvocation.getArguments().get(i);
			final MethodArgument methodArgument = new MethodArgument(value, parameters[i]);
			final ArgumentContext argumentContext = new ArgumentContext(commandInvocationBuilder, methodArgument);
			ARGUMENT_BUILDER.accept(argumentContext, value);
		}

		processInvocationBuilder.commandInvocation(commandInvocationBuilder.build());
		return processInvocationBuilder.build();
	}
}
