package com.g2forge.gearbox.serdes.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.serdes.AJacksonStreamingSerdesFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class YAMLSerdesFactory<T> extends AJacksonStreamingSerdesFactory<T> {
	protected final YAMLMapper mapper;

	protected final ITypeRef<T> type;
}
