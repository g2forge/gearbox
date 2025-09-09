package com.g2forge.gearbox.issue.slf4j;

import org.slf4j.Logger;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueSink;
import com.g2forge.gearbox.issue.IIssueType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class Slf4JIssueSink<Type extends IIssueType<?>> implements IIssueSink<Type> {
	protected final Logger logger;

	public Slf4JIssueSink() {
		this(Slf4JIssueSink.log);
	}

	@Override
	public void report(IIssue<? extends Type, ?> issue) {
		reportInternal(issue);
	}

	protected <_Type extends IIssueType<_Payload>, _Payload> void reportInternal(IIssue<_Type, _Payload> issue) {
		final _Type type = issue.getType();
		if (getLogger().isEnabledForLevel(type.getLevel().getSlf4j())) {
			final String message = type.computeMessage(issue.getPayload());
			getLogger().atLevel(type.getLevel().getSlf4j()).log(message);
		}
	}
}
