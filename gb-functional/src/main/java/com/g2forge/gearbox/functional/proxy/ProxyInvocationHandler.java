package com.g2forge.gearbox.functional.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;

import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.typeswitch.TypeSwitch2;
import com.g2forge.gearbox.functional.argument.Explicit;
import com.g2forge.gearbox.functional.argument.Flag;
import com.g2forge.gearbox.functional.argument.IArgument;
import com.g2forge.gearbox.functional.argument.IArgumentContext;
import com.g2forge.gearbox.functional.argument.IExplicitArgumentHandler;
import com.g2forge.gearbox.functional.argument.Working;
import com.g2forge.gearbox.functional.runner.Command;
import com.g2forge.gearbox.functional.runner.IProcess;
import com.g2forge.gearbox.functional.runner.IRunner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
class ProxyInvocationHandler implements InvocationHandler {
	@Data
	@Builder
	@AllArgsConstructor
	protected static class ArgumentContext implements IArgumentContext {
		protected final Command.CommandBuilder command;

		protected final IArgument<Object> argument;
	}

	protected static final IConsumer2<ArgumentContext, Object> argumentBuilder = new TypeSwitch2.ConsumerBuilder<ArgumentContext, Object>().with(builder -> {
		builder.add(ArgumentContext.class, String[].class, (c, v) -> {
			for (String value : v) {
				c.getCommand().argument(value);
			}
		});
		builder.add(ArgumentContext.class, Path.class, (c, v) -> {
			final Working working = c.getArgument().getAnnotation(Working.class);
			if (working != null) c.getCommand().working(v);
			else c.getCommand().argument(v.toString());
		});

		final IConsumer2<? super ArgumentContext, ? super Boolean> bool = (c, v) -> {
			final Flag flag = c.getArgument().getAnnotation(Flag.class);
			if (flag != null) {
				if (v) c.getCommand().argument(flag.value());
				return;
			}
		};
		builder.add(ArgumentContext.class, Boolean.class, bool);
		builder.add(ArgumentContext.class, Boolean.TYPE, bool);
	}).build();

	protected static IFunction1<? super IProcess, ? extends Object> getResultExtractor(Method method) {
		if (method.getReturnType().isAssignableFrom(String.class)) return process -> {
			try {
				final StringBuilder retVal = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOut()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						retVal.append(line).append("\n");
					}
				} catch (IOException exception) {
					throw new RuntimeIOException(exception);
				}

				return retVal.toString();
			} finally {
				process.close();
			}
		};
		if (method.getReturnType().isAssignableFrom(IProcess.class)) return IFunction1.identity();
		throw new IllegalArgumentException();
	}

	@Getter(AccessLevel.PROTECTED)
	protected final IRunner runner;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final IFunction1<? super IProcess, ? extends Object> result = getResultExtractor(method);
		final Command.CommandBuilder commandBuilder = Command.builder().argument(method.getName());
		final Parameter[] parameters = method.getParameters();
		for (int i = 0; i < method.getParameterCount(); i++) {
			final Argument argument = new Argument(args[i], parameters[i]);
			final ArgumentContext context = new ArgumentContext(commandBuilder, argument);

			final Explicit explicit = argument.getAnnotation(Explicit.class);
			if (explicit != null) {
				final IExplicitArgumentHandler handler = explicit.handler().newInstance();
				handler.accept(context, argument.get());
			} else argumentBuilder.accept(context, argument.get());
		}
		return result.apply(getRunner().run(commandBuilder.build()));
	}
}