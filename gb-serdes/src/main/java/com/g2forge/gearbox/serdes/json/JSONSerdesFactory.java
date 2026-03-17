package com.g2forge.gearbox.serdes.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.serdes.AJacksonStreamingSerdesFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class JSONSerdesFactory<T> extends AJacksonStreamingSerdesFactory<T> {
	protected final ObjectMapper mapper;

	protected final ITypeRef<T> type;
}
