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
import com.g2forge.alexandria.java.function.IFunction2;
import com.g2forge.alexandria.java.io.RuntimeIOException;
import com.g2forge.alexandria.java.io.dataaccess.AInputStreamDataSource;
import com.g2forge.alexandria.java.io.dataaccess.AOutputStreamDataSink;
import com.g2forge.alexandria.java.io.dataaccess.IDataSink;
import com.g2forge.alexandria.java.io.dataaccess.IDataSource;
import com.g2forge.alexandria.java.io.dataaccess.PathDataSink;
import com.g2forge.alexandria.java.io.dataaccess.PathDataSource;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.function.TypeSwitch2;
import com.g2forge.alexandria.java.type.ref.ITypeRef;

public abstract class AJacksonStreamingSerdesFactory<T> implements ISerdesFactoryRW<T> {
	public static final IFunction2<ObjectWriter, IDataSink, SequenceWriter> OBJECT_WRITER;

	public static final IFunction2<ObjectReader, IDataSource, MappingIterator<?>> OBJECT_READER;

	static {
		final TypeSwitch2.FunctionBuilder<ObjectWriter, IDataSink, SequenceWriter> objectWriterBuilder = new TypeSwitch2.FunctionBuilder<ObjectWriter, IDataSink, SequenceWriter>();
		objectWriterBuilder.add(ObjectWriter.class, PathDataSink.class, (writer, sink) -> {
			try {
				return writer.writeValues(sink.getPath().toFile());
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		});
		objectWriterBuilder.add(ObjectWriter.class, AOutputStreamDataSink.class, (writer, sink) -> {
			try {
				return writer.writeValues(sink.getStream());
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		});
		objectWriterBuilder.add(ObjectWriter.class, IDataSink.class, (writer, sink) -> {
			try {
				return writer.writeValues(sink.getStream(ITypeRef.of(OutputStream.class)));
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		});
		OBJECT_WRITER = objectWriterBuilder.build();

		final TypeSwitch2.FunctionBuilder<ObjectReader, IDataSource, MappingIterator<?>> objectReaderBuilder = new TypeSwitch2.FunctionBuilder<ObjectReader, IDataSource, MappingIterator<?>>();
		objectReaderBuilder.add(ObjectReader.class, PathDataSource.class, (reader, source) -> {
			try {
				return reader.readValues(source.getPath().toFile());
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		});
		objectReaderBuilder.add(ObjectReader.class, AInputStreamDataSource.class, (reader, source) -> {
			try {
				return reader.readValues(source.getStream());
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		});
		objectReaderBuilder.add(ObjectReader.class, IDataSource.class, (reader, source) -> {
			try {
				return reader.readValues(source.getStream(ITypeRef.of(InputStream.class)));
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		});
		OBJECT_READER = objectReaderBuilder.build();
	}

	public static <T> MappingIterator<T> create(final ObjectReader reader, IDataSource source) {
		@SuppressWarnings("unchecked")
		final MappingIterator<T> mappingIterator = (MappingIterator<T>) OBJECT_READER.apply(reader, source);
		return mappingIterator;
	}

	public static SequenceWriter create(final ObjectWriter objectWriter, IDataSink sink) {
		return OBJECT_WRITER.apply(objectWriter, sink);
	}

	@Override
	public ICloseableConsumer1<T> create(IDataSink sink) {
		final ObjectWriter objectWriter = getMapper().writerFor(getType().getErasedType());
		final SequenceWriter sequenceWriter = create(objectWriter, sink);
		try {
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
		final ObjectReader reader = getMapper().readerFor(getType().getErasedType());;
		final MappingIterator<T> mappingIterator = create(reader, source);

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
