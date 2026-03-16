package com.g2forge.gearbox.serdes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.g2forge.alexandria.java.close.ICloseableSupplier;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.io.dataaccess.IDataSource;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.ref.ITypeRef;

public abstract class AJacksonSerdesFactory<T> implements ISerdesFactoryRW<T> {
	@Override
	public ICloseableConsumer1<T> create(IDataSink sink) {
		final ObjectWriter objectWriter = getMapper().writerFor(getType().getErasedType());
		final SequenceWriter sequenceWriter;
		try {
			sequenceWriter = objectWriter.writeValues(sink.getStream(ITypeRef.of(OutputStream.class)));
			sequenceWriter.init(true);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}

		return new ICloseableConsumer1<T>() {
			@Override
			public void accept(T input) throws RuntimeException {
				try {
					sequenceWriter.write(input);
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
			}

			@Override
			public void close() {
				try {
					sequenceWriter.close();
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
			}
		};
	}

	@Override
	public ICloseableSupplier<T> create(IDataSource source) {
		final ObjectReader reader = getMapper().readerFor(getType().getErasedType());
		final MappingIterator<T> mappingIterator;
		try {
			mappingIterator = reader.readValues(source.getStream(ITypeRef.of(InputStream.class)));
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}

		return new ICloseableSupplier<T>() {
			@Override
			public void close() {
				try {
					mappingIterator.close();
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
			}

			@Override
			public T get() {
				try {
					return mappingIterator.nextValue();
				} catch (IOException e) {
					throw new RuntimeIOException(e);
				}
			}
		};
	}

	protected abstract ObjectMapper getMapper();

	protected abstract ITypeRef<T> getType();
}
