package com.g2forge.gearbox.functional.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.g2forge.alexandria.command.Invocation;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.gearbox.functional.control.Constant;
import com.g2forge.gearbox.functional.control.Explicit;
import com.g2forge.gearbox.functional.control.Flag;
import com.g2forge.gearbox.functional.control.IExplicitArgumentHandler;
import com.g2forge.gearbox.functional.control.IExplicitResultHandler;
import com.g2forge.gearbox.functional.control.IResultContext;
import com.g2forge.gearbox.functional.control.ModifyCommandException;
import com.g2forge.gearbox.functional.control.Named;
import com.g2forge.gearbox.functional.control.TypedInvocation;
import com.g2forge.gearbox.functional.control.Working;
import com.g2forge.gearbox.functional.runner.IProcess;
import com.g2forge.gearbox.functional.runner.IRunner;
import com.g2forge.gearbox.functional.runner.redirect.IRedirect;

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

	protected void constant(final Invocation.InvocationBuilder<IRedirect, IRedirect> commandBuilder, final Constant constant) {
		if (constant != null) commandBuilder.arguments(HCollection.asList(constant.value()));
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
		final IProcess process = getRunner().run(modified.getInvocation());
		return modified.getResultHandler().apply(process, resultContext);
	}

	protected TypedInvocation createInvocation(Method method, Object[] args, final IResultContext resultContext) throws InstantiationException, IllegalAccessException {
		final TypedInvocation.TypedInvocationBuilder typedInvocationBuilder = TypedInvocation.builder();
		final Invocation.InvocationBuilder<IRedirect, IRedirect> invocationBuilder = Invocation.builder();

		// Compute the command name
		final com.g2forge.gearbox.functional.control.Command command = method.getAnnotation(com.g2forge.gearbox.functional.control.Command.class);
		if (command != null) Stream.of(command.value()).forEach(invocationBuilder::argument);
		else invocationBuilder.argument(method.getName());

		// Compute the result handler
		final IExplicitResultHandler resultHandler;
		if ((command != null) && (command.handler() != IExplicitResultHandler.class)) resultHandler = command.handler().newInstance();
		else resultHandler = resultContext.getStandard(resultContext.getType());
		invocationBuilder.io(resultHandler.getRedirects());
		typedInvocationBuilder.resultHandler(resultHandler);

		// Sort out all the arguments
		final Parameter[] parameters = method.getParameters();
		for (int i = 0; i < method.getParameterCount(); i++) {
			final Argument argument = new Argument(args[i], parameters[i]);
			final ArgumentContext argumentContext = new ArgumentContext(invocationBuilder, argument);

			final Explicit explicit = argument.getAnnotation(Explicit.class);
			if (explicit != null) {
				final IExplicitArgumentHandler handler = explicit.value().newInstance();
				handler.accept(argumentContext, argument.get());
			} else argumentBuilder.accept(argumentContext, argument.get());

			constant(invocationBuilder, argument.getAnnotation(Constant.class));
		}

		typedInvocationBuilder.invocation(invocationBuilder.build());
		return typedInvocationBuilder.build();
	}
}