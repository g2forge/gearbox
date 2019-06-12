package com.g2forge.gearbox.command.v2.converter;

import java.util.stream.Stream;

import com.g2forge.alexandria.annotations.message.TODO;
import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.java.core.helpers.HStream;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.alexandria.metadata.IMetadataLoader;

public interface ICommandConverter__ {
	@TODO(value = "Check for metadata on the type containing the method, will require upgrades to metadata first", link = "G2-469")
	public static <T, U> T load(Class<T> type, IMetadata metadata, final Class<U> klass) {
		return IMetadataLoader.load(type, metadata, klass, m -> {
			final CommandConverters commandConvertersMetadata = m.getMetadata(CommandConverters.class);
			if (commandConvertersMetadata == null) return null;
			final Class<? extends ICommandConverter__> rendererType = HStream.findOne(Stream.of(commandConvertersMetadata.value()).map(CommandConverter::value).filter(klass::isAssignableFrom));
			try {
				return klass.cast(rendererType.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeReflectionException(e);
			}
		});
	}
}
