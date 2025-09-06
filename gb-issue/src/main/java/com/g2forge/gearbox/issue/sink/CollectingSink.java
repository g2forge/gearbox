package com.g2forge.gearbox.issue.sink;

import java.util.ArrayList;
import java.util.List;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;

import lombok.Getter;

@Getter
public class CollectingSink<Type extends IIssueType<?>> implements IIssueSink<Type> {
	protected final List<IIssue<? extends Type, ?>> issues = new ArrayList<>();

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		issues.add(issue);
	}
}