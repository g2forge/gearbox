package com.g2forge.gearbox.command.converter.manual.v1;

import java.lang.reflect.InvocationTargetException;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.function.IConsumer3;
import com.g2forge.gearbox.command.converter.IMethodArgument;
import com.g2forge.gearbox.command.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.proxy.process.ProcessInvocation;
import com.g2forge.habitat.metadata.access.ITypedMetadataAccessor;
import com.g2forge.habitat.metadata.access.indirect.IndirectMetadata;
import com.g2forge.habitat.metadata.type.predicate.IPredicateType;
import com.g2forge.habitat.metadata.value.predicate.ConstantPredicate;
import com.g2forge.habitat.metadata.value.predicate.IPredicate;
import com.g2forge.habitat.metadata.value.subject.ISubject;

@IndirectMetadata(IArgumentConsumer.MetadataAccessor.class)
@FunctionalInterface
public interface IArgumentConsumer extends IConsumer3<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation, IMethodArgument<?>> {
	public static class MetadataAccessor implements ITypedMetadataAccessor<IArgumentConsumer, ISubject, IPredicateType<IArgumentConsumer>> {
		@Override
		public IPredicate<IArgumentConsumer> bindTyped(ISubject subject, IPredicateType<IArgumentConsumer> predicateType) {
			final ArgumentConsumers argumentConsumers = subject.get(ArgumentConsumers.class);
			if (argumentConsumers == null) return ConstantPredicate.absent(subject, predicateType);
			return ConstantPredicate.present(subject, predicateType, new IArgumentConsumer() {
				@Override
				public void accept(ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation, IMethodArgument<?> argument) {
					for (ArgumentConsumer argumentConsumerMetadata : argumentConsumers.value()) {
						final IConsumer3<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation, IMethodArgument<?>> argumentConsumer;
						try {
							argumentConsumer = argumentConsumerMetadata.value().getDeclaredConstructor().newInstance();
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
							throw new RuntimeReflectionException(e);
						}
						argumentConsumer.accept(processInvocationBuilder, methodInvocation, argument);
					}
				}
			});
		}
	}

	public void accept(ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation, IMethodArgument<?> argument);
}
