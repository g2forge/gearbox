package com.g2forge.gearbox.csv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.io.RuntimeIOException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CSVMapper<T> {
	protected final Class<T> type;

	protected final List<String> columns;

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final CsvMapper mapper = createMapper();

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final CsvSchema schema = createSchema();

	public CSVMapper(Class<T> type, String... columns) {
		this(type, HCollection.asList(columns));
	}

	protected CsvMapper createMapper() {
		final CsvMapper retVal = new CsvMapper();
		retVal.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
		retVal.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
		return retVal;
	}

	protected CsvSchema createSchema() {
		final CsvSchema.Builder builder = CsvSchema.builder().setUseHeader(true);
		for (String column : getColumns()) {
			builder.addColumn(column);
		}
		return builder.build();
	}

	public List<T> read(InputStream stream) {
		final ObjectReader reader = getMapper().readerFor(getType()).with(getSchema());

		try {
			final MappingIterator<T> iterator = reader.readValues(stream);
			return iterator.readAll();
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public void write(Collection<T> values, Path path) {
		final ObjectWriter writer = getMapper().writerFor(getType()).with(getSchema());
		try {
			writer.writeValues(path.toFile()).writeAll(values);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}
}
