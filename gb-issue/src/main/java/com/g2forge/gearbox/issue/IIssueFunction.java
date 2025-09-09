package com.g2forge.gearbox.issue;

@FunctionalInterface
public interface IIssueFunction<T> {
	public static <T> IIssueFunction<T> create(IIssueFunction<T> function) {
		return function;
	}

	public default T apply(IIssue<?, ?> issue) {
		@SuppressWarnings("rawtypes")
		final IIssue cast = (IIssue) issue;
		@SuppressWarnings("unchecked")
		final T retVal = (T) applyTyped(cast);
		return retVal;
	}

	public <Type extends IIssueType<Payload>, Payload> T applyTyped(IIssue<Type, Payload> issue);

	public static final IIssueFunction<String> COMPUTE_MESSAGE = new IIssueFunction<String>() {
		@Override
		public <Type extends IIssueType<Payload>, Payload> String applyTyped(IIssue<Type, Payload> issue) {
			return issue.getType().computeMessage(issue.getPayload());
		}
	};
}
