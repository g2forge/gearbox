package com.g2forge.gearbox.issue;

import java.util.stream.Collectors;

import org.junit.Test;

import com.g2forge.alexandria.java.core.error.HError;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.java.core.marker.ISingleton;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.gearbox.issue.sink.CollectingSink;

public class TestGeneralThrowable {
	public static class ThrowableIssue implements IIssueType<Throwable>, ISingleton {
		protected static final ThrowableIssue INSTANCE = new ThrowableIssue();

		public static ThrowableIssue create() {
			return INSTANCE;
		}

		protected ThrowableIssue() {}

		@Override
		public String computeMessage(Throwable payload) {
			return HError.toString(payload);
		}

		@Override
		public String getDescription() {
			return "Throwable";
		}

		@Override
		public Level getLevel() {
			return Level.ERROR;
		}
	}

	@Test
	public void test() {
		final CollectingSink<IIssueType<Throwable>> sink = new CollectingSink<>();
		final Throwable expected = new Throwable();
		for (Throwable throwable : new Throwable[] { null, expected }) {
			try {
				if (throwable != null) throw throwable;
			} catch (Throwable payload) {
				sink.report(ThrowableIssue.create().of(payload));
			}
		}
		HAssert.assertEquals(HCollection.asList(expected), sink.getIssues().stream().map(IIssue::getPayload).collect(Collectors.toList()));
	}
}
