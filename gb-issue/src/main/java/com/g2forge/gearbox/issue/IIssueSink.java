package com.g2forge.gearbox.issue;

import java.util.Collection;

import com.g2forge.alexandria.java.core.helpers.HCollection;

@FunctionalInterface
public interface IIssueSink<Type extends IIssueType<?>> {
	public default Level getMinimum() {
		return Level.MINIMUM;
	}

	public default void report(Collection<? extends IIssue<? extends Type, ?>> issues) {
		for (IIssue<? extends Type, ?> issue : issues) {
			report(issue);
		}
	}

	public void report(IIssue<? extends Type, ?> issue);

	public default void report(@SuppressWarnings("unchecked") IIssue<? extends Type, ?>... issues) {
		report(HCollection.asList(issues));
	}
}
