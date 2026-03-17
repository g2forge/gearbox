package com.g2forge.gearbox.serdes.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.serdes.AJacksonStreamingSerdesFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class XMLSerdesFactory<T> extends AJacksonStreamingSerdesFactory<T> {
	protected final XmlMapper mapper;

	protected final ITypeRef<T> type;

	@Override
	public ICloseableConsumer1<T> create(IDataSink sink) {
		return new ICloseableConsumer1<T>() {
			protected List<T> list = new ArrayList<>();

			@Override
			public void accept(T input) throws RuntimeException {
				checkOpen();
				list.add(input);
			}

			protected void checkOpen() {
				if (list == null) throw new IllegalStateException();
			}

			@Override
			public void close() {
				checkOpen();
				final ObjectWriter objectWriter = getMapper().writerFor(getMapper().getTypeFactory().constructCollectionLikeType(List.class, getType().getErasedType()));
				try {
					objectWriter.writeValue(sink.getStream(ITypeRef.of(OutputStream.class)), list);
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
				list = null;
			}
		};
	}
}
