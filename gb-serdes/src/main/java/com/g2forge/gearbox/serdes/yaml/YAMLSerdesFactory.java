package com.g2forge.gearbox.serdes.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.serdes.AJacksonSerdesFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class YAMLSerdesFactory<T> extends AJacksonSerdesFactory<T> {
	protected final YAMLMapper mapper;

	protected final ITypeRef<T> type;
}
