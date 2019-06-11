package com.g2forge.gearbox.command.v2.outdated;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.function.IConsumer2;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.alexandria.metadata.IMetadataLoader;
import com.g2forge.alexandria.metadata.MetadataLoader;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;

@MetadataLoader(IMethodConsumer.MethodConsumerMetadataLoader.class)
public interface IMethodConsumer extends IConsumer2<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation> {
	public static class MethodConsumerMetadataLoader implements IMetadataLoader {
		@Override
		public <T> T load(Class<T> type, IMetadata metadata) {
			return IMetadataLoader.load(type, metadata, IMethodConsumer.class, m -> {
				final MethodConsumers methodConsumers = m.getMetadata(MethodConsumers.class);
				if (methodConsumers == null) return null;
				return (processInvocationBuilder, methodInvocation) -> {
					for (MethodConsumer methodConsumerMetadata : methodConsumers.value()) {
						final IConsumer2<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation> methodConsumer;
						try {
							methodConsumer = methodConsumerMetadata.value().newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							throw new RuntimeReflectionException(e);
						}
						methodConsumer.accept(processInvocationBuilder, methodInvocation);
					}
				};
			});
		}
	}

	public void accept(ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation);
}
