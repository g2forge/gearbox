package com.g2forge.gearbox.functional.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.typed.ATypeRef;
import com.g2forge.alexandria.java.typeswitch.TypeSwitch2;
import com.g2forge.gearbox.functional.control.Explicit;
import com.g2forge.gearbox.functional.control.Flag;
import com.g2forge.gearbox.functional.control.IArgument;
import com.g2forge.gearbox.functional.control.IArgumentContext;
import com.g2forge.gearbox.functional.control.ICommandBuilder;
import com.g2forge.gearbox.functional.control.IExplicitArgumentHandler;
import com.g2forge.gearbox.functional.control.Working;
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
		protected final ICommandBuilder command;

		protected final IArgument<Object> argument;
	}

	@AllArgsConstructor
	@Getter
	protected static class CommandBuilder implements ICommandBuilder {
		protected final Command.CommandBuilder commandBuilder;

		@Override
		public ICommandBuilder argument(String argument) {
			getCommandBuilder().argument(argument);
			return this;
		}

		@Override
		public ICommandBuilder working(Path working) {
			getCommandBuilder().working(working);
			return this;
		}
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
			} else c.getCommand().argument(Boolean.toString(v));
		};
		builder.add(ArgumentContext.class, Boolean.class, bool);
		builder.add(ArgumentContext.class, Boolean.TYPE, bool);
	}).build();

	protected static IFunction1<? super IProcess, ? extends Object> getResultExtractor(Method method) {
		if (method.getReturnType().isAssignableFrom(Boolean.class) || method.getReturnType().isAssignableFrom(Boolean.TYPE)) return process -> {
			try {
				return process.getExitCode() == 0;
			} finally {
				process.close();
			}
		};
		if (method.getReturnType().isAssignableFrom(Integer.class) || method.getReturnType().isAssignableFrom(Integer.TYPE)) return process -> {
			try {
				return process.getExitCode();
			} finally {
				process.close();
			}
		};
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

				process.assertSuccess();
				return retVal.toString();
			} finally {
				process.close();
			}
		};
		if (new ATypeRef<Stream<String>>() {}.getType().equals(method.getGenericReturnType())) return process -> {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getStandardOut()));
			final Iterator<String> iterator = new Iterator<String>() {
				protected boolean done = false;

				protected String line = null;

				protected void close() {
					done = true;
					process.assertSuccess();
					try {
						reader.close();
					} catch (IOException e) {
						throw new RuntimeIOException(e);
					} finally {
						process.close();
					}
				}

				@Override
				public boolean hasNext() {
					if (done) return false;
					if (line == null) {
						try {
							line = reader.readLine();
							if (line == null) close();
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					}
					return (line != null);
				}

				@Override
				public String next() {
					if (!hasNext()) throw new NoSuchElementException();
					final String retVal = line;
					line = null;
					return retVal;
				}
			};
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL), false);
		};
		if (method.getReturnType().isAssignableFrom(IProcess.class)) return IFunction1.identity();
		throw new IllegalArgumentException(String.format("Return type \"%1$s\" is not supported!", method.getGenericReturnType()));
	}

	@Getter(AccessLevel.PROTECTED)
	protected final IRunner runner;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final IFunction1<? super IProcess, ? extends Object> result = getResultExtractor(method);
		final Command.CommandBuilder commandBuilder = Command.builder();

		// Compute the command name
		final com.g2forge.gearbox.functional.control.Command command = method.getAnnotation(com.g2forge.gearbox.functional.control.Command.class);
		if (command != null) commandBuilder.argument(command.value());
		else commandBuilder.argument(method.getName());

		// Sort out all the arguments
		final Parameter[] parameters = method.getParameters();
		for (int i = 0; i < method.getParameterCount(); i++) {
			final Argument argument = new Argument(args[i], parameters[i]);
			final ArgumentContext context = new ArgumentContext(new CommandBuilder(commandBuilder), argument);

			final Explicit explicit = argument.getAnnotation(Explicit.class);
			if (explicit != null) {
				final IExplicitArgumentHandler handler = explicit.handler().newInstance();
				handler.accept(context, argument.get());
			} else argumentBuilder.accept(context, argument.get());
		}
		return result.apply(getRunner().run(commandBuilder.build()));
	}
}