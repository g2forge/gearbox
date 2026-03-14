package com.g2forge.gearbox.issue.sink;

import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.Level;

public abstract class AWrappingIssueSink<InputType extends IIssueType<?>, OutputType extends IIssueType<?>> implements IIssueSink<InputType> {
	@Override
	public Level getMinimum() {
		return getSink().getMinimum();
	}

	protected abstract IIssueSink<OutputType> getSink();
}
