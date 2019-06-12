package com.g2forge.gearbox.command.proxy.transformers;

import java.lang.reflect.Method;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.method.OverrideInvocationTransformer;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class MetadataDispatchInvocationTransformer implements IDelegatingInvocationTransformer {
	protected final IFunction1<MethodInvocation, ProcessInvocation<?>> delegate;

	@Override
	public ProcessInvocation<?> apply(MethodInvocation methodInvocation) {
		final Method method = methodInvocation.getMethod();
		final OverrideInvocationTransformer specify = IMetadata.of(method).getMetadata(OverrideInvocationTransformer.class);
		if (specify == null) return getDelegate().apply(methodInvocation);

		final Class<? extends IFunction1<MethodInvocation, ProcessInvocation<?>>> klass = specify.value();
		final IFunction1<MethodInvocation, ProcessInvocation<?>> transformer;
		try {
			transformer = klass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeReflectionException(e);
		}
		return transformer.apply(methodInvocation);
	}
}
