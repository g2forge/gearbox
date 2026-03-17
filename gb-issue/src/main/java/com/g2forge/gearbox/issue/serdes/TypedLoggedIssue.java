package com.g2forge.gearbox.issue.serdes;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.gearbox.issue.IIssue;
import com.g2forge.gearbox.issue.IIssueType;
import com.g2forge.gearbox.issue.Level;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class TypedLoggedIssue<Payload> {
	public static final String[] FIELDS = { "level", "code", "description" };

	public static String[] computeColumns(String... payloadColumns) {
		return HCollection.concatenate(HCollection.asList(FIELDS), HCollection.asList(payloadColumns)).toArray(String[]::new);
	}

	public static <Type extends IIssueType<Payload>, Payload> TypedLoggedIssue<Payload> create(IIssue<Type, Payload> issue) {
		final Type type = issue.getType();
		return new TypedLoggedIssue<>(type.getLevel(), type.getCode(), type.getDescription(), issue.getPayload());
	}

	protected final Level level;

	protected final String code;

	protected final String description;

	@JsonUnwrapped
	protected final Payload payload;
}