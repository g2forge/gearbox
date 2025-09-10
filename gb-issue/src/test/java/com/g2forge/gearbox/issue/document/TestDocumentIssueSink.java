package com.g2forge.gearbox.issue.document;

import org.junit.Test;

import com.g2forge.alexandria.java.core.resource.Resource;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.enigma.document.convert.md.MDRenderer;
import com.g2forge.enigma.document.model.IBlock;
import com.g2forge.habitat.trace.HTrace;

public class TestDocumentIssueSink {
	@Test
	public void basic() {
		final DocumentIssueSink<IDocumentIssueType<?>> sink = new DocumentIssueSink<>();;
		sink.report(DocumentIssueType.BasicIssue.of(new DocumentPayload("a")));
		sink.report(DocumentIssueType.BasicIssue.of(new DocumentPayload("b", "c")));
		sink.report(DocumentIssueType.BasicIssue.of(new DocumentPayload()));
		final IBlock block = sink.build();
		HAssert.assertEquals(new Resource(getClass(), HTrace.getCaller().getName() + ".md"), new MDRenderer().render(block));
	}
	
	@Test
	public void fancy() {
		final DocumentIssueSink<IDocumentIssueType<?>> sink = new DocumentIssueSink<>();;
		sink.report(DocumentIssueType.FancyIssue.of(new DocumentPayload("a")));
		sink.report(DocumentIssueType.FancyIssue.of(new DocumentPayload("b", "c")));
		sink.report(DocumentIssueType.FancyIssue.of(new DocumentPayload()));
		final IBlock block = sink.build();
		HAssert.assertEquals(new Resource(getClass(), HTrace.getCaller().getName() + ".md"), new MDRenderer().render(block));
	}
}
