package com.g2forge.gearbox.issue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.g2forge.alexandria.java.io.TextRangeSpecifier;
import com.g2forge.alexandria.java.io.file.TempDirectory;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.issue.csv.GeneralCSVIssueSink;
import com.g2forge.gearbox.issue.sink.ICloseableIssueSink;

public class TestGeneralCSVIssueSink {
	@Test
	public void test() throws IOException {
		try (final TempDirectory temp = new TempDirectory()) {
			final Path log = temp.get().resolve("Log.csv");
			try (final ICloseableIssueSink<ExampleIssueType> sink = new GeneralCSVIssueSink<>(log)) {
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
