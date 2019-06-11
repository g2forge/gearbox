package com.g2forge.gearbox.functional.v2.proxy.transformers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.functional.v2.proxy.method.IArgumentConsumer;
import com.g2forge.gearbox.functional.v2.proxy.method.IMethodConsumer;
import com.g2forge.gearbox.functional.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.functional.v2.proxy.process.ProcessInvocation;

import lombok.AllArgsConstructor;

public class MethodToCommandInvocationTransformer implements IInvocationTransformer {
	@AllArgsConstructor
	class Argument implements IArgumentConsumer.IArgument<Object> {
		protected final Object value;

		protected final Parameter parameter;

		@Override
		public Object get() {
			return value;
		}

		@Override
		public Type getGenericType() {
			return parameter.getParameterizedType();
		}

		@Override
		public IMetadata getMetadata() {
			return IMetadata.of(parameter, value);
		}

		@Override
		public String getName() {
			return parameter.getName();
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class<Object> getType() {
			return (Class<Object>) parameter.getType();
		}
	}

	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		final Method method = methodInvocation.getMethod();
		final ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder = ProcessInvocation.builder();

		final IMethodConsumer methodConsumer = IMetadata.of(method).getMetadata(IMethodConsumer.class);
		if (methodConsumer != null) methodConsumer.accept(processInvocationBuilder, methodInvocation);

		for (int i = 0; i < method.getParameterCount(); i++) {
			final Argument argument = new Argument(methodInvocation.getArguments().get(i), method.getParameters()[i]);
			final IArgumentConsumer argumentConsumer = argument.getMetadata().getMetadata(IArgumentConsumer.class);
			if (argumentConsumer != null) argumentConsumer.accept(processInvocationBuilder, methodInvocation, argument);
		}

		return processInvocationBuilder.build();
	}
}
