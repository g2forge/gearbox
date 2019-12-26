package com.g2forge.gearbox.command.converter.manual.v1;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.habitat.metadata.access.ITypedMetadataAccessor;
import com.g2forge.habitat.metadata.access.indirect.IndirectMetadata;
import com.g2forge.habitat.metadata.type.predicate.IPredicateType;
import com.g2forge.habitat.metadata.value.predicate.ConstantPredicate;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;
import com.g2forge.habitat.metadata.value.subject.ISubject;

@IndirectMetadata(IMethodConsumer.MetadataAccessor.class)
@FunctionalInterface
public interface IMethodConsumer extends IConsumer2<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation> {
	public static class MetadataAccessor implements ITypedMetadataAccessor<IMethodConsumer, ISubject, IPredicateType<IMethodConsumer>> {
		@Override
		public IPredicate<IMethodConsumer> bindTyped(ISubject subject, IPredicateType<IMethodConsumer> predicateType) {
			final MethodConsumers methodConsumers = subject.get(MethodConsumers.class);
			if (methodConsumers == null) return ConstantPredicate.absent(subject, predicateType);
			return ConstantPredicate.present(subject, predicateType, new IMethodConsumer() {
				@Override
				public void accept(ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation) {
					for (MethodConsumer methodConsumerMetadata : methodConsumers.value()) {
						final IConsumer2<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation> methodConsumer;
						try {
							methodConsumer = methodConsumerMetadata.value().newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							throw new RuntimeReflectionException(e);
						}
						methodConsumer.accept(processInvocationBuilder, methodInvocation);
					}
				}
			});
		}
	}

	public void accept(ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation);
}
