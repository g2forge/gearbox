package com.g2forge.gearbox.issue;

public interface IIssueSource<Type extends IIssueType<?>> {
	public void send(IIssueSink<? super Type> sink);
}
