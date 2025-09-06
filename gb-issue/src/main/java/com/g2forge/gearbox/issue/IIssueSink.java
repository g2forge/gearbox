package com.g2forge.gearbox.issue;

@FunctionalInterface
public interface IIssueSink<Type extends IIssueType<?>> {
	public void report(IIssue<? extends Type, ?> issue);
}
