package com.g2forge.gearbox.functional.v2.proxy.transformers;

import java.lang.reflect.Method;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.gearbox.functional.v2.process.ProcessInvocation;
import com.g2forge.gearbox.functional.v2.proxy.MethodInvocation;
import com.g2forge.gearbox.functional.v2.proxy.OverrideInvocationTransformer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
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
