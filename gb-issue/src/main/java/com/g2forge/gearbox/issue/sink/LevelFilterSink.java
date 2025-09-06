package com.g2forge.gearbox.issue.sink;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.Level;

import lombok.Getter;

@Getter
public class LevelFilterSink<Type extends IIssueType<?>> extends AFilterSink<Type> {
	protected final Level minimum;

	public LevelFilterSink(IIssueSink<Type> sink, Level minimum) {
		super(sink);
		this.minimum = minimum;
	}

	@Override
	protected boolean isAccepted(IIssue<? extends Type, ?> issue) {
		return (getMinimum() == null) || (getMinimum().compareTo(issue.getType().getLevel()) <= 0);
	}
}
