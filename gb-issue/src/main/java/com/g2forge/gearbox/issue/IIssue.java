package com.g2forge.gearbox.issue;

public interface IIssue<Type extends IIssueType<Payload>, Payload> {
	public Type getType();

	public Payload getPayload();
}
