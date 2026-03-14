package com.g2forge.gearbox.issue.sink;

import java.util.Collection;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AFilterIssueSink<Type extends IIssueType<?>> extends AWrappingIssueSink<Type, Type> {
	protected final IIssueSink<Type> sink;

	protected abstract boolean isAccepted(IIssue<? extends Type, ?> issue);

	@Override
	public void report(Collection<? extends IIssue<? extends Type, ?>> issues) {
		getSink().report(issues.stream().filter(this::isAccepted).toList());
	}

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		if (isAccepted(issue)) getSink().report(issue);
	}
}
