package com.g2forge.gearbox.command.converter.dumb;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.g2forge.alexandria.annotations.note.Note;
import com.g2forge.alexandria.annotations.note.NoteType;
import com.g2forge.alexandria.command.invocation.CommandInvocation;
import com.g2forge.alexandria.command.invocation.environment.SystemEnvironment;
import com.g2forge.alexandria.command.invocation.environment.modified.EnvironmentValue;
import com.g2forge.alexandria.command.invocation.environment.modified.IEnvironmentModifier;
import com.g2forge.alexandria.command.invocation.environment.modified.ModifiedEnvironment;
import com.g2forge.alexandria.command.invocation.format.ICommandFormat;
import com.g2forge.alexandria.command.stdio.StandardIO;
import com.g2forge.alexandria.java.core.enums.EnumException;
import com.g2forge.alexandria.java.core.error.HError;
import com.g2forge.alexandria.java.core.error.NotYetImplementedError;
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
import com.g2forge.habitat.metadata.IMetadata;
import com.g2forge.habitat.metadata.Metadata;
import com.g2forge.habitat.metadata.value.subject.ISubject;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class DumbCommandConverter implements ICommandConverterR_, ISingleton {
	@Data
	@Builder(toBuilder = true)
	protected static class ArgumentContext {
		protected final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> command;

		protected final ModifiedEnvironment.ModifiedEnvironmentBuilder environment;

		protected final IMethodArgument<Object> argument;
	}

	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	protected static class EnvPathModifier implements IEnvironmentModifier {
		protected final EnvPath.Usage usage;

		protected final Path value;

		@Override
		public String modify(String parent) {
			final String pathSeparator = HPlatform.getPlatform().getPathSpec().getPathSeparator();
			switch (getUsage()) {
				case AddFirst:
					return getValue().toString() + pathSeparator + parent;
				case Replace:
					return getValue().toString();
				case AddLast:
					return parent + pathSeparator + getValue().toString();
				default:
					throw new EnumException(EnvPath.Usage.class, getUsage());
			}
		}

	}

	protected static final DumbCommandConverter instance = new DumbCommandConverter();

	protected static final IConsumer2<ArgumentContext, Object> ARGUMENT_BUILDER = new TypeSwitch2.ConsumerBuilder<ArgumentContext, Object>().with(builder -> {
		builder.add(ArgumentContext.class, String[].class, (c, v) -> {
			final ISubject metadata = c.getArgument().getMetadata();
			if (metadata.isPresent(Named.class)) throw new IllegalArgumentException("Named string arrays are not supported!");
			if (metadata.isPresent(Environment.class)) throw new IllegalArgumentException("We do not support setting an environment variable to a string array!");
			if (metadata.isPresent(EnvPath.class)) throw new IllegalArgumentException("We do not support setting the PATH environment variable to a string array!");

			for (String value : v) {
				c.getCommand().argument(value);
			}
		});
		builder.add(ArgumentContext.class, String.class, (c, v) -> {
			HDumbCommandConverter.set(c, c.getArgument(), v);
		});
		builder.add(ArgumentContext.class, Integer.class, (c, v) -> {
			HDumbCommandConverter.set(c, c.getArgument(), Integer.toString(v));
		});
		builder.add(ArgumentContext.class, Path.class, (c, v) -> {
			boolean isNormal = true;

			final Working working = c.getArgument().getMetadata().get(Working.class);
			if (working != null) {
				if (v != null) c.getCommand().working(v);
				isNormal = false;
			}

			final EnvPath envPath = c.getArgument().getMetadata().get(EnvPath.class);
			if (envPath != null) {
				c.getEnvironment().modifier(HPlatform.PATH, new EnvPathModifier(envPath.usage(), v));
				isNormal = false;
			}

			if (isNormal) HDumbCommandConverter.set(c, c.getArgument(), v.toString());
			else if (c.getArgument().getMetadata().isPresent(Named.class)) throw new IllegalArgumentException("Paths used as environment paths or working directories cannot also be used in normal arguments & environment variables!");
		});

		final IConsumer2<? super ArgumentContext, ? super Boolean> bool = (c, v) -> {
			final Flag flag = c.getArgument().getMetadata().get(Flag.class);
			if (flag != null) {
				if (c.getArgument().getMetadata().isPresent(Named.class)) throw new IllegalArgumentException("Flags cannot also be named!");
				if (v) c.getCommand().argument(flag.value());
				return;
			} else HDumbCommandConverter.set(c, c.getArgument(), Boolean.toString(v));
		};
		builder.add(ArgumentContext.class, Boolean.class, bool);
		builder.add(ArgumentContext.class, Boolean.TYPE, bool);
		builder.add(ArgumentContext.class, Map.class, (c, v) -> {
			final Environment environment = c.getArgument().getMetadata().get(Environment.class);
			if ((environment == null) || (environment.value() != null)) throw new IllegalArgumentException("Map arguments must be environemt with \"null\" name!");
			final ModifiedEnvironment.ModifiedEnvironmentBuilder e = c.getEnvironment();
			@SuppressWarnings("unchecked")
			final Map<String, ?> map = (Map<String, ?>) v;
			for (Map.Entry<String, ?> entry : map.entrySet()) {
				final String key = entry.getKey();
				final Object value = entry.getValue();
				final IEnvironmentModifier modifier;
				if (value instanceof IEnvironmentModifier) modifier = (IEnvironmentModifier) value;
				else if (value instanceof String) modifier = new EnvironmentValue((String) value);
				else throw new IllegalArgumentException("Arguments of type \"" + value.getClass() + "\" are not supported!");
				e.modifier(key, modifier);
			}
		});
		builder.fallback((c, v) -> {
			if (v == null) {
				final ISubject subject = c.getArgument().getMetadata();
				if (subject.isPresent(Working.class)) return;
				if (subject.isPresent(Environment.class)) return;
				if (subject.isPresent(EnvPath.class)) return;
				HDumbCommandConverter.set(c, c.getArgument(), null);
			} else throw new IllegalArgumentException(String.format("Parameter %1$s cannot be converted to a command line argument because the type of \"%2$s\" (%3$s) is unknown.  Please consider implementing %4$s.", c.getArgument().getName(), v, v.getClass(), IArgumentRenderer.class.getSimpleName()));
		});
	}).build();

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private static final IMetadata metadata = Metadata.getStandard();

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
		final ITypeRef<T> returnTypeRef = computeReturnTypeRef(methodInvocation.getMethod());

		final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> commandInvocationBuilder;
		final ModifiedEnvironment.ModifiedEnvironmentBuilder environmentBuilder = ModifiedEnvironment.builder();
		if (processInvocation.getCommandInvocation() != null) {
			commandInvocationBuilder = processInvocation.getCommandInvocation().toBuilder();
			environmentBuilder.base(processInvocation.getCommandInvocation().getEnvironment());
		} else {
			commandInvocationBuilder = CommandInvocation.<IRedirect, IRedirect>builder().format(ICommandFormat.getDefault());
			environmentBuilder.base(SystemEnvironment.create());
		}

		// Compute the IO redirection
		if ((processInvocation.getCommandInvocation() == null) || (processInvocation.getCommandInvocation().getIo() == null)) {
			if (returnTypeRef.getErasedType().isAssignableFrom(Void.class) || returnTypeRef.getErasedType().isAssignableFrom(Void.TYPE)) commandInvocationBuilder.io(StandardIO.<IRedirect, IRedirect>builder().standardInput(InheritRedirect.create()).standardOutput(InheritRedirect.create()).standardError(InheritRedirect.create()).build());
		}

		final ISubject methodSubject = getMetadata().of(methodInvocation.getMethod());

		// Compute the command name & initial arguments
		commandInvocationBuilder.clearArguments();
		final Command command = methodSubject.get(Command.class);
		final List<String> commandArguments;
		if (command != null) commandArguments = HCollection.asList(command.value());
		else commandArguments = HCollection.asList(methodInvocation.getMethod().getName());
		commandArguments.forEach(commandInvocationBuilder::argument);

		{
			final ConstantEnvironment constantEnvironment = methodSubject.get(ConstantEnvironment.class);
			if (constantEnvironment != null) environmentBuilder.modifier(constantEnvironment.variable(), new EnvironmentValue(constantEnvironment.value()));
		}
		if (command != null) {
			final ConstantEnvironment[] env = command.env();
			if ((env != null) && (env.length > 0)) {
				for (ConstantEnvironment constantEnvironment : env) {
					environmentBuilder.modifier(constantEnvironment.variable(), new EnvironmentValue(constantEnvironment.value()));
				}
			}
		}

		// Compute the result generator
		if (processInvocation.getResultSupplier() == null) {
			final IResultSupplier<T> standard = getStandard(returnTypeRef);
			processInvocationBuilder.resultSupplier(standard);
		}

		// Generate the command & environment from the method arguments
		final Parameter[] parameters = methodInvocation.getMethod().getParameters();
		final List<Throwable> throwables = new ArrayList<>();
		for (int i = 0; i < parameters.length; i++) {
			// Convert all the parameters and collect any exceptions, so that the final exception report is comprehensive
			try {
				final Object value = methodInvocation.getArguments().get(i);
				final IMethodArgument<Object> methodArgument = new MethodArgument(value, parameters[i]);

				final IArgumentRenderer<?> argumentRenderer = methodArgument.getMetadata().get(IArgumentRenderer.class);
				if (argumentRenderer != null) {
					if (methodArgument.getMetadata().isPresent(Environment.class)) throw new NotYetImplementedError("Parameters with custom argument renderers cannot be used as environment variables (yet)!");
					@SuppressWarnings({ "unchecked", "rawtypes" })
					final List<String> arguments = argumentRenderer.render((IMethodArgument) methodArgument);
					commandInvocationBuilder.arguments(arguments);
				} else {
					final ArgumentContext argumentContext = new ArgumentContext(commandInvocationBuilder, environmentBuilder, methodArgument);
					ARGUMENT_BUILDER.accept(argumentContext, value);
				}

				final Constant constant = methodArgument.getMetadata().get(Constant.class);
				if ((constant != null) && (constant.value() != null)) commandInvocationBuilder.arguments(HCollection.asList(constant.value()));
			} catch (Throwable throwable) {
				throwables.add(throwable);
			}
		}
		if (!throwables.isEmpty()) throw HError.withSuppressed(new RuntimeException(String.format("Failed to convert parameters to arguments for %1$s", commandArguments.stream().collect(Collectors.joining(" ")))), throwables);

		processInvocationBuilder.commandInvocation(commandInvocationBuilder.environment(environmentBuilder.build().simplify()).build());
		return processInvocationBuilder.build();
	}
}
