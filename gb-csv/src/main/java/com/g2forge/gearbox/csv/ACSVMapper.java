package com.g2forge.gearbox.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.g2forge.alexandria.java.function.IThrowFunction1;
import com.g2forge.alexandria.java.io.RuntimeIOException;

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

	@Override
	public List<T> read(InputStream stream) {
		return read(reader -> reader.readValues(stream));
	}

	protected List<T> read(IThrowFunction1<ObjectReader, MappingIterator<Internal>, IOException> read) {
		final ObjectReader reader = createObjectReader();

		try {
			final MappingIterator<Internal> iterator = read.apply(reader);
			return readAll(iterator);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	@Override
	public List<T> read(Path path) {
		return read(reader -> reader.readValues(path.toFile()));
	}

	@Override
	public List<T> read(String string) {
		return read(reader -> reader.readValues(string));
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

	protected void write(Collection<T> values, IThrowFunction1<ObjectWriter, SequenceWriter, IOException> write) {
		final ObjectWriter objectWriter = createObjectWriter();
		try {
			final SequenceWriter sequenceWriter = write.apply(objectWriter);
			writeAll(values, sequenceWriter);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	@Override
	public void write(Collection<T> values, OutputStream stream) {
		write(values, writer -> writer.writeValues(stream));
	}

	@Override
	public void write(Collection<T> values, Path path) {
		write(values, writer -> writer.writeValues(path.toFile()));
	}

	protected abstract void writeAll(Collection<T> values, final SequenceWriter sequenceWriter) throws IOException;
}
