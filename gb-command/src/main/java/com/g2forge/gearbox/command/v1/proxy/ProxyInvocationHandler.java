package com.g2forge.gearbox.command.v1.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.g2forge.alexandria.command.CommandInvocation;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.gearbox.command.process.IProcess;
import com.g2forge.gearbox.command.process.redirect.IRedirect;
import com.g2forge.gearbox.command.v1.control.Constant;
import com.g2forge.gearbox.command.v1.control.Explicit;
import com.g2forge.gearbox.command.v1.control.Flag;
import com.g2forge.gearbox.command.v1.control.IExplicitArgumentHandler;
import com.g2forge.gearbox.command.v1.control.IExplicitResultHandler;
import com.g2forge.gearbox.command.v1.control.IResultContext;
import com.g2forge.gearbox.command.v1.control.ModifyCommandException;
import com.g2forge.gearbox.command.v1.control.Named;
import com.g2forge.gearbox.command.v1.control.TypedInvocation;
import com.g2forge.gearbox.command.v1.control.Working;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
class ProxyInvocationHandler implements InvocationHandler {
	protected static final IConsumer2<ArgumentContext, Object> argumentBuilder = new TypeSwitch2.ConsumerBuilder<ArgumentContext, Object>().with(builder -> {
		builder.add(ArgumentContext.class, String[].class, (c, v) -> {
			for (String value : v) {
				c.getCommandInvocation().argument(value);
			}
		});
		builder.add(ArgumentContext.class, String.class, (c, v) -> {
			final Named named = c.getArgument().getAnnotation(Named.class);
			c.getCommandInvocation().argument(named != null ? named.value() + v : v);
		});
		builder.add(ArgumentContext.class, Path.class, (c, v) -> {
			final Working working = c.getArgument().getAnnotation(Working.class);
			if (working != null) c.getCommandInvocation().working(v);
			else {
				final Named named = c.getArgument().getAnnotation(Named.class);
				final String string = v.toString();
				c.getCommandInvocation().argument(named != null ? named.value() + string : string);
			}
		});

		final IConsumer2<? super ArgumentContext, ? super Boolean> bool = (c, v) -> {
			final Flag flag = c.getArgument().getAnnotation(Flag.class);
			if (flag != null) {
				if (v) c.getCommandInvocation().argument(flag.value());
				return;
			} else c.getCommandInvocation().argument(Boolean.toString(v));
		};
		builder.add(ArgumentContext.class, Boolean.class, bool);
		builder.add(ArgumentContext.class, Boolean.TYPE, bool);
	}).build();

	@Getter(AccessLevel.PROTECTED)
	protected final IFunction1<CommandInvocation<IRedirect, IRedirect>, IProcess> runner;

	protected void constant(final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> commandBuilder, final Constant constant) {
		if (constant != null) commandBuilder.arguments(HCollection.asList(constant.value()));
	}

	protected TypedInvocation createInvocation(Method method, Object[] args, final IResultContext resultContext) throws InstantiationException, IllegalAccessException {
		final TypedInvocation.TypedInvocationBuilder typedInvocationBuilder = TypedInvocation.builder();
		final CommandInvocation.CommandInvocationBuilder<IRedirect, IRedirect> commandInvocationBuilder = CommandInvocation.builder();

		// Compute the command name
		final com.g2forge.gearbox.command.v1.control.Command command = method.getAnnotation(com.g2forge.gearbox.command.v1.control.Command.class);
		if (command != null) Stream.of(command.value()).forEach(commandInvocationBuilder::argument);
		else commandInvocationBuilder.argument(method.getName());

		// Compute the result handler
		final IExplicitResultHandler resultHandler;
		if ((command != null) && (command.handler() != IExplicitResultHandler.class)) resultHandler = command.handler().newInstance();
		else resultHandler = resultContext.getStandard(resultContext.getType());
		commandInvocationBuilder.io(resultHandler.getRedirects());
		typedInvocationBuilder.resultHandler(resultHandler);

		// Sort out all the arguments
		final Parameter[] parameters = method.getParameters();
		for (int i = 0; i < method.getParameterCount(); i++) {
			final Argument argument = new Argument(args[i], parameters[i]);
			final ArgumentContext argumentContext = new ArgumentContext(commandInvocationBuilder, argument);

			final Explicit explicit = argument.getAnnotation(Explicit.class);
			if (explicit != null) {
				final IExplicitArgumentHandler handler = explicit.value().newInstance();
				handler.accept(argumentContext, argument.get());
			} else argumentBuilder.accept(argumentContext, argument.get());

			constant(commandInvocationBuilder, argument.getAnnotation(Constant.class));
		}

		typedInvocationBuilder.invocation(commandInvocationBuilder.build());
		return typedInvocationBuilder.build();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final IFunction1<? super TypedInvocation, ? extends TypedInvocation> modifier;
		if (method.isDefault()) {
			final Class<?> declaringClass = method.getDeclaringClass();
			final Constructor<Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
			constructor.setAccessible(true);
			final MethodHandle bound = constructor.newInstance(declaringClass).in(declaringClass).unreflectSpecial(method, declaringClass).bindTo(proxy);
			try {
				return bound.invokeWithArguments(args);
				// return MethodHandles.lookup().in(declaringClass).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
			} catch (ModifyCommandException exception) {
				modifier = exception.getModifier();
			}
		} else modifier = IFunction1.identity();

		final ResultContext resultContext = new ResultContext(method);
		final TypedInvocation original = createInvocation(method, args, resultContext);
		final TypedInvocation modified = modifier.apply(original);
		final IProcess process = getRunner().apply(modified.getInvocation());
		return modified.getResultHandler().apply(process, resultContext);
	}
}