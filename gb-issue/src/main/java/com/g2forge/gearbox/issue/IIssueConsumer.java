package com.g2forge.gearbox.issue;

@FunctionalInterface
public interface IIssueConsumer {
	public static IIssueConsumer create(IIssueConsumer consumer) {
		return consumer;
	}

	@SuppressWarnings("unchecked")
	public default void accept(IIssue<?, ?> issue) {
		@SuppressWarnings("rawtypes")
		final IIssue cast = (IIssue) issue;
		acceptTyped(cast);
	}

	public <Type extends IIssueType<Payload>, Payload> void acceptTyped(IIssue<Type, Payload> issue);
}
