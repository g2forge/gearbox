package com.g2forge.gearbox.serdes.csv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.g2forge.alexandria.java.close.ICloseableSupplier;
import com.g2forge.alexandria.java.function.ICloseableConsumer1;
import com.g2forge.alexandria.java.function.IThrowFunction1;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.io.dataaccess.IDataSource;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.serdes.AJacksonStreamingSerdesFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class ACSVMapper<T, Internal> implements ICSVMapper<T> {
	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final CsvMapper mapper = createMapper();

	protected CsvMapper createMapper() {
		return new CsvMapper();
	}

	protected abstract ObjectReader createObjectReader();

	protected abstract ObjectWriter createObjectWriter();

	@SuppressWarnings("resource")
	@Override
	public ICloseableSupplier<T> read(IDataSource source) {
		return read(reader -> reader.readValues(source.getStream(ITypeRef.of(InputStream.class))));
	}

	protected ICloseableSupplier<T> read(IThrowFunction1<ObjectReader, MappingIterator<Internal>, IOException> read) {
		final ObjectReader reader = createObjectReader();

		try {
			final MappingIterator<Internal> iterator = read.apply(reader);
			return read(iterator);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	protected abstract ICloseableSupplier<T> read(final MappingIterator<Internal> iterator) throws IOException;

	@Override
	public List<T> readAll(IDataSource source) {
		return readAll(reader -> AJacksonStreamingSerdesFactory.create(reader, source));
	}

	protected List<T> readAll(IThrowFunction1<ObjectReader, MappingIterator<Internal>, IOException> read) {
		final ObjectReader reader = createObjectReader();

		try (final MappingIterator<Internal> iterator = read.apply(reader)) {
			return readAll(iterator);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	protected abstract List<T> readAll(final MappingIterator<Internal> iterator) throws IOException;

	@Override
	public String write(Collection<T> values) {
		try {
			return createObjectWriter().writeValueAsString(values);
		} catch (JsonProcessingException e) {
			throw new RuntimeIOException(e);
		}
	}

	@Override
	public void write(Collection<T> values, IDataSink sink) {
		write(values, writer -> AJacksonStreamingSerdesFactory.create(writer, sink));
	}

	protected void write(Collection<T> values, IThrowFunction1<ObjectWriter, SequenceWriter, IOException> write) {
		final ObjectWriter objectWriter = createObjectWriter();
		try (final SequenceWriter sequenceWriter = write.apply(objectWriter)) {
			writeAll(values, sequenceWriter);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	@Override
	public ICloseableConsumer1<? super T> write(IDataSink sink) {
		final ObjectWriter objectWriter = createObjectWriter();
		final SequenceWriter sequenceWriter = AJacksonStreamingSerdesFactory.create(objectWriter, sink);
		return new ICloseableConsumer1<T>() {
			@Override
			public void accept(T t) {
				try {
					sequenceWriter.write(t);
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

	protected abstract void writeAll(Collection<T> values, final SequenceWriter sequenceWriter) throws IOException;
}
