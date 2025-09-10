package com.g2forge.gearbox.issue.document;

import com.g2forge.gearbox.issue.IIssue;

@FunctionalInterface
public interface IDocumentIssueConsumer {
	public static IDocumentIssueConsumer create(IDocumentIssueConsumer consumer) {
		return consumer;
	}

	@SuppressWarnings("unchecked")
	public default void accept(IIssue<? extends IDocumentIssueType<?>, ?> issue) {
		@SuppressWarnings("rawtypes")
		final IIssue cast = (IIssue) issue;
		acceptTyped(cast);
	}

	public <Type extends IDocumentIssueType<Payload>, Payload> void acceptTyped(IIssue<Type, Payload> issue);
}
