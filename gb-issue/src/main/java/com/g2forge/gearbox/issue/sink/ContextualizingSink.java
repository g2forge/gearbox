package com.g2forge.gearbox.issue.sink;

import java.util.Map;

import com.g2forge.alexandria.java.function.IFunction1;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContextualizingSink<InputType extends IIssueType<?>, OutputType extends IIssueType<?>> implements IIssueSink<InputType> {
	protected final IIssueSink<OutputType> sink;

	protected final Map<InputType, IFunction1<IIssue<? extends InputType, ?>, IIssue<? extends OutputType, ?>>> functions;

	@Override
	public void report(IIssue<? extends InputType, ?> issue) {
		final InputType inputType = issue.getType();
		final IFunction1<IIssue<? extends InputType, ?>, IIssue<? extends OutputType, ?>> function = getFunctions().get(inputType);
		getSink().report(function.apply(issue));
	}
}
