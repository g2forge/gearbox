package com.g2forge.gearbox.issue.slf4j;

import java.nio.file.Paths;
import java.util.LinkedList;

import org.junit.Test;
import org.slf4j.event.Level;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.io.TextRangeSpecifier;
import com.g2forge.alexandria.log.CollectionLogger;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.issue.ExampleIssueType;
import com.g2forge.gearbox.issue.ExamplePayload;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;

public class TestSlf4jIssueSink {
	@Test
	public void test() {
		final LinkedList<CollectionLogger.CollectionLoggerEvent> queue = new LinkedList<>();
		final String loggerName = "test logger";
		final IIssueSink<IIssueType<?>> sink = new Slf4JIssueSink<>(new CollectionLogger(loggerName, queue));
		final IIssue<ExampleIssueType, ExamplePayload> issue = ExampleIssueType.Generic.of(new ExamplePayload(Paths.get("."), new TextRangeSpecifier(4)));
		sink.report(issue);
		final String message = issue.getType().computeMessage(issue.getPayload());
		HAssert.assertEquals(CollectionLogger.CollectionLoggerEvent.builder().level(Level.ERROR).loggerName(loggerName).timeStamp(0).message(message).argumentArray(new Object[0]).build(), HCollection.getOne(queue).toBuilder().timeStamp(0).threadName(null).build());
	}
}
