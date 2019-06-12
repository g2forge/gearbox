package com.g2forge.gearbox.command.v2.converter.manual.v1;

import java.lang.reflect.Method;

import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.command.v2.converter.MethodArgument;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;
import com.g2forge.gearbox.command.v2.proxy.transformers.IInvocationTransformer;

public class MethodToCommandInvocationTransformer implements IInvocationTransformer {
	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		final Method method = methodInvocation.getMethod();
		final ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder = ProcessInvocation.builder();

		final IMethodConsumer methodConsumer = IMetadata.of(method).getMetadata(IMethodConsumer.class);
		if (methodConsumer != null) methodConsumer.accept(processInvocationBuilder, methodInvocation);

		for (int i = 0; i < method.getParameterCount(); i++) {
			final MethodArgument argument = new MethodArgument(methodInvocation.getArguments().get(i), method.getParameters()[i]);
			final IArgumentConsumer argumentConsumer = argument.getMetadata().getMetadata(IArgumentConsumer.class);
			if (argumentConsumer != null) argumentConsumer.accept(processInvocationBuilder, methodInvocation, argument);
		}

		return processInvocationBuilder.build();
	}
}
