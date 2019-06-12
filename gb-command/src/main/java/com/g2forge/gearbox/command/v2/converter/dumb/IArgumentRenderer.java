package com.g2forge.gearbox.command.v2.converter.dumb;

import java.util.List;

import com.g2forge.alexandria.java.core.error.RuntimeReflectionException;
import com.g2forge.alexandria.metadata.IMetadata;
import com.g2forge.alexandria.metadata.IMetadataLoader;
import com.g2forge.alexandria.metadata.MetadataLoader;
import com.g2forge.gearbox.command.v2.converter.IMethodArgument;

@MetadataLoader(IArgumentRenderer.MetadataLoader.class)
public interface IArgumentRenderer<T> {
	public static class MetadataLoader implements IMetadataLoader {
		@Override
		public <T> T load(Class<T> type, IMetadata metadata) {
			return IMetadataLoader.load(type, metadata, IArgumentRenderer.class, m -> {
				final ArgumentRenderer argumentRendererMetadata = m.getMetadata(ArgumentRenderer.class);
				if (argumentRendererMetadata == null) return null;
				try {
					return argumentRendererMetadata.value().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeReflectionException(e);
				}
			});
		}
	}

	public List<String> render(IMethodArgument<T> argument);
}
