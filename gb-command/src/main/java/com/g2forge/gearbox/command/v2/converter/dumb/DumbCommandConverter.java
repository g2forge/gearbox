package com.g2forge.gearbox.command.v2.converter.dumb;

import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.command.runner.redirect.IRedirect;
import com.g2forge.gearbox.command.v2.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.v2.converter.IMethodArgument;
import com.g2forge.gearbox.command.v2.converter.MethodArgument;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation.ProcessInvocationBuilder;

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
			if (working != null) c.getCommand().working(v);
			else {
				final Named named = c.getArgument().getMetadata().getMetadata(Named.class);
				final String string = v.toString();
				c.getCommand().argument(named != null ? named.value() + string : string);
			}
		});

		final IConsumer2<? super ArgumentContext, ? super Boolean> bool = (c, v) -> {
			final Flag flag = c.getArgument().getMetadata().getMetadata(Flag.class);
			if (flag != null) {
				if (v) c.getCommand().argument(flag.value());
				return;
			} else c.getCommand().argument(Boolean.toString(v));
		};
		builder.add(ArgumentContext.class, Boolean.class, bool);
		builder.add(ArgumentContext.class, Boolean.TYPE, bool);
	}).build();

	@Override
	public <T> ProcessInvocation<T> apply(ProcessInvocation<T> processInvocation, MethodInvocation methodInvocation) {
		final ProcessInvocationBuilder<T> processInvocationBuilder = processInvocation.toBuilder();
		final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> commandInvocationBuilder = processInvocation.getInvocation().toBuilder();

		// Compute the command name
		commandInvocationBuilder.clearArguments();
		final Command command = IMetadata.of(methodInvocation.getMethod()).getMetadata(Command.class);
		if (command != null) Stream.of(command.value()).forEach(commandInvocationBuilder::argument);
		else commandInvocationBuilder.argument(methodInvocation.getMethod().getName());

		// Compute the result generator
		/*
		final IExplicitResultHandler resultHandler;
		if ((command != null) && (command.handler() != IExplicitResultHandler.class)) resultHandler = command.handler().newInstance();
		else resultHandler = resultContext.getStandard(resultContext.getType());
		commandInvocationBuilder.io(resultHandler.getRedirects());
		typedInvocationBuilder.resultHandler(resultHandler);*/

		// Sort out all the arguments
		final Parameter[] parameters = methodInvocation.getMethod().getParameters();
		for (int i = 0; i < parameters.length; i++) {
			final Object value = methodInvocation.getArguments().get(i);
			final MethodArgument methodArgument = new MethodArgument(value, parameters[i]);
			final ArgumentContext argumentContext = new ArgumentContext(commandInvocationBuilder, methodArgument);
			ARGUMENT_BUILDER.accept(argumentContext, value);
		}

		processInvocationBuilder.invocation(commandInvocationBuilder.build());
		return processInvocationBuilder.build();
	}
}
