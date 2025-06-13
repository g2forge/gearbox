package com.g2forge.gearbox.csv;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.g2forge.alexandria.java.core.helpers.HCollection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CSVMapper<T> extends ACSVMapper<T, T> {
	protected final Class<T> type;

	protected final List<String> columns;

	@Getter(lazy = true, value = AccessLevel.PROTECTED)
	private final CsvSchema schema = createSchema();

	public CSVMapper(Class<T> type, String... columns) {
		this(type, HCollection.asList(columns));
	}

	@Override
	protected CsvMapper createMapper() {
		final CsvMapper retVal = super.createMapper();
		retVal.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
		retVal.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
		return retVal;
	}

	@Override
	protected ObjectReader createObjectReader() {
		return getMapper().readerFor(getType()).with(getSchema());
	}

	@Override
	protected ObjectWriter createObjectWriter() {
		return getMapper().writerFor(getType()).with(getSchema());
	}

	protected CsvSchema createSchema() {
		final CsvSchema.Builder builder = CsvSchema.builder().setUseHeader(true);
		for (String column : getColumns()) {
			builder.addColumn(column);
		}
		return builder.build();
	}

	@Override
	protected List<T> readAll(MappingIterator<T> iterator) throws IOException {
		return iterator.readAll();
	}

	@Override
	protected void writeAll(Collection<T> values, final SequenceWriter sequenceWriter) throws IOException {
		sequenceWriter.writeAll(values);
	}
}
