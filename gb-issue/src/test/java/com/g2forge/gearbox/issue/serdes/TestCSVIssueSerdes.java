package com.g2forge.gearbox.issue.serdes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.g2forge.alexandria.java.io.TextRangeSpecifier;
import com.g2forge.alexandria.java.io.dataaccess.PathDataSink;
import com.g2forge.alexandria.java.io.file.TempDirectory;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.issue.ExampleIssueType;
import com.g2forge.gearbox.issue.ExamplePayload;
import com.g2forge.gearbox.serdes.SerdesFormat;
import com.g2forge.gearbox.serdes.csv.CSVMapper;
import com.g2forge.gearbox.serdes.csv.CSVSerdesFactory;

public class TestCSVIssueSerdes extends ATestIssueSerdes {
	@Override
	protected ISerdesFactoryRW<ExampleSerialized> createSerdesFactory() {
		return new CSVSerdesFactory<>(new CSVMapper<>(ExampleSerialized.class, "type", "path", "range"));
	}

	@Override
	protected SerdesFormat getFormat() {
		return SerdesFormat.CSV;
	}

	@Test
	public void typed() throws IOException {
		final IIssueFormat_W<ExampleIssueType, ExamplePayload, TypedLoggedIssue<ExamplePayload>> issueFormat = issue -> TypedLoggedIssue.create(issue);
		final CSVSerdesFactory<TypedLoggedIssue<ExamplePayload>> serdesFactory = new CSVSerdesFactory<>(new CSVMapper<>(TypedLoggedIssue.class, TypedLoggedIssue.computeColumns("path", "startLine", "endLine", "startCharacter", "endCharacter")));
		try (final TempDirectory temp = new TempDirectory()) {
			final Path log = temp.get().resolve("Log.csv");
			try (final SerializingIssueSink<ExampleIssueType, ExamplePayload, TypedLoggedIssue<ExamplePayload>> sink = new SerializingIssueSink<>(serdesFactory, new PathDataSink(log), issueFormat, ITypeRef.of(ExamplePayload.class))) {
				sink.report(ExampleIssueType.Generic.of(() -> new ExamplePayload(Paths.get("A"), new TextRangeSpecifier(0))));
				final long size0 = Files.size(log);
				sink.report(ExampleIssueType.Generic.of(() -> new ExamplePayload(Paths.get("B"), new TextRangeSpecifier(0, 5))));
				final long size1 = Files.size(log);
				HAssert.assertTrue(size0 > 0);
				HAssert.assertTrue(size1 > size0);
			}
		}
	}

	@Test
	public void general() throws IOException {
		final IIssueFormat_W<ExampleIssueType, ExamplePayload, BasicLoggedIssue> issueFormat = issue -> BasicLoggedIssue.create(issue);
		final CSVSerdesFactory<BasicLoggedIssue> serdesFactory = new CSVSerdesFactory<>(new CSVMapper<>(BasicLoggedIssue.class, BasicLoggedIssue.FIELDS));
		try (final TempDirectory temp = new TempDirectory()) {
			final Path log = temp.get().resolve("Log.csv");
			try (final SerializingIssueSink<ExampleIssueType, ExamplePayload, BasicLoggedIssue> sink = new SerializingIssueSink<>(serdesFactory, new PathDataSink(log), issueFormat, ITypeRef.of(ExamplePayload.class))) {
				sink.report(ExampleIssueType.Generic.of(() -> new ExamplePayload(Paths.get("A"), new TextRangeSpecifier(0))));
				final long size0 = Files.size(log);
				sink.report(ExampleIssueType.Generic.of(() -> new ExamplePayload(Paths.get("B"), new TextRangeSpecifier(0, 5))));
				final long size1 = Files.size(log);
				HAssert.assertTrue(size0 > 0);
				HAssert.assertTrue(size1 > size0);
			}
		}
	}
}
