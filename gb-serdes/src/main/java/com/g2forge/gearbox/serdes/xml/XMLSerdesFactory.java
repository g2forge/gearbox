package com.g2forge.gearbox.serdes.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.serdes.AJacksonSerdesFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class XMLSerdesFactory<T> extends AJacksonSerdesFactory<T> {
	protected final XmlMapper mapper;

	protected final ITypeRef<T> type;
}
