package com.g2forge.gearbox.command.v2.converter;

import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.alexandria.metadata.IMetadataLoader;
import com.g2forge.alexandria.metadata.MetadataLoader;
import com.g2forge.gearbox.command.v2.proxy.method.MethodInvocation;
import com.g2forge.gearbox.command.v2.proxy.process.ProcessInvocation;

@FunctionalInterface
@MetadataLoader(ICommandConverterR_.MetadataLoader.class)
public interface ICommandConverterR_ extends ICommandConverter__ {
	public static class MetadataLoader implements IMetadataLoader {
		@Override
		public <T> T load(Class<T> type, IMetadata metadata) {
			return ICommandConverter__.load(type, metadata, ICommandConverterR_.class);
		}
	}

	public <T> ProcessInvocation<T> apply(ProcessInvocation<T> processInvocation, MethodInvocation methodInvocation);
}
