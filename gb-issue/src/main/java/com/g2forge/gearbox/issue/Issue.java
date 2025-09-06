package com.g2forge.gearbox.issue;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Issue<Type extends IIssueType<Payload>, Payload> implements IIssue<Type, Payload> {
	protected final Type type;

	protected final Payload payload;
}
