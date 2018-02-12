package com.g2forge.gearbox.functional.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.typeswitch.TypeSwitch2;
import com.g2forge.gearbox.functional.control.Constant;
import com.g2forge.gearbox.functional.control.Explicit;
import com.g2forge.gearbox.functional.control.Flag;
import com.g2forge.gearbox.functional.control.IExplicitArgumentHandler;
import com.g2forge.gearbox.functional.control.IExplicitResultHandler;
import com.g2forge.gearbox.functional.control.IResultContext;
import com.g2forge.gearbox.functional.control.Named;
import com.g2forge.gearbox.functional.control.Working;
import com.g2forge.gearbox.functional.runner.Command;
import com.g2forge.gearbox.functional.runner.IProcess;
import com.g2forge.gearbox.functional.runner.IRunner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
class ProxyInvocationHandler implements InvocationHandler {
	protected static final IConsumer2<ArgumentContext, Object> argumentBuilder = new TypeSwitch2.ConsumerBuilder<ArgumentContext, Object>().with(builder -> {
		builder.add(ArgumentContext.class, String[].class, (c, v) -> {
			for (String value : v) {
				c.getCommand().argument(value);
			}
		});
		builder.add(ArgumentContext.class, String.class, (c, v) -> {
			final Named named = c.getArgument().getAnnotation(Named.class);
			c.getCommand().argument(named != null ? named.value() + v : v);
		});
		builder.add(ArgumentContext.class, Path.class, (c, v) -> {
			final Working working = c.getArgument().getAnnotation(Working.class);
			if (working != null) c.getCommand().working(v);
			else {
				final Named named = c.getArgument().getAnnotation(Named.class);
				final String string = v.toString();
				c.getCommand().argument(named != null ? named.value() + string : string);
			}
		});

		final IConsumer2<? super ArgumentContext, ? super Boolean> bool = (c, v) -> {
			final Flag flag = c.getArgument().getAnnotation(Flag.class);
			if (flag != null) {
				if (v) c.getCommand().argument(flag.value());
				return;
			} else c.getCommand().argument(Boolean.toString(v));
		};
		builder.add(ArgumentContext.class, Boolean.class, bool);
		builder.add(ArgumentContext.class, Boolean.TYPE, bool);
	}).build();

	@Getter(AccessLevel.PROTECTED)
	protected final IRunner runner;

	protected void constant(final Command.CommandBuilder commandBuilder, final Constant constant) {
		if (constant != null) commandBuilder.arguments(HCollection.asList(constant.value()));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Command.CommandBuilder commandBuilder = Command.builder();

		// Compute the command name
		final com.g2forge.gearbox.functional.control.Command command = method.getAnnotation(com.g2forge.gearbox.functional.control.Command.class);
		if (command != null) Stream.of(command.value()).forEach(commandBuilder::argument);
		else commandBuilder.argument(method.getName());

		// Compute the result handler
		final IResultContext resultContext = new ResultContext(method);
		final IExplicitResultHandler resultHandler;
		if ((command != null) && (command.handler() != IExplicitResultHandler.class)) resultHandler = command.handler().newInstance();
		else resultHandler = resultContext.getStandard(resultContext.getType());

		// Sort out all the arguments
		final Parameter[] parameters = method.getParameters();
		for (int i = 0; i < method.getParameterCount(); i++) {
			final Argument argument = new Argument(args[i], parameters[i]);
			final ArgumentContext argumentContext = new ArgumentContext(new CommandBuilder(commandBuilder), argument);

			final Explicit explicit = argument.getAnnotation(Explicit.class);
			if (explicit != null) {
				final IExplicitArgumentHandler handler = explicit.value().newInstance();
				handler.accept(argumentContext, argument.get());
			} else argumentBuilder.accept(argumentContext, argument.get());

			constant(commandBuilder, argument.getAnnotation(Constant.class));
		}
		final IProcess process = getRunner().run(commandBuilder.build());
		return resultHandler.apply(process, resultContext);
	}
}