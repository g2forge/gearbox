package com.g2forge.gearbox.command.converter.dumb;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.g2forge.alexandria.annotations.note.Note;
import com.g2forge.alexandria.annotations.note.NoteType;
import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.format.ICommandFormat;
import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.core.enums.EnumException;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.platform.HPlatform;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.alexandria.java.type.ref.ATypeRef;
import com.g2forge.alexandria.java.type.ref.ATypeRefIdentity;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.command.converter.ICommandConverterR_;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.converter.MethodArgument;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.process.redirect.InheritRedirect;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation.ProcessInvocationBuilder;
import com.g2forge.gearbox.command.proxy.result.BooleanResultSupplier;
import com.g2forge.gearbox.command.proxy.result.IResultSupplier;
import com.g2forge.gearbox.command.proxy.result.IntegerResultSupplier;
import com.g2forge.gearbox.command.proxy.result.ProcessResultSupplier;
import com.g2forge.gearbox.command.proxy.result.StreamResultSupplier;
import com.g2forge.gearbox.command.proxy.result.StringResultSupplier;
import com.g2forge.gearbox.command.proxy.result.VoidResultSupplier;
import com.g2forge.habitat.metadata.Metadata;

import lombok.Builder;
import lombok.Data;

public class DumbCommandConverter implements ICommandConverterR_, ISingleton {
	@Data
	@Builder(toBuilder = true)
	protected static class ArgumentContext {
		protected final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> command;

		protected final ProcessInvocation.ProcessInvocationBuilder<?> process;

		protected final IMethodArgument<Object> argument;
	}

	protected static final DumbCommandConverter instance = new DumbCommandConverter();

	protected static final IConsumer2<ArgumentContext, Object> ARGUMENT_BUILDER = new TypeSwitch2.ConsumerBuilder<ArgumentContext, Object>().with(builder -> {
		builder.add(ArgumentContext.class, String[].class, (c, v) -> {
			if (c.getArgument().getMetadata().isPresent(Named.class)) throw new IllegalArgumentException("Named string arrays are not supported!");
			for (String value : v) {
				c.getCommand().argument(value);
			}
		});
		builder.add(ArgumentContext.class, String.class, (c, v) -> {
			c.getCommand().arguments(HDumbCommandConverter.computeString(c.getArgument(), v));
		});
		builder.add(ArgumentContext.class, Integer.class, (c, v) -> {
			c.getCommand().arguments(HDumbCommandConverter.computeString(c.getArgument(), Integer.toString(v)));
		});
		builder.add(ArgumentContext.class, Path.class, (c, v) -> {
			boolean isArgument = true;

			final Working working = c.getArgument().getMetadata().get(Working.class);
			if (working != null) {
				c.getCommand().working(v);
				isArgument = false;
			}

			final EnvPath envPath = c.getArgument().getMetadata().get(EnvPath.class);
			if (envPath != null) {
				c.getProcess().environmentVariable("PATH", parent -> {
					final String pathSeparator = HPlatform.getPlatform().getPathSpec().getPathSeparator();
					switch (envPath.usage()) {
						case AddFirst:
							return v.toString() + pathSeparator + parent;
						case Replace:
							return v.toString();
						case AddLast:
							return parent + pathSeparator + v.toString();
						default:
							throw new EnumException(EnvPath.Usage.class, envPath.usage());
					}
				});
				isArgument = false;
			}

			if (isArgument) c.getCommand().arguments(HDumbCommandConverter.computeString(c.getArgument(), v.toString()));
			else if (c.getArgument().getMetadata().isPresent(Named.class)) throw new IllegalArgumentException("Paths used as non-arguments cannot also be named!");
		});

		final IConsumer2<? super ArgumentContext, ? super Boolean> bool = (c, v) -> {
			final Flag flag = c.getArgument().getMetadata().get(Flag.class);
			if (flag != null) {
				if (c.getArgument().getMetadata().isPresent(Named.class)) throw new IllegalArgumentException("Flags cannot also be named!");
				if (v) c.getCommand().argument(flag.value());
				return;
			} else c.getCommand().arguments(HDumbCommandConverter.computeString(c.getArgument(), Boolean.toString(v)));
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

	public static DumbCommandConverter create() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Note(type = NoteType.TODO, value = "Use static type switch", issue = "G2-432")
	protected static <T> IResultSupplier<T> getStandard(ITypeRef<T> type) {
		if (type.getErasedType().isAssignableFrom(Boolean.class) || type.getErasedType().isAssignableFrom(Boolean.TYPE)) return (IResultSupplier<T>) BooleanResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(Integer.class) || type.getErasedType().isAssignableFrom(Integer.TYPE)) return (IResultSupplier<T>) IntegerResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(Void.class) || type.getErasedType().isAssignableFrom(Void.TYPE)) return (IResultSupplier<T>) VoidResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(String.class)) return (IResultSupplier<T>) StringResultSupplier.create();
		if (type.getErasedType().isAssignableFrom(IProcess.class)) return (IResultSupplier<T>) ProcessResultSupplier.create();
		if (new ATypeRef<Stream<String>>() {}.getType().equals(type.getType())) return (IResultSupplier<T>) StreamResultSupplier.STANDARD;
		throw new IllegalArgumentException(String.format("Return type \"%1$s\" is not supported!", type.getType()));
	}

	@Note(type = NoteType.TODO, value = "Add an annotation to control format")
	@Override
	public <T> ProcessInvocation<T> apply(ProcessInvocation<T> processInvocation, MethodInvocation methodInvocation) {
		final ProcessInvocationBuilder<T> processInvocationBuilder = processInvocation.toBuilder();
		final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> commandInvocationBuilder = processInvocation.getCommandInvocation() != null ? processInvocation.getCommandInvocation().toBuilder() : CommandInvocation.<IRedirect, IRedirect>builder().format(ICommandFormat.getDefault());
		final ITypeRef<T> returnTypeRef = computeReturnTypeRef(methodInvocation.getMethod());

		// Compute the IO redirection
		if ((processInvocation.getCommandInvocation() == null) || (processInvocation.getCommandInvocation().getIo() == null)) {
			if (returnTypeRef.getErasedType().isAssignableFrom(Void.class) || returnTypeRef.getErasedType().isAssignableFrom(Void.TYPE)) commandInvocationBuilder.io(StandardIO.<IRedirect, IRedirect>builder().standardInput(InheritRedirect.create()).standardOutput(InheritRedirect.create()).standardError(InheritRedirect.create()).build());
		}

		// Compute the command name
		commandInvocationBuilder.clearArguments();
		final Command command = Metadata.getStandard().of(methodInvocation.getMethod()).get(Command.class);
		if (command != null) Stream.of(command.value()).forEach(commandInvocationBuilder::argument);
		else commandInvocationBuilder.argument(methodInvocation.getMethod().getName());

		// Compute the result generator
		if (processInvocation.getResultSupplier() == null) {
			final IResultSupplier<T> standard = getStandard(returnTypeRef);
			processInvocationBuilder.resultSupplier(standard);
		}

		// Sort out all the arguments
		final Parameter[] parameters = methodInvocation.getMethod().getParameters();
		for (int i = 0; i < parameters.length; i++) {
			final Object value = methodInvocation.getArguments().get(i);
			final IMethodArgument<Object> methodArgument = new MethodArgument(value, parameters[i]);

			final IArgumentRenderer<?> argumentRenderer = methodArgument.getMetadata().get(IArgumentRenderer.class);
			if (argumentRenderer != null) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				final List<String> arguments = argumentRenderer.render((IMethodArgument) methodArgument);
				commandInvocationBuilder.arguments(arguments);
			} else {
				final ArgumentContext argumentContext = new ArgumentContext(commandInvocationBuilder, processInvocationBuilder, methodArgument);
				ARGUMENT_BUILDER.accept(argumentContext, value);
			}

			final Constant constant = methodArgument.getMetadata().get(Constant.class);
			if ((constant != null) && (constant.value() != null)) commandInvocationBuilder.arguments(HCollection.asList(constant.value()));
		}

		processInvocationBuilder.commandInvocation(commandInvocationBuilder.build());
		return processInvocationBuilder.build();
	}
}
