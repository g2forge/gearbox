package com.g2forge.gearbox.command.v2.proxy.method;

import java.lang.reflect.Type;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.function.IConsumer3;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.alexandria.metadata.IMetadataLoader;
import com.g2forge.alexandria.metadata.MetadataLoader;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;

@MetadataLoader(IArgumentConsumer.ArgumentConsumerMetadataLoader.class)
public interface IArgumentConsumer extends IConsumer3<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation, IArgumentConsumer.IArgument<?>> {
	public static class ArgumentConsumerMetadataLoader implements IMetadataLoader {
		@Override
		public <T> T load(Class<T> type, IMetadata metadata) {
			return IMetadataLoader.load(type, metadata, IArgumentConsumer.class, m -> {
				final ArgumentConsumers argumentConsumers = m.getMetadata(ArgumentConsumers.class);
				if (argumentConsumers == null) return null;
				return (processInvocationBuilder, methodInvocation, argument) -> {
					for (ArgumentConsumer argumentConsumerMetadata : argumentConsumers.value()) {
						final IConsumer3<ProcessInvocation.ProcessInvocationBuilder<Object>, MethodInvocation, IArgument<?>> argumentConsumer;
						try {
							argumentConsumer = argumentConsumerMetadata.value().newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							throw new RuntimeReflectionException(e);
						}
						argumentConsumer.accept(processInvocationBuilder, methodInvocation, argument);
					}
				};
			});
		}
	}

	public interface IArgument<T> {
		public T get();

		public Type getGenericType();

		public IMetadata getMetadata();

		public String getName();

		public Class<T> getType();
	}

	public void accept(ProcessInvocation.ProcessInvocationBuilder<Object> processInvocationBuilder, MethodInvocation methodInvocation, IArgument<?> argument);
}
