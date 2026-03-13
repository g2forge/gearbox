package com.g2forge.gearbox.issue.sink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;

import lombok.Getter;

@Getter
public class CollectingIssueSink<Type extends IIssueType<?>> implements IIssueSink<Type> {
	protected final List<IIssue<? extends Type, ?>> issues = new ArrayList<>();

	public void dump(IIssueSink<? super Type> sink) {
		sink.report(getIssues());
	}

	@Override
	public void report(Collection<? extends IIssue<? extends Type, ?>> issues) {
		getIssues().addAll(issues);
	}

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		getIssues().add(issue);
	}
}