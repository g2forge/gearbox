package com.g2forge.gearbox.serdes.csv;

import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.gearbox.serdes.ATestSerdesFactory;
import com.g2forge.gearbox.serdes.SerdesFormat;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class TestCSVSerdesFactory extends ATestSerdesFactory<TestCSVSerdesFactory.Line> {
	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class Line {
		protected final String string;
	}

	@Override
	protected ISerdesFactoryRW<Line> createSerdesFactory() {
		return new CSVSerdesFactory<>(new CSVMapper<>(Line.class, "string"));
	}

	@Override
	protected Line fromString(String string) {
		return new Line(string);
	}

	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.CSV;
	}

	@Override
	protected ITypeRef<Line> getTypeRef() {
		return ITypeRef.of(Line.class);
	}

	@Override
	protected String toString(Line value) {
		return value.getString();
	}
}
