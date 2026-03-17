package com.g2forge.gearbox.issue.serdes;

import java.nio.file.Paths;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.io.TextRangeSpecifier;
import com.g2forge.alexandria.java.io.dataaccess.ByteArrayDataSink;
import com.g2forge.alexandria.java.io.dataaccess.ByteArrayDataSource;
import com.g2forge.alexandria.java.io.serdes.ISerdesFactoryRW;
import com.g2forge.alexandria.java.type.ref.ITypeRef;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.issue.ExampleIssueType;
import com.g2forge.gearbox.issue.ExamplePayload;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.sink.CollectingIssueSink;
import com.g2forge.gearbox.serdes.SerdesFormat;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public abstract class ATestIssueSerdes {
	@Data
	@Builder(toBuilder = true)
	@RequiredArgsConstructor
	public static class ExampleSerialized {
		protected final ExampleIssueType type;

		protected final String path;

		protected final String range;
	}

	protected ISerdesFactoryRW<ExampleSerialized> createSerdesFactory() {
		return getFormat().createSerdesFactory(ITypeRef.of(ExampleSerialized.class));
	}

	protected abstract SerdesFormat getFormat();

	@Test
	public void test() {
		final IIssue<ExampleIssueType, ExamplePayload> issue = ExampleIssueType.Generic.of(new ExamplePayload(Paths.get("B"), new TextRangeSpecifier(0, 5)));
		final IIssueFormatRW<ExampleIssueType, ExamplePayload, ExampleSerialized> issueFormat = new IIssueFormatRW<ExampleIssueType, ExamplePayload, ExampleSerialized>() {
			@Override
			public IIssue<ExampleIssueType, ExamplePayload> deserialize(ExampleSerialized serialized) {
				return serialized.getType().of(new ExamplePayload(Paths.get(serialized.getPath()), new TextRangeSpecifier(serialized.getRange())));
			}

			@Override
			public ExampleSerialized serialize(IIssue<ExampleIssueType, ExamplePayload> issue) {
				return new ExampleSerialized(issue.getType(), issue.getPayload().getPath().toString(), issue.getPayload().getRange().toRangeString());
			}
		};
		final ByteArrayDataSink bads = new ByteArrayDataSink();
		final ISerdesFactoryRW<ExampleSerialized> serdesFactory = createSerdesFactory();
		try (final SerializingIssueSink<ExampleIssueType, ExamplePayload, ExampleSerialized> sink = new SerializingIssueSink<>(serdesFactory, bads, issueFormat, ITypeRef.of(ExamplePayload.class))) {
			sink.report(issue);
		}

		try (final DeserializingIssueSource<ExampleIssueType, ExamplePayload, ExampleSerialized> source = new DeserializingIssueSource<>(serdesFactory, new ByteArrayDataSource(bads.getStream().toByteArray()), issueFormat)) {
			final CollectingIssueSink<ExampleIssueType> sink = new CollectingIssueSink<>();
			source.send(sink);
			HAssert.assertEquals(HCollection.asList(issue), sink.getIssues());
		}
	}
}
