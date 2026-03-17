package com.g2forge.gearbox.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.g2forge.alexandria.java.core.error.DependencyNotLoadedError;
import com.g2forge.alexandria.java.function.ISupplier;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.media.IMediaType;
import com.g2forge.alexandria.media.MediaType;
import com.g2forge.gearbox.serdes.json.JSONSerdesFactory;
import com.g2forge.gearbox.serdes.xml.XMLSerdesFactory;
import com.g2forge.gearbox.serdes.yaml.YAMLSerdesFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SerdesFormat {
	JSON(MediaType.JSON, new String[] { "gb-serdes" }) {
		@Override
		public <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type) {
			return tryWithModule(() -> new JSONSerdesFactory<>(new ObjectMapper(), type));
		}
	},
	XML(MediaType.XML, new String[] { "gb-serdes", "jackson-dataformat-xml" }) {
		@Override
		public <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type) {
			return tryWithModule(() -> new XMLSerdesFactory<>(new XmlMapper(), type));
		}
	},
	YAML(MediaType.YAML, new String[] { "gb-serdes", "jackson-dataformat-yaml" }) {
		@Override
		public <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type) {
			return tryWithModule(() -> new YAMLSerdesFactory<>(new YAMLMapper(), type));
		}
	},
	CSV(MediaType.CSV, new String[] { "gb-serdes", "jackson-dataformat-csv" }) {
		@Override
		public <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type) {
			throw new UnsupportedOperationException("Cannot create CSV serdes factories without a column specification!");
		}
	};

	protected final IMediaType mediaType;

	protected final String[] modules;

	public abstract <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type);

	public <T> T tryWithModule(ISupplier<? extends T> supplier) {
		try {
			return supplier.get();
		} catch (NoClassDefFoundError error) {
			throw new DependencyNotLoadedError(error, getModules());
		}
	}
}
