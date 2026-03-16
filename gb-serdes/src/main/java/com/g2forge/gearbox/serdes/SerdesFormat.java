package com.g2forge.gearbox.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
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
	JSON(MediaType.JSON) {
		@Override
		public <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type) {
			return new JSONSerdesFactory<>(new ObjectMapper(), type);
		}
	},
	XML(MediaType.XML) {
		@Override
		public <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type) {
			return new XMLSerdesFactory<>(new XmlMapper(), type);
		}
	},
	YAML(MediaType.YAML) {
		@Override
		public <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type) {
			return new YAMLSerdesFactory<>(new YAMLMapper(), type);
		}
	};

	protected final IMediaType mediaType;

	public abstract <T> ISerdesFactoryRW<T> createSerdesFactory(ITypeRef<T> type);
}
