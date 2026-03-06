package com.g2forge.gearbox.command.proxy.transformers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import com.g2forge.alexandria.java.core.error.HError;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ModifyProcessInvocationException;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class DefaultMethodInvocationTransformer implements IDelegatingInvocationTransformer {
	protected final IFunction1<MethodInvocation, ProcessInvocation<?>> delegate;

	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		final Method method = methodInvocation.getMethod();
		if (method.isDefault()) {
			final Class<?> declaringClass = method.getDeclaringClass();
			try {
				final Constructor<Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
				constructor.setAccessible(true);
				// return MethodHandles.lookup().in(declaringClass).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
				final MethodHandle bound = constructor.newInstance(declaringClass).in(declaringClass).unreflectSpecial(method, declaringClass).bindTo(methodInvocation.getObject());
				try {
					final List<Object> arguments = methodInvocation.getArguments();
					final Object retVal = bound.invokeWithArguments(arguments == null ? new Object[0] : arguments.toArray());
					return new ProcessInvocation<>(null, process -> retVal);
				} catch (ModifyProcessInvocationException exception) {
					final ProcessInvocation<?> processInvocation = getDelegate().apply(methodInvocation);
					return exception.getFunction().apply(processInvocation);
				}
			} catch (Throwable throwable) {
				HError.throwQuietly(throwable);
			}
		}

		return getDelegate().apply(methodInvocation);
	}
}
