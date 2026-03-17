package com.g2forge.gearbox.issue.serdes;

import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.Level;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class BasicLoggedIssue {
	public static final String[] FIELDS = { "level", "code", "message" };

	public static <Type extends IIssueType<Payload>, Payload> BasicLoggedIssue create(IIssue<Type, Payload> issue) {
		final Type type = issue.getType();
		return new BasicLoggedIssue(type.getLevel(), type.getCode(), type.computeMessage(issue.getPayload()));
	}

	protected final Level level;

	protected final String code;

	protected final String message;
}